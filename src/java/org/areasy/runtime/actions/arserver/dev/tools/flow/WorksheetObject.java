package org.areasy.runtime.actions.arserver.dev.tools.flow;

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

import com.bmc.arsys.api.*;
import org.areasy.runtime.engine.RuntimeLogger;
import org.areasy.runtime.engine.base.ServerConnection;
import org.areasy.common.data.StringUtility;
import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;

import java.util.List;

public class WorksheetObject
{
	/** Library logger */
	protected static Logger logger = LoggerFactory.getLog(WorksheetObject.class);

	private String signature;
	private String action;
	private String type;
	private String objectName;
	private String relatedData;
	private String notes;

	public WorksheetObject(String data[])
	{
		if(data == null || data.length < 6) throw new RuntimeException("Invalid input data structure for Development Package process");

		setSignature(data[0]);
		setAction(data[1]);
		setDevelopmentType(data[2]);
		setObjectName(data[3]);
		setRelatedData(data[4]);
		setNotes(data[5]);
	}

	public WorksheetObject(StructItemInfo info)
	{
		if(info == null) throw new RuntimeException("Invalid input data structure for Development Package process");

		setAction(WorksheetEvent.ACT_MODIFY);
		setSignature( WorksheetEvent.getObjectSignatureByType(info.getType()) );
		setObjectName( info.getName() );
		setRelatedData( StringUtility.join(info.getSelectedElements(), "\n") );
	}

	public String getSignature()
	{
		return signature;
	}

	public void setSignature(String signature)
	{
		this.signature = signature;
	}

	public String getObjectName()
	{
		return objectName;
	}

	public void setObjectName(String objectName)
	{
		this.objectName = objectName;
	}

	public String getRelatedData()
	{
		return relatedData;
	}

	public void setRelatedData(String relatedData)
	{
		this.relatedData = relatedData;
	}

	public boolean isOverlay()
	{
		return StringUtility.equalsIgnoreCase("overlay", this.type);
	}

	public boolean isCustom()
	{
		return StringUtility.equalsIgnoreCase("custom", this.type);
	}

	public void setDevelopmentType(String devtype)
	{
		if(StringUtility.equalsIgnoreCase("overlay", devtype)) setOverlay();
			else if(StringUtility.equalsIgnoreCase("custom", devtype)) setCustom();
				else this.type = null;
	}

	public void setOverlay()
	{
		this.type = "overlay";
	}

	public void setCustom()
	{
		this.type = "custom";
	}

	public String getNotes()
	{
		return notes;
	}

	public void setNotes(String notes)
	{
		this.notes = notes;
	}

	public String getAction()
	{
		return action;
	}

	public void setAction(String action)
	{
		this.action = action;
	}

	public StructItemInfo getStructItemInfo(ServerConnection connection)
	{
		StructItemInfo info = new StructItemInfo(WorksheetEvent.getObjectTypeIdBySignature(signature), getObjectName(), null);

		if(info.getType() == StructItemInfo.VUI) info = getViewStructItemInfo(info, connection);
			else if(info.getType() == StructItemInfo.FIELD) info = getFieldStructItemInfo(info, connection);

		return info;
	}

	/**
	 * Enrich <code>StructItemInfo</code> structure with <Code>View</Code> details.
	 * @param info <code>StructItemInfo</code> structure
	 * @param connection server connection session
	 * @return the same <code>StructItemInfo</code> structure but with secondary element(s) that could be
	 * one or more view names
	 */
	protected StructItemInfo getViewStructItemInfo(StructItemInfo info, ServerConnection connection)
	{
		if(info == null) return null;

		if(info.getType() == StructItemInfo.VUI)
		{
			if(StringUtility.isEmpty( getRelatedData() ))
			{
				if(connection != null)
				{
					try
					{
						ViewCriteria criteria = new ViewCriteria();
						criteria.setRetrieveAll(true);

						List views = connection.getContext().getListViewObjects(getObjectName(), 0, criteria);

						String viewNames[] = new String[views.size()];
						for(int i = 0; i < views.size(); i++) viewNames[i] = ((View)views.get(i)).getName();

						info.setSelectedElements(viewNames);
					}
					catch(Exception e)
					{
						RuntimeLogger.error("Error reading form views: " + e.getMessage());
						logger.debug("Exception", e);
					}
				}
				else
				{
					RuntimeLogger.warn("Could not read view names for form '" + getObjectName() + "'. The object(s) will be skipped");
					return null;
				}
			}
			else
			{
				String viewNames[] = StringUtility.split(getRelatedData(), '\n');
				info.setSelectedElements(viewNames);
			}
		}

		return info;
	}

	/**
	 * Enrich <code>StructItemInfo</code> structure with <Code>Field</Code> details.
	 * @param info <code>StructItemInfo</code> structure
	 * @param connection server connection session
	 * @return the same <code>StructItemInfo</code> structure but with secondary element(s) that could be
	 * one or more field names
	 */
	protected StructItemInfo getFieldStructItemInfo(StructItemInfo info, ServerConnection connection)
	{
		if(info == null) return null;

		if(info.getType() == StructItemInfo.FIELD)
		{
			if(StringUtility.isEmpty( getRelatedData() ))
			{
				if(connection != null)
				{
					try
					{
						FieldCriteria criteria = new FieldCriteria();
						criteria.setRetrieveAll(true);

						List fields = connection.getContext().getListFieldObjects(getObjectName(), Constants.AR_FIELD_TYPE_ALL, 0, criteria);

						String fieldNames[] = new String[fields.size()];
						for(int i = 0; i < fields.size(); i++) fieldNames[i] = ((Field)fields.get(i)).getName();

						info.setSelectedElements(fieldNames);
					}
					catch(Exception e)
					{
						RuntimeLogger.error("Error reading form fields: " + e.getMessage());
						logger.debug("Exception", e);
					}
				}
				else
				{
					RuntimeLogger.warn("Could not read field names for form '" + getObjectName() + "'. The object(s) will be skipped");
					return null;
				}
			}
			else
			{
				String fieldNames[] = StringUtility.split(getRelatedData(), '\n');
				info.setSelectedElements(fieldNames);
			}
		}

		return info;
	}

	public String toString()
	{
		return "[" + getSignature() + " - " + getObjectName() + (getRelatedData() != null ? "(" + StringUtility.join(StringUtility.split(getRelatedData(), '\n'), ',') + ")" : "" ) + "]";
	}
}
