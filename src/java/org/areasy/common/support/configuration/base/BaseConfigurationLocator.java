package org.areasy.common.support.configuration.base;

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

import org.areasy.common.data.IteratorUtility;
import org.areasy.common.data.StringUtility;
import org.areasy.common.data.type.map.ListOrderedMap;
import org.areasy.common.data.type.credential.MD5Credential;
import org.areasy.common.support.configuration.ConfigurationEntry;
import org.areasy.common.support.configuration.ConfigurationLocator;
import org.areasy.common.support.configuration.Configuration;
import org.areasy.common.support.configuration.base.predicates.ConfigurationEntryPredicate;
import org.areasy.common.support.configuration.base.predicates.DataConfigurationEntryPredicate;
import org.areasy.common.support.configuration.base.transformers.KeyConfigurationEntryTransformer;

import java.util.*;

/**
 * Base implementation of locator interface.
 *
 * @version $Id: BaseConfigurationLocator.java,v 1.8 2008/05/19 20:05:13 swd\stefan.damian Exp $
 */
public class BaseConfigurationLocator implements ConfigurationLocator
{
	/** Stores the configuration entries */
	private ListOrderedMap storage = null;

	/** Stores the parent configuration locator */
	private ConfigurationLocator parent = null;

	/** Children locators */
	private Map children = null;

	/**
	 * Default constructor for abstract locator
	 */
	public BaseConfigurationLocator()
	{
		this.storage = new ListOrderedMap();
	}

	/**
	 * Default constructor for abstract locator, defining a parent locator.
	 * @param parent build this located based on the parent locator.
	 */
	public BaseConfigurationLocator(ConfigurationLocator parent)
	{
		this.parent = parent;
		this.storage = new ListOrderedMap();
	}

	/**
	 * Get configuration locator identify. This implementation will return null values.
	 */
	public Object getIdentity()
	{
		return null;
	}

	/**
	 * Returns the current configuration locator source.
	 *
	 * @return the base path
	 */
	public Object getSource()
	{
		return null;
	}

	/**
	 * Get parent locator for the actual locator. If is root will return null value.
	 *
	 * @return parent configuration locator.
	 */
	public ConfigurationLocator getParent()
	{
		return this.parent;
	}

	/**
	 * Get root locator for the actual locator. If is root will return null value.
	 *
	 * @return root configuration locator.
	 */
	public ConfigurationLocator getRoot()
	{
		if(isRoot()) return this;

		return getParent().getRoot();
	}

	/**
	 * Check if the actual locator has a parent locator.
	 *
	 * @return true if the current locator has a parent (is not root)
	 */
	public boolean hasParent()
	{
		return (this.parent != null);
	}

	/**
	 * Check if actual locator is root.
	 *
	 * @return true is the current locator is root
	 */
	public boolean isRoot()
	{
		return (this.parent == null);
	}

	/**
	 * Append a configuration entry in the current locator. If node already exists the current value will be appended.
	 *
	 * @param entry configuration entry to be added
	 */
	public void addNode(ConfigurationEntry entry)
	{
		if(entry == null) return;

		if(entry.isData())
		{
			if(containsKey(entry.getKey()))
			{
				//append value to the old entity.
				ConfigurationEntry oldentry = getEntry(entry.getKey());
				oldentry.addValue(entry.getValues());
			}
			else
			{
				//store actual entry.
				this.storage.put(entry.getKey(), entry);

				//put locator signature in the configuration entry only is not exist.
				if(entry.getLocator() == null) entry.setLocator(this);

				//include this entry in root locator
				if(!isRoot())
				{
					if(getRoot().containsKey(entry.getKey()))
					{
						//append value to the old entity.
						ConfigurationEntry rootentry = getRoot().getEntry(entry.getKey());
						rootentry.addValue(entry);
					}
					else getRoot().addNode(entry);
				}
			}
		}
		else
		{
			//put locator signature in the configuration entry only is not exist.
			if(entry.getLocator() == null) entry.setLocator(this);

			//store comment
			this.storage.put(ConfigurationEntry.PROPERTY_COMMENT + MD5Credential.digest(entry.getComment() + this.storage.size()), entry);
		}
	}

