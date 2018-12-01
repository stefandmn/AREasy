package org.areasy.common.parser.excel.read.biff;

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

import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;
import org.areasy.common.parser.excel.biff.RecordData;

/**
 * A template record
 */
class TemplateRecord extends RecordData
{
	/**
	 * The logger
	 */
	private static Logger logger = LoggerFactory.getLog(TemplateRecord.class);

	/**
	 * The template
	 */
	private boolean template;

	/**
	 * Constructor
	 *
	 * @param t the record
	 */
	public TemplateRecord(Record t)
	{
		super(t);
		template = true;
	}

	/**
	 * Accessor for the template mode
	 *
	 * @return the template mode
	 */
	public boolean getTemplate()
	{
		return template;
	}
}
