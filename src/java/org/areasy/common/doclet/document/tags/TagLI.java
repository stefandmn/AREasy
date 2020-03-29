package org.areasy.common.doclet.document.tags;

/*
 * Copyright (c) 2007-2020 AREasy Runtime
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
import com.lowagie.text.ListItem;
import org.areasy.common.doclet.utilities.DocletUtility;


/**
 * Implements the list item tag.
 *
 * @version $Id: TagLI.java,v 1.2 2008/05/14 09:36:47 swd\stefan.damian Exp $
 */
public class TagLI extends HtmlTag
{
	ListItem listEntry = new ListItem();

	public TagLI(HtmlTag parent, int type)
	{
		super(parent, type);
	}

	public Element toElement(String text)
	{
		listEntry.add(new Chunk(DocletUtility.stripLineFeeds(text), getFont()));

		return null;
	}

	public void addNestedTagContent(Element[] content)
	{
		for (int i = 0; i < content.length; i++)
		{
			listEntry.add(content[i]);
		}
	}

	public Element[] closeTagElements()
	{
		listEntry.setLeading(getFont().size() + (float) 1.0);

		Element[] entries = new Element[1];
		entries[0] = listEntry;

		return entries;
	}
}
