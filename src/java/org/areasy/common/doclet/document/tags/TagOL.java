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
import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;

/**
 * Implements an Ordered-List Tag (OL)
 *
 * @version $Id: TagOL.java,v 1.2 2008/05/14 09:36:47 swd\stefan.damian Exp $
 */
public class TagOL extends HtmlTag
{

	/**
	 * Logger reference
	 */
	private static Logger log = LoggerFactory.getLog(TagOL.class);

	/**
	 * Stores list entries.
	 */
	List list = null;

	/**
	 * Default constructor.
	 *
	 * @param parent parent tag
	 * @param type tag type
	 */
	public TagOL(HtmlTag parent, int type)
	{
		super(parent, type);
	}

	protected char getTypeChar()
	{
		String listType = getAttribute("type");
		if (listType != null && listType.length() > 0) return listType.charAt(0);

		return '1';
	}

	protected int getFirstListIndex()
	{
		String firstAttr = getAttribute("start");

		try
		{
			if (firstAttr != null) return Integer.parseInt(firstAttr);
		}
		catch (NumberFormatException e)
		{
			log.debug("Invalid OL start value '" + firstAttr + "'", e);
		}

		return 1;
	}

	public Element[] openTagElements()
	{
		char typeChar = getTypeChar();
		int first = getFirstListIndex();

		switch (typeChar)
		{
			case 'a':
			case 'A':
				list = new List(false, true, 20);
				if (first > 0 && first <= 26) list.setFirst((char) (typeChar + (first - 1)));
				break;

			case 'i':
			case 'I':
				list = new RomanList(typeChar == 'i', 20);
				if (first > 0) list.setFirst(first);
				break;

			case '1':
			default:
				list = new List(true, 20);
				if (first > 0) list.setFirst(first);
		}

		list.setListSymbol(new Chunk("", getFont()));

		Element[] elements = new Element[1];
		elements[0] = new Paragraph((float) 8.0, " ", getFont());

		return elements;
	}

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

	public void addNestedTagContent(Element[] content)
	{
		for (int i = 0; i < content.length; i++)
		{
			list.add(content[i]);
		}
	}
}
