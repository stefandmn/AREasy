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
import org.areasy.common.data.type.Factory;
import org.areasy.common.data.type.Predicate;
import org.areasy.common.data.type.Transformer;
import org.areasy.common.data.workers.functors.*;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * <code>TransformerUtility</code> provides reference implementations and
 * utilities for the Transformer functor interface. The supplied transformers are:
 * <ul>
 * <li>Invoker - returns the result of a method call on the input object
 * <li>Clone - returns a clone of the input object
 * <li>Constant - always returns the same object
 * <li>Closure - performs a Closure and returns the input object
 * <li>Predicate - returns the result of the predicate as a Boolean
 * <li>Factory - returns a new object from a factory
 * <li>Chained - chains two or more transformers together
 * <li>Switch - calls one transformer based on one or more predicates
 * <li>SwitchMap - calls one transformer looked up from a Map
 * <li>Instantiate - the Class input object is instantiated
 * <li>Map - returns an object from a supplied Map
 * <li>Null - always returns null
 * <li>NOP - returns the input object, which should be immutable
 * <li>Exception - always throws an exception
 * <li>StringValue - returns a <code>java.lang.String</code> representation of the input object
 * </ul>
 * All the supplied transformers are Serializable.
 *
 * @version $Id: TransformerUtility.java,v 1.2 2008/05/14 09:32:29 swd\stefan.damian Exp $
 */
public class TransformerUtility
{

	/**
	 * This class is not normally instantiated.
	 */
	public TransformerUtility()
	{
		super();
	}

	/**
	 * Gets a transformer that always throws an exception.
	 * This could be useful during testing as a placeholder.
	 *
	 * @return the transformer
	 * @see org.areasy.common.data.workers.functors.ExceptionTransformer
	 */
	public static Transformer exceptionTransformer()
	{
		return ExceptionTransformer.INSTANCE;
	}

	/**
	 * Gets a transformer that always returns null.
	 *
	 * @return the transformer
	 * @see org.areasy.common.data.workers.functors.ConstantTransformer
	 */
	public static Transformer nullTransformer()
	{
		return ConstantTransformer.NULL_INSTANCE;
	}

	/**
	 * Gets a transformer that returns the input object.
	 * The input object should be immutable to maintain the
	 * contract of Transformer (although this is not checked).
	 *
	 * @return the transformer
	 * @see org.areasy.common.data.workers.functors.NOPTransformer
	 */
	public static Transformer nopTransformer()
	{
		return NOPTransformer.INSTANCE;
	}

	/**
	 * Gets a transformer that returns a clone of the input
	 * object. The input object will be cloned using one of these
	 * techniques (in order):
	 * <ul>
	 * <li>public clone method
	 * <li>public copy constructor
	 * <li>serialization clone
	 * <ul>
	 *
	 * @return the transformer
	 * @see org.areasy.common.data.workers.functors.CloneTransformer
	 */
	public static Transformer cloneTransformer()
	{
		return CloneTransformer.INSTANCE;
	}

	/**
	 * Creates a Transformer that will return the same object each time the
	 * transformer is used.
	 *
	 * @param constantToReturn the constant object to return each time in the transformer
	 * @return the transformer.
	 * @see org.areasy.common.data.workers.functors.ConstantTransformer
	 */
	public static Transformer constantTransformer(Object constantToReturn)
	{
		return ConstantTransformer.getInstance(constantToReturn);
	}

	/**
	 * Creates a Transformer that calls a Closure each time the transformer is used.
	 * The transformer returns the input object.
	 *
	 * @param closure the closure to run each time in the transformer, not null
	 * @return the transformer
	 * @throws IllegalArgumentException if the closure is null
	 * @see org.areasy.common.data.workers.functors.ClosureTransformer
	 */
	public static Transformer asTransformer(Closure closure)
	{
		return ClosureTransformer.getInstance(closure);
	}

	/**
	 * Creates a Transformer that calls a Predicate each time the transformer is used.
	 * The transformer will return either Boolean.TRUE or Boolean.FALSE.
	 *
	 * @param predicate the predicate to run each time in the transformer, not null
	 * @return the transformer
	 * @throws IllegalArgumentException if the predicate is null
	 * @see org.areasy.common.data.workers.functors.PredicateTransformer
	 */
	public static Transformer asTransformer(Predicate predicate)
	{
		return PredicateTransformer.getInstance(predicate);
	}

	/**
	 * Creates a Transformer that calls a Factory each time the transformer is used.
	 * The transformer will return the value returned by the factory.
	 *
	 * @param factory the factory to run each time in the transformer, not null
	 * @return the transformer
	 * @throws IllegalArgumentException if the factory is null
	 * @see org.areasy.common.data.workers.functors.FactoryTransformer
	 */
	public static Transformer asTransformer(Factory factory)
	{
		return FactoryTransformer.getInstance(factory);
	}

