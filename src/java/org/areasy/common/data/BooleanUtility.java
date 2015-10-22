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

/**
 * <p>Operations on boolean primitives and Boolean objects.</p>
 * <p/>
 * <p>This class tries to handle <code>null</code> input gracefully.
 * An exception will not be thrown for a <code>null</code> input.
 * Each method documents its behaviour in more detail.</p>
 *
 * @version $Id: BooleanUtility.java,v 1.3 2008/05/14 09:32:29 swd\stefan.damian Exp $
 */
public class BooleanUtility
{
	/**
	 * <p><code>BooleanUtility</code> instances should NOT be constructed in standard programming.
	 * Instead, the class should be used as <code>BooleanUtility.toBooleanObject(true);</code>.</p>
	 * <p/>
	 * <p>This constructor is public to permit tools that require a JavaBean instance
	 * to operate.</p>
	 */
	public BooleanUtility()
	{
		//nothing to do
	}

	// Boolean utilities
	/**
	 * <p>Negates the specified boolean.</p>
	 * <p/>
	 * <p>If <code>null</code> is passed in, <code>null</code> will be returned.</p>
	 * <p/>
	 * <pre>
	 *   BooleanUtility.negate(Boolean.TRUE)  = Boolean.FALSE;
	 *   BooleanUtility.negate(Boolean.FALSE) = Boolean.TRUE;
	 *   BooleanUtility.negate(null)          = null;
	 * </pre>
	 *
	 * @param bool the Boolean to negate, may be null
	 * @return the negated Boolean, or <code>null</code> if <code>null</code> input
	 */
	public static Boolean negate(Boolean bool)
	{
		if (bool == null) return null;

		return (bool.booleanValue() ? Boolean.FALSE : Boolean.TRUE);
	}

	// boolean Boolean methods
	/**
	 * <p>Is a Boolean value <code>true</code>, handling <code>null</code>.</p>
	 * <p/>
	 * <pre>
	 *   BooleanUtility.isTrue(Boolean.TRUE)  = true
	 *   BooleanUtility.isTrue(Boolean.FALSE) = false
	 *   BooleanUtility.isTrue(null)          = false
	 * </pre>
	 *
	 * @param bool the boolean to convert
	 * @return <code>true</code> only if the input is non-null and true
	 */
	public static boolean isTrue(Boolean bool)
	{
		if (bool == null)
		{
			return false;
		}

		return bool.booleanValue() ? true : false;
	}

	/**
	 * <p>Is a Boolean value <code>false</code>, handling <code>null</code>.</p>
	 * <p/>
	 * <pre>
	 *   BooleanUtility.isFalse(Boolean.TRUE)  = false
	 *   BooleanUtility.isFalse(Boolean.FALSE) = true
	 *   BooleanUtility.isFalse(null)          = false
	 * </pre>
	 *
	 * @param bool the boolean to convert
	 * @return <code>true</code> only if the input is non-null and false
	 */
	public static boolean isFalse(Boolean bool)
	{
		if (bool == null)
		{
			return false;
		}

		return bool.booleanValue() ? false : true;
	}

	/**
	 * <p>Boolean factory that avoids creating new Boolean objecs all the time.</p>
	 * <p/>
	 * <p>This method was added to JDK1.4 but is available here for earlier JDKs.</p>
	 * <p/>
	 * <pre>
	 *   BooleanUtility.toBooleanObject(false) = Boolean.FALSE
	 *   BooleanUtility.toBooleanObject(true)  = Boolean.TRUE
	 * </pre>
	 *
	 * @param bool the boolean to convert
	 * @return Boolean.TRUE or Boolean.FALSE as appropriate
	 */
	public static Boolean toBooleanObject(boolean bool)
	{
		return bool ? Boolean.TRUE : Boolean.FALSE;
	}

	/**
	 * <p>Converts a Boolean to a boolean handling <code>null</code>
	 * by returning <code>false</code>.</p>
	 * <p/>
	 * <pre>
	 *   BooleanUtility.toBoolean(Boolean.TRUE)  = true
	 *   BooleanUtility.toBoolean(Boolean.FALSE) = false
	 *   BooleanUtility.toBoolean(null)          = false
	 * </pre>
	 *
	 * @param bool the boolean to convert
	 * @return <code>true</code> or <code>false</code>,
	 *         <code>null</code> returns <code>false</code>
	 */
	public static boolean toBoolean(Boolean bool)
	{
		if (bool == null)
		{
			return false;
		}

		return bool.booleanValue() ? true : false;
	}

