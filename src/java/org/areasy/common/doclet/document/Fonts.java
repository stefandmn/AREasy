package org.areasy.common.doclet.document;

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

import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.pdf.BaseFont;
import org.areasy.common.doclet.AbstractConfiguration;
import org.areasy.common.doclet.DefaultConfiguration;
import org.areasy.common.doclet.utilities.DocletUtility;

import java.awt.*;
import java.io.File;
import java.util.Properties;

/**
 * Handles fonts loading and creation.
 *
 * @version $Id: Fonts.java,v 1.3 2008/05/14 09:36:47 swd\stefan.damian Exp $
 */

public class Fonts implements AbstractConfiguration
{
	/**
	 * Stores mapping of font face types to font files
	 */
	private static Properties fontTable = new Properties();

	/**
	 * Stores encoding of every font
	 */
	private static Properties encTable = new Properties();

	public static void mapFont(String filename, String encoding, int type)
	{
		if (type != CODE_FONT && type != TEXT_FONT) throw new IllegalArgumentException("Invalid font type: " + type);

		fontTable.put(String.valueOf(type), filename);

		if (encoding != null) encTable.put(String.valueOf(type), encoding);
	}

	/**
	 * Returns a font of a certain face family and
	 * a certain size.
	 *
	 * @param faceType The face (TIMES_ROMAN, COURIER).
	 * @param size     The size in points.
	 * @return The font object.
	 * @see org.areasy.common.doclet.AbstractConfiguration
	 */
	public static Font getFont(int faceType, int size)
	{
		return getFont(faceType, PLAIN, size);
	}

	public static Font getFont(int faceType, int style, int size)
	{
		Font font = null;
		String lookup = String.valueOf(faceType);
		String fontFile = fontTable.getProperty(lookup);

		int fontStyle = Font.NORMAL;
		Color color = COLOR_BLACK;

		if ((style & LINK) != 0)
		{
			fontStyle += Font.UNDERLINE;
			color = COLOR_LINK;
		}
		else if ((style & UNDERLINE) != 0) fontStyle += Font.UNDERLINE;

		if ((style & STRIKETHROUGH) != 0) fontStyle += Font.STRIKETHRU;

		if (fontFile != null)
		{

			File file = new File(DefaultConfiguration.getWorkDir(), fontFile);
			if (file.exists() && file.isFile())
			{

				try
				{
					String encoding = encTable.getProperty(lookup, BaseFont.CP1252);
					BaseFont bfComic = BaseFont.createFont(file.getAbsolutePath(), encoding, BaseFont.EMBEDDED);

					if ((style & AbstractConfiguration.ITALIC) > 0)
					{
						if ((style & AbstractConfiguration.BOLD) > 0) fontStyle += Font.BOLDITALIC;
							else fontStyle += Font.ITALIC;
					}
					else if ((style & AbstractConfiguration.BOLD) > 0) fontStyle += Font.BOLD;

					if (fontStyle != Font.NORMAL) font = new Font(bfComic, size, fontStyle, color);
						else font = new Font(bfComic, size);

					if (font == null) throw new IllegalArgumentException("Font null: " + fontFile);
				}
				catch (Exception e)
				{
					e.printStackTrace();
					throw new IllegalArgumentException("Font unusable");
				}
			}
			else DocletUtility.error("Font file not found: " + fontFile);
		}
		else
		{
			// Use predefined font
			String face = "";

			if (faceType == TEXT_FONT)
			{
				face = FontFactory.HELVETICA;

				if ((style & AbstractConfiguration.ITALIC) > 0)
				{
					if ((style & AbstractConfiguration.BOLD) > 0) face = FontFactory.HELVETICA_BOLDOBLIQUE;
						else face = FontFactory.HELVETICA_OBLIQUE;
				}
				else if ((style & AbstractConfiguration.BOLD) > 0) face = FontFactory.HELVETICA_BOLD;
			}
			else
			{
				face = FontFactory.COURIER;
				if ((style & ITALIC) > 0)
				{
					if ((style & BOLD) > 0) face = FontFactory.COURIER_BOLDOBLIQUE;
						else face = FontFactory.COURIER_OBLIQUE;
				}
				else if ((style & BOLD) > 0) face = FontFactory.COURIER_BOLD;
			}

			if (fontStyle != Font.NORMAL) font = FontFactory.getFont(face, size, fontStyle, color);
				else font = FontFactory.getFont(face, size);
		}

		return font;
	}
}
