package org.areasy.runtime.actions.arserver.data.tools.flow.events;

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

import org.areasy.runtime.RuntimeAction;
import org.areasy.runtime.RuntimeManager;
import org.areasy.runtime.actions.data.BaseData;
import org.areasy.runtime.actions.data.CoreData;
import org.areasy.runtime.engine.RuntimeLogger;
import org.areasy.runtime.engine.base.ARDictionary;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.runtime.engine.base.ServerConnection;
import org.areasy.runtime.engine.structures.CoreItem;
import org.areasy.runtime.engine.structures.data.cmdb.ConfigurationItem;
import org.areasy.runtime.engine.workflows.ProcessorLevel0Reader;
import org.areasy.common.data.DateUtility;
import org.areasy.common.data.NumberUtility;
import org.areasy.common.data.StringUtility;
import org.areasy.common.support.configuration.Configuration;
import org.areasy.common.support.configuration.ConfigurationException;
import org.areasy.common.support.configuration.base.BaseConfiguration;
import org.areasy.common.support.configuration.providers.properties.stream.PropertiesConfiguration;
import org.areasy.common.parser.csv.CsvWriter;
import org.areasy.common.velocity.context.Context;
import org.areasy.common.velocity.context.VelocityContext;

import java.io.File;
import java.util.*;

/**
 * Dedicated action to perform data import workflow.
 */
public class RunDataWorkflowEvent extends AbstractEvent
{
	/** The list of action which must be executed */
	private List actions = new Vector();

	/** The common configuration which must be appended to all actions */
	private Configuration commonConfig = new BaseConfiguration();

	/** Transformation file for scripting */
	private String transformationScript = null;

	/** In case of you want to post data into a remote server will be used this structure */
	private ServerConnection runnerConnection = null;

	/** Dedicated stream for in/out log */
	private CsvWriter inoutWriter = null;
	private String inoutFileName = null;

	/** Private maps that could contains data maps used to data conversion and validation */
	private Map<String, Dictionary> maps = null;

	/**
	 * Execute event
	 *
	 * @throws org.areasy.runtime.engine.base.AREasyException if any error occurs
	 */
	public void execute() throws AREasyException
	{
		// validate data-source
		if(getSource() == null) throw new AREasyException("Data-source is null. Check if -instanceid option has been specified and defined!");

		//validate job entry structure
		if(!getAction().getJobEntry().exists()) throw new AREasyException("Runtime job entry couldn't be found!");
		if(!StringUtility.equals(getAction().getJobEntry().getStringAttributeValue(536871088), getAction().getCode())) throw new AREasyException("Invalid runtime job signature: " + getAction().getJobEntry().getStringAttributeValue(536871088));

		//get action's list. Here are include action instances.
		setRequestedActions();

		//get common configuration coming from selected options.
		setRequestedOptions();

		//get job mapping
		List jobMapping = getAction().getMappingEntries();
		if(jobMapping == null || jobMapping.isEmpty()) throw new AREasyException("No data mapping found!");

		//execute action
		process(jobMapping);

		//set output (message)
		getAction().setOutputMessage();
	}

