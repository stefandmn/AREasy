package org.areasy.runtime.engine.structures.data.itsm;

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

import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.runtime.engine.base.ServerConnection;
import org.areasy.runtime.engine.structures.CoreItem;

import java.util.List;

/**
 * KnownError record structure for Problem Management application
 */
public class KnownError extends Base
{
	public KnownError()
	{
		setFormName("PBM:Known Error");
		clear();
	}

	/**
	 * Create a new known error structure.
	 *
	 * @return new known error instance
	 */
	public CoreItem getInstance()
	{
		return new KnownError();
	}

	/**
	 * Get known error id value.
	 *
	 * @return known error id.
	 */
	public String getKnownErrorId()
	{
		return getStringAttributeValue(PBM_KNOWNERRORID);
	}

	/**
	 * Set known error id.
	 *
	 * @param value known error id
	 */
	public void setKnownErrorId(String value)
	{
		setAttribute(PBM_KNOWNERRORID, value);
	}

	/**
	 * Set known error id.
	 *
	 * @param value known error id
	 */
	public void setDefaultKnownErrorId(String value)
	{
		setDefaultAttribute(PBM_KNOWNERRORID, value);
	}

	/**
	 * Get known error coordinator Company value.
	 *
	 * @return known error coordinator Company.
	 */
	public String getAssignedKnownErrorCoordinatorCompany()
	{
		return getStringAttributeValue(PBM_ASSIGNPCCOMPANY);
	}

	/**
	 * Set known error coordinator Company.
	 *
	 * @param value known error coordinator Company
	 */
	public void setDefaultAssignedKnownErrorCoordinatorCompany(String value)
	{
		setDefaultAttribute(PBM_ASSIGNPCCOMPANY, value);
	}

	/**
	 * Set known error coordinator Company.
	 *
	 * @param value known error coordinator Company
	 */
	public void setAssignedKnownErrorCoordinatorCompany(String value)
	{
		setAttribute(PBM_ASSIGNPCCOMPANY, value);
	}

	/**
	 * Get known error coordinator Organisation value.
	 *
	 * @return known error coordinator Organisation.
	 */
	public String getAssignedKnownErrorCoordinatorOrganisation()
	{
		return getStringAttributeValue(PBM_ASSIGNPCORGANISATION);
	}

	/**
	 * Set known error coordinator Organisation.
	 *
	 * @param value known error coordinator Organisation
	 */
	public void setDefaultAssignedKnownErrorCoordinatorOrganisation(String value)
	{
		setDefaultAttribute(PBM_ASSIGNPCORGANISATION, value);
	}

	/**
	 * Set known error coordinator Organisation.
	 *
	 * @param value known error coordinator Organisation
	 */
	public void setAssignedKnownErrorCoordinatorOrganisation(String value)
	{
		setAttribute(PBM_ASSIGNPCORGANISATION, value);
	}

	/**
	 * Get known error coordinator Group value.
	 *
	 * @return known error coordinator Group.
	 */
	public String getAssignedKnownErrorCoordinatorGroup()
	{
		return getStringAttributeValue(PBM_ASSIGNPCGROUP);
	}

	/**
	 * Set known error coordinator Group.
	 *
	 * @param value known error coordinator Group
	 */
	public void setDefaultAssignedKnownErrorCoordinatorGroup(String value)
	{
		setDefaultAttribute(PBM_ASSIGNPCGROUP, value);
	}

	/**
	 * Set known error coordinator Group.
	 *
	 * @param value known error coordinator Group
	 */
	public void setAssignedKnownErrorCoordinatorGroup(String value)
	{
		setAttribute(PBM_ASSIGNPCGROUP, value);
	}

	/**
	 * Get known error coordinator Group ID value.
	 *
	 * @return known error coordinator Group ID.
	 */
	public String getAssignedKnownErrorCoordinatorGroupId()
	{
		return getStringAttributeValue(PBM_ASSIGNPCGROUPID);
	}

	/**
	 * Set known error coordinator Group ID.
	 *
	 * @param value known error coordinator Group ID
	 */
	public void setDefaultAssignedKnownErrorCoordinatorGroupId(String value)
	{
		setDefaultAttribute(PBM_ASSIGNPCGROUPID, value);
	}

	/**
	 * Set known error coordinator Group ID.
	 *
	 * @param value known error coordinator Group ID
	 */
	public void setAssignedKnownErrorCoordinatorGroupId(String value)
	{
		setAttribute(PBM_ASSIGNPCGROUPID, value);
	}

	/**
	 * Get known error coordinator Person value.
	 *
	 * @return known error coordinator Person.
	 */
	public String getAssignedKnownErrorCoordinatorPerson()
	{
		return getStringAttributeValue(PBM_ASSIGNPCPERSON);
	}

