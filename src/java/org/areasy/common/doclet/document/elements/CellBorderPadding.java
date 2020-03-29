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

import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;

import java.awt.*;

/**
 * Customized version(s) of PdfPCell with
 * a border of with 1 (gray) and a padding of 6.
 *
 * @version $Id: CellBorderPadding.java,v 1.3 2008/05/14 09:36:47 swd\stefan.damian Exp $
 */
public class CellBorderPadding extends PdfPCell
{
	/**
	 * Creates a PdfPCell with a border and a padding of 6.
	 *
	 * @param data The cell content.
	 */
	public CellBorderPadding(Phrase data)
	{
		super(data);
		super.setBorder(Rectangle.TOP + Rectangle.LEFT + Rectangle.BOTTOM + Rectangle.RIGHT);
		super.setPadding(6);
		super.setBorderWidth(1);
		super.setBorderColor(Color.gray);
	}

	/**
	 * Creates a PdfPCell with no bottom border.
	 *
	 * @param data The cell content.
	 */
	public CellBorderPadding(Paragraph data)
	{
		super(data);
		super.setBorder(Rectangle.TOP + Rectangle.LEFT + Rectangle.RIGHT);
		super.setPadding(6);
		super.setBorderWidth(1);
		super.setBorderColor(Color.gray);
	}

}
