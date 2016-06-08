package org.areasy.runtime.actions.data;

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

import com.bmc.arsys.api.Constants;
import org.areasy.common.support.configuration.ConfigurationException;
import org.areasy.common.support.configuration.providers.properties.stream.PropertiesConfiguration;
import org.areasy.runtime.RuntimeManager;
import org.areasy.runtime.actions.AbstractAction;
import org.areasy.runtime.engine.RuntimeLogger;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.runtime.engine.services.status.BaseStatus;
import org.areasy.runtime.engine.structures.CoreItem;
import org.areasy.runtime.engine.structures.MultiPartItem;
import org.areasy.runtime.engine.structures.data.cmdb.ConfigurationItem;
import org.areasy.runtime.engine.workflows.ProcessorLevel2CmdbApp;
import org.areasy.common.data.NumberUtility;
import org.areasy.common.data.StopWatchUtility;
import org.areasy.common.data.StringUtility;
import org.areasy.common.support.configuration.Configuration;
import org.areasy.common.support.configuration.base.BaseConfiguration;
import org.areasy.common.support.configuration.providers.properties.stream.PropertiesEntry;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.*;

/**
 * Abstract library that publish a shared API for all common action that use Remedy interrogation.
 */
public abstract class BaseData extends AbstractAction implements CoreData
{
	/** action process status class */
	protected BaseDataStatus status = null;

	/** Identifier for query parameter: <code>Q + </code><field id> (number)</code> */
	public static final String FQUERY = "Q";

	/** Identifier for data parameter: <code>D + </code><field id> (number)</code> */
	public static final String FDATA = "D";

	/** start token  */
	protected static final String START_TOKEN = "@[";

	/** end token */
	protected static final String END_TOKEN = "]";

	/** String list will all field ids that should be ignored from constructions in case of their values are null */
	private List ignoreNulls = null;

	/** Dedicated flag tell to the final action that an operation has to be forced if it contain a loop processing */
	private Boolean force = null;

	/** Counter for total processed records */
	private int recordsCounter = 0;
	/** Counter for errors */
	private int errorsCounter = 0;

	/** Private maps that could contains data maps used to data conversion and validation */
	private Map<String, Dictionary> maps = null;

	/** Time control to measure the time for action execution*/
	private StopWatchUtility cron = new StopWatchUtility();

	/**
	 * Collect data input parameters and publish them into a <code>CoreItem</code> structure.
	 *
	 * @param entry <code>CoreItem</code> structure.
	 * @return true if at least one corresponding configuration data has been used to map a <code>CoreItem</code> attribute.
	 * @throws AREasyException is any error will occur
	 */
	public boolean setDataFields(CoreItem entry) throws AREasyException
	{
		boolean set = false;
		List<String> list = new Vector<String>();
		Iterator ids = getConfiguration().getKeys();

		while(ids != null && ids.hasNext())
		{
			String id = (String)ids.next();

			if(id != null && id.startsWith(FDATA))
			{
				String key = id.substring(1);
				if( NumberUtility.isNumber(key) ) list.add(id);
			}
		}

		for(int i = 0; !list.isEmpty() && i < list.size(); i++)
		{
			String id = list.get(i);
			String key = id.substring(1);

			Object value = getConfiguration().getKey(id);
			if(value instanceof String && (((String)value).startsWith("$") || ((String)value).indexOf("${") >= 0)) value = getConfiguration().getString(id);

			setAttribute(entry, key, value, "ignorenulldata");

			if(!set) set = true;
		}

		return set;
	}

