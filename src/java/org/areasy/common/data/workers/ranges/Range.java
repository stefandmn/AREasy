package org.areasy.common.data.workers.ranges;

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

/**
 * <p><code>Range</code> represents a range of numbers of the same type.</p>
 * <p/>
 * <p>Specific subclasses hold the range values as different types. Each
 * subclass should be immutable and {@link java.io.Serializable Serializable}
 * if possible.</p>
 *
 * @version $Id: Range.java,v 1.2 2008/05/14 09:32:36 swd\stefan.damian Exp $
 */
public abstract class Range
{

	/**
	 * <p>Constructs a new range.</p>
	 */
	public Range()
	{
		super();
	}

	// Accessors

	/**
	 * <p>Gets the minimum number in this range.</p>
	 *
	 * @return the minimum number in this range
	 */
	public abstract Number getMinimumNumber();

	/**
	 * <p>Gets the minimum number in this range as a <code>long</code>.</p>
	 * <p/>
	 * <p>This implementation uses the {@link #getMinimumNumber()} method.
	 * Subclasses may be able to optimise this.</p>
	 *
	 * @return the minimum number in this range
	 */
	public long getMinimumLong()
	{
		return getMinimumNumber().longValue();
	}

	/**
	 * <p>Gets the minimum number in this range as a <code>int</code>.</p>
	 * <p/>
	 * <p>This implementation uses the {@link #getMinimumNumber()} method.
	 * Subclasses may be able to optimise this.</p>
	 *
	 * @return the minimum number in this range
	 */
	public int getMinimumInteger()
	{
		return getMinimumNumber().intValue();
	}

	/**
	 * <p>Gets the minimum number in this range as a <code>double</code>.</p>
	 * <p/>
	 * <p>This implementation uses the {@link #getMinimumNumber()} method.
	 * Subclasses may be able to optimise this.</p>
	 *
	 * @return the minimum number in this range
	 */
	public double getMinimumDouble()
	{
		return getMinimumNumber().doubleValue();
	}

	/**
	 * <p>Gets the minimum number in this range as a <code>float</code>.</p>
	 * <p/>
	 * <p>This implementation uses the {@link #getMinimumNumber()} method.
	 * Subclasses may be able to optimise this.</p>
	 *
	 * @return the minimum number in this range
	 */
	public float getMinimumFloat()
	{
		return getMinimumNumber().floatValue();
	}

	/**
	 * <p>Gets the maximum number in this range.</p>
	 *
	 * @return the maximum number in this range
	 */
	public abstract Number getMaximumNumber();

	/**
	 * <p>Gets the maximum number in this range as a <code>long</code>.</p>
	 * <p/>
	 * <p>This implementation uses the {@link #getMaximumNumber()} method.
	 * Subclasses may be able to optimise this.</p>
	 *
	 * @return the maximum number in this range
	 */
	public long getMaximumLong()
	{
		return getMaximumNumber().longValue();
	}

	/**
	 * <p>Gets the maximum number in this range as a <code>int</code>.</p>
	 * <p/>
	 * <p>This implementation uses the {@link #getMaximumNumber()} method.
	 * Subclasses may be able to optimise this.</p>
	 *
	 * @return the maximum number in this range
	 */
	public int getMaximumInteger()
	{
		return getMaximumNumber().intValue();
	}

	/**
	 * <p>Gets the maximum number in this range as a <code>double</code>.</p>
	 * <p/>
	 * <p>This implementation uses the {@link #getMaximumNumber()} method.
	 * Subclasses may be able to optimise this.</p>
	 *
	 * @return the maximum number in this range
	 */
	public double getMaximumDouble()
	{
		return getMaximumNumber().doubleValue();
	}

	/**
	 * <p>Gets the maximum number in this range as a <code>float</code>.</p>
	 * <p/>
	 * <p>This implementation uses the {@link #getMaximumNumber()} method.
	 * Subclasses may be able to optimise this.</p>
	 *
	 * @return the maximum number in this range
	 */
	public float getMaximumFloat()
	{
		return getMaximumNumber().floatValue();
	}

	// Include tests

	/**
	 * <p>Tests whether the specified <code>Number</code> occurs within
	 * this range.</p>
	 * <p/>
	 * <p>The exact comparison implementation varies by subclass. It is
	 * intended that an <code>int</code> specific subclass will compare using
	 * <code>int</code> comparison.</p>
	 * <p/>
	 * <p><code>null</code> is handled and returns <code>false</code>.</p>
	 *
	 * @param number the number to test, may be <code>null</code>
	 * @return <code>true</code> if the specified number occurs within this range
	 * @throws IllegalArgumentException if the <code>Number</code> cannot be compared
	 */
	public abstract boolean containsNumber(Number number);

