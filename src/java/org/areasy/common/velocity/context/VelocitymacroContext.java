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
import org.areasy.common.velocity.runtime.RuntimeService;
import org.areasy.common.velocity.runtime.directive.VMProxyArg;
import org.areasy.common.velocity.runtime.introspection.IntrospectionCacheData;
import org.areasy.common.velocity.runtime.resource.Resource;

import java.util.HashMap;

/**
 * This is a special, internal-use-only context implementation to be
 * used for the new Velocimacro implementation.
 * <p/>
 * The main distinguishing feature is the management of the VMProxyArg objects
 * in the put() and get() methods.
 * <p/>
 * Further, this context also supports the 'VM local context' mode, where
 * any get() or put() of references that aren't args to the VM are considered
 * local to the vm, protecting the global context.
 *
 * @version $Id: VelocitymacroContext.java,v 1.1 2008/05/25 22:33:09 swd\stefan.damian Exp $
 */
public class VelocitymacroContext implements InternalContextAdapter
{
	/**
	 * container for our VMProxy Objects
	 */
	HashMap vmproxyhash = new HashMap();

	/**
	 * container for any local or constant VMProxy items
	 */
	HashMap localcontext = new HashMap();

	/**
	 * the base context store.  This is the 'global' context
	 */
	InternalContextAdapter innerContext = null;

	/**
	 * context that we are wrapping
	 */
	InternalContextAdapter wrappedContext = null;

	/**
	 * support for local context scope feature, where all references are local
	 */
	private boolean localcontextscope = false;

	/**
	 * CTOR, wraps an ICA
	 */
	public VelocitymacroContext(InternalContextAdapter inner, RuntimeService rsvc)
	{
		localcontextscope = rsvc.getBoolean("macro.context.localscope", false);

		wrappedContext = inner;
		innerContext = inner.getBaseContext();
	}

	/**
	 * return the inner / user context
	 */
	public Context getInternalUserContext()
	{
		return innerContext.getInternalUserContext();
	}

	public InternalContextAdapter getBaseContext()
	{
		return innerContext.getBaseContext();
	}

	/**
	 * Used to put VMProxyArgs into this context.  It separates
	 * the VMProxyArgs into constant and non-constant types
	 * pulling out the value of the constant types so they can
	 * be modified w/o damaging the VMProxyArg, and leaving the
	 * dynamic ones, as they modify context rather than their own
	 * state
	 *
	 * @param vmpa VMProxyArg to add
	 */
	public void addVMProxyArg(VMProxyArg vmpa)
	{
		String key = vmpa.getContextReference();

		if (vmpa.isConstant()) localcontext.put(key, vmpa.getObject(wrappedContext));
			else vmproxyhash.put(key, vmpa);
	}

	/**
	 * Impl of the Context.put() method.
	 *
	 * @param key   name of item to set
	 * @param value object to set to key
	 * @return old stored object
	 */
	public Object put(String key, Object value)
	{
		VMProxyArg vmpa = (VMProxyArg) vmproxyhash.get(key);

		if (vmpa != null) return vmpa.setObject(wrappedContext, value);
		else
		{
			if (localcontextscope) return localcontext.put(key, value);
			else
			{
				if (localcontext.containsKey(key)) return localcontext.put(key, value);
					else return innerContext.put(key, value);
			}
		}
	}

	/**
	 * Impl of the Context.gut() method.
	 *
	 * @param key name of item to get
	 * @return stored object or null
	 */
	public Object get(String key)
	{
		Object o = null;

		VMProxyArg vmpa = (VMProxyArg) vmproxyhash.get(key);

		if (vmpa != null) o = vmpa.getObject(wrappedContext);
		else
		{
			if (localcontextscope) o = localcontext.get(key);
			else
			{
				o = localcontext.get(key);

				if (o == null) o = innerContext.get(key);
			}
		}

		return o;
	}

	/**
	 * not yet impl
	 */
	public boolean containsKey(Object key)
	{
		return false;
	}

	/**
	 * impl badly
	 */
	public Object[] getKeys()
	{
		return vmproxyhash.keySet().toArray();
	}

	public Object remove(Object key)
	{
		return vmproxyhash.remove(key);
	}

	public void pushCurrentTemplateName(String s)
	{
		innerContext.pushCurrentTemplateName(s);
	}

	public void popCurrentTemplateName()
	{
		innerContext.popCurrentTemplateName();
	}

	public String getCurrentTemplateName()
	{
		return innerContext.getCurrentTemplateName();
	}

	public Object[] getTemplateNameStack()
	{
		return innerContext.getTemplateNameStack();
	}

	public IntrospectionCacheData icacheGet(Object key)
	{
		return innerContext.icacheGet(key);
	}

	public void icachePut(Object key, IntrospectionCacheData o)
	{
		innerContext.icachePut(key, o);
	}

	public EventCartridge attachEventCartridge(EventCartridge ec)
	{
		return innerContext.attachEventCartridge(ec);
	}

	public EventCartridge getEventCartridge()
	{
		return innerContext.getEventCartridge();
	}


	public void setCurrentResource(Resource r)
	{
		innerContext.setCurrentResource(r);
	}

	public Resource getCurrentResource()
	{
		return innerContext.getCurrentResource();
	}

	public void clear()
	{
		vmproxyhash.clear();
	}
}