	/**
	 * Collect query input parameters and publish them into a <code>CoreItem</code> structure.
	 *
	 * @param entry <code>CoreItem</code> structure.
	 * @return true if at least one corresponding configuration data has been used to map a <code>CoreItem</code> attribute.
	 * @throws AREasyException is any error will occur
	 */
	public boolean setQueryFields(CoreItem entry) throws AREasyException
	{
		boolean set = false;
		List<String> list = new Vector<String>();
		Iterator ids = getConfiguration().getKeys();

		while(ids != null && ids.hasNext())
		{
			String id = (String)ids.next();

			if(id != null && id.startsWith(FQUERY))
			{
				String key = id.substring(1);
				if( NumberUtility.isNumber(key) ) list.add(id);
			}
		}

		for(int i = 0; !list.isEmpty() && i < list.size(); i++)
		{
			String id = list.get(i);
			String key = id.substring(1);

			Object value = getConfiguration().getKey(id);
			if(value instanceof String && ((String)value).startsWith("$")) value = getConfiguration().getString(id);

			setAttribute(entry, key, value, "ignorenullquery");

			if(!set) set = true;
		}

		return set;
	}

	/**
	 * Collect data input parameters for a base structure and publish them into a <code>Map</code> structure.
	 *
	 * @return a <code>Map</code> structure.
	 * @throws AREasyException is any error will occur
	 */
	public Map getDataFields() throws AREasyException
	{
		Map map = new HashMap();
		List<String> list = new Vector<String>();
		Iterator ids = getConfiguration().getKeys();

		while(ids != null && ids.hasNext())
		{
			String id = (String)ids.next();

			if(id != null && id.startsWith(FDATA))
			{
				String key = id.substring(1);
				if( NumberUtility.isNumber(key) ) list.add(id);
			}
			else if(id != null && id.startsWith(FQUERY))
			{
				String key = id.substring(1);
				if( NumberUtility.isNumber(key) && !getConfiguration().containsKey(FDATA + key)) list.add(id);
			}
		}

		for(int i = 0; !list.isEmpty() && i < list.size(); i++)
		{
			String id = list.get(i);
			String key = id.substring(1);

			Object value = getConfiguration().getKey(id);
			if(value instanceof String && ((String)value).startsWith("$")) value = getConfiguration().getString(id);

			if(id.startsWith(FDATA)) setMap(map, key, value, "ignorenulldata");
				else if(id.startsWith(FQUERY)) setMap(map, key, value, "ignorenullquery");
		}

		return map;
	}

	/**
	 * Collect query input parameters for a base structure and publish them into a <code>Map</code> structure.
	 *
	 * @return a <code>Map</code> structure.
	 * @throws AREasyException is any error will occur
	 */
	public Map getQueryFields() throws AREasyException
	{
		Map map = new HashMap();
		List<String> list = new Vector<String>();
		Iterator ids = getConfiguration().getKeys();

		while(ids != null && ids.hasNext())
		{
			String id = (String)ids.next();

			if(id != null && id.startsWith(FQUERY))
			{
				String key = id.substring(1);
				if( NumberUtility.isNumber(key) ) list.add(id);
			}
		}

		for(int i = 0; !list.isEmpty() && i < list.size(); i++)
		{
			String id = list.get(i);
			String key = id.substring(1);

			Object value = getConfiguration().getKey(id);
			if(value instanceof String && ((String)value).startsWith("$")) value = getConfiguration().getString(id);

			setMap(map, key, value, "ignorenullquery");
		}

		return map;
	}

	/**
	 * Collect query input parameters for a multipart structure and publish them into a <code>CoreItem</code> structure.
	 *
	 * @param entry <code>CoreItem</code> structure.
	 * @return true if at least one corresponding configuration data has been used to map a <code>CoreItem</code> attribute.
	 * @throws AREasyException is any error will occur
	 */
	public boolean setMultiPartQueryFields(CoreItem entry) throws AREasyException
	{
		boolean set = false;
		List<String> list = new Vector<String>();
		boolean multipart = getConfiguration().getBoolean("multipart", false);
		Iterator ids = getConfiguration().getKeys();

		if(multipart)
		{
			while(ids != null && ids.hasNext())
			{
				String id = (String)ids.next();

				if(id != null && id.startsWith(FQUERY)) list.add(id);
			}

			for(int i = 0; !list.isEmpty() && i < list.size(); i++)
			{
				String id = list.get(i);
				String key = id.substring(1);

				if(id.indexOf(MultiPartItem.partSeparator) > 1)
				{
					Object value = getConfiguration().getKey(id);
					if(value instanceof String && ((String)value).startsWith("$")) value = getConfiguration().getString(id);

					setAttribute(entry, key, value, "ignorenullpartquery");

					if(!set) set = true;
				}
			}
		}

		return set;
	}