	/**
	 * Append a configuration entry in the current locator. If node already exists the current value will be appended.
	 *
	 * @param index node index location
	 * @param entry configuration entry to be added
	 */
	public void addNode(int index, ConfigurationEntry entry)
	{
		if(entry == null) return;

		if(entry.isData())
		{
			if(containsKey(entry.getKey()))
			{
				//append value to the old entity.
				ConfigurationEntry oldentry = getEntry(entry.getKey());
				oldentry.addValue(entry.getValues());
			}
			else
			{
				//store actual entry.
				this.storage.put(index, entry.getKey(), entry);

				//put locator signature in the configuration entry only is not exist.
				if(entry.getLocator() == null) entry.setLocator(this);

				//include this entry in root locator
				if(!isRoot())
				{
					if(getRoot().containsKey(entry.getKey()))
					{
						//append value to the old entity.
						ConfigurationEntry rootentry = getRoot().getEntry(entry.getKey());
						rootentry.addValue(entry);
					}
					else getRoot().addNode(index, entry);
				}
			}
		}
		else
		{
			//put locator signature in the configuration entry only is not exist.
			if(entry.getLocator() == null) entry.setLocator(this);

			//store comment
			this.storage.put(index, ConfigurationEntry.PROPERTY_COMMENT + MD5Credential.digest(entry.getComment() + this.storage.size()), entry);
		}
	}

	/**
	 * Gets the index of the specified key.
	 *
	 * @param entry the configuration entry to find the index of
	 * @return the index, or -1 if not found
	 */
	public int indexOf(ConfigurationEntry entry)
	{
		if(entry == null) return -1;

		if(entry.isData()) return this.storage.indexOf(entry.getKey());
			else return -1;
	}

	/**
	 * Gets the index of the specified key.
	 *
	 * @param key the key to find the index of
	 * @return the index, or -1 if not found
	 */
	public int indexOf(Object key)
	{
		if(key == null) return -1;

		return this.storage.indexOf(key);
	}

	/**
	 * Set a configuration entry in the current locator. If node already exists will be replaced, if not will be appended.
	 * Is locator object is not found in the specified entry will be registered the current locator.
	 *
	 * @param entry configuration entry to be updated
	 */
	public void setNode(ConfigurationEntry entry)
	{
		if(entry == null) return;

		//put locator signature in the configuration entry only is not exist.
		if(entry.getLocator() == null) entry.setLocator(this);

		if(entry.isData()) this.storage.put(entry.getKey(), entry);
			else this.storage.put(ConfigurationEntry.PROPERTY_COMMENT + MD5Credential.digest(entry.getComment() + this.storage.size()), entry);

		if(!isRoot() && entry.isData())
		{
			if(getRoot().containsKey(entry.getKey()))
			{
				//append value to the old entity.
				ConfigurationEntry rootentry = getRoot().getEntry(entry.getKey());

				int index = rootentry.getIndex(entry);
				rootentry.setValue(entry, index);
			}
			else getRoot().setNode(entry);
		}
	}

	/**
	 * Remove a configuration entry from the current locator.
	 *
	 * @param entry configuration entry to be removed
	 */
	public void removeNode(ConfigurationEntry entry)
	{
		Object object = entry.getKey();

		if(containsKey(object))
		{
			if(!isRoot() && getRoot().containsKey(object) && entry.isData())
			{
				//append value to the old entity.
				ConfigurationEntry rootentry = getRoot().getEntry(object);

				if(rootentry != null && entry.getValues().size() == rootentry.getValues().size())
				{
					//revoke this method.
					getRoot().removeNode(entry);
				}
				else if(rootentry != null)
				{
					for(int i = 0; entry.getValues() != null && i < entry.getValues().size(); i++)
					{
						Object value = entry.getValues().get(i);
						if(rootentry.getValues() != null) rootentry.getValues().remove(value);
					}
				}
			}

			this.storage.remove(object);
		}
	}

	/**
	 * Remove an object from the current locator.
	 *
	 * @param index index order from this location to remove the corresponding configuration entry
	 */
	public void removeNode(int index)
	{
		ConfigurationEntry entry = (ConfigurationEntry) this.storage.getValue(index);

		removeNode(entry);
	}

	/**
	 * Get all configuration entries from all locators.
	 *
	 * @return an <code>Iterator</code> structure with all entries
	 */
	public Iterator getAllEntries()
	{
		return this.storage.values().iterator();
	}

