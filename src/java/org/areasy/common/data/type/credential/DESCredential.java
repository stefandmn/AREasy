package org.areasy.common.data.type.credential;

/*
 * Copyright (c) 2007-2015 AREasy Runtime
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

import org.areasy.common.data.workers.parsers.DesCrypt;

/**
 * DES Crypt credentials
 */
public class DESCredential extends Credential
{
	public static final String TYPE = "DES:";

	private String credential = null;

	/**
	 * Constructor.
	 *
	 * @param credential The String password.
	 */
	protected DESCredential(String credential)
	{
		this.credential = credential.startsWith(TYPE) ? credential.substring(TYPE.length()) : credential;
	}

	public boolean match(String credential)
	{
		String des = DesCrypt.passwordEncrypt(credential);
		des = credential.startsWith(TYPE) ? TYPE + des : des;

		return this.credential.equals(des);
	}

	/**
	 * Encode credential source and return encoded credentials
	 * @return encoded credentials.
	 */
	public String encode()
	{
		return encrypt(this.credential);
	}

	/**
	 * Decode credential source and return original credential source.
	 * @return original credential source.
	 */
	public String decode()
	{
		return DesCrypt.passwordDecrypt(this.credential);
	}

	public static String encrypt(String text)
	{
		return TYPE + DesCrypt.passwordEncrypt(text);
	}
}