	protected void process(List jobMapping) throws AREasyException
	{
		//get data maps
		if(getAction().getConfiguration().containsKey("datamaps"))
		{
			maps = new HashMap();
			String[] datamaps = getAction().getConfiguration().getStringArray("datamaps", null);

			for(int i = 0; datamaps != null && i < datamaps.length; i++)
			{
				String parts[] = StringUtility.split(datamaps[i], '@');

				if(parts != null && parts.length == 2)
				{
					File objectfile = new File(parts[1]);

					if(!objectfile.exists())
					{
						objectfile = new File(RuntimeManager.getWorkingDirectory(), parts[1]);

						if(!objectfile.exists())
						{
							objectfile = new File(RuntimeManager.getCfgDirectory(), parts[1]);
						}
					}

					if(objectfile.exists())
					{
						maps.put(parts[0], new Dictionary(objectfile));
						RuntimeLogger.debug("Data dictionary '" + parts[0] + "' has been loaded");
					}
					else RuntimeLogger.warn("File doesn't exist for map '" + parts[0] + "': " + parts[1]);
				}

			}
		}

		//get custom transformation rules
		Configuration sectorConfig = getAction().getManager().getConfiguration().subset("app.runtime.action." + getAction().getCode());
		List<String> generalRules = sectorConfig.getList("workflow.rules", new Vector());

		//get number of maxim consecutive errors
		int maxConsecutive = getAction().getConfiguration().getInt("maxconsecutiveerrors", sectorConfig.getInt("workflow.maxconsecutive.errors", 25));
		int limit = getAction().getConfiguration().getInt("limit", 0);
		int consecutiveCounter = 0;

		boolean force = getAction().getConfiguration().getBoolean("force", false);
		boolean nextLoop = true;

		//get list of keys and initialize inout logger
		List keys = getMappingSourceKeys(jobMapping);
		setInOutLog(keys);

		do
		{
			CoreItem target = null;
			Context context = null;

			try
			{
				//get data from data-source, mapped to the source keys.
				Map sourceMap = getSource().getNextObject(keys);

				//if source object is not null processing it
				if(sourceMap != null)
				{
					//increment number of records processed
					getAction().setRecordsCounter();

					if(getAction().getConfiguration().containsKey("inoutlog"))
					{
						//write data in log file
						setInOutLogData(keys, sourceMap);
					}
					else
					{
						logger.debug("Processing record [" + getAction().getRecordsCounter() + "] = " + sourceMap);

						//execute actions
						for(int i = 0; actions != null && i < actions.size(); i++)
						{
							RuntimeAction action = (RuntimeAction) actions.get(i);
							logger.debug("Processing Step 1 - Found action: " + action);

							if(action != null && action instanceof CoreData)
							{
								//get action's specific configuration
								Configuration config = getActionConfiguration(action.getCode(), jobMapping, sourceMap);
								context = getContext();

								//initialize action.
								action.init(config, getAction().getManager(), getRunnerServerConnection());
								if(target == null) target = getTargetStructure(context, config, (CoreData)action);
								logger.debug("Processing Step 2 - Action initialized and Target created: " + target);

								//run data transformation before commit - apply scripting data transformation
								boolean skip = setTransformation(context, target, action, config, sectorConfig, generalRules, transformationScript, "COMMIT");

								//run action (if wasn't skipped by Velocity injection)
								if(!skip)
								{
									((CoreData)action).run(target);
									logger.debug("Processing Step 3 - Action executed");
								}
								else RuntimeLogger.debug("Action '" + action.getCode() + "' has been skipped because of data transformation script attached to the job definition. The following target structure has been skipped: " + target);

								//run data transformation after commit - apply scripting data transformation
								setTransformation(context, target, config, transformationScript, "AFTERCOMMIT");
								logger.debug("Processing Step 4 - Transformation after Run Action");

								//close action
								action.close();
							}
							else
							{
								nextLoop = false;
								throw new AREasyException("Action is null or doesn't have the proper format accepted used by Advanced Automation Runtime: " + (action != null ? action.getCode() : null));
							}
						}
					}
				}
				else nextLoop = false;

				//reset counter for the consecutive errors
				consecutiveCounter = 0;
			}
			catch(Throwable th)
			{
				getAction().setErrorsCounter();

				String errorMsg = "Error running action for '" + getAction().getRecordsCounter() + "' record: ";
				errorMsg += th.getMessage();

				if(errorMsg.endsWith(".")) errorMsg += " " + (target != null ? "Target entity managed by the current transaction: " + target : "");
					else errorMsg += ". " + (target != null ? "Target entity managed by the current transaction: " + target : "");

				RuntimeLogger.error(errorMsg);

				logger.error(errorMsg);
				logger.debug("Exception", th);

				if(!force && nextLoop) nextLoop = false;

				if(nextLoop)
				{
					consecutiveCounter++;

					if(consecutiveCounter >= maxConsecutive && maxConsecutive > 0)
					{
						nextLoop = false;
						RuntimeLogger.error("Too many consecutive errors met during job processing (" + consecutiveCounter + " errors)");
					}
				}
			}
			finally
			{
				//cleanup target details
				if(getAction().getConfiguration().getBoolean("cleanup", true)) getAction().cleanup(target);

				//release and destroy Velocity context
				if(context != null) setNullContext(context);
			}

			//evaluate cycle limitation
			if(limit > 0 && limit <= getAction().getRecordsCounter()) nextLoop = false;

			// check interruption and and exit if the execution was really interrupted
			if(getAction().isInterrupted())
			{
				RuntimeLogger.warn("Execution interrupted by user");
				return;
			}
		}
		while(nextLoop);

		//close in-out stream
		closeInOutLog();

		//close datamaps
		if(maps != null && !maps.isEmpty())
		{
			Iterator<String> iterator = maps.keySet().iterator();

			while(iterator != null && iterator.hasNext())
			{
				String object = iterator.next();
				Dictionary dictionary = maps.get(object);

				dictionary.close();
			}
		}
	}

