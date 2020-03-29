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

/**
 * <p>Operations on Strings that contain words.</p>
 * <p/>
 * <p>This class tries to handle <code>null</code> input gracefully.
 * An exception will not be thrown for a <code>null</code> input.
 * Each method documents its behaviour in more detail.</p>
 *
 * @version $Id: WordUtility.java,v 1.3 2008/05/14 09:32:29 swd\stefan.damian Exp $
 */
public class WordUtility
{
	/**
	 * <p><code>WordUtility</code> instances should NOT be constructed in
	 * standard programming. Instead, the class should be used as
	 * <code>WordUtility.wrap("foo bar", 20);</code>.</p>
	 * <p/>
	 * <p>This constructor is public to permit tools that require a JavaBean
	 * instance to operate.</p>
	 */
	public WordUtility()
	{
		//nothing to do here
	}

	/**
	 * <p>Wraps a single line of text, identifying words by <code>' '</code>.</p>
	 * <p/>
	 * <p>New lines will be separated by the system property line separator.
	 * Very long words, such as URLs will <i>not</i> be wrapped.</p>
	 * <p/>
	 * <p>Leading spaces on a new line are stripped.
	 * Trailing spaces are not stripped.</p>
	 * <p/>
	 * <pre>
	 * WordUtility.wrap(null, *) = null
	 * WordUtility.wrap("", *) = ""
	 * </pre>
	 *
	 * @param str        the String to be word wrapped, may be null
	 * @param wrapLength the column to wrap the words at, less than 1 is treated as 1
	 * @return a line with newlines inserted, <code>null</code> if null input
	 */
	public static String wrap(String str, int wrapLength)
	{
		return wrap(str, wrapLength, null, false);
	}

	/**
	 * <p>Wraps a single line of text, identifying words by <code>' '</code>.</p>
	 * <p/>
	 * <p>Leading spaces on a new line are stripped.
	 * Trailing spaces are not stripped.</p>
	 * <p/>
	 * <pre>
	 * WordUtility.wrap(null, *, *, *) = null
	 * WordUtility.wrap("", *, *, *) = ""
	 * </pre>
	 *
	 * @param str           the String to be word wrapped, may be null
	 * @param wrapLength    the column to wrap the words at, less than 1 is treated as 1
	 * @param newLineStr    the string to insert for a new line,
	 *                      <code>null</code> uses the system property line separator
	 * @param wrapLongWords true if long words (such as URLs) should be wrapped
	 * @return a line with newlines inserted, <code>null</code> if null input
	 */
	public static String wrap(String str, int wrapLength, String newLineStr, boolean wrapLongWords)
	{
		if (str == null) return null;

		if (newLineStr == null) newLineStr = SystemUtility.LINE_SEPARATOR;

		if (wrapLength < 1) wrapLength = 1;

		int inputLineLength = str.length();
		int offset = 0;

		StringBuffer wrappedLine = new StringBuffer(inputLineLength + 32);

		while ((inputLineLength - offset) > wrapLength)
		{
			if (str.charAt(offset) == ' ')
			{
				offset++;
				continue;
			}

			int spaceToWrapAt = str.lastIndexOf(' ', wrapLength + offset);

			if (spaceToWrapAt >= offset)
			{
				// normal case
				wrappedLine.append(str.substring(offset, spaceToWrapAt));
				wrappedLine.append(newLineStr);
				offset = spaceToWrapAt + 1;

			}
			else
			{
				// really long word or URL
				if (wrapLongWords)
				{
					// wrap really long word one line at a time
					wrappedLine.append(str.substring(offset, wrapLength + offset));
					wrappedLine.append(newLineStr);
					offset += wrapLength;
				}
				else
				{
					// do not wrap really long word, just extend beyond limit
					spaceToWrapAt = str.indexOf(' ', wrapLength + offset);
					if (spaceToWrapAt >= 0)
					{
						wrappedLine.append(str.substring(offset, spaceToWrapAt));
						wrappedLine.append(newLineStr);
						offset = spaceToWrapAt + 1;
					}
					else
					{
						wrappedLine.append(str.substring(offset));
						offset = inputLineLength;
					}
				}
			}
		}

		// Whatever is left in line is short enough to just pass through
		wrappedLine.append(str.substring(offset));

		return wrappedLine.toString();
	}

	// Capitalizing
	/**
	 * <p>Capitalizes all the whitespace separated words in a String.
	 * Only the first letter of each word is changed. To convert the
	 * rest of each word to lowercase at the same time,
	 * use {@link #capitalizeFully(String)}.</p>
	 * <p/>
	 * <p>Whitespace is defined by {@link Character#isWhitespace(char)}.
	 * A <code>null</code> input String returns <code>null</code>.
	 * Capitalization uses the unicode title case, normally equivalent to
	 * upper case.</p>
	 * <p/>
	 * <pre>
	 * WordUtility.capitalize(null)        = null
	 * WordUtility.capitalize("")          = ""
	 * WordUtility.capitalize("i am FINE") = "I Am FINE"
	 * </pre>
	 *
	 * @param str the String to capitalize, may be null
	 * @return capitalized String, <code>null</code> if null String input
	 * @see #uncapitalize(String)
	 * @see #capitalizeFully(String)
	 */
	public static String capitalize(String str)
	{
		return capitalize(str, null);
	}