	/**
	 * Get all configuration entries from the current locator.
	 * If the current locator is root will be returned only own entries.
	 *
	 * @return an <code>Iterator</code> structure with all entries from the current locator
	 */
	public Iterator getLocatorEntries()
	{
		return IteratorUtility.filteredIterator(this.storage.values().iterator(), new ConfigurationEntryPredicate(this));
	}

	/**
	 * Get all configuration entries from the specified locator.
	 *
	 * @param locator locator structure to be interrogated
	 * @return an <code>Iterator</code> structure with all entries from the specified locator
	 */
	public Iterator getLocatorEntries(ConfigurationLocator locator)
	{
		return IteratorUtility.filteredIterator(this.storage.values().iterator(), new ConfigurationEntryPredicate(locator));
	}

	/**
	 * Get all data configuration entries from all locators.
	 *
	 * @return an <code>Iterator</code> structure with all data entries (whithout comments)
	 */
	public Iterator getAllDataEntries()
	{
		return IteratorUtility.filteredIterator(this.storage.values().iterator(), new DataConfigurationEntryPredicate(null));
	}

	/**
	 * Get all data configuration entries from the current locator.
	 * If the current locator is root will be returned only own data entries.
	 *
	 * @return an <code>Iterator</code> structure with all data entries (whithout comments) from the current locator
	 */
	public Iterator getCurrentDataEntries()
	{
		return IteratorUtility.filteredIterator(this.storage.values().iterator(), new DataConfigurationEntryPredicate(this));
	}

	/**
	 * Get all data configuration keys from all locators (without comments).
	 *
	 * @return an <code>Iterator</code> structure with all data keys
	 */
	public Iterator getAllDataKeys()
	{
		Iterator iterator = getAllDataEntries();
		return IteratorUtility.transformedIterator(iterator, new KeyConfigurationEntryTransformer());
	}

	/**
	 * Get all data configuration keys from the current locator (without comments).
	 * This the current locator is root will be returned only own keys for registred data entries.
	 *
	 * @return an <code>Iterator</code> structure with all data keys from the current locator
	 */
	public Iterator getCurrentDataKeys()
	{
		Iterator iterator = getCurrentDataEntries();
		return IteratorUtility.transformedIterator(iterator, new KeyConfigurationEntryTransformer());
	}

	/**
	 * Found and return a configuration key using a specified key.
	 *
	 * @param key configuration entry key name to be used to found an entry structure.
	 * @return the configuration entry corresponding with the specified key.
	 */
	public ConfigurationEntry getEntry(Object key)
	{
		if(key == null) return null;

		Object object = this.storage.get(key);

		if(object != null) return (ConfigurationEntry)object;
			else return null;
	}

	/**
	 * Check if in the current locator registered the specified key.
	 *
	 * @param key configuration entry key name to be used to found an entry structure.
	 * @return true if configuration entry structure with the specified key already exist.
	 */
	public boolean containsKey(Object key)
	{
		if(key == null) return false;

		return this.storage.containsKey(key);
	}

	/**
	 * Check if in the current locator registered the specified configuration entry.
	 *
	 * @param entry configuration entry to be found
	 * @return true if the specified key (defined by the specified configuration entry) exist
	 */
	public boolean containsKey(ConfigurationEntry entry)
	{
		if(entry == null) return false;

		return this.storage.containsValue(entry);
	}

	/**
	 * Check if in the current locator registered the specified configuration value.
	 *
	 * @param value configuration entry key name to be used to found an entry structure.
	 * @return true if the specified value exists
	 */
	public boolean containsValue(Object value)
	{
		if(value == null) return false;

		boolean exist = true;
		Iterator entries = getCurrentDataEntries();

		while(entries != null && entries.hasNext() && !exist)
		{
			ConfigurationEntry entry = (ConfigurationEntry) entries.next();
			exist = entry.getValues().contains(value);
		}

		return exist;
	}

	/**
	 * Check if in the current locator registered the specified configuration entry.
	 *
	 * @param entry configuration entry to be found
	 * @return true if the specified value(s) (defined by the specified configuration entry) exist
	 */
	public boolean containsValue(ConfigurationEntry entry)
	{
		if(entry == null || !entry.isData()) return false;

		boolean exist = true;
		ConfigurationEntry entity = getEntry(entry.getKey());

		if(entity == null) return false;

		for(int i = 0; exist && i < entry.getValues().size(); i++)
		{
			Object value = entry.getValues().get(i);
			exist = entity.getValues().contains(value);
		}

		return exist;
	}

