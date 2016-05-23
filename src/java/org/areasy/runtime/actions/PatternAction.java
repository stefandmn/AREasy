package org.areasy.runtime.actions;

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

import com.bmc.arsys.api.*;
import org.areasy.runtime.actions.arserver.data.tools.flow.sources.AbstractSource;
import org.areasy.runtime.RuntimeAction;
import org.areasy.runtime.actions.arserver.data.BaseDataAction;
import org.areasy.runtime.engine.RuntimeLogger;
import org.areasy.runtime.engine.base.ARDictionary;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.runtime.engine.base.ServerConnection;
import org.areasy.runtime.engine.services.status.BaseStatus;
import org.areasy.runtime.engine.structures.CoreItem;
import org.areasy.runtime.engine.structures.MultiPartItem;
import org.areasy.runtime.engine.workflows.ProcessorLevel1Context;
import org.areasy.runtime.utilities.StreamUtility;
import org.areasy.common.data.StringEscapeUtility;
import org.areasy.common.data.StringUtility;
import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;
import org.areasy.common.support.configuration.Configuration;
import org.areasy.common.support.configuration.base.BaseConfigurationEntry;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.*;

/**
 * This action is the framework for Advanced Automation library. Any class that wants to be included in AAR framework
 * has to derive this class. This action is able to start an AAR workflow that record the execution startup, execute
 * proper sub-action and record the entire execution journal. All logging info are manage in some specific Remedy forms.
 */
public abstract class PatternAction extends BaseDataAction implements RuntimeAction, ARDictionary
{
	/** Library logger */
	protected static Logger logger = LoggerFactory.getLog(PatternAction.class);

	/** Sources repository */
	private static Map sources = new Hashtable();

	/** AAR action logger */
	private CoreItem jobLogger = null;

	/** AAR action registration */
	private CoreItem jobEntry = null;

	/** Dedicated flag that say what kind of running rule is applied to the current job */
	private boolean runsOnSchedule = false;

	/** buffer for temporary resources */
	private List tempResources = new ArrayList();

	/** private dedicated flag for logger to log or notify only one time (even if the ask method execution for many times */
	private boolean loggerClosed = false;

	static
	{
		sources.put("No Data", "org.areasy.runtime.actions.arserver.data.tools.sources.NoDataSource");
		sources.put("Local File", "org.areasy.runtime.actions.arserver.data.tools.ServerFileSource");
		sources.put("Remote File", "org.areasy.runtime.actions.arserver.data.tools.ServerFileSource");
		sources.put("Remedy Form", "org.areasy.runtime.actions.arserver.data.tools.RemedySource");
		sources.put("MySQL Database", "org.areasy.runtime.actions.arserver.data.tools.databases.MySQLSource");
		sources.put("MSSQL Database", "org.areasy.runtime.actions.arserver.data.tools.databases.MSSQLSource");
		sources.put("Oracle Database", "org.areasy.runtime.actions.arserver.data.tools.databases.OracleSource");
		sources.put("Sybase Database", "org.areasy.runtime.actions.arserver.data.tools.databases.SybaseSource");
		sources.put("Derby Database", "org.areasy.runtime.actions.arserver.data.tools.databases.DerbySource");
		sources.put("PostgreSQL Database", "org.areasy.runtime.actions.arserver.data.tools.databases.PostgreSQLSource");
		sources.put("LDAP Entities", "org.areasy.runtime.actions.arserver.data.tools.LDAPSource"); //@deprecated
		sources.put("LDAP Server", "org.areasy.runtime.actions.arserver.data.tools.LDAPSource");
	}

