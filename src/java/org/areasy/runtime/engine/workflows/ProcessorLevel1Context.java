package org.areasy.runtime.engine.workflows;

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
import com.bmc.arsys.arencrypt.PasswordReserveFieldEncryption;
import org.areasy.runtime.RuntimeManager;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.runtime.engine.base.ServerConnection;
import org.areasy.runtime.engine.structures.data.itsm.foundation.ProductCategory;
import org.areasy.common.data.NumberUtility;
import org.areasy.common.data.StringUtility;
import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Workflow processor: reader and modifier for data and workflows from an AR server, speaking about
 * abstract levels.
 *
 */
public abstract class ProcessorLevel1Context extends ProcessorLevel0Reader
{
	private static Logger logger = LoggerFactory.getLog(ProcessorLevel1Context.class);

	public static String FORM_SHRSCHEMANAMES 	= "SHR:SchemaNames";
	public static String FORM_OBJSTRCLASS 		= "OBJSTR:Class";
	public static String FORM_BASEELEMENT 		= "BMC.CORE:BMC_BaseElement";
	public static String FORM_BASERELATIONSHIP	= "BMC.CORE:BMC_BaseRelationship";
	public static String FORM_ASSETPEOPLE		= "AST:AssetPeople";
	public static String FORM_ASSETINVENTORYQTY	= "AST:InventoryQuantity";

	public static String DEFAULTDATASET 		= "BMC.ASSET";

	static
	{
		DEFAULTDATASET			= System.getProperty("DEFAULTDATASET", DEFAULTDATASET);
		FORM_BASEELEMENT 		= System.getProperty("BASEELEMENT", FORM_BASEELEMENT);
		FORM_BASERELATIONSHIP 	= System.getProperty("BASERELATIONSHIP", FORM_BASERELATIONSHIP);
	}

	public static String getSharedFormName(ServerConnection arsession, String formKeyword) throws AREasyException
	{
		String schemaname = null;
		
		if(formKeyword != null)
		{
			ArithmeticOrRelationalOperand opLeft = new ArithmeticOrRelationalOperand( 230000009 );
			ArithmeticOrRelationalOperand opRight = new ArithmeticOrRelationalOperand(new Value( formKeyword ));
			QualifierInfo qualInfo = new QualifierInfo( new RelationalOperationInfo(Constants.AR_REL_OP_EQUAL, opLeft, opRight) );

			boolean searchResult = false;
			int[] fieldIds = {1, 230000000};

			try
			{
				//Retrieve all entries
				List<Entry> entries = arsession.getContext().getListEntryObjects(FORM_SHRSCHEMANAMES, qualInfo, 0, 0, null, fieldIds, true, null);
				if(entries != null && entries.size() > 0)
				{
					schemaname = entries.get(0).get(230000000).toString();
					searchResult = true;
				}
			}
			catch(ARException are)
			{
				searchResult = true;
				logger.debug("Error reading form definition for Asset Management dictionary: " + are.getMessage());

				schemaname = getCmdbFormName(arsession, formKeyword);
			}

			//int case of the form wasn't found and form keyword is a classId value search also in CMDB dictionary
			if(!searchResult) schemaname = getCmdbFormName(arsession, formKeyword);
		}

		return schemaname;
	}

	public static String getCmdbFormName(ServerConnection arsession, String classId) throws AREasyException
	{
		String schemaname = null;

		if(classId != null)
		{
			ArithmeticOrRelationalOperand opLeft = new ArithmeticOrRelationalOperand(179);
			ArithmeticOrRelationalOperand opRight = new ArithmeticOrRelationalOperand(new Value( classId ));
			QualifierInfo qualInfo = new QualifierInfo( new RelationalOperationInfo(Constants.AR_REL_OP_EQUAL, opLeft, opRight) );

			int[] fieldIds = {1, 400130800};

			try
			{
				//Retrieve all entries
				List<Entry> entries = arsession.getContext().getListEntryObjects(FORM_OBJSTRCLASS, qualInfo, 0, 0, null, fieldIds, true, null);
				if(entries != null && entries.size() > 0) schemaname = entries.get(0).get(400130800).toString();
			}
			catch(ARException are)
			{
				throw new AREasyException(are);
			}
		}

		return schemaname;
	}

