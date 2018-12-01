package org.areasy.common.data.bean;

/*
 * Copyright (c) 2007-2018 AREasy Runtime
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
 * <p>Utility methods for converting String scalar values to objects of the
 * specified Class, String arrays to arrays of the specified Class.</p>
 * <p/>
 * <p>For more details, see <code>ConvertUtilsBean</code> which provides the
 * implementations for these methods.</p>
 *
 * @version $Id: ConvertUtility.java,v 1.2 2008/05/14 09:32:37 swd\stefan.damian Exp $
 */

public class ConvertUtility
{
	/**
	 * Gets the default value for Boolean conversions.
	 */
	public static boolean getDefaultBoolean()
	{
		return (ConvertBean.getInstance().getDefaultBoolean());
	}

	/**
	 * Sets the default value for Boolean conversions.
	 */
	public static void setDefaultBoolean(boolean newDefaultBoolean)
	{
		ConvertBean.getInstance().setDefaultBoolean(newDefaultBoolean);
	}


	/**
	 * Gets the default value for Byte conversions.
	 *
	 * @deprecated Register replacement converters for Byte.TYPE and
	 *             Byte.class instead
	 */
	public static byte getDefaultByte()
	{
		return ConvertBean.getInstance().getDefaultByte();
	}

	/**
	 * Sets the default value for Byte conversions.
	 */
	public static void setDefaultByte(byte newDefaultByte)
	{
		ConvertBean.getInstance().setDefaultByte(newDefaultByte);
	}


	/**
	 * Gets the default value for Character conversions.
	 */
	public static char getDefaultCharacter()
	{
		return ConvertBean.getInstance().getDefaultCharacter();
	}

	/**
	 * Sets the default value for Character conversions.
	 */
	public static void setDefaultCharacter(char newDefaultCharacter)
	{
		ConvertBean.getInstance().setDefaultCharacter(newDefaultCharacter);
	}


	/**
	 * Gets the default value for Double conversions.
	 */
	public static double getDefaultDouble()
	{
		return ConvertBean.getInstance().getDefaultDouble();
	}

	/**
	 * Sets the default value for Double conversions.
	 */
	public static void setDefaultDouble(double newDefaultDouble)
	{
		ConvertBean.getInstance().setDefaultDouble(newDefaultDouble);
	}


	/**
	 * Get the default value for Float conversions.
	 */
	public static float getDefaultFloat()
	{
		return ConvertBean.getInstance().getDefaultFloat();
	}

	/**
	 * Sets the default value for Float conversions.
	 */
	public static void setDefaultFloat(float newDefaultFloat)
	{
		ConvertBean.getInstance().setDefaultFloat(newDefaultFloat);
	}


	/**
	 * Gets the default value for Integer conversions.
	 */
	public static int getDefaultInteger()
	{
		return ConvertBean.getInstance().getDefaultInteger();
	}

	/**
	 * Sets the default value for Integer conversions.
	 */
	public static void setDefaultInteger(int newDefaultInteger)
	{
		ConvertBean.getInstance().setDefaultInteger(newDefaultInteger);
	}


	/**
	 * Gets the default value for Long conversions.
	 */
	public static long getDefaultLong()
	{
		return (ConvertBean.getInstance().getDefaultLong());
	}

	/**
	 * Sets the default value for Long conversions.
	 */
	public static void setDefaultLong(long newDefaultLong)
	{
		ConvertBean.getInstance().setDefaultLong(newDefaultLong);
	}


	/**
	 * Gets the default value for Short conversions.
	 *
	 * @deprecated Register replacement converters for Short.TYPE and
	 *             Short.class instead
	 */
	public static short getDefaultShort()
	{
		return ConvertBean.getInstance().getDefaultShort();
	}

	/**
	 * Sets the default value for Short conversions.
	 */
	public static void setDefaultShort(short newDefaultShort)
	{
		ConvertBean.getInstance().setDefaultShort(newDefaultShort);
	}


	/**
	 * <p>Convert the specified value into a String.</p>
	 * <p/>
	 * <p>For more details see <code>ConvertUtilsBean</code>.</p>
	 *
	 * @see ConvertBean#convert(Object)
	 */
	public static String convert(Object value)
	{
		return ConvertBean.getInstance().convert(value);
	}


	/**
	 * <p>Convert the specified value to an object of the specified class (if
	 * possible).  Otherwise, return a String representation of the value.</p>
	 * <p/>
	 * <p>For more details see <code>ConvertUtilsBean</code>.</p>
	 *
	 * @see ConvertBean#convert(String, Class)
	 */
	public static Object convert(String value, Class clazz)
	{
		return ConvertBean.getInstance().convert(value, clazz);
	}


	/**
	 * <p>Convert an array of specified values to an array of objects of the
	 * specified class (if possible).</p>
	 * <p/>
	 * <p>For more details see <code>ConvertUtilsBean</code>.</p>
	 *
	 * @see ConvertBean#convert(String[], Class)
	 */
	public static Object convert(String values[], Class clazz)
	{
		return ConvertBean.getInstance().convert(values, clazz);
	}


	/**
	 * <p>Remove all registered {@link org.areasy.common.data.bean.Converter}s, and re-establish the
	 * standard Converters.</p>
	 * <p/>
	 * <p>For more details see <code>ConvertUtilsBean</code>.</p>
	 *
	 * @see ConvertBean#deregister()
	 */
	public static void deregister()
	{
		ConvertBean.getInstance().deregister();
	}


	/**
	 * <p>Remove any registered {@link org.areasy.common.data.bean.Converter} for the specified destination
	 * <code>Class</code>.</p>
	 * <p/>
	 * <p>For more details see <code>ConvertUtilsBean</code>.</p>
	 *
	 * @see ConvertBean#deregister(Class)
	 */
	public static void deregister(Class clazz)
	{
		ConvertBean.getInstance().deregister(clazz);
	}


	/**
	 * <p>Look up and return any registered {@link org.areasy.common.data.bean.Converter} for the specified
	 * destination class; if there is no registered Converter, return
	 * <code>null</code>.</p>
	 * <p/>
	 * <p>For more details see <code>ConvertUtilsBean</code>.</p>
	 *
	 * @see ConvertBean#lookup(Class)
	 */
	public static Converter lookup(Class clazz)
	{
		return ConvertBean.getInstance().lookup(clazz);
	}


	/**
	 * <p>Register a custom {@link Converter} for the specified destination
	 * <code>Class</code>, replacing any previously registered Converter.</p>
	 * <p/>
	 * <p>For more details see <code>ConvertUtilsBean</code>.</p>
	 *
	 * @see ConvertBean#register(Converter, Class)
	 */
	public static void register(Converter converter, Class clazz)
	{
		ConvertBean.getInstance().register(converter, clazz);
	}
}