	/**
	 * <p>Converts a Boolean to a boolean handling <code>null</code>.</p>
	 * <p/>
	 * <pre>
	 *   BooleanUtility.toBooleanDefaultIfNull(Boolean.TRUE, false) = true
	 *   BooleanUtility.toBooleanDefaultIfNull(Boolean.FALSE, true) = false
	 *   BooleanUtility.toBooleanDefaultIfNull(null, true)          = true
	 * </pre>
	 *
	 * @param bool        the boolean to convert
	 * @param valueIfNull the boolean value to return if <code>null</code>
	 * @return <code>true</code> or <code>false</code>
	 */
	public static boolean toBooleanDefaultIfNull(Boolean bool, boolean valueIfNull)
	{
		if (bool == null)
		{
			return valueIfNull;
		}

		return bool.booleanValue() ? true : false;
	}

	// Integer to Boolean methods
	/**
	 * <p>Converts an int to a boolean using the convention that <code>zero</code>
	 * is <code>false</code>.</p>
	 * <p/>
	 * <pre>
	 *   BooleanUtility.toBoolean(0) = false
	 *   BooleanUtility.toBoolean(1) = true
	 *   BooleanUtility.toBoolean(2) = true
	 * </pre>
	 *
	 * @param value the int to convert
	 * @return <code>true</code> if non-zero, <code>false</code>
	 *         if zero
	 */
	public static boolean toBoolean(int value)
	{
		return value == 0 ? false : true;
	}

	/**
	 * <p>Converts an int to a Boolean using the convention that <code>zero</code>
	 * is <code>false</code>.</p>
	 * <p/>
	 * <pre>
	 *   BooleanUtility.toBoolean(0) = Boolean.FALSE
	 *   BooleanUtility.toBoolean(1) = Boolean.TRUE
	 *   BooleanUtility.toBoolean(2) = Boolean.TRUE
	 * </pre>
	 *
	 * @param value the int to convert
	 * @return Boolean.TRUE if non-zero, Boolean.FALSE if zero,
	 *         <code>null</code> if <code>null</code>
	 */
	public static Boolean toBooleanObject(int value)
	{
		return value == 0 ? Boolean.FALSE : Boolean.TRUE;
	}

	/**
	 * <p>Converts an Integer to a Boolean using the convention that <code>zero</code>
	 * is <code>false</code>.</p>
	 * <p/>
	 * <p><code>null</code> will be converted to <code>null</code>.</p>
	 * <p/>
	 * <pre>
	 *   BooleanUtility.toBoolean(new Integer(0))    = Boolean.FALSE
	 *   BooleanUtility.toBoolean(new Integer(1))    = Boolean.TRUE
	 *   BooleanUtility.toBoolean(new Integer(null)) = null
	 * </pre>
	 *
	 * @param value the Integer to convert
	 * @return Boolean.TRUE if non-zero, Boolean.FALSE if zero,
	 *         <code>null</code> if <code>null</code> input
	 */
	public static Boolean toBooleanObject(Integer value)
	{
		if (value == null)
		{
			return null;
		}
		return value.intValue() == 0 ? Boolean.FALSE : Boolean.TRUE;
	}

	/**
	 * <p>Converts an int to a boolean specifying the conversion values.</p>
	 * <p/>
	 * <pre>
	 *   BooleanUtility.toBoolean(0, 1, 0) = false
	 *   BooleanUtility.toBoolean(1, 1, 0) = true
	 *   BooleanUtility.toBoolean(2, 1, 2) = false
	 *   BooleanUtility.toBoolean(2, 2, 0) = true
	 * </pre>
	 *
	 * @param value      the Integer to convert
	 * @param trueValue  the value to match for <code>true</code>
	 * @param falseValue the value to match for <code>false</code>
	 * @return <code>true</code> or <code>false</code>
	 * @throws IllegalArgumentException if no match
	 */
	public static boolean toBoolean(int value, int trueValue, int falseValue)
	{
		if (value == trueValue)
		{
			return true;
		}
		else if (value == falseValue)
		{
			return false;
		}

		// no match
		throw new IllegalArgumentException("The Integer did not match either specified value");
	}

