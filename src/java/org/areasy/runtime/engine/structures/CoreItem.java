package org.areasy.runtime.engine.structures;

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

import com.bmc.arsys.api.*;
import org.areasy.common.data.NumberUtility;
import org.areasy.common.data.StringUtility;
import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;
import org.areasy.runtime.RuntimeManager;
import org.areasy.runtime.engine.RuntimeLogger;
import org.areasy.runtime.engine.RuntimeServer;
import org.areasy.runtime.engine.base.ARDictionary;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.runtime.engine.base.ServerConnection;

import java.io.File;
import java.util.*;

/**
 * ARS core item structure, defining an usual record instance from ARS forms.
 */
public class CoreItem implements ARDictionary
{
	/** Library logger */
	protected static Logger logger =  LoggerFactory.getLog(CoreItem.class);

	private Attribute entryId = new Attribute(1);
	private Map attributes = new Hashtable();

	private String formName = null;

	private boolean simulation = false;
	private boolean ignoreNullValues = true;
	private boolean ignoreUnchangedValues = true;
	private boolean simplifiedStructure = false;
	private boolean readFirstMatch = false;

	private String operatorStrings[] = { "%", ">=", ">", "<=", "<", "!=" };
	private int operatorIndex[] = {Constants.AR_REL_OP_LIKE, Constants.AR_REL_OP_GREATER_EQUAL, Constants.AR_REL_OP_GREATER, Constants.AR_REL_OP_LESS_EQUAL, Constants.AR_REL_OP_LESS, Constants.AR_REL_OP_NOT_EQUAL };

	private int[] entryFieldIds = null;
	private SortInfo sortInfo = new SortInfo(1, Constants.AR_SORT_ASCENDING);

	/**
	 * Default constructor
	 */
	public CoreItem()
	{
		clear();
		entryId.setLabel("Request ID");
	}

	/**
	 * Constructor that set the form name
	 *
	 * @param formName ARSystem form name
	 */
	public CoreItem(String formName)
	{
		this();

		if(formName != null) setFormName(formName);
	}

	/**
	 * Constructor who can fill attributes from this instance.
	 *
	 * @param map a map with attributes (key pairs with field id and object value)
	 */
	public CoreItem(Map map)
	{
		setDefaultData(map);
	}

	/**
	 * Create a new instance of core item structure.
	 *
	 * @return new instance of <code>CoreItem</code> structure
	 */
	public CoreItem getInstance()
	{
		return new CoreItem();
	}

	/**
	 * Get core item entry id.
	 * @return entry id (unique key for an ARS form)
	 */
	public String getEntryId()
	{
		return entryId.getStringValue();
	}

	/**
	 * Set entry Id for a core item instance
	 *
	 * @param entryId entry id unique key.
	 */
	protected void setEntryId(String entryId)
	{
		this.entryId.setDefaultValue(entryId);
	}

	/**
	 * Reset entry id value
	 */
	public void resetEntryId()
	{
		this.entryId.setDefaultValue(null);
	}

	/**
	 * Check if the current item instance contains specified field.
	 *
	 * @param id field id
	 * @return true if the specified field exist
	 */
	public boolean containsAttributeField(String id)
	{
		return attributes.containsKey(id);
	}

	/**
	 * Check if the current item instance contains specified field.
	 *
	 * @param id field id
	 * @return true if the specified field exist
	 */
	public boolean containsAttributeField(long id)
	{
		return attributes.containsKey(String.valueOf(id));
	}

	/**
	 * Add a new object attribute in the item attribute's list.
	 *
	 * @param id attribute field id
	 */
	public void setAttribute(String id)
	{
		if(id != null) attributes.put(id, new Attribute(id));
	}


	/**
	 * Add a new object attribute in the item attribute's list.
	 *
	 * @param id attribute field id
	 */
	public void setAttribute(long id)
	{
		attributes.put(String.valueOf(id), new Attribute(id));
	}

	/**
	 * Add a new object attribute in the item attribute's list.
	 *
	 * @param attr attribute field instance
	 */
	public void setAttribute(Attribute attr)
	{
		if(attr != null) attributes.put(attr.getId(), attr);
	}

	/**
	 * Add a new object attribute in the item attribute's list.
	 *
	 * @param id field id
	 * @param value field value
	 */
	public void setDefaultAttribute(String id, Object value)
	{
		attributes.put(id, new Attribute(id, value));
	}

	/**
	 * Add a new object attribute in the item aatribute's list.
	 *
	 * @param id field id
	 * @param value field value
	 */
	public void setDefaultAttribute(long id, Object value)
	{
		attributes.put(String.valueOf(id), new Attribute(id, value));
	}

	/**
	 * Set object value for this attribute structure with a null value
	 * @param id field id
	 */
	public void setNullAttribute(long id)
	{
		setNullAttribute(String.valueOf(id));
	}

	/**
	 * Set object value for this attribute structure with a null value
	 * @param id field id
	 */
	public void setNullAttribute(String id)
	{
		Attribute attr = getAttribute(id);

		if(attr != null) attr.setNullValue();
		else
		{
			attr = new Attribute(id);
			attr.setNullValue();

			//22.09.2009, SDA - Correction for searching after NULL
			attr.setChanged();

			setAttribute(attr);
		}
	}

	/**
	 * Set default object value for this attribute structure with a null value
	 * @param id field id
	 */
	public void setDefaultNullAttribute(String id)
	{
		Attribute attr = getAttribute(id);

		if(attr != null) attr.setDefaultNullValue();
		else
		{
			attr = new Attribute(id);
			attr.setDefaultNullValue();

			setAttribute(attr);
		}
	}

	/**
	 * Set default object value for this attribute structure with a null value
	 * @param id field id
	 */
	public void setDefaultNullAttribute(long id)
	{
		setDefaultNullAttribute(String.valueOf(id));
	}

	/**
	 * This method will set the attachment value of an existent field id (attribute field).
	 * If the attribute field id is not registered will create and register a new attribute but marked as changed.
	 *
	 * @param id field id
	 * @param value  field value which should be a file
	 * @throws AREasyException if the file is null or doesn't exist
	 */
	public void setAttribute(String id, File value) throws AREasyException
	{
		if(value == null) setNullAttribute(id);
		else
		{
			Attribute attr = getAttribute(id);

			if(attr != null) attr.setAttachment(value);
			else
			{
				attr = new Attribute(id);
				attr.setAttachment(value);

				setAttribute(attr);
			}
		}
	}

	/**
	 * This method will set the attachment value of an existent field id (attribute field).
	 * If the attribute field id is not registered will create and register a new attribute but marked as changed.
	 *
	 * @param id field id
	 * @param value  field value which should be a file
	 * @throws AREasyException if the file is null or doesn't exist
	 */
	public void setAttribute(long id, File value) throws AREasyException
	{
		setAttribute(String.valueOf(id), value);
	}

	/**
	 * This method will set the value of an existent field id (attribute field).
	 * If the attribute field id is not registered will create and register a new attrbuted but marked as changed.
	 *
	 * @param id field id
	 * @param value  field value
	 */
	public void setAttribute(long id, Object value)
	{
		setAttribute(String.valueOf(id), value);
	}

	/**
	 * This method will set the value of an existent field id (attribute field).
	 * If the attribute field id is not registered will create and register a new attribute but marked as changed.
	 *
	 * @param id field id
	 * @param value  field value
	 */
	public void setAttribute(String id, Object value)
	{
		if(id == null) return;

		if(value == null) setNullAttribute(id);
		else
		{
			Attribute attr = getAttribute(id);

			if(attr != null) attr.setValue(value);
			else
			{
				attr = new Attribute(id);
				attr.setValue(value);

				setAttribute(attr);
			}
		}
	}

	/**
	 * Get an object attribute value from the core item attribute's list.
	 *
 	 * @param id field id
	 * @return field value for the specified field id.
	 */
	public Attribute getAttribute(Object id)
	{
		return (Attribute) attributes.get(id);
	}

	/**
	 * Get an object attribute value from the core item attribute's list.
	 *
 	 * @param id field id
	 * @return field value for the specified field id.
	 */
	public Attribute getAttribute(long id)
	{
		return getAttribute(String.valueOf(id));
	}

	/**
	 * Check if specified attribute id is defined or if exist
	 *
	 * @param id field id
	 * @return true if the specified id exist
	 */
	public boolean isAttribute(String id)
	{
		return id != null && attributes.containsKey(id);
	}

	/**
	 * Check if specified attribute id is defined
	 *
	 * @param id field id
	 * @return true if the specified id exist
	 */
	public boolean isAttribute(long id)
	{
		return isAttribute(String.valueOf(id));
	}

	/**
	 * Check if specified attribute id is defined and is not null
	 *
	 * @param id field id
	 * @return true if the specified id exist and is not null
	 */
	public boolean isEmptyAttribute(String id)
	{
		if(isAttribute(id))
		{
			Attribute attr = getAttribute(id);

			if(attr != null)
			{
				Object value = attr.getValue();

				if(value != null)
				{
					if(value instanceof String) return StringUtility.isEmpty(value.toString());
						else return false;
				}
				else return true;
			}
			else return true;
		}
		else return true;
	}