	/**
	 * <p>Tests whether the specified <code>Number</code> occurs within
	 * this range using <code>long</code> comparison..</p>
	 * <p/>
	 * <p><code>null</code> is handled and returns <code>false</code>.</p>
	 * <p/>
	 * <p>This implementation forwards to the {@link #containsLong(long)} method.</p>
	 *
	 * @param value the long to test, may be <code>null</code>
	 * @return <code>true</code> if the specified number occurs within this
	 *         range by <code>long</code> comparison
	 */
	public boolean containsLong(Number value)
	{
		if (value == null)
		{
			return false;
		}
		return containsLong(value.longValue());
	}

	/**
	 * <p>Tests whether the specified <code>long</code> occurs within
	 * this range using <code>long</code> comparison.</p>
	 * <p/>
	 * <p>This implementation uses the {@link #getMinimumLong()} and
	 * {@link #getMaximumLong()} methods and should be good for most uses.</p>
	 *
	 * @param value the long to test
	 * @return <code>true</code> if the specified number occurs within this
	 *         range by <code>long</code> comparison
	 */
	public boolean containsLong(long value)
	{
		return value >= getMinimumLong() && value <= getMaximumLong();
	}

	/**
	 * <p>Tests whether the specified <code>Number</code> occurs within
	 * this range using <code>int</code> comparison..</p>
	 * <p/>
	 * <p><code>null</code> is handled and returns <code>false</code>.</p>
	 * <p/>
	 * <p>This implementation forwards to the {@link #containsInteger(int)} method.</p>
	 *
	 * @param value the integer to test, may be <code>null</code>
	 * @return <code>true</code> if the specified number occurs within this
	 *         range by <code>int</code> comparison
	 */
	public boolean containsInteger(Number value)
	{
		if (value == null)
		{
			return false;
		}
		return containsInteger(value.intValue());
	}

	/**
	 * <p>Tests whether the specified <code>int</code> occurs within
	 * this range using <code>int</code> comparison.</p>
	 * <p/>
	 * <p>This implementation uses the {@link #getMinimumInteger()} and
	 * {@link #getMaximumInteger()} methods and should be good for most uses.</p>
	 *
	 * @param value the int to test
	 * @return <code>true</code> if the specified number occurs within this
	 *         range by <code>int</code> comparison
	 */
	public boolean containsInteger(int value)
	{
		return value >= getMinimumInteger() && value <= getMaximumInteger();
	}

	/**
	 * <p>Tests whether the specified <code>Number</code> occurs within
	 * this range using <code>double</code> comparison..</p>
	 * <p/>
	 * <p><code>null</code> is handled and returns <code>false</code>.</p>
	 * <p/>
	 * <p>This implementation forwards to the {@link #containsDouble(double)} method.</p>
	 *
	 * @param value the double to test, may be <code>null</code>
	 * @return <code>true</code> if the specified number occurs within this
	 *         range by <code>double</code> comparison
	 */
	public boolean containsDouble(Number value)
	{
		if (value == null)
		{
			return false;
		}
		return containsDouble(value.doubleValue());
	}

	/**
	 * <p>Tests whether the specified <code>double</code> occurs within
	 * this range using <code>double</code> comparison.</p>
	 * <p/>
	 * <p>This implementation uses the {@link #getMinimumDouble()} and
	 * {@link #getMaximumDouble()} methods and should be good for most uses.</p>
	 *
	 * @param value the double to test
	 * @return <code>true</code> if the specified number occurs within this
	 *         range by <code>double</code> comparison
	 */
	public boolean containsDouble(double value)
	{
		int compareMin = NumberUtility.compare(getMinimumDouble(), value);
		int compareMax = NumberUtility.compare(getMaximumDouble(), value);
		return compareMin <= 0 && compareMax >= 0;
	}

	/**
	 * <p>Tests whether the specified <code>Number</code> occurs within
	 * this range using <code>float</code> comparison.</p>
	 * <p/>
	 * <p><code>null</code> is handled and returns <code>false</code>.</p>
	 * <p/>
	 * <p>This implementation forwards to the {@link #containsFloat(float)} method.</p>
	 *
	 * @param value the float to test, may be <code>null</code>
	 * @return <code>true</code> if the specified number occurs within this
	 *         range by <code>float</code> comparison
	 */
	public boolean containsFloat(Number value)
	{
		if (value == null)
		{
			return false;
		}
		return containsFloat(value.floatValue());
	}

