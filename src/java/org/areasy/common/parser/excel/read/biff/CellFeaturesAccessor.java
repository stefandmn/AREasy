package org.areasy.common.parser.excel.read.biff;

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

import org.areasy.common.parser.excel.CellFeatures;

/**
 * Allows the setting of the cell features in this package only
 */
interface CellFeaturesAccessor
{
	/**
	 * Sets the cell features
	 *
	 * @param cf the cell features
	 */
	public void setCellFeatures(CellFeatures cf);

	/**
	 * Convenience function (due to casting) to get the cell features
	 *
	 * @return the cell features
	 */
	public CellFeatures getCellFeatures();
}
