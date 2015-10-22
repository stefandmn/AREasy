package org.areasy.runtime.plugins.sso;

/*
 * Copyright (c) 2007-2015 AREasy Runtime
 *
 * This library, AREasy Runtime and API for BMC Remedy AR System, is free software ("Licensed Software");
 * you can redistribute it and/or modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either version 2.1 of the License,
 * or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * including but not limited to, the implied warranty of MERCHANTABILITY, NONINFRINGEMENT,
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */

import com.remedy.arsys.session.Authenticator;
import com.remedy.arsys.session.UserCredentials;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.runtime.plugins.sso.ntlm.engine.NtlmManager;
import org.areasy.runtime.plugins.sso.ntlm.engine.NtlmUserAccount;
import org.areasy.common.data.StringUtility;
import org.areasy.common.data.workers.parsers.Base64;
import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;
import org.ietf.jgss.*;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.kerberos.KerberosPrincipal;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.SecureRandom;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * This authenticator uses NTLM challenge to extract username, domain and workstation information.
 * It doesn't authenticate the user, it only extracts information from the type 3 message received (see NTLMv1 challenge).
 */
public class SingleSignOnAuthenticator extends SimpleAuthenticator implements Authenticator
{
	/** Logger instance */
	private static Logger logger = LoggerFactory.getLog(SingleSignOnAuthenticator.class);

	/**
	 * Default method to get authentication credentials.
	 *
	 * @param request HTTP request structure
	 * @param response HTTP response structure
	 * @return <code>UserCredentials</code> AR structure.
	 * @throws IOException is any I/O error will occur
	 */
	public UserCredentials getAuthenticatedCredentials(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		return getAuthenticatedCredentials(request, response, isNtlmUsed(), isKerberosUsed(), isBasicUsed());
	}

	/**
	 * Default method to get authentication credentials.
	 *
	 * @param request HTTP request structure
	 * @param response HTTP response structure
	 * @param useNtlm flag to specify if NTML authentication will be used in the authentication workflow
	 * @param useKerberos flag to specify if NTML authentication will be used in the authentication workflow
	 * @param useBasicAuth flag to specify if basic authentication will be used in case on NTML and Kerberos authentication will fail or are not configured.
	 * @return <code>UserCredentials</code> AR structure.
	 * @throws IOException is any I/O error will occur
	 */
	public final UserCredentials getAuthenticatedCredentials(HttpServletRequest request, HttpServletResponse response, Boolean useNtlm, Boolean useKerberos, Boolean useBasicAuth) throws IOException
	{
		UserCredentials user;
		String header = request.getHeader(HEADER_AUTH);

		//Send request for negotiation
		if (header == null)
		{
			//get the previous authentication method
			int authMethod = getPreviousAuthenticationMethod(request);

			if((authMethod == 0 || authMethod == 1) && useNtlm)
			{
				//call NTLM authentication
				askNtlmNegotiation(request, response);
			}
			else if((authMethod == 0 || authMethod == 2) && useKerberos)
			{
				//call Kerberos authentication
				askKerberosNegotiation(request, response);
			}
			else if((authMethod == 0 || authMethod == 3) && useBasicAuth)
			{
				//call Basic authentication
				askBasicNegotiation(request, response);
			}
		}
		else
		{
			try
			{
				if(header.startsWith(HEADER_NTLM))
				{
					user = ntlmLogon(header, request, response);
				}
				else if(header.startsWith(HEADER_NEGOTIATE))
				{
					user = kerberosLogon(header, request, response);
				}
				else if(header.startsWith(HEADER_BASIC))
				{
					user = basicLogon(header, request, response);
				}
				else
				{
					user = new UserCredentials(null, null, null);
					logger.error("Header is not currently supported. Authentication refused");
				}

				user = ssoConfig.checkUser(user);
				setAuthenticated(request, user);
				return user;
			}
			catch(AREasyException are)
			{
				logger.debug("Authentication error: " + are.getMessage());
				logger.trace("Exception", are);

				//get the previous authentication method
				int authMethod = getPreviousAuthenticationMethod(request) + 1;
				boolean hitNextProtocol = false;

				if(authMethod == 1 && useNtlm)
				{
					hitNextProtocol = true;
					askNtlmNegotiation(request, response);
				}
				else if(authMethod == 2 && useKerberos)
				{
					hitNextProtocol = true;
					askKerberosNegotiation(request, response);
				}
				else if(authMethod >= 1 && useBasicAuth)
				{
					hitNextProtocol = true;
					askBasicNegotiation(request, response);
				}

				if(!hitNextProtocol)
				{
					String urlToRedirect = null;

					if(isRedirectUsed() && StringUtility.isNotEmpty(getErrorsRedirectUrl())) urlToRedirect = getErrorsRedirectUrl();
						else urlToRedirect = request.getContextPath() + "/shared/login.jsp";

					logger.debug("Redirect to an URL due to SSO error: " + urlToRedirect);
					redirectToUrl(response, urlToRedirect);
				}
			}
		}

		return null;
	}

