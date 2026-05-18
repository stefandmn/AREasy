package org.areasy.runtime.actions.ars.data;

/*
 *  Copyright (c) 2007-2023 AREasy Runtime
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

import org.areasy.common.data.StringUtility;
import org.areasy.runtime.RuntimeAction;
import org.areasy.runtime.engine.RuntimeLogger;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.runtime.engine.structures.Attribute;
import org.areasy.runtime.engine.structures.CoreItem;
import org.areasy.runtime.engine.structures.MultiPartItem;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Dedicated library to perform all standard operations (data transactions) for a Remedy form.
 * This action is usually combined with filewrapper runtime action in order to process data from
 * a data file (excel csv, text delimited, etc. - all files supported by parser engine)
 */
public class FormData extends BaseData
{
	private boolean validateSet = false;
	private String validateDisplay = null;
	private String validateSeparator = null;
	private String validateSelectType = null;
	private String validateSelectData[] = null;
	private String validateSelectHeader[] = null;

	private boolean matchset = false;
	private boolean reportdata = false;
	private boolean reporterrors = false;
	private boolean createonly = false;
	private boolean firstmatchreading = false;
	private boolean exactmatchreading = false;
	private boolean operationRemoteTarget = false;
	private boolean callRemoteTarget = false;
	private boolean fastrun = false;

	/**
	 * Execute the current action.
	 *
	 * @throws AREasyException
	 * if any error will occur
	 */
	public void run() throws AREasyException
	{
		this.fastrun = getConfiguration().getBoolean("fastrun", false);
		this.matchset = getConfiguration().getBoolean("matchset", false);
		this.reportdata = getConfiguration().getBoolean("reportdata", false);
		this.reporterrors = getConfiguration().getBoolean("reporterrors", false);
		this.createonly = getConfiguration().getBoolean("createonly", false);
		this.firstmatchreading = getConfiguration().getBoolean("firstmatchreading", false);
		this.exactmatchreading = getConfiguration().getBoolean("exactmatchreading", false);
		this.operationRemoteTarget = getConfiguration().getBoolean("operation-remotetarget", false);
		this.callRemoteTarget = getConfiguration().getBoolean("call-remotetarget", false);

		String subAction = getConfiguration().getString("subaction",null);
		String callAction = getConfiguration().getString("call", null);

		if((subAction != null || callAction != null)  && !isSpinOf())
		{
            //read all ignored null field values.
            setIgnoreNullFields();

			CoreItem searchEntry = getEntity();
			setRemoteFormName(searchEntry);
            if (searchEntry.getFormName() == null) throw new AREasyException("Form name is null");

			if(!StringUtility.isEmpty(subAction))
			{
				if(!isLinked()) RuntimeLogger.info("Form Data processor runs '" + subAction + "' independent action");
				// getConfiguration().removeKey("subaction");
				call(searchEntry, subAction, false, 0);
			}
			else if(!StringUtility.isEmpty(callAction))
			{
				if(!isLinked()) RuntimeLogger.info("Form Data processor runs '" + callAction + "' spin action");
				call(searchEntry, callAction, true, 0);
			}
		}
		else
		{
			String operation = getConfiguration().getString("operation", getConfiguration().getString("call-operation", null));
			if (operation == null) throw new AREasyException("Data operation is not specified");

			//check if there are registered data-maps and load them
			setDataMaps();

			//read all ignored null field values.
			setIgnoreNullFields();

			CoreItem searchEntry = getEntity();
			setRemoteFormName(searchEntry);
            if (searchEntry.getFormName() == null) throw new AREasyException("Form name is null");

			//run operation procedure
			operation(searchEntry);
		}

		if(!isLinked() && reportdata)
		{
			if(RuntimeLogger.hasData())
			{
				if(getConfiguration().getString("dataout", null) != null)
				{
					if(validateSelectHeader != null) writeTextFile(getConfiguration().getString("dataout", null), StringUtility.join(validateSelectHeader,validateSeparator) + "\n" + RuntimeLogger.getCompactData());
						else writeTextFile(getConfiguration().getString("dataout", null), RuntimeLogger.getCompactData());
				}
				else
				{
					if(validateSelectHeader != null) RuntimeLogger.info(StringUtility.capitalize(getCode()) + " processor ended providing output data\n" + StringUtility.join(validateSelectHeader, validateSeparator) + "\n" + RuntimeLogger.getCompactData());
						else RuntimeLogger.info(StringUtility.capitalize(getCode()) + " processor ended providing output data\n" + RuntimeLogger.getCompactData());
				}
			}
			else RuntimeLogger.warn("Processor ended without output data");
		}
		else
		{
			if(!isLinked() && reporterrors)
			{
				if (RuntimeLogger.hasErrors())
				{
					if(getConfiguration().getString("errorsout", null) != null) writeTextFile(getConfiguration().getString("errorsout", null), RuntimeLogger.getCompactData());
					else RuntimeLogger.info(StringUtility.capitalize(getCode()) + " processor ended by collecting errors for request Ids\n" + RuntimeLogger.getCompactData());
				}
			}
		}
    }