	/**
	 * Run secondary initialization (from the final implementation class)
	 *
	 * @throws AREasyException if any error will occur.
	 */
	public void open() throws AREasyException
	{
		String instanceId = getConfiguration().getString("instanceid", null);

		//set job scheduled status
		if(getConfiguration().getBoolean("scheduled", false)) setRunsOnSchedule();

		//load job details from
		if(instanceId != null)
		{
			jobEntry = getJobEntryInstance(getServerConnection(), instanceId);

			//if job exists will be pu in running
			if(jobEntry.exists() && (Integer)jobEntry.getAttributeValue(7) != 1)
			{
				jobEntry.setAttribute(536871171, new Timestamp(new Date()));
				jobEntry.setAttribute(536871166, new Integer(0));
				jobEntry.setAttribute(536871165, new Integer(0));
				jobEntry.setAttribute(7, new Integer(1));
				jobEntry.update(getServerConnection());
			}
		}

		//save logger session
		if(!getConfiguration().getBoolean("notracks", false) && !getConfiguration().getBoolean("help", false))
		{
			// store event in the Remedy form
			jobLogger = new CoreItem();
			jobLogger.setFormName(ARDictionary.FORM_RUNTIME_LOGGER);

			jobLogger.setAttribute(7, new Integer(0));
			jobLogger.setAttribute(8, getCode());
			jobLogger.setAttribute(179, instanceId);
			jobLogger.setAttribute(536870924, getManager().getServerName());
			jobLogger.setAttribute(536870925, RuntimeLogger.getChannelName());
			jobLogger.setAttribute(536870914, getInputCommandLine());
			jobLogger.setAttribute(536870926, isRunsOnSchedule() ? new Integer(1) : new Integer(0));
			jobLogger.setAttribute(536870920, getServerConnection().getUserName());

			jobLogger.create(getServerConnection());

			//associating logger session with job instance
			if(jobLogger.exists() && jobEntry != null && jobEntry.exists())
			{
				jobEntry.setAttribute(536871138, jobLogger.getEntryId());
				jobEntry.update(getServerConnection());
			}
		}
	}

	/**
	 * Execute an action for a specific <code>CoreItem</code>. This item must be identified previously and then the method
	 * could be called. This method will used by standard actions which implement an workflow using these type of action
	 * which permit single change or update.
	 *
	 * @param item <code>CoreItem</code> structure, which should be instantiated.
	 * @throws org.areasy.runtime.engine.base.AREasyException if any error will occur.
	 */
	public void run(CoreItem item) throws AREasyException
	{
		//set job entry
		jobEntry = item;

		//run action
		run();
	}

	/**
	 * Run secondary disposer (from the final implementation class)
	 */
	public void close() throws AREasyException
	{
		String cascadeWorkflow = null;

		//close job entry
		if(jobEntry != null && jobEntry.exists())
		{
			jobEntry.setAttribute(536871166, new Integer(getRecordsCounter() - getErrorsCounter()));
			jobEntry.setAttribute(536871165, new Integer(getErrorsCounter()));
			jobEntry.setAttribute(7, new Integer(0));
			jobEntry.setAttribute(536871135, new Timestamp(new Date()));
			jobEntry.update(getServerConnection());

			//call cascaded workflow if exists
			if(jobEntry.getStringAttributeValue(536871167) != null && (!StringUtility.equals(getCode(), "data.processflow") ||
			(StringUtility.equals(getCode(), "data.processflow") && StringUtility.equals(getConfiguration().getString("event", null), "runworkflow"))))
			{
				cascadeWorkflow = jobEntry.getStringAttributeValue(536871167);
				RuntimeLogger.info("Call related workflow: " + cascadeWorkflow);
			}
		}

		//close current logger
		setLoggerClosed();

		//run cascaded workflow
		if(cascadeWorkflow != null)
		{
			//get related job definition
			CoreItem relatedJob = getJobEntryInstance(getServerConnection(), cascadeWorkflow);
			logger.info("Calling cascaded workflow: " + relatedJob);

			if(relatedJob != null && relatedJob.exists())
			{
				String actionName = relatedJob.getStringAttributeValue(536871088);
				String commandLine = relatedJob.getStringAttributeValue(2431);

				String relatedCommand = "-action " + actionName;
				if(StringUtility.equalsIgnoreCase(actionName, "data.processflow")) relatedCommand += " -event runworkflow";
				relatedCommand += " -user " + getServerConnection().getUserName();
				relatedCommand += " -instanceid " + cascadeWorkflow;
				relatedCommand += " " + commandLine;

				Configuration config = getManager().getConfiguration(relatedCommand);
				PatternAction action = (PatternAction) getManager().getRuntimeAction(actionName);
				getManager().process(action, config);
			}
		}
	}

	private void setLoggerClosed() throws AREasyException
	{
		if(loggerClosed) return;

		//save output in log
		if(jobLogger != null && jobLogger.exists() && !getConfiguration().getBoolean("notracks", false) )
		{
			long logSize = 0;
			String logFileName = RuntimeLogger.getChannelFileName();

			File file0 = new File(logFileName);
			logSize = file0.length();

			BaseStatus status = super.getCurrentStatus();
			String message = status != null ? status.getStatusMessage() : "";

			jobLogger.setAttribute(7, new Integer(1));
			jobLogger.setAttribute(536870975, StringUtility.join(RuntimeLogger.getData(), "\n"));
			jobLogger.setAttribute(536870915, "Job accomplished! " + message);

			if(logSize < StreamUtility.ONE_MB)
			{
				jobLogger.setAttribute(536870913, RuntimeLogger.getMessages());
				jobLogger.update(getServerConnection());
			}
			else
			{
				jobLogger.update(getServerConnection());

				String fileName = StreamUtility.getShortFileName(file0);
				addLoggerOutput(fileName, file0);

				int I = 1;
				boolean exists;

				do
				{
					File fileI = new File(logFileName + "." + I);
					exists = fileI.exists();

					if(exists)
					{
						fileName = StreamUtility.getShortFileName(fileI);
						addLoggerOutput(fileName, fileI);

						I++;
						if(!fileI.delete()) fileI.deleteOnExit();
					}
				}
				while(exists);
			}
		}

		loggerClosed = true;
	}

