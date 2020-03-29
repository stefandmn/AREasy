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

import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * TYPE Utilities.
 * Provides various static utility methods for manipulating types and their
 * string representations.
 */
public class TypeUtility
{
	private static Logger log = LoggerFactory.getLog(TypeUtility.class);

	private static final HashMap name2Class = new HashMap();

	static
	{
		name2Class.put("boolean", java.lang.Boolean.TYPE);
		name2Class.put("byte", java.lang.Byte.TYPE);
		name2Class.put("char", java.lang.Character.TYPE);
		name2Class.put("double", java.lang.Double.TYPE);
		name2Class.put("float", java.lang.Float.TYPE);
		name2Class.put("int", java.lang.Integer.TYPE);
		name2Class.put("long", java.lang.Long.TYPE);
		name2Class.put("short", java.lang.Short.TYPE);
		name2Class.put("void", java.lang.Void.TYPE);

		name2Class.put("java.lang.Boolean.TYPE", java.lang.Boolean.TYPE);
		name2Class.put("java.lang.Byte.TYPE", java.lang.Byte.TYPE);
		name2Class.put("java.lang.Character.TYPE", java.lang.Character.TYPE);
		name2Class.put("java.lang.Double.TYPE", java.lang.Double.TYPE);
		name2Class.put("java.lang.Float.TYPE", java.lang.Float.TYPE);
		name2Class.put("java.lang.Integer.TYPE", java.lang.Integer.TYPE);
		name2Class.put("java.lang.Long.TYPE", java.lang.Long.TYPE);
		name2Class.put("java.lang.Short.TYPE", java.lang.Short.TYPE);
		name2Class.put("java.lang.Void.TYPE", java.lang.Void.TYPE);

		name2Class.put("java.lang.Boolean", java.lang.Boolean.class);
		name2Class.put("java.lang.Byte", java.lang.Byte.class);
		name2Class.put("java.lang.Character", java.lang.Character.class);
		name2Class.put("java.lang.Double", java.lang.Double.class);
		name2Class.put("java.lang.Float", java.lang.Float.class);
		name2Class.put("java.lang.Integer", java.lang.Integer.class);
		name2Class.put("java.lang.Long", java.lang.Long.class);
		name2Class.put("java.lang.Short", java.lang.Short.class);

		name2Class.put("Boolean", java.lang.Boolean.class);
		name2Class.put("Byte", java.lang.Byte.class);
		name2Class.put("Character", java.lang.Character.class);
		name2Class.put("Double", java.lang.Double.class);
		name2Class.put("Float", java.lang.Float.class);
		name2Class.put("Integer", java.lang.Integer.class);
		name2Class.put("Long", java.lang.Long.class);
		name2Class.put("Short", java.lang.Short.class);

		name2Class.put(null, java.lang.Void.TYPE);
		name2Class.put("string", java.lang.String.class);
		name2Class.put("String", java.lang.String.class);
		name2Class.put("java.lang.String", java.lang.String.class);
	}

	private static final HashMap class2Name = new HashMap();

	static
	{
		class2Name.put(java.lang.Boolean.TYPE, "boolean");
		class2Name.put(java.lang.Byte.TYPE, "byte");
		class2Name.put(java.lang.Character.TYPE, "char");
		class2Name.put(java.lang.Double.TYPE, "double");
		class2Name.put(java.lang.Float.TYPE, "float");
		class2Name.put(java.lang.Integer.TYPE, "int");
		class2Name.put(java.lang.Long.TYPE, "long");
		class2Name.put(java.lang.Short.TYPE, "short");
		class2Name.put(java.lang.Void.TYPE, "void");

		class2Name.put(java.lang.Boolean.class, "java.lang.Boolean");
		class2Name.put(java.lang.Byte.class, "java.lang.Byte");
		class2Name.put(java.lang.Character.class, "java.lang.Character");
		class2Name.put(java.lang.Double.class, "java.lang.Double");
		class2Name.put(java.lang.Float.class, "java.lang.Float");
		class2Name.put(java.lang.Integer.class, "java.lang.Integer");
		class2Name.put(java.lang.Long.class, "java.lang.Long");
		class2Name.put(java.lang.Short.class, "java.lang.Short");

		class2Name.put(null, "void");
		name2Class.put(java.lang.String.class, "java.lang.String");
	}

