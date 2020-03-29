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

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * <p>Provides extra functionality for Java Number classes.</p>
 *
 * @version $Id: NumberUtility.java,v 1.4 2008/07/14 11:02:24 swd\stefan.damian Exp $
 */
public class NumberUtility
{
	/**
	 * Reusable Long constant for zero.
	 */
	public static final Long LONG_ZERO = new Long(0L);
	/**
	 * Reusable Long constant for one.
	 */
	public static final Long LONG_ONE = new Long(1L);
	/**
	 * Reusable Long constant for minus one.
	 */
	public static final Long LONG_MINUS_ONE = new Long(-1L);
	/**
	 * Reusable Integer constant for zero.
	 */
	public static final Integer INTEGER_ZERO = new Integer(0);
	/**
	 * Reusable Integer constant for one.
	 */
	public static final Integer INTEGER_ONE = new Integer(1);
	/**
	 * Reusable Integer constant for minus one.
	 */
	public static final Integer INTEGER_MINUS_ONE = new Integer(-1);
	/**
	 * Reusable Short constant for zero.
	 */
	public static final Short SHORT_ZERO = new Short((short) 0);
	/**
	 * Reusable Short constant for one.
	 */
	public static final Short SHORT_ONE = new Short((short) 1);
	/**
	 * Reusable Short constant for minus one.
	 */
	public static final Short SHORT_MINUS_ONE = new Short((short) -1);
	/**
	 * Reusable Byte constant for zero.
	 */
	public static final Byte BYTE_ZERO = new Byte((byte) 0);
	/**
	 * Reusable Byte constant for one.
	 */
	public static final Byte BYTE_ONE = new Byte((byte) 1);
	/**
	 * Reusable Byte constant for minus one.
	 */
	public static final Byte BYTE_MINUS_ONE = new Byte((byte) -1);
	/**
	 * Reusable Double constant for zero.
	 */
	public static final Double DOUBLE_ZERO = new Double(0.0d);
	/**
	 * Reusable Double constant for one.
	 */
	public static final Double DOUBLE_ONE = new Double(1.0d);
	/**
	 * Reusable Double constant for minus one.
	 */
	public static final Double DOUBLE_MINUS_ONE = new Double(-1.0d);
	/**
	 * Reusable Float constant for zero.
	 */
	public static final Float FLOAT_ZERO = new Float(0.0f);
	/**
	 * Reusable Float constant for one.
	 */
	public static final Float FLOAT_ONE = new Float(1.0f);
	/**
	 * Reusable Float constant for minus one.
	 */
	public static final Float FLOAT_MINUS_ONE = new Float(-1.0f);

	/**
	 * <p><code>NumberUtility</code> instances should NOT be constructed in standard programming.
	 * Instead, the class should be used as <code>NumberUtility.stringToInt("6");</code>.</p>
	 * <p/>
	 * <p>This constructor is public to permit tools that require a JavaBean instance
	 * to operate.</p>
	 */
	public NumberUtility()
	{
		//nothing to do
	}

	/**
	 * <p>Convert a <code>String</code> to an <code>int</code>, returning
	 * <code>zero</code> if the conversion fails.</p>
	 * <p/>
	 * <p>If the string is <code>null</code>, <code>zero</code> is returned.</p>
	 * <p/>
	 * <pre>
	 *   NumberUtility.toInt(null) = 0
	 *   NumberUtility.toInt("")   = 0
	 *   NumberUtility.toInt("1")  = 1
	 * </pre>
	 *
	 * @param str the string to convert, may be null
	 * @return the int represented by the string, or <code>zero</code> if
	 *         conversion fails
	 */
	public static int toInt(String str)
	{
		return toInt(str, 0);
	}

	/**
	 * <p>Convert a <code>String</code> to an <code>int</code>, returning a
	 * default value if the conversion fails.</p>
	 * <p/>
	 * <p>If the string is <code>null</code>, the default value is returned.</p>
	 * <p/>
	 * <pre>
	 *   NumberUtility.toInt(null, 1) = 1
	 *   NumberUtility.toInt("", 1)   = 1
	 *   NumberUtility.toInt("1", 0)  = 1
	 * </pre>
	 *
	 * @param str          the string to convert, may be null
	 * @param defaultValue the default value
	 * @return the int represented by the string, or the default if conversion fails
	 */
	public static int toInt(String str, int defaultValue)
	{
		if (str == null) return defaultValue;

		try
		{
			return Integer.parseInt(str);
		}
		catch (NumberFormatException nfe)
		{
			return defaultValue;
		}
	}

	/**
	 * <p>Convert a <code>String</code> to a <code>long</code>, returning
	 * <code>zero</code> if the conversion fails.</p>
	 * <p/>
	 * <p>If the string is <code>null</code>, <code>zero</code> is returned.</p>
	 * <p/>
	 * <pre>
	 *   NumberUtility.toLong(null) = 0L
	 *   NumberUtility.toLong("")   = 0L
	 *   NumberUtility.toLong("1")  = 1L
	 * </pre>
	 *
	 * @param str the string to convert, may be null
	 * @return the long represented by the string, or <code>0</code> if
	 *         conversion fails
	 */
	public static long toLong(String str)
	{
		return toLong(str, 0L);
	}

