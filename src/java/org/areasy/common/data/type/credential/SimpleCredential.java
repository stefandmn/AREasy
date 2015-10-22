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

public class SimpleCredential extends Credential
{
	private String credential = null;

	/**
	 * Constructor.
	 *
	 * @param credential The String password.
	 */
	protected SimpleCredential(String credential)
	{
		this.credential = credential;
	}

	/**
	 * Check a credential
	 *
	 * @param credential The credential to check against. This may either be
	 *                    another Credential object, a Password object or a String which is
	 *                    interpreted by this credential.
	 * @return True if the credentials indicated that the shared secret is
	 *         known to both this Credential and the passed credential.
	 */
	public boolean match(String credential)
	{
		return this.credential.equals(credential);
	}

	/**
	 * Encode credential source and return encoded credentials
	 * @return encoded credentials.
	 */
	public String encode()
	{
		return this.credential;
	}

	/**
	 * Decode credential source and return original credential source.
	 * @return original credential source.
	 */
	public String decode()
	{
		return this.credential;
	}
}