	/**
	 * Get status instance. In order to deliver a status of the current stage (for actions which the execution will take much more time)
	 * this method must be implemented.
	 *
	 * @return <code>BaseStatus</code> structure and in the current implementation is returning a null value.
	 */
	public EntryDataStatus getCurrentStatus()
	{
		EntryDataStatus status = getCurrentBaseStatus();

		if(jobLogger != null && jobLogger.exists() && !getConfiguration().getBoolean("notracks", false))
		{
			//set caller signature (nice identifier of action / on only the action code)
			if(jobEntry != null && jobEntry.exists() && status.getCallerSignature() == null)
			{
				status.setCallerSignature(jobEntry.getStringAttributeValue(2430));
			}

			try
			{
				jobLogger.setAttribute(536870915, status.getStatusMessage());
				jobLogger.update(getServerConnection());
			}
			catch(AREasyException are)
			{
				logger.error("Error updating message in runtime logger: " + are.getMessage());
			}
		}

		return status;
	}

	protected EntryDataStatus getCurrentBaseStatus()
	{
		return (EntryDataStatus) super.getCurrentStatus();
	}

	/**
	 * Get command line from an input configuration.
	 *
	 * @return command line string value
	 */
	protected String getInputCommandLine()
	{
		String command = null;
		Iterator iterator = getConfiguration().getKeys();

		while(iterator != null && iterator.hasNext())
		{
			String key = (String) iterator.next();
			String values[] = getConfiguration().getStringArray(key, null);

			if(key != null && !key.startsWith("clientSignature") && !key.startsWith("runnerSignature"))
			{
				String value = null;

				for(int a = 0; values != null && a < values.length; a++)
				{
					String partval = values[a];

					if(partval != null && (partval.matches("^.*\\p{Punct}.*$") || partval.matches("^.*\\s.*$")))
					{
						if(partval.indexOf('"') < 0) partval = "\"" + partval + "\"";
							else if(partval.indexOf('"') >= 0 && partval.indexOf('\'') < 0) partval = "'" + partval + "'";
								else if(partval.indexOf('"') >= 0 && partval.indexOf('\'') >= 0) partval = "\"" + StringEscapeUtility.escapeJava(partval) + "\"";
					}
					else if(partval == null)
					{
						value = "";
					}

					if(value == null) value = partval;
						else value += " " + partval;
				}

				if(command == null) command = "-" + key + " " + value;
					else command += " -" + key + " " + value;
			}
		}

		return command;
	}

	/**
	 * Get data source instance.
	 *
	 * @return a final data source instance having signature <code>AbstractSource</code> structure
	 * @throws org.areasy.runtime.engine.base.AREasyException if any error occurs
	 */
	public final AbstractSource getSource() throws AREasyException
	{
		AbstractSource source;

		String instanceId = getConfiguration().getString("instanceid", null);
		if(instanceId == null) return null;

		if(getConfiguration().getBoolean("nosource", false)) return null;

		CoreItem item = new CoreItem();
		item.setFormName(ARDictionary.FORM_DATA_SOURCE);
		item.setAttribute(179, instanceId);
		item.read(getServerConnection());

		if(item.exists())
		{
			String sourceName = item.getStringAttributeValue(536870981);

			if(StringUtility.isNotEmpty(sourceName))
			{
				String sourceClassName = (String) sources.get(sourceName);
				if(sourceClassName == null) throw new AREasyException("Data source instance '" + sourceName  + "' is not registered");

				try
				{
					Class sourceClass = Class.forName(sourceClassName);
					Constructor contructor = sourceClass.getConstructor(null);

					source = (AbstractSource) contructor.newInstance(null);
					source.setSourceItem(item);
					source.setAction(this);
				}
				catch(Throwable th)
				{
					throw new AREasyException("Data source ('" + sourceName + "') initialization error: " + th.getMessage(), th);
				}
			}
			else throw new AREasyException("Found data source is null");

		}
		else throw new AREasyException("Data source could not be found: " + item);

		return source;
	}