	/**
	 * <p>Capitalizes all the delimiter separated words in a String.
	 * Only the first letter of each word is changed. To convert the
	 * rest of each word to lowercase at the same time,
	 * use {@link #capitalizeFully(String, char[])}.</p>
	 * <p/>
	 * <p>The delimiters represent a set of characters understood to separate words.
	 * The first string character and the first non-delimiter character after a
	 * delimiter will be capitalized. </p>
	 * <p/>
	 * <p>A <code>null</code> input String returns <code>null</code>.
	 * Capitalization uses the unicode title case, normally equivalent to
	 * upper case.</p>
	 * <p/>
	 * <pre>
	 * WordUtility.capitalize(null, *)            = null
	 * WordUtility.capitalize("", *)              = ""
	 * WordUtility.capitalize(*, new char[0])     = *
	 * WordUtility.capitalize("i am fine", null)  = "I Am Fine"
	 * WordUtility.capitalize("i aM.fine", {'.'}) = "I aM.Fine"
	 * </pre>
	 *
	 * @param str        the String to capitalize, may be null
	 * @param delimiters set of characters to determine capitalization, null means whitespace
	 * @return capitalized String, <code>null</code> if null String input
	 * @see #uncapitalize(String)
	 * @see #capitalizeFully(String)
	 */
	public static String capitalize(String str, char[] delimiters)
	{
		if (str == null || str.length() == 0)
		{
			return str;
		}
		int strLen = str.length();
		StringBuffer buffer = new StringBuffer(strLen);

		int delimitersLen = 0;
		if (delimiters != null)
		{
			delimitersLen = delimiters.length;
		}

		boolean capitalizeNext = true;
		for (int i = 0; i < strLen; i++)
		{
			char ch = str.charAt(i);

			boolean isDelimiter = false;
			if (delimiters == null)
			{
				isDelimiter = Character.isWhitespace(ch);
			}
			else
			{
				for (int j = 0; j < delimitersLen; j++)
				{
					if (ch == delimiters[j])
					{
						isDelimiter = true;
						break;
					}
				}
			}

			if (isDelimiter)
			{
				buffer.append(ch);
				capitalizeNext = true;
			}
			else if (capitalizeNext)
			{
				buffer.append(Character.toTitleCase(ch));
				capitalizeNext = false;
			}
			else
			{
				buffer.append(ch);
			}
		}
		return buffer.toString();
	}

	/**
	 * <p>Converts all the whitespace separated words in a String into capitalized words,
	 * that is each word is made up of a titlecase character and then a series of
	 * lowercase characters.  </p>
	 * <p/>
	 * <p>Whitespace is defined by {@link Character#isWhitespace(char)}.
	 * A <code>null</code> input String returns <code>null</code>.
	 * Capitalization uses the unicode title case, normally equivalent to
	 * upper case.</p>
	 * <p/>
	 * <pre>
	 * WordUtility.capitalizeFully(null)        = null
	 * WordUtility.capitalizeFully("")          = ""
	 * WordUtility.capitalizeFully("i am FINE") = "I Am Fine"
	 * </pre>
	 *
	 * @param str the String to capitalize, may be null
	 * @return capitalized String, <code>null</code> if null String input
	 */
	public static String capitalizeFully(String str)
	{
		return capitalizeFully(str, null);
	}

	/**
	 * <p>Converts all the delimiter separated words in a String into capitalized words,
	 * that is each word is made up of a titlecase character and then a series of
	 * lowercase characters. </p>
	 * <p/>
	 * <p>The delimiters represent a set of characters understood to separate words.
	 * The first string character and the first non-delimiter character after a
	 * delimiter will be capitalized. </p>
	 * <p/>
	 * <p>A <code>null</code> input String returns <code>null</code>.
	 * Capitalization uses the unicode title case, normally equivalent to
	 * upper case.</p>
	 * <p/>
	 * <pre>
	 * WordUtility.capitalizeFully(null, *)            = null
	 * WordUtility.capitalizeFully("", *)              = ""
	 * WordUtility.capitalizeFully(*, null)            = *
	 * WordUtility.capitalizeFully(*, new char[0])     = *
	 * WordUtility.capitalizeFully("i aM.fine", {'.'}) = "I am.Fine"
	 * </pre>
	 *
	 * @param str        the String to capitalize, may be null
	 * @param delimiters set of characters to determine capitalization, null means whitespace
	 * @return capitalized String, <code>null</code> if null String input
	 */
	public static String capitalizeFully(String str, char[] delimiters)
	{
		if (str == null || str.length() == 0)
		{
			return str;
		}
		str = str.toLowerCase();
		return capitalize(str, delimiters);
	}

