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
 * <p><code>DoubleRange</code> represents an inclusive range of <code>double</code>s.</p>
 *
 * @version $Id: DoubleRange.java,v 1.2 2008/05/14 09:32:35 swd\stefan.damian Exp $
 */
public final class DoubleRange extends Range implements Serializable
{

	/**
	 * The minimum number in this range (inclusive).
	 */
	private final double min;
	/**
	 * The maximum number in this range (inclusive).
	 */
	private final double max;

	/**
	 * Cached output minObject (class is immutable).
	 */
	private transient Double minObject = null;
	/**
	 * Cached output maxObject (class is immutable).
	 */
	private transient Double maxObject = null;
	/**
	 * Cached output hashCode (class is immutable).
	 */
	private transient int hashCode = 0;
	/**
	 * Cached output toString (class is immutable).
	 */
	private transient String toString = null;

	/**
	 * <p>Constructs a new <code>DoubleRange</code> using the specified
	 * number as both the minimum and maximum in this range.</p>
	 *
	 * @param number the number to use for this range
	 * @throws IllegalArgumentException if the number is <code>NaN</code>
	 */
	public DoubleRange(double number)
	{
		super();
		if (Double.isNaN(number))
		{
			throw new IllegalArgumentException("The number must not be NaN");
		}
		this.min = number;
		this.max = number;
	}

	/**
	 * <p>Constructs a new <code>DoubleRange</code> using the specified
	 * number as both the minimum and maximum in this range.</p>
	 *
	 * @param number the number to use for this range, must not
	 *               be <code>null</code>
	 * @throws IllegalArgumentException if the number is <code>null</code>
	 * @throws IllegalArgumentException if the number is <code>NaN</code>
	 */
	public DoubleRange(Number number)
	{
		super();
		if (number == null)
		{
			throw new IllegalArgumentException("The number must not be null");
		}
		this.min = number.doubleValue();
		this.max = number.doubleValue();
		if (Double.isNaN(min) || Double.isNaN(max))
		{
			throw new IllegalArgumentException("The number must not be NaN");
		}
		if (number instanceof Double)
		{
			this.minObject = (Double) number;
			this.maxObject = (Double) number;
		}
	}

