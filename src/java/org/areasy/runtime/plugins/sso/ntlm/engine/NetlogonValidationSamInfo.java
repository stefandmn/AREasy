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

import jcifs.dcerpc.ndr.NdrBuffer;
import jcifs.dcerpc.ndr.NdrException;
import jcifs.dcerpc.ndr.NdrObject;
import jcifs.dcerpc.rpc;

public class NetlogonValidationSamInfo extends NdrObject
{
	@SuppressWarnings("unused")
	private short _badPasswordCount;

	private rpc.unicode_string effectiveName;
	private rpc.unicode_string fullName;
	private int groupCount;
	private GroupMembership[] groupIds;
	private rpc.unicode_string homeDirectory;
	private rpc.unicode_string homeDirectoryDrive;

	@SuppressWarnings("unused")
	private long kickoffTime;

	@SuppressWarnings("unused")
	private long logoffTime;

	@SuppressWarnings("unused")
	private short logonCount;

	private rpc.sid_t logonDomain;
	private rpc.unicode_string logonDomainName;
	private rpc.unicode_string logonScript;
	private rpc.unicode_string logonServer;

	@SuppressWarnings("unused")
	private long logonTime;

	@SuppressWarnings("unused")
	private long passwordCanChange;

	@SuppressWarnings("unused")
	private long passwordLastSet;

	@SuppressWarnings("unused")
	private long passwordMustChange;

	@SuppressWarnings("unused")
	private int primaryGroupId;

	private rpc.unicode_string profilePath;

	@SuppressWarnings("unused")
	private int userFlags;

	@SuppressWarnings("unused")
	private int userId;

	private byte[] userSessionKey;

	public NetlogonValidationSamInfo()
	{
		effectiveName = new rpc.unicode_string();
		fullName = new rpc.unicode_string();
		logonScript = new rpc.unicode_string();
		profilePath = new rpc.unicode_string();
		homeDirectory = new rpc.unicode_string();
		homeDirectoryDrive = new rpc.unicode_string();
		logonServer = new rpc.unicode_string();
		logonDomainName = new rpc.unicode_string();
		userSessionKey = new byte[16];
		logonDomain = new rpc.sid_t();
	}

