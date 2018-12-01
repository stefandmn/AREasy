package org.areasy.common.data.type.collection;

/*
 * Copyright (c) 2007-2018 AREasy Runtime
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

import java.util.Collection;
import java.util.Iterator;

/**
 * Decorates another <code>Collection</code> to validate that additions
 * match a specified predicate.
 * <p/>
 * This collection exists to provide validation for the decorated collection.
 * It is normally created to decorate an empty collection.
 * If an object cannot be added to the collection, an IllegalArgumentException is thrown.
 * <p/>
 * One usage would be to ensure that no null entries are added to the collection.
 * <pre>Collection coll = PredicatedCollection.decorate(new ArrayList(), NotNullPredicate.INSTANCE);</pre>
 *
 * @version $Id: PredicatedCollection.java,v 1.2 2008/05/14 09:32:34 swd\stefan.damian Exp $
 */
public class PredicatedCollection extends AbstractSerializableCollectionDecorator
{
	/**
	 * The predicate to use
	 */
	protected final Predicate predicate;

	/**
	 * Factory method to create a predicated (validating) collection.
	 * <p/>
	 * If there are any elements already in the collection being decorated, they
	 * are validated.
	 *
	 * @param coll      the collection to decorate, must not be null
	 * @param predicate the predicate to use for validation, must not be null
	 * @return a new predicated collection
	 * @throws IllegalArgumentException if collection or predicate is null
	 * @throws IllegalArgumentException if the collection contains invalid elements
	 */
	public static Collection decorate(Collection coll, Predicate predicate)
	{
		return new PredicatedCollection(coll, predicate);
	}

	/**
	 * Constructor that wraps (not copies).
	 * <p/>
	 * If there are any elements already in the collection being decorated, they
	 * are validated.
	 *
	 * @param coll      the collection to decorate, must not be null
	 * @param predicate the predicate to use for validation, must not be null
	 * @throws IllegalArgumentException if collection or predicate is null
	 * @throws IllegalArgumentException if the collection contains invalid elements
	 */
	protected PredicatedCollection(Collection coll, Predicate predicate)
	{
		super(coll);
		if (predicate == null)
		{
			throw new IllegalArgumentException("Predicate must not be null");
		}
		this.predicate = predicate;
		for (Iterator it = coll.iterator(); it.hasNext();)
		{
			validate(it.next());
		}
	}

	/**
	 * Validates the object being added to ensure it matches the predicate.
	 * <p/>
	 * The predicate itself should not throw an exception, but return false to
	 * indicate that the object cannot be added.
	 *
	 * @param object the object being added
	 * @throws IllegalArgumentException if the add is invalid
	 */
	protected void validate(Object object)
	{
		if (predicate.evaluate(object) == false)
		{
			throw new IllegalArgumentException("Cannot add Object '" + object + "' - Predicate rejected it");
		}
	}

	/**
	 * Override to validate the object being added to ensure it matches
	 * the predicate.
	 *
	 * @param object the object being added
	 * @return the result of adding to the underlying collection
	 * @throws IllegalArgumentException if the add is invalid
	 */
	public boolean add(Object object)
	{
		validate(object);
		return getCollection().add(object);
	}

	/**
	 * Override to validate the objects being added to ensure they match
	 * the predicate. If any one fails, no update is made to the underlying
	 * collection.
	 *
	 * @param coll the collection being added
	 * @return the result of adding to the underlying collection
	 * @throws IllegalArgumentException if the add is invalid
	 */
	public boolean addAll(Collection coll)
	{
		for (Iterator it = coll.iterator(); it.hasNext();)
		{
			validate(it.next());
		}
		return getCollection().addAll(coll);
	}

}
