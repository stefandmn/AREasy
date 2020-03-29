package org.areasy.common.velocity.runtime.resource;

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

import org.areasy.common.velocity.base.ResourceNotFoundException;
import org.areasy.common.velocity.runtime.RuntimeService;
import org.areasy.common.velocity.runtime.parser.ParseErrorException;
import org.areasy.common.velocity.runtime.resource.loader.ResourceLoader;

/**
 * This class represent a general text resource that
 * may have been retrieved from any number of possible
 * sources.
 *
 * @version $Id: Resource.java,v 1.1 2008/05/25 22:33:16 swd\stefan.damian Exp $
 */
public abstract class Resource
{
	protected RuntimeService runtime = null;

	/**
	 * The template loader that initially loaded the input
	 * stream for this template, and knows how to check the
	 * source of the input stream for modification.
	 */
	protected ResourceLoader resourceLoader;

	/**
	 * The number of milliseconds in a minute, used to calculate the
	 * check interval.
	 */
	protected static final long MILLIS_PER_SECOND = 1000;

	/**
	 * How often the file modification time is checked (in seconds).
	 */
	protected long modificationCheckInterval = 0;

	/**
	 * The file modification time (in milliseconds) for the cached template.
	 */
	protected long lastModified = 0;

	/**
	 * The next time the file modification time will be checked (in
	 * milliseconds).
	 */
	protected long nextCheck = 0;

	/**
	 * Name of the resource
	 */
	protected String name;

	/**
	 * Character encoding of this resource
	 */
	protected String encoding = "ISO-8859-1";

	/**
	 * Resource might require ancillary storage of some kind
	 */
	protected Object data = null;

	/**
	 * Default constructor
	 */
	public Resource()
	{
	}

	public void setRuntimeServices(RuntimeService rs)
	{
		runtime = rs;
	}

	/**
	 * Perform any subsequent processing that might need
	 * to be done by a resource. In the case of a template
	 * the actual parsing of the input stream needs to be
	 * performed.
	 *
	 * @return Whether the resource could be processed successfully.
	 *         For a {@link org.areasy.common.velocity.base.Template} or {@link
	 *         org.areasy.common.velocity.runtime.resource.ContentResource}, this
	 *         indicates whether the resource could be read.
	 * @throws ResourceNotFoundException Similar in semantics as
	 *                                   returning <code>false</code>.
	 */
	public abstract boolean process() throws ResourceNotFoundException, ParseErrorException, Exception;

	public boolean isSourceModified()
	{
		return resourceLoader.isSourceModified(this);
	}

	/**
	 * Set the modification check interval.
	 */
	public void setModificationCheckInterval(long modificationCheckInterval)
	{
		this.modificationCheckInterval = modificationCheckInterval;
	}

	/**
	 * Is it time to check to see if the resource
	 * source has been updated?
	 */
	public boolean requiresChecking()
	{
		/*
		 *  short circuit this if modificationCheckInterval == 0
		 *  as this means "don't check"
		 */

		if (modificationCheckInterval <= 0) return false;

		/*
		 *  see if we need to check now
		 */

		return (System.currentTimeMillis() >= nextCheck);
	}

	/**
	 * 'Touch' this template and thereby resetting
	 * the nextCheck field.
	 */
	public void touch()
	{
		nextCheck = System.currentTimeMillis() + (MILLIS_PER_SECOND * modificationCheckInterval);
	}

	/**
	 * Set the name of this resource, for example
	 * test.vm.
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Get the name of this template.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * set the encoding of this resource
	 * for example, "ISO-8859-1"
	 */
	public void setEncoding(String encoding)
	{
		this.encoding = encoding;
	}

	/**
	 * get the encoding of this resource
	 * for example, "ISO-8859-1"
	 */
	public String getEncoding()
	{
		return encoding;
	}


	/**
	 * Return the lastModifed time of this
	 * template.
	 */
	public long getLastModified()
	{
		return lastModified;
	}

	/**
	 * Set the last modified time for this
	 * template.
	 */
	public void setLastModified(long lastModified)
	{
		this.lastModified = lastModified;
	}

	/**
	 * Return the template loader that pulled
	 * in the template stream
	 */
	public ResourceLoader getResourceLoader()
	{
		return resourceLoader;
	}

	/**
	 * Set the template loader for this template. Set
	 * when the Runtime determines where this template
	 * came from the list of possible sources.
	 */
	public void setResourceLoader(ResourceLoader resourceLoader)
	{
		this.resourceLoader = resourceLoader;
	}

	/**
	 * Set arbitrary data object that might be used
	 * by the resource.
	 */
	public void setData(Object data)
	{
		this.data = data;
	}

	/**
	 * Get arbitrary data object that might be used
	 * by the resource.
	 */
	public Object getData()
	{
		return data;
	}
}