	/**
	 * Collect query input parameters for a multipart structure and publish them into a <code>Map</code> structure.
	 *
	 * @return a <code>Map</code> structure.
	 * @throws AREasyException is any error will occur
	 */
	public Map getMultiPartQueryFields() throws AREasyException
	{
		List<String> list = new Vector<String>();
		boolean multipart = getConfiguration().getBoolean("multipart", false);
		Iterator ids = getConfiguration().getKeys();
		Map map = new HashMap();

		if(multipart)
		{
			while(ids != null && ids.hasNext())
			{
				String id = (String)ids.next();
				if(id != null && id.startsWith(FQUERY)) list.add(id);
			}

			for(int i = 0; !list.isEmpty() && i < list.size(); i++)
			{
				String id = list.get(i);
				String key = id.substring(1);

				if(id.indexOf(MultiPartItem.partSeparator) > 1)
				{
					Object value = getConfiguration().getKey(id);
					if(value instanceof String && ((String)value).startsWith("$")) value = getConfiguration().getString(id);

					setMap(map, key, value, "ignorenullpartquery");
				}
			}
		}

		return map;
	}

	/**
	 * Collect data input parameters for a multipart structure and publish them into a <code>CoreItem</code> structure.
	 *
	 * @param entry <code>CoreItem</code> structure.
	 * @return true if at least one corresponding configuration data has been used to map a <code>CoreItem</code> attribute.
	 * @throws AREasyException is any error will occur
	 */
	public boolean setMultiPartDataFields(CoreItem entry) throws AREasyException
	{
		boolean set = false;
		List<String> list = new Vector<String>();
		boolean multipart = getConfiguration().getBoolean("multipart", false);
		Iterator ids = getConfiguration().getKeys();

		if(multipart)
		{
			while(ids != null && ids.hasNext())
			{
				String id = (String)ids.next();
				if(id != null && id.startsWith(FDATA)) list.add(id);
			}

			for(int i = 0; !list.isEmpty() && i < list.size(); i++)
			{
				String id = list.get(i);
				String key = id.substring(1);

				if(id.indexOf(MultiPartItem.partSeparator) > 1)
				{
					Object value = getConfiguration().getKey(id);
					if(value instanceof String && ((String)value).startsWith("$")) value = getConfiguration().getString(id);

					setAttribute(entry, key, value, "ignorenullpartdata");

					if(!set) set = true;
				}
			}
		}

		return set;
	}

	/**
	 * Collect data input parameters for a multipart structure and publish them into a <code>Map</code> structure.
	 *
	 * @return a <code>Map</code> structure.
	 * @throws AREasyException is any error will occur
	 */
	public Map getMultiPartDataFields() throws AREasyException
	{
		List<String> list = new Vector<String>();
		boolean multipart = getConfiguration().getBoolean("multipart", false);
		Iterator ids = getConfiguration().getKeys();
		Map map = new HashMap();

		if(multipart)
		{
			while(ids != null && ids.hasNext())
			{
				String id = (String)ids.next();
				if(id != null && id.startsWith(FDATA)) list.add(id);
			}

			for(int i = 0; !list.isEmpty() && i < list.size(); i++)
			{
				String id = list.get(i);
				String key = id.substring(1);

				if(id.indexOf(MultiPartItem.partSeparator) > 1)
				{
					Object value = getConfiguration().getKey(id);
					if(value instanceof String && ((String)value).startsWith("$")) value = getConfiguration().getString(id);

					setMap(map, key, value, "ignorenullpartdata");
				}
			}
		}

		return map;
	}

