package org.areasy.common.velocity.runtime.directive;

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

import org.areasy.common.velocity.base.MethodInvocationException;
import org.areasy.common.velocity.base.ResourceNotFoundException;
import org.areasy.common.velocity.context.InternalContextAdapter;
import org.areasy.common.velocity.runtime.RuntimeService;
import org.areasy.common.velocity.runtime.introspection.Information;
import org.areasy.common.velocity.runtime.parser.ParseErrorException;
import org.areasy.common.velocity.runtime.parser.node.Node;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

/**
 * Foreach directive used for moving through arrays,
 * or objects that provide an Iterator.
 *
 * @version $Id: Foreach.java,v 1.1 2008/05/25 22:33:14 swd\stefan.damian Exp $
 */
public class Foreach extends Directive
{
	/**
	 * Return name of this directive.
	 */
	public String getName()
	{
		return "foreach";
	}

	/**
	 * Return type of this directive.
	 */
	public int getType()
	{
		return BLOCK;
	}

	/**
	 * The name of the variable to use when placing
	 * the counter value into the context. Right
	 * now the default is $velocityCount.
	 */
	private String counterName;

	/**
	 * What value to start the loop counter at.
	 */
	private int counterInitialValue;

	/**
	 * The reference name used to access each
	 * of the elements in the list object. It
	 * is the $item in the following:
	 * <p/>
	 * #foreach ($item in $list)
	 * <p/>
	 * This can be used class wide because
	 * it is immutable.
	 */
	private String elementKey;

	/**
	 * immutable, so create in init
	 */
	protected Information uberInformation;

	/**
	 * simple init - init the tree and get the elementKey from
	 * the AST
	 */
	public void init(RuntimeService rs, InternalContextAdapter context, Node node) throws Exception
	{
		super.init(rs, context, node);

		counterName = rsvc.getConfiguration().getString("engine.directive.foreach.counter.name");
		counterInitialValue = rsvc.getConfiguration().getInt("engine.directive.foreach.counter.initial.value");

		/*
		 *  this is really the only thing we can do here as everything
		 *  else is context sensitive
		 */

		elementKey = node.jjtGetChild(0).getFirstToken().image.substring(1);

		/*
		 * make an uberinfo - saves new's later on
		 */

		uberInformation = new Information(context.getCurrentTemplateName(),
				getLine(), getColumn());
	}

	/**
	 * renders the #foreach() block
	 */
	public boolean render(InternalContextAdapter context,
						  Writer writer, Node node)
			throws IOException, MethodInvocationException, ResourceNotFoundException,
			ParseErrorException
	{
		/*
		 *  do our introspection to see what our collection is
		 */

		Object listObject = node.jjtGetChild(2).value(context);

		if (listObject == null)
		{
			return false;
		}

		Iterator i = null;

		try
		{
			i = rsvc.getUberspect().getIterator(listObject, uberInformation);
		}
		catch (Exception ee)
		{
			System.out.println(ee);
		}

		if (i == null)
		{
			return false;
		}

		int counter = counterInitialValue;

		/*
		 *  save the element key if there is one,
		 *  and the loop counter
		 */

		Object o = context.get(elementKey);
		Object ctr = context.get(counterName);

		while (i.hasNext())
		{
			context.put(counterName, new Integer(counter));
			context.put(elementKey, i.next());
			node.jjtGetChild(3).render(context, writer);
			counter++;
		}

		/*
		 * restores the loop counter (if we were nested)
		 * if we have one, else just removes
		 */

		if (ctr != null)
		{
			context.put(counterName, ctr);
		}
		else
		{
			context.remove(counterName);
		}


		/*
		 *  restores element key if exists
		 *  otherwise just removes
		 */

		if (o != null)
		{
			context.put(elementKey, o);
		}
		else
		{
			context.remove(elementKey);
		}

		return true;
	}
}
