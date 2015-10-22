package org.areasy.common.data;

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

import org.areasy.common.errors.NestableRuntimeException;

/**
 * <p>Thrown when it is impossible or undesirable to consume or throw a checked exception.</p>
 * This exception supplements the standard exception classes by providing a more
 * semantically rich description of the problem.</p>
 *
 * <p><code>UnhandledException</code> represents the case where a method has to deal
 * with a checked exception but does not wish to.
 * Instead, the checked exception is rethrown in this unchecked wrapper.</p>
 *
 * <pre>
 * public void foo() {
 *   try {
 *     // do something that throws IOException
 *   } catch (IOException ex) {
 *     // don't want to or can't throw IOException from foo()
 *     throw new UnhandledException(ex);
 *   }
 * }
 * </pre>
 */
public class UnhandledException extends NestableRuntimeException
{
    /**
     * Constructs the exception using a cause.
     *
     * @param cause  the underlying cause
     */
    public UnhandledException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs the exception using a message and cause.
     *
     * @param message  the message to use
     * @param cause  the underlying cause
     */
    public UnhandledException(String message, Throwable cause) {
        super(message, cause);
    }
}

