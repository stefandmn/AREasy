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

import org.areasy.common.parser.excel.SheetSettings;
import org.areasy.common.parser.excel.biff.IntegerHelper;
import org.areasy.common.parser.excel.biff.Type;
import org.areasy.common.parser.excel.biff.WritableRecordData;

/**
 * Contains the window attributes for a process
 */
class Window2Record extends WritableRecordData
{
	/**
	 * The binary data for output to file
	 */
	private byte[] data;

	/**
	 * Constructor
	 */
	public Window2Record(SheetSettings settings)
	{
		super(Type.WINDOW2);

		int options = 0;

		options |= 0x0; // display formula values, not formulas

		if (settings.getShowGridLines())
		{
			options |= 0x02;
		}

		options |= 0x04; // display row and column headings

		options |= 0x0; // panes should be not frozen

		if (settings.getDisplayZeroValues())
		{
			options |= 0x10;
		}

		options |= 0x20; // default header

		options |= 0x80; // display outline symbols

		// Handle the freeze panes
		if (settings.getHorizontalFreeze() != 0 ||
				settings.getVerticalFreeze() != 0)
		{
			options |= 0x08;
			options |= 0x100;
		}

		// Handle the selected flag
		if (settings.isSelected())
		{
			options |= 0x600;
		}

		// Handle the view mode
		if (settings.getPageBreakPreviewMode())
		{
			options |= 0x800;
		}

		// hard code the data in for now
		data = new byte[18];
		IntegerHelper.getTwoBytes(options, data, 0);
		IntegerHelper.getTwoBytes(0x40, data, 6); // grid line colour
		IntegerHelper.getTwoBytes(settings.getPageBreakPreviewMagnification(),
				data, 10);
		IntegerHelper.getTwoBytes(settings.getNormalMagnification(),
				data, 12);

	}

	/**
	 * Gets the binary data for output to file
	 *
	 * @return the binary data
	 */
	public byte[] getData()
	{
		return data;
	}
}
