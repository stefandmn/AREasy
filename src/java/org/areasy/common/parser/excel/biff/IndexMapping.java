package org.areasy.common.parser.excel.biff;

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

import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;

/**
 * This class is a wrapper for a list of mappings between indices.
 * It is used when removing duplicate records and specifies the new
 * index for cells which have the duplicate format
 */
public final class IndexMapping
{
	/**
	 * The logger
	 */
	private static Logger logger = LoggerFactory.getLog(IndexMapping.class);

	/**
	 * The array of new indexes for an old one
	 */
	private int[] newIndices;

	/**
	 * Constructor
	 *
	 * @param size the number of index numbers to be mapped
	 */
	public IndexMapping(int size)
	{
		newIndices = new int[size];
	}

	/**
	 * Sets a mapping
	 *
	 * @param oldIndex the old index
	 * @param newIndex the new index
	 */
	public void setMapping(int oldIndex, int newIndex)
	{
		newIndices[oldIndex] = newIndex;
	}

	/**
	 * Gets the new cell format index
	 *
	 * @param oldIndex the existing index number
	 * @return the new index number
	 */
	public int getNewIndex(int oldIndex)
	{
		return newIndices[oldIndex];
	}
}
