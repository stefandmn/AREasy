package org.areasy.common.parser.html.engine.visitors;

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
import org.areasy.common.parser.html.engine.tags.Tag;
import org.areasy.common.parser.html.utilities.NodeList;

public class ObjectFindingVisitor extends NodeVisitor
{

	private Class classTypeToFind;
	private NodeList tags;

	public ObjectFindingVisitor(Class classTypeToFind)
	{
		this(classTypeToFind, true);
	}

	public ObjectFindingVisitor(Class classTypeToFind, boolean recurse)
	{
		super(recurse, true);
		this.classTypeToFind = classTypeToFind;
		this.tags = new NodeList();
	}

	public int getCount()
	{
		return (tags.size());
	}

	public void visitTag(Tag tag)
	{
		if (tag.getClass().equals(classTypeToFind))
		{
			tags.add(tag);
		}
	}

	public Node[] getTags()
	{
		return tags.toNodeArray();
	}
}
