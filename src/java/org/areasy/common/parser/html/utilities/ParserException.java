package org.areasy.common.parser.html.utilities;

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
 * Library-specific support for chained exceptions.
 *
 * @see ChainedException
 *
 * @version $Id: ParserException.java,v 1.1 2008/05/25 17:26:04 swd\stefan.damian Exp $
 */
public class ParserException extends ChainedException
{

    public ParserException()
    {
    }

    public ParserException(String message)
    {
        super(message);
    }

    public ParserException(Throwable throwable)
    {
        super(throwable);
    }

    public ParserException(String message, Throwable throwable)
    {
        super(message, throwable);
    }
}

