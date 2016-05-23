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
import org.areasy.common.data.StringUtility;

/**
 * Implements the font HTML tag.
 *
 * @version $Id: TagFONT.java,v 1.2 2008/05/14 09:36:47 swd\stefan.damian Exp $
 */
public class TagFONT extends HtmlTag
{
	/**
	 * Constructort for font tag.
	 * @param parent parent tag.
	 * @param type tag type
	 */
	public TagFONT(HtmlTag parent, int type)
	{
		super(parent, type);
	}

	/**
	 * Returns any number of PDF Elements preceeding
	 * a given HTML tag.
	 */
	public Element[] openTagElements()
	{
		String colorStr = getAttribute("color");
		if(StringUtility.isNotEmpty(colorStr)) setFontColor(HtmlTagUtility.getColor(colorStr));

		String sizeStr = getAttribute("size");
		if(StringUtility.isNotEmpty(sizeStr)) setFontSize(sizeStr);

		String faceStr = getAttribute("face");
		if(StringUtility.isNotEmpty(faceStr)) setFontFace(faceStr);		

		return super.openTagElements();
	}
}
