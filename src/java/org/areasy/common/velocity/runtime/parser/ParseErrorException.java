package org.areasy.common.velocity.runtime.parser;

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

import org.areasy.common.velocity.VelocityException;

/**
 * Application-level exception thrown when a resource of any type
 * has a syntax or other error which prevents it from being parsed.
 * <br>
 * When this resource is thrown, a best effort will be made to have
 * useful information in the exception's message.  For complete
 * information, consult the runtime log.
 *
 * @version $Id: ParseErrorException.java,v 1.1 2008/05/25 22:33:13 swd\stefan.damian Exp $
 */
public class ParseErrorException extends VelocityException
{
	public ParseErrorException(String exceptionMessage)
	{
		super(exceptionMessage);
	}
}
