package org.areasy.runtime.engine;

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

import org.areasy.runtime.RuntimeManager;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.common.data.DateFormatUtility;
import org.areasy.common.data.StringUtility;
import org.areasy.common.data.workers.parsers.Base64;
import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;
import org.areasy.common.support.configuration.Configuration;
import org.areasy.common.support.configuration.ConfigurationEntry;
import org.areasy.common.support.configuration.base.BaseConfiguration;
import org.areasy.common.support.configuration.providers.properties.stream.PropertiesConfiguration;
import org.areasy.common.support.configuration.providers.properties.stream.PropertiesEntry;

import java.io.*;
import java.util.*;


/**
 * Primitive class derived by server and client runtime libraries
 * implemented here socket reading and writing methods.
 *
 */
public class RuntimeBase
{
	/** Library logger */
	protected static Logger logger = LoggerFactory.getLog(RuntimeBase.class);

	private PrintWriter output = null;
	private BufferedReader input = null;
	private RuntimeManager manager = null;

	private static String flagPackageStart 				= "areasy/package:start";
	private static String flagPackageEnd 				= "areasy/package:end";
	private static String flagPackageData 				= "areasy/package:data";
	private static String flagPackageConfig 			= "areasy/package:config";
	private static String flagPackageStream 			= "areasy/package:stream";
	private static String flagPackageMessage 			= "areasy/package:message";
	private static String flagPackageFlush 				= "areasy/package:flush";

	public static String flagFlush 						= "flush";
	public static String flagSignature 					= "signature";

	public static String flagClientSignatureHostIds 	= "clientSignatureHostIds";
	public static String flagClientSignatureModules		= "clientSignatureModules";
	public static String flagClientSignatureHomePath 	= "clientSignatureHomePath";
	public static String flagClientSignatureLibsPath 	= "clientSignatureLibsPath";
	public static String flagClientSignatureWorkPath 	= "clientSignatureWorkPath";

	public static String flagRunnerSignatureHostIds 	= "runnerSignatureHostIds";
	public static String flagRunnerSignatureModules 	= "runnerSignatureModules";
	public static String flagRunnerSignatureHomePath 	= "runnerSignatureHomePath";
	public static String flagRunnerSignatureLibsPath 	= "runnerSignatureLibsPath";
	public static String flagRunnerSignatureWorkPath 	= "runnerSignatureWorkPath";

	/**
	 * Get runtime manager.
	 *
	 * @return runtime manager structure.
	 */
	public final RuntimeManager getManager()
	{
		return manager;
	}

	/**
	 * Set runtime manager structure for this server instance.
	 *
	 * @param manager new runtime manager instance
	 */
	public final void setManager(RuntimeManager manager)
	{
		this.manager = manager;
	}

	/**
	 * Send data (a configuration structure) to the server layer. This method defines also the communication
	 * protocol between server and client, establishing a simple grammar to enhance the cummunication between
	 * various client implementations.
	 *
	 * @param config configuration structure
	 * @throws AREasyException if any error will occur
	 */
	public final void sendClientRequestByClient(Configuration config) throws AREasyException
	{
		if(config != null && !config.isEmpty())
		{
			List inputFileKeys = null;
			List outputFileKeys = null;

			//in case of communication is client server transform inputfile and outputfile parameters
			if(isRemoteSignature(config) && getManager().getExecutionMode() > RuntimeManager.RUNTIME)
			{
				String serverWorkDir = config.getString(flagRunnerSignatureWorkPath, null);

				//get input file parameters.
				inputFileKeys = getInputFilesConfiguration(config);

				for(int i = 0; i < inputFileKeys.size(); i++)
				{
					String inputParam = (String) inputFileKeys.get(i);
					String inputFile = config.getString(inputParam, null);

					if(inputFile != null)
					{
						config.setKey("cli_" + inputParam, inputFile);
						config.setKey(inputParam, getWorkCanonicalFilePath(serverWorkDir, inputFile));
					}
				}

				//get output file parameters.
				outputFileKeys = getOutputFilesConfiguration(config);

				for(int i = 0; i < outputFileKeys.size(); i++)
				{
					String outputParam = (String) outputFileKeys.get(i);
					String outputFile = config.getString(outputParam, null);

					if(outputFile != null)
					{
						config.setKey("cli_" + outputParam, outputFile);
						config.setKey(outputParam, getWorkCanonicalFilePath(serverWorkDir, outputFile));
					}
				}
			}

			try
			{
				//start request
				output.write(flagPackageStart);
				output.write("\n");
				output.flush();

				//processing request command
				output.write(flagPackageConfig);
				output.write("\n");
				output.flush();

				//send command
				output.write(serializeConfiguration(config).toString());
				output.write("\n");
				output.flush();

				if(isRemoteSignature(config) && getManager().getExecutionMode() > RuntimeManager.RUNTIME && inputFileKeys != null)
				{
					for(int i = 0; i < inputFileKeys.size(); i++)
					{
						String inputParam = "cli_" + inputFileKeys.get(i);
						String inputFile = config.getString(inputParam, null);

						if(inputFile != null)
						{
							File fileIn = new File(inputFile);
							String stream = getInputStream(fileIn);

							if(StringUtility.isNotEmpty(stream))
							{
								output.write(flagPackageStream);
								output.write("\n");
								output.flush();

								output.write(stream);
								output.write("\n");
								output.flush();
							}
						}
					}
				}

				//end request
				output.write(flagPackageEnd);
				output.write("\n");
				output.flush();
			}
			catch (Exception e)
			{
				throw new AREasyException(e);
			}
		}
	}

