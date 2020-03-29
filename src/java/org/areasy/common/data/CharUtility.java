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
 * <p>Operations on char primitives and Character objects.</p>
 * <p/>
 * <p>This class tries to handle <code>null</code> input gracefully.
 * An exception will not be thrown for a <code>null</code> input.
 * Each method documents its behaviour in more detail.</p>
 *
 * @version $Id: CharUtility.java,v 1.2 2008/05/14 09:32:29 swd\stefan.damian Exp $
 */
public class CharUtility
{

	private static final String CHAR_STRING =
			"\u0000\u0001\u0002\u0003\u0004\u0005\u0006\u0007" +
			"\b\t\n\u000b\f\r\u000e\u000f" +
			"\u0010\u0011\u0012\u0013\u0014\u0015\u0016\u0017" +
			"\u0018\u0019\u001a\u001b\u001c\u001d\u001e\u001f" +
			"\u0020\u0021\"\u0023\u0024\u0025\u0026\u0027" +
			"\u0028\u0029\u002a\u002b\u002c\u002d\u002e\u002f" +
			"\u0030\u0031\u0032\u0033\u0034\u0035\u0036\u0037" +
			"\u0038\u0039\u003a\u003b\u003c\u003d\u003e\u003f" +
			"\u0040\u0041\u0042\u0043\u0044\u0045\u0046\u0047" +
			"\u0048\u0049\u004a\u004b\u004c\u004d\u004e\u004f" +
			"\u0050\u0051\u0052\u0053\u0054\u0055\u0056\u0057" +
			"\u0058\u0059\u005a\u005b\\\u005d\u005e\u005f" +
			"\u0060\u0061\u0062\u0063\u0064\u0065\u0066\u0067" +
			"\u0068\u0069\u006a\u006b\u006c\u006d\u006e\u006f" +
			"\u0070\u0071\u0072\u0073\u0074\u0075\u0076\u0077" +
			"\u0078\u0079\u007a\u007b\u007c\u007d\u007e\u007f";

	private static final String[] CHAR_STRING_ARRAY = new String[128];
	private static final Character[] CHAR_ARRAY = new Character[128];

    /**
     * <code>\u000a</code> linefeed LF ('\n').
     *
     * @see <a href="http://java.sun.com/docs/books/jls/third_edition/html/lexical.html#101089">JLF: Escape Sequences
     *      for Character and String Literals</a>
     */
    public static final char LF = '\n';

    /**
     * <code>\u000d</code> carriage return CR ('\r').
     *
     * @see <a href="http://java.sun.com/docs/books/jls/third_edition/html/lexical.html#101089">JLF: Escape Sequences
     *      for Character and String Literals</a>
     */
    public static final char CR = '\r';

	static
	{
		for (int i = 127; i >= 0; i--)
		{
			CHAR_STRING_ARRAY[i] = CHAR_STRING.substring(i, i + 1);
			CHAR_ARRAY[i] = new Character((char) i);
		}
	}

	/**
	 * <p><code>CharUtility</code> instances should NOT be constructed in standard programming.
	 * Instead, the class should be used as <code>CharUtility.toString('c');</code>.</p>
	 * <p/>
	 * <p>This constructor is public to permit tools that require a JavaBean instance
	 * to operate.</p>
	 */
	public CharUtility()
	{
	}

	/**
	 * <p>Converts the character to a Character.</p>
	 * <p/>
	 * <p>For ASCII 7 bit characters, this uses a cache that will return the
	 * same Character object each time.</p>
	 * <p/>
	 * <pre>
	 *   CharUtility.toCharacterObject(' ')  = ' '
	 *   CharUtility.toCharacterObject('A')  = 'A'
	 * </pre>
	 *
	 * @param ch the character to convert
	 * @return a Character of the specified character
	 */
	public static Character toCharacterObject(char ch)
	{
		if (ch < CHAR_ARRAY.length)
		{
			return CHAR_ARRAY[ch];
		}
		else
		{
			return new Character(ch);
		}
	}

