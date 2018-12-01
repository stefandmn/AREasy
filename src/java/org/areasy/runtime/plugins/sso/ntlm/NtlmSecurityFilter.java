package org.areasy.runtime.plugins.sso.ntlm;

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

import com.remedy.arsys.session.UserCredentials;
import org.areasy.runtime.plugins.sso.AbstractFilter;
import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class NtlmSecurityFilter extends AbstractFilter
{
	/** Library logger */
	private static Logger logger = LoggerFactory.getLog(NtlmSecurityFilter.class);

	public boolean filter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException
	{
		if(isNtlmSecurityEnabled())
		{
			String header = request.getHeader(HEADER_AUTH);

			if(header != null && header.startsWith(HEADER_NTLM))
			{
				if(isAuthenticated(request))
				{
					logger.debug("Ask NTLM Security Filter for URL: " + request.getRequestURL());
					UserCredentials user = getAuthenticatedCredentials(request, response, isNtlmUsed(), false, false);

					if(user == null) return false;
						else return true;
				}
			}
		}

		return true;
	}
}
