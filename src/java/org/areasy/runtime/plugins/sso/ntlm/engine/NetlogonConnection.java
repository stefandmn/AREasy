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

import jcifs.dcerpc.DcerpcHandle;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.util.DES;
import jcifs.util.Encdec;
import jcifs.util.HMACT64;
import jcifs.util.MD4;
import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

public class NetlogonConnection
{
	/** Logger instance */
	private static Logger logger = LoggerFactory.getLog(NetlogonConnection.class);

	private byte[] clientCredential;
	private DcerpcHandle dcerpcHandle;
	private byte[] sessionKey;

	private static int negotiateFlags = 0x600FFFFF;

	public NetlogonAuthenticator computeNetlogonAuthenticator()
	{
		int timestamp = (int) System.currentTimeMillis();
		int input = Encdec.dec_uint32le(clientCredential, 0) + timestamp;

		Encdec.enc_uint32le(input, clientCredential, 0);
		byte[] credential = computeNetlogonCredential(clientCredential, sessionKey);

		return new NetlogonAuthenticator(credential, timestamp);
	}

	public void connect(String domainController, String domainControllerName, NtlmServiceAccount ntlmServiceAccount, SecureRandom secureRandom) throws IOException, NtlmLogonException, NoSuchAlgorithmException
	{
		NtlmPasswordAuthentication ntlmPasswordAuthentication = new NtlmPasswordAuthentication(null, ntlmServiceAccount.getAccount(), ntlmServiceAccount.getPassword());
		String endpoint = "ncacn_np:" + domainController + "[\\PIPE\\NETLOGON]";

		DcerpcHandle dcerpcHandle = DcerpcHandle.getHandle(endpoint, ntlmPasswordAuthentication);
		setDcerpcHandle(dcerpcHandle);

		dcerpcHandle.bind();
		logger.debug("Netlogon binded connection: " + dcerpcHandle);

		byte[] clientChallenge = new byte[8];
		secureRandom.nextBytes(clientChallenge);

		NetrServerReqChallenge netrServerReqChallenge = new NetrServerReqChallenge(domainControllerName, ntlmServiceAccount.getComputerName(), clientChallenge, new byte[8]);
		dcerpcHandle.sendrecv(netrServerReqChallenge);

		MD4 md4 = new MD4();
		md4.update(ntlmServiceAccount.getPassword().getBytes("UTF-16LE"));

		byte[] sessionKey = computeSessionKey(md4.digest(), clientChallenge, netrServerReqChallenge.getServerChallenge());
		byte[] clientCredential = computeNetlogonCredential(clientChallenge, sessionKey);

		NetrServerAuthenticate3 netrServerAuthenticate3 = new NetrServerAuthenticate3(domainControllerName, ntlmServiceAccount.getAccountName(), 2, ntlmServiceAccount.getComputerName(), clientCredential, new byte[8], negotiateFlags);
		dcerpcHandle.sendrecv(netrServerAuthenticate3);

		byte[] serverCredential = computeNetlogonCredential(netrServerReqChallenge.getServerChallenge(), sessionKey);

		if (!Arrays.equals(serverCredential, netrServerAuthenticate3.getServerCredential())) throw new NtlmLogonException("Session key negotiation failed");

		this.clientCredential = clientCredential;
		this.sessionKey = sessionKey;
	}

	public void disconnect() throws IOException
	{
		if (dcerpcHandle != null)
		{
			dcerpcHandle.close();
		}
	}

	public byte[] getClientCredential()
	{
		return clientCredential;
	}

	public DcerpcHandle getDcerpcHandle()
	{
		return dcerpcHandle;
	}

	public byte[] getSessionKey()
	{
		return sessionKey;
	}

	public void setDcerpcHandle(DcerpcHandle dcerpcHandle)
	{
		this.dcerpcHandle = dcerpcHandle;
	}

	protected byte[] computeNetlogonCredential(byte[] input, byte[] sessionKey)
	{
		byte[] k1 = new byte[7];
		byte[] k2 = new byte[7];

		System.arraycopy(sessionKey, 0, k1, 0, 7);
		System.arraycopy(sessionKey, 7, k2, 0, 7);

		DES k3 = new DES(k1);
		DES k4 = new DES(k2);

		byte[] output1 = new byte[8];
		byte[] output2 = new byte[8];

		k3.encrypt(input, output1);
		k4.encrypt(output1, output2);

		return output2;
	}

	protected byte[] computeSessionKey(byte[] sharedSecret, byte[] clientChallenge, byte[] serverChallenge) throws NoSuchAlgorithmException
	{
		MessageDigest messageDigest = MessageDigest.getInstance("MD5");

		byte[] zeroes = {0, 0, 0, 0};

		messageDigest.update(zeroes, 0, 4);
		messageDigest.update(clientChallenge, 0, 8);
		messageDigest.update(serverChallenge, 0, 8);

		HMACT64 hmact64 = new HMACT64(sharedSecret);

		hmact64.update(messageDigest.digest());

		return hmact64.digest();
	}
}
