package org.areasy.runtime.actions.flow;

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

import com.bmc.arsys.api.Constants;
import com.bmc.arsys.api.Field;
import com.bmc.arsys.api.SortInfo;
import org.areasy.common.data.BooleanUtility;
import org.areasy.common.data.NumberUtility;
import org.areasy.common.data.StringUtility;
import org.areasy.common.data.type.map.keyvalue.DefaultKeyValue;
import org.areasy.common.support.configuration.Configuration;
import org.areasy.common.support.configuration.base.BaseConfiguration;
import org.areasy.runtime.actions.flow.sources.AbstractSource;
import org.areasy.runtime.actions.flow.sources.RemedySource;
import org.areasy.runtime.engine.RuntimeLogger;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.runtime.engine.base.ServerConnection;
import org.areasy.runtime.engine.services.parser.ParserEngine;
import org.areasy.runtime.engine.structures.CoreItem;
import org.areasy.runtime.engine.structures.MultiPartItem;
import org.areasy.runtime.engine.workflows.ProcessorLevel0Reader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

/**
 * General library used for data development. This is a framework action used to manage any
 * kind of data transfer from a source to a target <code>CoreItem</code> structure.
 */
public class DataTransferAction extends FlowPatternAction
{
	private AbstractSource dataSource = null;
	private ServerConnection remoteConnection = null;
	private String dataTarget = null;

	private int sourceIndex = 0;
	private List searchList = null;
	private String lastRequestId = null;
	private Map dictionary = null;

	/**
	 * Execute action's workflow.
	 *
	 * @throws org.areasy.runtime.engine.base.AREasyException
	 *          if any global error occurs. All errors coming from action's execution will become output items
	 */
	public void run() throws AREasyException
	{
		getCron().start();
		Map<Integer, Object> map = null;

		//fill in all the other input option
		setOptions(getConfiguration());
		if(getConfiguration().containsKey("mergetype")) getConfiguration().setKey("operation", "merge");

		//get AAR data-source
		dataSource = getSource();

		if(dataSource != null && dataSource instanceof RemedySource)
		{
			dataSource.init();
			RuntimeLogger.debug("Get data-source: " + dataSource);

			//get AAR data target
			dataTarget = getTargetEntityFormName();
		}
		else
		{
			dataSource = null;
			dataTarget = null;

			//connect to source system
			remoteConnection = getRemoteServerConnection();
			RuntimeLogger.debug("Get remote connection (to the data-source): " + remoteConnection);
		}

		//validation in case of target is not specified
		if(!getConfiguration().containsKey("formname") && !getConfiguration().containsKey("form") && dataTarget == null)
		{
			getConfiguration().setKey("formname", getConfiguration().getString("remoteform", getConfiguration().getString("remoteformname", null)));
			getConfiguration().setKey("automap", new Boolean(true));
		}

		//get mapping structure
		 map = getMapping();

		//process workflow
		process(map);

		//clock stopped
		getCron().stop();
		setOutputMessage();
	}

