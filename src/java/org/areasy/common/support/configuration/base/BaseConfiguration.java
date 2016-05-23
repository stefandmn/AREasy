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

import org.areasy.common.data.BooleanUtility;
import org.areasy.common.data.StringUtility;
import org.areasy.common.data.StringEscapeUtility;
import org.areasy.common.data.type.Predicate;
import org.areasy.common.data.type.iterator.FilterIterator;
import org.areasy.common.support.configuration.Configuration;
import org.areasy.common.support.configuration.ConfigurationEntry;
import org.areasy.common.support.configuration.ConfigurationLocator;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * Abstract configuration class. Provide basic functionality but does not
 * store any data. If you want to write your own Configuration class
 * then you should implement only abstract methods from this class.
 *
 * @version $Id: BaseConfiguration.java,v 1.8 2008/05/14 09:32:42 swd\stefan.damian Exp $
 */
public class BaseConfiguration implements Configuration
{
	/** Configuration structure locator */
	private ConfigurationLocator locator = null;

	/** Root configuration structure */
	private Configuration root = null;


	/** start token  */
	protected static final String START_TOKEN = "${";

	/** end token */
	protected static final String END_TOKEN = "}";

	/**
	 * Base configuration structure: default constructor.
	 */
	public BaseConfiguration()
	{
		this.locator = new BaseConfigurationLocator();
	}

	/**
	 * Base configuration structure: default constructor.
	 *
	 * @param locator configuration locator structure
	 */
	public BaseConfiguration(ConfigurationLocator locator)
	{
		this.locator = locator;
	}

	/**
	 * Get configuration locator
	 */
	public ConfigurationLocator getLocator()
	{
		return locator;
	}

	/**
	 * Set configuration locator.
	 *
	 * @param locator configuration locator structure
	 */
	public void setLocator(ConfigurationLocator locator)
	{
		this.locator = locator;
	}

	/**
	 * Get root configuration structure.
	 */
	public Configuration getRootConfiguration()
	{
		if(root == null) return this;
			else if (root.getParentConfiguration() != null) return root.getRootConfiguration();
				else return root;
	}

	/**
	 * Get root configuration structure.
	 */
	public Configuration getParentConfiguration()
	{
		return root;
	}

	/**
	 * Set root configuration structure.
	 *
	 * @param root parent configuration structure
	 */
	protected void setParentConfiguration(Configuration root)
	{
		this.root = root;
	}

	/**
	 * Add a key to the configuration. If it already exists then the value
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
	 * @param token The Value to add.
	 */
	public void addKey(String key, Object token)
	{
		ConfigurationEntry entry;

		if (token instanceof ConfigurationEntry) entry = (ConfigurationEntry) token;
		else
		{
			entry = new BaseConfigurationEntry();
			entry.setData(key, token);
		}

		locator.addNode(entry);
	}

	/**
	 * Add a key to the configuration. If it already exists then the value
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
	 * @param token The Value to add.
	 * @param index an index order where to post this new configuration node
	 */
	public void addKey(int index, String key, Object token)
	{
		ConfigurationEntry entry;

		if (token instanceof ConfigurationEntry) entry = (ConfigurationEntry) token;
		else
		{
			entry = new BaseConfigurationEntry();
			entry.setData(key, token);
		}

		locator.addNode(index, entry);
	}

	/**
	 * Add a configuration entry. If entry already exists the value
	 * stored in the current locator will appended newly value object.
	 *
	 * <p>
	 * Note: Specified entry will be forced to take the current locator from this instance of
	 * configuration structure.
	 * 
	 * @param entry   The configuration to add in the current locator.
	 */
	public void addConfigurationEntry(ConfigurationEntry entry)
	{
		entry.setLocator(locator);
		locator.addNode(entry);
	}

	/**
	 * Gets a key from the configuration.
	 *
	 * @param key key to retrieve
	 * @return value as object. Will return user value if exists,
	 *         if not then default value if exists, otherwise null
	 */
	public Object getKey(String key)
	{
		// first, try to get from the 'user value' store
		ConfigurationEntry o = getConfigurationEntry(key);

		return (o != null ? o.getValue() : null);
	}

