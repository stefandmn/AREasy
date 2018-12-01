package org.areasy.common.data;

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

import java.io.*;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

/**
 * <p>Operations on <code>Object</code>.</p>
 * <p/>
 * <p>This class tries to handle <code>null</code> input gracefully.
 * An exception will generally not be thrown for a <code>null</code> input.
 * Each method documents its behaviour in more detail.</p>
 *
 * @version $Id: ObjectUtility.java,v 1.2 2008/05/14 09:32:29 swd\stefan.damian Exp $
 */
public class ObjectUtility
{

	/**
	 * <p>Singleton used as a <code>null</code> placeholder where
	 * <code>null</code> has another meaning.</p>
	 * <p/>
	 * <p>For example, in a <code>HashMap</code> the
	 * {@link java.util.HashMap#get(java.lang.Object)} method returns
	 * <code>null</code> if the <code>Map</code> contains
	 * <code>null</code> or if there is no matching key. The
	 * <code>Null</code> placeholder can be used to distinguish between
	 * these two cases.</p>
	 * <p/>
	 * <p>Another example is <code>Hashtable</code>, where <code>null</code>
	 * cannot be stored.</p>
	 * <p/>
	 * <p>This instance is Serializable.</p>
	 */
	public static final Null NULL = new Null();

	/**
	 * <p><code>ObjectUtility</code> instances should NOT be constructed in
	 * standard programming. Instead, the class should be used as
	 * <code>ObjectUtility.defaultIfNull("a","b");</code>.</p>
	 * <p/>
	 * <p>This constructor is public to permit tools that require a JavaBean instance
	 * to operate.</p>
	 */
	public ObjectUtility()
	{
		//nothing to do here
	}

	/**
	 * <p>Returns a default value if the object passed is
	 * <code>null</code>.</p>
	 * <p/>
	 * <pre>
	 * ObjectUtility.defaultIfNull(null, null)      = null
	 * ObjectUtility.defaultIfNull(null, "")        = ""
	 * ObjectUtility.defaultIfNull(null, "zz")      = "zz"
	 * ObjectUtility.defaultIfNull("abc", *)        = "abc"
	 * ObjectUtility.defaultIfNull(Boolean.TRUE, *) = Boolean.TRUE
	 * </pre>
	 *
	 * @param object       the <code>Object</code> to test, may be <code>null</code>
	 * @param defaultValue the default value to return, may be <code>null</code>
	 * @return <code>object</code> if it is not <code>null</code>, defaultValue otherwise
	 */
	public static Object defaultIfNull(Object object, Object defaultValue)
	{
		return object != null ? object : defaultValue;
	}

	/**
	 * <p>Compares two objects for equality, where either one or both
	 * objects may be <code>null</code>.</p>
	 * <p/>
	 * <pre>
	 * ObjectUtility.equals(null, null)                  = true
	 * ObjectUtility.equals(null, "")                    = false
	 * ObjectUtility.equals("", null)                    = false
	 * ObjectUtility.equals("", "")                      = true
	 * ObjectUtility.equals(Boolean.TRUE, null)          = false
	 * ObjectUtility.equals(Boolean.TRUE, "true")        = false
	 * ObjectUtility.equals(Boolean.TRUE, Boolean.TRUE)  = true
	 * ObjectUtility.equals(Boolean.TRUE, Boolean.FALSE) = false
	 * </pre>
	 *
	 * @param object1 the first object, may be <code>null</code>
	 * @param object2 the second object, may be <code>null</code>
	 * @return <code>true</code> if the values of both objects are the same
	 */
	public static boolean equals(Object object1, Object object2)
	{
		if (object1 == object2) return true;

		if ((object1 == null) || (object2 == null)) return false;

		return object1.equals(object2);
	}

