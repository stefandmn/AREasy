package org.areasy.common.data.bean;


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

import java.sql.SQLException;
import java.util.Iterator;
import java.util.NoSuchElementException;


/**
 * <p>Implementation of <code>java.util.Iterator</code> returned by the
 * <code>iterator()</code> method of {@link ResultSetDynamicClass}.  Each
 * object returned by this iterator will be a {@link DynamicBean} that
 * represents a single row from the result set being wrapped.</p>
 *
 * @version $Id: ResultSetIterator.java,v 1.2 2008/05/14 09:32:37 swd\stefan.damian Exp $
 */

public class ResultSetIterator implements DynamicBean, Iterator
{
	/**
	 * <p>Construct an <code>Iterator</code> for the result set being wrapped
	 * by the specified {@link ResultSetDynamicClass}.</p>
	 *
	 * @param dynaClass The {@link ResultSetDynamicClass} wrapping the
	 *                  result set we will iterate over
	 */
	ResultSetIterator(ResultSetDynamicClass dynaClass)
	{
		this.dynamicClass = dynaClass;
	}

	/**
	 * <p>Flag indicating whether the result set is currently positioned at a
	 * row for which we have not yet returned an element in the iteration.</p>
	 */
	protected boolean current = false;

	/**
	 * <p>The {@link ResultSetDynamicClass} we are associated with.</p>
	 */
	protected ResultSetDynamicClass dynamicClass = null;

	/**
	 * <p>Flag indicating whether the result set has indicated that there are
	 * no further rows.</p>
	 */
	protected boolean eof = false;

	/**
	 * Does the specified mapped property contain a value for the specified
	 * key value?
	 *
	 * @param name Name of the property to check
	 * @param key  Name of the key to check
	 * @throws IllegalArgumentException if there is no property of the specified name
	 */
	public boolean contains(String name, String key)
	{
		throw new UnsupportedOperationException ("Mapped properties not currently supported");
	}


	/**
	 * Return the value of a simple property with the specified name.
	 *
	 * @param name Name of the property whose value is to be retrieved
	 * @throws IllegalArgumentException if there is no property
	 *                                  of the specified name
	 */
	public Object get(String name)
	{

		if (dynamicClass.getDynamicProperty(name) == null) throw new IllegalArgumentException(name);

		try
		{
			return (dynamicClass.getResultSet().getObject(name));
		}
		catch (SQLException e)
		{
			throw new RuntimeException("get(" + name + "): SQLException: " + e);
		}

	}


	/**
	 * Return the value of an indexed property with the specified name.
	 *
	 * @param name  Name of the property whose value is to be retrieved
	 * @param index Index of the value to be retrieved
	 * @throws IllegalArgumentException  if there is no property
	 *                                   of the specified name
	 * @throws IllegalArgumentException  if the specified property
	 *                                   exists, but is not indexed
	 * @throws IndexOutOfBoundsException if the specified index
	 *                                   is outside the range of the underlying property
	 * @throws NullPointerException      if no array or List has been
	 *                                   initialized for this property
	 */
	public Object get(String name, int index)
	{
		throw new UnsupportedOperationException("Indexed properties not currently supported");
	}


	/**
	 * Return the value of a mapped property with the specified name,
	 * or <code>null</code> if there is no value for the specified key.
	 *
	 * @param name Name of the property whose value is to be retrieved
	 * @param key  Key of the value to be retrieved
	 * @throws IllegalArgumentException if there is no property
	 *                                  of the specified name
	 * @throws IllegalArgumentException if the specified property
	 *                                  exists, but is not mapped
	 */
	public Object get(String name, String key)
	{
		throw new UnsupportedOperationException("Mapped properties not currently supported");
	}


	/**
	 * Return the <code>DynamicClass</code> instance that describes the set of
	 * properties available for this DynamicBean.
	 */
	public DynamicClass getDynamicClass()
	{
		return (this.dynamicClass);
	}


	/**
	 * Remove any existing value for the specified key on the
	 * specified mapped property.
	 *
	 * @param name Name of the property for which a value is to
	 *             be removed
	 * @param key  Key of the value to be removed
	 * @throws IllegalArgumentException if there is no property
	 *                                  of the specified name
	 */
	public void remove(String name, String key)
	{
		throw new UnsupportedOperationException("Mapped operations not currently supported");
	}


	/**
	 * Set the value of a simple property with the specified name.
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
		if (dynamicClass.getDynamicProperty(name) == null) throw new IllegalArgumentException(name);

		try
		{
			dynamicClass.getResultSet().updateObject(name, value);
		}
		catch (SQLException e)
		{
			throw new RuntimeException("set(" + name + "): SQLException: " + e);
		}
	}


	/**
	 * Set the value of an indexed property with the specified name.
	 *
	 * @param name  Name of the property whose value is to be set
	 * @param index Index of the property to be set
	 * @param value Value to which this property is to be set
	 * @throws ConversionException       if the specified value cannot be
	 *                                   converted to the type required for this property
	 * @throws IllegalArgumentException  if there is no property
	 *                                   of the specified name
	 * @throws IllegalArgumentException  if the specified property
	 *                                   exists, but is not indexed
	 * @throws IndexOutOfBoundsException if the specified index
	 *                                   is outside the range of the underlying property
	 */
	public void set(String name, int index, Object value)
	{
		throw new UnsupportedOperationException("Indexed properties not currently supported");
	}


	/**
	 * Set the value of a mapped property with the specified name.
	 *
	 * @param name  Name of the property whose value is to be set
	 * @param key   Key of the property to be set
	 * @param value Value to which this property is to be set
	 * @throws ConversionException      if the specified value cannot be
	 *                                  converted to the type required for this property
	 * @throws IllegalArgumentException if there is no property
	 *                                  of the specified name
	 * @throws IllegalArgumentException if the specified property
	 *                                  exists, but is not mapped
	 */
	public void set(String name, String key, Object value)
	{
		throw new UnsupportedOperationException("Mapped properties not currently supported");
	}

	/**
	 * <p>Return <code>true</code> if the iteration has more elements.</p>
	 */
	public boolean hasNext()
	{

		try
		{
			advance();

			return (!eof);
		}
		catch (SQLException e)
		{
			throw new RuntimeException("hasNext():  SQLException:  " + e);
		}

	}


	/**
	 * <p>Return the next element in the iteration.</p>
	 */
	public Object next()
	{

		try
		{
			advance();
			if (eof) throw new NoSuchElementException();

			current = false;
			return (this);
		}
		catch (SQLException e)
		{
			throw new RuntimeException("next():  SQLException:  " + e);
		}
	}

	/**
	 * <p>Remove the current element from the iteration.  This method is
	 * not supported.</p>
	 */
	public void remove()
	{
		throw new UnsupportedOperationException("Remove the current element not currently supported");
	}

	/**
	 * <p>Advance the result set to the next row, if there is not a current
	 * row (and if we are not already at eof).</p>
	 *
	 * @throws SQLException if the result set throws an exception
	 */
	protected void advance() throws SQLException
	{
		if (!current && !eof)
		{
			if (dynamicClass.getResultSet().next())
			{
				current = true;
				eof = false;
			}
			else
			{
				current = false;
				eof = true;
			}
		}
	}
}
