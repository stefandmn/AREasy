package org.areasy.runtime.actions.arserver.admin;

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

import org.areasy.runtime.actions.arserver.data.BaseDataAction;
import org.areasy.runtime.engine.RuntimeLogger;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.runtime.engine.structures.Attribute;
import org.areasy.runtime.engine.structures.CoreItem;
import org.areasy.runtime.engine.workflows.ProcessorLevel2CmdbApp;
import org.areasy.common.data.NumberUtility;
import org.areasy.common.data.StringUtility;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * This is a dedicated action used to import service target objects in Service Level Management application based on an Excel file.
 * Additionally, this action is able to manipulate service target templates or service target component templates like (measurements milestones, etc.)
 * During creation or update process the action could trigger SVT building process.
 * @TODO - import of agreements and correlation between agreements and service targets
 */
public class ServiceTargetAdministration extends BaseDataAction
{
	boolean agreement = false;

	/**
	 * Execute the current action.
	 * @throws org.areasy.runtime.engine.base.AREasyException if any error will occur
	 */
	public void run() throws AREasyException
	{
		agreement = getConfiguration().getBoolean("agreement", false);;
		CoreItem entry = new CoreItem(!agreement ? "SLM:ServiceTarget" : "SLM:SLADefinition");

		if(!getConfiguration().containsKey("qualification"))
		{
			setQueryFields(entry);
			entry.read(getServerConnection());

			run(entry);
		}
		else
		{
			String qualification = getTranslatedQualification(getConfiguration().getString("qualification", null));
			List entries = entry.search(getServerConnection(), qualification);

			for (int i = 0; entries != null && i < entries.size(); i++)
			{
				CoreItem item = (CoreItem) entries.get(i);

				try
				{
					run(item);
				}
				catch(Throwable e)
				{
					RuntimeLogger.error("Error processing '" + item.getAttribute(300314700) + "' service entity: " + e.getMessage());
					logger.debug("Exception", e);
				}
			}
		}
	}

	/**
	 * Execute an action for a specific <code>CoreItem</code>. This item must be identified previously and then the method
	 * could be called. This method will used by standard actions which implement an workflow using these type of action
	 * which permit single change or update.
	 *
	 * @param entry <code>CoreItem</code> structure, which should be instantiated.
	 * @throws org.areasy.runtime.engine.base.AREasyException if any error will occur.
	 */
	public void run(CoreItem entry) throws AREasyException
	{
		boolean template = getConfiguration().getBoolean("istemplate", false);
		boolean build = getConfiguration().getBoolean("build", false);

		if(template && !agreement) build = false;

		//set data values
		setDataFields(entry);

		//start building
		if(build)
		{
			entry.setAttribute(300543500, "Build In Progress");	//BuildStatus
		}

		if(!entry.exists() && !agreement)
		{
			entry.setAttribute(300905300, new Integer(0)); //GoalTypes
			entry.setAttribute(300431700, new Integer(0)); //UseGoalCostSelection
			entry.setAttribute(301267400, ProcessorLevel2CmdbApp.getStringInstanceId(getServerConnection(), "GO")); //GoalGUID

			//Set SLM Goal Schedule -- todo check if this operation is not done automatically by the workflow (over filters).
			setGoalSchedule(entry);
		}

		if(!entry.exists())
		{
			//commit service target
			entry.create(getServerConnection());
			RuntimeLogger.info((!agreement ? "Service target" : "Service agreement") + " '" + entry.getAttribute(300314700) + "' created" + (build ? " and built" : "") + ": " + entry);
		}
		else
		{
			//commit service target
			entry.update(getServerConnection());
			RuntimeLogger.info((!agreement ? "Service target" : "Service agreement") + " '" + entry.getAttribute(300314700) + "' updated" + (build ? " and built" : "") + ": " + entry);
		}

		//wait to build it, to start execution for next SVT.
		if(build) waitToBuild(entry);
	}

	/**
	 * Collect query input parameters and publish them into a <code>CoreItem</code> structure.
	 *
	 * @param entry <code>CoreItem</code> structure.
	 * @return true if at least one corresponding configuration data has been used to map a <code>CoreItem</code> attribute.
	 * @throws AREasyException is any error will occur
	 */
	public boolean setQueryFields(CoreItem entry) throws AREasyException
	{
		boolean set = super.setQueryFields(entry);
		set |= setAttribute(entry, "slmid", 300314700);

		return set;
	}

