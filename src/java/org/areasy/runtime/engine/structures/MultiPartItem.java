package org.areasy.runtime.engine.structures;

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

import org.areasy.common.data.StringUtility;
import org.areasy.common.data.type.MapHashtable;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.runtime.engine.base.ServerConnection;

import java.util.*;

/**
 * ARS multi-part core item structure, defining a multi part record instance from ARS forms.
 */
public class MultiPartItem extends CoreItem
{
	/** subsets or core item parts which are secondary entities */
	private Hashtable partobjects = new Hashtable();

	/** Map to store relationship keys */
	private MapHashtable partkeys = new MapHashtable();

	public static String partSeparator = "@";
	public static String partVarStart = "${";
	public static String partVarEnd = "}";

	/**
	 * Default constructor
	 */
	public MultiPartItem()
	{
		super();
		clearParts();
	}

	/**
	 * Constructor that set the form name
	 *
	 * @param formName ARSystem form name
	 */
	public MultiPartItem(String formName)
	{
		this();

		if(formName != null) setFormName(formName);
	}

	/**
	 * Create a new instance of core item structure.
	 *
	 * @return new instance of <code>CoreItem</code> structure
	 */
	public MultiPartItem getInstance()
	{
		return new MultiPartItem();
	}

	/**
	 * Add a new object attribute in the item attribute's list for a specific part. If the part name is missing
	 * then the attribute will be added in the primary attribute's list
	 *
	 * @param part multi-part value
	 * @param attr attribute structure
	 */
	public void setAttribute(String part, Attribute attr)
	{
		if(attr == null) return;
		
		if(part != null)
		{
			CoreItem subset = getPartInstance(part);

			if(subset != null) subset.setAttribute(attr);
			else
			{
				subset = new CoreItem();
				subset.setAttribute(attr);

				addPart(part, subset);
			}
		}
		else setAttribute(attr);
	}

	/**
	 * This method will set the value of an existent field id (attribute field) into a specific part list.
	 * If the attribute field id is not registered will create and register a new attrbuted but marked as changed.
	 *
	 * @param part is the multi-part value
	 * @param id field id
	 * @param value  field value
	 */
	public void setAttribute(String part, String id, Object value)
	{
		if(part != null)
		{
			if(value == null) setNullAttribute(part, id);
			else
			{
				Attribute attr = getAttribute(part, id);

				if(attr != null) attr.setValue(value);
				else
				{
					attr = new Attribute(id);
					attr.setValue(value);

					setAttribute(part, attr);
				}
			}
		}
		else setAttribute(id, value);
	}

	/**
	 * Set object value for this attribute structure with a null value for a specific part
	 *
	 * @param part fields list part
	 * @param id field id
	 */
	public void setNullAttribute(String part, String id)
	{
		Attribute attr = getAttribute(part, id);

		if(attr != null) attr.setNullValue();
		else
		{
			attr = new Attribute(id);
			attr.setNullValue();
			attr.setChanged();

			setAttribute(part, attr);
		}
	}

	/**
	 * Set object value for this attribute structure with a null value
	 *
	 * @param part part item
	 * @param id field id
	 */
	public void setNullAttribute(String part, long id)
	{
		setNullAttribute(part, String.valueOf(id));
	}

	/**
	 * This method will set the value of an existent field id (attribute field).
	 * If the attribute field id is not registered will create and register a new attrbuted but marked as changed.
	 * If the attribute id has the format like <code>[value]-[value]</code> means that the id contains also the part name
	 * and the part is the left value and the real value is the right side.
	 *
	 * @param id field id
	 * @param value  field value
	 */
	public void setAttribute(String id, Object value)
	{
		if(id != null && id.contains(partSeparator))
		{
			int index = id.indexOf(partSeparator);

			String part = id.substring(0, index);
			String subid = id.substring(index + partSeparator.length());

			setAttribute(part, subid, value);
		}
		else super.setAttribute(id, value);
	}

	/**
	 * Get an object attribute value from the part item attribute's list.
	 *
	 * @param part item list
 	 * @param id fild object id
	 * @return field value for the specified field id.
	 */
	public Attribute getAttribute(String part, Object id)
	{
		if(part != null)
		{
			CoreItem subset = getPartInstance(part);

			if(subset != null) return subset.getAttribute(id);
				else return null;
		}
		else return getAttribute(id);
	}