	/**
	 * Set known error coordinator Person.
	 *
	 * @param value known error coordinator Person
	 */
	public void setDefaultAssignedKnownErrorCoordinatorPerson(String value)
	{
		setDefaultAttribute(PBM_ASSIGNPCPERSON, value);
	}

	/**
	 * Set known error coordinator Person.
	 *
	 * @param value known error coordinator Person
	 */
	public void setAssignedKnownErrorCoordinatorPerson(String value)
	{
		setAttribute(PBM_ASSIGNPCPERSON, value);
	}

	/**
	 * Get known error coordinator Person Login ID value.
	 *
	 * @return known error coordinator Person Login ID.
	 */
	public String getAssignedKnownErrorCoordinatorPersonLoginId()
	{
		return getStringAttributeValue(PBM_ASSIGNPCLOGINID);
	}

	/**
	 * Set known error coordinator Person Login ID.
	 *
	 * @param value known error coordinator Person Login ID
	 */
	public void setDefaultAssignedKnownErrorCoordinatorPersonLoginId(String value)
	{
		setDefaultAttribute(PBM_ASSIGNPCLOGINID, value);
	}

	/**
	 * Set known error coordinator Person Login ID.
	 *
	 * @param value known error coordinator Person Login ID
	 */
	public void setAssignedKnownErrorCoordinatorPersonLoginId(String value)
	{
		setAttribute(PBM_ASSIGNPCLOGINID, value);
	}

	/**
	 * Get known error assignment Company value.
	 *
	 * @return known error assignment Company.
	 */
	public String getAssignmentCompany()
	{
		return getStringAttributeValue(PBM_ASSIGNCOMPANY);
	}

	/**
	 * Set known error assignment Company.
	 *
	 * @param value known error assignment Company.
	 */
	public void setDefaultAssignmentCompany(String value)
	{
		setDefaultAttribute(PBM_ASSIGNCOMPANY, value);
	}

	/**
	 * Set known error assignment Company.
	 *
	 * @param value known error assignment Company.
	 */
	public void setAssignmentCompany(String value)
	{
		setAttribute(PBM_ASSIGNCOMPANY, value);
	}

	/**
	 * Get known error assignment Organisation value.
	 *
	 * @return known error assignment Organisation.
	 */
	public String getAssignmentOrganisation()
	{
		return getStringAttributeValue(PBM_ASSIGNORGANISATION);
	}

	/**
	 * Set known error assignment Organisation.
	 *
	 * @param value known error assignment Organisation.
	 */
	public void setDefaultAssignmentOrganisation(String value)
	{
		setDefaultAttribute(PBM_ASSIGNORGANISATION, value);
	}

	/**
	 * Set known error assignment Organisation.
	 *
	 * @param value known error assignment Organisation.
	 */
	public void setAssignmentOrganisation(String value)
	{
		setAttribute(PBM_ASSIGNORGANISATION, value);
	}

	/**
	 * Get known error assignment Group value.
	 *
	 * @return known error assignment Group.
	 */
	public String getAssignmentGroup()
	{
		return getStringAttributeValue(PBM_ASSIGNGROUP);
	}

	/**
	 * Set known error assignment Group.
	 *
	 * @param value known error assignment Group.
	 */
	public void setDefaultAssignmentGroup(String value)
	{
		setDefaultAttribute(PBM_ASSIGNGROUP, value);
	}

	/**
	 * Set known error assignment Group.
	 *
	 * @param value known error assignment Group.
	 */
	public void setAssignmentGroup(String value)
	{
		setAttribute(PBM_ASSIGNGROUP, value);
	}

	/**
	 * Get known error assignment Group ID value.
	 *
	 * @return known error assignment Group ID.
	 */
	public String getAssignmentGroupId()
	{
		return getStringAttributeValue(PBM_ASSIGNGROUPID);
	}

	/**
	 * Set known error assignment Group ID.
	 *
	 * @param value known error assignment Group ID.
	 */
	public void setDefaultAssignmentGroupId(String value)
	{
		setDefaultAttribute(PBM_ASSIGNGROUPID, value);
	}

	/**
	 * Set known error assignment Group ID.
	 *
	 * @param value known error assignment Group ID.
	 */
	public void setAssignmentGroupId(String value)
	{
		setAttribute(PBM_ASSIGNGROUPID, value);
	}

	/**
	 * Get known error assignment Person value.
	 *
	 * @return known error assignment Person.
	 */
	public String getAssignmentPerson()
	{
		return getStringAttributeValue(PBM_ASSIGNPERSON);
	}

	/**
	 * Set known error assignment Person.
	 *
	 * @param value known error assignment Person.
	 */
	public void setDefaultAssignmentPerson(String value)
	{
		setDefaultAttribute(PBM_ASSIGNPERSON, value);
	}

