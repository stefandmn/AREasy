package org.areasy.common.data.bean.locale;

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

/**
 * Locale bean utility descriptor.
 *
 * @version $Id: AbstractDescriptor.java,v 1.2 2008/05/14 09:32:41 swd\stefan.damian Exp $
 */
public class AbstractDescriptor
{
	private int index = -1;    // Indexed subscript value (if any)
	private String name;
	private String propName;   // Simple name of target property
	private String key;        // Mapped key value (if any)
	private Object target;

	public AbstractDescriptor(Object target, String name, String propName, String key, int index)
	{
		setTarget(target);
		setName(name);
		setPropName(propName);
		setKey(key);
		setIndex(index);
	}

	public Object getTarget()
	{
		return target;
	}

	public void setTarget(Object target)
	{
		this.target = target;
	}

	public String getKey()
	{
		return key;
	}

	public void setKey(String key)
	{
		this.key = key;
	}

	public int getIndex()
	{
		return index;
	}

	public void setIndex(int index)
	{
		this.index = index;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getPropName()
	{
		return propName;
	}

	public void setPropName(String propName)
	{
		this.propName = propName;
	}
}