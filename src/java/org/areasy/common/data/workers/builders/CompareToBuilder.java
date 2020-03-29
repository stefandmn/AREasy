package org.areasy.common.data.workers.builders;

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

import org.areasy.common.data.NumberUtility;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Comparator;

/**
 * Assists in implementing {@link java.lang.Comparable#compareTo(Object)} methods.
 * <p/>
 * It is consistent with <code>equals(Object)</code> and
 * <code>hashcode()</code> built with {@link EqualsBuilder} and
 * {@link HashCodeBuilder}.</p>
 * <p/>
 * <p>Two Objects that compare equal using <code>equals(Object)</code> should normally
 * also compare equal using <code>compareTo(Object)</code>.</p>
 * <p/>
 * <p>All relevant fields should be included in the calculation of the
 * comparison. Derived fields may be ignored. The same fields, in the same
 * order, should be used in both <code>compareTo(Object)</code> and
 * <code>equals(Object)</code>.</p>
 * <p/>
 * <p>To use this class write code as follows:</p>
 * <p/>
 * <pre>
 * public class MyClass {
 *   String field1;
 *   int field2;
 *   boolean field3;
 * <p/>
 *   ...
 * <p/>
 *   public int compareTo(Object o) {
 *     MyClass myClass = (MyClass) o;
 *     return new CompareToBuilder()
 *       .appendSuper(super.compareTo(o)
 *       .append(this.field1, myClass.field1)
 *       .append(this.field2, myClass.field2)
 *       .append(this.field3, myClass.field3)
 *       .toComparison();
 *   }
 * }
 * </pre>
 * <p/>
 * <p>Alternatively, there is are {@link #reflectionCompare reflectionCompare} method that uses
 * reflection to determine the fields to append. Because fields can be private,
 * <code>reflectionCompare</code> uses {@link java.lang.reflect.AccessibleObject#setAccessible(boolean)} to
 * bypass normal access control checks. This will fail under a security manager,
 * unless the appropriate permissions are set up correctly. It is also
 * slower than appending explicitly.</p>
 * <p/>
 * <p>A typical implementation of <code>compareTo(Object)</code> using
 * <code>reflectionCompare</code> looks like:</p>
 * <p/>
 * <pre>
 * public int compareTo(Object o) {
 *   return CompareToBuilder.reflectionCompare(this, o);
 * }
 * </pre>
 *
 * @version $Id: CompareToBuilder.java,v 1.2 2008/05/14 09:32:34 swd\stefan.damian Exp $
 * @see java.lang.Comparable
 * @see java.lang.Object#equals(Object)
 * @see java.lang.Object#hashCode()
 * @see EqualsBuilder
 * @see HashCodeBuilder
 */
public class CompareToBuilder
{

	/**
	 * Current state of the comparison as appended fields are checked.
	 */
	private int comparison;

	/**
	 * <p>Constructor for CompareToBuilder.</p>
	 * <p/>
	 * <p>Starts off assuming that the objects are equal. Multiple calls are
	 * then made to the various append methods, followed by a call to
	 * {@link #toComparison} to get the result.</p>
	 */
	public CompareToBuilder()
	{
		super();
		comparison = 0;
	}

	/**
	 * <p>Compares two <code>Object</code>s via reflection.</p>
	 * <p/>
	 * <p>Fields can be private, thus <code>AccessibleObject.setAccessible</code>
	 * is used to bypass normal access control checks. This will fail under a
	 * security manager unless the appropriate permissions are set.</p>
	 * <p/>
	 * <ul>
	 * <li>Static fields will not be compared</li>
	 * <li>Transient members will be not be compared, as they are likely derived
	 * fields</li>
	 * <li>Superclass fields will be compared</li>
	 * </ul>
	 * <p/>
	 * <p>If both <code>lhs</code> and <code>rhs</code> are <code>null</code>,
	 * they are considered equal.</p>
	 *
	 * @param lhs left-hand object
	 * @param rhs right-hand object
	 * @return a negative integer, zero, or a positive integer as <code>lhs</code>
	 *         is less than, equal to, or greater than <code>rhs</code>
	 * @throws NullPointerException if either (but not both) parameters are
	 *                              <code>null</code>
	 * @throws ClassCastException   if <code>rhs</code> is not assignment-compatible
	 *                              with <code>lhs</code>
	 */
	public static int reflectionCompare(Object lhs, Object rhs)
	{
		return reflectionCompare(lhs, rhs, false, null);
	}

