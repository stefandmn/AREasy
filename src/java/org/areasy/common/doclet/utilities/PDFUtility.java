package org.areasy.common.doclet.utilities;

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

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.areasy.common.data.StringUtility;
import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;
import org.areasy.common.support.configuration.Configuration;
import org.areasy.common.support.configuration.base.BaseConfiguration;
import org.areasy.common.support.configuration.providers.properties.BasePropertiesConfiguration;
import org.areasy.common.doclet.AbstractConfiguration;
import org.areasy.common.doclet.DefaultConfiguration;
import org.areasy.common.doclet.Doclet;
import org.areasy.common.doclet.document.*;
import org.areasy.common.doclet.document.Document;
import org.areasy.common.doclet.document.elements.CellNoBorderWithPadding;
import org.areasy.common.doclet.document.elements.LinkPhrase;
import org.areasy.common.doclet.document.elements.TableParagraph;
import org.areasy.common.doclet.document.tags.HtmlParserWrapper;

import java.io.File;
import java.io.FileInputStream;
import java.util.StringTokenizer;


/**
 * PDF utility class.
 *
 * @version $Id: PDFUtility.java,v 1.6 2008/05/14 09:36:48 swd\stefan.damian Exp $
 */
public class PDFUtility implements AbstractConfiguration
{

	/** Logger reference */
	private static Logger log = LoggerFactory.getLog(PDFUtility.class);

	/**
	 * Conversion procedure from HTML to PDF file format. This conversion is made without any other configuration.
	 * @param output output file (pdf document)
	 * @param input input file (html document)
	 */
	public static void Html2Pdf(File output, File input)
	{
		Html2Pdf(output, input, (Configuration)null);
	}

	/**
	 * Conversion procedure from HTML to PDF file format. Configuration structure is inherited from doclet utility.
	 *
	 * @param output output file (pdf document)
	 * @param input input file (html document)
	 * @param config configuration file (properties file)
	 */
	public static void Html2Pdf(File output, File input, File config)
	{
		Configuration configuration = null;

		try
		{
			configuration = BasePropertiesConfiguration.getConfiguration(config.getPath());
		}
		catch(Exception e)
		{
			log.error("Configuration file could not be read: " + e.getMessage());
			log.debug("Exception", e);
		}

		Html2Pdf(output, input, configuration);
	}

	/**
	 * Conversion methods from HTML to PDF file format. Configuration structure is inherited from doclet utility.
	 * This method is NOT thread safe.
	 *
	 * @param input an HTML file.
	 */
	public static void Html2Pdf(File output, File input, Configuration configuration)
	{
		String html;

		try
		{
			html = DocletUtility.getContentFromFile(input);
		}
		catch(Exception e)
		{
			log.error("Input file could not be read: " + e.getMessage());
			log.debug("Exception", e);

			return;
		}

		Html2Pdf(output, html, configuration);
	}


	/**
	 * Conversion methods from HTML to PDF file format. HTML content must be without <code>body</code>
	 * tags. Configuration structure is inherited from doclet utility. This method is NOT thread safe.
	 *
	 * @param html HTMl content.
	 */
	public static void Html2Pdf(File output, String html, Configuration configuration)
	{
		try
		{
			//instance configuration structure if is null.
			if(configuration == null) configuration = new BaseConfiguration();

			//set output file.
			Doclet.setPdfFile(output);

			//set configuration
			DefaultConfiguration.start(configuration);

			if(StringUtility.isNotEmpty(html))
			{
				DefaultConfiguration.setDesriptionContent(html);
				if(configuration.isEmpty()) configuration.setKey(ARG_DOC_DESCRIPTION_PAGE, "yes");
			}

			//initialize document
			Document.initialize();

			//open document
			Document.open();

			//init bookmarks.
			Bookmarks.init();

			// Print title page
			CustomTitle title = new CustomTitle(Document.instance());
			State.setCurrentHeaderType(HEADER_DETAILS);
			title.print();

			// Print description page
			CustomDescription description = new CustomDescription(Document.instance());
			State.setCurrentHeaderType(HEADER_DETAILS);
			description.print();

			//create bookmarks.
			Bookmarks.createBookmarkOutline();			

			//cloase document.
			Document.close();
		}
		catch(Exception e)
		{
            log.error("Error generating PDF document from HTML content: " + e.getMessage());
			log.debug("Exception", e);
		}
	}

