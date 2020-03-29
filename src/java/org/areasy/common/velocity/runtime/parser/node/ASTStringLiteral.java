package org.areasy.common.velocity.runtime.parser.node;

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

import org.areasy.common.velocity.context.InternalContextAdapter;
import org.areasy.common.velocity.runtime.parser.Parser;

import java.io.BufferedReader;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * ASTStringLiteral support.  Will interpolate!
 *
 * @version $Id: ASTStringLiteral.java,v 1.1 2008/05/25 22:33:08 swd\stefan.damian Exp $
 */
public class ASTStringLiteral extends SimpleNode
{
	/* cache the value of the interpolation switch */
	private boolean interpolate = true;
	private SimpleNode nodeTree = null;
	private String image = "";
	private String interpolateimage = "";

	public ASTStringLiteral(int id)
	{
		super(id);
	}

	public ASTStringLiteral(Parser p, int id)
	{
		super(p, id);
	}

	/**
	 * init : we don't have to do much.  Init the tree (there
	 * shouldn't be one) and then see if interpolation is turned on.
	 */
	public Object init(InternalContextAdapter context, Object data) throws Exception
	{
		/*
		 *  simple habit...  we prollie don't have an AST beneath us
		 */

		super.init(context, data);

		/*
		 *  the stringlit is set at template parse time, so we can
		 *  do this here for now.  if things change and we can somehow
		 * create stringlits at runtime, this must
		 *  move to the runtime execution path
		 *
		 *  so, only if interpolation is turned on AND it starts
		 *  with a " AND it has a  directive or reference, then we
		 *  can  interpolate.  Otherwise, don't bother.
		 */

		interpolate = rsvc.getBoolean("runtime.interpolate.string.literals", true)
				&& getFirstToken().image.startsWith("\"")
				&& ((getFirstToken().image.indexOf('$') != -1)
				|| (getFirstToken().image.indexOf('#') != -1));

		/*
		 *  get the contents of the string, minus the '/" at each end
		 */

		image = getFirstToken().image.substring(1,
				getFirstToken().image.length() - 1);

		/*
		 * tack a space on the end (dreaded <MORE> kludge)
		 */

		interpolateimage = image + " ";

		if (interpolate)
		{
			/*
			 *  now parse and init the nodeTree
			 */
			BufferedReader br = new BufferedReader(new StringReader(interpolateimage));

			/*
			 * it's possible to not have an initialization context - or we don't
			 * want to trust the caller - so have a fallback value if so
			 *
			 *  Also, do *not* dump the VM namespace for this template
			 */

			nodeTree = rsvc.parse(br, (context != null) ? context.getCurrentTemplateName() : "StringLiteral", false);

			/*
			 *  init with context. It won't modify anything
			 */
			nodeTree.init(context, rsvc);
		}

		return data;
	}

	/**
	 * Accept the visitor. *
	 */
	public Object jjtAccept(ParserVisitor visitor, Object data)
	{
		return visitor.visit(this, data);
	}

	/**
	 * renders the value of the string literal
	 * If the properties allow, and the string literal contains a $ or a #
	 * the literal is rendered against the context
	 * Otherwise, the stringlit is returned.
	 */
	public Object value(InternalContextAdapter context)
	{
		if (interpolate)
		{
			try
			{
				StringWriter writer = new StringWriter();
				nodeTree.render(context, writer);

				String ret = writer.toString();

				return ret.substring(0, ret.length() - 1);
			}
			catch (Exception e)
			{
				logger.error("Error in interpolating string literal : " + e.getMessage());
			}
		}

		return image;
	}
}
