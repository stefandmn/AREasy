package org.areasy.runtime.actions.arserver.data.flow.sources;

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

import org.areasy.runtime.RuntimeManager;
import org.areasy.runtime.engine.RuntimeLogger;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.runtime.engine.services.parser.ParserEngine;
import org.areasy.runtime.engine.workflows.ProcessorLevel0Reader;
import org.areasy.runtime.engine.workflows.ProcessorLevel1Context;
import org.areasy.runtime.utilities.StreamUtility;
import org.areasy.common.data.NumberUtility;
import org.areasy.common.data.StringUtility;
import org.areasy.common.support.configuration.Configuration;
import org.areasy.common.support.configuration.base.BaseConfiguration;

import java.io.*;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Local File data-source handles data from a file that will be stored on AR Easy Runtime server file system.
 */
public class ServerFileSource extends AbstractSource
{
	private File file = null;

	private ParserEngine parser = null;

	private boolean remoteFile = false;

	/**
	 * Dedicated method that has to be used internally, to detect and validate the data-source configuration (<code>CoreItem</code> structure)
	 * and to identify the original file data-source
	 *
	 * @throws AREasyException in case of any error will occur
	 */
	public void init() throws AREasyException
	{
		String type = getSourceItem().getStringAttributeValue(536870981);

		if (StringUtility.equalsIgnoreCase(type, "Local File"))
		{
			file = ProcessorLevel1Context.getDataFile(getAction().getServerConnection(), getSourceItem().getFormName(), "536870931", "179", getSourceItem().getStringAttributeValue(179));
		}
		else if (StringUtility.equalsIgnoreCase(type, "Remote File"))
		{
			file = new File(getSourceItem().getStringAttributeValue(536870976));
			remoteFile = true;
		}

		//validation
		if (!remoteFile && ((file == null || !file.exists()))) throw new AREasyException("File data source doesn't exist: " + file);
	}

	/**
	 * Dedicated method to release resources that are used by a data-source
	 *
	 * @throws AREasyException in case of any error will occur
	 */
	public void release() throws AREasyException
	{
		//close parser
		if (parser != null) parser.close();

		//check and delete the file
		if (getFile().delete()) getFile().deleteOnExit();
	}

	/**
	 * Take and deliver through a <code>Map</code> structure the data headers from the selected data-source.
	 *
	 * @return a <code>Map</code> with data-source headers.
	 * @throws AREasyException in case of any error will occur
	 */
	public Map getHeaders() throws AREasyException
	{
		Map map = null;

		//initialize parser (if is not)
		if (parser == null) initParser();

		try
		{
			String headers[] = null;

			//find data
			for (int i = parser.getStartIndex() - 1; map == null && i >= 0; i--)
			{
				headers = parser.read(i);

				//validate and create data map
				if (!getAction().isDataEmpty(headers))
				{
					map = new Hashtable();

					for (int x = 0; x < headers.length; x++)
					{
						String fieldId = ProcessorLevel0Reader.getGenericColumnFromIndex(x);
						String fieldName = headers[x];

						if (StringUtility.isEmpty(fieldName)) fieldName = fieldId;

						if (map.containsKey(fieldName))
						{
							fieldName += "(" + fieldId + ")";
							map.put(fieldName, fieldId);
						}
						else map.put(fieldName, fieldId);
					}
				}
			}

			if (map == null)
			{
				int noOfCol = parser.getNumberOfColumns();

				if (noOfCol > 0)
				{
					map = new Hashtable();

					for (int x = 0; x < noOfCol; x++)
					{
						String field = ProcessorLevel0Reader.getGenericColumnFromIndex(x);
						map.put(field, field);
					}
				}
				else getAction().getLogger().warn("Invalid parser information: no headers identified and no number od columns found");
			}
		}
		catch (AREasyException are)
		{
			throw are;
		}
		catch (Throwable th)
		{
			throw new AREasyException("Error initializing parser engine to read deaders: " + th.getMessage(), th);
		}

		return map;
	}

	/**
	 * Get data source file.
	 *
	 * @return <code>File</code> instance
	 */
	protected File getFile()
	{
		return this.file;
	}

