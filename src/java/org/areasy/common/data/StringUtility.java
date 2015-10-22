package org.areasy.common.data;

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

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * <p>Operations on {@link java.lang.String} that are
 * <code>null</code> safe.</p>
 * <p/>
 * <ul>
 * <li><b>IsEmpty/IsBlank</b>
 * - checks if a String contains text</li>
 * <li><b>Trim/Strip</b>
 * - removes leading and trailing whitespace</li>
 * <li><b>Equals</b>
 * - compares two strings null-safe</li>
 * <li><b>IndexOf/LastIndexOf/Contains</b>
 * - null-safe index-of checks
 * <li><b>IndexOfAny/LastIndexOfAny/IndexOfAnyBut/LastIndexOfAnyBut</b>
 * - index-of any of a set of Strings</li>
 * <li><b>ContainsOnly/ContainsNone</b>
 * - does String contains only/none of these characters</li>
 * <li><b>Substring/Left/Right/Mid</b>
 * - null-safe substring extractions</li>
 * <li><b>SubstringBefore/SubstringAfter/SubstringBetween</b>
 * - substring extraction relative to other strings</li>
 * <li><b>Split/Join</b>
 * - splits a String into an array of substrings and vice versa</li>
 * <li><b>Remove/Delete</b>
 * - removes part of a String</li>
 * <li><b>Replace/Overlay</b>
 * - Searches a String and replaces one String with another</li>
 * <li><b>Chomp/Chop</b>
 * - removes the last part of a String</li>
 * <li><b>LeftPad/RightPad/Center/Repeat</b>
 * - pads a String</li>
 * <li><b>UpperCase/LowerCase/SwapCase/Capitalize/Uncapitalize</b>
 * - changes the case of a String</li>
 * <li><b>CountMatches</b>
 * - counts the number of occurrences of one String in another</li>
 * <li><b>IsAlpha/IsNumeric/IsWhitespace/IsAsciiPrintable</b>
 * - checks the characters in a String</li>
 * <li><b>DefaultString</b>
 * - protects against a null input String</li>
 * <li><b>Reverse/ReverseDelimited</b>
 * - reverses a String</li>
 * <li><b>Abbreviate</b>
 * - abbreviates a string using ellipsis</li>
 * <li><b>Difference</b>
 * - compares two Strings and reports on their differences</li>
 * <li><b>LevensteinDistance</b>
 * - the number of changes needed to change one String into another</li>
 * </ul>
 * <p/>
 * <p>The <code>StringUtility</code> class defines certain words related to
 * String handling.</p>
 * <p/>
 * <ul>
 * <li>null - <code>null</code></li>
 * <li>empty - a zero-length string (<code>""</code>)</li>
 * <li>space - the space character (<code>' '</code>, char 32)</li>
 * <li>whitespace - the characters defined by {@link Character#isWhitespace(char)}</li>
 * <li>trim - the characters &lt;= 32 as in {@link String#trim()}</li>
 * </ul>
 * <p/>
 * <p><code>StringUtility</code> handles <code>null</code> input Strings quietly.
 * That is to say that a <code>null</code> input will return <code>null</code>.
 * Where a <code>boolean</code> or <code>int</code> is being returned
 * details vary by method.</p>
 * <p/>
 * <p>A side effect of the <code>null</code> handling is that a
 * <code>NullPointerException</code> should be considered a bug in
 * <code>StringUtility</code> (except for deprecated methods).</p>
 * <p/>
 * <p>Methods in this class give sample code to explain their operation.
 * The symbol <code>*</code> is used to indicate any input including <code>null</code>.</p>
 *
 * @version $Id: StringUtility.java,v 1.9 2008/11/07 14:59:12 swd\stefan.damian Exp $
 * @see java.lang.String
 */
public class StringUtility
{
	/**
	 * The empty String <code>""</code>.
	 */
	public static final String EMPTY = "";

	/**
	 * Represents a failed index search.
	 */
	public static final int INDEX_NOT_FOUND = -1;

	/**
	 * <p>The maximum size to which the padding constant(s) can expand.</p>
	 */
	private static final int PAD_LIMIT = 8192;

	/**
	 * <p>An array of <code>String</code>s used for padding.</p>
	 * <p/>
	 * <p>Used for efficient space padding. The length of each String expands as needed.</p>
	 */
	private static final String[] PADDING = new String[Character.MAX_VALUE];

	static
	{
		// space padding is most common, start with 64 chars
		PADDING[32] = "                                                                ";
	}

	/**
	 * <p><code>StringUtility</code> instances should NOT be constructed in
	 * standard programming. Instead, the class should be used as
	 * <code>StringUtility.trim(" foo ");</code>.</p>
	 * <p/>
	 * <p>This constructor is public to permit tools that require a JavaBean
	 * instance to operate.</p>
	 */
	public StringUtility()
	{
		// no init.
	}

	/**
	 * <p>Checks if a String is empty ("") or null.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.isEmpty(null)      = true
	 * StringUtility.isEmpty("")        = true
	 * StringUtility.isEmpty(" ")       = false
	 * StringUtility.isEmpty("bob")     = false
	 * StringUtility.isEmpty("  bob  ") = false
	 * </pre>
	 * <p/>
	 * <p>NOTE: This method changed in Lang version 2.0.
	 * It no longer trims the String.
	 * That functionality is available in isBlank().</p>
	 *
	 * @param str the String to check, may be null
	 * @return <code>true</code> if the String is empty or null
	 */
	public static boolean isEmpty(String str)
	{
		return str == null || str.length() == 0;
	}

	/**
	 * <p>Checks if a String is not empty ("") and not null.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.isNotEmpty(null)      = false
	 * StringUtility.isNotEmpty("")        = false
	 * StringUtility.isNotEmpty(" ")       = true
	 * StringUtility.isNotEmpty("bob")     = true
	 * StringUtility.isNotEmpty("  bob  ") = true
	 * </pre>
	 *
	 * @param str the String to check, may be null
	 * @return <code>true</code> if the String is not empty and not null
	 */
	public static boolean isNotEmpty(String str)
	{
		return str != null && str.length() > 0;
	}

	/**
	 * Check to see if all the string objects passed
	 * in are empty.
	 *
	 * @param list A list of {@link java.lang.String} objects.
	 * @return Whether all strings are empty.
	 */
	public static boolean allEmpty(List list)
	{
		int size = list.size();

		for (int i = 0; i < size; i++)
		{
			if (list.get(i) != null && list.get(i).toString().length() > 0) return false;
		}

		return true;
	}

	/**
	 * <p>Checks if a String is whitespace, empty ("") or null.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.isBlank(null)      = true
	 * StringUtility.isBlank("")        = true
	 * StringUtility.isBlank(" ")       = true
	 * StringUtility.isBlank("bob")     = false
	 * StringUtility.isBlank("  bob  ") = false
	 * </pre>
	 *
	 * @param str the String to check, may be null
	 * @return <code>true</code> if the String is null, empty or whitespace
	 */
	public static boolean isBlank(String str)
	{
		int strLen;
		if (str == null || (strLen = str.length()) == 0) return true;

		for (int i = 0; i < strLen; i++)
		{
			if ((!Character.isWhitespace(str.charAt(i)))) return false;
		}

		return true;
	}

	/**
	 * <p>Checks if a String is not empty (""), not null and not whitespace only.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.isNotBlank(null)      = false
	 * StringUtility.isNotBlank("")        = false
	 * StringUtility.isNotBlank(" ")       = false
	 * StringUtility.isNotBlank("bob")     = true
	 * StringUtility.isNotBlank("  bob  ") = true
	 * </pre>
	 *
	 * @param str the String to check, may be null
	 * @return <code>true</code> if the String is
	 *         not empty and not null and not whitespace
	 */
	public static boolean isNotBlank(String str)
	{
		int strLen;
		if (str == null || (strLen = str.length()) == 0) return false;

		for (int i = 0; i < strLen; i++)
		{
			if ((!Character.isWhitespace(str.charAt(i)))) return true;
		}

		return false;
	}

	/**
	 * <p>Removes control characters (char &lt;= 32) from both
	 * ends of this String, handling <code>null</code> by returning
	 * <code>null</code>.</p>
	 * <p/>
	 * <p>The String is trimmed using {@link String#trim()}.
	 * Trim removes start and end characters &lt;= 32.
	 * To strip whitespace use {@link #strip(String)}.</p>
	 * <p/>
	 * <p>To trim your choice of characters, use the
	 * {@link #strip(String, String)} methods.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.trim(null)          = null
	 * StringUtility.trim("")            = ""
	 * StringUtility.trim("     ")       = ""
	 * StringUtility.trim("abc")         = "abc"
	 * StringUtility.trim("    abc    ") = "abc"
	 * </pre>
	 *
	 * @param str the String to be trimmed, may be null
	 * @return the trimmed string, <code>null</code> if null String input
	 */
	public static String trim(String str)
	{
		return str == null ? null : str.trim();
	}

	/**
	 * <p>Removes control characters (char &lt;= 32) from both
	 * ends of this String returning <code>null</code> if the String is
	 * empty ("") after the trim or if it is <code>null</code>.
	 * <p/>
	 * <p>The String is trimmed using {@link String#trim()}.
	 * Trim removes start and end characters &lt;= 32.
	 * To strip whitespace use {@link #stripToNull(String)}.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.trimToNull(null)          = null
	 * StringUtility.trimToNull("")            = null
	 * StringUtility.trimToNull("     ")       = null
	 * StringUtility.trimToNull("abc")         = "abc"
	 * StringUtility.trimToNull("    abc    ") = "abc"
	 * </pre>
	 *
	 * @param str the String to be trimmed, may be null
	 * @return the trimmed String,
	 *         <code>null</code> if only chars &lt;= 32, empty or null String input
	 */
	public static String trimToNull(String str)
	{
		String ts = trim(str);
		return isEmpty(ts) ? null : ts;
	}

	/**
	 * <p>Removes control characters (char &lt;= 32) from both
	 * ends of this String returning an empty String ("") if the String
	 * is empty ("") after the trim or if it is <code>null</code>.
	 * <p/>
	 * <p>The String is trimmed using {@link String#trim()}.
	 * Trim removes start and end characters &lt;= 32.
	 * To strip whitespace use {@link #stripToEmpty(String)}.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.trimToEmpty(null)          = ""
	 * StringUtility.trimToEmpty("")            = ""
	 * StringUtility.trimToEmpty("     ")       = ""
	 * StringUtility.trimToEmpty("abc")         = "abc"
	 * StringUtility.trimToEmpty("    abc    ") = "abc"
	 * </pre>
	 *
	 * @param str the String to be trimmed, may be null
	 * @return the trimmed String, or an empty String if <code>null</code> input
	 */
	public static String trimToEmpty(String str)
	{
		return str == null ? EMPTY : str.trim();
	}

	// Stripping

	/**
	 * <p>Strips whitespace from the start and end of a String.</p>
	 * <p/>
	 * <p>This is similar to {@link #trim(String)} but removes whitespace.
	 * Whitespace is defined by {@link Character#isWhitespace(char)}.</p>
	 * <p/>
	 * <p>A <code>null</code> input String returns <code>null</code>.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.strip(null)     = null
	 * StringUtility.strip("")       = ""
	 * StringUtility.strip("   ")    = ""
	 * StringUtility.strip("abc")    = "abc"
	 * StringUtility.strip("  abc")  = "abc"
	 * StringUtility.strip("abc  ")  = "abc"
	 * StringUtility.strip(" abc ")  = "abc"
	 * StringUtility.strip(" ab c ") = "ab c"
	 * </pre>
	 *
	 * @param str the String to remove whitespace from, may be null
	 * @return the stripped String, <code>null</code> if null String input
	 */
	public static String strip(String str)
	{
		return strip(str, null);
	}

	/**
	 * <p>Strips whitespace from the start and end of a String  returning
	 * <code>null</code> if the String is empty ("") after the strip.</p>
	 * <p/>
	 * <p>This is similar to {@link #trimToNull(String)} but removes whitespace.
	 * Whitespace is defined by {@link Character#isWhitespace(char)}.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.strip(null)     = null
	 * StringUtility.strip("")       = null
	 * StringUtility.strip("   ")    = null
	 * StringUtility.strip("abc")    = "abc"
	 * StringUtility.strip("  abc")  = "abc"
	 * StringUtility.strip("abc  ")  = "abc"
	 * StringUtility.strip(" abc ")  = "abc"
	 * StringUtility.strip(" ab c ") = "ab c"
	 * </pre>
	 *
	 * @param str the String to be stripped, may be null
	 * @return the stripped String,
	 *         <code>null</code> if whitespace, empty or null String input
	 */
	public static String stripToNull(String str)
	{
		if (str == null) return null;

		str = strip(str, null);

		return str.length() == 0 ? null : str;
	}

	/**
	 * <p>Strips whitespace from the start and end of a String  returning
	 * an empty String if <code>null</code> input.</p>
	 * <p/>
	 * <p>This is similar to {@link #trimToEmpty(String)} but removes whitespace.
	 * Whitespace is defined by {@link Character#isWhitespace(char)}.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.strip(null)     = ""
	 * StringUtility.strip("")       = ""
	 * StringUtility.strip("   ")    = ""
	 * StringUtility.strip("abc")    = "abc"
	 * StringUtility.strip("  abc")  = "abc"
	 * StringUtility.strip("abc  ")  = "abc"
	 * StringUtility.strip(" abc ")  = "abc"
	 * StringUtility.strip(" ab c ") = "ab c"
	 * </pre>
	 *
	 * @param str the String to be stripped, may be null
	 * @return the trimmed String, or an empty String if <code>null</code> input
	 */
	public static String stripToEmpty(String str)
	{
		return str == null ? EMPTY : strip(str, null);
	}

	/**
	 * <p>Strips any of a set of characters from the start and end of a String.
	 * This is similar to {@link String#trim()} but allows the characters
	 * to be stripped to be controlled.</p>
	 * <p/>
	 * <p>A <code>null</code> input String returns <code>null</code>.
	 * An empty string ("") input returns the empty string.</p>
	 * <p/>
	 * <p>If the stripChars String is <code>null</code>, whitespace is
	 * stripped as defined by {@link Character#isWhitespace(char)}.
	 * Alternatively use {@link #strip(String)}.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.strip(null, *)          = null
	 * StringUtility.strip("", *)            = ""
	 * StringUtility.strip("abc", null)      = "abc"
	 * StringUtility.strip("  abc", null)    = "abc"
	 * StringUtility.strip("abc  ", null)    = "abc"
	 * StringUtility.strip(" abc ", null)    = "abc"
	 * StringUtility.strip("  abcyx", "xyz") = "  abc"
	 * </pre>
	 *
	 * @param str		the String to remove characters from, may be null
	 * @param stripChars the characters to remove, null treated as whitespace
	 * @return the stripped String, <code>null</code> if null String input
	 */
	public static String strip(String str, String stripChars)
	{
		if (isEmpty(str)) return str;

		str = stripStart(str, stripChars);

		return stripEnd(str, stripChars);
	}

	/**
	 * <p>Strips any of a set of characters from the start of a String.</p>
	 * <p/>
	 * <p>A <code>null</code> input String returns <code>null</code>.
	 * An empty string ("") input returns the empty string.</p>
	 * <p/>
	 * <p>If the stripChars String is <code>null</code>, whitespace is
	 * stripped as defined by {@link Character#isWhitespace(char)}.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.stripStart(null, *)          = null
	 * StringUtility.stripStart("", *)            = ""
	 * StringUtility.stripStart("abc", "")        = "abc"
	 * StringUtility.stripStart("abc", null)      = "abc"
	 * StringUtility.stripStart("  abc", null)    = "abc"
	 * StringUtility.stripStart("abc  ", null)    = "abc  "
	 * StringUtility.stripStart(" abc ", null)    = "abc "
	 * StringUtility.stripStart("yxabc  ", "xyz") = "abc  "
	 * </pre>
	 *
	 * @param str		the String to remove characters from, may be null
	 * @param stripChars the characters to remove, null treated as whitespace
	 * @return the stripped String, <code>null</code> if null String input
	 */
	public static String stripStart(String str, String stripChars)
	{
		int strLen;
		if (str == null || (strLen = str.length()) == 0) return str;

		int start = 0;
		if (stripChars == null)
		{
			while ((start != strLen) && Character.isWhitespace(str.charAt(start)))
			{
				start++;
			}
		}
		else if (stripChars.length() == 0) return str;
		else
		{
			while ((start != strLen) && (stripChars.indexOf(str.charAt(start)) != -1))
			{
				start++;
			}
		}

		return str.substring(start);
	}

	/**
	 * <p>Strips any of a set of characters from the end of a String.</p>
	 * <p/>
	 * <p>A <code>null</code> input String returns <code>null</code>.
	 * An empty string ("") input returns the empty string.</p>
	 * <p/>
	 * <p>If the stripChars String is <code>null</code>, whitespace is
	 * stripped as defined by {@link Character#isWhitespace(char)}.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.stripEnd(null, *)          = null
	 * StringUtility.stripEnd("", *)            = ""
	 * StringUtility.stripEnd("abc", "")        = "abc"
	 * StringUtility.stripEnd("abc", null)      = "abc"
	 * StringUtility.stripEnd("  abc", null)    = "  abc"
	 * StringUtility.stripEnd("abc  ", null)    = "abc"
	 * StringUtility.stripEnd(" abc ", null)    = " abc"
	 * StringUtility.stripEnd("  abcyx", "xyz") = "  abc"
	 * </pre>
	 *
	 * @param str		the String to remove characters from, may be null
	 * @param stripChars the characters to remove, null treated as whitespace
	 * @return the stripped String, <code>null</code> if null String input
	 */
	public static String stripEnd(String str, String stripChars)
	{
		int end;
		if (str == null || (end = str.length()) == 0)
		{
			return str;
		}

		if (stripChars == null)
		{
			while ((end != 0) && Character.isWhitespace(str.charAt(end - 1)))
			{
				end--;
			}
		}
		else if (stripChars.length() == 0)
		{
			return str;
		}
		else
		{
			while ((end != 0) && (stripChars.indexOf(str.charAt(end - 1)) != -1))
			{
				end--;
			}
		}

		return str.substring(0, end);
	}

	/**
	 * <p>Strips whitespace from the start and end of every String in an array.
	 * Whitespace is defined by {@link Character#isWhitespace(char)}.</p>
	 * <p/>
	 * <p>A new array is returned each time, except for length zero.
	 * A <code>null</code> array will return <code>null</code>.
	 * An empty array will return itself.
	 * A <code>null</code> array entry will be ignored.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.stripAll(null)             = null
	 * StringUtility.stripAll([])               = []
	 * StringUtility.stripAll(["abc", "  abc"]) = ["abc", "abc"]
	 * StringUtility.stripAll(["abc  ", null])  = ["abc", null]
	 * </pre>
	 *
	 * @param strs the array to remove whitespace from, may be null
	 * @return the stripped Strings, <code>null</code> if null array input
	 */
	public static String[] stripAll(String[] strs)
	{
		return stripAll(strs, null);
	}

	/**
	 * <p>Strips any of a set of characters from the start and end of every
	 * String in an array.</p>
	 * Whitespace is defined by {@link Character#isWhitespace(char)}.</p>
	 * <p/>
	 * <p>A new array is returned each time, except for length zero.
	 * A <code>null</code> array will return <code>null</code>.
	 * An empty array will return itself.
	 * A <code>null</code> array entry will be ignored.
	 * A <code>null</code> stripChars will strip whitespace as defined by
	 * {@link Character#isWhitespace(char)}.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.stripAll(null, *)                = null
	 * StringUtility.stripAll([], *)                  = []
	 * StringUtility.stripAll(["abc", "  abc"], null) = ["abc", "abc"]
	 * StringUtility.stripAll(["abc  ", null], null)  = ["abc", null]
	 * StringUtility.stripAll(["abc  ", null], "yz")  = ["abc  ", null]
	 * StringUtility.stripAll(["yabcz", null], "yz")  = ["abc", null]
	 * </pre>
	 *
	 * @param strs	   the array to remove characters from, may be null
	 * @param stripChars the characters to remove, null treated as whitespace
	 * @return the stripped Strings, <code>null</code> if null array input
	 */
	public static String[] stripAll(String[] strs, String stripChars)
	{
		int strsLen;
		if (strs == null || (strsLen = strs.length) == 0)
		{
			return strs;
		}
		String[] newArr = new String[strsLen];
		for (int i = 0; i < strsLen; i++)
		{
			newArr[i] = strip(strs[i], stripChars);
		}
		return newArr;
	}

	// Equals

	/**
	 * <p>Compares two Strings, returning <code>true</code> if they are equal.</p>
	 * <p/>
	 * <p><code>null</code>s are handled without exceptions. Two <code>null</code>
	 * references are considered to be equal. The comparison is case sensitive.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.equals(null, null)   = true
	 * StringUtility.equals(null, "abc")  = false
	 * StringUtility.equals("abc", null)  = false
	 * StringUtility.equals("abc", "abc") = true
	 * StringUtility.equals("abc", "ABC") = false
	 * </pre>
	 *
	 * @param str1 the first String, may be null
	 * @param str2 the second String, may be null
	 * @return <code>true</code> if the Strings are equal, case sensitive, or
	 *         both <code>null</code>
	 * @see java.lang.String#equals(Object)
	 */
	public static boolean equals(String str1, String str2)
	{
		return str1 == null ? str2 == null : str1.equals(str2);
	}

	/**
	 * <p>Compares two Strings, returning <code>true</code> if they are equal ignoring
	 * the case.</p>
	 * <p/>
	 * <p><code>null</code>s are handled without exceptions. Two <code>null</code>
	 * references are considered equal. Comparison is case insensitive.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.equalsIgnoreCase(null, null)   = true
	 * StringUtility.equalsIgnoreCase(null, "abc")  = false
	 * StringUtility.equalsIgnoreCase("abc", null)  = false
	 * StringUtility.equalsIgnoreCase("abc", "abc") = true
	 * StringUtility.equalsIgnoreCase("abc", "ABC") = true
	 * </pre>
	 *
	 * @param str1 the first String, may be null
	 * @param str2 the second String, may be null
	 * @return <code>true</code> if the Strings are equal, case insensitive, or
	 *         both <code>null</code>
	 * @see java.lang.String#equalsIgnoreCase(String)
	 */
	public static boolean equalsIgnoreCase(String str1, String str2)
	{
		return str1 == null ? str2 == null : str1.equalsIgnoreCase(str2);
	}

	// IndexOf

	/**
	 * <p>Finds the first index within a String, handling <code>null</code>.
	 * This method uses {@link String#indexOf(int)}.</p>
	 * <p/>
	 * <p>A <code>null</code> or empty ("") String will return <code>-1</code>.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.indexOf(null, *)         = -1
	 * StringUtility.indexOf("", *)           = -1
	 * StringUtility.indexOf("aabaabaa", 'a') = 0
	 * StringUtility.indexOf("aabaabaa", 'b') = 2
	 * </pre>
	 *
	 * @param str		the String to check, may be null
	 * @param searchChar the character to find
	 * @return the first index of the search character,
	 *         -1 if no match or <code>null</code> string input
	 */
	public static int indexOf(String str, char searchChar)
	{
		if (isEmpty(str))
		{
			return -1;
		}
		return str.indexOf(searchChar);
	}

	/**
	 * <p>Finds the first index within a String from a start position,
	 * handling <code>null</code>.
	 * This method uses {@link String#indexOf(int, int)}.</p>
	 * <p/>
	 * <p>A <code>null</code> or empty ("") String will return <code>-1</code>.
	 * A negative start position is treated as zero.
	 * A start position greater than the string length returns <code>-1</code>.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.indexOf(null, *, *)          = -1
	 * StringUtility.indexOf("", *, *)            = -1
	 * StringUtility.indexOf("aabaabaa", 'b', 0)  = 2
	 * StringUtility.indexOf("aabaabaa", 'b', 3)  = 5
	 * StringUtility.indexOf("aabaabaa", 'b', 9)  = -1
	 * StringUtility.indexOf("aabaabaa", 'b', -1) = 2
	 * </pre>
	 *
	 * @param str		the String to check, may be null
	 * @param searchChar the character to find
	 * @param startPos   the start position, negative treated as zero
	 * @return the first index of the search character,
	 *         -1 if no match or <code>null</code> string input
	 */
	public static int indexOf(String str, char searchChar, int startPos)
	{
		if (isEmpty(str))
		{
			return -1;
		}
		return str.indexOf(searchChar, startPos);
	}

	/**
	 * <p>Finds the first index within a String, handling <code>null</code>.
	 * This method uses {@link String#indexOf(String)}.</p>
	 * <p/>
	 * <p>A <code>null</code> String will return <code>-1</code>.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.indexOf(null, *)          = -1
	 * StringUtility.indexOf(*, null)          = -1
	 * StringUtility.indexOf("", "")           = 0
	 * StringUtility.indexOf("aabaabaa", "a")  = 0
	 * StringUtility.indexOf("aabaabaa", "b")  = 2
	 * StringUtility.indexOf("aabaabaa", "ab") = 1
	 * StringUtility.indexOf("aabaabaa", "")   = 0
	 * </pre>
	 *
	 * @param str	   the String to check, may be null
	 * @param searchStr the String to find, may be null
	 * @return the first index of the search String,
	 *         -1 if no match or <code>null</code> string input
	 */
	public static int indexOf(String str, String searchStr)
	{
		if (str == null || searchStr == null)
		{
			return -1;
		}
		return str.indexOf(searchStr);
	}

	/**
	 * <p>Finds the n-th index within a String, handling <code>null</code>.
	 * This method uses {@link String#indexOf(String)}.</p>
	 * <p/>
	 * <p>A <code>null</code> String will return <code>-1</code>.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.ordinalIndexOf(null, *, *)          = -1
	 * StringUtility.ordinalIndexOf(*, null, *)          = -1
	 * StringUtility.ordinalIndexOf("", "", *)           = 0
	 * StringUtility.ordinalIndexOf("aabaabaa", "a", 1)  = 0
	 * StringUtility.ordinalIndexOf("aabaabaa", "a", 2)  = 1
	 * StringUtility.ordinalIndexOf("aabaabaa", "b", 1)  = 2
	 * StringUtility.ordinalIndexOf("aabaabaa", "b", 2)  = 5
	 * StringUtility.ordinalIndexOf("aabaabaa", "ab", 1) = 1
	 * StringUtility.ordinalIndexOf("aabaabaa", "ab", 2) = 4
	 * StringUtility.ordinalIndexOf("aabaabaa", "", 1)   = 0
	 * StringUtility.ordinalIndexOf("aabaabaa", "", 2)   = 0
	 * </pre>
	 *
	 * @param str	   the String to check, may be null
	 * @param searchStr the String to find, may be null
	 * @param ordinal   the n-th <code>searchStr</code> to find
	 * @return the n-th index of the search String,
	 *         <code>-1</code> (<code>INDEX_NOT_FOUND</code>) if no match or <code>null</code> string input
	 */
	public static int ordinalIndexOf(String str, String searchStr, int ordinal)
	{
		if (str == null || searchStr == null || ordinal <= 0)
		{
			return INDEX_NOT_FOUND;
		}
		if (searchStr.length() == 0)
		{
			return 0;
		}
		int found = 0;
		int index = INDEX_NOT_FOUND;
		do
		{
			index = str.indexOf(searchStr, index + 1);
			if (index < 0)
			{
				return index;
			}
			found++;
		}
		while (found < ordinal);
		return index;
	}

	/**
	 * <p>Finds the first index within a String, handling <code>null</code>.
	 * This method uses {@link String#indexOf(String, int)}.</p>
	 * <p/>
	 * <p>A <code>null</code> String will return <code>-1</code>.
	 * A negative start position is treated as zero.
	 * An empty ("") search String always matches.
	 * A start position greater than the string length only matches
	 * an empty search String.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.indexOf(null, *, *)          = -1
	 * StringUtility.indexOf(*, null, *)          = -1
	 * StringUtility.indexOf("", "", 0)           = 0
	 * StringUtility.indexOf("aabaabaa", "a", 0)  = 0
	 * StringUtility.indexOf("aabaabaa", "b", 0)  = 2
	 * StringUtility.indexOf("aabaabaa", "ab", 0) = 1
	 * StringUtility.indexOf("aabaabaa", "b", 3)  = 5
	 * StringUtility.indexOf("aabaabaa", "b", 9)  = -1
	 * StringUtility.indexOf("aabaabaa", "b", -1) = 2
	 * StringUtility.indexOf("aabaabaa", "", 2)   = 2
	 * StringUtility.indexOf("abc", "", 9)        = 3
	 * </pre>
	 *
	 * @param str	   the String to check, may be null
	 * @param searchStr the String to find, may be null
	 * @param startPos  the start position, negative treated as zero
	 * @return the first index of the search String,
	 *         -1 if no match or <code>null</code> string input
	 */
	public static int indexOf(String str, String searchStr, int startPos)
	{
		if (str == null || searchStr == null) return -1;

		// JDK1.2/JDK1.3 have a bug, when startPos > str.length for "", hence
		if (searchStr.length() == 0 && startPos >= str.length()) return str.length();

		return str.indexOf(searchStr, startPos);
	}

	// LastIndexOf

	/**
	 * <p>Finds the last index within a String, handling <code>null</code>.
	 * This method uses {@link String#lastIndexOf(int)}.</p>
	 * <p/>
	 * <p>A <code>null</code> or empty ("") String will return <code>-1</code>.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.lastIndexOf(null, *)         = -1
	 * StringUtility.lastIndexOf("", *)           = -1
	 * StringUtility.lastIndexOf("aabaabaa", 'a') = 7
	 * StringUtility.lastIndexOf("aabaabaa", 'b') = 5
	 * </pre>
	 *
	 * @param str		the String to check, may be null
	 * @param searchChar the character to find
	 * @return the last index of the search character,
	 *         -1 if no match or <code>null</code> string input
	 */
	public static int lastIndexOf(String str, char searchChar)
	{
		if (isEmpty(str)) return -1;

		return str.lastIndexOf(searchChar);
	}

	/**
	 * <p>Finds the last index within a String from a start position,
	 * handling <code>null</code>.
	 * This method uses {@link String#lastIndexOf(int, int)}.</p>
	 * <p/>
	 * <p>A <code>null</code> or empty ("") String will return <code>-1</code>.
	 * A negative start position returns <code>-1</code>.
	 * A start position greater than the string length searches the whole string.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.lastIndexOf(null, *, *)          = -1
	 * StringUtility.lastIndexOf("", *,  *)           = -1
	 * StringUtility.lastIndexOf("aabaabaa", 'b', 8)  = 5
	 * StringUtility.lastIndexOf("aabaabaa", 'b', 4)  = 2
	 * StringUtility.lastIndexOf("aabaabaa", 'b', 0)  = -1
	 * StringUtility.lastIndexOf("aabaabaa", 'b', 9)  = 5
	 * StringUtility.lastIndexOf("aabaabaa", 'b', -1) = -1
	 * StringUtility.lastIndexOf("aabaabaa", 'a', 0)  = 0
	 * </pre>
	 *
	 * @param str		the String to check, may be null
	 * @param searchChar the character to find
	 * @param startPos   the start position
	 * @return the last index of the search character,
	 *         -1 if no match or <code>null</code> string input
	 */
	public static int lastIndexOf(String str, char searchChar, int startPos)
	{
		if (isEmpty(str)) return -1;

		return str.lastIndexOf(searchChar, startPos);
	}

	/**
	 * <p>Finds the last index within a String, handling <code>null</code>.
	 * This method uses {@link String#lastIndexOf(String)}.</p>
	 * <p/>
	 * <p>A <code>null</code> String will return <code>-1</code>.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.lastIndexOf(null, *)          = -1
	 * StringUtility.lastIndexOf(*, null)          = -1
	 * StringUtility.lastIndexOf("", "")           = 0
	 * StringUtility.lastIndexOf("aabaabaa", "a")  = 0
	 * StringUtility.lastIndexOf("aabaabaa", "b")  = 2
	 * StringUtility.lastIndexOf("aabaabaa", "ab") = 1
	 * StringUtility.lastIndexOf("aabaabaa", "")   = 8
	 * </pre>
	 *
	 * @param str	   the String to check, may be null
	 * @param searchStr the String to find, may be null
	 * @return the last index of the search String,
	 *         -1 if no match or <code>null</code> string input
	 */
	public static int lastIndexOf(String str, String searchStr)
	{
		if (str == null || searchStr == null) return -1;

		return str.lastIndexOf(searchStr);
	}

	/**
	 * <p>Finds the first index within a String, handling <code>null</code>.
	 * This method uses {@link String#lastIndexOf(String, int)}.</p>
	 * <p/>
	 * <p>A <code>null</code> String will return <code>-1</code>.
	 * A negative start position returns <code>-1</code>.
	 * An empty ("") search String always matches unless the start position is negative.
	 * A start position greater than the string length searches the whole string.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.lastIndexOf(null, *, *)          = -1
	 * StringUtility.lastIndexOf(*, null, *)          = -1
	 * StringUtility.lastIndexOf("aabaabaa", "a", 8)  = 7
	 * StringUtility.lastIndexOf("aabaabaa", "b", 8)  = 5
	 * StringUtility.lastIndexOf("aabaabaa", "ab", 8) = 4
	 * StringUtility.lastIndexOf("aabaabaa", "b", 9)  = 5
	 * StringUtility.lastIndexOf("aabaabaa", "b", -1) = -1
	 * StringUtility.lastIndexOf("aabaabaa", "a", 0)  = 0
	 * StringUtility.lastIndexOf("aabaabaa", "b", 0)  = -1
	 * </pre>
	 *
	 * @param str	   the String to check, may be null
	 * @param searchStr the String to find, may be null
	 * @param startPos  the start position, negative treated as zero
	 * @return the first index of the search String,
	 *         -1 if no match or <code>null</code> string input
	 */
	public static int lastIndexOf(String str, String searchStr, int startPos)
	{
		if (str == null || searchStr == null) return -1;

		return str.lastIndexOf(searchStr, startPos);
	}

	// Contains

	/**
	 * <p>Checks if String contains a search character, handling <code>null</code>.
	 * This method uses {@link String#indexOf(int)}.</p>
	 * <p/>
	 * <p>A <code>null</code> or empty ("") String will return <code>false</code>.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.contains(null, *)    = false
	 * StringUtility.contains("", *)      = false
	 * StringUtility.contains("abc", 'a') = true
	 * StringUtility.contains("abc", 'z') = false
	 * </pre>
	 *
	 * @param str		the String to check, may be null
	 * @param searchChar the character to find
	 * @return true if the String contains the search character,
	 *         false if not or <code>null</code> string input
	 */
	public static boolean contains(String str, char searchChar)
	{
		if (isEmpty(str)) return false;

		return str.indexOf(searchChar) >= 0;
	}

	/**
	 * <p>Checks if String contains a search String, handling <code>null</code>.
	 * This method uses {@link String#indexOf(int)}.</p>
	 * <p/>
	 * <p>A <code>null</code> String will return <code>false</code>.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.contains(null, *)     = false
	 * StringUtility.contains(*, null)     = false
	 * StringUtility.contains("", "")      = true
	 * StringUtility.contains("abc", "")   = true
	 * StringUtility.contains("abc", "a")  = true
	 * StringUtility.contains("abc", "z")  = false
	 * </pre>
	 *
	 * @param str	   the String to check, may be null
	 * @param searchStr the String to find, may be null
	 * @return true if the String contains the search String,
	 *         false if not or <code>null</code> string input
	 */
	public static boolean contains(String str, String searchStr)
	{
		if (str == null || searchStr == null) return false;

		return str.indexOf(searchStr) >= 0;
	}

	// IndexOfAny chars

	/**
	 * <p>Search a String to find the first index of any
	 * character in the given set of characters.</p>
	 * <p/>
	 * <p>A <code>null</code> String will return <code>-1</code>.
	 * A <code>null</code> or zero length search array will return <code>-1</code>.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.indexOfAny(null, *)                = -1
	 * StringUtility.indexOfAny("", *)                  = -1
	 * StringUtility.indexOfAny(*, null)                = -1
	 * StringUtility.indexOfAny(*, [])                  = -1
	 * StringUtility.indexOfAny("zzabyycdxx",['z','a']) = 0
	 * StringUtility.indexOfAny("zzabyycdxx",['b','y']) = 3
	 * StringUtility.indexOfAny("aba", ['z'])           = -1
	 * </pre>
	 *
	 * @param str		 the String to check, may be null
	 * @param searchChars the chars to search for, may be null
	 * @return the index of any of the chars, -1 if no match or null input
	 */
	public static int indexOfAny(String str, char[] searchChars)
	{
		if (isEmpty(str) || ArrayUtility.isEmpty(searchChars))
		{
			return -1;
		}
		for (int i = 0; i < str.length(); i++)
		{
			char ch = str.charAt(i);
			for (int j = 0; j < searchChars.length; j++)
			{
				if (searchChars[j] == ch)
				{
					return i;
				}
			}
		}
		return -1;
	}

	/**
	 * <p>Search a String to find the first index of any
	 * character in the given set of characters.</p>
	 * <p/>
	 * <p>A <code>null</code> String will return <code>-1</code>.
	 * A <code>null</code> search string will return <code>-1</code>.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.indexOfAny(null, *)            = -1
	 * StringUtility.indexOfAny("", *)              = -1
	 * StringUtility.indexOfAny(*, null)            = -1
	 * StringUtility.indexOfAny(*, "")              = -1
	 * StringUtility.indexOfAny("zzabyycdxx", "za") = 0
	 * StringUtility.indexOfAny("zzabyycdxx", "by") = 3
	 * StringUtility.indexOfAny("aba","z")          = -1
	 * </pre>
	 *
	 * @param str		 the String to check, may be null
	 * @param searchChars the chars to search for, may be null
	 * @return the index of any of the chars, -1 if no match or null input
	 */
	public static int indexOfAny(String str, String searchChars)
	{
		if (isEmpty(str) || isEmpty(searchChars))
		{
			return -1;
		}
		return indexOfAny(str, searchChars.toCharArray());
	}

	// IndexOfAnyBut chars

	/**
	 * <p>Search a String to find the first index of any
	 * character not in the given set of characters.</p>
	 * <p/>
	 * <p>A <code>null</code> String will return <code>-1</code>.
	 * A <code>null</code> or zero length search array will return <code>-1</code>.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.indexOfAnyBut(null, *)           = -1
	 * StringUtility.indexOfAnyBut("", *)             = -1
	 * StringUtility.indexOfAnyBut(*, null)           = -1
	 * StringUtility.indexOfAnyBut(*, [])             = -1
	 * StringUtility.indexOfAnyBut("zzabyycdxx",'za') = 3
	 * StringUtility.indexOfAnyBut("zzabyycdxx", '')  = 0
	 * StringUtility.indexOfAnyBut("aba", 'ab')       = -1
	 * </pre>
	 *
	 * @param str		 the String to check, may be null
	 * @param searchChars the chars to search for, may be null
	 * @return the index of any of the chars, -1 if no match or null input
	 */
	public static int indexOfAnyBut(String str, char[] searchChars)
	{
		if (isEmpty(str) || ArrayUtility.isEmpty(searchChars))
		{
			return -1;
		}
outer:
		for (int i = 0; i < str.length(); i++)
		{
			char ch = str.charAt(i);
			for (int j = 0; j < searchChars.length; j++)
			{
				if (searchChars[j] == ch)
				{
					continue outer;
				}
			}
			return i;
		}
		return -1;
	}

	/**
	 * <p>Search a String to find the first index of any
	 * character not in the given set of characters.</p>
	 * <p/>
	 * <p>A <code>null</code> String will return <code>-1</code>.
	 * A <code>null</code> search string will return <code>-1</code>.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.indexOfAnyBut(null, *)            = -1
	 * StringUtility.indexOfAnyBut("", *)              = -1
	 * StringUtility.indexOfAnyBut(*, null)            = -1
	 * StringUtility.indexOfAnyBut(*, "")              = -1
	 * StringUtility.indexOfAnyBut("zzabyycdxx", "za") = 3
	 * StringUtility.indexOfAnyBut("zzabyycdxx", "")   = 0
	 * StringUtility.indexOfAnyBut("aba","ab")         = -1
	 * </pre>
	 *
	 * @param str		 the String to check, may be null
	 * @param searchChars the chars to search for, may be null
	 * @return the index of any of the chars, -1 if no match or null input
	 */
	public static int indexOfAnyBut(String str, String searchChars)
	{
		if (isEmpty(str) || isEmpty(searchChars))
		{
			return -1;
		}
		for (int i = 0; i < str.length(); i++)
		{
			if (searchChars.indexOf(str.charAt(i)) < 0)
			{
				return i;
			}
		}
		return -1;
	}

	/**
	 * <p>Checks if the String contains only certain characters.</p>
	 * <p/>
	 * <p>A <code>null</code> String will return <code>false</code>.
	 * A <code>null</code> valid character array will return <code>false</code>.
	 * An empty String ("") always returns <code>true</code>.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.containsOnly(null, *)       = false
	 * StringUtility.containsOnly(*, null)       = false
	 * StringUtility.containsOnly("", *)         = true
	 * StringUtility.containsOnly("ab", '')      = false
	 * StringUtility.containsOnly("abab", 'abc') = true
	 * StringUtility.containsOnly("ab1", 'abc')  = false
	 * StringUtility.containsOnly("abz", 'abc')  = false
	 * </pre>
	 *
	 * @param str   the String to check, may be null
	 * @param valid an array of valid chars, may be null
	 * @return true if it only contains valid chars and is non-null
	 */
	public static boolean containsOnly(String str, char[] valid)
	{
		// All these pre-checks are to maintain API with an older version
		if ((valid == null) || (str == null))
		{
			return false;
		}

		if (str.length() == 0)
		{
			return true;
		}

		if (valid.length == 0)
		{
			return false;
		}

		return indexOfAnyBut(str, valid) == -1;
	}

	/**
	 * <p>Checks if the String contains only certain characters.</p>
	 * <p/>
	 * <p>A <code>null</code> String will return <code>false</code>.
	 * A <code>null</code> valid character String will return <code>false</code>.
	 * An empty String ("") always returns <code>true</code>.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.containsOnly(null, *)       = false
	 * StringUtility.containsOnly(*, null)       = false
	 * StringUtility.containsOnly("", *)         = true
	 * StringUtility.containsOnly("ab", "")      = false
	 * StringUtility.containsOnly("abab", "abc") = true
	 * StringUtility.containsOnly("ab1", "abc")  = false
	 * StringUtility.containsOnly("abz", "abc")  = false
	 * </pre>
	 *
	 * @param str		the String to check, may be null
	 * @param validChars a String of valid chars, may be null
	 * @return true if it only contains valid chars and is non-null
	 */
	public static boolean containsOnly(String str, String validChars)
	{
		if (str == null || validChars == null)
		{
			return false;
		}
		return containsOnly(str, validChars.toCharArray());
	}

	/**
	 * <p>Checks that the String does not contain certain characters.</p>
	 * <p/>
	 * <p>A <code>null</code> String will return <code>true</code>.
	 * A <code>null</code> invalid character array will return <code>true</code>.
	 * An empty String ("") always returns true.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.containsNone(null, *)       = true
	 * StringUtility.containsNone(*, null)       = true
	 * StringUtility.containsNone("", *)         = true
	 * StringUtility.containsNone("ab", '')      = true
	 * StringUtility.containsNone("abab", 'xyz') = true
	 * StringUtility.containsNone("ab1", 'xyz')  = true
	 * StringUtility.containsNone("abz", 'xyz')  = false
	 * </pre>
	 *
	 * @param str		  the String to check, may be null
	 * @param invalidChars an array of invalid chars, may be null
	 * @return true if it contains none of the invalid chars, or is null
	 */
	public static boolean containsNone(String str, char[] invalidChars)
	{
		if (str == null || invalidChars == null)
		{
			return true;
		}
		int strSize = str.length();
		int validSize = invalidChars.length;
		for (int i = 0; i < strSize; i++)
		{
			char ch = str.charAt(i);
			for (int j = 0; j < validSize; j++)
			{
				if (invalidChars[j] == ch)
				{
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * <p>Checks that the String does not contain certain characters.</p>
	 * <p/>
	 * <p>A <code>null</code> String will return <code>true</code>.
	 * A <code>null</code> invalid character array will return <code>true</code>.
	 * An empty String ("") always returns true.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.containsNone(null, *)       = true
	 * StringUtility.containsNone(*, null)       = true
	 * StringUtility.containsNone("", *)         = true
	 * StringUtility.containsNone("ab", "")      = true
	 * StringUtility.containsNone("abab", "xyz") = true
	 * StringUtility.containsNone("ab1", "xyz")  = true
	 * StringUtility.containsNone("abz", "xyz")  = false
	 * </pre>
	 *
	 * @param str		  the String to check, may be null
	 * @param invalidChars a String of invalid chars, may be null
	 * @return true if it contains none of the invalid chars, or is null
	 */
	public static boolean containsNone(String str, String invalidChars)
	{
		if (str == null || invalidChars == null)
		{
			return true;
		}
		return containsNone(str, invalidChars.toCharArray());
	}

	// IndexOfAny strings

	/**
	 * <p>Find the first index of any of a set of potential substrings.</p>
	 * <p/>
	 * <p>A <code>null</code> String will return <code>-1</code>.
	 * A <code>null</code> or zero length search array will return <code>-1</code>.
	 * A <code>null</code> search array entry will be ignored, but a search
	 * array containing "" will return <code>0</code> if <code>str</code> is not
	 * null. This method uses {@link String#indexOf(String)}.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.indexOfAny(null, *)                     = -1
	 * StringUtility.indexOfAny(*, null)                     = -1
	 * StringUtility.indexOfAny(*, [])                       = -1
	 * StringUtility.indexOfAny("zzabyycdxx", ["ab","cd"])   = 2
	 * StringUtility.indexOfAny("zzabyycdxx", ["cd","ab"])   = 2
	 * StringUtility.indexOfAny("zzabyycdxx", ["mn","op"])   = -1
	 * StringUtility.indexOfAny("zzabyycdxx", ["zab","aby"]) = 1
	 * StringUtility.indexOfAny("zzabyycdxx", [""])          = 0
	 * StringUtility.indexOfAny("", [""])                    = 0
	 * StringUtility.indexOfAny("", ["a"])                   = -1
	 * </pre>
	 *
	 * @param str		the String to check, may be null
	 * @param searchStrs the Strings to search for, may be null
	 * @return the first index of any of the searchStrs in str, -1 if no match
	 */
	public static int indexOfAny(String str, String[] searchStrs)
	{
		if ((str == null) || (searchStrs == null)) return -1;

		int sz = searchStrs.length;

		// String's can't have a MAX_VALUE index.
		int ret = Integer.MAX_VALUE;

		int tmp = 0;
		for (int i = 0; i < sz; i++)
		{
			String search = searchStrs[i];
			if (search == null) continue;

			tmp = str.indexOf(search);
			if (tmp == -1) continue;

			if (tmp < ret) ret = tmp;
		}

		return (ret == Integer.MAX_VALUE) ? -1 : ret;
	}

	/**
	 * <p>Find the latest index of any of a set of potential substrings.</p>
	 * <p/>
	 * <p>A <code>null</code> String will return <code>-1</code>.
	 * A <code>null</code> search array will return <code>-1</code>.
	 * A <code>null</code> or zero length search array entry will be ignored,
	 * but a search array containing "" will return the length of <code>str</code>
	 * if <code>str</code> is not null. This method uses {@link String#indexOf(String)}</p>
	 * <p/>
	 * <pre>
	 * StringUtility.lastIndexOfAny(null, *)                   = -1
	 * StringUtility.lastIndexOfAny(*, null)                   = -1
	 * StringUtility.lastIndexOfAny(*, [])                     = -1
	 * StringUtility.lastIndexOfAny(*, [null])                 = -1
	 * StringUtility.lastIndexOfAny("zzabyycdxx", ["ab","cd"]) = 6
	 * StringUtility.lastIndexOfAny("zzabyycdxx", ["cd","ab"]) = 6
	 * StringUtility.lastIndexOfAny("zzabyycdxx", ["mn","op"]) = -1
	 * StringUtility.lastIndexOfAny("zzabyycdxx", ["mn","op"]) = -1
	 * StringUtility.lastIndexOfAny("zzabyycdxx", ["mn",""])   = 10
	 * </pre>
	 *
	 * @param str		the String to check, may be null
	 * @param searchStrs the Strings to search for, may be null
	 * @return the last index of any of the Strings, -1 if no match
	 */
	public static int lastIndexOfAny(String str, String[] searchStrs)
	{
		if ((str == null) || (searchStrs == null))
		{
			return -1;
		}
		int sz = searchStrs.length;
		int ret = -1;
		int tmp = 0;
		for (int i = 0; i < sz; i++)
		{
			String search = searchStrs[i];
			if (search == null)
			{
				continue;
			}
			tmp = str.lastIndexOf(search);
			if (tmp > ret)
			{
				ret = tmp;
			}
		}
		return ret;
	}

	// Substring

	/**
	 * <p>Gets a substring from the specified String avoiding exceptions.</p>
	 * <p/>
	 * <p>A negative start position can be used to start <code>n</code>
	 * characters from the end of the String.</p>
	 * <p/>
	 * <p>A <code>null</code> String will return <code>null</code>.
	 * An empty ("") String will return "".</p>
	 * <p/>
	 * <pre>
	 * StringUtility.substring(null, *)   = null
	 * StringUtility.substring("", *)     = ""
	 * StringUtility.substring("abc", 0)  = "abc"
	 * StringUtility.substring("abc", 2)  = "c"
	 * StringUtility.substring("abc", 4)  = ""
	 * StringUtility.substring("abc", -2) = "bc"
	 * StringUtility.substring("abc", -4) = "abc"
	 * </pre>
	 *
	 * @param str   the String to get the substring from, may be null
	 * @param start the position to start from, negative means
	 *              count back from the end of the String by this many characters
	 * @return substring from start position, <code>null</code> if null String input
	 */
	public static String substring(String str, int start)
	{
		if (str == null) return null;

		// handle negatives, which means last n characters
		if (start < 0) start = str.length() + start; // remember start is negative

		if (start < 0) start = 0;
		if (start > str.length()) return EMPTY;

		return str.substring(start);
	}

	/**
	 * <p>Gets a substring from the specified String avoiding exceptions.</p>
	 * <p/>
	 * <p>A negative start position can be used to start/end <code>n</code>
	 * characters from the end of the String.</p>
	 * <p/>
	 * <p>The returned substring starts with the character in the <code>start</code>
	 * position and ends before the <code>end</code> position. All position counting is
	 * zero-based -- i.e., to start at the beginning of the string use
	 * <code>start = 0</code>. Negative start and end positions can be used to
	 * specify offsets relative to the end of the String.</p>
	 * <p/>
	 * <p>If <code>start</code> is not strictly to the left of <code>end</code>, ""
	 * is returned.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.substring(null, *, *)    = null
	 * StringUtility.substring("", * ,  *)    = "";
	 * StringUtility.substring("abc", 0, 2)   = "ab"
	 * StringUtility.substring("abc", 2, 0)   = ""
	 * StringUtility.substring("abc", 2, 4)   = "c"
	 * StringUtility.substring("abc", 4, 6)   = ""
	 * StringUtility.substring("abc", 2, 2)   = ""
	 * StringUtility.substring("abc", -2, -1) = "b"
	 * StringUtility.substring("abc", -4, 2)  = "ab"
	 * </pre>
	 *
	 * @param str   the String to get the substring from, may be null
	 * @param start the position to start from, negative means
	 *              count back from the end of the String by this many characters
	 * @param end   the position to end at (exclusive), negative means
	 *              count back from the end of the String by this many characters
	 * @return substring from start position to end positon,
	 *         <code>null</code> if null String input
	 */
	public static String substring(String str, int start, int end)
	{
		if (str == null) return null;

		// handle negatives
		if (end < 0) end = str.length() + end; // remember end is negative
		if (start < 0) start = str.length() + start; // remember start is negative

		// check length next
		if (end > str.length()) end = str.length();

		// if start is greater than end, return ""
		if (start > end) return EMPTY;
		if (start < 0) start = 0;
		if (end < 0) end = 0;

		return str.substring(start, end);
	}

	// Left/Right/Mid

	/**
	 * <p>Gets the leftmost <code>len</code> characters of a String.</p>
	 * <p/>
	 * <p>If <code>len</code> characters are not available, or the
	 * String is <code>null</code>, the String will be returned without
	 * an exception. An exception is thrown if len is negative.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.left(null, *)    = null
	 * StringUtility.left(*, -ve)     = ""
	 * StringUtility.left("", *)      = ""
	 * StringUtility.left("abc", 0)   = ""
	 * StringUtility.left("abc", 2)   = "ab"
	 * StringUtility.left("abc", 4)   = "abc"
	 * </pre>
	 *
	 * @param str the String to get the leftmost characters from, may be null
	 * @param len the length of the required String, must be zero or positive
	 * @return the leftmost characters, <code>null</code> if null String input
	 */
	public static String left(String str, int len)
	{
		if (str == null)
		{
			return null;
		}
		if (len < 0)
		{
			return EMPTY;
		}
		if (str.length() <= len)
		{
			return str;
		}
		else
		{
			return str.substring(0, len);
		}
	}

	/**
	 * <p>Gets the rightmost <code>len</code> characters of a String.</p>
	 * <p/>
	 * <p>If <code>len</code> characters are not available, or the String
	 * is <code>null</code>, the String will be returned without an
	 * an exception. An exception is thrown if len is negative.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.right(null, *)    = null
	 * StringUtility.right(*, -ve)     = ""
	 * StringUtility.right("", *)      = ""
	 * StringUtility.right("abc", 0)   = ""
	 * StringUtility.right("abc", 2)   = "bc"
	 * StringUtility.right("abc", 4)   = "abc"
	 * </pre>
	 *
	 * @param str the String to get the rightmost characters from, may be null
	 * @param len the length of the required String, must be zero or positive
	 * @return the rightmost characters, <code>null</code> if null String input
	 */
	public static String right(String str, int len)
	{
		if (str == null)
		{
			return null;
		}
		if (len < 0)
		{
			return EMPTY;
		}
		if (str.length() <= len)
		{
			return str;
		}
		else
		{
			return str.substring(str.length() - len);
		}
	}

	/**
	 * <p>Gets <code>len</code> characters from the middle of a String.</p>
	 * <p/>
	 * <p>If <code>len</code> characters are not available, the remainder
	 * of the String will be returned without an exception. If the
	 * String is <code>null</code>, <code>null</code> will be returned.
	 * An exception is thrown if len is negative.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.mid(null, *, *)    = null
	 * StringUtility.mid(*, *, -ve)     = ""
	 * StringUtility.mid("", 0, *)      = ""
	 * StringUtility.mid("abc", 0, 2)   = "ab"
	 * StringUtility.mid("abc", 0, 4)   = "abc"
	 * StringUtility.mid("abc", 2, 4)   = "c"
	 * StringUtility.mid("abc", 4, 2)   = ""
	 * StringUtility.mid("abc", -2, 2)  = "ab"
	 * </pre>
	 *
	 * @param str the String to get the characters from, may be null
	 * @param pos the position to start from, negative treated as zero
	 * @param len the length of the required String, must be zero or positive
	 * @return the middle characters, <code>null</code> if null String input
	 */
	public static String mid(String str, int pos, int len)
	{
		if (str == null)
		{
			return null;
		}
		if (len < 0 || pos > str.length())
		{
			return EMPTY;
		}
		if (pos < 0)
		{
			pos = 0;
		}
		if (str.length() <= (pos + len))
		{
			return str.substring(pos);
		}
		else
		{
			return str.substring(pos, pos + len);
		}
	}

	// SubStringAfter/SubStringBefore

	/**
	 * <p>Gets the substring before the first occurrence of a separator.
	 * The separator is not returned.</p>
	 * <p/>
	 * <p>A <code>null</code> string input will return <code>null</code>.
	 * An empty ("") string input will return the empty string.
	 * A <code>null</code> separator will return the input string.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.substringBefore(null, *)      = null
	 * StringUtility.substringBefore("", *)        = ""
	 * StringUtility.substringBefore("abc", "a")   = ""
	 * StringUtility.substringBefore("abcba", "b") = "a"
	 * StringUtility.substringBefore("abc", "c")   = "ab"
	 * StringUtility.substringBefore("abc", "d")   = "abc"
	 * StringUtility.substringBefore("abc", "")    = ""
	 * StringUtility.substringBefore("abc", null)  = "abc"
	 * </pre>
	 *
	 * @param str	   the String to get a substring from, may be null
	 * @param separator the String to search for, may be null
	 * @return the substring before the first occurrence of the separator,
	 *         <code>null</code> if null String input
	 */
	public static String substringBefore(String str, String separator)
	{
		if (isEmpty(str) || separator == null)
		{
			return str;
		}
		if (separator.length() == 0)
		{
			return EMPTY;
		}
		int pos = str.indexOf(separator);
		if (pos == -1)
		{
			return str;
		}
		return str.substring(0, pos);
	}

	/**
	 * <p>Gets the substring after the first occurrence of a separator.
	 * The separator is not returned.</p>
	 * <p/>
	 * <p>A <code>null</code> string input will return <code>null</code>.
	 * An empty ("") string input will return the empty string.
	 * A <code>null</code> separator will return the empty string if the
	 * input string is not <code>null</code>.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.substringAfter(null, *)      = null
	 * StringUtility.substringAfter("", *)        = ""
	 * StringUtility.substringAfter(*, null)      = ""
	 * StringUtility.substringAfter("abc", "a")   = "bc"
	 * StringUtility.substringAfter("abcba", "b") = "cba"
	 * StringUtility.substringAfter("abc", "c")   = ""
	 * StringUtility.substringAfter("abc", "d")   = ""
	 * StringUtility.substringAfter("abc", "")    = "abc"
	 * </pre>
	 *
	 * @param str	   the String to get a substring from, may be null
	 * @param separator the String to search for, may be null
	 * @return the substring after the first occurrence of the separator,
	 *         <code>null</code> if null String input
	 */
	public static String substringAfter(String str, String separator)
	{
		if (isEmpty(str))
		{
			return str;
		}
		if (separator == null)
		{
			return EMPTY;
		}
		int pos = str.indexOf(separator);
		if (pos == -1)
		{
			return EMPTY;
		}
		return str.substring(pos + separator.length());
	}

	/**
	 * <p>Gets the substring before the last occurrence of a separator.
	 * The separator is not returned.</p>
	 * <p/>
	 * <p>A <code>null</code> string input will return <code>null</code>.
	 * An empty ("") string input will return the empty string.
	 * An empty or <code>null</code> separator will return the input string.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.substringBeforeLast(null, *)      = null
	 * StringUtility.substringBeforeLast("", *)        = ""
	 * StringUtility.substringBeforeLast("abcba", "b") = "abc"
	 * StringUtility.substringBeforeLast("abc", "c")   = "ab"
	 * StringUtility.substringBeforeLast("a", "a")     = ""
	 * StringUtility.substringBeforeLast("a", "z")     = "a"
	 * StringUtility.substringBeforeLast("a", null)    = "a"
	 * StringUtility.substringBeforeLast("a", "")      = "a"
	 * </pre>
	 *
	 * @param str	   the String to get a substring from, may be null
	 * @param separator the String to search for, may be null
	 * @return the substring before the last occurrence of the separator,
	 *         <code>null</code> if null String input
	 */
	public static String substringBeforeLast(String str, String separator)
	{
		if (isEmpty(str) || isEmpty(separator))
		{
			return str;
		}
		int pos = str.lastIndexOf(separator);
		if (pos == -1)
		{
			return str;
		}
		return str.substring(0, pos);
	}

	/**
	 * <p>Gets the substring after the last occurrence of a separator.
	 * The separator is not returned.</p>
	 * <p/>
	 * <p>A <code>null</code> string input will return <code>null</code>.
	 * An empty ("") string input will return the empty string.
	 * An empty or <code>null</code> separator will return the empty string if
	 * the input string is not <code>null</code>.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.substringAfterLast(null, *)      = null
	 * StringUtility.substringAfterLast("", *)        = ""
	 * StringUtility.substringAfterLast(*, "")        = ""
	 * StringUtility.substringAfterLast(*, null)      = ""
	 * StringUtility.substringAfterLast("abc", "a")   = "bc"
	 * StringUtility.substringAfterLast("abcba", "b") = "a"
	 * StringUtility.substringAfterLast("abc", "c")   = ""
	 * StringUtility.substringAfterLast("a", "a")     = ""
	 * StringUtility.substringAfterLast("a", "z")     = ""
	 * </pre>
	 *
	 * @param str	   the String to get a substring from, may be null
	 * @param separator the String to search for, may be null
	 * @return the substring after the last occurrence of the separator,
	 *         <code>null</code> if null String input
	 */
	public static String substringAfterLast(String str, String separator)
	{
		if (isEmpty(str))
		{
			return str;
		}
		if (isEmpty(separator))
		{
			return EMPTY;
		}
		int pos = str.lastIndexOf(separator);
		if (pos == -1 || pos == (str.length() - separator.length()))
		{
			return EMPTY;
		}
		return str.substring(pos + separator.length());
	}

	// Substring between

	/**
	 * <p>Gets the String that is nested in between two instances of the
	 * same String.</p>
	 * <p/>
	 * <p>A <code>null</code> input String returns <code>null</code>.
	 * A <code>null</code> tag returns <code>null</code>.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.substringBetween(null, *)            = null
	 * StringUtility.substringBetween("", "")             = ""
	 * StringUtility.substringBetween("", "tag")          = null
	 * StringUtility.substringBetween("tagabctag", null)  = null
	 * StringUtility.substringBetween("tagabctag", "")    = ""
	 * StringUtility.substringBetween("tagabctag", "tag") = "abc"
	 * </pre>
	 *
	 * @param str the String containing the substring, may be null
	 * @param tag the String before and after the substring, may be null
	 * @return the substring, <code>null</code> if no match
	 */
	public static String substringBetween(String str, String tag)
	{
		return substringBetween(str, tag, tag);
	}

	/**
	 * <p>Gets the String that is nested in between two Strings.
	 * Only the first match is returned.</p>
	 * <p/>
	 * <p>A <code>null</code> input String returns <code>null</code>.
	 * A <code>null</code> open/close returns <code>null</code> (no match).
	 * An empty ("") open/close returns an empty string.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.substringBetween(null, *, *)          = null
	 * StringUtility.substringBetween("", "", "")          = ""
	 * StringUtility.substringBetween("", "", "tag")       = null
	 * StringUtility.substringBetween("", "tag", "tag")    = null
	 * StringUtility.substringBetween("yabcz", null, null) = null
	 * StringUtility.substringBetween("yabcz", "", "")     = ""
	 * StringUtility.substringBetween("yabcz", "y", "z")   = "abc"
	 * StringUtility.substringBetween("yabczyabcz", "y", "z")   = "abc"
	 * </pre>
	 *
	 * @param str   the String containing the substring, may be null
	 * @param open  the String before the substring, may be null
	 * @param close the String after the substring, may be null
	 * @return the substring, <code>null</code> if no match
	 */
	public static String substringBetween(String str, String open, String close)
	{
		if (str == null || open == null || close == null)
		{
			return null;
		}
		int start = str.indexOf(open);
		if (start != -1)
		{
			int end = str.indexOf(close, start + open.length());
			if (end != -1)
			{
				return str.substring(start + open.length(), end);
			}
		}
		return null;
	}

	/**
	 * <p>Splits the provided text into an array, using whitespace as the
	 * separator.
	 * Whitespace is defined by {@link Character#isWhitespace(char)}.</p>
	 * <p/>
	 * <p>The separator is not included in the returned String array.
	 * Adjacent separators are treated as one separator.
	 * For more control over the split use the StrTokenizer class.</p>
	 * <p/>
	 * <p>A <code>null</code> input String returns <code>null</code>.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.split(null)       = null
	 * StringUtility.split("")         = []
	 * StringUtility.split("abc def")  = ["abc", "def"]
	 * StringUtility.split("abc  def") = ["abc", "def"]
	 * StringUtility.split(" abc ")    = ["abc"]
	 * </pre>
	 *
	 * @param str the String to parse, may be null
	 * @return an array of parsed Strings, <code>null</code> if null String input
	 */
	public static String[] split(String str)
	{
		return split(str, null, -1);
	}

	/**
	 * <p>Splits the provided text into an array, separator specified.
	 * This is an alternative to using StringTokenizer.</p>
	 * <p/>
	 * <p>The separator is not included in the returned String array.
	 * Adjacent separators are treated as one separator.
	 * For more control over the split use the StrTokenizer class.</p>
	 * <p/>
	 * <p>A <code>null</code> input String returns <code>null</code>.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.split(null, *)         = null
	 * StringUtility.split("", *)           = []
	 * StringUtility.split("a.b.c", '.')    = ["a", "b", "c"]
	 * StringUtility.split("a..b.c", '.')   = ["a", "b", "c"]
	 * StringUtility.split("a:b:c", '.')    = ["a:b:c"]
	 * StringUtility.split("a\tb\nc", null) = ["a", "b", "c"]
	 * StringUtility.split("a b c", ' ')    = ["a", "b", "c"]
	 * </pre>
	 *
	 * @param str		   the String to parse, may be null
	 * @param separatorChar the character used as the delimiter,
	 *                      <code>null</code> splits on whitespace
	 * @return an array of parsed Strings, <code>null</code> if null String input
	 */
	public static String[] split(String str, char separatorChar)
	{
		return splitWorker(str, separatorChar, false);
	}

	/**
	 * <p>Splits the provided text into an array, separators specified.
	 * This is an alternative to using StringTokenizer.</p>
	 * <p/>
	 * <p>The separator is not included in the returned String array.
	 * Adjacent separators are treated as one separator.
	 * For more control over the split use the StrTokenizer class.</p>
	 * <p/>
	 * <p>A <code>null</code> input String returns <code>null</code>.
	 * A <code>null</code> separatorChars splits on whitespace.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.split(null, *)         = null
	 * StringUtility.split("", *)           = []
	 * StringUtility.split("abc def", null) = ["abc", "def"]
	 * StringUtility.split("abc def", " ")  = ["abc", "def"]
	 * StringUtility.split("abc  def", " ") = ["abc", "def"]
	 * StringUtility.split("ab:cd:ef", ":") = ["ab", "cd", "ef"]
	 * </pre>
	 *
	 * @param str			the String to parse, may be null
	 * @param separatorChars the characters used as the delimiters,
	 *                       <code>null</code> splits on whitespace
	 * @return an array of parsed Strings, <code>null</code> if null String input
	 */
	public static String[] split(String str, String separatorChars)
	{
		return splitWorker(str, separatorChars, -1, false);
	}

	/**
	 * <p>Splits the provided text into an array with a maximum length,
	 * separators specified.</p>
	 * <p/>
	 * <p>The separator is not included in the returned String array.
	 * Adjacent separators are treated as one separator.</p>
	 * <p/>
	 * <p>A <code>null</code> input String returns <code>null</code>.
	 * A <code>null</code> separatorChars splits on whitespace.</p>
	 * <p/>
	 * <p>If more than <code>max</code> delimited substrings are found, the last
	 * returned string includes all characters after the first <code>max - 1</code>
	 * returned strings (including separator characters).</p>
	 * <p/>
	 * <pre>
	 * StringUtility.split(null, *, *)            = null
	 * StringUtility.split("", *, *)              = []
	 * StringUtility.split("ab de fg", null, 0)   = ["ab", "cd", "ef"]
	 * StringUtility.split("ab   de fg", null, 0) = ["ab", "cd", "ef"]
	 * StringUtility.split("ab:cd:ef", ":", 0)    = ["ab", "cd", "ef"]
	 * StringUtility.split("ab:cd:ef", ":", 2)    = ["ab", "cd:ef"]
	 * </pre>
	 *
	 * @param str			the String to parse, may be null
	 * @param separatorChars the characters used as the delimiters,
	 *                       <code>null</code> splits on whitespace
	 * @param max			the maximum number of elements to include in the
	 *                       array. A zero or negative value implies no limit
	 * @return an array of parsed Strings, <code>null</code> if null String input
	 */
	public static String[] split(String str, String separatorChars, int max)
	{
		return splitWorker(str, separatorChars, max, false);
	}

	/**
	 * <p>Splits the provided text into an array, separator string specified.</p>
	 * <p/>
	 * <p>The separator(s) will not be included in the returned String array.
	 * Adjacent separators are treated as one separator.</p>
	 * <p/>
	 * <p>A <code>null</code> input String returns <code>null</code>.
	 * A <code>null</code> separator splits on whitespace.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.split(null, *)            = null
	 * StringUtility.split("", *)              = []
	 * StringUtility.split("ab de fg", null)   = ["ab", "de", "fg"]
	 * StringUtility.split("ab   de fg", null) = ["ab", "de", "fg"]
	 * StringUtility.split("ab:cd:ef", ":")    = ["ab", "cd", "ef"]
	 * StringUtility.split("abstemiouslyaeiouyabstemiously", "aeiouy")  = ["bst", "m", "sl", "bst", "m", "sl"]
	 * StringUtility.split("abstemiouslyaeiouyabstemiously", "aeiouy")  = ["abstemiously", "abstemiously"]
	 * </pre>
	 *
	 * @param str	   the String to parse, may be null
	 * @param separator String containing the String to be used as a delimiter,
	 *                  <code>null</code> splits on whitespace
	 * @return an array of parsed Strings, <code>null</code> if null String was input
	 */
	public static String[] splitByWholeSeparator(String str, String separator)
	{
		return splitByWholeSeparator(str, separator, -1);
	}

	/**
	 * <p>Splits the provided text into an array, separator string specified.
	 * Returns a maximum of <code>max</code> substrings.</p>
	 * <p/>
	 * <p>The separator(s) will not be included in the returned String array.
	 * Adjacent separators are treated as one separator.</p>
	 * <p/>
	 * <p>A <code>null</code> input String returns <code>null</code>.
	 * A <code>null</code> separator splits on whitespace.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.splitByWholeSeparator(null, *, *)               = null
	 * StringUtility.splitByWholeSeparator("", *, *)                 = []
	 * StringUtility.splitByWholeSeparator("ab de fg", null, 0)      = ["ab", "de", "fg"]
	 * StringUtility.splitByWholeSeparator("ab   de fg", null, 0)    = ["ab", "de", "fg"]
	 * StringUtility.splitByWholeSeparator("ab:cd:ef", ":", 2)       = ["ab", "cd"]
	 * StringUtility.splitByWholeSeparator("abstemiouslyaeiouyabstemiously", "aeiouy", 2) = ["bst", "m"]
	 * StringUtility.splitByWholeSeparator("abstemiouslyaeiouyabstemiously", "aeiouy", 2)  = ["abstemiously", "abstemiously"]
	 * </pre>
	 *
	 * @param str	   the String to parse, may be null
	 * @param separator String containing the String to be used as a delimiter,
	 *                  <code>null</code> splits on whitespace
	 * @param max	   the maximum number of elements to include in the returned
	 *                  array. A zero or negative value implies no limit.
	 * @return an array of parsed Strings, <code>null</code> if null String was input
	 */
	public static String[] splitByWholeSeparator(String str, String separator, int max)
	{
		if (str == null)
		{
			return null;
		}

		int len = str.length();

		if (len == 0)
		{
			return ArrayUtility.EMPTY_STRING_ARRAY;
		}

		if ((separator == null) || ("".equals(separator)))
		{
			// Split on whitespace.
			return split(str, null, max);
		}


		int separatorLength = separator.length();

		ArrayList substrings = new ArrayList();
		int numberOfSubstrings = 0;
		int beg = 0;
		int end = 0;
		while (end < len)
		{
			end = str.indexOf(separator, beg);

			if (end > -1)
			{
				if (end > beg)
				{
					numberOfSubstrings += 1;

					if (numberOfSubstrings == max)
					{
						end = len;
						substrings.add(str.substring(beg));
					}
					else
					{
						// The following is OK, because String.substring( beg, end ) excludes
						// the character at the position 'end'.
						substrings.add(str.substring(beg, end));

						// Set the starting point for the next search.
						// The following is equivalent to beg = end + (separatorLength - 1) + 1,
						// which is the right calculation:
						beg = end + separatorLength;
					}
				}
				else
				{
					// We found a consecutive occurrence of the separator, so skip it.
					beg = end + separatorLength;
				}
			}
			else
			{
				// String.substring( beg ) goes from 'beg' to the end of the String.
				substrings.add(str.substring(beg));
				end = len;
			}
		}

		return (String[]) substrings.toArray(new String[substrings.size()]);
	}


	/**
	 * <p>Splits the provided text into an array, using whitespace as the
	 * separator, preserving all tokens, including empty tokens created by
	 * adjacent separators. This is an alternative to using StringTokenizer.
	 * Whitespace is defined by {@link Character#isWhitespace(char)}.</p>
	 * <p/>
	 * <p>The separator is not included in the returned String array.
	 * Adjacent separators are treated as separators for empty tokens.
	 * For more control over the split use the StrTokenizer class.</p>
	 * <p/>
	 * <p>A <code>null</code> input String returns <code>null</code>.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.splitPreserveAllTokens(null)       = null
	 * StringUtility.splitPreserveAllTokens("")         = []
	 * StringUtility.splitPreserveAllTokens("abc def")  = ["abc", "def"]
	 * StringUtility.splitPreserveAllTokens("abc  def") = ["abc", "", "def"]
	 * StringUtility.splitPreserveAllTokens(" abc ")    = ["", "abc", ""]
	 * </pre>
	 *
	 * @param str the String to parse, may be <code>null</code>
	 * @return an array of parsed Strings, <code>null</code> if null String input
	 */
	public static String[] splitPreserveAllTokens(String str)
	{
		return splitWorker(str, null, -1, true);
	}

	/**
	 * <p>Splits the provided text into an array, separator specified,
	 * preserving all tokens, including empty tokens created by adjacent
	 * separators. This is an alternative to using StringTokenizer.</p>
	 * <p/>
	 * <p>The separator is not included in the returned String array.
	 * Adjacent separators are treated as separators for empty tokens.
	 * For more control over the split use the StrTokenizer class.</p>
	 * <p/>
	 * <p>A <code>null</code> input String returns <code>null</code>.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.splitPreserveAllTokens(null, *)         = null
	 * StringUtility.splitPreserveAllTokens("", *)           = []
	 * StringUtility.splitPreserveAllTokens("a.b.c", '.')    = ["a", "b", "c"]
	 * StringUtility.splitPreserveAllTokens("a..b.c", '.')   = ["a", "b", "c"]
	 * StringUtility.splitPreserveAllTokens("a:b:c", '.')    = ["a:b:c"]
	 * StringUtility.splitPreserveAllTokens("a\tb\nc", null) = ["a", "b", "c"]
	 * StringUtility.splitPreserveAllTokens("a b c", ' ')    = ["a", "b", "c"]
	 * StringUtility.splitPreserveAllTokens("a b c ", ' ')   = ["a", "b", "c", ""]
	 * StringUtility.splitPreserveAllTokens("a b c ", ' ')   = ["a", "b", "c", "", ""]
	 * StringUtility.splitPreserveAllTokens(" a b c", ' ')   = ["", a", "b", "c"]
	 * StringUtility.splitPreserveAllTokens("  a b c", ' ')  = ["", "", a", "b", "c"]
	 * StringUtility.splitPreserveAllTokens(" a b c ", ' ')  = ["", a", "b", "c", ""]
	 * </pre>
	 *
	 * @param str		   the String to parse, may be <code>null</code>
	 * @param separatorChar the character used as the delimiter,
	 *                      <code>null</code> splits on whitespace
	 * @return an array of parsed Strings, <code>null</code> if null String input
	 */
	public static String[] splitPreserveAllTokens(String str, char separatorChar)
	{
		return splitWorker(str, separatorChar, true);
	}

	/**
	 * Performs the logic for the <code>split</code> and
	 * <code>splitPreserveAllTokens</code> methods that do not return a
	 * maximum array length.
	 *
	 * @param str			   the String to parse, may be <code>null</code>
	 * @param separatorChar	 the separate character
	 * @param preserveAllTokens if <code>true</code>, adjacent separators are
	 *                          treated as empty token separators; if <code>false</code>, adjacent
	 *                          separators are treated as one separator.
	 * @return an array of parsed Strings, <code>null</code> if null String input
	 */
	private static String[] splitWorker(String str, char separatorChar, boolean preserveAllTokens)
	{
		if (str == null) return null;

		int len = str.length();
		if (len == 0) return ArrayUtility.EMPTY_STRING_ARRAY;

		List list = new ArrayList();

		int i = 0, start = 0;
		boolean match = false;
		boolean lastMatch = false;

		while (i < len)
		{
			if (str.charAt(i) == separatorChar)
			{
				if (i > 0 && str.charAt(i - 1) == '\\') lastMatch = false;
				else
				{
					if (match || preserveAllTokens)
					{
						if (preserveAllTokens) list.add(str.substring(start, i));
						else list.add(str.substring(start, i).trim());

						match = false;
						lastMatch = true;
					}

					start = ++i;
					continue;
				}
			}

			match = true;
			i++;
		}

		if (match || (preserveAllTokens && lastMatch))
		{
			if (preserveAllTokens) list.add(str.substring(start, i));
			else list.add(str.substring(start, i).trim());
		}

		return (String[]) list.toArray(new String[list.size()]);
	}

	/**
	 * <p>Splits the provided text into an array, separators specified,
	 * preserving all tokens, including empty tokens created by adjacent
	 * separators. This is an alternative to using StringTokenizer.</p>
	 * <p/>
	 * <p>The separator is not included in the returned String array.
	 * Adjacent separators are treated as separators for empty tokens.
	 * For more control over the split use the StrTokenizer class.</p>
	 * <p/>
	 * <p>A <code>null</code> input String returns <code>null</code>.
	 * A <code>null</code> separatorChars splits on whitespace.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.splitPreserveAllTokens(null, *)           = null
	 * StringUtility.splitPreserveAllTokens("", *)             = []
	 * StringUtility.splitPreserveAllTokens("abc def", null)   = ["abc", "def"]
	 * StringUtility.splitPreserveAllTokens("abc def", " ")    = ["abc", "def"]
	 * StringUtility.splitPreserveAllTokens("abc  def", " ")   = ["abc", "", def"]
	 * StringUtility.splitPreserveAllTokens("ab:cd:ef", ":")   = ["ab", "cd", "ef"]
	 * StringUtility.splitPreserveAllTokens("ab:cd:ef:", ":")  = ["ab", "cd", "ef", ""]
	 * StringUtility.splitPreserveAllTokens("ab:cd:ef::", ":") = ["ab", "cd", "ef", "", ""]
	 * StringUtility.splitPreserveAllTokens("ab::cd:ef", ":")  = ["ab", "", cd", "ef"]
	 * StringUtility.splitPreserveAllTokens(":cd:ef", ":")     = ["", cd", "ef"]
	 * StringUtility.splitPreserveAllTokens("::cd:ef", ":")    = ["", "", cd", "ef"]
	 * StringUtility.splitPreserveAllTokens(":cd:ef:", ":")    = ["", cd", "ef", ""]
	 * </pre>
	 *
	 * @param str			the String to parse, may be <code>null</code>
	 * @param separatorChars the characters used as the delimiters,
	 *                       <code>null</code> splits on whitespace
	 * @return an array of parsed Strings, <code>null</code> if null String input
	 */
	public static String[] splitPreserveAllTokens(String str, String separatorChars)
	{
		return splitWorker(str, separatorChars, -1, true);
	}

	/**
	 * <p>Splits the provided text into an array with a maximum length,
	 * separators specified, preserving all tokens, including empty tokens
	 * created by adjacent separators.</p>
	 * <p/>
	 * <p>The separator is not included in the returned String array.
	 * Adjacent separators are treated as separators for empty tokens.
	 * Adjacent separators are treated as one separator.</p>
	 * <p/>
	 * <p>A <code>null</code> input String returns <code>null</code>.
	 * A <code>null</code> separatorChars splits on whitespace.</p>
	 * <p/>
	 * <p>If more than <code>max</code> delimited substrings are found, the last
	 * returned string includes all characters after the first <code>max - 1</code>
	 * returned strings (including separator characters).</p>
	 * <p/>
	 * <pre>
	 * StringUtility.splitPreserveAllTokens(null, *, *)            = null
	 * StringUtility.splitPreserveAllTokens("", *, *)              = []
	 * StringUtility.splitPreserveAllTokens("ab de fg", null, 0)   = ["ab", "cd", "ef"]
	 * StringUtility.splitPreserveAllTokens("ab   de fg", null, 0) = ["ab", "cd", "ef"]
	 * StringUtility.splitPreserveAllTokens("ab:cd:ef", ":", 0)    = ["ab", "cd", "ef"]
	 * StringUtility.splitPreserveAllTokens("ab:cd:ef", ":", 2)    = ["ab", "cd:ef"]
	 * StringUtility.splitPreserveAllTokens("ab   de fg", null, 2) = ["ab", "  de fg"]
	 * StringUtility.splitPreserveAllTokens("ab   de fg", null, 3) = ["ab", "", " de fg"]
	 * StringUtility.splitPreserveAllTokens("ab   de fg", null, 4) = ["ab", "", "", "de fg"]
	 * </pre>
	 *
	 * @param str			the String to parse, may be <code>null</code>
	 * @param separatorChars the characters used as the delimiters,
	 *                       <code>null</code> splits on whitespace
	 * @param max			the maximum number of elements to include in the
	 *                       array. A zero or negative value implies no limit
	 * @return an array of parsed Strings, <code>null</code> if null String input
	 */
	public static String[] splitPreserveAllTokens(String str, String separatorChars, int max)
	{
		return splitWorker(str, separatorChars, max, true);
	}

	/**
	 * Performs the logic for the <code>split</code> and
	 * <code>splitPreserveAllTokens</code> methods that return a maximum array
	 * length.
	 *
	 * @param str			   the String to parse, may be <code>null</code>
	 * @param separatorChars	the separate character
	 * @param max			   the maximum number of elements to include in the
	 *                          array. A zero or negative value implies no limit.
	 * @param preserveAllTokens if <code>true</code>, adjacent separators are
	 *                          treated as empty token separators; if <code>false</code>, adjacent
	 *                          separators are treated as one separator.
	 * @return an array of parsed Strings, <code>null</code> if null String input
	 */
	private static String[] splitWorker(String str, String separatorChars, int max, boolean preserveAllTokens)
	{
		if (str == null) return null;

		int len = str.length();
		if (len == 0) return ArrayUtility.EMPTY_STRING_ARRAY;

		List list = new ArrayList();
		int sizePlus1 = 1;
		int i = 0, start = 0;
		boolean match = false;
		boolean lastMatch = false;

		if (separatorChars == null)
		{
			// Null separator means use whitespace
			while (i < len)
			{
				if (Character.isWhitespace(str.charAt(i)))
				{
					if (match || preserveAllTokens)
					{
						lastMatch = true;
						if (sizePlus1++ == max)
						{
							i = len;
							lastMatch = false;
						}

						list.add(str.substring(start, i).trim());
						match = false;
					}

					start = ++i;
					continue;
				}
				else lastMatch = false;

				match = true;
				i++;
			}
		}
		else if (separatorChars.length() == 1)
		{
			// Optimise 1 character case
			char sep = separatorChars.charAt(0);
			while (i < len)
			{
				if (str.charAt(i) == sep)
				{
					if (match || preserveAllTokens)
					{
						lastMatch = true;
						if (sizePlus1++ == max)
						{
							i = len;
							lastMatch = false;
						}

						list.add(str.substring(start, i).trim());
						match = false;
					}

					start = ++i;
					continue;
				}
				else lastMatch = false;

				match = true;
				i++;
			}
		}
		else
		{
			// standard case
			while (i < len)
			{
				if (separatorChars.indexOf(str.charAt(i)) >= 0)
				{
					if (match || preserveAllTokens)
					{
						lastMatch = true;
						if (sizePlus1++ == max)
						{
							i = len;
							lastMatch = false;
						}

						list.add(str.substring(start, i).trim());
						match = false;
					}

					start = ++i;
					continue;
				}
				else lastMatch = false;

				match = true;
				i++;
			}
		}

		if (match || (preserveAllTokens && lastMatch)) list.add(str.substring(start, i).trim());

		return (String[]) list.toArray(new String[list.size()]);
	}

	/**
	 * <p>Joins the elements of the provided array into a single String
	 * containing the provided list of elements.</p>
	 * <p/>
	 * <p>No separator is added to the joined String.
	 * Null objects or empty strings within the array are represented by
	 * empty strings.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.join(null)            = null
	 * StringUtility.join([])              = ""
	 * StringUtility.join([null])          = ""
	 * StringUtility.join(["a", "b", "c"]) = "abc"
	 * StringUtility.join([null, "", "a"]) = "a"
	 * </pre>
	 *
	 * @param array the array of values to join together, may be null
	 * @return the joined String, <code>null</code> if null array input
	 */
	public static String join(Object[] array)
	{
		return join(array, null);
	}

	/**
	 * <p>Joins the elements of the provided array into a single String
	 * containing the provided list of elements.</p>
	 * <p/>
	 * <p>No delimiter is added before or after the list.
	 * Null objects or empty strings within the array are represented by
	 * empty strings.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.join(null, *)               = null
	 * StringUtility.join([], *)                 = ""
	 * StringUtility.join([null], *)             = ""
	 * StringUtility.join(["a", "b", "c"], ';')  = "a;b;c"
	 * StringUtility.join(["a", "b", "c"], null) = "abc"
	 * StringUtility.join([null, "", "a"], ';')  = ";;a"
	 * </pre>
	 *
	 * @param array	 the array of values to join together, may be null
	 * @param separator the separator character to use
	 * @return the joined String, <code>null</code> if null array input
	 */
	public static String join(Object[] array, char separator)
	{
		if (array == null) return null;

		int arraySize = array.length;
		int bufSize = (arraySize == 0 ? 0 : ((array[0] == null ? 16 : array[0].toString().length()) + 1) * arraySize);
		StringBuffer buf = new StringBuffer(bufSize);

		for (int i = 0; i < arraySize; i++)
		{
			if (i > 0) buf.append(separator);

			if (array[i] != null) buf.append(array[i]);
		}

		return buf.toString();
	}

	/**
	 * <p>Joins the elements of the provided array into a single String
	 * containing the provided list of elements.</p>
	 * <p/>
	 * <p>No delimiter is added before or after the list.
	 * A <code>null</code> separator is the same as an empty String ("").
	 * Null objects or empty strings within the array are represented by
	 * empty strings.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.join(null, *)                = null
	 * StringUtility.join([], *)                  = ""
	 * StringUtility.join([null], *)              = ""
	 * StringUtility.join(["a", "b", "c"], "--")  = "a--b--c"
	 * StringUtility.join(["a", "b", "c"], null)  = "abc"
	 * StringUtility.join(["a", "b", "c"], "")    = "abc"
	 * StringUtility.join([null, "", "a"], ',')   = ",,a"
	 * </pre>
	 *
	 * @param array	 the array of values to join together, may be null
	 * @param separator the separator character to use, null treated as ""
	 * @return the joined String, <code>null</code> if null array input
	 */
	public static String join(Object[] array, String separator)
	{
		if (array == null) return null;

		if (separator == null) separator = EMPTY;
		int arraySize = array.length;

		// ArraySize ==  0: Len = 0
		// ArraySize > 0:   Len = NofStrings *(len(firstString) + len(separator))
		int bufSize = ((arraySize == 0) ? 0 : arraySize * ((array[0] == null ? 16 : array[0].toString().length()) + separator.length()));

		StringBuffer buf = new StringBuffer(bufSize);

		for (int i = 0; i < arraySize; i++)
		{
			if (i > 0) buf.append(separator);
			if (array[i] != null) buf.append(array[i]);
		}

		return buf.toString();
	}

	/**
	 * <p>Joins the elements of the provided <code>Iterator</code> into
	 * a single String containing the provided elements.</p>
	 * <p/>
	 * <p>No delimiter is added before or after the list. Null objects or empty
	 * strings within the iteration are represented by empty strings.</p>
	 * <p/>
	 * <p>See the examples here: {@link #join(Object[], char)}. </p>
	 *
	 * @param iterator  the <code>Iterator</code> of values to join together, may be null
	 * @param separator the separator character to use
	 * @return the joined String, <code>null</code> if null iterator input
	 */
	public static String join(Iterator iterator, char separator)
	{
		if (iterator == null) return null;

		StringBuffer buf = new StringBuffer(256); // Java default is 16, probably too small
		while (iterator.hasNext())
		{
			Object obj = iterator.next();
			if (obj != null) buf.append(obj);

			if (iterator.hasNext()) buf.append(separator);
		}
		return buf.toString();
	}

	/**
	 * <p>Joins the elements of the provided <code>Iterator</code> into
	 * a single String containing the provided elements.</p>
	 * <p/>
	 * <p>No delimiter is added before or after the list.
	 * A <code>null</code> separator is the same as an empty String ("").</p>
	 * <p/>
	 * <p>See the examples here: {@link #join(Object[], String)}. </p>
	 *
	 * @param iterator  the <code>Iterator</code> of values to join together, may be null
	 * @param separator the separator character to use, null treated as ""
	 * @return the joined String, <code>null</code> if null iterator input
	 */
	public static String join(Iterator iterator, String separator)
	{
		if (iterator == null) return null;
		StringBuffer buf = new StringBuffer(256); // Java default is 16, probably too small

		while (iterator.hasNext())
		{
			Object obj = iterator.next();
			if (obj != null) buf.append(obj);

			if ((separator != null) && iterator.hasNext()) buf.append(separator);
		}
		return buf.toString();
	}

	/**
	 * <p>Deletes all whitespaces from a String as defined by
	 * {@link Character#isWhitespace(char)}.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.deleteWhitespace(null)         = null
	 * StringUtility.deleteWhitespace("")           = ""
	 * StringUtility.deleteWhitespace("abc")        = "abc"
	 * StringUtility.deleteWhitespace("   ab  c  ") = "abc"
	 * </pre>
	 *
	 * @param str the String to delete whitespace from, may be null
	 * @return the String without whitespaces, <code>null</code> if null String input
	 */
	public static String deleteWhitespace(String str)
	{
		if (isEmpty(str)) return str;

		int sz = str.length();
		char[] chs = new char[sz];
		int count = 0;

		for (int i = 0; i < sz; i++)
		{
			if (!Character.isWhitespace(str.charAt(i)))
			{
				chs[count++] = str.charAt(i);
			}
		}

		if (count == sz)
		{
			return str;
		}
		return new String(chs, 0, count);
	}

	// Remove

	/**
	 * <p>Removes a substring only if it is at the begining of a source string,
	 * otherwise returns the source string.</p>
	 * <p/>
	 * <p>A <code>null</code> source string will return <code>null</code>.
	 * An empty ("") source string will return the empty string.
	 * A <code>null</code> search string will return the source string.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.removeStart(null, *)      = null
	 * StringUtility.removeStart("", *)        = ""
	 * StringUtility.removeStart(*, null)      = *
	 * StringUtility.removeStart("www.domain.com", "www.")   = "domain.com"
	 * StringUtility.removeStart("domain.com", "www.")       = "domain.com"
	 * StringUtility.removeStart("www.domain.com", "domain") = "www.domain.com"
	 * StringUtility.removeStart("abc", "")    = "abc"
	 * </pre>
	 *
	 * @param str	the source String to search, may be null
	 * @param remove the String to search for and remove, may be null
	 * @return the substring with the string removed if found,
	 *         <code>null</code> if null String input
	 */
	public static String removeStart(String str, String remove)
	{
		if (isEmpty(str) || isEmpty(remove))
		{
			return str;
		}
		if (str.startsWith(remove))
		{
			return str.substring(remove.length());
		}
		return str;
	}

	/**
	 * <p>Removes a substring only if it is at the end of a source string,
	 * otherwise returns the source string.</p>
	 * <p/>
	 * <p>A <code>null</code> source string will return <code>null</code>.
	 * An empty ("") source string will return the empty string.
	 * A <code>null</code> search string will return the source string.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.removeEnd(null, *)      = null
	 * StringUtility.removeEnd("", *)        = ""
	 * StringUtility.removeEnd(*, null)      = *
	 * StringUtility.removeEnd("www.domain.com", ".com.")  = "www,domain"
	 * StringUtility.removeEnd("www.domain.com", ".com")   = "www.domain"
	 * StringUtility.removeEnd("www.domain.com", "domain") = "www.domain.com"
	 * StringUtility.removeEnd("abc", "")    = "abc"
	 * </pre>
	 *
	 * @param str	the source String to search, may be null
	 * @param remove the String to search for and remove, may be null
	 * @return the substring with the string removed if found,
	 *         <code>null</code> if null String input
	 */
	public static String removeEnd(String str, String remove)
	{
		if (isEmpty(str) || isEmpty(remove))
		{
			return str;
		}
		if (str.endsWith(remove))
		{
			return str.substring(0, str.length() - remove.length());
		}
		return str;
	}

	/**
	 * <p>Removes all occurances of a substring from within the source string.</p>
	 * <p/>
	 * <p>A <code>null</code> source string will return <code>null</code>.
	 * An empty ("") source string will return the empty string.
	 * A <code>null</code> remove string will return the source string.
	 * An empty ("") remove string will return the source string.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.remove(null, *)        = null
	 * StringUtility.remove("", *)          = ""
	 * StringUtility.remove(*, null)        = *
	 * StringUtility.remove(*, "")          = *
	 * StringUtility.remove("queued", "ue") = "qd"
	 * StringUtility.remove("queued", "zz") = "queued"
	 * </pre>
	 *
	 * @param str	the source String to search, may be null
	 * @param remove the String to search for and remove, may be null
	 * @return the substring with the string removed if found,
	 *         <code>null</code> if null String input
	 */
	public static String remove(String str, String remove)
	{
		if (isEmpty(str) || isEmpty(remove))
		{
			return str;
		}
		return replace(str, remove, "", -1);
	}

	/**
	 * <p>Removes all occurances of a character from within the source string.</p>
	 * <p/>
	 * <p>A <code>null</code> source string will return <code>null</code>.
	 * An empty ("") source string will return the empty string.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.remove(null, *)       = null
	 * StringUtility.remove("", *)         = ""
	 * StringUtility.remove("queued", 'u') = "qeed"
	 * StringUtility.remove("queued", 'z') = "queued"
	 * </pre>
	 *
	 * @param str	the source String to search, may be null
	 * @param remove the char to search for and remove, may be null
	 * @return the substring with the char removed if found,
	 *         <code>null</code> if null String input
	 */
	public static String remove(String str, char remove)
	{
		if (isEmpty(str) || str.indexOf(remove) == -1) return str;

		char[] chars = str.toCharArray();
		int pos = 0;

		for (int i = 0; i < chars.length; i++)
		{
			if (chars[i] != remove)
			{
				chars[pos++] = chars[i];
			}
		}

		return new String(chars, 0, pos);
	}

	// Replacing

	/**
	 * <p>Replaces a String with another String inside a larger String, once.</p>
	 * <p/>
	 * <p>A <code>null</code> reference passed to this method is a no-op.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.replaceOnce(null, *, *)        = null
	 * StringUtility.replaceOnce("", *, *)          = ""
	 * StringUtility.replaceOnce("any", null, *)    = "any"
	 * StringUtility.replaceOnce("any", *, null)    = "any"
	 * StringUtility.replaceOnce("any", "", *)      = "any"
	 * StringUtility.replaceOnce("aba", "a", null)  = "aba"
	 * StringUtility.replaceOnce("aba", "a", "")    = "ba"
	 * StringUtility.replaceOnce("aba", "a", "z")   = "zba"
	 * </pre>
	 *
	 * @param text text to search and replace in, may be null
	 * @param repl the String to search for, may be null
	 * @param with the String to replace with, may be null
	 * @return the text with any replacements processed,
	 *         <code>null</code> if null String input
	 * @see #replace(String text, String repl, String with, int max)
	 */
	public static String replaceOnce(String text, String repl, String with)
	{
		return replace(text, repl, with, 1);
	}

	/**
	 * <p>Replaces all occurrences of a String within another String.</p>
	 * <p/>
	 * <p>A <code>null</code> reference passed to this method is a no-op.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.replace(null, *, *)        = null
	 * StringUtility.replace("", *, *)          = ""
	 * StringUtility.replace("any", null, *)    = "any"
	 * StringUtility.replace("any", *, null)    = "any"
	 * StringUtility.replace("any", "", *)      = "any"
	 * StringUtility.replace("aba", "a", null)  = "aba"
	 * StringUtility.replace("aba", "a", "")    = "b"
	 * StringUtility.replace("aba", "a", "z")   = "zbz"
	 * </pre>
	 *
	 * @param text text to search and replace in, may be null
	 * @param repl the String to search for, may be null
	 * @param with the String to replace with, may be null
	 * @return the text with any replacements processed,
	 *         <code>null</code> if null String input
	 * @see #replace(String text, String repl, String with, int max)
	 */
	public static String replace(String text, String repl, String with)
	{
		return replace(text, repl, with, -1);
	}

	/**
	 * <p>Replaces a String with another String inside a larger String,
	 * for the first <code>max</code> values of the search String.</p>
	 * <p/>
	 * <p>A <code>null</code> reference passed to this method is a no-op.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.replace(null, *, *, *)         = null
	 * StringUtility.replace("", *, *, *)           = ""
	 * StringUtility.replace("any", null, *, *)     = "any"
	 * StringUtility.replace("any", *, null, *)     = "any"
	 * StringUtility.replace("any", "", *, *)       = "any"
	 * StringUtility.replace("any", *, *, 0)        = "any"
	 * StringUtility.replace("abaa", "a", null, -1) = "abaa"
	 * StringUtility.replace("abaa", "a", "", -1)   = "b"
	 * StringUtility.replace("abaa", "a", "z", 0)   = "abaa"
	 * StringUtility.replace("abaa", "a", "z", 1)   = "zbaa"
	 * StringUtility.replace("abaa", "a", "z", 2)   = "zbza"
	 * StringUtility.replace("abaa", "a", "z", -1)  = "zbzz"
	 * </pre>
	 *
	 * @param text text to search and replace in, may be null
	 * @param repl the String to search for, may be null
	 * @param with the String to replace with, may be null
	 * @param max  maximum number of values to replace, or <code>-1</code> if no maximum
	 * @return the text with any replacements processed,
	 *         <code>null</code> if null String input
	 */
	public static String replace(String text, String repl, String with, int max)
	{
		if (text == null || isEmpty(repl) || with == null || max == 0)
		{
			return text;
		}

		StringBuffer buf = new StringBuffer(text.length());
		int start = 0, end = 0;
		while ((end = text.indexOf(repl, start)) != -1)
		{
			buf.append(text.substring(start, end)).append(with);
			start = end + repl.length();

			if (--max == 0)
			{
				break;
			}
		}
		buf.append(text.substring(start));
		return buf.toString();
	}

	// Replace, character based

	/**
	 * <p>Replaces all occurrences of a character in a String with another.
	 * This is a null-safe version of {@link String#replace(char, char)}.</p>
	 * <p/>
	 * <p>A <code>null</code> string input returns <code>null</code>.
	 * An empty ("") string input returns an empty string.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.replaceChars(null, *, *)        = null
	 * StringUtility.replaceChars("", *, *)          = ""
	 * StringUtility.replaceChars("abcba", 'b', 'y') = "aycya"
	 * StringUtility.replaceChars("abcba", 'z', 'y') = "abcba"
	 * </pre>
	 *
	 * @param str		 String to replace characters in, may be null
	 * @param searchChar  the character to search for, may be null
	 * @param replaceChar the character to replace, may be null
	 * @return modified String, <code>null</code> if null string input
	 */
	public static String replaceChars(String str, char searchChar, char replaceChar)
	{
		if (str == null) return null;

		return str.replace(searchChar, replaceChar);
	}

	/**
	 * <p>Replaces multiple characters in a String in one go.
	 * This method can also be used to delete characters.</p>
	 * <p/>
	 * <p>For example:<br />
	 * <code>replaceChars(&quot;hello&quot;, &quot;ho&quot;, &quot;jy&quot;) = jelly</code>.</p>
	 * <p/>
	 * <p>A <code>null</code> string input returns <code>null</code>.
	 * An empty ("") string input returns an empty string.
	 * A null or empty set of search characters returns the input string.</p>
	 * <p/>
	 * <p>The length of the search characters should normally equal the length
	 * of the replace characters.
	 * If the search characters is longer, then the extra search characters
	 * are deleted.
	 * If the search characters is shorter, then the extra replace characters
	 * are ignored.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.replaceChars(null, *, *)           = null
	 * StringUtility.replaceChars("", *, *)             = ""
	 * StringUtility.replaceChars("abc", null, *)       = "abc"
	 * StringUtility.replaceChars("abc", "", *)         = "abc"
	 * StringUtility.replaceChars("abc", "b", null)     = "ac"
	 * StringUtility.replaceChars("abc", "b", "")       = "ac"
	 * StringUtility.replaceChars("abcba", "bc", "yz")  = "ayzya"
	 * StringUtility.replaceChars("abcba", "bc", "y")   = "ayya"
	 * StringUtility.replaceChars("abcba", "bc", "yzx") = "ayzya"
	 * </pre>
	 *
	 * @param str		  String to replace characters in, may be null
	 * @param searchChars  a set of characters to search for, may be null
	 * @param replaceChars a set of characters to replace, may be null
	 * @return modified String, <code>null</code> if null string input
	 */
	public static String replaceChars(String str, String searchChars, String replaceChars)
	{
		if (isEmpty(str) || isEmpty(searchChars))
		{
			return str;
		}

		if (replaceChars == null)
		{
			replaceChars = "";
		}

		boolean modified = false;
		StringBuffer buf = new StringBuffer(str.length());
		for (int i = 0; i < str.length(); i++)
		{
			char ch = str.charAt(i);
			int index = searchChars.indexOf(ch);
			if (index >= 0)
			{
				modified = true;
				if (index < replaceChars.length())
				{
					buf.append(replaceChars.charAt(index));
				}
			}
			else
			{
				buf.append(ch);
			}
		}
		if (modified)
		{
			return buf.toString();
		}
		else
		{
			return str;
		}
	}

	/**
	 * <p>Removes all occurances of quota characters from within the source string.</p>
	 *
	 * @param str the source String to search, may be null
	 * @return the substring with the char removed if found,
	 *         <code>null</code> if null String input
	 */
	public static String ingoreQuota(String str)
	{
		return remove(str, '"');
	}

	/**
	 * <p>Overlays part of a String with another String.</p>
	 * <p/>
	 * <p>A <code>null</code> string input returns <code>null</code>.
	 * A negative index is treated as zero.
	 * An index greater than the string length is treated as the string length.
	 * The start index is always the smaller of the two indices.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.overlay(null, *, *, *)            = null
	 * StringUtility.overlay("", "abc", 0, 0)          = "abc"
	 * StringUtility.overlay("abcdef", null, 2, 4)     = "abef"
	 * StringUtility.overlay("abcdef", "", 2, 4)       = "abef"
	 * StringUtility.overlay("abcdef", "", 4, 2)       = "abef"
	 * StringUtility.overlay("abcdef", "zzzz", 2, 4)   = "abzzzzef"
	 * StringUtility.overlay("abcdef", "zzzz", 4, 2)   = "abzzzzef"
	 * StringUtility.overlay("abcdef", "zzzz", -1, 4)  = "zzzzef"
	 * StringUtility.overlay("abcdef", "zzzz", 2, 8)   = "abzzzz"
	 * StringUtility.overlay("abcdef", "zzzz", -2, -3) = "zzzzabcdef"
	 * StringUtility.overlay("abcdef", "zzzz", 8, 10)  = "abcdefzzzz"
	 * </pre>
	 *
	 * @param str	 the String to do overlaying in, may be null
	 * @param overlay the String to overlay, may be null
	 * @param start   the position to start overlaying at
	 * @param end	 the position to stop overlaying before
	 * @return overlayed String, <code>null</code> if null String input
	 */
	public static String overlay(String str, String overlay, int start, int end)
	{
		if (str == null) return null;

		if (overlay == null) overlay = EMPTY;

		int len = str.length();
		if (start < 0)
		{
			start = 0;
		}
		if (start > len)
		{
			start = len;
		}
		if (end < 0)
		{
			end = 0;
		}
		if (end > len)
		{
			end = len;
		}
		if (start > end)
		{
			int temp = start;
			start = end;
			end = temp;
		}
		return new StringBuffer(len + start - end + overlay.length() + 1)
				.append(str.substring(0, start))
				.append(overlay)
				.append(str.substring(end))
				.toString();
	}

	/**
	 * <p>Removes one newline from end of a String if it's there,
	 * otherwise leave it alone.  A newline is &quot;<code>\n</code>&quot;,
	 * &quot;<code>\r</code>&quot;, or &quot;<code>\r\n</code>&quot;.</p>
	 * <p/>
	 * <p>NOTE: This method changed in 2.0.
	 * It now more closely matches Perl chomp.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.chomp(null)          = null
	 * StringUtility.chomp("")            = ""
	 * StringUtility.chomp("abc \r")      = "abc "
	 * StringUtility.chomp("abc\n")       = "abc"
	 * StringUtility.chomp("abc\r\n")     = "abc"
	 * StringUtility.chomp("abc\r\n\r\n") = "abc\r\n"
	 * StringUtility.chomp("abc\n\r")     = "abc\n"
	 * StringUtility.chomp("abc\n\rabc")  = "abc\n\rabc"
	 * StringUtility.chomp("\r")          = ""
	 * StringUtility.chomp("\n")          = ""
	 * StringUtility.chomp("\r\n")        = ""
	 * </pre>
	 *
	 * @param str the String to chomp a newline from, may be null
	 * @return String without newline, <code>null</code> if null String input
	 */
	public static String chomp(String str)
	{
		if (isEmpty(str))
		{
			return str;
		}

		if (str.length() == 1)
		{
			char ch = str.charAt(0);
			if (ch == '\r' || ch == '\n')
			{
				return EMPTY;
			}
			else
			{
				return str;
			}
		}

		int lastIdx = str.length() - 1;
		char last = str.charAt(lastIdx);

		if (last == '\n')
		{
			if (str.charAt(lastIdx - 1) == '\r')
			{
				lastIdx--;
			}
		}
		else if (last == '\r')
		{
			// why is this block empty?
			// just to skip incrementing the index?
		}
		else
		{
			lastIdx++;
		}
		return str.substring(0, lastIdx);
	}

	/**
	 * <p>Removes <code>separator</code> from the end of
	 * <code>str</code> if it's there, otherwise leave it alone.</p>
	 * <p/>
	 * <p>NOTE: This method changed in version 2.0.
	 * It now more closely matches Perl chomp.
	 * For the previous behavior, use {@link #substringBeforeLast(String, String)}.
	 * This method uses {@link String#endsWith(String)}.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.chomp(null, *)         = null
	 * StringUtility.chomp("", *)           = ""
	 * StringUtility.chomp("foobar", "bar") = "foo"
	 * StringUtility.chomp("foobar", "baz") = "foobar"
	 * StringUtility.chomp("foo", "foo")    = ""
	 * StringUtility.chomp("foo ", "foo")   = "foo "
	 * StringUtility.chomp(" foo", "foo")   = " "
	 * StringUtility.chomp("foo", "foooo")  = "foo"
	 * StringUtility.chomp("foo", "")       = "foo"
	 * StringUtility.chomp("foo", null)     = "foo"
	 * </pre>
	 *
	 * @param str	   the String to chomp from, may be null
	 * @param separator separator String, may be null
	 * @return String without trailing separator, <code>null</code> if null String input
	 */
	public static String chomp(String str, String separator)
	{
		if (isEmpty(str) || separator == null)
		{
			return str;
		}
		if (str.endsWith(separator))
		{
			return str.substring(0, str.length() - separator.length());
		}
		return str;
	}

	/**
	 * <p>Remove the last character from a String.</p>
	 * <p/>
	 * <p>If the String ends in <code>\r\n</code>, then remove both
	 * of them.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.chop(null)          = null
	 * StringUtility.chop("")            = ""
	 * StringUtility.chop("abc \r")      = "abc "
	 * StringUtility.chop("abc\n")       = "abc"
	 * StringUtility.chop("abc\r\n")     = "abc"
	 * StringUtility.chop("abc")         = "ab"
	 * StringUtility.chop("abc\nabc")    = "abc\nab"
	 * StringUtility.chop("a")           = ""
	 * StringUtility.chop("\r")          = ""
	 * StringUtility.chop("\n")          = ""
	 * StringUtility.chop("\r\n")        = ""
	 * </pre>
	 *
	 * @param str the String to chop last character from, may be null
	 * @return String without last character, <code>null</code> if null String input
	 */
	public static String chop(String str)
	{
		if (str == null)
		{
			return null;
		}
		int strLen = str.length();
		if (strLen < 2)
		{
			return EMPTY;
		}
		int lastIdx = strLen - 1;
		String ret = str.substring(0, lastIdx);
		char last = str.charAt(lastIdx);
		if (last == '\n')
		{
			if (ret.charAt(lastIdx - 1) == '\r')
			{
				return ret.substring(0, lastIdx - 1);
			}
		}
		return ret;
	}

	/**
	 * <p>Remove the end of line and tab characters from a String. Also this method apply a trimming procedure</p>
	 * <p/>
	 * <p>If the String contains <code>\r\n</code> and <code>\t</code>, then remove them.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.chop(null)          = null
	 * StringUtility.chop("")            = ""
	 * StringUtility.chop("abc \r")      = "abc"
	 * StringUtility.chop("abc\n")       = "abc"
	 * StringUtility.chop("abc\r\n")     = "abc"
	 * StringUtility.chop("a bc")        = "a bc"
	 * StringUtility.chop("abc\nabc")    = "abcab"
	 * StringUtility.chop("a")           = ""
	 * StringUtility.chop("\r")          = ""
	 * StringUtility.chop("\n")          = ""
	 * StringUtility.chop("\r\n")        = ""
	 * </pre>
	 *
	 * @param str the String to chop last character from, may be null
	 * @return String without last character, <code>null</code> if null String input
	 */
	public static String chompAllAndTrim(String str)
	{
		StringBuffer buffer = new StringBuffer();

		for (int i = 0; i < str.length(); i++)
		{
			if (str.charAt(i) != '\n' && str.charAt(i) != '\r' && str.charAt(i) != '\t') buffer.append(str.charAt(i));
		}

		str = buffer.toString();
		buffer = null;

		return str;
	}

	/**
	 * <p>Repeat a String <code>repeat</code> times to form a
	 * new String.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.repeat(null, 2) = null
	 * StringUtility.repeat("", 0)   = ""
	 * StringUtility.repeat("", 2)   = ""
	 * StringUtility.repeat("a", 3)  = "aaa"
	 * StringUtility.repeat("ab", 2) = "abab"
	 * StringUtility.repeat("a", -2) = ""
	 * </pre>
	 *
	 * @param str	the String to repeat, may be null
	 * @param repeat number of times to repeat str, negative treated as zero
	 * @return a new String consisting of the original String repeated,
	 *         <code>null</code> if null String input
	 */
	public static String repeat(String str, int repeat)
	{
		if (str == null) return null;

		if (repeat <= 0) return EMPTY;

		int inputLength = str.length();
		if (repeat == 1 || inputLength == 0) return str;

		if (inputLength == 1 && repeat <= PAD_LIMIT) return padding(repeat, str.charAt(0));

		int outputLength = inputLength * repeat;
		switch (inputLength)
		{
			case 1:
				char ch = str.charAt(0);
				char[] output1 = new char[outputLength];
				for (int i = repeat - 1; i >= 0; i--)
				{
					output1[i] = ch;
				}
				return new String(output1);
			case 2:
				char ch0 = str.charAt(0);
				char ch1 = str.charAt(1);
				char[] output2 = new char[outputLength];
				for (int i = repeat * 2 - 2; i >= 0; i--, i--)
				{
					output2[i] = ch0;
					output2[i + 1] = ch1;
				}
				return new String(output2);
			default:
				StringBuffer buf = new StringBuffer(outputLength);
				for (int i = 0; i < repeat; i++)
				{
					buf.append(str);
				}
				return buf.toString();
		}
	}

	/**
	 * <p>Returns padding using the specified delimiter repeated
	 * to a given length.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.padding(0, 'e')  = ""
	 * StringUtility.padding(3, 'e')  = "eee"
	 * StringUtility.padding(-2, 'e') = IndexOutOfBoundsException
	 * </pre>
	 *
	 * @param repeat  number of times to repeat delim
	 * @param padChar character to repeat
	 * @return String with repeated character
	 * @throws IndexOutOfBoundsException if <code>repeat &lt; 0</code>
	 */
	private static String padding(int repeat, char padChar)
	{
		// be careful of synchronization in this method
		// we are assuming that get and set from an array index is atomic
		String pad = PADDING[padChar];
		if (pad == null)
		{
			pad = String.valueOf(padChar);
		}
		while (pad.length() < repeat)
		{
			pad = pad.concat(pad);
		}
		PADDING[padChar] = pad;
		return pad.substring(0, repeat);
	}

	/**
	 * <p>Right pad a String with spaces (' ').</p>
	 * <p/>
	 * <p>The String is padded to the size of <code>size</code>.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.rightPad(null, *)   = null
	 * StringUtility.rightPad("", 3)     = "   "
	 * StringUtility.rightPad("bat", 3)  = "bat"
	 * StringUtility.rightPad("bat", 5)  = "bat  "
	 * StringUtility.rightPad("bat", 1)  = "bat"
	 * StringUtility.rightPad("bat", -1) = "bat"
	 * </pre>
	 *
	 * @param str  the String to pad out, may be null
	 * @param size the size to pad to
	 * @return right padded String or original String if no padding is necessary,
	 *         <code>null</code> if null String input
	 */
	public static String rightPad(String str, int size)
	{
		return rightPad(str, size, ' ');
	}

	/**
	 * <p>Right pad a String with a specified character.</p>
	 * <p/>
	 * <p>The String is padded to the size of <code>size</code>.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.rightPad(null, *, *)     = null
	 * StringUtility.rightPad("", 3, 'z')     = "zzz"
	 * StringUtility.rightPad("bat", 3, 'z')  = "bat"
	 * StringUtility.rightPad("bat", 5, 'z')  = "batzz"
	 * StringUtility.rightPad("bat", 1, 'z')  = "bat"
	 * StringUtility.rightPad("bat", -1, 'z') = "bat"
	 * </pre>
	 *
	 * @param str	 the String to pad out, may be null
	 * @param size	the size to pad to
	 * @param padChar the character to pad with
	 * @return right padded String or original String if no padding is necessary,
	 *         <code>null</code> if null String input
	 */
	public static String rightPad(String str, int size, char padChar)
	{
		if (str == null)
		{
			return null;
		}
		int pads = size - str.length();
		if (pads <= 0)
		{
			return str; // returns original String when possible
		}
		if (pads > PAD_LIMIT)
		{
			return rightPad(str, size, String.valueOf(padChar));
		}
		return str.concat(padding(pads, padChar));
	}

	/**
	 * <p>Right pad a String with a specified String.</p>
	 * <p/>
	 * <p>The String is padded to the size of <code>size</code>.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.rightPad(null, *, *)      = null
	 * StringUtility.rightPad("", 3, "z")      = "zzz"
	 * StringUtility.rightPad("bat", 3, "yz")  = "bat"
	 * StringUtility.rightPad("bat", 5, "yz")  = "batyz"
	 * StringUtility.rightPad("bat", 8, "yz")  = "batyzyzy"
	 * StringUtility.rightPad("bat", 1, "yz")  = "bat"
	 * StringUtility.rightPad("bat", -1, "yz") = "bat"
	 * StringUtility.rightPad("bat", 5, null)  = "bat  "
	 * StringUtility.rightPad("bat", 5, "")    = "bat  "
	 * </pre>
	 *
	 * @param str	the String to pad out, may be null
	 * @param size   the size to pad to
	 * @param padStr the String to pad with, null or empty treated as single space
	 * @return right padded String or original String if no padding is necessary,
	 *         <code>null</code> if null String input
	 */
	public static String rightPad(String str, int size, String padStr)
	{
		if (str == null)
		{
			return null;
		}
		if (isEmpty(padStr))
		{
			padStr = " ";
		}
		int padLen = padStr.length();
		int strLen = str.length();
		int pads = size - strLen;
		if (pads <= 0)
		{
			return str; // returns original String when possible
		}
		if (padLen == 1 && pads <= PAD_LIMIT)
		{
			return rightPad(str, size, padStr.charAt(0));
		}

		if (pads == padLen)
		{
			return str.concat(padStr);
		}
		else if (pads < padLen)
		{
			return str.concat(padStr.substring(0, pads));
		}
		else
		{
			char[] padding = new char[pads];
			char[] padChars = padStr.toCharArray();
			for (int i = 0; i < pads; i++)
			{
				padding[i] = padChars[i % padLen];
			}
			return str.concat(new String(padding));
		}
	}

	/**
	 * <p>Left pad a String with spaces (' ').</p>
	 * <p/>
	 * <p>The String is padded to the size of <code>size<code>.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.leftPad(null, *)   = null
	 * StringUtility.leftPad("", 3)     = "   "
	 * StringUtility.leftPad("bat", 3)  = "bat"
	 * StringUtility.leftPad("bat", 5)  = "  bat"
	 * StringUtility.leftPad("bat", 1)  = "bat"
	 * StringUtility.leftPad("bat", -1) = "bat"
	 * </pre>
	 *
	 * @param str  the String to pad out, may be null
	 * @param size the size to pad to
	 * @return left padded String or original String if no padding is necessary,
	 *         <code>null</code> if null String input
	 */
	public static String leftPad(String str, int size)
	{
		return leftPad(str, size, ' ');
	}

	/**
	 * <p>Left pad a String with a specified character.</p>
	 * <p/>
	 * <p>Pad to a size of <code>size</code>.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.leftPad(null, *, *)     = null
	 * StringUtility.leftPad("", 3, 'z')     = "zzz"
	 * StringUtility.leftPad("bat", 3, 'z')  = "bat"
	 * StringUtility.leftPad("bat", 5, 'z')  = "zzbat"
	 * StringUtility.leftPad("bat", 1, 'z')  = "bat"
	 * StringUtility.leftPad("bat", -1, 'z') = "bat"
	 * </pre>
	 *
	 * @param str	 the String to pad out, may be null
	 * @param size	the size to pad to
	 * @param padChar the character to pad with
	 * @return left padded String or original String if no padding is necessary,
	 *         <code>null</code> if null String input
	 */
	public static String leftPad(String str, int size, char padChar)
	{
		if (str == null) return null;

		int pads = size - str.length();
		if (pads <= 0) return str; // returns original String when possible

		if (pads > PAD_LIMIT) return leftPad(str, size, String.valueOf(padChar));

		return padding(pads, padChar).concat(str);
	}

	/**
	 * <p>Left pad a String with a specified String.</p>
	 * <p/>
	 * <p>Pad to a size of <code>size</code>.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.leftPad(null, *, *)      = null
	 * StringUtility.leftPad("", 3, "z")      = "zzz"
	 * StringUtility.leftPad("bat", 3, "yz")  = "bat"
	 * StringUtility.leftPad("bat", 5, "yz")  = "yzbat"
	 * StringUtility.leftPad("bat", 8, "yz")  = "yzyzybat"
	 * StringUtility.leftPad("bat", 1, "yz")  = "bat"
	 * StringUtility.leftPad("bat", -1, "yz") = "bat"
	 * StringUtility.leftPad("bat", 5, null)  = "  bat"
	 * StringUtility.leftPad("bat", 5, "")    = "  bat"
	 * </pre>
	 *
	 * @param str	the String to pad out, may be null
	 * @param size   the size to pad to
	 * @param padStr the String to pad with, null or empty treated as single space
	 * @return left padded String or original String if no padding is necessary,
	 *         <code>null</code> if null String input
	 */
	public static String leftPad(String str, int size, String padStr)
	{
		if (str == null) return null;

		if (isEmpty(padStr)) padStr = " ";

		int padLen = padStr.length();
		int strLen = str.length();
		int pads = size - strLen;

		if (pads <= 0) return str; // returns original String when possible

		if (padLen == 1 && pads <= PAD_LIMIT) return leftPad(str, size, padStr.charAt(0));

		if (pads == padLen) return padStr.concat(str);
		else if (pads < padLen) return padStr.substring(0, pads).concat(str);
		else
		{
			char[] padding = new char[pads];
			char[] padChars = padStr.toCharArray();

			for (int i = 0; i < pads; i++)
			{
				padding[i] = padChars[i % padLen];
			}

			return new String(padding).concat(str);
		}
	}

	/**
	 * <p>Centers a String in a larger String of size <code>size</code>
	 * using the space character (' ').<p>
	 * <p/>
	 * <p>If the size is less than the String length, the String is returned.
	 * A <code>null</code> String returns <code>null</code>.
	 * A negative size is treated as zero.</p>
	 * <p/>
	 * <p>Equivalent to <code>center(str, size, " ")</code>.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.center(null, *)   = null
	 * StringUtility.center("", 4)     = "    "
	 * StringUtility.center("ab", -1)  = "ab"
	 * StringUtility.center("ab", 4)   = " ab "
	 * StringUtility.center("abcd", 2) = "abcd"
	 * StringUtility.center("a", 4)    = " a  "
	 * </pre>
	 *
	 * @param str  the String to center, may be null
	 * @param size the int size of new String, negative treated as zero
	 * @return centered String, <code>null</code> if null String input
	 */
	public static String center(String str, int size)
	{
		return center(str, size, ' ');
	}

	/**
	 * <p>Centers a String in a larger String of size <code>size</code>.
	 * Uses a supplied character as the value to pad the String with.</p>
	 * <p/>
	 * <p>If the size is less than the String length, the String is returned.
	 * A <code>null</code> String returns <code>null</code>.
	 * A negative size is treated as zero.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.center(null, *, *)     = null
	 * StringUtility.center("", 4, ' ')     = "    "
	 * StringUtility.center("ab", -1, ' ')  = "ab"
	 * StringUtility.center("ab", 4, ' ')   = " ab"
	 * StringUtility.center("abcd", 2, ' ') = "abcd"
	 * StringUtility.center("a", 4, ' ')    = " a  "
	 * StringUtility.center("a", 4, 'y')    = "yayy"
	 * </pre>
	 *
	 * @param str	 the String to center, may be null
	 * @param size	the int size of new String, negative treated as zero
	 * @param padChar the character to pad the new String with
	 * @return centered String, <code>null</code> if null String input
	 */
	public static String center(String str, int size, char padChar)
	{
		if (str == null || size <= 0)
		{
			return str;
		}
		int strLen = str.length();
		int pads = size - strLen;
		if (pads <= 0)
		{
			return str;
		}
		str = leftPad(str, strLen + pads / 2, padChar);
		str = rightPad(str, size, padChar);
		return str;
	}

	/**
	 * <p>Centers a String in a larger String of size <code>size</code>.
	 * Uses a supplied String as the value to pad the String with.</p>
	 * <p/>
	 * <p>If the size is less than the String length, the String is returned.
	 * A <code>null</code> String returns <code>null</code>.
	 * A negative size is treated as zero.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.center(null, *, *)     = null
	 * StringUtility.center("", 4, " ")     = "    "
	 * StringUtility.center("ab", -1, " ")  = "ab"
	 * StringUtility.center("ab", 4, " ")   = " ab"
	 * StringUtility.center("abcd", 2, " ") = "abcd"
	 * StringUtility.center("a", 4, " ")    = " a  "
	 * StringUtility.center("a", 4, "yz")   = "yayz"
	 * StringUtility.center("abc", 7, null) = "  abc  "
	 * StringUtility.center("abc", 7, "")   = "  abc  "
	 * </pre>
	 *
	 * @param str	the String to center, may be null
	 * @param size   the int size of new String, negative treated as zero
	 * @param padStr the String to pad the new String with, must not be null or empty
	 * @return centered String, <code>null</code> if null String input
	 * @throws IllegalArgumentException if padStr is <code>null</code> or empty
	 */
	public static String center(String str, int size, String padStr)
	{
		if (str == null || size <= 0)
		{
			return str;
		}
		if (isEmpty(padStr))
		{
			padStr = " ";
		}
		int strLen = str.length();
		int pads = size - strLen;
		if (pads <= 0)
		{
			return str;
		}
		str = leftPad(str, strLen + pads / 2, padStr);
		str = rightPad(str, size, padStr);
		return str;
	}

	// Case conversion

	/**
	 * <p>Converts a String to upper case as per {@link String#toUpperCase()}.</p>
	 * <p/>
	 * <p>A <code>null</code> input String returns <code>null</code>.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.upperCase(null)  = null
	 * StringUtility.upperCase("")    = ""
	 * StringUtility.upperCase("aBc") = "ABC"
	 * </pre>
	 *
	 * @param str the String to upper case, may be null
	 * @return the upper cased String, <code>null</code> if null String input
	 */
	public static String upperCase(String str)
	{
		if (str == null)
		{
			return null;
		}
		return str.toUpperCase();
	}

	/**
	 * <p>Converts a String to lower case as per {@link String#toLowerCase()}.</p>
	 * <p/>
	 * <p>A <code>null</code> input String returns <code>null</code>.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.lowerCase(null)  = null
	 * StringUtility.lowerCase("")    = ""
	 * StringUtility.lowerCase("aBc") = "abc"
	 * </pre>
	 *
	 * @param str the String to lower case, may be null
	 * @return the lower cased String, <code>null</code> if null String input
	 */
	public static String lowerCase(String str)
	{
		if (str == null) return null;

		return str.toLowerCase();
	}

	/**
	 * <p>Capitalizes a String changing the first letter to title case as
	 * per {@link Character#toTitleCase(char)}. No other letters are changed.</p>
	 * <p/>
	 * <p>For a word based algorithm, see {@link WordUtility#capitalize(String)}.
	 * A <code>null</code> input String returns <code>null</code>.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.capitalize(null)  = null
	 * StringUtility.capitalize("")    = ""
	 * StringUtility.capitalize("cat") = "Cat"
	 * StringUtility.capitalize("cAt") = "CAt"
	 * </pre>
	 *
	 * @param str the String to capitalize, may be null
	 * @return the capitalized String, <code>null</code> if null String input
	 * @see WordUtility#capitalize(String)
	 * @see #uncapitalize(String)
	 */
	public static String capitalize(String str)
	{
		int strLen;
		if (str == null || (strLen = str.length()) == 0) return str;

		return new StringBuffer(strLen)
				.append(Character.toTitleCase(str.charAt(0)))
				.append(str.substring(1))
				.toString();
	}


	/**
	 * <p>Capitalizes a String changing the first letter to title case as
	 * per {@link Character#toTitleCase(char)} and all the others will be lower cases.
	 * This procedure will be applied for all substrings.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.capitalizeAll(null)  = null
	 * StringUtility.capitalizeAll("")    = ""
	 * StringUtility.capitalizeAll("cat") = "Cat"
	 * StringUtility.capitalizeAll("cAt ") = "Cat"
	 * StringUtility.capitalizeAll("cAt and dog") = "Cat And Dog"
	 * </pre>
	 *
	 * @param str the String to capitalize, may be null
	 * @return the capitalized String, <code>null</code> if null String input
	 * @see WordUtility#capitalize(String)
	 * @see #capitalize(String)
	 */
	public static String capitalizeAll(String str)
	{
		int strLen;
		if (str == null || (strLen = str.length()) == 0) return str;

		StringBuffer buff = new StringBuffer(strLen);
		String[] text = split(str, ' ');

		for (int i = 0; i < text.length; i++)
		{
			String word = text[i].toLowerCase();

			buff.append(capitalize(word));
			buff.append(" ");
		}

		return buff.toString().trim();
	}

	/**
	 * <p>Capitalizes a String changing the first letter to title case as
	 * per {@link Character#toTitleCase(char)} and all the others will be lower cases.
	 * Separator in this case is white space and minus
	 * This procedure will be applied for all substrings.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.capitalizeAll(null)  = null
	 * StringUtility.capitalizeAll("")    = ""
	 * StringUtility.capitalizeAll("cat") = "Cat"
	 * StringUtility.capitalizeAll("cAt ") = "Cat"
	 * StringUtility.capitalizeAll("cAt and dog") = "Cat And Dog"
	 * StringUtility.capitalizeAll("cAt-and dog") = "Cat And Dog"
	 * </pre>
	 *
	 * @param str the String to capitalize, may be null
	 * @return the capitalized String, <code>null</code> if null String input
	 * @see WordUtility#capitalize(String)
	 * @see #capitalize(String)
	 */
	public static String capitalizeAllNames(String str)
	{
		str = capitalizeAll(str);

		int strLen;
		if (str == null || (strLen = str.length()) == 0) return str;

		StringBuffer buff = new StringBuffer(strLen);
		String[] text = split(str, '-');

		for (int i = 0; i < text.length; i++)
		{
			String word = text[i].toLowerCase();

			buff.append(capitalize(word));
			buff.append("-");
		}

		return buff.toString().trim();
	}

	/**
	 * <p>Capitalizes a String changing the first letter to title case as
	 * per {@link Character#toTitleCase(char)} and all the others will be lower acses.
	 * This procedure will be applied for all substrings.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.capitalizeAll(null)  = null
	 * StringUtility.capitalizeAll("")    = ""
	 * StringUtility.capitalizeAll("cat") = "Cat"
	 * StringUtility.capitalizeAll("cAt ") = "Cat"
	 * StringUtility.capitalizeAll("cAt and dog") = "CatAndDog"
	 * StringUtility.capitalizeAll("cAt_and_dog") = "CatAndDog"
	 * </pre>
	 *
	 * @param str the String to capitalize, may be null
	 * @return the capitalized String, <code>null</code> if null String input
	 * @see WordUtility#capitalize(String)
	 * @see #capitalize(String)
	 */
	public static String capitalizeAllTrim(String str)
	{
		if (str == null || (str.length()) == 0) return str;

		str = str.replaceAll("\\p{Punct}", "_");

		str = replace(str, "_", " ");
		str = capitalizeAll(str);

		return replace(str, " ", "");
	}

	/**
	 * <p>Capitalizes a String changing the first letter to title case as
	 * per {@link Character#toTitleCase(char)} and all the others will be lower acses.
	 * This procedure will be applied for all substrings.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.capitalizeAll(null)  = null
	 * StringUtility.capitalizeAll("")    = ""
	 * StringUtility.capitalizeAll("cat") = "Cat"
	 * StringUtility.capitalizeAll("cAt ") = "Cat"
	 * StringUtility.capitalizeAll("cAt and dog") = "CatAndDog"
	 * StringUtility.capitalizeAll("cAt_and_dog") = "CatAndDog"
	 * </pre>
	 *
	 * @param str the String to capitalize, may be null
	 * @return the capitalized String, <code>null</code> if null String input
	 * @see WordUtility#capitalize(String)
	 * @see #capitalize(String)
	 */
	public static String capitalizeAllTrim(String str, String defValue)
	{
		if (isEmpty(str) && isEmpty(defValue)) return str;
		else if (isEmpty(str) && isNotEmpty(defValue)) str = defValue;

		return capitalizeAllTrim(str);
	}

	/**
	 * <p>Uncapitalizes a String changing the first letter to title case as
	 * per {@link Character#toLowerCase(char)}. No other letters are changed.</p>
	 * <p/>
	 * <p>For a word based algorithm, see {@link WordUtility#uncapitalize(String)}.
	 * A <code>null</code> input String returns <code>null</code>.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.uncapitalize(null)  = null
	 * StringUtility.uncapitalize("")    = ""
	 * StringUtility.uncapitalize("Cat") = "cat"
	 * StringUtility.uncapitalize("CAT") = "cAT"
	 * </pre>
	 *
	 * @param str the String to uncapitalize, may be null
	 * @return the uncapitalized String, <code>null</code> if null String input
	 * @see WordUtility#uncapitalize(String)
	 * @see #capitalize(String)
	 */
	public static String uncapitalize(String str)
	{
		int strLen;
		if (str == null || (strLen = str.length()) == 0) return str;

		return new StringBuffer(strLen).append(Character.toLowerCase(str.charAt(0))).append(str.substring(1)).toString();
	}

	/**
	 * <p>Swaps the case of a String changing upper and title case to
	 * lower case, and lower case to upper case.</p>
	 * <p/>
	 * <ul>
	 * <li>Upper case character converts to Lower case</li>
	 * <li>Title case character converts to Lower case</li>
	 * <li>Lower case character converts to Upper case</li>
	 * </ul>
	 * <p/>
	 * <p>For a word based algorithm, see {@link WordUtility#swapCase(String)}.
	 * A <code>null</code> input String returns <code>null</code>.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.swapCase(null)                 = null
	 * StringUtility.swapCase("")                   = ""
	 * StringUtility.swapCase("The dog has a BONE") = "tHE DOG HAS A bone"
	 * </pre>
	 * <p/>
	 * <p>NOTE: This method changed in Lang version 2.0.
	 * It no longer performs a word based algorithm.
	 * If you only use ASCII, you will notice no change.
	 * That functionality is available in WordUtility.</p>
	 *
	 * @param str the String to swap case, may be null
	 * @return the changed String, <code>null</code> if null String input
	 */
	public static String swapCase(String str)
	{
		int strLen;
		if (str == null || (strLen = str.length()) == 0) return str;

		StringBuffer buffer = new StringBuffer(strLen);

		char ch = 0;
		for (int i = 0; i < strLen; i++)
		{
			ch = str.charAt(i);
			if (Character.isUpperCase(ch)) ch = Character.toLowerCase(ch);
			else if (Character.isTitleCase(ch)) ch = Character.toLowerCase(ch);
			else if (Character.isLowerCase(ch)) ch = Character.toUpperCase(ch);

			buffer.append(ch);
		}

		return buffer.toString();
	}

	/**
	 * <p>Counts how many times the substring appears in the larger String.</p>
	 * <p/>
	 * <p>A <code>null</code> or empty ("") String input returns <code>0</code>.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.countMatches(null, *)       = 0
	 * StringUtility.countMatches("", *)         = 0
	 * StringUtility.countMatches("abba", null)  = 0
	 * StringUtility.countMatches("abba", "")    = 0
	 * StringUtility.countMatches("abba", "a")   = 2
	 * StringUtility.countMatches("abba", "ab")  = 1
	 * StringUtility.countMatches("abba", "xxx") = 0
	 * </pre>
	 *
	 * @param str the String to check, may be null
	 * @param sub the substring to count, may be null
	 * @return the number of occurrences, 0 if either String is <code>null</code>
	 */
	public static int countMatches(String str, String sub)
	{
		if (isEmpty(str) || isEmpty(sub)) return 0;

		int count = 0;
		int idx = 0;

		while ((idx = str.indexOf(sub, idx)) != -1)
		{
			count++;
			idx += sub.length();
		}

		return count;
	}

	/**
	 * <p>Checks if the String contains only unicode letters.</p>
	 * <p/>
	 * <p><code>null</code> will return <code>false</code>.
	 * An empty String ("") will return <code>true</code>.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.isAlpha(null)   = false
	 * StringUtility.isAlpha("")     = true
	 * StringUtility.isAlpha("  ")   = false
	 * StringUtility.isAlpha("abc")  = true
	 * StringUtility.isAlpha("ab2c") = false
	 * StringUtility.isAlpha("ab-c") = false
	 * </pre>
	 *
	 * @param str the String to check, may be null
	 * @return <code>true</code> if only contains letters, and is non-null
	 */
	public static boolean isAlpha(String str)
	{
		if (str == null) return false;

		int sz = str.length();
		for (int i = 0; i < sz; i++)
		{
			if (!Character.isLetter(str.charAt(i))) return false;
		}

		return true;
	}

	/**
	 * <p>Checks if the String contains only unicode letters and
	 * space (' ').</p>
	 * <p/>
	 * <p><code>null</code> will return <code>false</code>
	 * An empty String ("") will return <code>true</code>.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.isAlphaSpace(null)   = false
	 * StringUtility.isAlphaSpace("")     = true
	 * StringUtility.isAlphaSpace("  ")   = true
	 * StringUtility.isAlphaSpace("abc")  = true
	 * StringUtility.isAlphaSpace("ab c") = true
	 * StringUtility.isAlphaSpace("ab2c") = false
	 * StringUtility.isAlphaSpace("ab-c") = false
	 * </pre>
	 *
	 * @param str the String to check, may be null
	 * @return <code>true</code> if only contains letters and space,
	 *         and is non-null
	 */
	public static boolean isAlphaSpace(String str)
	{
		if (str == null) return false;

		int sz = str.length();
		for (int i = 0; i < sz; i++)
		{
			if (!Character.isLetter(str.charAt(i)) && (str.charAt(i) != ' ')) return false;
		}

		return true;
	}

	/**
	 * <p>Checks if the String contains only unicode letters or digits.</p>
	 * <p/>
	 * <p><code>null</code> will return <code>false</code>.
	 * An empty String ("") will return <code>true</code>.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.isAlphanumeric(null)   = false
	 * StringUtility.isAlphanumeric("")     = true
	 * StringUtility.isAlphanumeric("  ")   = false
	 * StringUtility.isAlphanumeric("abc")  = true
	 * StringUtility.isAlphanumeric("ab c") = false
	 * StringUtility.isAlphanumeric("ab2c") = true
	 * StringUtility.isAlphanumeric("ab-c") = false
	 * </pre>
	 *
	 * @param str the String to check, may be null
	 * @return <code>true</code> if only contains letters or digits,
	 *         and is non-null
	 */
	public static boolean isAlphanumeric(String str)
	{
		if (str == null) return false;

		int sz = str.length();
		for (int i = 0; i < sz; i++)
		{
			if (Character.isLetterOrDigit(str.charAt(i)) == false) return false;
		}

		return true;
	}

	/**
	 * <p>Checks if the String contains only unicode letters, digits
	 * or space (<code>' '</code>).</p>
	 * <p/>
	 * <p><code>null</code> will return <code>false</code>.
	 * An empty String ("") will return <code>true</code>.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.isAlphanumeric(null)   = false
	 * StringUtility.isAlphanumeric("")     = true
	 * StringUtility.isAlphanumeric("  ")   = true
	 * StringUtility.isAlphanumeric("abc")  = true
	 * StringUtility.isAlphanumeric("ab c") = true
	 * StringUtility.isAlphanumeric("ab2c") = true
	 * StringUtility.isAlphanumeric("ab-c") = false
	 * </pre>
	 *
	 * @param str the String to check, may be null
	 * @return <code>true</code> if only contains letters, digits or space,
	 *         and is non-null
	 */
	public static boolean isAlphanumericSpace(String str)
	{
		if (str == null) return false;

		int sz = str.length();
		for (int i = 0; i < sz; i++)
		{
			if ((Character.isLetterOrDigit(str.charAt(i)) == false) && (str.charAt(i) != ' ')) return false;
		}

		return true;
	}

	/**
	 * <p>Checks if the string contains only ASCII printable characters.</p>
	 * <p/>
	 * <p><code>null</code> will return <code>false</code>.
	 * An empty String ("") will return <code>true</code>.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.isAsciiPrintable(null)     = false
	 * StringUtility.isAsciiPrintable("")       = true
	 * StringUtility.isAsciiPrintable(" ")      = true
	 * StringUtility.isAsciiPrintable("Ceki")   = true
	 * StringUtility.isAsciiPrintable("ab2c")   = true
	 * StringUtility.isAsciiPrintable("!ab-c~") = true
	 * StringUtility.isAsciiPrintable("\u0020") = true
	 * StringUtility.isAsciiPrintable("\u0021") = true
	 * StringUtility.isAsciiPrintable("\u007e") = true
	 * StringUtility.isAsciiPrintable("\u007f") = false
	 * StringUtility.isAsciiPrintable("Ceki G\u00fclc\u00fc") = false
	 * </pre>
	 *
	 * @param str the string to check, may be null
	 * @return <code>true</code> if every character is in the range
	 *         32 thru 126
	 */
	public static boolean isAsciiPrintable(String str)
	{
		if (str == null) return false;

		int sz = str.length();
		for (int i = 0; i < sz; i++)
		{
			if (CharUtility.isAsciiPrintable(str.charAt(i)) == false) return false;
		}

		return true;
	}

	/**
	 * <p>Checks if the String contains only unicode digits.
	 * A decimal point is not a unicode digit and returns false.</p>
	 * <p/>
	 * <p><code>null</code> will return <code>false</code>.
	 * An empty String ("") will return <code>true</code>.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.isNumeric(null)   = false
	 * StringUtility.isNumeric("")     = true
	 * StringUtility.isNumeric("  ")   = false
	 * StringUtility.isNumeric("123")  = true
	 * StringUtility.isNumeric("12 3") = false
	 * StringUtility.isNumeric("ab2c") = false
	 * StringUtility.isNumeric("12-3") = false
	 * StringUtility.isNumeric("12.3") = false
	 * </pre>
	 *
	 * @param str the String to check, may be null
	 * @return <code>true</code> if only contains digits, and is non-null
	 */
	public static boolean isNumeric(String str)
	{
		if (str == null) return false;

		int sz = str.length();
		for (int i = 0; i < sz; i++)
		{
			if (!Character.isDigit(str.charAt(i))) return false;
		}

		return true;
	}

	/**
	 * <p>Checks if the String contains only unicode digits or space
	 * (<code>' '</code>).
	 * A decimal point is not a unicode digit and returns false.</p>
	 * <p/>
	 * <p><code>null</code> will return <code>false</code>.
	 * An empty String ("") will return <code>true</code>.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.isNumeric(null)   = false
	 * StringUtility.isNumeric("")     = true
	 * StringUtility.isNumeric("  ")   = true
	 * StringUtility.isNumeric("123")  = true
	 * StringUtility.isNumeric("12 3") = true
	 * StringUtility.isNumeric("ab2c") = false
	 * StringUtility.isNumeric("12-3") = false
	 * StringUtility.isNumeric("12.3") = false
	 * </pre>
	 *
	 * @param str the String to check, may be null
	 * @return <code>true</code> if only contains digits or space,
	 *         and is non-null
	 */
	public static boolean isNumericSpace(String str)
	{
		if (str == null) return false;

		int sz = str.length();
		for (int i = 0; i < sz; i++)
		{
			if ((Character.isDigit(str.charAt(i)) == false) && (str.charAt(i) != ' ')) return false;
		}

		return true;
	}

	/**
	 * <p>Checks if the String contains only whitespace.</p>
	 * <p/>
	 * <p><code>null</code> will return <code>false</code>.
	 * An empty String ("") will return <code>true</code>.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.isWhitespace(null)   = false
	 * StringUtility.isWhitespace("")     = true
	 * StringUtility.isWhitespace("  ")   = true
	 * StringUtility.isWhitespace("abc")  = false
	 * StringUtility.isWhitespace("ab2c") = false
	 * StringUtility.isWhitespace("ab-c") = false
	 * </pre>
	 *
	 * @param str the String to check, may be null
	 * @return <code>true</code> if only contains whitespace, and is non-null
	 */
	public static boolean isWhitespace(String str)
	{
		if (str == null)
		{
			return false;
		}
		int sz = str.length();
		for (int i = 0; i < sz; i++)
		{
			if ((Character.isWhitespace(str.charAt(i)) == false))
			{
				return false;
			}
		}
		return true;
	}

	/**
	 * <p>Returns either the passed in String,
	 * or if the String is <code>null</code>, an empty String ("").</p>
	 * <p/>
	 * <pre>
	 * StringUtility.defaultString(null)  = ""
	 * StringUtility.defaultString("")    = ""
	 * StringUtility.defaultString("bat") = "bat"
	 * </pre>
	 *
	 * @param str the String to check, may be null
	 * @return the passed in String, or the empty String if it
	 *         was <code>null</code>
	 * @see ObjectUtility#toString(Object)
	 * @see String#valueOf(Object)
	 */
	public static String defaultString(String str)
	{
		return str == null ? EMPTY : str;
	}

	/**
	 * <p>Returns either the passed in String, or if the String is
	 * <code>null</code>, the value of <code>defaultStr</code>.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.defaultString(null, "NULL")  = "NULL"
	 * StringUtility.defaultString("", "NULL")    = ""
	 * StringUtility.defaultString("bat", "NULL") = "bat"
	 * </pre>
	 *
	 * @param str		the String to check, may be null
	 * @param defaultStr the default String to return
	 *                   if the input is <code>null</code>, may be null
	 * @return the passed in String, or the default if it was <code>null</code>
	 * @see ObjectUtility#toString(Object, String)
	 * @see String#valueOf(Object)
	 */
	public static String defaultString(String str, String defaultStr)
	{
		return str == null ? defaultStr : str;
	}

	/**
	 * <p>Returns either the passed in String, or if the String is
	 * empty or <code>null</code>, the value of <code>defaultStr</code>.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.defaultIfEmpty(null, "NULL")  = "NULL"
	 * StringUtility.defaultIfEmpty("", "NULL")    = "NULL"
	 * StringUtility.defaultIfEmpty("bat", "NULL") = "bat"
	 * </pre>
	 *
	 * @param str		the String to check, may be null
	 * @param defaultStr the default String to return
	 *                   if the input is empty ("") or <code>null</code>, may be null
	 * @return the passed in String, or the default
	 * @see StringUtility#defaultString(String, String)
	 */
	public static String defaultIfEmpty(String str, String defaultStr)
	{
		return StringUtility.isEmpty(str) ? defaultStr : str;
	}

	/**
	 * <p>Reverses a String as per {@link StringBuffer#reverse()}.</p>
	 * <p/>
	 * <p>A <code>null</code> String returns <code>null</code>.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.reverse(null)  = null
	 * StringUtility.reverse("")    = ""
	 * StringUtility.reverse("bat") = "tab"
	 * </pre>
	 *
	 * @param str the String to reverse, may be null
	 * @return the reversed String, <code>null</code> if null String input
	 */
	public static String reverse(String str)
	{
		if (str == null)
		{
			return null;
		}
		return new StringBuffer(str).reverse().toString();
	}

	/**
	 * <p>Reverses a String that is delimited by a specific character.</p>
	 * <p/>
	 * <p>The Strings between the delimiters are not reversed.
	 * Thus java.lang.String becomes String.lang.java (if the delimiter
	 * is <code>'.'</code>).</p>
	 * <p/>
	 * <pre>
	 * StringUtility.reverseDelimited(null, *)      = null
	 * StringUtility.reverseDelimited("", *)        = ""
	 * StringUtility.reverseDelimited("a.b.c", 'x') = "a.b.c"
	 * StringUtility.reverseDelimited("a.b.c", ".") = "c.b.a"
	 * </pre>
	 *
	 * @param str		   the String to reverse, may be null
	 * @param separatorChar the separator character to use
	 * @return the reversed String, <code>null</code> if null String input
	 */
	public static String reverseDelimited(String str, char separatorChar)
	{
		if (str == null) return null;

		// could implement manually, but simple way is to reuse other, probably slower, methods.
		String[] strs = split(str, separatorChar);
		ArrayUtility.reverse(strs);

		return join(strs, separatorChar);
	}

	/**
	 * <p>Abbreviates a String using ellipses. This will turn
	 * "Now is the time for all good men" into "Now is the time for..."</p>
	 * <p/>
	 * <p>Specifically:
	 * <ul>
	 * <li>If <code>str</code> is less than <code>maxWidth</code> characters
	 * long, return it.</li>
	 * <li>Else abbreviate it to <code>(substring(str, 0, max-3) + "...")</code>.</li>
	 * <li>If <code>maxWidth</code> is less than <code>4</code>, throw an
	 * <code>IllegalArgumentException</code>.</li>
	 * <li>In no case will it return a String of length greater than
	 * <code>maxWidth</code>.</li>
	 * </ul>
	 * </p>
	 * <p/>
	 * <pre>
	 * StringUtility.abbreviate(null, *)      = null
	 * StringUtility.abbreviate("", 4)        = ""
	 * StringUtility.abbreviate("abcdefg", 6) = "abc..."
	 * StringUtility.abbreviate("abcdefg", 7) = "abcdefg"
	 * StringUtility.abbreviate("abcdefg", 8) = "abcdefg"
	 * StringUtility.abbreviate("abcdefg", 4) = "a..."
	 * StringUtility.abbreviate("abcdefg", 3) = IllegalArgumentException
	 * </pre>
	 *
	 * @param str	  the String to check, may be null
	 * @param maxWidth maximum length of result String, must be at least 4
	 * @return abbreviated String, <code>null</code> if null String input
	 * @throws IllegalArgumentException if the width is too small
	 */
	public static String abbreviate(String str, int maxWidth)
	{
		return abbreviate(str, 0, maxWidth);
	}

	/**
	 * <p>Abbreviates a String using ellipses. This will turn
	 * "Now is the time for all good men" into "...is the time for..."</p>
	 * <p/>
	 * <p>Works like <code>abbreviate(String, int)</code>, but allows you to specify
	 * a "left edge" offset.  Note that this left edge is not necessarily going to
	 * be the leftmost character in the result, or the first character following the
	 * ellipses, but it will appear somewhere in the result.
	 * <p/>
	 * <p>In no case will it return a String of length greater than
	 * <code>maxWidth</code>.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.abbreviate(null, *, *)                = null
	 * StringUtility.abbreviate("", 0, 4)                  = ""
	 * StringUtility.abbreviate("abcdefghijklmno", -1, 10) = "abcdefg..."
	 * StringUtility.abbreviate("abcdefghijklmno", 0, 10)  = "abcdefg..."
	 * StringUtility.abbreviate("abcdefghijklmno", 1, 10)  = "abcdefg..."
	 * StringUtility.abbreviate("abcdefghijklmno", 4, 10)  = "abcdefg..."
	 * StringUtility.abbreviate("abcdefghijklmno", 5, 10)  = "...fghi..."
	 * StringUtility.abbreviate("abcdefghijklmno", 6, 10)  = "...ghij..."
	 * StringUtility.abbreviate("abcdefghijklmno", 8, 10)  = "...ijklmno"
	 * StringUtility.abbreviate("abcdefghijklmno", 10, 10) = "...ijklmno"
	 * StringUtility.abbreviate("abcdefghijklmno", 12, 10) = "...ijklmno"
	 * StringUtility.abbreviate("abcdefghij", 0, 3)        = IllegalArgumentException
	 * StringUtility.abbreviate("abcdefghij", 5, 6)        = IllegalArgumentException
	 * </pre>
	 *
	 * @param str	  the String to check, may be null
	 * @param offset   left edge of source String
	 * @param maxWidth maximum length of result String, must be at least 4
	 * @return abbreviated String, <code>null</code> if null String input
	 * @throws IllegalArgumentException if the width is too small
	 */
	public static String abbreviate(String str, int offset, int maxWidth)
	{
		if (str == null)
		{
			return null;
		}
		if (maxWidth < 4)
		{
			throw new IllegalArgumentException("Minimum abbreviation width is 4");
		}
		if (str.length() <= maxWidth)
		{
			return str;
		}
		if (offset > str.length())
		{
			offset = str.length();
		}
		if ((str.length() - offset) < (maxWidth - 3))
		{
			offset = str.length() - (maxWidth - 3);
		}
		if (offset <= 4)
		{
			return str.substring(0, maxWidth - 3) + "...";
		}
		if (maxWidth < 7)
		{
			throw new IllegalArgumentException("Minimum abbreviation width with offset is 7");
		}
		if ((offset + (maxWidth - 3)) < str.length())
		{
			return "..." + abbreviate(str.substring(offset), maxWidth - 3);
		}
		return "..." + str.substring(str.length() - (maxWidth - 3));
	}

	/**
	 * <p>Compares two Strings, and returns the portion where they differ.
	 * (More precisely, return the remainder of the second String,
	 * starting from where it's different from the first.)</p>
	 * <p/>
	 * <p>For example,
	 * <code>difference("i am a machine", "i am a robot") -> "robot"</code>.</p>
	 * <p/>
	 * <pre>
	 * StringUtility.difference(null, null) = null
	 * StringUtility.difference("", "") = ""
	 * StringUtility.difference("", "abc") = "abc"
	 * StringUtility.difference("abc", "") = ""
	 * StringUtility.difference("abc", "abc") = ""
	 * StringUtility.difference("ab", "abxyz") = "xyz"
	 * StringUtility.difference("abcde", "abxyz") = "xyz"
	 * StringUtility.difference("abcde", "xyz") = "xyz"
	 * </pre>
	 *
	 * @param str1 the first String, may be null
	 * @param str2 the second String, may be null
	 * @return the portion of str2 where it differs from str1; returns the
	 *         empty String if they are equal
	 */
	public static String difference(String str1, String str2)
	{
		if (str1 == null) return str2;

		if (str2 == null) return str1;

		int at = indexOfDifference(str1, str2);
		if (at == -1) return EMPTY;

		return str2.substring(at);
	}

	/**
	 * <p>Compares two Strings, and returns the index at which the
	 * Strings begin to differ.</p>
	 * <p/>
	 * <p>For example,
	 * <code>indexOfDifference("i am a machine", "i am a robot") -> 7</code></p>
	 * <p/>
	 * <pre>
	 * StringUtility.indexOfDifference(null, null) = -1
	 * StringUtility.indexOfDifference("", "") = -1
	 * StringUtility.indexOfDifference("", "abc") = 0
	 * StringUtility.indexOfDifference("abc", "") = 0
	 * StringUtility.indexOfDifference("abc", "abc") = -1
	 * StringUtility.indexOfDifference("ab", "abxyz") = 2
	 * StringUtility.indexOfDifference("abcde", "abxyz") = 2
	 * StringUtility.indexOfDifference("abcde", "xyz") = 0
	 * </pre>
	 *
	 * @param str1 the first String, may be null
	 * @param str2 the second String, may be null
	 * @return the index where str2 and str1 begin to differ; -1 if they are equal
	 */
	public static int indexOfDifference(String str1, String str2)
	{
		if (str1 == str2) return -1;

		if (str1 == null || str2 == null) return 0;

		int i;
		for (i = 0; i < str1.length() && i < str2.length(); ++i)
		{
			if (str1.charAt(i) != str2.charAt(i)) break;
		}

		if (i < str2.length() || i < str1.length())
		{
			return i;
		}

		return -1;
	}

	/**
	 * <p>Find the Levenshtein distance between two Strings.</p>
	 * <p/>
	 * <p>This is the number of changes needed to change one String into
	 * another, where each change is a single character modification (deletion,
	 * insertion or substitution).</p>
	 * <p/>
	 * <p>This implementation of the Levenshtein distance algorithm
	 * is from <a href="http://www.merriampark.com/ld.htm">http://www.merriampark.com/ld.htm</a></p>
	 * <p/>
	 * <pre>
	 * StringUtility.getLevenshteinDistance(null, *)             = IllegalArgumentException
	 * StringUtility.getLevenshteinDistance(*, null)             = IllegalArgumentException
	 * StringUtility.getLevenshteinDistance("","")               = 0
	 * StringUtility.getLevenshteinDistance("","a")              = 1
	 * StringUtility.getLevenshteinDistance("aaapppp", "")       = 7
	 * StringUtility.getLevenshteinDistance("frog", "fog")       = 1
	 * StringUtility.getLevenshteinDistance("fly", "ant")        = 3
	 * StringUtility.getLevenshteinDistance("elephant", "hippo") = 7
	 * StringUtility.getLevenshteinDistance("hippo", "elephant") = 7
	 * StringUtility.getLevenshteinDistance("hippo", "zzzzzzzz") = 8
	 * StringUtility.getLevenshteinDistance("hello", "hallo")    = 1
	 * </pre>
	 *
	 * @param s the first String, must not be null
	 * @param t the second String, must not be null
	 * @return result distance
	 * @throws IllegalArgumentException if either String input <code>null</code>
	 */
	public static int getLevenshteinDistance(String s, String t)
	{
		if (s == null || t == null)
		{
			throw new IllegalArgumentException("Strings must not be null");
		}
		int d[][]; // matrix
		int n; // length of s
		int m; // length of t
		int i; // iterates through s
		int j; // iterates through t
		char s_i; // ith character of s
		char t_j; // jth character of t
		int cost; // cost

		// Step 1
		n = s.length();
		m = t.length();
		if (n == 0) return m;
		if (m == 0) return n;

		d = new int[n + 1][m + 1];

		// Step 2
		for (i = 0; i <= n; i++)
		{
			d[i][0] = i;
		}

		for (j = 0; j <= m; j++)
		{
			d[0][j] = j;
		}

		// Step 3
		for (i = 1; i <= n; i++)
		{
			s_i = s.charAt(i - 1);

			// Step 4
			for (j = 1; j <= m; j++)
			{
				t_j = t.charAt(j - 1);

				// Step 5
				if (s_i == t_j) cost = 0;

				else cost = 1;

				// Step 6
				d[i][j] = min(d[i - 1][j] + 1, d[i][j - 1] + 1, d[i - 1][j - 1] + cost);
			}
		}

		// Step 7
		return d[n][m];
	}

	/**
	 * <p>Gets the minimum of three <code>int</code> values.</p>
	 *
	 * @param a value 1
	 * @param b value 2
	 * @param c value 3
	 * @return the smallest of the values
	 */
	private static int min(int a, int b, int c)
	{
		// Method copied from NumberUtility to avoid dependency on subpackage
		if (b < a) a = b;

		if (c < a) a = c;

		return a;
	}

	/**
	 * Remove/collapse multiple spaces.
	 *
	 * @param argStr string to remove multiple spaces from.
	 * @return String
	 */
	public static String collapseSpaces(String argStr)
	{
		char last = argStr.charAt(0);
		StringBuffer argBuf = new StringBuffer();

		for (int cIdx = 0; cIdx < argStr.length(); cIdx++)
		{
			char ch = argStr.charAt(cIdx);
			if (ch != ' ' || last != ' ')
			{
				argBuf.append(ch);
				last = ch;
			}
		}

		return argBuf.toString();
	}

	/**
	 * Append a new string arguments into an original text.
	 */
	public static String append(String original, String argStr)
	{
		if (isNotEmpty(argStr)) return (original + argStr);
		else return original;
	}

	/**
	 * Replaces all instances of oldString with newString in line.
	 * Taken from the Jive forum package.
	 *
	 * @param line	  original string.
	 * @param oldString string in line to replace.
	 * @param newString replace oldString with this.
	 * @return String string with replacements.
	 */
	public static final String sub(String line, String oldString, String newString)
	{
		int i = 0;
		if ((i = line.indexOf(oldString, i)) >= 0)
		{
			char[] line2 = line.toCharArray();
			char[] newString2 = newString.toCharArray();

			int oLength = oldString.length();

			StringBuffer buf = new StringBuffer(line2.length);
			buf.append(line2, 0, i).append(newString2);

			i += oLength;
			int j = i;

			while ((i = line.indexOf(oldString, i)) > 0)
			{
				buf.append(line2, j, i - j).append(newString2);
				i += oLength;
				j = i;
			}

			buf.append(line2, j, line2.length - j);

			return buf.toString();
		}

		return line;
	}

	/**
	 * If state is true then return the trueString, else
	 * return the falseString.
	 */
	public static String select(boolean state, String trueString, String falseString)
	{
		if (state) return trueString;
		else return falseString;
	}

	/**
	 * Return a context-relative path, beginning with a "/", that represents
	 * the canonical version of the specified path after ".." and "." elements
	 * are resolved out.  If the specified path attempts to go outside the
	 * boundaries of the current context (i.e. too many ".." path elements
	 * are present), return <code>null</code> instead.
	 *
	 * @param path Path to be normalized
	 * @return String normalized path
	 */
	public static final String normalizePath(String path)
	{
		// Normalize the slashes and add leading slash if necessary
		String normalized = path;
		if (normalized.indexOf('\\') >= 0) normalized = normalized.replace('\\', '/');

		if (!normalized.startsWith("/")) normalized = "/" + normalized;

		// Resolve occurrences of "//" in the normalized path
		while (true)
		{
			int index = normalized.indexOf("//");
			if (index < 0) break;

			normalized = normalized.substring(0, index) + normalized.substring(index + 1);
		}

		// Resolve occurrences of "%20" in the normalized path
		while (true)
		{
			int index = normalized.indexOf("%20");
			if (index < 0) break;

			normalized = normalized.substring(0, index) + " " + normalized.substring(index + 3);
		}

		// Resolve occurrences of "/./" in the normalized path
		while (true)
		{
			int index = normalized.indexOf("/./");
			if (index < 0) break;

			normalized = normalized.substring(0, index) + normalized.substring(index + 2);
		}

		// Resolve occurrences of "/../" in the normalized path
		while (true)
		{
			int index = normalized.indexOf("/../");

			if (index < 0) break;
			if (index == 0) return (null);  // Trying to go outside our context

			int index2 = normalized.lastIndexOf('/', index - 1);
			normalized = normalized.substring(0, index2) + normalized.substring(index + 3);
		}

		// Return the normalized path that we have completed
		return (normalized);
	}

	/**
	 * This method will forma a plain text making all replacement to be printed in HTML
	 * format. All white spaces will be replaced with <code><pre>&nbsp;</pre></code>
	 * escape sequence and all end-lines will be replaced with <code>BR</code> html tag.
	 *
	 * @param value plain text
	 * @return <code>String</code> formatter value.
	 */
	public static String getHtmlValue(String value)
	{
		if (StringUtility.isNotEmpty(value))
		{
			value = replace(value, "\n", "<br/>");
			value = replace(value, "\t", "&nbsp;&nbsp;&nbsp;&nbsp;");
			value = replace(value, " ", "&nbsp;");
		}

		return value;
	}

	/**
	 * Get Html value from plain text value. If parsed value is empty will be returned <code><pre>&nbsp;</pre></code>
	 * escape sequence.
	 *
	 * @param value plain text
	 */
	public static String getHtmlValueWithoutNull(String value)
	{
		value = getHtmlValue(value);

		if (StringUtility.isEmpty(value)) return "&nbsp;";
		else return value;
	}

	/**
	 * Get variable format from a string.
	 * This method will replace punctuation and blank spaces with underscore
	 *
	 * @param text string for transformation into a variable
	 * @return a text without special chars
	 */
	public static String escapeSpecialChars(String text)
	{
		if (text == null || text.trim().length() == 0) return text;

		text = text.replaceAll("\\p{Punct}", "_");
		text = text.replaceAll("\\s", "_");

		return text;
	}

	/**
	 * Get variable format from a string.
	 * This method will replace punctuation and blank spaces with underscore
	 *
	 * @param text string for transformation into a variable
	 * @return a text without special chars in variable format
	 */
	public static String variable(String text)
	{
		if(isNotEmpty(text))
		{
			text = escapeSpecialChars(text);

			if (NumberUtility.toInt(text.substring(0, 1), -1) >= 0) text = "v" + text;
		}
		else text = "v" + new Date().getTime();

		return text;
	}

	/**
	 * <p>Checks if the String contains any character in the given
	 * set of characters.</p>
	 * <p/>
	 * <p>A <code>null</code> String will return <code>false</code>.
	 * A <code>null</code> or zero length search array will return <code>false</code>.</p>
	 * <p/>
	 * <pre>
	 * StringUtils.containsAny(null, *)                = false
	 * StringUtils.containsAny("", *)                  = false
	 * StringUtils.containsAny(*, null)                = false
	 * StringUtils.containsAny(*, [])                  = false
	 * StringUtils.containsAny("zzabyycdxx",['z','a']) = true
	 * StringUtils.containsAny("zzabyycdxx",['b','y']) = true
	 * StringUtils.containsAny("aba", ['z'])           = false
	 * </pre>
	 *
	 * @param str		 the String to check, may be null
	 * @param searchChars the chars to search for, may be null
	 * @return the <code>true</code> if any of the chars are found,
	 *         <code>false</code> if no match or null input
	 */
	public static boolean containsAny(String str, char[] searchChars)
	{
		if (isEmpty(str) || ArrayUtility.isEmpty(searchChars)) return false;

		int csLength = str.length();
		int searchLength = searchChars.length;
		int csLast = csLength - 1;
		int searchLast = searchLength - 1;

		for (int i = 0; i < csLength; i++)
		{
			char ch = str.charAt(i);

			for (int j = 0; j < searchLength; j++)
			{
				if (searchChars[j] == ch)
				{
					if (CharUtility.isHighSurrogate(ch))
					{
						if (j == searchLast)
						{
							// missing low surrogate, fine, like String.indexOf(String)
							return true;
						}

						if (i < csLast && searchChars[j + 1] == str.charAt(i + 1))
						{
							return true;
						}
					}
					else
					{
						// ch is in the Basic Multilingual Plane
						return true;
					}
				}
			}
		}

		return false;
	}

	/**
	 * <p>
	 * Checks if the String contains any character in the given set of characters.
	 * </p>
	 * <p/>
	 * <p>
	 * A <code>null</code> String will return <code>false</code>. A <code>null</code> search string will return
	 * <code>false</code>.
	 * </p>
	 * <p/>
	 * <pre>
	 * StringUtils.containsAny(null, *)            = false
	 * StringUtils.containsAny("", *)              = false
	 * StringUtils.containsAny(*, null)            = false
	 * StringUtils.containsAny(*, "")              = false
	 * StringUtils.containsAny("zzabyycdxx", "za") = true
	 * StringUtils.containsAny("zzabyycdxx", "by") = true
	 * StringUtils.containsAny("aba","z")          = false
	 * </pre>
	 *
	 * @param str		 the String to check, may be null
	 * @param searchChars the chars to search for, may be null
	 * @return the <code>true</code> if any of the chars are found, <code>false</code> if no match or null input
	 * @since 2.4
	 */
	public static boolean containsAny(String str, String searchChars)
	{
		if (searchChars == null) return false;

		return containsAny(str, searchChars.toCharArray());
	}
}