	/**
	 * Check if specified attribute id is defined and is not null
	 *
	 * @param id field id
	 * @return true if the specified id exist and is not null
	 */
	public boolean isEmptyAttribute(long id)
	{
		return isEmptyAttribute(String.valueOf(id));
	}

	/**
	 * Get a object attribute value from the core item attribute's list.
	 *
 	 * @param id field id
	 * @return field value for the specified field id.
	 */
	public Object getAttributeValue(String id)
	{
		Attribute attr = getAttribute(id);

		if(attr != null) return attr.getValue();
		else
		{
			if(StringUtility.equals(id, "1")) return getEntryId();
				else return null;
		}
	}

	/**
	 * Get a string attribute value from the core item attribute's list.
	 *
 	 * @param id field id
	 * @return field value for the specified field id.
	 */
	public Object getAttributeValue(long id)
	{
		return getAttributeValue(String.valueOf(id));
	}


	/**
	 * Get a string attribute value from the core item attribute's list.
	 *
 	 * @param id field id
	 * @return field value for the specified field id.
	 */
	public Object getAttributeValue(int id)
	{
		return getAttributeValue(String.valueOf(id));
	}

	/**
	 * Get a string attribute value from the core item attribute's list.
	 *
 	 * @param id field id
	 * @return field value for the specified field id.
	 */
	public String getStringAttributeValue(String id)
	{
		Attribute attr = getAttribute(id);

		if(attr != null) return attr.getStringValue();
			else return null;
	}

	/**
	 * Get a string attribute value from the core item attribute's list.
	 *
 	 * @param id field id
	 * @return field value for the specified field id.
	 */
	public String getStringAttributeValue(long id)
	{
		return getStringAttributeValue(String.valueOf(id));
	}

	/**
	 * Get a <code>File</code> attribute value from the core item attribute's list.
	 *
	 * @param arsession user session
 	 * @param id field id
	 * @return field value for the specified field id.
	 */
	public File getFileAttributeValue(ServerConnection arsession, int id)
	{
		return getFileAttributeValue(arsession, String.valueOf(id));
	}

	/**
	 * Get a <code>File</code> attribute value from the core item attribute's list.
	 *
	 * @param arsession user session
 	 * @param id field id
	 * @return field value for the specified field id.
	 */
	public File getFileAttributeValue(ServerConnection arsession, String id)
	{
		Object value = null;
		Attribute attr = getAttribute(id);

		if(attr != null) value = attr.getValue();

		if(value != null && value instanceof File) return (File)value;

		if(value != null && (value instanceof AttachmentValue || (value instanceof Value && ((Value) value).getValue() instanceof AttachmentValue)))
		{
			AttachmentValue attachmentValue = null;

			if(value instanceof Value) attachmentValue = (AttachmentValue) ((Value) value).getValue();
				else if(value instanceof AttachmentValue) attachmentValue = (AttachmentValue) value;

			String attachementName = attachmentValue.getValueFileName();

			if(attachementName != null)
			{
				if(attachementName.lastIndexOf("\\") >= 0) attachementName = attachementName.substring(attachementName.lastIndexOf("\\") + 1);
					else if(attachementName.lastIndexOf("/") >= 0) attachementName = attachementName.substring(attachementName.lastIndexOf("/") + 1);

				File file = new File(RuntimeManager.getWorkingDirectory(), attachementName);
				if(file.exists()) file.delete();

				try
				{
					arsession.getContext().getEntryBlob(formName, getEntryId(), attr.getNumberId(), file.getPath());
				}
				catch(ARException are)
				{
					logger.error("Error downloading file '" + file.getPath() + "': " + are.getMessage());
					logger.debug("Exception", are);
				}

				return file;
			}
			else return null;
		}
		else return null;
	}

	/**
	 * Get number of attributes registered in this core item instance.
	 *
	 * @return total number of registered attributes.
	 */
	public int getNumberOfAttributes()
	{
		return attributes.size();
	}

	/**
	 * Delete an attribute from this core item instance.
	 *
	 * @param fieldid string key (field id in string format)
	 */
	public void deleteAttribute(String fieldid)
	{
		attributes.remove(fieldid);
	}

	/**
	 * Delete an attribute from this core item instance.
	 *
	 * @param fieldid field id in core format
	 */
	public void deleteAttribute(long fieldid)
	{
		attributes.remove( String.valueOf(fieldid));
	}

	/**
	 * Delete all attributes and reset the enty id.
	 */
	public void clear()
	{
		attributes.clear();
		setEntryId(null);
		resetSortInfo();
	}

	/**
	 * Get a list with all field ids registered in all attributes fr this core item instance.
	 *
	 * @return a vector instance with all field ids.
	 */
	public List getAttributeIds()
	{
		List list = new Vector();
		Iterator iterator = attributes.keySet().iterator();

		while(iterator != null && iterator.hasNext()) list.add(iterator.next());

		return list;
	}

	/**
	 * Get all core item structure attirbutes.
	 *
	 * @return collection of attributes.
	 */
	public Collection getAttributes()
	{
		return attributes.values();
	}

	/**
	 * Set the attribute's list for this core item instance. This method will remove all existent attributes
	 * and the will read this map and will record new atribute instances.
	 *
	 * @param attributes attribute's collection.
	 */
	public void setDefaultData(Collection attributes)
	{
		//clear all existent attributes
		clear();

		if(attributes != null && !attributes.isEmpty())
		{
			Iterator iterator = attributes.iterator();
			while(iterator != null && iterator.hasNext())
			{
				Attribute attr = (Attribute) iterator.next();
				if(attr == null) continue;

				if(StringUtility.equals(attr.getId(), "1")) setEntryId(String.valueOf(attr.getStringValue()));
					else setDefaultAttribute(attr.getId(), attr.getValue());
			}
		}
	}

	/**
	 * Set the attribute's list for this core item instance. This method will remove all existent attributes
	 * and the will read this map and will record new atribute instances.
	 *
	 * @param map attribute's map.
	 */
	public void setDefaultData(Map map)
	{
		//clear all existent attributes
		clear();

		if(map != null && !map.isEmpty())
		{
			Iterator iterator = map.keySet().iterator();

			while(iterator != null && iterator.hasNext())
			{
				Object key = iterator.next();
				Object value = map.get(key);

				if(key != null)
				{
					if(StringUtility.equals(String.valueOf(key), "1")) setEntryId(String.valueOf(value));
						else setDefaultAttribute(String.valueOf(key), value);
				}

			}
		}
	}

	/**
	 * Reset and set attributes using ARS <code>Entry</code> structures. This method will mark all existent
	 * attributes as not changed (default values).
	 *
	 * @param entry an <code>Entry</code> structure
	 */
	protected void setDefaultData(Entry entry)
	{
		//clear all existent attributes
		clear();

		if(entry != null)
		{
			Iterator iterator = entry.keySet().iterator();
			while(iterator!= null && iterator.hasNext())
			{
				Integer fieldId = (Integer)iterator.next();
				Value value = entry.get(fieldId);

				if(value != null && value.getDataType() != DataType.NULL)
				{
					if(fieldId == 1) setEntryId(String.valueOf(value));
						else setDefaultAttribute(fieldId, value);
				}
			}
		}
	}

	/**
	 * Get attributes' map
	 *
	 * @return A <code>Map</code> structure with all attributes.
	 */
	public Map getData()
	{
		return this.attributes;
	}

	/**
	 * Set the attribute's list for this core item instance. This method will mark all existent attributes as changed.
	 *
	 * @param map attribute's map.
	 */
	public void setData(Map map)
	{
		if(map != null && !map.isEmpty())
		{
			Iterator iterator = map.keySet().iterator();

			while(iterator != null && iterator.hasNext())
			{
				Object key = iterator.next();
				Object value = map.get(key);

				if(value != null && value instanceof Attribute)
				{
					setAttribute( (Attribute)value );
				}
				else if(key != null)
				{
					if(StringUtility.equals(String.valueOf(key), "1")) setEntryId(String.valueOf(value));
						else setAttribute(String.valueOf(key), value);
				}
			}
		}
	}

	/**
	 * Set the attribute's list for this core item instance. This method will mark all existent attributes as changed.
	 *
	 * @param collection collection of attributes
	 */
	public void setData(Collection collection)
	{
		if(collection != null && !collection.isEmpty())
		{
			Iterator iterator = collection.iterator();

			while(iterator != null && iterator.hasNext())
			{
				Attribute attr = (Attribute) iterator.next();

				setAttribute(attr);
			}
		}
	}

	/**
	 * Set the attribute's list for this core item instance only with attachment fields. This method will mark all existent attributes as changed.
	 *
	 * @param map attribute's map.
	 * @throws AREasyException will the file is null or doesn't exist.
	 */
	public void setAttachmentData(Map map) throws AREasyException
	{
		if(map != null && !map.isEmpty())
		{
			Iterator iterator = map.keySet().iterator();

			while(iterator != null && iterator.hasNext())
			{
				Object key = iterator.next();
				Object value = map.get(key);

				if(key != null && value != null)
				{
					File file = null;

					if(value instanceof File) file  = (File)value;
						else new File(String.valueOf(value));

					setAttribute(String.valueOf(key), file);
				}
			}
		}
	}

