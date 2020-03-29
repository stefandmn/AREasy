package org.areasy.common.parser.excel.biff.drawing;

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

import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;

/**
 * ???
 */
class ClientTextBox extends EscherAtom
{
	/**
	 * The logger
	 */
	private static Logger logger = LoggerFactory.getLog(ClientTextBox.class);

	/**
	 * The raw data
	 */
	private byte[] data;

	/**
	 * Constructor
	 *
	 * @param erd
	 */
	public ClientTextBox(EscherRecordData erd)
	{
		super(erd);
	}

	/**
	 * Constructor
	 */
	public ClientTextBox()
	{
		super(EscherRecordType.CLIENT_TEXT_BOX);
	}

	/**
	 * Accessor for the raw data
	 *
	 * @return
	 */
	byte[] getData()
	{
		data = new byte[0];
		return setHeaderData(data);
	}
}
