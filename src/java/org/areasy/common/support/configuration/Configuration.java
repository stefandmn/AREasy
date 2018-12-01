package org.areasy.common.support.configuration;


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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * Configuration interface.
 * The general idea here is to make something that will work like our
 * extended properties and be compatible with the preferences API if at all
 * possible.
 *
 * @version $Id: Configuration.java,v 1.2 2008/05/14 09:32:41 swd\stefan.damian Exp $
 */
public interface Configuration
{
	/**
	 * Get configuration locator.
	 */
	ConfigurationLocator getLocator();

	/**
	 * Set configuration locator.
	 */
	void setLocator(ConfigurationLocator locator);

	/**
	 * Get root configuration structure.
	 */
	Configuration getRootConfiguration();

	/**
	 * Get parent configuration structure.
	 */
	Configuration getParentConfiguration();

	/**
	 * Create an Configuration object that is a subset
	 * of this one. The new Configuration object contains every key from
	 * the current Configuration that starts with prefix. The prefix is
	 * removed from the keys in the subset.
	 *
	 * @param prefix The prefix used to select the properties.
	 */
	Configuration subset(String prefix);

	/**
	 * Merge two configuration structure. All entries from input configuration structure will be transfered in the
	 * current structure, merging only data keys. Newly entries will be marked for the current locator.
	 *
	 * @param configuration merged configuration structure.
	 */
	void merge(Configuration configuration);

	/**
	 * Replace current configuration entires with all parts from specified configuration structure.
	 *
	 * @param configuration merged configuration structure.
	 */
	void replace(Configuration configuration);

	/**
	 * Remove all configuration entries from the current configuration structure.
	 */
	void reset();

	/**
	 * Check if the configuration is empty.
	 *
	 * @return true is the configuration contains no key/value pair, false otherwise
	 */
	boolean isEmpty();

	/**
	 * Gets the index of the specified key.
	 *
	 * @param key the key to find the index of
	 * @return the index, or -1 if not found
	 */
	int indexOf(Object key);

	/**
	 * Gets the index of the specified key.
	 *
	 * @param entry the configuration entry to find the index of
	 * @return the index, or -1 if not found
	 */
	int indexOf(ConfigurationEntry entry);

	/**
	 * Check if the configuration contains the key.
	 *
	 * @return true is the configuration contains a value for this key, false otherwise
	 */
	boolean containsKey(String key);

	/**
	 * Check if in the current locator is registered the specified configuration entry.
	 *
	 * @param entry configuration entry to be found
	 * @return true if the specified key (defined by the specified configuration entry) exist
	 */
	boolean containsKey(ConfigurationEntry entry);

	/**
	 * Check if in the current locator registered the specified configuration value.
	 *
	 * @param value configuration entry key name to be used to found an entry structure.
	 * @return true if the specified value exists
	 */
	boolean containsValue(Object value);

	/**
	 * Check if the configuration contains the specified configuration entry
	 */
	boolean containsValue(ConfigurationEntry key);

	/**
	 * Check if the configuration contains the key and value
	 */
	boolean hasValue(String key, Object value);

	/**
	 * Add a new key to the configuration. If it already exists then the value
	 * stated here will be added to the configuration entry. For example, if
	 * <p/>
	 * resource.loader = file
	 * <p/>
	 * is already present in the configuration and you
	 * <p/>
	 * addProperty("resource.loader", "classpath")
	 * <p/>
	 * Then you will end up with a List like the following:
	 * <p/>
	 * ["file", "classpath"]
	 *
	 * @param key   The Key to add the key to.
	 * @param value The Value to add.
	 */
	void addKey(String key, Object value);

	/**
	 * Add a configuration entry. If entry already exists the value
	 * stored in the current locator will apended newly value object.
	 *
	 * <p>
	 * Note: Specified entry will be forced to take the current locator from this instance of
	 * configuration structure.
	 * 
	 * @param entry   The configuration to add in the current locator.
	 */
	void addConfigurationEntry(ConfigurationEntry entry);

	/**
	 * Set a configuration entry. If entry already exists the value
	 * stored in the current locator will be replaced by newly entry.
	 *
	 * <p>
	 * Note: Specified entry will be forced to take the current locator from this instance of
	 * configuration structure.
	 *
	 * @param entry   The configuration to add in the current locator.
	 */
	void setConfigurationEntry(ConfigurationEntry entry);

