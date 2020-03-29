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

import org.areasy.common.data.type.Closure;
import org.areasy.common.data.type.Predicate;
import org.areasy.common.data.type.Transformer;
import org.areasy.common.data.workers.functors.*;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * <code>ClosureUtility</code> provides reference implementations and utilities
 * for the Closure functor interface. The supplied closures are:
 * <ul>
 * <li>Invoker - invokes a method on the input object
 * <li>For - repeatedly calls a closure for a fixed number of times
 * <li>While - repeatedly calls a closure while a predicate is true
 * <li>DoWhile - repeatedly calls a closure while a predicate is true
 * <li>Chained - chains two or more closures together
 * <li>Switch - calls one closure based on one or more predicates
 * <li>SwitchMap - calls one closure looked up from a Map
 * <li>Transformer - wraps a Transformer as a Closure
 * <li>NOP - does nothing
 * <li>Exception - always throws an exception
 * </ul>
 * All the supplied closures are Serializable.
 *
 * @version $Id: ClosureUtility.java,v 1.2 2008/05/14 09:32:30 swd\stefan.damian Exp $
 */
public class ClosureUtility
{

	/**
	 * This class is not normally instantiated.
	 */
	public ClosureUtility()
	{
		super();
	}

	/**
	 * Gets a Closure that always throws an exception.
	 * This could be useful during testing as a placeholder.
	 *
	 * @return the closure
	 * @see org.areasy.common.data.workers.functors.ExceptionClosure
	 */
	public static Closure exceptionClosure()
	{
		return ExceptionClosure.INSTANCE;
	}

	/**
	 * Gets a Closure that will do nothing.
	 * This could be useful during testing as a placeholder.
	 *
	 * @return the closure
	 * @see org.areasy.common.data.workers.functors.NOPClosure
	 */
	public static Closure nopClosure()
	{
		return NOPClosure.INSTANCE;
	}

	/**
	 * Creates a Closure that calls a Transformer each time it is called.
	 * The transformer will be called using the closure's input object.
	 * The transformer's result will be ignored.
	 *
	 * @param transformer the transformer to run each time in the closure, null means nop
	 * @return the closure
	 * @see org.areasy.common.data.workers.functors.TransformerClosure
	 */
	public static Closure asClosure(Transformer transformer)
	{
		return TransformerClosure.getInstance(transformer);
	}

	/**
	 * Creates a Closure that will call the closure <code>count</code> times.
	 * <p/>
	 * A null closure or zero count returns the <code>NOPClosure</code>.
	 *
	 * @param count   the number of times to loop
	 * @param closure the closure to call repeatedly
	 * @return the <code>for</code> closure
	 * @see org.areasy.common.data.workers.functors.ForClosure
	 */
	public static Closure forClosure(int count, Closure closure)
	{
		return ForClosure.getInstance(count, closure);
	}

	/**
	 * Creates a Closure that will call the closure repeatedly until the
	 * predicate returns false.
	 *
	 * @param predicate the predicate to use as an end of loop test, not null
	 * @param closure   the closure to call repeatedly, not null
	 * @return the <code>while</code> closure
	 * @throws IllegalArgumentException if either argument is null
	 * @see org.areasy.common.data.workers.functors.WhileClosure
	 */
	public static Closure whileClosure(Predicate predicate, Closure closure)
	{
		return WhileClosure.getInstance(predicate, closure, false);
	}

	/**
	 * Creates a Closure that will call the closure once and then repeatedly
	 * until the predicate returns false.
	 *
	 * @param closure   the closure to call repeatedly, not null
	 * @param predicate the predicate to use as an end of loop test, not null
	 * @return the <code>do-while</code> closure
	 * @throws IllegalArgumentException if either argument is null
	 * @see org.areasy.common.data.workers.functors.WhileClosure
	 */
	public static Closure doWhileClosure(Closure closure, Predicate predicate)
	{
		return WhileClosure.getInstance(predicate, closure, true);
	}

