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

import com.lowagie.text.Element;
import com.lowagie.text.Paragraph;

/**
 * Implements a &lt;div&gt; tag
 * In this version will work in the same way like paragraph tag.
 *
 *
 */
public class TagDIV extends HtmlTag
{
	/**
	 * Create a link tag instance.
	 *
	 * @param parent The parent HTML tag.
	 * @param type   The type of this tag.
	 */
	public TagDIV(HtmlTag parent, int type)
	{
		super(parent, type);
	}

	public Element[] openTagElements()
	{
		int parentType = parent.getType();

		if (parentType == TAG_BODY || parentType == TAG_CENTER)
		{
			Element[] elements = new Element[2];

			Paragraph p = createParagraph(" ");
			p.setLeading(getLeading());

			elements[0] = p;

			p = createParagraph("");
			p.setLeading(parent.getLeading());

			elements[1] = p;

			return elements;
		}

		return null;
	}
}