	/**
	 * Set known error assignment Person.
	 *
	 * @param value known error assignment Person.
	 */
	public void setAssignmentPerson(String value)
	{
		setAttribute(PBM_ASSIGNPERSON, value);
	}

	/**
	 * Get known error assignment Person value.
	 *
	 * @return known error assignment Person Login Id.
	 */
	public String getAssignmentPersonLoginId()
	{
		return getStringAttributeValue(PBM_ASSIGNLOGINID);
	}

	/**
	 * Set known error assignment Person.
	 *
	 * @param value known error assignment Person Login Id.
	 */
	public void setDefaultAssignmentPersonLoginId(String value)
	{
		setDefaultAttribute(PBM_ASSIGNLOGINID, value);
	}

	/**
	 * Set known error assignment Person.
	 *
	 * @param value known error assignment Person Login Id.
	 */
	public void setAssignmentPersonLoginId(String value)
	{
		setAttribute(PBM_ASSIGNLOGINID, value);
	}

	/**
	 * Get known error workaround value
	 *
	 * @return known error workaround
	 */
	public String getWorkaround()
	{
		return getStringAttributeValue(PBM_WORKAROUND);
	}

	/**
	 * Set known error workaround
	 *
	 * @param value known error workaround
	 */
	public void setDefaultWorkaround(String value)
	{
		setDefaultAttribute(PBM_WORKAROUND, value);
	}

	/**
	 * Set known error workaround
	 *
	 * @param value known error workaround
	 */
	public void setWorkaround(String value)
	{
		setAttribute(PBM_WORKAROUND, value);
	}

	/**
	 * Get known error root cause value
	 *
	 * @return known error root cause
	 */
	public String getRootCause()
	{
		return getStringAttributeValue(PBM_ROOTCAUSE);
	}

	/**
	 * Set known error root cause
	 *
	 * @param value known error root cause
	 */
	public void setDefaultRootCause(String value)
	{
		setDefaultAttribute(PBM_ROOTCAUSE, value);
	}

	/**
	 * Set known error root cause
	 *
	 * @param value known error root cause
	 */
	public void setRootCause(String value)
	{
		setAttribute(PBM_ROOTCAUSE, value);
	}

	/**
	 * Get known error investigation driver value.
	 *
	 * @return known error investigation driver.
	 */
	public String getCategory()
	{
		return getStringAttributeValue(PBM_KNECATEGORY);
	}

	/**
	 * Set known error investigation driver.
	 *
	 * @param value known error investigation driver
	 */
	public void setDefaultCategory(String value)
	{
		setDefaultAttribute(PBM_KNECATEGORY, value);
	}

	/**
	 * Set known error investigation driver.
	 *
	 * @param value known error investigation driver
	 */
	public void setCategory(String value)
	{
		setAttribute(PBM_KNECATEGORY, value);
	}

	/**
	 * Set known error investigation driver.
	 *
	 * @param value known error investigation driver
	 */
	public void setDefaultCategory(int value)
	{
		setDefaultAttribute(PBM_KNECATEGORY, new Integer(value));
	}

	/**
	 * Set known error investigation driver.
	 *
	 * @param value known error investigation driver
	 */
	public void setCategory(int value)
	{
		setAttribute(PBM_KNECATEGORY, new Integer(value));
	}