	/**
	 * Check if a specific string array is null (array length 0 or all strings are null or with length 0)
	 *
	 * @param data string array
	 * @return true if string array is null
	 */
	public boolean isDataEmpty(String[] data)
	{
		if(data == null || data.length == 0) return true;
		else
		{
			boolean empty = true;

			for (int i = 0; empty && i < data.length; i++)
			{
				empty = StringUtility.isEmpty(data[i]);
			}

			return empty;
		}
	}

	/**
	 * Set into a specific <code>CoreItem</code> structure multi-part forms.
	 *
	 * @param entry a specific <code>CoreItem</code> structure
	 */
	public void setMultiPartForms(CoreItem entry)
	{
		boolean multipart = getConfiguration().getBoolean("multipart", false);
		String partforms[] = getConfiguration().getStringArray("partforms", null);

		for(int i = 0; partforms != null && multipart && i < partforms.length; i++)
		{
			int index = partforms[i].indexOf(MultiPartItem.partSeparator, 0);
			if(index > 0) ((MultiPartItem)entry).setFormName(partforms[i]);
		}
	}

	/**
	 * Get a processor instance. This is useful for Velocity contexts
	 *
	 * @return processor instance
	 */
	public ProcessorLevel2CmdbApp getProcessor()
	{
		return new ProcessorLevel2CmdbApp();
	}

	/**
	 * Get a new instance of <code>CoreItem</code> structure.
	 *
	 * @return fresh <code>CoreItem</code> structure
	 */
	public CoreItem getCoreItem()
	{
		return getCoreItem(null);
	}

	/**
	 * Get a new instance of <code>CoreItem</code> structure having predefined the target form name.
	 *
	 * @param formName target Remedy form name
	 * @return fresh <code>CoreItem</code> structure
	 */
	public CoreItem getCoreItem(String formName)
	{
		CoreItem item = new CoreItem(formName);

		if(getConfiguration().containsKey("ignorenullvalues")) item.setIgnoreNullValues(getConfiguration().getBoolean("ignorenullvalues"));
		if(getConfiguration().containsKey("ignoreunchangedvalues")) item.setIgnoreUnchangedValues(getConfiguration().getBoolean("ignoreunchangedvalues"));
		if(getConfiguration().containsKey("simplified")) item.setSimplifiedStructure(getConfiguration().getBoolean("simplified"));
		if(getConfiguration().containsKey("firstmatchreading")) item.setFirstMatchReading();
		if(getConfiguration().containsKey("exactmatchreading")) item.setExactMatchReading();

		return item;
	}

	/**
	 * Get runtime data structure in case of the command line included <code>entity</code> flag.
	 *
	 * @return a specialized <code>CoreItem</code> structure or <code>CoreItem</code> itself
	 * @throws AREasyException in case of any exception during instantiation occurs
	 */
	public CoreItem getEntity() throws AREasyException
	{
		CoreItem item = null;

		String entity = getConfiguration().getString("entity", null);
		String classId = getConfiguration().getString("classid", null);
		String formName = getConfiguration().getString("form", getConfiguration().getString("formname", null));

		if(entity != null) item = getEntity(entity);
		else
		{
			if(classId != null) item = new ConfigurationItem();
				else item = getEntity(null);
		}

		if(classId != null && (item instanceof ConfigurationItem))((ConfigurationItem)item).setClassId(classId);
		if(formName != null) item.setFormName(formName);

		if(item != null)
		{
			if(getConfiguration().containsKey("ignorenullvalues")) item.setIgnoreNullValues(getConfiguration().getBoolean("ignorenullvalues"));
			if(getConfiguration().containsKey("ignoreunchangedvalues")) item.setIgnoreUnchangedValues(getConfiguration().getBoolean("ignoreunchangedvalues"));
			if(getConfiguration().containsKey("simplified")) item.setSimplifiedStructure(getConfiguration().getBoolean("simplified"));
			if(getConfiguration().containsKey("firstmatchreading")) item.setFirstMatchReading();
			if(getConfiguration().containsKey("exactmatchreading")) item.setExactMatchReading();
		}

		return item;
	}

