package org.areasy.runtime.actions.arserver.defs.tools;

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

import com.bmc.arsys.api.*;
import org.areasy.runtime.RuntimeAction;
import org.areasy.runtime.actions.AbstractAction;
import org.areasy.runtime.engine.RuntimeLogger;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.common.data.StringUtility;
import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;

import java.util.*;

public class SetHierarchicPermissionGroup extends AbstractAction implements RuntimeAction
{
	private static Logger logger = LoggerFactory.getLog(SetHierarchicPermissionGroup.class);

	protected ARServerUser userOverlay = null;
	protected ARServerUser userBase = null;

	public void run() throws AREasyException
	{
		List<String> excluded;

		int sourceId = getConfiguration().getInt("source", 0);
		int parentId = getConfiguration().getInt("parent", 0);
		String formName = getConfiguration().getString("form", null);
		List forms = getConfiguration().getList("forms", new ArrayList());
		boolean recursivejoin = getConfiguration().getBoolean("recursivejoin", false);
		boolean skipvalidation = getConfiguration().getBoolean("skipvalidation", true);

		if(formName != null && !forms.contains(formName)) forms.add(formName);

		if(forms.isEmpty() || sourceId <= 0 || parentId <= 0) throw new AREasyException("Invalid input parameters");

		if(getServerConnection().isOverlayMode())
		{
			userOverlay = getServerConnection().getContext();
			userBase = getServerConnection().newConnection(false).getContext();
		}
		else
		{
			userBase = getServerConnection().getContext();
			userOverlay = getServerConnection().newConnection(true).getContext();
		}

		//define the form list that should be excluded from the process
		if(skipvalidation) excluded = getExclusionList(userOverlay);
			else excluded = new Vector();

		//permission groups validation
		if (!validateGroupId(sourceId, true)) throw new AREasyException("Source group " + sourceId + " is in wrong range");
		if (!validateGroupId(parentId, false)) throw new AREasyException("Parent group " + parentId + " is in wrong range");

		//create permission group
		createGroup(sourceId, parentId);

		//create fields and define mapping
		for(int i = 0; i < forms.size(); i++)
		{
			String form = (String) forms.get(i);

			if (StringUtility.isNotEmpty(form) && !excluded.contains(form))
			{
				try
				{
					fixForm(form, sourceId, parentId);
					if(recursivejoin) fixRecursiveJoinForms(form, sourceId, parentId);
				}
				catch(AREasyException are)
				{
					RuntimeLogger.error("Error setting hierarchic permission group for '" + form + "' form (or related join forms): " + are.getMessage());
					logger.debug("Exception", are);
				}
			}

		}
	}

	protected boolean fixForm(String formName, int sourceGroupId, int parentGroupId) throws AREasyException
	{
		logger.debug("Set hierarchic permission group " + parentGroupId + " for '" + formName + "' form name");

		try
		{
			Form form = userOverlay.getForm(formName);
			boolean isFormOverlayed = false;

			//validate form type
			if (form instanceof JoinForm) return fixJoinForm(formName, null, sourceGroupId, parentGroupId);

			if (!hasPermissionField(formName, sourceGroupId))
			{
				logger.warn("Form " + formName + " has no source field " + sourceGroupId);
				return false;
			}

			ObjectPropertyMap props = form.getProperties();
			Value overlay = (Value) props.get(Integer.valueOf(90015));
			if ((overlay != null) && ((overlay.getIntValue() == 2) || (overlay.getIntValue() == 4))) isFormOverlayed = true;

			if (!hasPermissionField(formName, parentGroupId))
			{
				if (!isFormOverlayed)
				{
					try
					{
						createFormOverlay(formName);

						form = userOverlay.getForm(formName);
						props = form.getProperties();
					}
					catch (ARException e)
					{
						throw new AREasyException("failed to create form " + formName + " overlay: " + e.getMessage(), e);
					}
				}

				createParentField(formName, sourceGroupId, parentGroupId);
			}

			Value dynamicInheritance = (Value) props.get(Integer.valueOf(60040));
			boolean saveForm = false;

			if (dynamicInheritance == null)
			{
				props.put(Integer.valueOf(60040), new Value(sourceGroupId + ":" + parentGroupId));
				saveForm = true;
			}
			else
			{
				String temp = dynamicInheritance.toString();

				if (!temp.contains(sourceGroupId + ":" + parentGroupId))
				{
					temp = temp + " " + sourceGroupId + ":" + parentGroupId;
					props.put(Integer.valueOf(60040), new Value(temp));
					saveForm = true;
				}
			}

			if (saveForm)
			{
				form.setProperties(props);
				userOverlay.setForm(form);
			}

			addRequestIdPermissions(formName, sourceGroupId, parentGroupId);
			return saveForm;
		}
		catch (ARException e)
		{
			throw new AREasyException("Failed to fix form " + formName + ": " + e.getMessage(), e);
		}
	}