	/**
	 * Get all attributes which are changed.
	 *
	 * @return a list of attributes
	 */
	public List getChangedAttributes()
	{
		List list = new Vector();

		Collection collection = getAttributes();

		if(collection != null && !collection.isEmpty())
		{
			Iterator iterator = collection.iterator();

			while(iterator != null && iterator.hasNext())
			{
				Attribute attr = (Attribute) iterator.next();

				if(attr.isChanged()) list.add(attr);
			}
		}

		return list;
	}

	/**
	 * Get all attributes which are not changed and not null.
	 *
	 * @return a list of attributes
	 */
	public List getUnchangedAttributes()
	{
		List list = new Vector();

		Collection collection = getAttributes();

		if(collection != null && !collection.isEmpty())
		{
			Iterator iterator = collection.iterator();

			while(iterator != null && iterator.hasNext())
			{
				Attribute attr = (Attribute) iterator.next();

				if(!attr.isChanged() && attr.getValue() != null) list.add(attr);
			}
		}

		return list;
	}

	/**
	 * Check if data attributes values were changed.
	 *
	 * @return true if attributes values were changed
	 */
	public boolean isChanged()
	{
		boolean changed = false;

		Collection collection = getAttributes();

		if(collection != null && !collection.isEmpty())
		{
			Iterator iterator = collection.iterator();

			while(!changed && iterator != null && iterator.hasNext())
			{
				Attribute attr = (Attribute) iterator.next();
				changed = attr.isChanged();
			}
		}

		return changed;
	}

	/**
	 * Mark all attributes unchanged.
	 */
	protected void setAttributesUnchanged()
	{
		Collection collection = getAttributes();

		if(collection != null && !collection.isEmpty())
		{
			Iterator iterator = collection.iterator();

			while(iterator != null && iterator.hasNext())
			{
				Attribute attr = (Attribute) iterator.next();
				attr.setUnchanged();
			}
		}
	}

	/**
	 * Populate and set form attributes using original form details. All attributes will have null "default" value.
	 *
	 * @param arsession user session
	 * @throws AREasyException if any error will occur
	 */
	public void setAttributes(ServerConnection arsession) throws AREasyException
	{
		try
		{
			// Retrieve the detail info of all fields from the form.
			List<Field> fields = arsession.getContext().getListFieldObjects(getFormName(), Constants.AR_FIELD_TYPE_DATA, 0);

			for(int i = 0; fields != null && i < fields.size(); i++)
			{
				Field field = fields.get(i);

				String name = field.getName();
				int id = field.getFieldID();
				int type = field.getDataType();

				Attribute attr = getAttribute(id);

				if(attr == null)
				{
					attr = new Attribute(id);
					attr.setDefaultNullValue();
					attr.setType(type);
				}

				attr.setLabel(name);

				setAttribute(attr);
			}
		}
		catch(Throwable th)
		{
			if(th instanceof AREasyException) throw (AREasyException)th;
				else throw new AREasyException(th);
		}
	}

	/**
	 * Check if this core item record instance was read and exist properly.
	 *
	 * @return true if exist (so has filled the request id attribute)
	 */
	public final boolean exists()
	{
		return StringUtility.isNotEmpty(getEntryId());
	}

	private int getQualificationCriteriaOperator(Attribute attr)
	{
		//set operator and get real value structure.
		boolean found = false;
		int index = 0;

		int output = Constants.AR_REL_OP_EQUAL;
		String value = attr.getStringValue();

		//check actual value
		if(attr.getValue() == null || value == null) return output;

		while(!found && index < operatorStrings.length)
		{
			if(value.indexOf(operatorStrings[index], 0) >= 0)
			{
				//check escape action
				if(value.indexOf(operatorStrings[index], 0) > 0 && value.charAt(value.indexOf(operatorStrings[index], 0) - 1) == '\\')
				{
					setAttribute(attr.getId(), value.replaceFirst("\\\\" + operatorStrings[index], operatorStrings[index]));
					index++;
					continue;
				}

				//mark that the operator is found
				found = true;

				//operator value
				output = operatorIndex[index];

				//operator replacement
				if(!operatorStrings[index].equals("%")) attr.setConvertedValue(StringUtility.replace(value, operatorStrings[index], "").trim());
			}

			index++;
		}

		return output;
	}

	/**
	 * Get an exact search using a specific qualification criteria
	 *
	 * @param arsession user session
	 * @param qualInfo specific qualification criteria
	 * @param maxlimit maximum limit for search
	 * @throws AREasyException if any error will occur.
	 * @return <code>ListEntryObject</code> structure which includes the list of entries and the fields list of the specified form.
	 */
	protected List getObjectsList(ServerConnection arsession, QualifierInfo qualInfo, int maxlimit) throws AREasyException
	{
		if(getFormName() == null) throw new AREasyException("Form name is not specified");

		if (!ignoreNullValues() || qualInfo != null)
		{
			try
			{
				OutputInteger nMatches = new OutputInteger();

				List<SortInfo> sortOrder = new ArrayList<SortInfo>();
				sortOrder.add(getSortInfo());

				//Retrieve all entries
				List<Entry> entries = null;

				if(hasSimplifiedStructure())
				{
					entries = arsession.getContext().getListEntryObjects(getFormName(), qualInfo, 0, maxlimit, sortOrder, null, true, nMatches);
				}
				else
				{
					entries = arsession.getContext().getListEntryObjects(getFormName(), qualInfo, 0, maxlimit, sortOrder, getEntryFieldIds(arsession), true, nMatches);
				}

				return entries;
			}
			catch(ARException are)
			{
				throw new AREasyException(are);
			}
		}
		else return null;
	}

	/**
	 * Get the total number of entries provided by the qualification criteria generated by structure attributes
	 *
	 * @param arsession user session
	 * @return total number of records in the <code>CoreItem</code> entity
	 * @throws AREasyException if any error occurs
	 */
	public int count(ServerConnection arsession) throws AREasyException
	{
		Collection collection = new Vector();

		if(ignoreUnchangedValues())
		{
			Collection data = getChangedAttributes();
			if(data != null && !data.isEmpty()) collection.addAll(data);
		}
		else
		{
			Collection data = getAttributes();
			if(data != null && !data.isEmpty()) collection.addAll(data);
		}

		return count(arsession, collection);
	}

	/**
	 * Get the total number of entries provided by a specific qualification criteria
	 *
	 * @param arsession user session
	 * @param collection collection structure that includes field details to compose qualification criteria
	 * @return total number of records in the <code>CoreItem</code> entity
	 * @throws AREasyException if any error occurs
	 */
	protected int count(ServerConnection arsession, Collection collection) throws AREasyException
	{
		//count data
		return count(arsession, getQualificationInfo(collection));
	}

	/**
	 * Get the total number of entries provided by a specific qualification criteria
	 *
	 * @param arsession user session
	 * @param map map structure that includes field details to compose qualification criteria
	 * @return total number of records in the <code>CoreItem</code> entity
	 * @throws AREasyException if any error occurs
	 */
	public int count(ServerConnection arsession, Map map) throws AREasyException
	{
		//count data
		return count(arsession, getQualificationInfo(map));
	}

	/**
	 * Get the total number of entries provided by a specific qualification criteria
	 *
	 * @param arsession user session
	 * @param qualification string qualification criteria
	 * @return total number of records in the <code>CoreItem</code> entity
	 * @throws AREasyException if any error occurs
	 */
	public int count(ServerConnection arsession, String qualification) throws AREasyException
	{
		List<Field> fields = null;
		QualifierInfo qualInfo = null;

		try
		{
			// Create the search qualifier.
			if(qualification != null)
			{
				// Retrieve the detail info of all fields from the form.
				fields = getListFieldObjects(arsession);

				qualInfo = arsession.getContext().parseQualification(qualification, fields, null, Constants.AR_QUALCONTEXT_DEFAULT);
			}
			else qualInfo = new QualifierInfo();
		}
		catch(Throwable th)
		{
			if(th instanceof AREasyException) throw (AREasyException)th;
				else throw new AREasyException(th);
		}

		//count data
		return count(arsession, qualInfo);
	}

	/**
	 * Get the total number of entries provided by a specific qualification criteria
	 *
	 * @param arsession user session
	 * @param qualInfo specific qualification criteria
	 * @return total number of records in the <code>CoreItem</code> entity
	 * @throws AREasyException if any error occurs
	 */
	protected int count(ServerConnection arsession, QualifierInfo qualInfo) throws AREasyException
	{
		if(getFormName() == null) throw new AREasyException("Form name is not specified");

		if (qualInfo != null)
		{
			int fields[] = new int[1];
			fields[0] = 1;

			try
			{
				//Retrieve all entries
				List<Entry> entries = arsession.getContext().getListEntryObjects(getFormName(), qualInfo, 0, 0, null, fields, true, new OutputInteger());

				if(entries != null) return entries.size();
					else return 0;
			}
			catch(ARException are)
			{
				throw new AREasyException(are);
			}
		}
		else return 0;
	}

	/**
	 * Get an exact search using a collection of attribute fields, composing fixed values and <code>AND</code> operators.
	 * Also the searching operation is performed using <b>Request ID</b> attribute if is filled.
	 *
	 * @param arsession user session
	 * @param collection collection of field attributes
	 * @throws AREasyException if any error will occure.
	 * @return <code>List</code> structure which includes the list of entries and the fields list of the specified form.
	 */
	protected List getObjectsList(ServerConnection arsession, Collection collection) throws AREasyException
	{
		return getObjectsList( arsession, getQualificationInfo(collection), 0);
	}