	/**
	 * <p>Compares two <code>Object</code>s via reflection.</p>
	 * <p/>
	 * <p>Fields can be private, thus <code>AccessibleObject.setAccessible</code>
	 * is used to bypass normal access control checks. This will fail under a
	 * security manager unless the appropriate permissions are set.</p>
	 * <p/>
	 * <ul>
	 * <li>Static fields will not be compared</li>
	 * <li>If <code>compareTransients</code> is <code>true</code>,
	 * compares transient members.  Otherwise ignores them, as they
	 * are likely derived fields.</li>
	 * <li>Superclass fields will be compared</li>
	 * </ul>
	 * <p/>
	 * <p>If both <code>lhs</code> and <code>rhs</code> are <code>null</code>,
	 * they are considered equal.</p>
	 *
	 * @param lhs               left-hand object
	 * @param rhs               right-hand object
	 * @param compareTransients whether to compare transient fields
	 * @return a negative integer, zero, or a positive integer as <code>lhs</code>
	 *         is less than, equal to, or greater than <code>rhs</code>
	 * @throws NullPointerException if either <code>lhs</code> or <code>rhs</code>
	 *                              (but not both) is <code>null</code>
	 * @throws ClassCastException   if <code>rhs</code> is not assignment-compatible
	 *                              with <code>lhs</code>
	 */
	public static int reflectionCompare(Object lhs, Object rhs, boolean compareTransients)
	{
		return reflectionCompare(lhs, rhs, compareTransients, null);
	}

	/**
	 * <p>Compares two <code>Object</code>s via reflection.</p>
	 * <p/>
	 * <p>Fields can be private, thus <code>AccessibleObject.setAccessible</code>
	 * is used to bypass normal access control checks. This will fail under a
	 * security manager unless the appropriate permissions are set.</p>
	 * <p/>
	 * <ul>
	 * <li>Static fields will not be compared</li>
	 * <li>If the <code>compareTransients</code> is <code>true</code>,
	 * compares transient members.  Otherwise ignores them, as they
	 * are likely derived fields.</li>
	 * <li>Compares superclass fields up to and including <code>reflectUpToClass</code>.
	 * If <code>reflectUpToClass</code> is <code>null</code>, compares all superclass fields.</li>
	 * </ul>
	 * <p/>
	 * <p>If both <code>lhs</code> and <code>rhs</code> are <code>null</code>,
	 * they are considered equal.</p>
	 *
	 * @param lhs               left-hand object
	 * @param rhs               right-hand object
	 * @param compareTransients whether to compare transient fields
	 * @param reflectUpToClass  last superclass for which fields are compared
	 * @return a negative integer, zero, or a positive integer as <code>lhs</code>
	 *         is less than, equal to, or greater than <code>rhs</code>
	 * @throws NullPointerException if either <code>lhs</code> or <code>rhs</code>
	 *                              (but not both) is <code>null</code>
	 * @throws ClassCastException   if <code>rhs</code> is not assignment-compatible
	 *                              with <code>lhs</code>
	 */
	public static int reflectionCompare(Object lhs, Object rhs, boolean compareTransients, Class reflectUpToClass)
	{
		if (lhs == rhs)
		{
			return 0;
		}
		if (lhs == null || rhs == null)
		{
			throw new NullPointerException();
		}
		Class lhsClazz = lhs.getClass();
		if (!lhsClazz.isInstance(rhs))
		{
			throw new ClassCastException();
		}
		CompareToBuilder compareToBuilder = new CompareToBuilder();
		reflectionAppend(lhs, rhs, lhsClazz, compareToBuilder, compareTransients);
		while (lhsClazz.getSuperclass() != null && lhsClazz != reflectUpToClass)
		{
			lhsClazz = lhsClazz.getSuperclass();
			reflectionAppend(lhs, rhs, lhsClazz, compareToBuilder, compareTransients);
		}
		return compareToBuilder.toComparison();
	}

