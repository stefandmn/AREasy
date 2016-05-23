package org.areasy.common.parser.excel.biff.formula;

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

import org.areasy.common.parser.excel.read.biff.BOFRecord;

/**
 * Interface which exposes the methods needed by formulas
 * to access external sheet records
 */
public interface ExternalSheet
{
	/**
	 * Gets the name of the external sheet specified by the index
	 *
	 * @param index the external sheet index
	 * @return the name of the external sheet
	 */
	public String getExternalSheetName(int index);

	/**
	 * Gets the index of the first external sheet for the name
	 *
	 * @param sheetName the name of the external sheet
	 * @return the index of the external sheet with the specified name
	 */
	public int getExternalSheetIndex(String sheetName);

	/**
	 * Gets the index of the first external sheet for the name
	 *
	 * @param index the external sheet index
	 * @return the sheet index of the external sheet index
	 */
	public int getExternalSheetIndex(int index);

	/**
	 * Gets the index of the last external sheet for the name
	 *
	 * @param sheetName the name of the external sheet
	 * @return the index of the external sheet with the specified name
	 */
	public int getLastExternalSheetIndex(String sheetName);

	/**
	 * Gets the index of the first external sheet for the name
	 *
	 * @param index the external sheet index
	 * @return the sheet index of the external sheet index
	 */
	public int getLastExternalSheetIndex(int index);

	/**
	 * Parsing of formulas is only supported for a subset of the available
	 * biff version, so we need to test to see if this version is acceptable
	 *
	 * @return the BOF record
	 */
	public BOFRecord getWorkbookBof();
}
