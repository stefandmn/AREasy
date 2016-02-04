package org.areasy.runtime.actions.arserver.dev.tools.flow;

import com.bmc.arsys.api.StructItemInfo;
import org.areasy.runtime.actions.arserver.dev.DefinitionAction;
import org.areasy.runtime.engine.base.ServerConnection;
import org.areasy.common.data.StringUtility;
import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;
import org.areasy.common.support.configuration.Configuration;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

public abstract class WorksheetEvent
{
	/** Library logger */
	protected static Logger logger = LoggerFactory.getLog(WorksheetEvent.class);

	private List objmap = null;
	private Configuration config = null;

	protected static String ACT_CREATE 				= "Create";
	protected static String ACT_MODIFY 				= "Modify";
	protected static String ACT_ENABLE 				= "Enable";
	protected static String ACT_DISABLE 			= "Disable";
	protected static String ACT_REMOVE 				= "Remove";
	protected static String ACT_RENAME 				= "Rename";

	protected static String OBJ_FORM				= "01 - Form";
	protected static String OBJ_FILTER				= "05 - Filter";
	protected static String OBJ_ACTIVELINK			= "06 - Active Link";
	protected static String OBJ_MENU				= "08 - Menu";
	protected static String OBJ_ESCALATION			= "09 - Escalation";
	protected static String OBJ_VIEW				= "14 - View";
	protected static String OBJ_FIELD				= "15 - Field";
	protected static String OBJ_IMAGE				= "17 - Image";
	protected static String OBJ_CONTAINER			= "12 - Container";
	protected static String OBJ_ACTIVELINKGUIDE		= "12.1 - Active Link Guide";
	protected static String OBJ_APPLICATION			= "12.2 - Application";
	protected static String OBJ_FILTERGUIDE			= "12.4 - Filter Guide";

	public WorksheetEvent(Configuration config, List objmap)
	{
		this.config = config;
		this.objmap = objmap;
	}

	public Configuration getConfiguration()
	{
		return this.config;
	}

	protected List getObjectsList()
	{
		return this.objmap;
	}

	/**
	 * Perform development action.
	 *
	 * @param develop the remote action who will call this library.
	 */
	public abstract void perform(DevProcessAction develop);

	/**
	 * Logger instance for the current action
	 *
	 * @return <code>Logger</code> instance
	 */
	protected Logger getLogger()
	{
		return logger;
	}

	/**
	 * Convert and normalize development records (<code>WorksheetObject</code> instances) into workflow object references
	 * (<code>StructItemInfo</code> object).
	 *
	 * @param connection this is an optional parameter that will be used in case of form related object have to be converted and
	 * related details (e.g. fields or views) are not explicitly declared. In this situation the workflow will try to detect
	 * all fields and views.
	 *
	 * @return a list of workflow object references described by <code>StructItemInfo</code> structure.
	 */
	protected List convert(ServerConnection connection)
	{
		List<StructItemInfo> output = new Vector<StructItemInfo>();

		//prepare data for export
		for(int i = 0; getObjectsList() != null && i < getObjectsList().size(); i++)
		{
			WorksheetObject devobj = (WorksheetObject) getObjectsList().get(i);
			StructItemInfo sii = devobj.getStructItemInfo( connection );

			if(sii != null)
			{
				boolean found = false;
				String CC = sii.getType() + "#" + sii.getName();

				for (int j = 0; !found && j < output.size(); j++)
				{
					StructItemInfo inObj = output.get(j);
					String inCC = inObj.getType() + "#" + inObj.getName();

					if(StringUtility.equals(inCC, CC))
					{
						found = true;

						if(sii.getType() == StructItemInfo.FIELD || sii.getType() == StructItemInfo.VUI)
						{
							List inList = new Vector();
							List siiList = Arrays.asList(sii.getSelectedElements());

							for(int x = 0; inObj.getSelectedElements() != null && x < inObj.getSelectedElements().length; x++) inList.add( inObj.getSelectedElements()[x] );

							for(int x = 0; siiList != null && x < siiList.size(); x++)
							{
								String relatedObj = (String) siiList.get(x);
								if(!inList.contains(relatedObj)) inList.add(relatedObj);
							}

							inObj.setSelectedElements((String[]) inList.toArray(new String[inList.size()]));
						}
					}
				}

				if(!found) output.add(sii);
			}
		}

		return output;
	}

