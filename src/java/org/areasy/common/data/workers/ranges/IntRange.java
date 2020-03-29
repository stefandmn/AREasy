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

import java.io.Serializable;

/**
 * <p><code>IntRange</code> represents an inclusive range of <code>int</code>s.</p>
 *
 * @version $Id: IntRange.java,v 1.2 2008/05/14 09:32:36 swd\stefan.damian Exp $
 */
public final class IntRange extends Range implements Serializable
{
	/**
	 * The minimum number in this range (inclusive).
	 */
	private final int min;
	/**
	 * The maximum number in this range (inclusive).
	 */
	private final int max;

	/**
	 * Cached output minObject (class is immutable).
	 */
	private transient Integer minObject = null;
	/**
	 * Cached output maxObject (class is immutable).
	 */
	private transient Integer maxObject = null;
	/**
	 * Cached output hashCode (class is immutable).
	 */
	private transient int hashCode = 0;
	/**
	 * Cached output toString (class is immutable).
	 */
	private transient String toString = null;

	/**
	 * <p>Constructs a new <code>IntRange</code> using the specified
	 * number as both the minimum and maximum in this range.</p>
	 *
	 * @param number the number to use for this range
	 */
	public IntRange(int number)
	{
		super();
		this.min = number;
		this.max = number;
	}

	/**
	 * <p>Constructs a new <code>IntRange</code> using the specified
	 * number as both the minimum and maximum in this range.</p>
	 *
	 * @param number the number to use for this range, must not be <code>null</code>
	 * @throws IllegalArgumentException if the number is <code>null</code>
	 */
	public IntRange(Number number)
	{
		super();
		if (number == null)
		{
			throw new IllegalArgumentException("The number must not be null");
		}
		this.min = number.intValue();
		this.max = number.intValue();

		if (number instanceof Integer)
		{
			this.minObject = (Integer) number;
			this.maxObject = (Integer) number;
		}
	}

	/**
	 * <p>Constructs a new <code>IntRange</code> with the specified
	 * minimum and maximum numbers (both inclusive).</p>
	 * <p/>
	 * <p>The arguments may be passed in the order (min,max) or (max,min). The
	 * getMinimum and getMaximum methods will return the correct values.</p>
	 *
	 * @param number1 first number that defines the edge of the range, inclusive
	 * @param number2 second number that defines the edge of the range, inclusive
	 */
	public IntRange(int number1, int number2)
	{
		super();
		if (number2 < number1)
		{
			this.min = number2;
			this.max = number1;
		}
		else
		{
			this.min = number1;
			this.max = number2;
		}
	}

	/**
	 * <p>Constructs a new <code>IntRange</code> with the specified
	 * minimum and maximum numbers (both inclusive).</p>
	 * <p/>
	 * <p>The arguments may be passed in the order (min,max) or (max,min). The
	 * getMinimum and getMaximum methods will return the correct values.</p>
	 *
	 * @param number1 first number that defines the edge of the range, inclusive
	 * @param number2 second number that defines the edge of the range, inclusive
	 * @throws IllegalArgumentException if either number is <code>null</code>
	 */
	public IntRange(Number number1, Number number2)
	{
		super();
		if (number1 == null || number2 == null)
		{
			throw new IllegalArgumentException("The numbers must not be null");
		}
		int number1val = number1.intValue();
		int number2val = number2.intValue();
		if (number2val < number1val)
		{
			this.min = number2val;
			this.max = number1val;
			if (number2 instanceof Integer)
			{
				this.minObject = (Integer) number2;
			}
			if (number1 instanceof Integer)
			{
				this.maxObject = (Integer) number1;
			}
		}
		else
		{
			this.min = number1val;
			this.max = number2val;
			if (number1 instanceof Integer)
			{
				this.minObject = (Integer) number1;
			}
			if (number2 instanceof Integer)
			{
				this.maxObject = (Integer) number2;
			}
		}
	}

	/**
	 * <p>Returns the minimum number in this range.</p>
	 *
	 * @return the minimum number in this range
	 */
	public Number getMinimumNumber()
	{
		if (minObject == null)
		{
			minObject = new Integer(min);
		}
		return minObject;
	}

	/**
	 * <p>Gets the minimum number in this range as a <code>long</code>.</p>
	 *
	 * @return the minimum number in this range
	 */
	public long getMinimumLong()
	{
		return min;
	}

	/**
	 * <p>Gets the minimum number in this range as a <code>int</code>.</p>
	 *
	 * @return the minimum number in this range
	 */
	public int getMinimumInteger()
	{
		return min;
	}

	/**
	 * <p>Gets the minimum number in this range as a <code>double</code>.</p>
	 *
	 * @return the minimum number in this range
	 */
	public double getMinimumDouble()
	{
		return min;
	}

