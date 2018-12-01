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
 * The encoding is changed invalidating already scanned characters.
 * When the encoding is changed, as for example when encountering a &lt;META&gt;
 * tag that includes a charset directive in the content attribute that
 * disagrees with the encoding specified by the HTTP header (or the default
 * encoding if none), the parser retraces the bytes it has interpreted so far
 * comparing the characters produced under the new encoding. If the new
 * characters differ from those it has already yielded to the application, it
 * throws this exception to indicate that processing should be restarted under
 * the new encoding.
 * This exception is the object thrown so that applications may distinguish
 * between an encoding change, which may be successfully cured by restarting
 * the parse from the beginning, from more serious errors.
 *
 * @see DefaultIterator
 * @see ParserException
 *
 * @version $Id: EncodingChangeException.java,v 1.1 2008/05/25 17:26:04 swd\stefan.damian Exp $
 */
public class EncodingChangeException extends ParserException
{

    /**
     * Create an exception idicative of a problematic encoding change.
     *
     * @param message The message describing the error condifion.
     */
    public EncodingChangeException(String message)
    {
        super(message);
    }
}

