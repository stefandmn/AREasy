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
 * <p>A contiguous range of characters, optionally negated.</p>
 * <p/>
 * <p>Instances are immutable.</p>
 *
 * @version $Id: CharRange.java,v 1.2 2008/05/14 09:32:36 swd\stefan.damian Exp $
 */
public final class CharRange implements Serializable
{
	/**
	 * The first character, inclusive, in the range.
	 */
	private final char start;
	/**
	 * The last character, inclusive, in the range.
	 */
	private final char end;
	/**
	 * True if the range is everything except the characters specified.
	 */
	private final boolean negated;

	/**
	 * Cached toString.
	 */
	private transient String iToString;

	/**
	 * <p>Constructs a <code>CharRange</code> over a single character.</p>
	 *
	 * @param ch only character in this range
	 */
	public CharRange(char ch)
	{
		this(ch, ch, false);
	}

	/**
	 * <p>Constructs a <code>CharRange</code> over a single character,
	 * optionally negating the range.</p>
	 * <p/>
	 * <p>A negated range includes everything except the specified char.</p>
	 *
	 * @param ch      only character in this range
	 * @param negated true to express everything except the range
	 */
	public CharRange(char ch, boolean negated)
	{
		this(ch, ch, negated);
	}

	/**
	 * <p>Constructs a <code>CharRange</code> over a set of characters.</p>
	 *
	 * @param start first character, inclusive, in this range
	 * @param end   last character, inclusive, in this range
	 */
	public CharRange(char start, char end)
	{
		this(start, end, false);
	}

	/**
	 * <p>Constructs a <code>CharRange</code> over a set of characters,
	 * optionally negating the range.</p>
	 * <p/>
	 * <p>A negated range includes everything except that defined by the
	 * start and end characters.</p>
	 * <p/>
	 * <p>If start and end are in the wrong order, they are reversed.
	 * Thus <code>a-e</code> is the same as <code>e-a</code>.</p>
	 *
	 * @param start   first character, inclusive, in this range
	 * @param end     last character, inclusive, in this range
	 * @param negated true to express everything except the range
	 */
	public CharRange(char start, char end, boolean negated)
	{
		super();
		if (start > end)
		{
			char temp = start;
			start = end;
			end = temp;
		}

		this.start = start;
		this.end = end;
		this.negated = negated;
	}

	// Accessors
	/**
	 * <p>Gets the start character for this character range.</p>
	 *
	 * @return the start char (inclusive)
	 */
	public char getStart()
	{
		return this.start;
	}

	/**
	 * <p>Gets the end character for this character range.</p>
	 *
	 * @return the end char (inclusive)
	 */
	public char getEnd()
	{
		return this.end;
	}

	/**
	 * <p>Is this <code>CharRange</code> negated.</p>
	 * <p/>
	 * <p>A negated range includes everything except that defined by the
	 * start and end characters.</p>
	 *
	 * @return <code>true</code> is negated
	 */
	public boolean isNegated()
	{
		return negated;
	}

	// Contains
	/**
	 * <p>Is the character specified contained in this range.</p>
	 *
	 * @param ch the character to check
	 * @return <code>true</code> if this range contains the input character
	 */
	public boolean contains(char ch)
	{
		return (ch >= start && ch <= end) != negated;
	}

	/**
	 * <p>Are all the characters of the passed in range contained in
	 * this range.</p>
	 *
	 * @param range the range to check against
	 * @return <code>true</code> if this range entirely contains the input range
	 * @throws IllegalArgumentException if <code>null</code> input
	 */
	public boolean contains(CharRange range)
	{
		if (range == null)
		{
			throw new IllegalArgumentException("The Range must not be null");
		}
		if (negated)
		{
			if (range.negated)
			{
				return start >= range.start && end <= range.end;
			}
			else
			{
				return range.end < start || range.start > end;
			}
		}
		else
		{
			if (range.negated)
			{
				return start == 0 && end == Character.MAX_VALUE;
			}
			else
			{
				return start <= range.start && end >= range.end;
			}
		}
	}

	// Basics
	/**
	 * <p>Compares two CharRange objects, returning true if they represent
	 * exactly the same range of characters defined in the same way.</p>
	 *
	 * @param obj the object to compare to
	 * @return true if equal
	 */
	public boolean equals(Object obj)
	{
		if (obj == this)
		{
			return true;
		}
		if (obj instanceof CharRange == false)
		{
			return false;
		}
		CharRange other = (CharRange) obj;
		return start == other.start && end == other.end && negated == other.negated;
	}

	/**
	 * <p>Gets a hashCode compatible with the equals method.</p>
	 *
	 * @return a suitable hashCode
	 */
	public int hashCode()
	{
		return 83 + start + 7 * end + (negated ? 1 : 0);
	}

	/**
	 * <p>Gets a string representation of the character range.</p>
	 *
	 * @return string representation of this range
	 */
	public String toString()
	{
		if (iToString == null)
		{
			StringBuffer buf = new StringBuffer(4);
			if (isNegated())
			{
				buf.append('^');
			}
			buf.append(start);
			if (start != end)
			{
				buf.append('-');
				buf.append(end);
			}
			iToString = buf.toString();
		}
		return iToString;
	}

}
