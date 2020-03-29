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

import org.areasy.common.data.workers.ranges.JVMRandom;

import java.util.Random;

/**
 * <p><code>RandomUtility</code> is a wrapper that supports all possible
 * {@link java.util.Random} methods via the {@link java.lang.Math#random()}
 * method and its system-wide <code>Random</code> object.
 *
 * @version $Id: RandomUtility.java,v 1.2 2008/05/14 09:32:30 swd\stefan.damian Exp $
 */
public class RandomUtility
{

	/**
	 * An instance of {@link org.areasy.common.data.workers.ranges.JVMRandom}.
	 */
	public static final Random JVM_RANDOM = new JVMRandom();


	/**
	 * <p>Returns the next pseudorandom, uniformly distributed int value
	 * from the Math.random() sequence.</p>
	 *
	 * @return the random int
	 */
	public static int nextInt()
	{
		return nextInt(JVM_RANDOM);
	}

	/**
	 * <p>Returns the next pseudorandom, uniformly distributed int value
	 * from the given <code>random</code> sequence.</p>
	 *
	 * @param random the Random sequence generator.
	 * @return the random int
	 */
	public static int nextInt(Random random)
	{
		return random.nextInt();
	}

	/**
	 * <p>Returns a pseudorandom, uniformly distributed int value
	 * between <code>0</code> (inclusive) and the specified value
	 * (exclusive), from the Math.random() sequence.</p>
	 *
	 * @param n the specified exclusive max-value
	 * @return the random int
	 */
	public static int nextInt(int n)
	{
		return nextInt(JVM_RANDOM, n);
	}

	/**
	 * <p>Returns a pseudorandom, uniformly distributed int value
	 * between <code>0</code> (inclusive) and the specified value
	 * (exclusive), from the given Random sequence.</p>
	 *
	 * @param random the Random sequence generator.
	 * @param n      the specified exclusive max-value
	 * @return the random int
	 */
	public static int nextInt(Random random, int n)
	{
		// check this cannot return 'n'
		return random.nextInt(n);
	}

	/**
	 * <p>Returns the next pseudorandom, uniformly distributed long value
	 * from the Math.random() sequence.</p>
	 *
	 * @return the random long
	 */
	public static long nextLong()
	{
		return nextLong(JVM_RANDOM);
	}

	/**
	 * <p>Returns the next pseudorandom, uniformly distributed long value
	 * from the given Random sequence.</p>
	 *
	 * @param random the Random sequence generator.
	 * @return the random long
	 */
	public static long nextLong(Random random)
	{
		return random.nextLong();
	}

	/**
	 * <p>Returns the next pseudorandom, uniformly distributed boolean value
	 * from the Math.random() sequence.</p>
	 *
	 * @return the random boolean
	 */
	public static boolean nextBoolean()
	{
		return nextBoolean(JVM_RANDOM);
	}

	/**
	 * <p>Returns the next pseudorandom, uniformly distributed boolean value
	 * from the given random sequence.</p>
	 *
	 * @param random the Random sequence generator.
	 * @return the random boolean
	 */
	public static boolean nextBoolean(Random random)
	{
		return random.nextBoolean();
	}

	/**
	 * <p>Returns the next pseudorandom, uniformly distributed float value
	 * between <code>0.0</code> and <code>1.0</code> from the Math.random()
	 * sequence.</p>
	 *
	 * @return the random float
	 */
	public static float nextFloat()
	{
		return nextFloat(JVM_RANDOM);
	}

	/**
	 * <p>Returns the next pseudorandom, uniformly distributed float value
	 * between <code>0.0</code> and <code>1.0</code> from the given Random
	 * sequence.</p>
	 *
	 * @param random the Random sequence generator.
	 * @return the random float
	 */
	public static float nextFloat(Random random)
	{
		return random.nextFloat();
	}

	/**
	 * <p>Returns the next pseudorandom, uniformly distributed float value
	 * between <code>0.0</code> and <code>1.0</code> from the Math.random()
	 * sequence.</p>
	 *
	 * @return the random double
	 */
	public static double nextDouble()
	{
		return nextDouble(JVM_RANDOM);
	}

	/**
	 * <p>Returns the next pseudorandom, uniformly distributed float value
	 * between <code>0.0</code> and <code>1.0</code> from the given Random
	 * sequence.</p>
	 *
	 * @param random the Random sequence generator.
	 * @return the random double
	 */
	public static double nextDouble(Random random)
	{
		return random.nextDouble();
	}

}