	/**
	 * Get an object attribute value from the part item attribute's list.
	 *
	 * @param part item list
 	 * @param id field id
	 * @return field value for the specified field id.
	 */
	public Attribute getAttribute(String part, long id)
	{
		return getAttribute(part, String.valueOf(id));
	}

	/**
	 * Add a new object attribute in the item attribute's list.
	 *
	 * @param part part item
	 * @param id field id
	 * @param value field value
	 */
	public void setDefaultAttribute(String part, String id, Object value)
	{
		if(part != null)
		{
			Attribute attr = new Attribute(id, value);
			setAttribute(part, attr);
		}
		else setDefaultAttribute(id, value);
	}

	/**
	 * Add a new object attribute in the item aatribute's list.
	 *
	 * @param part part item
	 * @param id field id
	 * @param value field value
	 */
	public void setDefaultAttribute(String part, long id, Object value)
	{
		setDefaultAttribute(part, String.valueOf(id), new Attribute(id, value));
	}

	/**
	 * Set default object value for this attribute structure with a null value
	 *
	 * @param part part item
	 * @param id field id
	 */
	public void setDefaultNullAttribute(String part, String id)
	{
		if(part != null)
		{
			Attribute attr = getAttribute(part, id);

			if(attr != null) attr.setDefaultNullValue();
			else
			{
				attr = new Attribute(id);
				attr.setDefaultNullValue();

				setAttribute(part, attr);
			}
		}
		else setDefaultNullAttribute(id);
	}

	/**
	 * Check if specified attribute id is defined or if exist
	 *
	 * @param part part item
	 * @param id field id
	 * @return true if the specified id exist
	 */
	public boolean isAttribute(String part, String id)
	{
		if(part != null)
		{
			CoreItem subset = getPartInstance(part);

			if(subset != null) return subset.isAttribute(id);
				else return false;
		}
		else return isAttribute(id);
	}

	/**
	 * Check if specified attribute id is defined
	 *
	 * @param part part item
	 * @param id field id
	 * @return true if the specified id exist
	 */
	public boolean isAttribute(String part, long id)
	{
		return isAttribute(part, String.valueOf(id));
	}

	/**
	 * Check if specified attribute id is defined and is not null
	 *
	 * @param part part item
	 * @param id field id
	 * @return true if the specified id exist and is not null
	 */
	public boolean isEmptyAttribute(String part, String id)
	{
		if(part != null)
		{
			CoreItem subset = getPartInstance(part);

			if(subset != null) return subset.isEmptyAttribute(id);
				else return false;
		}
		else return isEmptyAttribute(id);
	}

	/**
	 * Check if specified attribute id is defined and is not null
	 *
	 * @param part part item
	 * @param id field id
	 * @return true if the specified id exist and is not null
	 */
	public boolean isEmptyAttribute(String part, long id)
	{
		return isEmptyAttribute(part, String.valueOf(id));
	}

	/**
	 * Get a object attribute value from the core item attribute's list.
	 *
	 * @param part part item
 	 * @param id field id
	 * @return field value for the specified field id.
	 */
	public Object getAttributeValue(String part, String id)
	{
		if(part != null)
		{
			CoreItem subset = getPartInstance(part);

			if(subset != null) return subset.getAttributeValue(id);
				else return null;
		}
		else return getAttributeValue(id);
	}

	/**
	 * Get a string attribute value from the core item attribute's list.
	 *
	 * @param part part item
 	 * @param id field id
	 * @return field value for the specified field id.
	 */
	public Object getAttributeValue(String part, long id)
	{
		return getAttributeValue(part, String.valueOf(id));
	}

	/**
	 * Get a string attribute value from the core item attribute's list.
	 *
	 * @param part part item
 	 * @param id field id
	 * @return field value for the specified field id.
	 */
	public String getStringAttributeValue(String part, String id)
	{
		if(part != null)
		{
			CoreItem subset = getPartInstance(part);

			if(subset != null) return subset.getStringAttributeValue(id);
				else return null;
		}
		else return getStringAttributeValue(id);
	}

	/**
	 * Get a string attribute value from the core item attribute's list.
	 *
	 * @param part part item
 	 * @param id field id
	 * @return field value for the specified field id.
	 */
	public String getStringAttributeValue(String part, long id)
	{
		return getStringAttributeValue(part, String.valueOf(id));
	}

