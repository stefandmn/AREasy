package org.areasy.common.parser.excel.read.biff;

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

import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;
import org.areasy.common.parser.excel.biff.IntegerHelper;
import org.areasy.common.parser.excel.biff.RecordData;

/**
 * A calculation mode record
 */
class SaveRecalcRecord extends RecordData
{
	/**
	 * The logger
	 */
	private static Logger logger = LoggerFactory.getLog(SaveRecalcRecord.class);

	/**
	 * The calculation mode
	 */
	private boolean recalculateOnSave;

	/**
	 * Constructor
	 *
	 * @param t the record
	 */
	public SaveRecalcRecord(Record t)
	{
		super(t);
		byte[] data = t.getData();
		int mode = IntegerHelper.getInt(data[0], data[1]);
		recalculateOnSave = (mode == 1);
	}

	/**
	 * Accessor for the recalculate on save mode
	 *
	 * @return the recalculate on save mode
	 */
	public boolean getRecalculateOnSave()
	{
		return recalculateOnSave;
	}
}
