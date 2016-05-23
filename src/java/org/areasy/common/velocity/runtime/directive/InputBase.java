package org.areasy.common.velocity.runtime.directive;

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

import org.areasy.common.velocity.context.InternalContextAdapter;
import org.areasy.common.velocity.runtime.resource.Resource;

/**
 * Base class for directives which do input operations
 * (e.g. <code>#include()</code>, <code>#parse()</code>, etc.).
 */
public abstract class InputBase extends Directive
{
	/**
	 * Decides the encoding used during input processing of this
	 * directive.
	 * <p/>
	 * Get the resource, and assume that we use the encoding of the
	 * current template the 'current resource' can be
	 * <code>null</code> if we are processing a stream....
	 *
	 * @param context The context to derive the default input encoding  from.
	 * @return The encoding to use when processing this directive.
	 */
	protected String getInputEncoding(InternalContextAdapter context)
	{
		Resource current = context.getCurrentResource();

		if (current != null) return current.getEncoding();
			else return (String) rsvc.getKey("input.encoding");
	}
}
