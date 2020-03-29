package org.areasy.runtime.actions.flow.sources;

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

import com.bmc.arsys.api.*;
import org.areasy.common.data.NumberUtility;
import org.areasy.runtime.RuntimeManager;
import org.areasy.runtime.engine.RuntimeLogger;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.runtime.engine.base.ServerConnection;
import org.areasy.runtime.engine.structures.Attribute;
import org.areasy.runtime.engine.structures.CoreItem;

import java.io.File;
import java.util.*;

/**
 * Remedy data-source handles data from Remedy forms that will be processed to execute AAR actions.
 */
public class RemedySource extends AbstractSource
{
	private String formname = null;
	private String qualification = null;
	private ServerConnection connection = null;

	private int sourceIndex = 0;
	private List searchList = null;
	private String lastRequestId = null;

	/**
	 * Dedicated method that has to be used internally, to set and validate the data-source configuration (<code>CoreItem</code> structure)
	 *
	 * @throws AREasyException in case of any error will occur
	 */
	public void init() throws AREasyException
	{
		String serverName = getSourceItem().getStringAttributeValue(536871008);
		String serverPort = getSourceItem().getStringAttributeValue(536871011);
		String userName = getSourceItem().getStringAttributeValue(536871006);
		String userPassword = getSourceItem().getStringAttributeValue(536871005);

		String form = getSourceItem().getStringAttributeValue(536870980);
		this.qualification = getSourceItem().getStringAttributeValue(536871013);

		this.connection = new ServerConnection();
		this.connection.connect(serverName, userName, userPassword, NumberUtility.toInt(serverPort), getAction().getConfiguration().getInt("rpcqueue", 0), getAction().getConfiguration().getBoolean("overlay", true));

		this.formname = form;

		//validations
		if(formname == null)  throw new AREasyException("Data Form to AR System data source is null: " + formname);
		if(connection == null || !connection.isConnected()) throw new AREasyException("Connection to AR System data source is invalid: " + getTargetServerConnection());
	}

	/**
	 * Dedicated method to release resources that are used by a data-source
	 *
	 * @throws AREasyException in case of any error will occur
	 */
	public void release() throws AREasyException
	{
		if(getTargetServerConnection() != null && getTargetServerConnection().isConnected())
		{
			getTargetServerConnection().disconnect();
		}
	}

	/**
	 * Take and deliver through a <code>Map</code> structure the data headers from the selected data-source.
	 *
	 * @return a <code>Map</code> with data-source headers.
	 * @throws AREasyException in case of any error will occur
	 */
	public Map getHeaders() throws AREasyException
	{
		Map map = new Hashtable();

		try
		{
			List<Field> fields = getTargetServerConnection().getContext().getListFieldObjects(this.formname, Constants.AR_FIELD_TYPE_DATA);

			for(int i = 0; i < fields.size(); i++)
			{
				Field field = fields.get(i);
				map.put(field.getName(), String.valueOf(field.getFieldID()));
			}
		}
		catch(ARException are)
		{
			throw new AREasyException(are);
		}

		return map;
	}

	/**
	 * Get Remedy server connection instance.
	 *
	 * @return connection instance to the AR System server
	 */
	public ServerConnection getTargetServerConnection()
	{
		return this.connection;
	}

	/**
	 * Get string qualification that will be used to selected that from the specified data form.
	 *
	 * @return qualification to search data in Remedy form
	 */
	public String getTargetQualification()
	{
		return this.qualification;
	}