	/**
	 * <p>Appends to <code>builder</code> the comparison of <code>lhs</code>
	 * to <code>rhs</code> using the fields defined in <code>clazz</code>.</p>
	 *
	 * @param lhs           left-hand object
	 * @param rhs           right-hand object
	 * @param clazz         <code>Class</code> that defines fields to be compared
	 * @param builder       <code>CompareToBuilder</code> to append to
	 * @param useTransients whether to compare transient fields
	 */
	private static void reflectionAppend(Object lhs,
										 Object rhs,
										 Class clazz,
										 CompareToBuilder builder,
										 boolean useTransients)
	{

		Field[] fields = clazz.getDeclaredFields();
		AccessibleObject.setAccessible(fields, true);
		for (int i = 0; i < fields.length && builder.comparison == 0; i++)
		{
			Field f = fields[i];
			if ((f.getName().indexOf('$') == -1)
					&& (useTransients || !Modifier.isTransient(f.getModifiers()))
					&& (!Modifier.isStatic(f.getModifiers())))
			{
				try
				{
					builder.append(f.get(lhs), f.get(rhs));
				}
				catch (IllegalAccessException e)
				{
					// This can't happen. Would get a Security exception instead.
					// Throw a runtime exception in case the impossible happens.
					throw new InternalError("Unexpected IllegalAccessException");
				}
			}
		}
	}

	/**
	 * <p>Appends to the <code>builder</code> the <code>compareTo(Object)</code>
	 * result of the superclass.</p>
	 *
	 * @param superCompareTo result of calling <code>super.compareTo(Object)</code>
	 * @return this - used to chain append calls
	 */
	public CompareToBuilder appendSuper(int superCompareTo)
	{
		if (comparison != 0)
		{
			return this;
		}
		comparison = superCompareTo;
		return this;
	}

	/**
	 * <p>Appends to the <code>builder</code> the comparison of
	 * two <code>Object</code>s.</p>
	 * <p/>
	 * <ol>
	 * <li>Check if <code>lhs == rhs</code></li>
	 * <li>Check if either <code>lhs</code> or <code>rhs</code> is <code>null</code>,
	 * a <code>null</code> object is less than a non-<code>null</code> object</li>
	 * <li>Check the object contents</li>
	 * </ol>
	 * <p/>
	 * <p><code>lhs</code> must either be an array or implement {@link Comparable}.</p>
	 *
	 * @param lhs left-hand object
	 * @param rhs right-hand object
	 * @return this - used to chain append calls
	 * @throws ClassCastException if <code>rhs</code> is not assignment-compatible
	 *                            with <code>lhs</code>
	 */
	public CompareToBuilder append(Object lhs, Object rhs)
	{
		return append(lhs, rhs, null);
	}

