package org.areasy.common.data.bean;


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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;


/**
 * <p>Implementation of <code>DynamicClass</code> for DynamicBeans that wrap the
 * <code>java.sql.Row</code> objects of a <code>java.sql.ResultSet</code>.
 * The normal usage pattern is something like:</p>
 * <pre>
 *   ResultSet rs = ...;
 *   ResultSetDynamicClass rsdc = new ResultSetDynamicClass(rs);
 *   Iterator rows = rsdc.iterator();
 *   while (rows.hasNext())  {
 *     DynamicBean row = (DynamicBean) rows.next();
 *     ... process this row ...
 *   }
 *   rs.close();
 * </pre>
 * <p/>
 * <p>Each column in the result set will be represented as a DynamicBean
 * property of the corresponding name (optionally forced to lower case
 * for portability).</p>
 * <p/>
 * <p><strong>WARNING</strong> - Any {@link DynamicBean} instance returned by
 * this class, or from the <code>Iterator</code> returned by the
 * <code>iterator()</code> method, is directly linked to the row that the
 * underlying result set is currently positioned at.  This has the following
 * implications:</p>
 * <ul>
 * <li>Once you retrieve a different {@link DynamicBean} instance, you should
 * no longer use any previous instance.</li>
 * <li>Changing the position of the underlying result set will change the
 * data that the {@link DynamicBean} references.</li>
 * <li>Once the underlying result set is closed, the {@link DynamicBean}
 * instance may no longer be used.</li>
 * </ul>
 * <p/>
 * <p>Any database data that you wish to utilize outside the context of the
 * current row of an open result set must be copied.  For example, you could
 * use the following code to create standalone copies of the information in
 * a result set:</p>
 * <pre>
 *   ArrayList results = new ArrayList(); // To hold copied list
 *   ResultSetDynamicClass rsdc = ...;
 *   DynaProperty properties[] = rsdc.getDynaProperties();
 *   BasicDynamicClass bdc =
 *     new BasicDynamicClass("foo", BasicDynamicBean.class,
 *                        rsdc.getDynaProperties());
 *   Iterator rows = rsdc.iterator();
 *   while (rows.hasNext()) {
 *     DynamicBean oldRow = (DynamicBean) rows.next();
 *     DynamicBean newRow = bdc.newInstance();
 *     PropertyUtils.copyProperties(newRow, oldRow);
 *     results.add(newRow);
 *   }
 * </pre>
 *
 * @version $Id: ResultSetDynamicClass.java,v 1.2 2008/05/14 09:32:37 swd\stefan.damian Exp $
 */

public class ResultSetDynamicClass extends JdbcDynamicClass implements DynamicClass
{
	/**
	 * <p>Construct a new ResultSetDynamicClass for the specified
	 * <code>ResultSet</code>.  The property names corresponding
	 * to column names in the result set will be lower cased.</p>
	 *
	 * @param resultSet The result set to be wrapped
	 * @throws NullPointerException if <code>resultSet</code>
	 *                              is <code>null</code>
	 * @throws SQLException         if the metadata for this result set
	 *                              cannot be introspected
	 */
	public ResultSetDynamicClass(ResultSet resultSet) throws SQLException
	{
		this(resultSet, true);
	}


	/**
	 * <p>Construct a new ResultSetDynamicClass for the specified
	 * <code>ResultSet</code>.  The property names corresponding
	 * to the column names in the result set will be lower cased or not,
	 * depending on the specified <code>lowerCase</code> value.</p>
	 * <p/>
	 * <p><strong>WARNING</strong> - If you specify <code>false</code>
	 * for <code>lowerCase</code>, the returned property names will
	 * exactly match the column names returned by your JDBC driver.
	 * Because different drivers might return column names in different
	 * cases, the property names seen by your application will vary
	 * depending on which JDBC driver you are using.</p>
	 *
	 * @param resultSet The result set to be wrapped
	 * @param lowerCase Should property names be lower cased?
	 * @throws NullPointerException if <code>resultSet</code>
	 *                              is <code>null</code>
	 * @throws SQLException         if the metadata for this result set
	 *                              cannot be introspected
	 */
	public ResultSetDynamicClass(ResultSet resultSet, boolean lowerCase) throws SQLException
	{
		if (resultSet == null) throw new NullPointerException();

		this.resultSet = resultSet;
		this.lowerCase = lowerCase;

		introspect(resultSet);
	}

	/**
	 * <p>The <code>ResultSet</code> we are wrapping.</p>
	 */
	protected ResultSet resultSet = null;

	/**
	 * <p>Return an <code>Iterator</code> of {@link DynamicBean} instances for
	 * each row of the wrapped <code>ResultSet</code>, in "forward" order.
	 * Unless the underlying result set supports scrolling, this method
	 * should be called only once.</p>
	 */
	public Iterator iterator()
	{
		return (new ResultSetIterator(this));
	}


	/**
	 * <p>Return the result set we are wrapping.</p>
	 */
	ResultSet getResultSet()
	{
		return (this.resultSet);
	}

	/**
	 * <p>Loads the class of the given name which by default uses the class loader used
	 * to load this library.
	 * Dervations of this class could implement alternative class loading policies such as
	 * using custom ClassLoader or using the Threads's context class loader etc.
	 * </p>
	 */
	protected Class loadClass(String className) throws SQLException
	{
		try
		{
			return getClass().getClassLoader().loadClass(className);
		}
		catch (Exception e)
		{
			throw new SQLException("Cannot load column class '" + className + "': " + e);
		}
	}
}
