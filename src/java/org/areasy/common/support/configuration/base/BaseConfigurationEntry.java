package org.areasy.common.support.configuration.base;

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

import org.areasy.common.data.StringUtility;
import org.areasy.common.data.StringEscapeUtility;
import org.areasy.common.support.configuration.ConfigurationEntry;
import org.areasy.common.support.configuration.ConfigurationLocator;

import java.util.List;
import java.util.Vector;
import java.util.Collection;
import java.util.Iterator;


/**
 * Basic implementation for configuration data records.
 *
 * @version $Id: BaseConfigurationEntry.java,v 1.8 2008/05/19 20:05:14 swd\stefan.damian Exp $
 */
public class BaseConfigurationEntry implements ConfigurationEntry
{
	private String comment;
	private String key;
	private List values;

	private ConfigurationLocator locator = null;

	/**
	 * Default constructor, creating a null entry.
	 */
	public BaseConfigurationEntry()
	{
		this.key = null;
		this.values = null;
		this.comment = null;
	}

	/**
	 * Constructor to create a comment entry.
	 *
	 * @param comment comment line
	 */
	public BaseConfigurationEntry(String comment)
	{
		this.key = null;
		this.values = null;
		this.comment = comment;
	}

	/**
	 * Constructor to create a data key.
	 *
	 * @param key configuration key
	 * @param value string configuration value
	 */
	public BaseConfigurationEntry(String key, String value)
	{
		this.key = key;
		this.comment = null;

		this.values = new Vector();
		addValue(value);
	}

	/**
	 * Constructor to create a data key.
	 *
	 * @param key configuration key
	 * @param value object configuration value
	 */
	public BaseConfigurationEntry(String key, Object value)
	{
		this.key = key;
		this.comment = null;

		this.values = new Vector();
		addValue(value);
	}

	/**
	 * Copy constructor.
	 *
	 * @param entry configuration entry.
	 */
	public BaseConfigurationEntry(BaseConfigurationEntry entry)
	{
		this.key = entry.key;
		
		if(entry.values != null)
		{
			this.values = new Vector();
			addValue(entry.values);
		}

		this.comment = entry.comment;
	}

	/**
	 * Clone this object.
	 *
	 * @return a new instance of the current entry having the same data or comment values.
	 */
	public Object clone()
	{
		if(isData()) return new BaseConfigurationEntry(getKey(), getValues());
			else return new BaseConfigurationEntry(getComment());
	}

	/**
	 * Check if is this configuration record is comment.
	 *
	 * @return true if this entry is a comment.
	 */
	public final boolean isComment()
	{
		return comment != null;
	}

	/**
	 * Check if is configuration record is data (key and value(s))
	 *
	 * @return true if the current entry is data (key and value(s))
	 */
	public final boolean isData()
	{
		return key != null;
	}

	/**
	 * Get the configuration entry comment.
	 *
	 * @return resource comment
	 */
	public final String getComment()
	{
		return comment;
	}

	/**
	 * Get the configuration entry key.
	 *
	 * @return resource key
	 */
	public final String getKey()
	{
		return key;
	}

	/**
	 * Get the configuration entry value.
	 *
	 * @return resource key value
	 */
	public final Object getValue()
	{
		if(values.isEmpty()) return null;
			else if(values.get(0) instanceof ConfigurationEntry) return ((ConfigurationEntry) values.get(0)).getValue();
				else if(values.get(0) instanceof String) return StringEscapeUtility.unescapeComma((String)values.get(0)); 
					else return values.get(0);
	}

	/**
	 * Get a configuration entry value.
	 *
	 * @param index index position in the list of values stored by this entry.
	 * @return resource key value using an index
	 */
	public final Object getValue(int index)
	{
		if(values.size() <= index) return null;
			else if(values.get(index) instanceof ConfigurationEntry) return ((ConfigurationEntry) values.get(index)).getValue();
				else if(values.get(index) instanceof String) return StringEscapeUtility.unescapeComma((String)values.get(index)); 
					else return values.get(index);
	}

	/**
	 * Get the values list from the current configuration entry.
	 *
	 * @return resource key values
	 */
	public final List getValues()
	{
		if(isData())
		{
			List list = new Vector();

			for(int i = 0; values != null && i < values.size(); i++)
			{
				Object object = values.get(i);

				//validate output
				if(object instanceof ConfigurationEntry) list.addAll( ((ConfigurationEntry)object).getValues() );
					else if(object instanceof String) list.add( StringEscapeUtility.unescapeComma((String)object) );
						else list.add(object);
			}

			return list;
		}
		else return null;
	}

	/**
	 * Get the values from the current configuration entry, concatenated into one string
	 *
	 * @return resource key value
	 */
	public final Object getCompleteValue()
	{
		List list = getValues();

		if(list != null)
		{
			return StringUtility.join(list.listIterator(), ", ");
		}
		else return null;
	}

