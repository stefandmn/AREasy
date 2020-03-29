package org.areasy.common.parser.excel.write.biff;

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

import org.areasy.common.parser.excel.biff.IntegerHelper;
import org.areasy.common.parser.excel.biff.Type;
import org.areasy.common.parser.excel.biff.WritableRecordData;

/**
 * Contains the window attributes for a process
 */
class PaneRecord extends WritableRecordData
{
	/**
	 * The number of rows visible in the top left pane
	 */
	private int rowsVisible;
	/**
	 * The number of columns visible in the top left pane
	 */
	private int columnsVisible;

	/**
	 * The pane codes
	 */
	private final static int topLeftPane = 0x3;
	private final static int bottomLeftPane = 0x2;
	private final static int topRightPane = 0x1;
	private final static int bottomRightPane = 0x0;

	/**
	 * Code
	 * <p/>
	 * /**
	 * Constructor
	 */
	public PaneRecord(int cols, int rows)
	{
		super(Type.PANE);

		rowsVisible = rows;
		columnsVisible = cols;
	}

	/**
	 * Gets the binary data for output to file
	 *
	 * @return the binary data
	 */
	public byte[] getData()
	{
		byte[] data = new byte[10];

		// The x position
		IntegerHelper.getTwoBytes(columnsVisible, data, 0);

		// The y position
		IntegerHelper.getTwoBytes(rowsVisible, data, 2);

		// The top row visible in the bottom pane
		if (rowsVisible > 0)
		{
			IntegerHelper.getTwoBytes(rowsVisible, data, 4);
		}

		// The left most column visible in the right pane
		if (columnsVisible > 0)
		{
			IntegerHelper.getTwoBytes(columnsVisible, data, 6);
		}

		// The active pane
		int activePane = topLeftPane;

		if (rowsVisible > 0 && columnsVisible == 0)
		{
			activePane = bottomLeftPane;
		}
		else if (rowsVisible == 0 && columnsVisible > 0)
		{
			activePane = topRightPane;
		}
		else if (rowsVisible > 0 && columnsVisible > 0)
		{
			activePane = bottomRightPane;
		}
		// always present
		IntegerHelper.getTwoBytes(activePane, data, 8);

		return data;
	}
}