	/**
	 * Read entry from underlying map.
	 *
	 * @param key key to use for mapping
	 * @return object associated with the given configuration key.
	 */
	ConfigurationEntry getConfigurationEntry(String key);

	/**
	 * Set a key, this will replace any previously
	 * set values. Set values is implicitly a call
	 * to clearProperty(key), addProperty(key,value).
	 *
	 * @param key   The key of the key to change
	 * @param value The new value
	 */
	void setKey(String key, Object value);

	/**
	 * Clear a key in the configuration.
	 *
	 * @param key the key to remove along with corresponding value.
	 */
	void removeKey(String key);

	/**
	 * Gets a key from the configuration.
	 *
	 * @param key key to retrieve
	 * @return value as object. Will return user value if exists,
	 *         if not then default value if exists, otherwise null
	 */
	Object getKey(String key);

	/**
	 * Gets a key from the configuration using values from the current configuration entry, concatenated into one string
	 *
	 * @param key key to retrieve
	 * @return value as object. Will return user value if exists,
	 *         if not then default value if exists, otherwise null
	 */
	Object getCompleteKey(String key);

	/**
	 * Get the list of the keys contained in the configuration
	 * repository that match the specified prefix.
	 *
	 * @param prefix The prefix to test against.
	 * @return An Iterator of keys that match the prefix.
	 */
	Iterator getKeys(String prefix);

	/**
	 * Get the list of the keys contained in the configuration
	 * repository.
	 *
	 * @return An Iterator.
	 */
	Iterator getKeys();

	/**
	 * Get a list of properties for enties locator.
	 *
	 * @return The associated properties (<code> Properties</code> structure).
	 * @throws ClassCastException       is thrown if the key maps to an
	 *                                  object that is not a String/List.
	 * @throws IllegalArgumentException if one of the tokens is
	 *                                  malformed (does not contain an equals sign).
	 */
	Properties getProperties();

	/**
	 * Get a list of properties associated with the given
	 * configuration key.
	 *
	 * @param key The configuration key.
	 * @return The associated properties if key is found.
	 * @throws ClassCastException       is thrown if the key maps to an
	 *                                  object that is not a String/List.
	 * @throws IllegalArgumentException if one of the tokens is
	 *                                  malformed (does not contain an equals sign).
	 */
	Properties getProperties(String key);


	/**
	 * Get a boolean associated with the given configuration key.
	 *
	 * @param key The configuration key.
	 * @return The associated boolean.
	 * @throws NoSuchElementException is thrown if the key doesn't
	 *                                map to an existing object.
	 * @throws ClassCastException     is thrown if the key maps to an
	 *                                object that is not a Boolean.
	 */
	boolean getBoolean(String key);

	/**
	 * Get a boolean associated with the given configuration key.
	 *
	 * @param key          The configuration key.
	 * @param defaultValue The default value.
	 * @return The associated boolean.
	 * @throws ClassCastException is thrown if the key maps to an
	 *                            object that is not a Boolean.
	 */
	boolean getBoolean(String key, boolean defaultValue);

	/**
	 * Get a boolean associated with the given configuration key.
	 *
	 * @param key          The configuration key.
	 * @param defaultValue The default value.
	 * @return The associated boolean if key is found and has valid
	 *         format, default value otherwise.
	 * @throws ClassCastException is thrown if the key maps to an
	 *                            object that is not a Boolean.
	 */
	Boolean getBoolean(String key, Boolean defaultValue);

	/**
	 * Get a byte associated with the given configuration key.
	 *
	 * @param key The configuration key.
	 * @return The associated byte.
	 * @throws NoSuchElementException is thrown if the key doesn't
	 *                                map to an existing object.
	 * @throws ClassCastException     is thrown if the key maps to an
	 *                                object that is not a Byte.
	 * @throws NumberFormatException  is thrown if the value mapped
	 *                                by the key has not a valid number format.
	 */
	byte getByte(String key);

	/**
	 * Get a byte associated with the given configuration key.
	 *
	 * @param key          The configuration key.
	 * @param defaultValue The default value.
	 * @return The associated byte.
	 * @throws ClassCastException    is thrown if the key maps to an
	 *                               object that is not a Byte.
	 * @throws NumberFormatException is thrown if the value mapped
	 *                               by the key has not a valid number format.
	 */
	byte getByte(String key, byte defaultValue);