	/**
	 * Gets a key from the configuration using values from the current configuration entry, concatenated into one string
	 *
	 * @param key key to retrieve
	 * @return value as object. Will return user value if exists,
	 *         if not then default value if exists, otherwise null
	 */
	public Object getCompleteKey(String key)
	{
		// first, try to get from the 'user value' store
		ConfigurationEntry o = getConfigurationEntry(key);

		return (o != null ? o.getCompleteValue() : null);
	}

	/**
	 * Set a configuration entry. If entry already exists the value
	 * stored in the current locator will be replaced by newly entry.
	 * <p>
	 * Note: Specified entry will be forced to take the current locator from this instance of
	 * configuration structure.
	 * @param entry   The configuration to add in the current locator.
	 */
	public void setConfigurationEntry(ConfigurationEntry entry)
	{
		entry.setLocator(locator);
		locator.setNode(entry);
	}

	/**
	 * Read entry from underlying map.
	 *
	 * @param key key to use for mapping
	 * @return object associated with the given configuration key.
	 */
	public ConfigurationEntry getConfigurationEntry(String key)
	{
		return locator.getEntry(key);
	}

	/**
	 * Interpolate key names to handle ${key} stuff
	 *
	 * @param base string to interpolate
	 * @return returns the key name with the ${key} substituted
	 */
	protected String interpolate(String base)
	{
		String result = interpolateHelper(base, null);
		return (result);
	}

	/**
	 * Recursive handler for multiple levels of interpolation.
	 * <p/>
	 * When called the first time, priorVariables should be null.
	 *
	 * @param base           string with the ${key} variables
	 * @param priorVariables serves two purposes: to allow checking for
	 *                       loops, and creating a meaningful exception message should a loop
	 *                       occur.  It's 0'th element will be set to the value of base from
	 *                       the first call.  All subsequent interpolated variables are added
	 *                       afterward.
	 * @return the string with the interpolation taken care of
	 */
	protected String interpolateHelper(String base, List priorVariables)
	{
		if (base == null) return null;

		// on the first call initialize priorVariables and add base as the first element
		if (priorVariables == null)
		{
			priorVariables = new ArrayList();
			priorVariables.add(base);
		}

		int begin = -1;
		int end = -1;
		int prec = 0 - END_TOKEN.length();

		String variable = null;
		StringBuffer result = new StringBuffer();

		while (((begin = base.indexOf(START_TOKEN, prec + END_TOKEN.length())) > -1) && ((end = base.indexOf(END_TOKEN, begin)) > -1))
		{
			result.append(base.substring(prec + END_TOKEN.length(), begin));
			variable = base.substring(begin + START_TOKEN.length(), end);

			// if we've got a loop, create a useful exception message and throw
			if (priorVariables.contains(variable))
			{
				String initialBase = priorVariables.remove(0).toString();
				priorVariables.add(variable);

				StringBuffer priorVariableSb = new StringBuffer();

				// create a nice trace of interpolated variables like so: var1->var2->var3
				for (Iterator it = priorVariables.iterator(); it.hasNext();)
				{
					priorVariableSb.append(it.next());
					if (it.hasNext()) priorVariableSb.append("->");
				}

				throw new IllegalStateException("Infinite loop in key interpolation of " + initialBase + ": " + priorVariableSb.toString());
			}
			else priorVariables.add(variable);

			Object value = getKey(variable);
			if (value != null)
			{
				result.append(interpolateHelper(value.toString(), priorVariables));
				priorVariables.remove(priorVariables.size() - 1);
			}
			//else result.append(START_TOKEN).append(variable).append(END_TOKEN);

			prec = end;
		}

		result.append(base.substring(prec + END_TOKEN.length(), base.length()));

		return StringEscapeUtility.unescapeComma(result.toString());
	}

