package org.areasy.common.doclet.document.elements;

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

import com.lowagie.text.List;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;

/**
 * Customized version(s) of PdfPCell without
 * a border but with a given padding.
 *
 * @version $Id: CellNoBorderWithPadding.java,v 1.2 2008/05/14 09:36:47 swd\stefan.damian Exp $
 */
public class CellNoBorderWithPadding extends PdfPCell
{
	/**
	 * Creates a PdfPCell with a given padding and
	 * an additional wrapping Phrase for the given Phrase.
	 *
	 * @param padding The padding for the PdfPCell
	 * @param data    The content for the cell
	 */
	public CellNoBorderWithPadding(int padding, Phrase data)
	{
		super(data);
		super.setBorder(Rectangle.NO_BORDER);
		super.setPadding(padding);
	}

	public CellNoBorderWithPadding(int padding, PdfPTable table)
	{
		super(table);
		super.setBorder(Rectangle.NO_BORDER);
		super.setPadding(padding);
	}

	public CellNoBorderWithPadding(int padding, List list)
	{
		super();
		super.addElement(list);
		super.setBorder(Rectangle.NO_BORDER);
		super.setPadding(padding);
	}
}
