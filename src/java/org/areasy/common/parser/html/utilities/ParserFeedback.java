package org.areasy.common.parser.html.utilities;

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

/**
 * Interface for providing feedback without forcing the output
 * destination to be predefined. A default implementation is
 * provided to output events to the console but alternate
 * implementations that log, watch for specific messages, etc.
 * are also possible.
 *
 * @see DefaultParserFeedback
 * @see FeedbackManager
 *
 * @version $Id: ParserFeedback.java,v 1.1 2008/05/25 17:26:04 swd\stefan.damian Exp $
 */
public interface ParserFeedback
{

    public void info(String message);

    public void warning(String message);

    public void error(String message, ParserException e);
}