	/**
	 * <p>Converts the String to a Character using the first character, returning
	 * null for empty Strings.</p>
	 * <p/>
	 * <p>For ASCII 7 bit characters, this uses a cache that will return the
	 * same Character object each time.</p>
	 * <p/>
	 * <pre>
	 *   CharUtility.toCharacterObject(null) = null
	 *   CharUtility.toCharacterObject("")   = null
	 *   CharUtility.toCharacterObject("A")  = 'A'
	 *   CharUtility.toCharacterObject("BA") = 'B'
	 * </pre>
	 *
	 * @param str the character to convert
	 * @return the Character value of the first letter of the String
	 */
	public static Character toCharacterObject(String str)
	{
		if (StringUtility.isEmpty(str))
		{
			return null;
		}
		return toCharacterObject(str.charAt(0));
	}

	/**
	 * <p>Converts the Character to a char throwing an exception for <code>null</code>.</p>
	 * <p/>
	 * <pre>
	 *   CharUtility.toChar(null) = IllegalArgumentException
	 *   CharUtility.toChar(' ')  = ' '
	 *   CharUtility.toChar('A')  = 'A'
	 * </pre>
	 *
	 * @param ch the character to convert
	 * @return the char value of the Character
	 * @throws IllegalArgumentException if the Character is null
	 */
	public static char toChar(Character ch)
	{
		if (ch == null)
		{
			throw new IllegalArgumentException("The Character must not be null");
		}
		return ch.charValue();
	}

	/**
	 * <p>Converts the Character to a char handling <code>null</code>.</p>
	 * <p/>
	 * <pre>
	 *   CharUtility.toChar(null, 'X') = 'X'
	 *   CharUtility.toChar(' ', 'X')  = ' '
	 *   CharUtility.toChar('A', 'X')  = 'A'
	 * </pre>
	 *
	 * @param ch           the character to convert
	 * @param defaultValue the value to use if the  Character is null
	 * @return the char value of the Character or the default if null
	 */
	public static char toChar(Character ch, char defaultValue)
	{
		if (ch == null)
		{
			return defaultValue;
		}
		return ch.charValue();
	}

	/**
	 * <p>Converts the String to a char using the first character, throwing
	 * an exception on empty Strings.</p>
	 * <p/>
	 * <pre>
	 *   CharUtility.toChar(null) = IllegalArgumentException
	 *   CharUtility.toChar("")   = IllegalArgumentException
	 *   CharUtility.toChar("A")  = 'A'
	 *   CharUtility.toChar("BA") = 'B'
	 * </pre>
	 *
	 * @param str the character to convert
	 * @return the char value of the first letter of the String
	 * @throws IllegalArgumentException if the String is empty
	 */
	public static char toChar(String str)
	{
		if (StringUtility.isEmpty(str))
		{
			throw new IllegalArgumentException("The String must not be empty");
		}
		return str.charAt(0);
	}

	/**
	 * <p>Converts the String to a char using the first character, defaulting
	 * the value on empty Strings.</p>
	 * <p/>
	 * <pre>
	 *   CharUtility.toChar(null, 'X') = 'X'
	 *   CharUtility.toChar("", 'X')   = 'X'
	 *   CharUtility.toChar("A", 'X')  = 'A'
	 *   CharUtility.toChar("BA", 'X') = 'B'
	 * </pre>
	 *
	 * @param str          the character to convert
	 * @param defaultValue the value to use if the  Character is null
	 * @return the char value of the first letter of the String or the default if null
	 */
	public static char toChar(String str, char defaultValue)
	{
		if (StringUtility.isEmpty(str))
		{
			return defaultValue;
		}
		return str.charAt(0);
	}

	/**
	 * <p>Converts the character to the Integer it represents, throwing an
	 * exception if the character is not numeric.</p>
	 * <p/>
	 * <p>This method coverts the char '1' to the int 1 and so on.</p>
	 * <p/>
	 * <pre>
	 *   CharUtility.toIntValue('3')  = 3
	 *   CharUtility.toIntValue('A')  = IllegalArgumentException
	 * </pre>
	 *
	 * @param ch the character to convert
	 * @return the int value of the character
	 * @throws IllegalArgumentException if the character is not ASCII numeric
	 */
	public static int toIntValue(char ch)
	{
		if (isAsciiNumeric(ch) == false)
		{
			throw new IllegalArgumentException("The character " + ch + " is not in the range '0' - '9'");
		}
		return ch - 48;
	}

