package org.areasy.common.parser.html.engine;

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

import org.areasy.common.parser.html.engine.decorators.DecodingNode;
import org.areasy.common.parser.html.engine.decorators.EscapeCharacterRemovingNode;
import org.areasy.common.parser.html.engine.decorators.NonBreakingSpaceConvertingNode;
import org.areasy.common.parser.html.engine.lexer.Page;

import java.io.Serializable;

/**
 * @version $Id: StringNodeFactory.java,v 1.1 2008/05/25 17:26:05 swd\stefan.damian Exp $
 */
public class StringNodeFactory extends PrototypicalNodeFactory implements Serializable
{

	/**
	 * Flag to tell the parser to decode strings returned by StringNode's toPlainTextString.
	 * Decoding occurs via the method, org.areasy.common.parser.document.html.engine.util.Translate.decode()
	 */
	protected boolean mDecode;


	/**
	 * Flag to tell the parser to remove escape characters, like \n and \t, returned by StringNode's toPlainTextString.
	 * Escape character removal occurs via the method, org.areasy.common.parser.document.html.engine.util.ParserUtils.removeEscapeCharacters()
	 */
	protected boolean mRemoveEscapes;

	/**
	 * Flag to tell the parser to convert non breaking space (from \u00a0 to a space " ").
	 * If true, this will happen inside StringNode's toPlainTextString.
	 */
	protected boolean mConvertNonBreakingSpaces;

	public StringNodeFactory()
	{
		mDecode = false;
		mRemoveEscapes = false;
		mConvertNonBreakingSpaces = false;
	}

	/**
	 * Create a new string node.
	 *
	 * @param page  The page the node is on.
	 * @param start The beginning position of the string.
	 * @param end   The ending positiong of the string.
	 */
	public Node createStringNode(Page page, int start, int end)
	{
		Node ret;

		ret = super.createStringNode(page, start, end);
		if (getDecode())
		{
			ret = new DecodingNode(ret);
		}
		if (getRemoveEscapes())
		{
			ret = new EscapeCharacterRemovingNode(ret);
		}
		if (getConvertNonBreakingSpaces())
		{
			ret = new NonBreakingSpaceConvertingNode(ret);
		}

		return (ret);
	}

	/**
	 * Set the decoding state.
	 *
	 * @param decode If <code>true</code>, string nodes decode text using {@link org.areasy.common.parser.html.utilities.Translate#decode}.
	 */
	public void setDecode(boolean decode)
	{
		mDecode = decode;
	}

	/**
	 * Get the decoding state.
	 *
	 * @return <code>true</code> if string nodes decode text.
	 */
	public boolean getDecode()
	{
		return (mDecode);
	}

	/**
	 * Set the escape removing state.
	 *
	 * @param remove If <code>true</code>, string nodes remove escape characters.
	 */
	public void setRemoveEscapes(boolean remove)
	{
		mRemoveEscapes = remove;
	}

	/**
	 * Get the escape removing state.
	 *
	 * @return The removing state.
	 */
	public boolean getRemoveEscapes()
	{
		return (mRemoveEscapes);
	}

	/**
	 * Set the non-breaking space replacing state.
	 *
	 * @param convert If <code>true</code>, string nodes replace &semi;nbsp; characters with spaces.
	 */
	public void setConvertNonBreakingSpaces(boolean convert)
	{
		mConvertNonBreakingSpaces = convert;
	}

	/**
	 * Get the non-breaking space replacing state.
	 *
	 * @return The replacing state.
	 */
	public boolean getConvertNonBreakingSpaces()
	{
		return (mConvertNonBreakingSpaces);
	}
}
