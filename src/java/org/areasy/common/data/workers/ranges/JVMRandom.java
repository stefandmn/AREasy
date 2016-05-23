package org.areasy.common.data.workers.ranges;

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

import java.util.Random;

/**
 * <p><code>JVMRandom</code> is a wrapper that supports all possible
 * Random methods via the {@link java.lang.Math#random()} method
 * and its system-wide {@link Random} object.</p>
 *
 * @version $Id: JVMRandom.java,v 1.2 2008/05/14 09:32:36 swd\stefan.damian Exp $
 */
public final class JVMRandom extends Random
{

	/**
	 * Ensures that only the constructor can call reseed.
	 */
	private boolean constructed = false;

	/**
	 * Constructs a new instance.
	 */
	public JVMRandom()
	{
		this.constructed = true;
	}

	/**
	 * Unsupported in 2.0.
	 *
	 * @param seed ignored
	 * @throws UnsupportedOperationException
	 */
	public synchronized void setSeed(long seed)
	{
		if (this.constructed) throw new UnsupportedOperationException();
	}

	/**
	 * Unsupported in 2.0.
	 *
	 * @return Nothing, this method always throws an UnsupportedOperationException.
	 * @throws UnsupportedOperationException
	 */
	public synchronized double nextGaussian()
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * Unsupported in 2.0.
	 *
	 * @param byteArray ignored
	 * @throws UnsupportedOperationException
	 */
	public void nextBytes(byte[] byteArray)
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * <p>Returns the next pseudorandom, uniformly distributed int value
	 * from the Math.random() sequence.</p>
	 *
	 * @return the random int
	 */
	public int nextInt()
	{
		return nextInt(Integer.MAX_VALUE);
	}

	/**
	 * <p>Returns a pseudorandom, uniformly distributed int value between
	 * <code>0</code> (inclusive) and the specified value (exclusive), from
	 * the Math.random() sequence.</p>
	 *
	 * @param n the specified exclusive max-value
	 * @return the random int
	 * @throws IllegalArgumentException when <code>n &lt;= 0</code>
	 */
	public int nextInt(int n)
	{
		if (n <= 0) throw new IllegalArgumentException("Upper bound for nextInt must be positive");

		return (int) (Math.random() * n);
	}

	/**
	 * <p>Returns the next pseudorandom, uniformly distributed long value
	 * from the Math.random() sequence.</p>
	 *
	 * @return the random long
	 */
	public long nextLong()
	{
		// possible loss of precision?
		return nextLong(Long.MAX_VALUE);
	}


	/**
	 * <p>Returns a pseudorandom, uniformly distributed long value between
	 * <code>0</code> (inclusive) and the specified value (exclusive), from
	 * the Math.random() sequence.</p>
	 *
	 * @param n the specified exclusive max-value
	 * @return the random long
	 * @throws IllegalArgumentException when <code>n &lt;= 0</code>
	 */
	public static long nextLong(long n)
	{
		if (n <= 0) throw new IllegalArgumentException("Upper bound for nextInt must be positive");

		return (long) (Math.random() * n);
	}

	/**
	 * <p>Returns the next pseudorandom, uniformly distributed boolean value
	 * from the Math.random() sequence.</p>
	 *
	 * @return the random boolean
	 */
	public boolean nextBoolean()
	{
		return Math.random() > 0.5;
	}

	/**
	 * <p>Returns the next pseudorandom, uniformly distributed float value
	 * between <code>0.0</code> and <code>1.0</code> from the Math.random()
	 * sequence.</p>
	 *
	 * @return the random float
	 */
	public float nextFloat()
	{
		return (float) Math.random();
	}

	/**
	 * <p>Synonymous to the Math.random() call.</p>
	 *
	 * @return the random double
	 */
	public double nextDouble()
	{
		return Math.random();
	}

}