	protected void addRequestIdPermissions(String formName, int sourceId, int parentId) throws AREasyException
	{
		try
		{
			CharacterField requestId = (CharacterField) userOverlay.getField(formName, 1);
			List<com.bmc.arsys.api.PermissionInfo> permissions = requestId.getPermissions();

			PermissionInfo tempInfo = null;
			int tempSourceId = 7;
			boolean hasParent = false;

			if (sourceId != 112) tempSourceId = sourceId;

			for (PermissionInfo perm : permissions)
			{
				if (perm.getGroupID() == tempSourceId) tempInfo = perm;
					else if (perm.getGroupID() == parentId) hasParent = true;
			}

			if ((!hasParent) && (tempInfo != null)) tempInfo.setGroupID(parentId);
				else return;

			ObjectPropertyMap propMap = requestId.getObjectProperty();
			Value overlay = (Value) propMap.get(Integer.valueOf(90015));

			if ((overlay == null) || ((overlay.getIntValue() != 2) && (overlay.getIntValue() != 4) && (overlay.getIntValue() != 1)))
			{
				createFieldOverlay(formName, requestId.getName(), 1);
				requestId = (CharacterField) userOverlay.getField(formName, 1);
			}

			permissions = requestId.getAssignedGroup();
			permissions.add(tempInfo);
			requestId.setPermissions(permissions);

			userOverlay.setField(requestId);
		}
		catch (ARException e)
		{
			throw new AREasyException(e);
		}
	}

	protected void createParentField(String formName, int sourceId, int fieldId) throws AREasyException
	{
		try
		{
			CharacterField source = (CharacterField) userOverlay.getField(formName, sourceId);

			String newName = source.getName() + "_parent";

			CharacterField crField = new CharacterField();
			crField.setFieldID(fieldId);
			crField.setForm(formName);
			crField.setName(newName);
			crField.setFieldLimit(source.getFieldLimit());
			crField.setFieldOption(source.getFieldOption());
			crField.setCreateMode(source.getCreateMode());
			crField.setPermissions(source.getAssignedGroup());

			RegularFieldMapping mapInfo = new RegularFieldMapping();
			crField.setFieldMap(mapInfo);

			userOverlay.createField(crField, true);
		}
		catch (ARException e)
		{
			throw new AREasyException(e);
		}
	}

	protected boolean hasPermissionField(String formName, int fieldId)
	{
		try
		{
			userOverlay.getField(formName, fieldId);
		}
		catch (ARException e)
		{
			List<StatusInfo> statuses = userOverlay.getLastStatus();

			for (StatusInfo status : statuses)
			{
				if (status.getMessageNum() == 314L)
				{
					return false;
				}
			}

			RuntimeLogger.error("Unknown error checking permissions for form " + formName + " and field " + fieldId + ": " + e.getMessage());
			return false;
		}

		return true;
	}

	protected ArrayList<String> getUpLink(String formName) throws AREasyException
	{
		ArrayList forms = new ArrayList();

		try
		{
			SQLResult table1 = userOverlay.getListSQL("select resolvedName from arschema where schemaid in (select schemaId from schema_join where memberA='" + formName + "' or memberB='" + formName + "')", 999999999, true);

			for (List row : table1.getContents())
			{
				String temp = ((Value) row.get(0)).toString();
				if (!forms.contains(temp)) forms.add(temp);
			}
		}
		catch(ARException e)
		{
			throw new AREasyException(e);
		}

		return forms;
	}

	protected void fixRecursiveJoinForms(String formName, int sourceId, int parentId) throws AREasyException
	{
		List<String> joinForms = getUpLink(formName);

		for (String joinForm : joinForms)
		{
			if (fixJoinForm(joinForm, formName, sourceId, parentId))
			{
				fixRecursiveJoinForms(joinForm, sourceId, parentId);
			}
		}
	}

