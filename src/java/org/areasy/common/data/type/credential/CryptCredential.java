package org.areasy.common.data.type.credential;

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

import org.areasy.common.data.workers.parsers.UnixCrypt;

/**
 * Unix Crypt credentials
 */
public class CryptCredential extends Credential
{
	/** Credential type */
	public static final String TYPE = "CRYPT:";

	/** Credential source */
	private String credential = null;

	protected CryptCredential(String credential)
	{
		this.credential = credential.startsWith(TYPE) ? credential.substring(TYPE.length()) : credential;
	}

	/**
	 * Encode credential source and return encoded credentials
	 * @return encoded credentials.
	 */
	public String encode()
	{
		return crypt(this.credential, this.credential);
	}

	/**
	 * Decode credential source and return original credential source.
	 * <p>
	 * Unsupported method. This algorithm does not support
	 * </p>
	 *
	 * @return original credential source.
	 */
	public String decode()
	{
		throw new UnsupportedOperationException("Unix crypt credential doesn't support decode function.");
	}

	public boolean match(String credential)
	{
		String crypt = crypt(credential, credential);
		crypt = crypt.startsWith(TYPE) ? crypt.substring(TYPE.length()) : crypt;

		return this.credential.equals(crypt);
	}

	public static String crypt(String pw, String settings)
	{
		return TYPE + UnixCrypt.crypt(pw, settings);
	}
}

