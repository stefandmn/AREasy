package org.areasy.runtime.actions.arserver.data;

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

import org.areasy.runtime.RuntimeAction;
import org.areasy.runtime.engine.services.parser.ParserEngine;

/**
 * This is a template action which permits to execute usual tasks and specific operation to manage one specific parser step
 * base on the same parser data source..
 * <p>
 * The difference between standard action is that this library expose an additional method to have possibility to
 * define workflows, executing a chain of actions (which are instance of this interface) for a clear identified asset (CI).
 */
public interface ParserAction extends RuntimeAction
{
	/**
	 * Get the parser engine which is the main library who manipulates the specialized parsers.
	 *
	 * @return <code>ParserEngine</code> structure and the active instance
	 */
	ParserEngine getParserEngine();
}