	private void setTransformation(Context context, CoreItem target, Configuration actionConfig, String transformationScript, String mode) throws AREasyException
	{
		if(transformationScript == null) return;

		//set mode in context
		if(mode == null) context.put("mode", "");
			else context.put("mode", mode);

		//put in context target data structure
		if(target != null) context.put("target", target);

		//put in context source data structure
		if(actionConfig != null) context.put("config", actionConfig);

		//execute transformation script
		if(StringUtility.isEmpty(mode) || (StringUtility.isNotEmpty(mode) && transformationScript.contains("\"" + mode + "\"") &&
										   (transformationScript.contains("$mode") || transformationScript.contains("$!mode") || transformationScript.contains("$!{mode}"))))
		{
			String output = ProcessorLevel0Reader.parseText(context, transformationScript);

			if(StringUtility.isNotEmpty(output))
			{
				logger.debug("Simple transformation output: " + output);
				RuntimeLogger.debug("Simple transformation output: " + output);
			}
		}
	}

	private boolean setTransformation(Context context, CoreItem target, RuntimeAction action, Configuration actionConfig, Configuration sectorConfig, List<String> generalRules, String transformationScript, String mode) throws AREasyException
	{
		boolean skip = false;

		List<String> actionRules = sectorConfig.getList("workflow." + action.getCode() + ".rules", new Vector<String>());
		actionRules.addAll(generalRules);

		if(transformationScript == null &&(actionRules == null || actionRules.isEmpty())) return skip;

		//set mode in context
		if(mode == null) context.put("mode", "");
			else context.put("mode", mode);

		//set current action name in the context (it is called "runner")
		if(action != null) context.put("runner", action.getCode());

		//put in context target data structure
		if(target != null) context.put("target", target);

		//put in context source data structure
		if(actionConfig != null) context.put("config", actionConfig);

		//execute transformation script
		if(transformationScript != null)
		{
			String output = ProcessorLevel0Reader.parseText(context, transformationScript);

			if(StringUtility.isNotEmpty(output))
			{
				logger.debug("Data transformation returns: " + output);
				RuntimeLogger.debug("Data transformation returns: " + output);
			}

			skip = ProcessorLevel0Reader.evaluate(context, "$!skip==true");
		}

		//execute evaluation rules from the configuration
		for(int x = 0; generalRules != null && x < actionRules.size(); x++)
		{
			String rule = actionRules.get(x);
			String evalRule = sectorConfig.getString("workflow.rule." + rule + ".evaluation", null);
			String fieldIdRule = sectorConfig.getString("workflow.rule." + rule + ".fieldid", null);
			String fieldValueRule = sectorConfig.getString("workflow.rule." + rule + ".fieldvalue", null);

			if(StringUtility.isNotEmpty(fieldIdRule) && StringUtility.isNotEmpty(fieldValueRule))
			{
				boolean eval = evalRule == null || ProcessorLevel0Reader.evaluate(context, evalRule);

				if(eval)
				{
					try
					{
						fieldValueRule = ProcessorLevel0Reader.parseText(context, fieldValueRule);
						logger.debug("Data transformation rule '" + rule + "' returns: " + fieldValueRule);
						RuntimeLogger.debug("Data transformation rule '" + rule + "' returns: " + fieldValueRule);

						//set value
						if(NumberUtility.isNumber(fieldIdRule)) actionConfig.setKey(BaseData.FDATA + fieldIdRule, fieldValueRule);
							else actionConfig.setKey(fieldIdRule, fieldValueRule);
					}
					catch(Exception e)
					{
						RuntimeLogger.error("Exception evaluating rule '" + generalRules + "': " + e.getMessage());
						logger.debug("Exception", e);
					}
				}
			}
		}

		return skip;
	}