	/**
	 * Inserts pages from a PDF file into the document.
	 *
	 * @param pdfFile The PDF file object.
	 * @param pages   The string with the page numbers.
	 * @throws Exception
	 */
	public static void insertPdfPages(File pdfFile, String pages) throws Exception
	{
		PdfReader reader = new PdfReader(new FileInputStream(pdfFile));

		if (pages != null && pages.length() > 0)
		{
			StringTokenizer tok = new StringTokenizer(pages, ",");
			boolean createPage = false;

			while (tok.hasMoreTokens())
			{
				String token = tok.nextToken();
				// Check if token is single page number or page range
				int dashPos = token.indexOf("-");
				if (dashPos != -1)
				{
					// It's a page range
					try
					{
						int from = Integer.parseInt(token.substring(0, dashPos));
						int to = Integer.parseInt(token.substring(dashPos + 1, token.length()));

						insertPdfPages(reader, from, to, createPage);
					}
					catch (NumberFormatException e)
					{
						DocletUtility.error("Invalid PDF page numbers: " + token, e);
					}
				}
				else
				{
					// It's a single page
					try
					{
						int no = Integer.parseInt(token);
						insertPdfPages(reader, no, no, createPage);
					}
					catch (NumberFormatException e)
					{
						DocletUtility.error("Invalid PDF page number: " + token, e);
					}
				}

				createPage = true;
			}
		}
		else
		{
			// By default, insert page 1
			insertPdfPages(reader, 1, 1, false);
		}
	}

/**
	 * Inserts a PDF file into the document.
	 *
	 * @param pdfFile The PDF file object.
	 * @throws Exception
	 */
	public static void insertPdfDocument(File pdfFile) throws Exception
	{
		PdfWriter writer = Document.getWriter();
		PdfReader reader = new PdfReader(new FileInputStream(pdfFile));

		for (int pageNo = 1; pageNo < reader.getNumberOfPages() + 1; pageNo++)
		{
			Document.newPage();

			PdfImportedPage page = writer.getImportedPage(reader, pageNo);
			writer.getDirectContent().addTemplate(page, 0.94f, 0, 0, 0.94f, 0, 30.0f);
		}
	}

	/**
	 * Inserts pages from a PDF file into the document. If only one
	 * page should be imported, both parameters must have the value
	 * of that page number.
	 *
	 * @param reader The reader from which to import the pages.
	 * @param from   The first page to import.
	 * @param to     The last page to import.
	 * @throws Exception
	 */
	private static void insertPdfPages(PdfReader reader, int from, int to, boolean createPage) throws Exception
	{
		PdfWriter writer = Document.getWriter();
		if (from == to)
		{
			if (createPage) Document.newPage();

			PdfImportedPage page = writer.getImportedPage(reader, from);
			writer.getDirectContent().addTemplate(page, 0.94f, 0, 0, 0.94f, 0, 30.0f);
		}
		else
		{
			for (int pageNo = from; pageNo < to + 1; pageNo++)
			{
				if (createPage) Document.newPage();

				PdfImportedPage page = writer.getImportedPage(reader, pageNo);
				writer.getDirectContent().addTemplate(page, 0.94f, 0, 0, 0.94f, 0, 30.0f);
				createPage = true;
			}
		}
	}

	/**
	 * Returns a Chunk that is an anchor destination for the given label.
	 * The Chunk will be empty (have no text), so it can be placed anywhere.
	 * The font size can make a difference in the PDF reader's behavior when
	 * jumped to -- usually it will make sure to make the entire line height
	 * visible.
	 */
	public static Chunk createAnchor(String destination, Font font)
	{
		final String EMPTY_TEXT = "\uFEFF";
		Chunk anchorChunk = new Chunk(EMPTY_TEXT, font);
		anchorChunk.setLocalDestination(destination);

		return anchorChunk;
	}

	/**
	 * Creates a local destination chunk.
	 *
	 * @param destination The name of the local destination.
	 * @return The chunk that can be inserted as an anchor.
	 */
	public static Chunk createAnchor(String destination)
	{
		return createAnchor(destination, new Font());
	}

	/**
	 * Returns a Chunk containing the return type of a
	 * method in text String form. The text is either a
	 * primitive data type (such as "int", "float") or
	 * the class type. In case of a class, the name is
	 * fully qualified for "external" classes (outside
	 * the given API package) or short for classes that
	 * belong to the same API.
	 *
	 * @param methodDoc The method of which the returntype
	 *                  should be determined.
	 * @return A Chunk with a text string.
	 * @throws Exception
	 */
	public static Phrase getReturnType(com.sun.javadoc.MethodDoc methodDoc, int fontSize) throws Exception
	{
		Phrase returnPhrase = null;
		String returnType = methodDoc.returnType().qualifiedTypeName();

		if ((returnType != null) && (returnType.trim().length() > 0))
		{
			String dimension = methodDoc.returnType().dimension();
			com.sun.javadoc.ClassDoc returnClass = methodDoc.returnType().asClassDoc();

			if (returnClass != null)
			{
				returnType = DocletUtility.getQualifiedNameIfNecessary(returnClass).trim();
				returnPhrase = new LinkPhrase(returnClass.qualifiedName(), returnType + dimension, fontSize, false);
			}
			else returnPhrase = new LinkPhrase(returnType, returnType + dimension, fontSize, false);
		}

		return returnPhrase;
	}

