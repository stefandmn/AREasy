package org.areasy.common.velocity.runtime.resource.loader;

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

import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;
import org.areasy.common.support.configuration.Configuration;
import org.areasy.common.velocity.base.ResourceNotFoundException;
import org.areasy.common.velocity.runtime.resource.Resource;

import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * This is a simple template file loader that loads templates
 * from a DataSource instead of plain files.
 * <p/>
 * It can be configured with a datasource name, a table name,
 * id column (name), content column (the template body) and a
 * datetime column (for last modification info).
 * <br>
 * <br>
 * Example configuration snippet for velocity.properties:
 * <br>
 * <br>
 * resource.loader = file, ds <br>
 * <br>
 * ds.resource.loader.public.name = DataSource <br>
 * ds.resource.loader.description = Velocity DataSource Resource Loader <br>
 * ds.resource.loader.class = org.areasy.common.parser.engines.velocity.runtime.resource.loader.DataSourceResourceLoader <br>
 * ds.resource.loader.resource.datasource = java:comp/env/jdbc/Velocity <br>
 * ds.resource.loader.resource.table = tb_velocity_template <br>
 * ds.resource.loader.resource.keycolumn = id_template <br>
 * ds.resource.loader.resource.templatecolumn = template_definition <br>
 * ds.resource.loader.resource.timestampcolumn = template_timestamp <br>
 * ds.resource.loader.cache = false <br>
 * ds.resource.loader.modification.check.interval = 60 <br>
 * <br>
 * Example WEB-INF/web.xml: <br>
 * <br>
 * <resource-ref> <br>
 * <description>Velocity template DataSource</description> <br>
 * <res-ref-name>jdbc/Velocity</res-ref-name> <br>
 * <res-type>javax.sql.DataSource</res-type> <br>
 * <res-auth>Container</res-auth> <br>
 * </resource-ref> <br>
 * <br>
 * <br>
 * and Tomcat 4 server.xml file: <br>
 * [...] <br>
 * <Context path="/exampleVelocity" docBase="exampleVelocity" debug="0"> <br>
 * [...] <br>
 * <ResourceParams name="jdbc/Velocity"> <br>
 * <parameter> <br>
 * <name>driverClassName</name> <br>
 * <value>org.hsql.jdbcDriver</value> <br>
 * </parameter> <br>
 * <parameter> <br>
 * <name>driverName</name> <br>
 * <value>jdbc:HypersonicSQL:database</value> <br>
 * </parameter> <br>
 * <parameter> <br>
 * <name>user</name> <br>
 * <value>database_username</value> <br>
 * </parameter> <br>
 * <parameter> <br>
 * <name>password</name> <br>
 * <value>database_password</value> <br>
 * </parameter> <br>
 * </ResourceParams> <br>
 * [...] <br>
 * </Context> <br>
 * [...] <br>
 * <br>
 * Example sql script:<br>
 * CREATE TABLE tb_velocity_template ( <br>
 * id_template varchar (40) NOT NULL , <br>
 * template_definition text (16) NOT NULL , <br>
 * template_timestamp datetime NOT NULL  <br>
 * ) <br>
 *
 * @version $Id: DataSourceResourceLoader.java,v 1.1 2008/05/25 22:33:15 swd\stefan.damian Exp $
 */
public class DataSourceResourceLoader extends ResourceLoader
{
	/** the logger */
	private static Logger logger = LoggerFactory.getLog(DataSourceResourceLoader.class.getName());

	private String dataSourceName;
	private String tableName;
	private String keyColumn;
	private String templateColumn;
	private String timestampColumn;
	private InitialContext ctx;
	private DataSource dataSource;

	public void init(Configuration configuration)
	{
		dataSourceName = configuration.getString("resource.datasource");
		tableName = configuration.getString("resource.table");
		keyColumn = configuration.getString("resource.keycolumn");
		templateColumn = configuration.getString("resource.templatecolumn");
		timestampColumn = configuration.getString("resource.timestampcolumn");

		logger.debug("Resources loaded from: " + dataSourceName + "/" + tableName);
		logger.debug("Resource loader using columns: " + keyColumn + ", " + templateColumn + " and " + timestampColumn);
	}

