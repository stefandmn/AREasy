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

/**
 * Helper to convert an RK number into a double or an integer
 */
final class RKHelper
{
	/**
	 * Private constructor to prevent instantiation
	 */
	private RKHelper()
	{
	}

	/**
	 * Converts excel's internal RK format into a double value
	 *
	 * @param rk the rk number in bits
	 * @return the double representation
	 */
	public static double getDouble(int rk)
	{
		if ((rk & 0x02) != 0)
		{
			int intval = rk >> 2;

			double value = intval;
			if ((rk & 0x01) != 0)
			{
				value /= 100;
			}

			return value;
		}
		else
		{
			long valbits = (rk & 0xfffffffc);
			valbits <<= 32;
			double value = Double.longBitsToDouble(valbits);

			if ((rk & 0x01) != 0)
			{
				value /= 100;
			}

			return value;
		}
	}
}