	/**
	 * Send an answer to the client layer. This method defines also the communication protocol between server and client, establishing a simple grammar
	 * to enhance the communication between various client implementations.
	 *
	 * @param config read parameters translated into a <code>Configuration</code> structure
	 * @throws AREasyException if any error will occur
	 */
	public final void sendServerAnswerByServer(Configuration config) throws AREasyException
	{
		try
		{
			//start answer
			output.write(flagPackageStart);
			output.write("\n");
			output.flush();

			//processing configuration structure
			if(config != null && !config.isEmpty())
			{
				PropertiesConfiguration props = new PropertiesConfiguration();
				props.copy(config);

				StringWriter writer = new StringWriter();
				props.write(writer);

				output.write(flagPackageConfig);
				output.write("\n");
				output.flush();

				output.write(writer.toString());
				output.write("\n");
				output.flush();
			}

			//processing answer data
			List list = RuntimeLogger.getDataList();

			//add output in the configuration structure
			if(list != null && !list.isEmpty())
			{
				output.write(flagPackageData);
				output.write("\n");
				output.flush();

				if(RuntimeLogger.isCompact())
				{
					String data = StringUtility.join( list.toArray(new String[list.size()]) );

					output.write(data);
					output.write("\n");
					output.flush();
				}
				else
				{
					for(int i = 0; i < list.size(); i++)
					{
						String data = (String) list.get(i);

						output.write(data);
						output.write("\n");
						output.flush();
					}
				}
			}

			//processing answer details
			String text = RuntimeLogger.getMessages();

			if(StringUtility.isNotEmpty(text))
			{
				output.write(flagPackageMessage);
				output.write("\n");
				output.flush();

				output.write(text);
				output.write("\n");
				output.flush();
			}

			//processing output file(s)
			if(isRemoteSignature(config) && getManager().getExecutionMode() > RuntimeManager.RUNTIME)
			{
				//get output file parameters.
				List files = getOutputFilesConfiguration(config);

				for(int i = 0; i < files.size(); i++)
				{
					String outputParam = (String) files.get(i);
					String outputFile = config.getString(outputParam, null);

					if(outputFile != null)
					{
						File fileOut = new File(outputFile);
						String stream = getInputStream(fileOut);

						if(StringUtility.isNotEmpty(stream))
						{
							output.write(flagPackageStream);
							output.write("\n");
							output.flush();

							output.write(stream);
							output.write("\n");
							output.flush();
						}
					}
				}
			}

			//end request
			output.write(flagPackageEnd);
			output.write("\n");
			output.flush();
		}
		catch (Exception e)
		{
			throw new AREasyException(e);
		}
	}

