package org.areasy.common.doclet.document;

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

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Paragraph;
import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;
import org.areasy.common.doclet.AbstractConfiguration;
import org.areasy.common.doclet.DefaultConfiguration;
import org.areasy.common.doclet.document.tags.HtmlParserWrapper;
import org.areasy.common.doclet.utilities.DocletUtility;
import org.areasy.common.doclet.utilities.PDFUtility;

import java.io.File;


/**
 * Prints (optionally) a title page for the API documentation
 * based on the configuration properties.
 *
 * @version $Id: CustomTitle.java,v 1.2 2008/05/14 09:36:47 swd\stefan.damian Exp $
 */
public class CustomTitle implements AbstractConfiguration
{

	/**
	 * Logger reference
	 */
	private static Logger log = LoggerFactory.getLog(CustomTitle.class);

	private Document pdfDocument = null;

	/**
	 * Constructs a TitlePage object.
	 *
	 * @param pdfDocument The document into which the title page will be inserted.
	 */
	public CustomTitle(Document pdfDocument)
	{
		this.pdfDocument = pdfDocument;
	}

	/**
	 * Prints the title page.
	 *
	 * @throws Exception
	 */
	public void print() throws Exception
	{
		String apiTitlePageProp = DefaultConfiguration.getString(ARG_DOC_TITLE_PAGE, ARG_VAL_NO) .toLowerCase();

		if (apiTitlePageProp.equalsIgnoreCase(ARG_VAL_YES))
		{
			String apiFileProp = DefaultConfiguration.getConfiguration().getString(ARG_DOC_TITLE_FILE, "");

			// If the (pdf) filename contains page information, remove it,
			// because for the title page only 1 page can be imported
			if (apiFileProp.indexOf(",") != -1) apiFileProp = apiFileProp.substring(0, apiFileProp.indexOf(","));

			String labelTitle = DefaultConfiguration.getString(ARG_LB_OUTLINE_TITLE, LB_TITLE);
			String titleDest = "TITLEPAGE:";

			if (apiFileProp.length() > 0)
			{
				File apiFile = new File(DefaultConfiguration.getWorkDir(), apiFileProp);

				if (apiFile.exists() && apiFile.isFile())
				{
					Destinations.addValidDestinationFile(apiFile);
					State.setCurrentFile(apiFile);

					pdfDocument.newPage();
					pdfDocument.add(PDFUtility.createAnchor(titleDest));
                    Bookmarks.addRootBookmark(labelTitle, titleDest);

					if (apiFile.getName().toLowerCase().endsWith(".pdf"))
					{
						PDFUtility.insertPdfPages(apiFile, "1");
					}
					else
					{
						String html = DocletUtility.getHTMLBodyContentFromFile(apiFile);
						Element[] objs = HtmlParserWrapper.createPdfObjects(html);
						PDFUtility.printPdfElements(objs);
					}
				}
				else log.error("Title page file not found or invalid: " + apiFileProp);
			}
			else
			{
				String apiTitleProp = DefaultConfiguration.getConfiguration().getString(ARG_DOC_TITLE, "");
				String apiCopyrightProp = DefaultConfiguration.getConfiguration().getString(ARG_DOC_COPYRIGHT, "");
				String apiAuthorProp = DefaultConfiguration.getConfiguration().getString(ARG_DOC_AUTHOR, "");
				String apiVersionProp = DefaultConfiguration.getConfiguration().getString(ARG_DOC_VERSION, "");

				if(apiVersionProp != null && apiVersionProp.length() > 0) apiVersionProp = "Version " + apiVersionProp;

				pdfDocument.newPage();
				pdfDocument.add(PDFUtility.createAnchor(titleDest));
				Bookmarks.addRootBookmark(labelTitle, titleDest);

				Paragraph p1 = new Paragraph((float) 100.0, new Chunk(apiTitleProp, Fonts.getFont(TEXT_FONT, BOLD, 42)));
				Paragraph p2 = new Paragraph((float) 140.0, new Chunk(apiAuthorProp, Fonts.getFont(TEXT_FONT, BOLD, 18)));
				Paragraph p3 = new Paragraph((float) 20.0, new Chunk(apiCopyrightProp, Fonts.getFont(TEXT_FONT, 12)));
				Paragraph p4 = new Paragraph((float) 20.0, new Chunk(apiVersionProp, Fonts.getFont(TEXT_FONT, BOLD, 12)));

				p1.setAlignment(Element.ALIGN_CENTER);
				p2.setAlignment(Element.ALIGN_CENTER);
				p3.setAlignment(Element.ALIGN_CENTER);
				p4.setAlignment(Element.ALIGN_CENTER);

				pdfDocument.add(p1);
				pdfDocument.add(p2);
				pdfDocument.add(p3);
				pdfDocument.add(p4);
			}
		}
	}
}
