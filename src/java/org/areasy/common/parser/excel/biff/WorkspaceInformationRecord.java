package org.areasy.common.parser.excel.biff;

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
import org.areasy.common.parser.excel.read.biff.Record;

/**
 * A record detailing whether the sheet is protected
 */
public class WorkspaceInformationRecord extends WritableRecordData
{
	// the logger
	private static Logger logger =
			LoggerFactory.getLog(WorkspaceInformationRecord.class);

	/**
	 * The options byte
	 */
	private int wsoptions;

	/**
	 * The row outlines
	 */
	private boolean rowOutlines;

	/**
	 * The column outlines
	 */
	private boolean columnOutlines;

	/**
	 * The fit to pages flag
	 */
	private boolean fitToPages;

	// the masks
	private static final int FIT_TO_PAGES = 0x100;
	private static final int SHOW_ROW_OUTLINE_SYMBOLS = 0x400;
	private static final int SHOW_COLUMN_OUTLINE_SYMBOLS = 0x800;
	private static final int DEFAULT_OPTIONS = 0x4c1;


	/**
	 * Constructs this object from the raw data
	 *
	 * @param t the raw data
	 */
	public WorkspaceInformationRecord(Record t)
	{
		super(t);
		byte[] data = getRecord().getData();

		wsoptions = IntegerHelper.getInt(data[0], data[1]);
		fitToPages = (wsoptions | FIT_TO_PAGES) != 0;
		rowOutlines = (wsoptions | SHOW_ROW_OUTLINE_SYMBOLS) != 0;
		columnOutlines = (wsoptions | SHOW_COLUMN_OUTLINE_SYMBOLS) != 0;
	}

	/**
	 * Constructs this object from the raw data
	 */
	public WorkspaceInformationRecord()
	{
		super(Type.WSBOOL);
		wsoptions = DEFAULT_OPTIONS;
	}

	/**
	 * Gets the fit to pages flag
	 *
	 * @return TRUE if fit to pages is set
	 */
	public boolean getFitToPages()
	{
		return fitToPages;
	}

	/**
	 * Sets the fit to page flag
	 *
	 * @param b fit to page indicator
	 */
	public void setFitToPages(boolean b)
	{
		fitToPages = b;
	}

	/**
	 * Sets the outlines
	 */
	public void setRowOutlines(boolean ro)
	{
		rowOutlines = true;
	}

	/**
	 * Sets the outlines
	 */
	public void setColumnOutlines(boolean ro)
	{
		rowOutlines = true;
	}

	/**
	 * Gets the binary data for output to file
	 *
	 * @return the binary data
	 */
	public byte[] getData()
	{
		byte[] data = new byte[2];

		if (fitToPages)
		{
			wsoptions |= FIT_TO_PAGES;
		}

		if (rowOutlines)
		{
			wsoptions |= SHOW_ROW_OUTLINE_SYMBOLS;
		}

		if (columnOutlines)
		{
			wsoptions |= SHOW_COLUMN_OUTLINE_SYMBOLS;
		}

		IntegerHelper.getTwoBytes(wsoptions, data, 0);

		return data;
	}
}