	/**
	 * <p>Convert a <code>String</code> to a <code>long</code>, returning a
	 * default value if the conversion fails.</p>
	 * <p/>
	 * <p>If the string is <code>null</code>, the default value is returned.</p>
	 * <p/>
	 * <pre>
	 *   NumberUtility.toLong(null, 1L) = 1L
	 *   NumberUtility.toLong("", 1L)   = 1L
	 *   NumberUtility.toLong("1", 0L)  = 1L
	 * </pre>
	 *
	 * @param str          the string to convert, may be null
	 * @param defaultValue the default value
	 * @return the long represented by the string, or the default if conversion fails
	 */
	public static long toLong(String str, long defaultValue)
	{
		if (str == null) return defaultValue;

		try
		{
			return Long.parseLong(str);
		}
		catch (NumberFormatException nfe)
		{
			return defaultValue;
		}
	}

	/**
	 * <p>Convert a <code>String</code> to a <code>float</code>, returning
	 * <code>0.0f</code> if the conversion fails.</p>
	 * <p/>
	 * <p>If the string <code>str</code> is <code>null</code>,
	 * <code>0.0f</code> is returned.</p>
	 * <p/>
	 * <pre>
	 *   NumberUtility.toFloat(null)   = 0.0f
	 *   NumberUtility.toFloat("")     = 0.0f
	 *   NumberUtility.toFloat("1.5")  = 1.5f
	 * </pre>
	 *
	 * @param str the string to convert, may be <code>null</code>
	 * @return the float represented by the string, or <code>0.0f</code>
	 *         if conversion fails
	 */
	public static float toFloat(String str)
	{
		return toFloat(str, 0.0f);
	}

	/**
	 * <p>Convert a <code>String</code> to a <code>float</code>, returning a
	 * default value if the conversion fails.</p>
	 * <p/>
	 * <p>If the string <code>str</code> is <code>null</code>, the default
	 * value is returned.</p>
	 * <p/>
	 * <pre>
	 *   NumberUtility.toFloat(null, 1.1f)   = 1.0f
	 *   NumberUtility.toFloat("", 1.1f)     = 1.1f
	 *   NumberUtility.toFloat("1.5", 0.0f)  = 1.5f
	 * </pre>
	 *
	 * @param str          the string to convert, may be <code>null</code>
	 * @param defaultValue the default value
	 * @return the float represented by the string, or defaultValue
	 *         if conversion fails
	 */
	public static float toFloat(String str, float defaultValue)
	{
		if (str == null) return defaultValue;

		try
		{
			return Float.parseFloat(str);
		}
		catch (NumberFormatException nfe)
		{
			return defaultValue;
		}
	}

	/**
	 * Convert a double number into a long
	 * @param num double value to be converted
	 * @return converted long value
	 */
	public static long toLong(double num)
	{
		return (new Double(num)).longValue();
	}

	/**
	 * Convert a float number into a long
	 * @param num float value to be converted
	 * @return converted long value
	 */
	public static long toLong(float num)
	{
		return (new Float(num)).longValue();
	}

	/**
	 * Convert a double number into a integer
	 * @param num double value to be converted
	 * @return converted integer value
	 */
	public static long toInt(double num)
	{
		return (new Double(num)).intValue();
	}

	/**
	 * Convert a float number into a integer
	 * @param num float value to be converted
	 * @return converted integer value
	 */
	public static long toInt(float num)
	{
		return (new Float(num)).intValue();
	}

	/**
	 * <p>Convert a <code>String</code> to a <code>double</code>, returning
	 * <code>0.0d</code> if the conversion fails.</p>
	 * <p/>
	 * <p>If the string <code>str</code> is <code>null</code>,
	 * <code>0.0d</code> is returned.</p>
	 * <p/>
	 * <pre>
	 *   NumberUtility.toDouble(null)   = 0.0d
	 *   NumberUtility.toDouble("")     = 0.0d
	 *   NumberUtility.toDouble("1.5")  = 1.5d
	 * </pre>
	 *
	 * @param str the string to convert, may be <code>null</code>
	 * @return the double represented by the string, or <code>0.0d</code>
	 *         if conversion fails
	 */
	public static double toDouble(String str)
	{
		return toDouble(str, 0.0d);
	}

	/**
	 * <p>Convert a <code>String</code> to a <code>double</code>, returning a
	 * default value if the conversion fails.</p>
	 * <p/>
	 * <p>If the string <code>str</code> is <code>null</code>, the default
	 * value is returned.</p>
	 * <p/>
	 * <pre>
	 *   NumberUtility.toDouble(null, 1.1d)   = 1.1d
	 *   NumberUtility.toDouble("", 1.1d)     = 1.1d
	 *   NumberUtility.toDouble("1.5", 0.0d)  = 1.5d
	 * </pre>
	 *
	 * @param str          the string to convert, may be <code>null</code>
	 * @param defaultValue the default value
	 * @return the double represented by the string, or defaultValue
	 *         if conversion fails
	 */
	public static double toDouble(String str, double defaultValue)
	{
		if (str == null) return defaultValue;

		try
		{
			return Double.parseDouble(str);
		}
		catch (NumberFormatException nfe)
		{
			return defaultValue;
		}
	}