	/**
	 * Collect data input parameters and publish them into a <code>CoreItem</code> structure.
	 *
	 * @param entry <code>CoreItem</code> structure.
	 * @return true if at least one corresponding configuration data has been used to map a <code>CoreItem</code> attribute.
	 * @throws AREasyException is any error will occur
	 */
	public boolean setDataFields(CoreItem entry) throws AREasyException
	{
		boolean template = getConfiguration().getBoolean("istemplate", false);
		boolean set = super.setDataFields(entry);

		//if it's a new SVT create a new instanceId and take the next SLM ID
		if(!entry.exists())
		{
			set = true;
			String instanceId = ProcessorLevel2CmdbApp.getStringInstanceId(getServerConnection(), "SL");
			entry.setAttribute(179, instanceId);

			if(getConfiguration().containsKey("slmid"))
			{
				//set SLMID as is defined in the input parameters
				setAttribute(entry, "slmid", 300314700);
			}
			else
			{
				String slmId = getServiceTargetId(instanceId);
				entry.setAttribute(300314700, slmId);
			}

			if(!agreement)
			{
				//mark it as template
				if(template) entry.setAttribute(301268702, new Integer(0));

				//if have to be created a new SVT and has template specified, apply template parameters
				if(getConfiguration().containsKey("template"))
				{
					//run a procedure that put all the details that come from a SVT template
					applyServiceTargetTemplate(getConfiguration().getString("template"), entry);
				}
			}
		}

		//set individual parameters
		set |= setAttribute(entry, "title", 490000400);
		set |= setAttribute(entry, "description", 300260300);
		set |= setAttribute(entry, "status", 300314900);
		set |= setAttribute(entry, "agreementtype", 300260900);

		if(!agreement)
		{
			set |= setAppliedToAttribute(entry, "appliedto");
			set |= setGoalTypeAttribute(entry, "goaltype");
			set |= setAttribute(entry, "efectivefrom", 300272100);
			set |= setAttribute(entry, "termsandconditions", 300271400);
			set |= setIntegerAttribute(entry, "singlegoalhours", 300397900);
			set |= setIntegerAttribute(entry, "singlegoalminutes", 300398100);
			set |= setBusinessEntityAttribute(entry, "businessentity");
			set |= setAttribute(entry, "usegoalonapp", 301339900);
			set |= setAttribute(entry, "usestarttimeonapp", 301563900);
			set |= setAttribute(entry, "measurementdescription", 301648900);
			set |= setAttribute(entry, "measurementstartwhen", 300273000);
			set |= setAttribute(entry, "measurementstopwhen", 300273100);
			set |= setAttribute(entry, "measurementexclude", 300273200);
			set |= setFloatAttribute(entry, "measurementwarningat", 300413800, true);
			set |= setAttribute(entry, "measurementgroup", 300478200, true);
			set |= setAttribute(entry, "measurementresetgoal", 301473300);
			set |= setAttribute(entry, "measurementreopen", 303384400);
		}
		else
		{
			set |= setAttribute(entry, "serviceci", 303497300);
			set |= setAttribute(entry, "expirationdate", 240001000);
			set |= setAttribute(entry, "notificationdate", 240001015);
		}

		//validate and fix title value
		if(entry.getStringAttributeValue(490000400) != null && entry.getStringAttributeValue(490000400).length() > 80)
		{
			RuntimeLogger.warn("Title parameter is too large and it will be truncated to 40 chars");
			entry.setAttribute(490000400, entry.getStringAttributeValue(490000400).substring(0, 79));
		}

		//template and non-template details.
		if(template && !agreement)
		{
			entry.setAttribute(301307201, "No");
			entry.setAttribute(300543500, "Template");
			entry.setAttribute(301268701, "Template");
		}

		return set;
	}

