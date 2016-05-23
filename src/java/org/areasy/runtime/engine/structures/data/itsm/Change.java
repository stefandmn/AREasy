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
 * Change record structure for Change Management application.
 */
public class Change extends Base
{
	public Change()
	{
		setFormName("CHG:Infrastructure Change");
		clear();
	}

	/**
	 * Create a new Change structure.
	 *
	 * @return new Change instance
	 */
	public CoreItem getInstance()
	{
		return new Change();
	}

	/**
	 * Get change id value.
	 *
	 * @return change id.
	 */
	public String getChangeNumber()
	{
		return getStringAttributeValue(CHG_CHANGENO);
	}

	/**
	 * Set change id.
	 *
	 * @param value change id
	 */
	public void setChangeNumber(String value)
	{
		setAttribute(CHG_CHANGENO, value);
	}

	/**
	 * Set change id.
	 *
	 * @param value change id
	 */
	public void setDefaultChangeNumber(String value)
	{
		setDefaultAttribute(CHG_CHANGENO, value);
	}

	/**
	 * Get change risk level value.
	 *
	 * @return change risk level.
	 */
	public String getRiskLevel()
	{
		return getStringAttributeValue(CHG_CHANGERISKLEVEL);
	}

	/**
	 * Set change risk level.
	 *
	 * @param value change risk level
	 */
	public void setDefaultRiskLevel(String value)
	{
		setDefaultAttribute(CHG_CHANGERISKLEVEL, value);
	}

	/**
	 * Set change risk level.
	 *
	 * @param value change risk level
	 */
	public void setRiskLevel(String value)
	{
		setAttribute(CHG_CHANGERISKLEVEL, value);
	}

	/**
	 * Set change risk level.
	 *
	 * @param value change risk level
	 */
	public void setDefaultRiskLevel(int value)
	{
		setDefaultAttribute(CHG_CHANGERISKLEVEL, new Integer(value));
	}

	/**
	 * Set change risk level.
	 *
	 * @param value change risk level
	 */
	public void setRiskLevel(int value)
	{
		setAttribute(CHG_CHANGERISKLEVEL, new Integer(value));
	}

	/**
	 * Get change classification service type value.
	 *
	 * @return change classification service type.
	 */
	public String getChangeClass()
	{
		return getStringAttributeValue(CHG_CHANGECLASS);
	}

	/**
	 * Set change classification service type.
	 *
	 * @param value change classification service type
	 */
	public void setDefaultChangeClass(String value)
	{
		setDefaultAttribute(CHG_CHANGECLASS, value);
	}

	/**
	 * Set change classification service type.
	 *
	 * @param value change classification service type
	 */
	public void setChangeClass(String value)
	{
		setAttribute(CHG_CHANGECLASS, value);
	}

	/**
	 * Set change classification service type.
	 *
	 * @param value change classification service type
	 */
	public void setDefaultChangeClass(int value)
	{
		setDefaultAttribute(CHG_CHANGECLASS, new Integer(value));
	}

	/**
	 * Set change classification service type.
	 *
	 * @param value change classification service type
	 */
	public void setChangeClass(int value)
	{
		setAttribute(CHG_CHANGECLASS, new Integer(value));
	}

	/**
	 * Get change region location value.
	 *
	 * @return change region location.
	 */
	public String getLocationRegion()
	{
		return getStringAttributeValue(CHG_LOCATIONREGION);
	}

	/**
	 * Set change region location.
	 *
	 * @param value region location
	 */
	public void setDefaultLocationRegion(String value)
	{
		setDefaultAttribute(CHG_LOCATIONREGION, value);
	}

	/**
	 * Set change region location.
	 *
	 * @param value change region location
	 */
	public void setLocationRegion(String value)
	{
		setAttribute(CHG_LOCATIONREGION, value);
	}

	/**
	 * Set change site location.
	 *
	 * @param value change site location
	 */
	public void setLocationSite(String value)
	{
		setAttribute(CHG_LOCATIONSITE, value);
	}

	/**
	 * Get change site location.
	 *
	 * @return change site location
	 */
	public String getLocationSite()
	{
		return getStringAttributeValue(CHG_LOCATIONSITE);
	}

