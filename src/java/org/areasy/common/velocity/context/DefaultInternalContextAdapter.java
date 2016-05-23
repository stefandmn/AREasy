package org.areasy.common.velocity.context;

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

import org.areasy.common.velocity.base.event.EventCartridge;
import org.areasy.common.velocity.runtime.introspection.IntrospectionCacheData;
import org.areasy.common.velocity.runtime.resource.Resource;

/**
 * This adapter class is the container for all context types for internal
 * use.  The AST now uses this class rather than the app-level Context
 * interface to allow flexibility in the future.
 * <p/>
 * Currently, we have two context interfaces which must be supported :
 * <ul>
 * <li> Context : used for application/template data access
 * <li> InternalHousekeepingContext : used for internal housekeeping and caching
 * <li> InternalWrapperContext : used for getting root cache context and other
 * such.
 * <li> InternalEventContext : for event handling.
 * </ul>
 * <p/>
 * This class implements the two interfaces to ensure that all methods are
 * supported.  When adding to the interfaces, or adding more context
 * functionality, the interface is the primary definition, so alter that first
 * and then all classes as necessary.  As of this writing, this would be
 * the only class affected by changes to InternalContext
 * <p/>
 * This class ensures that an InternalContextBase is available for internal
 * use.  If an application constructs their own Context-implementing
 * object w/o subclassing AbstractContext, it may be that support for
 * InternalContext is not available.  Therefore, InternalContextAdapter will
 * create an InternalContextBase if necessary for this support.  Note that
 * if this is necessary, internal information such as node-cache data will be
 * lost from use to use of the context.  This may or may not be important,
 * depending upon application.
 *
 * @version $Id: DefaultInternalContextAdapter.java,v 1.1 2008/05/25 22:33:09 swd\stefan.damian Exp $
 */
public final class DefaultInternalContextAdapter implements InternalContextAdapter
{
	/**
	 * the user data Context that we are wrapping
	 */
	Context context = null;

	/**
	 * the ICB we are wrapping.  We may need to make one
	 * if the user data context implementation doesn't
	 * support one.  The default AbstractContext-derived
	 * VelocityContext does, and it's recommended that
	 * people derive new contexts from AbstractContext
	 * rather than piecing things together
	 */
	InternalHousekeepingContext icb = null;

	/**
	 * The InternalEventContext that we are wrapping.  If
	 * the context passed to us doesn't support it, no
	 * biggie.  We don't make it for them - since its a
	 * user context thing, nothing gained by making one
	 * for them now
	 */
	InternalEventContext iec = null;

	/**
	 * CTOR takes a Context and wraps it, delegating all 'data' calls
	 * to it.
	 * <p/>
	 * For support of internal contexts, it will create an InternalContextBase
	 * if need be.
	 */
	public DefaultInternalContextAdapter(Context c)
	{
		context = c;

		if (!(c instanceof InternalHousekeepingContext)) icb = new InternalContextBase();
			else icb = (InternalHousekeepingContext) context;

		if (c instanceof InternalEventContext) iec = (InternalEventContext) context;
	}

	public void pushCurrentTemplateName(String s)
	{
		icb.pushCurrentTemplateName(s);
	}

	public void popCurrentTemplateName()
	{
		icb.popCurrentTemplateName();
	}

	public String getCurrentTemplateName()
	{
		return icb.getCurrentTemplateName();
	}

	public Object[] getTemplateNameStack()
	{
		return icb.getTemplateNameStack();
	}

	public IntrospectionCacheData icacheGet(Object key)
	{
		return icb.icacheGet(key);
	}

	public void icachePut(Object key, IntrospectionCacheData o)
	{
		icb.icachePut(key, o);
	}

	public void setCurrentResource(Resource r)
	{
		icb.setCurrentResource(r);
	}

	public Resource getCurrentResource()
	{
		return icb.getCurrentResource();
	}

	public Object put(String key, Object value)
	{
		return context.put(key, value);
	}

	public Object get(String key)
	{
		return context.get(key);
	}

	public boolean containsKey(Object key)
	{
		return context.containsKey(key);
	}

	public Object[] getKeys()
	{
		return context.getKeys();
	}

	public Object remove(Object key)
	{
		return context.remove(key);
	}

	public void clear()
	{
		context.clear();
	}

	/**
	 * returns the user data context that
	 * we are wrapping
	 */
	public Context getInternalUserContext()
	{
		return context;
	}

	/**
	 * Returns the base context that we are
	 * wrapping. Here, its this, but for other thing
	 * like VM related context contortions, it can
	 * be something else
	 */
	public InternalContextAdapter getBaseContext()
	{
		return this;
	}

	public EventCartridge attachEventCartridge(EventCartridge ec)
	{
		if (iec != null) return iec.attachEventCartridge(ec);

		return null;
	}

	public EventCartridge getEventCartridge()
	{
		if (iec != null) return iec.getEventCartridge();

		return null;
	}
}


