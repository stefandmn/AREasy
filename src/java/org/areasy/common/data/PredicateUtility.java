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

import org.areasy.common.data.type.Predicate;
import org.areasy.common.data.type.Transformer;
import org.areasy.common.data.workers.functors.*;

import java.util.Collection;

/**
 * <code>PredicateUtility</code> provides reference implementations and utilities
 * for the Predicate functor interface. The supplied predicates are:
 * <ul>
 * <li>Invoker - returns the result of a method call on the input object
 * <li>InstanceOf - true if the object is an instanceof a class
 * <li>Equal - true if the object equals() a specified object
 * <li>Identity - true if the object == a specified object
 * <li>Null - true if the object is null
 * <li>NotNull - true if the object is not null
 * <li>Unique - true if the object has not already been evaluated
 * <li>And/All - true if all of the predicates are true
 * <li>Or/Any - true if any of the predicates is true
 * <li>Either/One - true if only one of the predicate is true
 * <li>Neither/None - true if none of the predicates are true
 * <li>Not - true if the predicate is false, and vice versa
 * <li>Transformer - wraps a Transformer as a Predicate
 * <li>True - always return true
 * <li>False - always return false
 * <li>Exception - always throws an exception
 * <li>NullIsException/NullIsFalse/NullIsTrue - check for null input
 * <li>Transformed - transforms the input before calling the predicate
 * </ul>
 * All the supplied predicates are Serializable.
 *
 * @version $Id: PredicateUtility.java,v 1.2 2008/05/14 09:32:29 swd\stefan.damian Exp $
 */
public class PredicateUtility
{

	/**
	 * This class is not normally instantiated.
	 */
	public PredicateUtility()
	{
		super();
	}

	// Simple predicates

	/**
	 * Gets a Predicate that always throws an exception.
	 * This could be useful during testing as a placeholder.
	 *
	 * @return the predicate
	 * @see org.areasy.common.data.workers.functors.ExceptionPredicate
	 */
	public static Predicate exceptionPredicate()
	{
		return ExceptionPredicate.INSTANCE;
	}

	/**
	 * Gets a Predicate that always returns true.
	 *
	 * @return the predicate
	 * @see org.areasy.common.data.workers.functors.TruePredicate
	 */
	public static Predicate truePredicate()
	{
		return TruePredicate.INSTANCE;
	}

	/**
	 * Gets a Predicate that always returns false.
	 *
	 * @return the predicate
	 * @see org.areasy.common.data.workers.functors.FalsePredicate
	 */
	public static Predicate falsePredicate()
	{
		return FalsePredicate.INSTANCE;
	}

	/**
	 * Gets a Predicate that checks if the input object passed in is null.
	 *
	 * @return the predicate
	 * @see org.areasy.common.data.workers.functors.NullPredicate
	 */
	public static Predicate nullPredicate()
	{
		return NullPredicate.INSTANCE;
	}

	/**
	 * Gets a Predicate that checks if the input object passed in is not null.
	 *
	 * @return the predicate
	 * @see org.areasy.common.data.workers.functors.NotNullPredicate
	 */
	public static Predicate notNullPredicate()
	{
		return NotNullPredicate.INSTANCE;
	}

	/**
	 * Creates a Predicate that checks if the input object is equal to the
	 * specified object using equals().
	 *
	 * @param value the value to compare against
	 * @return the predicate
	 * @see org.areasy.common.data.workers.functors.EqualPredicate
	 */
	public static Predicate equalPredicate(Object value)
	{
		return EqualPredicate.getInstance(value);
	}

	/**
	 * Creates a Predicate that checks if the input object is equal to the
	 * specified object by identity.
	 *
	 * @param value the value to compare against
	 * @return the predicate
	 * @see org.areasy.common.data.workers.functors.IdentityPredicate
	 */
	public static Predicate identityPredicate(Object value)
	{
		return IdentityPredicate.getInstance(value);
	}

	/**
	 * Creates a Predicate that checks if the object passed in is of
	 * a particular type, using instanceof. A <code>null</code> input
	 * object will return <code>false</code>.
	 *
	 * @param type the type to check for, may not be null
	 * @return the predicate
	 * @throws IllegalArgumentException if the class is null
	 * @see org.areasy.common.data.workers.functors.InstanceofPredicate
	 */
	public static Predicate instanceofPredicate(Class type)
	{
		return InstanceofPredicate.getInstance(type);
	}

