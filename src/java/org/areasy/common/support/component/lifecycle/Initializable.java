package org.areasy.common.support.component.lifecycle;

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
 * The Initializable interface is used by components that need to
 * allocate resources prior to them becoming active.
 *
 * @version $Id: Initializable.java,v 1.2 2008/05/14 09:32:39 swd\stefan.damian Exp $
 */

public interface Initializable
{

	/**
	 * Initialialize the component. Initialization includes
	 * allocating any resources required throughout the
	 * components lifecycle.
	 *
	 * @throws Exception if an error occurs
	 */
	public abstract void initialize() throws Exception;
}
