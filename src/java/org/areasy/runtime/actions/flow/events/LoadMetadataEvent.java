package org.areasy.runtime.actions.flow.events;

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

import com.bmc.arsys.api.ARException;
import com.bmc.arsys.api.SQLResult;
import com.bmc.arsys.api.Value;
import org.areasy.common.data.StringUtility;
import org.areasy.common.support.configuration.Configuration;
import org.areasy.runtime.engine.RuntimeLogger;
import org.areasy.runtime.engine.base.ARDictionary;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.runtime.engine.structures.CoreItem;

import java.util.List;
import java.util.Vector;

/**
 * This is a dedicated AAR event to read and push AR System and AAR entities (form, fields and options)
 * into Metadata forms.
 */
public class LoadMetadataEvent extends AbstractEvent
{
	/**
	 * Execute event
	 *
	 * @throws org.areasy.runtime.engine.base.AREasyException if any error occurs
	 */
	public void execute() throws AREasyException
	{
		//disable notification
		getAction().getConfiguration().setKey("notification", "false");

		//remove existing metadata
		resetMetadata();

		//add entities metadata
		setEntitiesMetadata();

		//add field attributes metadata
		setOptionAttributesMetadata();
	}

	/**
	 * Remove all records that corresponds to the current user and to the current data-source from "SNT:AAR:DMI:Source Selection" form.
	 *
	 * @throws org.areasy.runtime.engine.base.AREasyException is any error occur
	 */
	protected void resetMetadata() throws AREasyException
	{
		String query = null;
		CoreItem entitiesSearch = new CoreItem();
		entitiesSearch.setFormName(ARDictionary.FORM_METADATA_ENTITIES);

		if(getAction().getConfiguration().containsKey("entities"))
		{
			List dataEntities = getAction().getConfiguration().getList("entities", null);
			String entitiesFlag = dataEntities != null ? (String) dataEntities.get(0) : "";

			if(StringUtility.equals(entitiesFlag, "dataforms")) query = "'536870918' = 1";
			else if(StringUtility.equals(entitiesFlag, "joinforms")) query = "'536870918' = 2";
			else if(StringUtility.equals(entitiesFlag, "viewforms")) query = "'536870918' = 3";
			else if(StringUtility.equals(entitiesFlag, "aaroptions")) query = "'536870918' > 100";
			else if(dataEntities != null && dataEntities.size() > 0)
			{
				for(int i = 0; i < dataEntities.size(); i++)
				{
					String entity = (String) dataEntities.get(i);

					if(i == 0) query = "('536870917' = \"" + entity + "\"";
						else query += " OR '536870917' = \"" + entity + "\"";

					if(i == dataEntities.size() - 1) query += ")";
				}
			}
		}

		getAction().getCurrentStatus().setExecMessage("Data action removed {1} entities");
		List entities = entitiesSearch.search(getAction().getServerConnection(), query);

		for(int i = 0; entities != null && i < entities.size(); i++)
		{
			CoreItem item = (CoreItem) entities.get(i);
			item.setAttribute(8, "DELETE");

			item.update(getAction().getServerConnection());
			getAction().setRecordsCounter();
		}

		query = null;
		CoreItem attributesSearch = new CoreItem();
		attributesSearch.setFormName(ARDictionary.FORM_METADATA_ATTRIBUTES);

		if(getAction().getConfiguration().containsKey("entities"))
		{
			List dataEntities = getAction().getConfiguration().getList("entities", new Vector());
			String entitiesFlag = dataEntities != null ? (String) dataEntities.get(0) : "";

			if(StringUtility.equals(entitiesFlag, "dataforms")) query = "'536870919' = 1";
			else if(StringUtility.equals(entitiesFlag, "joinforms")) query = "'536870919' = 2";
			else if(StringUtility.equals(entitiesFlag, "viewforms")) query = "'536870919' = 3";
			else if(StringUtility.equals(entitiesFlag, "aaroptions")) query = "'536870919' > 100";
			else if(dataEntities != null && dataEntities.size() > 0)
			{
				for(int i = 0; i < dataEntities.size(); i++)
				{
					String entity = (String) dataEntities.get(i);

					if(i == 0) query = "('536870918' = \"" + entity + "\"";
						else query += " OR '536870918' = \"" + entity + "\"";

					if(i == dataEntities.size() - 1) query += ")";
				}
			}
		}

		List attributes = attributesSearch.search(getAction().getServerConnection(), query);

		for(int i = 0; attributes != null && i < attributes.size(); i++)
		{
			CoreItem item = (CoreItem) attributes.get(i);
			item.setAttribute(8, "DELETE");

			item.update(getAction().getServerConnection());
		}

		RuntimeLogger.info("Existing entities and attributes have been removed");
		getAction().resetCounters();
		getAction().getCurrentStatus().resetExecMessage();
	}

