package org.areasy.common.parser.excel.format;

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
 * Enumeration type which describes the orientation of data within a cell
 */
public final class Orientation
{
	/**
	 * The internal binary value which gets written to the generated Excel file
	 */
	private int value;

	/**
	 * The textual description
	 */
	private String string;

	/**
	 * The list of alignments
	 */
	private static Orientation[] orientations = new Orientation[0];

	/**
	 * Constructor
	 *
	 * @param val
	 */
	protected Orientation(int val, String s)
	{
		value = val;
		string = s;

		Orientation[] oldorients = orientations;
		orientations = new Orientation[oldorients.length + 1];
		System.arraycopy(oldorients, 0, orientations, 0, oldorients.length);
		orientations[oldorients.length] = this;
	}

	/**
	 * Accessor for the binary value
	 *
	 * @return the internal binary value
	 */
	public int getValue()
	{
		return value;
	}

	/**
	 * Gets the textual description
	 */
	public String getDescription()
	{
		return string;
	}

	/**
	 * Gets the alignment from the value
	 *
	 * @param val
	 * @return the alignment with that value
	 */
	public static Orientation getOrientation(int val)
	{
		for (int i = 0; i < orientations.length; i++)
		{
			if (orientations[i].getValue() == val)
			{
				return orientations[i];
			}
		}

		return HORIZONTAL;
	}


	/**
	 * Cells with this specified orientation will be horizontal
	 */
	public static Orientation HORIZONTAL = new Orientation(0, "horizontal");
	/**
	 * Cells with this specified orientation have their data
	 * presented vertically
	 */
	public static Orientation VERTICAL = new Orientation(0xff, "vertical");
	/**
	 * Cells with this specified orientation will have their data
	 * presented with a rotation of 90 degrees upwards
	 */
	public static Orientation PLUS_90 = new Orientation(90, "up 90");
	/**
	 * Cells with this specified orientation will have their data
	 * presented with a rotation of 90 degrees downwards
	 */
	public static Orientation MINUS_90 = new Orientation(180, "down 90");
	/**
	 * Cells with this specified orientation will have their data
	 * presented with a rotation 45 degrees upwards
	 */
	public static Orientation PLUS_45 = new Orientation(45, "up 45");
	/**
	 * Cells with this specified orientation will have their data
	 * presented with a rotation 45 degrees downwards
	 */
	public static Orientation MINUS_45 = new Orientation(135, "down 45");
	/**
	 * Cells with this specified orientation will have their text stacked
	 * downwards, but not rotated
	 */
	public static Orientation STACKED = new Orientation(255, "stacked");

}









