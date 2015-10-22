package org.areasy.runtime.plugins.sso.ntlm.engine;

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

import jcifs.dcerpc.ndr.NdrBuffer;
import jcifs.dcerpc.ndr.NdrObject;

public class NetlogonAuthenticator extends NdrObject
{
	private byte[] credential;
	private int timestamp;

	public NetlogonAuthenticator()
	{
		credential = new byte[8];
	}

	public NetlogonAuthenticator(byte[] credential, int timestamp)
	{
		this.credential = credential;
		this.timestamp = timestamp;
	}

	public void decode(NdrBuffer ndrBuffer)
	{
		ndrBuffer.align(4);

		int index = ndrBuffer.index;

		ndrBuffer.advance(8);

		timestamp = ndrBuffer.dec_ndr_long();

		ndrBuffer = ndrBuffer.derive(index);

		for (int i = 0; i < 8; i++)
		{
			credential[i] = (byte) ndrBuffer.dec_ndr_small();
		}
	}

	public void encode(NdrBuffer ndrBuffer)
	{
		ndrBuffer.align(4);

		int index = ndrBuffer.index;

		ndrBuffer.advance(8);

		ndrBuffer.enc_ndr_long(timestamp);

		ndrBuffer = ndrBuffer.derive(index);

		for (int i = 0; i < 8; i++)
		{
			ndrBuffer.enc_ndr_small(credential[i]);
		}
	}
}