	/**
	 * Here is the logic of the entire data import (migration) workflow.
	 *
	 * @param map data mapping between source and destination
	 * These file will include in the velocity context <code>source</code> object and <code>target</code> data structure.
	 * @throws org.areasy.runtime.engine.base.AREasyException in case of any error occurs
	 */
	protected final void process(Map<Integer, Object> map) throws AREasyException
	{
		boolean nextLoop = true;
		String transformationContent = null;

		CoreItem source = null;
		CoreItem target = null;

		//take input parameters
		int limit = getConfiguration().getInt("limit", 0);
		Boolean force = getConfiguration().getBoolean("force", false);
		List lookupData = getConfiguration().getList("lookupdata", null);
		String operation = getConfiguration().getString("operation", "commit");

		//take transformation content
		String transformationFile = getConfiguration().getString("transformationfile", null);
		if(transformationFile != null) transformationContent = getInputStream(new File(transformationFile));
		if(transformationContent == null) transformationContent = getVelocityTransformationScript();

		//validate operation options
		if(!StringUtility.equals(operation, "merge") && !StringUtility.equals(operation, "commit"))
		{
			RuntimeLogger.error("Operation mode is unknown: " + operation);
			return;
		}

		//initialize data dictionary for ETL
		initDictionary();

		do
		{
			//get source
			source = getNextSourceObject();

			//if source object is not null processing it
			if(source != null)
			{
				//increment number of records processed
				setRecordsCounter();

				try
				{
					//get target structure
					target = getTargetStructure(source);

					//handle first part of operation call (commit)
					if(StringUtility.equals(operation, "commit"))
					{
						applyTargetLookup(target, lookupData, source);
					}

					//apply mapping between source and target
					applyMapping(target, source, map);

					//data transformation
					if(transformationContent != null)
					{
						initContext();
						getContext().put("target", target);
						getContext().put("source", source);

						String output = ProcessorLevel0Reader.parseText(getContext(), transformationContent);
						logger.debug("Transformation results: " + output);
						setNullContext();
					}

					//execute data development workflow
					develop(source, target, map, operation);
				}
				catch(Throwable th)
				{
					setErrorsCounter();

					String errorMsg = "Error running action for '" + getRecordsCounter() + "' record: ";
					errorMsg += th.getMessage() + ". Target: " + target + ". Source: " + source;

					RuntimeLogger.error(errorMsg);
					logger.debug("Exception", th);

					if(!force) nextLoop = true;
				}
				finally
				{
					if(getConfiguration().getBoolean("cleanup", true)) cleanup(target);
				}

				//evaluate cycle limitation
				if(limit > 0 && limit <= getRecordsCounter()) nextLoop = false;

				// check interruption and and exit if the execution was really interrupted
				if(isInterrupted())
				{
					RuntimeLogger.warn("Execution interrupted by user");
					return;
				}
			}
			else nextLoop = false;
		}
		while(nextLoop);
	}

	/**
	 * This is the processing method for data development action. Actually this is the transactional engine
	 *
	 * @param source source <code>CoreItem</code> structure
	 * @param target target <code>CoreItem</code> structure
	 * @param map the map structure between source and target
	 * @param operation operation that is asked to be executed
	 * @throws AREasyException in case of any error occurs
	 */
	public void develop(CoreItem source, CoreItem target, Map map, String operation) throws AREasyException
	{
		//handle final operation call.
		if(StringUtility.equals(operation, "merge"))
		{
			execBeforeMerge(source, target);

			//get merge rules
			int mergeId = getMergeTypeAndOptions(getConfiguration());
			List mergeQualList = getConfiguration().getList("mergematchingfieldids", null);

			//execute merge
			target.merge(getServerConnection(), mergeId, mergeQualList);
		}
		else if(StringUtility.equals(operation, "commit"))
		{
			execBeforeCommit(source, target);

			if(target.exists()) target.update(getServerConnection());
				else target.create(getServerConnection());
		}

		RuntimeLogger.debug("Data development: " + target);

		if(getConfiguration().getBoolean("multipart", false) && target instanceof MultiPartItem)
		{
			((MultiPartItem)target).commitParts(getServerConnection());
		}
	}

	/**
	 * Create Velocity context. This is an extended context that include all standard objects plus <b>dictionary</b>
	 * object that if a Map object that other Maps with all dictionary valued described in dictionary file.
	 */
	protected void initContext()
	{
		super.initContext();

		if(dictionary != null) getContext().put("dictionary", dictionary);
	}