	/**
	 * This is the recursive caller method aimed to call a sub-action
	 *
	 * @throws AREasyException in case any error occurs
	 */
	protected void call(CoreItem searchEntry, String subaction, boolean spinFlag, int limit) throws AREasyException
	{
		call(searchEntry, subaction, spinFlag, limit, null);
	}

	/**
	 * This is the recursive caller method aimed to call a sub-action
	 *
	 * @throws AREasyException in case any error occurs
	 */
	private void call(CoreItem searchEntry, String subaction, boolean spinFlag, int limit, String labelIdentifier) throws AREasyException
	{
		if(labelIdentifier == null) labelIdentifier = "c" + UUID.randomUUID().toString().substring(0, 8);
		String labelChunk = "chunk";
		String labelLimit = "limit";
		String labelNextChunkId = "nextchunkid";

		String lastId = null;
		logger.debug("Start running caller: " + subaction);

		RuntimeAction action = null;
		boolean stopFlag = false;
		int indexLimit = limit;

		int maxLimit = getConfiguration().getInt(labelLimit, 0);
		int chunk = getConfiguration().getInt(labelChunk, 1000);

		List entries = searchData(searchEntry, labelIdentifier, callRemoteTarget);

		boolean nextChunk = chunk > 0 && entries.size() == chunk;
		logger.debug("Found " + entries.size() + "/" + chunk + " source records for caller");

		for (Object entryObj : entries)
		{
			CoreItem source = (CoreItem) entryObj;
			lastId = source.getEntryId();

			if (nextChunk) getConfiguration().setKey(labelNextChunkId + "-" + labelIdentifier, source.getEntryId());

			/* The delegated action to be executed **/
			if(!spinFlag) action = getManager().getRuntimeAction(subaction);
				else action = getManager().getSpinAction(subaction);

			getConfiguration().setKey("S1", source.getEntryId());
			getConfiguration().setKey("S8", source.getEntryId());
			if(source.containsAttributeField(179)) getConfiguration().setKey("S179", source.getStringAttributeValue(179));
			if(source.containsAttributeField(379)) getConfiguration().setKey("S379", source.getStringAttributeValue(379));
			if(source.containsAttributeField(400129200)) getConfiguration().setKey("S400129200", source.getStringAttributeValue(400129200));
			if(source.containsAttributeField(200000020)) getConfiguration().setKey("S200000020", source.getStringAttributeValue(200000020));
			if(getConfiguration().containsKey("sourceconfigmapping"))
			{
				List mappingList = getConfiguration().getList("sourceconfigmapping");
				for(int i = 0; mappingList != null && i < mappingList.size(); i++)
				{
					String key = (String) mappingList.get(i);
					getConfiguration().setKey("S" + key, source.getStringAttributeValue(key));
				}
			}

			try
			{
				//target data lockup
				if(getConfiguration().containsKey("targetidentification") || getConfiguration().containsKey("targetidentificationquery"))
				{
					boolean vchange = false;
					CoreItem target = new CoreItem();
					target.setFirstMatchReading(getConfiguration().getBoolean("targetidentification-firstmatch", false));
					target.setFirstMatchReading(getConfiguration().getBoolean("targetidentification-ignorenull", false));
					setFormName(target);

					if(getConfiguration().containsKey("targetidentification"))
					{
						for (Object fid : getConfiguration().getList("targetidentification", new ArrayList()))
						{
							if (source.getAttribute(fid) != null)
							{
								target.setAttribute(source.getAttribute(fid));
								vchange = true;
							}
						}
					}
					else if(getConfiguration().containsKey("targetidentificationquery")) vchange = true;

					if (vchange)
					{
						try
						{
							target.setChanged();
							if(getConfiguration().containsKey("targetidentificationquery")) target.read(getServerConnection(), getTranslatedQualification(getConfiguration().getString("targetidentificationquery")));
								else target.read(getServerConnection());
						}
						catch (Exception e)
						{
							logger.error("Error running target cursor identification: " + e.getMessage());
						}

						if (target.exists()) logger.debug("Target cursor identity found: " + target.toFullString());
							else logger.debug("Target cursor identity not found");

						//override source with target record
						source.set(target);
						logger.debug("Merged cursor data entry: " + source.toFullString());
					}
					else logger.debug("Target cursor identity skipped because identification data doesn't exist");
				}

				//add master data in the configuration
				if(source.exists())
				{
					getConfiguration().setKey("M1", source.getEntryId());
					for(Attribute attr : source.getAttributes())
					{
						getConfiguration().setKey("M" + attr.getId(), attr.getValue());
					}
				}
				else getConfiguration().setKey("M1", "NULL");

				if(logger.isDebugEnabled()) logger.debug("Preparing cursor action for record: " + source.toFullString());

				//initialize and execute command operation
				if(getWhenCondition(source))
				{
					action.init(this);
					action.run();
				}
				else RuntimeLogger.debug("Cursor action skipped due to 'when' condition: " + source);
			}
			catch (Throwable th)
			{
				setErrorsCounter();

				if (isForced())
				{
					RuntimeLogger.error("Error processing cursor data record with id " + source.getEntryId() + ": " + th.getMessage());
					logger.debug("Exception", th);
				}
				else
				{
					if (th instanceof AREasyException) throw (AREasyException) th;
						else throw new AREasyException(th);
				}
			}

            //execution counter incrementation
            if(action instanceof BaseData)
            {
                if( ((BaseData)action).getRecordsCounter() > 0 ) addRecordsCounter( ((BaseData)action).getRecordsCounter() );
                    else setRecordsCounter();

                if( ((BaseData)action).getProcessorsCounter() > 0 ) addProcessorsCounter( ((BaseData)action).getProcessorsCounter() );

                if( ((BaseData)action).getErrorsCounter() > 0 ) addErrorsCounter( ((BaseData)action).getErrorsCounter() );
            }
            else setRecordsCounter();

			indexLimit++;
			if(maxLimit > 0)
			{
				stopFlag = indexLimit >= maxLimit;
				if(stopFlag) break;
			}

			// check interruption and exit if the execution was really interrupted
			if(isInterrupted())
			{
				RuntimeLogger.warn("Execution interrupted by user");
				return;
			}

			//execution sleep
			if(getConfiguration().containsKey("sleep")&& !stopFlag)
			{
				try
				{
					int sleep = getConfiguration().getInt("sleep", 1000);
					if(sleep < 1000) sleep *= 1000;

					Thread.sleep(sleep);
				}
				catch(InterruptedException e) { /* nothing to do */ }
			}
		}

		// run for next chunk
		if (nextChunk && !stopFlag)
		{
			entries.clear();
			entries = null;

			displayChunkStatusMessage(false, lastId);
			call(searchEntry, subaction, spinFlag, indexLimit, labelIdentifier);
		}
		else if(chunk > 0 && getRecordsCounter() > chunk) displayChunkStatusMessage(true, lastId);
	}

