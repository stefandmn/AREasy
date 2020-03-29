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

/**
 * The HTMLSimpleEnumeration interface is similar to NodeIterator,
 * except that it does not throw exceptions. This interface is useful
 * when using HTMLVector, to enumerate through its elements in a simple
 * manner, without needing to do class casts for Node.
 *
 * @version $Id: SimpleNodeIterator.java,v 1.1 2008/05/25 17:26:05 swd\stefan.damian Exp $
 */
public interface SimpleNodeIterator extends NodeIterator
{

    /**
     * Check if more nodes are available.
     *
     * @return <code>true</code> if a call to <code>nextHTMLNode()</code> will
     *         succeed.
     */
    public boolean hasMoreNodes();

    /**
     * Get the next node.
     *
     * @return The next node in the HTML stream, or null if there are no more
     *         nodes.
     */
    public Node nextNode();
}