	/**
	 * <p>Gets the hash code of an object returning zero when the
	 * object is <code>null</code>.</p>
	 * <p/>
	 * <pre>
	 * ObjectUtility.hashCode(null)   = 0
	 * ObjectUtility.hashCode(obj)    = obj.hashCode()
	 * </pre>
	 *
	 * @param obj the object to obtain the hash code of, may be <code>null</code>
	 * @return the hash code of the object, or zero if null
	 */
	public static int hashCode(Object obj)
	{
		return (obj == null) ? 0 : obj.hashCode();
	}

	/**
	 * <p>Gets the toString that would be produced by <code>Object</code>
	 * if a class did not override toString itself. <code>null</code>
	 * will return <code>null</code>.</p>
	 * <p/>
	 * <pre>
	 * ObjectUtility.identityToString(null)         = null
	 * ObjectUtility.identityToString("")           = "java.lang.String@1e23"
	 * ObjectUtility.identityToString(Boolean.TRUE) = "java.lang.Boolean@7fa"
	 * </pre>
	 *
	 * @param object the object to create a toString for, may be
	 *               <code>null</code>
	 * @return the default toString text, or <code>null</code> if
	 *         <code>null</code> passed in
	 */
	public static String identityToString(Object object)
	{
		if (object == null) return null;

		return appendIdentityToString(null, object).toString();
	}

	/**
	 * <p>Appends the toString that would be produced by <code>Object</code>
	 * if a class did not override toString itself. <code>null</code>
	 * will return <code>null</code>.</p>
	 * <p/>
	 * <pre>
	 * ObjectUtility.appendIdentityToString(*, null)            = null
	 * ObjectUtility.appendIdentityToString(null, "")           = "java.lang.String@1e23"
	 * ObjectUtility.appendIdentityToString(null, Boolean.TRUE) = "java.lang.Boolean@7fa"
	 * ObjectUtility.appendIdentityToString(buf, Boolean.TRUE)  = buf.append("java.lang.Boolean@7fa")
	 * </pre>
	 *
	 * @param buffer the buffer to append to, may be <code>null</code>
	 * @param object the object to create a toString for, may be <code>null</code>
	 * @return the default toString text, or <code>null</code> if
	 *         <code>null</code> passed in
	 */
	public static StringBuffer appendIdentityToString(StringBuffer buffer, Object object)
	{
		if (object == null) return null;

		if (buffer == null) buffer = new StringBuffer();

		return buffer.append(object.getClass().getName()).append('@').append(Integer.toHexString(System.identityHashCode(object)));
	}

    /**
     * Returns a default value if the object passed is null.
     *
     * @param o    The object to test.
     * @param dflt The default value to return.
     * @return The object o if it is not null, dflt otherwise.
     */
    public static Object isNull(Object o, Object dflt)
    {
        if(o == null) return dflt;
        	else return o;
    }

    /**
     * Adds an object to a vector, making sure the object is in the
     * vector only once.
     *
     * @param v The vector.
     * @param o The object.
     */
    public static void addOnce(Vector v, Object o)
    {
        if(!v.contains(o)) v.addElement(o);
    }

    /**
     * Deserializes a single object from an array of bytes.
     *
     * @param objectData The serialized object.
     * @return The deserialized object, or <code>null</code> on failure.
     */
    public static Object deserialize(byte[] objectData)
    {
        Object object = null;
        if(objectData != null)
        {
            // These streams are closed in finally.
            ObjectInputStream in = null;
            ByteArrayInputStream bin = new ByteArrayInputStream(objectData);
            BufferedInputStream bufin = new BufferedInputStream(bin);
            try
            {
                in = new ObjectInputStream(bufin);

                // If objectData has not been initialized, an exception will occur.
                object = in.readObject();
            }
            catch(Exception e)
            {
            }
            finally
            {
                try
                {
                    if(in != null) in.close();

                    if(bufin != null) bufin.close();

                    if(bin != null) bin.close();
                }
                catch(IOException e)
                {
                }
            }
        }
        return object;
    }

