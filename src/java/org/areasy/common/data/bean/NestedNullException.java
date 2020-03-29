package org.areasy.common.data.bean;

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

/**
 * Thrown to indicate that the <em>Bean Access Language</em> cannot execute query
 * against given bean since a nested bean referenced is null.
 *
 * @version $Id: NestedNullException.java,v 1.2 2008/05/14 09:32:37 swd\stefan.damian Exp $
 */

public class NestedNullException extends BeanAccessLanguageException
{
	/**
	 * Constructs a <code>NestedNullException</code> without a detail message.
	 */
	public NestedNullException()
	{
		super();
	}

	/**
	 * Constructs a <code>NestedNullException</code> without a detail message.
	 *
	 * @param message the detail message explaining this exception
	 */
	public NestedNullException(String message)
	{
		super(message);
	}
}
