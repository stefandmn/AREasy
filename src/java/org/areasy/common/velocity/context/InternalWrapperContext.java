package org.areasy.common.velocity.context;

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
 * interface for internal context wrapping functionality
 *
 * @version $Id: InternalWrapperContext.java,v 1.1 2008/05/25 22:33:10 swd\stefan.damian Exp $
 */
public interface InternalWrapperContext
{
	/**
	 * returns the wrapped user context
	 */
	public Context getInternalUserContext();

	/**
	 * returns the base full context impl
	 */
	public InternalContextAdapter getBaseContext();

}