	/**
	 * Get mapping entries stored in AAR forms
	 *
	 * @return a list of <code>CoreItem</code> entries.
	 * @throws AREasyException in case of any error will occur
	 */
	public List getMappingEntries() throws AREasyException
	{
		CoreItem search = new CoreItem();
		search.setFormName(ARDictionary.FORM_DATA_MAPPING);
		search.setAttribute(179, getJobEntry().getStringAttributeValue(179));
		search.setAttribute(7, new Integer(0));

		return search.search(getServerConnection());
	}

	/**
	 * Check if the action that runs on schedule
	 * @return true is action runs on schedule
	 */
	public final boolean isRunsOnSchedule()
	{
		return runsOnSchedule;
	}

	/**
	 * Set action flag to say that the action run on schedule
	 */
	public final void setRunsOnSchedule()
	{
		this.runsOnSchedule = true;
	}

	/**
	 * Set action flag to say that the action run on demand
	 */
	public final void setRunsOnDemand()
	{
		this.runsOnSchedule = false;
	}

	/**
	 * Get job entry structure, managed in AR System form
	 * @return a <code>CoreItem</code> structure, containing all details about designed runtime job.
	 */
	public final CoreItem getJobEntry()
	{
		return this.jobEntry;
	}

	public Logger getLogger()
	{
		return logger;
	}

	/**
	 * Get Velocity transformation script.
	 *
	 * @return a string that contains a Velocity script for data transformation
	 */
	public String getVelocityTransformationScript()
	{
		if(jobEntry != null && jobEntry.exists())
		{
			String script = jobEntry.getStringAttributeValue(536871086);
			if(script == null && getConfiguration().containsKey("transformationfile")) script = StreamUtility.readTextFile("UTF-8", getConfiguration().getString("transformationfile"));

			return script;
		}
		else return null;
	}

	/**
	 * Get first attached file to the current job. This method take the file from the job
	 * definition and download it on the AREasy file system (in "Work" folder)
	 *
	 * @return <code>File</code> instance or null
	 */
	public File getFirstRelatedFile()
	{
		if(jobEntry != null && jobEntry.exists())
		{
			return ProcessorLevel1Context.getDataFile(getServerConnection(), jobEntry.getFormName(), "536871083", "179", jobEntry.getStringAttributeValue(179));
		}
		else return null;
	}

	/**
	 * Get first attached file to the current job. This method take the file from the job
	 * definition and download it on the AREasy file system (in "Work" folder)
	 *
	 * @return <code>File</code> instance or null
	 */
	public File getSecondRelatedFile()
	{
		if(jobEntry != null && jobEntry.exists())
		{
			return ProcessorLevel1Context.getDataFile(getServerConnection(), jobEntry.getFormName(), "536871084", "179", jobEntry.getStringAttributeValue(179));
		}
		else return null;
	}

	/**
	 * Get first attached file to the current job. This method take the file from the job
	 * definition and download it on the AREasy file system (in "Work" folder)
	 *
	 * @return <code>File</code> instance or null
	 */
	public File getThirdRelatedFile()
	{
		if(jobEntry != null && jobEntry.exists())
		{
			return ProcessorLevel1Context.getDataFile(getServerConnection(), jobEntry.getFormName(), "536871085", "179", jobEntry.getStringAttributeValue(179));
		}
		else return null;
	}

	public void setOptions(Configuration config) throws AREasyException
	{
		if(getJobEntry() == null) return;

		if(getJobEntry().getAttributeValue(536870977) != null)
		{
			config.setKey("loglevel", "debug");
		}

		if(getJobEntry().getAttributeValue(536870946) != null)
		{
			config.setKey("force", "true");
		}

		if(getJobEntry().getAttributeValue(536870944) != null)
		{
			config.setKey("simulation", "true");
		}

		if(getJobEntry().getAttributeValue(536870914) != null)
		{
			config.setKey("ignorenullvalues", "true");
		}
		else
		{
			config.setKey("ignorenullvalues", "false");
		}

		if(getJobEntry().getAttributeValue(536870913) != null)
		{
			config.setKey("ignoreunchangedvalues", "true");
		}
		else
		{
			config.setKey("ignoreunchangedvalues", "false");
		}

		if(getJobEntry().getAttributeValue(536871148) != null)
		{
			config.setKey("limit", getJobEntry().getAttributeValue(536871148));
		}

		if(getJobEntry().getStringAttributeValue(536871136) != null)
		{
			config.setKey("entity", getJobEntry().getStringAttributeValue(536871136));
		}

		if(getJobEntry().getAttributeValue(536871126) != null)
		{
			int mergeId = (Integer) getJobEntry().getAttributeValue(536871126);
			String mergeString = mergeId == 0 ? "duperror" : mergeId == 1 ? "dupnewid" : mergeId == 2 ? "dupoverwrite" : mergeId == 3 ? "dupupdate" : null;

			Object mergeOptNoRequired = getJobEntry().getAttributeValue(536871127);
			Object mergeOptNoPattern = getJobEntry().getAttributeValue(536871128);
			Object mergeOptNoWorkflow = getJobEntry().getAttributeValue(536871129);

			config.setKey("mergetype", mergeString);

			List values = new Vector();
			if(mergeOptNoRequired != null) values.add("norequired");
			if(mergeOptNoPattern != null) values.add("nopattern");
			if(mergeOptNoWorkflow != null) values.add("noworkflow");

			if(!values.isEmpty()) config.setConfigurationEntry(new BaseConfigurationEntry("mergeoptions", values));
		}

		//get attached files to the job and use it when <code>@fileX</code> code is found
		File file1 = getFirstRelatedFile();
		File file2 = getSecondRelatedFile();
		File file3 = getThirdRelatedFile();

		if(file1 != null) config.setKey("file1", file1);
		if(file2 != null) config.setKey("file2", file2);
		if(file3 != null) config.setKey("file3", file3);
	}

