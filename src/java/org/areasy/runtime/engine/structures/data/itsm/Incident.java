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
 * Incident record structure for Incident Management application
 */
public class Incident extends Base
{
	public Incident()
	{
		setFormName("HPD:Help Desk");
		clear();
	}

	/**
	 * Create a new Incident structure.
	 *
	 * @return new Incident instance
	 */
	public CoreItem getInstance()
	{
		return new Incident();
	}

	/**
	 * Get incident id value.
	 *
	 * @return incident id.
	 */
	public String getIncidentNumber()
	{
		return getStringAttributeValue(INC_INCIDENTNO);
	}

	/**
	 * Set incident id.
	 *
	 * @param value incident id
	 */
	public void setIncidentNumber(String value)
	{
		setAttribute(INC_INCIDENTNO, value);
	}

	/**
	 * Set incident id.
	 *
	 * @param value incident id
	 */
	public void setDefaultIncidentNumber(String value)
	{
		setDefaultAttribute(INC_INCIDENTNO, value);
	}

	/**
	 * Get incident weight value.
	 *
	 * @return incident weight.
	 */
	public String getWeight()
	{
		return getStringAttributeValue(INC_WEIGHT);
	}

	/**
	 * Set incident weight.
	 *
	 * @param value incident weight
	 */
	public void setDefaultWeight(String value)
	{
		setDefaultAttribute(INC_WEIGHT, value);
	}

	/**
	 * Set incident weight.
	 *
	 * @param value incident weight
	 */
	public void setWeight(String value)
	{
		setAttribute(INC_WEIGHT, value);
	}

	/**
	 * Set incident weight.
	 *
	 * @param value incident weight
	 */
	public void setDefaultWeight(int value)
	{
		setDefaultAttribute(INC_WEIGHT, new Integer(value));
	}

	/**
	 * Set incident weight.
	 *
	 * @param value incident weight
	 */
	public void setWeight(int value)
	{
		setAttribute(INC_WEIGHT, new Integer(value));
	}

	/**
	 * Set incident Classification Company.
	 *
	 * @param value incident Classification Company
	 */
	public void setDefaultClassificationCompany(String value)
	{
		setDefaultAttribute(INC_CLASSCOMPANY, value);
	}

	/**
	 * Set incident Classification Company.
	 *
	 * @param value incident Classification Company
	 */
	public void setClassificationCompany(String value)
	{
		setAttribute(INC_CLASSCOMPANY, value);
	}

	/**
	 * Get incident Classification Company value.
	 *
	 * @return incident Classification Company.
	 */
	public String getClassificationCompany()
	{
		return getStringAttributeValue(INC_CLASSCOMPANY);
	}

	/**
	 * Get incident Classification service type value.
	 *
	 * @return incident Classification service type.
	 */
	public String getClassificationServiceType()
	{
		return getStringAttributeValue(INC_CLASSSERVICETYPE);
	}

	/**
	 * Set incident Classification service type.
	 *
	 * @param value incident Classification service type
	 */
	public void setDefaultClassificationServiceType(String value)
	{
		setDefaultAttribute(INC_CLASSSERVICETYPE, value);
	}

	/**
	 * Set incident Classification service type.
	 *
	 * @param value incident Classification service type
	 */
	public void setClassificationServiceType(String value)
	{
		setAttribute(INC_CLASSSERVICETYPE, value);
	}

	/**
	 * Set incident Classification service type.
	 *
	 * @param value incident Classification service type
	 */
	public void setDefaultClassificationServiceType(int value)
	{
		setDefaultAttribute(INC_CLASSSERVICETYPE, new Integer(value));
	}

	/**
	 * Set incident Classification service type.
	 *
	 * @param value incident Classification service type
	 */
	public void setClassificationServiceType(int value)
	{
		setAttribute(INC_CLASSSERVICETYPE, new Integer(value));
	}

	/**
	 * Get incident Assignment Company value.
	 *
	 * @return incident Assignment Company.
	 */
	public String getAssignmentCompany()
	{
		return getStringAttributeValue(INC_ASSIGNCOMPANY);
	}

