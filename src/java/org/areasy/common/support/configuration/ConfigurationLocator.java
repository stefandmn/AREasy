package org.areasy.common.support.configuration;

/*
 * Copyright (c) 2007-2015 AREasy Runtime
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

import java.util.Iterator;
import java.util.Collection;
import java.util.List;

/**
 * Dedicated interface to define a configuration locator structure.
 * Configuration locator must store all entries found in locator object and all included sub-locators.
 * This interface expose all necessary methods to manage a general configuration entries store.
 * <p>
 * This structure is designed to keep all data configuration entries in the root locators and detailed
 * entries information in each sub-locator.
 *
 * @version $Id: ConfigurationLocator.java,v 1.4 2008/05/14 09:32:41 swd\stefan.damian Exp $
 */
public interface ConfigurationLocator
{
	/**
	 * Get parent locator for the actual locator. If is root will return null value.
	 *
	 * @return parent configuration locator.
	 */
	ConfigurationLocator getParent();

	/**
	 * Get root locator for the actual locator. If is root will return null value.
	 *
	 * @return root configuration locator.
	 */
	ConfigurationLocator getRoot();

	/**
	 * Get configuration locator identify. This method must be implemented by each
	 * locator providers.
	 *
	 * @return get locator identify (unique key in the locator structure)
	 */
	Object getIdentity();

	/**
	 * Check if the actual locator has a parent locator.
	 *
	 * @return true if the current locator has a parent (is not root)
	 */
	boolean hasParent();

	/**
	 * Check if actual locator is root.
	 *
	 * @return true is the current locator is root
	 */
	boolean isRoot();

	/**
	 * Returns the current configuration locator source.
	 *
	 * @return the base path
	 */
	Object getSource();

	/**
	 * Found and return a configuration key using a specified key.
	 *
	 * @param key configuration entry key name to be used to found an entry structure.
	 * @return the configuration entry corresponding with the specified key.
	 */
	ConfigurationEntry getEntry(Object key);

	/**
	 * Check if in the current locator registered the specified key.
	 *
	 * @param key configuration entry key name to be used to found an entry structure.
	 * @return true if configuration entry structure with the specified key already exist.
	 */
	boolean containsKey(Object key);

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
	 * Check if in the current locator registered the specified configuration entry.
	 *
	 * @param entry configuration entry to be found
	 * @return true if the specified value(s) (defined by the specified configuration entry) exist
	 */
	boolean containsValue(ConfigurationEntry entry);

	/**
	 * Check if in the current locator registered the specified configuration data key and
	 * if registered entry contains specified value..
	 *
	 * @param key configuration key to be found
	 * @param value configuration value to be match
	 * @return true if the specified key and value exist
	 */
	boolean hasValue(Object key, Object value);

	/**
	 * Append a configuration entry in the current locator.
	 * If node already exists the current value will be appended.
	 *
	 * @param entry configuration entry to be added
	 */
	public void addNode(ConfigurationEntry entry);

	/**
	 * Add a configuration entry in the current locator to a specific location.
	 * If node already exists the current value will be appended.
	 *
	 * @param index node index location
	 * @param entry configuration entry to be added
	 */
	public void addNode(int index, ConfigurationEntry entry);

	/**
	 * Gets the index of the specified key.
	 *
	 * @param key the key to find the index of
	 * @return the index, or -1 if not found
	 */
	public int indexOf(Object key);

	/**
	 * Gets the index of the specified key.
	 *
	 * @param entry the configuration entry to find the index of
	 * @return the index, or -1 if not found
	 */
	public int indexOf(ConfigurationEntry entry);

	/**
	 * Set a configuration entry in the current locator. If node already exists will be replaced, if not will be appended.
	 * Is locator object is not found in the specified entry will be registered the current locator.
	 *
	 * @param entry configuration entry to be updated
	 */
	void setNode(ConfigurationEntry entry);

	/**
	 * Remove a configuration entry from the current locator.
	 *
	 * @param entry configuration entry to be removed
	 */
	void removeNode(ConfigurationEntry entry);

	/**
	 * Remove an object from the current locator.
	 *
	 * @param index index order from this location to remove the corresponding configuration entry
	 */
	void removeNode(int index);

	/**
	 * Get all configuration entries from all locators.
	 *
	 * @return an <code>Iterator</code> structure with all entries
	 */
	Iterator getAllEntries();

	/**
	 * Get all configuration entries from the current locator.
	 * If the current locator is root will be returned only own entries.
	 *
	 * @return an <code>Iterator</code> structure with all entries from the current locator
	 */
	Iterator getLocatorEntries();

	/**
	 * Get all configuration entries from the specified locator.
	 *
	 * @param locator locator structure to be interrogated
	 * @return an <code>Iterator</code> structure with all entries from the specified locator
	 */
	Iterator getLocatorEntries(ConfigurationLocator locator);

	/**
	 * Get all data configuration entries from all locators.
	 *
	 * @return an <code>Iterator</code> structure with all data entries (whithout comments)
	 */
	Iterator getAllDataEntries();

	/**
	 * Get all data configuration entries from the current locator.
	 * If the current locator is root will be returned only own data entries.
	 *
	 * @return an <code>Iterator</code> structure with all data entries (whithout comments) from the current locator
	 */
	Iterator getCurrentDataEntries();

	/**
	 * Get all data configuration keys from all locators (without comments).
	 *
	 * @return an <code>Iterator</code> structure with all data keys
	 */
	Iterator getAllDataKeys();

	/**
	 * Get all data configuration keys from the current locator (without comments).
	 * This the current locator is root will be returned only own keys for registred data entries.
	 *
	 * @return an <code>Iterator</code> structure with all data keys from the current locator
	 */
	Iterator getCurrentDataKeys();

	/**
	 * Add a new child locator in the current locator.
	 *
	  * @param locator configuration locator to be appended like a child locator
	 */
	void addChild(ConfigurationLocator locator);

	/**
	 * Update entries for the current locator using all entries from the specified locator.
	 * This method will clean all existent entries and will add found entries in parameter locator.
	 *
	 * @param locator configuration locator structure.
	 */
	void update(ConfigurationLocator locator);

	/**
	 * Get child configuration locator using a specific identifier.
	 * If identifier is null will return the current  locator.
	 *
	 * @param identity configuration identity to discover the child locator
	 * @return the child locator.
	 */
	ConfigurationLocator getChild(Object identity);

	/**
	 * Create configuration structure the current locator.
	 * @return configuration structure which encapsulate this locator.
	 */
	Configuration getConfiguration();

	/**
	 * Get all configuration locators from the current locator.
	 *
	 * @return a <code>Collection</code> structure with locator childs
	 */
	Collection getChildren();

	/**
	 * Check if the current locator contains configuration entry structures.
	 *
	 * @return true if the current loactor doesn't have entries.
	 */
	boolean isEmpty();

	/**
	 * Returns a list with all comments (which are configuration entries) that will match the specified key.
	 * This method will search in all locators (including parent or children locators).
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
	List getAllCommentEntries(String key, int type);

	/**
	 * Returns a list with all comments (which are configuration entries) that will match the specified key.
	 * This method will search only in the current locator (wihtout to look in parent or children locators).
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
	List getLocatorCommentEntries(String key, int type);
}
