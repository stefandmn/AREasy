package org.areasy.common.data.type;

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

import java.util.Iterator;

/**
 * Defines an iterator that can be reset back to an initial state.
 * <p/>
 * This interface allows an iterator to be repeatedly reused.
 *
 * @version $Id: ResettableIterator.java,v 1.2 2008/05/14 09:32:34 swd\stefan.damian Exp $
 */
public interface ResettableIterator extends Iterator
{

	/**
	 * Resets the iterator back to the position at which the iterator
	 * was created.
	 */
	public void reset();

}
