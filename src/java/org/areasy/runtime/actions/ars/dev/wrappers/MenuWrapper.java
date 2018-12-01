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
import org.areasy.runtime.actions.ars.dev.DefinitionAction;

import java.util.Date;
import java.util.List;

/**
 * Menu definition object implementation.
 *
 */
public class MenuWrapper extends AbstractWrapper implements ObjectWrapper
{
	private int menuType = Constants.AR_CHAR_MENU_LIST;

	/**
	 * Dedicated method to instantiate a definition object
	 *
	 * @param action runtime action which are calling this object
	 */
	public MenuWrapper(DefinitionAction action)
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
		try
		{
			return getServerConnection().getContext().getListMenu(since != null ? new Timestamp(since).getValue() : 0, null, null);
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
		return getServerConnection().getContext().getMenu(name, new MenuCriteria());
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

		if(getMenuType() == Constants.AR_CHAR_MENU_FILE ) object = new FileMenu();
		else if(getMenuType() == Constants.AR_CHAR_MENU_LIST ) object = new ListMenu();
		else if(getMenuType() == Constants.AR_CHAR_MENU_QUERY ) object = new QueryMenu(null, null, null, 0, 0, false, null, null);
		else if(getMenuType() == Constants.AR_CHAR_MENU_SQL ) object = new SqlMenu(null, null, 0,0);
		else if(getMenuType() == Constants.AR_CHAR_MENU_DD_FIELD  ) object = new FieldDataDictionaryMenu();
		else if(getMenuType() == Constants.AR_CHAR_MENU_DD_FORM  ) object = new FormDataDictionaryMenu();

		if(object != null) object.setName(name);

		return object;
	}

	/**
	 * Get the object type id
	 *
	 * @return object type id
	 */
	public int getObjectTypeId()
	{
		return StructItemInfo.CHAR_MENU;
	}

	public int getMenuType()
	{
		return menuType;
	}

	public void setMenuType(int menuType)
	{
		this.menuType = menuType;
	}

	/**
	 * Get the wrapper name (signature code)
	 *
	 * @return wrapper signature code
	 */
	public String getPluralObjectTypeName()
	{
		return DefinitionAction.TYPE_MENUS;
	}
}