	/**
	 * Initiate de return target data structure that have to be submitted after reconciliation
	 *
	 * @param config action configuration
	 * @param action action will run detected structure
	 * @return <code>HybridConfigurationItem</code> structure
	 * @throws AREasyException is any error will occur
	 */
	protected CoreItem getTargetStructure(Context context, Configuration config, CoreData action) throws AREasyException
	{
		CoreItem target = null;
		String entity = config.getString("entity", null);

		if(entity != null)
		{
			target = getAction().getEntity(entity);

			if(getAction().getTargetEntityName() != null)
			{
				target.setFormName(getAction().getTargetEntityFormName());

				if(getAction().getTargetEntityType() == ARDictionary.TARGET_ENTITY_CLASS && target instanceof ConfigurationItem)
				{
					ConfigurationItem data = (ConfigurationItem) target;
					data.setClassId(getAction().getTargetEntityName());
				}
			}
		}
		else
		{
			if(getAction().getTargetEntityType() == ARDictionary.TARGET_ENTITY_CLASS || getAction().getConfiguration().getString("classid", null) != null)
			{
				String classid = getAction().getConfiguration().getString("classid", null);
				if(getAction().getTargetEntityName() != null) classid = getAction().getTargetEntityName();

				target = new ConfigurationItem();

				((ConfigurationItem)target).setClassId(classid);
				target.setFormName(getAction().getTargetEntityFormName());
			}
			else if(getAction().getTargetEntityName() != null || getAction().getConfiguration().getString("formname", null) != null)
			{
				String formName = getAction().getConfiguration().getString("formname", null);
				if(getAction().getTargetEntityName() != null) formName = getAction().getTargetEntityName();

				target = new CoreItem();
				target.setFormName(formName);
			}
		}

		//validate form name and read the data.
		if(target != null)
		{
			if(target.getFormName() == null) throw new AREasyException("Target form name is null");

			if(config.containsKey("ignorenullvalues")) target.setIgnoreNullValues(config.getBoolean("ignorenullvalues"));
			if(config.containsKey("ignoreunchangedvalues")) target.setIgnoreUnchangedValues(config.getBoolean("ignoreunchangedvalues"));
			if(config.containsKey("simplified")) target.setSimplifiedStructure(config.getBoolean("simplified"));
			if(config.getBoolean("simulation", false)) target.simulate();

			//transform query input
			setTransformation(context, target, config, transformationScript, "QUERY");

			//set query attributes
			boolean set = action.setQueryFields(target);

			//read item
			if(set) target.read(getRunnerServerConnection());
		}

		return target;
	}

	private Configuration getActionConfiguration(String actionName, List jobMapping, Map values)
	{
		Configuration config = new BaseConfiguration();
		config.merge(getAction().getConfiguration());

		String action = null;
		String classid = null;
		String formname = null;

		for(int i = 0; i < jobMapping.size(); i++)
		{
			CoreItem item = (CoreItem)jobMapping.get(i);

			int entity = (Integer) item.getAttributeValue(536870920);
			int type = (Integer) item.getAttributeValue(536870919);

			if(entity == 0) formname = item.getStringAttributeValue(536870951);
				else if(entity == 1) classid = item.getStringAttributeValue(536870951);
					else if(entity == 2) action = item.getStringAttributeValue(536870951);

			Object sourceMapType = item.getAttributeValue(536870917);
			String sourceKey = item.getStringAttributeValue(536870924);
			String targetKey = item.getStringAttributeValue(536870950);
			Object targetValue = null;

			if(sourceMapType == null) targetValue = values.get(sourceKey);
			else if( ((Integer)sourceMapType) == 0 ) targetValue = item.getStringAttributeValue(536870914);
			else if( ((Integer)sourceMapType) == 1 ) targetValue = config.getKey( item.getStringAttributeValue(536870914) );

			if(entity < 2)
			{
				if(type == 1) config.setKey(BaseData.FQUERY + targetKey, targetValue);
					else config.setKey(BaseData.FDATA  + targetKey, targetValue);
			}
			else if(entity == 2 && StringUtility.equals(action, actionName))
			{
				config.setKey(targetKey, targetValue);
			}
		}

		if(getAction().getTargetEntityType() == null)
		{
			if(classid != null) config.setKey("classid", classid);
			if(formname != null) config.setKey("formname", formname);
		}
		else
		{
			if(getAction().getTargetEntityType() == ARDictionary.TARGET_ENTITY_CLASS && getAction().getTargetEntityName() != null)
			{
				config.setKey("classid",getAction().getTargetEntityName());
				config.setKey("formname", getAction().getTargetEntityFormName());
			}
			else if(getAction().getTargetEntityType() == ARDictionary.TARGET_ENTITY_FORM)
			{
				config.setKey("formname", getAction().getTargetEntityName());
			}
		}

		return config;
	}

