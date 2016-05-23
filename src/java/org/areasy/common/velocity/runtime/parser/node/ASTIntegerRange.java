package org.areasy.common.velocity.runtime.parser.node;

/*
 * Copyright (c) 2007-2016 AREasy Runtime
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
import org.areasy.common.velocity.context.InternalContextAdapter;
import org.areasy.common.velocity.runtime.parser.Parser;

import java.util.ArrayList;

public class ASTIntegerRange extends SimpleNode
{
	public ASTIntegerRange(int id)
	{
		super(id);
	}

	public ASTIntegerRange(Parser p, int id)
	{
		super(p, id);
	}

	/**
	 * Accept the visitor. *
	 */
	public Object jjtAccept(ParserVisitor visitor, Object data)
	{
		return visitor.visit(this, data);
	}

	/**
	 * does the real work.  Creates an Vector of Integers with the
	 * right value range
	 *
	 * @param context app context used if Left or Right of .. is a ref
	 * @return Object array of Integers
	 */
	public Object value(InternalContextAdapter context)
			throws MethodInvocationException
	{
		/*
		 *  get the two range ends
		 */

		Object left = jjtGetChild(0).value(context);
		Object right = jjtGetChild(1).value(context);

		/*
		 *  if either is null, lets log and bail
		 */

		if (left == null || right == null)
		{
			logger.error((left == null ? "Left" : "Right") + " side of range operator [n..m] has null value."
					+ " Operation not possible. "
					+ context.getCurrentTemplateName() + " [line " + getLine()
					+ ", column " + getColumn() + "]");
			return null;
		}

		/*
		 *  if not an Integer, not much we can do either
		 */

		if (!(left instanceof Integer) || !(right instanceof Integer))
		{
			logger.error((!(left instanceof Integer) ? "Left" : "Right")
					+ " side of range operator is not a valid type. "
					+ "Currently only integers (1,2,3...) and Integer type is supported. "
					+ context.getCurrentTemplateName() + " [line " + getLine()
					+ ", column " + getColumn() + "]");

			return null;
		}


		/*
		 *  get the two integer values of the ends of the range
		 */

		int l = ((Integer) left).intValue();
		int r = ((Integer) right).intValue();

		/*
		 *  find out how many there are
		 */

		int num = Math.abs(l - r);
		num += 1;

		/*
		 *  see if your increment is Pos or Neg
		 */

		int delta = (l >= r) ? -1 : 1;

		/*
		 *  make the vector and fill it
		 */

		ArrayList foo = new ArrayList();
		int val = l;

		for (int i = 0; i < num; i++)
		{
			foo.add(new Integer(val));
			val += delta;
		}

		return foo;
	}
}

