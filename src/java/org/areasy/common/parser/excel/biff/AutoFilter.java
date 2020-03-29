package org.areasy.common.parser.excel.biff;

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

import org.areasy.common.parser.excel.write.biff.File;

import java.io.IOException;

/**
 * Information for autofiltering
 */
public class AutoFilter
{
	private FilterModeRecord filterMode;
	private AutoFilterInfoRecord autoFilterInfo;
	private AutoFilterRecord autoFilter;

	/**
	 * Constructor
	 */
	public AutoFilter(FilterModeRecord fmr,
					  AutoFilterInfoRecord afir)
	{
		filterMode = fmr;
		autoFilterInfo = afir;
	}

	public void add(AutoFilterRecord af)
	{
		autoFilter = af; // make this into a list sometime
	}

	/**
	 * Writes out the data validation
	 *
	 * @param outputFile the output file
	 * @throws IOException
	 */
	public void write(File outputFile) throws IOException
	{
		if (filterMode != null)
		{
			outputFile.write(filterMode);
		}

		if (autoFilterInfo != null)
		{
			outputFile.write(autoFilterInfo);
		}

		if (autoFilter != null)
		{
			outputFile.write(autoFilter);
		}
	}
}
