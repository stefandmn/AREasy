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

public class NetrServerAuthenticate3 extends DcerpcMessage
{
	private String accountName;
	private int accountRid;
	private byte[] clientCredential;
	private String computerName;
	private int negotiateFlags;
	private String primaryName;
	private short secureChannelType;
	private byte[] serverCredential;
	private int status;

	public NetrServerAuthenticate3(String primaryName, String accountName, int secureChannelType, String computerName, byte[] clientCredential, byte[] serverCredential, int negotiateFlags)
	{
		this.primaryName = primaryName;
		this.accountName = accountName;
		this.secureChannelType = (short) secureChannelType;
		this.computerName = computerName;
		this.clientCredential = clientCredential;
		this.serverCredential = serverCredential;
		this.negotiateFlags = negotiateFlags;

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
			serverCredential[i] = (byte) ndrBuffer.dec_ndr_small();
		}

		negotiateFlags = ndrBuffer.dec_ndr_long();
		accountRid = ndrBuffer.dec_ndr_long();
		status = ndrBuffer.dec_ndr_long();
	}

	public void encode_in(NdrBuffer ndrBuffer)
	{
		ndrBuffer.enc_ndr_referent(primaryName, 1);
		ndrBuffer.enc_ndr_string(primaryName);
		ndrBuffer.enc_ndr_string(accountName);
		ndrBuffer.enc_ndr_short(secureChannelType);
		ndrBuffer.enc_ndr_string(computerName);

		int index = ndrBuffer.index;

		ndrBuffer.advance(8);

		NdrBuffer derivedNrdBuffer = ndrBuffer.derive(index);

		for (int i = 0; i < 8; i++)
		{
			derivedNrdBuffer.enc_ndr_small(clientCredential[i]);
		}

		ndrBuffer.enc_ndr_long(negotiateFlags);
	}

	public int getAccountRid()
	{
		return accountRid;
	}

	public int getNegotiatedFlags()
	{
		return negotiateFlags;
	}

	public int getOpnum()
	{
		return 26;
	}

	public byte[] getServerCredential()
	{
		return serverCredential;
	}

	public int getStatus()
	{
		return status;
	}
}