	/**
	 * Create a <code>Configuration</code> object that is a subset of this one.
	 * <p>
	 * This method will returns all sub-entries from the all locators.
	 * This implementation will define a a new locator that will store all found data sub-entries.
	 * All returned sub-entries will be re-created (cloned) from "parent entries" and <b>will include only
	 * data entries</b> (all comments will be ignored). This locator will work like <b>root</b>
	 * locator for the current <code>Configuration</code> structure.
	 * </p>
	 * <p>
	 * If in the parent locator exists variables will be eliminated and key values will include complet value.
	 *
	 * @param prefix prefix string for keys
	 * @return subset of configuration if there is keys, that match
	 *         given prefix, or <code>null</code> if there is no such keys. This method will return all data
	 * configuration entries from all locators defining (like "host") a child locator
	 */
	public Configuration subset(String prefix)
	{
		BaseConfiguration subset = null;

		//check if prefix is null.
		if(StringUtility.isEmpty(prefix)) return this;

		Iterator iterator = getLocator().getAllEntries();

		if(iterator != null)
		{
			prefix += ".";
			ConfigurationLocator locator = new BaseConfigurationLocator();

			while(iterator.hasNext())
			{
				ConfigurationEntry entry = (ConfigurationEntry) iterator.next();
				if(entry.isData() && entry.getKey().startsWith(prefix) && entry.getKey().length() > prefix.length())
				{
					ConfigurationEntry clone = (ConfigurationEntry)entry.clone();

					Vector values = getVector(entry.getKey());

					clone.setData(entry.getKey().substring(prefix.length()), values);
					clone.setLocator(locator);

					locator.addNode(clone);
				}
			}

			subset = new BaseConfiguration(locator);
			subset.setParentConfiguration(this);
		}

		return subset;
	}

	/**
	 * Merge two configuration structure. All entries from input configuration structure will be transfered in the
	 * current structure, merging only data keys. Newly entries will be marked for the current locator.
	 *
	 * @param configuration merged configuration structure.
	 */
	public void merge(Configuration configuration)
	{
		if (configuration != null && !configuration.isEmpty())
		{
			Iterator iterator = configuration.getLocator().getAllDataEntries();

			while (iterator != null && iterator.hasNext())
			{
				ConfigurationEntry entry = (ConfigurationEntry)iterator.next();
				ConfigurationEntry clone = (ConfigurationEntry) entry.clone();

				setConfigurationEntry(clone);
			}
		}
	}

	/**
	 * Replace current configuration entires with all parts from specified configuration structure.
	 *
	 * @param configuration merged configuration structure.
	 */
	public void replace(Configuration configuration)
	{
		reset();
		merge(configuration);
	}

	/**
	 * Remove all configuration entries from the current configuration structure.
	 */
	public void reset()
	{
		Iterator iterator = getKeys();
		List keys = new Vector();

		while(iterator != null && iterator.hasNext())
		{
			String key = (String) iterator.next();
			if(StringUtility.isNotEmpty(key)) keys.add(key);
		}

		for(int i = 0; i < keys.size(); i++)
		{
			String key = (String) keys.get(i);
			removeKey(key);
		}
	}

	/**
	 * Check if the configuration is empty
	 *
	 * @return <code>true</code> if Configuration is empty,
	 *         <code>false</code> otherwise.
	 */
	public boolean isEmpty()
	{
		return getLocator().isEmpty();
	}

	/**
	 * Gets the index of the specified key.
	 *
	 * @param key the key to find the index of
	 * @return the index, or -1 if not found
	 */
	public int indexOf(Object key)
	{
		return getLocator().indexOf(key);
	}

	/**
	 * Gets the index of the specified key.
	 *
	 * @param entry the configuration entry to find the index of
	 * @return the index, or -1 if not found
	 */
	public int indexOf(ConfigurationEntry entry)
	{
		return getLocator().indexOf(entry);
	}

	/**
	 * Check if the configuration contains the key
	 *
	 * @param key the configuration key
	 * @return <code>true</code> if Configuration contain given key, <code>false</code> otherwise.
	 */
	public boolean containsKey(String key)
	{
		return getLocator().containsKey(key);
	}

	/**
	 * Check if in the current locator registered the specified configuration entry.
	 *
	 * @param entry configuration entry to be found
	 * @return true if the specified key (defined by the specified configuration entry) exist
	 */
	public boolean containsKey(ConfigurationEntry entry)
	{
		return getLocator().containsKey(entry);
	}

