package org.areasy.runtime.actions.arserver.dev;

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
import org.areasy.runtime.RuntimeAction;
import org.areasy.runtime.RuntimeManager;
import org.areasy.runtime.actions.AbstractAction;
import org.areasy.runtime.actions.arserver.dev.wrappers.*;
import org.areasy.runtime.engine.RuntimeLogger;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.runtime.engine.services.parser.ParserEngine;
import org.areasy.common.data.DateUtility;
import org.areasy.common.data.StringUtility;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

/**
 * Abstract runtime action to process definition objects: export, import deletion, activation, etc.
 */
public abstract class DefinitionAction extends AbstractAction implements RuntimeAction
{
	public static String TYPE_FORMS 			= "forms";
	public static String TYPE_VIEWS				= "views";
	public static String TYPE_ACTIVELINKS       = "activelinks";
	public static String TYPE_FILTERS           = "filters";
	public static String TYPE_ESCALATIONS       = "escalations";
	public static String TYPE_CONTAINERS        = "containers";
	public static String TYPE_APPLICATIONS      = "applications";
	public static String TYPE_ACTIVELINKGUIDES  = "activelinkguides";
	public static String TYPE_FILTERGUIDES		= "filterguides";
	public static String TYPE_MENUS				= "menus";
	public static String TYPE_IMAGES			= "images";
	public static String TYPE_FIELDS			= "fields";
	public static String TYPE_WEBSERVICES		= "webservices";

	public static String types[] = {TYPE_FORMS, TYPE_MENUS, TYPE_ACTIVELINKS, TYPE_FILTERS, TYPE_ESCALATIONS,
									TYPE_ACTIVELINKGUIDES, TYPE_APPLICATIONS, TYPE_FILTERGUIDES, TYPE_VIEWS,
									TYPE_IMAGES, TYPE_CONTAINERS, TYPE_FIELDS, TYPE_WEBSERVICES};

	public static final int ACTIVELINKGUIDE			= 121;
	public static final int FILTERGUIDE				= 124;
	public static final int APPLICATIONCONTAINER	= 122;
	public static final int WEBSERVICE 				= 125;

	/**
	 * Select and process abstract objects from an AR System server.
	 *
	 * @throws org.areasy.runtime.engine.base.AREasyException
	 * if any error will occur
	 */
	public void run() throws AREasyException
	{
		List input = getInputList();

		//get the list of objects and write it
		execute(input);
	}

	/**
	 * Execute general action using bulk data-structures. This method will execute the corresponding method from the subclass.
	 *
	 * @param data a list with all found objects
	 */
	public void execute(List data)
	{
		for (int i = 0; data!= null && i < data.size(); i++)
		{
			StructItemInfo info = (StructItemInfo) data.get(i);
			ObjectWrapper wrapper = getObjectWrapper(info);

			if(wrapper instanceof FormRelatedWrapper)
			{
				try
				{
					ObjectBase objects[] = ((FormRelatedWrapper)wrapper).getInstance(info.getName(), info.getSelectedElements());

					for (int j = 0; objects != null && j < objects.length; j++)
					{
						execute(objects[j]);
					}
				}
				catch (Throwable th)
				{
					RuntimeLogger.error("Error processing " + getTextFromStructItemInfo(info) + ": " + th.getMessage());
					getLogger().debug("Exception", th);
				}
			}
			else
			{
				try
				{
					ObjectBase object = wrapper.getInstance(info.getName());
					execute(object);
				}
				catch (Throwable th)
				{
					RuntimeLogger.error("Error processing " + getTextFromStructItemInfo(info) + ": " + th.getMessage());
					getLogger().debug("Exception", th);
				}
			}
		}
	}

	/**
	 * Get the input list of objects that have to be processes. In order to obtain it it should be
	 * used one of those parameters.
	 *
	 * <p/>
	 * <table border="1">
	 * 	<tr>
	 * 		<td><b>-prefix</b></td>
	 * 		<td>Specify a string prefix to match a list of object names to be exported.</td>
	 * 	</tr>
	 * 	<tr>
	 * 		<td><b>-include</b></td>
	 * 		<td>Specify an object name to be processed.</td>
	 * 	</tr>
	 * 	<tr>
	 * 		<td><b>-exclude</b></td>
	 * 		<td>Specify an object name which should be excluded from the final processing list.</td>
	 * 	</tr>
	 * 	<tr>
	 * 		<td><b>-since</b></td>
	 * 		<td>Get objects which are changed from the specified date until now.</td>
	 * 	</tr>
	 * 	<tr>
	 * 		<td><b>-inputfile</b></td>
	 * 		<td>Specify a list of object names declared in a file which can be parsed by AREasy Runtime Parser (text, cvs or excel).
	 * 			The file(s) must be located on runtime server.</td>
	 * 	</tr>
	 * 	<tr>
	 * 		<td><b>-owner</b></td>
	 * 		<td>Get objects which have the specified owner</td>
	 * 	</tr>
	 * 	<tr>
	 * 		<td><b>-changer</b></td>
	 * 		<td>Get objects which are changed last time by the specified user</td>
	 * 	</tr>
	 * 	<tr>
	 * 		<td><b>-keyword</b></td>
	 * 		<td>Get objects which contain in help text section the specified keyword</td>
	 * 	</tr>
	 * </table>
	 * <p/>
	 *
	 * @throws org.areasy.runtime.engine.base.AREasyException
	 * if any error will occur
	 */
	protected List getInputList() throws AREasyException
	{
		return getInputList(true);
	}

