package org.areasy.common.parser.excel.write.biff;

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

import org.areasy.common.parser.excel.biff.DisplayFormat;
import org.areasy.common.parser.excel.biff.FontRecord;
import org.areasy.common.parser.excel.biff.XFRecord;

/**
 * A style XF Record
 */
public class StyleXFRecord extends XFRecord
{
	/**
	 * Constructor
	 *
	 * @param fnt  the font for this style
	 * @param form the format of this style
	 */
	public StyleXFRecord(FontRecord fnt, DisplayFormat form)
	{
		super(fnt, form);

		setXFDetails(XFRecord.style, 0xfff0);
	}


	/**
	 * Sets the raw cell options.  Called by WritableFormattingRecord
	 * when setting the built in cell formats
	 *
	 * @param opt the cell options
	 */
	public final void setCellOptions(int opt)
	{
		super.setXFCellOptions(opt);
	}

	/**
	 * Sets whether or not this XF record locks the cell
	 *
	 * @param l the locked flag
	 * @throws WriteException
	 */
	public void setLocked(boolean l)
	{
		super.setXFLocked(l);
	}

}
