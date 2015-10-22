package org.areasy.common.parser.excel.biff.drawing;

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

class Chunk
{
	private int pos;
	private int length;
	private ChunkType type;
	private byte[] data;

	public Chunk(int p, int l, ChunkType ct, byte[] d)
	{
		pos = p;
		length = l;
		type = ct;
		data = new byte[length];
		System.arraycopy(d, pos, data, 0, length);

	}

	public byte[] getData()
	{
		return data;
	}
}