	/**
	 * Detect and initialize the data dictionary for ETL
	 */
	private void initDictionary()
	{
		//take transformation content
		String dictionaryFileName = getConfiguration().getString("dictionaryfile", null);

		if(dictionaryFileName != null)
		{
			RuntimeLogger.debug("Load dictionary: " + dictionaryFileName);

			int counter = 0;
			boolean stop = false;
			String dictionaryKey = null;
			dictionary = new HashMap();

			if(getConfiguration().containsKey("dictionarykey")) dictionaryKey = getConfiguration().getKey("dictionarykey").toString();
				else dictionaryKey = MultiPartItem.partVarStart + "A" + MultiPartItem.partVarEnd;

			try
			{
				//initialization
				ParserEngine parser = new ParserEngine(getServerConnection(), getManager().getConfiguration(), getConfiguration());
				parser.setResource("parserfile", dictionaryFileName);
				parser.setResource("parsertype", "file");
				parser.setResource("startindex", getConfiguration().getInt("dictionarystartindex", 0));
				parser.setResource("endindex", getConfiguration().getInt("dictionaryendindex", 0));
				parser.setResource("pageindex", getConfiguration().getInt("dictionarypageindex", 0));

				parser.init();

				do
				{
					//get data from parser engine
					String data[] = parser.read();
					Configuration config = new BaseConfiguration();

					//validate data
					if(!isDataEmpty(data))
					{
						for(int i = 0; i < data.length; i++)
						{
							String index = (i + 1) + "";
							String column = ProcessorLevel0Reader.getGenericColumnFromIndex(i);

							config.addKey(index, data[i]);
							config.addKey(column, data[i]);
						}

						config.addKey("index", String.valueOf(counter));
						config.addKey("key", dictionaryKey);

						String key = config.getString("key", null);
						if(StringUtility.isEmpty(key)) key = config.getString("index", null);
							else key = StringUtility.trim(key);

						dictionary.put(key, config);
						counter++;
					}
					else stop = true;
				}
				while(!stop);
			}
			catch(Throwable th)
			{
				RuntimeLogger.error("Error reading dictionary data file: " + th.getMessage());
				logger.debug("Exception", th);
			}

			if(counter > 0) RuntimeLogger.info("Load " + counter + " data record(s) in dictionary");
		}
	}

	/**
	 * Custom preparation for MERGE action.
	 *
	 * @param sourceOrig source structure
	 * @param targetOrig target structure
	 */
	protected void execBeforeMerge(CoreItem sourceOrig, CoreItem targetOrig)
	{
		//nothing to do here
	}

	/**
	 * Custom preparation for COMMIT action.
	 *
	 * @param sourceOrig source structure
	 * @param targetOrig target structure
	 */
	protected void execBeforeCommit(CoreItem sourceOrig, CoreItem targetOrig)
	{
		//nothing to do here
	}