	/**
	 * Creates a Predicate that returns true the first time an object is
	 * encountered, and false if the same object is received
	 * again. The comparison is by equals(). A <code>null</code> input object
	 * is accepted and will return true the first time, and false subsequently
	 * as well.
	 *
	 * @return the predicate
	 * @see org.areasy.common.data.workers.functors.UniquePredicate
	 */
	public static Predicate uniquePredicate()
	{
		// must return new instance each time
		return UniquePredicate.getInstance();
	}

	/**
	 * Creates a Predicate that invokes a method on the input object.
	 * The method must return either a boolean or a non-null Boolean,
	 * and have no parameters. If the input object is null, a
	 * PredicateException is thrown.
	 * <p/>
	 * For example, <code>PredicateUtility.invokerPredicate("isEmpty");</code>
	 * will call the <code>isEmpty</code> method on the input object to
	 * determine the predicate result.
	 *
	 * @param methodName the method name to call on the input object, may not be null
	 * @return the predicate
	 * @throws IllegalArgumentException if the methodName is null.
	 * @see org.areasy.common.data.workers.functors.InvokerTransformer
	 * @see org.areasy.common.data.workers.functors.TransformerPredicate
	 */
	public static Predicate invokerPredicate(String methodName)
	{
		// reuse transformer as it has caching - this is lazy really, should have inner class here
		return asPredicate(InvokerTransformer.getInstance(methodName));
	}

	/**
	 * Creates a Predicate that invokes a method on the input object.
	 * The method must return either a boolean or a non-null Boolean,
	 * and have no parameters. If the input object is null, a
	 * PredicateException is thrown.
	 * <p/>
	 * For example, <code>PredicateUtility.invokerPredicate("isEmpty");</code>
	 * will call the <code>isEmpty</code> method on the input object to
	 * determine the predicate result.
	 *
	 * @param methodName the method name to call on the input object, may not be null
	 * @param paramTypes the parameter types
	 * @param args       the arguments
	 * @return the predicate
	 * @throws IllegalArgumentException if the method name is null
	 * @throws IllegalArgumentException if the paramTypes and args don't match
	 * @see org.areasy.common.data.workers.functors.InvokerTransformer
	 * @see org.areasy.common.data.workers.functors.TransformerPredicate
	 */
	public static Predicate invokerPredicate(String methodName, Class[] paramTypes, Object[] args)
	{
		// reuse transformer as it has caching - this is lazy really, should have inner class here
		return asPredicate(InvokerTransformer.getInstance(methodName, paramTypes, args));
	}

	// Boolean combinations

	/**
	 * Create a new Predicate that returns true only if both of the specified
	 * predicates are true.
	 *
	 * @param predicate1 the first predicate, may not be null
	 * @param predicate2 the second predicate, may not be null
	 * @return the <code>and</code> predicate
	 * @throws IllegalArgumentException if either predicate is null
	 * @see org.areasy.common.data.workers.functors.AndPredicate
	 */
	public static Predicate andPredicate(Predicate predicate1, Predicate predicate2)
	{
		return AndPredicate.getInstance(predicate1, predicate2);
	}

	/**
	 * Create a new Predicate that returns true only if all of the specified
	 * predicates are true.
	 *
	 * @param predicates an array of predicates to check, may not be null
	 * @return the <code>all</code> predicate
	 * @throws IllegalArgumentException if the predicates array is null
	 * @throws IllegalArgumentException if the predicates array has less than 2 elements
	 * @throws IllegalArgumentException if any predicate in the array is null
	 * @see org.areasy.common.data.workers.functors.AllPredicate
	 */
	public static Predicate allPredicate(Predicate[] predicates)
	{
		return AllPredicate.getInstance(predicates);
	}

	/**
	 * Create a new Predicate that returns true only if all of the specified
	 * predicates are true. The predicates are checked in iterator order.
	 *
	 * @param predicates a collection of predicates to check, may not be null
	 * @return the <code>all</code> predicate
	 * @throws IllegalArgumentException if the predicates collection is null
	 * @throws IllegalArgumentException if the predicates collection has less than 2 elements
	 * @throws IllegalArgumentException if any predicate in the collection is null
	 * @see org.areasy.common.data.workers.functors.AllPredicate
	 */
	public static Predicate allPredicate(Collection predicates)
	{
		return AllPredicate.getInstance(predicates);
	}