	/**
	 * Check if in the current locator registered the specified configuration value.
	 *
	 * @param value configuration entry key name to be used to found an entry structure.
	 * @return true if the specified value exists
	 */
	public boolean containsValue(Object value)
	{
		return getLocator().containsValue(value);
	}

	/**
	 * Check if the configuration contains the specified configuration entry
	 */
	public boolean containsValue(ConfigurationEntry key)
	{
		return getLocator().containsKey(key);
	}

	/**
	 * Check if the configuration contains the key and value
	 */
	public boolean hasValue(String key, Object value)
	{
		return getLocator().hasValue(key, value);
	}

	/**
	 * Set a key, this will replace any previously
	 * set values. Set values is implicitly a call
	 * to clearProperty(key), addProperty(key,value).
	 *
	 * @param key   the configuration key
	 * @param value the key value
	 */
	public void setKey(String key, Object value)
	{
		if (containsKey(key))
		{
			ConfigurationEntry entry = getConfigurationEntry(key);
			entry.setValue(value);
		}
		else addKey(key, value);
	}

	/**
	 * Clear a key in the configuration.
	 *
	 * @param key the key to remove along with corresponding value.
	 */
	public void removeKey(String key)
	{
		if (containsKey(key))
		{
			ConfigurationEntry entry = getConfigurationEntry(key);
			getLocator().removeNode(entry);
		}
	}

	/**
	 * Get the list of the keys contained in the configuration
	 * repository.
	 *
	 * @return An Iterator.
	 */
	public Iterator getKeys()
	{
		return getLocator().getAllDataKeys();
	}

	/**
	 * Get the list of the keys contained in the configuration
	 * repository that match the specified prefix.
	 *
	 * @param prefix The prefix to test against.
	 * @return An Iterator of keys that match the prefix.
	 */
	public Iterator getKeys(final String prefix)
	{
		return new FilterIterator(getKeys(), new Predicate()
		{
			public boolean evaluate(Object obj)
			{
				boolean matching = false;

				if (obj instanceof String)
				{
					String key = (String) obj;
					matching = key.startsWith(prefix + ".") || key.equals(prefix);
				}

				return matching;
			}
		});
	}

	/**
	 * Get a list of properties for enties locator.
	 *
	 * @return The associated properties (<code> Properties</code> structure).
	 * @throws ClassCastException       is thrown if the key maps to an
	 *                                  object that is not a String/List.
	 * @throws IllegalArgumentException if one of the tokens is
	 *                                  malformed (does not contain an equals sign).
	 */
	public Properties getProperties()
	{
		Properties props = new Properties();

		Iterator iterator = getLocator().getAllEntries();

		if(iterator != null)
		{
			while(iterator.hasNext())
			{
				ConfigurationEntry entry = (ConfigurationEntry) iterator.next();
				if(entry.isData())
				{
					Vector vector = getVector(entry.getKey());

					String values[] = (String[])vector.toArray(new String[vector.size()]);
					props.setProperty(entry.getKey(), StringUtility.join(values, ','));
				}
			}
		}

		return props;
	}

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
	 * @see #getProperties(String, Properties)
	 */
	public Properties getProperties(String key)
	{
		return getProperties(key, null);
	}

	/**
	 * Get a list of properties associated with the given
	 * configuration key.
	 *
	 * @param key      The configuration key.
	 * @param defaults Any default values for the returned
	 *                 <code>Properties</code> object.  Ignored if <code>null</code>.
	 * @return The associated properties if key is found.
	 * @throws ClassCastException       is thrown if the key maps to an
	 *                                  object that is not a String/List of Strings.
	 * @throws IllegalArgumentException if one of the tokens is
	 *                                  malformed (does not contain an equals sign).
	 */
	public Properties getProperties(String key, Properties defaults)
	{
		String[] tokens = getStringArray(key);

		Properties props = (defaults == null ? new Properties() : new Properties(defaults));

		for (int i = 0; i < tokens.length; i++)
		{
			String token = tokens[i];
			int equalSign = token.indexOf('=');

			if (equalSign > 0)
			{
				String pkey = token.substring(0, equalSign).trim();
				String pvalue = token.substring(equalSign + 1).trim();
				props.put(pkey, pvalue);
			}
			else
			{
				if (tokens.length == 1 && "".equals(token)) break;
					else throw new IllegalArgumentException("'" + token + "' does not contain an equals sign");
			}
		}

		return props;
	}

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
	public boolean getBoolean(String key)
	{
		Boolean b = getBoolean(key, null);

		if (b != null) return b.booleanValue();
			else throw new NoSuchElementException("'" + key + "' doesn't map to an existing object");
	}

