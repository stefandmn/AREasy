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

import org.areasy.common.parser.html.engine.Node;
import org.areasy.common.parser.html.engine.NodeFilter;
import org.areasy.common.parser.html.engine.filters.NodeClassFilter;

/**
 * @version $Id: ParserTool.java,v 1.1 2008/05/25 17:26:05 swd\stefan.damian Exp $
 */
public class ParserTool
{

    public static String removeChars(String s, char occur)
    {
        StringBuffer newString = new StringBuffer();
        char ch;

        for(int i = 0; i < s.length(); i++)
        {
            ch = s.charAt(i);
            if(ch != occur) newString.append(ch);
        }

        return newString.toString();
    }

    public static String removeEscapeCharacters(String inputString)
    {
        inputString = ParserTool.removeChars(inputString, '\r');
        inputString = ParserTool.removeChars(inputString, '\n');
        inputString = ParserTool.removeChars(inputString, '\t');
        return inputString;
    }

    public static String removeTrailingBlanks(String text)
    {
        char ch = ' ';
        while(ch == ' ')
        {
            ch = text.charAt(text.length() - 1);
            if(ch == ' ') text = text.substring(0, text.length() - 1);
        }
		
        return text;
    }

    /**
     * Search given node and pick up any objects of given type.
     *
     * @param node The node to search.
     * @param type The class to search for.
     * @return A node array with the matching nodes.
     */
    public static Node[] findTypeInNode(Node node, Class type)
    {
        NodeFilter filter;
        NodeList ret;

        ret = new NodeList();
        filter = new NodeClassFilter(type);
        node.collectInto(ret, filter);

        return (ret.toNodeArray());
    }

}