	/**
	 * Set change site location
	 *
	 * @param value change site location
	 */
	public void setDefaultLocationSite(String value)
	{
		setDefaultAttribute(CHG_LOCATIONSITE, value);
	}

	/**
	 * Set change Location Company.
	 *
	 * @param value change Location Company
	 */
	public void setDefaultLocationCompany(String value)
	{
		setDefaultAttribute(CHG_LOCATIONCOMPANY, value);
	}

	/**
	 * Set change Location Company.
	 *
	 * @param value change Location Company
	 */
	public void setLocationCompany(String value)
	{
		setAttribute(CHG_LOCATIONCOMPANY, value);
	}

	/**
	 * Get change Location Company value.
	 *
	 * @return change Location Company.
	 */
	public String getLocationCompany()
	{
		return getStringAttributeValue(CHG_LOCATIONCOMPANY);
	}

	/**
	 * Get Assigned Change Manager Company value.
	 *
	 * @return Assigned Change Manager Company.
	 */
	public String getAssignedChangeManagerCompany()
	{
		return getStringAttributeValue(CHG_ASSIGNCMCOMPANY);
	}

	/**
	 * Set Assigned Change Manager Company.
	 *
	 * @param value Assigned Change Manager Company
	 */
	public void setDefaultAssignedChangeManagerCompany(String value)
	{
		setDefaultAttribute(CHG_ASSIGNCMCOMPANY, value);
	}

	/**
	 * Set Assigned Change Manager Company.
	 *
	 * @param value Assigned Change Manager Company
	 */
	public void setAssignedChangeManagerCompany(String value)
	{
		setAttribute(CHG_ASSIGNCMCOMPANY, value);
	}

	/**
	 * Get Assigned Change Manager Organisation value.
	 *
	 * @return Assigned Change Manager Organisation.
	 */
	public String getAssignedChangeManagerOrganisation()
	{
		return getStringAttributeValue(CHG_ASSIGNCMORGANISATION);
	}

	/**
	 * Set Assigned Change Manager Organisation.
	 *
	 * @param value Assigned Change Manager Organisation
	 */
	public void setDefaultAssignedChangeManagerOrganisation(String value)
	{
		setDefaultAttribute(CHG_ASSIGNCMORGANISATION, value);
	}

	/**
	 * Set Assigned Change Manager Organisation.
	 *
	 * @param value Assigned Change Manager Organisation
	 */
	public void setAssignedChangeManagerOrganisation(String value)
	{
		setAttribute(CHG_ASSIGNCMORGANISATION, value);
	}

	/**
	 * Get Assigned Change Manager Group value.
	 *
	 * @return Assigned Change Manager Group.
	 */
	public String getAssignedChangeManagerGroup()
	{
		return getStringAttributeValue(CHG_ASSIGNCMGROUP);
	}

	/**
	 * Set Assigned Change Manager Group.
	 *
	 * @param value Assigned Change Manager Group
	 */
	public void setDefaultAssignedChangeManagerGroup(String value)
	{
		setDefaultAttribute(CHG_ASSIGNCMGROUP, value);
	}

	/**
	 * Set Assigned Change Manager Group.
	 *
	 * @param value Assigned Change Manager Group
	 */
	public void setAssignedChangeManagerGroup(String value)
	{
		setAttribute(CHG_ASSIGNCMGROUP, value);
	}

	/**
	 * Get Assigned Change Manager Group ID value.
	 *
	 * @return Assigned Change Manager Group ID.
	 */
	public String getAssignedChangeManagerGroupId()
	{
		return getStringAttributeValue(CHG_ASSIGNCMGROUPID);
	}

	/**
	 * Set Assigned Change Manager Group ID.
	 *
	 * @param value Assigned Change Manager Group ID
	 */
	public void setDefaultAssignedChangeManagerGroupId(String value)
	{
		setDefaultAttribute(CHG_ASSIGNCMGROUPID, value);
	}

	/**
	 * Set Assigned Change Manager Group ID.
	 *
	 * @param value Assigned Change Manager Group ID
	 */
	public void setAssignedChangeManagerGroupId(String value)
	{
		setAttribute(CHG_ASSIGNCMGROUPID, value);
	}

