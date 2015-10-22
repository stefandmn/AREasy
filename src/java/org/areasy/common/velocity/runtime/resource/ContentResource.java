package org.areasy.common.velocity.runtime.resource;

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

import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;
import org.areasy.common.velocity.base.ResourceNotFoundException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringWriter;

/**
 * This class represent a general text resource that may have been
 * retrieved from any number of possible sources.
 * <p/>
 * Also of interest is Velocity's {@link org.areasy.common.velocity.base.Template}
 * <code>Resource</code>.
 *
 * @version $Id: ContentResource.java,v 1.1 2008/05/25 22:33:16 swd\stefan.damian Exp $
 */
public class ContentResource extends Resource
{
	/** the logger */
	private static Logger logger = LoggerFactory.getLog(ContentResource.class.getName());

	/**
	 * Default empty constructor
	 */
	public ContentResource()
	{
		//nothing to do
	}

	/**
	 * Pull in static content and store it.
	 *
	 * @throws org.areasy.common.velocity.base.ResourceNotFoundException Resource could not be found.
	 */
	public boolean process() throws ResourceNotFoundException
	{
		BufferedReader reader = null;

		try
		{
			StringWriter sw = new StringWriter();

			reader = new BufferedReader(new InputStreamReader(resourceLoader.getResourceStream(name), encoding));

			char buf[] = new char[1024];
			int len = 0;

			while ((len = reader.read(buf, 0, 1024)) != -1)
			{
				sw.write(buf, 0, len);
			}

			setData(sw.toString());

			return true;
		}
		catch (ResourceNotFoundException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			logger.error("Cannot process content resource : " + e.toString());
			logger.debug("Exception", e);
			
			return false;
		}
		finally
		{
			if (reader != null)
			{
				try
				{
					reader.close();
				}
				catch (Exception ignored)
				{
				}
			}
		}
	}
}
