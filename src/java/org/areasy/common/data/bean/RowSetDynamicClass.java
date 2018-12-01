package org.areasy.common.data.bean;


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

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Implementation of {@link DynamicClass} that creates an in-memory collection
 * of {@link DynamicBean}s representing the results of an SQL query.  Once the
 * {@link DynamicClass} instance has been created, the JDBC <code>ResultSet</code>
 * and <code>Statement</code> on which it is based can be closed, and the
 * underlying <code>Connection</code> can be returned to its connection pool
 * (if you are using one).</p>
 * <p/>
 * <p>The normal usage pattern is something like:</p>
 * <pre>
 *   Connection conn = ...;  // Acquire connection from pool
 *   Statement stmt = conn.createStatement();
 *   ResultSet rs = stmt.executeQuery("SELECT ...");
 *   RowSetDynamicClass rsdc = new RowSetDynamicClass(rs);
 *   rs.close();
 *   stmt.close();
 *   ...;                    // Return connection to pool
 *   List rows = rsdc.getRows();
 *   ...;                   // Process the rows as desired
 * </pre>
 * <p/>
 * <p>Each column in the result set will be represented as a {@link DynamicBean}
 * property of the corresponding name (optionally forced to lower case
 * for portability).  There will be one {@link DynamicBean} in the
 * <code>List</code> returned by <code>getRows()</code> for each
 * row in the original <code>ResultSet</code>.</p>
 * <p/>
 * <p>In general, instances of {@link RowSetDynamicClass} can be serialized
 * and deserialized, which will automatically include the list of
 * {@link DynamicBean}s representing the data content.  The only exception
 * to this rule would be when the underlying property values that were
 * copied from the <code>ResultSet</code> originally cannot themselves
 * be serialized.  Therefore, a {@link RowSetDynamicClass} makes a very
 * convenient mechanism for transporting data sets to remote Java-based
 * application components.</p>
 *
 * @version $Id: RowSetDynamicClass.java,v 1.2 2008/05/14 09:32:37 swd\stefan.damian Exp $
 */

public class RowSetDynamicClass extends JdbcDynamicClass implements DynamicClass, Serializable
{
	/**
	 * <p>Limits the size of the returned list.  The call to
	 * <code>getRows()</code> will return at most limit number of rows.
	 * If less than or equal to 0, does not limit the size of the result.
	 */
	protected int limit = -1;

	/**
	 * <p>The list of {@link DynamicBean}s representing the contents of
	 * the original <code>ResultSet</code> on which this
	 * {@link RowSetDynamicClass} was based.</p>
	 */
	protected List rows = new ArrayList();


	/**
	 * <p>Construct a new {@link RowSetDynamicClass} for the specified
	 * <code>ResultSet</code>.  The property names corresponding
	 * to column names in the result set will be lower cased.</p>
	 *
	 * @param resultSet The result set to be wrapped
	 * @throws NullPointerException if <code>resultSet</code>
	 *                              is <code>null</code>
	 * @throws SQLException         if the metadata for this result set
	 *                              cannot be introspected
	 */
	public RowSetDynamicClass(ResultSet resultSet) throws SQLException
	{
		this(resultSet, true, -1);
	}

	/**
	 * <p>Construct a new {@link RowSetDynamicClass} for the specified
	 * <code>ResultSet</code>.  The property names corresponding
	 * to column names in the result set will be lower cased.</p>
	 * <p/>
	 * If <code>limit</code> is not less than 0, max <code>limit</code>
	 * number of rows will be copied into the list.
	 *
	 * @param resultSet The result set to be wrapped
	 * @param limit     The maximum for the size of the result.
	 * @throws NullPointerException if <code>resultSet</code>
	 *                              is <code>null</code>
	 * @throws SQLException         if the metadata for this result set
	 *                              cannot be introspected
	 */
	public RowSetDynamicClass(ResultSet resultSet, int limit) throws SQLException
	{
		this(resultSet, true, limit);
	}


	/**
	 * <p>Construct a new {@link RowSetDynamicClass} for the specified
	 * <code>ResultSet</code>.  The property names corresponding
	 * to the column names in the result set will be lower cased or not,
	 * depending on the specified <code>lowerCase</code> value.</p>
	 * <p/>
	 * If <code>limit</code> is not less than 0, max <code>limit</code>
	 * number of rows will be copied into the resultset.
	 *
	 * @param resultSet The result set to be wrapped
	 * @param lowerCase Should property names be lower cased?
	 * @throws NullPointerException if <code>resultSet</code>
	 *                              is <code>null</code>
	 * @throws SQLException         if the metadata for this result set
	 *                              cannot be introspected
	 */
	public RowSetDynamicClass(ResultSet resultSet, boolean lowerCase) throws SQLException
	{
		this(resultSet, lowerCase, -1);
	}

	/**
	 * <p>Construct a new {@link RowSetDynamicClass} for the specified
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
	public RowSetDynamicClass(ResultSet resultSet, boolean lowerCase, int limit) throws SQLException
	{
		if (resultSet == null) throw new NullPointerException();

		this.lowerCase = lowerCase;
		this.limit = limit;

		introspect(resultSet);
		copy(resultSet);
	}

	/**
	 * <p>Return a <code>List</code> containing the {@link DynamicBean}s that
	 * represent the contents of each <code>Row</code> from the
	 * <code>ResultSet</code> that was the basis of this
	 * {@link RowSetDynamicClass} instance.  These {@link DynamicBean}s are
	 * disconnected from the database itself, so there is no problem with
	 * modifying the contents of the list, or the values of the properties
	 * of these {@link DynamicBean}s.  However, it is the application's
	 * responsibility to persist any such changes back to the database,
	 * if it so desires.</p>
	 */
	public List getRows()
	{
		return (this.rows);
	}

	/**
	 * <p>Copy the column values for each row in the specified
	 * <code>ResultSet</code> into a newly created {@link DynamicBean}, and add
	 * this bean to the list of {@link DynamicBean}s that will later by
	 * returned by a call to <code>getRows()</code>.</p>
	 *
	 * @param resultSet The <code>ResultSet</code> whose data is to be
	 *                  copied
	 * @throws SQLException if an error is encountered copying the data
	 */
	protected void copy(ResultSet resultSet) throws SQLException
	{
		int cnt = 0;
		while (resultSet.next() && (limit < 0 || cnt++ < limit))
		{
			DynamicBean bean = createDynamicBean();
			for (int i = 0; i < properties.length; i++)
			{
				String name = properties[i].getName();
				bean.set(name, resultSet.getObject(name));
			}

			rows.add(bean);
		}
	}

	/**
	 * <p>Create and return a new {@link DynamicBean} instance to be used for
	 * representing a row in the underlying result set.</p>
	 */
	protected DynamicBean createDynamicBean()
	{
		return (new BasicDynamicBean(this));
	}
}
