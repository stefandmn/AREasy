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

import org.areasy.common.data.bean.converters.*;
import org.areasy.common.data.type.FastHashMap;
import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;

import java.io.File;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;


/**
 * <p>Utility methods for converting String scalar values to objects of the
 * specified Class, String arrays to arrays of the specified Class.  The
 * actual {@link Converter} instance to be used can be registered for each
 * possible destination Class.  Unless you override them, standard
 * {@link Converter} instances are provided for all of the following
 * destination Classes:</p>
 * <ul>
 * <li>java.lang.BigDecimal</li>
 * <li>java.lang.BigInteger</li>
 * <li>boolean and java.lang.Boolean</li>
 * <li>byte and java.lang.Byte</li>
 * <li>char and java.lang.Character</li>
 * <li>java.lang.Class</li>
 * <li>double and java.lang.Double</li>
 * <li>float and java.lang.Float</li>
 * <li>int and java.lang.Integer</li>
 * <li>long and java.lang.Long</li>
 * <li>short and java.lang.Short</li>
 * <li>java.lang.String</li>
 * <li>java.io.File</li>
 * <li>java.net.URL</li>
 * <li>java.sql.Date</li>
 * <li>java.sql.Time</li>
 * <li>java.sql.Timestamp</li>
 * </ul>
 * <p/>
 * <p>For backwards compatibility, the standard Converters for primitive
 * types (and the corresponding wrapper classes) return a defined
 * default value when a conversion error occurs.  If you prefer to have a
 * {@link ConversionException} thrown instead, replace the standard Converter
 * instances with instances created with the zero-arguments constructor.  For
 * example, to cause the Converters for integers to throw an exception on
 * conversion errors, you could do this:</p>
 * <pre>
 *   // No-args constructor gets the version that throws exceptions
 *   Converter myConverter = IntegerConverter();
 *   ConvertUtility.register(myConverter, Integer.TYPE);    // Native type
 *   ConvertUtility.register(myConverter, Integer.class);   // Wrapper class
 * </pre>
 *
 * @version $Id: ConvertBean.java,v 1.2 2008/05/14 09:32:37 swd\stefan.damian Exp $
 */

public class ConvertBean
{
	/**
	 * Get singleton instance
	 */
	protected static ConvertBean getInstance()
	{
		return BeanUtilityBean.getInstance().getConvertUtility();
	}

	/**
	 * The set of {@link Converter}s that can be used to convert Strings
	 * into objects of a specified Class, keyed by the destination Class.
	 */
	private FastHashMap converters = new FastHashMap();

	/**
	 * The <code>Log</code> instance for this class.
	 */
	private Logger log = LoggerFactory.getLog(ConvertUtility.class);

	/**
	 * Construct a bean with standard converters registered
	 */
	public ConvertBean()
	{
		converters.setFast(false);

		deregister();

		converters.setFast(true);
	}


	/** The default value for Boolean conversions. */
	private Boolean defaultBoolean = Boolean.FALSE;

	/**
	 * Gets the default value for Boolean conversions.
	 */
	public boolean getDefaultBoolean()
	{
		return (defaultBoolean.booleanValue());
	}

	/**
	 * Sets the default value for Boolean conversions.
	 */
	public void setDefaultBoolean(boolean newDefaultBoolean)
	{
		defaultBoolean = new Boolean(newDefaultBoolean);

		register(new BooleanConverter(defaultBoolean), Boolean.TYPE);
		register(new BooleanConverter(defaultBoolean), Boolean.class);
	}


	/** The default value for Byte conversions. */
	private Byte defaultByte = new Byte((byte) 0);

	/**
	 * Gets the default value for Byte conversions.
	 */
	public byte getDefaultByte()
	{
		return (defaultByte.byteValue());
	}

	/**
	 * Sets the default value for Byte conversions.
	 */
	public void setDefaultByte(byte newDefaultByte)
	{
		defaultByte = new Byte(newDefaultByte);

		register(new ByteConverter(defaultByte), Byte.TYPE);
		register(new ByteConverter(defaultByte), Byte.class);
	}


	/** The default value for Character conversions. */
	private Character defaultCharacter = new Character(' ');

	/**
	 * Gets the default value for Character conversions.
	 */
	public char getDefaultCharacter()
	{
		return (defaultCharacter.charValue());
	}

	/**
	 * Sets the default value for Character conversions.
	 */
	public void setDefaultCharacter(char newDefaultCharacter)
	{
		defaultCharacter = new Character(newDefaultCharacter);

		register(new CharacterConverter(defaultCharacter), Character.TYPE);
		register(new CharacterConverter(defaultCharacter), Character.class);
	}


