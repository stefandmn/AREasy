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
 * Problem record structure for Problem Management application
 */
public class Problem extends Base
{
	public Problem()
	{
		setFormName("PBM:Problem Investigation");
		clear();
	}

	/**
	 * Create a new Problem structure.
	 *
	 * @return new Problem instance
	 */
	public CoreItem getInstance()
	{
		return new Problem();
	}

	/**
	 * Get problem id value.
	 *
	 * @return problem id.
	 */
	public String getProblemNumber()
	{
		return getStringAttributeValue(PBM_PROBLEMNO);
	}

	/**
	 * Set problem id.
	 *
	 * @param value problem id
	 */
	public void setProblemNumber(String value)
	{
		setAttribute(PBM_PROBLEMNO, value);
	}

	/**
	 * Set problem id.
	 *
	 * @param value problem id
	 */
	public void setDefaultProblemNumber(String value)
	{
		setDefaultAttribute(PBM_PROBLEMNO, value);
	}

	/**
	 * Get problem investigation driver value.
	 *
	 * @return problem investigation driver.
	 */
	public String getInvestigationDriver()
	{
		return getStringAttributeValue(PBM_INVESTIGATIONDRV);
	}

	/**
	 * Set problem investigation driver.
	 *
	 * @param value problem investigation driver
	 */
	public void setDefaultInvestigationDriver(String value)
	{
		setDefaultAttribute(PBM_INVESTIGATIONDRV, value);
	}

	/**
	 * Set problem investigation driver.
	 *
	 * @param value problem investigation driver
	 */
	public void setInvestigationDriver(String value)
	{
		setAttribute(PBM_INVESTIGATIONDRV, value);
	}

	/**
	 * Set problem investigation driver.
	 *
	 * @param value problem investigation driver
	 */
	public void setDefaultInvestigationDriver(int value)
	{
		setDefaultAttribute(PBM_INVESTIGATIONDRV, new Integer(value));
	}

	/**
	 * Set problem investigation driver.
	 *
	 * @param value problem investigation driver
	 */
	public void setInvestigationDriver(int value)
	{
		setAttribute(PBM_INVESTIGATIONDRV, new Integer(value));
	}

	/**
	 * Get problem coordinator Company value.
	 *
	 * @return problem coordinator Company.
	 */
	public String getAssignedProblemCoordinatorCompany()
	{
		return getStringAttributeValue(PBM_ASSIGNPCCOMPANY);
	}

	/**
	 * Set problem coordinator Company.
	 *
	 * @param value problem coordinator Company
	 */
	public void setDefaultAssignedProblemCoordinatorCompany(String value)
	{
		setDefaultAttribute(PBM_ASSIGNPCCOMPANY, value);
	}

	/**
	 * Set problem coordinator Company.
	 *
	 * @param value problem coordinator Company
	 */
	public void setAssignedProblemCoordinatorCompany(String value)
	{
		setAttribute(PBM_ASSIGNPCCOMPANY, value);
	}

	/**
	 * Get problem coordinator Organisation value.
	 *
	 * @return problem coordinator Organisation.
	 */
	public String getAssignedProblemCoordinatorOrganisation()
	{
		return getStringAttributeValue(PBM_ASSIGNPCORGANISATION);
	}

	/**
	 * Set problem coordinator Organisation.
	 *
	 * @param value problem coordinator Organisation
	 */
	public void setDefaultAssignedProblemCoordinatorOrganisation(String value)
	{
		setDefaultAttribute(PBM_ASSIGNPCORGANISATION, value);
	}

	/**
	 * Set problem coordinator Organisation.
	 *
	 * @param value problem coordinator Organisation
	 */
	public void setAssignedProblemCoordinatorOrganisation(String value)
	{
		setAttribute(PBM_ASSIGNPCORGANISATION, value);
	}

	/**
	 * Get problem coordinator Group value.
	 *
	 * @return problem coordinator Group.
	 */
	public String getAssignedProblemCoordinatorGroup()
	{
		return getStringAttributeValue(PBM_ASSIGNPCGROUP);
	}

	/**
	 * Set problem coordinator Group.
	 *
	 * @param value problem coordinator Group
	 */
	public void setDefaultAssignedProblemCoordinatorGroup(String value)
	{
		setDefaultAttribute(PBM_ASSIGNPCGROUP, value);
	}

	/**
	 * Set problem coordinator Group.
	 *
	 * @param value problem coordinator Group
	 */
	public void setAssignedProblemCoordinatorGroup(String value)
	{
		setAttribute(PBM_ASSIGNPCGROUP, value);
	}

	/**
	 * Get problem coordinator Group ID value.
	 *
	 * @return problem coordinator Group ID.
	 */
	public String getAssignedProblemCoordinatorGroupId()
	{
		return getStringAttributeValue(PBM_ASSIGNPCGROUPID);
	}