	/**
	 * Send an answer to the client layer. This method defines also the communication protocol between server and client, establishing a simple grammar
	 * to enhance the communication between various client implementations.
	 *
	 * @throws AREasyException if any error will occur
	 */
	public final void sendPartialServerAnswerByServer() throws AREasyException
	{
		try
		{
			//start answer
			output.write(flagPackageStart);
			output.write("\n");
			output.write(flagPackageFlush);
			output.write("\n");
			output.flush();

			//processing answer data
			List list = RuntimeLogger.getDataList();

			//add output in the configuration structure
			if(list != null && !list.isEmpty())
			{
				output.write(flagPackageData);
				output.write("\n");
				output.flush();

				if(RuntimeLogger.isCompact())
				{
					String data = StringUtility.join( list.toArray(new String[list.size()]) );

					output.write(data);
					output.write("\n");
					output.flush();
				}
				else
				{
					for(int i = 0; i < list.size(); i++)
					{
						String data = (String) list.get(i);

						output.write(data);
						output.write("\n");
						output.flush();
					}
				}
			}

			//end request
			output.write(flagPackageEnd);
			output.write("\n");
			output.flush();
		}
		catch (Exception e)
		{
			throw new AREasyException(e);
		}
	}

	/**
	 * Read communication and transform obtained content into a configuration structure.
	 *
	 * @param config read parameters translated into a <code>Configuration</code> structure
	 * @throws AREasyException if any error will occur
	 */
	public final void readClientRequestByServer(Configuration config) throws AREasyException
	{
		int mode = 0;
		String line;

		List streams = new ArrayList();
		StringBuffer bufferConfig = null;
		StringBuffer bufferStream = null;
		StringBuffer bufferMessage = null;
		List bufferData = null;

		boolean closed = false;
		boolean reading = false;

		try
		{
			do
			{
				//read input line
				line = input.readLine();

				//get flags for workflow
				if(line != null && StringUtility.equals(line, flagPackageStart))
				{
					reading = true;
					continue;
				}
				else if(line != null && StringUtility.equals(line, flagPackageEnd))
				{
					closed = true;
					reading = false;

					if(mode == 3 && bufferStream != null && bufferStream.length() > 0) streams.add(bufferStream);
				}
				else if(line == null && !reading) closed = true;

				if(reading)
				{
					if(StringUtility.equals(StringUtility.trim(line), flagPackageData))
					{
						mode = 1;
						bufferData = new Vector();
					}
					else if(StringUtility.equals(StringUtility.trim(line), flagPackageConfig))
					{
						mode = 2;
						bufferConfig = new StringBuffer();
					}
					else if(StringUtility.equals(StringUtility.trim(line), flagPackageStream))
					{
						if(bufferStream != null) streams.add(bufferStream);
						
						mode = 3;
						bufferStream = new StringBuffer();
					}
					else if(StringUtility.equals(StringUtility.trim(line), flagPackageMessage))
					{
						mode = 4;
						bufferMessage = new StringBuffer();
					}
					else if(line != null)
					{
						if(mode == 1) bufferData.add(line);
							else if(mode == 2) bufferConfig.append(line).append("\n");
								else if(mode == 3) bufferStream.append(line).append("\n");
									else if(mode == 4) bufferMessage.append(line).append("\n");
					}
				}
			}
			while(!closed);

			//add last file.
			if(mode == 3 && bufferStream != null) streams.add(bufferStream);

			//generate data buffer
			if(bufferData != null) RuntimeLogger.add(bufferData);

			//generate message buffer
			if(bufferMessage != null) RuntimeLogger.setMessages(bufferMessage.toString());

			//generate configuration buffer
			if(bufferConfig != null)
			{
				Configuration data = deserializeConfiguration(bufferConfig);

				//read and transform data into a configuration structure and then merge it with output configuration structure.
				config.replace(data);
			}

			//generate transferred input files
			if(isRemoteSignature(config) && getManager().getExecutionMode() > RuntimeManager.RUNTIME && !streams.isEmpty())
			{
				//get input file parameters.
				List files = getInputFilesConfiguration(config);

				for(int i = 0; i < files.size(); i++)
				{
					String inputParam = (String) files.get(i);
					String inputFile = config.getString(inputParam, null);

					if(inputFile != null)
					{
						bufferStream = (StringBuffer) streams.get(i);

						if(StringUtility.equals(File.separator, "/") && StringUtility.contains(inputFile, "\\"))
						{
							inputFile = StringUtility.replace(inputFile, "\\", "/");
							config.setKey(inputParam, inputFile);
						}
						else if(StringUtility.equals(File.separator, "\\") && StringUtility.contains(inputFile, "/"))
						{
							inputFile = StringUtility.replace(inputFile, "/", "\\");
							config.setKey(inputParam, inputFile);
						}

						File fileOut = new File(inputFile);
						setOutputStream(fileOut, bufferStream.deleteCharAt(bufferStream.length() - 1).toString());
					}
				}
			}
		}
		catch (Exception e)
		{
			throw new AREasyException(e);
		}
	}

