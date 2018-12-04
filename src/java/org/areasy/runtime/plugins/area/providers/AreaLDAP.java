package org.areasy.runtime.plugins.area.providers;

/*
 * Copyright (c) 2007-2018 AREasy Runtime
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

import com.bmc.arsys.api.ARException;
import com.bmc.arsys.pluginsvr.plugins.AREAResponse;
import com.bmc.arsys.pluginsvr.plugins.ARPluginContext;
import org.areasy.common.data.NumberUtility;
import org.areasy.common.data.StringUtility;
import org.areasy.common.data.type.credential.Credential;
import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;
import org.areasy.common.support.configuration.Configuration;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.runtime.engine.workflows.ProcessorLevel1Context;
import org.areasy.runtime.plugins.area.AbstractArea;

import javax.naming.CommunicationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

/**
 * AREA Plugin for LDAP Authentication.
 */
public class AreaLDAP extends AbstractArea
{
	/** LDAP Configuration */
	private List ldapConfig = null;

	public final String getSignature()
	{
		return "LDAP";
	}

	public void open(ARPluginContext context) throws ARException
	{
		ldapConfig = new Vector();
		Configuration config = getRuntimeArea().getManager().getConfiguration();

		if (StringUtility.isNotEmpty(config.getString("app.plugin.area.ldap.url", null)) || StringUtility.isNotEmpty(config.getString("app.plugin.area.ldap.1.url", null)))
		{
			int index = 0;
			boolean found = true;
			String suffix = "";

			while (found)
			{
				if(index > 0) suffix = index + ".";

				if(StringUtility.isNotEmpty(config.getString("app.plugin.area.ldap." + suffix + "url", null)))
				{
					AreaLDAPConfiguration ldap = AreaLDAPConfiguration.getInstance();

					ldap.LDAP_SERVERURLS = getRuntimeArea().getManager().getConfiguration().getStringArray("app.plugin.area.ldap." + suffix + "url", null);
					debug("LDAP server URL(s) [" + index + "]: " + StringUtility.join(ldap.LDAP_SERVERURLS, ", "));

					ldap.LDAP_BASEDN = getRuntimeArea().getManager().getConfiguration().getString("app.plugin.area.ldap." + suffix + "basedn", null);
					ldap.LDAP_BASEDN = StringUtility.replaceChars(ldap.LDAP_BASEDN, "\\", "");
					debug("LDAP binding base DN [" + index + "]: " + ldap.LDAP_BASEDN);

					ldap.LDAP_USERNAME = getRuntimeArea().getManager().getConfiguration().getString("app.plugin.area.ldap." + suffix + "username", null);
					debug("LDAP binding user name [" + index + "]: " + ldap.LDAP_USERNAME);

					ldap.LDAP_PASSWORD = getRuntimeArea().getManager().getConfiguration().getString("app.plugin.area.ldap." + suffix + "password", null);
					ldap.LDAP_PASSWORD = Credential.getCredential(ldap.LDAP_PASSWORD).decode();
					debug("LDAP binding user password [" + index + "]: ********");

					ldap.LDAP_FILTER = getRuntimeArea().getManager().getConfiguration().getString("app.plugin.area.ldap." + suffix + "bind.filter", null);
					debug("LDAP binding filter [" + index + "]: " + ldap.LDAP_FILTER);

					ldap.LDAP_KEYSTORE = getRuntimeArea().getManager().getConfiguration().getString("app.plugin.area.ldap." + suffix + "bind.keystore", null);
					debug("LDAP binding SSL keystore [" + index + "]: " + ldap.LDAP_KEYSTORE);

					ldap.LDAP_TIMEOUT = getRuntimeArea().getManager().getConfiguration().getInt("app.plugin.area.ldap." + suffix + "bind.timeout", 0);
					debug("AREA LDAP timeout [" + index + "]: " + ldap.LDAP_TIMEOUT);

					ldap.LDAP_ATTRIBUTES = getRuntimeArea().getManager().getConfiguration().getStringArray("app.plugin.area.ldap." + suffix + "bind.attributes", null);
					debug("AREA LDAP attributes [" + index + "]: " + StringUtility.join(ldap.LDAP_ATTRIBUTES, ", "));

					if (ldap.verify()) ldapConfig.add(ldap);
					found = true;
				}
				else
				{
					if(index == 0) found = true;
						else found = false;
				}

				index++;
			}
		}
		else
		{
			int index = 0;
			boolean found = true;
			String suffix = "";

			while (found)
			{
				if (index > 0) suffix = "_" + index;

				if (context.getARConfigEntry("AREA-LDAP-Hostname" + suffix) != null) found = true;
					else found = false;

				if (found)
				{
					AreaLDAPConfiguration ldap = AreaLDAPConfiguration.getInstance();

					ldap.LDAP_SERVERURLS = StringUtility.split(context.getARConfigEntry("AREA-LDAP-Hostname" + suffix));
					String SSL = context.getARConfigEntry("AREA-LDAP-UseSSL" + index);
					String port = context.getARConfigEntry("AREA-LDAP-Port" + suffix);
					if (port == null)
					{
						if (StringUtility.equals(SSL, "T")) port = "636";
							else port = "389";
					}

					for(int i = 0; i < ldap.LDAP_SERVERURLS.length; i++) ldap.LDAP_SERVERURLS[i] = "ldap://" + ldap.LDAP_SERVERURLS[i] + ":" + port;
					debug("AREA LDAP URL(s) [" + index + "]: " + StringUtility.join(ldap.LDAP_SERVERURLS, ", "));

					ldap.LDAP_USERNAME = context.getARConfigEntry("AREA-LDAP-Bind-User" + suffix);
					debug("AREA LDAP binding user name [" + index + "]: " + ldap.LDAP_USERNAME);

					ldap.LDAP_PASSWORD = context.getARConfigEntry("AREA-LDAP-Bind-Password" + suffix);
					try { ldap.LDAP_PASSWORD = ProcessorLevel1Context.decryptARPassword(ldap.LDAP_PASSWORD); } catch (Exception e) { /* nothing here */ }
					debug("AREA LDAP binding user password [" + index + "]: ********");

					ldap.LDAP_BASEDN = context.getARConfigEntry("AREA-LDAP-User-Base" + suffix);
					ldap.LDAP_BASEDN = StringUtility.replaceChars(ldap.LDAP_BASEDN, "\\", "");
					debug("AREA LDAP binding base DN [" + index + "]: " + ldap.LDAP_BASEDN);

					ldap.LDAP_FILTER = context.getARConfigEntry("AREA-LDAP-User-Filter" + suffix);
					debug("AREA LDAP binding filter [" + index + "]: " + ldap.LDAP_FILTER);

					if (StringUtility.equals(SSL, "T"))
					{
						ldap.LDAP_KEYSTORE = context.getARConfigEntry("AREA-LDAP-Cert-DB" + suffix);
						debug("AREA LDAP SSL keystore [" + index + "]: " + ldap.LDAP_KEYSTORE);
					}

					ldap.LDAP_TIMEOUT = NumberUtility.toInt(context.getARConfigEntry("AREA-LDAP-Connect-Timeout" + suffix), 0);
					debug("AREA LDAP timeout [" + index + "]: " + ldap.LDAP_TIMEOUT);

					if (ldap.verify()) ldapConfig.add(ldap);
					index++;
				}
			}
		}

		//validate configuration
		if (ldapConfig == null || ldapConfig.isEmpty()) warn(context, "No AREA configuration defined");
	}

