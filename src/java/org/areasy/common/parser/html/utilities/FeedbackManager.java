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
 * Implementaiton of static methods that allow the parser to
 * route various messages to any implementation of the
 * HTMLParserFeedback interface. End users can use the default
 * DefaultHTMLParserFeedback or may provide their own by calling
 * the setParserFeedback method.
 *
 * @see ParserFeedback
 * @see DefaultParserFeedback
 *
 * @version $Id: FeedbackManager.java,v 1.1 2008/05/25 17:26:04 swd\stefan.damian Exp $
 */

public class FeedbackManager
{

    protected static ParserFeedback callback = new DefaultParserFeedback();

    public static void setParserFeedback(ParserFeedback feedback)
    {
        callback = feedback;
    }

    public static void info(String message)
    {
        callback.info(message);
    }

    public static void warning(String message)
    {
        callback.warning(message);
    }

    public static void error(String message, ParserException e)
    {
        callback.error(message, e);
    }
}
