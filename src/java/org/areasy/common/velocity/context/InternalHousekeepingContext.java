package org.areasy.common.velocity.context;

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

import org.areasy.common.velocity.runtime.introspection.IntrospectionCacheData;
import org.areasy.common.velocity.runtime.resource.Resource;

/**
 * interface to encapsulate the 'stuff' for internal operation of velocity.
 * We use the context as a thread-safe storage : we take advantage of the
 * fact that it's a visitor  of sorts  to all nodes (that matter) of the
 * AST during init() and render().
 * <p/>
 * Currently, it carries the template name for namespace
 * support, as well as node-local context data introspection caching.
 *
 * @version $Id: InternalHousekeepingContext.java,v 1.1 2008/05/25 22:33:10 swd\stefan.damian Exp $
 */
interface InternalHousekeepingContext
{
	/**
	 * set the current template name on top of stack
	 *
	 * @param s current template name
	 */
	void pushCurrentTemplateName(String s);

	/**
	 * remove the current template name from stack
	 */
	void popCurrentTemplateName();

	/**
	 * get the current template name
	 *
	 * @return String current template name
	 */
	String getCurrentTemplateName();

	/**
	 * Returns the template name stack in form of an array.
	 *
	 * @return Object[] with the template name stack contents.
	 */
	Object[] getTemplateNameStack();

	/**
	 * returns an IntrospectionCache Data (@see IntrospectionCacheData)
	 * object if exists for the key
	 *
	 * @param key key to find in cache
	 * @return cache object
	 */
	IntrospectionCacheData icacheGet(Object key);

	/**
	 * places an IntrospectionCache Data (@see IntrospectionCacheData)
	 * element in the cache for specified key
	 *
	 * @param key key
	 * @param o   IntrospectionCacheData object to place in cache
	 */
	void icachePut(Object key, IntrospectionCacheData o);

	/**
	 * temporary fix to enable #include() to figure out
	 * current encoding.
	 */
	Resource getCurrentResource();

	void setCurrentResource(Resource r);


}
