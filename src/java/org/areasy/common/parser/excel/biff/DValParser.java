package org.areasy.common.parser.excel.biff;

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
 * Class which parses the binary data associated with Data Validity (DVal)
 * setting
 */
public class DValParser
{
	/**
	 * The logger
	 */
	private static Logger logger = LoggerFactory.getLog(DValParser.class);

	// The option masks
	private static int PROMPT_BOX_VISIBLE_MASK = 0x1;
	private static int PROMPT_BOX_AT_CELL_MASK = 0x2;
	private static int VALIDITY_DATA_CACHED_MASK = 0x4;

	/**
	 * Prompt box visible
	 */
	private boolean promptBoxVisible;

	/**
	 * Empty cells allowed
	 */
	private boolean promptBoxAtCell;

	/**
	 * Cell validity data cached in following DV records
	 */
	private boolean validityDataCached;

	/**
	 * The number of following DV records
	 */
	private int numDVRecords;

	/**
	 * The object id of the associated down arrow
	 */
	private int objectId;

	/**
	 * Constructor
	 */
	public DValParser(byte[] data)
	{
		int options = IntegerHelper.getInt(data[0], data[1]);

		promptBoxVisible = (options & PROMPT_BOX_VISIBLE_MASK) != 0;
		promptBoxAtCell = (options & PROMPT_BOX_AT_CELL_MASK) != 0;
		validityDataCached = (options & VALIDITY_DATA_CACHED_MASK) != 0;

		objectId = IntegerHelper.getInt(data[10], data[11], data[12], data[13]);
		numDVRecords = IntegerHelper.getInt(data[14], data[15],
				data[16], data[17]);
	}

	/**
	 * Constructor
	 */
	public DValParser(int objid, int num)
	{
		objectId = objid;
		numDVRecords = num;
		validityDataCached = true;
	}

	/**
	 * Gets the data
	 */
	public byte[] getData()
	{
		byte[] data = new byte[18];

		int options = 0;

		if (promptBoxVisible)
		{
			options |= PROMPT_BOX_VISIBLE_MASK;
		}

		if (promptBoxAtCell)
		{
			options |= PROMPT_BOX_AT_CELL_MASK;
		}

		if (validityDataCached)
		{
			options |= VALIDITY_DATA_CACHED_MASK;
		}

		IntegerHelper.getTwoBytes(options, data, 0);

		IntegerHelper.getFourBytes(objectId, data, 10);

		IntegerHelper.getFourBytes(numDVRecords, data, 14);

		return data;
	}

	/**
	 * Called when a remove row or column results in one of DV records being
	 * removed
	 */
	public void dvRemoved()
	{
		numDVRecords--;
	}

	/**
	 * Accessor for the number of DV records
	 *
	 * @return the number of DV records for this list
	 */
	public int getNumberOfDVRecords()
	{
		return numDVRecords;
	}

	/**
	 * Accessor for the object id
	 *
	 * @return the object id
	 */
	public int getObjectId()
	{
		return objectId;
	}

	/**
	 * Called when adding a DV record on a copied DVal
	 */
	public void dvAdded()
	{
		numDVRecords++;
	}
}
