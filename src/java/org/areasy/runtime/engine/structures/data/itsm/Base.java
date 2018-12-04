package org.areasy.runtime.engine.structures.data.itsm;

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
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.runtime.engine.base.ServerConnection;
import org.areasy.runtime.engine.structures.CoreItem;

import java.util.List;

/**
 * Abstract and record structure for Incident Management application
 */
public abstract class Base extends CoreItem
{
	public Base()
	{
		clear();
	}

	/**
	 * Get status value.
	 *
	 * @return status.
	 */
	public String getStatus()
	{
		return getStringAttributeValue(BASE_STATUS);
	}

	/**
	 * Set status.
	 *
	 * @param value status
	 */
	public void setDefaultStatus(String value)
	{
		setDefaultAttribute(BASE_STATUS, value);
	}

	/**
	 * Set status.
	 *
	 * @param value status
	 */
	public void setStatus(String value)
	{
		setAttribute(BASE_STATUS, value);
	}

	/**
	 * Set status.
	 *
	 * @param value status
	 */
	public void setStatus(int value)
	{
		setAttribute(BASE_STATUS, new Integer(value));
	}

	/**
	 * Set status.
	 *
	 * @param value status
	 */
	public void setDefaultStatus(int value)
	{
		setDefaultAttribute(BASE_STATUS, new Integer(value));
	}

	/**
	 * Get summary value.
	 *
	 * @return summary.
	 */
	public String getSummary()
	{
		return getStringAttributeValue(BASE_SUMMARY);
	}

	/**
	 * Set summary.
	 *
	 * @param value summary
	 */
	public void setSummary(String value)
	{
		setAttribute(BASE_SUMMARY, value);
	}

	/**
	 * Set default summary.
	 *
	 * @param value summary
	 */
	public void setDefaultSummary(String value)
	{
		setDefaultAttribute(BASE_SUMMARY, value);
	}

	/**
	 * Get notes value.
	 *
	 * @return notes.
	 */
	public String getNotes()
	{
		return getStringAttributeValue(BASE_NOTES);
	}

	/**
	 * Set notes.
	 *
	 * @param value notes
	 */
	public void setNotes(String value)
	{
		setAttribute(BASE_NOTES, value);
	}

	/**
	 * Set default notes.
	 *
	 * @param value notes
	 */
	public void setDefaultNotes(String value)
	{
		setDefaultAttribute(BASE_NOTES, value);
	}

	/**
	 * Get impact value.
	 *
	 * @return impact.
	 */
	public String getImpact()
	{
		return getStringAttributeValue(BASE_IMPACT);
	}

	/**
	 * Set impact.
	 *
	 * @param value impact
	 */
	public void setDefaultImpact(String value)
	{
		setDefaultAttribute(BASE_IMPACT, value);
	}

	/**
	 * Set impact.
	 *
	 * @param value impact
	 */
	public void setImpact(String value)
	{
		setAttribute(BASE_IMPACT, value);
	}

	/**
	 * Set impact.
	 *
	 * @param value impact
	 */
	public void setImpact(int value)
	{
		setAttribute(BASE_IMPACT, new Integer(value));
	}

	/**
	 * Set impact.
	 *
	 * @param value impact
	 */
	public void setDefaultImpact(int value)
	{
		setDefaultAttribute(BASE_IMPACT, new Integer(value));
	}

	/**
	 * Set instance id
	 *
	 * @param value instance id
	 */
	public void setInstanceId(String value)
	{
		setAttribute(BASE_INSTANCEID, value);
	}

	/**
	 * Set instance id
	 *
	 * @param value instance id
	 */
	public void setDefaultInstanceId(String value)
	{
		setDefaultAttribute(BASE_INSTANCEID, value);
	}

	/**
	 * Get incident ticket instance id.
	 *
	 * @return instance id
	 */
	public String getInstanceId()
	{
		return getStringAttributeValue(BASE_INSTANCEID);
	}

	/**
	 * Get priority value.
	 *
	 * @return priority.
	 */
	public String getPriority()
	{
		return getStringAttributeValue(BASE_PRIORITY);
	}

	/**
	 * Set priority.
	 *
	 * @param value priority
	 */
	public void setDefaultPriority(String value)
	{
		setDefaultAttribute(BASE_PRIORITY, value);
	}


