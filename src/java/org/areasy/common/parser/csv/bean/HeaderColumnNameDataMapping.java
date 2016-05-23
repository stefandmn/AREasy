package org.areasy.common.parser.csv.bean;
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

import org.areasy.common.parser.csv.CsvReader;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;

public class HeaderColumnNameDataMapping implements DataMapping
{
	protected String[] header;
	protected PropertyDescriptor[] descriptors;
	protected Class type;

	public void captureHeader(CsvReader reader) throws IOException
	{
		header = reader.readNext();
	}

	public PropertyDescriptor findDescriptor(int col) throws IntrospectionException
	{
		String columnName = getColumnName(col);
		return (null != columnName && columnName.trim().length() > 0) ? findDescriptor(columnName) : null;
	}

	protected String getColumnName(int col)
	{
		return (null != header && col < header.length) ? header[col] : null;
	}

	protected PropertyDescriptor findDescriptor(String name) throws IntrospectionException
	{
		if (null == descriptors) descriptors = loadDescriptors(getType()); //lazy load descriptors

		for (int i = 0; i < descriptors.length; i++)
		{
			PropertyDescriptor desc = descriptors[i];
			if (matches(name, desc)) return desc;
		}
		
		return null;
	}

	protected boolean matches(String name, PropertyDescriptor desc)
	{
		return desc.getName().equals(name);
	}

	protected PropertyDescriptor[] loadDescriptors(Class cls) throws IntrospectionException
	{
		BeanInfo beanInfo = Introspector.getBeanInfo(cls);
		return beanInfo.getPropertyDescriptors();
	}

	public Object createBean() throws InstantiationException, IllegalAccessException
	{
		return type.newInstance();
	}

	public Class getType()
	{
		return type;
	}

	public void setType(Class type)
	{
		this.type = type;
	}
}