	/**
	 * <p>Constructs a new <code>DoubleRange</code> with the specified
	 * minimum and maximum numbers (both inclusive).</p>
	 * <p/>
	 * <p>The arguments may be passed in the order (min,max) or (max,min). The
	 * getMinimum and getMaximum methods will return the correct values.</p>
	 *
	 * @param number1 first number that defines the edge of the range, inclusive
	 * @param number2 second number that defines the edge of the range, inclusive
	 * @throws IllegalArgumentException if either number is <code>NaN</code>
	 */
	public DoubleRange(double number1, double number2)
	{
		super();
		if (Double.isNaN(number1) || Double.isNaN(number2))
		{
			throw new IllegalArgumentException("The numbers must not be NaN");
		}
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
	 * <p>Constructs a new <code>DoubleRange</code> with the specified
	 * minimum and maximum numbers (both inclusive).</p>
	 * <p/>
	 * <p>The arguments may be passed in the order (min,max) or (max,min). The
	 * getMinimum and getMaximum methods will return the correct values.</p>
	 *
	 * @param number1 first number that defines the edge of the range, inclusive
	 * @param number2 second number that defines the edge of the range, inclusive
	 * @throws IllegalArgumentException if either number is <code>null</code>
	 * @throws IllegalArgumentException if either number is <code>NaN</code>
	 */
	public DoubleRange(Number number1, Number number2)
	{
		super();
		if (number1 == null || number2 == null)
		{
			throw new IllegalArgumentException("The numbers must not be null");
		}
		double number1val = number1.doubleValue();
		double number2val = number2.doubleValue();
		if (Double.isNaN(number1val) || Double.isNaN(number2val))
		{
			throw new IllegalArgumentException("The numbers must not be NaN");
		}
		if (number2val < number1val)
		{
			this.min = number2val;
			this.max = number1val;
			if (number2 instanceof Double)
			{
				this.minObject = (Double) number2;
			}
			if (number1 instanceof Double)
			{
				this.maxObject = (Double) number1;
			}
		}
		else
		{
			this.min = number1val;
			this.max = number2val;
			if (number1 instanceof Double)
			{
				this.minObject = (Double) number1;
			}
			if (number2 instanceof Double)
			{
				this.maxObject = (Double) number2;
			}
		}
	}

	// Accessors

	/**
	 * <p>Returns the minimum number in this range.</p>
	 *
	 * @return the minimum number in this range
	 */
	public Number getMinimumNumber()
	{
		if (minObject == null)
		{
			minObject = new Double(min);
		}
		return minObject;
	}

	/**
	 * <p>Gets the minimum number in this range as a <code>long</code>.</p>
	 * <p/>
	 * <p>This conversion can lose information for large values or decimals.</p>
	 *
	 * @return the minimum number in this range
	 */
	public long getMinimumLong()
	{
		return (long) min;
	}

	/**
	 * <p>Gets the minimum number in this range as a <code>int</code>.</p>
	 * <p/>
	 * <p>This conversion can lose information for large values or decimals.</p>
	 *
	 * @return the minimum number in this range
	 */
	public int getMinimumInteger()
	{
		return (int) min;
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
	 * <p/>
	 * <p>This conversion can lose information for large values.</p>
	 *
	 * @return the minimum number in this range
	 */
	public float getMinimumFloat()
	{
		return (float) min;
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
			maxObject = new Double(max);
		}
		return maxObject;
	}

	/**
	 * <p>Gets the maximum number in this range as a <code>long</code>.</p>
	 * <p/>
	 * <p>This conversion can lose information for large values or decimals.</p>
	 *
	 * @return the maximum number in this range
	 */
	public long getMaximumLong()
	{
		return (long) max;
	}

	/**
	 * <p>Gets the maximum number in this range as a <code>int</code>.</p>
	 * <p/>
	 * <p>This conversion can lose information for large values or decimals.</p>
	 *
	 * @return the maximum number in this range
	 */
	public int getMaximumInteger()
	{
		return (int) max;
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
	 * <p/>
	 * <p>This conversion can lose information for large values.</p>
	 *
	 * @return the maximum number in this range
	 */
	public float getMaximumFloat()
	{
		return (float) max;
	}

	// Tests

	/**
	 * <p>Tests whether the specified <code>number</code> occurs within
	 * this range using <code>double</code> comparison.</p>
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
		return containsDouble(number.doubleValue());
	}

	/**
	 * <p>Tests whether the specified <code>double</code> occurs within
	 * this range using <code>double</code> comparison.</p>
	 * <p/>
	 * <p>This implementation overrides the superclass for performance as it is
	 * the most common case.</p>
	 *
	 * @param value the double to test
	 * @return <code>true</code> if the specified number occurs within this
	 *         range by <code>double</code> comparison
	 */
	public boolean containsDouble(double value)
	{
		return value >= min && value <= max;
	}

	// Range tests

	/**
	 * <p>Tests whether the specified range occurs entirely within this range
	 * using <code>double</code> comparison.</p>
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
		return containsDouble(range.getMinimumDouble())
				&& containsDouble(range.getMaximumDouble());
	}

	/**
	 * <p>Tests whether the specified range overlaps with this range
	 * using <code>double</code> comparison.</p>
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
		return range.containsDouble(min)
				|| range.containsDouble(max)
				|| containsDouble(range.getMinimumDouble());
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
		if (obj instanceof DoubleRange == false)
		{
			return false;
		}
		DoubleRange range = (DoubleRange) obj;
		return (Double.doubleToLongBits(min) == Double.doubleToLongBits(range.min) &&
				Double.doubleToLongBits(max) == Double.doubleToLongBits(range.max));
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
			long lng = Double.doubleToLongBits(min);
			hashCode = 37 * hashCode + ((int) (lng ^ (lng >> 32)));
			lng = Double.doubleToLongBits(max);
			hashCode = 37 * hashCode + ((int) (lng ^ (lng >> 32)));
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