	/**
	 * Set priority.
	 *
	 * @param value priority
	 */
	public void setPriority(String value)
	{
		setAttribute(BASE_PRIORITY, value);
	}

	/**
	 * Set priority.
	 *
	 * @param value priority
	 */
	public void setDefaultPriority(int value)
	{
		setDefaultAttribute(BASE_PRIORITY, new Integer(value));
	}

	/**
	 * Set priority.
	 *
	 * @param value priority
	 */
	public void setPriority(int value)
	{
		setAttribute(BASE_PRIORITY, new Integer(value));
	}

	/**
	 * Get urgency value.
	 *
	 * @return urgency.
	 */
	public String getUrgency()
	{
		return getStringAttributeValue(BASE_URGENCY);
	}

	/**
	 * Set urgency.
	 *
	 * @param value urgency
	 */
	public void setDefaultUrgency(String value)
	{
		setDefaultAttribute(BASE_URGENCY, value);
	}

	/**
	 * Set urgency.
	 *
	 * @param value urgency
	 */
	public void setUrgency(String value)
	{
		setAttribute(BASE_URGENCY, value);
	}

	/**
	 * Set urgency.
	 *
	 * @param value urgency
	 */
	public void setDefaultUrgency(int value)
	{
		setDefaultAttribute(BASE_URGENCY, new Integer(value));
	}

	/**
	 * Set urgency.
	 *
	 * @param value urgency
	 */
	public void setUrgency(int value)
	{
		setAttribute(BASE_URGENCY, new Integer(value));
	}


	/**
	 * Get Customer First Name value.
	 *
	 * @return Customer First Name.
	 */
	public String getCustomerFirstName()
	{
		return getStringAttributeValue(BASE_CUSTFIRSTNAME);
	}

	/**
	 * Set Customer First Name.
	 *
	 * @param value Customer First Name
	 */
	public void setDefaultCustomerFirstName(String value)
	{
		setDefaultAttribute(BASE_CUSTFIRSTNAME, value);
	}

	/**
	 * Set Customer First Name.
	 *
	 * @param value Customer First Name
	 */
	public void setCustomerFirstName(String value)
	{
		setAttribute(BASE_CUSTFIRSTNAME, value);
	}

	/**
	 * Get Customer Last Name value.
	 *
	 * @return Customer Last Name.
	 */
	public String getCustomerLastName()
	{
		return getStringAttributeValue(BASE_CUSTLASTNAME);
	}

	/**
	 * Set Customer Last Name.
	 *
	 * @param value Customer Last Name
	 */
	public void setDefaultCustomerLastName(String value)
	{
		setDefaultAttribute(BASE_CUSTLASTNAME, value);
	}

	/**
	 * Set Customer Last Name.
	 *
	 * @param value Customer Last Name
	 */
	public void setCustomerLastName(String value)
	{
		setAttribute(BASE_CUSTLASTNAME, value);
	}

	/**
	 * Get Customer Bussiness Phone value.
	 *
	 * @return Customer Bussiness Phone.
	 */
	public String getCustomerBussinessPhone()
	{
		return getStringAttributeValue(BASE_CUSTBUSSINESSPHONE);
	}

	/**
	 * Set Customer Bussiness Phone.
	 *
	 * @param value Customer Bussiness Phone
	 */
	public void setDefaultCustomerBussinessPhone(String value)
	{
		setDefaultAttribute(BASE_CUSTBUSSINESSPHONE, value);
	}

	/**
	 * Set Customer Bussiness Phone.
	 *
	 * @param value Customer Bussiness Phone
	 */
	public void setCustomerBussinessPhone(String value)
	{
		setAttribute(BASE_CUSTBUSSINESSPHONE, value);
	}

	/**
	 * Get incident Customer Company value.
	 *
	 * @return Customer Company.
	 */
	public String getCustomerCompany()
	{
		return getStringAttributeValue(BASE_CUSTCOMPANY);
	}

	/**
	 * Set Customer Company.
	 *
	 * @param value Customer Company
	 */
	public void setDefaultCustomerCompany(String value)
	{
		setDefaultAttribute(BASE_CUSTCOMPANY, value);
	}

	/**
	 * Set Customer Company.
	 *
	 * @param value Customer Company
	 */
	public void setCustomerCompany(String value)
	{
		setAttribute(BASE_CUSTCOMPANY, value);
	}