	/**
	 * <p>Appends to the <code>builder</code> the comparison of
	 * two <code>Object</code>s.</p>
	 * <p/>
	 * <ol>
	 * <li>Check if <code>lhs == rhs</code></li>
	 * <li>Check if either <code>lhs</code> or <code>rhs</code> is <code>null</code>,
	 * a <code>null</code> object is less than a non-<code>null</code> object</li>
	 * <li>Check the object contents</li>
	 * </ol>
	 * <p/>
	 * <p>If <code>lhs</code> is an array, array comparison methods will be used.
	 * Otherwise <code>comparator</code> will be used to compare the objects.
	 * If <code>comparator</code> is <code>null</code>, <code>lhs</code> must
	 * implement {@link Comparable} instead.</p>
	 *
	 * @param lhs        left-hand object
	 * @param rhs        right-hand object
	 * @param comparator <code>Comparator</code> used to compare the objects,
	 *                   <code>null</code> means treat lhs as <code>Comparable</code>
	 * @return this - used to chain append calls
	 * @throws ClassCastException if <code>rhs</code> is not assignment-compatible
	 *                            with <code>lhs</code>
	 */
	public CompareToBuilder append(Object lhs, Object rhs, Comparator comparator)
	{
		if (comparison != 0)
		{
			return this;
		}
		if (lhs == rhs)
		{
			return this;
		}
		if (lhs == null)
		{
			comparison = -1;
			return this;
		}
		if (rhs == null)
		{
			comparison = +1;
			return this;
		}
		if (lhs.getClass().isArray())
		{
			// switch on type of array, to dispatch to the correct handler
			// handles multi dimensional arrays
			// throws a ClassCastException if rhs is not the correct array type
			if (lhs instanceof long[])
			{
				append((long[]) lhs, (long[]) rhs);
			}
			else if (lhs instanceof int[])
			{
				append((int[]) lhs, (int[]) rhs);
			}
			else if (lhs instanceof short[])
			{
				append((short[]) lhs, (short[]) rhs);
			}
			else if (lhs instanceof char[])
			{
				append((char[]) lhs, (char[]) rhs);
			}
			else if (lhs instanceof byte[])
			{
				append((byte[]) lhs, (byte[]) rhs);
			}
			else if (lhs instanceof double[])
			{
				append((double[]) lhs, (double[]) rhs);
			}
			else if (lhs instanceof float[])
			{
				append((float[]) lhs, (float[]) rhs);
			}
			else if (lhs instanceof boolean[])
			{
				append((boolean[]) lhs, (boolean[]) rhs);
			}
			else
			{
				// not an array of primitives
				// throws a ClassCastException if rhs is not an array
				append((Object[]) lhs, (Object[]) rhs, comparator);
			}
		}
		else
		{
			// the simple case, not an array, just test the element
			if (comparator == null)
			{
				comparison = ((Comparable) lhs).compareTo(rhs);
			}
			else
			{
				comparison = comparator.compare(lhs, rhs);
			}
		}
		return this;
	}

	/**
	 * Appends to the <code>builder</code> the comparison of
	 * two <code>long</code>s.
	 *
	 * @param lhs left-hand value
	 * @param rhs right-hand value
	 * @return this - used to chain append calls
	 */
	public CompareToBuilder append(long lhs, long rhs)
	{
		if (comparison != 0)
		{
			return this;
		}
		comparison = ((lhs < rhs) ? -1 : ((lhs > rhs) ? 1 : 0));
		return this;
	}

	/**
	 * Appends to the <code>builder</code> the comparison of
	 * two <code>int</code>s.
	 *
	 * @param lhs left-hand value
	 * @param rhs right-hand value
	 * @return this - used to chain append calls
	 */
	public CompareToBuilder append(int lhs, int rhs)
	{
		if (comparison != 0)
		{
			return this;
		}
		comparison = ((lhs < rhs) ? -1 : ((lhs > rhs) ? 1 : 0));
		return this;
	}

	/**
	 * Appends to the <code>builder</code> the comparison of
	 * two <code>short</code>s.
	 *
	 * @param lhs left-hand value
	 * @param rhs right-hand value
	 * @return this - used to chain append calls
	 */
	public CompareToBuilder append(short lhs, short rhs)
	{
		if (comparison != 0)
		{
			return this;
		}
		comparison = ((lhs < rhs) ? -1 : ((lhs > rhs) ? 1 : 0));
		return this;
	}

	/**
	 * Appends to the <code>builder</code> the comparison of
	 * two <code>char</code>s.
	 *
	 * @param lhs left-hand value
	 * @param rhs right-hand value
	 * @return this - used to chain append calls
	 */
	public CompareToBuilder append(char lhs, char rhs)
	{
		if (comparison != 0)
		{
			return this;
		}
		comparison = ((lhs < rhs) ? -1 : ((lhs > rhs) ? 1 : 0));
		return this;
	}