	/**
	 * <p>Converts an Integer to a boolean specifying the conversion values.</p>
	 * <p/>
	 * <pre>
	 *   BooleanUtility.toBoolean(new Integer(0), new Integer(1), new Integer(0)) = false
	 *   BooleanUtility.toBoolean(new Integer(1), new Integer(1), new Integer(0)) = true
	 *   BooleanUtility.toBoolean(new Integer(2), new Integer(1), new Integer(2)) = false
	 *   BooleanUtility.toBoolean(new Integer(2), new Integer(2), new Integer(0)) = true
	 *   BooleanUtility.toBoolean(null, null, new Integer(0))                     = true
	 * </pre>
	 *
	 * @param value      the Integer to convert
	 * @param trueValue  the value to match for <code>true</code>,
	 *                   may be <code>null</code>
	 * @param falseValue the value to match for <code>false</code>,
	 *                   may be <code>null</code>
	 * @return <code>true</code> or <code>false</code>
	 * @throws IllegalArgumentException if no match
	 */
	public static boolean toBoolean(Integer value, Integer trueValue, Integer falseValue)
	{
		if (value == null)
		{
			if (trueValue == null)
			{
				return true;
			}
			else if (falseValue == null)
			{
				return false;
			}
		}
		else if (value.equals(trueValue))
		{
			return true;
		}
		else if (value.equals(falseValue))
		{
			return false;
		}

		// no match
		throw new IllegalArgumentException("The Integer did not match either specified value");
	}

	/**
	 * <p>Converts an int to a Boolean specifying the conversion values.</p>
	 * <p/>
	 * <pre>
	 *   BooleanUtility.toBooleanObject(0, 0, 2, 3) = Boolean.TRUE
	 *   BooleanUtility.toBooleanObject(2, 1, 2, 3) = Boolean.FALSE
	 *   BooleanUtility.toBooleanObject(3, 1, 2, 3) = null
	 * </pre>
	 *
	 * @param value      the Integer to convert
	 * @param trueValue  the value to match for <code>true</code>
	 * @param falseValue the value to match for <code>false</code>
	 * @param nullValue  the value to to match for <code>null</code>
	 * @return Boolean.TRUE, Boolean.FALSE, or <code>null</code>
	 * @throws IllegalArgumentException if no match
	 */
	public static Boolean toBooleanObject(int value, int trueValue, int falseValue, int nullValue)
	{
		if (value == trueValue)
		{
			return Boolean.TRUE;
		}
		else if (value == falseValue)
		{
			return Boolean.FALSE;
		}
		else if (value == nullValue)
		{
			return null;
		}

		// no match
		throw new IllegalArgumentException("The Integer did not match any specified value");
	}

	/**
	 * <p>Converts an Integer to a Boolean specifying the conversion values.</p>
	 * <p/>
	 * <pre>
	 *   BooleanUtility.toBooleanObject(new Integer(0), new Integer(0), new Integer(2), new Integer(3)) = Boolean.TRUE
	 *   BooleanUtility.toBooleanObject(new Integer(2), new Integer(1), new Integer(2), new Integer(3)) = Boolean.FALSE
	 *   BooleanUtility.toBooleanObject(new Integer(3), new Integer(1), new Integer(2), new Integer(3)) = null
	 * </pre>
	 *
	 * @param value      the Integer to convert
	 * @param trueValue  the value to match for <code>true</code>,
	 *                   may be <code>null</code>
	 * @param falseValue the value to match for <code>false</code>,
	 *                   may be <code>null</code>
	 * @param nullValue  the value to to match for <code>null</code>,
	 *                   may be <code>null</code>
	 * @return Boolean.TRUE, Boolean.FALSE, or <code>null</code>
	 * @throws IllegalArgumentException if no match
	 */
	public static Boolean toBooleanObject(Integer value, Integer trueValue, Integer falseValue, Integer nullValue)
	{
		if (value == null)
		{
			if (trueValue == null)
			{
				return Boolean.TRUE;
			}
			else if (falseValue == null)
			{
				return Boolean.FALSE;
			}
			else if (nullValue == null)
			{
				return null;
			}
		}
		else if (value.equals(trueValue))
		{
			return Boolean.TRUE;
		}
		else if (value.equals(falseValue))
		{
			return Boolean.FALSE;
		}
		else if (value.equals(nullValue))
		{
			return null;
		}

		// no match
		throw new IllegalArgumentException("The Integer did not match any specified value");
	}