	public String getTargetEntityName()
	{
		if(jobEntry != null && jobEntry.exists())
		{
			return jobEntry.getStringAttributeValue(536871141);
		}
		else return null;
	}

	public Integer getTargetEntityType()
	{
		if(jobEntry != null && jobEntry.exists())
		{
			Object type = jobEntry.getAttributeValue(536871142);

			if(type != null) return (Integer)type;
				else return null;
		}
		else return null;
	}

	public String getTargetEntityFormName()
	{
		Integer type = getTargetEntityType();
		String name = getTargetEntityName();

		if(type != null && name != null)
		{
			try
			{
				if(type == 1) name = ProcessorLevel1Context.getSharedFormName(getServerConnection(), name);
					else if(type >= 2) name = null;
			}
			catch(AREasyException are)
			{
				logger.error("Error reading target form name for '" + type + "' target type and '" + name + "' key: " + are.getMessage());
				logger.debug("Exception", are);

				RuntimeLogger.error("Error reading target form name for '" + type + "' target type and '" + name + "' key: " + are.getMessage());
				name = null;
			}

			return name;
		}
		else return null;
	}

	/**
	 * Create a new <Code>CoreItem</code> instance in order to be used in Velocity content
	 * @return a new CoreItem structure
	 */
	public CoreItem getCoreItemInstance()
	{
		return new CoreItem();
	}

	/**
	 * Create a new <Code>CoreItem</code> instance in order to be used in Velocity content
	 * @return a new CoreItem structure
	 */
	public CoreItem getCoreItemInstance(String formName)
	{
		return new CoreItem(formName);
	}

	public void addLoggerOutput(String name) throws AREasyException
	{
		addLoggerOutput(name, null, null);
	}

	public void addLoggerOutput(String name, String details) throws AREasyException
	{
		addLoggerOutput(name, details, null);
	}

	public void addLoggerOutput(String name, File file) throws AREasyException
	{
		addLoggerOutput(name, null, file);
	}

	public void addLoggerOutput(String name, String details, File file) throws AREasyException
	{
		if(name == null) throw new AREasyException("'Name' output logger is null");

		if(jobLogger != null && jobLogger.exists())
		{
			CoreItem item = new CoreItem();
			item.setFormName(ARDictionary.FORM_RUNTIME_OUTPUT);

			item.setAttribute(179, jobLogger.getStringAttributeValue(179));
			item.setAttribute(536870919, jobLogger.getEntryId());

			item.setAttribute(8, name);
			item.setAttribute(536870917, details);
			item.setAttribute(536870914, file);

			item.create(getServerConnection());
		}
		else throw new AREasyException("Logger instance doesn't exist");
	}

	public List getLoggerOutput() throws AREasyException
	{
		if(jobLogger != null && jobLogger.exists())
		{
			CoreItem item = new CoreItem();
			item.setFormName(ARDictionary.FORM_RUNTIME_OUTPUT);

			item.setAttribute(179, jobLogger.getStringAttributeValue(179));
			item.setAttribute(536870919, jobLogger.getEntryId());

			return item.search(getServerConnection());
		}
		else return null;
	}