	/**
	 * Get a byte associated with the given configuration key.
	 *
	 * @param key          The configuration key.
	 * @param defaultValue The default value.
	 * @return The associated byte if key is found and has valid format, default
	 *         value otherwise.
	 * @throws ClassCastException    is thrown if the key maps to an object that
	 *                               is not a Byte.
	 * @throws NumberFormatException is thrown if the value mapped by the key
	 *                               has not a valid number format.
	 */
	Byte getByte(String key, Byte defaultValue);

	/**
	 * Get a double associated with the given configuration key.
	 *
	 * @param key The configuration key.
	 * @return The associated double.
	 * @throws NoSuchElementException is thrown if the key doesn't
	 *                                map to an existing object.
	 * @throws ClassCastException     is thrown if the key maps to an
	 *                                object that is not a Double.
	 * @throws NumberFormatException  is thrown if the value mapped
	 *                                by the key has not a valid number format.
	 */
	double getDouble(String key);

	/**
	 * Get a double associated with the given configuration key.
	 *
	 * @param key          The configuration key.
	 * @param defaultValue The default value.
	 * @return The associated double.
	 * @throws ClassCastException    is thrown if the key maps to an
	 *                               object that is not a Double.
	 * @throws NumberFormatException is thrown if the value mapped
	 *                               by the key has not a valid number format.
	 */
	double getDouble(String key, double defaultValue);

	/**
	 * Get a double associated with the given configuration key.
	 *
	 * @param key          The configuration key.
	 * @param defaultValue The default value.
	 * @return The associated double if key is found and has valid
	 *         format, default value otherwise.
	 * @throws ClassCastException    is thrown if the key maps to an
	 *                               object that is not a Double.
	 * @throws NumberFormatException is thrown if the value mapped
	 *                               by the key has not a valid number format.
	 */
	Double getDouble(String key, Double defaultValue);

	/**
	 * Get a float associated with the given configuration key.
	 *
	 * @param key The configuration key.
	 * @return The associated float.
	 * @throws NoSuchElementException is thrown if the key doesn't
	 *                                map to an existing object.
	 * @throws ClassCastException     is thrown if the key maps to an
	 *                                object that is not a Float.
	 * @throws NumberFormatException  is thrown if the value mapped
	 *                                by the key has not a valid number format.
	 */
	float getFloat(String key);

	/**
	 * Get a float associated with the given configuration key.
	 *
	 * @param key          The configuration key.
	 * @param defaultValue The default value.
	 * @return The associated float.
	 * @throws ClassCastException    is thrown if the key maps to an
	 *                               object that is not a Float.
	 * @throws NumberFormatException is thrown if the value mapped
	 *                               by the key has not a valid number format.
	 */
	float getFloat(String key, float defaultValue);

	/**
	 * Get a float associated with the given configuration key.
	 *
	 * @param key          The configuration key.
	 * @param defaultValue The default value.
	 * @return The associated float if key is found and has valid
	 *         format, default value otherwise.
	 * @throws ClassCastException    is thrown if the key maps to an
	 *                               object that is not a Float.
	 * @throws NumberFormatException is thrown if the value mapped
	 *                               by the key has not a valid number format.
	 */
	Float getFloat(String key, Float defaultValue);

	/**
	 * Get a int associated with the given configuration key.
	 *
	 * @param key The configuration key.
	 * @return The associated int.
	 * @throws NoSuchElementException is thrown if the key doesn't
	 *                                map to an existing object.
	 * @throws ClassCastException     is thrown if the key maps to an
	 *                                object that is not a Integer.
	 * @throws NumberFormatException  is thrown if the value mapped
	 *                                by the key has not a valid number format.
	 */
	int getInt(String key);

	/**
	 * Get a int associated with the given configuration key.
	 *
	 * @param key          The configuration key.
	 * @param defaultValue The default value.
	 * @return The associated int.
	 * @throws ClassCastException    is thrown if the key maps to an
	 *                               object that is not a Integer.
	 * @throws NumberFormatException is thrown if the value mapped
	 *                               by the key has not a valid number format.
	 */
	int getInt(String key, int defaultValue);

