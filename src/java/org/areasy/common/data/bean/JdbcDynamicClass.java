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

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>Provides common logic for JDBC implementations of {@link DynamicClass}.</p>
 *
 * @version $Id: JdbcDynamicClass.java,v 1.2 2008/05/14 09:32:37 swd\stefan.damian Exp $
 */

abstract class JdbcDynamicClass implements DynamicClass, Serializable
{

	/**
	 * <p>Flag defining whether column names should be lower cased when
	 * converted to property names.</p>
	 */
	protected boolean lowerCase = true;

	/**
	 * <p>The set of dynamic properties that are part of this
	 * {@link DynamicClass}.</p>
	 */
	protected DynamicProperty properties[] = null;

	/**
	 * <p>The set of dynamic properties that are part of this
	 * {@link DynamicClass}, keyed by the property name.  Individual descriptor
	 * instances will be the same instances as those in the
	 * <code>properties</code> list.</p>
	 */
	protected Map propertiesMap = new HashMap();

	/**
	 * <p>Return the name of this DynamicClass (analogous to the
	 * <code>getName()</code> method of <code>java.lang.Class</code), which
	 * allows the same <code>DynamicClass</code> implementation class to support
	 * different dynamic classes, with different sets of properties.</p>
	 */
	public String getName()
	{
		return (this.getClass().getName());
	}

	/**
	 * <p>Return a property descriptor for the specified property, if it
	 * exists; otherwise, return <code>null</code>.</p>
	 *
	 * @param name Name of the dynamic property for which a descriptor
	 *             is requested
	 * @throws IllegalArgumentException if no property name is specified
	 */
	public DynamicProperty getDynamicProperty(String name)
	{
		if (name == null) throw new IllegalArgumentException("No property name specified");

		return ((DynamicProperty) propertiesMap.get(name));
	}

	/**
	 * <p>Return an array of <code>ProperyDescriptors</code> for the properties
	 * currently defined in this DynamicClass.  If no properties are defined, a
	 * zero-length array will be returned.</p>
	 */
	public DynamicProperty[] getDynamicProperties()
	{
		return (properties);
	}

	/**
	 * <p>Instantiate and return a new DynamicBean instance, associated
	 * with this DynamicClass.  <strong>NOTE</strong> - This operation is not
	 * supported, and throws an exception.</p>
	 *
	 * @throws IllegalAccessException if the Class or the appropriate
	 *                                constructor is not accessible
	 * @throws InstantiationException if this Class represents an abstract
	 *                                class, an array class, a primitive type, or void; or if instantiation
	 *                                fails for some other reason
	 */
	public DynamicBean newInstance() throws IllegalAccessException, InstantiationException
	{
		throw new UnsupportedOperationException("newInstance() not supported");
	}

	/**
	 * <p>Loads and returns the <code>Class</code> of the given name.
	 * By default, a load from the thread context class loader is attempted.
	 * If there is no such class loader, the class loader used to load this
	 * class will be utilized.</p>
	 *
	 * @throws SQLException if an exception was thrown trying to load
	 *                      the specified class
	 */
	protected Class loadClass(String className) throws SQLException
	{
		try
		{
			ClassLoader cl = Thread.currentThread().getContextClassLoader();
			if (cl == null) cl = this.getClass().getClassLoader();

			return (cl.loadClass(className));
		}
		catch (Exception e)
		{
			throw new SQLException("Cannot load column class '" + className + "': " + e);
		}

	}

	/**
	 * <p>Factory method to create a new DynaProperty for the given index
	 * into the result set metadata.</p>
	 *
	 * @param metadata is the result set metadata
	 * @param i        is the column index in the metadata
	 * @return the newly created DynaProperty instance
	 */
	protected DynamicProperty createDynamicProperty(ResultSetMetaData metadata, int i) throws SQLException
	{

		String name = null;
		if (lowerCase) name = metadata.getColumnName(i).toLowerCase();
			else name = metadata.getColumnName(i);

		String className = null;
		try
		{
			className = metadata.getColumnClassName(i);
		}
		catch (SQLException e)
		{
			// this is a patch for HsqlDb to ignore exceptions
			// thrown by its metadata implementation
		}

		// Default to Object type if no class name could be retrieved
		// from the metadata
		Class clazz = Object.class;
		if (className != null) clazz = loadClass(className);

		return new DynamicProperty(name, clazz);

	}

	/**
	 * <p>Introspect the metadata associated with our result set, and populate
	 * the <code>properties</code> and <code>propertiesMap</code> instance
	 * variables.</p>
	 *
	 * @param resultSet The <code>resultSet</code> whose metadata is to
	 *                  be introspected
	 * @throws SQLException if an error is encountered processing the
	 *                      result set metadata
	 */
	protected void introspect(ResultSet resultSet) throws SQLException
	{

		// Accumulate an ordered list of DynaProperties
		ArrayList list = new ArrayList();
		ResultSetMetaData metadata = resultSet.getMetaData();
		int n = metadata.getColumnCount();

		for (int i = 1; i <= n; i++)
		{ // JDBC is one-relative!
			DynamicProperty dynamicProperty = createDynamicProperty(metadata, i);
			if (dynamicProperty != null) list.add(dynamicProperty);
		}

		// Convert this list into the internal data structures we need
		properties = (DynamicProperty[]) list.toArray(new DynamicProperty[list.size()]);
		for (int i = 0; i < properties.length; i++)
		{
			propertiesMap.put(properties[i].getName(), properties[i]);
		}
	}
}