	protected boolean fixJoinForm(String formName, String parentForm, int sourceGroupId, int parentGroupId) throws AREasyException
	{
		logger.info("Set hierarchic permission group " + parentGroupId + " for " + formName + " join form name");

		try
		{
			JoinForm form = (JoinForm) userOverlay.getForm(formName);
			boolean isFormOverlayed = false;

			if (!hasJoinPermissionField(formName, parentForm, sourceGroupId))
			{
				RuntimeLogger.error("Form " + formName + " must have source field " + sourceGroupId);
				return false;
			}

			if(parentForm == null)
			{
				Field field = userOverlay.getField(formName, sourceGroupId);

				if ((field.getFieldMap() != null) && (field.getFieldMap().getMappingType() == 2))
				{
					int formindx = ((JoinFieldMapping) field.getFieldMap()).getIndex();
					JoinForm jForm = (JoinForm) userOverlay.getForm(formName);

					if (formindx == 0) parentForm = jForm.getMemberA();
						else parentForm = jForm.getMemberB();
				}

				if(parentForm == null) throw new AREasyException("Can not detect the members of '" + formName + "' join form");
			}

			ObjectPropertyMap props = form.getProperties();
			Value overlay = (Value) props.get(Integer.valueOf(90015));

			if ((overlay != null) && ((overlay.getIntValue() == 2) || (overlay.getIntValue() == 4))) isFormOverlayed = true;

			if (!hasJoinPermissionField(formName, parentForm, parentGroupId))
			{
				if (!isFormOverlayed)
				{
					try
					{
						createFormOverlay(formName);

						form = (JoinForm) userOverlay.getForm(formName);
						props = form.getProperties();
					}
					catch (Exception e)
					{
						RuntimeLogger.error("Failed to create form '" + formName + "' overlay: " + e.getMessage());
						logger.debug("Exception", e);

						return false;
					}
				}

				try
				{
					createJoinParentField(formName, sourceGroupId, parentGroupId);
				}
				catch (Exception e)
				{
					RuntimeLogger.error("Failed to add parent field " + parentGroupId + " to form '" + formName + "' overlay: " + e.getMessage());
					logger.debug("Exception", e);

					return false;
				}
			}

			Value dynamicInheritance = (Value) props.get(Integer.valueOf(60040));
			boolean saveForm = false;

			if (dynamicInheritance == null)
			{
				props.put(Integer.valueOf(60040), new Value(sourceGroupId + ":" + parentGroupId));
				saveForm = true;
			}
			else
			{
				String temp = dynamicInheritance.toString();
				if (!temp.contains(sourceGroupId + ":" + parentGroupId))
				{
					temp = temp + " " + sourceGroupId + ":" + parentGroupId;
					props.put(Integer.valueOf(60040), new Value(temp));

					saveForm = true;
				}
			}

			if (saveForm)
			{
				form.setProperties(props);

				try
				{
					userOverlay.setForm(form);
				}
				catch (ARException e)
				{
					if (e.getMessage().indexOf("ERROR (303):") > -1)
					{
						Value owner = (Value) props.get(Integer.valueOf(90002));

						if ((owner != null) && (owner.toString().equals("BMC:Atrium CMDB")))
						{
							form = (JoinForm) userBase.getForm(formName);
							props = form.getProperties();
							dynamicInheritance = (Value) props.get(Integer.valueOf(60040));
							saveForm = false;

							if (dynamicInheritance == null)
							{
								props.put(Integer.valueOf(60040), new Value(sourceGroupId + ":" + parentGroupId));
								saveForm = true;
							}
							else
							{
								String temp = dynamicInheritance.toString();
								if (!temp.contains(sourceGroupId + ":" + parentGroupId))
								{
									temp = temp + " " + sourceGroupId + ":" + parentGroupId;
									props.put(Integer.valueOf(60040), new Value(temp));
									saveForm = true;
								}
							}

							if (saveForm)
							{
								form.setProperties(props);
								userBase.setForm(form);
							}
						}
						else
						{
							RuntimeLogger.error("Failed to fix Join form '" + formName + "': " + e.getMessage());
							logger.debug("Exception", e);

							return false;
						}
					}
				}
			}

			addRequestIdPermissions(formName, sourceGroupId, parentGroupId);
			return true;
		}
		catch (ARException e)
		{
			RuntimeLogger.error("Failed to fix Join form '" + formName + "': " + e.getMessage());
			logger.debug("Exception", e);
			return false;
		}
	}