	protected Map<Integer, Object> getMapping() throws AREasyException
	{
		Map<Integer, Object> map = new Hashtable<Integer, Object>();

		if(dataSource == null)
		{
			Boolean automap = getConfiguration().getBoolean("automap", true);
			String mappingfile = getConfiguration().getString("mappingfile", null);
			List addmaps = getConfiguration().getList("addmaps", null);
			List delmaps = getConfiguration().getList("delmaps", null);

			//include the external mapping
			if(mappingfile != null)
			{
				setExternalMapping(map, mappingfile);
				if(!getConfiguration().containsKey("automap")) automap = false;
			}

			//get auto-mapping
			if(automap) map = getAutoMap();

			//add the command line mapping
			if(addmaps != null) addMappings(map, addmaps);

			//remove exceptions
			if(delmaps != null) delMappings(map, delmaps);
		}
		else
		{
			List lookupdata = new Vector();
			List jobMapping = getMappingEntries();
			if(jobMapping == null || jobMapping.isEmpty()) throw new AREasyException("No data mapping found!");

			for(int i = 0; i < jobMapping.size(); i++)
			{
				CoreItem item = (CoreItem)jobMapping.get(i);

				String targetStringId = item.getStringAttributeValue(536870950);

				Integer sourceType = (Integer) item.getAttributeValue(536870917);
				String sourceStringId = item.getStringAttributeValue(536870924);
				String sourceStringName = item.getStringAttributeValue(536870914);

				Integer mapEntity = (Integer) item.getAttributeValue(536870920);
				Integer mapType = (Integer) item.getAttributeValue(536870919);

				if(mapEntity != 2)
				{
					if(mapType == 0)
					{
						Integer targetId = new Integer(NumberUtility.toInt(targetStringId));

						if(sourceType == null)
						{
							Integer sourceId = new Integer(NumberUtility.toInt(sourceStringId));
							if(!map.containsKey(targetId)) map.put(targetId, sourceId);
						}
						else if(sourceType == 0)
						{
							if(!map.containsKey(targetId)) map.put(targetId, sourceStringName);
						}
						else if(sourceType == 1)
						{
							if(!map.containsKey(targetId)) map.put(targetId, getConfiguration().getKey(sourceStringName));
						}
					}
					else if(mapType == 1 && sourceType == null)
					{
						lookupdata.add(sourceStringId);
					}
				}
				else
				{
					if(sourceType == null)
					{
						getConfiguration().setKey(targetStringId, MultiPartItem.partVarStart + FDATA + sourceStringId + MultiPartItem.partVarEnd);
					}
					else if(sourceType == 0)
					{
						getConfiguration().setKey(targetStringId, sourceStringName);
					}
					else if(sourceType == 1)
					{
						getConfiguration().setKey(targetStringId, getConfiguration().getKey(sourceStringName));
					}

				}
			}

			//if lookupdata exist put it in configuration
			if(!lookupdata.isEmpty()) getConfiguration().setKey("lookupdata", lookupdata);
		}

		return map;
	}

	public void setOutputMessage()
	{
		RuntimeLogger.add("Data Development Results:");
		RuntimeLogger.add("\tSource: " + (remoteConnection != null ? remoteConnection.getServerName() : dataSource != null ? dataSource : "localhost"));
		RuntimeLogger.add("\tDestination: " + (getServerConnection() != null ? getServerConnection().getServerName() : "localhost"));
		RuntimeLogger.add("\tTotal number of records: " + getRecordsCounter());
		RuntimeLogger.add("\tNumber of errors: " + getErrorsCounter());
		RuntimeLogger.add("\tExecution time: " + getCronTime());
		RuntimeLogger.add("");
	}

	/**
	 * Initiate de return target data structure that have to be submitted after reconciliation
	 *
	 * @param source the target structure could use details from source (form name or class id)
	 * @return <code>HybridConfigurationItem</code> structure
	 * @throws org.areasy.runtime.engine.base.AREasyException is any error will occur
	 */
	protected CoreItem getTargetStructure(CoreItem source) throws AREasyException
	{
		CoreItem target = getEntity();

		if(dataTarget != null)
		{
			target.setFormName(dataTarget);
		}
		else
		{
			String formName = getConfiguration().getString("form", getConfiguration().getString("formname", null));
			if(formName != null) target.setFormName(formName);
			if(target.getFormName() == null) target.setFormName(source.getFormName());
		}

		//validate forms
		if(target.getFormName() == null) throw new AREasyException("Target form name is null");

		return target;
	}

	protected CoreItem getSourceStructure() throws AREasyException
	{
		CoreItem source = getEntity();

		if(dataSource != null)
		{
			source.setFormName(dataSource.getSourceItem().getStringAttributeValue(536870980));
		}
		else
		{
			String remoteFormName = getConfiguration().getString("remoteform", getConfiguration().getString("remoteformname", null));
			if(remoteFormName != null) source.setFormName(remoteFormName);
		}

		//validate forms
		if(source.getFormName() == null) throw new AREasyException("Source form name is null");

		return source;
	}

