package org.areasy.common.velocity.base.event;

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


/**
 * Reference 'Stream insertion' event handler.  Called with object
 * that will be inserted into stream via value.toString().
 * <p/>
 * Please return an Object that will toString() nicely :)
 *
 * @version $Id: ReferenceInsertionEventHandler.java,v 1.1 2008/05/25 22:33:15 swd\stefan.damian Exp $
 */
public interface ReferenceInsertionEventHandler extends EventHandler
{
	/**
	 * A call-back which is executed during Velocity merge before a
	 * reference value is inserted into the output stream.
	 *
	 * @param reference Reference from template about to be inserted.
	 * @param value     Value about to be inserted (after its
	 *                  <code>toString()</code> method is called).
	 * @return Object on which <code>toString()</code> should be
	 *         called for output.
	 */
	public Object referenceInsert(String reference, Object value);
}