	protected final int call(ARPluginContext context, AREAResponse response, String user, String password, String networkAddress, String authString) throws ARException
	{
		String message = null;
		debug("Run authentication for user: " + user + "@" + networkAddress);

		//check user name
		if (StringUtility.isEmpty(user))
		{
			authFailedAnswer(response, "[1011] User name is null", user);
			return 1011;
		}

		//check password
		if (StringUtility.isEmpty(password))
		{
			authFailedAnswer(response, "[1012] Password is null", user);
			return 1012;
		}

		for (int i = 0; i < ldapConfig.size(); i++)
		{
			AreaLDAPConfiguration ldap = (AreaLDAPConfiguration) ldapConfig.get(i);

			try
			{
				String dn = bind(ldap, user);

				if (StringUtility.isNotEmpty(dn))
				{
					try
					{
						//SDA - workaround to fix authentication over midtier(s) when the password contains special chars.
						String tmpPassword = password;
						verify(ldap, dn, tmpPassword);

						authSucceededAnswer(response, null, user);
						return 0;
					}
					catch (AREasyException are)
					{
						warn(are.getMessage());

						String data = are.getMessage();
						if (StringUtility.isNotEmpty(data))
						{
							int index = data.indexOf("comment:");

							if (index > 0)
							{
								String comment = data.substring(index + "comment:".length());
								if (StringUtility.contains(comment, "data 52e")) message = "Wrong password";
								else if (StringUtility.contains(comment, "data 525")) message = "User not found";
								else if (StringUtility.contains(comment, "data 530")) message = "Not permitted to logon at this time";
								else if (StringUtility.contains(comment, "data 531")) message = "Not permitted to logon at this workstation";
								else if (StringUtility.contains(comment, "data 532")) message = "Password expired";
								else if (StringUtility.contains(comment, "data 533")) message = "Account disabled";
								else if (StringUtility.contains(comment, "data 701")) message = "Account expired";
								else if (StringUtility.contains(comment, "data 773")) message = "User must reset the password";
								else if (StringUtility.contains(comment, "data 775")) message = "User account locked";
							}
						}

						if (message == null) message = "[1015] Invalid username or password";
							else message = "[1015]" + message;

						authFailedAnswer(response, message, user);
						return 1015;
					}
				}
				else
				{
					authFailedAnswer(response, "[1014] No valid for LDAP configuration(s)", user);
				}
			}
			catch (AREasyException are)
			{
				debug(are);

				warn(context, "Invalid authentication: " + are.getMessage());
				authFailedAnswer(response, "[1013] No valid authentication domain", user);

				return 1013;
			}
		}

		return 1014;
	}

