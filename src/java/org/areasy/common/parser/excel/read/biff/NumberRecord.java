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
import org.areasy.common.parser.excel.CellType;
import org.areasy.common.parser.excel.NumberCell;
import org.areasy.common.parser.excel.biff.DoubleHelper;
import org.areasy.common.parser.excel.biff.FormattingRecords;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * A number record.  This is stored as 8 bytes, as opposed to the
 * 4 byte RK record
 */
class NumberRecord extends CellValue implements NumberCell
{
	/**
	 * The logger
	 */
	private static Logger logger = LoggerFactory.getLog(NumberRecord.class);

	/**
	 * The value
	 */
	private double value;

	/**
	 * The java equivalent of the excel format
	 */
	private NumberFormat format;

	/**
	 * The formatter to convert the value into a string
	 */
	private static DecimalFormat defaultFormat = new DecimalFormat("#.###");

	/**
	 * Constructs this object from the raw data
	 *
	 * @param t  the raw data
	 * @param fr the available formats
	 * @param si the sheet
	 */
	public NumberRecord(Record t, FormattingRecords fr, DefaultSheet si)
	{
		super(t, fr, si);
		byte[] data = getRecord().getData();

		value = DoubleHelper.getIEEEDouble(data, 6);

		// Now get the number format
		format = fr.getNumberFormat(getXFIndex());
		if (format == null)
		{
			format = defaultFormat;
		}
	}

	/**
	 * Accessor for the value
	 *
	 * @return the value
	 */
	public double getValue()
	{
		return value;
	}

	/**
	 * Returns the contents of this cell as a string
	 *
	 * @return the value formatted into a string
	 */
	public String getContents()
	{
		return format.format(value);
	}

	/**
	 * Accessor for the cell type
	 *
	 * @return the cell type
	 */
	public CellType getType()
	{
		return CellType.NUMBER;
	}

	/**
	 * Gets the NumberFormat used to format this cell.  This is the java
	 * equivalent of the Excel format
	 *
	 * @return the NumberFormat used to format the cell
	 */
	public NumberFormat getNumberFormat()
	{
		return format;
	}
}



