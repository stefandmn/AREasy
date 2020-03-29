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

import org.areasy.common.data.StringUtility;
import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

public abstract class AbstractFilter extends SingleSignOnAuthenticator implements Filter
{
	/** Library logger */
	private static Logger logger = LoggerFactory.getLog(AbstractFilter.class);

	public final void init(FilterConfig config) throws ServletException
	{
		Properties map = new Properties();
		String filePath = config.getInitParameter("arsystem.authenticator.config.file");

		if (filePath != null)
		{
			if (!new File(filePath).exists()) filePath = config.getServletContext().getRealPath(filePath);
			logger.debug("Authenticator configuration file detected in Filter: " + filePath);
		}

		if(!isConfigLoaded())
		{
			logger.debug("Create Filter ConfigurationLoader using configuration file: " + filePath);
			ssoConfig = new ConfigurationLoader(new File(filePath));
			ssoConfig.start();
		}
		else
		{
			logger.debug("Update Filter ConfigurationLoader using configuration file: " + filePath);
			ssoConfig.setFile(new File(filePath));
			ssoConfig.start();
		}
	}

	public final void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException
	{
		try
		{
			if(filter((HttpServletRequest) req, (HttpServletResponse) res, chain))
			{
				chain.doFilter(req, res);
			}
		}
		catch(Throwable th)
		{
			if(!res.isCommitted() && isRedirectUsed() && StringUtility.isNotEmpty(getErrorsRedirectUrl()))
			{
				redirectToUrl( (HttpServletResponse) res, getErrorsRedirectUrl() );
			}
			else
			{
				logger.error("Error executing HTTP filters chain: " + th.getMessage());
				logger.trace("Exception", th);
			}
		}
	}

	public void destroy()
	{
		//nothing to do here
	}

	/**
	 * Implement AREasy filter based on HTTP content
	 *
	 * @param request http request structure
	 * @param response http response structure
	 * @param chain http filter chains
	 * @return tru if the chain has to be executed or not
	 * @throws IOException in case of any IOException will occur
	 * @throws ServletException in case of any ServletException will occur
	 */
	public abstract boolean filter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException;
}
