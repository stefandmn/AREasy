package org.areasy.runtime.actions.flow.events;

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
import org.areasy.common.data.CharUtility;
import org.areasy.common.data.StringUtility;
import org.areasy.runtime.RuntimeManager;
import org.areasy.runtime.engine.RuntimeLogger;
import org.areasy.runtime.engine.base.ARDictionary;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.runtime.engine.structures.CoreItem;
import org.areasy.runtime.engine.workflows.ProcessorLevel2CmdbApp;
import org.areasy.runtime.utilities.StreamUtility;

import java.io.File;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Vector;

/**
 * Dedicated event (from data process workflow) to export all related data to a job
 * in order to be migrated to another ARSystem server where is installed AAR engine.
 */
public class RunJobExportEvent extends AbstractEvent
{
	/**
	 * Execute event
	 *
	 * @throws org.areasy.runtime.engine.base.AREasyException if any error occurs
	 */
	public void execute() throws AREasyException
	{
		//disable notification
		getAction().getConfiguration().setKey("notification", "false");

		Integer status = null;
		List fields = new Vector();
		StringBuffer buffer = new StringBuffer();

		//get and validate job instance id
		String instanceId = getAction().getJobEntry().getStringAttributeValue(179);
		if(instanceId == null) throw new AREasyException("No job found");

		//update job
		status = (Integer)getAction().getJobEntry().getAttributeValue(7);

		//put status on standby
		if(status == 1)
		{
			getAction().getJobEntry().setAttribute(7, new Integer(0));
			getAction().getJobEntry().update(getAction().getServerConnection());
		}

		//create export context
		fields.clear();
		setExportMeta(ARDictionary.FORM_RUNTIME_ENTRY, buffer, fields);
		setExportData(ARDictionary.FORM_RUNTIME_ENTRY, buffer, fields, instanceId);

		fields.clear();
		setExportMeta(ARDictionary.FORM_DATA_SOURCE, buffer, fields);
		setExportData(ARDictionary.FORM_DATA_SOURCE, buffer, fields, instanceId);

		fields.clear();
		setExportMeta(ARDictionary.FORM_DATA_MAPPING, buffer, fields);
		setExportData(ARDictionary.FORM_DATA_MAPPING, buffer, fields, instanceId);

		//put status on running again
		if(status == 1)
		{
			getAction().getJobEntry().setAttribute(7, new Integer(1));
			getAction().getJobEntry().update(getAction().getServerConnection());
		}

		//get job name
		String jobName = getAction().getJobEntry().getStringAttributeValue(2430);
		String jobCat = getAction().getJobEntry().getStringAttributeValue(536871097);

		String jobVarName = StringUtility.replace(jobName, " ", "");
		jobVarName = StringUtility.escapeSpecialChars(jobVarName);

		if(StringUtility.isNotEmpty(jobCat))
		{
			String jobVarCat = StringUtility.replace(jobCat, " ", "");
			jobVarCat = StringUtility.variable(jobVarCat);

			jobVarName = jobVarCat + "@" + jobVarName;
		}

		//create export file
		File jobFile = new File(RuntimeManager.getWorkingDirectory(), jobVarName + ".arx");
		if(jobFile.exists()) jobFile.delete();
		StreamUtility.writeTextFile("UTF-8", jobFile, buffer.toString());

		//save export in Output form
		String outputId = ProcessorLevel2CmdbApp.getStringInstanceId(getAction().getServerConnection());
		CoreItem output = new CoreItem(ARDictionary.FORM_RUNTIME_OUTPUT);
		output.setAttribute(7, new Integer(0));
		output.setAttribute(8, "EXPORT: " + jobName);
		output.setAttribute(179, outputId);
		output.setAttribute(536870914, jobFile);
		output.create(getAction().getServerConnection());

		//return the instance id of the output
		RuntimeLogger.clearData();
		RuntimeLogger.add(outputId);
		RuntimeLogger.info("Data export workflow processed " + getAction().getRecordsCounter() + " record(s)");
	}