	/**
	 * Get an exact search using mapped fields, fixed values and <code>AND</code> operators.
	 *
	 * @param arsession user session
	 * @param map map with field ids and values
	 * @return an array with entries in ARS API format.
	 * @throws AREasyException if any error will occure.
	 */
	protected List getObjectsList(ServerConnection arsession, Map map) throws AREasyException
	{
		return getObjectsList(arsession, map, 0);
	}

	/**
	 * Get an exact search using mapped fields, fixed values and <code>AND</code> operators.
	 *
	 * @param arsession user session
	 * @param map map with field ids and values
	 * @param maxlimit maximum limit for search
	 * @return an array with entries in ARS API format.
	 * @throws AREasyException if any error will occure.
	 */
	protected List getObjectsList(ServerConnection arsession, Map map, int maxlimit) throws AREasyException
	{
		return getObjectsList(arsession, getQualificationInfo(map), maxlimit);
	}

	/**
	 * Get an exact search using only changed fields, fixed values and <code>AND</code> operators.
	 *
	 * @param arsession user session
	 * @return an array with entries in ARS API format.
	 * @throws AREasyException if any error will occure.
	 */
	protected List getObjectsList4ChangedAttributes(ServerConnection arsession) throws AREasyException
	{
		return getObjectsList(arsession, getChangedAttributes());
	}

	/**
	 * Get an exact search using all defined attribute fields, fixed values and <code>AND</code> operators.
	 *
	 * @param arsession user session
	 * @return an array with entries in ARS API format.
	 * @throws AREasyException if any error will occure.
	 */
	protected List getObjectsList4AllAttributes(ServerConnection arsession) throws AREasyException
	{
		return getObjectsList(arsession, getAttributes());
	}

	/**
	 * Get qualification structure using the specified mapping attributes collection.
	 *
	 * @param map collection mapping of field attributes
	 * @return <code>QualifierInfo</code> structure which includes the qualification mapping for searches and read events.
	 */
	protected QualifierInfo getQualificationInfo(Map map)
	{
		QualifierInfo qualInfo = null;

		if(map != null && !map.isEmpty())
		{
			Iterator iterator = map.keySet().iterator();

			while(iterator != null && iterator.hasNext())
			{
				Object key = iterator.next();
				Object value = map.get(key);

				if(key == null || StringUtility.isEmpty( String.valueOf(key) ) || (ignoreNullValues() && value == null)) continue;

				Attribute attr = new Attribute(String.valueOf(key), value);
				int operator = getQualificationCriteriaOperator(attr);
				value = attr.getValue();

				if(qualInfo == null) qualInfo = new QualifierInfo( new RelationalOperationInfo(operator, new ArithmeticOrRelationalOperand(attr.getFieldIdFormat()), new ArithmeticOrRelationalOperand(attr.getValueFormat()) ) );
					else qualInfo = new QualifierInfo(QualifierInfo.AR_COND_OP_AND,  qualInfo, new QualifierInfo( new RelationalOperationInfo(operator, new ArithmeticOrRelationalOperand(attr.getFieldIdFormat()), new ArithmeticOrRelationalOperand( attr.getValueFormat() ) ) ) );
			}
		}
		else qualInfo = new QualifierInfo();

		return qualInfo;
	}

	/**
	 * Get qualification structure using the specified attribute collection.
	 *
	 * @param collection collection of field attributes
	 * @return <code>QualifierInfo</code> structure which includes the qualification mapping for searches and read events.
	 */
	protected QualifierInfo getQualificationInfo(Collection collection)
	{
		QualifierInfo qualInfo = null;

		if(StringUtility.isNotEmpty(getEntryId())) qualInfo = new QualifierInfo( new RelationalOperationInfo(Constants.AR_REL_OP_EQUAL, new ArithmeticOrRelationalOperand(new Integer(1)), new ArithmeticOrRelationalOperand(new Value(getEntryId())) ) );

		if(collection != null && !collection.isEmpty())
		{
			Iterator iterator = collection.iterator();

			while(iterator != null && iterator.hasNext())
			{
				Attribute attr = (Attribute) iterator.next();

				if(attr == null || (ignoreNullValues() && attr.getValue() == null)) continue;

				int operator = getQualificationCriteriaOperator(attr);

				if(qualInfo == null) qualInfo = new QualifierInfo( new RelationalOperationInfo(operator, new ArithmeticOrRelationalOperand(attr.getFieldIdFormat()), new ArithmeticOrRelationalOperand(attr.getValueFormat()) ) );
					else qualInfo = new QualifierInfo(QualifierInfo.AR_COND_OP_AND,  qualInfo, new QualifierInfo( new RelationalOperationInfo(operator, new ArithmeticOrRelationalOperand(attr.getFieldIdFormat()), new ArithmeticOrRelationalOperand( attr.getValueFormat() ) ) ) );
			}
		}
		else qualInfo = new QualifierInfo();

		return qualInfo;
	}

	/**
	 *  Remove data structure(s) found based on matched criteria filled in the current structure.
	 *
	 * @param arsession user session
	 * @throws AREasyException if any error will occur.
	 */
	public void remove(ServerConnection arsession) throws AREasyException
	{
		Collection collection = new Vector();

		//put entry id if was specified
		if(exists())
		{
			collection.add(new Attribute(1, getEntryId()));
		}
		else
		{
			if(ignoreUnchangedValues())
			{
				Collection data = getChangedAttributes();
				if(data != null && !data.isEmpty()) collection.addAll(data);
			}
			else
			{
				Collection data = getAttributes();
				if(data != null && !data.isEmpty()) collection.addAll(data);
			}
		}

		remove(arsession, collection);
	}

	/**
	 * Remove data structure(s) found based on matched criteria filled in the current structure.
	 *
	 * @param arsession user session
	 * @param collection attribute's collection
	 * @throws AREasyException if any error will occur.
	 */
	protected void remove(ServerConnection arsession, Collection collection) throws AREasyException
	{
		try
		{
			if(simulation)
			{
				RuntimeLogger.info("DELETE: " + toFullString());
			}
			else
			{
				//get record data
				List objects = getObjectsList(arsession, collection);

				for(int i = 0; objects != null && i < objects.size(); i++)
				{
					arsession.getContext().deleteEntry(getFormName(), ((Entry)objects.get(i)).getEntryId(), 0);
				}

				//reset data
				resetEntryId();
				setDefaultData(collection);
			}
		}
		catch(Throwable th)
		{
			throw new AREasyException("Error removing data entry in '" + getFormName() + "' form, for '" + this + "' data structure" + ( th.getMessage() != null ? ". " + th.getMessage() : ""), th);
		}
	}

	/**
	 * Read data structure and fill all found field attributes. This method will consider only changed field to perform a searching
	 * operation. Attributes will be loaded in the structure only if there is exactly one search result.
         * Nothing is done when no entry or more than one entry is found.
	 *
	 * @param arsession user session
	 * @throws AREasyException if any error will occur.
	 */
	public void read(ServerConnection arsession) throws AREasyException
	{
		read(arsession, false);
	}

	/**
	 * Read data structure and fill all found field attributes. This method will consider only changed fields to perform a searching
	 * operation and the entry id (if is filled). Attributes will be loaded in the structure only if there is exactly one search result.
     * Nothing is done when no entry or more than one entry is found.
	 *
	 * @param arsession user session
	 * @param useId specify if in the qualification criteria will be use also entry id value (if is already filled)
	 * @throws AREasyException if any error will occur.
	 */
	public void read(ServerConnection arsession, boolean useId) throws AREasyException
	{
		Collection collection = new Vector();

		if(ignoreUnchangedValues())
		{
			Collection data = getChangedAttributes();
			if(data != null && !data.isEmpty()) collection.addAll(data);
		}
		else
		{
			Collection data = getAttributes();
			if(data != null && !data.isEmpty()) collection.addAll(data);
		}

		//put entry id if was specified
		if(getEntryId() != null && useId) collection.add(new Attribute(1, getEntryId()));

		read(arsession, collection);
	}

	/**
	 * Read data structure and fill all found field attributes. This method will consider only fields defined in this map.
	 * Attributes will be loaded in the structure only if there is exactly one search result.
         * Nothing is done when no entry or more than one entry is found.
         *
	 * @param arsession user session
	 * @param map user map with field ids and field values
	 * @throws AREasyException if any error will occur.
	 */
	public void read(ServerConnection arsession, Map map) throws AREasyException
	{
		QualifierInfo qualInfo = getQualificationInfo(map);

		//read data
		read(arsession, qualInfo);
	}

	/**
	 * Read data structure and fill all found field attributes. This method will consider all field attributes to perform a searching
	 * operation.
	 *
	 * @param arsession user session
	 * @param qualification string qualification criteria for search
	 * @throws AREasyException if any error will occur.
	 */
	public void read(ServerConnection arsession, String qualification) throws AREasyException
	{
		List<Field> fields = null;
		QualifierInfo qualInfo = null;

		try
		{
			// Retrieve the detail info of all fields from the form.
			fields = getListFieldObjects(arsession);

			// Create the search qualifier.
			qualInfo = arsession.getContext().parseQualification(qualification, fields, null, Constants.AR_QUALCONTEXT_DEFAULT);
		}
		catch(Throwable th)
		{
			if(th instanceof AREasyException) throw (AREasyException)th;
				else throw new AREasyException(th);
		}

		//read data
		read(arsession, qualInfo);
	}

