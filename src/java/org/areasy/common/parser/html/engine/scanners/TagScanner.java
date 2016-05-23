package org.areasy.common.parser.html.engine.scanners;

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

import org.areasy.common.parser.html.engine.lexer.Lexer;
import org.areasy.common.parser.html.engine.tags.Tag;
import org.areasy.common.parser.html.utilities.NodeList;
import org.areasy.common.parser.html.utilities.ParserException;

import java.io.Serializable;

/**
 * TagScanner is an abstract superclass, subclassed to create specific scanners.
 * When asked to scan the tag, this class does nothing other than perform the
 * tag's semantic action.
 * Use TagScanner when you have a meta task to do like setting the BASE url for
 * the page when a BASE tag is encountered.
 * If you want to match end tags and handle special syntax between tags,
 * then you'll probably want to subclass {@link CompositeTagScanner} instead.
 */
public class TagScanner implements Scanner, Serializable
{

	/**
	 * Create a (non-composite) tag scanner.
	 */
	public TagScanner()
	{
	}

	/**
	 * Scan the tag.
	 * For this implementation, the only operation is to perform the tag's
	 * semantic action.
	 *
	 * @param tag   The tag to scan.
	 * @param lexer Provides html page access.
	 * @param stack The parse stack. May contain pending tags that enclose
	 *              this tag.
	 * @return The resultant tag (may be unchanged).
	 */
	public Tag scan(Tag tag, Lexer lexer, NodeList stack) throws ParserException
	{
		tag.doSemanticAction();

		return (tag);
	}
}