	/**
	 * Get Assigned Change Manager Person value.
	 *
	 * @return Assigned Change Manager Person.
	 */
	public String getAssignedChangeManagerPerson()
	{
		return getStringAttributeValue(CHG_ASSIGNCMPERSON);
	}

	/**
	 * Set Assigned Change Manager Person.
	 *
	 * @param value Assigned Change Manager Person
	 */
	public void setDefaultAssignedChangeManagerPerson(String value)
	{
		setDefaultAttribute(CHG_ASSIGNCMPERSON, value);
	}

	/**
	 * Set Assigned Change Manager Person.
	 *
	 * @param value Assigned Change Manager Person
	 */
	public void setAssignedChangeManagerPerson(String value)
	{
		setAttribute(CHG_ASSIGNCMPERSON, value);
	}

	/**
	 * Get Assigned Change Manager Person Login ID value.
	 *
	 * @return Assigned Change Manager Person Login ID.
	 */
	public String getAssignedChangeManagerPersonLoginId()
	{
		return getStringAttributeValue(CHG_ASSIGNCMLOGINID);
	}

	/**
	 * Set Assigned Change Manager Person Login ID.
	 *
	 * @param value Assigned Change Manager Person Login ID
	 */
	public void setDefaultAssignedChangeManagerPersonLoginId(String value)
	{
		setDefaultAttribute(CHG_ASSIGNCMLOGINID, value);
	}

	/**
	 * Set Assigned Change Manager Person Login ID.
	 *
	 * @param value Assigned Change Manager Person Login ID
	 */
	public void setAssignedChangeManagerPersonLoginId(String value)
	{
		setAttribute(CHG_ASSIGNCMLOGINID, value);
	}

	/**
	 * Get Assigned Change Coordinator Company value.
	 *
	 * @return Assigned Change Coordinator Company.
	 */
	public String getAssignedChangeCoordinatorCompany()
	{
		return getStringAttributeValue(CHG_ASSIGNCCCOMPANY);
	}

	/**
	 * Set Assigned Change Coordinator Company.
	 *
	 * @param value Assigned Change Coordinator Company.
	 */
	public void setDefaultAssignedChangeCoordinatorCompany(String value)
	{
		setDefaultAttribute(CHG_ASSIGNCCCOMPANY, value);
	}

	/**
	 * Set Assigned Change Coordinator Company.
	 *
	 * @param value Assigned Change Coordinator Company.
	 */
	public void setAssignedChangeCoordinatorCompany(String value)
	{
		setAttribute(CHG_ASSIGNCCCOMPANY, value);
	}

	/**
	 * Get Assigned Change Coordinator Organisation value.
	 *
	 * @return Assigned Change Coordinator Organisation.
	 */
	public String getAssignedChangeCoordinatorOrganisation()
	{
		return getStringAttributeValue(CHG_ASSIGNCCORGANISATION);
	}

	/**
	 * Set Assigned Change Coordinator Organisation.
	 *
	 * @param value Assigned Change Coordinator Organisation.
	 */
	public void setDefaultAssignedChangeCoordinatorOrganisation(String value)
	{
		setDefaultAttribute(CHG_ASSIGNCCORGANISATION, value);
	}

	/**
	 * Set Assigned Change Coordinator Organisation.
	 *
	 * @param value Assigned Change Coordinator Organisation.
	 */
	public void setAssignedChangeCoordinatorOrganisation(String value)
	{
		setAttribute(CHG_ASSIGNCCORGANISATION, value);
	}

	/**
	 * Get Assigned Change Coordinator Group value.
	 *
	 * @return Assigned Change Coordinator Group.
	 */
	public String getAssignedChangeCoordinatorGroup()
	{
		return getStringAttributeValue(CHG_ASSIGNCCGROUP);
	}

	/**
	 * Set Assigned Change Coordinator Group.
	 *
	 * @param value Assigned Change Coordinator Group.
	 */
	public void setDefaultAssignedChangeCoordinatorGroup(String value)
	{
		setDefaultAttribute(CHG_ASSIGNCCGROUP, value);
	}