	/**
	 * Take and deliver through a <code>Map</code> structure the data read it from
	 * the selected data-source. If the output is null means that the data-source goes to the end.
	 *
	 * @param list this is the list of data source keys.
	 * @return a <code>Map</code> having data source indexes as keys and data as values.
	 * @throws AREasyException in case of any error will occur
	 */
	public Map getNextObject(List list) throws AREasyException
	{
		Map map = null;

		if(searchList == null)
		{
			//qualification for the current session
			String sessionQualification = null;

			//get input details to generate session qualification criteria
			String requestIdQualification = null;
			String remoteQualification = getTargetQualification();
			int chunksize = getAction().getConfiguration().getInt("chunksize", getAction().getManager().getConfiguration().getInt("app.runtime.action." + getAction().getCode() + ".workflow.search.chunksize", 100));

			//translate qualification
			if(remoteQualification != null) remoteQualification = getAction().getTranslatedQualification(remoteQualification);

			//build part of qualification which should delivered next chunks.
			if(lastRequestId != null)
			{
				requestIdQualification = "'1' > \"" + lastRequestId + "\"";
				getAction().getLogger().debug("Create next qualification part using Request ID: " + lastRequestId);
			}

			//build the complete qualification
			if(remoteQualification != null)
			{
				if(requestIdQualification != null) sessionQualification = "(" + remoteQualification + ") AND (" + requestIdQualification + ")";
					else sessionQualification = remoteQualification;
			}
			else
			{
				if(requestIdQualification != null) sessionQualification =  requestIdQualification;
					else sessionQualification = "'1' != $NULL$";
			}

			//search data
			CoreItem source = new CoreItem();
			source.setFormName(formname);
			source.setSortInfo(new SortInfo(1, Constants.AR_SORT_ASCENDING));

			searchList = source.search(getTargetServerConnection(), sessionQualification, chunksize);
		}

		//take the object and maintain the search session details.
		if(searchList != null && sourceIndex < searchList.size())
		{
			map = new HashMap();
			CoreItem source = (CoreItem) searchList.get(sourceIndex);

			sourceIndex++;
			if(source != null)
			{
				lastRequestId = source.getEntryId();

				//validate sourceIndex
				if(searchList.size() == sourceIndex)
				{
					//make null original source for a new search
					searchList = null;

					//reset sourceIndex
					sourceIndex = 0;
				}
			}

			if(source != null)
			{
				if(list != null)
				{
					//get values
					for(int i = 0; i < list.size(); i++)
					{
						String fieldId = (String) list.get(i);

						if(fieldId != null)
						{
							setAttribute2Map(map, fieldId, source);
						}
					}
				}
				else
				{
					map.put("1", source.getEntryId());
					Iterator iterator = source.getAttributes().iterator();

					while(iterator != null && iterator.hasNext())
					{
						Attribute attr = (Attribute) iterator.next();

						if(attr != null)
						{
							String fieldId = attr.getId();

							if(fieldId != null)
							{
								setAttribute2Map(map, fieldId, source);
							}
						}
					}
				}

				return map;
			}
			else return null;
		}
		else return null;
	}

	protected void setAttribute2Map(Map map, String fieldId, CoreItem source)
	{
		if(map == null || fieldId == null || source == null) return;

		Object fieldValue = source.getAttributeValue(fieldId);

		//handle special cases: attachments
		if(fieldValue != null && fieldValue instanceof AttachmentValue)
		{
			AttachmentValue download = (AttachmentValue) fieldValue;

			try
			{
				String attachmentName = download.getValueFileName();
				if(attachmentName.lastIndexOf("\\") >= 0) attachmentName = attachmentName.substring(attachmentName.lastIndexOf("\\") + 1);
					else if(attachmentName.lastIndexOf("/") >= 0) attachmentName = attachmentName.substring(attachmentName.lastIndexOf("/") + 1);

				File file = new File(RuntimeManager.getWorkingDirectory(), attachmentName);
				if(file.exists()) file.delete();

				connection.getContext().getEntryBlob(source.getFormName(), source.getEntryId(), new Integer(NumberUtility.toInt(fieldId)), file.getPath());

				AttachmentValue upload = new AttachmentValue(download.getName(), file.getPath());
				map.put(fieldId, upload);
			}
			catch(Throwable th)
			{
				RuntimeLogger.warn("Error extracting attachment from " + source.getEntryId() + " source entry id: " + th.getMessage());
				getAction().getLogger().debug("Exception" + th);
			}
		}
		else map.put(fieldId, source.getAttributeValue(fieldId));
	}

	/**
	 * Read and return the total number of records found in the data-source.
	 * This input have to be optimized for long data-sources
	 *
	 * @return number of records found
	 */
	public int getDataCount()
	{
		int numberOfRows = 0;

		if(getTargetServerConnection() != null)
		{
			String remoteQualification = getTargetQualification();

			//translate qualification
			if(remoteQualification != null) remoteQualification = getAction().getTranslatedQualification(remoteQualification);

			CoreItem source = new CoreItem();
			source.setFormName(formname);

			try
			{
				numberOfRows = source.count(getTargetServerConnection(), remoteQualification);
			}
			catch(AREasyException are)
			{
				RuntimeLogger.info("Error getting number of records: " + are.getMessage());
				getAction().getLogger().debug("Exception", are);
			}
		}

		return numberOfRows;
	}
}