	/**
	 * Initialize parser engine to extract data (not headers)
	 *
	 * @throws AREasyException in case of any error will occur
	 */
	private void initParser() throws AREasyException
	{
		int pageIndex = Math.max((Integer) getSourceItem().getAttributeValue(536871007) - 1, 0);
		int startIndex = Math.max((Integer) getSourceItem().getAttributeValue(536870929) - 1, 0);
		int endIndex = Math.max((Integer) getSourceItem().getAttributeValue(536871009) - 1, 0);

		if (remoteFile)
		{
			String remoteServerName = getSourceItem().getStringAttributeValue(536870965);
			String remoteUserName = getSourceItem().getStringAttributeValue(536870967);
			String remoteUserPassword = getSourceItem().getStringAttributeValue(536870969);
			Object remoteProtocol = getSourceItem().getAttributeValue(536870970);

			if (remoteServerName != null)
			{
				int protocol = -1;
				if (remoteProtocol != null) protocol = ((Integer) remoteProtocol).intValue();

				if (protocol == 1)
				{
					int remotePort = 22;
					byte[] buf = new byte[1024];
					FileOutputStream fos = null;

					if (remoteServerName.contains(":"))
					{
						int portSeparator = remoteUserName.indexOf("\\", 0);
						remoteServerName = remoteUserName.substring(0, portSeparator);
						remotePort = NumberUtility.toInt(remoteUserName.substring(portSeparator + 1), 0);
					}

					try
					{
						com.jcraft.jsch.Session session = (new com.jcraft.jsch.JSch()).getSession(remoteUserName, remoteServerName, remotePort);
						session.setPassword(remoteUserPassword);

						java.util.Properties config = new java.util.Properties();
						config.put("StrictHostKeyChecking", "no");
						session.setConfig(config);
						session.connect();

						// exec 'scp -f rfile' remotely
						String command = "scp -f " + file.getPath();
						com.jcraft.jsch.Channel channel = session.openChannel("exec");
						((com.jcraft.jsch.ChannelExec) channel).setCommand(command);

						// Get I/O streams for remote scp
						OutputStream out = channel.getOutputStream();
						InputStream in = channel.getInputStream();
						channel.connect();

						// send '\0'
						buf[0] = 0;
						out.write(buf, 0, 1);
						out.flush();

						while (true)
						{
							int c = checkAck(in);
							if (c != 'C') break;
						}

						// read '0644 '
						in.read(buf, 0, 5);
						long filesize = 0L;

						while (true)
						{
							// error
							if (in.read(buf, 0, 1) < 0) throw new RuntimeException("Error data reading over SSH communication channel");
							if (buf[0] == ' ') break;

							filesize = filesize * 10L + (long) (buf[0] - '0');
						}

						String fileName = null;
						for (int i = 0; ; i++)
						{
							in.read(buf, i, 1);
							if (buf[i] == (byte) 0x0a)
							{
								fileName = new String(buf, 0, i);
								break;
							}
						}

						// send '\0'
						buf[0] = 0;
						out.write(buf, 0, 1);
						out.flush();

						// read a content of lfile
						file = new File(RuntimeManager.getWorkingDirectory(), fileName);
						fos = new FileOutputStream(file);
						int foo;

						while (true)
						{
							if (buf.length < filesize) foo = buf.length;
								else foo = (int) filesize;

							foo = in.read(buf, 0, foo);
							if (foo < 0) throw new RuntimeException("Error data reading over SSH communication channel");

							fos.write(buf, 0, foo);
							filesize -= foo;
							if (filesize == 0L) break;
						}

						fos.close();
						fos = null;

						// send '\0'
						buf[0] = 0;
						out.write(buf, 0, 1);
						out.flush();

                        session.disconnect();
					}
					catch(Exception	e)
					{
						throw new AREasyException("Error copying remote file to Work directory over SSH protocol: " + e.getMessage(), e);
					}
				}
				else if (protocol == 0)
				{
					String remoteDomain = remoteServerName;

					if (remoteUserName != null && remoteUserName.contains("\\"))
					{
						int domainSeparator = remoteUserName.indexOf("\\", 0);
						remoteDomain = remoteUserName.substring(0, domainSeparator);
						remoteUserName = remoteUserName.substring(domainSeparator + 1);
					}

					int bytesRead;
					InputStream in = null;
					OutputStream out = null;
					byte[] buffer = new byte[StreamUtility.BUFFER_SIZE];
					String remoteLocation = "smb://" + remoteServerName + "/" + file.getPath();
					jcifs.smb.NtlmPasswordAuthentication auth = new jcifs.smb.NtlmPasswordAuthentication(remoteDomain, remoteUserName, remoteUserPassword);

					try
					{
						jcifs.smb.SmbFile fileIn = new jcifs.smb.SmbFile(remoteLocation, auth);
						in = new jcifs.smb.SmbFileInputStream(fileIn);

						file = new File(RuntimeManager.getWorkingDirectory(), fileIn.getName());
						out = new FileOutputStream(file);

						while ((bytesRead = in.read(buffer)) != -1)
						{
							out.write(buffer, 0, bytesRead);
						}
					}
					catch (Exception e)
					{
						throw new AREasyException("Error copying remote file to Work directory over SMB protocol: " + e.getMessage(), e);
					}
					finally
					{
						if (in != null)
						{
							try
							{
								in.close();
							}
							catch (Exception ex)
							{ /* nothing to do */ }
						}

						if (out != null)
						{
							try
							{
								out.close();
							}
							catch (Exception ex)
							{ /* nothing to do */ }
						}
					}
				}
				else throw new AREasyException("Invalid protocol to access remote location");
			}
			else
			{
				try
				{
					File localFile = new File(RuntimeManager.getWorkingDirectory(), file.getName());
					StreamUtility.copyFile(file, localFile);

					file = localFile;
				}
				catch (IOException ioe)
				{
					throw new AREasyException("Error copying remote file to Work directory : " + ioe.getMessage(), ioe);
				}
			}
		}

		Configuration config = new BaseConfiguration();
		config.setKey("pageindex",pageIndex);
		config.setKey("startindex",startIndex);
		config.setKey("endindex",endIndex);
		config.setKey("parsertype","file");
		config.setKey("parserfile",getFile().getPath());

		try
		{
			//initialization
			parser = new ParserEngine(getAction().getServerConnection(), getAction().getManager().getConfiguration(), config);
			parser.init();
		}
		catch (AREasyException are)
		{
			throw are;
		}
		catch(Throwable th)
		{
			throw new AREasyException("Error initializing parser engine to read data : " + th.getMessage(), th);
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
		if (parser == null) initParser();

		//read data
		String data[] = parser.read();

		if (data != null)
		{
			map = new HashMap();

			for (int i = 0; i < list.size(); i++)
			{
				String colName = (String) list.get(i);

				if (colName != null)
				{
					int colIndex = ProcessorLevel0Reader.getIndexFromGenericColumn(colName);

					if (colIndex >= data.length) throw new AREasyException("Requested data mapping if out of bounds from parser engine array: " + colIndex + "/" + data.length);
						else map.put(colName, data[colIndex]);
				}
			}
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

		try
		{
			//initialize parser (if is not)
			if (parser == null) initParser();

			numberOfRows = 1 + parser.getEndIndex() - parser.getStartIndex();
		}
		catch (AREasyException are)
		{
			RuntimeLogger.info("Error getting number of records: " + are.getMessage());
			getAction().getLogger().debug("Exception", are);
		}

		return numberOfRows;
	}

	static int checkAck(InputStream in) throws IOException
	{
		int b = in.read();
		// b may be 0 for success,
		// 1 for error,
		// 2 for fatal error,
		// -1
		if (b == 0) return b;
		if (b == -1) return b;

		if (b == 1 || b == 2)
		{
			StringBuffer sb = new StringBuffer();
			int c;

			do
			{
				c = in.read();
				sb.append((char) c);
			}
			while (c != '\n');

			// error
			if (b == 1) throw new RuntimeException("Error checking SSH communication channel: " + sb.toString());

			// fatal error
			if (b == 2) throw new RuntimeException("Fatal error checking SSH communication channel: " + sb.toString());
		}

		return b;
	}
}