	public static String getPluralObjectTypeNameBySignature(String signature)
	{
		if(signature == null) return null;
		else if(signature.equals(OBJ_APPLICATION)) return DefinitionAction.TYPE_APPLICATIONS;
		else if(signature.equals(OBJ_FORM)) return DefinitionAction.TYPE_FORMS;
		else if(signature.equals(OBJ_VIEW)) return DefinitionAction.TYPE_VIEWS;
		else if(signature.equals(OBJ_ACTIVELINK)) return DefinitionAction.TYPE_ACTIVELINKS;
		else if(signature.equals(OBJ_FILTER)) return DefinitionAction.TYPE_FILTERS;
		else if(signature.equals(OBJ_ESCALATION)) return DefinitionAction.TYPE_ESCALATIONS;
		else if(signature.equals(OBJ_ACTIVELINKGUIDE)) return DefinitionAction.TYPE_ACTIVELINKGUIDES;
		else if(signature.equals(OBJ_FILTERGUIDE)) return DefinitionAction.TYPE_FILTERGUIDES;
		else if(signature.equals(OBJ_MENU)) return DefinitionAction.TYPE_MENUS;
		else if(signature.equals(OBJ_IMAGE)) return DefinitionAction.TYPE_IMAGES;
		else if(signature.equals(OBJ_CONTAINER)) return DefinitionAction.TYPE_CONTAINERS;
		else if(signature.equals(OBJ_FIELD)) return DefinitionAction.TYPE_FIELDS;
		else return null;
	}

	public static int getObjectTypeIdBySignature(String signature)
	{
		if(signature == null) return 0;
		else if(signature.equals(OBJ_APPLICATION)) return StructItemInfo.CONTAINER;
		else if(signature.equals(OBJ_FORM)) return StructItemInfo.SCHEMA;
		else if(signature.equals(OBJ_VIEW)) return StructItemInfo.VUI;
		else if(signature.equals(OBJ_ACTIVELINK)) return StructItemInfo.ACTIVE_LINK;
		else if(signature.equals(OBJ_FILTER)) return StructItemInfo.FILTER;
		else if(signature.equals(OBJ_ESCALATION)) return StructItemInfo.ESCALATION;
		else if(signature.equals(OBJ_ACTIVELINKGUIDE)) return StructItemInfo.CONTAINER;
		else if(signature.equals(OBJ_FILTERGUIDE)) return StructItemInfo.CONTAINER;
		else if(signature.equals(OBJ_CONTAINER)) return StructItemInfo.CONTAINER;
		else if(signature.equals(OBJ_MENU)) return StructItemInfo.CHAR_MENU;
		else if(signature.equals(OBJ_IMAGE)) return StructItemInfo.IMAGE;
		else if(signature.equals(OBJ_FIELD)) return StructItemInfo.FIELD;
		else return 0;
	}

	public static String getObjectSignatureByType(int type)
	{
		if(type == 0) return null;
		else if(type == StructItemInfo.SCHEMA) return OBJ_FORM;
		else if(type == StructItemInfo.VUI) return OBJ_VIEW;
		else if(type == StructItemInfo.ACTIVE_LINK) return OBJ_ACTIVELINK;
		else if(type == StructItemInfo.FILTER) return OBJ_FILTER;
		else if(type == StructItemInfo.ESCALATION) return OBJ_ESCALATION;
		else if(type == StructItemInfo.CONTAINER || type == StructItemInfo.APPLICATION) return OBJ_CONTAINER;
		else if(type == StructItemInfo.CHAR_MENU) return OBJ_MENU;
		else if(type == StructItemInfo.IMAGE) return OBJ_IMAGE;
		else if(type == StructItemInfo.FIELD) return OBJ_FIELD;
		else return null;
	}

	public static boolean removeFolder(File dir)
	{
		if (dir.isDirectory())
		{
			String[] children = dir.list();
			for (int i=0; i<children.length; i++)
			{
				boolean success = removeFolder(new File(dir, children[i]));
				if (!success) return false;
			}
		}

		// The directory is now empty so delete it
		return dir.delete();
	}

	public static String escapeString(String value)
	{
		if(value != null)
		{
			Pattern pattern = Pattern.compile("[^a-zA-Z0-9]");
			Matcher matcher = pattern.matcher(value);

			return matcher.replaceAll("_");
		}
		else return null;
	}
}
