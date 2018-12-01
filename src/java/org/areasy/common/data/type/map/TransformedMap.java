package org.areasy.common.data.type.map;

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

import org.areasy.common.data.type.Transformer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

/**
 * Decorates another <code>Map</code> to transform objects that are added.
 * <p/>
 * The Map put methods and Map.Entry setValue method are affected by this class.
 * Thus objects must be removed or searched for using their transformed form.
 * For example, if the transformation converts Strings to Integers, you must
 * use the Integer form to remove objects.
 *
 * @version $Id: TransformedMap.java,v 1.2 2008/05/14 09:32:31 swd\stefan.damian Exp $
 */
public class TransformedMap extends AbstractInputCheckedMapDecorator implements Serializable
{
	/**
	 * The transformer to use for the key
	 */
	protected final Transformer keyTransformer;
	/**
	 * The transformer to use for the value
	 */
	protected final Transformer valueTransformer;

	/**
	 * Factory method to create a transforming map.
	 * <p/>
	 * If there are any elements already in the map being decorated, they
	 * are NOT transformed.
	 *
	 * @param map              the map to decorate, must not be null
	 * @param keyTransformer   the transformer to use for key conversion, null means no conversion
	 * @param valueTransformer the transformer to use for value conversion, null means no conversion
	 * @throws IllegalArgumentException if map is null
	 */
	public static Map decorate(Map map, Transformer keyTransformer, Transformer valueTransformer)
	{
		return new TransformedMap(map, keyTransformer, valueTransformer);
	}

	/**
	 * Constructor that wraps (not copies).
	 * <p/>
	 * If there are any elements already in the collection being decorated, they
	 * are NOT transformed.
	 *
	 * @param map              the map to decorate, must not be null
	 * @param keyTransformer   the transformer to use for key conversion, null means no conversion
	 * @param valueTransformer the transformer to use for value conversion, null means no conversion
	 * @throws IllegalArgumentException if map is null
	 */
	protected TransformedMap(Map map, Transformer keyTransformer, Transformer valueTransformer)
	{
		super(map);
		this.keyTransformer = keyTransformer;
		this.valueTransformer = valueTransformer;
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

	/**
	 * Transforms a key.
	 * <p/>
	 * The transformer itself may throw an exception if necessary.
	 *
	 * @param object the object to transform
	 */
	protected Object transformKey(Object object)
	{
		if (keyTransformer == null)
		{
			return object;
		}
		return keyTransformer.transform(object);
	}

	/**
	 * Transforms a value.
	 * <p/>
	 * The transformer itself may throw an exception if necessary.
	 *
	 * @param object the object to transform
	 */
	protected Object transformValue(Object object)
	{
		if (valueTransformer == null)
		{
			return object;
		}
		return valueTransformer.transform(object);
	}

	/**
	 * Transforms a map.
	 * <p/>
	 * The transformer itself may throw an exception if necessary.
	 *
	 * @param map the map to transform
	 */
	protected Map transformMap(Map map)
	{
		Map result = new LinkedMap(map.size());
		for (Iterator it = map.entrySet().iterator(); it.hasNext();)
		{
			Map.Entry entry = (Map.Entry) it.next();
			result.put(transformKey(entry.getKey()), transformValue(entry.getValue()));
		}
		return result;
	}

	/**
	 * Override to transform the value when using <code>setValue</code>.
	 *
	 * @param value the value to transform
	 * @return the transformed value
	 */
	protected Object checkSetValue(Object value)
	{
		return valueTransformer.transform(value);
	}

	/**
	 * Override to only return true when there is a value transformer.
	 *
	 * @return true if a value transformer is in use
	 */
	protected boolean isSetValueChecking()
	{
		return (valueTransformer != null);
	}

	public Object put(Object key, Object value)
	{
		key = transformKey(key);
		value = transformValue(value);
		return getMap().put(key, value);
	}

	public void putAll(Map mapToCopy)
	{
		mapToCopy = transformMap(mapToCopy);
		getMap().putAll(mapToCopy);
	}

}
