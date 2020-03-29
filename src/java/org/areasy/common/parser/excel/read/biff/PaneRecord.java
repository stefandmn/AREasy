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
 * Contains the cell dimensions of this process
 */
class PaneRecord extends RecordData
{
	/**
	 * The logger
	 */
	private static Logger logger = LoggerFactory.getLog(PaneRecord.class);

	/**
	 * The number of rows visible in the top left pane
	 */
	private int rowsVisible;
	/**
	 * The number of columns visible in the top left pane
	 */
	private int columnsVisible;

	/**
	 * Constructs the dimensions from the raw data
	 *
	 * @param t the raw data
	 */
	public PaneRecord(Record t)
	{
		super(t);
		byte[] data = t.getData();

		columnsVisible = IntegerHelper.getInt(data[0], data[1]);
		rowsVisible = IntegerHelper.getInt(data[2], data[3]);
	}

	/**
	 * Accessor for the number of rows in the top left pane
	 *
	 * @return the number of rows visible in the top left pane
	 */
	public final int getRowsVisible()
	{
		return rowsVisible;
	}

	/**
	 * Accessor for the numbe rof columns visible in the top left pane
	 *
	 * @return the number of columns visible in the top left pane
	 */
	public final int getColumnsVisible()
	{
		return columnsVisible;
	}
}







