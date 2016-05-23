package org.areasy.common.support.configuration.providers.database;

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

import org.areasy.common.data.StringEscapeUtility;
import org.areasy.common.data.StringUtility;
import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;
import org.areasy.common.support.configuration.ConfigurationEntry;
import org.areasy.common.support.configuration.ConfigurationException;
import org.areasy.common.support.configuration.ConfigurationLocator;
import org.areasy.common.support.configuration.ConfigurationStream;
import org.areasy.common.support.configuration.base.BaseConfigurationEntry;
import org.areasy.common.support.configuration.base.BaseConfigurationLocator;

import java.sql.*;
import java.util.Iterator;
import java.util.Properties;


/**
 *  Implementation of locator interface for a database.
 * Database repository for a configuration structure must supply a specific table and two columns (one for
 * data key (value) and second for data entry). Table must have an index on key column and value column must be not null.
 * Do not supply a unique constraint on key column.
 *
 * @version $Id: DatabaseLocator.java,v 1.3 2008/05/14 09:32:34 swd\stefan.damian Exp $
 */
public class DatabaseLocator extends BaseConfigurationLocator implements ConfigurationStream
{
	/**
	 * Static logger
	 */
	private Logger logger = LoggerFactory.getLog(DatabaseLocator.class);

	/** Source for this configuration locator */
	DatabaseLocatorObject database = null;

	/**
	 * Default constructor establishing base (parent - root) locator
	 *
	 * @param base database locator identity (connectivity structure)
	 */
	public DatabaseLocator(DatabaseLocatorObject base)
	{
		super();
		this.database = base;
	}

	/**
	 * Create a database locator, specifing a connection structure, a table name and columns.
	 *
	 * @param connection database connection
	 * @param table table name
	 */
	public DatabaseLocator(Connection connection, String table, String keyColumn, String valueColumn)
	{
		super();
		this.database = new DatabaseLocatorObject(connection, table, keyColumn, valueColumn);
	}

	/**
	 * Create a database locator, creating a database connection and specifing table name and columns.
	 * Database connectionis created with auto-commit.
	 */
	public DatabaseLocator(String driverClass, String url, String userName, String password,
						   String table, String keyColumn, String valueColumn) throws ConfigurationException
	{
		super();

		Connection connection = null;
		Driver driverInstance = null;

		try
		{
			Class dc = DatabaseLocator.class.getClassLoader().loadClass(driverClass);
			if(dc == null) dc = Class.forName(driverClass);

			driverInstance = (Driver) dc.newInstance();
		}
		catch (ClassNotFoundException e)
		{
			throw new ConfigurationException("Class not found: JDBC driver '" + driverClass + "' could not be loaded");
		}
		catch (IllegalAccessException e)
		{
			throw new ConfigurationException("Illegal access: JDBC driver '" + driverClass + "' could not be used", e);
		}
		catch (InstantiationException e)
		{
			throw new ConfigurationException("Instantiation exception: JDBC driver " + driverClass + " could not be instantiated", e);
		}

		// Load driver and generate database connection.
		try
		{
			Properties info = new Properties();
			info.put("user", userName);
			info.put("password", password);

			connection = driverInstance.connect(url, info);

			if (connection == null) throw new SQLException("No suitable driver for url: " + url);
			connection.setAutoCommit(true);
		}
		catch(Throwable th)
		{
			throw new ConfigurationException(th);
		}

		this.database = new DatabaseLocatorObject(connection, table, keyColumn, valueColumn);
	}

	/**
	 * Constructor to define a new locator with a specific parent
	 */
	public DatabaseLocator(ConfigurationLocator parent, DatabaseLocatorObject base)
	{
		super(parent);
		this.database = base;
	}

	/**
	 * Get real location from the current locator. For database repository wil exist only one
	 * repository (a single database connection)
	 */
	public Object getSource()
	{
		return this.database;
	}

