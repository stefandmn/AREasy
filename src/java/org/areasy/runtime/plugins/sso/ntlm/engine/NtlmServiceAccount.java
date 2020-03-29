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

import org.areasy.common.data.StringUtility;
import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;

public class NtlmServiceAccount
{
	/** Logger instance */
	private static Logger logger = LoggerFactory.getLog(NtlmServiceAccount.class);

	private String account;
	private String accountName;
	private String computerName;
	private String password;

	public NtlmServiceAccount(String account, String password)
	{
		setAccount(account);
		setPassword(password);
	}

	public String getAccount()
	{
		return account;
	}

	public String getAccountName()
	{
		return accountName;
	}

	public String getComputerName()
	{
		return computerName;
	}

	public String getPassword()
	{
		return password;
	}

	public void setAccount(String account)
	{
		this.account = account;

		if(StringUtility.isNotEmpty(this.account))
		{
			if(this.account.contains(NtlmManager.AT)) accountName = this.account.substring(0, this.account.indexOf(NtlmManager.AT));
			if(this.account.contains(NtlmManager.DOLLAR)) computerName = this.account.substring(0, this.account.indexOf(NtlmManager.DOLLAR));
		}
		else throw new RuntimeException("Configuration account is null");
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public String toString()
	{
		return "[" + accountName + ", " + computerName + ", " + (logger.isTraceEnabled() ? this.password : "********") + "]";
	}
}