	public static ProductCategory getProductCategoryFromMapping(Map map)
	{
		if(map == null || map.isEmpty()) return null;

		String category = (String) map.get(String.valueOf(CI_CATEGORY));
		if(StringUtility.isNotEmpty(category)) map.remove(String.valueOf(CI_CATEGORY));

		String type = (String) map.get(String.valueOf(CI_TYPE));
		if(StringUtility.isNotEmpty(type)) map.remove(String.valueOf(CI_TYPE));

		String item = (String) map.get(String.valueOf(CI_ITEM));
		if(StringUtility.isNotEmpty(item)) map.remove(String.valueOf(CI_ITEM));

		String productname = (String) map.get(String.valueOf(CI_MODEL));
		if(StringUtility.isNotEmpty(productname)) map.remove(String.valueOf(CI_MODEL));

		String manufacturer = (String) map.get(String.valueOf(CI_MANUFACTURER));
		if(StringUtility.isNotEmpty(manufacturer)) map.remove(String.valueOf(CI_MANUFACTURER));

		return new ProductCategory(category, type, item, productname, manufacturer);
	}

	/**
	 * Get a data file stored into an ARS server. This method will interrogate the specified form (and in this situation the initialization
	 * of parser engine should be done with not null ARS server connection) and if will find any attachment data will download it on the local file system.
	 *
	 * @param arsession user connection structure and session
	 * @param formName ARS form name.
	 * @param searchFieldId form field id used to search the record where is located the attachment data.
	 * @param searchFieldValue field value used to search the record where is located the attachment data.
	 * @param targetFieldId form field id which should describe an attachment field. The value of this field will be downloaded,
	 * composing a file structure on the local file system.
	 * @return a file structure which will point to the <code>Work</code> directory from the local file system
	 * (where the AREasy application is running - for server or standalone mode)
	 */
	public static File getDataFile(ServerConnection arsession, String formName, String targetFieldId, String searchFieldId, String searchFieldValue)
	{
		File file = null;

		try
		{
			QualifierInfo qualInfo = new QualifierInfo( new RelationalOperationInfo(Constants.AR_REL_OP_EQUAL, new ArithmeticOrRelationalOperand(NumberUtility.toInt(searchFieldId)), new ArithmeticOrRelationalOperand(new Value( searchFieldValue ))) );
			int[] fieldIds = {1, NumberUtility.toInt(targetFieldId)};

			//Retrieve all entries
			List<Entry> entries = arsession.getContext().getListEntryObjects(formName, qualInfo, 0, 0, null, fieldIds, true, null);

			if(entries != null && entries.size() > 0)
			{
				Value attachment = entries.get(0).get(fieldIds[1]);

				if(attachment!= null && attachment.getDataType() == DataType.ATTACHMENT)
				{
					AttachmentValue attachmentValue = (AttachmentValue) attachment.getValue();
					String attachementName = attachmentValue.getValueFileName();
					
					if(attachementName.lastIndexOf("\\") >= 0) attachementName = attachementName.substring(attachementName.lastIndexOf("\\") + 1);
						else if(attachementName.lastIndexOf("/") >= 0) attachementName = attachementName.substring(attachementName.lastIndexOf("/") + 1);

					file = new File(RuntimeManager.getWorkingDirectory(), attachementName);
					if(file.exists()) file.delete();

					logger.debug("Downloading attachment: " + file.getAbsolutePath());
					arsession.getContext().getEntryBlob(formName, entries.get(0).getEntryId(), fieldIds[1], file.getPath());
				}
				else logger.debug("No attachment found or no file uploaded");
			}
			else logger.warn("No records found for '" + searchFieldId + "' = \"" + searchFieldValue + "\"");
		}
		catch(Throwable th)
		{
			file = null;

			logger.error("Error downloading file: " + th.getMessage());
			logger.debug("Exception", th);
		}

		return file;
	}