	/**
	 * This is the recursive operation method aimed to search or read data from Remedy and then to apply
	 * requested operation (transaction)
	 *
	 * @param searchEntry empty <code>CoreItem</code> structure used by read or search process
	 * @throws AREasyException in case any error occurs
	 */
	protected void operation(CoreItem searchEntry) throws AREasyException
	{
		operation(searchEntry, null);
	}

	private void operation(CoreItem searchEntry, String labelIdentifier) throws AREasyException
	{
		if(labelIdentifier != null) labelIdentifier = "o" + UUID.randomUUID().toString().substring(0, 8);

		String labelChunk = "chunk";
		String labelNextChunkId = "nextchunkid";

		if(isSpinOf())
		{
			labelChunk = "call-chunk";
			labelNextChunkId = "call-nextchunkid";
		}

		String lastId = null;
		List entries = null;

		logger.debug("Start running '" + getConfiguration().getString("operation", getConfiguration().getString("call-operation", null)) + "' operation for '" + getCode() + "' " + (isSpinOf() ? "spin" : "independent") + " action");

		//run search operation
		if(firstmatchreading || exactmatchreading)
		{
			CoreItem entry = readData(searchEntry, operationRemoteTarget);

			if(entry != null && entry.exists())
			{
				entries = new ArrayList<CoreItem>();
				entries.add(entry);
			}
			logger.debug("Reading data for " + (isSpinOf() ? "spin" : "main") + " processor: " + entry);
		}
		else if(!createonly)
		{
			entries = searchData(searchEntry, labelIdentifier, operationRemoteTarget);
			if (entries == null || entries.isEmpty()) logger.debug("No record found for " + (isSpinOf() ? "spin" : "main") + " processor");
		}

		if (entries == null || entries.isEmpty())
		{
			run(searchEntry);
		}
		else
		{
			int chunk = getConfiguration().getInt(labelChunk, 1000);
			boolean nextChunk = chunk > 0 && entries.size() == chunk;
			logger.debug("Found " + entries.size() + "/" + chunk + " source record(s) for " + (isSpinOf() ? "spin" : "main") + " processor");

			for (Object entryObj : entries)
			{
				CoreItem source = (CoreItem) entryObj;
				lastId = source.getEntryId();

				if (nextChunk) getConfiguration().setKey(labelNextChunkId + "-" + labelIdentifier, source.getEntryId());
				if(logger.isDebugEnabled()) logger.debug("Preparing " + (isSpinOf() ? "spin" : "main") + " action for record: " + source.toFullString());

				getConfiguration().setKey("S1", source.getEntryId());
				getConfiguration().setKey("S8", source.getEntryId());
				if(source.containsAttributeField(179)) getConfiguration().setKey("S179", source.getStringAttributeValue(179));
				if(source.containsAttributeField(379)) getConfiguration().setKey("S379", source.getStringAttributeValue(379));
				if(source.containsAttributeField(400129200)) getConfiguration().setKey("S400129200", source.getStringAttributeValue(400129200));
				if(source.containsAttributeField(200000020)) getConfiguration().setKey("S200000020", source.getStringAttributeValue(200000020));
				if(getConfiguration().containsKey("sourceconfigmapping"))
				{
					List mappingList = getConfiguration().getList("sourceconfigmapping");
					for(int i = 0; mappingList != null && i < mappingList.size(); i++)
					{
						String key = (String) mappingList.get(i);
						getConfiguration().setKey("S" + key, source.getStringAttributeValue(key));
					}
				}

				//call lockup or identification procedure in target system
				if(getConfiguration().containsKey(isSpinOf() ? "call-targetidentification" : "targetidentification") || getConfiguration().containsKey(isSpinOf() ? "call-targetidentificationquery" : "targetidentificationquery"))
				{
					CoreItem target = new CoreItem();
					target.setFirstMatchReading(getConfiguration().getBoolean((isSpinOf() ? "call-targetidentification" : "targetidentification") +"-firstmatch", false));
					target.setFirstMatchReading(getConfiguration().getBoolean((isSpinOf() ? "call-targetidentification" : "targetidentification") +"-ignorenull", true));
					boolean vchange = false;
					setFormName(target);

					if(getConfiguration().containsKey(isSpinOf() ? "call-targetidentification" : "targetidentification"))
					{
						for (Object fid : getConfiguration().getList(isSpinOf() ? "call-targetidentification" : "targetidentification", new ArrayList()))
						{
							if (source.getAttribute(fid) != null)
							{
								target.setAttribute(source.getAttribute(fid));
								vchange = true;
							}
						}
					}
					else if(getConfiguration().containsKey(isSpinOf() ? "call-targetidentificationquery" : "targetidentificationquery")) vchange = true;

					if (vchange)
					{
						try
						{
							target.setChanged();
							if(getConfiguration().containsKey(isSpinOf() ? "call-targetidentificationquery" : "targetidentificationquery")) target.read(getServerConnection(), getTranslatedQualification(getConfiguration().getString(isSpinOf() ? "call-targetidentificationquery" : "targetidentificationquery")));
								else target.read(getServerConnection());
						}
						catch (Exception e)
						{
							logger.error("Error running target " + (isSpinOf() ? "spin" : "main") + " identification call: " + e.getMessage());
						}

						if (target.exists()) logger.debug("Target " + (isSpinOf() ? "spin" : "main") + " identity found: " + target.toFullString());
							else logger.debug("Target " + (isSpinOf() ? "spin" : "main") + " identity not found");

						if(!matchset) source.offset(target);
							else source.set(target);
						logger.debug("Merged " + (isSpinOf() ? "spin" : "main") + " data entry, " + (matchset ? "with" : "without") + " full matchset: " + source.toFullString());
					}
					else logger.debug("Target " + (isSpinOf() ? "spin" : "main") + " identity skipped because identification data doesn't exist");
				}

				try
				{
					//execute transaction
					run(source);
				}
				catch (Throwable th)
				{
					setErrorsCounter();
					if(reporterrors) RuntimeLogger.add(source.getEntryId());

					if (isForced())
					{
						RuntimeLogger.error("Error processing " + (isSpinOf() ? "spin" : "main") + " data record with id " + lastId + ": " + th.getMessage());
						logger.debug("Exception", th);
					}
					else
					{
						if (th instanceof AREasyException) throw (AREasyException) th;
							else throw new AREasyException(th);
					}
				}

				//execution counter incrementation
				setRecordsCounter();
				logger.debug("Closing operation for entry: " + source.toString());
			}

			// run for next chunk
			if (nextChunk)
			{
				entries.clear();
				entries = null;

				displayChunkStatusMessage(false, lastId);
				operation(searchEntry, labelIdentifier);
			}
			else if(chunk > 0 && getRecordsCounter() > chunk) displayChunkStatusMessage(true, lastId);
		}
	}