	/**
	 * Set problem coordinator Group ID.
	 *
	 * @param value problem coordinator Group ID
	 */
	public void setDefaultAssignedProblemCoordinatorGroupId(String value)
	{
		setDefaultAttribute(PBM_ASSIGNPCGROUPID, value);
	}

	/**
	 * Set problem coordinator Group ID.
	 *
	 * @param value problem coordinator Group ID
	 */
	public void setAssignedProblemCoordinatorGroupId(String value)
	{
		setAttribute(PBM_ASSIGNPCGROUPID, value);
	}

	/**
	 * Get problem coordinator Person value.
	 *
	 * @return problem coordinator Person.
	 */
	public String getAssignedProblemCoordinatorPerson()
	{
		return getStringAttributeValue(PBM_ASSIGNPCPERSON);
	}

	/**
	 * Set problem coordinator Person.
	 *
	 * @param value problem coordinator Person
	 */
	public void setDefaultAssignedProblemCoordinatorPerson(String value)
	{
		setDefaultAttribute(PBM_ASSIGNPCPERSON, value);
	}

	/**
	 * Set problem coordinator Person.
	 *
	 * @param value problem coordinator Person
	 */
	public void setAssignedProblemCoordinatorPerson(String value)
	{
		setAttribute(PBM_ASSIGNPCPERSON, value);
	}

	/**
	 * Get problem coordinator Person Login ID value.
	 *
	 * @return problem coordinator Person Login ID.
	 */
	public String getAssignedProblemCoordinatorPersonLoginId()
	{
		return getStringAttributeValue(PBM_ASSIGNPCLOGINID);
	}

	/**
	 * Set problem coordinator Person Login ID.
	 *
	 * @param value problem coordinator Person Login ID
	 */
	public void setDefaultAssignedProblemCoordinatorPersonLoginId(String value)
	{
		setDefaultAttribute(PBM_ASSIGNPCLOGINID, value);
	}

	/**
	 * Set problem coordinator Person Login ID.
	 *
	 * @param value problem coordinator Person Login ID
	 */
	public void setAssignedProblemCoordinatorPersonLoginId(String value)
	{
		setAttribute(PBM_ASSIGNPCLOGINID, value);
	}

	/**
	 * Get problem assignment Company value.
	 *
	 * @return problem assignment Company.
	 */
	public String getAssignmentCompany()
	{
		return getStringAttributeValue(PBM_ASSIGNCOMPANY);
	}

	/**
	 * Set problem assignment Company.
	 *
	 * @param value problem assignment Company.
	 */
	public void setDefaultAssignmentCompany(String value)
	{
		setDefaultAttribute(PBM_ASSIGNCOMPANY, value);
	}

	/**
	 * Set problem assignment Company.
	 *
	 * @param value problem assignment Company.
	 */
	public void setAssignmentCompany(String value)
	{
		setAttribute(PBM_ASSIGNCOMPANY, value);
	}

	/**
	 * Get problem assignment Organisation value.
	 *
	 * @return problem assignment Organisation.
	 */
	public String getAssignmentOrganisation()
	{
		return getStringAttributeValue(PBM_ASSIGNORGANISATION);
	}

	/**
	 * Set problem assignment Organisation.
	 *
	 * @param value problem assignment Organisation.
	 */
	public void setDefaultAssignmentOrganisation(String value)
	{
		setDefaultAttribute(PBM_ASSIGNORGANISATION, value);
	}

	/**
	 * Set problem assignment Organisation.
	 *
	 * @param value problem assignment Organisation.
	 */
	public void setAssignmentOrganisation(String value)
	{
		setAttribute(PBM_ASSIGNORGANISATION, value);
	}

	/**
	 * Get problem assignment Group value.
	 *
	 * @return problem assignment Group.
	 */
	public String getAssignmentGroup()
	{
		return getStringAttributeValue(PBM_ASSIGNGROUP);
	}

	/**
	 * Set problem assignment Group.
	 *
	 * @param value problem assignment Group.
	 */
	public void setDefaultAssignmentGroup(String value)
	{
		setDefaultAttribute(PBM_ASSIGNGROUP, value);
	}

	/**
	 * Set problem assignment Group.
	 *
	 * @param value problem assignment Group.
	 */
	public void setAssignmentGroup(String value)
	{
		setAttribute(PBM_ASSIGNGROUP, value);
	}

	/**
	 * Get problem assignment Group ID value.
	 *
	 * @return problem assignment Group ID.
	 */
	public String getAssignmentGroupId()
	{
		return getStringAttributeValue(PBM_ASSIGNGROUPID);
	}

	/**
	 * Set problem assignment Group ID.
	 *
	 * @param value problem assignment Group ID.
	 */
	public void setDefaultAssignmentGroupId(String value)
	{
		setDefaultAttribute(PBM_ASSIGNGROUPID, value);
	}

	/**
	 * Set problem assignment Group ID.
	 *
	 * @param value problem assignment Group ID.
	 */
	public void setAssignmentGroupId(String value)
	{
		setAttribute(PBM_ASSIGNGROUPID, value);
	}