	/**
	 * This method will prepare target data structure for mapping and then for transaction. Actually, this method
	 * will check if the data structure instance is already exists or not.
	 *
	 * @param target target data structure
	 * @param lookupData the list containing all attributes that have to be use to find the target instance.
	 * @param source data source object
	 * @throws org.areasy.runtime.engine.base.AREasyException is any error will occur
	 */
	protected void applyTargetLookup(CoreItem target, List lookupData, Object source) throws AREasyException
	{
		if(target == null || source == null)throw new AREasyException("Target or source object is null");

		if(lookupData == null)
		{
			lookupData = new Vector();
			lookupData.add("1");
		}

		for(int x  = 0; x < lookupData.size(); x++)
		{
			String stringFieldId = (String) lookupData.get(x);
			Integer intFieldId = NumberUtility.toInt(stringFieldId);
			Object sourceValue = ((CoreItem)source).getAttributeValue(intFieldId);

			if(intFieldId > 0) target.setAttribute(intFieldId, sourceValue);
				else RuntimeLogger.debug("Lookup field ignored: " + stringFieldId);
		}

		//read target data for commit operation.
		target.read(getServerConnection());
	}

	/**
	 * Get next found source object to be processed and transformed into a target object
	 *
	 * @return source object found in source repository
	 * @throws org.areasy.runtime.engine.base.AREasyException is any error will occur
	 */
	protected CoreItem getNextSourceObject() throws AREasyException
	{
		if(dataSource != null)
		{
			Map map = dataSource.getNextObject(null);

			if(map != null && !map.isEmpty())
			{
				CoreItem source = getSourceStructure();
				source.setDefaultData(map);

				Iterator iterator = map.keySet().iterator();
				while(iterator != null && iterator.hasNext())
				{
					String key = (String) iterator.next();
					Object value = map.get(key);

					getConfiguration().setKey(FDATA + key, value);
				}

				return source;
			}
			else return null;
		}
		else
		{
			if(searchList == null)
			{
				//qualification for the current session
				String sessionQualification = null;

				//get input details to generate session qualification criteria
				String requestIdQualification = null;
				int chunksize = getConfiguration().getInt("chunksize", 100);
				String remoteQualification = getConfiguration().getString("remotequalification", null);

				//translate qualification
				if(remoteQualification != null) remoteQualification = getTranslatedQualification(remoteQualification);

				//build part of qualification which should delivered next chunks.
				if(lastRequestId != null) requestIdQualification = "'1' > \"" + lastRequestId + "\"";

				//build the complete qualification
				if(remoteQualification != null)
				{
					if(requestIdQualification != null) sessionQualification = "(" + remoteQualification + ") AND (" + requestIdQualification + ")";
						else sessionQualification = remoteQualification;
				}
				else
				{
					if(requestIdQualification != null) sessionQualification =  requestIdQualification;
						else sessionQualification = "'1' != $NULL$";
				}

				//search data
				CoreItem source = getSourceStructure();

				//set sort-order by and type
				if(getConfiguration().containsKey("sortorderby"))
				{
					int sortType = Constants.AR_SORT_ASCENDING;
					if(StringUtility.equalsIgnoreCase(getConfiguration().getString("sortordertype", "asc"), "desc")) sortType = Constants.AR_SORT_DESCENDING;

					SortInfo sortInfo = new SortInfo(getConfiguration().getInt("sortorderby", 1), sortType);
					source.setSortInfo(sortInfo);
				}
				searchList = source.search(getRemoteServerConnection(), sessionQualification, chunksize);
			}

			//take the object and maintain the search session details.
			if(searchList != null && sourceIndex < searchList.size())
			{
				//obtain source object
				CoreItem source = (CoreItem) searchList.get(sourceIndex);

				sourceIndex++;
				if(source != null)
				{
					lastRequestId = source.getEntryId();

					//validate sourceIndex
					if(searchList.size() == sourceIndex)
					{
						//make null original source for a new search
						searchList = null;

						//reset sourceIndex
						sourceIndex = 0;
					}
				}

				return source;
			}
			else return null;
		}
	}

