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

import com.lowagie.text.Chunk;
import com.lowagie.text.Element;
import com.lowagie.text.Graphic;
import com.lowagie.text.Paragraph;

import java.awt.*;

/**
 * @version $Id: TagHR.java,v 1.2 2008/05/14 09:36:47 swd\stefan.damian Exp $
 */
public class TagHR extends HtmlTag
{
	public TagHR(HtmlTag parent, int type)
	{
		super(parent, type);
	}

	/* Need a subclass to call addSpecial() */
	private static class HRParagraph extends Paragraph
	{
		private HRParagraph(Graphic hr)
		{
			super();
			add(Chunk.NEWLINE);
			addSpecial(hr);
		}
	}

	public Element[] openTagElements()
	{
		float height = Math.min(HtmlTagUtility.parseFloat(getAttribute("size"), 2.0f), 100f);
		float width;

		Color color = null;

		String widthStr = getAttribute("width");

		if (widthStr == null || !widthStr.endsWith("%")) width = 100f;
			else width = HtmlTagUtility.parseFloat(widthStr.substring(0, widthStr.length() - 1), 100f);

		String colorStr = getAttribute("color");
		if (colorStr == null) colorStr = getAttribute("bgcolor");

		color = HtmlTagUtility.getColor(colorStr);
		if (color == null) color = HtmlTagUtility.getColor("gray");

		Graphic graphic = new Graphic();
		graphic.setHorizontalLine(height, width, color);

		/*
		 * The returned elements here are added to Paragraph instances.
		 * Since PdfPTable cannot be used this way, we need to put it
		 * inside a special Paragraph instance in order for it to work.
		 */
		return new Element[]{new HRParagraph(graphic)};
	}

}