	// Boolean to Integer methods
	/**
	 * <p>Converts a boolean to an int using the convention that
	 * <code>zero</code> is <code>false</code>.</p>
	 * <p/>
	 * <pre>
	 *   BooleanUtility.toInteger(true)  = 1
	 *   BooleanUtility.toInteger(false) = 0
	 * </pre>
	 *
	 * @param bool the boolean to convert
	 * @return one if <code>true</code>, zero if <code>false</code>
	 */
	public static int toInteger(boolean bool)
	{
		return bool ? 1 : 0;
	}

	/**
	 * <p>Converts a boolean to an Integer using the convention that
	 * <code>zero</code> is <code>false</code>.</p>
	 * <p/>
	 * <pre>
	 *   BooleanUtility.toIntegerObject(true)  = new Integer(1)
	 *   BooleanUtility.toIntegerObject(false) = new Integer(0)
	 * </pre>
	 *
	 * @param bool the boolean to convert
	 * @return one if <code>true</code>, zero if <code>false</code>
	 */
	public static Integer toIntegerObject(boolean bool)
	{
		return bool ? NumberUtility.INTEGER_ONE : NumberUtility.INTEGER_ZERO;
	}

	/**
	 * <p>Converts a Boolean to a Integer using the convention that
	 * <code>zero</code> is <code>false</code>.</p>
	 * <p/>
	 * <p><code>null</code> will be converted to <code>null</code>.</p>
	 * <p/>
	 * <pre>
	 *   BooleanUtility.toIntegerObject(Boolean.TRUE)  = new Integer(1)
	 *   BooleanUtility.toIntegerObject(Boolean.FALSE) = new Integer(0)
	 * </pre>
	 *
	 * @param bool the Boolean to convert
	 * @return one if Boolean.TRUE, zero if Boolean.FALSE, <code>null</code> if <code>null</code>
	 */
	public static Integer toIntegerObject(Boolean bool)
	{
		if (bool == null)
		{
			return null;
		}

		return bool.booleanValue() ? NumberUtility.INTEGER_ONE : NumberUtility.INTEGER_ZERO;
	}

	/**
	 * <p>Converts a boolean to an int specifying the conversion values.</p>
	 * <p/>
	 * <pre>
	 *   BooleanUtility.toInteger(true, 1, 0)  = 1
	 *   BooleanUtility.toInteger(false, 1, 0) = 0
	 * </pre>
	 *
	 * @param bool       the to convert
	 * @param trueValue  the value to return if <code>true</code>
	 * @param falseValue the value to return if <code>false</code>
	 * @return the appropriate value
	 */
	public static int toInteger(boolean bool, int trueValue, int falseValue)
	{
		return bool ? trueValue : falseValue;
	}

	/**
	 * <p>Converts a Boolean to an int specifying the conversion values.</p>
	 * <p/>
	 * <pre>
	 *   BooleanUtility.toInteger(Boolean.TRUE, 1, 0, 2)  = 1
	 *   BooleanUtility.toInteger(Boolean.FALSE, 1, 0, 2) = 0
	 *   BooleanUtility.toInteger(null, 1, 0, 2)          = 2
	 * </pre>
	 *
	 * @param bool       the Boolean to convert
	 * @param trueValue  the value to return if <code>true</code>
	 * @param falseValue the value to return if <code>false</code>
	 * @param nullValue  the value to return if <code>null</code>
	 * @return the appropriate value
	 */
	public static int toInteger(Boolean bool, int trueValue, int falseValue, int nullValue)
	{
		if (bool == null)
		{
			return nullValue;
		}

		return bool.booleanValue() ? trueValue : falseValue;
	}

	/**
	 * <p>Converts a boolean to an Integer specifying the conversion values.</p>
	 * <p/>
	 * <pre>
	 *   BooleanUtility.toIntegerObject(true, new Integer(1), new Integer(0))  = new Integer(1)
	 *   BooleanUtility.toIntegerObject(false, new Integer(1), new Integer(0)) = new Integer(0)
	 * </pre>
	 *
	 * @param bool       the to convert
	 * @param trueValue  the value to return if <code>true</code>,
	 *                   may be <code>null</code>
	 * @param falseValue the value to return if <code>false</code>,
	 *                   may be <code>null</code>
	 * @return the appropriate value
	 */
	public static Integer toIntegerObject(boolean bool, Integer trueValue, Integer falseValue)
	{
		return bool ? trueValue : falseValue;
	}