	protected void setEntitiesMetadata() throws AREasyException
	{
		boolean cmdbEnabled = false;
		String query = "SELECT a.schemaId FROM arschema a WHERE a.name = 'SHR:SchemaNames'";

		try
		{
		    SQLResult result = getAction().getServerConnection().getContext().getListSQL(query, 1, true);
			if(result.getTotalNumberOfMatches() > 0) cmdbEnabled = true;
		}
		catch(ARException are)
		{
			logger.error("Error checking if 'SHR:SchemaNames' exists: " + are.getMessage());
			logger.debug("Exception", are);
		}

		String whereClause = "a.schemaType < 4";

		if(getAction().getConfiguration().containsKey("entities"))
		{
			List dataEntities = getAction().getConfiguration().getList("entities", new Vector());
			String entitiesFlag = dataEntities != null ? (String) dataEntities.get(0) : "";

			if(StringUtility.equals(entitiesFlag, "dataforms")) whereClause = "a.schemaType = 1";
			else if(StringUtility.equals(entitiesFlag, "joinforms")) whereClause = "a.schemaType = 2";
			else if(StringUtility.equals(entitiesFlag, "viewforms")) whereClause = "a.schemaType = 3";
			else if(StringUtility.equals(entitiesFlag, "aaroptions")) whereClause = "a.schemaType > 100";
			else if(dataEntities != null && dataEntities.size() > 0)
			{
				for(int i = 0; i < dataEntities.size(); i++)
				{
					String entity = (String) dataEntities.get(i);

					if(i == 0) whereClause += " AND (a.resolvedName = '" + entity + "'";
						else whereClause += " OR a.resolvedName = '" + entity + "'";

					if(i == dataEntities.size() - 1) whereClause += ")";
				}
			}
		}

		if(cmdbEnabled)
		{
			query = "SELECT DISTINCT a.schemaId formId, a.resolvedName formName, a.name derivedName, a.schemaType formType, b.Lookup_Keyword classKeyword, b.zIntClassType classType, b.Proper_Name classProperName, b.OSSchemaName classOSSchemaName \n" +
					"FROM arschema a left outer join SHR_SchemaNames b ON a.resolvedName = b.Schema_Name WHERE (" + whereClause + ") ORDER BY 1";
		}
		else
		{
			query = "SELECT DISTINCT a.schemaId formId, a.resolvedName formName, a.name derivedName, a.schemaType formType \n" +
					"FROM arschema a WHERE (" + whereClause + ") ORDER BY 1";
		}

		try
		{
			SQLResult result = getAction().getServerConnection().getContext().getListSQL(query, 5000, true);
			List<List<Value>> records = result.getContents();
			String formName = null;
			String derivedForm = null;

			for(int i = 0; records != null && i < records.size(); i++)
			{
				List<Value> row = records.get(i);
				formName = (String) row.get(1).getValue();
				derivedForm = (String) row.get(2).getValue();

				//validate form name and derived form name values.
				if(formName == null && derivedForm != null) formName = derivedForm;
				if(formName != null && derivedForm == null) formName = derivedForm;

				//if it is still null or a database view form from metadata pool, continue to scan
				if(formName == null ) continue;
				if(formName.startsWith("AR System Metadata")) continue;

				CoreItem item = new CoreItem();
				item.setFormName(ARDictionary.FORM_METADATA_ENTITIES);
				item.setAttribute(536870917, formName);

				//check if this item already exists - maybe it is an overlay
				item.read(getAction().getServerConnection());

				item.setAttribute(7, new Integer(0));
				item.setAttribute(8, "SET");
				item.setAttribute(536870919, formName);
				item.setAttribute(536870918, row.get(3).getValue());

				if(cmdbEnabled)
				{
					String classKeyword = (String) row.get(4).getValue();
					String classProperName = (String) row.get(6).getValue();

					item.setAttribute(536870913, classKeyword);
					item.setAttribute(536870916, row.get(5).getValue());
					item.setAttribute(536870914, row.get(7).getValue());

					if(classProperName != null && classKeyword != null)
					{
						//overwrite description field
						item.setAttribute(536870919, classProperName + " (" + classKeyword + ")");
					}
				}

				//save form or class entity
				if(!item.exists()) item.create(getAction().getServerConnection());
					else item.update(getAction().getServerConnection());

				//discover and save fields related entities
				setFieldAttributesMetadata(derivedForm);

				getAction().setRecordsCounter();
			}
		}
		catch(ARException are)
		{
			throw new AREasyException("Error saving entities metadata: " + are.getMessage(), are);
		}

		RuntimeLogger.info("Entities added" + (cmdbEnabled ? " (including CMDB details)" : ""));
	}

