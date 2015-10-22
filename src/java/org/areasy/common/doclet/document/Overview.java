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

import com.lowagie.text.Element;
import com.lowagie.text.Paragraph;
import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;
import org.areasy.common.doclet.AbstractConfiguration;
import org.areasy.common.doclet.DefaultConfiguration;
import org.areasy.common.doclet.document.tags.HtmlParserWrapper;
import org.areasy.common.doclet.utilities.DocletUtility;
import org.areasy.common.doclet.utilities.PDFUtility;
import com.sun.javadoc.RootDoc;

import java.io.File;

/**
 * Prints the overview.
 *
 * @version $Id: Overview.java,v 1.3 2008/05/14 09:36:47 swd\stefan.damian Exp $
 */
public class Overview implements AbstractConfiguration
{

	/**
	 * Logger reference
	 */
	private static Logger log = LoggerFactory.getLog(Overview.class);

	/**
	 * Processes overview.
	 *
	 * @param rootDoc The javadoc information for the API.
	 * @throws Exception
	 */
	public static void print(RootDoc rootDoc) throws Exception
	{
		String overview = DocletUtility.getComment(rootDoc);

		// Check if PDF file has been specified
		String[] overviewFileNames = DefaultConfiguration.findNumberedProperties(ARG_OVERVIEW_PDF_FILE);

		// Only do something if either standard overview or PDF file has been specified
		if ((overview == null || overview.length() == 0) && overviewFileNames == null) return;

		State.setOverview(true);
		State.setCurrentDoc(rootDoc);

		Document.newPage();

		String bmLabel = DefaultConfiguration.getString(ARG_LB_OUTLINE_OVERVIEW, LB_OVERVIEW);
		String dest = Destinations.createAnchorDestination(State.getCurrentFile(), bmLabel);

		Bookmarks.addRootBookmark(bmLabel, dest);
		Document.instance().add(PDFUtility.createAnchor(dest));

		if (State.getCurrentFile() != null)
		{
			String packageAnchor = Destinations.createAnchorDestination(State.getCurrentFile(), null);
			Document.instance().add(PDFUtility.createAnchor(packageAnchor));
		}

		// If the (pdf) filename contains page information, extract it
		boolean pdfPagesInserted = false;

		if (overviewFileNames != null)
		{
			for (int i = 0; i < overviewFileNames.length; i++)
			{
				String overviewFileName = overviewFileNames[i];
				String pages = "";

				if (overviewFileName.indexOf(",") != -1)
				{
					pages = overviewFileName.substring(overviewFileName.indexOf(",") + 1, overviewFileName.length());
					overviewFileName = overviewFileName.substring(0, overviewFileName.indexOf(","));
				}

				if (overviewFileName.endsWith(".pdf"))
				{
					if (pages.length() == 0) pages = "1";

					File overviewFile = new File(DefaultConfiguration.getWorkDir(), overviewFileName);
					if (overviewFile.exists() && overviewFile.isFile())
					{
						State.setContinued(false);
						Destinations.addValidDestinationFile(overviewFile);
						State.setCurrentFile(overviewFile);

						PDFUtility.insertPdfPages(overviewFile, pages);
						pdfPagesInserted = true;
					}
				}

				if (overview != null && overview.length() > 0)
				{
					if (pdfPagesInserted) Document.newPage();
				}
			}
		}

		if (overview != null && overview.length() > 0)
		{
			State.setContinued(true);

			Paragraph label = new Paragraph((float) 22.0, "", Fonts.getFont(TEXT_FONT, BOLD, 18));
			label.add(bmLabel);
			Document.instance().add(label);

			// Some empty space
			Document.instance().add(new Paragraph((float) 20.0, " "));

			Element[] objs = HtmlParserWrapper.createPdfObjects(overview);

			if ((objs == null) || (objs.length == 0))
			{
				String rootDesc = DocletUtility.stripLineFeeds(overview);
				Document.instance().add(new Paragraph((float) 11.0, rootDesc, Fonts.getFont(TEXT_FONT, 10)));
			}
			else PDFUtility.printPdfElements(objs);
		}

		State.setOverview(false);
		State.setContinued(false);
	}

}