	/**
	 * Get a boolean associated with the given configuration key.
	 *
	 * @param key          The configuration key.
	 * @param defaultValue The default value.
	 * @return The associated boolean.
	 * @throws ClassCastException is thrown if the key maps to an
	 *                            object that is not a Boolean.
	 */
	public boolean getBoolean(String key, boolean defaultValue)
	{
		return getBoolean(key, new Boolean(defaultValue)).booleanValue();
	}

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
	public Boolean getBoolean(String key, Boolean defaultValue)
	{
		Object value = getKey(key);

		if (value instanceof Boolean) return (Boolean) value;
			else if (value instanceof String) return BooleanUtility.toBooleanObject(interpolate((String) value));
				else if (value == null) return defaultValue;
					else throw new ClassCastException("Key '" + key + "' doesn't map to a Boolean object");
	}

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
	public byte getByte(String key)
	{
		Byte b = getByte(key, null);

		if (b != null) return b.byteValue();
			else throw new NoSuchElementException("Key '" + key + " doesn't map to an existing object");
	}

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
	public byte getByte(String key, byte defaultValue)
	{
		return getByte(key, new Byte(defaultValue)).byteValue();
	}

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
	public Byte getByte(String key, Byte defaultValue)
	{
		Object value = getKey(key);

		if (value instanceof Byte) return (Byte) value;
			else if (value instanceof String) return new Byte((String) value);
				else if (value == null) return defaultValue;
					else throw new ClassCastException("Key '" + key + "' doesn't map to a Byte object");
	}

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
	public double getDouble(String key)
	{
		Double d = getDouble(key, null);

		if (d != null) return d.doubleValue();
			else throw new NoSuchElementException("Key '" + key + "' doesn't map to an existing object");
	}

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
	public double getDouble(String key, double defaultValue)
	{
		return getDouble(key, new Double(defaultValue)).doubleValue();
	}

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
	public Double getDouble(String key, Double defaultValue)
	{
		Object value = getKey(key);

		if (value instanceof Double) return (Double) value;
			else if (value instanceof String) return new Double( interpolate((String) value) );
				else if (value == null) return defaultValue;
					else throw new ClassCastException("Key '" + key + "' doesn't map to a Double object");
	}

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
	public float getFloat(String key)
	{
		Float f = getFloat(key, null);

		if (f != null) return f.floatValue();
			else throw new NoSuchElementException("Key '" + key + "' doesn't map to an existing object");
	}

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
	public float getFloat(String key, float defaultValue)
	{
		return getFloat(key, new Float(defaultValue)).floatValue();
	}

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
	public Float getFloat(String key, Float defaultValue)
	{
		Object value = getKey(key);

		if (value instanceof Float) return (Float) value;
			else if (value instanceof String) return new Float( interpolate((String) value) );
				else if (value == null) return defaultValue;
					else throw new ClassCastException("Key '" + key + "' doesn't map to a Float object");
	}

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
	public int getInt(String key)
	{
		Integer i = getInteger(key, null);

		if (i != null) return i.intValue();
			else throw new NoSuchElementException("Key '" + key + "' doesn't map to an existing object");
	}

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
	public int getInt(String key, int defaultValue)
	{
		Integer i = getInteger(key, null);

		if (i == null) return defaultValue;

		return i.intValue();
	}

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
	public Integer getInteger(String key, Integer defaultValue)
	{
		Object value = getKey(key);

		if (value instanceof Integer) return (Integer) value;
			else if (value instanceof String) return new Integer( interpolate((String) value) );
				else if (value == null) return defaultValue;
					else throw new ClassCastException("Key '" + key + "' doesn't map to a Integer object");
	}

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
	public long getLong(String key)
	{
		Long l = getLong(key, null);

		if (l != null) return l.longValue();
			else throw new NoSuchElementException("'" + key + "' doesn't map to an existing object");
	}

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
	public long getLong(String key, long defaultValue)
	{
		return getLong(key, new Long(defaultValue)).longValue();
	}

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
	public Long getLong(String key, Long defaultValue)
	{
		Object value = getKey(key);

		if (value instanceof Long) return (Long) value;
			else if (value instanceof String) return new Long( interpolate((String) value) );
				else if (value == null) return defaultValue;
					else throw new ClassCastException("Key '" + key + "' doesn't map to a Long object");
	}

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
	public short getShort(String key)
	{
		Short s = getShort(key, null);

		if (s != null) return s.shortValue();
			else throw new NoSuchElementException("Key '" + key + "' doesn't map to an existing object");
	}

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
	public short getShort(String key, short defaultValue)
	{
		return getShort(key, new Short(defaultValue)).shortValue();
	}

