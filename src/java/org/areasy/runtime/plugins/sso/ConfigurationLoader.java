package org.areasy.runtime.plugins.sso;

/*
 * Copyright (c) 2007-2020 AREasy Runtime
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

import com.remedy.arsys.session.UserCredentials;
import org.areasy.common.data.StringUtility;
import org.areasy.common.data.type.credential.Credential;
import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;
import org.areasy.common.support.configuration.Configuration;
import org.areasy.common.support.configuration.providers.properties.stream.PropertiesConfiguration;
import org.areasy.runtime.RuntimeManager;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.runtime.engine.base.ServerConnection;
import org.areasy.runtime.engine.structures.CoreItem;

import java.io.File;
import java.util.Hashtable;

/**
 * SSO configuration.
 */
public class ConfigurationLoader extends Thread
{
	/** Logger instance */
	private static Logger logger = LoggerFactory.getLog(ConfigurationLoader.class);

	/** The default delay between every configFile modification execute, set to 60 seconds. */
	static final public long DEFAULT_DELAY 			= 30000;

	public final String CONFIG_AUTHSTRING			= "app.plugin.area.authentication_string";

	public final String CONFIG_USEBASIC				= "app.plugin.area.sso.use_basicauth";
	public final String CONFIG_USENTLM				= "app.plugin.area.sso.use_ntlmauth";
	public final String CONFIG_USEKERBEROS			= "app.plugin.area.sso.use_kerberosauth";
	public final String CONFIG_NTLMDOMNAME 			= "app.plugin.area.sso.ntlm.domain_name";
	public final String CONFIG_NTLMDOMCTRLIP 		= "app.plugin.area.sso.ntlm.domain_controller_ip";
	public final String CONFIG_NTLMDOMCTRLNAME 		= "app.plugin.area.sso.ntlm.domain_controller_name";
	public final String CONFIG_NTLMSRVACCOUNT 		= "app.plugin.area.sso.ntlm.service_account";
	public final String CONFIG_NTLMSRVPASS			= "app.plugin.area.sso.ntlm.service_password";
	public final String CONFIG_KERBAUTHTYPE 		= "app.plugin.area.sso.kerberos.auth_method";
	public final String CONFIG_KERBSRVNAME			= "app.plugin.area.sso.kerberos.service_account";
	public final String CONFIG_KERBSRVPASS			= "app.plugin.area.sso.kerberos.service_password";

	public final String CONFIG_USENTLMSEC 			= "app.plugin.area.sso.filter.ntlm_sec.enabled";
	public final String CONFIG_USEREDIRECT			= "app.plugin.area.sso.filter.redirect.enabled";
	public final String CONFIG_REDIRECTALL			= "app.plugin.area.sso.filter.redirect.all.url";
	public final String CONFIG_REDIRECTERRORS 		= "app.plugin.area.sso.filter.redirect.errors.url";

	public final String CONFIG_VALIDENABLED			= "app.plugin.area.sso.validation.enabled";
	public final String CONFIG_VALIDMAPPINGFORM		= "app.plugin.area.sso.validation.mapping.form";
	public final String CONFIG_VALIDMAPPINGQUERY 	= "app.plugin.area.sso.validation.mapping.query";
	public final String CONFIG_VALIDMAPPINGOUTPUT 	= "app.plugin.area.sso.validation.mapping.output";

	private String ntlmDomainName = null;
	private String ntlmDomainControllerIP = null;
	private String ntlmDomainControllerName = null;
	private String ntlmServiceAccount = null;
	private String ntlmServicePassword = null;
	private boolean ntlmSecurityEnabled = false;

	private String kerberosServiceAccount = null;
	private String kerberosServicePassword = null;
	private String kerberosAuthMethod = null;
	
	private Boolean useBasic = false;
	private Boolean useNtlm = false;
	private Boolean useKerberos = false;

	private Boolean redirectEnabled = false;
	private String allRedirectUrl = null;
	private String errorsRedirectUrl = null;

	private Boolean validationEnabled = false;
	private String validationMappingForm = null;
	private String validationMappingQuery = null;
	private String validationMappingOutput = null;

