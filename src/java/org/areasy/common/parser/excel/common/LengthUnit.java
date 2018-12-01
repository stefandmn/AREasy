package org.areasy.common.parser.excel.common;


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

/**
 * Enumeration for units
 */
public class LengthUnit extends BaseUnit
{
	private static int count = 0;

	private LengthUnit()
	{
		super(count++);
	}

	public static int getCount()
	{
		return count;
	}

	public static LengthUnit POINTS = new LengthUnit();
	public static LengthUnit METRES = new LengthUnit();
	public static LengthUnit CENTIMETRES = new LengthUnit();
	public static LengthUnit INCHES = new LengthUnit();
}