	/** The default value for Double conversions. */
	private Double defaultDouble = new Double((double) 0.0);

	/**
	 * Gets the default value for Double conversions.
	 */
	public double getDefaultDouble()
	{
		return (defaultDouble.doubleValue());
	}

	/**
	 * Sets the default value for Double conversions.
	 *
	 * @deprecated Register replacement converters for Double.TYPE and
	 *             Double.class instead
	 */
	public void setDefaultDouble(double newDefaultDouble)
	{
		defaultDouble = new Double(newDefaultDouble);

		register(new DoubleConverter(defaultDouble), Double.TYPE);
		register(new DoubleConverter(defaultDouble), Double.class);
	}


	/** The default value for Float conversions. */
	private Float defaultFloat = new Float((float) 0.0);

	/**
	 * Gets the default value for Float conversions.
	 */
	public float getDefaultFloat()
	{
		return (defaultFloat.floatValue());
	}

	/**
	 * Sets the default value for Float conversions.
	 */
	public void setDefaultFloat(float newDefaultFloat)
	{
		defaultFloat = new Float(newDefaultFloat);

		register(new FloatConverter(defaultFloat), Float.TYPE);
		register(new FloatConverter(defaultFloat), Float.class);
	}


	/** The default value for Integer conversions. */
	private Integer defaultInteger = new Integer(0);

	/**
	 * Gets the default value for Integer conversions.
	 */
	public int getDefaultInteger()
	{
		return (defaultInteger.intValue());
	}

	/**
	 * Sets the default value for Integer conversions.
	 *
	 */
	public void setDefaultInteger(int newDefaultInteger)
	{
		defaultInteger = new Integer(newDefaultInteger);

		register(new IntegerConverter(defaultInteger), Integer.TYPE);
		register(new IntegerConverter(defaultInteger), Integer.class);
	}


	/** The default value for Long conversions. */
	private Long defaultLong = new Long((long) 0);

	/**
	 * Gets the default value for Long conversions.
	 */
	public long getDefaultLong()
	{
		return (defaultLong.longValue());
	}

	/**
	 * Sets the default value for Long conversions.
	 */
	public void setDefaultLong(long newDefaultLong)
	{
		defaultLong = new Long(newDefaultLong);

		register(new LongConverter(defaultLong), Long.TYPE);
		register(new LongConverter(defaultLong), Long.class);
	}


	/** The default value for Short conversions. */
	private static Short defaultShort = new Short((short) 0);

	/**
	 * Gets the default value for Short conversions.
	 */
	public short getDefaultShort()
	{
		return (defaultShort.shortValue());
	}

	/**
	 * Sets the default value for Short conversions.
	 */
	public void setDefaultShort(short newDefaultShort)
	{
		defaultShort = new Short(newDefaultShort);

		register(new ShortConverter(defaultShort), Short.TYPE);
		register(new ShortConverter(defaultShort), Short.class);
	}


	/**
	 * Convert the specified value into a String.  If the specified value
	 * is an array, the first element (converted to a String) will be
	 * returned.  The registered {@link Converter} for the
	 * <code>java.lang.String</code> class will be used, which allows
	 * applications to customize Object->String conversions (the default
	 * implementation simply uses toString()).
	 *
	 * @param value Value to be converted (may be null)
	 */
	public String convert(Object value)
	{
		if (value == null) return ((String) null);
		else if (value.getClass().isArray())
		{
			if (Array.getLength(value) < 1) return (null);

			value = Array.get(value, 0);
			if (value == null) return ((String) null);
			else
			{
				Converter converter = lookup(String.class);
				return ((String) converter.convert(String.class, value));
			}
		}
		else
		{
			Converter converter = lookup(String.class);
			return ((String) converter.convert(String.class, value));
		}
	}

	/**
	 * Convert the specified value to an object of the specified class (if
	 * possible).  Otherwise, return a String representation of the value.
	 *
	 * @param value Value to be converted (may be null)
	 * @param clazz Java class to be converted to
	 * @throws ConversionException if thrown by an underlying Converter
	 */
	public Object convert(String value, Class clazz)
	{
		log.debug("Convert string '" + value + "' to class '" + clazz.getName() + "'");

		Converter converter = lookup(clazz);
		if (converter == null) converter = lookup(String.class);

		return (converter.convert(clazz, value));
	}


