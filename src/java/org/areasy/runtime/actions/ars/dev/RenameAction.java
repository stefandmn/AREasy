package org.areasy.runtime.actions.ars.dev;

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

import com.bmc.arsys.api.*;
import org.areasy.common.data.StringUtility;
import org.areasy.runtime.RuntimeAction;
import org.areasy.runtime.actions.ars.dev.wrappers.FormRelatedWrapper;
import org.areasy.runtime.actions.ars.dev.wrappers.ObjectWrapper;
import org.areasy.runtime.engine.RuntimeLogger;
import org.areasy.runtime.engine.base.AREasyException;

import java.util.List;

/**
 * Abstract runtime action to rename one definition object.
 *
 */
public class RenameAction extends DefinitionAction implements RuntimeAction
{
	/**
	 * Rename one definition object from an AR System server.
	 * This action could contain the following parametrization:
	 * <table border="1">
	 * <tr>
	 * <td><b>-oldname</b></td>
	 * <td>Specify the actual object name.</td>
	 * </tr>
	 * <tr>
	 * <td><b>-newname</b></td>
	 * <td>Specify the new object name.</td>
	 * </tr>
	 * <td><b>-type</b></td>
	 * <td>Specify the object type. All possible values could be: <code>form|menu|activelink|filter|escalation|container|activelinkguide|filterguide|webservice|field|view</code></td>
	 * </tr>
	 * <td><b>-relatedform</b></td>
	 * <td>For type <code>view</code> and <code>field</code> it is mandatory to declare the related form name.</td>
	 * </tr>
	 * </table>
	 * <p/>
	 *
	 * @throws org.areasy.runtime.engine.base.AREasyException if any error will occur
	 */
	public void run() throws AREasyException
	{
		ObjectWrapper wrapper = null;

		String oldname = getConfiguration().getString("oldname", getConfiguration().getString("name", null));
		String newname = getConfiguration().getString("newname", oldname);
		String type = getConfiguration().getString("type", null);

		if (StringUtility.isNotEmpty(oldname) && StringUtility.isNotEmpty(newname) && type != null)
		{
			type = toPlural(type);
			wrapper = getObjectWrapper(type);

			if(wrapper != null)
			{
				if(wrapper instanceof FormRelatedWrapper)
				{
					String relatedForm = getConfiguration().getString("relatedform", null);
					if(relatedForm != null) ((FormRelatedWrapper) wrapper).setFormName(relatedForm);
				}

				try
				{
					ObjectBase objdata = wrapper.getInstance(oldname);
					if(objdata == null) RuntimeLogger.error("No object found with this name: " + oldname);
					else
					{
						objdata.setNewName(newname);
						commit(objdata);
					}
				}
				catch (Throwable th)
				{
					if(!getConfiguration().getBoolean("force", false))
					{
						RuntimeLogger.error("Error renaming definition object: " + th.getMessage());
						getLogger().debug("Exception", th);
					}
					else RuntimeLogger.warn("Error renaming definition object: " + th.getMessage());
				}
			}
			else RuntimeLogger.error("No object type has been specified");
		}
		else super.run();
	}

	/**
	 * Execute general action using bulk data-structures. This method will execute the corresponding method from the subclass.
	 *
	 * @param objects a list with all found objects
	 */
	public void execute(List objects)
	{
		String append = getConfiguration().getString("append", null);
		String insert = getConfiguration().getString("insert", null);
		String replace = getConfiguration().getString("replace", null);

		for (int i = 0; objects!= null && i < objects.size(); i++)
		{
			StructItemInfo info = (StructItemInfo) objects.get(i);
			ObjectWrapper wrapper = getObjectWrapper(info);

			try
			{
				ObjectBase object = wrapper.getInstance(info.getName());

				if(StringUtility.isNotEmpty(append)) setAppendToName(object, append);
				if(StringUtility.isNotEmpty(insert)) setInsertToName(object, insert);
				if(StringUtility.isNotEmpty(replace)) setReplaceToName(object, replace);

				commit(object);
			}
			catch (Throwable th)
			{
				if(!getConfiguration().getBoolean("force", false))
				{
					RuntimeLogger.error("Error processing '" + info.getName() + "' object name: " + th.getMessage());
					getLogger().debug("Exception", th);
				}
				else RuntimeLogger.warn("Error processing '" + info.getName() + "' object name: " + th.getMessage());
			}
		}
	}

	/**
	 * Commit changes made in the specified object instance
	 *
	 * @param object managed object instance.
	 * @throws ARException if the object factory will return an error
	 */
	public void commit(ObjectBase object) throws ARException
	{
		if(!StringUtility.equals( object.getNewName(),  object.getName() ))
		{
			if(object instanceof ActiveLink)
			{
				ActiveLink objdata = (ActiveLink) object;
				getServerConnection().getContext().setActiveLink(objdata);
			}
			else if(object instanceof Filter)
			{
				Filter objdata = (Filter) object;
				getServerConnection().getContext().setFilter(objdata);
			}
			else if(object instanceof Escalation)
			{
				Escalation objdata = (Escalation) object;
				getServerConnection().getContext().setEscalation(objdata);
			}
			else if(object instanceof Container)
			{
				Container objdata = (Container) object;
				getServerConnection().getContext().setContainer(objdata);
			}
			else if(object instanceof Form)
			{
				Form objdata = (Form) object;
				getServerConnection().getContext().setForm(objdata);
			}
			else if(object instanceof Image)
			{
				Image objdata = (Image) object;
				getServerConnection().getContext().setImage(objdata);
			}
			else if(object instanceof Menu)
			{
				Menu objdata = (Menu) object;
				getServerConnection().getContext().setMenu(objdata);
			}
			else if(object instanceof View)
			{
				View objdata = (View) object;
				getServerConnection().getContext().setView(objdata);
			}
			else if(object instanceof Field)
			{
				Field objdata = (Field) object;
				getServerConnection().getContext().setField(objdata);
			}

			RuntimeLogger.info("Object name '" + object.getName() + "' has been renamed to '" + object.getNewName() + "'");
		}
	}

	private void setAppendToName(ObjectBase object, String append)
	{
		if(object == null || append == null) return;

		String newname = object.getName() + append;
		object.setNewName(newname);
	}

	private void setInsertToName(ObjectBase object, String insert)
	{
		if(object == null || insert == null) return;

		String newname = insert + object.getName();
		object.setNewName(newname);
	}

	private void setReplaceToName(ObjectBase object, String replace)
	{
		if(object == null || replace == null) return;
		int index = replace.indexOf("/", 0);

		if(index > 0)
		{
			String oldString = replace.substring(0, index);
			String newString = replace.substring(index + 1);
			String newname = StringUtility.replace(object.getName(), oldString, newString);

			object.setNewName(newname);
		}
	}
}