	/**
	 * <p>Converts the character to the Integer it represents, throwing an
	 * exception if the character is not numeric.</p>
	 * <p/>
	 * <p>This method coverts the char '1' to the int 1 and so on.</p>
	 * <p/>
	 * <pre>
	 *   CharUtility.toIntValue('3', -1)  = 3
	 *   CharUtility.toIntValue('A', -1)  = -1
	 * </pre>
	 *
	 * @param ch           the character to convert
	 * @param defaultValue the default value to use if the character is not numeric
	 * @return the int value of the character
	 */
	public static int toIntValue(char ch, int defaultValue)
	{
		if (isAsciiNumeric(ch) == false)
		{
			return defaultValue;
		}
		return ch - 48;
	}

	/**
	 * <p>Converts the character to the Integer it represents, throwing an
	 * exception if the character is not numeric.</p>
	 * <p/>
	 * <p>This method coverts the char '1' to the int 1 and so on.</p>
	 * <p/>
	 * <pre>
	 *   CharUtility.toIntValue(null) = IllegalArgumentException
	 *   CharUtility.toIntValue('3')  = 3
	 *   CharUtility.toIntValue('A')  = IllegalArgumentException
	 * </pre>
	 *
	 * @param ch the character to convert, not null
	 * @return the int value of the character
	 * @throws IllegalArgumentException if the Character is not ASCII numeric or is null
	 */
	public static int toIntValue(Character ch)
	{
		if (ch == null)
		{
			throw new IllegalArgumentException("The character must not be null");
		}
		return toIntValue(ch.charValue());
	}

	/**
	 * <p>Converts the character to the Integer it represents, throwing an
	 * exception if the character is not numeric.</p>
	 * <p/>
	 * <p>This method coverts the char '1' to the int 1 and so on.</p>
	 * <p/>
	 * <pre>
	 *   CharUtility.toIntValue(null, -1) = -1
	 *   CharUtility.toIntValue('3', -1)  = 3
	 *   CharUtility.toIntValue('A', -1)  = -1
	 * </pre>
	 *
	 * @param ch           the character to convert
	 * @param defaultValue the default value to use if the character is not numeric
	 * @return the int value of the character
	 */
	public static int toIntValue(Character ch, int defaultValue)
	{
		if (ch == null)
		{
			return defaultValue;
		}
		return toIntValue(ch.charValue(), defaultValue);
	}

	/**
	 * <p>Converts the character to a String that contains the one character.</p>
	 * <p/>
	 * <p>For ASCII 7 bit characters, this uses a cache that will return the
	 * same String object each time.</p>
	 * <p/>
	 * <pre>
	 *   CharUtility.toString(' ')  = " "
	 *   CharUtility.toString('A')  = "A"
	 * </pre>
	 *
	 * @param ch the character to convert
	 * @return a String containing the one specified character
	 */
	public static String toString(char ch)
	{
		if (ch < 128)
		{
			return CHAR_STRING_ARRAY[ch];
		}
		else
		{
			return new String(new char[]{ch});
		}
	}

	/**
	 * <p>Converts the character to a String that contains the one character.</p>
	 * <p/>
	 * <p>For ASCII 7 bit characters, this uses a cache that will return the
	 * same String object each time.</p>
	 * <p/>
	 * <p>If <code>null</code> is passed in, <code>null</code> will be returned.</p>
	 * <p/>
	 * <pre>
	 *   CharUtility.toString(null) = null
	 *   CharUtility.toString(' ')  = " "
	 *   CharUtility.toString('A')  = "A"
	 * </pre>
	 *
	 * @param ch the character to convert
	 * @return a String containing the one specified character
	 */
	public static String toString(Character ch)
	{
		if (ch == null)
		{
			return null;
		}
		else
		{
			return toString(ch.charValue());
		}
	}

