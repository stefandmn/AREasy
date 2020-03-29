package org.areasy.common.parser.html.utilities;

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

import org.areasy.common.parser.html.engine.Node;
import org.areasy.common.parser.html.engine.lexer.Cursor;
import org.areasy.common.parser.html.engine.lexer.Lexer;
import org.areasy.common.parser.html.engine.scanners.Scanner;
import org.areasy.common.parser.html.engine.tags.Tag;

/**
 * @version $Id: DefaultIterator.java,v 1.1 2008/05/25 17:26:04 swd\stefan.damian Exp $
 */
public class DefaultIterator implements NodeIterator
{

    Lexer mLexer;
    ParserFeedback mFeedback;
    Cursor mCursor;

    public DefaultIterator(Lexer lexer, ParserFeedback fb)
    {
        mLexer = lexer;
        mFeedback = fb;
        mCursor = new Cursor(mLexer.getPage(), 0);
    }

    /**
     * Check if more nodes are available.
     *
     * @return <code>true</code> if a call to <code>nextNode()</code> will succeed.
     */
    public boolean hasMoreNodes() throws ParserException
    {
        boolean ret;

        mCursor.setPosition(mLexer.getPosition());
        ret = 0 != mLexer.getPage().getCharacter(mCursor); // more characters?

        return (ret);
    }

    /**
     * Get the next node.
     *
     * @return The next node in the HTML stream, or null if there are no more nodes.
     * @throws ParserException If an unrecoverable error occurs.
     */
    public Node nextNode() throws ParserException
    {
        Tag tag;
        Scanner scanner;
        NodeList stack;
        Node ret;

        try
        {
            ret = mLexer.nextNode();
            if(null != ret)
            {
                // kick off recursion for the top level node
                if(ret instanceof Tag)
                {
                    tag = (Tag) ret;
                    if(!tag.isEndTag())
                    {
                        // now recurse if there is a scanner for this type of tag
                        scanner = tag.getThisScanner();
                        if(null != scanner)
                        {
                            stack = new NodeList();
                            ret = scanner.scan(tag, mLexer, stack);
                        }
                    }
                }
            }
        }
        catch(ParserException pe)
        {
            throw pe; // no need to wrap an existing ParserException
        }
        catch(Exception e)
        {
            StringBuffer msgBuffer = new StringBuffer();
            msgBuffer.append("Unexpected Exception occurred while reading ");
            msgBuffer.append(mLexer.getPage().getUrl());
            msgBuffer.append(", in nextNode");

            ParserException ex = new ParserException(msgBuffer.toString(), e);
            mFeedback.error(msgBuffer.toString(), ex);
            throw ex;
        }

        return (ret);
    }
}
