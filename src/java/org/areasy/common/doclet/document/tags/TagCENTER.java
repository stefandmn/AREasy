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


/**
 * Implements the CENTER tag.
 *
 * @version $Id: TagCENTER.java,v 1.2 2008/05/14 09:36:47 swd\stefan.damian Exp $
 */
public class TagCENTER extends HtmlTag
{

	public TagCENTER(HtmlTag parent, int type)
	{
		super(parent, type);
		setCentered(true);
	}

	public Element[] openTagElements()
	{
		Element[] elements = new Element[1];
		elements[0] = createParagraph("");
		return elements;
	}

	public Element[] closeTagElements()
	{
		Element[] elements = new Element[1];
		elements[0] = createParagraph("\n");
		return elements;
	}
}