	public void cleanup(CoreItem target)
	{
		if(target != null && tempResources.isEmpty())
		{
			try
			{
				List<Field> fields = getServerConnection().getContext().getListFieldObjects(target.getFormName(), Constants.AR_FIELD_TYPE_ATTACH);

				for(int i = 0; i < fields.size(); i++)
				{
					Field field = fields.get(i);
					tempResources.add( String.valueOf(field.getFieldID()) );
				}

				//put parts attachment files
				if(target instanceof MultiPartItem)
				{
					List partTypes = new Vector();
					Iterator iterator = ((MultiPartItem)target).getPartCodes();

					while(iterator != null && iterator.hasNext())
					{
						String partCode = (String) iterator.next();
						String partCodePrefix = null;

						if(partCode != null && partCode.contains("-")) partCodePrefix =  partCode.substring(0, partCode.lastIndexOf('-'));
							else partCodePrefix = partCode;

						if(partCodePrefix != null && !partTypes.contains(partCodePrefix))
						{
							CoreItem part = ((MultiPartItem)target).getPartInstance(partCode);
							if(part == null) continue;

							partTypes.add(partCodePrefix);

							if(fields != null && !fields.isEmpty()) fields.clear();
							fields = getServerConnection().getContext().getListFieldObjects(part.getFormName(), Constants.AR_FIELD_TYPE_ATTACH);

							for(int i = 0; i < fields.size(); i++)
							{
								Field field = fields.get(i);

								if(field.getFieldType() == Constants.AR_FIELD_TYPE_ATTACH)
								{
									tempResources.add( partCodePrefix + "@" + String.valueOf(field.getFieldID()) );
								}
							}
						}
					}
				}

				//repeat operation to remove the files.
				if(!tempResources.isEmpty()) cleanup(target);
			}
			catch(ARException are)
			{
				logger.warn("Error detecting attachment fields to remove downloaded resources on the local file system, during data transfer: " + are.getMessage());
				logger.debug("Exception", are);
			}
		}
		else if(target != null && !tempResources.isEmpty())
		{
			for(Object fieldId : tempResources)
			{
				String fieldKey = (String)fieldId;

				if(fieldKey != null && !fieldKey.contains("@"))
				{
					Object attrValue = target.getAttributeValue(fieldKey);

					if(attrValue != null && attrValue instanceof File)
					{
						File file = (File) attrValue;
						if(file.exists() && !file.delete()) file.deleteOnExit();
					}
					else if(attrValue != null && attrValue instanceof AttachmentValue)
					{
						File file = new File( ((AttachmentValue)attrValue).getValueFileName() );
						if(file.exists() && !file.delete()) file.deleteOnExit();
					}
				}
			}

			//find in children
			if(target instanceof MultiPartItem)
			{
				Iterator iterator = ((MultiPartItem)target).getPartCodes();

				while(iterator != null && iterator.hasNext())
				{
					String partCode = (String) iterator.next();
					String partCodePrefix = null;

					if(partCode != null && partCode.lastIndexOf('-') > 0) partCodePrefix =  partCode.substring(0, partCode.lastIndexOf('-'));
						else partCodePrefix = partCode;

					if(partCode != null && partCodePrefix != null)
					{
						CoreItem part = ((MultiPartItem)target).getPartInstance(partCode);

						if(part != null && !tempResources.isEmpty())
						{
							for(Object fieldId : tempResources)
							{
								String fieldKey = (String)fieldId;

								if(fieldKey != null && fieldKey.contains("@"))
								{
									String keyPrefix = fieldKey.substring(0, fieldKey.lastIndexOf("@"));
									String keyValue = fieldKey.substring(fieldKey.lastIndexOf("@") + 1);

									if(StringUtility.equals(keyPrefix, partCodePrefix) && StringUtility.isNotEmpty(keyValue))
									{
										Object attrValue = part.getAttributeValue(keyValue);

										if(attrValue != null && attrValue instanceof File)
										{
											File file = (File) attrValue;
											if(file.exists() && !file.delete()) file.deleteOnExit();
										}
										else if(attrValue != null && attrValue instanceof AttachmentValue)
										{
											File file = new File( ((AttachmentValue)attrValue).getValueFileName() );
											if(file.exists() && !file.delete()) file.deleteOnExit();
										}
									}
								}
								//close block for composite part key
							}
							//close loop for temporary resources
						}
						//close block for not null part instance
					}
					//close block for not null part code
				}
				//close iterator for parts
			}
			//close target multipart
		}
	}

	/**
	 * Check if the current action permit notifications
	 *
	 * @return true of false if the action has specified <code>notification</code> flag.
	 */
	protected boolean isNotified() throws AREasyException
	{
		if(jobEntry != null && jobLogger != null && jobEntry.exists() && jobLogger.exists())
		{
			Object notification = jobEntry.getAttributeValue(536871157);

			if(notification != null && !getConfiguration().containsKey("notification"))
			{
				getConfiguration().setKey("notification", "true");

				//close logger to send it via email
				setLoggerClosed();
			}
		}

		return super.isNotified();
	}

