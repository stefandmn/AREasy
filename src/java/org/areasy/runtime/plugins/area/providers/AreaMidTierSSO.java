package org.areasy.runtime.plugins.area.providers;

/*
 * Copyright (c) 2007-2016 AREasy Runtime
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
import org.areasy.runtime.plugins.area.AbstractArea;
import org.areasy.common.data.StringUtility;
import org.areasy.common.data.type.credential.Credential;
import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;

/**
 * AREA Plugin for MidTier Single Sign On.
 */
public class AreaMidTierSSO extends AbstractArea
{
	/** Library logger */
	protected static Logger logger = LoggerFactory.getLog(AreaMidTierSSO.class);
	
	private static String PASS_STRING = null;

	public final String getSignature()
	{
		return "MIDTIER.SSO";
	}

	public void open(ARPluginContext context) throws ARException
	{
		try
		{
            //authentication string password
			PASS_STRING = Credential.getCredential(getRuntimeArea().getManager().getConfiguration().getString("app.plugin.area.authentication_string", null), null).decode();
			debug("Authentication string: " + PASS_STRING);
		}
		catch(Exception e)
		{
			logger.error("Error reading SSO string authentication key: " + e.getMessage());
			logger.debug("Exception", e);
		}
	}	

	protected int call(ARPluginContext context, AREAResponse response, String user, String password, String networkAddress, String authString) throws ARException
	{
		debug("Run authentication for user: " + user + "@" + networkAddress);
		
		//check user name - ID no username the login dialog will be prompted
		if (StringUtility.isEmpty(user))
		{
			authFailedAnswer(response, "[2011] User name is null", user);
			return 2011;
		}

		//check authentication string - Misconfiguration of midtier: No pairing authentication key is specified
		if (StringUtility.isEmpty(authString))
		{
			authFailedAnswer(response, "[2013] Authentication string is null", user);
			return 2013;
		}

		//validation
		if (StringUtility.isEmpty(password) && StringUtility.equals(PASS_STRING, authString))
		{
			authSucceededAnswer(response, null, user);
			return 0;
		}
		else if (StringUtility.isNotEmpty(user) && StringUtility.isNotEmpty(password))
		{
			authFailedAnswer(response, "[2014] User request authentication with specific credentials", user);
			return 2014;
		}
		else
		{
			authFailedAnswer(response, "[-2015] Invalid authentication through MidTier SSO", user);
			return -2015;
		}
	}
}
