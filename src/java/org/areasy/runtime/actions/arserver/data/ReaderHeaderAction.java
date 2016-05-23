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

import org.areasy.runtime.engine.RuntimeLogger;
import org.areasy.runtime.engine.base.AREasyException;

/**
 * Dedicated parser action to extract header columns from a data source file.
 *
 */
public class ReaderHeaderAction extends ReaderDataAction implements ParserAction
{
	/**
	 * Initialize the data source. Reading file and prepare the parser engine to parse the data.
	 * If the file not exist is trying to downloaded it from the ARS server.
	 *
	 * @throws AREasyException if any error will occur.
	 */
	public void open() throws AREasyException
	{
		if(getConfiguration().containsKey("startline")) getConfiguration().setKey("startline", "1");
			else if(getConfiguration().containsKey("startindex")) getConfiguration().setKey("startindex", "0");
				else getConfiguration().setKey("startindex", "0");

		super.open();
	}

	/**
	 * This action will read and parse a data source file and will extract the header columns.
	 *
	 * @throws org.areasy.runtime.engine.base.AREasyException if any error will occur
	 */
	public void run() throws AREasyException
	{
		int index = 0;
		String[] output = null;

		while(output == null && index <10)
		{
			output = engine.read();
			index++;
		}

		//register the output like an answer.
		if(output == null) throw new AREasyException("No file header found in the first 10 lines");
			else RuntimeLogger.add(output);
	}
}
