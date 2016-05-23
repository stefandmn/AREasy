package org.areasy.common.doclet.document.elements;

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
import com.lowagie.text.Font;
import com.lowagie.text.Phrase;
import org.areasy.common.doclet.AbstractConfiguration;
import org.areasy.common.doclet.DefaultConfiguration;
import org.areasy.common.doclet.document.Destinations;
import org.areasy.common.doclet.document.Fonts;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Chunk with internal hyperlink, if possible. For
 * instance, if the target is outside the packages of the
 * current javadoc, it is obviously impossible to create a
 * link. In such cases, the Chunk will just be plain text.
 *
 * @version $Id: LinkPhrase.java,v 1.4 2008/05/14 09:36:47 swd\stefan.damian Exp $
 */
public class LinkPhrase extends Phrase implements AbstractConfiguration
{
	/**
	 * Creates hyperlink chunk where the font is defined
	 * after the following rules:
	 * <ol>
	 * <li>If it's a link, its courier, underlined</li>
	 * <li>If the parameter "isTimesRoman" is true, it is regular TimesRoman</li>
	 * <li>Otherwise, it's regular courier</li>
	 * </ol>
	 * <p/>
	 * The size defaults to 10 if a value less or equal than zero is given.
	 */
	public LinkPhrase(String destination, String label, int size, boolean isTimesRoman)
	{
		super("");

		Font font = null;

		if (!DefaultConfiguration.isLinksCreationActive()) destination = "";

		if (size < 9) throw new RuntimeException("INVALID SIZE");

		if (size == 0) size = 9;

		destination = normalizeDestination(destination);

		if (Destinations.isValid(destination))
		{
			if (isTimesRoman) font = Fonts.getFont(TEXT_FONT, LINK, size);
				else font = Fonts.getFont(CODE_FONT, LINK, size);
		}
		else if (isTimesRoman) font = Fonts.getFont(TEXT_FONT, size);
			else font = Fonts.getFont(CODE_FONT, size);

		init(destination, label, font);
	}

	/**
	 * Creates a hyperlink chunk.
	 *
	 * @param destination The original destination as defined
	 *                    in the javadoc.
	 * @param label       The text label for the link
	 * @param font        The base font for the link (for example, could be
	 *                    a bold italic font in case of a "deprecated" tag).
	 */
	public LinkPhrase(String destination, String label, Font font)
	{
		super("");

		Font newFont = null;
		float size = font.size();

		if (size == 0) size = 9;

		if (!DefaultConfiguration.isLinksCreationActive()) destination = "";

		destination = normalizeDestination(destination);

		if (Destinations.isValid(destination))
		{
			if (font.family() == Font.TIMES_ROMAN) newFont = Fonts.getFont(TEXT_FONT, LINK, (int) size);
				else newFont = Fonts.getFont(CODE_FONT, LINK, (int) size);
		}
		else if (font.family() == Font.TIMES_ROMAN) newFont = Fonts.getFont(TEXT_FONT, (int) size);
			else newFont = Fonts.getFont(CODE_FONT, (int) size);

		init(destination, label, newFont);
	}

	private static String normalizeDestination(String destination)
	{
		if (destination == null) return "";

		// Tidy can encode spaces etc. in the URLs... (i.e. %20)
		try
		{
			return URLDecoder.decode(destination, "UTF-8").trim();
		}
		catch (UnsupportedEncodingException e)
		{
			return destination;
		}
	}

	/**
	 * Initializes the link chunk with given values.
	 *
	 * @param destination The original destination as defined
	 *                    in the javadoc.
	 * @param label       The text label for the link
	 * @param font        The base font for the link (for example, could be
	 *                    a bold italic font in case of a "deprecated" tag).
	 */
	private void init(String destination, String label, Font font)
	{
		if (label == null) label = destination;

		String createLinksProp = DefaultConfiguration.getString(ARG_CREATE_LINKS, ARG_VAL_NO);
		if (createLinksProp.equalsIgnoreCase(ARG_VAL_NO)) destination = null;

		super.font = font;

		Chunk chunk = new Chunk("");
		chunk.append(label);

		if (destination != null && Destinations.isValid(destination)) chunk.setLocalGoto(destination);

		add(chunk);
	}
}
