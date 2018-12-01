package org.areasy.common.doclet.document.tags;

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
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfAction;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfString;
import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;
import org.areasy.common.doclet.DefaultConfiguration;
import org.areasy.common.doclet.document.Bookmarks;
import org.areasy.common.doclet.document.Destinations;
import org.areasy.common.doclet.document.State;
import org.areasy.common.doclet.document.elements.LinkPhrase;
import org.areasy.common.doclet.utilities.DocletUtility;
import org.areasy.common.doclet.utilities.PDFUtility;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Anchor tag
 *
 * @version $Id: TagA.java,v 1.2 2008/05/14 09:36:47 swd\stefan.damian Exp $
 */
public class TagA extends HtmlTag
{
	/**
	 * Logger reference
	 */
	private static Logger log = LoggerFactory.getLog(TagA.class);

	/**
	 * Create a link tag instance.
	 *
	 * @param parent The parent HTML tag.
	 * @param type   The type of this tag.
	 */
	public TagA(HtmlTag parent, int type)
	{
		super(parent, type);
		setLink(true);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.areasy.common.doclet.document.tags.HTMLTag#openTagElements()
	 */
	public Element[] openTagElements()
	{
		String addr = getAttribute("href");
		String name = getAttribute("name");

		if (addr != null && addr.equalsIgnoreCase("newpage"))
		{
			Chunk anchor = new Chunk("");
			anchor.setNewPage();

			return new Element[]{anchor};
		}
		else if (name != null && name.equalsIgnoreCase("bookmark"))
		{
			String label = getAttribute("label");

			String destinationName = null;
			if(HtmlParserWrapper.getRootBookmarkEntry() != null)
			{
				String target = getAttribute("target");
				destinationName = HtmlParserWrapper.getRootBookmarkEntry().addBookmarkEntry(target, label);
			}
			else
			{
				destinationName = Destinations.createAnchorDestination(State.getCurrentFile(), label);
				Bookmarks.addRootBookmark(label, destinationName);
			}

			Chunk anchor = PDFUtility.createAnchor(destinationName);

			return new Element[]{anchor};
		}
		else if (name != null && name.length() > 0)
		{
			String dest = Destinations.createAnchorDestination(State.getCurrentFile(), name);

			setLink(false);
			Phrase anchor = new Phrase(PDFUtility.createAnchor(dest, getFont()));

			return new Element[]{anchor};
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.areasy.common.doclet.document.tags.HTMLTag#toElement(java.lang.String)
	 */
	public Element toElement(String text)
	{
		String addr = getAttribute("href");

		if (addr == null || !DefaultConfiguration.isLinksCreationActive()) addr = "";
		if (!isPre()) text = DocletUtility.stripLineFeeds(text);

		Element aChunk;
		if (addr.startsWith("locallink"))
		{
			boolean plainText = addr.startsWith("locallinkplain");
			String dest = addr.substring(addr.indexOf(':') + 1).trim();

			setCode(!plainText);

			return new LinkPhrase(dest, text, Math.max(9, (int) getFont() .size()), plainText);
		}
		else if (addr.equalsIgnoreCase("newpage")) return super.toElement(text);
		else if (addr.startsWith("http://") || addr.startsWith("https://"))
		{
			try
			{
				URL url = new URL(addr);
				return new Chunk(text, getFont()).setAnchor(url);
			}
			catch (MalformedURLException e)
			{
				log.error("Malformed URL: " + addr);
			}
		}
		else
		{
			String fileName = addr.trim();
			String anchorName = "";

			int hashIndex = addr.indexOf('#');

			if (hashIndex >= 0)
			{
				fileName = addr.substring(0, hashIndex).trim();
				anchorName = addr.substring(hashIndex + 1).trim();
			}

			boolean isLocalAnchor = (fileName.length() == 0 && anchorName.length() > 0);
			File file = null;

			try
			{
				if (fileName.length() > 0) file = new File(DocletUtility.getFilePath(fileName));
					else file = State.getCurrentFile();
			}
			catch (FileNotFoundException e)
			{
				log.debug("Could not find linked file " + fileName);
			}

			if (isLocalAnchor || Destinations.isValidDestinationFile(file))
			{
				String fullAnchor = Destinations.createAnchorDestination(file, anchorName);

				PdfAction action = new PdfAction("", "");
				action.remove(PdfName.F);
				action.put(PdfName.S, PdfName.GOTO);
				action.put(PdfName.D, new PdfString(fullAnchor));

				aChunk = new Phrase();

				Chunk chunk = createChunk(text);
				((Phrase) aChunk).add(chunk.setAction(action));

				return aChunk;
			}
		}

		if (getAttribute("name") != null) setLink(false); // no underline for anchors

		Font font = getFont();
		font.setColor(0, 0, 0);

		aChunk = new Chunk(text, font);

		setLink(false);

		return aChunk;
	}

	protected Chunk createChunk(String text)
	{
		Chunk chunk = new Chunk(text);

		Font font = chunk.font();
		font.setStyle(Font.NORMAL);
		font.setColor(Color.blue);

		chunk.setFont(font);

		return chunk;
	}
}
