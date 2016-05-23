package org.areasy.common.doclet.document.elements;

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

import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;

/**
 * Customized version(s) of PdfPCell without
 * a border and without padding.
 *
 * @version $Id: CellNoBorderNoPadding.java,v 1.2 2008/05/14 09:36:47 swd\stefan.damian Exp $
 */
public class CellNoBorderNoPadding extends PdfPCell
{
	/**
	 * Creates a PdfPCell with a border and a padding of 6.
	 *
	 * @param data The cell content.
	 */
	public CellNoBorderNoPadding(Phrase data)
	{
		super(data);
		super.setBorder(Rectangle.NO_BORDER);
		super.setPadding(0);
	}

	/**
	 * Creates a PdfPCell with a border and a padding of 6.
	 *
	 * @param data The cell content.
	 */
	public CellNoBorderNoPadding(Paragraph data)
	{
		super(data);
		super.setBorder(Rectangle.NO_BORDER);
		super.setPadding(0);
	}

}