	/**
	 * Get runtime data structure in case of the command line included <code>entity</code> flag.
	 *
	 * @param entity entity name that have to be registered in the configuration sector(s)
	 * @return a specialized <code>CoreItem</code> structure or <code>CoreItem</code> itself
	 * @throws AREasyException in case of any exception during instantiation occurs
	 */
	public CoreItem getEntity(String entity) throws AREasyException
	{
		CoreItem item = null;

		if(StringUtility.isEmpty(entity)) item = getCoreItem();
		else
		{
			String className = getManager().getConfiguration().getString("app.runtime.structure." + entity + ".class", null);
			if(className == null) throw new AREasyException("Implementation library of '" + entity + "' entity name is null");

			try
			{
				Class classEntity = Class.forName(className);
				Constructor contructor = classEntity.getConstructor(null);

				item = (CoreItem) contructor.newInstance(null);
			}
			catch(Throwable th)
			{
				throw new AREasyException("Error reading entity type: " + th.getMessage(), th);
			}
		}

		return item;
	}

	private void setAttribute(CoreItem entry, String key, Object value, String ignorenull) throws AREasyException
	{
		if(entry == null) return;

		if(value instanceof String)
		{
			value = getLookupValue((String)value, entry);

			if(((ignoreNulls != null && ignoreNulls.contains(key)) || getConfiguration().getBoolean(ignorenull, false)) && StringUtility.isEmpty( (String)value))
			{
				RuntimeLogger.debug("Ignore field '" + key + "' because has a null value");
			}
			else if(StringUtility.equals((String)value, "NULL") || StringUtility.equals((String)value, ""))
			{
				entry.setNullAttribute(key);
			}
			else
			{
				entry.setAttribute(key, value);
			}
		}
		else
		{
			if(((ignoreNulls != null && ignoreNulls.contains(key)) || getConfiguration().getBoolean(ignorenull, false)) && value == null)
			{
				RuntimeLogger.debug("Ignore field '" + key + "' because has a null value");
			}
			else
			{
				entry.setAttribute(key, value);
			}
		}
	}

	private void setMap(Map map, String key, Object value, String ignorenull) throws AREasyException
	{
		if(map == null) return;

		if(value instanceof String)
		{
			value = getLookupValue( (String)value, null);

			if(((ignoreNulls != null && ignoreNulls.contains(key)) || getConfiguration().getBoolean(ignorenull, false)) && StringUtility.isEmpty( (String)value))
			{
				RuntimeLogger.debug("Ignore field '" + key + "' because has a null value");
			}
			else map.put(key, value);
		}
		else
		{
			if(((ignoreNulls != null && ignoreNulls.contains(key)) || getConfiguration().getBoolean(ignorenull, false)) && value == null)
			{
				RuntimeLogger.debug("Ignore field '" + key + "' because has a null value");
			}
			else map.put(key, value);
		}
	}

