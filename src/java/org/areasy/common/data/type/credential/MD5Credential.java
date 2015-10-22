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

import org.areasy.common.data.TypeUtility;

import java.security.MessageDigest;

/**
 * MD5 credentials.
 */
public class MD5Credential extends Credential
{
	public static final String TYPE = "MD5:";
	private static MessageDigest md;

	private String credential = null;

	/**
	 * Default and protected credentials for MD5 credentials
	 * @param credential
	 */
	protected MD5Credential(String credential)
	{
		this.credential = credential.startsWith(TYPE) ? credential.substring(TYPE.length()) : credential;
	}

	public boolean match(String credential)
	{
		String crypt = digest(credential);
		crypt = crypt.startsWith(TYPE) ? crypt.substring(TYPE.length()) : crypt;

		byte[] digest1 = TypeUtility.parseBytes(this.credential, 16);
		byte[] digest2 = TypeUtility.parseBytes(crypt, 16);

		if (digest1.length != digest2.length) return false;

		for (int i = 0; i < digest1.length; i++)
		{
			if (digest1[i] != digest2[i]) return false;
		}

		return true;
	}

	/**
	 * Encode credential source and return encoded credentials
	 * @return encoded credentials.
	 */
	public String encode()
	{
		return digest(this.credential);
	}

	/**
	 * Decode credential source and return original credential source.
	 * @return original credential source.
	 */
	public String decode()
	{
		throw new UnsupportedOperationException("MD5 credential doesn't support decode function.");
	}

	public static String digest(String password)
	{
		try
		{
			byte[] digest;

			synchronized (TYPE)
			{
				if (md == null)
				{
					try
					{
						md = MessageDigest.getInstance("MD5");
					}
					catch (Exception e)
					{
						throw new RuntimeException(e);
					}
				}

				md.reset();
				md.update(password.getBytes("UTF-8"));

				digest = md.digest();
			}

			return TYPE + TypeUtility.toString(digest, 16);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}
}
