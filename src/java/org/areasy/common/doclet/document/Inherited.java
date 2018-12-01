package org.areasy.common.doclet.document;

/*
 * Copyright (c) 2007-2018 AREasy Runtime
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
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;
import org.areasy.common.doclet.AbstractConfiguration;
import org.areasy.common.doclet.DefaultConfiguration;
import org.areasy.common.doclet.document.elements.CellBorderPadding;
import org.areasy.common.doclet.document.elements.CustomPdfPCell;
import org.areasy.common.doclet.document.elements.CustomPdfPTable;
import org.areasy.common.doclet.document.elements.LinkPhrase;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;

import java.awt.*;
import java.util.Arrays;

/**
 * Prints the inherited tables.
 *
 * @version $Id: Inherited.java,v 1.3 2008/05/14 09:36:47 swd\stefan.damian Exp $
 */
public class Inherited implements AbstractConfiguration
{

	/**
	 * Logger reference
	 */
	private static Logger log = LoggerFactory.getLog(Inherited.class);

	/**
	 * Prints inherited methods and fields from superclasses
	 *
	 * @param supercls  class source to get inherited fields and methods for.
	 * @param show SHOW_METHODS or SHOW_FIELDS
	 * @throws Exception
	 */
	public static void print(ClassDoc supercls, int show) throws Exception
	{
		String type;

		FieldDoc[] fields = supercls.fields();

		Arrays.sort(fields);

		if (supercls.isInterface()) type = "interface";
			else type = "class";

		// Create cell for additional spacing below
		PdfPCell spacingCell = new PdfPCell();
		spacingCell.addElement(new Chunk(" "));
		spacingCell.setFixedHeight((float) 4.0);
		spacingCell.setBorder(Rectangle.BOTTOM + Rectangle.LEFT + Rectangle.RIGHT);
		spacingCell.setBorderColor(Color.gray);

		if ((fields.length > 0) && (show == SHOW_FIELDS))
		{
			Document.instance().add(new Paragraph((float) 6.0, " "));

			PdfPTable table = new PdfPTable(1);
			table.setWidthPercentage((float) 100);

			Paragraph newLine = new Paragraph();
			newLine.add(new Chunk("Fields inherited from " + type + " ", Fonts.getFont(TEXT_FONT, BOLD, 10)));
			newLine.add(new LinkPhrase(supercls.qualifiedTypeName(), null, 10, false));

			table.addCell(new CustomPdfPCell(newLine, COLOR_INHERITED_SUMMARY));

			Paragraph paraList = new Paragraph();

			for (int i = 0; i < fields.length; i++)
			{
				paraList.add(new LinkPhrase(fields[i].qualifiedName(), fields[i].name(), 10, false));

				if (i != (fields.length - 1)) paraList.add(new Chunk(", ", Fonts.getFont(TEXT_FONT, BOLD, 12)));
			}

			PdfPCell contentCell = new CellBorderPadding(paraList);
			float leading = (float) contentCell.getLeading() + (float) 1.1;
			contentCell.setLeading(leading, leading);
			table.addCell(contentCell);
			table.addCell(spacingCell);

			Document.instance().add(table);
		}

		MethodDoc[] meth = supercls.methods();

		Arrays.sort(meth);

		if ((meth.length > 0) && (show == SHOW_METHODS))
		{
			Document.instance().add(new Paragraph((float) 6.0, " "));

			PdfPTable table = new CustomPdfPTable();

			Paragraph newLine = new Paragraph();
			newLine.add(new Chunk("Methods inherited from " + type + " ", Fonts.getFont(TEXT_FONT, BOLD, 10)));
			newLine.add(new LinkPhrase(supercls.qualifiedTypeName(), null, 10, false));

			table.addCell(new CustomPdfPCell(newLine, COLOR_INHERITED_SUMMARY));
			Paragraph paraList = new Paragraph();

			for (int i = 0; i < meth.length; i++)
			{
				String methodLabel = meth[i].name();

				// Do not list static initializers like "<clinit>"
				if (!methodLabel.startsWith("<"))
				{
					paraList.add(new LinkPhrase(supercls.qualifiedTypeName() + "." + meth[i].name(), meth[i].name(), 10, false));

					if (i != (meth.length - 1)) paraList.add(new Chunk(", ", Fonts.getFont(CODE_FONT, 10)));
				}
			}

			PdfPCell contentCell = new CellBorderPadding(paraList);
			float leading = (float) contentCell.getLeading() + (float) 1.1;
			contentCell.setLeading(leading, leading);
			table.addCell(contentCell);
			table.addCell(spacingCell);

			Document.instance().add(table);
		}

		// Print inherited interfaces / class methods and fields recursively
		ClassDoc supersupercls = null;

		if (supercls.isClass()) supersupercls = supercls.superclass();

		if (supersupercls != null)
		{
			String className = supersupercls.qualifiedName();
			if (ifClassMustBePrinted(className)) Inherited.print(supersupercls, show);
		}

		ClassDoc[] interfaces = supercls.interfaces();
		for (int i = 0; i < interfaces.length; i++)
		{
			supersupercls = interfaces[i];
			String className = supersupercls.qualifiedName();
			if (ifClassMustBePrinted(className)) Inherited.print(supersupercls, show);
		}
	}

	/**
	 * @param className
	 * @return
	 */
	public static boolean ifClassMustBePrinted(String className)
	{
		boolean printClass = true;
		if (!Destinations.isValid(className) && !DefaultConfiguration.isShowExternalInheritedSummaryActive()) printClass = false;

		return printClass;
	}
}
