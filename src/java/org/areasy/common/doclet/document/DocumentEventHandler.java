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

import com.lowagie.text.Document;
import com.lowagie.text.pdf.*;
import org.areasy.common.doclet.AbstractConfiguration;
import org.areasy.common.doclet.DefaultConfiguration;

/**
 * Handler for PDF document page events. This class
 * creates headers and footers and the navigation frame (outline).
 *
 * @version $Id: DocumentEventHandler.java,v 1.4 2008/05/14 09:36:47 swd\stefan.damian Exp $
 */
public class DocumentEventHandler extends PdfPageEventHelper implements AbstractConfiguration
{
	private static String leftHeader = "";
	private static String centerHeader = "";
	private static String rightHeader = "";

	private BaseFont bf = null;
	private PdfContentByte cb = null;
	private int currentPage = 0;
	private int PAGE_NUMBER_SIMPLE = 1;
	private int PAGE_NUMBER_FULL = 2;
	private int pageNumberType = PAGE_NUMBER_FULL;
	private int PAGE_NUMBER_ALIGN_LEFT = 1;
	private int PAGE_NUMBER_ALIGN_CENTER = 2;
	private int PAGE_NUMBER_ALIGN_RIGHT = 3;
	private int PAGE_NUMBER_ALIGN_SWITCH = 4;
	private int pageNumberAlign = PAGE_NUMBER_ALIGN_CENTER;

	// we will put the final number of pages in a template
	private PdfTemplate template;

	/**
	 * Constructs an event handler for a given PDF writer.
	 *
	 * @param pdfWriter The writer used to create the document.
	 * @throws Exception
	 */
	public DocumentEventHandler(PdfWriter pdfWriter) throws Exception
	{
		cb = pdfWriter.getDirectContent();
		bf = BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
		template = cb.createTemplate(bf.getWidthPoint("Page 999 of 999", 8), 250);

		leftHeader = DefaultConfiguration.getString(ARG_HEADER_LEFT, "");
		centerHeader = DefaultConfiguration.getString(ARG_HEADER_CENTER, "");
		rightHeader = DefaultConfiguration.getString(ARG_HEADER_RIGHT, "");

		String pageNumberTypeValue = DefaultConfiguration.getString(ARG_PGN_TYPE, ARG_VAL_FULL);

		if (pageNumberTypeValue.equalsIgnoreCase(ARG_VAL_FULL)) pageNumberType = PAGE_NUMBER_FULL;
			else pageNumberType = PAGE_NUMBER_SIMPLE;

		String pageNumberAlignValue = DefaultConfiguration.getString(ARG_PGN_ALIGNMENT, ARG_VAL_SWITCH);

		if (pageNumberAlignValue.equalsIgnoreCase(ARG_VAL_LEFT)) pageNumberAlign = PAGE_NUMBER_ALIGN_LEFT;

		if (pageNumberAlignValue.equalsIgnoreCase(ARG_VAL_CENTER)) pageNumberAlign = PAGE_NUMBER_ALIGN_CENTER;

		if (pageNumberAlignValue.equalsIgnoreCase(ARG_VAL_RIGHT)) pageNumberAlign = PAGE_NUMBER_ALIGN_RIGHT;

		if (pageNumberAlignValue.equalsIgnoreCase(ARG_VAL_SWITCH)) pageNumberAlign = PAGE_NUMBER_ALIGN_SWITCH;
	}