	/**
	 * Get number of attributes registered in this core item instance.
	 *
	 * @param part part item
	 * @return total number of registered attributes.
	 */
	public int getNumberOfAttributes(String part)
	{
		if(part != null)
		{
			CoreItem subset = getPartInstance(part);

			if(subset != null) return subset.getNumberOfAttributes();
				else return 0;
		}
		else return getNumberOfAttributes();
	}

	/**
	 * Delete an attribute from this core item instance.
	 *
	 * @param part part item
	 * @param fieldid string key (field id in string format)
	 */
	public void deleteAttribute(String part, String fieldid)
	{
		if(part != null)
		{
			CoreItem subset = getPartInstance(part);

			if(subset != null) subset.deleteAttribute(fieldid);
		}
		else deleteAttribute(fieldid);
	}

	/**
	 * Delete an attribute from this core item instance.
	 *
	 * @param part part item
	 * @param fieldid field id in core format
	 */
	public void deleteAttribute(String part, long fieldid)
	{
		deleteAttribute(part, String.valueOf(fieldid));
	}

	/**
	 * Get a list with all field ids registered in all attributes fr this core item instance.
	 *
	 * @param part part item
	 * @return a vector instance with all field ids.
	 */
	public List getAttributeIds(String part)
	{
		if(part != null)
		{
			CoreItem subset = getPartInstance(part);

			if(subset != null) return subset.getAttributeIds();
				else return null;
		}
		else return getAttributeIds();
	}

	/**
	 * Delete all attributes and reset the enty id.
	 */
	public void clearParts()
	{
		if(hasMultiParts())
		{
			Iterator iterator = getPartCodes();

			while(iterator!= null && iterator.hasNext())
			{
				String part = (String) iterator.next();
				CoreItem item = getPartInstance(part);
				
				item.clear();
			}
		}

		//clear keys between base element and parts
		if(this.partkeys != null) this.partkeys.clear();
	}

	/**
	 * Get the ARS form name for this record instance.
	 *
	 * @param part part item
	 * @return the form name.
	 */
	public String getFormName(String part)
	{
		if(part != null)
		{
			CoreItem subset = getPartInstance(part);

			if(subset != null) return subset.getFormName();
				else return null;
		}
		else return getFormName();
	}

	/**
	 * Set the corresponding form name for this core item instance
	 *
	 * @param part part item.
	 * @param formName ARS form name.
	 */
	public void setFormName(String part, String formName)
	{
		if(part != null)
		{
			CoreItem subset = getPartInstance(part);

			if(subset != null) subset.setFormName(formName);
			else
			{
				subset = new CoreItem();
				subset.setFormName(formName);

				addPart(part, subset);
			}
		}
		else setFormName(formName);
	}

	/**
	 * Set the corresponding form name for this core item instance
	 * 
	 * @param formName ARS form name.
	 */
	public void setFormName(String formName)
	{
		if(formName != null && formName.contains(partSeparator))
		{
			int index = formName.indexOf(partSeparator);

			String part = formName.substring(0, index);
			String name = formName.substring(index + partSeparator.length());

			setFormName(part, name);
		}
		else super.setFormName(formName);
	}

	/**
	 * Check if data attributes values were changed.
	 *
	 * @param part part item.
	 * @return true if attributes values were changed
	 */
	public boolean isChanged(String part)
	{
		if(part != null)
		{
			CoreItem subset = getPartInstance(part);

			if(subset != null) return subset.isChanged();
				else return false;
		}
		else return isChanged(part);
	}

	/**
	 * Check if data attributes values were changed.
	 *
	 * @return true if attributes values were changed
	 */
	public boolean isChanged()
	{
		boolean changed = super.isChanged();

		if(hasMultiParts() && !changed)
		{
			Iterator iterator = getPartCodes();

			while(iterator!= null && iterator.hasNext() && !changed)
			{
				String part = (String) iterator.next();
				CoreItem item = getPartInstance(part);

				changed = item.isChanged();
			}
		}

		return changed;              
	}

	/**
	 * Check if this core item record instance was read and exist properly.
	 *
	 * @param part part item.
	 * @return true if exist (so has filled the request id attribute)
	 */
	public final boolean exists(String part)
	{
		if(part != null)
		{
			CoreItem subset = getPartInstance(part);

			if(subset != null) return subset.exists();
				else return false;
		}
		else return exists(part);
	}