	/**
	 * Get a data file stored into an ARS server. This method will interrogate the specified form (and in this situation the initialization
	 * of parser engine should be done with not null ARS server connection) and if will find any attachment data will download it on the local file system.
	 *
	 * @param arsession user connection structure and session
	 * @param formName ARS form name.
	 * @param qualification string search qualification criteria.
	 * @param targetFieldId form field id which should describe an attachment field. The value of this field will be downloaded,
	 * composing a file structure on the local file system.
	 * @return a file structure which will point to the <code>Work</code> directory from the local file system
	 * (where the AREasy application is running - for server or standalone mode)
	 */
	public static File getDataFile(ServerConnection arsession, String formName, String targetFieldId, String qualification)
	{
		File file = null;

		try
		{
			// Retrieve the detail info of all fields from the form.
			List<Field> fields = arsession.getContext().getListFieldObjects(formName);

			// Create the search qualifier.
			QualifierInfo qualInfo = arsession.getContext().parseQualification(qualification, fields, null, Constants.AR_QUALCONTEXT_DEFAULT);
			int[] fieldIds = {1, NumberUtility.toInt(targetFieldId)};

			//Retrieve all entries
			List<Entry> entries = arsession.getContext().getListEntryObjects(formName, qualInfo, 0, 0, null, fieldIds, true, null);

			if(entries != null && entries.size() > 0)
			{
				Value attachment = entries.get(0).get(fieldIds[1]);

				if(attachment!= null && attachment.getDataType() == DataType.ATTACHMENT)
				{
					AttachmentValue attachmentValue = (AttachmentValue) attachment.getValue();
					String attachementName = attachmentValue.getValueFileName();

					if(attachementName.lastIndexOf("\\") >= 0) attachementName = attachementName.substring(attachementName.lastIndexOf("\\") + 1);
						else if(attachementName.lastIndexOf("/") >= 0) attachementName = attachementName.substring(attachementName.lastIndexOf("/") + 1);

					file = new File(RuntimeManager.getWorkingDirectory(), attachementName);
					if(file.exists()) file.delete();

					logger.debug("Downloading attachment: " + file.getAbsolutePath());
					arsession.getContext().getEntryBlob(formName, entries.get(0).getEntryId(), fieldIds[1], file.getPath());
				}
				else logger.debug("No attachment found or no file uploaded");
			}
			else logger.warn("No records found for " + qualification);
		}
		catch(Throwable th)
		{
			file = null;

			logger.error("Error downloading file: " + th.getMessage());
			logger.debug("Exception", th);
		}

		return file;
	}

	/**
	 * Encrypt a string in ARSystem format to become a classic AR password.
	 *
	 * @param password password string
	 * @return encrypted string
	 * @throws Exception in case of any exception occur
	 */
	public static String encryptARPassword(String password) throws Exception
	{
		PasswordReserveFieldEncryption pwdEnc = new PasswordReserveFieldEncryption(false);
		String encryptedPassword = new String(pwdEnc.encryptPasswordEx(password.getBytes()));

		return encryptedPassword;
	}

	/**
	 * Decrypt an AR system password are return the clear password value.
	 *
	 * @param password password string
	 * @return decrypted string
	 * @throws Exception in case of any exception occur
	 */
	public static String decryptARPassword(String password) throws Exception
	{
		PasswordReserveFieldEncryption pwdEnc = new PasswordReserveFieldEncryption(false);
		String decryptedPassword = new String(pwdEnc.decryptPasswordEx(password.getBytes()));

		return decryptedPassword;
	}
}