	/**
	 * Dedicated method to be used in <code>AutoMap</code> procedure to filter different attributes.
	 *
	 * @param source <code>CoreItem</code> source structure
	 * @param target <code>CoreItem</code> target structure
	 * @param remoteField <code>Field</code> structure from source that have to be compared of evaluated
	 * @param field <code>Field</code> structure from target that have to be compared of evaluated
	 * @return true if the fields have to be skipped (filtered to not be part of the mapping)
	 */
	protected boolean hasAutomapFilter(CoreItem source, Field remoteField, CoreItem target, Field field)
	{
		return false;
	}

	/**
	 * This method create automatically the mapping between source and target using field Id and field type.
	 * @return a <code>Map</code> structure where the key is the target system and the value is the source environment
	 * @throws org.areasy.runtime.engine.base.AREasyException is any error will occur
	 */
	public Map<Integer,Object> getAutoMap() throws AREasyException
	{
		CoreItem source = getSourceStructure();
		CoreItem target = getTargetStructure(source);
		Map<Integer, Object> map = new Hashtable<Integer, Object>();

		//validate destination server connection
		if(getServerConnection() == null) throw new AREasyException("Target connection is null");

		//validate source server connection
		if(getRemoteServerConnection() == null) throw new AREasyException("Source connection is null");

		try
		{
			List<Field> fields = getRemoteServerConnection().getContext().getListFieldObjects(source.getFormName(), Constants.AR_FIELD_TYPE_DATA);

			for(int i = 0; fields != null && i < fields.size(); i++)
			{
				Field remoteField = null;
				Field field = null;

				try
				{
					remoteField = fields.get(i);
					field = getServerConnection().getContext().getField(target.getFormName(), remoteField.getFieldID());

					//apply filters
					if(hasAutomapFilter(source, remoteField, target, field)) continue;

					if(field != null)
					{
						if(field.getDataType() != remoteField.getDataType())
						{
							RuntimeLogger.debug("Field id '" + remoteField.getFieldID() + "' has been ignored because has a different data type");
							continue;
						}

						if(!map.containsKey(field.getFieldID())) map.put(field.getFieldID(), remoteField.getFieldID());
					}
				}
				catch(Throwable th)
				{
					String message = "Processing mapping: " + (remoteField != null ? remoteField.getFieldID() + "/" + remoteField.getName() : "source field is null") + " - " +
												(field != null ? field.getFieldID() + "/" + field.getName() : "target field is null") + ". " + th.getMessage();

					RuntimeLogger.debug(message);
					//logger.debug("Exception", th);
				}
			}
		}
		catch(Throwable th)
		{
			throw new AREasyException("Error processing source fields: " + th.getMessage(), th);
		}

		return map;
	}

	/**
	 * Add new entries to the found mapping.
	 *
	 * @param map found mapping
	 * @param mappings list of new entries
	 */
	public void addMappings(Map<Integer, Object> map, List mappings)
	{
		//validate mapping
		if(map == null) return;

		//validate new fields mapping
		if(mappings == null || mappings.isEmpty()) return;

		for(int i = 0; i < mappings.size(); i++)
		{
			String value = (String) mappings.get(i);
			int index = value.indexOf("=", 0);

			if(index > 0)
			{
				String destString = value.substring(0, index).trim();
				Integer destInteger = NumberUtility.toInt(destString, 0);

				if(destInteger > 0)
				{
					String sourceString = value.substring(index + 1).trim();

					if(sourceString.startsWith("constant:"))
					{
						String sourceValue = sourceString.substring("constant:".length());
						map.put(destInteger, sourceValue);
					}
					else
					{
						Integer sourceInteger = NumberUtility.toInt(sourceString, 0);

						if(sourceInteger > 0) map.put(destInteger, sourceInteger);
							else RuntimeLogger.debug("Source field Id format is not defined: " + value);
					}
				}
				else RuntimeLogger.debug("Destination field Id format is not defined: " + value);
			}
			else RuntimeLogger.debug("Mapping string doesn't have a proper format: " + value);
		}
	}