	/**
	 * This method returns the next SLM ID in order to register a new service target. The algorithm implemented here
	 * is similar with the Remedy procedure designed over <code>SLM:RegisterSVTID</code> form.
	 *
	 * @param instanceId in order to issue a new SVT ID you have to say for what SVT instance id will be created it
	 * @return next SLM ID value to create a new service target.
	 * @throws AREasyException in case of any error occurs
	 */
	protected String getServiceTargetId(String instanceId) throws AREasyException
	{
		String slmId = null;

		if(StringUtility.isEmpty(instanceId)) throw new AREasyException("SVT ID couldn't be created because InstanceId is null");

		CoreItem config  = new CoreItem("SLM:ConfigPreferences");
		config.setAttribute(8, "Not Used");
		config.read(getServerConnection());

		if(config.exists())
		{
			String Prefix = config.getStringAttributeValue(300594200);
			Integer IncrementBy = (Integer) config.getAttributeValue(300400900);

			CoreItem item  = new CoreItem("SLM:RegisterSVTID");
			item.setAttribute(2, getServerConnection().getUserName());
			item.setAttribute(4, "Not Used");
			item.setAttribute(7, 0);
			item.setAttribute(8, "Not Used");
			item.setAttribute(179, instanceId);
			item.setAttribute(300400900, IncrementBy);
			item.setAttribute(300594200, Prefix);
			item.create(getServerConnection());

			if(item.exists())
			{
				String entryId = item.getEntryId();
				entryId = entryId.substring(entryId.length() - 5);

				Integer newId = IncrementBy + NumberUtility.createInteger(entryId);
				slmId = Prefix + StringUtility.leftPad(String.valueOf(newId), 5, "0");
			}
			else RuntimeLogger.warn("SVT ID couldn't be registered");
		}
		else RuntimeLogger.warn("No configuration record found in SLM Preferences form");

		return slmId;
	}

	private boolean setIntegerAttribute(CoreItem entry, String fieldKey, int fieldId, boolean ignorenull)
	{
		if(getConfiguration().containsKey(fieldKey))
		{
			Integer value = getConfiguration().getInteger(fieldKey, null);

			if(ignorenull)
			{
				if(value == null) entry.setAttribute(fieldId, value);
			}
			else entry.setAttribute(fieldId, value);

			return true;
		}
		else return false;
	}

	private boolean setIntegerAttribute(CoreItem entry, String fieldKey, int fieldId)
	{
		return setIntegerAttribute(entry,fieldKey, fieldId, false);
	}

	private boolean setFloatAttribute(CoreItem entry, String fieldKey, int fieldId, boolean ignorenull)
	{
		if(getConfiguration().containsKey(fieldKey))
		{
			Float value = getConfiguration().getFloat(fieldKey, null);

			if(ignorenull)
			{
				if(value == null) entry.setNullAttribute(fieldId);
					else entry.setAttribute(fieldId, value);
			}
			else entry.setAttribute(fieldId, value);

			return true;
		}
		else return false;
	}

	private boolean setFloatAttribute(CoreItem entry, String fieldKey, int fieldId)
	{
		return setIntegerAttribute(entry,fieldKey, fieldId, false);
	}

	private boolean setAttribute(CoreItem entry, String fieldKey, int fieldId, boolean ignorenull)
	{
		if(getConfiguration().containsKey(fieldKey))
		{
			String value = getConfiguration().getString(fieldKey, null);

			if(ignorenull)
			{
				if(StringUtility.isNotEmpty(value)) entry.setAttribute(fieldId, value);
			}
			else entry.setAttribute(fieldId, value);

			return true;
		}
		else return false;
	}

	private boolean setAttribute(CoreItem entry, String fieldKey, int fieldId)
	{
		return setAttribute(entry, fieldKey, fieldId, false);
	}

