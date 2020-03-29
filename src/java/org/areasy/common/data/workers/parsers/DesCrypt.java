package org.areasy.common.data.workers.parsers;

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

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;

/**
 * General library to encrypt and decrypt text credentials using DES algorithm.
 */
public class DesCrypt
{
	static final String SECRET = "ieywuHDKOEL329fuiqe9378y8hildsi9038HFSDJ2uhfieaso";

	public static String passwordEncrypt(String plaintext)
	{
		try
		{
			byte[] secret = (SECRET.hashCode() + "").substring(0, 8).getBytes();
			Cipher des = Cipher.getInstance("DES");
			des.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(secret, "DES"));
			byte[] ciphertext = des.doFinal(plaintext.getBytes());

			return Base64.encode2(ciphertext);
		}
		catch (GeneralSecurityException e)
		{
			throw new RuntimeException(e);
		}
	}

	public static String passwordDecrypt(String ciphertext)
	{
		try
		{
			byte[] secret = (SECRET.hashCode() + "").substring(0, 8).getBytes();
			Cipher des = Cipher.getInstance("DES");
			des.init(Cipher.DECRYPT_MODE, new SecretKeySpec(secret, "DES"));

			byte[] plaintext = des.doFinal(Base64.decode2(ciphertext));

			return new String(plaintext);
		}
		catch (GeneralSecurityException e)
		{
			throw new RuntimeException(e);
		}
	}

	/**
	 * Usage:
	 * -encrypt plaintext
	 * -decrypt crypted_text
	 */
	public static void main(String args[])
	{
		if (args.length < 2)
		{
			System.out.println("Usage - org.areasy.common.data.workers.parsers.DesCrypt [-encrypt|-decrypt] <text>");
			return;
		}

		if (args[0].equals("-encrypt") || args[0].equals("-e"))
		{
			System.out.println("des > " + passwordEncrypt(args[1]));
		}
		else if (args[0].equals("-decrypt") || args[0].equals("-d"))
		{
			System.out.println("des > " + passwordDecrypt(args[1]));
		}
	}
}
