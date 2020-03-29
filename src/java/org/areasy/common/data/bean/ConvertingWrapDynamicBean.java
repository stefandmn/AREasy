package org.areasy.common.data.bean;

/*
 * Copyright (c) 2007-2020 AREasy Runtime
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
 * <p>Implementation of <code>DynamicBean</code> that wraps a standard JavaBean
 * instance, so that DynamicBean APIs can be used to access its properties,
 * though this implementation allows type conversion to occur when properties are set.
 * This means that (say) Strings can be passed in as values in setter methods and
 * this DynamicBean will convert them to the correct primitive data types.</p>
 * <p/>
 * <p><strong>IMPLEMENTATION NOTE</strong> - This implementation does not
 * support the <code>contains()</code> and <code>remove()</code> methods.</p>
 *
 * @version $Id: ConvertingWrapDynamicBean.java,v 1.2 2008/05/14 09:32:37 swd\stefan.damian Exp $
 */

public class ConvertingWrapDynamicBean extends WrapDynamicBean
{


	/**
	 * Construct a new <code>DynamicBean</code> associated with the specified
	 * JavaBean instance.
	 *
	 * @param instance JavaBean instance to be wrapped
	 */
	public ConvertingWrapDynamicBean(Object instance)
	{
		super(instance);
	}


	/**
	 * Set the value of the property with the specified name
	 * performing any type conversions if necessary. So this method
	 * can accept String values for primitive numeric data types for example.
	 *
	 * @param name  Name of the property whose value is to be set
	 * @param value Value to which this property is to be set
	 * @throws ConversionException      if the specified value cannot be
	 *                                  converted to the type required for this property
	 * @throws IllegalArgumentException if there is no property
	 *                                  of the specified name
	 * @throws NullPointerException     if an attempt is made to set a
	 *                                  primitive property to null
	 */
	public void set(String name, Object value)
	{
		try
		{
			BeanUtility.copyProperty(instance, name, value);
		}
		catch (Throwable t)
		{
			throw new IllegalArgumentException("Property '" + name + "' has no write method");
		}
	}
}
