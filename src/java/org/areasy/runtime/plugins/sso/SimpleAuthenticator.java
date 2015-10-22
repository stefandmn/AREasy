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
import com.remedy.arsys.session.DefaultAuthenticator;
import com.remedy.arsys.session.HttpSessionKeys;
import com.remedy.arsys.session.UserCredentials;
import org.areasy.runtime.RuntimeManager;
import org.areasy.common.data.StringUtility;
import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;

/**
 * Remedy authenticator taking the username from the browser header and will consider it as a trust source.
 * This authentication should not be used in production system or only as a temporary authenticator.
 */
public class SimpleAuthenticator extends DefaultAuthenticator implements Authenticator
{
	public static final String HEADER_AUTH 				= "Authorization";
	public static final String HEADER_NEGOTIATE 		= "Negotiate";
	public static final String HEADER_NTLM 				= "NTLM";
	public static final String HEADER_BASIC 			= "Basic";
	public static final String CONFIG_HOME				= "areasy.home";

	/** Logger instance */
	protected static Logger logger = LoggerFactory.getLog(SimpleAuthenticator.class);

	protected String HOME = null;

	/** SSO configuration structure */
	protected static ConfigurationLoader ssoConfig = null;

	public UserCredentials getAuthenticatedCredentials(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		logger.debug("Credentials for '" + request.getRemoteUser() + "' remote user");

		if (request.getRemoteUser() == null) return new UserCredentials(null, null, null);
			else return new UserCredentials(request.getRemoteUser().toLowerCase(), null, getUniqueAuthString());
	}

	@SuppressWarnings("unchecked")
	public void init(Map properties)
	{
		if (properties != null && properties.get(CONFIG_HOME) != null)
		{
			HOME = (String) properties.remove(CONFIG_HOME);

			if(ssoConfig == null && HOME != null)
			{
				ssoConfig = new ConfigurationLoader(HOME);
				ssoConfig.start();
			}
		}
	}

	/**
	 * Get runtime manager configuration structure.
	 *
	 * @return configuration structure
	 */
	protected RuntimeManager getManager()
	{
		return ssoConfig.getManager();
	}

	/**
	 * Return unique authentication string configured in the configuration file.
	 *
	 * @return authentication string from configuration file.
	 */
	protected String getUniqueAuthString()
	{
		return ssoConfig.getUniqueAuthString();
	}

	/**
	 * Put in session an authenticator flag
	 *
	 * @param request HTTP ServletRequest structure
	 * @param user authenticated user credential structure
	 */
	protected void setAuthenticated(HttpServletRequest request, UserCredentials user)
	{
		if(user != null && user.getUser() != null)
		{
			HttpSession httpSession = request.getSession(true);
			httpSession.setAttribute(HttpSessionKeys.USER_CREDENTIALS, user);
		}
	}

	/**
	 * Check if user has already logged in through NTML negotiation
	 *
	 * @param request HTTP ServletRequest structure
	 *
	 * @return true is the user is authenticated
	 */
	protected boolean isAuthenticated(HttpServletRequest request)
	{
		HttpSession session = request.getSession(false);
		Object object = session.getAttribute(HttpSessionKeys.USER_CREDENTIALS);

		return object != null && object instanceof UserCredentials && ((UserCredentials) object).getUser() != null;
	}

	/**
	 * Ask HTTP response structure to start NTLM negotiation
	 *
	 * @param request HTTP servlet request parameter
	 * @param response HTTP servlet response structure
	 * @throws IOException if any I/O error will occur
	 */
	protected void askNtlmNegotiation(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		setAuthenticationMethod(request, HEADER_NTLM);
		logger.debug("Ask NTML Authenticator for URL: " + request.getRequestURL());

		response.setHeader("WWW-Authenticate", HEADER_NTLM);
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentLength(0);

		response.flushBuffer();
	}

	/**
	 * Ask HTTP response structure to start Kerberos negotiation
	 *
	 * @param request HTTP servlet request parameter
	 * @param response HTTP servlet response structure
	 * @throws IOException if any I/O error will occur
	 */
	protected void askKerberosNegotiation(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		setAuthenticationMethod(request, HEADER_NEGOTIATE);
		logger.debug("Ask Kerberos Authenticator for URL: " + request.getRequestURL());

		response.setHeader("WWW-Authenticate", HEADER_NEGOTIATE);
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentLength(0);

		response.flushBuffer();
	}

	/**
	 * Ask HTTP response structure to start Basic negotiation
	 *
	 * @param request HTTP servlet request parameter
	 * @param response HTTP servlet response structure
	 * @throws IOException if any I/O error will occur
	 */
	protected void askBasicNegotiation(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		setAuthenticationMethod(request, HEADER_BASIC);
		logger.debug("Ask Basic Authenticator for URL: " + request.getRequestURL());

		response.setHeader("WWW-Authenticate", HEADER_BASIC + " realm=\"BMC Remedy Mid-Tier Login\"");
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentLength(0);

		response.flushBuffer();
	}

	/**
	 * Put in the current session a special flag to say that was used the basic authentication method.
	 *
	 * @param method authentication method (NTLM, Kerberos (Negotiate) and Basic)
	 * @param request HTTP servlet request parameter
	 */
	private void setAuthenticationMethod(HttpServletRequest request, String method)
	{
		HttpSession session = request.getSession(true);
		session.setAttribute("AREasyAuthenticator.AuthMethod", method);
	}

	/**
	 * Check if the current user session used basic authentication
	 *
	 * @param request HTTP ServletRequest structure
	 * @return true is the user used previously basic authentication
	 */
	protected int getPreviousAuthenticationMethod(HttpServletRequest request)
	{
		int answer = 0;
		HttpSession session = request.getSession(true);
		Object object = session.getAttribute("AREasyAuthenticator.AuthMethod");

		if(object != null)
		{
			if (StringUtility.equalsIgnoreCase(HEADER_NTLM, (String)object)) answer = 1;
				else if (StringUtility.equalsIgnoreCase(HEADER_NEGOTIATE, (String)object)) answer = 2;
					else if (StringUtility.equalsIgnoreCase(HEADER_BASIC, (String)object)) answer = 3;

			logger.debug("Set Authentication method flag: " + object);
		}

		return answer;
	}

	public static boolean isConfigLoaded()
	{
		return ssoConfig != null;
	}
}