	/**
	 * Check if an entry exist in the LDAP server and returns the DN value
	 *
	 * @param ldap LDAP context
	 * @param user username to compose a string filter
	 * @return the distinguish name of the found entry in LDAP or null in case of no entry is found
	 * @throws AREasyException in case of any error will occur
	 */
	private String bind(AreaLDAPConfiguration ldap, String user) throws AREasyException
	{
		return bind(ldap, user, true);
	}

	/**
	 * Check if an entry exist in the LDAP server and returns the DN value
	 *
	 * @param ldap LDAP context
	 * @param user username to compose a string filter
	 * @param throwable specify if <code>reset communication error</code> is handled or not.
	 * 					This is defined to avoid infinite loop handling this type of exceptions.
	 * @return the distinguish name of the found entry in LDAP or null in case of no entry is found
	 * @throws AREasyException in case of any error will occur
	 */
	private String bind(AreaLDAPConfiguration ldap, String user, boolean throwable) throws AREasyException
	{
		String dn = null;
		LdapContext context = ldap.getContext();
		String attributes[] = ldap.LDAP_ATTRIBUTES;

		if(attributes == null) attributes = new String[]{"dn", "distinguishedName", "uid", "cn"};

		if (context == null) throw new AREasyException("LDAP context is null");

		try
		{
			SearchControls search = new SearchControls();
			search.setReturningAttributes(attributes);
			search.setSearchScope(SearchControls.SUBTREE_SCOPE);

			String filter = StringUtility.replace(ldap.LDAP_FILTER, "%USER%", user);
			filter = StringUtility.replace(StringUtility.replace(filter, "$USER$", user), "$\\USER$", user);

			NamingEnumeration answer = context.search(ldap.LDAP_BASEDN, filter, search);

			while (answer.hasMoreElements())
			{
				SearchResult result = (SearchResult) answer.next();

				int index = 0;
				boolean found = false;

				while (!found && index < attributes.length)
				{
					Attribute attr = result.getAttributes().get(attributes[index]);
					debug("Looking for LDAP attribute: get(" + attributes[index] + ") = " + attr);

					if(attr!= null)
					{
						dn = attr.get().toString();
						found = true;
					}

					index++;
				}
			}

			debug("Searching in " + context.getEnvironment().get(Context.PROVIDER_URL) + ", using filter '" + filter + "' was found the following DN: " + dn);
		}
		catch (CommunicationException ce)
		{
			if(throwable)
			{
				logger.debug("Reinitializing LDAP context because of a communication error: " + ce.getMessage());
				ldap.close();

				return bind(ldap, user, false);
			}
			else throw new AREasyException(ce);
		}
		catch (NamingException ne)
		{
			throw new AREasyException(ne);
		}
		catch (Throwable th)
		{
			throw new AREasyException(th);
		}

		return dn;
	}

