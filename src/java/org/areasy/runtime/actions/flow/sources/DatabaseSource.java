package org.areasy.runtime.actions.flow.sources;

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

import org.areasy.runtime.engine.RuntimeLogger;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.common.data.StringUtility;

import java.sql.*;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Dedicated data source to process data from a database.
 */
public abstract class DatabaseSource extends AbstractSource
{
	private Connection connection = null;
	private String qualification = null;
	private ResultSet resultSet = null;

	/**
	 * Dedicated method to release resources that are used by a data-source
	 *
	 * @throws org.areasy.runtime.engine.base.AREasyException in case of any error will occur
	 */
	public void release() throws AREasyException
	{
		if(resultSet != null)
		{
			try
			{
				resultSet.close();
			}
			catch (Exception e) { /* ignore close errors */ }
		}

		if (getConnection() != null)
		{
			try
			{
				getConnection().close();
			}
			catch (Exception e) { /* ignore close errors */ }
	   }
	}

	/**
	 * Get database qualification that could be a statement (select) or a stored procedure call
	 *
	 * @return database qualification to select data and to point out the exact data source
	 */
	protected String getQualification()
	{
		return qualification;
	}

	/**
	 * Set database qualification that could be a statement (select) or a stored procedure call
	 *
	 * @param qualification database qualification to select data and to point out the exact data source
	 */
	protected void setQualification(String qualification)
	{
		this.qualification = qualification;
	}

	/**
	 * Get database connection structure
	 *
	 * @return SQL <code>Connection</code> instance
	 */
	protected Connection getConnection()
	{
		return connection;
	}

	protected void setConnection(String url, String driverSignature) throws AREasyException
	{
		try
		{
			String myURL = url;

			if(getAction().getConfiguration().containsKey("append2connectionstring"))
			{
				String append = getAction().getConfiguration().getString("append2connectionstring", null);

				if(StringUtility.isNotEmpty(append))
				{
					if(append.startsWith("&") || append.startsWith("?")) myURL += append;
					else
					{
						if(myURL.contains("&")) myURL += "&" + append;
							else myURL += "?" + append;
					}
				}
			}

			Class.forName(driverSignature).newInstance();
			this.connection = DriverManager.getConnection(myURL);
		}
		catch(Exception e)
		{
			throw new AREasyException("Error initiating database connection: " + e.getMessage(), e);
		}
	}

	protected void setConnection(String url, String driverSignature, String user, String password) throws AREasyException
	{
		try
		{
			String myURL = url;

			if(getAction().getConfiguration().containsKey("append2connectionstring"))
			{
				String append = getAction().getConfiguration().getString("append2connectionstring", null);

				if(StringUtility.isNotEmpty(append))
				{
					if(append.startsWith("&") || append.startsWith("?")) myURL += append;
					else
					{
						if(myURL.contains("&")) myURL += "&" + append;
							else myURL += "?" + append;
					}
				}
			}

			Class.forName(driverSignature).newInstance();
			this.connection = DriverManager.getConnection(myURL, user, password);
		}
		catch(Exception e)
		{
			throw new AREasyException("Error initiating database connection: " + e.getMessage(), e);
		}
	}

	/**
	 * Take and deliver through a <code>Map</code> structure the data headers from the selected data-source.
	 *
	 * @return a <code>Map</code> with data-source headers.
	 * @throws AREasyException in case of any error will occur
	 */
	public Map getHeaders() throws AREasyException
	{
		Map map = new Hashtable();

		try
		{
			Statement st = getConnection().createStatement();
			ResultSet rs = st.executeQuery(getQualification());
			ResultSetMetaData meta = rs.getMetaData();

			for(int i = 1; i <= meta.getColumnCount(); i++)
			{
				String label = meta.getColumnLabel(i);
				String name = meta.getColumnName(i);

				map.put(name, label);
			}

			//close statement
			st.close();
		}
		catch(SQLException sqle)
		{
			throw new AREasyException("Error getting database headers: " + sqle.getMessage(), sqle);
		}

		return map;
	}

	private void initResultSet() throws AREasyException
	{
		try
		{
			Statement statement = getConnection().createStatement();
			resultSet = statement.executeQuery(getQualification());
		}
		catch(SQLException sqle)
		{
			throw new AREasyException("Error initializing data cursor: " + sqle.getMessage(), sqle);
		}
	}

	/**
	 * Take and deliver through a <code>Map</code> structure the data read it from
	 * the selected data-source. If the output is null means that the data-source goes to the end.
	 *
	 * @param list this is the list of data source keys.
	 * @return a <code>Map</code> having data source indexes as keys and data as values.
	 * @throws AREasyException in case of any error will occur
	 */
	public Map getNextObject(List list) throws AREasyException
	{
		Map map = null;

		//initialize parser (if is not)
		if(resultSet == null) initResultSet();

		try
		{
			if(resultSet.next())
			{
				map = new HashMap();

				for(int i = 0; i < list.size(); i++)
				{
					String colName = (String) list.get(i);

					if(colName != null)
					{
						map.put(colName, resultSet.getObject(colName));
					}
				}
			}
		}
		catch(SQLException sqle)
		{
			throw new AREasyException("Error reading data: " + sqle.getMessage(), sqle);
		}

		return map;
	}

	/**
	 * Read and return the total number of records found in the data-source.
	 *
	 * @return number of records found
	 */
	public int getDataCount()
	{
		int numberOfRows = 0;

		if(getConnection() != null)
		{
			ResultSet resultSet = null;
			Statement statement = null;
			String query = getQualification();
			if(query == null) return numberOfRows;

			try
			{
				statement = getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
				resultSet = statement.executeQuery(query);

				resultSet.last();
				numberOfRows = resultSet.getRow();
			}
			catch(SQLException sqle)
			{
				RuntimeLogger.info("Error getting number of records: " + sqle.getMessage());

				getAction().getLogger().error("Error getting number of records: " + sqle.getMessage());
				getAction().getLogger().debug("Exception", sqle);
			}
			finally
			{
				try
				{
					if(resultSet != null) resultSet.close();
        			if(statement != null) statement.close();
				}
				catch (SQLException e) { /* nothing here */ }
			}
		}

		return numberOfRows;
	}
}
