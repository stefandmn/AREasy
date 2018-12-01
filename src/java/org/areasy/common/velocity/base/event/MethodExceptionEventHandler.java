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
 * Called when a method throws an exception.  This gives the
 * application a chance to deal with it and either
 * return something nice, or throw.
 * <p/>
 * Please return what you want rendered into the output stream.
 *
 * @version $Id: MethodExceptionEventHandler.java,v 1.1 2008/05/25 22:33:15 swd\stefan.damian Exp $
 */
public interface MethodExceptionEventHandler extends EventHandler
{
	public Object methodException(Class claz, String method, Exception e) throws Exception;
}