	/**
	 * <p>Converts a Boolean to an Integer specifying the conversion values.</p>
	 * <p/>
	 * <pre>
	 *   BooleanUtility.toIntegerObject(Boolean.TRUE, new Integer(1), new Integer(0), new Integer(2))  = new Integer(1)
	 *   BooleanUtility.toIntegerObject(Boolean.FALSE, new Integer(1), new Integer(0), new Integer(2)) = new Integer(0)
	 *   BooleanUtility.toIntegerObject(null, new Integer(1), new Integer(0), new Integer(2))          = new Integer(2)
	 * </pre>
	 *
	 * @param bool       the Boolean to convert
	 * @param trueValue  the value to return if <code>true</code>,
	 *                   may be <code>null</code>
	 * @param falseValue the value to return if <code>false</code>,
	 *                   may be <code>null</code>
	 * @param nullValue  the value to return if <code>null</code>,
	 *                   may be <code>null</code>
	 * @return the appropriate value
	 */
	public static Integer toIntegerObject(Boolean bool, Integer trueValue, Integer falseValue, Integer nullValue)
	{
		if (bool == null)
		{
			return nullValue;
		}

		return bool.booleanValue() ? trueValue : falseValue;
	}

	// String to Boolean methods
	/**
	 * <p>Converts a String to a Boolean.</p>
	 * <p/>
	 * <p><code>'true'</code>, <code>'on'</code> or <code>'yes'</code>
	 * (case insensitive) will return <code>true</code>.
	 * <code>'false'</code>, <code>'off'</code> or <code>'no'</code>
	 * (case insensitive) will return <code>false</code>.
	 * Otherwise, <code>null</code> is returned.</p>
	 * <p/>
	 * <pre>
	 *   BooleanUtility.toBooleanObject(null)    = null
	 *   BooleanUtility.toBooleanObject("true")  = Boolean.TRUE
	 *   BooleanUtility.toBooleanObject("false") = Boolean.FALSE
	 *   BooleanUtility.toBooleanObject("on")    = Boolean.TRUE
	 *   BooleanUtility.toBooleanObject("ON")    = Boolean.TRUE
	 *   BooleanUtility.toBooleanObject("off")   = Boolean.FALSE
	 *   BooleanUtility.toBooleanObject("oFf")   = Boolean.FALSE
	 *   BooleanUtility.toBooleanObject("blue")  = null
	 * </pre>
	 *
	 * @param str the String to check
	 * @return the Boolean value of the string,
	 *         <code>null</code> if no match or <code>null</code> input
	 */
	public static Boolean toBooleanObject(String str)
	{
		if ("true".equalsIgnoreCase(str))
		{
			return Boolean.TRUE;
		}
		else if ("false".equalsIgnoreCase(str))
		{
			return Boolean.FALSE;
		}
		else if ("on".equalsIgnoreCase(str))
		{
			return Boolean.TRUE;
		}
		else if ("off".equalsIgnoreCase(str))
		{
			return Boolean.FALSE;
		}
		else if ("yes".equalsIgnoreCase(str))
		{
			return Boolean.TRUE;
		}
		else if ("no".equalsIgnoreCase(str))
		{
			return Boolean.FALSE;
		}

		// no match
		return null;
	}

	/**
	 * <p>Converts a String to a Boolean throwing an exception if no match.</p>
	 * <p/>
	 * <pre>
	 *   BooleanUtility.toBooleanObject("true", "true", "false", "null")  = Boolean.TRUE
	 *   BooleanUtility.toBooleanObject("false", "true", "false", "null") = Boolean.FALSE
	 *   BooleanUtility.toBooleanObject("null", "true", "false", "null")  = null
	 * </pre>
	 *
	 * @param str         the String to check
	 * @param trueString  the String to match for <code>true</code>
	 *                    (case sensitive), may be <code>null</code>
	 * @param falseString the String to match for <code>false</code>
	 *                    (case sensitive), may be <code>null</code>
	 * @param nullString  the String to match for <code>null</code>
	 *                    (case sensitive), may be <code>null</code>
	 * @return the Boolean value of the string,
	 *         <code>null</code> if no match or <code>null</code> input
	 */
	public static Boolean toBooleanObject(String str, String trueString, String falseString, String nullString)
	{
		if (str == null)
		{
			if (trueString == null)
			{
				return Boolean.TRUE;
			}
			else if (falseString == null)
			{
				return Boolean.FALSE;
			}
			else if (nullString == null)
			{
				return null;
			}
		}
		else if (str.equals(trueString))
		{
			return Boolean.TRUE;
		}
		else if (str.equals(falseString))
		{
			return Boolean.FALSE;
		}
		else if (str.equals(nullString))
		{
			return null;
		}

		// no match
		throw new IllegalArgumentException("The String did not match any specified value");
	}