	/**
	 * Read communication and transform obtained content into an answer structure.
	 *
	 * @param config read parameters translated into a <code>Configuration</code> structure
	 * @throws org.areasy.runtime.engine.base.AREasyException if any error will occur
	 */
	public final void readServerAnswerByClient(Configuration config) throws AREasyException
	{
		int mode = 0;
		String line;

		List streams = new ArrayList();
		StringBuffer bufferConfig = null;
		StringBuffer bufferStream = null;
		StringBuffer bufferMessage = null;
		List bufferData = null;

		boolean closed = false;
		boolean reading = false;

		//reset flush flag if it exists
		if(config != null && config.containsKey(flagFlush)) config.removeKey(flagFlush);

		try
		{
			do
			{
				//read input line
				line = input.readLine();

				//get flags for workflow
				if(line != null && StringUtility.equals(StringUtility.trim(line), flagPackageStart))
				{
					reading = true;
					continue;
				}
				else if(line != null && StringUtility.equals(StringUtility.trim(line), flagPackageEnd))
				{
					closed = true;
					reading = false;
				}
				if(line != null && StringUtility.equals(StringUtility.trim(line), flagPackageFlush))
				{
					if(config != null) config.setKey(flagFlush, new Boolean(true));
					else
					{
						config = new BaseConfiguration();
						config.setKey(flagFlush, new Boolean(true));
					}
				}
				else if(line == null && !reading) closed = true;

				if(reading)
				{
					if(StringUtility.equals(StringUtility.trim(line), flagPackageData))
					{
						mode = 1;
						bufferData = new Vector();
					}
					else if(StringUtility.equals(StringUtility.trim(line), flagPackageConfig))
					{
						mode = 2;
						bufferConfig = new StringBuffer();
					}
					else if(StringUtility.equals(StringUtility.trim(line), flagPackageStream))
					{
						if(bufferStream != null) streams.add(bufferStream);

						mode = 3;
						bufferStream = new StringBuffer();
					}
					else if(StringUtility.equals(StringUtility.trim(line), flagPackageMessage))
					{
						mode = 4;
						bufferMessage = new StringBuffer();
					}
					else if(line != null)
					{
						if(mode == 1) bufferData.add(line);
							else if(mode == 2) bufferConfig.append(line).append("\n");
								else if(mode == 3) bufferStream.append(line).append("\n");
									else if(mode == 4) bufferMessage.append(line).append("\n");
					}
				}
			}
			while(!closed);

			//add last file.
			if(mode == 3 && bufferStream != null) streams.add(bufferStream);

			//generate data buffer
			if(bufferData != null) RuntimeLogger.add(bufferData);

			//generate message buffer
			if(bufferMessage != null) RuntimeLogger.setMessages(bufferMessage.toString());			

			//generate configuration buffer
			if(bufferConfig != null)
			{
				PropertiesConfiguration data = new PropertiesConfiguration();
				LineNumberReader reader = new LineNumberReader(new StringReader(bufferConfig.toString().trim()));

				//read and transform data into a configuration structure and then merge it with output configuration structure.
				data.read(reader);
				config.replace(data);
			}

			//generate transferred input files
			if(isRemoteSignature(config) && getManager().getExecutionMode() > RuntimeManager.RUNTIME && !streams.isEmpty())
			{
				//get output file parameters.
				List files = getOutputFilesConfiguration(config);

				for(int i = 0; i < files.size(); i++)
				{
					String outputParam = (String) files.get(i);
					String outputFile = config.getString("cli_" + outputParam, null);

					if(outputFile != null)
					{
						bufferStream = (StringBuffer) files.get(i);

						if(StringUtility.equals(File.separator, "/") && StringUtility.contains(outputFile, "/"))
						{
							outputFile = StringUtility.replace(outputFile, "\\", "/");
							config.setKey(outputParam, outputFile);
						}
						else if(StringUtility.equals(File.separator, "\\") && outputFile.indexOf("/", 0) > 0)
						{
							outputFile = StringUtility.replace(outputFile, "/", "\\");
							config.setKey(outputParam, outputFile);
						}

						File fileOut = new File(outputFile);
						setOutputStream(fileOut, bufferStream.deleteCharAt(bufferStream.length() - 1).toString());
					}
				}
			}
		}
		catch (Exception e)
		{
			throw new AREasyException(e);
		}
	}

