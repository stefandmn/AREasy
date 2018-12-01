package org.areasy.common.parser.excel.biff.drawing;

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

import java.util.Arrays;

/**
 * Enumeration for the various chunk types
 */
class ChunkType
{
	private byte[] id;
	private String name;

	private static ChunkType[] chunkTypes = new ChunkType[0];

	private ChunkType(int d1, int d2, int d3, int d4, String n)
	{
		id = new byte[]{(byte) d1, (byte) d2, (byte) d3, (byte) d4};
		name = n;

		ChunkType[] ct = new ChunkType[chunkTypes.length + 1];
		System.arraycopy(chunkTypes, 0, ct, 0, chunkTypes.length);
		ct[chunkTypes.length] = this;
		chunkTypes = ct;
	}

	public String getName()
	{
		return name;
	}

	public static ChunkType getChunkType(byte d1, byte d2, byte d3, byte d4)
	{
		byte[] cmp = new byte[]{d1, d2, d3, d4};

		boolean found = false;
		ChunkType chunk = ChunkType.UNKNOWN;

		for (int i = 0; i < chunkTypes.length && !found; i++)
		{
			if (Arrays.equals(chunkTypes[i].id, cmp))
			{
				chunk = chunkTypes[i];
				found = true;
			}
		}

		return chunk;
	}


	public static ChunkType IHDR = new ChunkType(0x49, 0x48, 0x44, 0x52, "IHDR");
	public static ChunkType IEND = new ChunkType(0x49, 0x45, 0x4e, 0x44, "IEND");
	public static ChunkType PHYS = new ChunkType(0x70, 0x48, 0x59, 0x73, "pHYs");
	public static ChunkType UNKNOWN = new ChunkType(0xff, 0xff, 0xff, 0xff, "UNKNOWN");
}