	/**
	 * Read data from Remedy search and in case of matching it will return only one record
	 *
	 * @param entry <code>CoreItem</code> structure used by read or search process
	 * @return <code>CoreItem</code> structure returned by read operation
	 * @throws AREasyException in case any error occurs
	 */
	protected CoreItem readData(CoreItem entry, boolean target) throws AREasyException
	{
		String id = getConfiguration().getString("id", null);
		if(StringUtility.isNotEmpty(id)) entry.setAttribute(1, id);

		String labelQualification = "qualification";
		if(isSpinOf()) labelQualification = "call-qualification";

		if(getConfiguration().getString(labelQualification, null) == null)
		{
			setQueryFields(entry);

			if (!isRemoteConnection() || target) entry.read(getServerConnection());
				else entry.read(getRemoteConnection());
		}
		else
		{
			String qualification = getTranslatedQualification(getConfiguration().getString(labelQualification, null));

            if (!isRemoteConnection() || target) entry.read(getServerConnection(), qualification);
                else entry.read(getRemoteConnection(), qualification);
		}

		return entry;
	}

	/**
	 * Search data from Remedy search and in case of matching it will return a list of records
	 *
	 * @param entry <code>CoreItem</code> structure used by read or search process
	 * @return <code>CoreItem</code> structure returned by read operation
	 * @throws AREasyException in case any error occurs
	 */
	protected List searchData(CoreItem entry, String identifier, boolean target) throws AREasyException
	{
		String id = getConfiguration().getString("id", null);
		if(StringUtility.isNotEmpty(id)) entry.setAttribute(1, id);

		String labelQualification = "qualification";
		String lavelChunk = "chunk";
		String labelNextChunkId = "nextchunkid" + "-" + identifier;
		String labelLimit = "limit";

		if(isSpinOf())
		{
			labelQualification = "call-qualification";
			lavelChunk = "call-chunk";
			labelNextChunkId = "call-nextchunkid" + "-" + identifier;
			labelLimit = "call-limit";
		}

		if(getConfiguration().getString(labelQualification, null) == null)
		{
			setQueryFields(entry);
			int chunk = getConfiguration().getInt(lavelChunk, 0);

			if(chunk > 0)
			{
				if(getConfiguration().containsKey(labelNextChunkId)) entry.setAttribute(1, ">" + getConfiguration().getString(labelNextChunkId));

                if (!isRemoteConnection()) return entry.search(getServerConnection(), chunk);
                    else  return entry.search(getRemoteConnection(), chunk);

			}
			else
            {
                if (!isRemoteConnection()) return entry.search(getServerConnection(), chunk);
                    else  return entry.search(getRemoteConnection(), chunk);
            }
		}
		else
		{
			String qualification = getTranslatedQualification(getConfiguration().getString(labelQualification, null));
			int limit = getConfiguration().getInt(labelLimit, 0);

			if(limit > 0)
			{
				if(getConfiguration().containsKey(lavelChunk)) getConfiguration().removeKey(lavelChunk);

                if (!isRemoteConnection() || target) return entry.search(getServerConnection(), qualification, limit);
                    else  return entry.search(getRemoteConnection(), qualification, limit);
			}
			else
			{
				int chunk = getConfiguration().getInt(lavelChunk, 0);
				if(getConfiguration().containsKey(labelNextChunkId)) qualification = "('1'>\"" + getConfiguration().getString(labelNextChunkId) + "\") AND (" + qualification + ")";

                if (!isRemoteConnection() || target) return entry.search(getServerConnection(), qualification, chunk);
                    else return entry.search(getRemoteConnection(), qualification, chunk);
			}
		}
	}