	/**
	 * <p>Tests whether the specified <code>float</code> occurs within
	 * this range using <code>float</code> comparison.</p>
	 * <p/>
	 * <p>This implementation uses the {@link #getMinimumFloat()} and
	 * {@link #getMaximumFloat()} methods and should be good for most uses.</p>
	 *
	 * @param value the float to test
	 * @return <code>true</code> if the specified number occurs within this
	 *         range by <code>float</code> comparison
	 */
	public boolean containsFloat(float value)
	{
		int compareMin = NumberUtility.compare(getMinimumFloat(), value);
		int compareMax = NumberUtility.compare(getMaximumFloat(), value);
		return compareMin <= 0 && compareMax >= 0;
	}

	// Range tests

	/**
	 * <p>Tests whether the specified range occurs entirely within this range.</p>
	 * <p/>
	 * <p>The exact comparison implementation varies by subclass. It is
	 * intended that an <code>int</code> specific subclass will compare using
	 * <code>int</code> comparison.</p>
	 * <p/>
	 * <p><code>null</code> is handled and returns <code>false</code>.</p>
	 * <p/>
	 * <p>This implementation uses the {@link #containsNumber(Number)} method.
	 * Subclasses may be able to optimise this.</p>
	 *
	 * @param range the range to test, may be <code>null</code>
	 * @return <code>true</code> if the specified range occurs entirely within
	 *         this range; otherwise, <code>false</code>
	 * @throws IllegalArgumentException if the <code>Range</code> cannot be compared
	 */
	public boolean containsRange(Range range)
	{
		if (range == null)
		{
			return false;
		}
		return containsNumber(range.getMinimumNumber())
				&& containsNumber(range.getMaximumNumber());
	}

	/**
	 * <p>Tests whether the specified range overlaps with this range.</p>
	 * <p/>
	 * <p>The exact comparison implementation varies by subclass. It is
	 * intended that an <code>int</code> specific subclass will compare using
	 * <code>int</code> comparison.</p>
	 * <p/>
	 * <p><code>null</code> is handled and returns <code>false</code>.</p>
	 * <p/>
	 * <p>This implementation uses the {@link #containsNumber(Number)} and
	 * {@link #containsRange(Range)} methods.
	 * Subclasses may be able to optimise this.</p>
	 *
	 * @param range the range to test, may be <code>null</code>
	 * @return <code>true</code> if the specified range overlaps with this
	 *         range; otherwise, <code>false</code>
	 * @throws IllegalArgumentException if the <code>Range</code> cannot be compared
	 */
	public boolean overlapsRange(Range range)
	{
		if (range == null)
		{
			return false;
		}
		return range.containsNumber(getMinimumNumber())
				|| range.containsNumber(getMaximumNumber())
				|| containsNumber(range.getMinimumNumber());
	}

	// Basics

	/**
	 * <p>Compares this range to another object to test if they are equal.</p>.
	 * <p/>
	 * <p>To be equal, the class, minimum and maximum must be equal.</p>
	 * <p/>
	 * <p>This implementation uses the {@link #getMinimumNumber()} and
	 * {@link #getMaximumNumber()} methods.
	 * Subclasses may be able to optimise this.</p>
	 *
	 * @param obj the reference object with which to compare
	 * @return <code>true</code> if this object is equal
	 */
	public boolean equals(Object obj)
	{
		if (obj == this)
		{
			return true;
		}
		else if (obj == null || obj.getClass() != getClass())
		{
			return false;
		}
		else
		{
			Range range = (Range) obj;
			return getMinimumNumber().equals(range.getMinimumNumber()) &&
					getMaximumNumber().equals(range.getMaximumNumber());
		}
	}

	/**
	 * <p>Gets a hashCode for the range.</p>
	 * <p/>
	 * <p>This implementation uses the {@link #getMinimumNumber()} and
	 * {@link #getMaximumNumber()} methods.
	 * Subclasses may be able to optimise this.</p>
	 *
	 * @return a hash code value for this object
	 */
	public int hashCode()
	{
		int result = 17;
		result = 37 * result + getClass().hashCode();
		result = 37 * result + getMinimumNumber().hashCode();
		result = 37 * result + getMaximumNumber().hashCode();
		return result;
	}

	/**
	 * <p>Gets the range as a <code>String</code>.</p>
	 * <p/>
	 * <p>The format of the String is 'Range[<i>min</i>,<i>max</i>]'.</p>
	 * <p/>
	 * <p>This implementation uses the {@link #getMinimumNumber()} and
	 * {@link #getMaximumNumber()} methods.
	 * Subclasses may be able to optimise this.</p>
	 *
	 * @return the <code>String</code> representation of this range
	 */
	public String toString()
	{
		StringBuffer buf = new StringBuffer(32);
		buf.append("Range[");
		buf.append(getMinimumNumber());
		buf.append(',');
		buf.append(getMaximumNumber());
		buf.append(']');
		return buf.toString();
	}

}
