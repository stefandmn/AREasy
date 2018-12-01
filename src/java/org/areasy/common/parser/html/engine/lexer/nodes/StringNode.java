package org.areasy.common.parser.html.engine.lexer.nodes;

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

import org.areasy.common.parser.html.engine.AbstractNode;
import org.areasy.common.parser.html.engine.lexer.Cursor;
import org.areasy.common.parser.html.engine.lexer.Page;
import org.areasy.common.parser.html.utilities.ParserException;

/**
 * Normal text in the HTML document is represented by this class.
 */
public class StringNode extends AbstractNode
{

	/**
	 * The contents of the string node, or override text.
	 */
	protected String mText;

	/**
	 * Constructor takes in the text string.
	 *
	 * @param text The string node text. For correct generation of HTML, this
	 *             should not contain representations of tags (unless they are balanced).
	 */
	public StringNode(String text)
	{
		super(null, 0, 0);
		setText(text);
	}

	/**
	 * Constructor takes in the page and beginning and ending posns.
	 *
	 * @param page  The page this string is on.
	 * @param start The beginning position of the string.
	 * @param end   The ending positiong of the string.
	 */
	public StringNode(Page page, int start, int end)
	{
		super(page, start, end);
		mText = null;
	}

	/**
	 * Returns the text of the string line.
	 */
	public String getText()
	{
		return (toHtml());
	}

	/**
	 * Sets the string contents of the node.
	 *
	 * @param text The new text for the node.
	 */
	public void setText(String text)
	{
		mText = text;
		nodeBegin = 0;
		nodeEnd = mText.length();
	}

	public String toPlainTextString()
	{
		return (toHtml());
	}

	public String toHtml()
	{
		String ret;

		ret = mText;
		if (null == ret)
		{
			ret = mPage.getText(getStartPosition(), getEndPosition());
		}

		return (ret);
	}

	/**
	 * Express this string node as a printable string
	 * This is suitable for display in a debugger or output to a printout.
	 * Control characters are replaced by their equivalent escape
	 * sequence and contents is truncated to 80 characters.
	 *
	 * @return A string representation of the string node.
	 */
	public String toString()
	{
		int startpos;
		int endpos;
		Cursor start;
		Cursor end;
		char c;
		StringBuffer ret;

		startpos = getStartPosition();
		endpos = getEndPosition();
		ret = new StringBuffer(endpos - startpos + 20);
		if (null == mText)
		{
			start = new Cursor(getPage(), startpos);
			end = new Cursor(getPage(), endpos);
			ret.append("Txt (");
			ret.append(start);
			ret.append(",");
			ret.append(end);
			ret.append("): ");
			while (start.getPosition() < endpos)
			{
				try
				{
					c = mPage.getCharacter(start);
					switch (c)
					{
						case '\t':
							ret.append("\\t");
							break;
						case '\n':
							ret.append("\\n");
							break;
						case '\r':
							ret.append("\\r");
							break;
						default:
							ret.append(c);
					}
				}
				catch (ParserException pe)
				{
					// not really expected, but we're only doing toString, so ignore
				}
				if (77 <= ret.length())
				{
					ret.append("...");
					break;
				}
			}
		}
		else
		{
			ret.append("Txt (");
			ret.append(startpos);
			ret.append(",");
			ret.append(endpos);
			ret.append("): ");
			while (startpos < endpos)
			{
				c = mText.charAt(startpos);
				switch (c)
				{
					case '\t':
						ret.append("\\t");
						break;
					case '\n':
						ret.append("\\n");
						break;
					case '\r':
						ret.append("\\r");
						break;
					default:
						ret.append(c);
				}
				if (77 <= ret.length())
				{
					ret.append("...");
					break;
				}
				startpos++;
			}
		}

		return (ret.toString());
	}

	public void accept(Object visitor)
	{
	}
}
