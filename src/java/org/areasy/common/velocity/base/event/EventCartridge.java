package org.areasy.common.velocity.base.event;

/*
 * Copyright (c) 2007-2015 AREasy Runtime
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

import org.areasy.common.velocity.context.Context;
import org.areasy.common.velocity.context.InternalEventContext;

/**
 * 'Package' of event handlers...
 *
 * @version $Id: EventCartridge.java,v 1.1 2008/05/25 22:33:15 swd\stefan.damian Exp $
 */
public class EventCartridge implements ReferenceInsertionEventHandler, NullSetEventHandler, MethodExceptionEventHandler
{
	private ReferenceInsertionEventHandler rieh = null;
	private NullSetEventHandler nseh = null;
	private MethodExceptionEventHandler meeh = null;

	/**
	 * Adds an event handler(s) to the Cartridge.  This method
	 * will find all possible event handler interfaces supported
	 * by the passed in object.
	 *
	 * @param ev object impementing a valid EventHandler-derived interface
	 * @return true if a supported interface, false otherwise or if null
	 */
	public boolean addEventHandler(EventHandler ev)
	{
		if (ev == null)
		{
			return false;
		}

		boolean found = false;

		if (ev instanceof ReferenceInsertionEventHandler)
		{
			rieh = (ReferenceInsertionEventHandler) ev;
			found = true;
		}

		if (ev instanceof NullSetEventHandler)
		{
			nseh = (NullSetEventHandler) ev;
			found = true;
		}

		if (ev instanceof MethodExceptionEventHandler)
		{
			meeh = (MethodExceptionEventHandler) ev;
			found = true;
		}

		return found;
	}

	/**
	 * Removes an event handler(s) from the Cartridge.  This method
	 * will find all possible event handler interfaces supported
	 * by the passed in object and remove them.
	 *
	 * @param ev object impementing a valid EventHandler-derived interface
	 * @return true if a supported interface, false otherwise or if null
	 */
	public boolean removeEventHandler(EventHandler ev)
	{
		if (ev == null)
		{
			return false;
		}

		boolean found = false;

		if (ev == rieh)
		{
			rieh = null;
			found = true;
		}

		if (ev == nseh)
		{
			nseh = null;
			found = true;
		}

		if (ev == meeh)
		{
			meeh = null;
			found = true;
		}

		return found;
	}

	/**
	 * Implementation of ReferenceInsertionEventHandler method
	 * <code>referenceInsert()</code>.
	 * <p/>
	 * Called during Velocity merge before a reference value will
	 * be inserted into the output stream.
	 *
	 * @param reference reference from template about to be inserted
	 * @param value     value about to be inserted (after toString() )
	 * @return Object on which toString() should be called for output.
	 */
	public Object referenceInsert(String reference, Object value)
	{
		if (rieh == null)
		{
			return value;
		}

		return rieh.referenceInsert(reference, value);
	}

	/**
	 * Implementation of NullSetEventHandler method
	 * <code>shouldLogOnNullSet()</code>.
	 * <p/>
	 * Called during Velocity merge to determine if when
	 * a #set() results in a null assignment, a warning
	 * is logged.
	 *
	 * @return true if to be logged, false otherwise
	 */
	public boolean shouldLogOnNullSet(String lhs, String rhs)
	{
		if (nseh == null)
		{
			return true;
		}

		return nseh.shouldLogOnNullSet(lhs, rhs);
	}

	/**
	 * Implementation of MethodExceptionEventHandler  method
	 * <code>methodException()</code>.
	 * <p/>
	 * Called during Velocity merge if a reference is null
	 *
	 * @param claz   Class that is causing the exception
	 * @param method method called that causes the exception
	 * @param e      Exception thrown by the method
	 * @return Object to return as method result
	 */
	public Object methodException(Class claz, String method, Exception e)
			throws Exception
	{
		/*
		 *  if we don't have a handler, just throw what we were handed
		 */
		if (meeh == null)
		{
			throw e;
		}

		/*
		 *  otherwise, call it..
		 */
		return meeh.methodException(claz, method, e);
	}

	/**
	 * Attached the EventCartridge to the context
	 * <p/>
	 * Final because not something one should mess with lightly :)
	 *
	 * @param context context to attach to
	 * @return true if successful, false otherwise
	 */
	public final boolean attachToContext(Context context)
	{
		if (context instanceof InternalEventContext)
		{
			InternalEventContext iec = (InternalEventContext) context;

			iec.attachEventCartridge(this);

			return true;
		}
		else
		{
			return false;
		}
	}
}