	/**
	 * Delete entries from the found mapping
	 *
	 * @param map found mapping
	 * @param mappings entries to be deleted
	 */
	public void delMappings(Map<Integer, Object> map, List mappings)
	{
		//validate mapping
		if(map == null) return;

		//validate new fields mapping
		if(mappings == null || mappings.isEmpty()) return;

		for(int i = 0; i < mappings.size(); i++)
		{
			String destString = (String) mappings.get(i);
			Integer destInteger = NumberUtility.toInt(destString, 0);

			if(destInteger > 0) map.remove(destInteger);
				else RuntimeLogger.debug("Destination field Id format is not defined: " + destString);
		}
	}

	public void setExternalMapping(Map<Integer, Object> map, String mappingfile)
	{
		ParserEngine parser = null;

		//validate mapping
		if(map == null) return;

		//validate new fields mapping
		if(mappingfile == null) return;

		try
		{
			//initialization
			parser = new ParserEngine(getServerConnection(), getManager().getConfiguration(), getConfiguration());
			parser.setResource("parserfile", mappingfile);
			parser.setResource("parsertype", "file");

			parser.init();
		}
		catch(Throwable th)
		{
			RuntimeLogger.error("Error reading mapping data source file: " + th.getMessage());
			logger.debug("Exception", th);

			return;
		}

		boolean empty = false;

		do
		{
			String data[] = null;

			try
			{
				//get data from parser engine
				RuntimeLogger.reset();
				data = parser.read();
				RuntimeLogger.clearData();

				//validate data
				if(!isDataEmpty(data))
				{
					if(data.length < 2)
					{
						RuntimeLogger.error("Data extracted from line '" + parser.getParser().getCurrentIndex() + "' doesn't have a proper format");
						continue;
					}

					String destString = data[1];
					Integer destInteger = NumberUtility.toInt(destString, 0);

					if(destInteger > 0)
					{
						String sourceString = data[0];
						boolean constant = false;

						if(data.length == 3) constant = BooleanUtility.toBoolean(data[2]);

						if(constant)
						{
							map.put(destInteger, sourceString);
						}
						else
						{
							if(sourceString.startsWith("constant:"))
							{
								String sourceValue = sourceString.substring("constant:".length());
								map.put(destInteger, sourceValue);
							}
							else
							{
								Integer sourceInteger = NumberUtility.toInt(sourceString, 0);

								if(sourceInteger > 0) map.put(destInteger, sourceInteger);
									else RuntimeLogger.debug("Source field Id format is not defined: " + sourceString);
							}
						}
					}
					else RuntimeLogger.debug("Destination field Id format is not defined: " + destString);
				}
				else empty = true;
			}
			catch(Throwable th)
			{
				String errorMsg = "Error reading data extracted from line '" + parser.getParser().getCurrentIndex() + "'";
				errorMsg += ": " + th.getMessage();

				RuntimeLogger.error(errorMsg);
				getLogger().debug("Exception", th);
			}
		}
		while(!empty);
	}

	/**
	 * Dedicated method to apply data mapping and to prepare target data structure for transaction
	 *
	 * @param target <code>CoreItem</code> data structure
	 * @param source source object
	 * @param map data mapping between source and target
	 * @throws org.areasy.runtime.engine.base.AREasyException is any error will occur
	 */
	protected void applyMapping(CoreItem target, Object source, Map<Integer, Object> map) throws AREasyException
	{
		if(target == null || source == null) throw new AREasyException("Target or source object is null");

		//read all related sub-structure in case of the structure instance is 'Related'
		if(dataSource != null && dataSource instanceof RemedySource)
		{
			//add custom multiparts
			setCustomMultiParts((MultiPartItem)source, ((RemedySource)dataSource).getTargetServerConnection());
		}
		else
		{
			//add custom multiparts
			setCustomMultiParts((MultiPartItem)source, getRemoteServerConnection());
		}
	}