	/**
	 * SetAssigned Change Coordinator Group.
	 *
	 * @param value Assigned Change Coordinator Group.
	 */
	public void setAssignedChangeCoordinatorGroup(String value)
	{
		setAttribute(CHG_ASSIGNCCGROUP, value);
	}

	/**
	 * Get Assigned Change Coordinator Group ID value.
	 *
	 * @return Assigned Change Coordinator Group ID.
	 */
	public String getAssignedChangeCoordinatorGroupId()
	{
		return getStringAttributeValue(CHG_ASSIGNCCGROUPID);
	}

	/**
	 * Set Assigned Change Coordinator Group ID.
	 *
	 * @param value Assigned Change Coordinator Group ID.
	 */
	public void setDefaultAssignedChangeCoordinatorGroupId(String value)
	{
		setDefaultAttribute(CHG_ASSIGNCCGROUPID, value);
	}

	/**
	 * Set Assigned Change Coordinator Group ID.
	 *
	 * @param value Assigned Change Coordinator Group ID.
	 */
	public void setAssignedChangeCoordinatorGroupId(String value)
	{
		setAttribute(CHG_ASSIGNCCGROUPID, value);
	}

	/**
	 * Get Assigned Change Coordinator Person value.
	 *
	 * @return Assigned Change Coordinator Person.
	 */
	public String getAssignedChangeCoordinatorPerson()
	{
		return getStringAttributeValue(CHG_ASSIGNCCPERSON);
	}

	/**
	 * Set Assigned Change Coordinator Person.
	 *
	 * @param value Assigned Change Coordinator Person.
	 */
	public void setDefaultAssignedChangeCoordinatorPerson(String value)
	{
		setDefaultAttribute(CHG_ASSIGNCCPERSON, value);
	}

	/**
	 * Set Assigned Change Coordinator Person.
	 *
	 * @param value Assigned Change Coordinator Person.
	 */
	public void setAssignedChangeCoordinatorPerson(String value)
	{
		setAttribute(CHG_ASSIGNCCPERSON, value);
	}

	/**
	 * Get Assigned Change Coordinator Person value.
	 *
	 * @return Assigned Change Coordinator Person.
	 */
	public String getAssignedChangeCoordinatorLoginId()
	{
		return getStringAttributeValue(CHG_ASSIGNCCLOGINID);
	}

	/**
	 * Set Assigned Change Coordinator Person.
	 *
	 * @param value Assigned Change Coordinator Person.
	 */
	public void setDefaultAssignedChangeCoordinatorLoginId(String value)
	{
		setDefaultAttribute(CHG_ASSIGNCCLOGINID, value);
	}

	/**
	 * Set Assigned Change Coordinator Person.
	 *
	 * @param value Assigned Change Coordinator Person.
	 */
	public void setAssignedChangeCoordinatorLoginId(String value)
	{
		setAttribute(CHG_ASSIGNCCLOGINID, value);
	}

	/**
	 * Get change Assignment Company value.
	 *
	 * @return change Assignment Company.
	 */
	public String getAssignmentCompany()
	{
		return getStringAttributeValue(CHG_ASSIGNCOMPANY);
	}

	/**
	 * Set change Assignment Company.
	 *
	 * @param value change Assignment Company
	 */
	public void setDefaultAssignmentCompany(String value)
	{
		setDefaultAttribute(CHG_ASSIGNCOMPANY, value);
	}

	/**
	 * Set change Assignment Company.
	 *
	 * @param value change Assignment Company
	 */
	public void setAssignmentCompany(String value)
	{
		setAttribute(CHG_ASSIGNCOMPANY, value);
	}

	/**
	 * Get change Assignment Organisation value.
	 *
	 * @return change Assignment Organisation.
	 */
	public String getAssignmentOrganisation()
	{
		return getStringAttributeValue(CHG_ASSIGNORGANISATION);
	}

	/**
	 * Set change Assignment Organisation.
	 *
	 * @param value change Assignment Organisation
	 */
	public void setDefaultAssignmentOrganisation(String value)
	{
		setDefaultAttribute(CHG_ASSIGNORGANISATION, value);
	}

	/**
	 * Set change Assignment Organisation.
	 *
	 * @param value change Assignment Organisation
	 */
	public void setAssignmentOrganisation(String value)
	{
		setAttribute(CHG_ASSIGNORGANISATION, value);
	}