	/**
	 * If a string value has the format could be analyzed as a lookup value by this function.
	 * <pre>@[<b>schema</b>=<Remedy form name>, <b>query</b>=<string qualification>, <b>return</b>=<form field id>, <b>store</b>=<list of field ids>, <b>key</b>=<key to store the the field mapping>]</pre>
	 * The expression will be decoded, interpreted, read targeted entry from remedy server and in the end
	 * will return the value corresponding to the specified <b>return</b> - inside of expression. If the <b>return</b> is missing will be returned
	 * the record id (field no. 1). In case of the <code>key</code> value is missing the default key is the escaped form name and with upper cases.
	 * In order to access the field you have refer them using the following syntax: <pre>${<keyname>:<fieldid>}</pre>. For example to access RequestID field from <code>PCT:Product Catalog</code> form
	 * and without to specify the key name you have to specify the following expression: <code>${PCT_PRODUCT_CATALOG:1}</code>
	 *
	 * @param value string expression that has to be analyzed
	 * @return a lookup value from Remedy server according to teh specified expression
	 * @throws AREasyException if any error will occur
	 */
	public String getLookupValue(String value, CoreItem entry) throws AREasyException
	{
		if(value != null)
		{
			int startIndex1 = value.indexOf(START_TOKEN, 0);

			if(startIndex1 >= 0)
			{
				String output = null;
				String expression = null;

				int startIndex2 = value.indexOf(START_TOKEN, startIndex1 + START_TOKEN.length());

				if(startIndex2 > startIndex1)
				{
					expression = value.substring(startIndex1 + START_TOKEN.length());
					output = getLookupValue(expression, entry);

					//validate output
					if(output == null) output = "";

					//apply output in the original expression
					value = StringUtility.replace(value, expression, output);
				}

				int endIndex = value.indexOf(END_TOKEN, 0);

				if(endIndex > 0)
				{
					expression = value.substring(startIndex1, endIndex + END_TOKEN.length());
					String qualification = expression.substring(START_TOKEN.length(), expression.length() - END_TOKEN.length());
					String data[] = StringUtility.split(qualification, ',');

					Configuration config = new BaseConfiguration();
					config.merge(getConfiguration());

					for(int i = 0; i < data.length; i++)
					{
						String part = data[i];
						config.setConfigurationEntry(new PropertiesEntry(part));
					}

					String schema = config.getString("schema", null);
					String map = config.getString("map", null);

					if(StringUtility.isNotEmpty(schema))
					{
						String query = config.getString("query", null);
						String store = config.getString("store", null);
						String code = config.getString("key", StringUtility.variable(schema).toUpperCase());
						int fieldId = config.getInt("return", 0);

						//get interrogation text into a real qualification
						query = getTranslatedQualification(query);

						CoreItem item = getCoreItem(schema);
						item.read(getServerConnection(), query);

						if (item.exists())
						{
							if (fieldId > 1) output = item.getStringAttributeValue(fieldId);
							else output = item.getEntryId();

							if (store != null)
							{
								String fields[] = StringUtility.split(store, ";");

								for (int i = 0; fields != null && i < fields.length; i++)
								{
									String storeKey = code + ":" + fields[i];
									String storeValue = StringUtility.equals(fields[i], "1") ? item.getEntryId() : item.getStringAttributeValue(fields[i]);

									getConfiguration().setKey(storeKey, storeValue);
								}
							}
						}
						else
						{
							if (store != null)
							{
								String fields[] = StringUtility.split(store, ";");

								for (int i = 0; fields != null && i < fields.length; i++)
								{
									String key = code + ":" + fields[i];
									if (getConfiguration().containsKey(key)) getConfiguration().removeKey(key);
								}
							}
						}
					}
					else if(StringUtility.isNotEmpty(map))
					{
						String id = config.getString("key", null);

						if(entry != null && id != null && id.startsWith(FDATA))
						{
							String key = id.substring(1);
							if( NumberUtility.isNumber(key))
							{
								key = entry.getStringAttributeValue(key);

								if(maps.get(map) != null)
								{
									value = String.valueOf( ((Dictionary)maps.get(map)).get(key) );
								}
								else value = "";
							}
							else value = "";
						}
						else value = "";
					}
				}

				//validate output
				if(output == null) output = "";

				//apply output in the original expression
				value = StringUtility.replace(value, expression, output);
			}
		}

		return value;
	}