	/**
	 * Get Customer Organisation value.
	 *
	 * @return Customer Organisation.
	 */
	public String getCustomerOrganisation()
	{
		return getStringAttributeValue(BASE_CUSTORGANISATION);
	}

	/**
	 * Set Customer Organisation.
	 *
	 * @param value Customer Organisation
	 */
	public void setDefaultCustomerOrganisation(String value)
	{
		setDefaultAttribute(BASE_CUSTORGANISATION, value);
	}

	/**
	 * Set Customer Organisation.
	 *
	 * @param value Customer Organisation
	 */
	public void setCustomerOrganisation(String value)
	{
		setAttribute(BASE_CUSTORGANISATION, value);
	}

	/**
	 * Get Customer Department value.
	 *
	 * @return Customer Department.
	 */
	public String getCustomerDepartment()
	{
		return getStringAttributeValue(BASE_CUSTDEPARTMENT);
	}

	/**
	 * Set Customer Department.
	 *
	 * @param value Customer Department
	 */
	public void setDefaultCustomerDepartment(String value)
	{
		setDefaultAttribute(BASE_CUSTDEPARTMENT, value);
	}

	/**
	 * Set Customer Department.
	 *
	 * @param value Customer Department
	 */
	public void setCustomerDepartment(String value)
	{
		setAttribute(BASE_CUSTDEPARTMENT, value);
	}

	/**
	 * Get Customer Site value.
	 *
	 * @return Customer Site.
	 */
	public String getCustomerSite()
	{
		return getStringAttributeValue(BASE_CUSTSITE);
	}

	/**
	 * Set Customer Site.
	 *
	 * @param value Customer Site
	 */
	public void setDefaultCustomerSite(String value)
	{
		setDefaultAttribute(BASE_CUSTSITE, value);
	}

	/**
	 * Set Customer Site.
	 *
	 * @param value Customer Site
	 */
	public void setCustomerSite(String value)
	{
		setAttribute(BASE_CUSTSITE, value);
	}

	/**
	 * Get operational categorization tier 1.
	 *
	 * @return operational categorization tier 1.
	 */
	public String getOperationalCategorizationTier1()
	{
		return getStringAttributeValue(BASE_CATEGOP1);
	}

	/**
	 * Set default operational categorization tier 1.
	 *
	 * @param value operational categorization tier 1
	 */
	public void setDefaultOperationalCategorizationTier1(String value)
	{
		setDefaultAttribute(BASE_CATEGOP1, value);
	}

	/**
	 * Set operational categorization tier 1.
	 *
	 * @param value operational categorization tier 1
	 */
	public void setOperationalCategorizationTier1(String value)
	{
		setAttribute(BASE_CATEGOP1, value);
	}

	/**
	 * Get operational categorization tier 2.
	 *
	 * @return operational categorization tier 2.
	 */
	public String getOperationalCategorizationTier2()
	{
		return getStringAttributeValue(BASE_CATEGOP2);
	}

	/**
	 * Set default operational categorization tier 2.
	 *
	 * @param value operational categorization tier 2
	 */
	public void setDefaultOperationalCategorizationTier2(String value)
	{
		setDefaultAttribute(BASE_CATEGOP2, value);
	}

	/**
	 * Set operational categorization tier 2.
	 *
	 * @param value operational categorization tier 2
	 */
	public void setOperationalCategorizationTier2(String value)
	{
		setAttribute(BASE_CATEGOP2, value);
	}

	/**
	 * Get operational categorization tier 3.
	 *
	 * @return operational categorization tier 3.
	 */
	public String getOperationalCategorizationTier3()
	{
		return getStringAttributeValue(BASE_CATEGOP3);
	}

	/**
	 * Set default operational categorization tier 3.
	 *
	 * @param value operational categorization tier 3
	 */
	public void setDefaultOperationalCategorizationTier3(String value)
	{
		setDefaultAttribute(BASE_CATEGOP3, value);
	}

	/**
	 * Set operational categorization tier 3.
	 *
	 * @param value operational categorization tier 3
	 */
	public void setOperationalCategorizationTier3(String value)
	{
		setAttribute(BASE_CATEGOP3, value);
	}

