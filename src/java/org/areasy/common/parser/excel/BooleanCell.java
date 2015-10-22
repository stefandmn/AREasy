package org.areasy.common.parser.excel;

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
 * This type represents the Microsoft concept of a Boolean.  Accordingly, this
 * cell represents either TRUE, FALSE or an error condition.  This third
 * state naturally makes handling BooleanCells quite tricky, and use of
 * the specific access methods should be handled with care
 */
public interface BooleanCell extends Cell
{
	/**
	 * Gets the boolean value stored in this cell.  If this cell contains an
	 * error, then returns FALSE.  Always query this cell type using the
	 * accessor method isError() prior to calling this method
	 *
	 * @return TRUE if this cell contains TRUE, FALSE if it contains FALSE or
	 *         an error code
	 */
	public boolean getValue();
}