	/**
	 * Convert an array of specified values to an array of objects of the
	 * specified class (if possible).  If the specified Java class is itself
	 * an array class, this class will be the type of the returned value.
	 * Otherwise, an array will be constructed whose component type is the
	 * specified class.
	 *
	 * @param values Values to be converted (may be null)
	 * @param clazz  Java array or element class to be converted to
	 * @throws ConversionException if thrown by an underlying Converter
	 */
	public Object convert(String values[], Class clazz)
	{
		Class type = clazz;
		if (clazz.isArray()) type = clazz.getComponentType();

		log.debug("Convert String[" + values.length + "] to class '" + type.getName() + "[]'");

		Converter converter = lookup(type);
		if (converter == null) converter = lookup(String.class);

		Object array = Array.newInstance(type, values.length);
		for (int i = 0; i < values.length; i++)
		{
			Array.set(array, i, converter.convert(type, values[i]));
		}

		return (array);
	}


	/**
	 * Remove all registered {@link Converter}s, and re-establish the
	 * standard Converters.
	 */
	public void deregister()
	{

		boolean booleanArray[] = new boolean[0];
		byte byteArray[] = new byte[0];
		char charArray[] = new char[0];
		double doubleArray[] = new double[0];
		float floatArray[] = new float[0];
		int intArray[] = new int[0];
		long longArray[] = new long[0];
		short shortArray[] = new short[0];
		String stringArray[] = new String[0];

		converters.clear();

		register(BigDecimal.class, new BigDecimalConverter());
		register(BigInteger.class, new BigIntegerConverter());
		register(Boolean.TYPE, new BooleanConverter(defaultBoolean));
		register(Boolean.class, new BooleanConverter(defaultBoolean));
		register(booleanArray.getClass(), new BooleanArrayConverter(booleanArray));
		register(Byte.TYPE, new ByteConverter(defaultByte));
		register(Byte.class, new ByteConverter(defaultByte));
		register(byteArray.getClass(), new ByteArrayConverter(byteArray));
		register(Character.TYPE, new CharacterConverter(defaultCharacter));
		register(Character.class, new CharacterConverter(defaultCharacter));
		register(charArray.getClass(), new CharacterArrayConverter(charArray));
		register(Class.class, new ClassConverter());
		register(Double.TYPE, new DoubleConverter(defaultDouble));
		register(Double.class, new DoubleConverter(defaultDouble));
		register(doubleArray.getClass(), new DoubleArrayConverter(doubleArray));
		register(Float.TYPE, new FloatConverter(defaultFloat));
		register(Float.class, new FloatConverter(defaultFloat));
		register(floatArray.getClass(), new FloatArrayConverter(floatArray));
		register(Integer.TYPE, new IntegerConverter(defaultInteger));
		register(Integer.class, new IntegerConverter(defaultInteger));
		register(intArray.getClass(), new IntegerArrayConverter(intArray));
		register(Long.TYPE, new LongConverter(defaultLong));
		register(Long.class, new LongConverter(defaultLong));
		register(longArray.getClass(), new LongArrayConverter(longArray));
		register(Short.TYPE, new ShortConverter(defaultShort));
		register(Short.class, new ShortConverter(defaultShort));
		register(shortArray.getClass(), new ShortArrayConverter(shortArray));
		register(String.class, new StringConverter());
		register(stringArray.getClass(), new StringArrayConverter(stringArray));
		register(Date.class, new SqlDateConverter());
		register(Time.class, new SqlTimeConverter());
		register(Timestamp.class, new SqlTimestampConverter());
		register(File.class, new FileConverter());
		register(URL.class, new URLConverter());
	}

	/**
	 * strictly for convenience since it has same parameter order as Map.put
	 */
	private void register(Class clazz, Converter converter)
	{
		register(converter, clazz);
	}

	/**
	 * Remove any registered {@link Converter} for the specified destination
	 * <code>Class</code>.
	 *
	 * @param clazz Class for which to remove a registered Converter
	 */
	public void deregister(Class clazz)
	{
		converters.remove(clazz);
	}


	/**
	 * Look up and return any registered {@link Converter} for the specified
	 * destination class; if there is no registered Converter, return
	 * <code>null</code>.
	 *
	 * @param clazz Class for which to return a registered Converter
	 */
	public Converter lookup(Class clazz)
	{
		return ((Converter) converters.get(clazz));
	}


	/**
	 * Register a custom {@link Converter} for the specified destination
	 * <code>Class</code>, replacing any previously registered Converter.
	 *
	 * @param converter Converter to be registered
	 * @param clazz     Destination class for conversions performed by this
	 *                  Converter
	 */
	public void register(Converter converter, Class clazz)
	{
		converters.put(clazz, converter);
	}
}
