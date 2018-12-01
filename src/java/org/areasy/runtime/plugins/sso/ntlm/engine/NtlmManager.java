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

import org.areasy.common.data.StringUtility;
import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;
import jcifs.ntlmssp.NtlmFlags;
import jcifs.ntlmssp.Type1Message;
import jcifs.ntlmssp.Type2Message;
import jcifs.ntlmssp.Type3Message;
import jcifs.util.Encdec;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class NtlmManager
{
	/** Logger instance */
	private static Logger logger = LoggerFactory.getLog(Netlogon.class);

	private static final int NTLMSSP_NEGOTIATE_EXTENDED_SESSION_SECURITY = 0x00080000;

	public static final String BLANK = "";
	public static final String DOLLAR = "$";
	public static final String AT = "@";

	private String domain;
	private String domainController;
	private String domainControllerName;
	private Netlogon netlogon;
	private NtlmServiceAccount ntlmServiceAccount;

	public NtlmManager(String domain, String domainController, String domainControllerName, String serviceAccount, String servicePassword)
	{
		setConfiguration(domain, domainController, domainControllerName, serviceAccount, servicePassword);
	}

	public NtlmUserAccount authenticate(byte[] material, byte[] serverChallenge) throws IOException, NoSuchAlgorithmException, NtlmLogonException
	{
		Type3Message type3Message = new Type3Message(material);

		if (type3Message.getFlag(NTLMSSP_NEGOTIATE_EXTENDED_SESSION_SECURITY) && (type3Message.getNTResponse().length == 24))
		{
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");

			byte[] bytes = new byte[16];

			System.arraycopy(serverChallenge, 0, bytes, 0, 8);
			System.arraycopy(type3Message.getLMResponse(), 0, bytes, 8, 8);

			messageDigest.update(bytes);
			serverChallenge = messageDigest.digest();
		}

		return netlogon.logon(StringUtility.isNotEmpty(type3Message.getDomain()) ? type3Message.getDomain() : getDomain(),
				type3Message.getUser(), type3Message.getWorkstation(), serverChallenge,
				type3Message.getNTResponse(), type3Message.getLMResponse());
	}

	public String[] getM3Details(byte[] material)
	{
		try
		{
			String data[] = new String[3];
			Type3Message type3Message = new Type3Message(material);

			data[0] = new String(type3Message.getUser());
			data[1] = new String(type3Message.getWorkstation());
			data[2] = new String(type3Message.getDomain() != null ? type3Message.getDomain() : getDomain());

			return data;
		}
		catch(Throwable throwable)
		{
			return null;
		}
	}

	public String getDomain()
	{
		return domain;
	}

	public String getDomainController()
	{
		return domainController;
	}

	public String getDomainControllerName()
	{
		return domainControllerName;
	}

	public String getServiceAccount()
	{
		return ntlmServiceAccount.getAccount();
	}

	public String getServicePassword()
	{
		return ntlmServiceAccount.getPassword();
	}

	public byte[] negotiate(byte[] material, byte[] serverChallenge) throws IOException
	{
		logger.debug("Run NTLM negotiation getting message type 1 and 2");
		Type1Message type1Message = new Type1Message(material);
		Type2Message type2Message = new Type2Message(type1Message.getFlags(), serverChallenge, domain);

		if (type2Message.getFlag(NTLMSSP_NEGOTIATE_EXTENDED_SESSION_SECURITY))
		{
			type2Message.setFlag(NtlmFlags.NTLMSSP_NEGOTIATE_LM_KEY, false);
			type2Message.setFlag(NtlmFlags.NTLMSSP_NEGOTIATE_TARGET_INFO, true);
			type2Message.setTargetInformation(getTargetInformation());
		}

		return type2Message.toByteArray();
	}

	public void setConfiguration(String domain, String domainController, String domainControllerName, String serviceAccount, String servicePassword)
	{
		this.domain = domain;
		this.domainController = domainController;
		this.domainControllerName = domainControllerName;
		this.ntlmServiceAccount = new NtlmServiceAccount(serviceAccount, servicePassword);

		this.netlogon = new Netlogon();
		this.netlogon.setConfiguration(getDomainController(), getDomainControllerName(), ntlmServiceAccount);
	}

	protected byte[] getAVPairBytes(int avId, String value) throws UnsupportedEncodingException
	{
		byte[] valueBytes = value.getBytes("UTF-16LE");
		byte[] avPairBytes = new byte[4 + valueBytes.length];

		Encdec.enc_uint16le((short) avId, avPairBytes, 0);
		Encdec.enc_uint16le((short) valueBytes.length, avPairBytes, 2);
		System.arraycopy(valueBytes, 0, avPairBytes, 4, valueBytes.length);

		return avPairBytes;
	}

	protected byte[] getTargetInformation() throws UnsupportedEncodingException
	{
		byte[] computerName = getAVPairBytes(1, ntlmServiceAccount.getComputerName());
		byte[] domainName = getAVPairBytes(2, domain);

		byte[] targetInformation = NtlmManager.append(computerName, domainName);
		byte[] eol = getAVPairBytes(0, NtlmManager.BLANK);

		targetInformation = NtlmManager.append(targetInformation, eol);

		return targetInformation;
	}

	public static byte[] append(byte[]... arrays)
	{
		int length = 0;

		for (byte[] array : arrays)
		{
			length += array.length;
		}

		byte[] newArray = new byte[length];

		int previousLength = 0;

		for (byte[] array : arrays)
		{
			System.arraycopy(array, 0, newArray, previousLength, array.length);
			previousLength += array.length;
		}

		return newArray;
	}
}