	/**
	 * Get problem assignment Person value.
	 *
	 * @return problem assignment Person.
	 */
	public String getAssignmentPerson()
	{
		return getStringAttributeValue(PBM_ASSIGNPERSON);
	}

	/**
	 * Set problem assignment Person.
	 *
	 * @param value problem assignment Person.
	 */
	public void setDefaultAssignmentPerson(String value)
	{
		setDefaultAttribute(PBM_ASSIGNPERSON, value);
	}

	/**
	 * Set problem assignment Person.
	 *
	 * @param value problem assignment Person.
	 */
	public void setAssignmentPerson(String value)
	{
		setAttribute(PBM_ASSIGNPERSON, value);
	}

	/**
	 * Get problem assignment Person value.
	 *
	 * @return problem assignment Person Login Id.
	 */
	public String getAssignmentPersonLoginId()
	{
		return getStringAttributeValue(PBM_ASSIGNLOGINID);
	}

	/**
	 * Set problem assignment Person.
	 *
	 * @param value problem assignment Person Login Id.
	 */
	public void setDefaultAssignmentPersonLoginId(String value)
	{
		setDefaultAttribute(PBM_ASSIGNLOGINID, value);
	}

	/**
	 * Set problem assignment Person.
	 *
	 * @param value problem assignment Person Login Id.
	 */
	public void setAssignmentPersonLoginId(String value)
	{
		setAttribute(PBM_ASSIGNLOGINID, value);
	}

	/**
	 * Get problem workaround value
	 *
	 * @return problem workaround
	 */
	public String getWorkaround()
	{
		return getStringAttributeValue(PBM_WORKAROUND);
	}

	/**
	 * Set problem workaround
	 *
	 * @param value problem workaround
	 */
	public void setDefaultWorkaround(String value)
	{
		setDefaultAttribute(PBM_WORKAROUND, value);
	}

	/**
	 * Set problem workaround
	 *
	 * @param value problem workaround
	 */
	public void setWorkaround(String value)
	{
		setAttribute(PBM_WORKAROUND, value);
	}

	/**
	 * Get problem root cause value
	 *
	 * @return problem root cause
	 */
	public String getRootCause()
	{
		return getStringAttributeValue(PBM_ROOTCAUSE);
	}

	/**
	 * Set problem root cause
	 *
	 * @param value problem root cause
	 */
	public void setDefaultRootCause(String value)
	{
		setDefaultAttribute(PBM_ROOTCAUSE, value);
	}

	/**
	 * Set problem root cause
	 *
	 * @param value problem root cause
	 */
	public void setRootCause(String value)
	{
		setAttribute(PBM_ROOTCAUSE, value);
	}

	/**
	 * Set default attributes for a general company structure.
	 */
	protected void setDefault()
	{
		super.setDefault();

		setDefaultProblemNumber(null);
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
		setDefaultInvestigationDriver(null);
		setDefaultAssignmentCompany(null);
		setDefaultAssignmentOrganisation(null);
		setDefaultAssignmentGroup(null);
		setDefaultAssignmentGroupId(null);
		setDefaultAssignmentPerson(null);
		setDefaultWorkaround(null);
		setDefaultRootCause(null);
		setDefaultAssignmentPersonLoginId(null);
		setDefaultAssignedProblemCoordinatorCompany(null);
		setDefaultAssignedProblemCoordinatorOrganisation(null);
		setDefaultAssignedProblemCoordinatorGroup(null);
		setDefaultAssignedProblemCoordinatorGroupId(null);
		setDefaultAssignedProblemCoordinatorPerson(null);
		setDefaultAssignedProblemCoordinatorPersonLoginId(null);

		if (containsAttributeField(PBM_PROBLEMNO)) getAttribute(PBM_PROBLEMNO).setLabel("Problem Number");
		if (containsAttributeField(PBM_INVESTIGATIONDRV)) getAttribute(PBM_INVESTIGATIONDRV).setLabel("Investigation Driver");
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
		return "Problem [Id = " + getProblemNumber() + ", Status = " + getStatus() +
			", Priority = " + getPriority() + ", Customer = " + getCustomerFirstName() + " " + getCustomerLastName() + ", Summary = " + getSummary() + "]";

	}

	public String toLargeString()
	{
		return "Problem [Id = " + getProblemNumber() +
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
			   ", Investigation Driver = " + getInvestigationDriver() +
			   ", Assigned Problem Coordinator Company = " + getAssignedProblemCoordinatorCompany() +
			   ", Assigned Problem Coordinator Organisation = " + getAssignedProblemCoordinatorOrganisation() +
			   ", Assigned Problem Coordinator Group = " + getAssignedProblemCoordinatorGroup() +
			   ", Assigned Problem Coordinator Person = " + getAssignedProblemCoordinatorPerson() +
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
		item.setFormName("PBM:Investigation WorkLog");

		item.setAttribute(1000000232, getProblemNumber());
		return item.search(arsession);
	}
}
