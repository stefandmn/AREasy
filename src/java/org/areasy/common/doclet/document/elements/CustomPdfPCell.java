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
import org.areasy.common.doclet.AbstractConfiguration;
import org.areasy.common.doclet.document.Fonts;

import java.awt.*;

/**
 * Customized version(s) of PdfPCell.
 *
 * @version $Id: CustomPdfPCell.java,v 1.3 2008/05/14 09:36:47 swd\stefan.damian Exp $
 */
public class CustomPdfPCell extends PdfPCell implements AbstractConfiguration
{
	/**
	 * A coloured title bar (for the "Fields", "Methods" and
	 * "Constructors" titles).
	 */
	public CustomPdfPCell(String title)
	{
		super(new Phrase(title, Fonts.getFont(TEXT_FONT, 18)));
		super.setPaddingTop((float) 0.0);
		super.setPaddingBottom((float) 5.0);
		super.setPaddingLeft((float) 3.0);
		super.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
		super.setBackgroundColor(COLOR_SUMMARY_HEADER);
		super.setBorder(Rectangle.TOP + Rectangle.LEFT + Rectangle.BOTTOM + Rectangle.RIGHT);
		super.setBorderWidth(1);
		super.setBorderColor(Color.gray);
	}

	/**
	 * A coloured title bar (for summary tables etc.)
	 *
	 * @param paragraph       The text for the title.
	 * @param backgroundColor Color of the cell
	 */
	public CustomPdfPCell(Paragraph paragraph, Color backgroundColor)
	{
		super(paragraph);
		super.setPaddingTop((float) 0.0);
		super.setPaddingBottom((float) 5.0);
		super.setPaddingLeft((float) 3.0);
		super.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
		super.setBackgroundColor(backgroundColor);
		super.setBorder(Rectangle.TOP + Rectangle.LEFT + Rectangle.BOTTOM + Rectangle.RIGHT);
		super.setBorderWidth(1);
		super.setBorderColor(Color.gray);
	}

	/**
	 * Creates a PdfPCell with certain attributes
	 *
	 * @param border      The border type for the cell
	 * @param phrase      The content for the cell.
	 * @param borderWidth The border width for the cell
	 * @param borderColor The color of the border
	 */
	public CustomPdfPCell(int border, Phrase phrase, int borderWidth, Color borderColor)
	{

		super(phrase);
		super.setBorderColor(borderColor);
		super.setBorderWidth(borderWidth);
		super.setPaddingBottom((float) 6.0);
		super.setPaddingLeft((float) 6.0);
		super.setPaddingRight((float) 6.0);

		super.setBorder(border);

		super.setVerticalAlignment(PdfPCell.ALIGN_TOP);
		super.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
	}

}