	/**
	 * Get configuration locator identify. This implementation will return input table name value.
	 */
	public Object getIdentity()
	{
		return this.database.getTable();
	}

	/**
	 * Read a configuration entries. Concatenates lines ending with "\".
	 *
	 * @param all flag which should be null
	 * @throws ConfigurationException
	 */
	public void read(boolean all) throws ConfigurationException
	{
		String sql = "SELECT * FROM " + this.database.getTable();

		Statement stmt = null;

		try
		{
			//create statement
			stmt = database.getConnection().createStatement();

			//execute statement.
			stmt.execute(sql);
			ResultSet set = stmt.getResultSet();

			while(set.next())
			{
				String key = set.getString(database.getKeyColumn());
				String value = set.getString(database.getValueColumn());

				//create entry.
				ConfigurationEntry entry = null;
				if(StringUtility.isEmpty(key)) entry = new BaseConfigurationEntry(StringEscapeUtility.unescapeJava(value));
					else entry = new BaseConfigurationEntry(StringEscapeUtility.unescapeJava(key), StringEscapeUtility.unescapeJava(value));

				//validate entry.
				if(entry.isData() && containsKey(entry.getKey()))
				{
					//append value to the old entity.
					ConfigurationEntry old = getEntry(entry.getKey());
					old.addValue(entry.getValue());
				}
				else addNode(entry);
			}
		}
		catch(Exception e)
		{
			throw new ConfigurationException("Error reading configuration entries from '" + database.getTable() + "' table", e);
		}
		finally
		{
			try
			{
				if(stmt != null) stmt.close();
			}
			catch(SQLException sqle)
			{
				logger.debug("Error closing statement: " + sqle.getMessage());
			}
		}
	}

	/**
	 * Write configuration entries from a specific locator.
	 *
	 * @throws ConfigurationException
	 */
	public void write(boolean all) throws ConfigurationException
	{
		String delete = "DELETE * FROM " + this.database.getTable();
		String insert = "INSERT INTO " + this.database.getTable() + " (" + this.database.getKeyColumn() + ", " + this.database.getValueColumn() + ") VALUES (?, ?)";

		PreparedStatement pstmtd = null;
		PreparedStatement pstmti = null;

		Iterator iterator = getAllEntries();
		if(iterator == null) return;

		try
		{
			//create and execute delete statement
			pstmtd =  this.database.getConnection().prepareStatement(delete);
			pstmtd.executeUpdate();

			//make commit if is necessary
			if(!this.database.getConnection().getAutoCommit()) this.database.getConnection().commit();

			//execute insert statements.
			while(iterator.hasNext())
			{
				//create statement
				pstmti =  this.database.getConnection().prepareStatement(insert);

				//fill statement
				ConfigurationEntry entry = (ConfigurationEntry) iterator.next();
				if(entry.isComment())
				{
					pstmti.setString(1, "NULL");
					pstmti.setString(2, StringEscapeUtility.escapeJava( entry.getComment()) );
				}
				else if(entry.isData())
				{
					for(int j = 0; j < entry.getValues().size(); j++)
					{
						pstmti.setString(1, StringEscapeUtility.escapeJava( entry.getKey()) );
						pstmti.setString(2, StringEscapeUtility.escapeJava( (String)entry.getValues().get(j)) );
					}
				}

				//execute statement
				pstmti.executeUpdate();
				pstmti.close();
				pstmti = null;
			}

			//make commit if is necessary
			if(!this.database.getConnection().getAutoCommit()) this.database.getConnection().commit();
		}
		catch(Exception e)
		{
			throw new ConfigurationException("Error writing configuration entries in '" + database.getTable() + "' table", e);
		}
		finally
		{
			try
			{
				if(pstmtd != null) pstmtd.close();
				if(pstmti != null) pstmti.close();
			}
			catch(SQLException sqle)
			{
				logger.debug("Error closing statement(s): " + sqle.getMessage());
			}
		}
	}
}