	/**
	 * Get a short associated with the given configuration key.
	 *
	 * @param key          The configuration key.
	 * @param defaultValue The default value.
	 * @return The associated short if key is found and has valid
	 *         format, default value otherwise.
	 * @throws ClassCastException    is thrown if the key maps to an
	 *                               object that is not a Short.
	 * @throws NumberFormatException is thrown if the value mapped
	 *                               by the key has not a valid number format.
	 */
	public Short getShort(String key, Short defaultValue)
	{
		Object value = getKey(key);

		if (value instanceof Short) return (Short) value;
			else if (value instanceof String) return new Short( interpolate((String) value) );
				else if (value == null) return defaultValue;
					else throw new ClassCastException("Key '" + key + "' doesn't map to a Short object");
	}

	public BigDecimal getBigDecimal(String key) throws NoSuchElementException
	{
		BigDecimal number = getBigDecimal(key, null);

		if (number != null) return number;
			else throw new NoSuchElementException("Key '" + key + "' doesn't map to an existing object");
	}

	public BigDecimal getBigDecimal(String key, BigDecimal defaultValue)
	{
		Object value = getKey(key);

		if (value instanceof BigDecimal) return (BigDecimal) value;
			else if (value instanceof String) return new BigDecimal( interpolate((String) value) );
				else if (value == null) return defaultValue;
					else throw new ClassCastException("Key '" + key + "' doesn't map to a BigDecimal object");
	}

	public BigInteger getBigInteger(String key) throws NoSuchElementException
	{
		BigInteger number = getBigInteger(key, null);

		if (number != null) return number;
			else throw new NoSuchElementException("Key '" + key + "' doesn't map to an existing object");
	}

	public BigInteger getBigInteger(String key, BigInteger defaultValue)
	{
		Object value = getKey(key);

		if (value instanceof BigInteger) return (BigInteger) value;
			else if (value instanceof String) return new BigInteger( interpolate((String) value) );
				else if (value == null) return defaultValue;
					else throw new ClassCastException("Key '" + key + "' doesn't map to a getBigInteger object");
	}

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
	public String getString(String key)
	{
		String s = getString(key, null);

		if (s != null) return s;
			else throw new NoSuchElementException("Key '" + key + "' doesn't map to an existing object");
	}

	/**
	 * Get a string associated with the given configuration key.
	 *
	 * @param key          The configuration key.
	 * @param defaultValue The default value.
	 * @return The associated string if key is found, default value otherwise.
	 * @throws ClassCastException is thrown if the key maps to an object that
	 *                            is not a String.
	 */
	public String getString(String key, String defaultValue)
	{
		Object value = getKey(key);

		if (value instanceof String) return interpolate((String) value);
			else if (value == null) return interpolate(defaultValue);
				else throw new ClassCastException("Key '" + key + "' doesn't map to a String object");
	}


	/**
	 * Get a complete string associated with the given configuration key.
	 * Complete means if there is a strings array will return all values concatenated.
	 *
	 * @param key          The configuration key.
	 * @return The associated string if key is found, default value otherwise.
	 * @throws ClassCastException is thrown if the key maps to an object that
	 *                            is not a String.
	 */
	public String getCompleteString(String key)
	{
		String s = getCompleteString(key, null);

		if (s != null) return s;
			else throw new NoSuchElementException("Key '" + key + "' doesn't map to an existing object");
	}