	protected void setFieldAttributesMetadata(String formName) throws AREasyException
	{
		if(formName == null) return;

		String query = "SELECT DISTINCT a.schemaId formId, a.resolvedName formName, a.schemaType formType, b.fieldId, b.recordId, b.fieldName, b.fieldType, b.datatype fieldDataType \n" +
					   "FROM arschema a join field b on a.schemaId = b.schemaId WHERE (b.datatype < 30) AND a.name = '" + formName + "' ORDER BY 3";

		try
		{
			SQLResult result = getAction().getServerConnection().getContext().getListSQL(query, 5000, true);
			List<List<Value>> records = result.getContents();
			String fieldFormName = null;

			for(int i = 0; records != null && i < records.size(); i++)
			{
				List<Value> row = records.get(i);
				fieldFormName = (String) row.get(1).getValue();
				if(fieldFormName == null) fieldFormName = formName;

				CoreItem item = new CoreItem();
				item.setFormName(ARDictionary.FORM_METADATA_ATTRIBUTES);
				item.setAttribute(536870918, fieldFormName);
				item.setAttribute(536870914, String.valueOf(row.get(3).getValue()));
				item.setAttribute(536870915, (String) row.get(5).getValue());

				//check if this item already exists - maybe it is an overlay
				item.read(getAction().getServerConnection());

				item.setAttribute(7, new Integer(0));
				item.setAttribute(8, "SET");
				item.setAttribute(536870919, row.get(2).getValue());
				item.setAttribute(536870916, row.get(6).getValue());
				item.setAttribute(536870913, row.get(7).getValue());

				//save field entity
				if(!item.exists()) item.create(getAction().getServerConnection());
					else item.create(getAction().getServerConnection());
			}
		}
		catch(ARException are)
		{
			throw new AREasyException("Error saving entities metadata: " + are.getMessage(), are);
		}

		RuntimeLogger.info("Form attributes added: " + formName);
	}

	protected void setOptionAttributesMetadata() throws AREasyException
	{
		boolean next = true;

		String entitiesFlag = getAction().getConfiguration().getString("entities", "all");
		if(!StringUtility.equals(entitiesFlag, "all") && !StringUtility.equals(entitiesFlag, "aaroptions")) return;

		Configuration config = getAction().getManager().getConfiguration().subset("app.runtime.action." + getAction().getCode());

		for(int x = 1; next && x < 100; x++)
		{
			String actionCode = "action";

			if(x < 10) actionCode += "0" + x;
				else actionCode += x;

			String actionLabel = config.getString(actionCode + ".label", null);
			String actionName = config.getString(actionCode + ".name", null);
			List actionOptions = config.getList(actionCode + ".options", null);
			String actionCatalog = config.getString(actionCode + ".catalog", null);

			if(actionCatalog != null && actionName == null && actionLabel == null)
			{
				String data[] = StringUtility.split(actionCatalog, ";");

				if(data != null && data.length == 2)
				{
					actionLabel = data[0];
					actionName = data[1];
				}
				else if(data != null) actionName = data[0];
			}

			if(actionLabel != null && actionName != null)
			{
				CoreItem entry = new CoreItem();
				entry.setFormName(ARDictionary.FORM_METADATA_ENTITIES);
				entry.setAttribute(7, new Integer(0));
				entry.setAttribute(8, "SET");
				entry.setAttribute(536870917, actionName);
				entry.setAttribute(536870919, actionLabel + " (" + actionName + ")");
				entry.setAttribute(536870918, new Integer(100 + x));

				entry.create(getAction().getServerConnection());

				for(int i = 0; actionOptions != null && i < actionOptions.size(); i++)
				{
					String option = (String) actionOptions.get(i);
					String data[] = StringUtility.split(option, ";");

					if(data != null && data.length == 2)
					{
						CoreItem item = new CoreItem();
						item.setFormName(ARDictionary.FORM_METADATA_ATTRIBUTES);
						item.setAttribute(7, new Integer(0));
						item.setAttribute(8, "SET");
						item.setAttribute(536870918, actionName);
						item.setAttribute(536870919, new Integer(100 + x));
						item.setAttribute(536870914, data[1].trim());
						item.setAttribute(536870915, data[0].trim());
						item.setAttribute(536870916, new Integer(100));

						item.create(getAction().getServerConnection());
					}

					getAction().setRecordsCounter();
				}
			}
			else next = false;
		}
	}
}