	// String to boolean methods
	/**
	 * <p>Converts a String to a boolean (optimised for performance).</p>
	 * <p/>
	 * <p><code>'true'</code>, <code>'on'</code> or <code>'yes'</code>
	 * (case insensitive) will return <code>true</code>. Otherwise,
	 * <code>false</code> is returned.</p>
	 * <p/>
	 * <p>This method performs 4 times faster (JDK1.4) than
	 * <code>Boolean.valueOf(String)</code>. However, this method accepts
	 * 'on' and 'yes' as true values.
	 * <p/>
	 * <pre>
	 *   BooleanUtility.toBoolean(null)    = false
	 *   BooleanUtility.toBoolean("true")  = true
	 *   BooleanUtility.toBoolean("TRUE")  = true
	 *   BooleanUtility.toBoolean("tRUe")  = true
	 *   BooleanUtility.toBoolean("on")    = true
	 *   BooleanUtility.toBoolean("yes")   = true
	 *   BooleanUtility.toBoolean("false") = false
	 *   BooleanUtility.toBoolean("x gti") = false
	 * </pre>
	 *
	 * @param str the String to check
	 * @return the boolean value of the string, <code>false</code> if no match
	 */
	public static boolean toBoolean(String str)
	{
		if (str == null) return false;
		if (str.equalsIgnoreCase("true")) return true;

		switch (str.length())
		{
			case 2:
				{
					char ch0 = str.charAt(0);
					char ch1 = str.charAt(1);
					return
							(ch0 == 'o' || ch0 == 'O') &&
							(ch1 == 'n' || ch1 == 'N');
				}
			case 3:
				{
					char ch = str.charAt(0);
					if (ch == 'y')
					{
						return
								(str.charAt(1) == 'e' || str.charAt(1) == 'E') &&
								(str.charAt(2) == 's' || str.charAt(2) == 'S');
					}
					if (ch == 'Y')
					{
						return
								(str.charAt(1) == 'E' || str.charAt(1) == 'e') &&
								(str.charAt(2) == 'S' || str.charAt(2) == 's');
					}
				}
			case 4:
				{
					char ch = str.charAt(0);
					if (ch == 't')
					{
						return
								(str.charAt(1) == 'r' || str.charAt(1) == 'R') &&
								(str.charAt(2) == 'u' || str.charAt(2) == 'U') &&
								(str.charAt(3) == 'e' || str.charAt(3) == 'E');
					}
					if (ch == 'T')
					{
						return
								(str.charAt(1) == 'R' || str.charAt(1) == 'r') &&
								(str.charAt(2) == 'U' || str.charAt(2) == 'u') &&
								(str.charAt(3) == 'E' || str.charAt(3) == 'e');
					}
				}
		}

		return false;
	}

	/**
	 * <p>Converts a String to a Boolean throwing an exception if no match found.</p>
	 * <p/>
	 * <p>null is returned if there is no match.</p>
	 * <p/>
	 * <pre>
	 *   BooleanUtility.toBoolean("true", "true", "false")  = true
	 *   BooleanUtility.toBoolean("false", "true", "false") = false
	 * </pre>
	 *
	 * @param str         the String to check
	 * @param trueString  the String to match for <code>true</code>
	 *                    (case sensitive), may be <code>null</code>
	 * @param falseString the String to match for <code>false</code>
	 *                    (case sensitive), may be <code>null</code>
	 * @return the boolean value of the string
	 * @throws IllegalArgumentException if the String doesn't match
	 */
	public static boolean toBoolean(String str, String trueString, String falseString)
	{
		if (str == null)
		{
			if (trueString == null)
			{
				return true;
			}
			else if (falseString == null)
			{
				return false;
			}
		}
		else if (str.equals(trueString))
		{
			return true;
		}
		else if (str.equals(falseString))
		{
			return false;
		}

		// no match
		throw new IllegalArgumentException("The String did not match either specified value");
	}

