package org.areasy.common.parser.html.engine.scanners;

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

import org.areasy.common.parser.html.engine.lexer.Lexer;
import org.areasy.common.parser.html.engine.tags.Tag;
import org.areasy.common.parser.html.utilities.NodeList;
import org.areasy.common.parser.html.utilities.ParserException;

/**
 * Generic interface for scanning.
 * Tags needing specialized operations can provide an object that implements
 * this interface via getThisScanner().
 * By default non-composite tags simply perform the semantic action and
 * return while composite tags will gather their children.
 */
public interface Scanner
{

	/**
	 * Scan the tag.
	 * The Lexer is provided in order to do a lookahead operation.
	 *
	 * @param tag   HTML tag to be scanned for identification.
	 * @param lexer Provides html page access.
	 * @param stack The parse stack. May contain pending tags that enclose
	 *              this tag. Nodes on the stack should be considered incomplete.
	 * @return The resultant tag (may be unchanged).
	 * @throws ParserException if an unrecoverable problem occurs.
	 */
	public Tag scan(Tag tag, Lexer lexer, NodeList stack) throws ParserException;
}
