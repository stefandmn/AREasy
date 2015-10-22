package org.areasy.common.parser.csv.bean;

/*
 * Copyright (c) 2007-2015 AREasy Runtime
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

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class CsvToBean
{
	public CsvToBean()
	{
		//nothing to do
	}

	public List parse(DataMapping mapper, Reader reader)
	{
		try
		{
			CsvReader csv = new CsvReader(reader);

			mapper.captureHeader(csv);
			String[] line;
			List list = new ArrayList();

			while (null != (line = csv.readNext()))
			{
				Object obj = processLine(mapper, line);
				list.add(obj);
			}

			return list;
		}
		catch (Exception e)
		{
			throw new RuntimeException("Error parsing CSV!", e);
		}
	}

	protected Object processLine(DataMapping mapper, String[] line) throws IllegalAccessException, InvocationTargetException, InstantiationException, IntrospectionException
	{
		Object bean = mapper.createBean();

		for (int col = 0; col < line.length; col++)
		{
			String value = line[col];
			PropertyDescriptor prop = mapper.findDescriptor(col);

			if (null != prop)
			{
				Object obj = convertValue(value, prop);
				prop.getWriteMethod().invoke(bean, new Object[]{obj});
			}
		}

		return bean;
	}

	protected Object convertValue(String value, PropertyDescriptor prop) throws InstantiationException, IllegalAccessException
	{
		PropertyEditor editor = getPropertyEditor(prop);
		Object obj = value;

		if (null != editor)
		{
			editor.setAsText(value);
			obj = editor.getValue();
		}

		return obj;
	}

	/*
		 * Attempt to find custom property editor on descriptor first, else try the propery editor manager.
		 */
	protected PropertyEditor getPropertyEditor(PropertyDescriptor desc) throws InstantiationException, IllegalAccessException
	{
		Class cls = desc.getPropertyEditorClass();
		if (null != cls) return (PropertyEditor) cls.newInstance();

		return PropertyEditorManager.findEditor(desc.getPropertyType());
	}

}