	protected List getInputList(boolean validate) throws AREasyException
	{
		//get list of included objects
		List input = new Vector();

		//get list of excluded objects
		List exclude = getConfiguration().getVector("exclude", new Vector());

		//get prefix value.
		String prefix = getConfiguration().getString("prefix", null);

		Date since = null;
		String changedSince = getConfiguration().getString("since", null);
		try { since = DateUtility.parseDate(changedSince); } catch (Throwable th) { /* nothing to do now */ }

		//get the owner, changer and keyword
		String owner = getConfiguration().getString("owner", null);
		String changer = getConfiguration().getString("changer", null);
		String keyword = getConfiguration().getString("keyword", null);

		//check if objects is a file
		if (getConfiguration().containsKey("includefile"))
		{
			//set file parser environment parameters
			ParserEngine engine = new ParserEngine(getServerConnection(), getManager().getConfiguration(), getConfiguration());
			engine.setResource("parserfile", getConfiguration().getString("includefile", null));
			engine.init("file");

			String objects[] = null;

			do
			{
				objects = engine.read();

				if (objects != null && StringUtility.isNotEmpty(objects[0]) && !objects[0].trim().startsWith("#"))
				{
					if (!input.contains(objects[0])) input.add(objects[0]);
					else RuntimeLogger.warn("Object '" + objects[0] + "' is skipped because is already included in the objects list");
				}
			}
			while(objects != null);

			engine.close();
		}

		return getItemList(input, exclude, prefix, since, owner, changer, keyword, validate);
	}

	/**
	 * Find ARS objects and return a list with confirmed (found) objects.
	 *
	 * @param input input lines that could be workflow object identifiers read from a file
	 * @param exclude a list of object names which must be excluded
	 * @param prefix  a prefix string
	 * @param since	changed since
	 * @param owner	an user name which should be the owner of the selected objects which must be processed
	 * @param changer  an user name which should be the last changer of the selected objects which must be processed
	 * @param keyword  a keyword which should identify all objects which must be processed. This identification is done using help text
	 *                 section for each object name.
	 * @return a list with confirmed (found) objects
	 * @throws AREasyException in case of any exception occur
	 */
	protected List getItemList(List input, List exclude, String prefix, Date since, String owner, String changer, String keyword, boolean validate) throws AREasyException
	{
		List objects = new ArrayList();
		List wrappers = new ArrayList();

		List exclusiveTypes = getExclusiveOptions();

		if(exclusiveTypes == null)
		{
			wrappers.add(new FormWrapper(this));
			wrappers.add(new ActiveLinkWrapper(this));
			wrappers.add(new ActiveLinkGuideWrapper(this));
			wrappers.add(new FilterWrapper(this));
			wrappers.add(new FilterGuideWrapper(this));
			wrappers.add(new MenuWrapper(this));
			wrappers.add(new ImageWrapper(this));
			wrappers.add(new EscalationWrapper(this));
			wrappers.add(new ApplicationWrapper(this));
			wrappers.add(new WebserviceWrapper(this));
			wrappers.add(new ContainerWrapper(this));
			wrappers.add(new FieldWrapper(this));
			wrappers.add(new ViewWrapper(this));
		}
		else
		{
			for(int i = 0; i < exclusiveTypes.size(); i++)
			{
				String exclusiveType = (String) exclusiveTypes.get(i);

				ObjectWrapper wrapper = getObjectWrapper(exclusiveType);
				if(wrapper != null) wrappers.add(wrapper);
			}
		}

		for(int i = 0; i < wrappers.size(); i++)
		{
			ObjectWrapper wrapper = (ObjectWrapper) wrappers.get(i);

			setObjectsListByWrapper(objects, wrapper, exclude, prefix, since, owner, changer, keyword);
			setObjectsListByInputFile(objects, wrapper, input, exclude, validate);
			setObjectsListByInputParameters(objects, wrapper, validate);
		}

		return objects;
	}

	private void setObjectsListByWrapper(List output, ObjectWrapper wrapper, List exclude, String prefix, Date since, String owner, String changer, String keyword) throws AREasyException
	{
		List<String> objectKeys = null;
		List objects = new Vector();

		if (wrapper != null)
		{
			//check server based parameters
			if (prefix != null || since != null || StringUtility.isNotEmpty(owner) || StringUtility.isNotEmpty(changer) || StringUtility.isNotEmpty(keyword))
			{
				//get all object keys and structure.
				if(wrapper instanceof FormRelatedWrapper)
				{
					for (int i = 0; output != null && i < output.size(); i++)
					{
						StructItemInfo related = (StructItemInfo)output.get(i);

						if(related.getType() == StructItemInfo.SCHEMA)
						{
							FormRelatedWrapper formWrapper = (FormRelatedWrapper)wrapper;

							formWrapper.setFormName(related.getName());
							objectKeys = formWrapper.find(since);

							for (int j = 0; objectKeys != null && j < objectKeys.size(); j++)
							{
								String objectKey = objectKeys.get(j);

								//build object and idd on the final list
								StructItemInfo info = getStructInfoFromObject(objectKey, wrapper, exclude, prefix, since, owner, changer, keyword);

								if(info != null)
								{
									objects.add(info);
									RuntimeLogger.debug(getTextFromStructItemInfo(info) + " has been included in the processing list");
								}
							}
						}
					}
				}
				else
				{
					objectKeys = wrapper.find(since);

					for (int j = 0; objectKeys != null && j < objectKeys.size(); j++)
					{
						String objectKey = objectKeys.get(j);

						//build object and idd on the final list
						StructItemInfo info = getStructInfoFromObject(objectKey, wrapper, exclude, prefix, since, owner, changer, keyword);

						if(info != null)
						{
							objects.add(info);
							RuntimeLogger.debug(getTextFromStructItemInfo(info) + " has been included in the processing list");
						}
					}
				}
			}
		}

		if(!objects.isEmpty()) output.addAll(objects);
	}

