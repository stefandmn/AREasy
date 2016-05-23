package org.areasy.common.doclet.document.tags;

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

import com.lowagie.text.*;


/**
 * Implements an Unordered-List Tag (OL)
 *
 * @version $Id: TagUL.java,v 1.2 2008/05/14 09:36:47 swd\stefan.damian Exp $
 */
public class TagUL extends HtmlTag
{

	/**
	 * Bullet symbol.
	 */
	private static final String BULLET = "\u2022";

	/**
	 * Stores list entries.
	 */
	List list = null;

	/**
	 * @param parent
	 * @param type
	 */
	public TagUL(HtmlTag parent, int type)
	{
		super(parent, type);
		list = new List(false, 8);
	}

	/*
	 *  (non-Javadoc)
	 * @see org.areasy.common.doclet.document.tags.HTMLTag#openTagElements()
	 */
	public Element[] openTagElements()
	{
		Font symbolFont = new Font(Font.UNDEFINED, getFont().size());
		list.setListSymbol(new Chunk(BULLET, symbolFont));
		list.setIndentationLeft(12);

		Element[] elements = new Element[1];
		elements[0] = new Paragraph(" ", getFont());
		return elements;
	}

	/*
	 *  (non-Javadoc)
	 * @see org.areasy.common.doclet.document.tags.HTMLTag#closeTagElements()
	 */
	public Element[] closeTagElements()
	{
		if (this.parent.getType() == TAG_LI || this.parent.getType() == TAG_UL || this.parent.getType() == TAG_OL)
		{
			// If this list is nested in another ordered list, do not add additional empty space at the end of if.
			Element[] entries = new Element[1];
			entries[0] = list;
			return entries;
		}
		else
		{
			Element[] entries = new Element[2];
			entries[0] = list;
			entries[1] = new Paragraph("");
			return entries;
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see org.areasy.common.doclet.document.tags.HTMLTag#addNestedTagContent(com.lowagie.text.Element[])
	 */
	public void addNestedTagContent(Element[] content)
	{
		for (int i = 0; i < content.length; i++)
		{
			list.add(content[i]);
		}
	}
}
