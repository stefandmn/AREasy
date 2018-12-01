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

import org.areasy.common.velocity.base.Template;

/**
 * Class responsible for instantiating <code>Resource</code> objects,
 * given name and type.
 *
 * @version $Id: ResourceFactory.java,v 1.1 2008/05/25 22:33:16 swd\stefan.damian Exp $
 */
public class ResourceFactory
{
	public static Resource getResource(String resourceName, int resourceType)
	{
		Resource resource = null;

		switch (resourceType)
		{
			case ResourceManager.RESOURCE_TEMPLATE:
				resource = new Template();
				break;

			case ResourceManager.RESOURCE_CONTENT:
				resource = new ContentResource();
				break;
		}

		if(resource != null) resource.setName(resourceName);

		return resource;
	}
}