	protected void setExportMeta(String formname, StringBuffer buffer, List ids) throws AREasyException
	{
		if(ids == null) ids = new Vector();
		if(buffer == null) buffer = new StringBuffer();

		buffer.append("CHAR-SET utf-8");
		buffer.append(CharUtility.CR);

		buffer.append("SCHEMA \"" + formname + "\"");
		buffer.append(CharUtility.CR);

		try
		{
			List<Field> fields = getAction().getServerConnection().getContext().getListFieldObjects(formname, Constants.AR_FIELD_TYPE_DATA);
			String fldNameLine = "FIELDS";
			String fldIdLine = "FLD-ID";
			String fldTypeLine = "DTYPES";

			for(int i = 0; i < fields.size(); i++)
			{
				Field field = fields.get(i);

				if(field.getDataType() == Constants.AR_DATA_TYPE_ATTACH ||
				   field.getFieldID() == 15 ||
				   field.getFieldOption() == Constants.AR_FIELD_OPTION_DISPLAY) continue;

				ids.add(new Integer(field.getFieldID()) );
				fldNameLine += " \"" + field.getName() +"\"";
				fldIdLine += " " + field.getFieldID();

				if(field.getDataType() == Constants.AR_DATA_TYPE_CHAR) fldTypeLine += " CHAR";
				else if(field.getDataType() == Constants.AR_DATA_TYPE_ENUM) fldTypeLine += " ENUM";
				else if(field.getDataType() == Constants.AR_DATA_TYPE_TIME) fldTypeLine += " TIME";
				else if(field.getDataType() == Constants.AR_DATA_TYPE_DATE) fldTypeLine += " DATE";
				else if(field.getDataType() == Constants.AR_DATA_TYPE_REAL) fldTypeLine += " REAL";
				else if(field.getDataType() == Constants.AR_DATA_TYPE_INTEGER) fldTypeLine += " INTEGER";
				else if(field.getDataType() == Constants.AR_DATA_TYPE_DECIMAL) fldTypeLine += " DECIMAL";
				else if(field.getDataType() == Constants.AR_DATA_TYPE_TIME_OF_DAY) fldTypeLine += " TIMEOFDAY";
				else RuntimeLogger.error("Data type (" + field.getDataType() + ") could not be recognized for field " + field.getName() + "(" + field.getFieldID() + ")");
			}

			buffer.append(fldNameLine);
			buffer.append(CharUtility.CR);

			buffer.append(fldIdLine);
			buffer.append(CharUtility.CR);

			buffer.append(fldTypeLine);
			buffer.append(CharUtility.CR);
		}
		catch(ARException are)
		{
			throw new AREasyException(are);
		}
	}

	protected void setExportData(String formname, StringBuffer buffer, List ids, String instanceId) throws AREasyException
	{
		if(ids == null || buffer == null) throw new AREasyException("Invalid metadata headers");

		CoreItem search = new CoreItem(formname);
		search.setAttribute(179, instanceId);

		List<CoreItem> items = search.search(getAction().getServerConnection());

		for(int i = 0; items != null && i < items.size(); i++)
		{
			CoreItem item = items.get(i);
			String dataLine = "DATA";

			for(int j = 0; j < ids.size(); j++)
			{
				Integer id = (Integer) ids.get(j);
				Object value = item.getAttributeValue(id.intValue());

				if(value == null) dataLine += " \"\"";
				else if(value instanceof String) dataLine += " \"" + getStringValue((String)value) + "\"";
				else if(value instanceof Integer || value instanceof Float) dataLine += " " + value;
				else if(value instanceof Timestamp) dataLine += " " + ((Timestamp)value).getValue();
				else if(value instanceof Time) dataLine += " " + ((Time)value).getValue();
				else if(value instanceof DateInfo) dataLine += " " + ((DateInfo)value).getValue();
				else if(value instanceof Date) dataLine += " " + ((Date)value).getTime();
				else if(value instanceof BigDecimal) dataLine += " " + ((BigDecimal)value).floatValue();
				else if(value instanceof CurrencyValue) dataLine += " \"" + getStringValue( ((CurrencyValue)value).getValueString() ) + "\"";
				else if(value instanceof DiaryListValue) dataLine += " \"" + getStringValue( ((DiaryListValue)value).toString() ) + "\"";
			}

			buffer.append(dataLine);
			buffer.append(CharUtility.CR);

			getAction().setRecordsCounter();
		}
	}

	private String getStringValue(String strValue)
	{
		if(strValue != null && strValue.contains("\"")) strValue =  StringUtility.replace(strValue, "\"", "\\\"");
		if(strValue != null && strValue.contains("\r\n")) strValue =  StringUtility.replace(strValue, "\r\n", "\\r\\n");
		if(strValue != null && strValue.contains("\n")) strValue =  StringUtility.replace(strValue, "\n", "\\n");

		return strValue;
	}
}