    /**
     * Compares two Objects, returns true if their values are the
     * same.  It checks for null values prior to an o1.equals(o2)
     * check
     *
     * @param o1 The first object.
     * @param o2 The second object.
     * @return True if the values of both xstrings are the same.
     */
    public static boolean equalsIncludeNull(Object o1, Object o2)
    {
        if(o1 == null) return (o2 == null);
        else
        {
            if(o2 == null) return false;
            	else return o1.equals(o2);
        }
    }

    /**
     * Nice method for adding data to a Hashtable in such a way
     * as to not get NPE's. The point being that if the
     * value is null, Hashtable.put() will throw an exception.
     * That blows in the case of this class cause you may want to
     * essentially treat put("Not Null", null ) == put("Not Null", "")
     * We will still throw a NPE if the key is null cause that should
     * never happen.
     */
    public static final void safeAddToHashtable(Hashtable hash, Object key, Object value) throws NullPointerException
    {
        if(value == null) hash.put(key, "");
        	else hash.put(key, value);
    }

	/**
	 * Sort ascending a list structure.
	 * The elements of list structure must be strings or other structure derivated by string
	 */
	public static final List sort(List list)
	{
		for(int i = 0; list != null && i < list.size(); i++)
		{
			String actual = (String) list.get(i);
			for(int j = i; j < list.size(); j++)
			{
				String next = (String)list.get(j);
				if(actual.compareTo(next) > 0)
				{
					list.set(i, next);
					list.set(j, actual);

					actual = next;
				}
			}
		}

		return list;
	}

	/**
	 * <p>Gets the <code>toString</code> of an <code>Object</code> returning
	 * an empty string ("") if <code>null</code> input.</p>
	 * <p/>
	 * <pre>
	 * ObjectUtility.toString(null)         = ""
	 * ObjectUtility.toString("")           = ""
	 * ObjectUtility.toString("bat")        = "bat"
	 * ObjectUtility.toString(Boolean.TRUE) = "true"
	 * </pre>
	 *
	 * @param obj the Object to <code>toString</code>, may be null
	 * @return the passed in Object's toString, or nullStr if <code>null</code> input
	 * @see StringUtility#defaultString(String)
	 * @see String#valueOf(Object)
	 */
	public static String toString(Object obj)
	{
		return obj == null ? "" : obj.toString();
	}

	/**
	 * <p>Gets the <code>toString</code> of an <code>Object</code> returning
	 * a specified text if <code>null</code> input.</p>
	 * <p/>
	 * <pre>
	 * ObjectUtility.toString(null, null)           = null
	 * ObjectUtility.toString(null, "null")         = "null"
	 * ObjectUtility.toString("", "null")           = ""
	 * ObjectUtility.toString("bat", "null")        = "bat"
	 * ObjectUtility.toString(Boolean.TRUE, "null") = "true"
	 * </pre>
	 *
	 * @param obj     the Object to <code>toString</code>, may be null
	 * @param nullStr the String to return if <code>null</code> input, may be null
	 * @return the passed in Object's toString, or nullStr if <code>null</code> input
	 * @see StringUtility#defaultString(String,String)
	 * @see String#valueOf(Object)
	 */
	public static String toString(Object obj, String nullStr)
	{
		return obj == null ? nullStr : obj.toString();
	}

	/**
	 * <p>Class used as a null placeholder where <code>null</code>
	 * has another meaning.</p>
	 * <p/>
	 * <p>For example, in a <code>HashMap</code> the
	 * {@link java.util.HashMap#get(java.lang.Object)} method returns
	 * <code>null</code> if the <code>Map</code> contains
	 * <code>null</code> or if there is no matching key. The
	 * <code>Null</code> placeholder can be used to distinguish between
	 * these two cases.</p>
	 * <p/>
	 * <p>Another example is <code>Hashtable</code>, where <code>null</code>
	 * cannot be stored.</p>
	 */
	public static class Null implements Serializable
	{

		/**
		 * Restricted constructor - singleton.
		 */
		Null()
		{
			//null constructor.
		}

		/**
		 * <p>Ensure singleton.</p>
		 *
		 * @return the singleton value
		 */
		private Object readResolve()
		{
			return ObjectUtility.NULL;
		}
	}

}
