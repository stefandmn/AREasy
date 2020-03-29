package org.areasy.common.parser.excel.write.biff;

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

import org.areasy.common.parser.excel.biff.Fonts;
import org.areasy.common.parser.excel.write.WritableFont;

/**
 * A container for the list of fonts used in this workbook  The writable
 * subclass instantiates the predetermined list of fonts available to
 * users of the writable API
 */
public class WritableFonts extends Fonts
{
	/**
	 * Constructor.  Creates the predetermined list of fonts
	 */
	public WritableFonts(DefaultWritableWorkbook w)
	{
		super();

		addFont(w.getStyles().getArial10Pt());

		// Create the default fonts
		WritableFont f = new WritableFont(WritableFont.ARIAL);
		addFont(f);

		f = new WritableFont(WritableFont.ARIAL);
		addFont(f);

		f = new WritableFont(WritableFont.ARIAL);
		addFont(f);
	}
}
