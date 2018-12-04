package org.areasy.runtime.actions.itsm.data;

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

import org.areasy.common.data.StringUtility;
import org.areasy.runtime.engine.RuntimeLogger;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.runtime.engine.structures.data.cmdb.ConfigurationItem;
import org.areasy.runtime.engine.workflows.ProcessorLevel1Context;
import org.areasy.runtime.engine.workflows.ProcessorLevel2CmdbApp;

import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * Dedicated CMDB runtime action which should be able to perform relationships between assets.
 *
 */
public class RelationshipConfigurationItemCreateAction extends BaseConfigurationItemAction implements ConfigurationItemAction
{
	/**
	 * Create a relationship between two CIs.
	 *
	 * @param item configuration item structure. If it is null will return an exception
	 * @throws org.areasy.runtime.engine.base.AREasyException will any error occurs.
	 */
	public void run(ConfigurationItem item) throws AREasyException
	{
		boolean ignorenullvalue = getConfiguration().getBoolean("ignorenullvalues", true);
		boolean ignoreunchangedvalues = getConfiguration().getBoolean("ignoreunchangedvalues", true);

		String classrelationtype = getConfiguration().getString("classrelationshiptype", "BMC_Component");
		String classrelationrole = getConfiguration().getString("classrelationshiprole", "child");
		String classrelationimpact = getConfiguration().getString("classrelationshipimpact", null);

		//set validation flags
		item.setIgnoreNullValues(ignorenullvalue);
		item.setIgnoreUnchangedValues(ignoreunchangedvalues);

		boolean isparent = false;

		if(StringUtility.equalsIgnoreCase(classrelationrole, "parent")) isparent = true;
			else if(StringUtility.equalsIgnoreCase(classrelationrole, "child")) isparent = false;

		//transform impact abbreviations into real values.
		if(StringUtility.equalsIgnoreCase(classrelationimpact, "sd")) classrelationimpact = "Source-Destination";
			else if(StringUtility.equalsIgnoreCase(classrelationimpact, "ds")) classrelationimpact = "Destination-Source";

		//related data structure
		ConfigurationItem item2 = new ConfigurationItem();
		item2.setFormName(ProcessorLevel1Context.FORM_BASEELEMENT);

		if(getConfiguration().containsKey("cirelationqualification"))
		{
			//specify key attributes to process search operation.
			String qualification = getTranslatedQualification(getConfiguration().getString("cirelationqualification", null));

			//get list of found CIs
			item2.read(getServerConnection(), qualification);
		}
		else if(getConfiguration().containsKey("cirelationinstanceid") || getConfiguration().containsKey("cirelationreconciliationid") ||
				getConfiguration().containsKey("cirelationassetid") || getConfiguration().containsKey("cirelationserialnumber") ||
				getConfiguration().containsKey("cirelationtokenid") || getConfiguration().containsKey("cirelationkeyid") ||
				getConfiguration().containsKey("cirelationtagnumber") || getConfiguration().containsKey("cirelationproduct") ||
				getConfiguration().containsKey("cirelationitem") || getConfiguration().containsKey("cirelationtype") ||
				getConfiguration().containsKey("cirelationcategory") || getConfiguration().containsKey("cirelationname") ||
				getConfiguration().containsKey("cirelationdatasetid") || getConfiguration().containsKey("cirelationclassid"))
		{
			if(getConfiguration().containsKey("cirelationinstanceid")) item2.setAttribute(179, getConfiguration().getString("cirelationinstanceid", null));
			if(getConfiguration().containsKey("cirelationreconciliationid")) item2.setAttribute(400129200, getConfiguration().getString("cirelationreconciliationid", null));
			if(getConfiguration().containsKey("cirelationassetid")) item2.setAttribute(210000000, getConfiguration().getString("cirelationassetid", null));
			if(getConfiguration().containsKey("cirelationserialnumber")) item2.setAttribute(200000001, getConfiguration().getString("cirelationserialnumber", null));
			if(getConfiguration().containsKey("cirelationname")) item2.setAttribute(200000020, getConfiguration().getString("cirelationname", null));
			if(getConfiguration().containsKey("cirelationcategory")) item2.setAttribute(200000003, getConfiguration().getString("cirelationcategory", null));
			if(getConfiguration().containsKey("cirelationtype")) item2.setAttribute(200000004, getConfiguration().getString("cirelationtype", null));
			if(getConfiguration().containsKey("cirelationitem")) item2.setAttribute(200000005, getConfiguration().getString("cirelationitem", null));
			if(getConfiguration().containsKey("cirelationproduct")) item2.setAttribute(240001002, getConfiguration().getString("cirelationproduct", null));
			if(getConfiguration().containsKey("cirelationtagnumber")) item2.setAttribute(260100004, getConfiguration().getString("cirelationtagnumber", null));
			if(getConfiguration().containsKey("cirelationtokenid")) item2.setAttribute(530010100, getConfiguration().getString("cirelationtokenid", null));
			if(getConfiguration().containsKey("cirelationdatasetid")) item2.setAttribute(400127400, getConfiguration().getString("cirelationdatasetid", null));
			if(getConfiguration().containsKey("cirelationclassid")) item2.setAttribute(400079600, getConfiguration().getString("cirelationclassid", null));
			if(getConfiguration().containsKey("cirelationkeyid") && getConfiguration().containsKey("cirelationvalue")) item2.setAttribute(getConfiguration().getString("cirelationkeyid", null), getConfiguration().getString("cirelationvalue", null));

			item2.read(getServerConnection());
		}
		else if(getConfiguration().containsKey("cirelationkeyids"))
		{
			//check CI relationships flags
			Map map = getRelationKeyMap();
			item2.setData(map);

			item2.read(getServerConnection());
		}
		else throw new AREasyException("CI relationship keys are not defined");

		//process action
		boolean output = ProcessorLevel2CmdbApp.setConfigurationItemRelationship(getServerConnection(), item, item2, classrelationimpact, classrelationtype, isparent);
		if(output) RuntimeLogger.debug("CI relationship has been created: " + item + " -> " + item2);
	}

	protected Map getRelationKeyMap()
	{
		List cirelationkeyids = getConfiguration().getVector("cirelationkeyids", new Vector());
		List cirelationkeyvalues = getConfiguration().getVector("cirelationkeyvalues", new Vector());

		return getMap(cirelationkeyids, cirelationkeyvalues);
	}
}
