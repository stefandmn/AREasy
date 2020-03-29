package org.areasy.common.support.configuration;

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

import java.util.List;

/**
 * Dedicated interface to record a fragment configuration structure.
 *
 * @version $Id: ConfigurationEntry.java,v 1.4 2008/05/19 20:05:14 swd\stefan.damian Exp $
 */
public interface ConfigurationEntry extends Cloneable, java.io.Serializable
{
	/** Comment static character */
	public static final String PROPERTY_COMMENT = "#";
	public static final String PROPERTY_BODYSTRING = "@body:";
	
	/**
	 * This is the name of the property that can point to other
	 * properties file for including other properties files.
	 */
	public static final String INCLUDE = "include";

	/**
	 * Get the configuration entry key.
	 *
	 * @return resource key
	 */
	String getKey();

	/**
	 * Get the configuration entry value.
	 *
	 * @return resource key value
	 */
	Object getValue();

	/**
	 * Get a configuration entry value.
	 *
	 * @param index index position in the list of values stored by this entry.
	 * @return resource key value using an index
	 */
	Object getValue(int index);

	/**
	 * Get the values list from the current configuration entry.
	 *
	 * @return resource key values
	 */
	List getValues();

	/**
	 * Get the values from the current configuration entry, concatenated into one string
	 *
	 * @return resource key value
	 */
	Object getCompleteValue();

	/**
	 * Get the configuration entry comment.
	 *
	 * @return resource comment
	 */
	String getComment();

	/**
	 * Check if is this configuration record is comment.
	 *
	 * @return true if this entry is a comment.
	 */
	boolean isComment();

	/**
	 * Check if is configuration record is data (key and value(s))
	 *
	 * @return true if the current entry is data (key and value(s))
	 */
	boolean isData();

	/**
	 * Change comment into a data (if conversion could be made).
	 *
	 * @return true is actual comment could be converted in data
	 */
	boolean convertInData();

	/**
	 * Convert a data record into a comment (transformation from a key and a value into a comment).
	 *
	 * @return true if actual entry could be converted in comment.
	 */
	boolean convertInComment();

	/**
	 * Set data entity (key and value pair).
	 *
	 * @param key configuration key
	 * @param value configuration value
	 */
	void setData(String key, Object value);

	/**
	 * Set a comment entry/value.
	 *
	 * @param text comment value
	 */
	void setComment(String text);

	/**
	 * Get the index value of the specified object.
	 *
	 * @param object object in the configuration list
	 * @return the index in the list
	 */
	int getIndex(Object object);

	/**
	 * Set record value (is the record is data).
	 *
	 * @param value object value for this data entry.
	 */
	void setValue(Object value);

	/**
	 * Set record value for a specified index from values list.
	 *
	 * @param value object value to be set.
	 * @param index index order where to be set the specified value.
	 */
	public void setValue(Object value, int index);

	/**
	 * Add new value (is the record is data)
	 *
	 * @param value value object to be added.
	 */
	void addValue(Object value);

	/**
	 * Remove resource key value using an index
	 *
	 * @param index index order from where will be removed the corresponding value.
	 */
	void removeValue(int index);

	/**
	 * Append a new comment text.
	 *
	 * @param text append a new comment line in this comment entry.
	 */
	void appendInComment(String text);

	/**
	 * Set entry locator. Tell where is situated this record.
	 *
	 * @param locator set the configuration locator for the current entry.
	 */
	void setLocator(ConfigurationLocator locator);

	/**
	 * Get records locator.
	 *
	 * @return the configuration locator structure or null for the current entry.
	 */
	ConfigurationLocator getLocator();

	/**
	 * Check if this confiuration entry is null or empty (null comment or null key and data).
	 *
	 * @return true if entry is null or empty (null comment or null key and data).
	 */
	boolean isEmpty();

	/**
	 * Clone this record.
	 *
	 * @return a new instance of the current entry having the same data or comment values.
	 */
	public Object clone();
}