	// Boolean to String methods
	/**
	 * <p>Converts a Boolean to a String returning <code>'true'</code>,
	 * <code>'false'</code>, or <code>null</code>.</p>
	 * <p/>
	 * <pre>
	 *   BooleanUtility.toStringTrueFalse(Boolean.TRUE)  = "true"
	 *   BooleanUtility.toStringTrueFalse(Boolean.FALSE) = "false"
	 *   BooleanUtility.toStringTrueFalse(null)          = null;
	 * </pre>
	 *
	 * @param bool the Boolean to check
	 * @return <code>'true'</code>, <code>'false'</code>,
	 *         or <code>null</code>
	 */
	public static String toStringTrueFalse(Boolean bool)
	{
		return toString(bool, "true", "false", null);
	}

	/**
	 * <p>Converts a Boolean to a String returning <code>'on'</code>,
	 * <code>'off'</code>, or <code>null</code>.</p>
	 * <p/>
	 * <pre>
	 *   BooleanUtility.toStringOnOff(Boolean.TRUE)  = "on"
	 *   BooleanUtility.toStringOnOff(Boolean.FALSE) = "off"
	 *   BooleanUtility.toStringOnOff(null)          = null;
	 * </pre>
	 *
	 * @param bool the Boolean to check
	 * @return <code>'on'</code>, <code>'off'</code>,
	 *         or <code>null</code>
	 */
	public static String toStringOnOff(Boolean bool)
	{
		return toString(bool, "on", "off", null);
	}

	/**
	 * <p>Converts a Boolean to a String returning <code>'yes'</code>,
	 * <code>'no'</code>, or <code>null</code>.</p>
	 * <p/>
	 * <pre>
	 *   BooleanUtility.toStringYesNo(Boolean.TRUE)  = "yes"
	 *   BooleanUtility.toStringYesNo(Boolean.FALSE) = "no"
	 *   BooleanUtility.toStringYesNo(null)          = null;
	 * </pre>
	 *
	 * @param bool the Boolean to check
	 * @return <code>'yes'</code>, <code>'no'</code>,
	 *         or <code>null</code>
	 */
	public static String toStringYesNo(Boolean bool)
	{
		return toString(bool, "yes", "no", null);
	}

	/**
	 * <p>Converts a Boolean to a String returning one of the input Strings.</p>
	 * <p/>
	 * <pre>
	 *   BooleanUtility.toString(Boolean.TRUE, "true", "false", null)   = "true"
	 *   BooleanUtility.toString(Boolean.FALSE, "true", "false", null)  = "false"
	 *   BooleanUtility.toString(null, "true", "false", null)           = null;
	 * </pre>
	 *
	 * @param bool        the Boolean to check
	 * @param trueString  the String to return if <code>true</code>,
	 *                    may be <code>null</code>
	 * @param falseString the String to return if <code>false</code>,
	 *                    may be <code>null</code>
	 * @param nullString  the String to return if <code>null</code>,
	 *                    may be <code>null</code>
	 * @return one of the three input Strings
	 */
	public static String toString(Boolean bool, String trueString, String falseString, String nullString)
	{
		if (bool == null)
		{
			return nullString;
		}

		return bool.booleanValue() ? trueString : falseString;
	}

	// boolean to String methods
	/**
	 * <p>Converts a boolean to a String returning <code>'true'</code>
	 * or <code>'false'</code>.</p>
	 * <p/>
	 * <pre>
	 *   BooleanUtility.toStringTrueFalse(true)   = "true"
	 *   BooleanUtility.toStringTrueFalse(false)  = "false"
	 * </pre>
	 *
	 * @param bool the Boolean to check
	 * @return <code>'true'</code>, <code>'false'</code>,
	 *         or <code>null</code>
	 */
	public static String toStringTrueFalse(boolean bool)
	{
		return toString(bool, "true", "false");
	}

	/**
	 * <p>Converts a boolean to a String returning <code>'on'</code>
	 * or <code>'off'</code>.</p>
	 * <p/>
	 * <pre>
	 *   BooleanUtility.toStringOnOff(true)   = "on"
	 *   BooleanUtility.toStringOnOff(false)  = "off"
	 * </pre>
	 *
	 * @param bool the Boolean to check
	 * @return <code>'on'</code>, <code>'off'</code>,
	 *         or <code>null</code>
	 */
	public static String toStringOnOff(boolean bool)
	{
		return toString(bool, "on", "off");
	}