	public boolean isSourceModified(Resource resource)
	{
		return (resource.getLastModified() != readLastModified(resource, "checking timestamp"));
	}

	public long getLastModified(Resource resource)
	{
		return readLastModified(resource, "getting timestamp");
	}

	/**
	 * Get an InputStream so that the Runtime can build a
	 * template with it.
	 *
	 * @param name name of template
	 * @return InputStream containing template
	 */
	public synchronized InputStream getResourceStream(String name) throws ResourceNotFoundException
	{
		if (name == null || name.length() == 0) throw new ResourceNotFoundException("Need to specify a template name!");

		try
		{
			Connection conn = openDbConnection();

			try
			{
				ResultSet rs = readData(conn, templateColumn, name);

				try
				{
					if (rs.next())
					{
						return new BufferedInputStream(rs.getAsciiStream(templateColumn));
					}
					else
					{
						String msg = "Cannot find resource " + name;
						logger.error(msg);

						throw new ResourceNotFoundException(msg);
					}
				}
				finally
				{
					rs.close();
				}
			}
			finally
			{
				closeDbConnection(conn);
			}
		}
		catch (Exception e)
		{
			String msg = "Database problem trying to load resource " + name + ": " + e.toString();

			logger.error(msg);

			throw new ResourceNotFoundException(msg);

		}

	}

	/**
	 * Fetches the last modification time of the resource
	 *
	 * @param resource    Resource object we are finding timestamp of
	 * @param i_operation string for logging, indicating caller's intention
	 * @return timestamp as long
	 */
	private long readLastModified(Resource resource, String i_operation)
	{
		String name = resource.getName();

		try
		{
			Connection conn = openDbConnection();

			try
			{
				ResultSet rs = readData(conn, timestampColumn, name);
				try
				{
					if (rs.next())
					{
						return rs.getTimestamp(timestampColumn).getTime();
					}
					else
					{
						logger.error("While " + i_operation + " could not find resource " + name);
					}
				}
				finally
				{
					rs.close();
				}
			}
			finally
			{
				closeDbConnection(conn);
			}
		}
		catch (Exception e)
		{
			logger.error("Error while " + i_operation + " when trying to load resource " + name + ": " + e.toString());
			logger.debug("Exception", e);
		}
		return 0;
	}

	/**
	 * gets connection to the datasource specified through the configuration
	 * parameters.
	 *
	 * @return connection
	 */
	private Connection openDbConnection() throws Exception
	{
		if (ctx == null) ctx = new InitialContext();

		if (dataSource == null) dataSource = (DataSource) ctx.lookup(dataSourceName);

		return dataSource.getConnection();
	}

	/**
	 * Closes connection to the datasource
	 */
	private void closeDbConnection(Connection conn)
	{
		try
		{
			conn.close();
		}
		catch (Exception e)
		{
			logger.error("Problem when closing connection: " + e.toString());
			logger.debug("Exception", e);
		}
	}

	/**
	 * Reads the data from the datasource.  It simply does the following query :
	 * <br>
	 * SELECT <i>columnNames</i> FROM <i>tableName</i> WHERE <i>keyColumn</i>
	 * = '<i>templateName</i>'
	 * <br>
	 * where <i>keyColumn</i> is a class member set in init()
	 *
	 * @param conn         connection to datasource
	 * @param columnNames  columns to fetch from datasource
	 * @param templateName name of template to fetch
	 * @return result set from query
	 */
	private ResultSet readData(Connection conn, String columnNames, String templateName) throws SQLException
	{
		Statement stmt = conn.createStatement();

		String sql = "SELECT " + columnNames + " FROM " + tableName + " WHERE " + keyColumn + " = '" + templateName + "'";

		return stmt.executeQuery(sql);
	}
}