	/**
	 * <p>Gets the minimum number in this range as a <code>float</code>.</p>
	 *
	 * @return the minimum number in this range
	 */
	public float getMinimumFloat()
	{
		return min;
	}

	/**
	 * <p>Returns the maximum number in this range.</p>
	 *
	 * @return the maximum number in this range
	 */
	public Number getMaximumNumber()
	{
		if (maxObject == null)
		{
			maxObject = new Integer(max);
		}
		return maxObject;
	}

	/**
	 * <p>Gets the maximum number in this range as a <code>long</code>.</p>
	 *
	 * @return the maximum number in this range
	 */
	public long getMaximumLong()
	{
		return max;
	}

	/**
	 * <p>Gets the maximum number in this range as a <code>int</code>.</p>
	 *
	 * @return the maximum number in this range
	 */
	public int getMaximumInteger()
	{
		return max;
	}

	/**
	 * <p>Gets the maximum number in this range as a <code>double</code>.</p>
	 *
	 * @return the maximum number in this range
	 */
	public double getMaximumDouble()
	{
		return max;
	}

	/**
	 * <p>Gets the maximum number in this range as a <code>float</code>.</p>
	 *
	 * @return the maximum number in this range
	 */
	public float getMaximumFloat()
	{
		return max;
	}

	// Tests

	/**
	 * <p>Tests whether the specified <code>number</code> occurs within
	 * this range using <code>int</code> comparison.</p>
	 * <p/>
	 * <p><code>null</code> is handled and returns <code>false</code>.</p>
	 *
	 * @param number the number to test, may be <code>null</code>
	 * @return <code>true</code> if the specified number occurs within this range
	 */
	public boolean containsNumber(Number number)
	{
		if (number == null)
		{
			return false;
		}
		return containsInteger(number.intValue());
	}

	/**
	 * <p>Tests whether the specified <code>int</code> occurs within
	 * this range using <code>int</code> comparison.</p>
	 * <p/>
	 * <p>This implementation overrides the superclass for performance as it is
	 * the most common case.</p>
	 *
	 * @param value the int to test
	 * @return <code>true</code> if the specified number occurs within this
	 *         range by <code>int</code> comparison
	 */
	public boolean containsInteger(int value)
	{
		return value >= min && value <= max;
	}

	// Range tests

	/**
	 * <p>Tests whether the specified range occurs entirely within this range
	 * using <code>int</code> comparison.</p>
	 * <p/>
	 * <p><code>null</code> is handled and returns <code>false</code>.</p>
	 *
	 * @param range the range to test, may be <code>null</code>
	 * @return <code>true</code> if the specified range occurs entirely within this range
	 * @throws IllegalArgumentException if the range is not of this type
	 */
	public boolean containsRange(Range range)
	{
		if (range == null)
		{
			return false;
		}
		return containsInteger(range.getMinimumInteger()) &&
				containsInteger(range.getMaximumInteger());
	}

	/**
	 * <p>Tests whether the specified range overlaps with this range
	 * using <code>int</code> comparison.</p>
	 * <p/>
	 * <p><code>null</code> is handled and returns <code>false</code>.</p>
	 *
	 * @param range the range to test, may be <code>null</code>
	 * @return <code>true</code> if the specified range overlaps with this range
	 */
	public boolean overlapsRange(Range range)
	{
		if (range == null)
		{
			return false;
		}
		return range.containsInteger(min) ||
				range.containsInteger(max) ||
				containsInteger(range.getMinimumInteger());
	}

	// Basics

	/**
	 * <p>Compares this range to another object to test if they are equal.</p>.
	 * <p/>
	 * <p>To be equal, the class, minimum and maximum must be equal.</p>
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
		if (obj instanceof IntRange == false)
		{
			return false;
		}
		IntRange range = (IntRange) obj;
		return min == range.min && max == range.max;
	}

	/**
	 * <p>Gets a hashCode for the range.</p>
	 *
	 * @return a hash code value for this object
	 */
	public int hashCode()
	{
		if (hashCode == 0)
		{
			hashCode = 17;
			hashCode = 37 * hashCode + getClass().hashCode();
			hashCode = 37 * hashCode + min;
			hashCode = 37 * hashCode + max;
		}
		return hashCode;
	}

	/**
	 * <p>Gets the range as a <code>String</code>.</p>
	 * <p/>
	 * <p>The format of the String is 'Range[<i>min</i>,<i>max</i>]'.</p>
	 *
	 * @return the <code>String</code> representation of this range
	 */
	public String toString()
	{
		if (toString == null)
		{
			StringBuffer buf = new StringBuffer(32);
			buf.append("Range[");
			buf.append(min);
			buf.append(',');
			buf.append(max);
			buf.append(']');
			toString = buf.toString();
		}
		return toString;
	}

}