	/**
	 * Read data structure and fill all found field attributes. This method will consider all field attributes to perform a searching
	 * operation. Attributes will be loaded in the structure only if there is exactly one search result.
         * Nothing is done when no entry or more than one entry is found.
	 *
	 * @param arsession user session
	 * @param collection attribute's collection
	 * @throws AREasyException if any error will occur.
	 */
	protected void read(ServerConnection arsession, Collection collection) throws AREasyException
	{
		QualifierInfo qualInfo = getQualificationInfo(collection);

		//read data
		read(arsession, qualInfo);
	}

	/**
	 * Read data structure and fill all found field attributes. This method will consider all field attributes to perform a searching
	 * operation.
	 *
	 * @param arsession user session
	 * @param qualInfo qualification criteria structure for search
	 * @throws AREasyException if any error will occur.
	 */
	protected void read(ServerConnection arsession, QualifierInfo qualInfo) throws AREasyException
	{
		try
		{
			//get record data
			List objects = getObjectsList(arsession, qualInfo, 2);

			if(objects != null)
			{
				if((objects.size() == 1) || (objects.size() > 1 && isFirstMatchReading())) fetch(arsession, (Entry)objects.get(0));
					else if(objects.size() > 1 && !isFirstMatchReading()) logger.debug("Multiple matches for structure: " + this);
						else if(objects.size() == 0) logger.debug("No match found: " + this);
			}
			else logger.debug("No requests match for structure: " + this);
		}
		catch(Throwable th)
		{
			throw new AREasyException("Error reading data in '" + getFormName() + "' form, for '" + qualInfo + "' qualification" + (th.getMessage() != null ? ". " + th.getMessage() : ""), th);
		}
	}

	/**
	 * Get the list of field objects related to a form. Because the operation is quite slow the method use the <code>RuntimeServer</code> cache
	 * layer, each entry have TTL = 2h
	 *
	 * @param arsession user session
	 * @return the list of field objects
	 * @throws AREasyException if any error will occur.
	 */
	private List<Field> getListFieldObjects(ServerConnection arsession) throws AREasyException
	{
		List<Field> fields = null;
		String cacheKey = arsession.getServerName() + "::" + getFormName() + "::fieldObjectsList";
		try
		{
			// Retrieve the detail info of all fields from the form.
			fields = (List<Field>)RuntimeServer.getCache().get(cacheKey);

			if(fields == null)
			{
				fields = arsession.getContext().getListFieldObjects(getFormName());
				RuntimeServer.getCache().add(cacheKey, fields, 7200);
			}
		}
		catch(Throwable th)
		{
			if(th instanceof AREasyException) throw (AREasyException)th;
				else throw new AREasyException(th);
		}

		return fields;
	}

	/**
	 * Read data structure and fill all found field attributes. This method will consider all field attributes to perform a searching
	 * operation.
	 *
	 * @param arsession user session
	 * @param requestId record identifier (for RequestID field)
	 * @throws AREasyException if any error will occur.
	 */
	public void readById(ServerConnection arsession, String requestId) throws AREasyException
	{
		if(getFormName() == null) throw new AREasyException("Form name is not specified");

		try
		{
			Entry entry = arsession.getContext().getEntry(getFormName(), requestId, null);
			fetch(arsession, entry);
		}
		catch(Throwable th)
		{
			throw new AREasyException("Error reading data in '" + getFormName() + "' form, for '" + requestId + "' request Id" + (th.getMessage() != null ? ". " + th.getMessage() : ""), th);
		}
	}

	/**
	 * Read data structure and fill all found field attributes. This method will consider all attributes (changed or unchanged)
	 * but excluding core and floating attributes (1,3,5,6) and all attributes that have null values.
	 *
	 * @param arsession user session
	 * @throws AREasyException if any error will occur.
	 */
	public void readByNonCoreFloating(ServerConnection arsession) throws AREasyException
	{
		Collection collection = null;

		//reset entry id (if exists)
		resetEntryId();

		//check if instance id field is defined.
		if(containsAttributeField(179))
		{
		    collection = new Vector();
			collection.add(getAttribute(179));
		}
		else
		{
			Collection current = getAttributes();

			if(current != null && !current.isEmpty())
			{
				Iterator iterator = collection.iterator();

				while(iterator != null && iterator.hasNext())
				{
					Attribute attr = (Attribute) iterator.next();

					if (attr.getValue() != null && attr.getNumberId() != 1 && attr.getNumberId() != 3 && attr.getNumberId() != 5 && attr.getNumberId() != 6)
					{
						collection.add(attr);
					}
				}
			}
		}

		read(arsession, collection);
	}

	/**
	 * Search all core item structures in the ARS server using this core item template and specified attribute's collection for searching
	 *
	 * @param arsession user session
	 * @param map mapping with field ids and field values.
	 * @return a list with all found <code>CoreItem</code> instances.
	 * @throws AREasyException if any error will occur
	 */
	public List search(ServerConnection arsession, Map map) throws AREasyException
	{
		//search data
		return search(arsession, getQualificationInfo(map), 0);
	}

	/**
	 * Search all core item structures in the ARS server using this core item template and specified attribute's collection for searching
	 *
	 * @param arsession user session
	 * @param map mapping with field ids and field values.
	 * @param maxlimit maximum limit for search
	 * @return a list with all found <code>CoreItem</code> instances.
	 * @throws AREasyException if any error will occur
	 */
	public List search(ServerConnection arsession, Map map, int maxlimit) throws AREasyException
	{
		//search data
		return search(arsession, getQualificationInfo(map), maxlimit);
	}

	/**
	 * Search all core item structures in the ARS server using this core item template and specified attribute's collection for searching
	 *
	 * @param arsession user session
	 * @param collection attribute's collection
	 * @return a list with all found <code>CoreItem</code> instances.
	 * @throws AREasyException if any error will occur
	 */
	protected List search(ServerConnection arsession, Collection collection) throws AREasyException
	{
		return search(arsession, collection, 0);
	}

	/**
	 * Search all core item structures in the ARS server using this core item template and specified attribute's collection for searching
	 *
	 * @param arsession user session
	 * @param collection attribute's collection
	 * @param maxlimit maximum limit for search
	 * @return a list with all found <code>CoreItem</code> instances.
	 * @throws AREasyException if any error will occur
	 */
	protected List<CoreItem> search(ServerConnection arsession, Collection collection, int maxlimit) throws AREasyException
	{
		//search data
		return search(arsession, getQualificationInfo(collection), maxlimit);
	}

	/**
	 * Search all core item structures in the ARS server using a string format of the qualification string
	 *
	 * @param arsession user session
	 * @param qualification qualification string
	 * @return a list with all found <code>CoreItem</code> instances.
	 * @throws AREasyException if any error will occur
	 */
	public List search(ServerConnection arsession, String qualification) throws AREasyException
	{
		return search(arsession, qualification, 0);
	}

	/**
	 * Search all core item structures in the ARS server using a string format of the qualification string
	 *
	 * @param arsession user session
	 * @param qualification qualification string
	 * @return a list with all found <code>CoreItem</code> instances.
	 * @param maxlimit maximum limit for search
	 * @throws AREasyException if any error will occur
	 */
	public List search(ServerConnection arsession, String qualification, int maxlimit) throws AREasyException
	{
		List<Field> fields = null;
		QualifierInfo qualInfo = null;

		try
		{
			// Create the search qualifier.
			if(qualification != null)
			{
				// Retrieve the detail info of all fields from the form.
				fields = getListFieldObjects(arsession);

				qualInfo = arsession.getContext().parseQualification(qualification, fields, null, Constants.AR_QUALCONTEXT_DEFAULT);
			}
			else qualInfo = new QualifierInfo();
		}
		catch(Throwable th)
		{
			if(th instanceof AREasyException) throw (AREasyException)th;
				else throw new AREasyException(th);
		}

		//search data
		return search(arsession, qualInfo, maxlimit);
	}

	/**
	 * Search all core item structures in the ARS server using this core item template and specified attribute's collection for searching
	 *
	 * @param arsession user session
	 * @param qualInfo qualification criteria
	 * @param maxlimit maximum limit for search
	 * @return a list with all found <code>CoreItem</code> instances.
	 * @throws AREasyException if any error will occur
	 */
	protected List<CoreItem> search(ServerConnection arsession, QualifierInfo qualInfo, int maxlimit) throws AREasyException
	{
		//validate associated form name.
		if(getFormName() == null) throw new AREasyException("Associated form name is null");

		try
		{
			//get record data
			List objects = getObjectsList(arsession, qualInfo, maxlimit);

			return fetch(arsession, objects);
		}
		catch(Throwable th)
		{
			throw new AREasyException("Error searching data in '" + getFormName() + "' form, for '" + qualInfo + "' qualification" + (th.getMessage() != null ? ". " + th.getMessage() : ""), th);
		}
	}