	/**
	 * Verify user authorization in LDAP
	 *
	 * @param ldap     ldap configuration
	 * @param dn       user DN
	 * @param password user password
	 * @throws AREasyException in case of any error will occur
	 */
	private void verify(AreaLDAPConfiguration ldap, String dn, String password) throws AREasyException
	{
		LdapContext context = null;
		Hashtable env = new Hashtable();

		if (ldap == null || ldap.getContext() == null) throw new AREasyException("LDAP context is null");

		try
		{
			env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
			env.put(Context.PROVIDER_URL, ldap.getContext().getEnvironment().get(Context.PROVIDER_URL));
			env.put(Context.SECURITY_AUTHENTICATION, "simple");
			env.put(Context.SECURITY_PRINCIPAL, dn);
			env.put(Context.SECURITY_CREDENTIALS, password);
			env.put(Context.AUTHORITATIVE, "true");
			env.put(Context.REFERRAL, "follow");

			if (StringUtility.isNotEmpty(ldap.LDAP_KEYSTORE))
			{
				AreaLDAPSSLFactory.setKeyStorePath(ldap.LDAP_KEYSTORE);
				env.put("java.naming.security.protocol", "ssl");
				env.put("java.naming.ldap.factory.socket", AreaLDAPSSLFactory.class.toString());
			}

			if(ldap.LDAP_TIMEOUT > 0) env.put("com.sun.jndi.ldap.connect.timeout", Integer.toString(ldap.LDAP_TIMEOUT * 1000));
		}
		catch (NamingException ne)
		{
			throw new AREasyException(ne);
		}

		try
		{
			context = new InitialLdapContext(env, null);
			debug("Authentication succeeded of DN '" + dn + "' to LDAP " + env.get(Context.PROVIDER_URL));
		}
		catch (NamingException ne)
		{
			try
			{
				env.put(Context.SECURITY_CREDENTIALS, java.net.URLEncoder.encode(password));

				context = new InitialLdapContext(env, null);
				debug("Authentication succeeded of DN '" + dn + "' to LDAP " + env.get(Context.PROVIDER_URL));
			}
			catch (NamingException nee)
			{
				throw new AREasyException("Error performing authentication of DN '" + dn + "' to LDAP " + env.get(Context.PROVIDER_URL) + ": " + nee.getMessage(), nee);
			}
		}
		finally
		{
			if (context != null)
			{
				debug("Closing LDAP context after user verification");

				try
				{
					context.close();
				}
				catch (Throwable th)
				{ /* nothing here */ }
				context = null;
			}
		}
	}
}

class AreaLDAPConfiguration
{
	/** Library logger */
	protected static Logger logger = LoggerFactory.getLog(AreaLDAPConfiguration.class);

	private LdapContext context = null;

	public String LDAP_SERVERURLS[] = null;
	public String LDAP_BASEDN = null;
	public String LDAP_USERNAME = null;
	public String LDAP_PASSWORD = null;
	public String LDAP_FILTER = null;
	public String LDAP_KEYSTORE = null;
	public String[] LDAP_ATTRIBUTES = null;
	public int LDAP_TIMEOUT = 0;

	private AreaLDAPConfiguration()
	{
		LDAP_SERVERURLS = null;
		LDAP_BASEDN = null;
		LDAP_USERNAME = null;
		LDAP_PASSWORD = null;
		LDAP_FILTER = null;
		LDAP_KEYSTORE = null;
		LDAP_TIMEOUT = 0;
	}

	public static AreaLDAPConfiguration getInstance()
	{
		return new AreaLDAPConfiguration();
	}

	public LdapContext getContext()
	{
		if (context == null) initContext();

		return context;
	}

	private void setContext(LdapContext context)
	{
		this.context = context;
	}

	/**
	 * Initialize LDAP configuration with LDAP contexts.
	 */
	private void initContext()
	{
		int index = 0;
		String url = null;
		boolean found = false;

		while (index < LDAP_SERVERURLS.length && !found)
		{
			url = LDAP_SERVERURLS[index];
			logger.debug("Open LDAP: " + url);

			try
			{
				Hashtable env = new Hashtable();
				env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
				env.put(Context.PROVIDER_URL, url);
				env.put(Context.REFERRAL, "follow");
				env.put(Context.SECURITY_AUTHENTICATION, "simple");
				if (StringUtility.isNotEmpty(LDAP_USERNAME)) env.put(Context.SECURITY_PRINCIPAL, LDAP_USERNAME);
				if (StringUtility.isNotEmpty(LDAP_PASSWORD)) env.put(Context.SECURITY_CREDENTIALS, LDAP_PASSWORD);
				if (StringUtility.isNotEmpty(LDAP_KEYSTORE))
				{
					AreaLDAPSSLFactory.setKeyStorePath(LDAP_KEYSTORE);
					env.put("java.naming.security.protocol", "ssl");
					env.put("java.naming.ldap.factory.socket", AreaLDAPSSLFactory.class.toString());
				}

				if(LDAP_TIMEOUT > 0) env.put("com.sun.jndi.ldap.connect.timeout", Integer.toString(LDAP_TIMEOUT * 1000));

				context = new InitialLdapContext(env, null);
				found = true;

				logger.debug("LDAP context initiation for provider: " + context.getEnvironment().get(Context.PROVIDER_URL));
			}
			catch (NamingException ne)
			{
				logger.error("Error LDAP context initialization: " + ne.getMessage());
				logger.debug("Exception", ne);

				context = null;
				found = false;
			}

			index++;
		}
	}

