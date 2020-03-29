package org.areasy.common.velocity.context;

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

import org.areasy.common.velocity.base.event.EventCartridge;

/**
 * Interface for event support.  Note that this is a public internal
 * interface, as it is something that will be accessed from outside
 * of the .context package.
 *
 * @version $Id: InternalEventContext.java,v 1.1 2008/05/25 22:33:09 swd\stefan.damian Exp $
 */
public interface InternalEventContext
{
	public EventCartridge attachEventCartridge(EventCartridge ec);

	public EventCartridge getEventCartridge();
}
