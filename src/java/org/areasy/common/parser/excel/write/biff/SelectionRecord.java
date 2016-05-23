package org.areasy.common.parser.excel.write.biff;

/*
 * Copyright (c) 2007-2016 AREasy Runtime
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
 * Stores the current selection
 */
class SelectionRecord extends WritableRecordData
{
	/**
	 * The pane type
	 */
	private PaneType pane;

	/**
	 * The top left column in this pane
	 */
	private int column;

	/**
	 * The top left row  in this pane
	 */
	private int row;

	// Enumeration for the pane type
	private static class PaneType
	{
		int val;

		PaneType(int v)
		{
			val = v;
		}
	}

	// The pane types
	public final static PaneType lowerRight = new PaneType(0);
	public final static PaneType upperRight = new PaneType(1);
	public final static PaneType lowerLeft = new PaneType(2);
	public final static PaneType upperLeft = new PaneType(3);

	/**
	 * Constructor
	 */
	public SelectionRecord(PaneType pt, int col, int r)
	{
		super(Type.SELECTION);
		column = col;
		row = r;
		pane = pt;
	}

	/**
	 * Gets the binary data
	 *
	 * @return the binary data
	 */
	public byte[] getData()
	{
		// hard code the data in for now
		byte[] data = new byte[15];

		data[0] = (byte) pane.val;
		IntegerHelper.getTwoBytes(row, data, 1);
		IntegerHelper.getTwoBytes(column, data, 3);

		data[7] = (byte) 0x01;

		IntegerHelper.getTwoBytes(row, data, 9);
		IntegerHelper.getTwoBytes(row, data, 11);
		data[13] = (byte) column;
		data[14] = (byte) column;

		return data;
	}
}
