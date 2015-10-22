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

import com.lowagie.text.Element;
import com.lowagie.text.pdf.PdfPCell;

/**
 * Implements the table row tag.
 *
 * @version $Id: TagTR.java,v 1.2 2008/05/14 09:36:47 swd\stefan.damian Exp $
 */
public class TagTR extends HtmlTag
{
	public static final String ROW_START_ATTR = "doclet.table.row.start";

	public TagTR(HtmlTag parent, int type)
	{
		super(parent, type);
	}

	/**
	 * Override to only allow PdfPCell instances
	 */
	public void addNestedTagContent(Element[] elements)
	{
		for (int i = 0; elements != null && i < elements.length; i++)
		{
			if (elements[i] instanceof PdfPCell)
			{
				super.addNestedTagContent(new Element[]{elements[i]});
			}
		}
	}

	/**
	 * Interior text is ignored between TR tags
	 */
	public Element toElement(String text)
	{
		return null;
	}

	/*
	 *  (non-Javadoc)
	 * @see org.areasy.common.doclet.document.tags.HTMLTag#toPdfObjects()
	 */
	public Element[] toPdfObjects()
	{
		Element[] objs = super.toPdfObjects();
		if (objs != null && objs.length > 0 && objs[0] instanceof PdfPCell)
		{
			((PdfPCell) objs[0]).setMarkupAttribute(ROW_START_ATTR, "1");
		}
		return objs;
	}
}