	/**
	 * <p>Turns a string value into a java.lang.Number.</p>
	 * <p/>
	 * <p>First, the value is examined for a type qualifier on the end
	 * (<code>'f','F','d','D','l','L'</code>).  If it is found, it starts
	 * trying to create successively larger types from the type specified
	 * until one is found that can represent the value.</p>
	 * <p/>
	 * <p>If a type specifier is not found, it will check for a decimal point
	 * and then try successively larger types from <code>Integer</code> to
	 * <code>BigInteger</code> and from <code>Float</code> to
	 * <code>BigDecimal</code>.</p>
	 * <p/>
	 * <p>If the string starts with <code>0x</code> or <code>-0x</code>, it
	 * will be interpreted as a hexadecimal integer.  Values with leading
	 * <code>0</code>'s will not be interpreted as octal.</p>
	 * <p/>
	 * <p>Returns <code>null</code> if the string is <code>null</code>.</p>
	 * <p/>
	 * <p>This method does not trim the input string, i.e., strings with leading
	 * or trailing spaces will generate NumberFormatExceptions.</p>
	 *
	 * @param str String containing a number, may be null
	 * @return Number created from the string
	 * @throws NumberFormatException if the value cannot be converted
	 */
	public static Number createNumber(String str) throws NumberFormatException
	{
		if (str == null) return null;

		if (StringUtility.isBlank(str)) throw new NumberFormatException("A blank string is not a valid number");

		if (str.startsWith("--"))
		{
			// this is protection for poorness in java.lang.BigDecimal. it accepts this as a legal value, but it does not appear
			// to be in specification of class. OS X Java parses it to a wrong value.
			return null;
		}

		if (str.startsWith("0x") || str.startsWith("-0x")) return createInteger(str);
		
		char lastChar = str.charAt(str.length() - 1);
		String mant;
		String dec;
		String exp;

		int decPos = str.indexOf('.');
		int expPos = str.indexOf('e') + str.indexOf('E') + 1;

		if (decPos > -1)
		{

			if (expPos > -1)
			{
				if (expPos < decPos) throw new NumberFormatException(str + " is not a valid number.");

				dec = str.substring(decPos + 1, expPos);
			}
			else dec = str.substring(decPos + 1);

			mant = str.substring(0, decPos);
		}
		else
		{
			if (expPos > -1) mant = str.substring(0, expPos);
				else mant = str;

			dec = null;
		}
		if (!Character.isDigit(lastChar))
		{
			if (expPos > -1 && expPos < str.length() - 1) exp = str.substring(expPos + 1, str.length() - 1);
				else exp = null;

			//Requesting a specific type..
			String numeric = str.substring(0, str.length() - 1);
			boolean allZeros = isAllZeros(mant) && isAllZeros(exp);
			switch (lastChar)
			{
				case 'l':
				case 'L':
					if (dec == null && exp == null && isDigits(numeric.substring(1)) && (numeric.charAt(0) == '-' || Character.isDigit(numeric.charAt(0))))
					{
						try
						{
							return createLong(numeric);
						}
						catch (NumberFormatException nfe)
						{
							//Too big for a long
						}
						return createBigInteger(numeric);

					}
					throw new NumberFormatException(str + " is not a valid number.");

				case 'f':
				case 'F':
					try
					{
						Float f = NumberUtility.createFloat(numeric);
						if (!(f.isInfinite() || (f.floatValue() == 0.0F && !allZeros)))
						{
							//If it's too big for a float or the float value = 0 and the string
							//has non-zeros in it, then float does not have the precision we want
							return f;
						}
					}
					catch (NumberFormatException nfe)
					{
						//nothing to do here
					}

					//Fall through
				case 'd':
				case 'D':
					try
					{
						Double d = NumberUtility.createDouble(numeric);
						if (!(d.isInfinite() || (d.floatValue() == 0.0D && !allZeros))) return d;
					}
					catch (NumberFormatException nfe)
					{
						//nothing to do here
					}

					try
					{
						return createBigDecimal(numeric);
					}
					catch (NumberFormatException e)
					{
						//nothing to do here
					}

					//Fall through
				default :
					throw new NumberFormatException(str + " is not a valid number.");

			}
		}
		else
		{
			//User doesn't have a preference on the return type, so let's start small and go from there...
			if (expPos > -1 && expPos < str.length() - 1) exp = str.substring(expPos + 1, str.length());
				else exp = null;

			if (dec == null && exp == null)
			{
				//Must be an int,long,bigint
				try
				{
					return createInteger(str);
				}
				catch (NumberFormatException nfe)
				{
					//nothing to do here
				}

				try
				{
					return createLong(str);
				}
				catch (NumberFormatException nfe)
				{
					//nothing to do here
				}

				return createBigInteger(str);

			}
			else
			{
				//Must be a float,double,BigDec
				boolean allZeros = isAllZeros(mant) && isAllZeros(exp);

				try
				{
					Float f = createFloat(str);
					if (!(f.isInfinite() || (f.floatValue() == 0.0F && !allZeros))) return f;
				}
				catch (NumberFormatException nfe)
				{
					//nothing to do here
				}

				try
				{
					Double d = createDouble(str);
					if (!(d.isInfinite() || (d.doubleValue() == 0.0D && !allZeros))) return d;
				}
				catch (NumberFormatException nfe)
				{
					//nothing to do here
				}

				return createBigDecimal(str);
			}
		}
	}

	/**
	 * <p>Utility method for {@link #createNumber(java.lang.String)}.</p>
	 * <p/>
	 * <p>Returns <code>true</code> if s is <code>null</code>.</p>
	 *
	 * @param str the String to check
	 * @return if it is all zeros or <code>null</code>
	 */
	private static boolean isAllZeros(String str)
	{
		if (str == null)
		{
			return true;
		}

		for (int i = str.length() - 1; i >= 0; i--)
		{
			if (str.charAt(i) != '0')
			{
				return false;
			}
		}

		return str.length() > 0;
	}

	/**
	 * <p>Convert a <code>String</code> to a <code>Float</code>.</p>
	 * <p/>
	 * <p>Returns <code>null</code> if the string is <code>null</code>.</p>
	 *
	 * @param str a <code>String</code> to convert, may be null
	 * @return converted <code>Float</code>
	 * @throws NumberFormatException if the value cannot be converted
	 */
	public static Float createFloat(String str)
	{
		if (str == null) return null;

		return Float.valueOf(str);
	}

