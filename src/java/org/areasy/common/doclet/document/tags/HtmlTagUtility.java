package org.areasy.common.doclet.document.tags;

/*
 * Copyright (c) 2007-2018 AREasy Runtime
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
import org.areasy.common.doclet.AbstractConfiguration;

import java.awt.*;
import java.util.Hashtable;
import java.util.Map;

/**
 * This class provides static utility methods for
 * HTML and HTML tag handling.
 *
 * @version $Id: HtmlTagUtility.java,v 1.2 2008/05/14 09:36:47 swd\stefan.damian Exp $
 */
public class HtmlTagUtility implements AbstractConfiguration
{
	private static int UNKNOWN = -1;
	protected static String[] tags = HtmlTagFactory.tags;

	private static Map colors = new Hashtable();

	static
	{
		colors.put("black", Color.black);
		colors.put("blue", Color.blue);
		colors.put("cyan", Color.cyan);
		colors.put("darkgray", Color.darkGray);
		colors.put("gray", Color.gray);
		colors.put("green", Color.green);
		colors.put("lightgray", Color.lightGray);
		colors.put("magenta", Color.magenta);
		colors.put("orange", Color.orange);
		colors.put("pink", Color.pink);
		colors.put("red", Color.red);
		colors.put("white", Color.white);
		colors.put("yellow", Color.yellow);
	}

	/**
	 * Determines the type of a HTML tag by
	 * parsing it and gettings its type from
	 * an internal table.
	 *
	 * @param text The HTML tag (opening tag or closing tag)
	 * @return The type of the given tag.
	 */
	public static int getTagType(String text)
	{
		int tagType = UNKNOWN;

		if (text.startsWith("</")) text = text.substring(2, text.length());

		if (text.startsWith("<")) text = text.substring(1, text.length());

		if (text.endsWith(">")) text = text.substring(0, text.length() - 1);

		if (text.endsWith("/>")) text = text.substring(0, text.length() - 2);

		if (text.indexOf(" ") != -1) text = text.substring(0, text.indexOf(" "));

		text = text.trim();

		for (int i = 0; (i < tags.length) && (tagType == UNKNOWN); i++)
		{
			if (text.equalsIgnoreCase(tags[i])) tagType = i;
		}

		return tagType;
	}

	/**
	 * Utility method to parse a float value, and return a default if
	 * the String value is null or malformed.
	 */
	public static float parseFloat(String str, float defaultValue)
	{
		float value = defaultValue;

		try
		{
			if (str != null)
			{
				str = str.toLowerCase().replaceAll("[a-z]", "");
				str = str.replaceAll("\\p{Punct}", "");
				str = str.replaceAll("\\s", "");

				value = Float.parseFloat(str.trim());
			}
		}
		catch (NumberFormatException e) {}

		return value;
	}

	/**
	 * Utility method to parse a float value, and return a default if
	 * the String value is null or malformed.
	 */
	public static int parseInt(String str, int defaultValue)
	{
		int value = defaultValue;

		try
		{
			if (str != null)
			{
				str = str.toLowerCase().replaceAll("[a-z]", "");
				str = str.replaceAll("\\p{Punct}", "");
				str = str.replaceAll("\\s", "");

				value = Integer.parseInt(str.trim());
			}
		}
		catch (NumberFormatException e) {}

		return value;
	}

	/**
	 * Returns the Element constant associated with the specified horizontal
	 * alignment (left, right, center, justified).
	 */
	public static int getAlignment(String htmlAlignString, int defaultAlign)
	{
		if (htmlAlignString == null) return defaultAlign;

		if ("center".equalsIgnoreCase(htmlAlignString)) return Element.ALIGN_CENTER;
		if ("right".equalsIgnoreCase(htmlAlignString)) return Element.ALIGN_RIGHT;
		if ("left".equalsIgnoreCase(htmlAlignString)) return Element.ALIGN_LEFT;
		if ("justify".equalsIgnoreCase(htmlAlignString)) return Element.ALIGN_JUSTIFIED;

		return defaultAlign;
	}

	/**
	 * Returns the Element constant associated with the specified vertical
	 * alignment (top, middle, bottom, baseline).
	 */
	public static int getVerticalAlignment(String htmlAlignString, int defaultAlign)
	{
		if (htmlAlignString == null) return defaultAlign;

		if ("top".equalsIgnoreCase(htmlAlignString)) return Element.ALIGN_TOP;
		if ("middle".equalsIgnoreCase(htmlAlignString)) return Element.ALIGN_MIDDLE;
		if ("bottom".equalsIgnoreCase(htmlAlignString)) return Element.ALIGN_BOTTOM;
		if ("baseline".equalsIgnoreCase(htmlAlignString)) return Element.ALIGN_BASELINE;

		return defaultAlign;
	}

	/**
	 * Give a Color from a hexadecimal representation within a string
	 *
	 * @param strColor the string to convert
	 * @return the Color
	 */
	public static Color getColor(String strColor)
	{
		Color color;

		//anyway return black.
		if (StringUtility.isEmpty(strColor)) return null;
		else if (strColor.startsWith("#"))
		{
			strColor = strColor.substring(1);
			color = (new Color(Integer.parseInt(strColor.trim(), 16)));
		}
		else color = (Color) colors.get(strColor.toLowerCase());

		return color;
	}
}