	/**
	 * Appends to the <code>builder</code> the comparison of
	 * two <code>byte</code>s.
	 *
	 * @param lhs left-hand value
	 * @param rhs right-hand value
	 * @return this - used to chain append calls
	 */
	public CompareToBuilder append(byte lhs, byte rhs)
	{
		if (comparison != 0)
		{
			return this;
		}
		comparison = ((lhs < rhs) ? -1 : ((lhs > rhs) ? 1 : 0));
		return this;
	}

	/**
	 * <p>Appends to the <code>builder</code> the comparison of
	 * two <code>double</code>s.</p>
	 * <p/>
	 * <p>This handles NaNs, Infinities, and <code>-0.0</code>.</p>
	 * <p/>
	 * <p>It is compatible with the hash code generated by
	 * <code>HashCodeBuilder</code>.</p>
	 *
	 * @param lhs left-hand value
	 * @param rhs right-hand value
	 * @return this - used to chain append calls
	 */
	public CompareToBuilder append(double lhs, double rhs)
	{
		if (comparison != 0)
		{
			return this;
		}
		comparison = NumberUtility.compare(lhs, rhs);
		return this;
	}

	/**
	 * <p>Appends to the <code>builder</code> the comparison of
	 * two <code>float</code>s.</p>
	 * <p/>
	 * <p>This handles NaNs, Infinities, and <code>-0.0</code>.</p>
	 * <p/>
	 * <p>It is compatible with the hash code generated by
	 * <code>HashCodeBuilder</code>.</p>
	 *
	 * @param lhs left-hand value
	 * @param rhs right-hand value
	 * @return this - used to chain append calls
	 */
	public CompareToBuilder append(float lhs, float rhs)
	{
		if (comparison != 0)
		{
			return this;
		}
		comparison = NumberUtility.compare(lhs, rhs);
		return this;
	}

	/**
	 * Appends to the <code>builder</code> the comparison of
	 * two <code>booleans</code>s.
	 *
	 * @param lhs left-hand value
	 * @param rhs right-hand value
	 * @return this - used to chain append calls
	 */
	public CompareToBuilder append(boolean lhs, boolean rhs)
	{
		if (comparison != 0)
		{
			return this;
		}
		if (lhs == rhs)
		{
			return this;
		}
		if (lhs == false)
		{
			comparison = -1;
		}
		else
		{
			comparison = +1;
		}
		return this;
	}

	/**
	 * <p>Appends to the <code>builder</code> the deep comparison of
	 * two <code>Object</code> arrays.</p>
	 * <p/>
	 * <ol>
	 * <li>Check if arrays are the same using <code>==</code></li>
	 * <li>Check if for <code>null</code>, <code>null</code> is less than non-<code>null</code></li>
	 * <li>Check array length, a short length array is less than a long length array</li>
	 * <li>Check array contents element by element using {@link #append(Object, Object, Comparator)}</li>
	 * </ol>
	 * <p/>
	 * <p>This method will also will be called for the top level of multi-dimensional,
	 * ragged, and multi-typed arrays.</p>
	 *
	 * @param lhs left-hand array
	 * @param rhs right-hand array
	 * @return this - used to chain append calls
	 * @throws ClassCastException if <code>rhs</code> is not assignment-compatible
	 *                            with <code>lhs</code>
	 */
	public CompareToBuilder append(Object[] lhs, Object[] rhs)
	{
		return append(lhs, rhs, null);
	}