	public ServerConnection getRunnerServerConnection() throws AREasyException
	{
		if(this.runnerConnection == null)
		{
			if(getAction().getConfiguration().getBoolean("remoterunner", true))
			{
				runnerConnection = getAction().getRemoteServerConnection();
			}

			if(this.runnerConnection == null)
			{
				runnerConnection = getAction().getServerConnection();
			}
		}

		return this.runnerConnection;
	}

	private List getMappingSourceKeys(List jobMapping)
	{
		List keys = new Vector();

		for(int i = 0; i < jobMapping.size(); i++)
		{
			CoreItem item = (CoreItem)jobMapping.get(i);

			String keyName = item.getStringAttributeValue(536870924);
			Object mapType = item.getAttributeValue(536870917);

			if(mapType == null)
			{
				if(keyName != null) keys.add(keyName);
					else RuntimeLogger.debug("Mapping key name is null. Please check the mapping '" + item.getStringAttributeValue(536870914) + "' -> '" + item.getStringAttributeValue(536870990) + "'");
			}
		}

		return keys;
	}

	/**
	 * Create Velocity context.
	 *
	 * @return Velocity <code>Context</code>
	 */
	protected Context getContext()
	{
		//create context.
		Context context = new VelocityContext();

		//set skip flag in the current context
		context.put("skip", false);

		//put in context this library
		context.put("action", getAction());

		//put in context this library
		context.put("event", this);

		//put in context DateUtility library
		context.put("dates", new DateUtility());

		//put in context StringUtility library
		context.put("strings", new StringUtility());

		//put in context NumberUtility library
		context.put("numbers", new NumberUtility());

		//check maps
		if(maps != null && !maps.isEmpty())
		{
			Iterator<String> iterator = maps.keySet().iterator();

			while(iterator != null && iterator.hasNext())
			{
				String object = iterator.next();
				Dictionary dictionary = maps.get(object);

				context.put(object, dictionary);
			}
		}

		return context;
	}

	protected void setNullContext(Context context)
	{
		if(context != null)
		{
			context.clear();
			context = null;
		}
	}

	private void setRequestedOptions() throws AREasyException
	{
		//get common options
		getAction().setOptions(commonConfig);

		//merge common configuration with general action configuration
		if(!commonConfig.isEmpty()) getAction().getConfiguration().merge(commonConfig);

		//read transformation scripts
		transformationScript = getAction().getVelocityTransformationScript();
	}

	private void setRequestedActions() throws AREasyException
	{
		String actionIndex = null;
		int actionFields[]	= { 536870915, 536870916, 536870921, 536870919, 536870920, 536870918, 536870967, 536870917, 536870922, 536870948,
								536870943, 536870945, 536870947, 536870923, 536871094, 536871095, 536871099, 536871100, 536871101, 536871105 };

		for(int z = 1; z <= actionFields.length; z++)
		{
			if(z < 10) actionIndex = "action0" + String.valueOf(z);
				else actionIndex = "action"+ String.valueOf(z);

			Object value = getAction().getJobEntry().getAttributeValue(actionFields[z - 1]);

			if(value != null && value instanceof Integer && ((Integer)value) == 0)
			{
				String actionName = getAction().getManager().getConfiguration().getString("app.runtime.action." + getAction().getCode() + "." + actionIndex + ".name", null);
				String actionCatalog = getAction().getManager().getConfiguration().getString("app.runtime.action." + getAction().getCode() + "." + actionIndex + ".catalog", null);

				if(actionCatalog != null && actionName == null)
				{
					String data[] = StringUtility.split(actionCatalog, ";");

					if(data != null && data.length == 2) actionName = data[1];
						else if(data != null) actionName = data[0];
				}

				if(actionName == null) throw new AREasyException("Configuration error: Null action name for index: " + actionIndex);

				RuntimeAction action = getAction().getManager().getRuntimeAction(actionName);
				if(action == null) throw new AREasyException("Configuration error: Null action instance for name: " + actionName);

				int priority = getAction().getManager().getConfiguration().getInt("app.runtime.action." + getAction().getCode() + ".priority." + actionName, 0);

				if(actions.isEmpty())
				{
					actions.add(action);
				}
				else
				{
					boolean found = false;

					for(int x = 0; !found && x < actions.size(); x++)
					{
						RuntimeAction prevAction = (RuntimeAction) actions.get(x);
						int entryPriority = getAction().getManager().getConfiguration().getInt("app.runtime.action." + getAction().getCode() + ".priority." + prevAction.getCode(), 0);

						if(priority < entryPriority)
						{
							found = true;
							actions.add(x, action);
						}
					}

					if(!found) actions.add(action);
				}
			}
		}

		//check -call option is included in action command line
		if(getAction().getConfiguration().containsKey("call"))
		{
			String[] actionNames = getAction().getConfiguration().getStringArray("call");

			for(int i = 0; i < actionNames.length; i++)
			{
				String actionName = actionNames[i];

				if(StringUtility.isNotEmpty(actionName))
				{
					RuntimeAction action = getAction().getManager().getRuntimeAction(actionName);
					if(action == null) throw new AREasyException("Configuration error: Null action instance for name: " + actionName);
						else actions.add(action);
				}
			}
		}

		//validate actions list
		if(actions.isEmpty()) throw new AREasyException("There is no action selected! You have choose at least one action and related execution options!");
	}

