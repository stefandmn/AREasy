package org.areasy.common.velocity.runtime.resource;

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

import org.areasy.common.velocity.base.ResourceNotFoundException;
import org.areasy.common.velocity.runtime.RuntimeService;
import org.areasy.common.velocity.runtime.parser.ParseErrorException;

/**
 * Class to manage the text resource for the Velocity
 * Runtime.
 *
 * @version $Id: ResourceManager.java,v 1.1 2008/05/25 22:33:16 swd\stefan.damian Exp $
 */
public interface ResourceManager
{
	/**
	 * A template resources.
	 */
	public static final int RESOURCE_TEMPLATE = 1;

	/**
	 * A static content resource.
	 */
	public static final int RESOURCE_CONTENT = 2;

	/**
	 * Initialize the ResourceManager.
	 */
	public void initialize(RuntimeService rs) throws Exception;

	/**
	 * Gets the named resource.  Returned class type corresponds to specified type
	 * (i.e. <code>Template</code> to <code>RESOURCE_TEMPLATE</code>).
	 *
	 * @param resourceName The name of the resource to retrieve.
	 * @param resourceType The type of resource (<code>RESOURCE_TEMPLATE</code>,
	 *                     <code>RESOURCE_CONTENT</code>, etc.).
	 * @param encoding     The character encoding to use.
	 * @return Resource with the template parsed and ready.
	 * @throws org.areasy.common.velocity.base.ResourceNotFoundException if template not found
	 *                                   from any available source.
	 * @throws org.areasy.common.velocity.runtime.parser.ParseErrorException       if template cannot be parsed due
	 *                                   to syntax (or other) error.
	 * @throws Exception                 if a problem in parse
	 */
	public Resource getResource(String resourceName, int resourceType, String encoding) throws ResourceNotFoundException, ParseErrorException, Exception;

	/**
	 * Determines is a template exists, and returns name of the loader that
	 * provides it.  This is a slightly less hokey way to support
	 * the Velocity.templateExists() utility method, which was broken
	 * when per-template encoding was introduced.  We can revisit this.
	 *
	 * @param resourceName Name of template or content resource
	 * @return class name of loader than can provide it
	 */
	public String getLoaderNameForResource(String resourceName);

}