	/**
	 * <p>Appends to the <code>builder</code> the deep comparison of
	 * two <code>Object</code> arrays.</p>
	 * <p/>
	 * <ol>
	 * <li>Check if arrays are the same using <code>==</code></li>
	 * <li>Check if for <code>null</code>, <code>null</code> is less than non-<code>null</code></li>
	 * <li>Check array length, a short length array is less than a long length array</li>
	 * <li>Check array contents element by element using {@link #append(Object, Object, Comparator)}</li>
	 * </ol>
	 * <p/>
	 * <p>This method will also will be called for the top level of multi-dimensional,
	 * ragged, and multi-typed arrays.</p>
	 *
	 * @param lhs        left-hand array
	 * @param rhs        right-hand array
	 * @param comparator <code>Comparator</code> to use to compare the array elements,
	 *                   <code>null</code> means to treat <code>lhs</code> elements as <code>Comparable</code>.
	 * @return this - used to chain append calls
	 * @throws ClassCastException if <code>rhs</code> is not assignment-compatible
	 *                            with <code>lhs</code>
	 */
	public CompareToBuilder append(Object[] lhs, Object[] rhs, Comparator comparator)
	{
		if (comparison != 0)
		{
			return this;
		}
		if (lhs == rhs)
		{
			return this;
		}
		if (lhs == null)
		{
			comparison = -1;
			return this;
		}
		if (rhs == null)
		{
			comparison = +1;
			return this;
		}
		if (lhs.length != rhs.length)
		{
			comparison = (lhs.length < rhs.length) ? -1 : +1;
			return this;
		}
		for (int i = 0; i < lhs.length && comparison == 0; i++)
		{
			append(lhs[i], rhs[i], comparator);
		}
		return this;
	}

	/**
	 * <p>Appends to the <code>builder</code> the deep comparison of
	 * two <code>long</code> arrays.</p>
	 * <p/>
	 * <ol>
	 * <li>Check if arrays are the same using <code>==</code></li>
	 * <li>Check if for <code>null</code>, <code>null</code> is less than non-<code>null</code></li>
	 * <li>Check array length, a shorter length array is less than a longer length array</li>
	 * <li>Check array contents element by element using {@link #append(long, long)}</li>
	 * </ol>
	 *
	 * @param lhs left-hand array
	 * @param rhs right-hand array
	 * @return this - used to chain append calls
	 */
	public CompareToBuilder append(long[] lhs, long[] rhs)
	{
		if (comparison != 0)
		{
			return this;
		}
		if (lhs == rhs)
		{
			return this;
		}
		if (lhs == null)
		{
			comparison = -1;
			return this;
		}
		if (rhs == null)
		{
			comparison = +1;
			return this;
		}
		if (lhs.length != rhs.length)
		{
			comparison = (lhs.length < rhs.length) ? -1 : +1;
			return this;
		}
		for (int i = 0; i < lhs.length && comparison == 0; i++)
		{
			append(lhs[i], rhs[i]);
		}
		return this;
	}

	/**
	 * <p>Appends to the <code>builder</code> the deep comparison of
	 * two <code>int</code> arrays.</p>
	 * <p/>
	 * <ol>
	 * <li>Check if arrays are the same using <code>==</code></li>
	 * <li>Check if for <code>null</code>, <code>null</code> is less than non-<code>null</code></li>
	 * <li>Check array length, a shorter length array is less than a longer length array</li>
	 * <li>Check array contents element by element using {@link #append(int, int)}</li>
	 * </ol>
	 *
	 * @param lhs left-hand array
	 * @param rhs right-hand array
	 * @return this - used to chain append calls
	 */
	public CompareToBuilder append(int[] lhs, int[] rhs)
	{
		if (comparison != 0)
		{
			return this;
		}
		if (lhs == rhs)
		{
			return this;
		}
		if (lhs == null)
		{
			comparison = -1;
			return this;
		}
		if (rhs == null)
		{
			comparison = +1;
			return this;
		}
		if (lhs.length != rhs.length)
		{
			comparison = (lhs.length < rhs.length) ? -1 : +1;
			return this;
		}
		for (int i = 0; i < lhs.length && comparison == 0; i++)
		{
			append(lhs[i], rhs[i]);
		}
		return this;
	}