	/**
	 * Get product categorization tier 1
	 *
	 * @return product categorization tier 1
	 */
	public String getProductCategorizationTier1()
	{
		return getStringAttributeValue(BASE_CATEGPR1);
	}

	/**
	 * Set default product categorization tier 1
	 *
	 * @param value product categorization tier 1
	 */
	public void setDefaultProductCategorizationTier1(String value)
	{
		setDefaultAttribute(BASE_CATEGPR1, value);
	}

	/**
	 * Set product categorization tier 1
	 *
	 * @param value product categorization tier 1
	 */
	public void setProductCategorizationTier1(String value)
	{
		setAttribute(BASE_CATEGPR1, value);
	}

	/**
	 * Get product categorization tier 2
	 *
	 * @return product categorization tier 2
	 */
	public String getProductCategorizationTier2()
	{
		return getStringAttributeValue(BASE_CATEGPR2);
	}

	/**
	 * Set default product categorization tier 2
	 *
	 * @param value product categorization tier 2
	 */
	public void setDefaultProductCategorizationTier2(String value)
	{
		setDefaultAttribute(BASE_CATEGPR2, value);
	}

	/**
	 * Set product categorization tier 2
	 *
	 * @param value product categorization tier 2
	 */
	public void setProductCategorizationTier2(String value)
	{
		setAttribute(BASE_CATEGPR2, value);
	}

	/**
	 * Get product categorization tier 3
	 *
	 * @return product categorization tier 3
	 */
	public String getProductCategorizationTier3()
	{
		return getStringAttributeValue(BASE_CATEGPR3);
	}

	/**
	 * Set default product categorization tier 3
	 *
	 * @param value product categorization tier 3
	 */
	public void setDefaultProductCategorizationTier3(String value)
	{
		setDefaultAttribute(BASE_CATEGPR3, value);
	}

	/**
	 * Set product categorization tier 3
	 *
	 * @param value product categorization tier 3
	 */
	public void setProductCategorizationTier3(String value)
	{
		setAttribute(BASE_CATEGPR3, value);
	}

	/**
	 * Get product name
	 *
	 * @return product name
	 */
	public String getProductName()
	{
		return getStringAttributeValue(BASE_PRODNAME);
	}

	/**
	 * Set default product name
	 *
	 * @param value product name
	 */
	public void setDefaultProductName(String value)
	{
		setDefaultAttribute(BASE_PRODNAME, value);
	}

	/**
	 * Set product name
	 *
	 * @param value product name
	 */
	public void setProductName(String value)
	{
		setAttribute(BASE_PRODNAME, value);
	}

	/**
	 * Get product version
	 *
	 * @return product version
	 */
	public String getProductVersion()
	{
		return getStringAttributeValue(BASE_PRODVER);
	}

	/**
	 * Set default product version
	 *
	 * @param value product version
	 */
	public void setDefaultProductVersion(String value)
	{
		setDefaultAttribute(BASE_PRODVER, value);
	}

	/**
	 * Set product version
	 *
	 * @param value product version
	 */
	public void setProductVersion(String value)
	{
		setAttribute(BASE_PRODVER, value);
	}

	/**
	 * Get product manufacturer
	 *
	 * @return product manufacturer
	 */
	public String getProductManufacturer()
	{
		return getStringAttributeValue(BASE_PRODMAN);
	}

	/**
	 * Set default product manufacturer
	 *
	 * @param value product manufacturer
	 */
	public void setDefaultProductManufacturer(String value)
	{
		setDefaultAttribute(BASE_PRODMAN, value);
	}

	/**
	 * Set product manufacturer
	 *
	 * @param value product manufacturer
	 */
	public void setProductManufacturer(String value)
	{
		setAttribute(BASE_PRODMAN, value);
	}