	/**
	 * Set into a specific <code>CoreItem</code> structure multi-part forms.
	 *
	 * @param entry a specific <code>CoreItem</code> structure
	 */
	public void setCustomMultiParts(MultiPartItem entry, ServerConnection connection) throws AREasyException
	{
		boolean multipart = getConfiguration().getBoolean("multipart", false);
		String partforms[] = getConfiguration().getStringArray("partforms", null);
		String partkeys[] = getConfiguration().getStringArray("partkeys", null);

		for(int i = 0; partforms != null && multipart && i < partforms.length; i++)
		{
			String partCode = null;
			int index = partforms[i].indexOf(MultiPartItem.partSeparator, 0);

			if(index > 0)
			{
				partCode = partforms[i].substring(0, index);
				String formname = partforms[i].substring(index + 1);

				//create part instance
				CoreItem customPart = new CoreItem();
				customPart.setFormName(formname);

				Iterator ids = getConfiguration().getKeys();

				//put query fields
				while(ids != null && ids.hasNext())
				{
					String id = (String)ids.next();
					String partId = FQUERY + partCode;

					if(id != null && id.startsWith(partId))
					{
						index = id.indexOf(MultiPartItem.partSeparator, 0);

						if(index > 0)
						{
							String partKey = id.substring(index + MultiPartItem.partSeparator.length());
							Object partValue = getConfiguration().getKey(id);

							customPart.setAttribute(partKey, partValue);
						}
					}
				}

				//simaple qualification validation
				if(customPart.getAttributes().size() == 0)
				{
					RuntimeLogger.warn("No qualification criteria found for part '" + partCode + "'. It will be skipped.");
					continue;
				}

				//take and register custom parts
				List list = entry.searchPreparedParts(connection, customPart);

				if(list != null && !list.isEmpty())
				{
					List<DefaultKeyValue> keypairs = new Vector<DefaultKeyValue>();

					for(int x = 0; partkeys != null && x < partkeys.length; x++)
					{
						index = partkeys[x].indexOf(MultiPartItem.partSeparator, 0);
						String matchPart = null;

						if(index > 0)
						{
							matchPart = partkeys[x].substring(0, index);

							if(StringUtility.equals(matchPart, partCode))
							{
								String keyValues[] = StringUtility.split(partkeys[x].substring(index + MultiPartItem.partSeparator.length()), '=');

								if(matchPart != null && keyValues != null && keyValues.length == 2);
								{
									DefaultKeyValue key = new DefaultKeyValue(new Integer(NumberUtility.toInt(keyValues[0])), new Integer(NumberUtility.toInt(keyValues[1])));
									keypairs.add(key);
								}
							}
						}
					}

					//register each part
					for(int x = 0; x < list.size(); x++)
					{
						CoreItem item = (CoreItem) list.get(x);

						entry.addPartWithPrefix(partCode, item);

						for(int y = 0; y < keypairs.size(); y++)
						{
							DefaultKeyValue key = keypairs.get(y);
							entry.addPartKeyPairWithPrefix(partCode, item, (Integer) key.getKey(), (Integer) key.getValue());
						}
					}
				}
			}
		}
	}

	public ServerConnection getRemoteServerConnection() throws AREasyException
	{
		if(this.remoteConnection == null) remoteConnection = super.getRemoteServerConnection();

		return this.remoteConnection;
	}

	public String getDataTarget()
	{
		return dataTarget;
	}

	public AbstractSource getDataSource()
	{
		return dataSource;
	}

	/**
	 * Get answer stream.
	 *
	 * @param fileIn input file
	 * @return stream data content
	 */
	protected String getInputStream(File fileIn)
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

				content = new String(output.toByteArray());
			}
			catch (Exception e)
			{
				logger.error("Error reading input stream: " + e.getMessage());
				logger.debug("Exception", e);

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
}