	/**
	 * Create a new Predicate that returns true if either of the specified
	 * predicates are true.
	 *
	 * @param predicate1 the first predicate, may not be null
	 * @param predicate2 the second predicate, may not be null
	 * @return the <code>or</code> predicate
	 * @throws IllegalArgumentException if either predicate is null
	 * @see org.areasy.common.data.workers.functors.OrPredicate
	 */
	public static Predicate orPredicate(Predicate predicate1, Predicate predicate2)
	{
		return OrPredicate.getInstance(predicate1, predicate2);
	}

	/**
	 * Create a new Predicate that returns true if any of the specified
	 * predicates are true.
	 *
	 * @param predicates an array of predicates to check, may not be null
	 * @return the <code>any</code> predicate
	 * @throws IllegalArgumentException if the predicates array is null
	 * @throws IllegalArgumentException if the predicates array has less than 2 elements
	 * @throws IllegalArgumentException if any predicate in the array is null
	 * @see org.areasy.common.data.workers.functors.AnyPredicate
	 */
	public static Predicate anyPredicate(Predicate[] predicates)
	{
		return AnyPredicate.getInstance(predicates);
	}

	/**
	 * Create a new Predicate that returns true if any of the specified
	 * predicates are true. The predicates are checked in iterator order.
	 *
	 * @param predicates a collection of predicates to check, may not be null
	 * @return the <code>any</code> predicate
	 * @throws IllegalArgumentException if the predicates collection is null
	 * @throws IllegalArgumentException if the predicates collection has less than 2 elements
	 * @throws IllegalArgumentException if any predicate in the collection is null
	 * @see org.areasy.common.data.workers.functors.AnyPredicate
	 */
	public static Predicate anyPredicate(Collection predicates)
	{
		return AnyPredicate.getInstance(predicates);
	}

	/**
	 * Create a new Predicate that returns true if one, but not both, of the
	 * specified predicates are true.
	 *
	 * @param predicate1 the first predicate, may not be null
	 * @param predicate2 the second predicate, may not be null
	 * @return the <code>either</code> predicate
	 * @throws IllegalArgumentException if either predicate is null
	 * @see org.areasy.common.data.workers.functors.OnePredicate
	 */
	public static Predicate eitherPredicate(Predicate predicate1, Predicate predicate2)
	{
		return onePredicate(new Predicate[]{predicate1, predicate2});
	}

	/**
	 * Create a new Predicate that returns true if only one of the specified
	 * predicates are true.
	 *
	 * @param predicates an array of predicates to check, may not be null
	 * @return the <code>one</code> predicate
	 * @throws IllegalArgumentException if the predicates array is null
	 * @throws IllegalArgumentException if the predicates array has less than 2 elements
	 * @throws IllegalArgumentException if any predicate in the array is null
	 * @see org.areasy.common.data.workers.functors.OnePredicate
	 */
	public static Predicate onePredicate(Predicate[] predicates)
	{
		return OnePredicate.getInstance(predicates);
	}

	/**
	 * Create a new Predicate that returns true if only one of the specified
	 * predicates are true. The predicates are checked in iterator order.
	 *
	 * @param predicates a collection of predicates to check, may not be null
	 * @return the <code>one</code> predicate
	 * @throws IllegalArgumentException if the predicates collection is null
	 * @throws IllegalArgumentException if the predicates collection has less than 2 elements
	 * @throws IllegalArgumentException if any predicate in the collection is null
	 * @see org.areasy.common.data.workers.functors.OnePredicate
	 */
	public static Predicate onePredicate(Collection predicates)
	{
		return OnePredicate.getInstance(predicates);
	}

	/**
	 * Create a new Predicate that returns true if neither of the specified
	 * predicates are true.
	 *
	 * @param predicate1 the first predicate, may not be null
	 * @param predicate2 the second predicate, may not be null
	 * @return the <code>neither</code> predicate
	 * @throws IllegalArgumentException if either predicate is null
	 * @see org.areasy.common.data.workers.functors.NonePredicate
	 */
	public static Predicate neitherPredicate(Predicate predicate1, Predicate predicate2)
	{
		return nonePredicate(new Predicate[]{predicate1, predicate2});
	}

