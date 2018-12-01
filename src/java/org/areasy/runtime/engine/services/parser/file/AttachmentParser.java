package org.areasy.runtime.engine.services.parser.file;

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

import org.areasy.runtime.engine.services.parser.ParserException;
import org.areasy.runtime.engine.workflows.ProcessorLevel1Context;
import org.areasy.common.data.StringUtility;

import java.io.File;

/**
 * Specilized parser class to extract data file from a Remedy form, to download it
 * and to call <code>BaseFileParser</code> in order to parse it and to deliver the content.
 *
 */
public class AttachmentParser extends BaseFileParser
{
	/**
	 * Initialize parser class
	 *
	 * @throws org.areasy.runtime.engine.services.parser.ParserException if any error will occur
	 */
	public void open() throws ParserException
	{
		String formName = getParserConfig().getString("formname", null);
		String targetFieldId = getParserConfig().getString("targetfieldid", null);
		String sourceFileId = getParserConfig().getString("sourcefieldid", null);
		String sourceFieldValue = getParserConfig().getString("sourcefieldvalue", null);

		if(getServerConnection() == null  || !getServerConnection().isConnected() || StringUtility.isEmpty(formName) || StringUtility.isEmpty(targetFieldId) ||
				StringUtility.isEmpty(sourceFileId) || StringUtility.isEmpty(sourceFieldValue)) throw new ParserException("Remedy attachment data source couldn't be identified");

		File file = ProcessorLevel1Context.getDataFile(getServerConnection(), formName, targetFieldId, sourceFileId, sourceFieldValue);
		getParserConfig().setKey("parserfile", file!= null ? file.getPath() : null);

		//call open method from super-parser
		super.open();
	}

	/**
	 * Close and dipose parser class
	 */
	public void close()
	{
		String fileName = getParserConfig().getString("parserfile", null);
		File file = new File(fileName);

		if(file.exists())
		{
			if(!file.delete()) file.deleteOnExit();
		}

		//close super-parser.
		super.close();
	}
}