	/**
	 * Creates a Closure that will invoke a specific method on the closure's
	 * input object by reflection.
	 *
	 * @param methodName the name of the method
	 * @return the <code>invoker</code> closure
	 * @throws IllegalArgumentException if the method name is null
	 * @see org.areasy.common.data.workers.functors.InvokerTransformer
	 * @see org.areasy.common.data.workers.functors.TransformerClosure
	 */
	public static Closure invokerClosure(String methodName)
	{
		// reuse transformer as it has caching - this is lazy really, should have inner class here
		return asClosure(InvokerTransformer.getInstance(methodName));
	}

	/**
	 * Creates a Closure that will invoke a specific method on the closure's
	 * input object by reflection.
	 *
	 * @param methodName the name of the method
	 * @param paramTypes the parameter types
	 * @param args       the arguments
	 * @return the <code>invoker</code> closure
	 * @throws IllegalArgumentException if the method name is null
	 * @throws IllegalArgumentException if the paramTypes and args don't match
	 * @see org.areasy.common.data.workers.functors.InvokerTransformer
	 * @see org.areasy.common.data.workers.functors.TransformerClosure
	 */
	public static Closure invokerClosure(String methodName, Class[] paramTypes, Object[] args)
	{
		// reuse transformer as it has caching - this is lazy really, should have inner class here
		return asClosure(InvokerTransformer.getInstance(methodName, paramTypes, args));
	}

	/**
	 * Create a new Closure that calls two Closures, passing the result of
	 * the first into the second.
	 *
	 * @param closure1 the first closure
	 * @param closure2 the second closure
	 * @return the <code>chained</code> closure
	 * @throws IllegalArgumentException if either closure is null
	 * @see org.areasy.common.data.workers.functors.ChainedClosure
	 */
	public static Closure chainedClosure(Closure closure1, Closure closure2)
	{
		return ChainedClosure.getInstance(closure1, closure2);
	}

	/**
	 * Create a new Closure that calls each closure in turn, passing the
	 * result into the next closure.
	 *
	 * @param closures an array of closures to chain
	 * @return the <code>chained</code> closure
	 * @throws IllegalArgumentException if the closures array is null
	 * @throws IllegalArgumentException if any closure in the array is null
	 * @see org.areasy.common.data.workers.functors.ChainedClosure
	 */
	public static Closure chainedClosure(Closure[] closures)
	{
		return ChainedClosure.getInstance(closures);
	}

	/**
	 * Create a new Closure that calls each closure in turn, passing the
	 * result into the next closure. The ordering is that of the iterator()
	 * method on the collection.
	 *
	 * @param closures a collection of closures to chain
	 * @return the <code>chained</code> closure
	 * @throws IllegalArgumentException if the closures collection is null
	 * @throws IllegalArgumentException if the closures collection is empty
	 * @throws IllegalArgumentException if any closure in the collection is null
	 * @see org.areasy.common.data.workers.functors.ChainedClosure
	 */
	public static Closure chainedClosure(Collection closures)
	{
		return ChainedClosure.getInstance(closures);
	}

	/**
	 * Create a new Closure that calls one of two closures depending
	 * on the specified predicate.
	 *
	 * @param predicate    the predicate to switch on
	 * @param trueClosure  the closure called if the predicate is true
	 * @param falseClosure the closure called if the predicate is false
	 * @return the <code>switch</code> closure
	 * @throws IllegalArgumentException if the predicate is null
	 * @throws IllegalArgumentException if either closure is null
	 * @see org.areasy.common.data.workers.functors.IfClosure
	 */
	public static Closure ifClosure(Predicate predicate, Closure trueClosure, Closure falseClosure)
	{
		return IfClosure.getInstance(predicate, trueClosure, falseClosure);
	}

