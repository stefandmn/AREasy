package org.areasy.common.parser.excel.biff.drawing;

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

import org.areasy.common.parser.excel.biff.IntegerHelper;

/**
 * The Drawing Group
 */
class Dg extends EscherAtom
{
	/**
	 * The data
	 */
	private byte[] data;

	/**
	 * The id of this drawing
	 */
	private int drawingId;

	/**
	 * The number of shapes
	 */
	private int shapeCount;

	/**
	 * The seed for drawing ids
	 */
	private int seed;

	/**
	 * Constructor invoked when reading in an escher stream
	 *
	 * @param erd the escher record
	 */
	public Dg(EscherRecordData erd)
	{
		super(erd);
		drawingId = getInstance();

		byte[] bytes = getBytes();
		shapeCount = IntegerHelper.getInt(bytes[0], bytes[1], bytes[2], bytes[3]);
		seed = IntegerHelper.getInt(bytes[4], bytes[5], bytes[6], bytes[7]);
	}

	/**
	 * Constructor invoked when writing out an escher stream
	 *
	 * @param numDrawings the number of drawings
	 */
	public Dg(int numDrawings)
	{
		super(EscherRecordType.DG);
		drawingId = 1;
		shapeCount = numDrawings + 1;
		seed = 1024 + shapeCount + 1;
		setInstance(drawingId);
	}

	/**
	 * Gets the drawing id
	 *
	 * @return the drawing id
	 */
	public int getDrawingId()
	{
		return drawingId;
	}

	/**
	 * Gets the shape count
	 *
	 * @return the shape count
	 */
	int getShapeCount()
	{
		return shapeCount;
	}

	/**
	 * Used to generate the drawing data
	 *
	 * @return the data
	 */
	byte[] getData()
	{
		data = new byte[8];
		IntegerHelper.getFourBytes(shapeCount, data, 0);
		IntegerHelper.getFourBytes(seed, data, 4);

		return setHeaderData(data);
	}
}