	/**
	 *  Remove data structure(s) found based on matched criteria filled in the current structure.
	 *
	 * @param arsession user session
	 * @throws org.areasy.runtime.engine.base.AREasyException if any error will occur.
	 */
	public void removeParts(ServerConnection arsession) throws AREasyException
	{
		Iterator iterator = getPartCodes();

		while(iterator!= null && iterator.hasNext())
		{
			String part = (String) iterator.next();
			CoreItem item = getPartInstance(part);

			item.remove(arsession);
		}
	}

	/**
	 * Read data structure and fill all found field attributes. This method will consider only changed field to perform a seaching
	 * operation.
	 *
	 * @param arsession user session
	 * @throws org.areasy.runtime.engine.base.AREasyException if any error will occur.
	 */
	public void readParts(ServerConnection arsession) throws AREasyException
	{
		Iterator iterator = getPartCodes();

		while(iterator!= null && iterator.hasNext())
		{
			String part = (String) iterator.next();
			CoreItem item = getPartInstance(part);

			interpolate(item);
			item.read(arsession);
		}
	}

	/**
	 * Read part data structure. This method is used to prepare a multipart entry that is dependent
	 * by root part (or root element) event is the sub-part is not registered yet.
	 *
	 * @param arsession user session
	 * @param item sub part that will be registered as a child structure for the current entity
	 *
	 * @throws org.areasy.runtime.engine.base.AREasyException if any error will occur.
	 */
	public void readPreparedParts(ServerConnection arsession, CoreItem item) throws AREasyException
	{
		interpolate(item);

		item.read(arsession);
	}

	/**
	 * Search part data structures. This method is used to prepare multipart entries provided by search criteria that are dependent
	 * by root part (or root element) event is the sub-parts are not registered yet.
	 *
	 * @param item <code>CoreItem</code> that will be used as a search structures that will become parts of the current entity
	 * @param arsession user session
	 * @return a list of  <code>CoreItem</code> structure or an empty list
	 * @throws org.areasy.runtime.engine.base.AREasyException if any error will occur.
	 */
	public List searchPreparedParts(ServerConnection arsession, CoreItem item) throws AREasyException
	{
		interpolate(item);

		return item.search(arsession);
	}

	/**
	 * Update or create the current multipart core item instance based on changed attributes.
	 *
	 * @param arsession user session
	 * @throws AREasyException if any error will occur
	 */
	public void commitParts(ServerConnection arsession) throws AREasyException
	{
		Iterator iterator = getPartCodes();

		while(iterator!= null && iterator.hasNext())
		{
			String part = (String) iterator.next();
			CoreItem item = getPartInstance(part);

			interpolate(item);

			if(item.exists()) item.update(arsession);
				else item.create(arsession);
		}
	}

	/**
	 * Process list of parts (read and commit) of a multipart core item instance based on changed attributes.
	 *
	 * @param arsession user session
	 * @param commit map containing parameters for commit action
	 * @param read map containing parameters for read action
	 * @throws AREasyException if any error will occur
	 */
	public void commitParts(ServerConnection arsession, Map read, Map commit) throws AREasyException
	{
		Iterator iterator = getPartCodes();

		while(iterator!= null && iterator.hasNext())
		{
			String part = (String) iterator.next();
			CoreItem item = getPartInstance(part);

			//process input parameters and read part item structure
			if(read != null)
			{
				Iterator readit = read.keySet().iterator();

				while(readit.hasNext())
				{
					String key = (String) readit.next();
					if( key != null && key.startsWith(part + partSeparator) && key.length() > part.length() + 1)
					{
						String fid = key.substring((part + partSeparator).length());
						Object fvalue = read.get(key);

						item.setAttribute(fid, fvalue);
					}
				}

				interpolate(item);
				item.read(arsession);
			}

			//read output parameters and commit part item structure
			if(commit != null)
			{
				Iterator commitit = commit.keySet().iterator();

				while(commitit.hasNext())
				{
					String key = (String) commitit.next();
					if( key != null && key.startsWith(part + partSeparator) && key.length() > part.length() + 1)
					{
						String fid = key.substring((part + partSeparator).length());
						Object fvalue = commit.get(key);

						item.setAttribute(fid, fvalue);
					}
				}

				interpolate(item);
				if(item.exists()) item.update(arsession);
					else item.create(arsession);
			}
		}
	}

