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
import com.lowagie.text.Element;
import org.areasy.common.data.StringUtility;
import org.areasy.common.doclet.AbstractConfiguration;
import org.areasy.common.doclet.DefaultConfiguration;
import org.areasy.common.doclet.document.tags.HtmlParserWrapper;
import org.areasy.common.doclet.utilities.DocletUtility;
import org.areasy.common.doclet.utilities.PDFUtility;
import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;

import java.io.File;
import java.util.Arrays;

/**
 * Prints (optionally) a description page for the API documentation
 * based on the configuration properties.
 * @version $Id: CustomDescription.java,v 1.3 2008/05/14 09:36:47 swd\stefan.damian Exp $
 */
public class CustomDescription implements AbstractConfiguration
{
	/**
	 * Logger reference
	 */
	private static Logger log = LoggerFactory.getLog(CustomDescription.class);

	private Document pdfDocument = null;

	private BookmarkEntry root = null;

	/**
	 * Constructs a TitlePage object.
	 *
	 * @param pdfDocument The document into which the title page will be inserted.
	 */
	public CustomDescription(Document pdfDocument)
	{
		this.pdfDocument = pdfDocument;
	}

	/**
	 * Prints the description page.
	 *
	 * @throws Exception
	 */
	public void print() throws Exception
	{
		String apiDescriptionPageProp = DefaultConfiguration.getString(ARG_DOC_DESCRIPTION_PAGE, ARG_VAL_NO) .toLowerCase();

		if (apiDescriptionPageProp.equalsIgnoreCase(ARG_VAL_YES))
		{
			String apiFileDescription = DefaultConfiguration.getConfiguration().getString(ARG_API_DESCRIPTION_FILE, "");

			// If the (pdf) filename contains page information, remove it,
			// because for the title page only 1 page can be imported
			if (apiFileDescription.indexOf(",") != -1) apiFileDescription = apiFileDescription.substring(0, apiFileDescription.indexOf(","));

			//append description file
			String labelDescription = DefaultConfiguration.getString(ARG_LB_OUTLINE_DESCRIPTION, LB_DESCRIPTION);
			String descriptionDest = "DESCRIPTIONPAGE:";

			if (apiFileDescription.length() > 0)
			{
				File apiFile = new File(DefaultConfiguration.getWorkDir(), apiFileDescription);

				if (apiFile.exists() && apiFile.isFile())
				{
					Destinations.addValidDestinationFile(apiFile);
					State.setCurrentFile(apiFile);

					pdfDocument.newPage();
					pdfDocument.add(PDFUtility.createAnchor(descriptionDest));

					//define an set the root bookmark
					root = Bookmarks.addRootBookmark(labelDescription, descriptionDest);
					HtmlParserWrapper.setRootBookmarkEntry(root);

					if (apiFile.getName().toLowerCase().endsWith(".pdf"))
					{
						PDFUtility.insertPdfDocument(apiFile);
					}
					else
					{
						String html = DocletUtility.getHTMLBodyContentFromFile(apiFile);
						Element[] objs = HtmlParserWrapper.createPdfObjects(html);

						PDFUtility.printPdfElements(objs);
					}

					//destroy root bookmark
					destroyRootBookmark();
				}
				else log.error("Description page file not found or invalid: " + apiFile.getPath());
			}
			else if (StringUtility.isNotEmpty(DefaultConfiguration.getDesriptionContent()))
			{
				pdfDocument.newPage();
				pdfDocument.add(PDFUtility.createAnchor(descriptionDest));

				//define and set the root bookmark
				root = Bookmarks.addRootBookmark(labelDescription, descriptionDest);
				HtmlParserWrapper.setRootBookmarkEntry(root);

				String html = DocletUtility.getHTMLBodyContent(DefaultConfiguration.getDesriptionContent());
				Element[] objs = HtmlParserWrapper.createPdfObjects(html);

				PDFUtility.printPdfElements(objs);
				DefaultConfiguration.setDesriptionContent(null);

				//destroy root bookmark
				destroyRootBookmark();
			}
		}
	}

	protected void destroyRootBookmark()
	{
		BookmarkEntry root = HtmlParserWrapper.getRootBookmarkEntry();
		if(root != null && root.getChildren().length == 1)
		{
			BookmarkEntry entry = root.getChildren()[0];

			root.setLabel(entry.getLabel());
			root.setDestinationName(entry.getDestinationName());
			root.setChildren(Arrays.asList(entry.getChildren()));
		}

		HtmlParserWrapper.setRootBookmarkEntry(null);
	}
}