	/**
	 * <p>Converts the string to the unicode format '\u0020'.</p>
	 * <p/>
	 * <p>This format is the Java source code format.</p>
	 * <p/>
	 * <pre>
	 *   CharUtility.unicodeEscaped(' ') = "\u0020"
	 *   CharUtility.unicodeEscaped('A') = "\u0041"
	 * </pre>
	 *
	 * @param ch the character to convert
	 * @return the escaped unicode string
	 */
	public static String unicodeEscaped(char ch)
	{
		if (ch < 0x10)
		{
			return "\\u000" + Integer.toHexString(ch);
		}
		else if (ch < 0x100)
		{
			return "\\u00" + Integer.toHexString(ch);
		}
		else if (ch < 0x1000)
		{
			return "\\u0" + Integer.toHexString(ch);
		}
		return "\\u" + Integer.toHexString(ch);
	}

	/**
	 * <p>Converts the string to the unicode format '\u0020'.</p>
	 * <p/>
	 * <p>This format is the Java source code format.</p>
	 * <p/>
	 * <p>If <code>null</code> is passed in, <code>null</code> will be returned.</p>
	 * <p/>
	 * <pre>
	 *   CharUtility.unicodeEscaped(null) = null
	 *   CharUtility.unicodeEscaped(' ')  = "\u0020"
	 *   CharUtility.unicodeEscaped('A')  = "\u0041"
	 * </pre>
	 *
	 * @param ch the character to convert, may be null
	 * @return the escaped unicode string, null if null input
	 */
	public static String unicodeEscaped(Character ch)
	{
		if (ch == null)
		{
			return null;
		}
		return unicodeEscaped(ch.charValue());
	}

	/**
	 * <p>Checks whether the character is ASCII 7 bit.</p>
	 * <p/>
	 * <pre>
	 *   CharUtility.isAscii('a')  = true
	 *   CharUtility.isAscii('A')  = true
	 *   CharUtility.isAscii('3')  = true
	 *   CharUtility.isAscii('-')  = true
	 *   CharUtility.isAscii('\n') = true
	 *   CharUtility.isAscii('&copy;') = false
	 * </pre>
	 *
	 * @param ch the character to check
	 * @return true if less than 128
	 */
	public static boolean isAscii(char ch)
	{
		return ch < 128;
	}

	/**
	 * <p>Checks whether the character is ASCII 7 bit printable.</p>
	 * <p/>
	 * <pre>
	 *   CharUtility.isAsciiPrintable('a')  = true
	 *   CharUtility.isAsciiPrintable('A')  = true
	 *   CharUtility.isAsciiPrintable('3')  = true
	 *   CharUtility.isAsciiPrintable('-')  = true
	 *   CharUtility.isAsciiPrintable('\n') = false
	 *   CharUtility.isAsciiPrintable('&copy;') = false
	 * </pre>
	 *
	 * @param ch the character to check
	 * @return true if between 32 and 126 inclusive
	 */
	public static boolean isAsciiPrintable(char ch)
	{
		return ch >= 32 && ch < 127;
	}

	/**
	 * <p>Checks whether the character is ASCII 7 bit control.</p>
	 * <p/>
	 * <pre>
	 *   CharUtility.isAsciiControl('a')  = false
	 *   CharUtility.isAsciiControl('A')  = false
	 *   CharUtility.isAsciiControl('3')  = false
	 *   CharUtility.isAsciiControl('-')  = false
	 *   CharUtility.isAsciiControl('\n') = true
	 *   CharUtility.isAsciiControl('&copy;') = false
	 * </pre>
	 *
	 * @param ch the character to check
	 * @return true if less than 32 or equals 127
	 */
	public static boolean isAsciiControl(char ch)
	{
		return ch < 32 || ch == 127;
	}