	protected boolean hasJoinPermissionField(String formName, String parentForm, int fieldId)
	{
		try
		{
			Field field = userOverlay.getField(formName, fieldId);

			if ((field.getFieldMap() != null) && (field.getFieldMap().getMappingType() == 2))
			{
				if(parentForm != null)
				{
					int formindx = ((JoinFieldMapping) field.getFieldMap()).getIndex();
					int jfieldid = ((JoinFieldMapping) field.getFieldMap()).getFieldID();
					JoinForm jForm = (JoinForm) userOverlay.getForm(formName);

					String tempForm = null;
					if (formindx == 0) tempForm = jForm.getMemberA();
						else tempForm = jForm.getMemberB();

					if ((tempForm.equals(parentForm)) && (jfieldid == fieldId)) return true;

					return false;
				}
				else return true;
			}
			else return false;
		}
		catch (ARException e)
		{
			List<com.bmc.arsys.api.StatusInfo>  statuses = userOverlay.getLastStatus();

			for (StatusInfo status : statuses)
			{
				if (status.getMessageNum() == 314L)
				{
					return false;
				}
			}

			RuntimeLogger.error("Unknown error checking permissions for form " + formName + " and field " + fieldId + ": " + e.getMessage());
			return false;
		}
	}

	protected void createJoinParentField(String formName, int sourceId, int fieldId) throws AREasyException
	{
		try
		{
			CharacterField source = (CharacterField) userOverlay.getField(formName, sourceId);
			String newName = source.getName() + "_parent";

			CharacterField crField = new CharacterField();
			crField.setFieldID(fieldId);
			crField.setForm(formName);
			crField.setName(newName);
			crField.setFieldLimit(source.getFieldLimit());
			crField.setFieldOption(source.getFieldOption());
			crField.setCreateMode(source.getCreateMode());

			JoinFieldMapping mapInfo = (JoinFieldMapping) source.getFieldMap();
			mapInfo.setFieldID(fieldId);
			crField.setFieldMap(mapInfo);

			userOverlay.createField(crField, true);
		}
		catch (ARException e)
		{
			throw new AREasyException(e);
		}
	}

	protected void createFormOverlay(String formName) throws AREasyException
	{
		try
		{
			OverlaidInfo baseObj = new OverlaidInfo();
			baseObj.setName(formName);
			baseObj.setObjType(1);

			userOverlay.createOverlay(baseObj);
		}
		catch (ARException e)
		{
			throw new AREasyException(e);
		}
	}

	protected void createFieldOverlay(String formName, String fieldName, int fieldId) throws AREasyException
	{
		try
		{
			OverlaidInfo info = new OverlaidInfo();
			info.setName(fieldName);
			info.setFormName(formName);
			info.setId(fieldId);
			info.setObjType(15);

			userOverlay.createOverlay(info);
		}
		catch (ARException e)
		{
			throw new AREasyException(e);
		}
	}

	protected void createGroup(int sourceGroupId, int parentGroupId) throws AREasyException
	{
		int groupNameId = 105;
		int groupIdId = 106;
		int groupLongName = 8;

		int groupCategory = 120;
		int groupTypeId = 107;
		int tempGroupId = 7;

		if (sourceGroupId != 112)
		{
			tempGroupId = sourceGroupId;
		}

		try
		{
			QualifierInfo qual = userOverlay.parseQualification("Group", "'106'=" + tempGroupId + " OR '106'=" + parentGroupId);
			List entries = userOverlay.getListEntryObjects("Group", qual, 0, 2, null, new int[]{groupNameId, groupIdId, groupTypeId, groupLongName, groupCategory}, false, null);

			if (entries.size() > 1) return;

			if (entries.size() == 0) return;

			Entry entry = (Entry) entries.get(0);

			if (((Value) entry.get(Integer.valueOf(groupIdId))).getIntValue() != tempGroupId) return;

			entry.put(Integer.valueOf(groupNameId), new Value(parentGroupId));
			entry.put(Integer.valueOf(groupIdId), new Value(parentGroupId));
			entry.put(Integer.valueOf(groupLongName), new Value(parentGroupId));

			userOverlay.createEntry("Group", entry);
		}
		catch (ARException e)
		{
			throw new AREasyException(e);
		}
	}

	protected boolean validateGroupId(int groupId, boolean isSource)
	{
		if ((isSource) && (groupId == 112))
		{
			return true;
		}

		if ((groupId >= 60000) && (groupId <= 69999))
		{
			return true;
		}

		return false;
	}

