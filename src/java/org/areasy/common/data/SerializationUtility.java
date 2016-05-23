package org.areasy.common.data;

/*
 * Copyright (c) 2007-2016 AREasy Runtime
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

import java.io.*;

/**
 * <p>Assists with the serialization process and performs additional functionality based
 * on serialization.</p>
 * <p/>
 * <ul>
 * <li>Deep clone using serialization
 * <li>Serialize managing finally and IOException
 * <li>Deserialize managing finally and IOException
 * </ul>
 * <p/>
 * <p>This class throws exceptions for invalid <code>null</code> inputs.
 * Each method documents its behaviour in more detail.</p>
 *
 * @version $Id: SerializationUtility.java,v 1.2 2008/05/14 09:32:29 swd\stefan.damian Exp $
 */
public class SerializationUtility
{

	/**
	 * <p>SerializationUtility instances should NOT be constructed in standard programming.
	 * Instead, the class should be used as <code>SerializationUtility.clone(object)</code>.</p>
	 * <p/>
	 * <p>This constructor is public to permit tools that require a JavaBean instance
	 * to operate.</p>
	 *
	 */
	public SerializationUtility()
	{
		super();
	}

	/**
	 * <p>Deep clone an <code>Object</code> using serialization.</p>
	 * <p/>
	 * <p>This is many times slower than writing clone methods by hand
	 * on all objects in your object graph. However, for complex object
	 * graphs, or for those that don't support deep cloning this can
	 * be a simple alternative implementation. Of course all the objects
	 * must be <code>Serializable</code>.</p>
	 *
	 * @param object the <code>Serializable</code> object to clone
	 * @return the cloned object
	 * @throws SerializationException (runtime) if the serialization fails
	 */
	public static Object clone(Serializable object)
	{
		return deserialize(serialize(object));
	}

	/**
	 * <p>Serializes an <code>Object</code> to the specified stream.</p>
	 * <p/>
	 * <p>The stream will be closed once the object is written.
	 * This avoids the need for a finally clause, and maybe also exception
	 * handling, in the application code.</p>
	 * <p/>
	 * <p>The stream passed in is not buffered internally within this method.
	 * This is the responsibility of your application if desired.</p>
	 *
	 * @param obj          the object to serialize to bytes, may be null
	 * @param outputStream the stream to write to, must not be null
	 * @throws IllegalArgumentException if <code>outputStream</code> is <code>null</code>
	 * @throws org.areasy.common.data.SerializationException   (runtime) if the serialization fails
	 */
	public static void serialize(Serializable obj, OutputStream outputStream)
	{
		if (outputStream == null) throw new IllegalArgumentException("The OutputStream must not be null");

		ObjectOutputStream out = null;
		try
		{
			// stream closed in the finally
			out = new ObjectOutputStream(outputStream);
			out.writeObject(obj);

		}
		catch (IOException ex)
		{
			throw new SerializationException(ex);
		}
		finally
		{
			try
			{
				if (out != null)
				{
					out.close();
				}
			}
			catch (IOException ex)
			{
				// ignore;
			}
		}
	}

	/**
	 * <p>Serializes an <code>Object</code> to a byte array for
	 * storage/serialization.</p>
	 *
	 * @param obj the object to serialize to bytes
	 * @return a byte[] with the converted Serializable
	 * @throws SerializationException (runtime) if the serialization fails
	 */
	public static byte[] serialize(Serializable obj)
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream(512);
		serialize(obj, baos);
		return baos.toByteArray();
	}

	// Deserialize
	/**
	 * <p>Deserializes an <code>Object</code> from the specified stream.</p>
	 * <p/>
	 * <p>The stream will be closed once the object is written. This
	 * avoids the need for a finally clause, and maybe also exception
	 * handling, in the application code.</p>
	 * <p/>
	 * <p>The stream passed in is not buffered internally within this method.
	 * This is the responsibility of your application if desired.</p>
	 *
	 * @param inputStream the serialized object input stream, must not be null
	 * @return the deserialized object
	 * @throws IllegalArgumentException if <code>inputStream</code> is <code>null</code>
	 * @throws SerializationException   (runtime) if the serialization fails
	 */
	public static Object deserialize(InputStream inputStream)
	{
		if (inputStream == null) throw new IllegalArgumentException("The InputStream must not be null");

		ObjectInputStream in = null;
		try
		{
			// stream closed in the finally
			in = new ObjectInputStream(inputStream);
			return in.readObject();

		}
		catch (ClassNotFoundException ex)
		{
			throw new SerializationException(ex);
		}
		catch (IOException ex)
		{
			throw new SerializationException(ex);
		}
		finally
		{
			try
			{
				if (in != null)
				{
					in.close();
				}
			}
			catch (IOException ex)
			{
				// ignore
			}
		}
	}

	/**
	 * <p>Deserializes a single <code>Object</code> from an array of bytes.</p>
	 *
	 * @param objectData the serialized object, must not be null
	 * @return the deserialized object
	 * @throws IllegalArgumentException if <code>objectData</code> is <code>null</code>
	 * @throws SerializationException   (runtime) if the serialization fails
	 */
	public static Object deserialize(byte[] objectData)
	{
		if (objectData == null) throw new IllegalArgumentException("The byte[] must not be null");

		ByteArrayInputStream bais = new ByteArrayInputStream(objectData);
		return deserialize(bais);
	}
}