	/**
	 * <p>Uncapitalizes all the whitespace separated words in a String.
	 * Only the first letter of each word is changed.</p>
	 * <p/>
	 * <p>Whitespace is defined by {@link Character#isWhitespace(char)}.
	 * A <code>null</code> input String returns <code>null</code>.</p>
	 * <p/>
	 * <pre>
	 * WordUtility.uncapitalize(null)        = null
	 * WordUtility.uncapitalize("")          = ""
	 * WordUtility.uncapitalize("I Am FINE") = "i am fINE"
	 * </pre>
	 *
	 * @param str the String to uncapitalize, may be null
	 * @return uncapitalized String, <code>null</code> if null String input
	 * @see #capitalize(String)
	 */
	public static String uncapitalize(String str)
	{
		return uncapitalize(str, null);
	}

	/**
	 * <p>Uncapitalizes all the whitespace separated words in a String.
	 * Only the first letter of each word is changed.</p>
	 * <p/>
	 * <p>The delimiters represent a set of characters understood to separate words.
	 * The first string character and the first non-delimiter character after a
	 * delimiter will be uncapitalized. </p>
	 * <p/>
	 * <p>Whitespace is defined by {@link Character#isWhitespace(char)}.
	 * A <code>null</code> input String returns <code>null</code>.</p>
	 * <p/>
	 * <pre>
	 * WordUtility.uncapitalize(null, *)            = null
	 * WordUtility.uncapitalize("", *)              = ""
	 * WordUtility.uncapitalize(*, null)            = *
	 * WordUtility.uncapitalize(*, new char[0])     = *
	 * WordUtility.uncapitalize("I AM.FINE", {'.'}) = "i AM.fINE"
	 * </pre>
	 *
	 * @param str        the String to uncapitalize, may be null
	 * @param delimiters set of characters to determine uncapitalization, null means whitespace
	 * @return uncapitalized String, <code>null</code> if null String input
	 * @see #capitalize(String)
	 */
	public static String uncapitalize(String str, char[] delimiters)
	{
		if (str == null || str.length() == 0)
		{
			return str;
		}
		int strLen = str.length();

		int delimitersLen = 0;
		if (delimiters != null)
		{
			delimitersLen = delimiters.length;
		}

		StringBuffer buffer = new StringBuffer(strLen);
		boolean uncapitalizeNext = true;
		for (int i = 0; i < strLen; i++)
		{
			char ch = str.charAt(i);

			boolean isDelimiter = false;
			if (delimiters == null)
			{
				isDelimiter = Character.isWhitespace(ch);
			}
			else
			{
				for (int j = 0; j < delimitersLen; j++)
				{
					if (ch == delimiters[j])
					{
						isDelimiter = true;
						break;
					}
				}
			}

			if (isDelimiter)
			{
				buffer.append(ch);
				uncapitalizeNext = true;
			}
			else if (uncapitalizeNext)
			{
				buffer.append(Character.toLowerCase(ch));
				uncapitalizeNext = false;
			}
			else
			{
				buffer.append(ch);
			}
		}
		return buffer.toString();
	}

	/**
	 * <p>Swaps the case of a String using a word based algorithm.</p>
	 * <p/>
	 * <ul>
	 * <li>Upper case character converts to Lower case</li>
	 * <li>Title case character converts to Lower case</li>
	 * <li>Lower case character after Whitespace or at start converts to Title case</li>
	 * <li>Other Lower case character converts to Upper case</li>
	 * </ul>
	 * <p/>
	 * <p>Whitespace is defined by {@link Character#isWhitespace(char)}.
	 * A <code>null</code> input String returns <code>null</code>.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.swapCase(null)                 = null
	 * StringUtility.swapCase("")                   = ""
	 * StringUtility.swapCase("The dog has a BONE") = "tHE DOG HAS A bone"
	 * </pre>
	 *
	 * @param str the String to swap case, may be null
	 * @return the changed String, <code>null</code> if null String input
	 */
	public static String swapCase(String str)
	{
		int strLen;
		if (str == null || (strLen = str.length()) == 0) return str;

		StringBuffer buffer = new StringBuffer(strLen);

		boolean whitespace = true;
		char ch = 0;
		char tmp = 0;

		for (int i = 0; i < strLen; i++)
		{
			ch = str.charAt(i);

			if (Character.isUpperCase(ch)) tmp = Character.toLowerCase(ch);
			else if (Character.isTitleCase(ch)) tmp = Character.toLowerCase(ch);
			else if (Character.isLowerCase(ch))
			{
				if (whitespace) tmp = Character.toTitleCase(ch);
					else tmp = Character.toUpperCase(ch);
			}
			else tmp = ch;

			buffer.append(tmp);
			whitespace = Character.isWhitespace(ch);
		}
		return buffer.toString();
	}

}