	/**
	 * Write configuration entries to a <code>StringBuffer</code> in order to obtain a string representation of
	 * the specified configuration structure.
	 *
	 * @param config input configuration
	 * @return <code>StringBuffer</code> structure containing all configuration entries presenting basic data types. using this structure
	 * you can obtain a string representation of the initial configuration structure.
	 */
	protected StringBuffer serializeConfiguration(Configuration config)
	{
		if(config == null) return null;

		Iterator iterator = config.getKeys();
		StringBuffer buffer = new StringBuffer();

		while(iterator != null && iterator.hasNext())
		{
			String key = (String) iterator.next();
			ConfigurationEntry entry = config.getConfigurationEntry(key);

			if(entry.isComment())
			{
				buffer.append( entry.getComment() ).append('\n');
			}
			else if(entry.isData())
			{
				for(int j = 0; j < entry.getValues().size(); j++)
				{
					Object valueObject = entry.getValues().get(j);
					buffer.append( entry.getKey() + " = " + getManager().getStringFromInstance(valueObject)).append('\n');
				}
			}
		}

		return buffer;
	}

	/**
	 * Read and translate configuration entries from a text. This method is able to concatenate lines ending with "\".
	 *
	 * @param buffer <code>BufferedReader</code> structure
	 * @return <code>Configuration</code> structure filled in with all found and decoded keys and values. The values are transformed into
	 * proper data type in case of the values contain functions.
	 */
	public Configuration deserializeConfiguration(StringBuffer buffer)
	{
		StringBuffer lineBuffer = new StringBuffer();
		PropertiesConfiguration data = new PropertiesConfiguration();
		LineNumberReader reader = new LineNumberReader(new StringReader(buffer.toString().trim()));

		try
		{
			String line;

			do
			{
				//read next line.
				line = reader.readLine();

				if(line != null)
				{
					//compose line to be parsed.
					if (line.endsWith("\\"))
					{
						line = line.substring(0, line.length() - 1);

						lineBuffer.append(line);
						lineBuffer.append('\n');
					}
					else
					{
						//create data entry
						lineBuffer.append(line);
						String dataLine = lineBuffer.toString();
						PropertiesEntry entry = new PropertiesEntry();

						if(dataLine.startsWith(ConfigurationEntry.PROPERTY_COMMENT))
						{
							entry.setComment(dataLine);
						}
						else
						{
							int index = dataLine.indexOf('=', 0);

							if( index > 0 )
							{
								String key = dataLine.substring(0, index).trim();
								String stringValue = dataLine.substring(index + 1).trim();
								Object objectValue = getManager().getInstanceFromString(stringValue);

								entry.setData(key, objectValue);
							}
							else entry.setComment(line);
						}

						//cleaning line buffer.
						lineBuffer = new StringBuffer();

						if(!entry.isEmpty())
						{
							if( entry.isData() && data.containsKey(entry.getKey()) ) data.getConfigurationEntry(entry.getKey()).addValue(entry.getValue());
								else data.setConfigurationEntry(entry);
						}
					}
				}
			}
			while(line != null);
		}
		catch(IOException e)
		{
			logger.error("Error deserialize configuration structure: " + e.getMessage());
			logger.debug("Exception", e);
		}
		finally
		{
			try
			{
				//close reader.
				reader.close();
			}
			catch(IOException ioe) { /* not important */ }
		}

		return data;
	}

	public PrintWriter getOut()
	{
		return output;
	}

	public void setOut(PrintWriter out)
	{
		this.output = out;
	}

