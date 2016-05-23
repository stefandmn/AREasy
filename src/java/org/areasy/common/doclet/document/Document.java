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

import com.lowagie.text.DocumentException;
import com.lowagie.text.Graphic;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.areasy.common.doclet.AbstractConfiguration;
import org.areasy.common.doclet.DefaultConfiguration;
import org.areasy.common.doclet.Doclet;

import java.io.FileOutputStream;

/**
 * Encapsulates a PDF document. Handles initialization of
 * and access to the document instance.
 *
 * @version $Id: Document.java,v 1.4 2008/05/14 09:36:47 swd\stefan.damian Exp $
 */
public class Document implements AbstractConfiguration
{
	/**
	 * Document instance.
	 */
	private static com.lowagie.text.Document pdfDocument = null;

	/**
	 * Stores a reference to the PdfWriter instance.
	 */
	private static PdfWriter pdfWriter = null;

	/**
	 * Creates the document instance and initializes it.
	 */
	public static void initialize() throws Exception
	{
		// step 1: creation of a document-object
		pdfDocument = new com.lowagie.text.Document();

		float leftmargin = DefaultConfiguration.getFloat(ARG_DOC_MARGIN_LEFT, LEFT_MARGIN_WIDTH);
		float rightmargin = DefaultConfiguration.getFloat(ARG_DOC_MARGIN_RIGHT, RIGHT_MARGIN_WIDTH);
		float topmargin = DefaultConfiguration.getFloat(ARG_DOC_MARGIN_TOP, TOP_MARGIN_WIDTH);
		float bottommargin = DefaultConfiguration.getFloat(ARG_DOC_MARGIN_BOTTOM, BOTTOM_MARGIN_WIDTH);
		boolean printing = DefaultConfiguration.getBooleanConfigValue(ARG_ALLOW_PRINTING, ARG_VAL_YES);
		boolean encrypting = DefaultConfiguration.getBooleanConfigValue(ARG_ALLOW_ENCRYPTION, ARG_VAL_NO);

		// set left-, right-, top- and bottom-margins
		pdfDocument.setMargins(leftmargin, rightmargin, topmargin, bottommargin);

		// step 2:
		// we create a writer that listens to the document and directs a PDF-stream to a file
		pdfWriter = PdfWriter.getInstance(pdfDocument, new FileOutputStream(Doclet.getPdfFile()));

		if (encrypting)
		{
			if (printing) pdfWriter.setEncryption(PdfWriter.STRENGTH40BITS, null, null, PdfWriter.AllowPrinting);
				else pdfWriter.setEncryption(PdfWriter.STRENGTH40BITS, null, null, 0);
		}

		//set document data
		pdfDocument.addAuthor(DefaultConfiguration.getConfiguration().getString(ARG_DOC_AUTHOR, ""));
		pdfDocument.addSubject(DefaultConfiguration.getConfiguration().getString(ARG_DOC_TITLE, ""));
		pdfDocument.addTitle(DefaultConfiguration.getConfiguration().getString(ARG_DOC_TITLE, ""));
	}

	/**
	 * Open document instance to append content.
	 *
	 * @throws Exception
	 */
	public static void open() throws Exception
	{
		pdfDocument.open();

		pdfWriter.setPageEvent(new DocumentEventHandler(pdfWriter));
	}

	/**
	 * Returns a reference to the PdfWriter instance.
	 *
	 * @return The PdfWriter object.
	 */
	public static PdfWriter getWriter()
	{
		return pdfWriter;
	}

	/**
	 * Returns a reference to the PDF document object.
	 *
	 * @return The PDF document object.
	 */
	public static com.lowagie.text.Document instance()
	{
		return pdfDocument;
	}

	/**
	 * Conveniency method
	 */
	public static void add(PdfPTable table) throws DocumentException
	{
		pdfDocument.add(table);
	}

	/**
	 * Conveniency method
	 */
	public static void add(Paragraph label) throws DocumentException
	{
		pdfDocument.add(label);
	}

	/**
	 * Conveniency method
	 */
	public static void add(Graphic graphic) throws DocumentException
	{
		pdfDocument.add(graphic);
	}

	/**
	 * Conveniency method
	 */
	public static void newPage() throws DocumentException
	{
		pdfDocument.newPage();
	}

	/**
	 * Conveniency method
	 */
	public static void close()
	{
		pdfDocument.close();
	}

	/**
	 * Add document author
	 */
	public static void addAuthor(String author)
	{
		pdfDocument.addAuthor(author);
	}


	/**
	 * Add document title
	 */
	public static void addTitle(String title)
	{
		pdfDocument.addTitle(title);
	}

	/**
	 * Add document title
	 */
	public static void addSubject(String subject)
	{
		pdfDocument.addSubject(subject);
	}

}