	/**
	 * Create a new Transformer that calls two transformers, passing the result of
	 * the first into the second.
	 *
	 * @param transformer1 the first transformer
	 * @param transformer2 the second transformer
	 * @return the transformer
	 * @throws IllegalArgumentException if either transformer is null
	 * @see org.areasy.common.data.workers.functors.ChainedTransformer
	 */
	public static Transformer chainedTransformer(Transformer transformer1, Transformer transformer2)
	{
		return ChainedTransformer.getInstance(transformer1, transformer2);
	}

	/**
	 * Create a new Transformer that calls each transformer in turn, passing the
	 * result into the next transformer.
	 *
	 * @param transformers an array of transformers to chain
	 * @return the transformer
	 * @throws IllegalArgumentException if the transformers array is null
	 * @throws IllegalArgumentException if any transformer in the array is null
	 * @see org.areasy.common.data.workers.functors.ChainedTransformer
	 */
	public static Transformer chainedTransformer(Transformer[] transformers)
	{
		return ChainedTransformer.getInstance(transformers);
	}

	/**
	 * Create a new Transformer that calls each transformer in turn, passing the
	 * result into the next transformer. The ordering is that of the iterator()
	 * method on the collection.
	 *
	 * @param transformers a collection of transformers to chain
	 * @return the transformer
	 * @throws IllegalArgumentException if the transformers collection is null
	 * @throws IllegalArgumentException if any transformer in the collection is null
	 * @see org.areasy.common.data.workers.functors.ChainedTransformer
	 */
	public static Transformer chainedTransformer(Collection transformers)
	{
		return ChainedTransformer.getInstance(transformers);
	}

	/**
	 * Create a new Transformer that calls one of two transformers depending
	 * on the specified predicate.
	 *
	 * @param predicate        the predicate to switch on
	 * @param trueTransformer  the transformer called if the predicate is true
	 * @param falseTransformer the transformer called if the predicate is false
	 * @return the transformer
	 * @throws IllegalArgumentException if the predicate is null
	 * @throws IllegalArgumentException if either transformer is null
	 * @see org.areasy.common.data.workers.functors.SwitchTransformer
	 */
	public static Transformer switchTransformer(Predicate predicate, Transformer trueTransformer, Transformer falseTransformer)
	{
		return SwitchTransformer.getInstance(new Predicate[]{predicate}, new Transformer[]{trueTransformer}, falseTransformer);
	}

	/**
	 * Create a new Transformer that calls one of the transformers depending
	 * on the predicates. The transformer at array location 0 is called if the
	 * predicate at array location 0 returned true. Each predicate is evaluated
	 * until one returns true. If no predicates evaluate to true, null is returned.
	 *
	 * @param predicates   an array of predicates to check
	 * @param transformers an array of transformers to call
	 * @return the transformer
	 * @throws IllegalArgumentException if the either array is null
	 * @throws IllegalArgumentException if the either array has 0 elements
	 * @throws IllegalArgumentException if any element in the arrays is null
	 * @throws IllegalArgumentException if the arrays are different sizes
	 * @see org.areasy.common.data.workers.functors.SwitchTransformer
	 */
	public static Transformer switchTransformer(Predicate[] predicates, Transformer[] transformers)
	{
		return SwitchTransformer.getInstance(predicates, transformers, null);
	}

	/**
	 * Create a new Transformer that calls one of the transformers depending
	 * on the predicates. The transformer at array location 0 is called if the
	 * predicate at array location 0 returned true. Each predicate is evaluated
	 * until one returns true. If no predicates evaluate to true, the default
	 * transformer is called. If the default transformer is null, null is returned.
	 *
	 * @param predicates         an array of predicates to check
	 * @param transformers       an array of transformers to call
	 * @param defaultTransformer the default to call if no predicate matches, null means return null
	 * @return the transformer
	 * @throws IllegalArgumentException if the either array is null
	 * @throws IllegalArgumentException if the either array has 0 elements
	 * @throws IllegalArgumentException if any element in the arrays is null
	 * @throws IllegalArgumentException if the arrays are different sizes
	 * @see org.areasy.common.data.workers.functors.SwitchTransformer
	 */
	public static Transformer switchTransformer(Predicate[] predicates, Transformer[] transformers, Transformer defaultTransformer)
	{
		return SwitchTransformer.getInstance(predicates, transformers, defaultTransformer);
	}

	/**
	 * Create a new Transformer that calls one of the transformers depending
	 * on the predicates.
	 * <p/>
	 * The Map consists of Predicate keys and Transformer values. A transformer
	 * is called if its matching predicate returns true. Each predicate is evaluated
	 * until one returns true. If no predicates evaluate to true, the default
	 * transformer is called. The default transformer is set in the map with a
	 * null key. If no default transformer is set, null will be returned in a default
	 * case. The ordering is that of the iterator() method on the entryset collection
	 * of the map.
	 *
	 * @param predicatesAndTransformers a map of predicates to transformers
	 * @return the transformer
	 * @throws IllegalArgumentException if the map is null
	 * @throws IllegalArgumentException if the map is empty
	 * @throws IllegalArgumentException if any transformer in the map is null
	 * @throws ClassCastException       if the map elements are of the wrong type
	 * @see org.areasy.common.data.workers.functors.SwitchTransformer
	 */
	public static Transformer switchTransformer(Map predicatesAndTransformers)
	{
		return SwitchTransformer.getInstance(predicatesAndTransformers);
	}