    /**
	 * Get string representation of the current multi part item structure.
	 *
	 * @return string data model.
	 */
	public String toString()
	{
		String data = "MultiPart Item ("+ getFormName() + ") [Request Id = " + getEntryId() + ", Status = " + getAttributeValue(7) + ", Submitter = " + getAttributeValue(2) + "]";

		Iterator iterator = getPartCodes();

		while(iterator!= null && iterator.hasNext())
		{
			String part = (String) iterator.next();
			CoreItem item = getPartInstance(part);

			data += "\n " + part + " = " + item;
		}

		return data;
	}

	/**
	 * Replace is parts referred values from other parts. Referred value are define
	 * as fallow: <code>${<part name>@<field id>}</code>
	 *
	 * @param entry multi-part core item structure
	 */
	protected void interpolate(CoreItem entry)
	{
		if(entry == null) return;
		Collection collection = entry.getAttributes();

		if(collection != null && !collection.isEmpty())
		{
			Iterator iterator = collection.iterator();

			while(iterator != null && iterator.hasNext())
			{
				Attribute attr = (Attribute) iterator.next();

				if(attr.getValue() instanceof String && attr.getStringValue().startsWith(partVarStart) && attr.getStringValue().indexOf(partSeparator, 0) > 0 && attr.getStringValue().endsWith(partVarEnd))
				{
					String label = attr.getStringValue();
					int index = label.indexOf(partSeparator);

					String part = label.substring(partVarStart.length(), index);
					String fieldId = label.substring(index + 1, label.length() - partVarEnd.length());

					CoreItem tmpitem = null;

					if(StringUtility.equalsIgnoreCase(part, "base")) tmpitem = this;
                    	else tmpitem = getPartInstance(part);

					if(tmpitem != null)
					{
						Object value = null;

						if(StringUtility.equals(fieldId, "1")) value = tmpitem.getEntryId();
							else value = tmpitem.getAttributeValue(fieldId);

                    	attr.setValue(value);
					}
					else attr.setNullValue();
				}
			}
		}
	}

	/**
	 * return number of parts registered in the core item structure.
	 * @return number of core item parts
	 */
	public int getNumberOfParts()
	{
		return this.partobjects.size();
	}

	/**
	 * Check if the parent core item structure has additional parts
	 * @return true if the core item structure has parts
	 */
	public boolean hasMultiParts()
	{
		return this.partobjects != null && !this.partobjects.isEmpty();
	}

	/**
	 * Get additional parts.
	 *
	 * @return An <code>Iterator</code> with all core item part instances.
	 */
	public Iterator getPartInstances()
	{
		return (!this.partobjects.isEmpty() ? this.partobjects.values().iterator() : null);
	}

	/**
	 * Get part codes.
	 *
	 * @return An <code>Iterator</code> with all part codes.
	 */
	public Iterator getPartCodes()
	{
		return (!this.partobjects.isEmpty() ? this.partobjects.keySet().iterator() : null);
	}

	/**
	 * Get a <code>CoreItem</code> part.
	 *
	 * @param code part identifier
	 * @return <code>CoreItem</code> instance
	 */
	public CoreItem getPartInstance(String code)
	{
		return (CoreItem) this.partobjects.get(code);
	}

	public List getPartInstancesByPrefix(String string)
	{
		List list = new Vector();
		if(string == null) return list;

		Iterator iterator = getPartCodes();
		while(iterator != null && iterator.hasNext())
		{
			String code = (String) iterator.next();

			if(code != null && code.startsWith(string) ) list.add(getPartInstance(code));
		}

		return list;
	}

	/**
	 * Get the string code for a part using a class prefix and the part instance.
	 *
	 * @param prefix part prefix
	 * @return <code>String</code> part code
	 */
	public final String getPartCodeBasedOnPrefix(String prefix, CoreItem item)
	{
		return prefix + "-" + Integer.toHexString(item.hashCode());
	}

	public void addPart(String code, CoreItem item)
	{
		this.partobjects.put(code, item);
	}

	public void addPartWithPrefix(String prefix, CoreItem item)
	{
		addPart(getPartCodeBasedOnPrefix(prefix, item), item);
	}