	/**
	 * Check if the actual LDAP configuration is valid.
	 *
	 * @return true if the configuration could initiate a context.
	 */
	public boolean verify()
	{
		if (context == null) initContext();

		if (context != null) return true;
		else
		{
			logger.error("Invalid LDAP server configuration and it will be ignored: " + StringUtility.join(LDAP_SERVERURLS));
			return false;
		}
	}

	public void reset()
	{
		setContext(null);
	}

	public void close()
	{
		if (context != null)
		{
			logger.debug("Closing LDAP context: " + context);

			try
			{
				context.close();
			}
			catch (Throwable th)
			{ /* nothing here */ }

			context = null;
		}
	}

	public String toString()
	{
		if(context != null)
		{
			return StringUtility.join(LDAP_SERVERURLS, ", ") + " > [" + LDAP_USERNAME + "] in [" + LDAP_BASEDN + "]";
		}
		else return super.toString();
	}
}

class AreaLDAPSSLFactory extends AreaLDAPSSLSocketFactoryBase
{
	private static String keyStorePath;
	private static String storePass;

	private AreaLDAPSSLFactory(String keyStorePath, String storePass) throws Exception
	{
		super(keyStorePath, storePass);
	}

	public static SSLSocketFactory getDefault()
	{
		try
		{
			return new AreaLDAPSSLFactory(keyStorePath, storePass);
		}
		catch (Throwable e)
		{
			throw new RuntimeException(e);
		}
	}

	public static void setKeyStorePath(String keyPath)
	{
		keyStorePath = keyPath;
	}

	public static void setStorePassword(String storePassword)
	{
		storePass = storePassword;
	}
}

class AreaLDAPSSLSocketFactoryBase extends SSLSocketFactory
{
	private SSLSocketFactory sslSocketFactory;

	public AreaLDAPSSLSocketFactoryBase(String keyStorePath, String storePass) throws Exception
	{
		this.sslSocketFactory = getSSLSocketFactory(keyStorePath, storePass);
	}

	private static SSLSocketFactory getSSLSocketFactory(String keyStorePath, String storePass) throws Exception
	{
		KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
		char[] password = null;

		if (storePass != null) password = storePass.toCharArray();

		FileInputStream fis = new FileInputStream(keyStorePath);
		ks.load(fis, password);
		fis.close();

		TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		tmf.init(ks);

		SSLContext sslCtx = SSLContext.getInstance("ssl");
		sslCtx.init(null, tmf.getTrustManagers(), new SecureRandom());

		return sslCtx.getSocketFactory();
	}

	public Socket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException
	{
		this.sslSocketFactory.createSocket(s, host, port, autoClose);

		return this.sslSocketFactory.createSocket(s, host, port, autoClose);
	}

	public String[] getDefaultCipherSuites()
	{
		return this.sslSocketFactory.getDefaultCipherSuites();
	}

	public String[] getSupportedCipherSuites()
	{
		return this.sslSocketFactory.getSupportedCipherSuites();
	}

	public Socket createSocket(String host, int port) throws IOException, UnknownHostException
	{
		return this.sslSocketFactory.createSocket(host, port);
	}

	public Socket createSocket(InetAddress addr, int port) throws IOException
	{
		return this.sslSocketFactory.createSocket(addr, port);
	}

	public Socket createSocket(String host, int port, InetAddress addr, int lport) throws IOException, UnknownHostException
	{
		return this.sslSocketFactory.createSocket(addr, port, addr, lport);
	}

	public Socket createSocket(InetAddress addr, int port, InetAddress laddr, int lport) throws IOException
	{
		return this.sslSocketFactory.createSocket(addr, port, laddr, lport);
	}
}