	/**
	 * <p>Checks whether the character is ASCII 7 bit alphabetic.</p>
	 * <p/>
	 * <pre>
	 *   CharUtility.isAsciiAlpha('a')  = true
	 *   CharUtility.isAsciiAlpha('A')  = true
	 *   CharUtility.isAsciiAlpha('3')  = false
	 *   CharUtility.isAsciiAlpha('-')  = false
	 *   CharUtility.isAsciiAlpha('\n') = false
	 *   CharUtility.isAsciiAlpha('&copy;') = false
	 * </pre>
	 *
	 * @param ch the character to check
	 * @return true if between 65 and 90 or 97 and 122 inclusive
	 */
	public static boolean isAsciiAlpha(char ch)
	{
		return (ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z');
	}

	/**
	 * <p>Checks whether the character is ASCII 7 bit alphabetic upper case.</p>
	 * <p/>
	 * <pre>
	 *   CharUtility.isAsciiAlphaUpper('a')  = false
	 *   CharUtility.isAsciiAlphaUpper('A')  = true
	 *   CharUtility.isAsciiAlphaUpper('3')  = false
	 *   CharUtility.isAsciiAlphaUpper('-')  = false
	 *   CharUtility.isAsciiAlphaUpper('\n') = false
	 *   CharUtility.isAsciiAlphaUpper('&copy;') = false
	 * </pre>
	 *
	 * @param ch the character to check
	 * @return true if between 65 and 90 inclusive
	 */
	public static boolean isAsciiAlphaUpper(char ch)
	{
		return ch >= 'A' && ch <= 'Z';
	}

	/**
	 * <p>Checks whether the character is ASCII 7 bit alphabetic lower case.</p>
	 * <p/>
	 * <pre>
	 *   CharUtility.isAsciiAlphaLower('a')  = true
	 *   CharUtility.isAsciiAlphaLower('A')  = false
	 *   CharUtility.isAsciiAlphaLower('3')  = false
	 *   CharUtility.isAsciiAlphaLower('-')  = false
	 *   CharUtility.isAsciiAlphaLower('\n') = false
	 *   CharUtility.isAsciiAlphaLower('&copy;') = false
	 * </pre>
	 *
	 * @param ch the character to check
	 * @return true if between 97 and 122 inclusive
	 */
	public static boolean isAsciiAlphaLower(char ch)
	{
		return ch >= 'a' && ch <= 'z';
	}

	/**
	 * <p>Checks whether the character is ASCII 7 bit numeric.</p>
	 * <p/>
	 * <pre>
	 *   CharUtility.isAsciiNumeric('a')  = false
	 *   CharUtility.isAsciiNumeric('A')  = false
	 *   CharUtility.isAsciiNumeric('3')  = true
	 *   CharUtility.isAsciiNumeric('-')  = false
	 *   CharUtility.isAsciiNumeric('\n') = false
	 *   CharUtility.isAsciiNumeric('&copy;') = false
	 * </pre>
	 *
	 * @param ch the character to check
	 * @return true if between 48 and 57 inclusive
	 */
	public static boolean isAsciiNumeric(char ch)
	{
		return ch >= '0' && ch <= '9';
	}

	/**
	 * <p>Checks whether the character is ASCII 7 bit numeric.</p>
	 * <p/>
	 * <pre>
	 *   CharUtility.isAsciiAlphanumeric('a')  = true
	 *   CharUtility.isAsciiAlphanumeric('A')  = true
	 *   CharUtility.isAsciiAlphanumeric('3')  = true
	 *   CharUtility.isAsciiAlphanumeric('-')  = false
	 *   CharUtility.isAsciiAlphanumeric('\n') = false
	 *   CharUtility.isAsciiAlphanumeric('&copy;') = false
	 * </pre>
	 *
	 * @param ch the character to check
	 * @return true if between 48 and 57 or 65 and 90 or 97 and 122 inclusive
	 */
	public static boolean isAsciiAlphanumeric(char ch)
	{
		return (ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z') || (ch >= '0' && ch <= '9');
	}

    /**
     * Indicates whether {@code ch} is a high- (or leading-) surrogate code unit
     * that is used for representing supplementary characters in UTF-16
     * encoding.
     *
     * @param ch
     *            the character to test.
     * @return {@code true} if {@code ch} is a high-surrogate code unit;
     *         {@code false} otherwise.
     */
    static boolean isHighSurrogate(char ch)
	{
        return ('\uD800' <= ch && '\uDBFF' >= ch);
    }
}