	private StructItemInfo getStructInfoFromObject(String objectKey, ObjectWrapper wrapper, List exclude, String prefix, Date since, String owner, String changer, String keyword)
	{
		StructItemInfo info = null;

		try
		{
			if(objectKey != null)
			{
				ObjectBase object = wrapper.getInstance(objectKey);

			    String name = null;
				String elements[] = null;

				if(wrapper instanceof FormRelatedWrapper)
				{
					name = ((FormRelatedWrapper)wrapper).getFormName() ;
					elements = new String[1];
					elements[0] = object.getName();
				}
				else
				{
					name = object.getName();
					elements = null;
				}

				String ownerval = object.getOwner();
				String changerval = object.getLastChangedBy();
				String helptextval = object.getHelpText();

				//check if is on the excluded list
				if(StringUtility.isEmpty(name) || exclude.contains(name)) return null;

				//check prefix
				if (StringUtility.isNotEmpty(prefix) && !name.startsWith(prefix)) return null;

				//check owner
				if (StringUtility.isNotEmpty(owner) && !StringUtility.equals(ownerval, owner)) return null;

				//check changer
				if (StringUtility.isNotEmpty(changer) && !StringUtility.equals(changerval, changer)) return null;

				//check keywords
				if (StringUtility.isNotEmpty(keyword) && !StringUtility.contains(helptextval, keyword)) return null;


				info = new StructItemInfo(wrapper.getObjectTypeId(), name, elements);
			}
		}
		catch (Exception ex)
		{
			RuntimeLogger.error("Object '" + objectKey + "' could not be found: " + ex.getMessage());
			logger.debug("Exception", ex);
		}

		return info;
	}

	private void setObjectsListByInputFile(List output, ObjectWrapper wrapper, List input, List exclude, boolean validate) throws AREasyException
	{
		List objects = new Vector();

		if (wrapper != null)
		{
			List exclusiveTypes = getExclusiveOptions();
			if(exclusiveTypes != null && !exclusiveTypes.contains(wrapper.getPluralObjectTypeName())) return;

			//check if there object specified into a file
			if(input != null && !input.isEmpty())
			{
				try
				{
					for (int i = 0; i < input.size(); i++)
					{
						StructItemInfo info = null;
						String objectName = null;
						boolean match = false;

						if(exclusiveTypes == null)
						{
							String objectKey = getObjectTypeNameByObjectTypeId(wrapper.getObjectTypeId());
							String objectLine = (String) input.get(i);

							int indexObjectType = objectLine.indexOf(':', 0);
							String objectType = indexObjectType > 0 ? objectLine.substring(0, indexObjectType).trim() : "";
							objectName = indexObjectType > 0 ? objectLine.substring(indexObjectType + 1).trim() : "";

							match = StringUtility.equalsIgnoreCase(objectType, objectKey);
						}
						else
						{
							match = true;
							objectName = (String) input.get(i);
						}

						if(match)
						{
							try
							{
								//check if the input object is a form with one or many views
								if(wrapper.getObjectTypeId() == StructItemInfo.VUI)
								{
									ViewWrapper view = (ViewWrapper)wrapper;
									info = getViewStringItemInfo(view, objectName);

									String name = (info != null ? info.getName() : null);

									//check if is on the excluded list
									if(StringUtility.isEmpty(name) || exclude.contains(name)) continue;
								}
								else if(wrapper.getObjectTypeId() == StructItemInfo.FIELD)
								{
									FieldWrapper field = (FieldWrapper)wrapper;
									info = getFieldStringItemInfo(field, objectName);

									String name = (info != null ? info.getName() : null);

									//check if is on the excluded list
									if(StringUtility.isEmpty(name) || exclude.contains(name)) continue;
								}
								else
								{
									if(validate)
									{
										ObjectBase object = wrapper.getInstance(objectName);
										String name = object.getName();

										//check if is on the excluded list
										if(StringUtility.isEmpty(name) || exclude.contains(name)) continue;

										//build object and id on the final list
										info = new StructItemInfo(wrapper.getObjectTypeId(), name, null);
									}
									else info = new StructItemInfo(wrapper.getObjectTypeId(), objectName, null);

								}

								//if structure is not null add it
								if(info != null)
								{
									objects.add(info);
									RuntimeLogger.debug(getTextFromStructItemInfo(info) + " has been included in the processing list");
								}
							}
							catch (Exception ex)
							{
								RuntimeLogger.error("Object '" + objectName  + "' could not be found: " + ex.getMessage());
								logger.debug("Exception", ex);
							}
						}
					}
				}
				catch (Throwable th)
				{
					throw new AREasyException("Error reading object definitions for server parameters: " + th.getMessage(), th);
				}
			}
		}

		if(!objects.isEmpty()) output.addAll(objects);
	}

