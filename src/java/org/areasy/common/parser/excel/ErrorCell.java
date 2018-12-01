package org.areasy.common.parser.excel;

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
 * This type represents a cell which contains an error.  This error will
 * usually, but not always be the result of some error resulting from
 * a formula
 */
public interface ErrorCell extends Cell
{
	/**
	 * Gets the error code for this cell.  If this cell does not represent
	 * an error, then it returns 0.  Always use the method isError() to
	 * determine this prior to calling this method
	 *
	 * @return the error code if this cell contains an error, 0 otherwise
	 */
	public int getErrorCode();
}
