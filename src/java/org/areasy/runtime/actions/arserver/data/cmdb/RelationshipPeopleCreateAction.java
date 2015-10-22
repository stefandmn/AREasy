package org.areasy.runtime.actions.arserver.data.cmdb;

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

import org.areasy.runtime.engine.RuntimeLogger;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.runtime.engine.structures.CoreItem;
import org.areasy.runtime.engine.structures.data.cmdb.ConfigurationItem;
import org.areasy.runtime.engine.structures.data.itsm.foundation.People;
import org.areasy.runtime.engine.structures.data.itsm.foundation.SupportGroup;
import org.areasy.runtime.engine.workflows.ProcessorLevel2CmdbApp;
import org.areasy.common.data.StringUtility;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;


/**
 * Dedicated CMDB runtime action which should be able to perform relationships between assets and people or support groups.
 *
 */
public class RelationshipPeopleCreateAction extends BaseConfigurationItemAction implements ConfigurationItemAction
{
	/**
	 * Create relationships between discovered CIs and specified organisational entities (people and support groups).
	 * This action could contain the following parametrization:
	 * <table border="1">
	 * 	<tr>
	 * 		<td nowrap width="15%">-simulation</td>
	 * 		<td width="65%">Optional parameter which specify that this execution is just a simulation and the result is not committed in the CMDB database</td>
	 * 	</tr>
	 * 	<tr>
	 * 		<td nowrap width="35%"><b>-keyid</b></td>
	 * 		<td width="65%">Specify the key (the field id from CMDB class) which will be used to identify an asset</td>
	 * 	</tr>
	 * 	<tr>
	 * 		<td nowrap width="35%"><b>-keyvalue</b></td>
	 * 		<td width="65%">Specify the value of the specified key which will be used to identify an asset.
	 * 			For example, if the key is the serial number (field id = 2000000001) this switch should be the found value.
	 * 			<pre>runtime -action cmdb.relationship.people.create -arserver T200 -aruser Demo -keyid 2000000001 -keyvalue CND873928 -peoplerelationrole "used by" -peoplerelationentity "support group"</pre></td>
	 * 	</tr>
	 * 	<tr>
	 * 		<td nowrap width="35%">-keyids</td>
	 * 		<td width="65%">Specify the keys (the field ids from CMDB class) which will be used to identify an asset. Both attributes (keyid and keyids)
	 * 			could be used in the same command but will not find two assets (CIs), both will restrict the searching operation to build a criteria
	 * 			based on all specified field ids.</td>
	 * 	</tr>
	 * 	<tr>
	 * 		<td nowrap width="35%">-keyvalues</td>
	 * 		<td width="65%">Corresponding field values for specified key names (field ids) for previous parameter.
	 * 			<pre>runtime -action cmdb.relationship.people.create -arserver T200 -aruser Demo -keyids 2000000001 2000000003 -keyvalues CND873928 Hardware  -peoplerelationrole "used by" -peoplerelationentity "support group"</pre>
	 * 			In this example the action will search a CI which must have a serial number (2000000001) like "CND873928" and a category (2000000003) like "Hardware"</td>
	 * 	</tr>
	 * 	<tr>
	 * 		<td nowrap width="35%"><b>-peoplerelationentity</b></td>
	 * 		<td width="90%">Specify which type of entity is referring this relationship. The possible values are: <code>people</code> or "<code>support group</code>".
	 *  		If this parameter will not be specified, the action will consider an implicit entity which is <code>people</code></td>
	 * 	</tr>
	 * 	<tr>
	 * 		<td nowrap width="35%"><b>-peoplerelationrole</b></td>
	 * 		<td width="65%">Specify the role value for indicated entity type. The possible values are: "<code>approved by<code>", "<code>created by</code>", "<code>managed by</code>",
	 * 			"<code>owned by</code>", "<code>supported by</code>" and "</code>used by</code>".If this parameter will not be specified the action will consider an implicit
	 * 			role which is "<code>used by</code>".</td>
	 * 	</tr>
	 * 	<tr>
	 * 		<td nowrap width="35%">-peoplerelationkeyids</td>
	 * 		<td width="65%">Defines a key (field id from a remote/targeted entity) to be used to identify the entity instance (object). For example if you want to identify a person
	 * 			(from <code>CTM:People</code> form using <code>Last Name</code> you should specify here the field id for <code>Last Name</code> field. If this parameter
	 * 			is missing will be considered two form attributes to identify the instance: <code>Corporate ID</code> (or <code>HR ID</code>) or <code>Remedy Login ID</code></td>
	 * 	</tr>
	 * 	<tr>
	 * 		<td nowrap width="35%"><b>-peoplerelationkeyvalues</b></td>
	 * 		<td width="65%">Specify a value to identify the object instance (in correlation with <code>targetid</code> parameter - eexplicitly or default value). This parameter
	 * 			is mandatory, otherwise this action will return an exception.</td>
	 * 	</tr>
	 * 	<tr>
	 * 		<td nowrap width="35%">-peoplerelationmapids</td>
	 * 		<td width="65%">Specific field ids from the relationship structure to fill with data additional relationship attributes.</td>
	 * 	</tr>
	 * 	<tr>
	 * 		<td nowrap width="35%">-peoplerelationmapvalues</td>
	 * 		<td width="65%">Specific field values (and must be defined in correlation with <code>relationmapids</code> parameter) to specify clear values to define a complete relationship.</td>
	 * 	</tr>
	 * 	<tr>
	 * 		<td nowrap width="35%">-classid</td>
	 * 		<td width="65%">Because the first search (to identify the CI) will be performed in <code>BaseElement</code> CMDB class, optionally you can use this parameter
	 * 			to help the searching procedure to identify the asset from the first phase telling in what class is stored. This parameter should receive only
	 * 			class keyword values (e.g. BMC_COMPUTERSYSTEM or BMC_EQUIPMENT) and is necessary when the <code>keyid</code>(s) is an attribute which is not member of
	 * 			<code>BaseElement</code> class.</td>
	 * 	</tr>
	 * 	<tr>
	 * 		<td nowrap width="35%">-datasetid</td>
	 * 		<td width="65%">Optional parameter to restrict the searching procedure you can use this parameter to find only in one dataset. By default this value is <b>BMC_ASSET</b>, so
	 * 			without to specify this parameter will be found only CIs from <b>BMC_ASSET</b> dataset.</td>
	 * 	</tr>
	 * </table>
	 *
	 * @throws org.areasy.runtime.engine.base.AREasyException if any error will occur
	 */
	public void run(ConfigurationItem item) throws AREasyException
	{
		boolean ignorenullvalue = getConfiguration().getBoolean("ignorenullvalues", true);
		boolean ignoreunchangedvalues = getConfiguration().getBoolean("ignoreunchangedvalues", true);

		String peoplerelationrole = getConfiguration().getString("peoplerelationrole", ProcessorLevel2CmdbApp.CONST_PEOPLE_ROLENAMES[5]);
		String peoplerelationentity = getConfiguration().getString("peoplerelationentity", ProcessorLevel2CmdbApp.CONST_PEOPLE_RELATIONENTITIES[0]);

		//related data structure
		CoreItem item2 = null;

		if(StringUtility.equalsIgnoreCase(peoplerelationentity, ProcessorLevel2CmdbApp.CONST_PEOPLE_RELATIONENTITIES[0])) item2 = new People();
			else if(StringUtility.equalsIgnoreCase(peoplerelationentity, ProcessorLevel2CmdbApp.CONST_PEOPLE_RELATIONENTITIES[1])) item2 = new SupportGroup();
				else throw new AREasyException("Invalid relationship entity type: " + peoplerelationentity);

		if(getConfiguration().containsKey("entityqualification"))
		{
			//specify key attributes to process search operation.
			String qualification = getTranslatedQualification(getConfiguration().getString("entityqualification", null));
			item2.read(getServerConnection(), qualification);
		}
		else if(StringUtility.equalsIgnoreCase(peoplerelationentity, ProcessorLevel2CmdbApp.CONST_PEOPLE_RELATIONENTITIES[0]) && (getConfiguration().containsKey("peopleloginid") ||
			getConfiguration().containsKey("peopleemail") || getConfiguration().containsKey("peoplefirstname") || getConfiguration().containsKey("peoplelastname") ||
			getConfiguration().containsKey("peoplecorporateid") || getConfiguration().containsKey("peoplepersonid") || getConfiguration().containsKey("peoplefullname")))
		{
			if(getConfiguration().containsKey("peopleloginid")) item2.setAttribute(4, getConfiguration().getString("peopleloginid", null));
			if(getConfiguration().containsKey("peopleemail")) item2.setAttribute(1000000048, getConfiguration().getString("peopleemail", null));
			if(getConfiguration().containsKey("peoplefirstname")) item2.setAttribute(1000000019, getConfiguration().getString("peoplefirstname", null));
			if(getConfiguration().containsKey("peoplelastname")) item2.setAttribute(1000000018, getConfiguration().getString("peoplelastname", null));
			if(getConfiguration().containsKey("peoplefullname")) item2.setAttribute(1000000017, getConfiguration().getString("peoplefullname", null));
			if(getConfiguration().containsKey("peoplecorporateid")) item2.setAttribute(1000000054, getConfiguration().getString("peoplecorporateid", null));
			if(getConfiguration().containsKey("peoplepersonid")) item2.setAttribute(1, getConfiguration().getString("peoplepersonid", null));

			item2.read(getServerConnection());
		}
		else if(StringUtility.equalsIgnoreCase(peoplerelationentity, ProcessorLevel2CmdbApp.CONST_PEOPLE_RELATIONENTITIES[1]) && (getConfiguration().containsKey("sgroupid") ||
				getConfiguration().containsKey("sgroupname") || getConfiguration().containsKey("sgroupcompany") || getConfiguration().containsKey("sgrouporganisation")))
		{
			if(getConfiguration().containsKey("sgroupid")) item2.setAttribute(1, getConfiguration().getString("sgroupid", null));
			if(getConfiguration().containsKey("sgroupname")) item2.setAttribute(1000000015, getConfiguration().getString("sgroupname", null));
			if(getConfiguration().containsKey("sgroupcompany")) item2.setAttribute(1000000001, getConfiguration().getString("sgroupcompany", null));
			if(getConfiguration().containsKey("sgrouporganisation")) item2.setAttribute(1000000014, getConfiguration().getString("sgrouporganisation", null));

			item2.read(getServerConnection());
		}
		else if(getConfiguration().containsKey("peoplerelationkeyids") && getConfiguration().containsKey("peoplerelationkeyvalues"))
		{
			List peoplerelationkeyids = getConfiguration().getVector("peoplerelationkeyids", new Vector());
			List peoplerelationkeyvalues = getConfiguration().getVector("peoplerelationkeyvalues", new Vector());

			Map map = getMap(peoplerelationkeyids, peoplerelationkeyvalues);

			item2.setData(map);
			item2.read(getServerConnection());
		}

		Map relationdatamap = null;
		if(getConfiguration().containsKey("primarycontact") || getConfiguration().containsKey("unavailabilityassignment") || getConfiguration().containsKey("accesspermitted") || getConfiguration().containsKey("rowlevelaccessenabled"))
		{
			relationdatamap = new HashMap();

			if(getConfiguration().containsKey("primarycontact")) relationdatamap.put(301501300, getConfiguration().getString("primarycontact", null));
			if(getConfiguration().containsKey("unavailabilityassignment")) relationdatamap.put(1000002883, getConfiguration().getString("unavailabilityassignment", null));
			if(getConfiguration().containsKey("accesspermitted")) relationdatamap.put(301501400, getConfiguration().getString("accesspermitted", null));
			if(getConfiguration().containsKey("assignmentlocked")) relationdatamap.put(1000002884, getConfiguration().getString("assignmentlocked", null));
			if(getConfiguration().containsKey("rowlevelaccessenabled")) relationdatamap.put(301497700, getConfiguration().getString("rowlevelaccessenabled", null));
		}
		else if(getConfiguration().containsKey("peoplerelationdataids") && getConfiguration().containsKey("peoplerelationdatavalues"))
		{
			List peoplerelationmapids = getConfiguration().getVector("peoplerelationdataids", new Vector());
			List peoplerelationmapvalues = getConfiguration().getVector("peoplerelationdatavalues", new Vector());

			relationdatamap = getMap(peoplerelationmapids, peoplerelationmapvalues);
		}

		//set validation flags
		item.setIgnoreNullValues(ignorenullvalue);
		item.setIgnoreUnchangedValues(ignoreunchangedvalues);

		boolean output = ProcessorLevel2CmdbApp.setPeopleRelationship(getServerConnection(), item, item2, relationdatamap, peoplerelationrole);
		if(output) RuntimeLogger.debug("People relationship has been created: " + item + " -> " + item2);
	}
}