	private boolean setAppliedToAttribute(CoreItem entry, String key) throws AREasyException
	{
		if((key != null && getConfiguration().containsKey(key)) || key == null)
		{
			String value = null;

			if(key == null) value = entry.getStringAttributeValue(300523400);
				else value = getConfiguration().getString(key);

			entry.setAttribute(300523400, value);											//AppliesTo

			CoreItem item = new CoreItem("SLM:ConfigDataSource");
			item.setAttribute(300520600, value);											//AppliesTo
			item.read(getServerConnection());

			if(item.exists())
			{
				entry.setAttribute(303699500, item.getStringAttributeValue(300260400));		//DataSourceInstanceID
				entry.setAttribute(303484900, item.getStringAttributeValue(303484900));		//FieldContainingOLAEntityFieldID
				entry.setAttribute(301576100, item.getAttributeValue(301576100));			//ServiceTargetExecutionOrder
				entry.setAttribute(301576000, item.getStringAttributeValue(301576000));		//QualBuilderFormName
				entry.setAttribute(301472900, item.getStringAttributeValue(301472900));		//RestartWhen
				entry.setAttribute(301413300, item.getStringAttributeValue(301413300));		//FieldContainingEntityFieldID
				entry.setAttribute(301387500, item.getAttributeValue(301374100));			//ReferenceEndGoalforRequestBasedSVTsSel
				entry.setAttribute(301387400, item.getStringAttributeValue(301387400));		//ReferenceEndGoalforRequestBasedSVTsFieldID
				entry.setAttribute(301362600, item.getStringAttributeValue(301362600));		//ReferenceTimeGoalforRequestBasedSVTsFieldID
				entry.setAttribute(301270300, item.getStringAttributeValue(301270300));		//FieldUsedforGroupAccessFieldID
				entry.setAttribute(301270100, item.getStringAttributeValue(301270100));		//StartTimeforRequestBasedSVTsFieldID
				entry.setAttribute(301187000, item.getStringAttributeValue(301186700));		//zD_AccessGroupLookupForm - displayonly
				entry.setAttribute(301172900, item.getStringAttributeValue(301172500));		//zD_FieldUsedforGroupAccess - displayonly
				entry.setAttribute(301172700, item.getAttributeValue(301172400));			//zD_IsYourApplicationUsingSharedServices - displayonly
				entry.setAttribute(300695300, item.getAttributeValue(300695300));			//SLA_Appform_Applicable Type
				entry.setAttribute(300504800, item.getStringAttributeValue(300504800));		//FieldContainingHolidayScheduleFieldID
				entry.setAttribute(300504700, item.getStringAttributeValue(300504700));		//FieldContainingBusinessHoursFieldID
				entry.setAttribute(300504600, item.getStringAttributeValue(300504600));		//SLMConfDSFieldAsUniqueIdentifierFieldID
				entry.setAttribute(300421200, item.getStringAttributeValue(303119300));		//zD_TempInt1 - displayonly
				entry.setAttribute(300260400, item.getStringAttributeValue(300260400));		//ReferenceFormID
				entry.setAttribute(300260200, item.getStringAttributeValue(300260200));		//DataSourceName

				return true;
			}
			else
			{
				RuntimeLogger.warn("SLM Data-Source not found for identifier: " + key);
				return false;
			}
		}
		else return false;
	}

	private boolean setGoalTypeAttribute(CoreItem entry, String key) throws AREasyException
	{
		if((key != null && getConfiguration().containsKey(key)) || key == null)
		{
			String value = null;

			if(key == null) value = entry.getStringAttributeValue(300315600);
				else value = getConfiguration().getString(key);

			entry.setAttribute(300315600, value);											//SLMGoalType

			CoreItem item = new CoreItem("SLM:ConfigGoalType");
			item.setAttribute(8, value);
			item.read(getServerConnection());

			if(item.exists())
			{
				entry.setAttribute(301419600, item.getAttributeValue(301419600));			//HelpDeskERDFlag
				entry.setAttribute(300905300, item.getAttributeValue(300905300));			//GoalTypes

				return true;
			}
			else
			{
				RuntimeLogger.warn("SLM Goal Type not found for identifier: " + key);
				return false;
			}
		}
		else return false;
	}

	private boolean setBusinessEntityAttribute(CoreItem entry, String key) throws AREasyException
	{
		if((key != null && getConfiguration().containsKey(key)) || key == null)
		{
			String value = null;

			if(key == null) value = entry.getStringAttributeValue(300830700);
				else value = getConfiguration().getString(key);

			if(StringUtility.isNotEmpty(value))
			{
				entry.setAttribute(300830700, value);											//BusinessEntityTag

				CoreItem item = new CoreItem("Business Time Shared Entity");
				item.setAttribute(8, value);
				item.read(getServerConnection());

				if(item.exists())
				{
					entry.setAttribute(301413200, item.getAttributeValue(179));			//BusinessEntityID
					entry.setAttribute(300830700, item.getAttributeValue(8));			//BusinessEntityTag

					return true;
				}
				else
				{
					RuntimeLogger.warn("SLM Business Entity not found for identifier: " + key);
					entry.setNullAttribute(301413200);
					entry.setNullAttribute(300830700);

					return false;
				}
			}
			else return false;
		}
		else
		{
			entry.deleteAttribute(301413200);
			entry.deleteAttribute(300830700);

			return false;
		}
	}