	/**
	 * <p>Convert a <code>String</code> to a <code>Double</code>.</p>
	 * <p/>
	 * <p>Returns <code>null</code> if the string is <code>null</code>.</p>
	 *
	 * @param str a <code>String</code> to convert, may be null
	 * @return converted <code>Double</code>
	 * @throws NumberFormatException if the value cannot be converted
	 */
	public static Double createDouble(String str)
	{
		if (str == null) return null;

		return Double.valueOf(str);
	}

	/**
	 * <p>Convert a <code>String</code> to a <code>Integer</code>, handling
	 * hex and octal notations.</p>
	 * <p/>
	 * <p>Returns <code>null</code> if the string is <code>null</code>.</p>
	 *
	 * @param str a <code>String</code> to convert, may be null
	 * @return converted <code>Integer</code>
	 * @throws NumberFormatException if the value cannot be converted
	 */
	public static Integer createInteger(String str)
	{
		if (str == null) return null;

		// decode() handles 0xAABD and 0777 (hex and octal) as well.
		//return Integer.decode(str);
		return Integer.valueOf(str);
	}

	public static Integer createInteger(int str)
	{
		return Integer.valueOf(str);
	}

	/**
	 * <p>Convert a <code>String</code> to a <code>Long</code>.</p>
	 * <p/>
	 * <p>Returns <code>null</code> if the string is <code>null</code>.</p>
	 *
	 * @param str a <code>String</code> to convert, may be null
	 * @return converted <code>Long</code>
	 * @throws NumberFormatException if the value cannot be converted
	 */
	public static Long createLong(String str)
	{
		if (str == null) return null;

		return Long.valueOf(str);
	}

	public static Long createLong(long str)
	{
		return Long.valueOf(str);
	}

	public static Long createLong(int str)
	{
		return Long.valueOf(str);
	}

	/**
	 * <p>Convert a <code>String</code> to a <code>BigInteger</code>.</p>
	 * <p/>
	 * <p>Returns <code>null</code> if the string is <code>null</code>.</p>
	 *
	 * @param str a <code>String</code> to convert, may be null
	 * @return converted <code>BigInteger</code>
	 * @throws NumberFormatException if the value cannot be converted
	 */
	public static BigInteger createBigInteger(String str)
	{
		if (str == null) return null;

		return new BigInteger(str);
	}

	/**
	 * <p>Convert a <code>String</code> to a <code>BigDecimal</code>.</p>
	 * <p/>
	 * <p>Returns <code>null</code> if the string is <code>null</code>.</p>
	 *
	 * @param str a <code>String</code> to convert, may be null
	 * @return converted <code>BigDecimal</code>
	 * @throws NumberFormatException if the value cannot be converted
	 */
	public static BigDecimal createBigDecimal(String str)
	{
		if (str == null) return null;

		// handle JDK1.3.1 bug where "" throws IndexOutOfBoundsException
		if (StringUtility.isBlank(str)) throw new NumberFormatException("A blank string is not a valid number");

		return new BigDecimal(str);
	}

	/**
	 * Get bitwise number for AND operator.
	 *
	 * @param number principal value
	 * @param bit bit value
	 * @return bitwise value
	 */
	public static int getBitwiseAnd(int number, int bit)
	{
		return (number & bit);
	}

	/**
	 * Get bitwise number for an exclusive OR operator.
	 *
	 * @param number principal value
	 * @param bit bit value
	 * @return bitwise value
	 */
	public static int getBitwiseExclusiveOr(int number, int bit)
	{
		return (number ^ bit);
	}

	/**
	 * Get bitwise number for an inclusive OR operator.
	 *
	 * @param number principal value
	 * @param bit bit value
	 * @return bitwise value
	 */
	public static int getBitwiseInclusiveOr(int number, int bit)
	{
		return (number | bit);
	}

	/**
	 * <p>Returns the minimum value in an array.</p>
	 *
	 * @param array an array, must not be null or empty
	 * @return the minimum value in the array
	 * @throws IllegalArgumentException if <code>array</code> is <code>null</code> or if <code>array</code> is empty
	 */
	public static long min(long[] array)
	{
		// Validates input
		if (array == null) throw new IllegalArgumentException("The array must not be null");
			else if (array.length == 0) throw new IllegalArgumentException("Array cannot be empty.");

		// Finds and returns min
		long min = array[0];

		for (int i = 1; i < array.length; i++)
		{
			if (array[i] < min) min = array[i];
		}

		return min;
	}

	/**
	 * <p>Returns the minimum value in an array.</p>
	 *
	 * @param array an array, must not be null or empty
	 * @return the minimum value in the array
	 * @throws IllegalArgumentException if <code>array</code> is <code>null</code> or if <code>array</code> is empty
	 */
	public static int min(int[] array)
	{
		// Validates input
		if (array == null)
		{
			throw new IllegalArgumentException("The array must not be null");
		}
		else if (array.length == 0)
		{
			throw new IllegalArgumentException("Array cannot be empty.");
		}

		// Finds and returns min
		int min = array[0];
		for (int j = 1; j < array.length; j++)
		{
			if (array[j] < min)
			{
				min = array[j];
			}
		}

		return min;
	}

	/**
	 * <p>Returns the minimum value in an array.</p>
	 *
	 * @param array an array, must not be null or empty
	 * @return the minimum value in the array
	 * @throws IllegalArgumentException if <code>array</code> is <code>null</code> or if <code>array</code> is empty
	 */
	public static short min(short[] array)
	{
		// Validates input
		if (array == null)
		{
			throw new IllegalArgumentException("The Array must not be null");
		}
		else if (array.length == 0)
		{
			throw new IllegalArgumentException("Array cannot be empty.");
		}

		// Finds and returns min
		short min = array[0];
		for (int i = 1; i < array.length; i++)
		{
			if (array[i] < min)
			{
				min = array[i];
			}
		}

		return min;
	}