	/** ARS Target server connection */
	private static ServerConnection arServer = null;
	private static Hashtable arCache = new Hashtable();

	private File configFile;
	private long lastModify = 0;
	private boolean interrupted = false;
	private RuntimeManager manager = null;

	/** Authentication string for process validation */
	private String uniqueAuthString = null;

	public ConfigurationLoader(Configuration config)
	{
		try
		{
			manager = RuntimeManager.getManager();
			manager.setConfiguration(config);
			logger.info("AREasy Runtime Manager has been initialized");
		}
		catch(Throwable th)
		{
			logger.debug("Exception", th);
			throw new RuntimeException("AREasy Runtime Manager couldn't be (re)initialized: "  + th.getMessage());
		}
	}

	public ConfigurationLoader(File file)
	{
		if(file != null && file.exists() && (file.isFile() || file.isDirectory()))
		{
			try
			{
				manager = RuntimeManager.getManager(file.getCanonicalPath());
				logger.info("AREasy Runtime Manager has been initialized");
			}
			catch(Throwable th)
			{
				logger.debug("Exception", th);
				throw new RuntimeException("AREasy Runtime Manager couldn't be (re)initialized: "  + th.getMessage());
			}
		}
		else throw new RuntimeException("AREasy Runtime Manager initialization error because home path is not defined");

		if(file.isDirectory()) file = new File(RuntimeManager.getCfgDirectory(), "plugin.areasy.properties");
		setFile(file);
	}

	public void setFile(File file)
	{
		if(file != null && file.isFile())
		{
			configFile = file;

			setDaemon(true);
			execute();
		}
	}

	protected void execute()
	{
		if (configFile != null && configFile.exists() && configFile.isFile())
		{
			long l = configFile.lastModified();

			if (l > lastModify)
			{
				if(lastModify > 0)
				{
					logger.info("Reloading AREasy SSO configuration based on watchdog signalling");

					try
					{
						getManager().setConfiguration(new PropertiesConfiguration(configFile.getCanonicalPath()));
					}
					catch(Throwable th)
					{
						logger.error("Error reloading server configuration: " + th.getMessage());
					}
				}
				else logger.info("Loading AREasy SSO configuration");

				lastModify = l;
				loadConfiguration();
			}
		}
		else
		{
			interrupted = true;
		}
	}

	public void run()
	{
		while (!interrupted)
		{
			try
			{
				Thread.currentThread().sleep(DEFAULT_DELAY);
			}
			catch (InterruptedException e)  { /* no interruption expected */  }

			execute();
		}
	}