	protected ArrayList<String> getApplicationFormNames(ARServerUser server, String applicationName)
	{
		ArrayList list = new ArrayList();

		try
		{
			ApplicationContainer cmdbApp = (ApplicationContainer) server.getContainer(applicationName);
			List references = cmdbApp.getReferences();
			int size = references.size();

			for (int iLoop = 0; iLoop < size; iLoop++)
			{
				Reference reference = (Reference) references.get(iLoop);
				if (reference.getReferenceType().equals(ReferenceType.APPLICATION_FORMS))
				{
					if (size > iLoop + 1)
					{
						String formName = null;
						Reference formRef = (Reference) references.get(++iLoop);

						if (formRef.getReferenceType().equals(ReferenceType.SCHEMA))
						{
							formName = formRef.getName();
						}

						if (formName != null)
						{
							list.add(formName);
						}
					}
				}
			}
		}
		catch (ARException e)
		{
			logger.warn("List of CMDB forms not retrieved from 'BMC:Atrium CMDB' application: " + e.getMessage());
		}

		return list;
	}

	protected ArrayList<String> getFormsListFromOBJSTRClass(ARServerUser serverUser, ArrayList excluded)
	{
		int NAMESPACE = 400109900;
		int CLASS_NAME = 490001100;

		ArrayList list = new ArrayList();

		try
		{
			QualifierInfo qualification = serverUser.parseQualification("OBJSTR:Class", "1=1");
			List<com.bmc.arsys.api.Entry> entries = serverUser.getListEntryObjects("OBJSTR:Class", qualification, 0, 0, null, new int[]{NAMESPACE, CLASS_NAME}, false, new OutputInteger());

			for (Entry entry : entries)
			{
				String namespace = ((Value) entry.get(new Integer(NAMESPACE))).toString();
				String className = ((Value) entry.get(new Integer(CLASS_NAME))).toString();
				if ((namespace != null) && (className != null))
				{
					String formName = namespace.trim() + ":" + className.trim();

					if (!excluded.contains(formName))
					{
						try
						{
							serverUser.getForm(formName);
							list.add(formName);
						}
						catch (ARException localARException1)
						{
							logger.warn("Form '" + formName + "' could not be identified");
						}
					}
				}
			}
		}
		catch (ARException e)
		{
			logger.warn("Error occurred while querying 'OBJSTR:Class' CMDB form: " + e.getMessage());
		}

		return list;
	}

	protected ArrayList<String> getDynamicallyAddedSLMForms(ARServerUser server, ArrayList excluded)
	{
		ArrayList list = new ArrayList();

		try
		{
			List<String> formNames = server.getListForm();

			for (String formName : formNames)
			{
				if (formName.endsWith("_SLA") && !excluded.contains(formName))
				{
					list.add(formName);
				}
			}
		}
		catch (ARException e)
		{
			logger.warn("Failed while finding dynamically added SLM Forms: " + e.getMessage());
		}

		return list;
	}

	protected ArrayList<String> getCMDBSelfJoinForms(ARServerUser server, ArrayList<String> excluded)
	{
		ArrayList list = new ArrayList();

		try
		{
			List<String> joinForms = server.getListJoinForm(0L);

			for (String joinFormName : joinForms)
			{
				if (!excluded.contains(joinFormName))
				{
					JoinForm joinForm = (JoinForm) server.getForm(joinFormName);

					String memberA = joinForm.getMemberA();
					String memberB = joinForm.getMemberB();

					if ((memberA.equalsIgnoreCase(memberB)) && (excluded.contains(memberA)) && (excluded.contains(memberB)))
					{
						list.add(joinFormName);
					}
				}
			}
		}
		catch (ARException e)
		{
			logger.warn("Failed while finding CMDB Self Join Forms: " + e.getMessage());
		}

		return list;
	}

	protected List getExclusionList(ARServerUser serverUser) throws AREasyException
	{
		ArrayList excluded = new ArrayList();

		excluded.addAll(getApplicationFormNames(serverUser, "BMC:Atrium CMDB"));
		excluded.addAll(getFormsListFromOBJSTRClass(serverUser, excluded) );
		logger.info("Number of CMDB forms = " + excluded.size());

		excluded.addAll(getDynamicallyAddedSLMForms(serverUser, excluded));
		logger.info("Number of dynamically added SLM forms and CMDB: " + excluded.size());

		return excluded;
	}
}
