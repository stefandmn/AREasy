package org.areasy.common.support.configuration.providers.database;

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

import java.sql.Connection;

/**
 * Define a database locator object.
 *
 * @version $Id: DatabaseLocatorObject.java,v 1.2 2008/05/14 09:32:34 swd\stefan.damian Exp $
 */
public class DatabaseLocatorObject
{
	/**
	 * Database connection.
	 */
	private Connection connection;

	/**
	 * The name of the table containing the configurations.
	 */
	private String table;

	/**
	 * The column containing the keys.
	 */
	private String keyColumn;

	/**
	 * The column containing the values.
	 */
	private String valueColumn;

	/**
	 * Create a database locator object, specifing a connection structure, a table name and columns.
	 * @param connection database connection
	 * @param table table name
	 * @param key column name
	 * @param value column value
	 */
	public DatabaseLocatorObject(Connection connection, String table, String key, String value)
	{
		this.connection = connection;
		this.table = table;
		
		this.keyColumn = key;
		this.valueColumn = value;
	}

	public Connection getConnection()
	{
		return connection;
	}

	public void setConnection(Connection connection)
	{
		this.connection = connection;
	}

	public String getTable()
	{
		return table;
	}

	public void setTable(String table)
	{
		this.table = table;
	}

	public String getKeyColumn()
	{
		return keyColumn;
	}

	public void setKeyColumn(String keyColumn)
	{
		this.keyColumn = keyColumn;
	}

	public String getValueColumn()
	{
		return valueColumn;
	}

	public void setValueColumn(String valueColumn)
	{
		this.valueColumn = valueColumn;
	}
}