	/**
	 * Set default attributes for a general company structure.
	 */
	protected void setDefault()
	{
		super.setDefault();

		setDefaultKnownErrorId(null);
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
		setDefaultAssignmentCompany(null);
		setDefaultAssignmentOrganisation(null);
		setDefaultAssignmentGroup(null);
		setDefaultAssignmentGroupId(null);
		setDefaultAssignmentPerson(null);
		setDefaultWorkaround(null);
		setDefaultRootCause(null);
		setDefaultCategory(null);
		setDefaultAssignmentPersonLoginId(null);
		setDefaultAssignedKnownErrorCoordinatorCompany(null);
		setDefaultAssignedKnownErrorCoordinatorOrganisation(null);
		setDefaultAssignedKnownErrorCoordinatorGroup(null);
		setDefaultAssignedKnownErrorCoordinatorGroupId(null);
		setDefaultAssignedKnownErrorCoordinatorPerson(null);
		setDefaultAssignedKnownErrorCoordinatorPersonLoginId(null);

		if (containsAttributeField(PBM_KNOWNERRORID)) getAttribute(PBM_KNOWNERRORID).setLabel("Known Error ID");
		if (containsAttributeField(PBM_INVESTIGATIONDRV)) getAttribute(PBM_INVESTIGATIONDRV).setLabel("Investigation Driver");
		if (containsAttributeField(PBM_KNECATEGORY)) getAttribute(PBM_KNECATEGORY).setLabel("Category");
		if (containsAttributeField(PBM_ASSIGNPCCOMPANY)) getAttribute(PBM_ASSIGNPCCOMPANY).setLabel("Problem Coordinator Company");
		if (containsAttributeField(PBM_ASSIGNPCORGANISATION)) getAttribute(PBM_ASSIGNPCORGANISATION).setLabel("Problem Coordinator Organisation");
		if (containsAttributeField(PBM_ASSIGNPCGROUP)) getAttribute(PBM_ASSIGNPCGROUP).setLabel("Problem Coordinator Group");
		if (containsAttributeField(PBM_ASSIGNPCGROUPID)) getAttribute(PBM_ASSIGNPCGROUPID).setLabel("Problem Coordinator Group ID");
		if (containsAttributeField(PBM_ASSIGNPCPERSON)) getAttribute(PBM_ASSIGNPCPERSON).setLabel("Problem Coordinator Person");
		if (containsAttributeField(PBM_ASSIGNPCLOGINID)) getAttribute(PBM_ASSIGNPCLOGINID).setLabel("Problem Coordinator Person Login ID");
		if (containsAttributeField(PBM_ASSIGNCOMPANY)) getAttribute(PBM_ASSIGNCOMPANY).setLabel("Assignment Company");
		if (containsAttributeField(PBM_ASSIGNORGANISATION)) getAttribute(PBM_ASSIGNORGANISATION).setLabel("Assignment Organisation");
		if (containsAttributeField(PBM_ASSIGNGROUP)) getAttribute(PBM_ASSIGNGROUP).setLabel("Assignment Group");
		if (containsAttributeField(PBM_ASSIGNGROUPID)) getAttribute(PBM_ASSIGNGROUPID).setLabel("Assignment Group ID");
		if (containsAttributeField(PBM_ASSIGNPERSON)) getAttribute(PBM_ASSIGNPERSON).setLabel("Assignment Person");
		if (containsAttributeField(PBM_ASSIGNLOGINID)) getAttribute(PBM_ASSIGNLOGINID).setLabel("Assignment Person Login ID");
		if (containsAttributeField(PBM_WORKAROUND)) getAttribute(PBM_WORKAROUND).setLabel("Workaround");
		if (containsAttributeField(PBM_ROOTCAUSE)) getAttribute(PBM_ROOTCAUSE).setLabel("Root Cause");
	}

	/**
	 * Delete all attributes and reset the entry id.
	 */
	public void clear()
	{
		super.clear();
		setDefault();
	}

	public String toString()
	{
		return "Known Error [Id = " + getKnownErrorId() + ", Status = " + getStatus() +
			", Priority = " + getPriority() + ", Customer = " + getCustomerFirstName() + " " + getCustomerLastName() + ", Summary = " + getSummary() + "]";

	}

	public String toLargeString()
	{
		return "Known Error [Id = " + getKnownErrorId() +
			   ", Status = " + getStatus() +
			   ", Priority = " + getPriority() +
			   ", Impact = " + getImpact() +
			   ", Urgency = " + getUrgency() +
			   ", Customer First Name = " + getCustomerFirstName() +
			   ", Customer Last Name = " + getCustomerLastName() +
			   ", Customer Business Phone = " + getCustomerBussinessPhone() +
			   ", Customer Company = " + getCustomerCompany() +
			   ", Customer Organisation = " + getCustomerOrganisation() +
			   ", Customer Department = " + getCustomerDepartment() +
			   ", Assigned Problem Coordinator Company = " + getAssignedKnownErrorCoordinatorCompany() +
			   ", Assigned Problem Coordinator Organisation = " + getAssignedKnownErrorCoordinatorOrganisation() +
			   ", Assigned Problem Coordinator Group = " + getAssignedKnownErrorCoordinatorGroup() +
			   ", Assigned Problem Coordinator Person = " + getAssignedKnownErrorCoordinatorPerson() +
			   ", Operational Category Tier 1 = " + getOperationalCategorizationTier1() +
			   ", Operational Category Tier 2 = " + getOperationalCategorizationTier2() +
			   ", Operational Category Tier 3 = " + getOperationalCategorizationTier3() +
			   ", Product Category Tier 1 = " + getProductCategorizationTier1() +
			   ", Product Category Tier 2 = " + getProductCategorizationTier2() +
			   ", Product Category Tier 3 = " + getProductCategorizationTier3() +
			   ", Product Name = " + getProductName() +
			   ", Product Version = " + getProductVersion() +
			   ", Product Manufacturer = " + getProductManufacturer() + "]";
	}

	public List getWorkLog(ServerConnection arsession) throws AREasyException
	{
		CoreItem item = new CoreItem();
		item.setFormName("PBM:Known Error WorkLog");

		item.setAttribute(1000000232, getKnownErrorId());
		return item.search(arsession);
	}
}