	/**
	 * Get a int associated with the given configuration key.
	 *
	 * @param key          The configuration key.
	 * @param defaultValue The default value.
	 * @return The associated int if key is found and has valid format, default
	 *         value otherwise.
	 * @throws ClassCastException    is thrown if the key maps to an object that
	 *                               is not a Integer.
	 * @throws NumberFormatException is thrown if the value mapped by the key
	 *                               has not a valid number format.
	 */
	Integer getInteger(String key, Integer defaultValue);

	/**
	 * Get a long associated with the given configuration key.
	 *
	 * @param key The configuration key.
	 * @return The associated long.
	 * @throws NoSuchElementException is thrown if the key doesn't
	 *                                map to an existing object.
	 * @throws ClassCastException     is thrown if the key maps to an
	 *                                object that is not a Long.
	 * @throws NumberFormatException  is thrown if the value mapped
	 *                                by the key has not a valid number format.
	 */
	long getLong(String key);

	/**
	 * Get a long associated with the given configuration key.
	 *
	 * @param key          The configuration key.
	 * @param defaultValue The default value.
	 * @return The associated long.
	 * @throws ClassCastException    is thrown if the key maps to an
	 *                               object that is not a Long.
	 * @throws NumberFormatException is thrown if the value mapped
	 *                               by the key has not a valid number format.
	 */
	long getLong(String key, long defaultValue);

	/**
	 * Get a long associated with the given configuration key.
	 *
	 * @param key          The configuration key.
	 * @param defaultValue The default value.
	 * @return The associated long if key is found and has valid
	 *         format, default value otherwise.
	 * @throws ClassCastException    is thrown if the key maps to an
	 *                               object that is not a Long.
	 * @throws NumberFormatException is thrown if the value mapped
	 *                               by the key has not a valid number format.
	 */
	Long getLong(String key, Long defaultValue);

	/**
	 * Get a short associated with the given configuration key.
	 *
	 * @param key The configuration key.
	 * @return The associated short.
	 * @throws NoSuchElementException is thrown if the key doesn't
	 *                                map to an existing object.
	 * @throws ClassCastException     is thrown if the key maps to an
	 *                                object that is not a Short.
	 * @throws NumberFormatException  is thrown if the value mapped
	 *                                by the key has not a valid number format.
	 */
	short getShort(String key);

	/**
	 * Get a short associated with the given configuration key.
	 *
	 * @param key          The configuration key.
	 * @param defaultValue The default value.
	 * @return The associated short.
	 * @throws ClassCastException    is thrown if the key maps to an
	 *                               object that is not a Short.
	 * @throws NumberFormatException is thrown if the value mapped
	 *                               by the key has not a valid number format.
	 */
	short getShort(String key, short defaultValue);

	/**
	 * Get a short associated with the given configuration key.
	 *
	 * @param key          The configuration key.
	 * @param defaultValue The default value.
	 * @return The associated short if key is found and has valid
	 *         format, default value otherwise.
	 * @throws ClassCastException     is thrown if the key maps to an
	 *                                object that is not a Short.
	 * @throws NumberFormatException  is thrown if the value mapped
	 *                                by the key has not a valid number format.
	 * @throws NoSuchElementException is thrown if the key doesn't
	 *                                map to an existing object.
	 */
	Short getShort(String key, Short defaultValue);

	/**
	 * Get a BigDecimal associated with the given configuration key.
	 *
	 * @param key The configuration key.
	 * @return The associated BigDecimal if key is found and has valid format
	 * @throws NoSuchElementException is thrown if the key doesn't
	 *                                map to an existing object.
	 */
	BigDecimal getBigDecimal(String key) throws NoSuchElementException;

	/**
	 * Get a BigDecimal associated with the given configuration key.
	 *
	 * @param key          The configuration key.
	 * @param defaultValue The default value.
	 * @return The associated BigDecimal if key is found and has valid
	 *         format, default value otherwise.
	 */
	BigDecimal getBigDecimal(String key, BigDecimal defaultValue);

	/**
	 * Get a BigInteger associated with the given configuration key.
	 *
	 * @param key The configuration key.
	 * @return The associated BigInteger if key is found and has valid format
	 * @throws NoSuchElementException is thrown if the key doesn't
	 *                                map to an existing object.
	 */
	BigInteger getBigInteger(String key) throws NoSuchElementException;