	protected void setInOutLog(List keys) throws AREasyException
	{
		if(getAction().getConfiguration().containsKey("inoutlog"))
		{
			String inoutlog = getAction().getConfiguration().getString("inoutlog", null);

			if(getAction().getConfiguration().getBoolean("inoutlog", false) || StringUtility.equalsIgnoreCase(inoutlog, "true"))
			{
				inoutlog = RuntimeManager.getWorkingDirectory() + File.separator  + "inout" + "-" + Thread.currentThread().getName()+ ".csv";
			}

			File file = new File(inoutlog);

			if(!file.exists())
			{
				if(!inoutlog.contains("/") && !inoutlog.contains("\\"))
				{
					inoutlog = RuntimeManager.getWorkingDirectory().getAbsolutePath() + File.separator + inoutlog;
					file = new File(inoutlog);
				}
			}

			//remove it the file already exist
			if(file.exists())
			{
				if(!file.delete()) throw new AREasyException("InOut log file could already exists: " + file.getAbsolutePath());
			}
			else
			{
				try
				{
					inoutFileName = file.getAbsolutePath();
					inoutWriter = new CsvWriter(file);
				}
				catch (Exception e)
				{
					logger.error("Error creating inout log stream: " + e.getMessage());
					logger.debug("Exception", e);
				}
			}

			if(inoutWriter != null)
			{
				String values[] = new String[keys.size()];

				try
				{
					for(int i = 0; i < keys.size(); i++)
					{
						String key = (String) keys.get(i);
						values[i] = key;
					}

					inoutWriter.writeNext(values);
					inoutWriter.flush();
				}
				catch (Exception e)
				{
					throw new AREasyException(e);
				}
			}
		}
	}

	protected void setInOutLogData(List keys, Map source) throws AREasyException
	{
		if(inoutWriter != null && keys != null)
		{
			String values[] = new String[keys.size()];

			try
			{
				for(int i = 0; i < keys.size(); i++)
				{
					String key = (String) keys.get(i);
					Object value = source.get(key);

					if(value != null) values[i] = value.toString();
						else values[i] = "";
				}

				inoutWriter.writeNext(values);
				inoutWriter.flush();
			}
			catch (Exception e)
			{
				throw new AREasyException(e);
			}
		}
	}

	protected void closeInOutLog()
	{
		if(inoutWriter != null)
		{
			try
			{
				inoutWriter.close();

				//append the output to job data history.
				getAction().addLoggerOutput("In/Out Data", new File(inoutFileName));
			}
			catch (Exception e)
			{
				logger.error("Error closing inout log: " + e.getMessage());
				logger.debug("Exception", e);
			}
		}
	}

	public class Dictionary
	{
		private PropertiesConfiguration sector = null;

		public Dictionary(File file) throws AREasyException
		{
			try
			{
				if(file != null && file.exists()) sector = new PropertiesConfiguration(file.getAbsolutePath());
			}
			catch(ConfigurationException ce)
			{
				throw new AREasyException(ce);
			}
		}

		public String get(String key)
		{
			if(sector != null)
			{
				if(sector.containsKey(key)) return sector.getString(key, "");
				else
				{
					sector.setKey(key, "");
					return "";
				}
			}
			else return null;
		}

		public void close() throws AREasyException
		{
			try
			{
				sector.save();
			}
			catch(ConfigurationException ce)
			{
				throw new AREasyException(ce);
			}
		}
	}
}