	public void loadConfiguration()
	{
		//authentication string
		if (getManager().getConfiguration().containsKey(CONFIG_AUTHSTRING))
		{
			uniqueAuthString = Credential.getCredential(getManager().getConfiguration().getString(CONFIG_AUTHSTRING, null), null).decode();
			logger.debug("MidTier 'authentication_string' value: " + (logger.isTraceEnabled() ? uniqueAuthString : "********"));
		}
		else throw new RuntimeException("The MidTier doesn't have a authentication_string configured in the corresponding configuration configFile. It will fail authenticating users");

		useBasic = getManager().getConfiguration().getBoolean(CONFIG_USEBASIC, false);
		logger.info("SSO uses BASIC authentication: " + useBasic);

		useNtlm = getManager().getConfiguration().getBoolean(CONFIG_USENTLM, false);
		logger.info("SSO uses NTLM authentication: " + useNtlm);

		useKerberos = getManager().getConfiguration().getBoolean(CONFIG_USEKERBEROS);
		logger.info("SSO uses Kerberos authentication: " + useKerberos);

		if(isNtlmUsed())
		{
			ntlmDomainName = getManager().getConfiguration().getString(CONFIG_NTLMDOMNAME, null);
			logger.info("SSO NTLM domain name: " + ntlmDomainName);

			ntlmDomainControllerIP = getManager().getConfiguration().getString(CONFIG_NTLMDOMCTRLIP, null);
			logger.info("SSO NTLM domain controller IP: " + ntlmDomainControllerIP);

			ntlmDomainControllerName = getManager().getConfiguration().getString(CONFIG_NTLMDOMCTRLNAME, null);
			logger.info("SSO NTLM domain controller Name: " + ntlmDomainControllerName);

			ntlmServiceAccount = getManager().getConfiguration().getString(CONFIG_NTLMSRVACCOUNT, null);
			logger.info("SSO NTLM service account: " + ntlmServiceAccount);

			try
			{
				ntlmServicePassword = getManager().getConfiguration().getString(CONFIG_NTLMSRVPASS, null);
				if(ntlmServicePassword != null) ntlmServicePassword = Credential.getCredential(ntlmServicePassword, null).decode();
				logger.debug("SSO NTLM service account password: " + (logger.isTraceEnabled() ? ntlmServicePassword : "********"));
			}
			catch (Exception e)
			{
				logger.error("Error decrypting '" + CONFIG_NTLMSRVPASS + "': " + e.getMessage());
				logger.debug("Exception", e);
			}

			ntlmSecurityEnabled = getManager().getConfiguration().getBoolean(CONFIG_USENTLMSEC, false);
			logger.info("SSO uses NTLM security filter: " + isNtlmSecurityEnabled());
		}

		if(isKerberosUsed())
		{
			kerberosAuthMethod = getManager().getConfiguration().getString(CONFIG_KERBAUTHTYPE, "SPN");
			logger.info("SSO Kerberos authentication method: " + kerberosAuthMethod);

			kerberosServiceAccount = getManager().getConfiguration().getString(CONFIG_KERBSRVNAME, null);
			logger.info("SSO Kerberos service account name: " + kerberosServiceAccount);

			try
			{
				kerberosServicePassword = getManager().getConfiguration().getString(CONFIG_KERBSRVPASS, null);
				if(kerberosServicePassword != null) kerberosServicePassword = Credential.getCredential(kerberosServicePassword, null).decode();
				logger.debug("Reading configuration - kerberos service account password: " + (logger.isTraceEnabled() ? kerberosServicePassword : "********"));
			}
			catch (Exception e)
			{
				logger.error("Error decrypting '" + CONFIG_KERBSRVPASS + "': " + e.getMessage());
				logger.debug("Exception", e);
			}
		}

		redirectEnabled = getManager().getConfiguration().getBoolean(CONFIG_USEREDIRECT, false);
		logger.info("SSO uses redirect filter: " + isRedirectUsed());

		if(isRedirectUsed())
		{
			allRedirectUrl = getManager().getConfiguration().getString(CONFIG_REDIRECTALL, null);
			logger.info("SSO redirect for all: " + getAllRedirectUrl());

			errorsRedirectUrl = getManager().getConfiguration().getString(CONFIG_REDIRECTERRORS, null);
			logger.info("SSO redirect for on error: " + getErrorsRedirectUrl());
		}

		validationEnabled = getManager().getConfiguration().getBoolean(CONFIG_VALIDENABLED, false);
		logger.info("SSO uses user validation: " + isValidationUsed());

		if(isValidationUsed())
		{
			validationMappingForm = getManager().getConfiguration().getString(CONFIG_VALIDMAPPINGFORM, "User");
			logger.info("SSO validation mapping form: " + getValidationMappingForm());

			validationMappingQuery = getManager().getConfiguration().getString(CONFIG_VALIDMAPPINGQUERY, "'103'=\"$USER$\"");
			logger.info("SSO validation mapping query: " + getValidationMappingQuery());

			validationMappingOutput = getManager().getConfiguration().getString(CONFIG_VALIDMAPPINGOUTPUT, "101");
			logger.info("SSO validation mapping output: " + getValidationMappingOutput());
		}
	}

	protected boolean isUserChecked(String user) throws AREasyException
	{
		if(arCache != null && user != null)
		{
			return arCache.containsKey(user.toLowerCase());
		}

		return false;
	}

