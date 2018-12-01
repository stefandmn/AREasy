package org.areasy.runtime.engine.services.cache;

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

import org.areasy.runtime.RuntimeManager;
import org.areasy.runtime.engine.RuntimeServer;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * This is a dedicated class (loader and template) to allow runtime server to preload objects that will be stored in cache.
 * All these objects are initialized and prepared for consumptions during loading procedure and then transformed in
 * cache entries with specific properties. In case of an object have to be used all the time must have TTL property
 * -1 (TTL_FOREVER). Also, this class could be used to call stored objects in cache or to append others, during
 * runtime server execution.
 */
public abstract class InitialObject
{
	/** Library logger */
	protected static Logger logger =  LoggerFactory.getLog(InitialObject.class);

	/** Do not expire the object */
	public static final int TTL_FOREVER = CacheEntry.FOREVER;

	/** Cache the object with the Default TTL is 72h*/
	public static final int TTL_DEFAULT = CacheEntry.DEFAULT;

	/** Context runtime manager instance */
	private RuntimeManager manager = null;

	/**
	 * This is the method that have to be implemented by any object that wants to be stored in cache when
	 * the server is started. Here should be call the entire logic of the initialization of this object
	 *
	 * @throws AREasyException in case of any exception will occur
	 */
	public abstract void prepare() throws AREasyException;

	/**
	 * Define the "time to live" parameter of the object in cache layer.
	 *
	 * @return the TTL value.
	 */
	public abstract int getTimeToLive();

	/**
	 * Because these persistent object are run usually into a specific context they have to
	 * dbe destroyed.
	 */
	public abstract void destroy();

	/**
	 * Get runtime manager context.
	 * @return <code>RuntimeManager</code> is the context used to run all these objects.
	 */
	public RuntimeManager getManager()
	{
		return manager;
	}

	/**
	 * Set runtime manager context.
	 * @param manager <code>RuntimeManager</code> is the context used to run all these objects.
	 */
	protected void setManager(RuntimeManager manager)
	{
		this.manager = manager;
	}

	/**
	 * Take from the cache layer the persistent object, registered when the runtime server started or later.
	 * @param clazz the class name (signature) of the object that have to be found
	 * @return the persistent object instance
	 */
	public static final InitialObject getObjectInstance(Class clazz)
	{
		return clazz != null ? getObjectInstance(clazz.getName()) : null;
	}

	/**
	 * Take from the cache layer the persistent object, registered when the runtime server started or later.
	 * @param clazz the class structure of the object that have to be found
	 * @return the persistent object instance
	 */
	public static final InitialObject getObjectInstance(String clazz)
	{
		if(clazz == null) return null;

		if(RuntimeServer.getCache().contains(clazz)) return (InitialObject) RuntimeServer.getCache().get(clazz);
			else return null;
	}

	/**
	 * Prepare and load in cache layer all registered objects.
	 *
	 * @param manager <code>RuntimeManager</code> is the context used to run all these objects.
	 */
	public static final void load(RuntimeManager manager)
	{
		if(manager == null)
		{
			logger.warn("Runtime manager is null. No persistent object could be load");

			return;
		}

		List list = manager.getConfiguration().getList("app.server.cache.preload.object.class", null);

		for(int i = 0; list != null && i < list.size(); i++)
		{
			String oClassName = (String) list.get(i);

			try
			{
				load(manager, oClassName);
			}
			catch(AREasyException th)
			{
				logger.error("Error registering object '" + oClassName + "' in the persistent layer: " + th.getMessage());
				logger.debug("Exception", th);
			}
		}
	}

	/**
	 * Prepare and load in cache layer all registered objects.
	 *
	 * @param manager <code>RuntimeManager</code> is the context used to run all these objects.
	 * @param oClassName persistent class signature used to define the cache identifier
	 * @throws AREasyException in case of any exception will occur
	 */
	public static final void load(RuntimeManager manager, String oClassName) throws AREasyException
	{
		if(manager == null) throw new AREasyException("Runtime manager is null");

		if(oClassName == null) throw new AREasyException("Persistent class signature is null");

		try
		{
			Class oClassStructure = Class.forName(oClassName);

			if(oClassStructure != null)
			{
				logger.trace("Preloaded object has been identified: " + oClassStructure);
				Constructor contructor = oClassStructure.getConstructor(null);

				//create object
				InitialObject oClassInstance = (InitialObject) contructor.newInstance(null);
				oClassInstance.register(manager);
			}
		}
		catch(Throwable th)
		{
			throw new AREasyException(th);
		}
	}

	/**
	 * Prepare and load/register in cache layer th current object.
	 * <br/>
	 * <br/>
	 * Note: If you register twice the same object the last instance will be kept
	 *
	 * @param manager <code>RuntimeManager</code> is the context used to run all these objects.
	 * @throws AREasyException in case of any exception will occur
	 */
	public final void register(RuntimeManager manager) throws AREasyException
	{
		if(manager == null) throw new AREasyException("Runtime manager is null");

		setManager(manager);
		prepare();

		//create cache entry and register it
		CacheEntry entry = new CacheEntry(this, getTimeToLive());
		RuntimeServer.getCache().add(getClass().getName(), entry);
	}

	/**
	 * Destroy all preloaded objects and release them from cache layer.
	 * The objects are not cleaned from the cache layer, this method just call <code>destroy</code> method.
	 */
	public static final void release()
	{
		Collection entries = RuntimeServer.getCache().getBuffer().values();

		if(entries != null)
		{
			Iterator iterator = entries.iterator();

			while(iterator.hasNext())
			{
				Object object = iterator.next();
				if(object == null) continue;

				CacheEntry entry = (CacheEntry) object;
				Object preload = entry.getContent();

				if(preload instanceof InitialObject)
				{
					((InitialObject) preload).destroy();
				}
			}
		}
	}
}
