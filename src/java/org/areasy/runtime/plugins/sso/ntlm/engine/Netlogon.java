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

import jcifs.dcerpc.DcerpcBinding;
import jcifs.dcerpc.DcerpcHandle;
import jcifs.dcerpc.UnicodeString;
import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class Netlogon
{
	static
	{
		DcerpcBinding.addInterface("netlogon", "12345678-1234-abcd-ef00-01234567cffb:1.0");
	}

	/** Logger instance */
	private static Logger logger = LoggerFactory.getLog(Netlogon.class);

	private String domainController;
	private String domainControllerName;
	private NtlmServiceAccount ntlmServiceAccount;
	private SecureRandom secureRandom = new SecureRandom();

	public void setConfiguration(String domainController, String domainControllerName, NtlmServiceAccount ntlmServiceAccount)
	{
		this.domainController = domainController;
		this.domainControllerName = domainControllerName;
		this.ntlmServiceAccount = ntlmServiceAccount;
	}

	public NtlmUserAccount logon(String domain, String userName, String workstation, byte[] serverChallenge, byte[] ntResponse, byte[] lmResponse) throws NtlmLogonException
	{
		NetlogonConnection netlogonConnection = new NetlogonConnection();
		logger.debug("Netlogon connectivity: [Domain = " + domain + ", User = " + userName + ", Workstation = " + workstation + ", Domain Controller = " + domainController + ", Domain Controller Name = " + domainControllerName + ", Service Account = " + ntlmServiceAccount + "]");
		
		try
		{
			netlogonConnection.connect(domainController, domainControllerName, ntlmServiceAccount, secureRandom);

			NetlogonAuthenticator netlogonAuthenticator = netlogonConnection.computeNetlogonAuthenticator();
			NetlogonIdentityInfo netlogonIdentityInfo = new NetlogonIdentityInfo(domain, 0x00000820, 0, 0, userName, workstation);
			NetlogonNetworkInfo netlogonNetworkInfo = new NetlogonNetworkInfo(netlogonIdentityInfo, serverChallenge, ntResponse, lmResponse);
			NetrLogonSamLogon netrLogonSamLogon = new NetrLogonSamLogon(domainControllerName, ntlmServiceAccount.getComputerName(), netlogonAuthenticator, new NetlogonAuthenticator(), 2, netlogonNetworkInfo, 2, new NetlogonValidationSamInfo(), 0);

			DcerpcHandle dcerpcHandle = netlogonConnection.getDcerpcHandle();
			dcerpcHandle.sendrecv(netrLogonSamLogon);

			if (netrLogonSamLogon.getStatus() == 0)
			{
				NetlogonValidationSamInfo netlogonValidationSamInfo = netrLogonSamLogon.getNetlogonValidationSamInfo();
				UnicodeString name = new UnicodeString(netlogonValidationSamInfo.getEffectiveName(), false);

				return new NtlmUserAccount(name.toString());
			}
			else throw new NtlmLogonException("Unable to authenticate due to status " + netrLogonSamLogon.getStatus());
		}
		catch (NoSuchAlgorithmException e)
		{
			throw new NtlmLogonException("Unable to authenticate due to invalid encryption algorithm", e);
		}
		catch (IOException e)
		{
			throw new NtlmLogonException("Unable to authenticate due to communication failure with server", e);
		}
		finally
		{
			try
			{
				netlogonConnection.disconnect();
			}
			catch (Exception e)
			{
				logger.error("Unable to disconnect Netlogon connection", e);
			}
		}
	}
}
