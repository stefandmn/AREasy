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

import org.areasy.common.data.type.Predicate;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

/**
 * Decorates another <code>Map</code> to validate that additions
 * match a specified predicate.
 * <p/>
 * This map exists to provide validation for the decorated map.
 * It is normally created to decorate an empty map.
 * If an object cannot be added to the map, an IllegalArgumentException is thrown.
 * <p/>
 * One usage would be to ensure that no null keys are added to the map.
 * <pre>Map map = PredicatedSet.decorate(new HashMap(), NotNullPredicate.INSTANCE, null);</pre>
 * <p/>
 *
 * @version $Id: PredicatedMap.java,v 1.2 2008/05/14 09:32:30 swd\stefan.damian Exp $
 */
public class PredicatedMap extends AbstractInputCheckedMapDecorator implements Serializable
{
	/**
	 * The key predicate to use
	 */
	protected final Predicate keyPredicate;
	/**
	 * The value predicate to use
	 */
	protected final Predicate valuePredicate;

	/**
	 * Factory method to create a predicated (validating) map.
	 * <p/>
	 * If there are any elements already in the list being decorated, they
	 * are validated.
	 *
	 * @param map            the map to decorate, must not be null
	 * @param keyPredicate   the predicate to validate the keys, null means no check
	 * @param valuePredicate the predicate to validate to values, null means no check
	 * @throws IllegalArgumentException if the map is null
	 */
	public static Map decorate(Map map, Predicate keyPredicate, Predicate valuePredicate)
	{
		return new PredicatedMap(map, keyPredicate, valuePredicate);
	}

	/**
	 * Constructor that wraps (not copies).
	 *
	 * @param map            the map to decorate, must not be null
	 * @param keyPredicate   the predicate to validate the keys, null means no check
	 * @param valuePredicate the predicate to validate to values, null means no check
	 * @throws IllegalArgumentException if the map is null
	 */
	protected PredicatedMap(Map map, Predicate keyPredicate, Predicate valuePredicate)
	{
		super(map);
		this.keyPredicate = keyPredicate;
		this.valuePredicate = valuePredicate;

		Iterator it = map.entrySet().iterator();
		while (it.hasNext())
		{
			Map.Entry entry = (Map.Entry) it.next();
			Object key = entry.getKey();
			Object value = entry.getValue();
			validate(key, value);
		}
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
	 * Validates a key value pair.
	 *
	 * @param key   the key to validate
	 * @param value the value to validate
	 * @throws IllegalArgumentException if invalid
	 */
	protected void validate(Object key, Object value)
	{
		if (keyPredicate != null && keyPredicate.evaluate(key) == false)
		{
			throw new IllegalArgumentException("Cannot add key - Predicate rejected it");
		}
		if (valuePredicate != null && valuePredicate.evaluate(value) == false)
		{
			throw new IllegalArgumentException("Cannot add value - Predicate rejected it");
		}
	}

	/**
	 * Override to validate an object set into the map via <code>setValue</code>.
	 *
	 * @param value the value to validate
	 * @throws IllegalArgumentException if invalid
	 */
	protected Object checkSetValue(Object value)
	{
		if (valuePredicate.evaluate(value) == false)
		{
			throw new IllegalArgumentException("Cannot set value - Predicate rejected it");
		}
		return value;
	}

	/**
	 * Override to only return true when there is a value transformer.
	 *
	 * @return true if a value predicate is in use
	 */
	protected boolean isSetValueChecking()
	{
		return (valuePredicate != null);
	}

	public Object put(Object key, Object value)
	{
		validate(key, value);
		return map.put(key, value);
	}

	public void putAll(Map mapToCopy)
	{
		Iterator it = mapToCopy.entrySet().iterator();
		while (it.hasNext())
		{
			Map.Entry entry = (Map.Entry) it.next();
			Object key = entry.getKey();
			Object value = entry.getValue();
			validate(key, value);
		}
		map.putAll(mapToCopy);
	}

}
