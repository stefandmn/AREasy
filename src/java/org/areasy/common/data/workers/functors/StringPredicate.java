package org.areasy.common.data.workers.functors;

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

import java.io.Serializable;
import java.util.regex.Pattern;

/**
 * Predicate implementation that returns true if the input fits on string comparison or
 * regular expressions. The predicate indicate the type of operation that should be applied on: <br/>
 * - 0 = starts with <br/>
 * - 1 = contains <br/>
 * - 2 = regular expression <br/>
 *
 * @version $Id: EqualPredicate.java,v 1.2 2008/05/14 09:32:32 swd\stefan.damian Exp $
 */
public final class StringPredicate implements Predicate, Serializable
{
	/**
	 * The value to compare to
	 */
	private String keyword = null;
	private int operation = 0;
	private Pattern pattern = null;

	/**
	 * Factory to create the identity predicate.
	 *
	 * @param keyword the object to compare to
	 * @return the predicate
	 * @throws IllegalArgumentException if the predicate is null
	 */
	public static Predicate getInstance(String keyword)
	{
		return getInstance(keyword, 0);
	}

	/**
	 * Factory to create the identity predicate.
	 *
	 * @param keyword the object to compare to
	 * @return the predicate
	 * @throws IllegalArgumentException if the predicate is null
	 */
	public static Predicate getInstance(String keyword, int operation)
	{
		if (keyword == null)
		{
			return NullPredicate.INSTANCE;
		}

		return new StringPredicate(keyword, operation);
	}

	/**
	 * Constructor that performs no validation.
	 * Use <code>getInstance</code> if you want that.
	 *
	 * @param keywork the object to compare to
	 */
	public StringPredicate(String keywork, int operation)
	{
		super();
		this.keyword = keywork;
		this.operation = operation;

		if(operation == 2) this.pattern = Pattern.compile(keywork);
	}

	/**
	 * Evaluates the predicate returning true if the input equals the stored value.
	 *
	 * @param object the input object
	 * @return true if input object equals stored value
	 */
	public boolean evaluate(Object object)
	{
		String signature  = object instanceof String ? (String)object : object.toString();

		switch(this.operation)
		{
			case 2:
				return pattern.matcher(signature).matches();
			case 1:
				return signature.contains(keyword);
			case 0:
				return signature.startsWith(keyword);
			default:
				return false;
		}
	}

	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public Object getValue()
	{
		return this.pattern;
	}

}
