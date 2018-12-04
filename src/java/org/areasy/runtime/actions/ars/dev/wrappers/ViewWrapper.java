package org.areasy.runtime.actions.ars.dev.wrappers;

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

import com.bmc.arsys.api.*;
import org.areasy.common.data.StringUtility;
import org.areasy.runtime.actions.ars.dev.DefinitionAction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * View definition object implementation.
 */
public class ViewWrapper extends AbstractWrapper implements FormRelatedWrapper
{
	private String formName = null;

	/**
	 * Dedicated method to instantiate a definition object
	 *
	 * @param action runtime action which are calling this object
	 */
	public ViewWrapper(DefinitionAction action)
	{
		init(action);
	}

	/**
	 * Get an array with the keys for all found objects
	 *
	 * @param since changed since
	 * @return object's keys list
	 */
	public List<String> find(Date since)
	{
		if(getFormName() != null)
		{
			List<View> views = findObjectsByForm(since);
			List<String> data = new ArrayList<String>();

			for(int i = 0; views != null && i < views.size(); i++)
			{
				View view = views.get(i);
				data.add(view.getName());
			}

			return data;
		}
		else return null;
	}

	/**
	 * Get an array with the key for all found objects
	 *
	 * @return object's structure arrays
	 */
	public List<View> findObjectsByForm()
	{
		return findObjectsByForm(null);
	}

	/**
	 * Get an array with the key for all found objects
	 *
	 * @return object's structure arrays
	 */
	public List<View> findObjectsByForm(Date since)
	{
		try
		{
			ViewCriteria criteria = new ViewCriteria();
			criteria.setRetrieveAll(true);

			return getServerConnection().getContext().getListViewObjects(getFormName(), since != null ? new Timestamp(since).getValue() : 0, criteria);
		}
		catch(ARException are)
		{
			return null;
		}
	}

	/**
	 * Get the object instance. Here the nam eis actually the view Id
	 *
	 * @param name object name
	 * @return object instance
	 * @throws com.bmc.arsys.api.ARException if core exception will occur
	 */
	public ObjectBase getInstance(String name) throws ARException
	{
		ObjectBase object = null;

		if(getFormName() != null)
		{
			ViewCriteria criteria = new ViewCriteria();
			criteria.setRetrieveAll(true);

			List views = getServerConnection().getContext().getListViewObjects(getFormName(), 0, criteria);

			for(int i = 0; i < views.size(); i++)
			{
				View view = (View) views.get(i);

				if( StringUtility.equals(name, view.getName()))
				{
					object = view;
					break;
				}
			}
		}

		return object;
	}

	/**
	 * Get a new object instance and it will be initialized the specified name
	 *
	 * @param name object name
	 * @return object instance
	 */
	public ObjectBase newInstance(String name)
	{
		ObjectBase object = new View(getFormName(), 0);

		object.setName(name);
		((View)object).setFormName(getFormName());

		return object;
	}

	/**
	* Get an object instance based on primary coordinates.
	*
	* @param form related form name
	* @param name object name
	* @return an object instances
	* @throws com.bmc.arsys.api.ARException if core exception will occur
	*/
	public ObjectBase getInstance(String form, String name) throws ARException
	{
		ObjectBase data = null;

		//set related form name
		setFormName(form);

		if(name != null)
		{
			ViewCriteria criteria = new ViewCriteria();
			criteria.setRetrieveAll(true);

			List objects = getServerConnection().getContext().getListViewObjects(getFormName(), 0, criteria);

			for(int i = 0; i < objects.size(); i++)
			{
				View view = (View) objects.get(i);

				if( StringUtility.equals(view.getName(), name) )
				{
					data = view;
					break;
				}
			}
		}

		return data;
	}

	/**
	* Get an array of object instances.
	*
	* @param form related form name
	* @param names array of object names
	* @return an array of object instances
	* @throws com.bmc.arsys.api.ARException if core exception will occur
	*/
	public ObjectBase[] getInstance(String form, String names[]) throws ARException
	{
		ObjectBase[] data = null;

		//set related form name
		setFormName(form);

		if(names != null && names.length > 0)
		{
			data = new ObjectBase[names.length];

			ViewCriteria criteria = new ViewCriteria();
			criteria.setRetrieveAll(true);

			int index = 0;
			List views = Arrays.asList(names);
			List objects = getServerConnection().getContext().getListViewObjects(getFormName(), 0, criteria);

			for(int i = 0; i < objects.size(); i++)
			{
				View view = (View) objects.get(i);

				if( views.contains(view.getName()) )
				{
					data[index] = view;
					index++;
				}

				if(index >= data.length) break;
			}

		}

		return data;
	}

	/**
	 * Check if an object instance exists with the specified name
	 *
	 * @param form related form name
	 * @param name object name
	 * @return true if the object exists
	 */
	public boolean exists(String form, String name)
	{
		setFormName(form);

		return exists(name);
	}

	/**
	 * Get the object type id
	 *
	 * @return object type id
	 */
	public int getObjectTypeId()
	{
		return StructItemInfo.VUI;
	}

	public String getFormName()
	{
		return formName;
	}

	public void setFormName(String formName)
	{
		this.formName = formName;
	}

	/**
	 * Get the wrapper name (signature code)
	 *
	 * @return wrapper signature code
	 */
	public String getPluralObjectTypeName()
	{
		return DefinitionAction.TYPE_VIEWS;
	}
}