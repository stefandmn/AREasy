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

import com.lowagie.text.Element;
import com.lowagie.text.pdf.PdfPCell;

/**
 * Implements the THEAD tag.
 *
 * @version $Id: TagTHEAD.java,v 1.2 2008/05/14 09:36:47 swd\stefan.damian Exp $
 */
public class TagTHEAD extends HtmlTag
{

	public TagTHEAD(HtmlTag parent, int type)
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
				PdfPCell cell = (PdfPCell) elements[i];
				/* Mark cell as a header */
				cell.setMarkupAttribute(TagTD.HEADER_INDICATOR_ATTR, "true");
				super.addNestedTagContent(new Element[]{cell});
			}
		}
	}

	/**
	 * Interior text is ignored between THEAD tags
	 */
	public Element toElement(String text)
	{
		return null;
	}

}