	/**
	 * Execute an action for a specific <code>CoreItem</code>. This item must be identified previously and then the method
	 * could be called. This method will use by standard actions which implement an workflow using these type of action
	 * which permit single change or update.
	 *
	 * @param entry <code>CoreItem</code> structure, which should be instantiated.
	 * @throws org.areasy.runtime.engine.base.AREasyException if any error will occur.
	 */
	public void run(CoreItem entry) throws AREasyException
	{
		String operation = getConfiguration().getString("operation", getConfiguration().getString("call-operation", null));
		logger.debug("Start executing operation: " + operation);

		boolean condition = false;
		if (!fastrun) condition = getRunCondition(entry);

		if (fastrun || condition)
		{
			if (!fastrun) setFlowAdaptation(entry);

			if (StringUtility.equalsIgnoreCase(operation, "insert") || StringUtility.equalsIgnoreCase(operation, "create")) runCreate(entry);
			else if (StringUtility.equalsIgnoreCase(operation, "update") || StringUtility.equalsIgnoreCase(operation, "modify")) runUpdate(entry);
			else if (StringUtility.equalsIgnoreCase(operation, "delete") || StringUtility.equalsIgnoreCase(operation, "remove")) runRemove(entry);
			else if (StringUtility.equalsIgnoreCase(operation, "merge")) runMerge(entry);
			else if (StringUtility.equalsIgnoreCase(operation, "migrate")) runMigrate(entry);
			else if (StringUtility.equalsIgnoreCase(operation, "validate")) runValidate(entry);
		}
		else RuntimeLogger.debug("Execution skipped due to run-condition evaluation: " + entry);

	}