	/**
	 * Create a new Transformer that uses the input object as a key to find the
	 * transformer to call.
	 * <p/>
	 * The Map consists of object keys and Transformer values. A transformer
	 * is called if the input object equals the key. If there is no match, the
	 * default transformer is called. The default transformer is set in the map
	 * using a null key. If no default is set, null will be returned in a default case.
	 *
	 * @param objectsAndTransformers a map of objects to transformers
	 * @return the transformer
	 * @throws IllegalArgumentException if the map is null
	 * @throws IllegalArgumentException if the map is empty
	 * @throws IllegalArgumentException if any transformer in the map is null
	 * @see org.areasy.common.data.workers.functors.SwitchTransformer
	 */
	public static Transformer switchMapTransformer(Map objectsAndTransformers)
	{
		Transformer[] trs = null;
		Predicate[] preds = null;
		if (objectsAndTransformers == null) throw new IllegalArgumentException("The object and transformer map must not be null");

		Transformer def = (Transformer) objectsAndTransformers.remove(null);

		int size = objectsAndTransformers.size();
		trs = new Transformer[size];
		preds = new Predicate[size];
		int i = 0;

		for (Iterator it = objectsAndTransformers.entrySet().iterator(); it.hasNext();)
		{
			Map.Entry entry = (Map.Entry) it.next();
			preds[i] = EqualPredicate.getInstance(entry.getKey());
			trs[i] = (Transformer) entry.getValue();
			i++;
		}

		return switchTransformer(preds, trs, def);
	}

	/**
	 * Gets a Transformer that expects an input Class object that it will instantiate.
	 *
	 * @return the transformer
	 * @see org.areasy.common.data.workers.functors.InstantiateTransformer
	 */
	public static Transformer instantiateTransformer()
	{
		return InstantiateTransformer.NO_ARG_INSTANCE;
	}

	/**
	 * Creates a Transformer that expects an input Class object that it will
	 * instantiate. The constructor used is determined by the arguments specified
	 * to this method.
	 *
	 * @param paramTypes parameter types for the constructor, can be null
	 * @param args       the arguments to pass to the constructor, can be null
	 * @return the transformer
	 * @throws IllegalArgumentException if the paramTypes and args don't match
	 * @see org.areasy.common.data.workers.functors.InstantiateTransformer
	 */
	public static Transformer instantiateTransformer(Class[] paramTypes, Object[] args)
	{
		return InstantiateTransformer.getInstance(paramTypes, args);
	}

	/**
	 * Creates a Transformer that uses the passed in Map to transform the input
	 * object (as a simple lookup).
	 *
	 * @param map the map to use to transform the objects
	 * @return the transformer
	 * @throws IllegalArgumentException if the map is null
	 * @see org.areasy.common.data.workers.functors.MapTransformer
	 */
	public static Transformer mapTransformer(Map map)
	{
		return MapTransformer.getInstance(map);
	}

	/**
	 * Gets a Transformer that invokes a method on the input object.
	 * The method must have no parameters. If the input object is null,
	 * null is returned.
	 * <p/>
	 * For example, <code>TransformerUtility.invokerTransformer("getName");</code>
	 * will call the <code>getName/code> method on the input object to
	 * determine the transformer result.
	 *
	 * @param methodName the method name to call on the input object, may not be null
	 * @return the transformer
	 * @throws IllegalArgumentException if the methodName is null.
	 * @see org.areasy.common.data.workers.functors.InvokerTransformer
	 */
	public static Transformer invokerTransformer(String methodName)
	{
		return InvokerTransformer.getInstance(methodName, null, null);
	}

	/**
	 * Gets a Transformer that invokes a method on the input object.
	 * The method parameters are specified. If the input object is null,
	 * null is returned.
	 *
	 * @param methodName the name of the method
	 * @param paramTypes the parameter types
	 * @param args       the arguments
	 * @return the transformer
	 * @throws IllegalArgumentException if the method name is null
	 * @throws IllegalArgumentException if the paramTypes and args don't match
	 * @see org.areasy.common.data.workers.functors.InvokerTransformer
	 */
	public static Transformer invokerTransformer(String methodName, Class[] paramTypes, Object[] args)
	{
		return InvokerTransformer.getInstance(methodName, paramTypes, args);
	}

	/**
	 * Gets a transformer that returns a <code>java.lang.String</code>
	 * representation of the input object. This is achieved via the
	 * <code>toString</code> method, <code>null</code> returns 'null'.
	 *
	 * @return the transformer
	 * @see org.areasy.common.data.workers.functors.StringValueTransformer
	 */
	public static Transformer stringValueTransformer()
	{
		return StringValueTransformer.INSTANCE;
	}

}
