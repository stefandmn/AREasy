package org.areasy.common.parser.excel.biff.drawing;

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

import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;

/**
 * Class for atoms.  This may be instantiated as is for unknown/uncared about
 * atoms, or subclassed if we have some semantic interest in the contents
 */
class EscherAtom extends EscherRecord
{
	/**
	 * The logger
	 */
	private static Logger logger = LoggerFactory.getLog(EscherAtom.class);

	/**
	 * Constructor
	 *
	 * @param erd the escher record data
	 */
	public EscherAtom(EscherRecordData erd)
	{
		super(erd);
	}

	/**
	 * Constructor
	 *
	 * @param type the type
	 */
	protected EscherAtom(EscherRecordType type)
	{
		super(type);
	}

	/**
	 * Gets the data for writing
	 *
	 * @return the data
	 */
	byte[] getData()
	{
		logger.warn("escher atom getData called on object of type " +
				getClass().getName() + " code " +
				Integer.toString(getType().getValue(), 16));
		return null;
	}
}
