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

import org.areasy.common.data.type.list.StaticList;

import java.util.*;

/**
 * A multi valued Map.
 * This Map specializes HashMap and provides methods
 * that operate on multi valued items.
 * <P>
 * Implemented as a map of LazyList values
 *
 * @version $Id: MultiValuesMap.java,v 1.2 2008/05/14 09:32:30 swd\stefan.damian Exp $
 * @see StaticList
 */
public class MultiValuesMap extends HashMap implements Cloneable
{
	/**
	 * Constructor.
	 */
	public MultiValuesMap()
	{
		//nothing to do.
	}

	/**
	 * Constructor.
	 *
	 * @param size Capacity of the map
	 */
	public MultiValuesMap(int size)
	{
		super(size);
	}

	/**
	 * Constructor.
	 *
	 * @param map
	 */
	public MultiValuesMap(Map map)
	{
		super((map.size() * 3) / 2);
		putAll(map);
	}

	/**
	 * Get multiple values.
	 * Single valued entries are converted to singleton lists.
	 *
	 * @param name The entry key.
	 * @return Unmodifieable List of values.
	 */
	public List getValues(Object name)
	{
		return StaticList.getList(super.get(name), true);
	}

	/**
	 * Get a value from a multiple value.
	 * If the value is not a multivalue, then index 0 retrieves the
	 * value or null.
	 *
	 * @param name The entry key.
	 * @param i    Index of element to get.
	 * @return Unmodifieable List of values.
	 */
	public Object getValue(Object name, int i)
	{
		Object l = super.get(name);
		if (i == 0 && StaticList.size(l) == 0) return null;

		return StaticList.get(l, i);
	}


	/**
	 * Get value as String.
	 * Single valued items are converted to a String with the toString()
	 * Object method. Multi valued entries are converted to a comma separated
	 * List.  No quoting of commas within values is performed.
	 *
	 * @param name The entry key.
	 * @return String value.
	 */
	public String getString(Object name)
	{
		Object l = super.get(name);
		switch (StaticList.size(l))
		{
			case 0:
				return null;
			case 1:
				Object o = StaticList.get(l, 0);
				return o == null ? null : o.toString();
			default:
				StringBuffer values = new StringBuffer(128);
				synchronized (values)
				{
					for (int i = 0; i < StaticList.size(l); i++)
					{
						Object e = StaticList.get(l, i);
						if (e != null)
						{
							if (values.length() > 0) values.append(',');
							values.append(e.toString());
						}
					}

					return values.toString();
				}
		}
	}

	public Object get(Object name)
	{
		Object l = super.get(name);
		switch (StaticList.size(l))
		{
			case 0:
				return null;
			case 1:
				return StaticList.get(l, 0);
			default:
				return StaticList.getList(l, true);
		}
	}

	/**
	 * Put and entry into the map.
	 *
	 * @param name  The entry key.
	 * @param value The entry value.
	 * @return The previous value or null.
	 */
	public Object put(Object name, Object value)
	{
		return super.put(name, StaticList.add(null, value));
	}

	/**
	 * Put multi valued entry.
	 *
	 * @param name   The entry key.
	 * @param values The List of multiple values.
	 * @return The previous value or null.
	 */
	public Object putValues(Object name, List values)
	{
		return super.put(name, values);
	}

	/**
	 * Put multi valued entry.
	 *
	 * @param name   The entry key.
	 * @param values The String array of multiple values.
	 * @return The previous value or null.
	 */
	public Object putValues(Object name, String[] values)
	{
		Object list = null;
		for (int i = 0; i < values.length; i++)
		{
			list = StaticList.add(list, values[i]);
		}

		return put(name, list);
	}


	/**
	 * Add value to multi valued entry.
	 * If the entry is single valued, it is converted to the first
	 * value of a multi valued entry.
	 *
	 * @param name  The entry key.
	 * @param value The entry value.
	 */
	public void add(Object name, Object value)
	{
		Object lo = super.get(name);
		Object ln = StaticList.add(lo, value);
		if (lo != ln) super.put(name, ln);
	}

	/**
	 * Add values to multi valued entry.
	 * If the entry is single valued, it is converted to the first
	 * value of a multi valued entry.
	 *
	 * @param name   The entry key.
	 * @param values The List of multiple values.
	 */
	public void addValues(Object name, List values)
	{
		Object lo = super.get(name);
		Object ln = StaticList.addCollection(lo, values);

		if (lo != ln) super.put(name, ln);
	}

	/**
	 * Add values to multi valued entry.
	 * If the entry is single valued, it is converted to the first
	 * value of a multi valued entry.
	 *
	 * @param name   The entry key.
	 * @param values The String array of multiple values.
	 */
	public void addValues(Object name, String[] values)
	{
		Object lo = super.get(name);
		Object ln = StaticList.addCollection(lo, Arrays.asList(values));

		if (lo != ln) super.put(name, ln);
	}

	/**
	 * Remove value.
	 *
	 * @param name  The entry key.
	 * @param value The entry value.
	 * @return true if it was removed.
	 */
	public boolean removeValue(Object name, Object value)
	{
		Object lo = super.get(name);
		Object ln = lo;
		int s = StaticList.size(lo);

		if (s > 0)
		{
			ln = StaticList.remove(lo, value);
			if (ln == null) super.remove(name);
				else super.put(name, ln);
		}

		return StaticList.size(ln) != s;
	}

	/**
	 * Put all contents of map.
	 *
	 * @param m Map
	 */
	public void putAll(Map m)
	{
		Iterator i = m.entrySet().iterator();
		boolean multi = m instanceof MultiValuesMap;

		while (i.hasNext())
		{
			Map.Entry entry = (Map.Entry) i.next();

			if (multi) super.put(entry.getKey(), StaticList.clone(entry.getValue()));
			else put(entry.getKey(), entry.getValue());
		}
	}

	/**
	 * @return Map of String arrays
	 */
	public Map toStringArrayMap()
	{
		HashMap map = new HashMap(size() * 3 / 2);

		Iterator i = super.entrySet().iterator();
		while (i.hasNext())
		{
			Map.Entry entry = (Map.Entry) i.next();
			Object l = entry.getValue();
			map.put(entry.getKey(), StaticList.toStringArray(l));
		}

		return map;
	}

	public Object clone()
	{
		MultiValuesMap mm = (MultiValuesMap) super.clone();

		Iterator iter = mm.entrySet().iterator();
		while (iter.hasNext())
		{
			Map.Entry entry = (Map.Entry) iter.next();
			entry.setValue(StaticList.clone(entry.getValue()));
		}

		return mm;
	}
}

