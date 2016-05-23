package org.areasy.common.data.type.map;

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

import org.areasy.common.data.type.Factory;
import org.areasy.common.data.type.Transformer;
import org.areasy.common.data.workers.functors.FactoryTransformer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;

/**
 * Decorates another <code>Map</code> to create objects in the map on demand.
 * <p/>
 * When the {@link #get(Object)} method is called with a key that does not
 * exist in the map, the factory is used to create the object. The created
 * object will be added to the map using the requested key.
 * <p/>
 * For instance:
 * <pre>
 * Factory factory = new Factory() {
 *     public Object create() {
 *         return new Date();
 *     }
 * }
 * Map lazy = Lazy.map(new HashMap(), factory);
 * Object obj = lazy.get("NOW");
 * </pre>
 * <p/>
 * After the above code is executed, <code>obj</code> will contain
 * a new <code>Date</code> instance. Furthermore, that <code>Date</code>
 * instance is mapped to the "NOW" key in the map.
 *
 * @version $Id: SlowMap.java,v 1.2 2008/05/14 09:32:30 swd\stefan.damian Exp $
 */
public class SlowMap extends AbstractMapDecorator implements Map, Serializable
{
	/**
	 * The factory to use to construct elements
	 */
	protected final Transformer factory;

	/**
	 * Factory method to create a lazily instantiated map.
	 *
	 * @param map     the map to decorate, must not be null
	 * @param factory the factory to use, must not be null
	 * @throws IllegalArgumentException if map or factory is null
	 */
	public static Map decorate(Map map, Factory factory)
	{
		return new SlowMap(map, factory);
	}

	/**
	 * Factory method to create a lazily instantiated map.
	 *
	 * @param map     the map to decorate, must not be null
	 * @param factory the factory to use, must not be null
	 * @throws IllegalArgumentException if map or factory is null
	 */
	public static Map decorate(Map map, Transformer factory)
	{
		return new SlowMap(map, factory);
	}

	/**
	 * Constructor that wraps (not copies).
	 *
	 * @param map     the map to decorate, must not be null
	 * @param factory the factory to use, must not be null
	 * @throws IllegalArgumentException if map or factory is null
	 */
	protected SlowMap(Map map, Factory factory)
	{
		super(map);
		if (factory == null)
		{
			throw new IllegalArgumentException("Factory must not be null");
		}
		this.factory = FactoryTransformer.getInstance(factory);
	}

	/**
	 * Constructor that wraps (not copies).
	 *
	 * @param map     the map to decorate, must not be null
	 * @param factory the factory to use, must not be null
	 * @throws IllegalArgumentException if map or factory is null
	 */
	protected SlowMap(Map map, Transformer factory)
	{
		super(map);
		if (factory == null)
		{
			throw new IllegalArgumentException("Factory must not be null");
		}
		this.factory = factory;
	}

	/**
	 * Write the map out using a custom routine.
	 *
	 * @param out the output stream
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException
	{
		out.defaultWriteObject();
		out.writeObject(map);
	}

	/**
	 * Read the map in using a custom routine.
	 *
	 * @param in the input stream
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();
		map = (Map) in.readObject();
	}

	public Object get(Object key)
	{
		// create value for key if key is not currently in the map
		if (map.containsKey(key) == false)
		{
			Object value = factory.transform(key);
			map.put(key, value);
			return value;
		}
		return map.get(key);
	}

	// no need to wrap keySet, entrySet or values as they are views of
	// existing map entries - you can't do a map-style get on them.
}
