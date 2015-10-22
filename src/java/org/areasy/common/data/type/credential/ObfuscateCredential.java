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

/**
 * Obfuscate credentials.
 *
 * @version $Id: ObfuscateCredential.java,v 1.2 2008/05/14 09:32:36 swd\stefan.damian Exp $
 */
public class ObfuscateCredential extends Credential
{
	public static final String TYPE = "OBF:";

	private String credential = null;

	/**
	 * Constructor.
	 *
	 * @param credential The String password.
	 */
	protected ObfuscateCredential(String credential)
	{
		this.credential = credential.startsWith(TYPE) ? credential.substring(TYPE.length()) : credential;
	}

	public boolean match(String credential)
	{
		String crypt = obfuscate(credential);
		crypt = crypt.startsWith(TYPE) ? crypt.substring(TYPE.length()) : crypt;
		
		return this.credential.equals(crypt);
	}

	/**
	 * Encode credential source and return encoded credentials
	 * @return encoded credentials.
	 */
	public String encode()
	{
		return obfuscate(this.credential);
	}

	/**
	 * Decode credential source and return original credential source.
	 * @return original credential source.
	 */
	public String decode()
	{
		return deobfuscate(this.credential);
	}

	public static String obfuscate(String s)
	{
		StringBuffer buf = new StringBuffer();
		byte[] b = s.getBytes();

		synchronized (buf)
		{
			buf.append(TYPE);

			for (int i = 0; i < b.length; i++)
			{
				byte b1 = b[i];
				byte b2 = b[s.length() - (i + 1)];

				int i1 = (int) b1 + (int) b2 + 127;
				int i2 = (int) b1 - (int) b2 + 127;
				int i0 = i1 * 256 + i2;

				String x = Integer.toString(i0, 36);

				switch (x.length())
				{
					case 1:
						buf.append('0');
					case 2:
						buf.append('0');
					case 3:
						buf.append('0');
					default:
						buf.append(x);
				}
			}

			return buf.toString();
		}
	}

	public static String deobfuscate(String s)
	{
		if (s.startsWith(TYPE)) s = s.substring(4);

		byte[] b = new byte[s.length() / 2];
		int l = 0;

		for (int i = 0; i < s.length(); i += 4)
		{
			String x = s.substring(i, i + 4);
			int i0 = Integer.parseInt(x, 36);
			int i1 = (i0 / 256);
			int i2 = (i0 % 256);

			b[l++] = (byte) ((i1 + i2 - 254) / 2);
		}

		return new String(b, 0, l);
	}
}