	/**
	 * Get report content containing data and log message (according with specified log level for the current action).
	 *
	 * @return content for this notification
	 */
	protected String getReportContent() throws AREasyException
	{
		if(jobEntry != null && jobLogger != null && jobEntry.exists() && jobLogger.exists())
		{
			StringBuffer body = new StringBuffer();

			Object dataFlag = jobEntry.getAttributeValue(536871162);
			Object logFlag = jobEntry.getAttributeValue(536871163);
			Object outFlag = jobEntry.getAttributeValue(536871164);

			body.append("\n");

			if(dataFlag != null)
			{
				body.append( jobLogger.getAttributeValue(536870975) );
				body.append("\n");
			}

			if(outFlag != null)
			{
				List list = getLoggerOutput();

				if(list != null && !list.isEmpty())
				{
					if(dataFlag != null)
					{
						body.append("\n");
						body.append("=== AREasy AAR Output ===");
						body.append("\n").append("\n");
					}

					for(int i = 1; i <= list.size(); i++)
					{
						CoreItem item = (CoreItem) list.get(i-1);

						body.append(i).append(") ");
						body.append(item.getStringAttributeValue(8));
						body.append("\n");
					}
				}
			}

			if(logFlag != null)
			{
				String content = jobLogger.getStringAttributeValue(536870913);

				if((dataFlag != null || outFlag != null) && content != null)
				{
					body.append("\n");
					body.append("=== AREasy AAR Log Message ===");
					body.append("\n").append("\n");
				}

				if(content != null)
				{
					body.append( jobLogger.getAttributeValue(536870913) );
					body.append("\n");
				}
			}

			return body.toString();
		}
		else return super.getReportContent();
	}

	/**
	 * Send the notification corresponding with actual action execution.
	 *
	 * @param body notification content
	 * @return notification <code>CoreItem</code> data structure
	 */
	protected CoreItem sendReport(String body) throws AREasyException
	{
		if(jobEntry != null && jobLogger != null && jobEntry.exists() && jobLogger.exists())
		{
			String recipientsTo = jobEntry.getStringAttributeValue(536871159);
			String recipientsCc = jobEntry.getStringAttributeValue(536871160);
			String subject =  jobEntry.getStringAttributeValue(536871158);

			//get last status before sending notification
			getCurrentStatus();

			//get notification subject
			if(subject != null) subject += " - " + status.getStatusMessage();
				else subject = status.getStatusMessage();

			if(subject == null) subject = jobEntry.getStringAttributeValue(2430);

			if(recipientsTo != null) getConfiguration().setKey("notificationrecipientto", recipientsTo);
			if(recipientsCc != null) getConfiguration().setKey("notificationrecipientcc", recipientsCc);
			getConfiguration().setKey("notificationsubject", subject);
			getConfiguration().setKey("notificationaction", "No");
		}

		CoreItem notification = super.sendReport(body);

		if(notification != null)
		{
			if(jobEntry != null && jobLogger != null && jobEntry.exists() && jobLogger.exists() && jobEntry.getAttributeValue(536871164) != null)
			{
				List list = getLoggerOutput();

				for(int i = 0; i < list.size(); i++)
				{
					CoreItem item = (CoreItem) list.get(i);

					try
					{
						String name = item.getStringAttributeValue(8);
						File file = item.getFileAttributeValue(getServerConnection(), 536870914);

						CoreItem nAttach = new CoreItem("AR System Email Attachments");
						nAttach.setAttribute(18005, "Email");
						nAttach.setAttribute(18133, name);
						nAttach.setAttribute(18004, file);
						nAttach.create(getServerConnection());

						CoreItem nAssoc = new CoreItem("AR System Email Association");
						nAssoc.setAttribute(18001, "Email");
						nAssoc.setAttribute(18002, "Attachment");
						nAssoc.setAttribute(18134, notification.getStringAttributeValue(179));
						nAssoc.setAttribute(18000, nAttach.getStringAttributeValue(179));
						nAssoc.create(getServerConnection());
					}
					catch(AREasyException are)
					{
						RuntimeLogger.warn("Error sending notification attachment: " + item + ". " + are.getMessage());
						logger.debug("Exception", are);
					}
				}
			}

			notification.setAttribute(18099, "Yes");
			notification.update(getServerConnection());
		}

		return notification;
	}