	/**
	 * Get change Assignment Group value.
	 *
	 * @return change Assignment Group.
	 */
	public String getAssignmentGroup()
	{
		return getStringAttributeValue(CHG_ASSIGNGROUP);
	}

	/**
	 * Set change Assignment Group.
	 *
	 * @param value change Assignment Group
	 */
	public void setDefaultAssignmentGroup(String value)
	{
		setDefaultAttribute(CHG_ASSIGNGROUP, value);
	}

	/**
	 * Set change Assignment Group.
	 *
	 * @param value change Assignment Group
	 */
	public void setAssignmentGroup(String value)
	{
		setAttribute(CHG_ASSIGNGROUP, value);
	}

	/**
	 * Get change Assignment Group ID value.
	 *
	 * @return change Assignment Group ID.
	 */
	public String getAssignmentGroupId()
	{
		return getStringAttributeValue(CHG_ASSIGNGROUPID);
	}

	/**
	 * Set change Assignment Group ID.
	 *
	 * @param value change Assignment Group ID
	 */
	public void setDefaultAssignmentGroupId(String value)
	{
		setDefaultAttribute(CHG_ASSIGNGROUPID, value);
	}

	/**
	 * Set change Assignment Group ID.
	 *
	 * @param value change Assignment Group ID
	 */
	public void setAssignmentGroupId(String value)
	{
		setAttribute(CHG_ASSIGNGROUPID, value);
	}

	/**
	 * Get change Assignment Person value.
	 *
	 * @return change Assignment Person.
	 */
	public String getAssignmentPerson()
	{
		return getStringAttributeValue(CHG_ASSIGNPERSON);
	}

	/**
	 * Set change Assignment Person.
	 *
	 * @param value change Assignment Person
	 */
	public void setDefaultAssignmentPerson(String value)
	{
		setDefaultAttribute(CHG_ASSIGNPERSON, value);
	}

	/**
	 * Set change Assignment Person.
	 *
	 * @param value change Assignment Person
	 */
	public void setAssignmentPerson(String value)
	{
		setAttribute(CHG_ASSIGNPERSON, value);
	}

	/**
	 * Get change Assignment Person Login ID value.
	 *
	 * @return change Assignment Person Login ID.
	 */
	public String getAssignmentPersonLoginId()
	{
		return getStringAttributeValue(CHG_ASSIGNLOGINID);
	}

	/**
	 * Set change Assignment Person Login ID.
	 *
	 * @param value change Assignment Person Login ID
	 */
	public void setDefaultAssignmentPersonLoginId(String value)
	{
		setDefaultAttribute(CHG_ASSIGNLOGINID, value);
	}

	/**
	 * Set change Assignment Person Login ID.
	 *
	 * @param value change Assignment Person Login ID
	 */
	public void setAssignmentPersonLoginId(String value)
	{
		setAttribute(CHG_ASSIGNLOGINID, value);
	}

