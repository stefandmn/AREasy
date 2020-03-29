package org.areasy.common.parser.excel.biff.drawing;

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
 * Enumerations for the shape type
 */
final class ShapeType
{
	/**
	 * The value
	 */
	private int value;

	/**
	 * The list of shape types
	 */
	private static ShapeType[] types = new ShapeType[0];

	/**
	 * Constructor
	 *
	 * @param v the value
	 */
	ShapeType(int v)
	{
		value = v;

		ShapeType[] old = types;
		types = new ShapeType[types.length + 1];
		System.arraycopy(old, 0, types, 0, old.length);
		types[old.length] = this;
	}

	/**
	 * Gets the shape type given the value
	 *
	 * @param v the value
	 * @return the shape type for the value
	 */
	static ShapeType getType(int v)
	{
		ShapeType st = UNKNOWN;
		boolean found = false;
		for (int i = 0; i < types.length && !found; i++)
		{
			if (types[i].value == v)
			{
				found = true;
				st = types[i];
			}
		}
		return st;
	}

	/**
	 * Accessor for the value
	 *
	 * @return the value
	 */
	public int getValue()
	{
		return value;
	}

	public static final ShapeType MIN = new ShapeType(0);
	public static final ShapeType PICTURE_FRAME = new ShapeType(75);
	public static final ShapeType HOST_CONTROL = new ShapeType(201);
	public static final ShapeType TEXT_BOX = new ShapeType(202);
	public static final ShapeType UNKNOWN = new ShapeType(-1);
}
