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

/**
 * Serves up Record objects from a biff file.  This object is used by the
 * demo programs BiffDump and ... only and has no influence whatsoever on
 * the JExcelApi reading and writing of excel sheets
 */
public class BiffRecordReader
{
	/**
	 * The biff file
	 */
	private File file;

	/**
	 * The current record retrieved
	 */
	private Record record;

	/**
	 * Constructor
	 *
	 * @param f the biff file
	 */
	public BiffRecordReader(File f)
	{
		file = f;
	}

	/**
	 * Sees if there are any more records to read
	 *
	 * @return TRUE if there are more records, FALSE otherwise
	 */
	public boolean hasNext()
	{
		return file.hasNext();
	}

	/**
	 * Gets the next record
	 *
	 * @return the next record
	 */
	public Record next()
	{
		record = file.next();
		return record;
	}

	/**
	 * Gets the position of the current record in the biff file
	 *
	 * @return the position
	 */
	public int getPos()
	{
		return file.getPos() - record.getLength() - 4;
	}
}