	/**
	 * Set default attributes for a general company structure.
	 */
	protected void setDefault()
	{
		super.setDefault();

		setDefaultChangeNumber(null);
		setDefaultRiskLevel(null);
		setDefaultLocationRegion(null);
		setDefaultLocationCompany(null);
		setDefaultChangeClass(null);
		setDefaultAssignedChangeManagerCompany(null);
		setDefaultAssignedChangeManagerOrganisation(null);
		setDefaultAssignedChangeManagerGroup(null);
		setDefaultAssignedChangeManagerGroupId(null);
		setDefaultAssignedChangeManagerPerson(null);
		setDefaultAssignedChangeManagerPersonLoginId(null);
		setDefaultAssignedChangeCoordinatorCompany(null);
		setDefaultAssignedChangeCoordinatorOrganisation(null);
		setDefaultAssignedChangeCoordinatorGroup(null);
		setDefaultAssignedChangeCoordinatorGroupId(null);
		setDefaultAssignedChangeCoordinatorPerson(null);
		setDefaultAssignedChangeCoordinatorLoginId(null);
		setDefaultAssignmentCompany(null);
		setDefaultAssignmentOrganisation(null);
		setDefaultAssignmentGroup(null);
		setDefaultAssignmentGroupId(null);
		setDefaultAssignmentPerson(null);
		setDefaultAssignmentPersonLoginId(null);

		if (containsAttributeField(CHG_CHANGENO)) getAttribute(CHG_CHANGENO).setLabel("Change Number");
		if (containsAttributeField(CHG_LOCATIONCOMPANY)) getAttribute(CHG_LOCATIONCOMPANY).setLabel("Location for Company");
		if (containsAttributeField(CHG_ASSIGNCMCOMPANY)) getAttribute(CHG_ASSIGNCMCOMPANY).setLabel("Assigned Company");
		if (containsAttributeField(CHG_ASSIGNCMORGANISATION)) getAttribute(CHG_ASSIGNCMORGANISATION).setLabel("Assigned Organisation");
		if (containsAttributeField(CHG_ASSIGNCMGROUP)) getAttribute(CHG_ASSIGNCMGROUP).setLabel("Assigned Group");
		if (containsAttributeField(CHG_ASSIGNCMGROUPID)) getAttribute(CHG_ASSIGNCMGROUPID).setLabel("Assigned Group ID");
		if (containsAttributeField(CHG_ASSIGNCMPERSON)) getAttribute(CHG_ASSIGNCMPERSON).setLabel("Assigned Person");
		if (containsAttributeField(CHG_ASSIGNCMLOGINID)) getAttribute(CHG_ASSIGNCMLOGINID).setLabel("Assigned Person Login ID");
		if (containsAttributeField(CHG_ASSIGNCCCOMPANY)) getAttribute(CHG_ASSIGNCCCOMPANY).setLabel("Owner Company");
		if (containsAttributeField(CHG_ASSIGNCCORGANISATION)) getAttribute(CHG_ASSIGNCCORGANISATION).setLabel("Owner Organisation");
		if (containsAttributeField(CHG_ASSIGNCCGROUP)) getAttribute(CHG_ASSIGNCCGROUP).setLabel("Owner Group");
		if (containsAttributeField(CHG_ASSIGNCCGROUPID)) getAttribute(CHG_ASSIGNCCGROUPID).setLabel("Owner Group ID");
		if (containsAttributeField(CHG_ASSIGNCCPERSON)) getAttribute(CHG_ASSIGNCCPERSON).setLabel("Owner Person");
		if (containsAttributeField(CHG_CHANGECLASS)) getAttribute(CHG_CHANGECLASS).setLabel("Change Class");
		if (containsAttributeField(CHG_CHANGERISKLEVEL)) getAttribute(CHG_CHANGERISKLEVEL).setLabel("Risk Level");
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
		return "Change [Id = " + getChangeNumber() + ", Status = " + getStatus() +
			   ", Priority = " + getPriority() + ", Customer = " + getCustomerFirstName() + " " + getCustomerLastName() + ", Summary = " + getSummary() + "]";

	}

	public String toLargeString()
	{
		return "Change [Id = " + getChangeNumber() +
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
			   ", Company Location = " + getLocationCompany() +
			   ", Region Location = " + getLocationRegion() +
			   ", Site Location = " + getLocationSite() +
			   ", Assigned Change Manager Company = " + getAssignedChangeManagerCompany() +
			   ", Assigned Change Manager Organisation = " + getAssignedChangeManagerOrganisation() +
			   ", Assigned Change Manager Group = " + getAssignedChangeManagerGroup() +
			   ", Assigned Change Manager Person = " + getAssignedChangeManagerPerson() +
			   ", Assigned Change Coordinator Company = " + getAssignedChangeCoordinatorCompany() +
			   ", Assigned Change Coordinator Organisation = " + getAssignedChangeCoordinatorOrganisation() +
			   ", Assigned Change Coordinator Group = " + getAssignedChangeCoordinatorGroup() +
			   ", Assigned Change Coordinator Person = " + getAssignedChangeCoordinatorPerson() +
			   ", Change Class = " + getChangeClass() +
			   ", Risk Level = " + getRiskLevel() +
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
		item.setFormName("CHG:WorkLog");

		item.setAttribute(1000000182, getChangeNumber());
		return item.search(arsession);
	}
}