	/**
	 * Creates a table cell without border and without
	 * padding for a given Element.
	 *
	 * @param padding The padding for the cell.
	 * @param element The content element.
	 * @return The PdfPCell object.
	 */
	public static PdfPCell createElementCell(int padding, Element element)
	{
		// PdfPTable and List objects must be added to the cell directly.
		// If they are wrapped up in a Phrase or Paragraph, they won't show up.
		if (element instanceof PdfPTable) return new CellNoBorderWithPadding(padding, (PdfPTable) element);

		if (element instanceof List) return new CellNoBorderWithPadding(padding, (List) element);

		// Everything else can be wrapped in a Phrase.
		Phrase newPhrase = new Phrase();
		newPhrase.add(element);

		return new CellNoBorderWithPadding(padding, newPhrase);
	}

	/**
	 * Creates a table cell without border and with given
	 * padding and alignment for a given Element.
	 *
	 * @param padding   The padding for the cell.
	 * @param alignment The alignment for the cell.
	 * @param element   The content element.
	 * @return The PdfPCell object.
	 */
	public static PdfPCell createElementCell(int padding, int alignment, Element element)
	{
		PdfPCell cell = createElementCell(padding, element);
		cell.setHorizontalAlignment(alignment);

		return cell;
	}

	/**
	 * Create a cell element.
	 */
	public static PdfPCell createElementCell(int padding, int alignment, Element[] elements)
	{
		Element mainElement;

		// If there are no nested tables in content
		if (!hasTablesOrLists(elements))
		{
			Paragraph para = new Paragraph();
			para.setAlignment(alignment);

			for (int i = 0; i < elements.length; i++)
			{
				try
				{
					para.add(elements[i]);
				}
				catch (Exception e)
				{
					String msg = "Failed to add element to paragraph: " + e.toString();
					DocletUtility.error(msg);
				}
			}

			mainElement = para;

		}
		else
		{

			PdfPTable cellTable = new PdfPTable(1);
			Paragraph currInnerPara = null;

			for (int i = 0; i < elements.length; i++)
			{

				Element element = elements[i];

				/* Check for special element created by TagTABLE */
				if (element instanceof TableParagraph) element = ((TableParagraph) elements[i]).getTable();

				if (element instanceof PdfPTable || element instanceof List)
				{

					if (currInnerPara != null)
					{
						PdfPCell innerCell = createElementCell(0, alignment, currInnerPara);
						innerCell.setUseDescender(true); // needs newer iText
						innerCell.setUseAscender(true);  // needs newer iText
						innerCell.setPaddingBottom(2.0f);
						cellTable.addCell(innerCell);
					}

					currInnerPara = null;
					cellTable.addCell(createElementCell(0, alignment, element));

				}
				else
				{
					if (currInnerPara == null)
					{
						currInnerPara = new Paragraph();
						currInnerPara.setAlignment(alignment);
					}

					try
					{
						currInnerPara.add(element);
					}
					catch (Exception e)
					{
						String msg = "Failed to add element to inner paragraph: " + e.toString();
						DocletUtility.error(msg);
					}
				}
			}

			if (currInnerPara != null) cellTable.addCell(createElementCell(0, alignment, currInnerPara));

			mainElement = cellTable;
		}

		return createElementCell(padding, alignment, mainElement);
	}

	/**
	 * Checks if the given list of objects contains any PdfPTable,
	 * TableParagraph or List objects.
	 *
	 * @param objs The list of objects to check.
	 * @return True if there is any one of these element types in the list.
	 */
	public static boolean hasTablesOrLists(Element[] objs)
	{
		for (int i = 0; i < objs.length; i++)
		{
			if (objs[i] instanceof PdfPTable || objs[i] instanceof TableParagraph || objs[i] instanceof List)
			{
				return true;
			}
		}

		return false;
	}

	/**
	 * Get parameter type phase.
	 * 
	 * @param param parameter structure.
	 * @param fontSize font size
	 * @return a link phase
	 */
	public static LinkPhrase getParameterTypePhrase(com.sun.javadoc.Parameter param, int fontSize)
	{
		String fullType = DocletUtility.getQualifiedParameterType(param);
		String shortType = DocletUtility.getParameterType(param);

		return new LinkPhrase(fullType, shortType, fontSize, false);
	}

