package org.areasy.common.doclet.document;

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

import com.lowagie.text.Chunk;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import org.areasy.common.doclet.AbstractConfiguration;
import org.areasy.common.doclet.document.elements.CellNoBorderNoPadding;
import org.areasy.common.doclet.document.elements.LinkPhrase;
import org.areasy.common.doclet.utilities.DocletUtility;
import org.areasy.common.doclet.utilities.PDFUtility;

/**
 * Prints the implementor info.
 *
 * @version $Id: Implementors.java,v 1.2 2008/05/14 09:36:47 swd\stefan.damian Exp $
 */
public class Implementors implements AbstractConfiguration
{
	/**
	 * Prints all known subclasses or implementing classes.
	 *
	 * @param title The label for the name list
	 * @param names The names (classes or interfaces)
	 * @throws Exception
	 */
	public static void print(String title, String[] names) throws Exception
	{
		float[] widths = {(float) 6.0, (float) 94.0};
		PdfPTable table = new PdfPTable(widths);
		table.setWidthPercentage((float) 100);

		PdfPCell spacingCell = new CellNoBorderNoPadding(new Phrase(""));
		spacingCell.setFixedHeight((float) 12.0);
		spacingCell.setColspan(2);
		table.addCell(spacingCell);

		PdfPCell titleCell = new CellNoBorderNoPadding(new Paragraph((float) 20.0, title, Fonts.getFont(TEXT_FONT, BOLD, 10)));
		titleCell.setColspan(2);
		table.addCell(titleCell);

		PdfPCell leftCell = PDFUtility.createElementCell(5, new Phrase(""));
		Paragraph descPg = new Paragraph((float) 24.0);

		for (int i = names.length - 1; i > -1; i--)
		{
			String subclassName = DocletUtility.getQualifiedNameIfNecessary(names[i]);
			Phrase subclassPhrase = new LinkPhrase(names[i], subclassName, 10, true);
			descPg.add(subclassPhrase);

			if (i > 0) descPg.add(new Chunk(", ", Fonts.getFont(TEXT_FONT, BOLD, 12)));
		}

		table.addCell(leftCell);
		table.addCell(new CellNoBorderNoPadding(descPg));

		table.addCell(spacingCell);
		Document.instance().add(table);
	}

}
