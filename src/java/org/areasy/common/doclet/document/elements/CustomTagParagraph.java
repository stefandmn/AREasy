package org.areasy.common.doclet.document.elements;

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
import com.lowagie.text.Paragraph;
import org.areasy.common.doclet.AbstractConfiguration;
import org.areasy.common.doclet.document.Fonts;
import org.areasy.common.doclet.document.tags.HtmlParserWrapper;
import org.areasy.common.doclet.utilities.DocletUtility;

/**
 * Customized version(s) of Paragraph.
 *
 * @version $Id: CustomTagParagraph.java,v 1.2 2008/05/14 09:36:47 swd\stefan.damian Exp $
 */
public class CustomTagParagraph extends Paragraph implements AbstractConfiguration
{

	/**
	 * Creates a paragraph of PDF phrases for the given tag text.
	 *
	 * @param isKeyValue if the text starts with a key name, e.g., the param tag.
	 * @param text       the text of the tag (starting with the key word, if any).
	 */
	public CustomTagParagraph(boolean isKeyValue, String text)
	{
		super((float) 11.0);
		text = DocletUtility.stripLineFeeds(text).trim();

		if (isKeyValue)
		{
			int firstWhiteSpaceIndex = text.indexOf(" ");
			int firstTabIndex = text.indexOf("\t");

			if (firstTabIndex != -1)
			{
				if ((firstWhiteSpaceIndex == -1) ||
						(firstTabIndex < firstWhiteSpaceIndex))
				{
					firstWhiteSpaceIndex = firstTabIndex;
				}
			}

			if (firstWhiteSpaceIndex != -1)
			{
				// Parameter or exception with explanation
				String key = text.substring(0, firstWhiteSpaceIndex).trim();
				text = text.substring(firstWhiteSpaceIndex, text.length()).trim();

				Chunk keyChunk = new Chunk(key + " - ", Fonts.getFont(CODE_FONT, 10));
				super.add(keyChunk);
			}
			else
			{
				// Parameter or exception without explanation
				Chunk keyChunk = new Chunk(text.trim(), Fonts.getFont(CODE_FONT, 10));
				super.add(keyChunk);
				text = "";
			}
		}

		Element[] objs = HtmlParserWrapper.createPdfObjects(text);

		if (objs.length == 0)
		{
			super.add(new Chunk(text, Fonts.getFont(TEXT_FONT, 10)));
		}
		else
		{
			//descCell = new PdfPCell();
			for (int i = 0; i < objs.length; i++)
			{
				try
				{
					// PdfPTable objects cannot be added into a paragraph
					if (objs[i] instanceof TableParagraph)
					{
						TableParagraph tablePara = (TableParagraph) objs[i];
						super.add(tablePara.getTable());
					}
					else
					{
						super.add(objs[i]);
					}
				}
				catch (Exception e)
				{
					DocletUtility.error("Invalid tag text found, ignoring tag!");
				}
			}
		}
	}
}