	/**
	 * Set incident Assignment Company.
	 *
	 * @param value incident Assignment Company
	 */
	public void setDefaultAssignmentCompany(String value)
	{
		setDefaultAttribute(INC_ASSIGNCOMPANY, value);
	}

	/**
	 * Set incident Assignment Company.
	 *
	 * @param value incident Assignment Company
	 */
	public void setAssignmentCompany(String value)
	{
		setAttribute(INC_ASSIGNCOMPANY, value);
	}

	/**
	 * Get incident Assignment Organisation value.
	 *
	 * @return incident Assignment Organisation.
	 */
	public String getAssignmentOrganisation()
	{
		return getStringAttributeValue(INC_ASSIGNORGANISATION);
	}

	/**
	 * Set incident Assignment Organisation.
	 *
	 * @param value incident Assignment Organisation
	 */
	public void setDefaultAssignmentOrganisation(String value)
	{
		setDefaultAttribute(INC_ASSIGNORGANISATION, value);
	}

	/**
	 * Set incident Assignment Organisation.
	 *
	 * @param value incident Assignment Organisation
	 */
	public void setAssignmentOrganisation(String value)
	{
		setAttribute(INC_ASSIGNORGANISATION, value);
	}

	/**
	 * Get incident Assignment Group value.
	 *
	 * @return incident Assignment Group.
	 */
	public String getAssignmentGroup()
	{
		return getStringAttributeValue(INC_ASSIGNGROUP);
	}

	/**
	 * Set incident Assignment Group.
	 *
	 * @param value incident Assignment Group
	 */
	public void setDefaultAssignmentGroup(String value)
	{
		setDefaultAttribute(INC_ASSIGNGROUP, value);
	}

	/**
	 * Set incident Assignment Group.
	 *
	 * @param value incident Assignment Group
	 */
	public void setAssignmentGroup(String value)
	{
		setAttribute(INC_ASSIGNGROUP, value);
	}

	/**
	 * Get incident Assignment Group ID value.
	 *
	 * @return incident Assignment Group ID.
	 */
	public String getAssignmentGroupId()
	{
		return getStringAttributeValue(INC_ASSIGNGROUPID);
	}

	/**
	 * Set incident Assignment Group ID.
	 *
	 * @param value incident Assignment Group ID
	 */
	public void setDefaultAssignmentGroupId(String value)
	{
		setDefaultAttribute(INC_ASSIGNGROUPID, value);
	}

	/**
	 * Set incident Assignment Group ID.
	 *
	 * @param value incident Assignment Group ID
	 */
	public void setAssignmentGroupId(String value)
	{
		setAttribute(INC_ASSIGNGROUPID, value);
	}

	/**
	 * Get incident Assignment Person value.
	 *
	 * @return incident Assignment Person.
	 */
	public String getAssignmentPerson()
	{
		return getStringAttributeValue(INC_ASSIGNPERSON);
	}

	/**
	 * Set incident Assignment Person.
	 *
	 * @param value incident Assignment Person
	 */
	public void setDefaultAssignmentPerson(String value)
	{
		setDefaultAttribute(INC_ASSIGNPERSON, value);
	}

	/**
	 * Set incident Assignment Person.
	 *
	 * @param value incident Assignment Person
	 */
	public void setAssignmentPerson(String value)
	{
		setAttribute(INC_ASSIGNPERSON, value);
	}

	/**
	 * Get incident Assignment Person Login ID value.
	 *
	 * @return incident Assignment Person Login ID.
	 */
	public String getAssignmentPersonLoginId()
	{
		return getStringAttributeValue(INC_ASSIGNLOGINID);
	}

	/**
	 * Set incident Assignment Person Login ID.
	 *
	 * @param value incident Assignment Person Login ID
	 */
	public void setDefaultAssignmentPersonLoginId(String value)
	{
		setDefaultAttribute(INC_ASSIGNLOGINID, value);
	}

	/**
	 * Set incident Assignment Person Login ID.
	 *
	 * @param value incident Assignment Person Login ID
	 */
	public void setAssignmentPersonLoginId(String value)
	{
		setAttribute(INC_ASSIGNLOGINID, value);
	}

