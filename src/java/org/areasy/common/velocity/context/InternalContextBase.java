package org.areasy.common.velocity.context;

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

import org.areasy.common.velocity.base.event.EventCartridge;
import org.areasy.common.velocity.runtime.introspection.IntrospectionCacheData;
import org.areasy.common.velocity.runtime.resource.Resource;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Stack;

/**
 * class to encapsulate the 'stuff' for internal operation of velocity.
 * We use the context as a thread-safe storage : we take advantage of the
 * fact that it's a visitor  of sorts  to all nodes (that matter) of the
 * AST during init() and render().
 * Currently, it carries the template name for namespace
 * support, as well as node-local context data introspection caching.
 * <p/>
 * Note that this is not a public class.  It is for package access only to
 * keep application code from accessing the internals, as AbstractContext
 * is derived from this.
 *
 * @version $Id: InternalContextBase.java,v 1.1 2008/05/25 22:33:09 swd\stefan.damian Exp $
 */
class InternalContextBase implements InternalHousekeepingContext, InternalEventContext, Serializable
{
	/**
	 * Cache for node/context specific introspection information
	 */
	private HashMap introspectionCache = new HashMap(33);

	/**
	 * Template name stack. The stack top contains the current template name.
	 */
	private Stack templateNameStack = new Stack();

	/**
	 * EventCartridge we are to carry.  Set by application
	 */
	private EventCartridge eventCartridge = null;

	/**
	 * Current resource - used for carrying encoding and other
	 * information down into the rendering process
	 */
	private Resource currentResource = null;

	/**
	 * set the current template name on top of stack
	 *
	 * @param s current template name
	 */
	public void pushCurrentTemplateName(String s)
	{
		templateNameStack.push(s);
		return;
	}

	/**
	 * remove the current template name from stack
	 */
	public void popCurrentTemplateName()
	{
		templateNameStack.pop();
		return;
	}

	/**
	 * get the current template name
	 *
	 * @return String current template name
	 */
	public String getCurrentTemplateName()
	{
		if (templateNameStack.empty())
		{
			return "<undef>";
		}
		else
		{
			return (String) templateNameStack.peek();
		}
	}

	/**
	 * get the current template name stack
	 *
	 * @return Object[] with the template name stack contents.
	 */
	public Object[] getTemplateNameStack()
	{
		return templateNameStack.toArray();
	}

	/**
	 * returns an IntrospectionCache Data (@see IntrospectionCacheData)
	 * object if exists for the key
	 *
	 * @param key key to find in cache
	 * @return cache object
	 */
	public IntrospectionCacheData icacheGet(Object key)
	{
		return (IntrospectionCacheData) introspectionCache.get(key);
	}

	/**
	 * places an IntrospectionCache Data (@see IntrospectionCacheData)
	 * element in the cache for specified key
	 *
	 * @param key key
	 * @param o   IntrospectionCacheData object to place in cache
	 */
	public void icachePut(Object key, IntrospectionCacheData o)
	{
		introspectionCache.put(key, o);
	}

	public void setCurrentResource(Resource r)
	{
		currentResource = r;
	}

	public Resource getCurrentResource()
	{
		return currentResource;
	}

	public EventCartridge attachEventCartridge(EventCartridge ec)
	{
		EventCartridge temp = eventCartridge;

		eventCartridge = ec;

		return temp;
	}

	public EventCartridge getEventCartridge()
	{
		return eventCartridge;
	}
}

