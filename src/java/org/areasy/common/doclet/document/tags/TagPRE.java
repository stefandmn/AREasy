package org.areasy.common.doclet.document.tags;

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

import com.lowagie.text.Element;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import org.areasy.common.doclet.document.elements.CustomPdfPTable;

import java.awt.*;


/**
 * Implements the PRE tag.
 *
 * @version $Id: TagPRE.java,v 1.2 2008/05/14 09:36:47 swd\stefan.damian Exp $
 */
public class TagPRE extends HtmlTag
{
	private PdfPTable mainTable = new CustomPdfPTable();
	private Paragraph cellPara = null;
	private PdfPCell colorTitleCell = null;

	/**
	 * Creates a PRE tag object.
	 *
	 * @param parent The parent HTML object.
	 * @param type   The type for this tag.
	 */
	public TagPRE(HtmlTag parent, int type)
	{
		super(parent, type);
		setPre(true);

		cellPara = new Paragraph("", getFont());

		colorTitleCell = new PdfPCell(cellPara);
		colorTitleCell.setBorder(Rectangle.TOP + Rectangle.LEFT + Rectangle.RIGHT + Rectangle.BOTTOM);
		colorTitleCell.setPadding(6);
		colorTitleCell.setPaddingBottom(12);
		colorTitleCell.setPaddingLeft(10);
		colorTitleCell.setPaddingRight(10);
		colorTitleCell.setBorderWidth(1);
		colorTitleCell.setBorderColor(Color.gray);
		colorTitleCell.setBackgroundColor(COLOR_LIGHTER_GRAY);
		colorTitleCell.addElement(cellPara);

		mainTable.addCell(colorTitleCell);
	}

	/*
	 *  (non-Javadoc)
	 * @see org.areasy.common.doclet.document.tags.HTMLTag#toElement(java.lang.String)
	 */
	public Element toElement(String text)
	{
		cellPara.add(text);
		return null;
	}

	/*
	 *  (non-Javadoc)
	 * @see org.areasy.common.doclet.document.tags.HTMLTag#addNestedTagContent(com.lowagie.text.Element[])
	 */
	public void addNestedTagContent(Element[] content)
	{
		for (int i = 0; i < content.length; i++)
		{
			cellPara.add(content[i]);
		}
	}


	/*
	 *  (non-Javadoc)
	 * @see org.areasy.common.doclet.document.tags.HTMLTag#closeTagElements()
	 */
	public Element[] closeTagElements()
	{
		Element[] elements = new Element[2];

		Paragraph empty = createParagraph("");
		empty.setLeading(getFont().size() + (float) 1.0);

		elements[0] = createParagraph(" ");
		elements[1] = createParagraph("");

		return elements;
	}

	/*
	 *  (non-Javadoc)
	 * @see org.areasy.common.doclet.document.tags.HTMLTag#openTagElements()
	 */
	public Element[] openTagElements()
	{
		Element[] elements = new Element[3];

		Paragraph empty = createParagraph("");
		empty.setLeading(getFont().size() + (float) 1.0);

		elements[0] = createParagraph(" ");
		elements[1] = createParagraph("");
		elements[2] = mainTable;

		return elements;
	}

	/*
	 *  (non-Javadoc)
	 * @see org.areasy.common.doclet.document.tags.HTMLTag#isPre()
	 */
	public boolean isPre()
	{
		return true;
	}
}