	protected boolean applyServiceTargetTemplate(String templateName, CoreItem entry) throws AREasyException
	{
		if(StringUtility.isEmpty(templateName)) return false;

		CoreItem template = new CoreItem("SLM:ServiceTarget");
		template.setAttribute(301268702, new Integer(0));
		template.setAttribute(490000400, templateName);
		template.read(getServerConnection());

		if(template.exists())
		{
			//default value when the template is selected
			entry.setAttribute(301874800, template.getStringAttributeValue(300314700));  	// TemplateSLMID
			entry.setAttribute(300525700, template.getStringAttributeValue(179));  			// SVTTemplateMenu
			entry.setAttribute(300338100, template.getStringAttributeValue(179)); 			//SVTTemplateInstanceID
			entry.setAttribute(301322801, template.getStringAttributeValue(490000400)); 	//SVTTemplateName
			entry.setAttribute(490000400, template.getStringAttributeValue(490000400) + "_" + entry.getStringAttributeValue(300314700)); //new Title
			entry.setAttribute(301267400, ProcessorLevel2CmdbApp.getStringInstanceId(getServerConnection(), "GO")); //goalGUID

			template.deleteAttribute(179);			//instance id
			template.deleteAttribute(300525700);	//template instance id
			template.deleteAttribute(301322801);	//template name
			template.deleteAttribute(490000400);    //title
			template.deleteAttribute(300314700);    //SLMId
			template.deleteAttribute(301268702);    //template mark
			template.deleteAttribute(301267400);    //GoalGUID
			template.deleteAttribute(301307201);    //BMCSVTTemplate
			template.deleteAttribute(300543500);	//BuildStatus
			template.deleteAttribute(301268701);	//ObjectTemplateType
			template.deleteAttribute(300338100);	//SVTTemplateInstanceID
			template.deleteAttribute(301874800);	//TemplateSLMID
			template.deleteAttribute(3);
			template.deleteAttribute(5);
			template.deleteAttribute(6);
			template.deleteAttribute(7);

			Collection collection = template.getAttributes();
			if(collection != null && !collection.isEmpty())
			{
				Iterator iterator = collection.iterator();

				while(iterator != null && iterator.hasNext())
				{
					Attribute attr = (Attribute) iterator.next();
					if(attr != null && attr.getValue() != null) entry.setAttribute(attr.getNumberId(), attr.getValue());
				}
			}
		}
		else RuntimeLogger.warn("SLM Template not found for identifier: " + template);

		entry.setNullAttribute(301307201);					//BMCSVTTemplate
		entry.setAttribute(300543500, "Need To Be Built");	//BuildStatus
		entry.setAttribute(301268701, "Custom");			//ObjectTemplateType

		//set AppliedTo
		setAppliedToAttribute(entry, null);

		//set GoalType
		setGoalTypeAttribute(entry, null);

		//create Base Clone
		registerSLMCloneBase(entry);

		//find milestone templates and trigger instance creation
		CoreItem search = new CoreItem("SLM:Milestone");
		search.setAttribute(300394800, entry.getStringAttributeValue(300338100));
		List list = search.search(getServerConnection());

		for(int i = 0; list != null && i < list.size(); i++)
		{
			CoreItem milestone = (CoreItem) list.get(i);

			milestone.setAttribute(300397800, entry.getStringAttributeValue(301874800));	//ParentSLMId
			milestone.setAttribute(300398700, entry.getStringAttributeValue(301322801));	//SVT_Title
			milestone.setAttribute(300525800, entry.getStringAttributeValue(179));			//zD_ParentCloneID
			milestone.setAttribute(301412100, entry.getStringAttributeValue(300314700));	//zD_SLMIDforSVTTemplate_Mile
			milestone.setAttribute(303737002, "YES");										//zD_FromSVTTemplate

			milestone.update(getServerConnection());
		}

		//remove clone base
		maskAsDeleteSLMCloneBase();

		return true;
	}