	/**
	 * <p>Returns the minimum value in an array.</p>
	 *
	 * @param array an array, must not be null or empty
	 * @return the minimum value in the array
	 * @throws IllegalArgumentException if <code>array</code> is <code>null</code> or if <code>array</code> is empty
	 */
	public static double min(double[] array)
	{
		// Validates input
		if (array == null)
		{
			throw new IllegalArgumentException("The Array must not be null");
		}
		else if (array.length == 0)
		{
			throw new IllegalArgumentException("Array cannot be empty.");
		}

		// Finds and returns min
		double min = array[0];
		for (int i = 1; i < array.length; i++)
		{
			if (array[i] < min)
			{
				min = array[i];
			}
		}

		return min;
	}

	/**
	 * <p>Returns the minimum value in an array.</p>
	 *
	 * @param array an array, must not be null or empty
	 * @return the minimum value in the array
	 * @throws IllegalArgumentException if <code>array</code> is <code>null</code> or if <code>array</code> is empty
	 */
	public static float min(float[] array)
	{
		// Validates input
		if (array == null)
		{
			throw new IllegalArgumentException("The Array must not be null");
		}
		else if (array.length == 0)
		{
			throw new IllegalArgumentException("Array cannot be empty.");
		}

		// Finds and returns min
		float min = array[0];
		for (int i = 1; i < array.length; i++)
		{
			if (array[i] < min)
			{
				min = array[i];
			}
		}

		return min;
	}

	// Max in array
	/**
	 * <p>Returns the maximum value in an array.</p>
	 *
	 * @param array an array, must not be null or empty
	 * @return the minimum value in the array
	 * @throws IllegalArgumentException if <code>array</code> is <code>null</code> or if <code>array</code> is empty
	 */
	public static long max(long[] array)
	{
		// Validates input
		if (array == null)
		{
			throw new IllegalArgumentException("The Array must not be null");
		}
		else if (array.length == 0)
		{
			throw new IllegalArgumentException("Array cannot be empty.");
		}

		// Finds and returns max
		long max = array[0];
		for (int j = 1; j < array.length; j++)
		{
			if (array[j] > max)
			{
				max = array[j];
			}
		}

		return max;
	}

	/**
	 * <p>Returns the maximum value in an array.</p>
	 *
	 * @param array an array, must not be null or empty
	 * @return the minimum value in the array
	 * @throws IllegalArgumentException if <code>array</code> is <code>null</code> or if <code>array</code> is empty
	 */
	public static int max(int[] array)
	{
		// Validates input
		if (array == null)
		{
			throw new IllegalArgumentException("The Array must not be null");
		}
		else if (array.length == 0)
		{
			throw new IllegalArgumentException("Array cannot be empty.");
		}

		// Finds and returns max
		int max = array[0];
		for (int j = 1; j < array.length; j++)
		{
			if (array[j] > max)
			{
				max = array[j];
			}
		}

		return max;
	}

	/**
	 * <p>Returns the maximum value in an array.</p>
	 *
	 * @param array an array, must not be null or empty
	 * @return the minimum value in the array
	 * @throws IllegalArgumentException if <code>array</code> is <code>null</code> or if <code>array</code> is empty
	 */
	public static short max(short[] array)
	{
		// Validates input
		if (array == null)
		{
			throw new IllegalArgumentException("The Array must not be null");
		}
		else if (array.length == 0)
		{
			throw new IllegalArgumentException("Array cannot be empty.");
		}

		// Finds and returns max
		short max = array[0];
		for (int i = 1; i < array.length; i++)
		{
			if (array[i] > max)
			{
				max = array[i];
			}
		}

		return max;
	}

	/**
	 * <p>Returns the maximum value in an array.</p>
	 *
	 * @param array an array, must not be null or empty
	 * @return the minimum value in the array
	 * @throws IllegalArgumentException if <code>array</code> is <code>null</code> or if <code>array</code> is empty
	 */
	public static double max(double[] array)
	{
		// Validates input
		if (array == null)
		{
			throw new IllegalArgumentException("The Array must not be null");
		}
		else if (array.length == 0)
		{
			throw new IllegalArgumentException("Array cannot be empty.");
		}

		// Finds and returns max
		double max = array[0];
		for (int j = 1; j < array.length; j++)
		{
			if (array[j] > max)
			{
				max = array[j];
			}
		}

		return max;
	}

	/**
	 * <p>Returns the maximum value in an array.</p>
	 *
	 * @param array an array, must not be null or empty
	 * @return the minimum value in the array
	 * @throws IllegalArgumentException if <code>array</code> is <code>null</code> or if <code>array</code> is empty
	 */
	public static float max(float[] array)
	{
		// Validates input
		if (array == null)
		{
			throw new IllegalArgumentException("The Array must not be null");
		}
		else if (array.length == 0)
		{
			throw new IllegalArgumentException("Array cannot be empty.");
		}

		// Finds and returns max
		float max = array[0];
		for (int j = 1; j < array.length; j++)
		{
			if (array[j] > max)
			{
				max = array[j];
			}
		}

		return max;
	}

	// 3 param min
	/**
	 * <p>Gets the minimum of three <code>long</code> values.</p>
	 *
	 * @param a value 1
	 * @param b value 2
	 * @param c value 3
	 * @return the smallest of the values
	 */
	public static long min(long a, long b, long c)
	{
		if (b < a)
		{
			a = b;
		}

		if (c < a)
		{
			a = c;
		}

		return a;
	}

