package org.areasy.common.parser.excel.format;

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

/**
 * Exposes the cell formatting information
 */
public interface Format
{
	/**
	 * Accesses the excel format string which is applied to the cell
	 * Note that this is the string that excel uses, and not the java
	 * equivalent
	 *
	 * @return the cell format string
	 */
	public String getFormatString();
}