	private void setObjectsListByInputParameters(List output, ObjectWrapper wrapper, boolean validate) throws AREasyException
	{
		List<String> objectKeys = null;
		List objects = new Vector();

		if (wrapper != null)
		{
			//check specific parameters for the loaded wrapper
			String param1 = getObjectTypeNameByObjectTypeId(wrapper.getObjectTypeId());
			String param2 = getPluralObjectTypeNameByObjectTypeId(wrapper.getObjectTypeId());

			if((param1 != null || param2 != null) && (getConfiguration().containsKey(param1) || getConfiguration().containsKey(param2)))
			{
				String objectKey = getConfiguration().getString(param1, null);
				objectKeys = getConfiguration().getList(param2, null);

				if(objectKeys == null && objectKey != null)
				{
					objectKeys = new Vector();
					objectKeys.add(objectKey);
				}
				else
				{
					if(objectKey != null) objectKeys.add(0, objectKey);
				}

				for (int i = 0; objectKeys != null && i < objectKeys.size(); i++)
				{
					objectKey = objectKeys.get(i);
					StructItemInfo info = null;

					try
					{
						if(wrapper.getObjectTypeId() == StructItemInfo.VUI)
						{
							ViewWrapper view = (ViewWrapper) wrapper;
							info = getViewStringItemInfo(view, objectKey);
						}
						else if(wrapper.getObjectTypeId() == StructItemInfo.FIELD)
						{
							FieldWrapper field = (FieldWrapper) wrapper;
							info = getFieldStringItemInfo(field, objectKey);
						}
						else
						{
							if(validate)
							{
								ObjectBase object = wrapper.getInstance(objectKey);
								String name = object.getName();

								//build the object and add it in the final list
								info = new StructItemInfo(wrapper.getObjectTypeId(), name, null);
							}
							else info = new StructItemInfo(wrapper.getObjectTypeId(), objectKey, null);
						}

						if(info != null)
						{
							objects.add(info);
							RuntimeLogger.debug(getTextFromStructItemInfo(info) + " has been included in the processing list");
						}
					}
					catch (Throwable th)
					{
						RuntimeLogger.error("Error reading '" + objectKey + "' object definition: " + th.getMessage());
						logger.debug("Exception", th);
					}
				}

			}
		}
		else
		{
			for(int x = 0; x < types.length; x++)
			{
				String objectKey = getConfiguration().getString(toSingural(types[x]), null);
				objectKeys = getConfiguration().getList(types[x], null);

				if(objectKeys == null && objectKey != null)
				{
					objectKeys = new Vector();
					objectKeys.add(objectKey);
				}
				else
				{
					if(objectKey != null) objectKeys.add(0, objectKey);
				}

				//build the final list
				if(objectKeys != null)
				{
					for (int i = 0; i < objectKeys.size(); i++)
					{
						String name = objectKeys.get(i);

						StructItemInfo info = new StructItemInfo(getObjectTypeIdByPluralObjectTypeName(types[x]), name, null);
						objects.add(info);

						RuntimeLogger.debug(getTextFromStructItemInfo(info) + " has been included in the processing list");
					}
				}
			}
		}

		if(!objects.isEmpty()) output.addAll(objects);
	 }

	private StructItemInfo getViewStringItemInfo(ViewWrapper view, String objectKey) throws ARException
	{
		int indexOP = objectKey.indexOf("(", 0);
		int indexCP = objectKey.indexOf(")", Math.max(0, indexOP));

		if(indexOP > 0 && indexCP > 0)
		{
			String formName = objectKey.substring(0, indexOP).trim();
			view.setFormName(formName);

			String viewNames[] = StringUtility.split(objectKey.substring(indexOP + 1, indexCP), ',');

			for(int i = 0; i < viewNames.length; i++)
			{
				ObjectBase object = view.getInstance(viewNames[i].trim());

				if(object == null)
				{
					RuntimeLogger.error("Object '" + viewNames[i] + "' is null");
					return null;
				}
			}

			return new StructItemInfo(view.getObjectTypeId(), view.getFormName(), viewNames);
		}
		else
		{
			view.setFormName(objectKey);
			List views = view.findObjectsByForm();
			
			String viewNames[] = new String[views.size()];

			for(int i = 0; i < views.size(); i++)
			{
				viewNames[i] = ((View)views.get(i)).getName();
			}

			return new StructItemInfo(view.getObjectTypeId(), view.getFormName(), viewNames);
		}
	}

	private StructItemInfo getFieldStringItemInfo(FieldWrapper field, String objectKey) throws ARException
	{
		int indexOP = objectKey.indexOf("(", 0);
		int indexCP = objectKey.indexOf(")", Math.max(0, indexOP));

		if(indexOP > 0 && indexCP > 0)
		{
			String formName = objectKey.substring(0, indexOP).trim();
			field.setFormName(formName);

			String fieldNames[] = StringUtility.split(objectKey.substring(indexOP + 1, indexCP), ',');

			for(int i = 0; i < fieldNames.length; i++)
			{
				ObjectBase object = field.getInstance(fieldNames[i].trim());

				if(object == null)
				{
					RuntimeLogger.error("Object '" + fieldNames[i] + "' is null");
					return null;
				}
			}

			return new StructItemInfo(field.getObjectTypeId(), field.getFormName(), fieldNames);
		}
		else
		{
			field.setFormName(objectKey);
			List fields = field.findObjectsByForm();

			String fieldNames[] = new String[fields.size()];

			for(int i = 0; i < fields.size(); i++)
			{
				fieldNames[i] = ((Field)fields.get(i)).getName();
			}

			return new StructItemInfo(field.getObjectTypeId(), field.getFormName(), fieldNames);
		}
	}

