package org.areasy.runtime.plugins.sso.ntlm.engine;

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

import jcifs.dcerpc.DcerpcMessage;
import jcifs.dcerpc.ndr.NdrBuffer;
import jcifs.dcerpc.ndr.NdrException;

public class NetrLogonSamLogon extends DcerpcMessage
{
	private NetlogonAuthenticator authenticator;

	@SuppressWarnings("unused")
	private byte authoritative;

	private String computerName;
	private NetlogonNetworkInfo logonInformation;
	private short logonLevel;
	private String logonServer;
	private NetlogonAuthenticator returnAuthenticator;
	private int status;
	private NetlogonValidationSamInfo validationInformation;
	private short validationLevel;

	public NetrLogonSamLogon(String logonServer, String computerName, NetlogonAuthenticator netlogonAuthenticator,
			NetlogonAuthenticator returnNetlogonAuthenticator, int logonLevel, NetlogonNetworkInfo netlogonNetworkInfo, int validationLevel,
			NetlogonValidationSamInfo netlogonValidationSamInfo, int authoritative)
	{

		this.logonServer = logonServer;
		this.computerName = computerName;
		authenticator = netlogonAuthenticator;
		returnAuthenticator = returnNetlogonAuthenticator;
		this.logonLevel = (short) logonLevel;
		logonInformation = netlogonNetworkInfo;
		this.validationLevel = (short) validationLevel;
		validationInformation = netlogonValidationSamInfo;
		this.authoritative = (byte) authoritative;

		ptype = 0;
		flags = DCERPC_FIRST_FRAG | DCERPC_LAST_FRAG;
	}

	public void decode_out(NdrBuffer ndrBuffer) throws NdrException
	{
		int returnAuthenticator = ndrBuffer.dec_ndr_long();

		if (returnAuthenticator > 0)
		{
			this.returnAuthenticator.decode(ndrBuffer);
		}

		ndrBuffer.dec_ndr_short();

		int validationInformation = ndrBuffer.dec_ndr_long();

		if (validationInformation > 0)
		{
			ndrBuffer = ndrBuffer.deferred;
			this.validationInformation.decode(ndrBuffer);
		}

		authoritative = (byte) ndrBuffer.dec_ndr_small();
		status = ndrBuffer.dec_ndr_long();
	}

	public void encode_in(NdrBuffer ndrBuffer)
	{
		ndrBuffer.enc_ndr_referent(logonServer, 1);
		ndrBuffer.enc_ndr_string(logonServer);

		ndrBuffer.enc_ndr_referent(computerName, 1);
		ndrBuffer.enc_ndr_string(computerName);

		ndrBuffer.enc_ndr_referent(authenticator, 1);

		authenticator.encode(ndrBuffer);

		ndrBuffer.enc_ndr_referent(returnAuthenticator, 1);

		returnAuthenticator.encode(ndrBuffer);

		ndrBuffer.enc_ndr_short(logonLevel);
		ndrBuffer.enc_ndr_short(logonLevel);

		ndrBuffer.enc_ndr_referent(logonInformation, 1);

		logonInformation.encode(ndrBuffer);

		ndrBuffer.enc_ndr_short(validationLevel);
	}

	public NetlogonValidationSamInfo getNetlogonValidationSamInfo()
	{
		return validationInformation;
	}

	public int getOpnum()
	{
		return 2;
	}

	public int getStatus()
	{
		return status;
	}
}
