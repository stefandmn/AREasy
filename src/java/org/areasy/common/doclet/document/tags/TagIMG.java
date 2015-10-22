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
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Rectangle;
import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;
import org.areasy.common.data.NumberUtility;
import org.areasy.common.doclet.utilities.DocletUtility;

import java.awt.*;
import java.io.FileNotFoundException;
import java.net.URL;


/**
 * @version $Id: TagIMG.java,v 1.3 2008/05/14 09:36:47 swd\stefan.damian Exp $
 */
public class TagIMG extends HtmlTag
{
	private Image img = null;

	/**
	 * Logger reference
	 */
	private static Logger log = LoggerFactory.getLog(TagIMG.class);

	public TagIMG(HtmlTag parent, int type)
	{
		super(parent, type);
	}

	public Element[] toPdfObjects()
	{
		String src = getAttribute("src");
		Element[] results = new Element[0];
		Element result = null;

		if (src == null)
		{
			log.error("Image tag has no 'src' attribute.");
			return null;
		}

		try
		{
			// now we use awt Image to create iText image instead of
			// letting it read images by itself
			java.awt.Image awt = null;

			if (src.indexOf("://") > 0) awt = Toolkit.getDefaultToolkit().createImage(new URL(src));
			else
			{
				String filePath = DocletUtility.getFilePath(src);
				awt = Toolkit.getDefaultToolkit().createImage(filePath);
			}

			//img = Image.getInstance(PDFUtil.getFilePath(src));
			img = Image.getInstance(awt, null);

			// for the time being let's stick with A4
			Rectangle size = PageSize.A4;
			String width = getAttribute("width");

			if (width != null)
			{
				// trying to cope with the resolution differences
				float w = NumberUtility.toFloat(width, 0) / 96 * 72;
				img.scaleAbsoluteWidth(w);
			}

			float maxW = size.width() - 100;

			if (img.plainWidth() > maxW) img.scaleAbsoluteWidth(maxW);

			String height = getAttribute("height");

			if (height != null)
			{
				// trying to cope with the resolution differences
				float h = NumberUtility.toFloat(height, 0) / 96 * 72;
				img.scaleAbsoluteHeight(h);
			}

			float maxH = size.height() - 100;

			if (img.plainHeight() > maxH) img.scaleAbsoluteHeight(maxH);

			result = img;

			img.setAlignment(HtmlTagUtility.getAlignment(getAttribute("align"), Element.ALIGN_CENTER));

			String border = getAttribute("border");

			if (border != null)
			{
				// trying to cope with the resolution differences
				float b = NumberUtility.toFloat(border, 0);

				img.setBorder((int)b);
				img.setBorderWidthTop(b);
				img.setBorderWidthLeft(b);
				img.setBorderWidthRight(b);
				img.setBorderWidthBottom(b);
				img.setBorderColor(Color.black);
			}

		}
		catch (FileNotFoundException e)
		{
			DocletUtility.error("** Image not found: " + src, e);
		}
		catch (Exception e)
		{
			DocletUtility.error("** Failed to read image: " + src, e);
		}

		if (result != null)
		{
			results = new Element[1];
			results[0] = result;
		}


		return results;
	}

	public Element[] openTagElements()
	{
		return null;
	}

	public Element[] closeTagElements()
	{
		return null;
	}
}
