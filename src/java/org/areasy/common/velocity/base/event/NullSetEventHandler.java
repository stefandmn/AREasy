package org.areasy.common.velocity.base.event;

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


/**
 * Event handler : lets an app approve / veto
 * writing a log message when RHS of #set() is null.
 *
 * @version $Id: NullSetEventHandler.java,v 1.1 2008/05/25 22:33:15 swd\stefan.damian Exp $
 */
public interface NullSetEventHandler extends EventHandler
{
	/**
	 * Called when the RHS of a #set() is null, which will result
	 * in a null LHS.
	 *
	 * @param lhs reference literal of left-hand-side of set statement
	 * @param rhs reference literal of right-hand-side of set statement
	 * @return true if log message should be written, false otherwise
	 */
	public boolean shouldLogOnNullSet(String lhs, String rhs);
}