	/**
	 * Fetch an AREasy entry
	 *
	 * @param arsession user session
	 * @param entry entry structure returned by AR System server
	 * @throws AREasyException if any error will occur
	 */
	protected void fetch(ServerConnection arsession, Entry entry) throws AREasyException
	{
		if(entry != null) setDefaultData(entry);
	}

	/**
	 * Fetch all entries delivered a search operations
	 *
	 * @param arsession user session
	 * @param objects list returned by AR System server
	 * @return a list with all found <code>CoreItem</code> instances.
	 * @throws AREasyException if any error will occur
	 */
	protected List<CoreItem> fetch(ServerConnection arsession, List objects) throws AREasyException
	{
		List<CoreItem> list = new Vector<CoreItem>();

		for(int i = 0; objects != null && i < objects.size(); i++)
		{
			CoreItem item = getInstance();
			item.setIgnoreNullValues(ignoreNullValues());
			item.setIgnoreUnchangedValues(ignoreUnchangedValues());
			item.setSimplifiedStructure(hasSimplifiedStructure());

			if(item.formName == null) item.setFormName(getFormName());

			item.fetch(arsession, (Entry)objects.get(i));
			list.add(item);
		}

		return list;
	}

	/**
	 * Search all core item structures in the ARS server using this core item template and only changed attributes (for searching)
	 *
	 * @param arsession user session
	 * @return a list with all found <code>CoreItem</code> instances.
	 * @throws AREasyException if any error will occur
	 */
	public List<CoreItem> search(ServerConnection arsession) throws AREasyException
	{
		return search(arsession, 0);
	}

	/**
	 * Search all core item structures in the ARS server using this core item template and only changed attributes (for searching)
	 *
	 * @param arsession user session
	 * @param maxlimit maximum limit for search
	 * @return a list with all found <code>CoreItem</code> instances.
	 * @throws AREasyException if any error will occur
	 */
	public List<CoreItem> search(ServerConnection arsession, int maxlimit) throws AREasyException
	{
		Collection collection = new Vector();

		if(ignoreUnchangedValues())
		{
			Collection data = getChangedAttributes();
			if(data != null && !data.isEmpty()) collection.addAll(data);
		}
		else
		{
			Collection data = getAttributes();
			if(data != null && !data.isEmpty()) collection.addAll(data);
		}

		return search(arsession, collection, maxlimit);
	}

	/**
	 * Create an entry record in the ARS server using an attribute's collection and then is read it
	 * to transform it into a valid core item instance.
	 *
	 * @param arsession user session
	 * @param collection attribute's collection
	 * @throws AREasyException if any error will occur
	 */
	protected void create(ServerConnection arsession, Collection collection) throws AREasyException
	{
		//validate associated form name.
		if(getFormName() == null) throw new AREasyException("Associated form name is null");

		if(collection != null && !collection.isEmpty())
		{
			Entry entry = new Entry();
			Iterator iterator = collection.iterator();

			while(iterator != null && iterator.hasNext())
			{
				Attribute attr = (Attribute) iterator.next();

				if ((ignoreNullValues() && attr != null && attr.getValue() != null) || (!ignoreNullValues() && attr != null))  entry.put(attr.getFieldIdFormat(), attr.getValueFormat());
			}

			try
			{
				if(simulation)
				{
					RuntimeLogger.info("CREATE: " + toFullString());
				}
				else
				{
					//create entry and read it
					String entryId = arsession.getContext().createEntry(getFormName(), entry);

					//read entry
					if(StringUtility.isNotEmpty(entryId))
					{
						clear();
						readById(arsession, entryId);
					}
					else if(attributes.containsKey("179"))
					{
						String instanceid = ((Attribute)attributes.get("179")).getStringValue();

						clear();
						setIgnoreUnchangedValues(true);
						setAttribute(179, instanceid);
						read(arsession);
					}
					else logger.warn("Item creation action was performed but the structure could be read: " + this);
				}
			}
			catch(Throwable th)
			{
				throw new AREasyException("Error creating data entry in '" + getFormName() + "' form, for '" + this + "' data structure" + (th.getMessage() != null ? ". " + th.getMessage() : ""), th);
			}
		}
		else logger.warn("No record created because attributes collection is null: " + this);
	}

	/**
	 * Create an entry record in the ARS server using core item attributes and then is read it
	 * to transform it into a valid core item instance.
	 *
	 * @param arsession user session
	 * @throws AREasyException if any error will occur
	 */
	public void create(ServerConnection arsession) throws AREasyException
	{
		Collection collection = new Vector();

		if(ignoreUnchangedValues())
		{
			Collection data = getChangedAttributes();
			if(data != null && !data.isEmpty()) collection.addAll(data);
		}
		else
		{
			Collection data = getAttributes();
			if(data != null && !data.isEmpty()) collection.addAll(data);
		}

		create(arsession, collection);
	}


    /**
	 * Update the current core item instance based on specified attributes.
	 *
	 * @param arsession user session
	 * @param collection attribute's collection
	 * @throws AREasyException if any error will occur
	 */
	protected void update(ServerConnection arsession, Collection collection) throws AREasyException
	{
		if(StringUtility.isEmpty(getEntryId())) throw new AREasyException("This item instance could be updated because doesn't have an entry id");

		if(collection != null && !collection.isEmpty())
		{
			try
			{
				Entry entry = new Entry();
				//entry.setEntryId(getEntryId());

				Iterator iterator = collection.iterator();

				while(iterator != null && iterator.hasNext())
				{
					Attribute attr = (Attribute) iterator.next();

					if ((ignoreNullValues() && attr != null && attr.getValue() != null) || (!ignoreNullValues() && attr != null)) entry.put(attr.getFieldIdFormat(), attr.getValueFormat());
				}

				if(simulation)
				{
					RuntimeLogger.info("UPDATE: " + toFullString());
				}
				else
				{
					//create entry and read it
					arsession.getContext().setEntry(getFormName(), getEntryId(), entry, null, 0);
				}
			}
			catch(Throwable th)
			{
				throw new AREasyException("Error updating data entry in '" + getFormName() + "' form, for '" + this + "' data structure" + (th.getMessage() != null ? ". " + th.getMessage() : ""), th);
			}
		}
		else logger.debug("Nothing to be updated because attributes collection is null: " +  this);

		//mark unchanged all used fields
		setAttributesUnchanged();
	}

	/**
	 * Update the current core item instance based on changed attributes.
	 *
	 * @param arsession user session
	 * @throws AREasyException if any error will occur
	 */
	public void update(ServerConnection arsession) throws AREasyException
	{
		Collection collection = new Vector();

		if(ignoreUnchangedValues())
		{
			Collection data = getChangedAttributes();
			if(data != null && !data.isEmpty()) collection.addAll(data);
		}
		else
		{
			Collection data = getAttributes();
			if(data != null && !data.isEmpty()) collection.addAll(data);
		}

		update(arsession, collection);
	}

    /**
     * Merge an entry record in the ARS server using core item attributes and then is read it
     * to transform it into a valid core item instance. If Entry ID already exists, an error is generated.
     *
     * @param arsession user session
     * @throws AREasyException if any error will occur
     */
    public void merge(ServerConnection arsession) throws AREasyException
    {
        merge(arsession, Constants.AR_MERGE_ENTRY_DUP_ERROR);
    }

    /**
     * Merge an entry record in the ARS server using core item attributes and then is read it
     * to transform it into a valid core item instance. If Entry ID already exists, an action is performed depending on the value of parameter nMergeType:
     *
     * @param arsession user session
     * @param nMergeType A value indicating the action to take if the Entry ID already exists in the target form.
     *      This parameter is ignored if you do not specify the Entry ID or the ID specified does not conflict with existing entry IDs.
     *      <ul>
     *          <li>Generate an error (AR_MERGE_ENTRY_DUP_ERROR) = 1</li>
     *          <li>Create a new entry with a new ID (AR_MERGE_ENTRY_DUP_NEW_ID) = 2</li>
     *          <li>Delete the existing entry and create a new one in its place (AR_MERGE_ENTRY_DUP_OVERWRITE) = 3</li>
     *          <li>Update the fields specified in fieldList in the existing entry (AR_MERGE_ENTRY_DUP_MERGE) = 4</li>
     *      </ul>
     *      To omit some field validation steps, add the appropriate increments to the merge type.
     *      <ul>
     *          <li>Allow NULL in required fields (not applicable for the Submitter, Status, or Short-Description core fields) (AR_MERGE_NO_REQUIRED_INCREMENT) = 1024</li>
     *           <li>Skip field pattern checking (including $MENU$) (AR_MERGE_NO_PATTERNS_INCREMENT) = 2048</li>
     *      </ul>
     * @throws AREasyException if any error will occur
     */
    public void merge(ServerConnection arsession, int nMergeType) throws AREasyException
    {
		merge(arsession, nMergeType, (List) null);
    }