	/**
	 * <p>Appends to the <code>builder</code> the deep comparison of
	 * two <code>short</code> arrays.</p>
	 * <p/>
	 * <ol>
	 * <li>Check if arrays are the same using <code>==</code></li>
	 * <li>Check if for <code>null</code>, <code>null</code> is less than non-<code>null</code></li>
	 * <li>Check array length, a shorter length array is less than a longer length array</li>
	 * <li>Check array contents element by element using {@link #append(short, short)}</li>
	 * </ol>
	 *
	 * @param lhs left-hand array
	 * @param rhs right-hand array
	 * @return this - used to chain append calls
	 */
	public CompareToBuilder append(short[] lhs, short[] rhs)
	{
		if (comparison != 0)
		{
			return this;
		}
		if (lhs == rhs)
		{
			return this;
		}
		if (lhs == null)
		{
			comparison = -1;
			return this;
		}
		if (rhs == null)
		{
			comparison = +1;
			return this;
		}
		if (lhs.length != rhs.length)
		{
			comparison = (lhs.length < rhs.length) ? -1 : +1;
			return this;
		}
		for (int i = 0; i < lhs.length && comparison == 0; i++)
		{
			append(lhs[i], rhs[i]);
		}
		return this;
	}

	/**
	 * <p>Appends to the <code>builder</code> the deep comparison of
	 * two <code>char</code> arrays.</p>
	 * <p/>
	 * <ol>
	 * <li>Check if arrays are the same using <code>==</code></li>
	 * <li>Check if for <code>null</code>, <code>null</code> is less than non-<code>null</code></li>
	 * <li>Check array length, a shorter length array is less than a longer length array</li>
	 * <li>Check array contents element by element using {@link #append(char, char)}</li>
	 * </ol>
	 *
	 * @param lhs left-hand array
	 * @param rhs right-hand array
	 * @return this - used to chain append calls
	 */
	public CompareToBuilder append(char[] lhs, char[] rhs)
	{
		if (comparison != 0)
		{
			return this;
		}
		if (lhs == rhs)
		{
			return this;
		}
		if (lhs == null)
		{
			comparison = -1;
			return this;
		}
		if (rhs == null)
		{
			comparison = +1;
			return this;
		}
		if (lhs.length != rhs.length)
		{
			comparison = (lhs.length < rhs.length) ? -1 : +1;
			return this;
		}
		for (int i = 0; i < lhs.length && comparison == 0; i++)
		{
			append(lhs[i], rhs[i]);
		}
		return this;
	}

	/**
	 * <p>Appends to the <code>builder</code> the deep comparison of
	 * two <code>byte</code> arrays.</p>
	 * <p/>
	 * <ol>
	 * <li>Check if arrays are the same using <code>==</code></li>
	 * <li>Check if for <code>null</code>, <code>null</code> is less than non-<code>null</code></li>
	 * <li>Check array length, a shorter length array is less than a longer length array</li>
	 * <li>Check array contents element by element using {@link #append(byte, byte)}</li>
	 * </ol>
	 *
	 * @param lhs left-hand array
	 * @param rhs right-hand array
	 * @return this - used to chain append calls
	 */
	public CompareToBuilder append(byte[] lhs, byte[] rhs)
	{
		if (comparison != 0)
		{
			return this;
		}
		if (lhs == rhs)
		{
			return this;
		}
		if (lhs == null)
		{
			comparison = -1;
			return this;
		}
		if (rhs == null)
		{
			comparison = +1;
			return this;
		}
		if (lhs.length != rhs.length)
		{
			comparison = (lhs.length < rhs.length) ? -1 : +1;
			return this;
		}
		for (int i = 0; i < lhs.length && comparison == 0; i++)
		{
			append(lhs[i], rhs[i]);
		}
		return this;
	}