	/**
	 * Creates a list of Paragraph objects for one method based
	 * on a given list of parameters.
	 *
	 * @param parmsColumn The column at which to begin printing text.
	 * @param parms       List of Parameters of the method.
	 * @throws Exception
	 */
	public static Paragraph[] createParameters(int parmsColumn, com.sun.javadoc.Parameter[] parms) throws Exception
	{
		Paragraph[] para = new Paragraph[0];

		// lines with more parameters
		if ((parms != null) && (parms.length > 1))
		{
			para = new Paragraph[parms.length - 1];

			// begin with parm 1 (second parm), because 1st one has
			// already been printed
			for (int i = 1; i < parms.length; i++)
			{
				String text = "";

				for (int u = 0; u < parmsColumn; u++)
				{
					text = text + " ";
				}

				Chunk emptySpaceLeft = new Chunk(text + "       ", Fonts.getFont(CODE_FONT, 10));
				LinkPhrase link = getParameterTypePhrase(parms[i], 10);
				String rightText = " " + parms[i].name();

				if (i < (parms.length - 1)) rightText = rightText + ",";
				if (i == (parms.length - 1)) rightText = rightText + ")";

				Chunk parmName = new Chunk(rightText, Fonts.getFont(CODE_FONT, 10));
				para[i - 1] = new Paragraph((float) 10.0, "", Fonts.getFont(CODE_FONT, 10));
				para[i - 1].add(emptySpaceLeft);
				para[i - 1].add(link);
				para[i - 1].add(parmName);
			}
		}

		return para;
	}

	/**
	 * Creates a list of Paragraph objects for one method based
	 * on a given list of exceptions.
	 *
	 * @param parmsColumn      The column at which to begin printing text.
	 * @param thrownExceptions List of exceptions thrown by the method.
	 * @throws Exception
	 */
	public static Paragraph[] createExceptions(int parmsColumn, com.sun.javadoc.ClassDoc[] thrownExceptions) throws Exception
	{
		Paragraph[] para = new Paragraph[0];

		// lines with exceptions
		if ((thrownExceptions != null) && (thrownExceptions.length > 0))
		{
			para = new Paragraph[thrownExceptions.length];

			for (int i = 0; i < thrownExceptions.length; i++)
			{
				com.sun.javadoc.ClassDoc exp = thrownExceptions[i];
				String text = "";

				for (int u = 0; u < parmsColumn; u++)
				{
					text = text + " ";
				}

				if (i > 0) text = text + "       ";
					else text = text + "throws ";


				Chunk emptySpaceLeft = new Chunk(text, Fonts.getFont(CODE_FONT, 10));
				String type = exp.qualifiedName();
				String label = DocletUtility.getQualifiedNameIfNecessary(type);
				LinkPhrase link = new LinkPhrase(type, label, Fonts.getFont(CODE_FONT, 10));
				para[i] = new Paragraph((float) 10.0, "", Fonts.getFont(CODE_FONT, 10));
				para[i].add(emptySpaceLeft);
				para[i].add(link);

				if (i < (thrownExceptions.length - 1)) para[i].add(new Chunk(",", Fonts.getFont(CODE_FONT, 10)));
			}
		}

		return para;
	}

	/**
	 * Creates Chunks for informational javadoc tags
	 *
	 * @param destination The name of the package/class/method this
	 *                    link should point to.
	 * @param label       The text of the link displayed to the user.
	 * @return A Chunk with a hyperlink.
	 * @throws Exception
	 */
	public static Phrase createTagPhrase(String destination, String label) throws Exception
	{
		Phrase result = null;

		if (label == null) label = destination;

		String finalDestination = destination;
		String createLinksProp = DefaultConfiguration.getString(ARG_CREATE_LINKS, ARG_VAL_NO);

		if (createLinksProp.equalsIgnoreCase(ARG_VAL_NO)) finalDestination = null;

		if (finalDestination != null) result = new LinkPhrase(destination, label, 10, true);
		else
		{
			result = new Phrase(new Chunk("", Fonts.getFont(CODE_FONT, LINK, 10)));

			Element[] objs = HtmlParserWrapper.createPdfObjects(label);

			for (int i = 0; i < objs.length; i++)
			{
				result.add(objs[i]);
			}
		}

		return result;
	}

	/**
	 * Inserts a horizontal line into the document at
	 * the current position.
	 *
	 * @throws Exception
	 */
	public static void printLine() throws Exception
	{
		Document.instance().add(new Paragraph((float) 12.0, " "));
		Document.instance().add(new Paragraph((float) 1.0, " "));

		Graphic graphic = new Graphic();
		graphic.setHorizontalLine(1f, 100f);
		
		Document.instance().add(graphic);
	}

	/**
	 * Prints an array of elements
	 * to a PDF file.
	 *
	 * @param objs The elements
	 */
	public static void printPdfElements(Element[] objs) throws com.lowagie.text.DocumentException
	{

		for (int i = 0; i < objs.length; i++)
		{
			if (objs[i] != null) Document.instance().add(objs[i]);
				else log.warn("Null element returned!");
		}
	}
}