	protected void setGoalSchedule(CoreItem entry) throws AREasyException
	{
		CoreItem goal = new CoreItem("SLM:GoalSchedule");
		goal.setAttribute(301267400, entry.getAttributeValue(301267400));
		goal.read(getServerConnection());

		goal.setAttribute(300365600, "12:00:01 AM");
		goal.setAttribute(301263600, "11:59:59 PM");
		goal.setAttribute(300431700, new Integer(0));
		goal.setAttribute(300462600, entry.getAttributeValue(300462600));
		goal.setAttribute(301263400, new Integer(7));	//Default - value
		goal.setAttribute(301263700, new Integer(7));
		goal.setAttribute(301263800, new Integer(7));
		goal.setAttribute(301267400, entry.getAttributeValue(301267400));
		goal.setAttribute(490000400, "Single -" + entry.getStringAttributeValue(300314700));

		if(goal.exists()) goal.update(getServerConnection());
			else goal.create(getServerConnection());
	}

	protected void registerSLMCloneBase(CoreItem entry) throws AREasyException
	{
		CoreItem clone = new CoreItem("SLM:CloneBase");

		clone.setAttribute(300519200, entry.getStringAttributeValue(300338100));
		clone.setAttribute(300519300, "SLM_SLODEFINITION");
		clone.setAttribute(300537700, entry.getStringAttributeValue(301322801));
		clone.setAttribute(301241900, entry.getStringAttributeValue(300314700));
		clone.setAttribute(301242300, entry.getStringAttributeValue(179));
		clone.setAttribute(303737001, "YES");

		clone.create(getServerConnection());
	}

	protected void maskAsDeleteSLMCloneBase() throws AREasyException
	{
		CoreItem search = new CoreItem("SLM:CloneBase");
		search.setAttribute(490000200, new Integer(0));

		List list = search.search(getServerConnection());

		for(int i = 0; list != null && i < list.size(); i++)
		{
			CoreItem clone = (CoreItem) list.get(i);

			clone.setAttribute(490000200, "true");
			clone.update(getServerConnection());
		}
	}

	protected void applyMeasurementTemplate(String template, CoreItem entry)
	{

	}

	protected void addMilestoneTemplate(CoreItem entry)
	{

	}

	protected void runBuild(CoreItem svt)
	{

	}

	private void waitToBuild(CoreItem entry) throws AREasyException
	{
		if(getConfiguration().getBoolean("waittobuild", false))
		{
			int waitCount = 0;
			boolean wait = true;
			String instanceId = entry.getStringAttributeValue(179);

			do
			{
				CoreItem svt = new CoreItem(!agreement ? "SLM:ServiceTarget" : "SLM:SLADefinition");
				svt.setAttribute(179, instanceId);
				svt.read(getServerConnection());

				if(svt.exists())
				{
					Integer status = (Integer) svt.getAttributeValue(300543500);

					if(status == 0)
					{
						wait = true;
						waitCount++;
					}
					else if(status == 1)
					{
						wait  = false;
						logger.warn("Build failed for service entity " + svt.getAttribute(300314700));
					}
					else if(status == 2)
					{
						wait  = false;
						logger.debug("Build successfully for service entity " + svt.getAttribute(300314700));
					}
					else if(status >= 3)
					{
						wait  = false;
					}
				}
				else
				{
					wait  = false;
					RuntimeLogger.warn((!agreement ? "Service target" : "Service agreement") + " couldn't be identified after commit: " + entry);
				}

				if(waitCount > 300)
				{
					logger.warn("Waiting too long (more than 5m) to build the service entity (target or agreement): " + svt);
				}

				if(wait)
				{
					try
					{
						int sleep = getConfiguration().getInt("buildsleep", 1000);
						if(sleep < 1000) sleep *= 1000;
						if(sleep < 1) sleep = 1000;

						Thread.sleep(sleep);
					}
					catch(InterruptedException e) { /** nothing to do */ }
				}
			}
			while(wait);
		}
	}
}
