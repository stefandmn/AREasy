package org.areasy.common.data;

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

import java.util.Random;

/**
 * <p>Operations for random <code>String</code>s.</p>
 *
 * @version $Id: RandomStringUtility.java,v 1.2 2008/05/14 09:32:29 swd\stefan.damian Exp $
 */
public class RandomStringUtility
{

	/**
	 * <p>Random object used by random method. This has to be not local
	 * to the random method so as to not return the same value in the
	 * same millisecond.</p>
	 */
	private static final Random RANDOM = new Random();

	/**
	 * <p><code>RandomStringUtility</code> instances should NOT be constructed in
	 * standard programming. Instead, the class should be used as
	 * <code>RandomStringUtility.random(5);</code>.</p>
	 * <p/>
	 * <p>This constructor is public to permit tools that require a JavaBean instance
	 * to operate.</p>
	 */
	public RandomStringUtility()
	{
		//nothing to do here
	}

	// Random
	/**
	 * <p>Creates a random string whose length is the number of characters
	 * specified.</p>
	 * <p/>
	 * <p>Characters will be chosen from the set of all characters.</p>
	 *
	 * @param count the length of random string to create
	 * @return the random string
	 */
	public static String random(int count)
	{
		return random(count, false, false);
	}

	/**
	 * <p>Creates a random string whose length is the number of characters
	 * specified.</p>
	 * <p/>
	 * <p>Characters will be chosen from the set of characters whose
	 * ASCII value is between <code>32</code> and <code>126</code> (inclusive).</p>
	 *
	 * @param count the length of random string to create
	 * @return the random string
	 */
	public static String randomAscii(int count)
	{
		return random(count, 32, 127, false, false);
	}

	/**
	 * <p>Creates a random string whose length is the number of characters
	 * specified.</p>
	 * <p/>
	 * <p>Characters will be chosen from the set of alphabetic
	 * characters.</p>
	 *
	 * @param count the length of random string to create
	 * @return the random string
	 */
	public static String randomAlphabetic(int count)
	{
		return random(count, true, false);
	}

	/**
	 * <p>Creates a random string whose length is the number of characters
	 * specified.</p>
	 * <p/>
	 * <p>Characters will be chosen from the set of alpha-numeric
	 * characters.</p>
	 *
	 * @param count the length of random string to create
	 * @return the random string
	 */
	public static String randomAlphanumeric(int count)
	{
		return random(count, true, true);
	}

	/**
	 * <p>Creates a random string whose length is the number of characters
	 * specified.</p>
	 * <p/>
	 * <p>Characters will be chosen from the set of numeric
	 * characters.</p>
	 *
	 * @param count the length of random string to create
	 * @return the random string
	 */
	public static String randomNumeric(int count)
	{
		return random(count, false, true);
	}

	/**
	 * <p>Creates a random string whose length is the number of characters
	 * specified.</p>
	 * <p/>
	 * <p>Characters will be chosen from the set of alpha-numeric
	 * characters as indicated by the arguments.</p>
	 *
	 * @param count   the length of random string to create
	 * @param letters if <code>true</code>, generated string will include
	 *                alphabetic characters
	 * @param numbers if <code>true</code>, generated string will include
	 *                numeric characters
	 * @return the random string
	 */
	public static String random(int count, boolean letters, boolean numbers)
	{
		return random(count, 0, 0, letters, numbers);
	}

	/**
	 * <p>Creates a random string whose length is the number of characters
	 * specified.</p>
	 * <p/>
	 * <p>Characters will be chosen from the set of alpha-numeric
	 * characters as indicated by the arguments.</p>
	 *
	 * @param count   the length of random string to create
	 * @param start   the position in set of chars to start at
	 * @param end     the position in set of chars to end before
	 * @param letters if <code>true</code>, generated string will include
	 *                alphabetic characters
	 * @param numbers if <code>true</code>, generated string will include
	 *                numeric characters
	 * @return the random string
	 */
	public static String random(int count, int start, int end, boolean letters, boolean numbers)
	{
		return random(count, start, end, letters, numbers, null, RANDOM);
	}

