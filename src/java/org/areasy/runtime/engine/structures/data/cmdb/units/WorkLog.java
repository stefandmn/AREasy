package org.areasy.runtime.engine.structures.data.cmdb.units;

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

import com.bmc.arsys.api.AttachmentValue;
import com.bmc.arsys.api.Entry;
import com.bmc.arsys.api.Timestamp;
import org.areasy.runtime.RuntimeManager;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.runtime.engine.base.ServerConnection;
import org.areasy.runtime.engine.structures.CoreItem;

import java.io.File;
import java.util.Date;

/**
 * CI work info structure.
 */
public class WorkLog extends CoreItem
{
	protected void fetch(ServerConnection arsession, Entry entry) throws AREasyException
	{
		super.fetch(arsession, entry);

		setDefaultAttribute(1000000351, getAttachment(arsession, 1000000351));
		setDefaultAttribute(1000000352, getAttachment(arsession, 1000000352));
		setDefaultAttribute(1000000353, getAttachment(arsession, 1000000353));
	}

	protected AttachmentValue getAttachment(ServerConnection arsession, Integer fieldId) throws AREasyException
	{
		AttachmentValue download = (AttachmentValue) getAttributeValue(fieldId);

		if(download == null) return null;

		try
		{
			String attachementName = download.getValueFileName();

			if(attachementName.lastIndexOf("\\") >= 0) attachementName = attachementName.substring(attachementName.lastIndexOf("\\") + 1);
				else if(attachementName.lastIndexOf("/") >= 0) attachementName = attachementName.substring(attachementName.lastIndexOf("/") + 1);

			File file = new File(RuntimeManager.getWorkingDirectory(), attachementName);
			if(file.exists()) file.delete();

			arsession.getContext().getEntryBlob(getFormName(), getEntryId(), fieldId, file.getPath());

			return new AttachmentValue(file.getPath());
		}
		catch(Throwable th)
		{
			throw new AREasyException("Error extracting attachment from " + getEntryId() + " source entry id: " + th.getMessage(), th);
		}
	}

	/**
	 * Default constructor
	 */
	public WorkLog()
	{
		setFormName("AST:WorkLog");
	}

	/**
	 * Create a new instance of core item structure.
	 *
	 * @return new instance of <code>CoreItem</code> structure
	 */
	public CoreItem getInstance()
	{
		return new WorkLog();
	}

	/**
	 * Get string representation of the current core item structure.
	 *
	 * @return string data model.
	 */
	public String toString()
	{
		return "CI Work Log [CI Id = " + getAstId() + ", Submitter = " + getSubmitter() +
				", Submit Date = " +  getSubmitDate() + ", Summary = " + getAttributeValue(1000000000) + "]";
	}

	/**
	 * Get incident id attribute value.
	 *
	 * @return incident id.
	 */
	public String getAstId()
	{
		return getStringAttributeValue(1000002670);
	}

	/**
	 * Set incident id.
	 *
	 * @param id incident id
	 */
	public void setAstId(String id)
	{
		setAttribute(1000002670, id);
	}

	/**
	 * Get submitter attribute value.
	 *
	 * @return submitter name.
	 */
	public String getSubmitter()
	{
		return getStringAttributeValue(1000000159);
	}

	/**
	 * Set submitter name.
	 *
	 * @param value submitter name
	 */
	public void setSubmitter(String value)
	{
		setAttribute(1000000159, value);
	}

	/**
	 * Get submit date attribute value.
	 *
	 * @return submit date.
	 */
	public Timestamp getSubmitDate()
	{
		return (Timestamp) getAttributeValue(1000000157);
	}

	/**
	 * Set submit date.
	 *
	 * @param value submit date
	 */
	public void setSubmitDate(Date value)
	{
		setAttribute(1000000157, value);
	}

	/**
	 * Set submit date.
	 *
	 * @param value submit date
	 */
	public void setSubmitDate(long value)
	{
		setAttribute(1000000157, new Long(value));
	}

	/**
	 * Set submit date.
	 *
	 * @param value submit date
	 */
	public void setSubmitDate(Timestamp value)
	{
		setAttribute(1000000157, value);
	}

	/**
	 * Get status attribute value.
	 *
	 * @return status value.
	 */
	public String getStatus()
	{
		return getStringAttributeValue(7);
	}

	/**
	 * Set status value.
	 *
	 * @param value status value
	 */
	public void setStatus(String value)
	{
		setAttribute(7, value);
	}

	/**
	 * Set status value.
	 *
	 * @param value status value
	 */
	public void setStatus(int value)
	{
		setAttribute(7, new Integer(value));
	}

	/**
	 * Get type attribute value.
	 *
	 * @return type name.
	 */
	public String getType()
	{
		return getStringAttributeValue(1000000170);
	}

	/**
	 * Set type name.
	 *
	 * @param value type name
	 */
	public void setType(String value)
	{
		setAttribute(1000000170, value);
	}

	/**
	 * Set type value.
	 *
	 * @param value type value
	 */
	public void setType(int value)
	{
		setAttribute(1000000170, new Integer(value));
	}

	/**
	 * Get locked attribute value.
	 *
	 * @return locked name.
	 */
	public String getLocked()
	{
		return getStringAttributeValue(1000001476);
	}

	/**
	 * Set locked name.
	 *
	 * @param value locked name
	 */
	public void setLocked(String value)
	{
		setAttribute(1000001476, value);
	}

	/**
	 * Set locked value.
	 *
	 * @param value locked value
	 */
	public void setLocked(int value)
	{
		setAttribute(1000001476, new Integer(value));
	}

	/**
	 * Get view access attribute value.
	 *
	 * @return view access value.
	 */
	public String getViewAccess()
	{
		return getStringAttributeValue(1000000761);
	}

	/**
	 * Set view access value.
	 *
	 * @param value view access
	 */
	public void setViewAccess(String value)
	{
		setAttribute(1000000761, value);
	}

	/**
	 * Set view access value.
	 *
	 * @param value view access value
	 */
	public void setViewAccess(int value)
	{
		setAttribute(1000000761, new Integer(value));
	}

	/**
	 * Get summary attribute value.
	 *
	 * @return summary value.
	 */
	public String getSummary()
	{
		return getStringAttributeValue(1000000000);
	}

	/**
	 * Set summary value.
	 *
	 * @param value summary value
	 */
	public void setSummary(String value)
	{
		setAttribute(1000000000, value);
	}

	/**
	 * Get notes attribute value.
	 *
	 * @return notes value.
	 */
	public String getNotes()
	{
		return getStringAttributeValue(1000000151);
	}

	/**
	 * Set notes value.
	 *
	 * @param value notes value
	 */
	public void setNotes(String value)
	{
		setAttribute(1000000151, value);
	}
}