	/**
	 * <p>Appends to the <code>builder</code> the deep comparison of
	 * two <code>double</code> arrays.</p>
	 * <p/>
	 * <ol>
	 * <li>Check if arrays are the same using <code>==</code></li>
	 * <li>Check if for <code>null</code>, <code>null</code> is less than non-<code>null</code></li>
	 * <li>Check array length, a shorter length array is less than a longer length array</li>
	 * <li>Check array contents element by element using {@link #append(double, double)}</li>
	 * </ol>
	 *
	 * @param lhs left-hand array
	 * @param rhs right-hand array
	 * @return this - used to chain append calls
	 */
	public CompareToBuilder append(double[] lhs, double[] rhs)
	{
		if (comparison != 0)
		{
			return this;
		}
		if (lhs == rhs)
		{
			return this;
		}
		if (lhs == null)
		{
			comparison = -1;
			return this;
		}
		if (rhs == null)
		{
			comparison = +1;
			return this;
		}
		if (lhs.length != rhs.length)
		{
			comparison = (lhs.length < rhs.length) ? -1 : +1;
			return this;
		}
		for (int i = 0; i < lhs.length && comparison == 0; i++)
		{
			append(lhs[i], rhs[i]);
		}
		return this;
	}

	/**
	 * <p>Appends to the <code>builder</code> the deep comparison of
	 * two <code>float</code> arrays.</p>
	 * <p/>
	 * <ol>
	 * <li>Check if arrays are the same using <code>==</code></li>
	 * <li>Check if for <code>null</code>, <code>null</code> is less than non-<code>null</code></li>
	 * <li>Check array length, a shorter length array is less than a longer length array</li>
	 * <li>Check array contents element by element using {@link #append(float, float)}</li>
	 * </ol>
	 *
	 * @param lhs left-hand array
	 * @param rhs right-hand array
	 * @return this - used to chain append calls
	 */
	public CompareToBuilder append(float[] lhs, float[] rhs)
	{
		if (comparison != 0)
		{
			return this;
		}
		if (lhs == rhs)
		{
			return this;
		}
		if (lhs == null)
		{
			comparison = -1;
			return this;
		}
		if (rhs == null)
		{
			comparison = +1;
			return this;
		}
		if (lhs.length != rhs.length)
		{
			comparison = (lhs.length < rhs.length) ? -1 : +1;
			return this;
		}
		for (int i = 0; i < lhs.length && comparison == 0; i++)
		{
			append(lhs[i], rhs[i]);
		}
		return this;
	}

	/**
	 * <p>Appends to the <code>builder</code> the deep comparison of
	 * two <code>boolean</code> arrays.</p>
	 * <p/>
	 * <ol>
	 * <li>Check if arrays are the same using <code>==</code></li>
	 * <li>Check if for <code>null</code>, <code>null</code> is less than non-<code>null</code></li>
	 * <li>Check array length, a shorter length array is less than a longer length array</li>
	 * <li>Check array contents element by element using {@link #append(boolean, boolean)}</li>
	 * </ol>
	 *
	 * @param lhs left-hand array
	 * @param rhs right-hand array
	 * @return this - used to chain append calls
	 */
	public CompareToBuilder append(boolean[] lhs, boolean[] rhs)
	{
		if (comparison != 0)
		{
			return this;
		}
		if (lhs == rhs)
		{
			return this;
		}
		if (lhs == null)
		{
			comparison = -1;
			return this;
		}
		if (rhs == null)
		{
			comparison = +1;
			return this;
		}
		if (lhs.length != rhs.length)
		{
			comparison = (lhs.length < rhs.length) ? -1 : +1;
			return this;
		}
		for (int i = 0; i < lhs.length && comparison == 0; i++)
		{
			append(lhs[i], rhs[i]);
		}
		return this;
	}

	/**
	 * Returns a negative integer, a positive integer, or zero as
	 * the <code>builder</code> has judged the "left-hand" side
	 * as less than, greater than, or equal to the "right-hand"
	 * side.
	 *
	 * @return final comparison result
	 */
	public int toComparison()
	{
		return comparison;
	}

}