	/**
	 * Create a new Closure that calls one of the closures depending
	 * on the predicates.
	 * <p/>
	 * The closure at array location 0 is called if the predicate at array
	 * location 0 returned true. Each predicate is evaluated
	 * until one returns true.
	 *
	 * @param predicates an array of predicates to check, not null
	 * @param closures   an array of closures to call, not null
	 * @return the <code>switch</code> closure
	 * @throws IllegalArgumentException if the either array is null
	 * @throws IllegalArgumentException if any element in the arrays is null
	 * @throws IllegalArgumentException if the arrays are different sizes
	 * @see org.areasy.common.data.workers.functors.SwitchClosure
	 */
	public static Closure switchClosure(Predicate[] predicates, Closure[] closures)
	{
		return SwitchClosure.getInstance(predicates, closures, null);
	}

	/**
	 * Create a new Closure that calls one of the closures depending
	 * on the predicates.
	 * <p/>
	 * The closure at array location 0 is called if the predicate at array
	 * location 0 returned true. Each predicate is evaluated
	 * until one returns true. If no predicates evaluate to true, the default
	 * closure is called.
	 *
	 * @param predicates     an array of predicates to check, not null
	 * @param closures       an array of closures to call, not null
	 * @param defaultClosure the default to call if no predicate matches
	 * @return the <code>switch</code> closure
	 * @throws IllegalArgumentException if the either array is null
	 * @throws IllegalArgumentException if any element in the arrays is null
	 * @throws IllegalArgumentException if the arrays are different sizes
	 * @see org.areasy.common.data.workers.functors.SwitchClosure
	 */
	public static Closure switchClosure(Predicate[] predicates, Closure[] closures, Closure defaultClosure)
	{
		return SwitchClosure.getInstance(predicates, closures, defaultClosure);
	}

	/**
	 * Create a new Closure that calls one of the closures depending
	 * on the predicates.
	 * <p/>
	 * The Map consists of Predicate keys and Closure values. A closure
	 * is called if its matching predicate returns true. Each predicate is evaluated
	 * until one returns true. If no predicates evaluate to true, the default
	 * closure is called. The default closure is set in the map with a
	 * null key. The ordering is that of the iterator() method on the entryset
	 * collection of the map.
	 *
	 * @param predicatesAndClosures a map of predicates to closures
	 * @return the <code>switch</code> closure
	 * @throws IllegalArgumentException if the map is null
	 * @throws IllegalArgumentException if the map is empty
	 * @throws IllegalArgumentException if any closure in the map is null
	 * @throws ClassCastException       if the map elements are of the wrong type
	 * @see org.areasy.common.data.workers.functors.SwitchClosure
	 */
	public static Closure switchClosure(Map predicatesAndClosures)
	{
		return SwitchClosure.getInstance(predicatesAndClosures);
	}

	/**
	 * Create a new Closure that uses the input object as a key to find the
	 * closure to call.
	 * <p/>
	 * The Map consists of object keys and Closure values. A closure
	 * is called if the input object equals the key. If there is no match, the
	 * default closure is called. The default closure is set in the map
	 * using a null key.
	 *
	 * @param objectsAndClosures a map of objects to closures
	 * @return the closure
	 * @throws IllegalArgumentException if the map is null
	 * @throws IllegalArgumentException if the map is empty
	 * @throws IllegalArgumentException if any closure in the map is null
	 * @see org.areasy.common.data.workers.functors.SwitchClosure
	 */
	public static Closure switchMapClosure(Map objectsAndClosures)
	{
		Closure[] trs = null;
		Predicate[] preds = null;
		if (objectsAndClosures == null)
		{
			throw new IllegalArgumentException("The object and closure map must not be null");
		}
		Closure def = (Closure) objectsAndClosures.remove(null);
		int size = objectsAndClosures.size();
		trs = new Closure[size];
		preds = new Predicate[size];
		int i = 0;
		for (Iterator it = objectsAndClosures.entrySet().iterator(); it.hasNext();)
		{
			Map.Entry entry = (Map.Entry) it.next();
			preds[i] = EqualPredicate.getInstance(entry.getKey());
			trs[i] = (Closure) entry.getValue();
			i++;
		}
		return switchClosure(preds, trs, def);
	}

}
