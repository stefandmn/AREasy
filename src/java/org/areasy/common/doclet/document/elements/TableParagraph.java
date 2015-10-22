package org.areasy.common.doclet.document.elements;

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

import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;

/**
 * A Paragraph that contains a single PdfPTable instance (or at least starts with one).
 * The normal add() method does not work for these; the protected addSpecial() method
 * must be used instead which requires a subclass.
 *
 * @version $Id: TableParagraph.java,v 1.2 2008/05/14 09:36:47 swd\stefan.damian Exp $
 */
public class TableParagraph extends Paragraph
{

	/**
	 * Default constructor.
	 *
	 * @param table pdf table cell
	 */
	public TableParagraph(PdfPTable table)
	{
		super();
		addSpecial(table);
	}

	/**
	 * Get pdf table cell
	 */
	public PdfPTable getTable()
	{
		return (PdfPTable) get(0);
	}
}