	public void decode(NdrBuffer ndrBuffer) throws NdrException
	{
		logonTime = ndrBuffer.dec_ndr_hyper();
		logoffTime = ndrBuffer.dec_ndr_hyper();
		kickoffTime = ndrBuffer.dec_ndr_hyper();
		passwordLastSet = ndrBuffer.dec_ndr_hyper();
		passwordCanChange = ndrBuffer.dec_ndr_hyper();
		passwordMustChange = ndrBuffer.dec_ndr_hyper();

		effectiveName.length = (short) ndrBuffer.dec_ndr_short();
		effectiveName.maximum_length = (short) ndrBuffer.dec_ndr_short();

		int effectiveNamePtr = ndrBuffer.dec_ndr_long();

		fullName.length = (short) ndrBuffer.dec_ndr_short();
		fullName.maximum_length = (short) ndrBuffer.dec_ndr_short();

		int fullNamePtr = ndrBuffer.dec_ndr_long();

		logonScript.length = (short) ndrBuffer.dec_ndr_short();
		logonScript.maximum_length = (short) ndrBuffer.dec_ndr_short();

		int logonScriptPtr = ndrBuffer.dec_ndr_long();

		profilePath.length = (short) ndrBuffer.dec_ndr_short();
		profilePath.maximum_length = (short) ndrBuffer.dec_ndr_short();

		int profilePathPtr = ndrBuffer.dec_ndr_long();

		homeDirectory.length = (short) ndrBuffer.dec_ndr_short();
		homeDirectory.maximum_length = (short) ndrBuffer.dec_ndr_short();

		int homeDirectoryPtr = ndrBuffer.dec_ndr_long();

		homeDirectoryDrive.length = (short) ndrBuffer.dec_ndr_short();
		homeDirectoryDrive.maximum_length = (short) ndrBuffer.dec_ndr_short();

		int homeDirectoryDrivePtr = ndrBuffer.dec_ndr_long();

		logonCount = (short) ndrBuffer.dec_ndr_short();
		_badPasswordCount = (short) ndrBuffer.dec_ndr_short();

		userId = ndrBuffer.dec_ndr_long();
		primaryGroupId = ndrBuffer.dec_ndr_long();

		groupCount = ndrBuffer.dec_ndr_long();

		int groupIdsPtr = ndrBuffer.dec_ndr_long();

		userFlags = ndrBuffer.dec_ndr_long();

		int userSessionKeyI = ndrBuffer.index;

		ndrBuffer.advance(16);

		logonServer.length = (short) ndrBuffer.dec_ndr_short();
		logonServer.maximum_length = (short) ndrBuffer.dec_ndr_short();

		int logonServerPtr = ndrBuffer.dec_ndr_long();

		logonDomainName.length = (short) ndrBuffer.dec_ndr_short();
		logonDomainName.maximum_length = (short) ndrBuffer.dec_ndr_short();

		int logonDomainNamePtr = ndrBuffer.dec_ndr_long();

		int logonDomainPtr = ndrBuffer.dec_ndr_long();

		ndrBuffer.advance(40);

		if (effectiveNamePtr > 0)
		{
			decodeUnicodeString(ndrBuffer, effectiveName);
		}

		if (fullNamePtr > 0)
		{
			decodeUnicodeString(ndrBuffer, fullName);
		}

		if (logonScriptPtr > 0)
		{
			decodeUnicodeString(ndrBuffer, logonScript);
		}

		if (profilePathPtr > 0)
		{
			decodeUnicodeString(ndrBuffer, profilePath);
		}

		if (homeDirectoryPtr > 0)
		{
			decodeUnicodeString(ndrBuffer, homeDirectory);
		}

		if (homeDirectoryDrivePtr > 0)
		{
			decodeUnicodeString(ndrBuffer, homeDirectoryDrive);
		}

		if (groupIdsPtr > 0)
		{
			groupIds = new GroupMembership[groupCount];

			ndrBuffer = ndrBuffer.deferred;

			int groupIdsS = ndrBuffer.dec_ndr_long();
			int groupIdsI = ndrBuffer.index;

			ndrBuffer.advance(8 * groupIdsS);

			ndrBuffer = ndrBuffer.derive(groupIdsI);

			for (int i = 0; i < groupIdsS; i++)
			{
				if (groupIds[i] == null)
				{
					groupIds[i] = new GroupMembership();
				}

				groupIds[i].decode(ndrBuffer);
			}
		}

		ndrBuffer = ndrBuffer.derive(userSessionKeyI);

		for (int i = 0; i < 16; i++)
		{
			userSessionKey[i] = (byte) ndrBuffer.dec_ndr_small();
		}

		if (logonServerPtr > 0)
		{
			decodeUnicodeString(ndrBuffer, logonServer);
		}

		if (logonDomainNamePtr > 0)
		{
			decodeUnicodeString(ndrBuffer, logonDomainName);
		}

		if (logonDomainPtr > 0)
		{
			ndrBuffer = ndrBuffer.deferred;

			logonDomain.decode(ndrBuffer);
		}
	}

	public void encode(NdrBuffer ndrBuffer)
	{
		//nothing here
	}

	public rpc.unicode_string getEffectiveName()
	{
		return effectiveName;
	}

	protected void decodeUnicodeString(NdrBuffer ndrBuffer, rpc.unicode_string string)
	{
		ndrBuffer = ndrBuffer.deferred;

		int bufferS = ndrBuffer.dec_ndr_long();

		ndrBuffer.dec_ndr_long();

		int bufferL = ndrBuffer.dec_ndr_long();
		int bufferI = ndrBuffer.index;

		ndrBuffer.advance(2 * bufferL);

		if (string.buffer == null)
		{
			string.buffer = new short[bufferS];
		}

		ndrBuffer = ndrBuffer.derive(bufferI);

		for (int i = 0; i < bufferL; i++)
		{
			string.buffer[i] = (short) ndrBuffer.dec_ndr_short();
		}
	}
}
