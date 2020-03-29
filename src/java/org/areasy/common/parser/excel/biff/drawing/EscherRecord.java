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
 * The base class for all escher records.  This class contains
 * the org.areasy.common.parser.documents.excel.common.header data and is basically a wrapper for the EscherRecordData
 * object
 */
abstract class EscherRecord
{
	/**
	 * The logger
	 */
	private static Logger logger = LoggerFactory.getLog(EscherRecord.class);

	/**
	 * The escher data
	 */
	private EscherRecordData data;
	//protected EscherRecordData data;

	/**
	 * The length of the escher header on all records
	 */
	protected static final int HEADER_LENGTH = 8;

	/**
	 * Constructor
	 *
	 * @param erd the data
	 */
	protected EscherRecord(EscherRecordData erd)
	{
		data = erd;
	}

	/**
	 * Constructor
	 *
	 * @param type the type
	 */
	protected EscherRecord(EscherRecordType type)
	{
		data = new EscherRecordData(type);
	}

	/**
	 * Identifies whether this item is a container
	 *
	 * @param cont TRUE if this is a container, FALSE otherwise
	 */
	protected void setContainer(boolean cont)
	{
		data.setContainer(cont);
	}

	/**
	 * Gets the entire length of the record, including the header
	 *
	 * @return the length of the record, including the header data
	 */
	public int getLength()
	{
		return data.getLength() + HEADER_LENGTH;
	}

	/**
	 * Accessor for the escher stream
	 *
	 * @return the escher stream
	 */
	protected final EscherStream getEscherStream()
	{
		return data.getEscherStream();
	}

	/**
	 * The position of this escher record in the stream
	 *
	 * @return the position
	 */
	protected final int getPos()
	{
		return data.getPos();
	}

	/**
	 * Accessor for the instance
	 *
	 * @return the instance
	 */
	protected final int getInstance()
	{
		return data.getInstance();
	}

	/**
	 * Sets the instance number when writing out the escher data
	 *
	 * @param i the instance
	 */
	protected final void setInstance(int i)
	{
		data.setInstance(i);
	}

	/**
	 * Sets the version when writing out the escher data
	 *
	 * @param v the version
	 */
	protected final void setVersion(int v)
	{
		data.setVersion(v);
	}

	/**
	 * Accessor for the escher type
	 *
	 * @return the type
	 */
	public EscherRecordType getType()
	{
		return data.getType();
	}

	/**
	 * Abstract method used to retrieve the generated escher data when writing
	 * out image information
	 *
	 * @return the escher data
	 */
	abstract byte[] getData();

	/**
	 * Prepends the standard header data to the first eight bytes of the array
	 * and returns it
	 *
	 * @param d the data
	 * @return the binary data
	 */
	final byte[] setHeaderData(byte[] d)
	{
		return data.setHeaderData(d);
	}

	/**
	 * Gets the data that was read in, excluding the header data
	 *
	 * @return the bytes read in, excluding the header data
	 */
	byte[] getBytes()
	{
		return data.getBytes();
	}

	/**
	 * Accessor for the stream length
	 *
	 * @return the stream length
	 */
	protected int getStreamLength()
	{
		return data.getStreamLength();
	}

	/**
	 * Used by the EscherDisplay class to retrieve the data
	 *
	 * @return the data
	 */
	protected EscherRecordData getEscherData()
	{
		return data;
	}
}