	/**
	 * Execute NTLM authentication which is performing a simple NTLM negotiation and extract username, domain and host from the header.
	 *
	 * @param header HTTP string header
	 * @param request HTTP servlet request structure
	 * @param response HTTP servlet response structure
	 * @return <code>UserCredentials</code> data structure
	 * @throws IOException is any I/O exception will occur
	 * @throws AREasyException if the authentication fails to trigger the next negotiation process
	 */
	private UserCredentials ntlmLogon(String header, HttpServletRequest request, HttpServletResponse response) throws IOException, AREasyException
	{
		//Extract message
		byte[] msg = Base64.decode2(header.substring(HEADER_NTLM.length() + 1));

		if (msg[8] == 1)
		{
			logger.debug("Initiating NTLM negotiation");

			NtlmManager ntlmManager = new NtlmManager(getNtlmDomainName(), getNtlmDomainControllerIP(), getNtlmDomainControllerName(), getNtlmServiceAccount(), getNtlmServicePassword());
			SecureRandom secureRandom = new SecureRandom();
			byte[] serverChallenge = new byte[8];

			secureRandom.nextBytes(serverChallenge);
			byte[] challengeMessage = ntlmManager.negotiate(msg, serverChallenge);
			String authorization = Base64.encode2(challengeMessage);
			request.getSession(true).setAttribute("AREasyAuthenticator.ServerChallenge", serverChallenge);

			//send response
			response.setHeader("WWW-Authenticate", HEADER_NTLM + " " + authorization);
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setContentLength(0);
			return null;
		}
		else if (msg[8] == 3)
		{
			logger.debug("Run NTLM negotiation getting message type 3");

			try
			{
				NtlmManager ntlmManager = new NtlmManager(getNtlmDomainName(), getNtlmDomainControllerIP(), getNtlmDomainControllerName(), getNtlmServiceAccount(), getNtlmServicePassword());
				String ntlmM3Data[] =  ntlmManager.getM3Details(msg);

				if(ntlmM3Data != null && ntlmM3Data[0] != null && ssoConfig.isUserChecked(ntlmM3Data[0]))
				{
					return new UserCredentials(ntlmM3Data[0].toLowerCase(), null, getUniqueAuthString());
				}
				else
				{
					Object object = request.getSession(false).getAttribute("AREasyAuthenticator.ServerChallenge");
					byte[] serverChallenge = object != null ? (byte[]) object : null;

					NtlmUserAccount ntlmUserAccount = ntlmManager.authenticate(msg, serverChallenge);

					if(ntlmUserAccount != null)
					{
						logger.debug("NTLM Authentication done");
						return new UserCredentials(ntlmUserAccount.getUserName().toLowerCase(), null, getUniqueAuthString());
					}
					else throw new AREasyException("NTLM remote user is null");
				}
			}
			catch(Exception e)
			{
				if(e instanceof AREasyException) throw (AREasyException)e;
				else throw new AREasyException(e);
			}
		}
		else throw new AREasyException("Invalid NTLM message");
	}

