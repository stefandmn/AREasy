package org.areasy.runtime.plugins.sso.ntlm.engine;

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

import jcifs.dcerpc.ndr.NdrBuffer;
import jcifs.dcerpc.ndr.NdrObject;

public class NetlogonNetworkInfo extends NdrObject
{
	private byte[] lmChallenge;
	private byte[] lmChallengeResponse;
	private NetlogonIdentityInfo netlogonIdentityInfo;
	private byte[] ntChallengeResponse;

	public NetlogonNetworkInfo(NetlogonIdentityInfo netlogonIdentityInfo, byte[] lmChallenge, byte[] ntChallengeResponse, byte[] lmChallengeResponse)
	{
		this.lmChallenge = lmChallenge;
		this.ntChallengeResponse = ntChallengeResponse;
		this.lmChallengeResponse = lmChallengeResponse;
		this.netlogonIdentityInfo = netlogonIdentityInfo;
	}

	public void decode(NdrBuffer ndrBuffer)
	{
		//nothing here (yet)
	}

	public void encode(NdrBuffer ndrBuffer)
	{
		ndrBuffer.align(4);

		netlogonIdentityInfo.encode(ndrBuffer);

		int lmChallengeIndex = ndrBuffer.index;

		ndrBuffer.advance(8);

		ndrBuffer.enc_ndr_short((short) ntChallengeResponse.length);
		ndrBuffer.enc_ndr_short((short) ntChallengeResponse.length);
		ndrBuffer.enc_ndr_referent(ntChallengeResponse, 1);

		ndrBuffer.enc_ndr_short((short) lmChallengeResponse.length);
		ndrBuffer.enc_ndr_short((short) lmChallengeResponse.length);
		ndrBuffer.enc_ndr_referent(lmChallengeResponse, 1);

		netlogonIdentityInfo.encodeLogonDomainName(ndrBuffer);
		netlogonIdentityInfo.encodeUserName(ndrBuffer);
		netlogonIdentityInfo.encodeWorkStationName(ndrBuffer);

		ndrBuffer = ndrBuffer.derive(lmChallengeIndex);

		for (int i = 0; i < 8; i++)
		{
			ndrBuffer.enc_ndr_small(lmChallenge[i]);
		}

		encodeChallengeResponse(ndrBuffer, ntChallengeResponse);
		encodeChallengeResponse(ndrBuffer, lmChallengeResponse);
	}

	protected void encodeChallengeResponse(NdrBuffer ndrBuffer, byte[] challenge)
	{
		ndrBuffer = ndrBuffer.deferred;

		ndrBuffer.enc_ndr_long(challenge.length);
		ndrBuffer.enc_ndr_long(0);
		ndrBuffer.enc_ndr_long(challenge.length);

		int index = ndrBuffer.index;

		ndrBuffer.advance(challenge.length);

		ndrBuffer = ndrBuffer.derive(index);

		for (int i = 0; i < challenge.length; i++)
		{
			ndrBuffer.enc_ndr_small(challenge[i]);
		}
	}
}