	/**
	 * <p>Gets the minimum of three <code>int</code> values.</p>
	 *
	 * @param a value 1
	 * @param b value 2
	 * @param c value 3
	 * @return the smallest of the values
	 */
	public static int min(int a, int b, int c)
	{
		if (b < a)
		{
			a = b;
		}

		if (c < a)
		{
			a = c;
		}

		return a;
	}

	/**
	 * <p>Gets the minimum of three <code>short</code> values.</p>
	 *
	 * @param a value 1
	 * @param b value 2
	 * @param c value 3
	 * @return the smallest of the values
	 */
	public static short min(short a, short b, short c)
	{
		if (b < a)
		{
			a = b;
		}

		if (c < a)
		{
			a = c;
		}

		return a;
	}

	/**
	 * <p>Gets the minimum of three <code>byte</code> values.</p>
	 *
	 * @param a value 1
	 * @param b value 2
	 * @param c value 3
	 * @return the smallest of the values
	 */
	public static byte min(byte a, byte b, byte c)
	{
		if (b < a)
		{
			a = b;
		}

		if (c < a)
		{
			a = c;
		}

		return a;
	}

	/**
	 * <p>Gets the minimum of three <code>double</code> values.</p>
	 * <p/>
	 * <p>If any value is <code>NaN</code>, <code>NaN</code> is
	 * returned. Infinity is handled.</p>
	 *
	 * @param a value 1
	 * @param b value 2
	 * @param c value 3
	 * @return the smallest of the values
	 */
	public static double min(double a, double b, double c)
	{
		return Math.min(Math.min(a, b), c);
	}

	/**
	 * <p>Gets the minimum of three <code>float</code> values.</p>
	 * <p/>
	 * <p>If any value is <code>NaN</code>, <code>NaN</code> is
	 * returned. Infinity is handled.</p>
	 *
	 * @param a value 1
	 * @param b value 2
	 * @param c value 3
	 * @return the smallest of the values
	 */
	public static float min(float a, float b, float c)
	{
		return Math.min(Math.min(a, b), c);
	}

	// 3 param max
	/**
	 * <p>Gets the maximum of three <code>long</code> values.</p>
	 *
	 * @param a value 1
	 * @param b value 2
	 * @param c value 3
	 * @return the largest of the values
	 */
	public static long max(long a, long b, long c)
	{
		if (b > a)
		{
			a = b;
		}

		if (c > a)
		{
			a = c;
		}

		return a;
	}

	/**
	 * <p>Gets the maximum of three <code>int</code> values.</p>
	 *
	 * @param a value 1
	 * @param b value 2
	 * @param c value 3
	 * @return the largest of the values
	 */
	public static int max(int a, int b, int c)
	{
		if (b > a)
		{
			a = b;
		}

		if (c > a)
		{
			a = c;
		}

		return a;
	}

	/**
	 * <p>Gets the maximum of three <code>short</code> values.</p>
	 *
	 * @param a value 1
	 * @param b value 2
	 * @param c value 3
	 * @return the largest of the values
	 */
	public static short max(short a, short b, short c)
	{
		if (b > a)
		{
			a = b;
		}

		if (c > a)
		{
			a = c;
		}

		return a;
	}

	/**
	 * <p>Gets the maximum of three <code>byte</code> values.</p>
	 *
	 * @param a value 1
	 * @param b value 2
	 * @param c value 3
	 * @return the largest of the values
	 */
	public static byte max(byte a, byte b, byte c)
	{
		if (b > a)
		{
			a = b;
		}

		if (c > a)
		{
			a = c;
		}

		return a;
	}

	/**
	 * <p>Gets the maximum of three <code>double</code> values.</p>
	 * <p/>
	 * <p>If any value is <code>NaN</code>, <code>NaN</code> is
	 * returned. Infinity is handled.</p>
	 *
	 * @param a value 1
	 * @param b value 2
	 * @param c value 3
	 * @return the largest of the values
	 */
	public static double max(double a, double b, double c)
	{
		return Math.max(Math.max(a, b), c);
	}

	/**
	 * <p>Gets the maximum of three <code>float</code> values.</p>
	 * <p/>
	 * <p>If any value is <code>NaN</code>, <code>NaN</code> is
	 * returned. Infinity is handled.</p>
	 *
	 * @param a value 1
	 * @param b value 2
	 * @param c value 3
	 * @return the largest of the values
	 */
	public static float max(float a, float b, float c)
	{
		return Math.max(Math.max(a, b), c);
	}