	/**
	 * Get incident Owner Company value.
	 *
	 * @return incident Owner Company.
	 */
	public String getOwnerCompany()
	{
		return getStringAttributeValue(INC_OWNERCOMPANY);
	}

	/**
	 * Set incident Owner Company.
	 *
	 * @param value incident Owner Company.
	 */
	public void setDefaultOwnerCompany(String value)
	{
		setDefaultAttribute(INC_OWNERCOMPANY, value);
	}

	/**
	 * Set incident Owner Company.
	 *
	 * @param value incident Owner Company.
	 */
	public void setOwnerCompany(String value)
	{
		setAttribute(INC_OWNERCOMPANY, value);
	}

	/**
	 * Get incident Owner Organisation value.
	 *
	 * @return incident Owner Organisation.
	 */
	public String getOwnerOrganisation()
	{
		return getStringAttributeValue(INC_OWNERORGANISATION);
	}

	/**
	 * Set incident Owner Organisation.
	 *
	 * @param value incident Owner Organisation.
	 */
	public void setDefaultOwnerOrganisation(String value)
	{
		setDefaultAttribute(INC_OWNERORGANISATION, value);
	}

	/**
	 * Set incident Owner Organisation.
	 *
	 * @param value incident Owner Organisation.
	 */
	public void setOwnerOrganisation(String value)
	{
		setAttribute(INC_OWNERORGANISATION, value);
	}

	/**
	 * Get incident Owner Group value.
	 *
	 * @return incident Owner Group.
	 */
	public String getOwnerGroup()
	{
		return getStringAttributeValue(INC_OWNERGROUP);
	}

	/**
	 * Set incident Owner Group.
	 *
	 * @param value incident Owner Group.
	 */
	public void setDefaultOwnerGroup(String value)
	{
		setDefaultAttribute(INC_OWNERGROUP, value);
	}

	/**
	 * Set incident Owner Group.
	 *
	 * @param value incident Owner Group.
	 */
	public void setOwnerGroup(String value)
	{
		setAttribute(INC_OWNERGROUP, value);
	}

	/**
	 * Get incident Owner Group ID value.
	 *
	 * @return incident Owner Group ID.
	 */
	public String getOwnerGroupId()
	{
		return getStringAttributeValue(INC_OWNERGROUPID);
	}

	/**
	 * Set incident Owner Group ID.
	 *
	 * @param value incident Owner Group ID.
	 */
	public void setDefaultOwnerGroupId(String value)
	{
		setDefaultAttribute(INC_OWNERGROUPID, value);
	}

	/**
	 * Set incident Owner Group ID.
	 *
	 * @param value incident Owner Group ID.
	 */
	public void setOwnerGroupId(String value)
	{
		setAttribute(INC_OWNERGROUPID, value);
	}

	/**
	 * Get incident Owner Person value.
	 *
	 * @return incident Owner Person.
	 */
	public String getOwnerPerson()
	{
		return getStringAttributeValue(INC_OWNERPERSON);
	}

	/**
	 * Set incident Owner Person.
	 *
	 * @param value incident Owner Person.
	 */
	public void setDefaultOwnerPerson(String value)
	{
		setDefaultAttribute(INC_OWNERPERSON, value);
	}

	/**
	 * Set incident Owner Person.
	 *
	 * @param value incident Owner Person.
	 */
	public void setOwnerPerson(String value)
	{
		setAttribute(INC_OWNERPERSON, value);
	}