    /**
     * Merge an entry record in the ARS server using core item attributes and then is read it
     * to transform it into a valid core item instance. If Entry ID already exists, an action is performed depending on the value of parameter nMergeType:
     *
     * @param arsession user session
     * @param nMergeType A value indicating the action to take if the Entry ID already exists in the target form.
     *      This parameter is ignored if you do not specify the Entry ID or the ID specified does not conflict with existing entry IDs.
     *      <ul>
     *          <li>Generate an error (AR_MERGE_ENTRY_DUP_ERROR) = 1</li>
     *          <li>Create a new entry with a new ID (AR_MERGE_ENTRY_DUP_NEW_ID) = 2</li>
     *          <li>Delete the existing entry and create a new one in its place (AR_MERGE_ENTRY_DUP_OVERWRITE) = 3</li>
     *          <li>Update the fields specified in fieldList in the existing entry (AR_MERGE_ENTRY_DUP_MERGE) = 4</li>
     *      </ul>
     *      To omit some field validation steps, add the appropriate increments to the merge type.
     *      <ul>
     *          <li>Allow NULL in required fields (not applicable for the Submitter, Status, or Short-Description core fields) (AR_MERGE_NO_REQUIRED_INCREMENT) = 1024</li>
     *           <li>Skip field pattern checking (including $MENU$) (AR_MERGE_NO_PATTERNS_INCREMENT) = 2048</li>
     *      </ul>
	 * @param matchingField by default the matching is done based on the request Id. In case of you want to do the match using other field you have to specify this parameter
     * @throws AREasyException if any error will occur
     */
    public void merge(ServerConnection arsession, int nMergeType, String matchingField) throws AREasyException
	{
		if(matchingField != null)
		{
			List list = new ArrayList();
			list.add(matchingField);

			merge(arsession, nMergeType, list);
		}
		else merge(arsession, nMergeType, (List) null);
	}

    /**
     * Merge an entry record in the ARS server using core item attributes and then is read it
     * to transform it into a valid core item instance. If Entry ID already exists, an action is performed depending on the value of parameter nMergeType:
     *
     * @param arsession user session
     * @param nMergeType A value indicating the action to take if the Entry ID already exists in the target form.
     *      This parameter is ignored if you do not specify the Entry ID or the ID specified does not conflict with existing entry IDs.
     *      <ul>
     *          <li>Generate an error (AR_MERGE_ENTRY_DUP_ERROR) = 1</li>
     *          <li>Create a new entry with a new ID (AR_MERGE_ENTRY_DUP_NEW_ID) = 2</li>
     *          <li>Delete the existing entry and create a new one in its place (AR_MERGE_ENTRY_DUP_OVERWRITE) = 3</li>
     *          <li>Update the fields specified in fieldList in the existing entry (AR_MERGE_ENTRY_DUP_MERGE) = 4</li>
     *      </ul>
     *      To omit some field validation steps, add the appropriate increments to the merge type.
     *      <ul>
     *          <li>Allow NULL in required fields (not applicable for the Submitter, Status, or Short-Description core fields) (AR_MERGE_NO_REQUIRED_INCREMENT) = 1024</li>
     *           <li>Skip field pattern checking (including $MENU$) (AR_MERGE_NO_PATTERNS_INCREMENT) = 2048</li>
     *      </ul>
	 * @param matchingFields by default the matching is done based on the request Id. In case of you want to do the match using a list of fields you have to specify this parameter
     * @throws AREasyException if any error will occur
     */
    public void merge(ServerConnection arsession, int nMergeType, List matchingFields) throws AREasyException
	{
		Collection collection = new Vector();

		if(ignoreUnchangedValues() && nMergeType == Constants.AR_MERGE_ENTRY_DUP_MERGE)
		{
			Collection data = getChangedAttributes();
			if(data != null && !data.isEmpty()) collection.addAll(data);
		}
		else
		{
			Collection data = getAttributes();
			if(data != null && !data.isEmpty()) collection.addAll(data);
		}

        merge(arsession, collection, nMergeType, matchingFields);
	}

    /**
     * Merge an entry record in the ARS server using an attribute's collection and then is read it
     * to transform it into a valid core item instance.
     *
     * @param arsession user session
     * @param collection attribute's collection
     * @throws AREasyException if any error will occur
	 * @param nMergeType merge type for conflicts
     */
    protected void merge(ServerConnection arsession, Collection collection, int nMergeType) throws AREasyException
	{
		merge(arsession, collection, nMergeType, (QualifierInfo) null);
	}

    /**
     * Merge an entry record in the ARS server using an attribute's collection and then is read it
     * to transform it into a valid core item instance.
     *
     * @param arsession user session
     * @param collection attribute's collection
     * @throws AREasyException if any error will occur
	 * @param nMergeType merge type for conflicts
	 * @param matchingFields list of field ids that will compose a qualification to identify source records for merge
     */
    protected void merge(ServerConnection arsession, Collection collection, int nMergeType, List matchingFields) throws AREasyException
	{
		QualifierInfo qualInfo = null;

		try
		{
			for(int i = 0; matchingFields != null && i < matchingFields.size(); i++)
			{
				String key = (String) matchingFields.get(i);
				Attribute attr = getAttribute(key);

				if(key == null || StringUtility.isEmpty( String.valueOf(key) ) || (ignoreNullValues() && attr.getValue() == null)) continue;

				if(qualInfo == null) qualInfo = new QualifierInfo( new RelationalOperationInfo(Constants.AR_REL_OP_EQUAL, new ArithmeticOrRelationalOperand(attr.getFieldIdFormat()), new ArithmeticOrRelationalOperand(attr.getValueFormat()) ) );
					else qualInfo = new QualifierInfo(QualifierInfo.AR_COND_OP_AND,  qualInfo, new QualifierInfo( new RelationalOperationInfo(Constants.AR_REL_OP_EQUAL, new ArithmeticOrRelationalOperand(attr.getFieldIdFormat()), new ArithmeticOrRelationalOperand( attr.getValueFormat() ) ) ) );
			}
		}
		catch(Throwable th)
		{
			throw new AREasyException("Error creating matching qualification: " + th.getMessage(), th);
		}

		merge(arsession, collection, nMergeType, qualInfo);
	}

    /**
     * Merge an entry record in the ARS server using an attribute's collection and then is read it
     * to transform it into a valid core item instance.
     *
     * @param arsession user session
     * @param collection attribute's collection
	 * @param nMergeType merge type for conflicts
	 * @param matching qualification structure to identify source records for merge
     * @throws AREasyException if any error will occur
     */
    protected void merge(ServerConnection arsession, Collection collection, int nMergeType, QualifierInfo matching) throws AREasyException
    {
        if(collection != null && !collection.isEmpty())
        {
            try
            {
				Entry entry = new Entry();
				entry.setEntryId(getEntryId());

				Iterator iterator = collection.iterator();

				while(iterator != null && iterator.hasNext())
				{
					Attribute attr = (Attribute) iterator.next();

					if ((ignoreNullValues() && attr != null && attr.getValue() != null) || (!ignoreNullValues() && attr != null)) entry.put(attr.getFieldIdFormat(), attr.getValueFormat());
				}

				//create entry and read it
				String entryId = null;

				if(simulation)
				{
					RuntimeLogger.info("MERGE: " + toFullString());
				}
				else
				{
					if(matching != null) entryId = arsession.getContext().mergeEntry(getFormName(), entry, nMergeType, matching, 0);
						else entryId = arsession.getContext().mergeEntry(getFormName(), entry, nMergeType);

					//read entry
					if(StringUtility.isNotEmpty(entryId))
					{
						clear();
						setEntryId(entryId);
						readById(arsession, entryId);
					}
					else if(attributes.containsKey("179"))
					{
						String instanceid = ((Attribute)attributes.get("179")).getStringValue();

						clear();
						setAttribute(179, instanceid);
						read(arsession);
					}
					else logger.debug("Item merge action was performed but the structure could be read: " + this);
				}
            }
            catch(Throwable th)
            {
				throw new AREasyException("Error merging data entry in '" + getFormName() + "' form, for '" + this + "' data structure" + (th.getMessage() != null ? ". " + th.getMessage() : ""), th);
            }
        }
        else logger.warn("No record merged because attributes collection is null: " + this);
    }

    /**
	 * Get string representation of the current core item structure.
	 *
	 * @return string data model.
	 */
	public String toString()
	{
		if(exists()) return "Item ("+ getFormName() + ") [Request Id = " + getEntryId() +
			   ", Status = " + getAttributeValue(7) +
			   ", Submitter = " + getAttributeValue(2) +
			   ", Instance Id = " + getAttributeValue(179) + "]";
		else return "Item ("+ getFormName() + ") [Request Id = " + getEntryId() + ", " + getStringFromChangedAttributes() + "]";
	}

	/**
	 * Get string representation of the current core item structure.
	 *
	 * @return string data model.
	 */
	public String toFullString()
	{
		String data = getStringFromAttributes();
		data = "Item ("+ getFormName() + ") [Request Id = " + getEntryId() + ", " + data + "]";

		return data;
	}

	protected String getStringFromAttributes()
	{
		String data = null;

		Collection collection = getAttributes();

		if(collection != null && !collection.isEmpty())
		{
			Iterator iterator = collection.iterator();

			while(iterator != null && iterator.hasNext())
			{
				Attribute attr = (Attribute) iterator.next();

				String label = attr.getLabel();
				if(StringUtility.isEmpty(label)) label = attr.getId();

				String value = attr.getStringValue();

				if(StringUtility.isEmpty(data)) data = label + " = " + value;
					else data += ", " + label + " = " + value;
			}
		}

		return data;
	}