	/**
	 * Commit changes made in the specified object instance.
	 *
	 * @param object managed object instance.
	 * @throws com.bmc.arsys.api.ARException if the object factory will return an error
	 */
	public void execute(ObjectBase object) throws ARException
	{
		//nothing to do here.
	}

	protected String getTextFromStructItemInfo(StructItemInfo info)
	{
		if(info == null) return null;

		String infoObjName = StringUtility.capitalize(getObjectTypeNameByObjectTypeId(info.getType()));
		infoObjName += info.getType() != StructItemInfo.FIELD && info.getType() != StructItemInfo.VUI ?	" '" + info.getName() + "'" :
				(info.getSelectedElements().length > 1 ? "s '" + StringUtility.join(info.getSelectedElements(), ",") + "'" : " '" + info.getSelectedElements()[0] + "'") +
				" from '" + info.getName() + "' form";

		return infoObjName.trim();
	}

	protected String getTextFromObjectBase(ObjectBase object)
	{
		if(object == null) return null;

		int type = getObjectTypeId(object);
		String name = type != StructItemInfo.FIELD && type != StructItemInfo.VUI ? object.getName() : type == StructItemInfo.FIELD ? ((Field)object).getForm() :  ((View)object).getFormName();
		String selectedelements[] = { type == StructItemInfo.FIELD || type == StructItemInfo.VUI ? object.getName() : null };

		StructItemInfo info = new StructItemInfo(type, name, selectedelements);

		return getTextFromStructItemInfo(info);
	}

	protected String toSingural(String name)
	{
		if(name != null && name.endsWith("s")) name = name.substring(0, name.length() - 1);
		return name;
	}

	protected String toPlural(String name)
	{
		if(name != null && !name.endsWith("s")) name += "s";
		return name;
	}

	/**
	 * Get string object type based on the object type id.
	 *
	 * @param typeId object type
	 * @return object base name.
	 */
	public String getObjectTypeNameByObjectTypeId(int typeId)
	{
		String name = getPluralObjectTypeNameByObjectTypeId(typeId);
		return toSingural(name);
	}

	/**
	 * Get string object types, based on the object type id.
	 *
	 * @param typeId object type
	 * @return object base name.
	 */
	public String getPluralObjectTypeNameByObjectTypeId(int typeId)
	{
		String name;

		switch (typeId)
		{
			case StructItemInfo.SCHEMA:
				name = TYPE_FORMS;
				break;
			case StructItemInfo.ACTIVE_LINK:
				name = TYPE_ACTIVELINKS;
				break;
			case StructItemInfo.CONTAINER:
				name = TYPE_CONTAINERS;
				break;
			case StructItemInfo.CHAR_MENU:
				name = TYPE_MENUS;
				break;
			case StructItemInfo.ESCALATION:
				name = TYPE_ESCALATIONS;
				break;
			case StructItemInfo.FILTER:
				name = TYPE_FILTERS;
				break;
			case StructItemInfo.IMAGE:
				name = TYPE_IMAGES;
				break;
			case StructItemInfo.VUI:
				name = TYPE_VIEWS;
				break;
			case StructItemInfo.FIELD:
				name = TYPE_FIELDS;
				break;
			case DefinitionAction.ACTIVELINKGUIDE:
				name = TYPE_ACTIVELINKGUIDES;
				break;
			case DefinitionAction.FILTERGUIDE:
				name = TYPE_FILTERGUIDES;
				break;
			case DefinitionAction.APPLICATIONCONTAINER:
				name = TYPE_APPLICATIONS;
				break;
			case DefinitionAction.WEBSERVICE:
				name = TYPE_WEBSERVICES;
				break;
			default:
				name = null;
				break;
		}

		return name;
	}

	public int getObjectTypeIdByPluralObjectTypeName(String types)
	{
		if(types == null) return 0;
		else if(types.equals(TYPE_FORMS)) return StructItemInfo.SCHEMA;
		else if(types.equals(TYPE_ACTIVELINKS)) return StructItemInfo.ACTIVE_LINK;
		else if(types.equals(TYPE_ACTIVELINKGUIDES)) return DefinitionAction.ACTIVELINKGUIDE;
		else if(types.equals(TYPE_FILTERS)) return StructItemInfo.FILTER;
		else if(types.equals(TYPE_FILTERGUIDES)) return DefinitionAction.FILTERGUIDE;
		else if(types.equals(TYPE_ESCALATIONS)) return StructItemInfo.ESCALATION;
		else if(types.equals(TYPE_VIEWS)) return StructItemInfo.VUI;
		else if(types.equals(TYPE_CONTAINERS)) return StructItemInfo.CONTAINER;
		else if(types.equals(TYPE_IMAGES)) return StructItemInfo.IMAGE;
		else if(types.equals(TYPE_MENUS)) return StructItemInfo.CHAR_MENU;
		else if(types.equals(TYPE_APPLICATIONS)) return DefinitionAction.APPLICATIONCONTAINER;
		else if(types.equals(TYPE_FIELDS)) return StructItemInfo.FIELD;
		else if(types.equals(TYPE_WEBSERVICES)) return DefinitionAction.WEBSERVICE;
       	else return 0;
	}

