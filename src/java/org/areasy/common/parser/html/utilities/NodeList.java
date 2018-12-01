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

import java.io.Serializable;
import java.util.NoSuchElementException;

/**
 * @version $Id: NodeList.java,v 1.1 2008/05/25 17:26:04 swd\stefan.damian Exp $
 */
public class NodeList implements Serializable
{

    private static final int INITIAL_CAPACITY = 10;
    //private static final int CAPACITY_INCREMENT=20;
    private Node nodeData[];
    private int size;
    private int capacity;
    private int capacityIncrement;
    private int numberOfAdjustments;

    public NodeList()
    {
        size = 0;
        capacity = INITIAL_CAPACITY;
        nodeData = newNodeArrayFor(capacity);
        capacityIncrement = capacity * 2;
        numberOfAdjustments = 0;
    }

    /**
     * Create a one element node list.
     *
     * @param node The initial node to add.
     */
    public NodeList(Node node)
    {
        this();
        add(node);
    }

    public void add(Node node)
    {
        if(size == capacity)
        {
            adjustVectorCapacity();
        }
        nodeData[size++] = node;
    }

    /**
     * Add another node list to this one.
     *
     * @param list The list to add.
     */
    public void add(NodeList list)
    {
        for(int i = 0; i < list.size; i++)
        {
            add(list.nodeData[i]);
        }
    }

    /**
     * Insert the given node at the head of the list.
     *
     * @param node The new first element.
     */
    public void prepend(Node node)
    {
        if(size == capacity)
        {
            adjustVectorCapacity();
        }
        System.arraycopy(nodeData, 0, nodeData, 1, size);
        size++;
        nodeData[0] = node;
    }

    private void adjustVectorCapacity()
    {
        capacity += capacityIncrement;
        capacityIncrement *= 2;
        Node oldData [] = nodeData;
        nodeData = newNodeArrayFor(capacity);
        System.arraycopy(oldData, 0, nodeData, 0, size);
        numberOfAdjustments++;
    }

    private Node[] newNodeArrayFor(int capacity)
    {
        return new Node[capacity];
    }

    public int size()
    {
        return size;
    }

    public Node elementAt(int i)
    {
        return nodeData[i];
    }

    public int getNumberOfAdjustments()
    {
        return numberOfAdjustments;
    }

    public SimpleNodeIterator elements()
    {
        return new SimpleNodeIterator()
        {
            int count = 0;

            public boolean hasMoreNodes()
            {
                return count < size;
            }

            public Node nextNode()
            {
                synchronized(NodeList.this)
                {
                    if(count < size)
                    {
                        return nodeData[count++];
                    }
                }
                throw new NoSuchElementException("Vector Enumeration");
            }
        };
    }

    public Node[] toNodeArray()
    {
        Node[] nodeArray = newNodeArrayFor(size);
        System.arraycopy(nodeData, 0, nodeArray, 0, size);
        return nodeArray;
    }

    public void copyToNodeArray(Node[] array)
    {
        System.arraycopy(nodeData, 0, array, 0, size);
    }

    public String asString()
    {
        StringBuffer buff = new StringBuffer();
        for(int i = 0; i < size; i++)
        {
            buff.append(nodeData[i].toPlainTextString());
        }
        return buff.toString();
    }

    public String asHtml()
    {
        StringBuffer buff = new StringBuffer();
        for(int i = 0; i < size; i++)
        {
            buff.append(nodeData[i].toHtml());
        }
        return buff.toString();
    }

    public Node remove(int index)
    {
        Node ret;
        ret = nodeData[index];
        System.arraycopy(nodeData, index + 1, nodeData, index, size - index - 1);
        nodeData[size - 1] = null;
        size--;
        return (ret);
    }

    public void removeAll()
    {
        size = 0;
        capacity = INITIAL_CAPACITY;
        nodeData = newNodeArrayFor(capacity);
        capacityIncrement = capacity * 2;
        numberOfAdjustments = 0;
    }

    public String toString()
    {
        StringBuffer text = new StringBuffer();
        for(int i = 0; i < size; i++)
        {
            text.append(nodeData[i].toPlainTextString());
        }
        return text.toString();
    }

    /**
     * Filter the list with the given filter non-recursively.
     *
     * @param filter The filter to use.
     * @return A new node array containing the nodes accepted by the filter.
     *         This is a linear list and preserves the nested structure of the returned
     *         nodes only.
     */
    public NodeList extractAllNodesThatMatch(NodeFilter filter)
    {
        return (extractAllNodesThatMatch(filter, false));
    }

    /**
     * Filter the list with the given filter.
     *
     * @param filter    The filter to use.
     * @param recursive If <code>true<code> digs into the children recursively.
     * @return A new node array containing the nodes accepted by the filter.
     *         This is a linear list and preserves the nested structure of the returned
     *         nodes only.
     */
    public NodeList extractAllNodesThatMatch(NodeFilter filter, boolean recursive)
    {
        String name;
        Node node;
        NodeList children;
        NodeList ret;

        ret = new NodeList();
        for(int i = 0; i < size; i++)
        {
            node = nodeData[i];
            if(filter.accept(node))
            {
                ret.add(node);
            }
            if(recursive)
            {
                children = node.getChildren();
                if(null != children)
                {
                    ret.add(children.extractAllNodesThatMatch(filter, recursive));
                }
            }
        }

        return (ret);
    }

    /**
     * Remove nodes not matching the given filter non-recursively.
     *
     * @param filter The filter to use.
     */
    public void keepAllNodesThatMatch(NodeFilter filter)
    {
        keepAllNodesThatMatch(filter, false);
    }

    /**
     * Remove nodes not matching the given filter.
     *
     * @param filter    The filter to use.
     * @param recursive If <code>true<code> digs into the children recursively.
     */
    public void keepAllNodesThatMatch(NodeFilter filter, boolean recursive)
    {
        String name;
        Node node;
        NodeList children;
        NodeList ret;

        for(int i = 0; i < size;)
        {
            node = nodeData[i];
            if(!filter.accept(node))
            {
                remove(i);
            }
            else
            {
                if(recursive)
                {
                    children = node.getChildren();
                    if(null != children)
                    {
                        children.keepAllNodesThatMatch(filter, recursive);
                    }
                }
                i++;
            }
        }
    }

    /**
     * Convenience method to search for nodes of the given type non-recursively.
     *
     * @param classType The class to search for.
     */
    public NodeList searchFor(Class classType)
    {
        return (searchFor(classType, false));
    }

    /**
     * Convenience method to search for nodes of the given type.
     *
     * @param classType The class to search for.
     * @param recursive If <code>true<code> digs into the children recursively.
     */
    public NodeList searchFor(Class classType, boolean recursive)
    {
        return (extractAllNodesThatMatch(new NodeClassFilter(classType), recursive));
    }
}