	/**
	 * <p>Converts a boolean to a String returning <code>'yes'</code>
	 * or <code>'no'</code>.</p>
	 * <p/>
	 * <pre>
	 *   BooleanUtility.toStringYesNo(true)   = "yes"
	 *   BooleanUtility.toStringYesNo(false)  = "no"
	 * </pre>
	 *
	 * @param bool the Boolean to check
	 * @return <code>'yes'</code>, <code>'no'</code>,
	 *         or <code>null</code>
	 */
	public static String toStringYesNo(boolean bool)
	{
		return toString(bool, "yes", "no");
	}

	/**
	 * <p>Converts a boolean to a String returning one of the input Strings.</p>
	 * <p/>
	 * <pre>
	 *   BooleanUtility.toString(true, "true", "false")   = "true"
	 *   BooleanUtility.toString(false, "true", "false")  = "false"
	 * </pre>
	 *
	 * @param bool        the Boolean to check
	 * @param trueString  the String to return if <code>true</code>,
	 *                    may be <code>null</code>
	 * @param falseString the String to return if <code>false</code>,
	 *                    may be <code>null</code>
	 * @return one of the two input Strings
	 */
	public static String toString(boolean bool, String trueString, String falseString)
	{
		return bool ? trueString : falseString;
	}

	// xor methods
	/**
	 * <p>Performs an xor on a set of booleans.</p>
	 * <p/>
	 * <pre>
	 *   BooleanUtility.xor(new boolean[] { true, true })   = false
	 *   BooleanUtility.xor(new boolean[] { false, false }) = false
	 *   BooleanUtility.xor(new boolean[] { true, false })  = true
	 * </pre>
	 *
	 * @param array an array of <code>boolean<code>s
	 * @return <code>true</code> if the xor is successful.
	 * @throws IllegalArgumentException if <code>array</code> is <code>null</code>
	 * @throws IllegalArgumentException if <code>array</code> is empty.
	 */
	public static boolean xor(boolean[] array)
	{
		// Validates input
		if (array == null)
		{
			throw new IllegalArgumentException("The Array must not be null");
		}
		else if (array.length == 0)
		{
			throw new IllegalArgumentException("Array is empty");
		}

		// Loops through array, comparing each item
		int trueCount = 0;
		for (int i = 0; i < array.length; i++)
		{
			// If item is true, and trueCount is < 1, increments count
			// Else, xor fails
			if (array[i])
			{
				if (trueCount < 1)
				{
					trueCount++;
				}
				else
				{
					return false;
				}
			}
		}

		// Returns true if there was exactly 1 true item
		return trueCount == 1;
	}

	/**
	 * <p>Performs an xor on an array of Booleans.</p>
	 * <p/>
	 * <pre>
	 *   BooleanUtility.xor(new Boolean[] { Boolean.TRUE, Boolean.TRUE })   = Boolean.FALSE
	 *   BooleanUtility.xor(new Boolean[] { Boolean.FALSE, Boolean.FALSE }) = Boolean.FALSE
	 *   BooleanUtility.xor(new Boolean[] { Boolean.TRUE, Boolean.FALSE })  = Boolean.TRUE
	 * </pre>
	 *
	 * @param array an array of <code>Boolean<code>s
	 * @return <code>true</code> if the xor is successful.
	 * @throws IllegalArgumentException if <code>array</code> is <code>null</code>
	 * @throws IllegalArgumentException if <code>array</code> is empty.
	 * @throws IllegalArgumentException if <code>array</code> contains a <code>null</code>
	 */
	public static Boolean xor(Boolean[] array)
	{
		if (array == null)
		{
			throw new IllegalArgumentException("The Array must not be null");
		}
		else if (array.length == 0)
		{
			throw new IllegalArgumentException("Array is empty");
		}
		boolean[] primitive = null;
		try
		{
			primitive = ArrayUtility.toPrimitive(array);
		}
		catch (NullPointerException ex)
		{
			throw new IllegalArgumentException("The array must not contain any null elements");
		}
		return xor(primitive) ? Boolean.TRUE : Boolean.FALSE;
	}

}