	/**
	 * Insert/Create data through a Remedy regular form.
	 *
	 * @param entry <code>CoreItem</code> structure, which should be instantiated.
	 * @throws AREasyException if any error will occur.
	 */
	public void runValidate(CoreItem entry) throws AREasyException
	{
		boolean log = false;

		//set data values
		setDataFields(entry);

		boolean condition = false;
		if (!fastrun) condition = getCommitCondition(entry);

		if (fastrun || condition)
		{
			if(!validateSet)
			{
				validateSelectData = getConfiguration().getStringArray("select-data", null);
				validateSelectHeader = getConfiguration().getStringArray("select-header", null);
				validateSelectType = getConfiguration().getString("select-type", null);
				validateDisplay = getConfiguration().getString("display", null);
				validateSeparator = getConfiguration().getString("separator", ",");
				validateSet = true;
			}

			if(entry.exists() && (validateDisplay == null || StringUtility.equalsIgnoreCase(validateDisplay, "on")))
			{
				RuntimeLogger.info("On : " + entry);
				log = true;
			}
			else if(!entry.exists() && (validateDisplay == null || StringUtility.equalsIgnoreCase(validateDisplay, "off")))
			{
				RuntimeLogger.info("Off: " + entry);
				log = true;
			}

			if(entry.exists() && (validateSelectType == null || StringUtility.equalsIgnoreCase(validateSelectType, "on") || StringUtility.equalsIgnoreCase(validateSelectType, "all")))
			{
				log = true;
			}
			else if(!entry.exists() && (validateSelectType == null || StringUtility.equalsIgnoreCase(validateSelectType, "off") || StringUtility.equalsIgnoreCase(validateSelectType, "all")))
			{
				log = true;
			}

			setProcessorsCounter();

			if(log && reportdata)
			{
				if(validateSelectData == null) RuntimeLogger.add(entry.getEntryId());
				else
				{
					String line = "";
					for(int i = 0; i < validateSelectData.length; i++)
					{
						if(validateSelectData.length == 1 || !StringUtility.equals(validateSelectType, ","))
						{
							if(i > 0) line += validateSeparator + entry.getStringAttributeValue(StringUtility.trim(validateSelectData[i]));
								else line = entry.getStringAttributeValue(StringUtility.trim(validateSelectData[i]));
						}
						else
						{
							if(i > 0) line += validateSeparator + entry.getEnclosedStringAttributeValue(StringUtility.trim(validateSelectData[i]));
								else line = entry.getEnclosedStringAttributeValue(StringUtility.trim(validateSelectData[i]));
						}
					}

					RuntimeLogger.add(line);
				}
			}
		}
		else RuntimeLogger.debug("Execution skipped due to commit-condition evaluation: " + entry);
    }