	/**
	 * Execute basic authentication which take user name and password and send them to AREA LDAP plugin
	 *
	 * @param header HTTP string header
	 * @param request HTTP servlet request structure
	 * @param response HTTP response structure
	 * @return <code>UserCredentials</code> data structure
	 * @throws IOException is any I/O exception will occur
	 * @throws AREasyException if the authentication fails to trigger the next negotiation process
	 */
	private UserCredentials kerberosLogon(String header, HttpServletRequest request, HttpServletResponse response) throws IOException, AREasyException
	{
		final GSSCredential gssCredential;
		final GSSManager gssManager = GSSManager.getInstance();

		if(StringUtility.equalsIgnoreCase(getKerberosAuthMethod(), "keytab"))
		{
			try
			{
				final Oid spnegoOid = new Oid("1.3.6.1.5.5.2");//Spnego Oid
				gssCredential = gssManager.createCredential(null, GSSCredential.DEFAULT_LIFETIME, spnegoOid, GSSCredential.ACCEPT_ONLY);
			}
			catch (GSSException gsse)
			{
				throw new AREasyException(gsse);
			}
		}
		else
		{
			try
			{
				final Oid spnegoOid = new Oid( "1.3.6.1.5.5.2" ); //Spnego Oid
				final CallbackHandler handler = getUsernamePasswordHandler(getKerberosServiceAccount(), getKerberosServicePassword());

				// Obtain server credentials
				LoginContext loginContext = new LoginContext("kerberos.server", handler);
				loginContext.login();

				final PrivilegedExceptionAction<GSSCredential> action = new PrivilegedExceptionAction<GSSCredential>()
				{
					public GSSCredential run() throws GSSException
					{
						return gssManager.createCredential(null, GSSCredential.INDEFINITE_LIFETIME, spnegoOid , GSSCredential.ACCEPT_ONLY);
					}
				};

				gssCredential = Subject.doAs(loginContext.getSubject(), action);
			}
			catch (PrivilegedActionException e)
			{
				throw new AREasyException(e);
			}
			catch (LoginException e)
			{
				throw new AREasyException(e);
			}
			catch(GSSException gsse)
			{
				throw new AREasyException(gsse);
			}
		}

		//The data following the word Negotiate is the GSS-API data to process.
		byte gssapiData[] = Base64.decode2(header.substring(10));

		// The server attempts to establish a security context.  Establishment may result in
		// tokens that the server must return to the client.  Tokens are BASE-64 encoded GSS-API data.
		GSSContext context = null;
		String outToken = null;

		// Check service credentials
		if (gssCredential == null || gssManager == null) throw new AREasyException("The server's credentials or the manager was not obtained. Authentication fails");

		// Authenticate
		try
		{
			context = gssManager.createContext(gssCredential);
			byte tokenBytes[] = context.acceptSecContext(gssapiData, 0, gssapiData.length);

			if (tokenBytes == null) throw new AREasyException("The context return a null token, after accepting security context");
			outToken = new String(Base64.encode(tokenBytes));
		}
		catch (GSSException gsse)
		{
			throw new AREasyException(gsse);
		}

		// If the context is established, we can attempt to retrieve the name of the "context initiator."  In the case of the Kerberos
		// mechanism, the context initiator is the Kerberos principal of the client.  Additionally, the client may be delegating credentials.
		if (context.isEstablished())
		{
			logger.debug("Context established, attempting Kerberos principal retrieval");

			try
			{
				Subject subject = new Subject();
				GSSName clientGSSName = context.getSrcName();

				Principal clientPrincipal = new KerberosPrincipal(clientGSSName.toString());
				subject.getPrincipals().add(clientPrincipal);
				logger.debug("Got client Kerberos principal: " + clientGSSName);

				if (context.getCredDelegState())
				{
					GSSCredential delegateCredential = context.getDelegCred();
					GSSName delegateGSSName = delegateCredential.getName();

					Principal delegatePrincipal = new KerberosPrincipal(delegateGSSName.toString());
					subject.getPrincipals().add(delegatePrincipal);
					subject.getPrivateCredentials().add(delegateCredential);
					logger.debug("Got delegated Kerberos principal: " + delegateGSSName);
				}

				//Remove @domain from the principal name.
				//We have to send only the user name so it can be compared to the value from User form
				String principalName = clientGSSName.toString();
				if (principalName.contains("@"))
				{
					int idx = principalName.indexOf("@");
					principalName = principalName.substring(0, idx);
				}

				logger.debug("Kerberos Authentication done");
				return new UserCredentials(principalName.toLowerCase(), null, getUniqueAuthString());
			}
			catch (GSSException gsse)
			{
				throw new AREasyException(gsse);
			}
		}
		else
		{
			// Any returned code other than a success 2xx code represents an authentication error.  If a 401 containing a "WWW-Authenticate"
			// header with "Negotiate" and gssapi-data is returned from the server, it is a continuation of the authentication request.
			if (outToken != null && outToken.length() > 0)
			{
				response.setHeader("WWW-Authenticate", "Negotiate " + outToken.getBytes());
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.setContentLength(0);

				logger.error("Additional negotiation processing required, returning token");
				return null;
			}
			else throw new AREasyException("Kerberos negotiation failed.");
		}		
	}