	/**
	 * Set default attributes for a general company structure.
	 */
	protected void setDefault()
	{
		super.setDefault();

		setDefaultIncidentNumber(null);
		setDefaultClassificationCompany(null);
		setDefaultClassificationServiceType(null);
		setDefaultAssignmentCompany(null);
		setDefaultAssignmentOrganisation(null);
		setDefaultAssignmentGroup(null);
		setDefaultAssignmentGroupId(null);
		setDefaultAssignmentPerson(null);
		setDefaultAssignmentPersonLoginId(null);
		setDefaultOwnerCompany(null);
		setDefaultOwnerOrganisation(null);
		setDefaultOwnerGroup(null);
		setDefaultOwnerGroupId(null);
		setDefaultOwnerPerson(null);

		if (containsAttributeField(INC_INCIDENTNO)) getAttribute(INC_INCIDENTNO).setLabel("Incident Number");
		if (containsAttributeField(INC_WEIGHT)) getAttribute(INC_WEIGHT).setLabel("Weight");
		if (containsAttributeField(INC_CLASSCOMPANY)) getAttribute(INC_CLASSCOMPANY).setLabel("Classification for Company");
		if (containsAttributeField(INC_CLASSSERVICETYPE)) getAttribute(INC_CLASSSERVICETYPE).setLabel("Classification for Service Type");
		if (containsAttributeField(INC_ASSIGNCOMPANY)) getAttribute(INC_ASSIGNCOMPANY).setLabel("Assigned Company");
		if (containsAttributeField(INC_ASSIGNORGANISATION)) getAttribute(INC_ASSIGNORGANISATION).setLabel("Assigned Organisation");
		if (containsAttributeField(INC_ASSIGNGROUP)) getAttribute(INC_ASSIGNGROUP).setLabel("Assigned Group Name");
		if (containsAttributeField(INC_ASSIGNGROUPID)) getAttribute(INC_ASSIGNGROUPID).setLabel("Assigned Group ID");
		if (containsAttributeField(INC_ASSIGNPERSON)) getAttribute(INC_ASSIGNPERSON).setLabel("Assigned Person");
		if (containsAttributeField(INC_ASSIGNLOGINID)) getAttribute(INC_ASSIGNLOGINID).setLabel("Assigned Person Login ID");
		if (containsAttributeField(INC_OWNERCOMPANY)) getAttribute(INC_OWNERCOMPANY).setLabel("Owner Company");
		if (containsAttributeField(INC_OWNERORGANISATION)) getAttribute(INC_OWNERORGANISATION).setLabel("Owner Organisation");
		if (containsAttributeField(INC_OWNERGROUP)) getAttribute(INC_OWNERGROUP).setLabel("Owner Group");
		if (containsAttributeField(INC_OWNERGROUPID)) getAttribute(INC_OWNERGROUPID).setLabel("Owner Group ID");
		if (containsAttributeField(INC_OWNERPERSON)) getAttribute(INC_OWNERPERSON).setLabel("Owner Person");
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
		return "Incident [Id = " + getIncidentNumber() + ", Status = " + getStatus() + ", Instance Id = " + getInstanceId() +
			   ", Priority = " + getPriority() + ", Customer = " + getCustomerFirstName() + " " + getCustomerLastName() + ", Summary = " + getSummary() + "]";
	}

	public String toLargeString()
	{
		return "Incident [Id = " + getIncidentNumber() +
			   ", Status = " + getStatus() +
			   ", Instance Id = " + getInstanceId() +
			   ", Priority = " + getPriority() +
			   ", Impact = " + getImpact() +
			   ", Urgency = " + getUrgency() +
			   ", Weight = " + getWeight() +
			   ", Customer First Name = " + getCustomerFirstName() +
			   ", Customer Last Name = " + getCustomerLastName() +
			   ", Customer Business Phone = " + getCustomerBussinessPhone() +
			   ", Customer Company = " + getCustomerCompany() +
			   ", Customer Organisation = " + getCustomerOrganisation() +
			   ", Customer Department = " + getCustomerDepartment() +
			   ", Customer Site = " + getCustomerSite() +
			   ", Assigned Company = " + getAssignmentCompany() +
			   ", Assigned Organisation = " + getAssignmentOrganisation() +
			   ", Assigned Group = " + getAssignmentGroup() +
			   ", Assigned Person = " + getAssignmentPerson() +
			   ", Company Classification = " + getClassificationCompany() +
			   ", Service Type Classification = " + getClassificationServiceType() +
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
		item.setFormName("HPD:WorkLog");

		item.setAttribute(1000000161, getIncidentNumber());
		return item.search(arsession);
	}
}