	/**
	 * Check if in the current locator is registered the specified configuration data key and
	 * if registered entry contains specified value..
	 *
	 * @param key configuration key to be found
	 * @param value configuration value to be match
	 * @return true if the specified key and value exist
	 */
	public boolean hasValue(Object key, Object value)
	{
		ConfigurationEntry entry = getEntry(key);

		return entry != null && entry.getValues().contains(value);
	}

	/**
	 * Check if the current locator contains data configuration entries.
	 *
	 * @return true if the current loactor doesn't have entries.
	 */
	public boolean isEmpty()
	{
		return !getCurrentDataEntries().hasNext();
	}

	/**
	 * Get child configuration locator using a specific identifier.
	 * If identifier is null will return the current  locator.
	 *
	 * @param identity configuration identity to discover the child locator
	 * @return the child locator.
	 */
	public ConfigurationLocator getChild(Object identity)
	{
		if(identity == null) return this;
		else
		{
			if(this.children != null && this.children.containsKey(identity))
			{
				return (ConfigurationLocator) this.children.get(identity);
			}
			else if(this.children != null)
			{
				Iterator childs = getChildren().iterator();

				while(childs != null && childs.hasNext())
				{
					ConfigurationLocator child = (ConfigurationLocator) childs.next();
					ConfigurationLocator result = child.getChild(identity);

					if(result != null) return result;
				}

				return null;
			}
			else return null;
		}
	}

	/**
	 * Create configuration structure the current locator.
	 *
	 * @return configuration structure which encapsulate this locator.
	 */
	public Configuration getConfiguration()
	{
		return new BaseConfiguration(this);
	}

	/**
	 * Get all configuration locators from the current locator.
	 *
	 * @return a <code>Collection</code> structure with locator childs
	 */
	public Collection getChildren()
	{
		return this.children != null ? this.children.values() : null;
	}

	/**
	 * Add a new child locator in the current locator..
	 *
	 * @param locator configuration locator to be appended like a child locator
	 */
	public void addChild(ConfigurationLocator locator)
	{
		if(this.children == null) this.children = new Hashtable();

		if(getIdentity() == null) this.children.put(String.valueOf(locator.hashCode()), locator);
			else this.children.put(locator.getIdentity(), locator);
	}

	/**
	 * Update entries for the current locator using all entries from the specified locator.
	 * This method will clean all existent entries and will add found entries in parameter locator.
	 *
	 * @param locator configuration locator structure.
	 */
	public void update(ConfigurationLocator locator)
	{
		if(this.storage != null) this.storage.clear();

		Iterator iterator = locator.getLocatorEntries();
		while(iterator != null && iterator.hasNext())
		{
			ConfigurationEntry entry = (ConfigurationEntry) iterator.next();
			addNode(entry);
		}
	}

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
	public List getAllCommentEntries(String key, int type)
	{
		return findCommentEntries(getAllEntries(), key, type);
	}

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
	public List getLocatorCommentEntries(String key, int type)
	{
		return findCommentEntries(getLocatorEntries(), key, type);
	}

	/**
	 * Returns a list with all comments (which are configuration entries) that will match the specified key.
	 * If the key is null will return null list. If the key isn't matched will return an empty list.
	 *
	 * @param iterator an entity containing <code>ConfigurationEntry</code> structure which should be interrogated.
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
	private List findCommentEntries(Iterator iterator, String key, int type)
	{
		if(key == null) return null;
		List list = new Vector();

		while(iterator != null && iterator.hasNext())
		{
			ConfigurationEntry entry = (ConfigurationEntry)iterator.next();
			if(entry != null && entry.isComment())
			{
				String comment = entry.getComment();

				switch(type)
				{
					case 0:
						if(StringUtility.equals(comment, key)) list.add(entry);
						break;
					case 1:
						if(comment != null && comment.startsWith(key)) list.add(entry);
						break;
					case 2:
						if(StringUtility.contains(comment, key)) list.add(entry);
						break;
					case 3:
						if(comment != null && comment.endsWith(key)) list.add(entry);
						break;
					default:
						break;
				}
			}
		}

		return list;
	}
}


    
