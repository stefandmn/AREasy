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
import com.lowagie.text.Element;
import com.lowagie.text.Paragraph;
import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;
import org.areasy.common.support.configuration.Configuration;
import org.areasy.common.doclet.AbstractConfiguration;
import org.areasy.common.doclet.DefaultConfiguration;
import org.areasy.common.doclet.document.tags.HtmlParserWrapper;
import org.areasy.common.doclet.utilities.DocletUtility;
import org.areasy.common.doclet.utilities.PDFUtility;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

/**
 * Prints extra appendices
 * @version $Id: Appendices.java,v 1.4 2008/05/14 09:36:47 swd\stefan.damian Exp $
 */
public class Appendices implements AbstractConfiguration
{
	/**
	 * Logger reference
	 */
	private static Logger log = LoggerFactory.getLog(Appendices.class);

	private static ArrayList appendices = new ArrayList();

	/**
	 * Initializes the appendix creation.
	 */
	public static void initialize()
	{

		int prefixLen = ARG_APPENDIX_PREFIX.length();
		int suffixLen = ARG_APPENDIX_FILE_SUFFIX.length();
		
		Configuration config = DefaultConfiguration.getConfiguration();
		Iterator names = config.getKeys();

		while (names.hasNext())
		{
			String key = (String) names.next();

			if (key.startsWith(ARG_APPENDIX_PREFIX) && key.endsWith(ARG_APPENDIX_FILE_SUFFIX))
			{

				File file = null;
				String fileName = config.getString(key, null);

				// If the (pdf) filename contains page information, extract it
				String pages = "";
				if (fileName != null && fileName.indexOf(",") != -1)
				{
					pages = fileName.substring(fileName.indexOf(",") + 1, fileName.length());
					fileName = fileName.substring(0, fileName.indexOf(","));
				}

				String appendixNum = key.substring(prefixLen, key.length() - suffixLen);
				file = new File(fileName);
				if (!file.exists())
				{
					file = new File(DefaultConfiguration.getWorkDir(), fileName);
				}
				if (file.exists() && file.isFile() && file.canRead())
				{
					try
					{
						int index = Integer.parseInt(appendixNum);
						AppendixInfo info = new AppendixInfo(index, file, pages);
						appendices.add(info);

						Destinations.addValidDestinationFile(file);
					}
					catch (RuntimeException e)
					{
						log.trace("Error processing appendix argument " + key);
					}

				}
				else
				{
					log.trace("Could not find appendix file " + fileName);
				}
			}
		}

		Collections.sort(appendices);
	}

	/**
	 * @throws Exception
	 */
	public static void print() throws Exception
	{
		if (appendices.isEmpty())
		{
			return;
		}

		String bmLabel = DefaultConfiguration.getString(ARG_LB_OUTLINE_APPENDICES, LB_APPENDICES);
		BookmarkEntry bookmark = Bookmarks.addStaticRootBookmark(bmLabel);

		for (Iterator iter = appendices.iterator(); iter.hasNext();)
		{
			printAppendix((AppendixInfo) iter.next(), bookmark);
		}
	}

	private static void printAppendix(AppendixInfo info, BookmarkEntry entry) throws Exception
	{

		File file = info.file;
		State.setCurrentDoc(null);
		State.setCurrentPackage(null);
		State.setCurrentFile(file);
		State.increasePackageChapter();
		State.setCurrentHeaderType(HEADER_APPENDIX);
		State.setContinued(false);
		String label = DefaultConfiguration.getString(ARG_LB_APPENDIX, LB_APPENDIX);
		String fullTitle = label + " " + info.name;
		if (info.title != null) fullTitle += ": " + info.title;

		Document.newPage();
		State.setContinued(true);
		State.setCurrentClass(fullTitle);
		String appendixAnchor = Destinations.createAnchorDestination(file, null);
		Bookmarks.addSubBookmark(entry, fullTitle, appendixAnchor);

		if (file.getName().toLowerCase().endsWith(".pdf"))
		{
			Chunk anchorChunk = PDFUtility.createAnchor(appendixAnchor);
			Document.instance().add(anchorChunk);
			PDFUtility.insertPdfPages(file, info.pages);
		}
		else
		{
			String html = DocletUtility.getHTMLBodyContentFromFile(file);
			Chunk titleChunk = new Chunk(fullTitle, Fonts.getFont(TEXT_FONT, BOLD, 22));
			titleChunk.setLocalDestination(appendixAnchor);
			Paragraph titleParagraph = new Paragraph((float) 22.0, titleChunk);
			Document.add(titleParagraph);

			Element[] objs = HtmlParserWrapper.createPdfObjects(html);
			PDFUtility.printPdfElements(objs);
		}

		State.setContinued(false);
		State.setCurrentFile(null);
	}

	/**
	 *
	 */
	private static class AppendixInfo implements Comparable
	{
		int index;
		String name;
		String title;
		File file;
		String pages;

		/**
		 * @param index
		 * @param file
		 */
		private AppendixInfo(int index, File file, String pages)
		{
			Configuration config = DefaultConfiguration.getConfiguration();

			this.index = index;
			this.file = file;
			this.pages = pages;

			this.name = config.getString(ARG_APPENDIX_PREFIX + index + ARG_APPENDIX_NAME_SUFFIX, null);
			this.title = config.getString(ARG_APPENDIX_PREFIX + index + ARG_APPENDIX_TITLE_SUFFIX, null);

			if (this.name == null) this.name = String.valueOf((char) ('A' + ((index - 1) % 26)));
		}

		public int compareTo(Object other)
		{
			return index - ((AppendixInfo) other).index;
		}
	}
}