	public void addPart(String code, CoreItem item, Integer key1, Integer key2)
	{
		addPart(code, item);
		addPartKeyPair(code, key1, key2);
	}

	public void addPartWithPrefix(String prefix, CoreItem item, Integer key1, Integer key2)
	{
		addPart(getPartCodeBasedOnPrefix(prefix, item), item, key1, key2);
	}

	public void addPartKeyPair(String code, Integer partId, Integer baseId)
	{
		this.partkeys.put(code, partId, baseId);
	}

	public void addPartKeyPairWithPrefix(String prefix, CoreItem item, Integer partId, Integer baseId)
	{
		this.partkeys.put(getPartCodeBasedOnPrefix(prefix, item), partId, baseId);
	}

	/**
	 * Remove a part object using his string signature
	 *
	 * @param code known string signature that have to identify the part object that must be deleted
	 */
	public void removePart(String code)
	{
		if(code == null) return;

		if(this.partobjects.containsKey(code)) this.partobjects.remove(code);
		if(this.partkeys.contains(code)) this.partkeys.remove(code);
	}

	/**
	 * Remove a part object using his instance signature.
	 *
	 * @param item part object that have to be deleted
	 */
	public void removePart(CoreItem item)
	{
		if(item == null) return;

		if(this.partobjects.containsValue(item))
		{
			String code = null;
			boolean found = false;
			Iterator iterator = this.partobjects.keySet().iterator();

			while(iterator != null && !found && iterator.hasNext())
			{
				String string = (String) iterator.next();
				CoreItem entry = (CoreItem) this.partobjects.get(string);

				if(entry != null && entry.equals(item))
				{
					found = true;
					code = string;
				}
			}

			if(code != null)
			{
				this.partobjects.remove(code);
				this.partkeys.remove(code);
			}
		}
	}

	public Map getPartKeyPairs(String code)
	{
		return this.partkeys.get(code);
	}

	public Iterator getPartKeys(String code)
	{
		return this.partkeys.get(code).keySet().iterator();
	}

	public Integer getPartKeyValue(String code, Integer key)
	{
		return (Integer) this.partkeys.get(code).get(key);
	}

	public Map getPartKeyPairsWithPrefix(String prefix, CoreItem item)
	{
		return getPartKeyPairs(getPartCodeBasedOnPrefix(prefix, item));
	}

	public Iterator getPartKeysWithPrefix(String prefix, CoreItem item)
	{
		return getPartKeys(getPartCodeBasedOnPrefix(prefix, item));
	}

	/**
	 * Copy multipart details from the source instance.
	 *
	 * @param source of the source instance.
	 */
	public void setMultiPart(MultiPartItem source)
	{
		this.partobjects = (Hashtable) source.partobjects.clone();
		this.partkeys = (MapHashtable) source.partkeys.clone();
	}

	/**
	 * Clone this object.
	 *
	 * @return a copy of the current instance.
	 */
	public MultiPartItem copy()
	{
		return copy(false);
	}

	/**
	 * Clone this object.
	 *
	 * @param reset reset the status of all attributes.
	 * @return a copy of the current instance.
	 */
	public MultiPartItem copy(boolean reset)
	{
		MultiPartItem item = (MultiPartItem) super.copy(reset);

		item.partobjects = (Hashtable) this.partobjects.clone();
		item.partkeys = (MapHashtable) this.partkeys.clone();

		return item;
	}

	/**
	 * Synchronize part structures (instances) with the base (main) instance.
	 */
	public void synch()
	{
		Iterator iterator = getPartCodes();

		while(iterator!= null && iterator.hasNext())
		{
			String part = (String) iterator.next();
			CoreItem item = getPartInstance(part);

			//call interpolation
			interpolate(item);

			//get mapping.
			Map partMap = getPartKeyPairs(part);

			if(partMap != null)
			{
				Iterator partIterator = partMap.keySet().iterator();

				while(partIterator!= null && partIterator.hasNext())
				{
					Integer keyPart = (Integer) partIterator.next();
					Integer keyBase = (Integer) partMap.get(keyPart);

					if(keyBase == 1) item.setAttribute(keyPart, getEntryId());
						else item.setAttribute(keyPart, getAttributeValue(keyBase));
				}
			}
		}
	}
}
