package org.areasy.common.support.component.lifecycle;

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
 * The Disposable interface is used when components need to
 * deallocate and dispose resources prior to their destruction.
 */
public interface Disposable
{
	/**
	 * The dispose operation is called at the end of a components lifecycle.
	 * This method will be called after Startable.stop() method (if implemented
	 * by component). Components use this method to release and destroy any
	 * resources that the Component owns.
	 */
	public abstract void dispose();
}