	/**
	 * Insert/Create data through a Remedy regular form.
	 *
	 * @param entry <code>CoreItem</code> structure, which should be instantiated.
	 * @throws AREasyException if any error will occur.
	 */
	public void runMigrate(CoreItem entry) throws AREasyException
	{
		try
		{
			runMerge(entry);
		}
		catch(Exception e)
		{
			logger.debug("Merging data entry is not possible, trying to create/update it: " + entry + ". " + e.getMessage());

			if(entry.exists())
			{
				runUpdate(entry);
			}
			else
			{
				runCreate(entry);
			}
		}
	}

	/**
	 * Insert/Create data through a Remedy regular form.
	 *
	 * @param entry <code>CoreItem</code> structure, which should be instantiated.
	 * @throws AREasyException if any error will occur.
	 */
	public void runCreate(CoreItem entry) throws AREasyException
	{
        //check if the target forms is different
		setFormName(entry);

		//data fill-in and create record
		if(!entry.exists())
		{
			//set data values
			setDataFields(entry, getConfiguration().getBoolean("createifnotexist", false), getConfiguration().getBoolean("updateifexists", false));

			boolean condition = false;
			if (!fastrun) condition = getCommitCondition(entry);

			if (fastrun || condition)
			{
				entry.create(getServerConnection());
				setProcessorsCounter();

				if (RuntimeLogger.isTraceLevel()) RuntimeLogger.trace("Created data entry: " + entry.toFullString());
				else RuntimeLogger.debug("Created data entry: " + entry);
				if (reportdata) RuntimeLogger.add(entry.getEntryId());

				if (getConfiguration().getBoolean("multipart", false) && entry instanceof MultiPartItem)
				{
					//set multipart form names
					setMultiPartForms(entry);

					//execute transactions
					((MultiPartItem) entry).commitParts(getServerConnection(), getMultiPartQueryFields(), getMultiPartDataFields());
				}
			}
			else RuntimeLogger.debug("Execution skipped due to commit-condition evaluation: " + entry);
		}
		else if(getConfiguration().getBoolean("updateifexists", false) && entry.exists()) runUpdate(entry);
	}

	public void runUpdate(CoreItem entry) throws AREasyException
	{
        //check if the target forms is different
		setFormName(entry);

		if(entry.exists())
		{
			//set data values
			setDataFields(entry, getConfiguration().getBoolean("createifnotexist", false), getConfiguration().getBoolean("updateifexists", false));

			boolean condition = false;
			if (!fastrun) condition = getCommitCondition(entry);

			if (fastrun || condition)
			{
				entry.update(getServerConnection());
				setProcessorsCounter();

				if(RuntimeLogger.isTraceLevel()) RuntimeLogger.trace("Updated data entry: " + entry.toFullString());
					else RuntimeLogger.debug("Updated data entry: " + entry);
				if(reportdata) RuntimeLogger.add(entry.getEntryId());

				if (getConfiguration().getBoolean("multipart", false) && entry instanceof MultiPartItem)
				{
					//set multipart form names
					setMultiPartForms(entry);

					//execute transactions
					if(!isRemoteConnection()) ((MultiPartItem) entry).commitParts(getServerConnection(), getMultiPartQueryFields(), getMultiPartDataFields());
						else ((MultiPartItem) entry).commitParts(getRemoteConnection(), getMultiPartQueryFields(), getMultiPartDataFields());
				}
			}
			else RuntimeLogger.debug("Execution skipped due to commit-condition evaluation: " + entry);
		}
		else if(getConfiguration().getBoolean("createifnotexist", false) && !entry.exists()) runCreate(entry);
	}