	public int getObjectTypeId(ObjectBase object)
	{
		if(object == null) return 0;
		else if(object instanceof ApplicationContainer) return DefinitionAction.APPLICATIONCONTAINER;
		else if(object instanceof Form) return StructItemInfo.SCHEMA;
		else if(object instanceof ActiveLink) return StructItemInfo.ACTIVE_LINK;
		else if(object instanceof ActiveLinkGuide) return DefinitionAction.ACTIVELINKGUIDE;
		else if(object instanceof Menu) return StructItemInfo.CHAR_MENU;
		else if(object instanceof Escalation) return StructItemInfo.ESCALATION;
		else if(object instanceof Filter) return StructItemInfo.FILTER;
		else if(object instanceof FilterGuide) return DefinitionAction.FILTERGUIDE;
		else if(object instanceof Image) return StructItemInfo.IMAGE;
		else if(object instanceof View) return StructItemInfo.VUI;
		else if(object instanceof WebService) return DefinitionAction.WEBSERVICE;
		else if(object instanceof Container) return StructItemInfo.CONTAINER;
		else if(object instanceof Field) return StructItemInfo.FIELD;
		else return 0;
	}

	public ObjectWrapper getObjectWrapper(String model)
	{
		if(model == null) return null;
		else if(model.equals(TYPE_FORMS)) return new FormWrapper(this);
		else if(model.equals(TYPE_ACTIVELINKS)) return new ActiveLinkWrapper(this);
		else if(model.equals(TYPE_ACTIVELINKGUIDES)) return new ActiveLinkGuideWrapper(this);
		else if(model.equals(TYPE_MENUS)) return new MenuWrapper(this);
		else if(model.equals(TYPE_ESCALATIONS)) return new EscalationWrapper(this);
		else if(model.equals(TYPE_FILTERS)) return new FilterWrapper(this);
		else if(model.equals(TYPE_FILTERGUIDES)) return new FilterGuideWrapper(this);
		else if(model.equals(TYPE_IMAGES)) return new ImageWrapper(this);
		else if(model.equals(TYPE_VIEWS)) return new ViewWrapper(this);
		else if(model.equals(TYPE_APPLICATIONS)) return new ApplicationWrapper(this);
		else if(model.equals(TYPE_WEBSERVICES)) return new WebserviceWrapper(this);
		else if(model.equals(TYPE_CONTAINERS)) return new ContainerWrapper(this);
		else if(model.equals(TYPE_FIELDS)) return new FieldWrapper(this);
		else return null;
	}

	public ObjectWrapper getObjectWrapper(ObjectBase object)
	{
		if(object instanceof Form) return new FormWrapper(this);
		else if(object instanceof ActiveLink) return new ActiveLinkWrapper(this);
		else if(object instanceof ActiveLinkGuide) return new ActiveLinkGuideWrapper(this);
		else if(object instanceof Menu) return new MenuWrapper(this);
		else if(object instanceof Escalation) return new EscalationWrapper(this);
		else if(object instanceof Filter) return new FilterWrapper(this);
		else if(object instanceof FilterGuide) return new FilterGuideWrapper(this);
		else if(object instanceof Image) return new ImageWrapper(this);
		else if(object instanceof View) return new ViewWrapper(this);
		else if(object instanceof ApplicationContainer) return new ApplicationWrapper(this);
		else if(object instanceof WebService) return new WebserviceWrapper(this);
		else if(object instanceof Container) return new ContainerWrapper(this);
		else if(object instanceof Field) return new FieldWrapper(this);
		else return null;
	}

	public ObjectWrapper getObjectWrapper(StructItemInfo info)
	{
		if(info.getType() == StructItemInfo.SCHEMA) return new FormWrapper(this);
		else if(info.getType() == StructItemInfo.VUI) return new ViewWrapper(this);
		else if(info.getType() == StructItemInfo.ACTIVE_LINK) return new ActiveLinkWrapper(this);
		else if(info.getType() == StructItemInfo.FILTER) return new FilterWrapper(this);
		else if(info.getType() == StructItemInfo.CONTAINER) return new ContainerWrapper(this);
		else if(info.getType() == StructItemInfo.ESCALATION) return new EscalationWrapper(this);
		else if(info.getType() == StructItemInfo.CHAR_MENU) return new MenuWrapper(this);
		else if(info.getType() == StructItemInfo.IMAGE) return new ImageWrapper(this);
		else if(info.getType() == StructItemInfo.FIELD) return new FieldWrapper(this);
		else if(info.getType() == DefinitionAction.APPLICATIONCONTAINER) return new ApplicationWrapper(this);
		else if(info.getType() == DefinitionAction.ACTIVELINKGUIDE) return new ActiveLinkGuideWrapper(this);
		else if(info.getType() == DefinitionAction.FILTERGUIDE) return new FilterGuideWrapper(this);
		else if(info.getType() == DefinitionAction.WEBSERVICE) return new WebserviceWrapper(this);
		else return null;
	}

	/**
	 * Read or detect the import format. This format could be explicitly specified using <code>-format [def|xml|bin]</code> option
	 * or could be detected based on the output file extension.
	 *
	 * @return the format of import file, that could be: def, xml or bin
	 */
	protected String getInputFormat()
	{
		String format = getConfiguration().getString("format", null);

		if(format == null)
		{
			String fileName = getConfiguration().getString("inputfile", null);
			format = getInputFileFormat(fileName);
		}
		else format = format.toLowerCase();

		if(format == null) format = "def";

		return format;
	}

	/**
	 * get the definition file format for an input file.
	 *
	 * @return the format of import file, that could be: def, xml or bin
	 */
	protected String getInputFileFormat(String fileName)
	{
		String format = null;

		if(fileName != null)
		{
			int index = fileName.lastIndexOf('.');
			String ext = index < fileName.length() - 1 ? fileName.substring(index + 1) : null;

			if(ext != null && (StringUtility.equalsIgnoreCase(ext, "def") || StringUtility.equalsIgnoreCase(ext, "xml") || StringUtility.equalsIgnoreCase(ext, "bin"))) format = ext.toLowerCase();
		}

		if(format == null) format = "def";

		return format;
	}

