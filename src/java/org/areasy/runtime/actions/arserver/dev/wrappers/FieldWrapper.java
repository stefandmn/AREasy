package org.areasy.runtime.actions.arserver.dev.wrappers;

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

import com.bmc.arsys.api.*;
import org.areasy.runtime.actions.arserver.dev.DefinitionAction;
import org.areasy.common.data.StringUtility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Field definition object implementation.
 */
public class FieldWrapper extends AbstractWrapper implements FormRelatedWrapper
{
	private String formName = null;

	private int dataType = Constants.AR_DATA_TYPE_CHAR;

	/**
	 * Dedicated method to instantiate a definition object
	 *
	 * @param action runtime action which are calling this object
	 */
	public FieldWrapper(DefinitionAction action)
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
			List<Field> fields = findObjectsByForm(since);
			List<String> data = new ArrayList<String>();

			for(int i = 0; fields != null && i < fields.size(); i++)
			{
				Field field = fields.get(i);
				data.add(field.getName());
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
	public List<Field> findObjectsByForm()
	{
		return findObjectsByForm(null);
	}

	/**
	 * Get an array with the key for all found objects
	 *
	 * @param since changed since
	 * @return object's structure arrays
	 */
	public List<Field> findObjectsByForm(Date since)
	{
		try
		{
			FieldCriteria criteria = new FieldCriteria();
			criteria.setRetrieveAll(true);

			return getServerConnection().getContext().getListFieldObjects(getFormName(), Constants.AR_FIELD_TYPE_ALL, since != null ? new Timestamp(since).getValue() : 0,  criteria);
		}
		catch(ARException are)
		{
			return null;
		}
	}

	/**
	 * Get the object name instance. In case of the instance is not defined the workflow
	 * will return an exception.
	 *
	 * @param name object name
	 * @return object instance
	 * @throws ARException if object is not returned or any other exception
	 */
	public ObjectBase getInstance(String name) throws ARException
	{
		ObjectBase object = null;

		if(getFormName() != null)
		{
			List fields = getServerConnection().getContext().getListFieldObjects(getFormName());

			for(int i = 0; i < fields.size(); i++)
			{
				Field field = (Field) fields.get(i);

				if( StringUtility.equals(name, field.getName()))
				{
					object = field;
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
		ObjectBase object = null;

		if(getDataType() == Constants.AR_DATA_TYPE_CHAR) object = new CharacterField();
		else if(getDataType() == Constants.AR_DATA_TYPE_INTEGER) object = new IntegerField();
		else if(getDataType() == Constants.AR_DATA_TYPE_DIARY) object = new DiaryField();
		else if(getDataType() == Constants.AR_DATA_TYPE_DECIMAL) object = new DecimalField();
		else if(getDataType() == Constants.AR_DATA_TYPE_DATE) object = new DateOnlyField();
		else if(getDataType() == Constants.AR_DATA_TYPE_ATTACH) object = new AttachmentField();
		else if(getDataType() == Constants.AR_DATA_TYPE_ATTACH_POOL) object = new AttachmentPoolField();
		else if(getDataType() == Constants.AR_DATA_TYPE_COLUMN) object = new ColumnField();
		else if(getDataType() == Constants.AR_DATA_TYPE_CONTROL) object = new ColumnField();
		else if(getDataType() == Constants.AR_DATA_TYPE_CURRENCY) object = new CurrencyField();
		else if(getDataType() == Constants.AR_DATA_TYPE_TIME) object = new DateTimeField();
		else if(getDataType() == Constants.AR_DATA_TYPE_TIME_OF_DAY) object = new TimeOnlyField();
		else if(getDataType() == Constants.AR_DATA_TYPE_DISPLAY) object = new DisplayField();
		else if(getDataType() == Constants.AR_DATA_TYPE_PAGE) object = new PageField();
		else if(getDataType() == Constants.AR_DATA_TYPE_PAGE_HOLDER) object = new PageHolderField();
		else if(getDataType() == Constants.AR_DATA_TYPE_REAL) object = new RealField();
		else if(getDataType() == Constants.AR_DATA_TYPE_ENUM) object = new SelectionField();
		else if(getDataType() == Constants.AR_DATA_TYPE_TRIM) object = new TrimField();
		else if(getDataType() == Constants.AR_DATA_TYPE_VIEW) object = new ViewField();
		else if(getDataType() == Constants.AR_DATA_TYPE_TABLE) object = new TableField();

		if(object != null)
		{
			object.setName(name);
			((Field)object).setForm(getFormName());
		}

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
		//set related form name
		setFormName(form);

		return getInstance(name);
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

			int index = 0;
			List fields = Arrays.asList(names);
			List objects = getServerConnection().getContext().getListFieldObjects(getFormName());

			for(int i = 0; i < objects.size(); i++)
			{
				Field field = (Field) objects.get(i);

				if( fields.contains(field.getName()) )
				{
					data[index] = field;
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
		return StructItemInfo.FIELD;
	}

	public String getFormName()
	{
		return formName;
	}

	public void setFormName(String formName)
	{
		this.formName = formName;
	}

	public int getDataType()
	{
		return dataType;
	}

	public void setDataType(int dataType)
	{
		this.dataType = dataType;
	}

	/**
	 * Get the wrapper name (signature code)
	 *
	 * @return wrapper signature code
	 */
	public String getPluralObjectTypeName()
	{
		return DefinitionAction.TYPE_FILTERS;
	}
}