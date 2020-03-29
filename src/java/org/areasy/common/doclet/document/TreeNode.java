package org.areasy.common.doclet.document;

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

import java.util.Vector;


/**
 * Class used to build a hierachical
 * derivation tree of all classes.
 *
 * @version $Id: TreeNode.java,v 1.2 2008/05/14 09:36:47 swd\stefan.damian Exp $
 */
public class TreeNode
{

	private static Vector treeResults = null;

	private String name = "";
	private TreeNode parent = null;
	private TreeNode[] next = new TreeNode[0];

	/**
	 * Constructs a node with a
	 * given class name.
	 *
	 * @param name The qualified name of the class.
	 */
	public TreeNode(String name)
	{
		this.name = name;
	}

	/**
	 * Traverses the tree beginning from this node
	 * and returns all subnodes in an array.
	 *
	 * @return All subnodes of this node.
	 */
	public TreeNode[] getNodes()
	{
		// Initialize the static Vector which will hold the results
		treeResults = new Vector();
		getSubNodes();

		// Then put everything into an array
		TreeNode[] nodes = new TreeNode[treeResults.size()];
		nodes = (TreeNode[]) treeResults.toArray(nodes);

		return nodes;
	}

	/**
	 * Traverses the tree beginning from this node
	 * and returns all parent nodes in an array.
	 *
	 * @return All parent nodes of this node.
	 */
	public TreeNode[] getParents()
	{
		// Initialize the static Vector which will hold the results
		treeResults = new Vector();
		getParentNodes();

		// Then put everything into an array
		TreeNode[] nodes = new TreeNode[treeResults.size()];
		nodes = (TreeNode[]) treeResults.toArray(nodes);

		return nodes;
	}

	/**
	 * Collects all subnodes of this node
	 * in the static Vector object. This method
	 * calls itself recursively on all subnodes.
	 */
	private void getSubNodes()
	{
		TreeNode[] nodes = next();

		for (int i = 0; i < nodes.length; i++)
		{
			nodes[i].getSubNodes();
			treeResults.addElement(nodes[i]);
		}
	}

	/**
	 * Collects all parent nodes of this node
	 * in the static Vector object. This method
	 * calls itself recursively on all subnodes.
	 */
	private void getParentNodes()
	{
		if (parent != null)
		{
			parent.getParentNodes();
			treeResults.addElement(parent);
		}
	}

	/**
	 * Adds a subnode to this node.
	 *
	 * @param node The subnode to be added.
	 */
	public void addNode(TreeNode node)
	{
		node.parent = this;

		TreeNode[] nodes = new TreeNode[this.next.length + 1];
		System.arraycopy(next, 0, nodes, 0, this.next.length);
		nodes[nodes.length - 1] = node;
		this.next = nodes;
	}

	/**
	 * Returns a list of all direct
	 * subnodes of this node.
	 *
	 * @return All direct subnodes of this node.
	 */
	public TreeNode[] next()
	{
		return this.next;
	}

	/**
	 * Returns the the direct parent
	 * node of this node.
	 *
	 * @return The parent node.
	 */
	public TreeNode parent()
	{
		return this.parent;
	}

	/**
	 * Returns the qualified class name
	 * of this tree node.
	 *
	 * @return The qualified class name.
	 */
	public String getName()
	{
		return this.name;
	}
}