	/**
	 * Read or detect the export format. This format could be explicitly specified using <code>-format [def|xml|bin]</code> option
	 * or could be detected based on the output file extension.
	 *
	 * @param defExt default extension
	 * @return the format of export file, that could be: def, xml or bin
	 */
	protected String getOutputFileFormat(String defExt)
	{
		String format = getConfiguration().getString("format", null);

		if(format == null)
		{
			String fileName = getConfiguration().getString("outputfile", null);

			if(fileName != null)
			{
				int index = fileName.lastIndexOf('.');
				String ext = index < fileName.length() - 1 ? fileName.substring(index + 1) : null;

				if(ext != null) format = ext.toLowerCase();
			}

			if(format == null && defExt != null) format = defExt;
		}
		else format = format.toLowerCase();

		return format;
	}

	/**
	 * get and set the file name that have to be considered for export action. Several validations will be done
	 * <ul>
	 *     <li>file name in case of the input is null</li>
	 *     <li>file name for export format or in case of inventory</li>
	 *     <li>file object in case of append operation</li>
	 * </ul>
	 * @return validated file name
	 */
	protected String getOutputFileName()
	{
		return getOutputFileName(null);
	}

	/**
	 * get and set the file name that have to be considered for export action. Several validations will be done
	 * <ul>
	 *     <li>file name in case of the input is null</li>
	 *     <li>file name for export format or in case of inventory</li>
	 *     <li>file object in case of append operation</li>
	 * </ul>
	 * @param extension default file extension
	 * @return validated file name
	 */
	protected String getOutputFileName(String extension)
	{
		String format = getOutputFileFormat(extension);
		String fileName = getConfiguration().getString("outputfile", null);
		boolean append = getConfiguration().getBoolean("append", false);

		if(fileName == null)
		{
			fileName = RuntimeManager.getWorkingDirectory() + File.separator + "out-" + RuntimeLogger.getChannelName();
		}

		if(!fileName.endsWith(format)) fileName += "." + format;

		if(!append)
		{
			File file = new File(fileName);
			if(file.exists()) file.delete();
		}

		return fileName;
	}

	/**
	 * Get the list of exclusivity object types.
	 *
	 * @return a list of exclusive object types
	 */
	protected List getExclusiveOptions()
	{
		List selectedTypes = new Vector();

		for(int x = 0; x < types.length; x++)
		{
			boolean exclusive = getConfiguration().getBoolean("only-" + types[x], false);
			if(exclusive) selectedTypes.add(types[x]);
		}

		if(selectedTypes.isEmpty()) return null;
			else return selectedTypes;
	}

	/**
	 * Get the list of <code>StructItemInfo</code> structures from a definitions file
	 * @TODO - implement also XML definition file format.
	 *
	 * @param filePath definition file path
	 * @return a list of <code>StructItemInfo</code> instances.
	 * @throws AREasyException in case of any parsing or IO error occurs
	 */
	public List getDefinitionFileStructure(String filePath) throws AREasyException
	{
		BufferedReader reader = null;
		List list = new Vector();
		String str = null;
		int i = 0;

		try
		{
			reader = openFile(filePath, null);

			while ((str = reader.readLine()) != null)
			{
				if(str.startsWith("begin "))
				{
					readDefObjectType(str, reader, list);
				}
			}
		}
		catch (Exception localException)
		{
			throw new AREasyException("Error encountered while parsing definition file for content: " + localException.getMessage(), localException.getCause());
		}
		finally
		{
			if(reader != null) try { reader.close(); } catch(Exception e) { /* nothing to de here */ }
		}

		return list;
	}

	/**
	 * Parser for Definition file
	 *
	 * @param param parameter value from the current line reader
	 * @param reader buffer reader object
	 * @param list output list will will be filled in with the discovered definition structures.
	 * @throws AREasyException AREasyException in case of any parsing or IO error occurs
	 */
	private void readDefObjectType(String param, BufferedReader reader, List list) throws AREasyException
	{
		try
		{
			if (param.startsWith("begin schema data"))
			{
				param = reader.readLine();
				String objName = getDefTagName(param);

				if (objName != null) new StructItemInfo(StructItemInfo.SCHEMA_DATA, objName);
			}
			else if (param.startsWith("begin schema"))
			{
				StructItemInfo info = getStructInfo(StructItemInfo.SCHEMA, reader);
				if (info != null) list.add(info);
			}
			else if (param.startsWith("begin active link"))
			{
				StructItemInfo info = getStructInfo(StructItemInfo.ACTIVE_LINK, reader);
				if (info != null) list.add(info);
			}
			else if (param.startsWith("begin char menu"))
			{
				StructItemInfo info = getStructInfo(StructItemInfo.CHAR_MENU, reader);
				if (info != null) list.add(info);
			}
			else if (param.startsWith("begin container"))
			{
				StructItemInfo info = getStructInfo(StructItemInfo.CONTAINER, reader);
				if (info != null) list.add(info);
			}
			else if (param.startsWith("begin image"))
			{
				StructItemInfo info = getStructInfo(StructItemInfo.IMAGE, reader);
				if (info != null) list.add(info);
			}
			else if (param.startsWith("begin distributed mapping"))
			{
				StructItemInfo info = getStructInfo(StructItemInfo.DIST_MAP, reader);
				if (info != null) list.add(info);
			}
			else if (param.startsWith("begin filter"))
			{
				StructItemInfo info = getStructInfo(StructItemInfo.FILTER, reader);
				if (info != null) list.add(info);
			}
			else if (param.startsWith("begin escalation"))
			{
				StructItemInfo info = getStructInfo(StructItemInfo.ESCALATION, reader);
				if (info != null) list.add(info);
			}
			else if (param.startsWith("begin distributed pool"))
			{
				StructItemInfo info = getStructInfo(StructItemInfo.DIST_POOL, reader);
				if (info != null) list.add(info);
			}
			else if (param.startsWith("begin vui"))
			{
				StructItemInfo info = getViewStructInfo(reader);
				if (info != null) list.add(info);
			}
		}
		catch(Exception e)
		{
			throw new AREasyException("Definition parser error: "  + e.getMessage(), e);
		}
	}