	/**
	 * Get a complete string associated with the given configuration key.
	 * Complete means if there is a strings array will return all values concatenated.
	 *
	 * @param key          The configuration key.
	 * @param defaultValue The default value.
	 * @return The associated string if key is found, default value otherwise.
	 * @throws ClassCastException is thrown if the key maps to an object that
	 *                            is not a String.
	 */
	public String getCompleteString(String key, String defaultValue)
	{
		Object value = getCompleteKey(key);

		if (value instanceof String) return interpolate((String) value);
			else if (value == null) return interpolate(defaultValue);
				else throw new ClassCastException("Key '" + key + "' doesn't map to a String object");
	}

	/**
	 * Get an array of strings associated with the given configuration
	 * key.
	 *
	 * @param key The configuration key.
	 * @return The associated string array if key is found.
	 * @throws ClassCastException is thrown if the key maps to an
	 *                            object that is not a String/List of Strings.
	 */
	public String[] getStringArray(String key)
	{
		ConfigurationEntry entry = getConfigurationEntry(key);
		if (entry == null) return null;

		List list = entry.getValues();

		String tokens[] = new String[list.size()];
		for (int i = 0; i < list.size(); i++)
		{
			tokens[i] = interpolate(list.get(i).toString());
		}

		return tokens;
	}

	/**
	 * Get a vector of strings associated with the given configuration key.
	 *
	 * @param key The configuration key.
	 * @return The associated string array if key is found.
	 */
	public Vector getVector(String key)
	{
		ConfigurationEntry entry = getConfigurationEntry(key);
		if (entry == null) return null;

		Vector vector = new Vector();
		Vector source = (Vector)entry.getValues();

		for(int i = 0; i < source.size(); i++)
		{
			Object value = source.get(i);

			if(value instanceof String) vector.add( interpolate((String)value) );
				else vector.add(value.toString());
		}

		return vector;
	}

	/**
	 * Get a list of strings associated with the given configuration key.
	 *
	 * @param key The configuration key.
	 * @return The associated string array if key is found.
	 */
	public List getList(String key)
	{
		return getVector(key);
	}

	/**
	 * Get an array of strings associated with the given configuration
	 * key.
	 *
	 * @param key The configuration key.
	 * @return The associated string array if key is found.
	 * @throws ClassCastException is thrown if the key maps to an
	 *                            object that is not a String/List of Strings.
	 */
	public String[] getStringArray(String key, String[] defaultValue)
	{
		String value[] = null;

		try
		{
			value = getStringArray(key);
			if (value == null) return defaultValue;
		}
		catch(Exception e)
		{
			value = defaultValue;
		}

		return value;
	}

	/**
	 * Get a vector of strings associated with the given configuration key.
	 *
	 * @param key The configuration key.
	 * @return The associated string array if key is found.
	 */
	public Vector getVector(String key, Vector defaultValue)
	{
		Vector value = null;

		try
		{
			value = getVector(key);
			if (value == null) return defaultValue;
		}
		catch(Exception e)
		{
			value = defaultValue;
		}

		return value;
	}

	/**
	 * Get a list of strings associated with the given configuration key.
	 *
	 * @param key The configuration key.
	 * @return The associated string array if key is found.
	 */
	public List getList(String key, List defaultValue)
	{
		List value = null;

		try
		{
			value = getVector(key);
			if (value == null) return defaultValue;
		}
		catch(Exception e)
		{
			value = defaultValue;
		}

		return value;
	}

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
	public List getComments(String key, int type)
	{
		return getLocator().getAllCommentEntries(key, type);
	}

	/**
	 * Returns a list with all comments (which are configuration entries) that will match the specified key.
	 * If the key is null will return null list. If the key isn't matched will return an empty list. The match
	 * means to find the key anywhere in the body of the comment value.
	 *
	 * @param key string key to identify comments
	 * @return list with all matched configuration entries
	 */
	public List getComments(String key)
	{
		return getComments(key, 2);
	}
}
