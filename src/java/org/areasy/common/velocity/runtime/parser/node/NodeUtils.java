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

import org.areasy.common.velocity.context.Context;
import org.areasy.common.velocity.runtime.parser.Token;

/**
 * Utilities for dealing with the AST node structure.
 *
 * @version $Id: NodeUtils.java,v 1.1 2008/05/25 22:33:08 swd\stefan.damian Exp $
 */
public class NodeUtils
{
	/**
	 * Collect all the <SPECIAL_TOKEN>s that
	 * are carried along with a token. Special
	 * tokens do not participate in parsing but
	 * can still trigger certain lexical actions.
	 * In some cases you may want to retrieve these
	 * special tokens, this is simply a way to
	 * extract them.
	 */
	public static String specialText(Token t)
	{
		String specialText = "";

		if (t.specialToken == null || t.specialToken.image.startsWith("##"))
		{
			return specialText;
		}

		Token tmp_t = t.specialToken;

		while (tmp_t.specialToken != null)
		{
			tmp_t = tmp_t.specialToken;
		}

		while (tmp_t != null)
		{
			String st = tmp_t.image;

			StringBuffer sb = new StringBuffer();

			for (int i = 0; i < st.length(); i++)
			{
				char c = st.charAt(i);

				if (c == '#' || c == '$')
				{
					sb.append(c);
				}

				/*
				 *  more dreaded MORE hack :)
				 *
				 *  looking for ("\\")*"$" sequences
				 */

				if (c == '\\')
				{
					boolean ok = true;
					boolean term = false;

					int j = i;
					for (ok = true; ok && j < st.length(); j++)
					{
						char cc = st.charAt(j);

						if (cc == '\\')
						{
							/*
							 *  if we see a \, keep going
							 */
							continue;
						}
						else if (cc == '$')
						{
							/*
							 *  a $ ends it correctly
							 */
							term = true;
							ok = false;
						}
						else
						{
							/*
							 *  nah...
							 */
							ok = false;
						}
					}

					if (term)
					{
						String foo = st.substring(i, j);
						sb.append(foo);
						i = j;
					}
				}
			}

			specialText += sb.toString();

			tmp_t = tmp_t.next;
		}

		return specialText;
	}

	/**
	 * complete node literal
	 */
	public static String tokenLiteral(Token t)
	{
		return specialText(t) + t.image;
	}

	/**
	 * Utility method to interpolate context variables
	 * into string literals. So that the following will
	 * work:
	 * <p/>
	 * #set $name = "candy"
	 * $image.getURI("${name}.jpg")
	 * <p/>
	 * And the string literal argument will
	 * be transformed into "candy.jpg" before
	 * the method is executed.
	 */
	public static String interpolate(String argStr, Context vars)
	{
		StringBuffer argBuf = new StringBuffer();

		for (int cIdx = 0; cIdx < argStr.length();)
		{
			char ch = argStr.charAt(cIdx);

			switch (ch)
			{
				case '$':
					StringBuffer nameBuf = new StringBuffer();
					for (++cIdx; cIdx < argStr.length(); ++cIdx)
					{
						ch = argStr.charAt(cIdx);
						if (ch == '_' || ch == '-'
								|| Character.isLetterOrDigit(ch))
						{
							nameBuf.append(ch);
						}
						else if (ch == '{' || ch == '}')
						{
							continue;
						}
						else
						{
							break;
						}
					}

					if (nameBuf.length() > 0)
					{
						Object value = vars.get(nameBuf.toString());

						if (value == null)
						{
							argBuf.append("$").append(nameBuf.toString());
						}
						else
						{
							argBuf.append(value.toString());
						}
					}
					break;

				default:
					argBuf.append(ch);
					++cIdx;
					break;
			}
		}

		return argBuf.toString();
	}
}
