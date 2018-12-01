package org.areasy.common.parser.excel.biff.drawing;

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
import org.areasy.common.parser.excel.biff.Type;
import org.areasy.common.parser.excel.biff.WritableRecordData;
import org.areasy.common.parser.excel.read.biff.Record;

/**
 * A record which merely holds the MSODRAWING data.  Used when copying files
 * which contain images
 */
public class MsoDrawingRecord extends WritableRecordData
{
	/**
	 * The logger
	 */
	private static Logger logger = LoggerFactory.getLog(MsoDrawingRecord.class);

	/**
	 * Flag to indicate whether this is the first drawing on the sheet
	 * - needed for copying
	 */
	private boolean first;
	/**
	 * The raw drawing data which was read in
	 */
	private byte[] data;

	/**
	 * Constructs this object from the raw data
	 *
	 * @param t the raw data
	 */
	public MsoDrawingRecord(Record t)
	{
		super(t);
		data = getRecord().getData();
		first = false;
	}

	/**
	 * Constructor
	 *
	 * @param d the drawing data
	 */
	public MsoDrawingRecord(byte[] d)
	{
		super(Type.MSODRAWING);
		data = d;
		first = false;
	}

	/**
	 * Expose the protected function
	 *
	 * @return the raw record data
	 */
	public byte[] getData()
	{
		return data;
	}

	/**
	 * Expose the protected function to the DefaultSheet in this package
	 *
	 * @return the raw record data
	 */
	public Record getRecord()
	{
		return super.getRecord();
	}

	/**
	 * Sets the flag to indicate that this is the first drawing on the sheet
	 */
	public void setFirst()
	{
		first = true;
	}

	/**
	 * Accessor for the first drawing on the sheet.  This is used when
	 * copying unmodified sheets to indicate that this drawing contains
	 * the first time Escher gubbins
	 *
	 * @return TRUE if this MSORecord is the first drawing on the sheet
	 */
	public boolean isFirst()
	{
		return first;
	}
}




