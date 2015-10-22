package org.areasy.common.parser.excel.biff;

/*
 * Copyright (c) 2007-2015 AREasy Runtime
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

import org.areasy.common.parser.excel.read.biff.Record;

/**
 * The record data within a record
 */
public abstract class RecordData
{
	/**
	 * The raw data
	 */
	private Record record;

	/**
	 * The Biff code for this record.  This is set up when the record is
	 * used for writing
	 */
	private int code;

	/**
	 * Constructs this object from the raw data
	 *
	 * @param r the raw data
	 */
	protected RecordData(Record r)
	{
		record = r;
		code = r.getCode();
	}

	/**
	 * Constructor used by the writable records
	 *
	 * @param t the type
	 */
	protected RecordData(Type t)
	{
		code = t.value;
	}

	/**
	 * Returns the raw data to its subclasses
	 *
	 * @return the raw data
	 */
	protected Record getRecord()
	{
		return record;
	}

	/**
	 * Accessor for the code
	 *
	 * @return the code
	 */
	protected final int getCode()
	{
		return code;
	}
}








