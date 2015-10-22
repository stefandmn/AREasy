package org.areasy.runtime.actions.arserver.data;

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

import org.areasy.runtime.actions.AbstractAction;
import org.areasy.runtime.engine.RuntimeLogger;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.runtime.engine.services.parser.ParserEngine;

/**
 * Dedicated parser action to extract data columns from a data source file.
 *
 */
public class ReaderDataAction extends AbstractAction implements ParserAction
{
	/** parser engine instance */
	protected ParserEngine engine = null;

	/**
	 * Initialize the data source. Reading file and prepare the parser engine to parse the data.
	 * If the file not exist is trying to downloaded it from the ARS server.
	 *
	 * @throws AREasyException if any error will occur.
	 */
	public void open() throws AREasyException
	{
		engine = new ParserEngine(getServerConnection(), getManager().getConfiguration(), getConfiguration());
		engine.init();
	}

	/**
	 * This action will read and parse a data source file and will extract the data columns.
	 *
	 * @throws org.areasy.runtime.engine.base.AREasyException if any error will occur
	 */
	public void run() throws AREasyException
	{
		//get or define the header index.
		RuntimeLogger.add( engine.read() );
	}

	/**
	 * Get the parser engine which is the main library who manipulates the specialized parsers.
	 *
	 * @return <code>ParserEngine</code> structure and the active instance
	 */
	public ParserEngine getParserEngine()
	{
		return this.engine;
	}

	/**
	 * Dispoase the parser engine.
	 */
	public void close() throws AREasyException
	{
		//close the engine.
		engine.close();
		engine = null;
	}
}