	/**
	 * Set default attributes for a general company structure.
	 */
	protected void setDefault()
	{
		setDefaultStatus(null);
		setDefaultPriority(null);
		setDefaultImpact(null);
		setDefaultUrgency(null);
		setDefaultCustomerFirstName(null);
		setDefaultCustomerLastName(null);
		setDefaultCustomerBussinessPhone(null);
		setDefaultCustomerCompany(null);
		setDefaultCustomerOrganisation(null);
		setDefaultCustomerDepartment(null);
		setDefaultCustomerSite(null);
		setDefaultOperationalCategorizationTier1(null);
		setDefaultOperationalCategorizationTier2(null);
		setDefaultOperationalCategorizationTier3(null);
		setDefaultProductCategorizationTier1(null);
		setDefaultProductCategorizationTier2(null);
		setDefaultProductCategorizationTier3(null);
		setDefaultProductName(null);
		setDefaultProductVersion(null);
		setDefaultProductManufacturer(null);

		setDefaultAttribute(BASE_CUSTPERSONID, (String) null);

		if (containsAttributeField(BASE_SUMMARY)) getAttribute(BASE_SUMMARY).setLabel("Summary");
		if (containsAttributeField(BASE_NOTES)) getAttribute(BASE_NOTES).setLabel("Notes");
		if (containsAttributeField(BASE_STATUS)) getAttribute(BASE_STATUS).setLabel("Status");
		if (containsAttributeField(BASE_IMPACT)) getAttribute(BASE_IMPACT).setLabel("Impact");
		if (containsAttributeField(BASE_URGENCY)) getAttribute(BASE_URGENCY).setLabel("Urgency");
		if (containsAttributeField(BASE_PRIORITY)) getAttribute(BASE_PRIORITY).setLabel("Priority");
		if (containsAttributeField(BASE_CUSTPERSONID)) getAttribute(BASE_CUSTPERSONID).setLabel("Customer Person Id");
		if (containsAttributeField(BASE_CUSTFIRSTNAME)) getAttribute(BASE_CUSTFIRSTNAME).setLabel("Customer First Name");
		if (containsAttributeField(BASE_CUSTLASTNAME)) getAttribute(BASE_CUSTLASTNAME).setLabel("Customer last Name");
		if (containsAttributeField(BASE_CUSTBUSSINESSPHONE)) getAttribute(BASE_CUSTBUSSINESSPHONE).setLabel("Customer Business Phone");
		if (containsAttributeField(BASE_CUSTCOMPANY)) getAttribute(BASE_CUSTCOMPANY).setLabel("Customer Company");
		if (containsAttributeField(BASE_CUSTORGANISATION)) getAttribute(BASE_CUSTORGANISATION).setLabel("Customer Organisation");
		if (containsAttributeField(BASE_CUSTDEPARTMENT)) getAttribute(BASE_CUSTDEPARTMENT).setLabel("Customer Department");
		if (containsAttributeField(BASE_CUSTSITE)) getAttribute(BASE_CUSTSITE).setLabel("Customer Site");
		if (containsAttributeField(BASE_CATEGOP1)) getAttribute(BASE_CATEGOP1).setLabel("Operational Categorization Tier 1");
		if (containsAttributeField(BASE_CATEGOP2)) getAttribute(BASE_CATEGOP2).setLabel("Operational Categorization Tier 2");
		if (containsAttributeField(BASE_CATEGOP3)) getAttribute(BASE_CATEGOP3).setLabel("Operational Categorization Tier 3");
		if (containsAttributeField(BASE_CATEGPR1)) getAttribute(BASE_CATEGPR1).setLabel("Product Categorization Tier 1");
		if (containsAttributeField(BASE_CATEGPR2)) getAttribute(BASE_CATEGPR2).setLabel("Product Categorization Tier 2");
		if (containsAttributeField(BASE_CATEGPR3)) getAttribute(BASE_CATEGPR3).setLabel("Product Categorization Tier 3");
		if (containsAttributeField(BASE_PRODNAME)) getAttribute(BASE_PRODNAME).setLabel("Product Name");
		if (containsAttributeField(BASE_PRODVER)) getAttribute(BASE_PRODVER).setLabel("Product Model/Version");
		if (containsAttributeField(BASE_PRODMAN)) getAttribute(BASE_PRODMAN).setLabel("Product Manufacturer");
	}

	/**
	 * Delete all attributes and reset the entry id.
	 */
	public void clear()
	{
		super.clear();
		setDefault();
	}

	public abstract String toLargeString();

	public String toFullString()
	{
		String data = toLargeString();

		if (StringUtility.isNotEmpty(getEntryId())) data += " - " + getEntryId();
		else data += "]";

		return data;
	}

	public abstract List getWorkLog(ServerConnection arsession) throws AREasyException;
}