	private StructItemInfo getStructInfo(int type, BufferedReader reader) throws IOException
	{
		boolean stop = false;
		String str1, str2 = null;

		while (!stop && ((str1 = reader.readLine()) != null))
		{
			if (str1.startsWith("   name           : ") && (str2 == null))
			{
				str2 = getDefTagName(str1);
			}
			else if (str1.startsWith("end") && (str2 != null))
			{
				stop = true;
			}
		}

		if (str2 != null) return new StructItemInfo(type, str2);
			else return null;
	}

	public StructItemInfo getViewStructInfo(BufferedReader reader) throws IOException
	{
		boolean stop = false;
		String str1, str2 = null, str3 = null;
		List<String> views = new ArrayList<String>();

		while (!stop && ((str1 = reader.readLine()) != null))
		{
			if (str1.startsWith("   name           : ") && (str2 == null))
			{
				str2 = getDefTagName(str1);
			}
			else if ((str1.startsWith("vui  {")) && (str2 != null))
			{
				reader.readLine();
				str3 = getDefTagName(reader.readLine());

				views.add(str3);
			}
			else if (str1.startsWith("end") && (str2 != null))
			{
				stop = true;
			}
		}

		if (str2 != null) return new StructItemInfo(StructItemInfo.VUI, str2, (String[])views.toArray(new String[views.size()]));
		else return null;
	}

	private String getDefTagName(String paramString)
	{
		String str = null;

		if (paramString.startsWith("   name           : "))
		{
			str = paramString.replace("   name           : ", "");
		}

		return str;
	}

	private BufferedReader openFile(String filePath, String charset) throws IOException
	{
		FileInputStream inputStream = new FileInputStream(filePath);
		InputStreamReader inputReader = null;

		if (StringUtility.isNotEmpty(charset)) inputReader = new InputStreamReader(inputStream, charset);
			else inputReader = new InputStreamReader(inputStream);

		BufferedReader outputReader = new BufferedReader(inputReader);

		if (charset == null)
		{
			String str1 = outputReader.readLine();

			if ((str1 != null) && (str1.startsWith("char-set:")))
			{
				charset = str1.replaceFirst("char-set:", "").trim();
				outputReader.close();

				return openFile(filePath, charset);
			}

			outputReader.close();
			return openFile(filePath, "UTF-8");
		}

		return outputReader;
	}

	protected List<StructItemInfo> getOnlyOverlays(List list, boolean force)
	{
		List<String> validation = new ArrayList<String>();
		List<StructItemInfo> output = new Vector<StructItemInfo>();

		for (int i = 0; list!= null && i < list.size(); i++)
		{
			StructItemInfo info = (StructItemInfo) list.get(i);

			if(info.getName() != null && !info.getName().endsWith("__o") && force)
			{
				String name = info.getName() + "__o";
				String check = info.getType() + ":" + name;

				if(!validation.contains(check))
				{
					info.setName(name);
					output.add(info);
					validation.add(check);
				}
			}
			else if(info.getName() != null && info.getName().endsWith("__o"))
			{
				String check = info.getType() + ":" + info.getName();

				if(!validation.contains(check))
				{
					output.add(info);
					validation.add(check);
				}
			}
		}

		validation.clear();

		return output;
	}

	protected List<StructItemInfo> getValidatedList(List list, List fullist)
	{
		List<String> validation = new ArrayList<String>();
		List<StructItemInfo> output = new Vector<StructItemInfo>();

		for (int i = 0; fullist!= null && i < fullist.size(); i++)
		{
			StructItemInfo info = (StructItemInfo) fullist.get(i);

			String validname = info.getType() + ":" + info.getName();
			validation.add(validname);
		}

		for (int i = 0; list!= null && i < list.size(); i++)
		{
			StructItemInfo info = (StructItemInfo) list.get(i);
			String check = info.getType() + ":" + info.getName();

			if(validation.contains(check)) output.add(info);
		}

		return output;
	}

	protected String getStructItemInfoListToString(List list)
	{
		if(list != null && !list.isEmpty())
		{
			String values[] = new String[list.size()];

			for(int i = 0; i < list.size(); i++)
			{
				StructItemInfo info = (StructItemInfo) list.get(i);
				values[i] = getStructItemInfoToString(info);
			}

			return StringUtility.join(values, ",");
		}
		else return null;
	}

	protected String getStructItemInfoToString(StructItemInfo info)
	{
		if(info != null) return getObjectTypeNameByObjectTypeId(info.getType()) + " '" + info.getName() + "'";
			else return null;
	}
}
