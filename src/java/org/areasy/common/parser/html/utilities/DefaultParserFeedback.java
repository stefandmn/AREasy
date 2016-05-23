package org.areasy.common.parser.html.utilities;

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

import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;

import java.io.Serializable;

/**
 * Default implementation of the HTMLParserFeedback interface.
 * This implementation prints output to the console but users
 * can implement their own classes to support alternate behavior.
 *
 * @see ParserFeedback
 * @see FeedbackManager
 *
 * @version $Id: DefaultParserFeedback.java,v 1.1 2008/05/25 17:26:05 swd\stefan.damian Exp $
 */
public class DefaultParserFeedback implements ParserFeedback, Serializable
{

    /**
     * The logger
     */
    private static Logger logger = LoggerFactory.getLog(DefaultParserFeedback.class);

    /**
     * Constructor argument for a quiet feedback.
     */
    public static final int QUIET = 0;

    /**
     * Constructor argument for a normal feedback.
     */
    public static final int NORMAL = 1;

    /**
     * Constructor argument for a debugging feedback.
     */
    public static final int DEBUG = 2;

    /**
     * Verbosity level.
     * Corresponds to constructor arguments:
     * <pre>
     *   DEBUG = 2;
     *   NORMAL = 1;
     *   QUIET = 0;
     * </pre>
     */
    protected int mMode;

    /**
     * Construct a feedback object of the given type.
     *
     * @param mode The type of feedback:
     * <pre>
     *   DEBUG - verbose debugging with stack traces
     *   NORMAL - normal messages
     *   QUIET - no messages
     * </pre>
     * @throws IllegalArgumentException if mode is not QUIET, NORMAL or DEBUG.
     */
    public DefaultParserFeedback(int mode)
    {
        if(mode < QUIET || mode > DEBUG) throw new IllegalArgumentException("Illegal mode (" + mode + "), must be one of: QUIET, NORMAL, DEBUG");

        mMode = mode;
    }

    /**
     * Construct a NORMAL feedback object.
     */
    public DefaultParserFeedback()
    {
        this(NORMAL);
    }

    /**
     * Print an info message.
     *
     * @param message The message to print.
     */
    public void info(String message)
    {
        if(QUIET != mMode) logger.info(message);
    }

    /**
     * Print an warning message.
     *
     * @param message The message to print.
     */
    public void warning(String message)
    {
        if(QUIET != mMode) logger.warn(message);
    }

    /**
     * Print an error message.
     *
     * @param message   The message to print.
     * @param exception The exception for stack tracing.
     */
    public void error(String message, ParserException exception)
    {
        if(QUIET != mMode)
        {
            logger.error(message);
            if(DEBUG == mMode && (null != exception)) logger.debug(exception);
        }
    }
}

