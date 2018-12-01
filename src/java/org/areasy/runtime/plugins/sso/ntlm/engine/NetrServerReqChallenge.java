package org.areasy.runtime.plugins.sso.ntlm.engine;

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

import jcifs.dcerpc.DcerpcMessage;
import jcifs.dcerpc.ndr.NdrBuffer;

public class NetrServerReqChallenge extends DcerpcMessage
{
	private byte[] clientChallenge;
	private String computerName;
	private String primaryName;
	private byte[] serverChallenge;
	private int status;

	public NetrServerReqChallenge(String primaryName, String computerName, byte[] clientChallenge, byte[] serverChallenge)
	{

		this.primaryName = primaryName;
		this.computerName = computerName;
		this.clientChallenge = clientChallenge;
		this.serverChallenge = serverChallenge;

		ptype = 0;
		flags = DCERPC_FIRST_FRAG | DCERPC_LAST_FRAG;
	}

	public void decode_out(NdrBuffer ndrBuffer)
	{
		int index = ndrBuffer.index;

		ndrBuffer.advance(8);

		ndrBuffer = ndrBuffer.derive(index);

		for (int i = 0; i < 8; i++)
		{
			serverChallenge[i] = (byte) ndrBuffer.dec_ndr_small();
		}

		status = ndrBuffer.dec_ndr_long();
	}

	public void encode_in(NdrBuffer ndrBuffer)
	{
		ndrBuffer.enc_ndr_referent(primaryName, 1);
		ndrBuffer.enc_ndr_string(primaryName);
		ndrBuffer.enc_ndr_string(computerName);

		int index = ndrBuffer.index;

		ndrBuffer.advance(8);

		ndrBuffer = ndrBuffer.derive(index);

		for (int i = 0; i < 8; i++)
		{
			ndrBuffer.enc_ndr_small(clientChallenge[i]);
		}
	}

	public int getOpnum()
	{
		return 4;
	}

	public byte[] getServerChallenge()
	{
		return serverChallenge;
	}

	public int getStatus()
	{
		return status;
	}
}