	/**
	 * This method allows you to call synchrony a discrete action that will use data processed by global action.
	 * Being a public method, could be called also from Velocity scripts
	 *
	 * @param actionName action signature
	 * @param commandLine action command line
	 */
	public void callAction(String actionName, String commandLine)
	{
		callAction(null, actionName, commandLine);
	}

	/**
	 * This method allows you to call synchrony a discrete action that will use data processed by global action.
	 * Being a public method, could be called also from Velocity scripts
	 *
	 * @param mainConfig special configuration structure that will be chose to initialize the action
	 * @param actionName action signature
	 * @param commandLine action command line
	 */
	public void callAction(Configuration mainConfig, String actionName, String commandLine)
	{
		String logLevel = null;

		if(actionName == null) return;
		if(commandLine == null) commandLine = "";

		//detect log level from configuration
		if(mainConfig != null) logLevel = mainConfig.getString("loglevel", null);
			else  logLevel = getConfiguration().getString("loglevel", null);

		//if log level wasn't found it will be taken from the logger answer
		if(logLevel == null) logLevel = RuntimeLogger.getLevel();

		try
		{
			//get command line
			commandLine = getTranslatedQualification(commandLine);

			//get action and his configuration
			RuntimeAction action = getManager().getRuntimeAction(actionName);
			Configuration config = getManager().getConfiguration(commandLine);

			//detect combined configuration asked by the caller
			if(mainConfig == null) mainConfig = config;
				else mainConfig.merge(config);

			//action initialization
			action.init(mainConfig, getManager(), getServerConnection());

			//action execution
			RuntimeLogger.debug("Prepare to call action: " + actionName);
			action.run();
		}
		catch(Throwable th)
		{
			RuntimeLogger.warn("Error running '" + actionName + "' action: " + th.getMessage());
			logger.debug("Error running '" + actionName + "' action with the command line: " + commandLine, th);
		}

		//set back the log level that was previously configured
		RuntimeLogger.setLevel(logLevel);
	}

	public void setLoggerDebug(String message)
	{
		RuntimeLogger.debug(StringUtility.trim(message));
	}

	public void setLoggerDebug(String message, String details)
	{
		RuntimeLogger.debug(StringUtility.trim(message + details));
	}

	public void setLoggerInfo(String message)
	{
		RuntimeLogger.info(StringUtility.trim(message));
	}

	public void setLoggerInfo(String message, String details)
	{
		RuntimeLogger.info(StringUtility.trim(message + details));
	}

	public void setLoggerWarn(String message)
	{
		RuntimeLogger.warn(StringUtility.trim(message));
	}

	public void setLoggerWarn(String message, String details)
	{
		RuntimeLogger.warn(StringUtility.trim(message + details));
	}

	public void setLoggerError(String message)
	{
		RuntimeLogger.error(StringUtility.trim(message));
	}

	public JobEntry getJobEntryInstance()
	{
		return new JobEntry();
	}

	public JobEntry getJobEntryInstance(ServerConnection arsession, String instanceId) throws AREasyException
	{
		return new JobEntry(arsession, instanceId);
	}

	public class JobEntry extends CoreItem
	{
		public JobEntry()
		{
			setFormName(FORM_RUNTIME_ENTRY);
		}

		public JobEntry(ServerConnection arsession, String instanceId) throws AREasyException
		{
			this();

			setAttribute(179, instanceId);
			read(arsession);
		}

		public String toString()
		{
			Object status = getAttributeValue(7);

			return "JobEntry ("+ getStringAttributeValue(536871088) + ") [Name = " + getStringAttributeValue(2430) + ", Request ID = " + getEntryId() +
			   ", Status = " + (status != null && (Integer)status == 1 ? "Running" : "Standby") + ", Category = " + getAttributeValue(536871097) + ")";
		}

		public CoreItem getInstance()
		{
			return new JobEntry();
		}
	}

	public void setOutputMessage()
	{
		if(jobEntry == null) super.setOutputMessage();
		else
		{
			StringBuffer buffer = new StringBuffer();

			String name = jobEntry.getStringAttributeValue(2430);
			String category = jobEntry.getStringAttributeValue(536871097);
			if(StringUtility.isNotEmpty(category)) name = category + "/" + name;

			buffer.append("\n").append(getCode()).append(" (").append(name).append(") - Execution results:" + "\n");
			buffer.append("\tTotal number of records: ").append(getRecordsCounter()).append("\n");
			buffer.append("\tNumber of errors: ").append(getErrorsCounter()).append("\n");
			buffer.append("\tExecution time: ").append(getCronTime()).append("\n" + "\n");

			RuntimeLogger.add(buffer.toString());
		}
	}
}