	/**
	 * Create a new Predicate that returns true if none of the specified
	 * predicates are true.
	 *
	 * @param predicates an array of predicates to check, may not be null
	 * @return the <code>none</code> predicate
	 * @throws IllegalArgumentException if the predicates array is null
	 * @throws IllegalArgumentException if the predicates array has less than 2 elements
	 * @throws IllegalArgumentException if any predicate in the array is null
	 * @see org.areasy.common.data.workers.functors.NonePredicate
	 */
	public static Predicate nonePredicate(Predicate[] predicates)
	{
		return NonePredicate.getInstance(predicates);
	}

	/**
	 * Create a new Predicate that returns true if none of the specified
	 * predicates are true. The predicates are checked in iterator order.
	 *
	 * @param predicates a collection of predicates to check, may not be null
	 * @return the <code>none</code> predicate
	 * @throws IllegalArgumentException if the predicates collection is null
	 * @throws IllegalArgumentException if the predicates collection has less than 2 elements
	 * @throws IllegalArgumentException if any predicate in the collection is null
	 * @see org.areasy.common.data.workers.functors.NonePredicate
	 */
	public static Predicate nonePredicate(Collection predicates)
	{
		return NonePredicate.getInstance(predicates);
	}

	/**
	 * Create a new Predicate that returns true if the specified predicate
	 * returns false and vice versa.
	 *
	 * @param predicate the predicate to not
	 * @return the <code>not</code> predicate
	 * @throws IllegalArgumentException if the predicate is null
	 * @see org.areasy.common.data.workers.functors.NotPredicate
	 */
	public static Predicate notPredicate(Predicate predicate)
	{
		return NotPredicate.getInstance(predicate);
	}

	// Adaptors

	/**
	 * Create a new Predicate that wraps a Transformer. The Transformer must
	 * return either Boolean.TRUE or Boolean.FALSE otherwise a PredicateException
	 * will be thrown.
	 *
	 * @param transformer the transformer to wrap, may not be null
	 * @return the transformer wrapping predicate
	 * @throws IllegalArgumentException if the transformer is null
	 * @see org.areasy.common.data.workers.functors.TransformerPredicate
	 */
	public static Predicate asPredicate(Transformer transformer)
	{
		return TransformerPredicate.getInstance(transformer);
	}

	// Null handlers

	/**
	 * Gets a Predicate that throws an exception if the input object is null,
	 * otherwise it calls the specified Predicate. This allows null handling
	 * behaviour to be added to Predicates that don't support nulls.
	 *
	 * @param predicate the predicate to wrap, may not be null
	 * @return the predicate
	 * @throws IllegalArgumentException if the predicate is null.
	 * @see org.areasy.common.data.workers.functors.NullIsExceptionPredicate
	 */
	public static Predicate nullIsExceptionPredicate(Predicate predicate)
	{
		return NullIsExceptionPredicate.getInstance(predicate);
	}

	/**
	 * Gets a Predicate that returns false if the input object is null, otherwise
	 * it calls the specified Predicate. This allows null handling behaviour to
	 * be added to Predicates that don't support nulls.
	 *
	 * @param predicate the predicate to wrap, may not be null
	 * @return the predicate
	 * @throws IllegalArgumentException if the predicate is null.
	 * @see org.areasy.common.data.workers.functors.NullIsFalsePredicate
	 */
	public static Predicate nullIsFalsePredicate(Predicate predicate)
	{
		return NullIsFalsePredicate.getInstance(predicate);
	}

	/**
	 * Gets a Predicate that returns true if the input object is null, otherwise
	 * it calls the specified Predicate. This allows null handling behaviour to
	 * be added to Predicates that don't support nulls.
	 *
	 * @param predicate the predicate to wrap, may not be null
	 * @return the predicate
	 * @throws IllegalArgumentException if the predicate is null.
	 * @see org.areasy.common.data.workers.functors.NullIsTruePredicate
	 */
	public static Predicate nullIsTruePredicate(Predicate predicate)
	{
		return NullIsTruePredicate.getInstance(predicate);
	}

	// Transformed
	/**
	 * Creates a predicate that transforms the input object before passing it
	 * to the predicate.
	 *
	 * @param transformer the transformer to call first
	 * @param predicate   the predicate to call with the result of the transform
	 * @return the predicate
	 * @throws IllegalArgumentException if the transformer or the predicate is null
	 * @see org.areasy.common.data.workers.functors.TransformedPredicate
	 */
	public static Predicate transformedPredicate(Transformer transformer, Predicate predicate)
	{
		return TransformedPredicate.getInstance(transformer, predicate);
	}

}
