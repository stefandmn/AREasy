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

import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;

/**
 * Credentials.
 * The Credential class represents an abstract mechanism for checking
 * authentication credentials.  A credential instance either represents a
 * secret, or some data that could only be derived from knowing the secret.
 * <p/>
 * Often a Credential is related to a Password via a one way algorithm, so
 * while a Password itself is a Credential, a UnixCrypt or MD5 digest of a
 * a password is only a credential that can be checked against the password.
 * <p/>
 * This class includes an implementation for unix Crypt an MD5 digest.
 *
 * @version $Id: Credential.java,v 1.2 2008/05/14 09:32:36 swd\stefan.damian Exp $
 */
public abstract class Credential
{
	protected static Logger log = LoggerFactory.getLog(Credential.class);

	/**
	 * Check a credential
	 *
	 * @param credential The credential to check against. This may either be
	 *                    another Credential object, a Password object or a String which is
	 *                    interpreted by this credential.
	 * @return True if the credentials indicated that the shared secret is
	 *         known to both this Credential and the passed credential.
	 */
	public abstract boolean match(String credential);

	/**
	 * Encode credential source and return encoded credentials
	 * @return encoded credentials.
	 */
	public abstract String encode();

	/**
	 * Decode credential source and return original credential source.
	 * @return original credential source.
	 */
	public abstract String decode();

	/**
	 * Get a credential from a String.
	 * If the credential String starts with a known Credential type (eg
	 * "CRYPT:" or "MD5:" ) then a Credential of that type is returned. Else the
	 * credential is assumed to be a Password.
	 *
	 * @param credential String representation of the credential
	 * @return A Credential or Password instance.
	 */
	public static Credential getCredential(String credential)
	{
		if (credential.startsWith(CryptCredential.TYPE)) return new CryptCredential(credential);
			else if (credential.startsWith(MD5Credential.TYPE)) return new MD5Credential(credential);
				else if (credential.startsWith(ObfuscateCredential.TYPE)) return new ObfuscateCredential(credential);
					else if (credential.startsWith(DESCredential.TYPE)) return new DESCredential(credential);
						else return new SimpleCredential(credential);
	}

	/**
	 * Get a credential from a String.
	 * If the credential String starts with a known Credential type (eg
	 * "CRYPT:" or "MD5:" ) then a Credential of that type is returned. Else the
	 * credential is assumed to be a DES Credential entity.
	 *
	 * @param credential String representation of the credential
     * @param type credential type string representation of the output
	 * @return A Credential or Password instance.
	 */
	public static Credential getCredential(String credential, String type)
	{
		if (type != null)
		{
			if(type.equals(CryptCredential.TYPE)) return new CryptCredential(credential);
				else if(type.equals(MD5Credential.TYPE)) return new MD5Credential(credential);
					else if(type.equals(ObfuscateCredential.TYPE)) return new ObfuscateCredential(credential);
						else return new DESCredential(credential);

		}
		else
		{
			if (credential.startsWith(CryptCredential.TYPE)) return new CryptCredential(credential);
				else if (credential.startsWith(MD5Credential.TYPE)) return new MD5Credential(credential);
					else if (credential.startsWith(ObfuscateCredential.TYPE)) return new ObfuscateCredential(credential);
						else return new DESCredential(credential);
		}
	}

	/**
	 * Utility to generate password credentials for each implemented type.
	 * @param args input parameters
	 */
	public static void main(String[] args)
	{
		if (args == null || args.length < 2 || (!args[0].equals("-encrypt") && !args[0].equals("-e") && !args[0].equals("-decrypt") && !args[0].equals("-d")))
		{
			System.out.println("Usage - java com.snt.common.data.type.credential.Credential [-e|-d] <password>");
			System.exit(1);
		}

		System.out.println("Input: [" + args[1] +"]\n");

		if (args[0].equals("-encrypt") || args[0].equals("-e"))
		{
			System.out.println("Output 1 : [" + ObfuscateCredential.obfuscate(args[1]) + "]");
			System.out.println("Output 2 : [" + DESCredential.encrypt(args[1]) + "]");
			System.out.println("Output 3 : [" + MD5Credential.digest(args[1]) + "]");

			if(args.length >= 3) System.out.println("Output 4 : [" + CryptCredential.crypt(args[1], args[2]) + "]");
				else System.out.println("Output 4 : [" + CryptCredential.crypt(args[1], args[1]) + "]");
		}
		else if (args[0].equals("-decrypt") || args[0].equals("-d"))
		{
			Credential credential = Credential.getCredential(args[1]);
			System.out.println("Output: [" + credential.decode() + "]");
		}

		System.out.println();
	}
}