	/**
	 * <p>Compares two <code>doubles</code> for order.</p>
	 * <p/>
	 * <p>This method is more comprehensive than the standard Java greater
	 * than, less than and equals operators.</p>
	 * <ul>
	 * <li>It returns <code>-1</code> if the first value is less than the second.</li>
	 * <li>It returns <code>+1</code> if the first value is greater than the second.</li>
	 * <li>It returns <code>0</code> if the values are equal.</li>
	 * </ul>
	 * <p/>
	 * <p/>
	 * The ordering is as follows, largest to smallest:
	 * <ul>
	 * <li>NaN
	 * <li>Positive infinity
	 * <li>Maximum double
	 * <li>Normal positive numbers
	 * <li>+0.0
	 * <li>-0.0
	 * <li>Normal negative numbers
	 * <li>Minimum double (<code>-Double.MAX_VALUE</code>)
	 * <li>Negative infinity
	 * </ul>
	 * </p>
	 * <p/>
	 * <p>Comparing <code>NaN</code> with <code>NaN</code> will
	 * return <code>0</code>.</p>
	 *
	 * @param lhs the first <code>double</code>
	 * @param rhs the second <code>double</code>
	 * @return <code>-1</code> if lhs is less, <code>+1</code> if greater,
	 *         <code>0</code> if equal to rhs
	 */
	public static int compare(double lhs, double rhs)
	{
		if (lhs < rhs)
		{
			return -1;
		}

		if (lhs > rhs)
		{
			return +1;
		}

		// Need to compare bits to handle 0.0 == -0.0 being true
		// compare should put -0.0 < +0.0
		// Two NaNs are also == for compare purposes
		// where NaN == NaN is false
		long lhsBits = Double.doubleToLongBits(lhs);
		long rhsBits = Double.doubleToLongBits(rhs);
		if (lhsBits == rhsBits)
		{
			return 0;
		}

		// Something exotic! A comparison to NaN or 0.0 vs -0.0
		// Fortunately NaN's long is > than everything else
		// Also negzeros bits < poszero
		// NAN: 9221120237041090560
		// MAX: 9218868437227405311
		// NEGZERO: -9223372036854775808
		if (lhsBits < rhsBits)
		{
			return -1;
		}
		else
		{
			return +1;
		}
	}

	/**
	 * <p>Compares two floats for order.</p>
	 * <p/>
	 * <p>This method is more comprehensive than the standard Java greater than,
	 * less than and equals operators.</p>
	 * <ul>
	 * <li>It returns <code>-1</code> if the first value is less than the second.
	 * <li>It returns <code>+1</code> if the first value is greater than the second.
	 * <li>It returns <code>0</code> if the values are equal.
	 * </ul>
	 * <p/>
	 * <p> The ordering is as follows, largest to smallest:
	 * <ul>
	 * <li>NaN
	 * <li>Positive infinity
	 * <li>Maximum float
	 * <li>Normal positive numbers
	 * <li>+0.0
	 * <li>-0.0
	 * <li>Normal negative numbers
	 * <li>Minimum float (<code>-Float.MAX_VALUE</code>)
	 * <li>Negative infinity
	 * </ul>
	 * <p/>
	 * <p>Comparing <code>NaN</code> with <code>NaN</code> will return
	 * <code>0</code>.</p>
	 *
	 * @param lhs the first <code>float</code>
	 * @param rhs the second <code>float</code>
	 * @return <code>-1</code> if lhs is less, <code>+1</code> if greater,
	 *         <code>0</code> if equal to rhs
	 */
	public static int compare(float lhs, float rhs)
	{
		if (lhs < rhs)
		{
			return -1;
		}

		if (lhs > rhs)
		{
			return +1;
		}

		//Need to compare bits to handle 0.0 == -0.0 being true
		// compare should put -0.0 < +0.0
		// Two NaNs are also == for compare purposes
		// where NaN == NaN is false
		int lhsBits = Float.floatToIntBits(lhs);
		int rhsBits = Float.floatToIntBits(rhs);
		if (lhsBits == rhsBits)
		{
			return 0;
		}

		//Something exotic! A comparison to NaN or 0.0 vs -0.0
		//Fortunately NaN's int is > than everything else
		//Also negzeros bits < poszero
		//NAN: 2143289344
		//MAX: 2139095039
		//NEGZERO: -2147483648
		if (lhsBits < rhsBits)
		{
			return -1;
		}
		else
		{
			return +1;
		}
	}

	/**
	 * <p>Compares two long for order.</p>
	 * <p/>
	 * <p>This method is more comprehensive than the standard Java greater than,
	 * less than and equals operators.</p>
	 * <ul>
	 * <li>It returns <code>-1</code> if the first value is less than the second.
	 * <li>It returns <code>+1</code> if the first value is greater than the second.
	 * <li>It returns <code>0</code> if the values are equal.
	 * </ul>
	 * <p/>
	 * <p>Comparing <code>NaN</code> with <code>NaN</code> will return
	 * <code>0</code>.</p>
	 *
	 * @param lhs the first <code>long</code>
	 * @param rhs the second <code>long</code>
	 * @return <code>-1</code> if lhs is less, <code>+1</code> if greater,
	 *         <code>0</code> if equal to rhs
	 */
	public static int compare(long lhs, long rhs)
	{
		if (lhs < rhs)
		{
			return -1;
		}

		if (lhs > rhs)
		{
			return +1;
		}

		return 0;
	}

	/**
	 * Make sum of two double values. This method is created because Velocity
	 * is not supporting basic operations with this data type
	 * @param a first double value
	 * @param b second double value
	 * @return sum
	 */
	public static double sum(double a, double b)
	{
		return a + b;
	}

	/**
	 * Make sum of two float values. This method is created because Velocity
	 * is not supporting basic operations with this data type
	 * @param a first float value
	 * @param b second float value
	 * @return sum
	 */
	public static float sum(float a, float b)
	{
		return a + b;
	}

	/**
	 * Make sum of two long values. This method is created because Velocity
	 * is not supporting basic operations with this data type
	 * @param a first long value
	 * @param b second long value
	 * @return sum
	 */
	public static long sum(long a, long b)
	{
		return a + b;
	}

	/**
	 * Make diff of two double values. This method is created because Velocity
	 * is not supporting basic operations with this data type
	 * @param a first double value
	 * @param b second double value
	 * @return diff
	 */
	public static double dif(double a, double b)
	{
		return a - b;
	}

	/**
	 * Make diff of two float values. This method is created because Velocity
	 * is not supporting basic operations with this data type
	 * @param a first float value
	 * @param b second float value
	 * @return diff
	 */
	public static float dif(float a, float b)
	{
		return a - b;
	}

