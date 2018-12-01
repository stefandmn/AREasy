package org.areasy.common.velocity.runtime.visitor;

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

import org.areasy.common.velocity.runtime.parser.node.ASTReference;

import java.util.Map;

/**
 * This class is a visitor used by the VM proxy to change the
 * literal representation of a reference in a VM.  The reason is
 * to preserve the 'render literal if null' behavior w/o making
 * the VMProxy stuff more complicated than it is already.
 *
 * @version $Id: VMReferenceMungeVisitor.java,v 1.1 2008/05/25 22:33:14 swd\stefan.damian Exp $
 */
public class VMReferenceMungeVisitor extends BaseVisitor
{
	/**
	 * Map containing VM arg to instance-use reference
	 * Passed in with CTOR
	 */
	private Map argmap = null;

	/**
	 * CTOR - takes a map of args to reference
	 */
	public VMReferenceMungeVisitor(Map map)
	{
		argmap = map;
	}

	/**
	 * Visitor method - if the literal is right, will
	 * set the literal in the ASTReference node
	 *
	 * @param node ASTReference to work on
	 * @param data Object to pass down from caller
	 */
	public Object visit(ASTReference node, Object data)
	{
		/*
		 *  see if there is an override value for this
		 *  reference
		 */
		String override = (String) argmap.get(node.literal().substring(1));

		/*
		 *  if so, set in the node
		 */
		if (override != null)
		{
			node.setLiteral(override);
		}

		/*
		 *  feed the children...
		 */
		data = node.childrenAccept(this, data);

		return data;
	}
}

