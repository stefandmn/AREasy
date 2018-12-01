package org.areasy.runtime.plugins.sso.tools;

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

import org.areasy.runtime.plugins.sso.AbstractFilter;
import org.areasy.common.data.StringUtility;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RedirectFilter extends AbstractFilter
{
	public boolean filter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException
	{
		if(isRedirectUsed() && request.getRequestURI() != null && StringUtility.isNotEmpty(getAllRedirectUrl()))
		{
			if(!request.getRequestURI().contains(getAllRedirectUrl()) &&
				!request.getRequestURI().endsWith(".css") && !request.getRequestURI().endsWith(".js") &&
				!request.getRequestURI().endsWith(".jpg") && !request.getRequestURI().endsWith(".jpeg") &&
				!request.getRequestURI().endsWith(".png") && !request.getRequestURI().endsWith(".gif") &&
				!request.getRequestURI().endsWith(".ico") )
			{
				redirectToUrl(response, getAllRedirectUrl());
				return false;
			}
		}

		return true;
	}
}