	/**
	 * Set a comment entry/value.
	 *
	 * @param text comment value
	 */
	public final void setComment(String text)
	{
		this.comment = text;

		this.key = null;
		this.values = null;
	}

	/**
	 * Set data entity (key and value pair).
	 *
	 * @param key configuration key
	 * @param value configuration value
	 */
	public final void setData(String key, Object value)
	{
		this.key = key;
		setValue(value);

		this.comment = null;
	}
	
	/**
	 * Get the index value of the specified object.
	 *
	 * @param object object in the configuration list
	 * @return the index in the list
	 */
	public int getIndex(Object object)
	{
		if(object == null) return -1;

		return values.indexOf(object);
	}

	/**
	 * Set record value (is the record is data).
	 * Is value member is a Collection is not admitted to have one object on many position in list.
	 */
	public void setValue(Object value)
	{
		if(isData())
		{
			if(this.values == null) this.values = new Vector();
			this.values.clear();

			addValue(value);
		}
	}

	/**
	 * Set record value for a specified index from values list.
	 *
	 * @param value object value to be set.
	 * @param index index order where to be set the specified value.
	 */
	public void setValue(Object value, int index)
	{
		if(index > this.getValues().size() || index < 0) throw new IndexOutOfBoundsException("Length of values list in out of range");

		if(isData()) this.values.set(index, value);
	}

	/**
	 * Add new value (is the record is data).
	 * Is value member is a Collection is not admitted to have one object on many position in list.
	 * If value is string will be escaped for java.
	 *
	 * @param value value object to be added.
	 */
	public void addValue(Object value)
	{
		if(isData())
		{
			if(this.values == null) this.values = new Vector();

			if(value instanceof Collection)
			{
				Iterator iterator = ((Collection)value).iterator();
				
				while(iterator!= null && iterator.hasNext())
				{
					Object object = iterator.next();

					if(object instanceof Collection)
					{
						Collection collection = (Collection)object;
						this.values.addAll( collection );
					}
					else if(object instanceof String)
					{
						String objvalue = StringEscapeUtility.escapeComma((String)object);
						this.values.add(objvalue);
					}
					else this.values.add(object);
				}
			}
			else if(value instanceof String)
			{
				String objvalue = StringEscapeUtility.escapeComma((String)value);
				this.values.add(objvalue);
			}
			else this.values.add(value);
		}
	}

	/**
	 * Remove resource key value using an index
	 *
	 * @param index index order from where will be removed the corresponding value.
	 */
	public void removeValue(int index)
	{
		if(isData()) this.values.remove(index);
	}

	/**
	 * Indicates whether some other object is "equal to" this one.
	 */
	public final boolean equals(Object object)
	{
		if(object instanceof BaseConfigurationEntry)
		{
			BaseConfigurationEntry entry = (BaseConfigurationEntry)object;

			if(entry.isData() && isData()) return !(!StringUtility.equals(getKey(), entry.getKey()) || !getValue().equals(entry.getValue()));
				else return entry.isComment() && isComment() && StringUtility.equals(getComment(), entry.getComment());
		}

		return false;
	}

	/**
	 * Set entry locator. Tell where is situated this record.
	 *
	 * @param locator set the configuration locator for the current entry.
	 */
	public final void setLocator(ConfigurationLocator locator)
	{
		this.locator = locator;
	}

	/**
	 * Get records locator.
	 *
	 * @return the configuration locator structure or null for the current entry.
	 */
	public final ConfigurationLocator getLocator()
	{
		return this.locator;
	}

	/**
	 * Append a new comment text.
	 *
	 * @param text comment line
	 */
	public void appendInComment(String text)
	{
		if(isComment()) this.comment += text;
	}

	/**
	 * Convert a data record into a comment (transformation from data key and value into comment).
	 * Not implemented in this library and will return always false.
	 */
	public boolean convertInComment()
	{
		return false;
	}

	/**
	 * Change comment value into a data. This method is not implemented and will return
	 * always false.
	 */
	public boolean convertInData()
	{
		return false;
	}

	/**
	 * Check if this confiuration entry is null or empty (null comment or null key and data).
	 *
	 * @return true if entry is null or empty (null comment or null key and data).
	 */
	public boolean isEmpty()
	{
		return isComment() && StringUtility.isEmpty(getComment()) || isData() && StringUtility.isEmpty(getKey()) && (getValues() == null || getValues().size() == 0);
	}

	/**
	 * Get string representation of the current entry.
	 *
	 * @return resource statement (line)
	 */
	public String toString()
	{
		if(isData()) return getKey() + " = " + getValue();
			else return getComment();
	}
}