	protected UserCredentials checkUser(UserCredentials user) throws AREasyException
	{
		if(user != null && user.getUser() != null)
		{
			String userKey = user.getUser().toLowerCase();
			logger.debug("Validating user: " + userKey);

			if(arCache.containsKey(userKey))
			{
				logger.debug("Found user in cache: " + arCache.get(userKey));
				return new UserCredentials(arCache.get(userKey).toString(), user.getPassword(), user.getAuthentication());
			}
			else
			{
				if(isValidationUsed())
				{
					if(arServer == null)
					{
						String arserver		= manager.getConfiguration().getString("app.server.default.arsystem.server.name", "localhost");
						int arport			= manager.getConfiguration().getInt("app.server.default.arsystem.port.number", 0);
						int rpcqueue 		= manager.getConfiguration().getInt("app.server.default.arsystem.rpc.queue", 0);
						String aruser		= manager.getConfiguration().getString("app.server.default.arsystem.user.name", null);
						String arpassword	= manager.getConfiguration().getString("app.server.default.arsystem.user.password", null);
						String mode			= manager.getConfiguration().getString("app.server.default.arsystem.mode", "base");

						arServer = new ServerConnection();
						arServer.connect(arserver, aruser, arpassword, arport, rpcqueue, mode);
					}

					//user validation procedure
					if(arServer != null && arServer.isConnected())
					{
						CoreItem entry = new CoreItem( getValidationMappingForm() );
						entry.read(arServer, StringUtility.replace(getValidationMappingQuery(), "$USER$", user.getUser()));

						if(entry.exists())
						{
							logger.debug("Found user in AR System server: " + entry.toFullString());
							arCache.put(userKey, entry.getStringAttributeValue(getValidationMappingOutput()));

							return new UserCredentials(entry.getStringAttributeValue(getValidationMappingOutput()), user.getPassword(), user.getAuthentication());
						}
						else
						{
							logger.warn("No user found based on '" + userKey + "' input: " + entry.toFullString());
							return new UserCredentials(null, null, null);
						}
					}
				}
				else
				{
					arCache.put(userKey, userKey);
				}
			}
		}

		return user;
	}

	/**
	 * Get runtime manager configuration structure.
	 *
	 * @return configuration structure
	 */
	protected RuntimeManager getManager()
	{
		return manager;
	}

	/**
	 * Return unique authentication string configured in the configuration configFile.
	 *
	 * @return authentication string from configuration configFile.
	 */
	protected String getUniqueAuthString()
	{
		return uniqueAuthString;
	}
	
	public Boolean isBasicUsed()
	{
		return useBasic;
	}

	public Boolean isNtlmUsed()
	{
		return useNtlm;
	}

	public Boolean isKerberosUsed()
	{
		return useKerberos;
	}

	public Boolean isRedirectUsed()
	{
		return redirectEnabled;
	}

	public String getAllRedirectUrl()
	{
		return allRedirectUrl;
	}

	public String getErrorsRedirectUrl()
	{
		return errorsRedirectUrl;
	}

	public boolean isValidationUsed()
	{
		return validationEnabled;
	}

	public String getValidationMappingForm()
	{
		return validationMappingForm;
	}
	public String getValidationMappingQuery()
	{
		return validationMappingQuery;
	}

	public String getValidationMappingOutput()
	{
		return validationMappingOutput;
	}

	public String getNtlmDomainName()
	{
		return ntlmDomainName;
	}

	public String getNtlmDomainControllerIP()
	{
		return ntlmDomainControllerIP;
	}

	public String getNtlmDomainControllerName()
	{
		return ntlmDomainControllerName;
	}

	public String getNtlmServiceAccount()
	{
		return ntlmServiceAccount;
	}

	public String getNtlmServicePassword()
	{
		return ntlmServicePassword;
	}

	public String getKerberosServiceAccount()
	{
		return kerberosServiceAccount;
	}

	public String getKerberosServicePassword()
	{
		return kerberosServicePassword;
	}

	public String getKerberosAuthMethod()
	{
		return kerberosAuthMethod;
	}

	public boolean isNtlmSecurityEnabled()
	{
		return ntlmSecurityEnabled;
	}
}