	public BufferedReader getIn()
	{
		return input;
	}

	public void setIn(BufferedReader in)
	{
		this.input = in;
	}

	/**
	 * Get answer stream.
	 *
	 * @param fileIn input file
	 * @return stream data content
	 */
	private String getInputStream(File fileIn)
	{
		if(fileIn != null && fileIn.exists())
		{
			String content = null;
			InputStream stream = null;

			try
			{
				byte[] buffer = new byte[1024];
				stream = new FileInputStream(fileIn);
				ByteArrayOutputStream output = new ByteArrayOutputStream();

				while(true)
				{
					int read = stream.read(buffer);
					if(read <= 0) break;

					output.write(buffer, 0, read);
				}

				char[] encoded = Base64.encode(output.toByteArray());
				content = new String(encoded);
			}
			catch (Exception e)
			{
				LoggerFactory.getLog(RuntimeBase.class).error("Error reading input stream: " + e.getMessage());
				LoggerFactory.getLog(RuntimeBase.class).debug("Exception", e);

				content = null;
			}
			finally
			{
				if(stream != null) try { stream.close(); } catch(Exception e) { /* nothing to do here */}
			}

			return content;
		}
		else return null;
	}

	/**
	 * Write a text message which is comming from another answer.
	 *
	 * @param fileOut data container
	 * @param content data content
	 */
	private void setOutputStream(File fileOut, String content)
	{
		if(fileOut != null)
		{
			OutputStream stream = null;

			try
			{
				ByteArrayInputStream input = new ByteArrayInputStream(Base64.decode(content.toCharArray()));
				stream = new FileOutputStream(fileOut.getAbsolutePath());

				int read;
				byte[] buffer = new byte[1024];

				while ((read = input.read(buffer)) != -1)
				{
					stream.write(buffer, 0, read);
				}
			}
			catch (Exception e)
			{
				LoggerFactory.getLog(RuntimeBase.class).error("Error writing output stream: " + e.getMessage());
				LoggerFactory.getLog(RuntimeBase.class).debug("Exception", e);
			}
			finally
			{
				if(stream != null) try { stream.close(); } catch(Exception e) { /* nothing to do here */}
			}
		}
	}

	protected boolean isRemoteSignature(Configuration config)
	{
		boolean remote = true;

		List client = config.getList(flagClientSignatureHostIds, null);
		List server = config.getList(flagRunnerSignatureHostIds, null);

		//if one of the lists couldn't be identified means that networking layer couldbn't be identified.
		if(server == null || client == null) remote = false;
		else
		{
			for(int i = 0 ; remote && i < client.size(); i++)
			{
				String host = (String) client.get(i);
				remote = !server.contains(host);
			}
		}

		return remote;
	}

	protected String getWorkCanonicalFilePath(String workDir,  String filename)
	{
		if(filename == null) return null;

		String fname = null;
		int index1 = filename.lastIndexOf(File.separator);
		int index2 = filename.lastIndexOf("\\");
		int index3 = filename.lastIndexOf("/");
		int index = Math.max(Math.max(index1, index2), index3);

		if (index > 0) fname = filename.substring(index + 1);
			else fname = filename;

		fname = DateFormatUtility.DB_TRIM_DATETIME_FORMAT.format(new Date()) + "@" + fname;

		return new File(workDir, fname).getPath();
	}

	protected List getInputFilesConfiguration(Configuration config)
	{
		List files = null;

		if(config == null) return files;

		Iterator iterator = config.getKeys();

		if(iterator != null)
		{
			files = new Vector();

			while(iterator.hasNext())
			{
				String key = (String) iterator.next();

				if(key != null && !files.contains(key) && (key.equals("file") || key.equals("parserfile") || key.contains("inputfile")) ) files.add(key);
			}
		}

		return files;
	}

	protected List getOutputFilesConfiguration(Configuration config)
	{
		List files = null;

		if(config == null) return files;

		Iterator iterator = config.getKeys();

		if(iterator != null)
		{
			files = new Vector();

			while(iterator.hasNext())
			{
				String key = (String) iterator.next();

				if(key != null && !files.contains(key) && key.contains("outputfile")) files.add(key);
			}
		}

		return files;
	}
}