	private static final HashMap class2Value = new HashMap();

	static
	{
		try
		{
			Class[] s = {java.lang.String.class};

			class2Value.put(java.lang.Boolean.TYPE, java.lang.Boolean.class.getMethod("valueOf", s));
			class2Value.put(java.lang.Byte.TYPE, java.lang.Byte.class.getMethod("valueOf", s));
			class2Value.put(java.lang.Double.TYPE, java.lang.Double.class.getMethod("valueOf", s));
			class2Value.put(java.lang.Float.TYPE, java.lang.Float.class.getMethod("valueOf", s));
			class2Value.put(java.lang.Integer.TYPE, java.lang.Integer.class.getMethod("valueOf", s));
			class2Value.put(java.lang.Long.TYPE, java.lang.Long.class.getMethod("valueOf", s));
			class2Value.put(java.lang.Short.TYPE, java.lang.Short.class.getMethod("valueOf", s));

			class2Value.put(java.lang.Boolean.class, java.lang.Boolean.class.getMethod("valueOf", s));
			class2Value.put(java.lang.Byte.class, java.lang.Byte.class.getMethod("valueOf", s));
			class2Value.put(java.lang.Double.class, java.lang.Double.class.getMethod("valueOf", s));
			class2Value.put(java.lang.Float.class, java.lang.Float.class.getMethod("valueOf", s));
			class2Value.put(java.lang.Integer.class, java.lang.Integer.class.getMethod("valueOf", s));
			class2Value.put(java.lang.Long.class, java.lang.Long.class.getMethod("valueOf", s));
			class2Value.put(java.lang.Short.class, java.lang.Short.class.getMethod("valueOf", s));
		}
		catch (Exception e)
		{
			log.warn("Exception", e);
		}
	}

	private static Class[] stringArg = {java.lang.String.class};

	private static int intCacheSize = Integer.getInteger("org.areasy.common.data.TypeUtility.IntegerCacheSize", 600).intValue();
	private static Integer[] integerCache = new Integer[intCacheSize];
	private static String[] integerStrCache = new String[intCacheSize];
	private static Integer minusOne = new Integer(-1);

	/**
	 * Class from a canonical name for a type.
	 *
	 * @param name A class or type name.
	 * @return A class , which may be a primitive TYPE field..
	 */
	public static Class fromName(String name)
	{
		return (Class) name2Class.get(name);
	}

	/**
	 * Canonical name for a type.
	 *
	 * @param type A class , which may be a primitive TYPE field.
	 * @return Canonical name.
	 */
	public static String toName(Class type)
	{
		return (String) class2Name.get(type);
	}

	/**
	 * Convert String value to instance.
	 *
	 * @param type  The class of the instance, which may be a primitive TYPE field.
	 * @param value The value as a string.
	 * @return The value as an Object.
	 */
	public static Object valueOf(Class type, String value)
	{
		try
		{
			if (type.equals(java.lang.String.class)) return value;

			Method m = (Method) class2Value.get(type);
			if (m != null) return m.invoke(null, new Object[]{value});

			if (type.equals(java.lang.Character.TYPE) || type.equals(java.lang.Character.class)) return new Character(value.charAt(0));

			Constructor c = type.getConstructor(stringArg);
			return c.newInstance(new Object[]{value});
		}
		catch (NoSuchMethodException e)
		{
			log.trace(e);
		}
		catch (IllegalAccessException e)
		{
			log.trace(e);
		}
		catch (InstantiationException e)
		{
			log.trace(e);
		}
		catch (InvocationTargetException e)
		{
			if (e.getTargetException() instanceof Error) throw (Error) (e.getTargetException());

			log.trace(e);
		}
		return null;
	}

	/**
	 * Convert String value to instance.
	 *
	 * @param type  classname or type (eg int)
	 * @param value The value as a string.
	 * @return The value as an Object.
	 */
	public static Object valueOf(String type, String value)
	{
		return valueOf(fromName(type), value);
	}