	/**
	 * Get a BigInteger associated with the given configuration key.
	 *
	 * @param key          The configuration key.
	 * @param defaultValue The default value.
	 * @return The associated BigInteger if key is found and has valid
	 *         format, default value otherwise.
	 */
	BigInteger getBigInteger(String key, BigInteger defaultValue);

	/**
	 * Get a string associated with the given configuration key.
	 *
	 * @param key The configuration key.
	 * @return The associated string.
	 * @throws ClassCastException     is thrown if the key maps to an object that
	 *                                is not a String.
	 * @throws NoSuchElementException is thrown if the key doesn't
	 *                                map to an existing object.
	 */
	String getString(String key);

	/**
	 * Get a string associated with the given configuration key.
	 *
	 * @param key          The configuration key.
	 * @param defaultValue The default value.
	 * @return The associated string if key is found and has valid
	 *         format, default value otherwise.
	 * @throws ClassCastException is thrown if the key maps to an object that
	 *                            is not a String.
	 */
	String getString(String key, String defaultValue);

	/**
	 * Get an array of strings associated with the given configuration
	 * key.
	 *
	 * @param key The configuration key.
	 * @return The associated string array if key is found.
	 * @throws ClassCastException is thrown if the key maps to an
	 *                            object that is not a String/List of Strings.
	 */
	String[] getStringArray(String key);

	/**
	 * Get a string associated with the given configuration key but in base of the result is an array is concatenated
	 *
	 * @param key The configuration key.
	 * @return The associated string array if key is found.
	 * @throws ClassCastException is thrown if the key maps to an object that is not a String/List of Strings.
	 */
	String getCompleteString(String key);

	/**
	 * Get a string associated with the given configuration key but in base of the result is an array is concatenated
	 *
	 * @param key The configuration key.
	 * @param defaultValue The default value.
	 * @return The associated string array if key is found.
	 * @throws ClassCastException is thrown if the key maps to an object that is not a String/List of Strings.
	 */
	String getCompleteString(String key, String defaultValue);

	/**
	 * Get a vector of strings associated with the given configuration
	 * key.
	 *
	 * @param key The configuration key.
	 * @return The associated string array if key is found.
	 */
	Vector getVector(String key);

	/**
	 * Get a list of strings associated with the given configuration key.
	 *
	 * @param key The configuration key.
	 * @return The associated string array if key is found.
	 */
	public List getList(String key);

	/**
	 * Get an array of strings associated with the given configuration
	 * key.
	 *
	 * @param key The configuration key.
	 * @return The associated string array if key is found.
	 * @throws ClassCastException is thrown if the key maps to an
	 *                            object that is not a String/List of Strings.
	 */
	String[] getStringArray(String key, String[] defVal);

	/**
	 * Get a vector of strings associated with the given configuration
	 * key.
	 *
	 * @param key The configuration key.
	 * @return The associated string array if key is found.
	 */
	Vector getVector(String key, Vector defVal);

	/**
	 * Get a list of strings associated with the given configuration key.
	 *
	 * @param key The configuration key.
	 * @return The associated string array if key is found.
	 */
	List getList(String key, List defVal);

	/**
	 * Returns a list with all comments (which are configuration entries) that will match the specified key.
	 * If the key is null will return null list. If the key isn't matched will return an empty list.
	 *
	 * @param key string key to identify comments
	 * @param type type of match:
	 * 	<ul>
	 * 		<li>0 = exact match</li>
	 * 		<li>1 = the comment should start with the <code>key</code> value</li>
	 * 		<li>2 = the comment should contain the <code>key</code> value</li>
	 * 		<li>3 = the comment should end the <code>key</code> value</li>
	 * 	</ul>
	 * @return list with all matched configuration entries
	 */
	List getComments(String key, int type);

	/**
	 * Returns a list with all comments (which are configuration entries) that will match the specified key.
	 * If the key is null will return null list. If the key isn't matched will return an empty list. The match
	 * means to find the key anywhere in the body of the comment value.
	 *
	 * @param key string key to identify comments
	 * @return list with all matched configuration entries
	 */
	List getComments(String key);
}