	/**
	 * Make diff of two long values. This method is created because Velocity
	 * is not supporting basic operations with this data type
	 * @param a first long value
	 * @param b second long value
	 * @return diff
	 */
	public static long dif(long a, long b)
	{
		return a - b;
	}

	/**
	 * Make multiply of two double values. This method is created because Velocity
	 * is not supporting basic operations with this data type
	 * @param a first double value
	 * @param b second double value
	 * @return multiply
	 */
	public static double mul(double a, double b)
	{
		return a * b;
	}

	/**
	 * Make multiply of two float values. This method is created because Velocity
	 * is not supporting basic operations with this data type
	 * @param a first float value
	 * @param b second float value
	 * @return multiply
	 */
	public static float mul(float a, float b)
	{
		return a * b;
	}

	/**
	 * Make multiply of two long values. This method is created because Velocity
	 * is not supporting basic operations with this data type
	 * @param a first long value
	 * @param b second long value
	 * @return multiply
	 */
	public static long mul(long a, long b)
	{
		return a * b;
	}

	/**
	 * Make divided of two double values. This method is created because Velocity
	 * is not supporting basic operations with this data type
	 * @param a first double value
	 * @param b second double value
	 * @return divided
	 */
	public static double div(double a, double b)
	{
		return a / b;
	}

	/**
	 * Make divided of two float values. This method is created because Velocity
	 * is not supporting basic operations with this data type
	 * @param a first float value
	 * @param b second float value
	 * @return divided
	 */
	public static float div(float a, float b)
	{
		return a / b;
	}

	/**
	 * Make divided of two long values. This method is created because Velocity
	 * is not supporting basic operations with this data type
	 * @param a first long value
	 * @param b second long value
	 * @return divided
	 */
	public static float div(long a, long b)
	{
		return a / b;
	}

	/**
	 * <p>Checks whether the <code>String</code> contains only
	 * digit characters.</p>
	 * <p/>
	 * <p><code>Null</code> and empty String will return
	 * <code>false</code>.</p>
	 *
	 * @param str the <code>String</code> to check
	 * @return <code>true</code> if str contains only unicode numeric
	 */
	public static boolean isDigits(String str)
	{
		if (StringUtility.isEmpty(str)) return false;

		for (int i = 0; i < str.length(); i++)
		{
			if (!Character.isDigit(str.charAt(i))) return false;
		}

		return true;
	}

	/**
	 * <p>Checks whether the String a valid Java number.</p>
	 * <p/>
	 * <p>Valid numbers include hexadecimal marked with the <code>0x</code>
	 * qualifier, scientific notation and numbers marked with a type
	 * qualifier (e.g. 123L).</p>
	 * <p/>
	 * <p><code>Null</code> and empty String will return
	 * <code>false</code>.</p>
	 *
	 * @param str the <code>String</code> to check
	 * @return <code>true</code> if the string is a correctly formatted number
	 */
	public static boolean isNumber(String str)
	{
		if (StringUtility.isEmpty(str)) return false;

		char[] chars = str.toCharArray();
		int sz = chars.length;
		boolean hasExp = false;
		boolean hasDecPoint = false;
		boolean allowSigns = false;
		boolean foundDigit = false;

		// deal with any possible sign up front
		int start = (chars[0] == '-') ? 1 : 0;

		if (sz > start + 1)
		{
			if (chars[start] == '0' && chars[start + 1] == 'x')
			{
				int i = start + 2;
				if (i == sz) return false; // str == "0x"

				// checking hex (it can't be anything else)
				for (; i < chars.length; i++)
				{
					if ((chars[i] < '0' || chars[i] > '9') && (chars[i] < 'a' || chars[i] > 'f') && (chars[i] < 'A' || chars[i] > 'F')) return false;
				}

				return true;
			}
		}

		sz--; // don't want to loop to the last char, check it afterwords

		// for type qualifiers
		int i = start;

		// loop to the next to last char or to the last char if we need another digit to make a valid number (e.g. chars[0..5] = "1234E")
		while (i < sz || (i < sz + 1 && allowSigns && !foundDigit))
		{
			if (chars[i] >= '0' && chars[i] <= '9')
			{
				foundDigit = true;
				allowSigns = false;

			}
			else if (chars[i] == '.')
			{
				// two decimal points or dec in exponent
				if (hasDecPoint || hasExp) return false;

				hasDecPoint = true;
			}
			else if (chars[i] == 'e' || chars[i] == 'E')
			{
				// we've already taken care of hex.
				if (hasExp) return false;

				if (!foundDigit) return false;

				hasExp = true;
				allowSigns = true;
			}
			else if (chars[i] == '+' || chars[i] == '-')
			{
				if (!allowSigns) return false;

				allowSigns = false;
				foundDigit = false; // we need a digit after the E
			}
			else return false;

			i++;
		}

		if (i < chars.length)
		{
			// no type qualifier, OK
			if (chars[i] >= '0' && chars[i] <= '9') return true;

			// can't have an E at the last byte
			if (chars[i] == 'e' || chars[i] == 'E') return false;

			if (!allowSigns && (chars[i] == 'd' || chars[i] == 'D' || chars[i] == 'f' || chars[i] == 'F')) return foundDigit;
			if (chars[i] == 'l' || chars[i] == 'L') return foundDigit && !hasExp;

			// last character is illegal
			return false;
		}

		// allowSigns is true iff the val ends in 'E'
		// found digit it to make sure weird stuff like '.' and '1E-' doesn't pass
		return !allowSigns && foundDigit;
	}

}
