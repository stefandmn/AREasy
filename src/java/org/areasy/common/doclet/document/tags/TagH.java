package org.areasy.common.doclet.document.tags;

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
import org.areasy.common.doclet.DefaultConfiguration;
import org.areasy.common.doclet.document.Fonts;
import org.areasy.common.doclet.utilities.PDFUtility;

/**
 * Header tag (H1 - H6)
 *
 * @version $Id: TagH.java,v 1.2 2008/05/14 09:36:47 swd\stefan.damian Exp $
 */
public class TagH extends HtmlTag
{
	public TagH(HtmlTag parent, int type)
	{
		super(parent, type);
	}

	/*
	 *  (non-Javadoc)
	 * @see org.areasy.common.doclet.document.tags.HTMLTag#openTagElements()
	 */
	public Element[] openTagElements()
	{
		Element[] elements;

		Chunk anchor = addHeaderBookmarkEntry();
		if(anchor == null) elements = new Element[2];
			else elements = new Element[3];

		Paragraph p1 = createParagraph(new Chunk(" ", Fonts.getFont(TEXT_FONT, 16)));
		p1.setLeading(parent.getLeading());

		Paragraph p2 = createParagraph("");

		if(anchor != null)
		{
			elements[0] = anchor;
			elements[1] = p1;
			elements[2] = p2;
		}
		else
		{
			elements[0] = p1;
			elements[1] = p2;
		}

		return elements;
	}

	/*
	 *  (non-Javadoc)
	 * @see org.areasy.common.doclet.document.tags.HTMLTag#closeTagElements()
	 */
	public Element[] closeTagElements()
	{
		Element[] elements = new Element[2];

		Paragraph p1 = createParagraph(new Chunk(" ", Fonts.getFont(TEXT_FONT, 16)));
		p1.setLeading((float) 10.0);

		Paragraph p2 = createParagraph("");
		p2.setLeading(parent.getLeading());

		elements[0] = p1;
		elements[1] = p2;

		return elements;
	}

	protected Chunk addHeaderBookmarkEntry()
	{
		if(DefaultConfiguration.getBooleanConfigValue(ARG_API_DESCRIPTION_BOOKMARKS, ARG_VAL_YES) && HtmlParserWrapper.getRootBookmarkEntry() != null)
		{
			int type = 0;

			if(getType() == HtmlTag.TAG_H1) type = 1;
				else if(getType() == HtmlTag.TAG_H2) type = 2;
					else if(getType() == HtmlTag.TAG_H3) type = 3;
						else if(getType() == HtmlTag.TAG_H4) type = 4;
							else if(getType() == HtmlTag.TAG_H5) type = 5;
								else if(getType() == HtmlTag.TAG_H6) type = 6;

			String label = (String)getContentTags().get(0);
			String destinationName = HtmlParserWrapper.getRootBookmarkEntry().addBookmarkEntryByHeaderType(type, label);

			if(destinationName != null)
			{
				Chunk anchor = PDFUtility.createAnchor(destinationName);
                return anchor;
			}
			else return null;
		}
		else return null;
	}
}