	/**
	 * Translate a pseudo-expression into a normal expression using for qualifications or others. The specific of these pseudo-expression
	 * are the symbols: <br/>
	 * <table>
	 *     <tr><td>||</td><td>double quote</td></tr>
	 *     <tr><td>|</td><td>simple quote</td></tr>
	 *     <tr><td>-lt</td><td><code>&#60;</code></td></tr>
	 *     <tr><td>-le</td><td><code>&#60;=</code></td></tr>
	 *     <tr><td>-gt</td><td><code>&#62;</code></td></tr>
	 *     <tr><td>-ge</td><td><code>&#62;=</code></td></tr>
	 * </table>
	 * @param value pseudo-expression
	 * @return translated value
	 */
	public String getTranslatedQualification(String value)
	{
		if(value != null)
		{
			//convert empty values into NULL identifier
			if(value.contains("||||")) value = StringUtility.replace(value, "||||", "$\\NULL$");

			//check if the values contains quota
			if(value.contains("\"") && value.contains("||")) value = StringUtility.replace(value, "\"", "\"\"");

			value = StringUtility.replace(value, "||", "\"");
			value = StringUtility.replace(value, "|", "'");

			value = StringUtility.replace(value, "-lt", "<");
			value = StringUtility.replace(value, "-le", "<=");
			value = StringUtility.replace(value, "-gt", ">");
			value = StringUtility.replace(value, "-ge", ">=");
		}

		return value;
	}

	/**
	 * Read from configuration <code>ignorenulls</code> parameter that fill in the list of field that has to be ignored
	 * in case of the values are null. If the parameter is not defined the method will return an empty field.
	 */
	public void setIgnoreNullFields()
	{
		this.ignoreNulls = getConfiguration().getList("ignorenulls", new Vector());
	}

	/**
	 * Set (if the list is null) or add a list of field that has to be ignored in case of the values are null
	 * @param ignorenulls list of field ids
	 */
	public void setIgnoreNullFields(List ignorenulls)
	{
		if(this.ignoreNulls != null) this.ignoreNulls.addAll(ignorenulls);
			else this.ignoreNulls = ignorenulls;
	}

	/**
	 * Get the list of fields that have to be ignored
	 * @return <code>List structure</code>
	 */
	public List getIgnoreNullFields()
	{
		return this.ignoreNulls;
	}

	/**
	 * Read from configuration <code>force</code> parameter that will be used to force transactions in case of the main transaction contains
	 * a cycle that repeats sub-transactions.
	 */
	public void setForceFlag()
	{
		this.force = new Boolean(getConfiguration().getBoolean("force", false));
	}

	/**
	 * Read <code>force</code> flag that will be used to force transactions in case of the main transaction contains
	 * a cycle that repeats sub-transactions.
	 * @param force force flag
	 */
	public void setForceFlag(boolean force)
	{
		this.force = new Boolean(force);
	}

	/**
	 * Get the <code>force</code> flag
	 * @return <code>force</code> flag value
	 */
	public boolean isForced()
	{
		if(this.force == null) setForceFlag();

		return this.force.booleanValue();
	}

	public int addRecordsCounter(int counter)
	{
		return this.recordsCounter += counter;
	}

	public int setRecordsCounter()
	{
		return this.recordsCounter++;
	}

	public int addErrorsCounter(int counter)
	{
		return this.errorsCounter += counter;
	}

	public int setErrorsCounter()
	{
		return this.errorsCounter++;
	}

	public int getRecordsCounter()
	{
		return this.recordsCounter;
	}

	public int getErrorsCounter()
	{
		return this.errorsCounter;
	}

	public void resetCounters()
	{
		this.errorsCounter = 0;
		this.recordsCounter = 0;
	}

	public String getCronTime()
	{
		return this.cron.toString();
	}

	public StopWatchUtility getCron()
	{
		return this.cron;
	}

	public void setOutputMessage()
	{
		StringBuffer buffer = new StringBuffer();

		buffer.append("\n").append(getCode()).append(" - Execution results:" + "\n");
		buffer.append("\tTotal number of records: ").append(getRecordsCounter()).append("\n");
		buffer.append("\tNumber of errors: ").append(getErrorsCounter()).append("\n");
		buffer.append("\tExecution time: ").append(getCronTime()).append("\n" + "\n");

		RuntimeLogger.add(buffer.toString());
	}