	/**
	 * At the end of each page, index information is collected
	 * and footer and headers are inserted.
	 *
	 * @param document The current PDF document.
	 * @param writer   The writer used to create the document.
	 */
	public void onEndPage(PdfWriter writer, Document document)
	{
		currentPage = document.getPageNumber();
		State.setCurrentPage(currentPage);

		if (State.getCurrentHeaderType() != HEADER_DEFAULT)
		{
			float len;

			if (State.isContinued() && !State.isLastMethod())
			{
				String cont = "(continued on next page)";
				len = bf.getWidthPoint(cont, 7);
				cb.beginText();
				cb.setFontAndSize(bf, 7);
				cb.setTextMatrix(300 - (len / 2), 56);
				cb.showText(cont);
				cb.endText();
			}

			if (State.getCurrentHeaderType() != HEADER_DETAILS)
			{
				// add lines
				cb.setLineWidth(1f);
				cb.moveTo(LEFT_MARGIN, 812);
				cb.lineTo(RIGHT_MARGIN, 812);
			}

			cb.moveTo(LEFT_MARGIN, 42);
			cb.lineTo(RIGHT_MARGIN, 42);
			cb.stroke();

			// page footer with number of pages
			float textX = (float) 0.0;
			float templateX = (float) 0.0;
			float textWidth = (float) 0.0;
			float numWidth = (float) 0.0;
			int currPageNumberAlign;

			if (pageNumberAlign == PAGE_NUMBER_ALIGN_SWITCH)
			{
				if ((currentPage % 2) == 0) currPageNumberAlign = PAGE_NUMBER_ALIGN_LEFT;
					else currPageNumberAlign = PAGE_NUMBER_ALIGN_RIGHT;
			}
			else currPageNumberAlign = pageNumberAlign;

			String text = DefaultConfiguration.getString(ARG_PGN_PREFIX, "Page ") + currentPage;
			if (pageNumberType == PAGE_NUMBER_FULL) text = text + " of ";


			textWidth = bf.getWidthPoint(text, 8);
			numWidth = bf.getWidthPoint("999", 8);

			if (currPageNumberAlign == PAGE_NUMBER_ALIGN_LEFT) textX = LEFT_MARGIN;

			if (currPageNumberAlign == PAGE_NUMBER_ALIGN_CENTER) textX = (float) (DOCUMENT_WIDTH / 2) - (textWidth / 2);

			if (currPageNumberAlign == PAGE_NUMBER_ALIGN_RIGHT) textX = RIGHT_MARGIN - textWidth - numWidth;

			templateX = textX + textWidth;

			cb.beginText();
			cb.setFontAndSize(bf, 8);

			if (currPageNumberAlign == PAGE_NUMBER_ALIGN_LEFT)
			{
				cb.showTextAligned(PdfContentByte.ALIGN_LEFT,
						text,
						textX,
						FOOTER_BASELINE,
						0);
			}

			if (currPageNumberAlign == PAGE_NUMBER_ALIGN_CENTER)
			{
				cb.showTextAligned(PdfContentByte.ALIGN_LEFT,
						text,
						textX,
						FOOTER_BASELINE,
						0);
			}

			if (currPageNumberAlign == PAGE_NUMBER_ALIGN_RIGHT)
			{
				cb.showTextAligned(PdfContentByte.ALIGN_LEFT,
						text,
						textX,
						FOOTER_BASELINE,
						0);
			}

			cb.endText();

			if (pageNumberType == PAGE_NUMBER_FULL)
			{
				// add template for total page number
				cb.addTemplate(template, templateX, FOOTER_BASELINE);
			}

			// headers (left, right, center)
			// temporary solution: handling of first page of package
			// not correct yet, so use fix heading configuration now
			if (State.getCurrentHeaderType() == HEADER_API)
			{
				leftHeader = "";
				centerHeader = "$CLASS";
				rightHeader = "";
			}

			if (State.getCurrentHeaderType() == HEADER_INDEX)
			{
				leftHeader = "";
				centerHeader = "Index";
				rightHeader = "";
			}

			if (State.getCurrentHeaderType() == HEADER_DETAILS)
			{
				leftHeader = "";
				centerHeader = "";
				rightHeader = "";
			}

			cb.beginText();
			cb.setFontAndSize(bf, 8);
			cb.showTextAligned(PdfContentByte.ALIGN_CENTER,
					parseHeader(centerHeader),
					DOCUMENT_WIDTH / 2,
					HEADER_BASELINE,
					0);
			cb.endText();

			cb.beginText();
			cb.setFontAndSize(bf, 8);
			cb.showTextAligned(PdfContentByte.ALIGN_LEFT,
					parseHeader(leftHeader),
					LEFT_MARGIN,
					HEADER_BASELINE,
					0);
			cb.endText();

			cb.beginText();
			cb.setFontAndSize(bf, 8);
			cb.showTextAligned(PdfContentByte.ALIGN_RIGHT,
					parseHeader(rightHeader),
					RIGHT_MARGIN,
					HEADER_BASELINE,
					0);
			cb.endText();
		}
	}

	private String parseHeader(String text)
	{
		if (text.equalsIgnoreCase("$SHORTCLASS"))
		{
			String name = State.getCurrentClass();

			if (name.indexOf(".") != -1) name = name.substring(name.lastIndexOf(".") + 1, name.length());

			return name;
		}

		if (text.equalsIgnoreCase("$CLASS")) return State.getCurrentClass();

		if (text.equalsIgnoreCase("$PACKAGE")) return State.getCurrentPackage();

		return text;
	}

	/**
	 * This method is called every time a new paragraph begins.
	 * It is used to create the frame (or outline) with all
	 * classes in the PDF document.
	 */
	public void onParagraph(PdfWriter writer, Document document, float position)
	{
		//nothing to do here
	}

	/**
	 * Creates bookmarks (navigation tree) entry for method
	 */
	public void onParagraphEnd(PdfWriter writer, Document document, float position)
	{
		//nothing to do here
	}


	/**
	 * This method is called when a new page is started.
	 * It is used to print the "(continued from last page)"
	 * header if necessary.
	 */
	public void onStartPage(PdfWriter writer, Document document)
	{
		if (State.isContinued())
		{
			try
			{
				if (State.getCurrentHeaderType() != HEADER_DEFAULT)
				{
					float len;
					String cont = "(continued from last page)";
					len = bf.getWidthPoint(cont, 7);
					cb.beginText();
					cb.setFontAndSize(bf, 7);
					cb.setTextMatrix(300 - (len / 2), 796);
					cb.showText(cont);
					cb.endText();
				}
			}
			catch (Exception e)
			{
			}
		}
	}

	// we override the onCloseDocument method
	public void onCloseDocument(PdfWriter writer, Document document)
	{
		if (pageNumberType == PAGE_NUMBER_FULL)
		{
			template.beginText();
			template.setFontAndSize(bf, 8);
			template.showText(String.valueOf(writer.getPageNumber() - 1));
			template.endText();
		}
	}
}
