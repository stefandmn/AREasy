package org.areasy.common.parser.html;

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

import org.areasy.common.parser.html.utilities.ParserException;
import org.areasy.common.parser.html.engine.lexer.Page;
import org.areasy.common.parser.html.engine.lexer.Lexer;
import org.areasy.common.parser.html.engine.Parser;
import org.areasy.common.parser.html.engine.NodeFilter;
import org.areasy.common.parser.html.engine.filters.TagNameFilter;
import org.areasy.common.parser.html.beans.StringBean;


/**
 * Wrapper library that will help you to extract string from an HTML file or
 * from a tag from an HTML document.
 *
 * @version $Id: HtmlUtility.java,v 1.1 2008/05/25 17:26:08 swd\stefan.damian Exp $
 */
public class HtmlUtility
{
	/**
	 * Extract a plain text from an HTML content. Is specified like parameter user session
	 * because is necessary to extract real encoding type of specified HTML text.
	 * If is specified a null user user session will used default encoding and charset (configured in the portal).
	 *
	 * @param content HTML content
	 * @param encoding character set
	 *
	 * @return extract plain text
	 */
	public static String getPlainText(String content, String encoding) throws ParserException
	{
		if(content == null || content.length() <= 0) return null;

		//define ecoding type for HTML parser
		Page.setCharSet(encoding);

		StringBean sb = new StringBean();
		sb.setLinks(false);

		Lexer lexer = new Lexer(content);
		Parser mParser = new Parser(lexer);

		mParser.visitAllNodesWith(sb);

		mParser.parse(null);

		return  sb.getStrings();
	}

    /**
     * Extract a part from an HTML content - tag content.
	 * @param content HTML content
	 * @param encoding character set
	 * @param tag HTML tag name
	 *
     * @return the plain text included in specified tag.
     */
    public static String getPlainTextFromTag(String content, String tag, String encoding) throws ParserException
    {
		if(content == null || content.length() <= 0) return null;

		if(tag == null || tag.length() <= 0) return getPlainText(content, encoding);

		//define ecoding type for HTML parser
		Page.setCharSet(encoding);

		StringBean sb = new StringBean();
		sb.setLinks(false);

		Lexer lexer = new Lexer(content);
		Parser mParser = new Parser(lexer);

		mParser.visitAllNodesWith(sb);

		NodeFilter filter = new TagNameFilter(tag);
		mParser.parse(filter);

		return sb.getStrings();
    }
}
