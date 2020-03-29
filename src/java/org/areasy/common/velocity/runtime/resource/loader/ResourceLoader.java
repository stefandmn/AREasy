package org.areasy.common.velocity.runtime.resource.loader;

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

import org.areasy.common.support.configuration.Configuration;
import org.areasy.common.velocity.base.ResourceNotFoundException;
import org.areasy.common.velocity.runtime.RuntimeService;
import org.areasy.common.velocity.runtime.resource.Resource;

import java.io.InputStream;

/**
 * This is abstract class the all text resource loaders should
 * extend.
 *
 * @version $Id: ResourceLoader.java,v 1.1 2008/05/25 22:33:15 swd\stefan.damian Exp $
 */
public abstract class ResourceLoader
{
	/**
	 * Does this loader want templates produced with it
	 * cached in the Runtime.
	 */
	protected boolean isCachingOn = false;

	/**
	 * This property will be passed on to the templates
	 * that are created with this loader.
	 */
	protected long modificationCheckInterval = 2;

	/**
	 * Class name for this loader, for logging/debuggin
	 * purposes.
	 */
	protected String className = null;

	protected RuntimeService rsvc = null;

	/**
	 * This initialization is used by all resource
	 * loaders and must be called to set up common
	 * properties shared by all resource loaders
	 */
	public void commonInit(RuntimeService rs, Configuration configuration)
	{
		this.rsvc = rs;

		isCachingOn = configuration.getBoolean("cache", false);
		modificationCheckInterval = configuration.getLong("modification.check.interval", 0);

		className = configuration.getString("class");
	}

	/**
	 * Initialize the template loader with a
	 * a resources class.
	 */
	public abstract void init(Configuration configuration);

	/**
	 * Get the InputStream that the Runtime will parse
	 * to create a template.
	 */
	public abstract InputStream getResourceStream(String source) throws ResourceNotFoundException;

	/**
	 * Given a template, check to see if the source of InputStream
	 * has been modified.
	 */
	public abstract boolean isSourceModified(Resource resource);

	/**
	 * Get the last modified time of the InputStream source
	 * that was used to create the template. We need the template
	 * here because we have to extract the name of the template
	 * in order to locate the InputStream source.
	 */
	public abstract long getLastModified(Resource resource);

	/**
	 * Return the class name of this resource Loader
	 */
	public String getClassName()
	{
		return className;
	}

	/**
	 * Set the caching state. If true, then this loader
	 * would like the Runtime to cache templates that
	 * have been created with InputStreams provided
	 * by this loader.
	 */
	public void setCachingOn(boolean value)
	{
		isCachingOn = value;
	}

	/**
	 * The Runtime uses this to find out whether this
	 * template loader wants the Runtime to cache
	 * templates created with InputStreams provided
	 * by this loader.
	 */
	public boolean isCachingOn()
	{
		return isCachingOn;
	}

	/**
	 * Set the interval at which the InputStream source
	 * should be checked for modifications.
	 */
	public void setModificationCheckInterval(long modificationCheckInterval)
	{
		this.modificationCheckInterval = modificationCheckInterval;
	}

	/**
	 * Get the interval at which the InputStream source
	 * should be checked for modifications.
	 */
	public long getModificationCheckInterval()
	{
		return modificationCheckInterval;
	}
}