	protected String getStringFromChangedAttributes()
	{
		String data = null;

		Collection collection = getChangedAttributes();

		if(collection != null && !collection.isEmpty())
		{
			Iterator iterator = collection.iterator();

			while(iterator != null && iterator.hasNext())
			{
				Attribute attr = (Attribute) iterator.next();

				String label = attr.getLabel();
				if(StringUtility.isEmpty(label)) label = attr.getId();

				String value = attr.getStringValue();

				if(StringUtility.isEmpty(data)) data = label + " = " + value;
					else data += ", " + label + " = " + value;
			}
		}

		return data;
	}

	/**
	 * Get the ARS form name for this record instance.
	 * @return the form name.
	 */
	public String getFormName()
	{
		return formName;
	}

	/**
	 * Set the corresponding form name for this core item instance
	 * @param formName ARS form name.
	 */
	public void setFormName(String formName)
	{
		this.formName = formName;
	}

	/**
	 * Check if all null values are ignored for all transactional and non transactional operations.
	 *
	 * @return true if null values will be ignored.
	 */
	public boolean ignoreNullValues()
	{
		return ignoreNullValues;
	}

	/**
	 * Specify if all null values are ignored for all transactional and non transactional operations.
	 *
	 * @param ignoreNullValues ignore null values flag
	 */
	public void setIgnoreNullValues(boolean ignoreNullValues)
	{
		this.ignoreNullValues = ignoreNullValues;
	}

	/**
	 * Specify if all null values are ignored for all transactional and non transactional operations.
	 */
	public void setNotIgnoreNullValues()
	{
		this.ignoreNullValues = false;
	}

	/**
	 * Check if unchanged value will be ignored in all transactional and non transactional operations.
	 *
	 * @return true if unchanged values will be ignored.
	 */
	public boolean ignoreUnchangedValues()
	{
		return ignoreUnchangedValues;
	}

	/**
	 * Specify if all unchanged values are ignored for all transactional and non transactional operations.
	 *
	 * @param ignoreUnchangedValues ignore unchanged values flag
	 */
	public void setIgnoreUnchangedValues(boolean ignoreUnchangedValues)
	{
		this.ignoreUnchangedValues = ignoreUnchangedValues;
	}

	/**
	 * Specify if all unchanged values are ignored for all transactional and non transactional operations.
	 */
	public void setNotIgnoreUnchangedValues()
	{
		this.ignoreUnchangedValues = false;
	}

	/**
	 * Check if the transaction will use "fast return" mechanism avoiding to re-read the submitted or updated record.
	 *
	 * @return true if "fast return" mechanism is active
	 */
	public boolean hasSimplifiedStructure()
	{
		return simplifiedStructure;
	}

	/**
	 * Specify if the transaction will use "fast return" mechanism avoiding to re-read the submitted or updated record.
	 *
	 * @param simplifiedStructure "fast return" mechanism to become active or inactive
	 */
	public void setSimplifiedStructure(boolean simplifiedStructure)
	{
		this.simplifiedStructure = simplifiedStructure;
	}

	/**
	 * Clone this object.
	 *
	 * @return a copy of the current instance.
	 */
	public CoreItem copy()
	{
		return copy(false);
	}

	/**
	 * Clone this object.
	 *
	 * @param reset reset the status of all attributes.
	 * @return a copy of the current instance.
	 */
	public CoreItem copy(boolean reset)
	{
		CoreItem item = getInstance();

		item.setFormName(getFormName());
		item.setIgnoreNullValues(ignoreNullValues());
		item.setIgnoreUnchangedValues(ignoreUnchangedValues());
		item.setSimplifiedStructure(hasSimplifiedStructure());

		Collection collection = getAttributes();

		if(!reset)
		{
			if(collection != null && collection.iterator() != null )
			{
				Iterator iterator = collection.iterator();

				while(iterator.hasNext())
				{
					Attribute attr = (Attribute) iterator.next();
					if(attr != null && NumberUtility.toInt(attr.getId(), 0) != 1)
					{
						Attribute clone = attr.copy();
						item.attributes.put(clone.getId(), clone);
					}
				}
			}
		}
		else item.setDefaultData(collection);

		item.setEntryId(getEntryId());

		return item;
	}

	/**
	 * Mark all fields as changed and remove core fields: 1,2,5,6.
	 */
	public void setChanged()
	{
		if(getAttribute(3) != null) deleteAttribute(3);  //create date
		if(getAttribute(6) != null) deleteAttribute(6);  //modified date
		if(getAttribute(5) != null) deleteAttribute(5);  //modified by

		Collection collection = getAttributes();

		if(collection != null && !collection.isEmpty())
		{
			Iterator iterator = collection.iterator();
			while(iterator != null && iterator.hasNext())
			{
				Attribute attr = (Attribute) iterator.next();
				if(attr != null) attr.setChanged();
			}
		}
	}

	/**
	 * Clone this object and prepare it for <code>create</code> transaction.
	 *
	 * @return a copy of the current instance.
	 */
	public CoreItem copyToNew()
	{
		CoreItem item = getInstance();

		item.setFormName(getFormName());
		item.setIgnoreNullValues(ignoreNullValues());
		item.setIgnoreUnchangedValues(ignoreUnchangedValues());
		item.setSimplifiedStructure(hasSimplifiedStructure());

		Collection collection = getAttributes();
		item.setData(collection);

		item.resetEntryId();
		if(item.getAttribute(1) != null) item.deleteAttribute(1);  //create date
		if(item.getAttribute(3) != null) item.deleteAttribute(3);  //create date
		if(item.getAttribute(6) != null) item.deleteAttribute(6);  //modified date
		if(item.getAttribute(5) != null) item.deleteAttribute(5);  //modified by

		return item;
	}

	public int[] getEntryFieldIds(ServerConnection arsession) throws AREasyException
	{
		if(entryFieldIds == null)
		{
			try
			{
				List list = new Vector();

				// Retrieve the detail info of all fields from the form.
				List<Integer> fields = arsession.getContext().getListField(getFormName(), Constants.AR_FIELD_TYPE_DATA, 0);

				for(int i = 0; i < fields.size(); i++)
				{
					Integer id = fields.get(i);

					if(id == 15) continue;
					list.add(id);
				}

				entryFieldIds = new int[list.size()];
				for(int i = 0; i < list.size(); i++) entryFieldIds[i] = (Integer)list.get(i);
			}
			catch(ARException are)
			{
				throw new AREasyException(are);
			}
		}

		return entryFieldIds;
	}

	/**
	 *  Validate all filled in attributes with standard attributes discovered for the corresponding form
	 *
	 * @param arsession Remedy server session
	 * @throws AREasyException if any error occur
	 */
	public void attrFixedToData(ServerConnection arsession) throws AREasyException
	{
		List<Integer> fields = null;

		//get structure
		try
		{
			// Retrieve the detail info of all fields from the form.
			fields = arsession.getContext().getListField(getFormName(), Constants.AR_FIELD_TYPE_DATA, 0);
		}
		catch(ARException are)
		{
			throw new AREasyException(are);
		}

		//get data
		List<Integer> attrIds = new ArrayList<Integer>();
		Iterator iterator = getAttributes().iterator();

		while(iterator != null && iterator.hasNext())
		{
			Attribute attr = (Attribute) iterator.next();

			Integer id = attr.getNumberId();
			attrIds.add(id);
		}

		//cleaning procedure
		for(int i = 0; i < attrIds.size(); i++)
		{
			Integer id = attrIds.get(i);

			if(!fields.contains(id))
			{
				RuntimeLogger.debug("Field id '" + id + "' doesn't belong to '" + getFormName() + "' AR form");

				//delete attribute
				deleteAttribute(id);
			}
		}
	}

	public int[] getEntryFieldIds()
	{
		return entryFieldIds;
	}

	public void setEntryFieldIds(int[] entryFieldIds)
	{
		this.entryFieldIds = entryFieldIds;
	}

	public void resetEntryFieldIds()
	{
		this.entryFieldIds = null;
	}

	public void setSortInfo(SortInfo sortInfo)
	{
		this.sortInfo = sortInfo;
	}

	public void resetSortInfo()
	{
		this.sortInfo = new SortInfo(1, Constants.AR_SORT_ASCENDING);
	}

	public SortInfo getSortInfo()
	{
		return this.sortInfo;
	}

	/**
	 * Activate simulation transaction. If this method is executed no <code>create</code>, <code>merge</code>
	 * or <code>update</code> transaction is executed, the system will return to client output log ALL details
	 * from item structure.
	 */
	public void simulate()
	{
		this.simulation = true;
	}

	/**
	 * Check if read operation will consider the first match record or is asking for exact match.
	 *
	 * @return true if the first match operation is used
	 */
	public boolean isFirstMatchReading()
	{
		return readFirstMatch;
	}

	/**
	 * Activate exact match operation and disable first match opeartion
	 */
	public void setExactMatchReading()
	{
		this.readFirstMatch = false;
	}

	/**
	 * Activate first match operation and disable exact match opeartion
	 */
	public void setFirstMatchReading()
	{
		this.readFirstMatch = true;
	}
}
