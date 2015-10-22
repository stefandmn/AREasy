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
import com.lowagie.text.Paragraph;


/**
 * Implements a &lt;p&gt; tag
 *
 * @version $Id: TagP.java,v 1.2 2008/05/14 09:36:47 swd\stefan.damian Exp $
 */
public class TagP extends HtmlTag
{
	/**
	 * Constructor for Paragraph tag.
	 *
	 * @param parent parent tag
	 * @param type tag type.
	 */
	public TagP(HtmlTag parent, int type)
	{
		super(parent, type);
	}

	public Element[] openTagElements()
	{
		int parentType = parent.getType();

		if (parentType == TAG_BODY || parentType == TAG_CENTER)
		{
			Element[] elements = new Element[2];
			Paragraph p1 = createParagraph(" ");

			p1.setLeading(getLeading());
			Paragraph p2 = createParagraph("");

			p2.setLeading(parent.getLeading());
			elements[0] = p1;
			elements[1] = p2;

			return elements;
		}

		return null;
	}
}
