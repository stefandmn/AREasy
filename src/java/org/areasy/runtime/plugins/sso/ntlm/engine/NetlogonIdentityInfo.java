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

import jcifs.dcerpc.UnicodeString;
import jcifs.dcerpc.ndr.NdrBuffer;
import jcifs.dcerpc.ndr.NdrObject;
import jcifs.dcerpc.rpc;

public class NetlogonIdentityInfo extends NdrObject
{
	private rpc.unicode_string logonDomainName;
	private int parameterControl;
	private int reservedHigh;
	private int reservedLow;
	private rpc.unicode_string userName;
	private rpc.unicode_string workstation;

	public NetlogonIdentityInfo(String logonDomainName, int parameterControl, int reservedLow, int reservedHigh, String userName, String workstation)
	{
		this.logonDomainName = new UnicodeString(logonDomainName, false);
		this.parameterControl = parameterControl;
		this.reservedLow = reservedLow;
		this.reservedHigh = reservedHigh;
		this.userName = new UnicodeString(userName, false);
		this.workstation = new UnicodeString(workstation, false);
	}

	public void decode(NdrBuffer ndrBuffer)
	{
		//nothing to do here
	}

	public void encode(NdrBuffer ndrBuffer)
	{
		ndrBuffer.enc_ndr_short(logonDomainName.length);
		ndrBuffer.enc_ndr_short(logonDomainName.maximum_length);
		ndrBuffer.enc_ndr_referent(logonDomainName.buffer, 1);
		ndrBuffer.enc_ndr_long(parameterControl);
		ndrBuffer.enc_ndr_long(reservedLow);
		ndrBuffer.enc_ndr_long(reservedHigh);
		ndrBuffer.enc_ndr_short(userName.length);
		ndrBuffer.enc_ndr_short(userName.maximum_length);
		ndrBuffer.enc_ndr_referent(userName.buffer, 1);
		ndrBuffer.enc_ndr_short(workstation.length);
		ndrBuffer.enc_ndr_short(workstation.maximum_length);
		ndrBuffer.enc_ndr_referent(workstation.buffer, 1);
	}

	public void encodeLogonDomainName(NdrBuffer ndrBuffer)
	{
		encodeUnicodeString(ndrBuffer, logonDomainName);
	}

	public void encodeUserName(NdrBuffer ndrBuffer)
	{
		encodeUnicodeString(ndrBuffer, userName);
	}

	public void encodeWorkStationName(NdrBuffer ndrBuffer)
	{
		encodeUnicodeString(ndrBuffer, workstation);
	}

	protected void encodeUnicodeString(NdrBuffer ndrBuffer, rpc.unicode_string string)
	{
		ndrBuffer = ndrBuffer.deferred;

		int stringBufferl = string.length / 2;
		int stringBuffers = string.maximum_length / 2;

		ndrBuffer.enc_ndr_long(stringBuffers);
		ndrBuffer.enc_ndr_long(0);
		ndrBuffer.enc_ndr_long(stringBufferl);

		int stringBufferIndex = ndrBuffer.index;

		ndrBuffer.advance(2 * stringBufferl);
		ndrBuffer = ndrBuffer.derive(stringBufferIndex);

		for (int _i = 0; _i < stringBufferl; _i++)
		{
			ndrBuffer.enc_ndr_short(string.buffer[_i]);
		}
	}
}
