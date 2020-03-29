package org.areasy.common.velocity.runtime.directive;

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

import org.areasy.common.velocity.runtime.parser.ParseException;

/**
 * Exception to indicate problem happened while constructing #macro()
 * <p/>
 * For internal use in parser - not to be passed to app level
 *
 * @version $Id: MacroParseException.java,v 1.1 2008/05/25 22:33:14 swd\stefan.damian Exp $
 */
public class MacroParseException extends ParseException
{
	public MacroParseException(String msg)
	{
		super(msg);
	}
}