	/**
	 * Remove/delete data from a Remedy regular form.
	 *
	 * @param entry <code>CoreItem</code> structure, which should be instantiated.
	 * @throws org.areasy.runtime.engine.base.AREasyException if any error will occur.
	 */
	public void runRemove(CoreItem entry) throws AREasyException
	{
        //check if the target forms is different
		setFormName(entry);

		if(entry.exists())
		{
			//set data values
			setDataFields(entry);

			boolean condition = false;
			if (!fastrun) condition = getCommitCondition(entry);

			if (fastrun || condition)
			{
				if (getConfiguration().getBoolean("multipart", false) && entry instanceof MultiPartItem)
				{
					//set multipart form names
					setMultiPartForms(entry);

					setMultiPartQueryFields(entry);
					((MultiPartItem) entry).readParts(getServerConnection());
					((MultiPartItem) entry).removeParts(getServerConnection());
				}

				entry.remove(getServerConnection());
				setProcessorsCounter();

				if(RuntimeLogger.isTraceLevel()) RuntimeLogger.trace("Deleted data entry: " + entry.toFullString());
					else RuntimeLogger.debug("Deleted data entry: " + entry);
				if(reportdata) RuntimeLogger.add(entry.getEntryId());
			}
			else RuntimeLogger.debug("Execution skipped due to commit-condition evaluation: " + entry);
		}
	}

	public void runMerge(CoreItem entry) throws AREasyException
	{
        //check if the target forms is different
		setFormName(entry);

		if (getConfiguration().getBoolean("mergevaliddata", false) && !entry.exists())
		{
			RuntimeLogger.debug("Skip merging because data was not validated: " + entry);
			return;
		}

		//set data values
		setDataFields(entry);

		boolean condition = false;
		if (!fastrun) condition = getCommitCondition(entry);

		if (fastrun || condition)
		{
			List qualList = getConfiguration().getList("mergematchingfields", null);
			String qualData = getConfiguration().getString("mergematchingquery", null);
			if (qualList != null) entry.merge(getServerConnection(), getMergeTypeAndOptions(getConfiguration()), qualList);
				else if (qualData != null) entry.merge(getServerConnection(), getMergeTypeAndOptions(getConfiguration()), qualData);
					else entry.merge(getServerConnection(), getMergeTypeAndOptions(getConfiguration()));

			setProcessorsCounter();

			if (RuntimeLogger.isTraceLevel()) RuntimeLogger.trace("Merged data entry: " + entry.toFullString());
				else RuntimeLogger.debug("Merged data entry: " + entry);
			if (reportdata) RuntimeLogger.add(entry.getEntryId());
		}
		else RuntimeLogger.debug("Execution skipped due to commit-condition evaluation: " + entry);
	}

	protected void setRemoteFormName(CoreItem entry)
	{
        String formName = isSpinOf() ? getConfiguration().getString("call-remoteform", getConfiguration().getString("call-remoteformname", null)) :
                getConfiguration().getString("remoteform", getConfiguration().getString("remoteformname", null));

		//check if the target forms is different
		if (StringUtility.isNotEmpty(formName) && getConfiguration().getString("arremoteserver", null) != null)
		{
            entry.setFormName(formName);
		}
        else setFormName(entry);
	}

    protected void setFormName(CoreItem entry)
    {
        String formName = isSpinOf() ? getConfiguration().getString("call-form", getConfiguration().getString("call-formname", null)) :
                getConfiguration().getString("form", getConfiguration().getString("formname", null));

        //check if the target forms is different
        entry.setFormName(formName);
    }

}