	/**
	 * <p>Creates a random string based on a variety of options, using
	 * default source of randomness.</p>
	 * <p/>
	 * <p>This method has exactly the same semantics as
	 * {@link #random(int,int,int,boolean,boolean,char[],Random)}, but
	 * instead of using an externally supplied source of randomness, it uses
	 * the internal static {@link Random} instance.</p>
	 *
	 * @param count   the length of random string to create
	 * @param start   the position in set of chars to start at
	 * @param end     the position in set of chars to end before
	 * @param letters only allow letters?
	 * @param numbers only allow numbers?
	 * @param chars   the set of chars to choose randoms from.
	 *                If <code>null</code>, then it will use the set of all chars.
	 * @return the random string
	 * @throws ArrayIndexOutOfBoundsException if there are not
	 *                                        <code>(end - start) + 1</code> characters in the set array.
	 */
	public static String random(int count, int start, int end, boolean letters, boolean numbers, char[] chars)
	{
		return random(count, start, end, letters, numbers, chars, RANDOM);
	}

	/**
	 * <p>Creates a random string based on a variety of options, using
	 * supplied source of randomness.</p>
	 * <p/>
	 * <p>If start and end are both <code>0</code>, start and end are set
	 * to <code>' '</code> and <code>'z'</code>, the ASCII printable
	 * characters, will be used, unless letters and numbers are both
	 * <code>false</code>, in which case, start and end are set to
	 * <code>0</code> and <code>Integer.MAX_VALUE</code>.
	 * <p/>
	 * <p>If set is not <code>null</code>, characters between start and
	 * end are chosen.</p>
	 * <p/>
	 * <p>This method accepts a user-supplied {@link Random}
	 * instance to use as a source of randomness. By seeding a single
	 * {@link Random} instance with a fixed seed and using it for each call,
	 * the same random sequence of strings can be generated repeatedly
	 * and predictably.</p>
	 *
	 * @param count   the length of random string to create
	 * @param start   the position in set of chars to start at
	 * @param end     the position in set of chars to end before
	 * @param letters only allow letters?
	 * @param numbers only allow numbers?
	 * @param chars   the set of chars to choose randoms from.
	 *                If <code>null</code>, then it will use the set of all chars.
	 * @param random  a source of randomness.
	 * @return the random string
	 * @throws ArrayIndexOutOfBoundsException if there are not
	 *                                        <code>(end - start) + 1</code> characters in the set array.
	 * @throws IllegalArgumentException       if <code>count</code> &lt; 0.
	 */
	public static String random(int count, int start, int end, boolean letters, boolean numbers,
								char[] chars, Random random)
	{
		if (count == 0)
		{
			return "";
		}
		else if (count < 0)
		{
			throw new IllegalArgumentException("Requested random string length " + count + " is less than 0.");
		}
		if ((start == 0) && (end == 0))
		{
			end = 'z' + 1;
			start = ' ';
			if (!letters && !numbers)
			{
				start = 0;
				end = Integer.MAX_VALUE;
			}
		}

		StringBuffer buffer = new StringBuffer();
		int gap = end - start;

		while (count-- != 0)
		{
			char ch;
			if (chars == null)
			{
				ch = (char) (random.nextInt(gap) + start);
			}
			else
			{
				ch = chars[random.nextInt(gap) + start];
			}
			if ((letters && numbers && Character.isLetterOrDigit(ch))
					|| (letters && Character.isLetter(ch))
					|| (numbers && Character.isDigit(ch))
					|| (!letters && !numbers))
			{
				buffer.append(ch);
			}
			else
			{
				count++;
			}
		}
		return buffer.toString();
	}

	/**
	 * <p>Creates a random string whose length is the number of characters
	 * specified.</p>
	 * <p/>
	 * <p>Characters will be chosen from the set of characters
	 * specified.</p>
	 *
	 * @param count the length of random string to create
	 * @param chars the String containing the set of characters to use,
	 *              may be null
	 * @return the random string
	 * @throws IllegalArgumentException if <code>count</code> &lt; 0.
	 */
	public static String random(int count, String chars)
	{
		if (chars == null)
		{
			return random(count, 0, 0, false, false, null, RANDOM);
		}
		return random(count, chars.toCharArray());
	}

	/**
	 * <p>Creates a random string whose length is the number of characters
	 * specified.</p>
	 * <p/>
	 * <p>Characters will be chosen from the set of characters specified.</p>
	 *
	 * @param count the length of random string to create
	 * @param chars the character array containing the set of characters to use,
	 *              may be null
	 * @return the random string
	 * @throws IllegalArgumentException if <code>count</code> &lt; 0.
	 */
	public static String random(int count, char[] chars)
	{
		if (chars == null)
		{
			return random(count, 0, 0, false, false, null, RANDOM);
		}
		return random(count, 0, chars.length, false, false, chars, RANDOM);
	}

}