	/**
	 * Convert int to Integer using cache.
	 */
	public static Integer newInteger(int i)
	{
		if (i >= 0 && i < intCacheSize)
		{
			if (integerCache[i] == null) integerCache[i] = new Integer(i);

			return integerCache[i];
		}
		else if (i == -1) return minusOne;

		return new Integer(i);
	}


	/**
	 * Convert int to String using cache.
	 */
	public static String toString(int i)
	{
		if (i >= 0 && i < intCacheSize)
		{
			if (integerStrCache[i] == null) integerStrCache[i] = Integer.toString(i);

			return integerStrCache[i];
		}
		else if (i == -1) return "-1";

		return Integer.toString(i);
	}


	/**
	 * Parse an int from a substring.
	 * Negative numbers are not handled.
	 *
	 * @param s      String
	 * @param offset Offset within string
	 * @param length Length of integer or -1 for remainder of string
	 * @param base   base of the integer
	 * @throws NumberFormatException
	 */
	public static int parseInt(String s, int offset, int length, int base) throws NumberFormatException
	{
		int value = 0;

		if (length < 0) length = s.length() - offset;

		for (int i = 0; i < length; i++)
		{
			char c = s.charAt(offset + i);

			int digit = c - '0';
			if (digit < 0 || digit >= base || digit >= 10)
			{
				digit = 10 + c - 'A';
				if (digit < 10 || digit >= base) digit = 10 + c - 'a';
			}

			if (digit < 0 || digit >= base) throw new NumberFormatException(s.substring(offset, offset + length));

			value = value * base + digit;
		}

		return value;
	}

	public static byte[] parseBytes(String s, int base)
	{
		byte[] bytes = new byte[s.length() / 2];
		for (int i = 0; i < s.length(); i += 2)
		{
			bytes[i / 2] = (byte) TypeUtility.parseInt(s, i, 2, base);
		}

		return bytes;
	}

	public static String toString(byte[] bytes, int base)
	{
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < bytes.length; i++)
		{
			int bi = 0xff & bytes[i];
			int c = '0' + (bi / base) % base;

			if (c > '9') c = 'a' + (c - '0' - 10);

			buf.append((char) c);
			c = '0' + bi % base;

			if (c > '9') c = 'a' + (c - '0' - 10);

			buf.append((char) c);
		}

		return buf.toString();
	}

	/**
	 * @param b An ASCII encoded character 0-9 a-f A-F
	 * @return The byte value of the character 0-16.
	 */
	public static byte convertHexDigit(byte b)
	{
		if ((b >= '0') && (b <= '9')) return (byte) (b - '0');
		if ((b >= 'a') && (b <= 'f')) return (byte) (b - 'a' + 10);
		if ((b >= 'A') && (b <= 'F')) return (byte) (b - 'A' + 10);

		return 0;
	}

	public static String toHexString(byte[] b)
	{
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < b.length; i++)
		{
			int bi = 0xff & b[i];
			int c = '0' + (bi / 16) % 16;

			if (c > '9') c = 'A' + (c - '0' - 10);

			buf.append((char) c);
			c = '0' + bi % 16;

			if (c > '9') c = 'a' + (c - '0' - 10);

			buf.append((char) c);
		}
		return buf.toString();
	}

	public static String toHexString(byte[] b, int offset, int length)
	{
		StringBuffer buf = new StringBuffer();
		for (int i = offset; i < offset + length; i++)
		{
			int bi = 0xff & b[i];
			int c = '0' + (bi / 16) % 16;
			if (c > '9')
			{
				c = 'A' + (c - '0' - 10);
			}
			buf.append((char) c);
			c = '0' + bi % 16;
			if (c > '9')
			{
				c = 'a' + (c - '0' - 10);
			}
			buf.append((char) c);
		}
		return buf.toString();
	}

	public static byte[] fromHexString(String s)
	{
		if (s.length() % 2 != 0) throw new IllegalArgumentException(s);

		byte[] array = new byte[s.length() / 2];
		for (int i = 0; i < array.length; i++)
		{
			int b = Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16);
			array[i] = (byte) (0xff & b);
		}

		return array;
	}
}