	/**
	 * Get merge combination using input parameters.
	 *
	 * @param config input parameters managed by a Configuration structure.
	 * @return integer merge value
	 */
	public static int getMergeTypeAndOptions(Configuration config)
	{
		int type = 0;

		if(config == null) return type;

		String mergetype = config.getString("mergetype", null);
		List mergeoptions = config.getList("mergeoptions", null);

		if(NumberUtility.toInt(mergetype) > 0) type = NumberUtility.toInt(mergetype);
		else
		{
			if(StringUtility.equals(mergetype, "duperror")) type = Constants.AR_MERGE_ENTRY_DUP_ERROR;
			else if(StringUtility.equals(mergetype, "dupnewid")) type = Constants.AR_MERGE_ENTRY_DUP_NEW_ID;
			else if(StringUtility.equals(mergetype, "dupoverwrite")) type = Constants.AR_MERGE_ENTRY_DUP_OVERWRITE;
			else if(StringUtility.equals(mergetype, "dupmerge") || StringUtility.equals(mergetype, "dupupdate")) type = Constants.AR_MERGE_ENTRY_DUP_MERGE;
		}

		for(int i = 0; mergeoptions != null && i < mergeoptions.size(); i++)
		{
			String option = (String) mergeoptions.get(i);

			if(StringUtility.equals(option, "norequired")) type = type | Constants.AR_MERGE_NO_REQUIRED_INCREMENT;
			if(StringUtility.equals(option, "nopattern")) type = type | Constants.AR_MERGE_NO_PATTERNS_INCREMENT;
			if(StringUtility.equals(option, "noworkflow")) type = type | Constants.AR_MERGE_NO_WORKFLOW_FIRED;
		}

		return type;
	}

	@Override
	public BaseStatus getCurrentStatus()
	{
		if(status == null) status = new BaseDataStatus(this);

		return status;
	}

	/**
	 * To change this template use File | Settings | File Templates.
	 */
	public class BaseDataStatus extends BaseStatus
	{
		BaseData action;

		private String signature = null;
		private String execMessage = null;
		private String noexecMessage = null;

		public BaseDataStatus(BaseData action)
		{
			this.action = action;
		}

		protected String getMessage()
		{
			String message;
			String prefix;

			if(this.signature != null) prefix = signature + ": ";
				else prefix = action.getCode() + ": ";

			if(action == null) return null;

			if (action.getRecordsCounter() > 0)
			{
				if(execMessage == null)
				{
					message = prefix + "Action processed " + action.getRecordsCounter() + " entries in " + action.getCronTime();

					if (action.getErrorsCounter() > 0) message += " and discovered " + action.getErrorsCounter() + " errors.";
						else message += " without errors";
				}
				else message = prefix + getInternalMessage(execMessage);
			}
			else
			{
				if(noexecMessage == null) message = prefix + "Action didn't start yet to process entries";
					else message = prefix + getInternalMessage(noexecMessage);
			}

			return message;
		}

		private String getInternalMessage(String message)
		{
			if(message != null)
			{
				message = StringUtility.replace( message, "{1}", String.valueOf(action.getRecordsCounter()) );
				message = StringUtility.replace( message, "{2}", String.valueOf(action.getErrorsCounter()) );
				message = StringUtility.replace( message, "{3}", action.getCronTime() );
			}

			return message;
		}

		public void setExecMessage(String execMessage)
		{
			this.execMessage = execMessage;
		}

		public void setNoexecMessage(String noexecMessage)
		{
			this.noexecMessage = noexecMessage;
		}

		public void resetExecMessage()
		{
			this.execMessage = null;
		}

		public void resetNoexecMessage()
		{
			this.noexecMessage = null;
		}

		public void setCallerSignature(String signature)
		{
			this.signature = signature;
		}

		public String getCallerSignature()
		{
			return this.signature;
		}
	}

	/**
	 * Load data maps from external files
	 *
	 * @throws AREasyException
	 */
	protected void setDataMaps() throws AREasyException
	{
		//get data maps
		if(getConfiguration().containsKey("datamaps"))
		{
			maps = new HashMap();
			String[] datamaps = getConfiguration().getStringArray("datamaps", null);

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
	}

	public Map getDataMaps()
	{
		return this.maps;
	}

	/**
	 * File dictionary in order to generate variales maps.
	 */
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