	/**
	 * Execute basic authentication which take user name and password and send them to AREA LDAP plugin
	 *
	 * @param header HTTP string header
	 * @param request HTTP servlet request structure
	 * @param response HTTP response structure
	 * @return <code>UserCredentials</code> data structure
	 * @throws IOException is any I/O exception will occur
	 * @throws AREasyException if the authentication fails to trigger the again the basic authentication negotiation process
	 */
	private UserCredentials basicLogon(String header, HttpServletRequest request, HttpServletResponse response) throws IOException, AREasyException
	{
		StringTokenizer token = new StringTokenizer(header);
		String basic = token.nextToken();

		//validate again
		if(!basic.equalsIgnoreCase(HEADER_BASIC))
		{
			response.setHeader("WWW-Authenticate", HEADER_BASIC + " realm=\"BMC Remedy Mid-Tier Login\"");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setContentLength(0);

			logger.error("Additional authentication step required, returning token.");
			return null;
		}

		String credentials = token.nextToken();
		sun.misc.BASE64Decoder decoder = new sun.misc.BASE64Decoder();
		String userPass = new String(decoder.decodeBuffer(credentials));

		int p = userPass.indexOf(":");

		if (p != -1)
		{
			String username = userPass.substring(0, p);
			String password = userPass.substring(p + 1);

			//eliminate domain name from the user expression
			if(username.indexOf('\\') > 0) username = username.substring(username.indexOf('\\') + 1);

			logger.debug("Basic Authentication done");
			return new UserCredentials(username, password, null);
		}
		else
		{
			logger.error("Header is not currently supported. Authentication refused");
			return new UserCredentials(null, null, null);
		}
	}

	public static CallbackHandler getUsernamePasswordHandler(final String username, final String password)
	{
		final CallbackHandler handler = new CallbackHandler()
		{
			public void handle(final Callback[] callback)
			{
				for (int i=0; i<callback.length; i++)
				{
					if (callback[i] instanceof NameCallback)
					{
						final NameCallback nameCallback = (NameCallback) callback[i];
						nameCallback.setName(username);
					}
					else if (callback[i] instanceof PasswordCallback)
					{
						final PasswordCallback passCallback = (PasswordCallback) callback[i];
						passCallback.setPassword(password.toCharArray());
					}
					else  logger.warn("Unsupported Callback: i = " + i + "; class = " + callback[i].getClass().getName());
				}
			}
		};

		return handler;
	}

	@SuppressWarnings("unchecked")
	public void init(Map properties)
	{
		//super initialization
		super.init(properties);
	}

	public Boolean isBasicUsed()
	{
		return ssoConfig.isBasicUsed();
	}

	public Boolean isNtlmUsed()
	{
		return ssoConfig.isNtlmUsed();
	}

	public Boolean isKerberosUsed()
	{
		return ssoConfig.isKerberosUsed();
	}

	public Boolean isRedirectUsed()
	{
		return ssoConfig.isRedirectUsed();
	}

	public String getAllRedirectUrl()
	{
		return ssoConfig.getAllRedirectUrl();
	}

	public String getErrorsRedirectUrl()
	{
		return ssoConfig.getErrorsRedirectUrl();
	}

	public String getNtlmDomainName()
	{
		return ssoConfig.getNtlmDomainName();
	}

	public String getNtlmDomainControllerIP()
	{
		return ssoConfig.getNtlmDomainControllerIP();
	}

	public String getNtlmDomainControllerName()
	{
		return ssoConfig.getNtlmDomainControllerName();
	}

	public String getNtlmServiceAccount()
	{
		return ssoConfig.getNtlmServiceAccount();
	}

	public String getNtlmServicePassword()
	{
		return ssoConfig.getNtlmServicePassword();
	}

	public String getKerberosServiceAccount()
	{
		return ssoConfig.getKerberosServiceAccount();
	}

	public String getKerberosServicePassword()
	{
		return ssoConfig.getKerberosServicePassword();
	}

	public String getKerberosAuthMethod()
	{
		return ssoConfig.getKerberosAuthMethod();
	}

	public boolean isNtlmSecurityEnabled()
	{
		return ssoConfig.isNtlmSecurityEnabled();
	}

	public void redirectToUrl(HttpServletResponse response, String url) throws IOException
	{
		response.sendRedirect(response.encodeRedirectURL(url));
	}
}
