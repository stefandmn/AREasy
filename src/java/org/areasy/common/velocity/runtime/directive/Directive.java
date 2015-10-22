package org.areasy.common.velocity.runtime.directive;

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

import org.areasy.common.velocity.base.MethodInvocationException;
import org.areasy.common.velocity.base.ResourceNotFoundException;
import org.areasy.common.velocity.context.InternalContextAdapter;
import org.areasy.common.velocity.runtime.parser.ParseErrorException;
import org.areasy.common.velocity.runtime.RuntimeService;
import org.areasy.common.velocity.runtime.parser.node.Node;

import java.io.IOException;
import java.io.Writer;


/**
 * Base class for all directives used in Velocity.
 *
 * @version $Id: Directive.java,v 1.1 2008/05/25 22:33:14 swd\stefan.damian Exp $
 */
public abstract class Directive implements Cloneable
{
	/** Block directive indicator */
	public static final int BLOCK = 1;

	/** Line directive indicator */
	public static final int LINE = 2;

	private int line = 0;
	private int column = 0;

	protected RuntimeService rsvc = null;

	/**
	 * Return the name of this directive
	 */
	public abstract String getName();

	/**
	 * Get the directive type BLOCK/LINE
	 */
	public abstract int getType();

	/**
	 * Allows the template location to be set
	 */
	public void setLocation(int line, int column)
	{
		this.line = line;
		this.column = column;
	}

	/**
	 * for log msg purposes
	 */
	public int getLine()
	{
		return line;
	}

	/**
	 * for log msg purposes
	 */
	public int getColumn()
	{
		return column;
	}

	/**
	 * How this directive is to be initialized.
	 */
	public void init(RuntimeService rs, InternalContextAdapter context, Node node) throws Exception
	{
		rsvc = rs;
	}

	/**
	 * How this directive is to be rendered
	 */
	public abstract boolean render(InternalContextAdapter context, Writer writer, Node node) throws IOException, ResourceNotFoundException, ParseErrorException, MethodInvocationException;
}
