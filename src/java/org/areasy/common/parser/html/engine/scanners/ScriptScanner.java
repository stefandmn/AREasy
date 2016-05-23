package org.areasy.common.parser.html.engine.scanners;

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

import org.areasy.common.parser.html.engine.Node;
import org.areasy.common.parser.html.engine.PrototypicalNodeFactory;
import org.areasy.common.parser.html.engine.RemarkNode;
import org.areasy.common.parser.html.engine.StringNode;
import org.areasy.common.parser.html.engine.lexer.Lexer;
import org.areasy.common.parser.html.engine.lexer.nodes.NodeFactory;
import org.areasy.common.parser.html.engine.tags.CompositeTag;
import org.areasy.common.parser.html.engine.tags.ScriptTag;
import org.areasy.common.parser.html.engine.tags.Tag;
import org.areasy.common.parser.html.utilities.NodeList;
import org.areasy.common.parser.html.utilities.ParserException;

import java.util.Vector;

/**
 * The ScriptScanner handles script code.
 * It gathers all interior nodes into one undifferentiated string node.
 */
public class ScriptScanner extends CompositeTagScanner
{

	/**
	 * Create a script scanner.
	 */
	public ScriptScanner()
	{
	}

	/**
	 * Scan for script.
	 * Accumulates nodes returned from the lexer, until &lt;/SCRIPT&gt;,
	 * &lt;BODY&gt; or &lt;HTML&gt; is encountered. Replaces the node factory
	 * in the lexer with a new (empty) one to avoid other scanners missing their
	 * end tags and accumulating even the &lt;/SCRIPT&gt; tag.
	 *
	 * @param tag   The tag this scanner is responsible for.
	 * @param lexer The source of subsequent nodes.
	 * @param stack The parse stack, <em>not used</em>.
	 */
	public Tag scan(Tag tag, Lexer lexer, NodeList stack)
			throws ParserException
	{
		String language;
		Node node;
		boolean done;
		int position;
		StringNode last;
		Tag end;
		NodeFactory factory;
		CompositeTag ret;

		done = false;
		last = null;
		end = null;
		factory = lexer.getNodeFactory();
		if (tag instanceof ScriptTag)
		{
			language = ((ScriptTag) tag).getLanguage();
			if ((null != language) &&
					(language.equalsIgnoreCase("JScript.Encode") ||
					language.equalsIgnoreCase("VBScript.Encode")))
			{
				int start = lexer.getPosition();
				String code = ScriptDecoder.Decode(lexer.getPage(), lexer.getCursor());
				((ScriptTag) tag).setScriptCode(code);
				last = (StringNode) factory.createStringNode(lexer.getPage(), start, lexer.getPosition());
			}
		}
		lexer.setNodeFactory(new PrototypicalNodeFactory(true));
		try
		{
			do
			{
				position = lexer.getPosition();
				node = lexer.nextNode(true);
				if (null == node)
				{
					break;
				}
				else
				{
					if (node instanceof Tag)
					{
						if (((Tag) node).isEndTag()
								&& ((Tag) node).getTagName().equals(tag.getIds()[0]))
						{
							end = (Tag) node;
							done = true;
						}
						else
						{
							if (isTagToBeEndedFor(tag, (Tag) node))
							{
								lexer.setPosition(position);
								done = true;
							}
							else
							{
								// must be a string, even though it looks like a tag
								if (null != last)
								// append it to the previous one
								{
									last.setEndPosition(node.elementEnd());
								}
								else
								{
									last = (StringNode) factory.createStringNode(lexer.getPage(), node.elementBegin(), node.elementEnd());
								}
							}
						}
					}
					else
					{
						if (node instanceof RemarkNode)
						{
							if (null != last)
							{
								last.setEndPosition(node.getEndPosition());
							}
							else
							{
								last = (StringNode) factory.createStringNode(lexer.getPage(), node.elementBegin(), node.elementEnd());
							}
						}
						else
						{
							if (null != last)
							{
								last.setEndPosition(node.getEndPosition());
							}
							else
							{
								last = (StringNode) node;
							}
						}
					}
				}

			}
			while (!done);

			// build new string tag if required
			if (null == last)
			{
				last = (StringNode) factory.createStringNode(lexer.getPage(), position, position);
			}
			// build new end tag if required
			if (null == end)
			{
				end = new Tag(lexer.getPage(), tag.getEndPosition(), tag.getEndPosition(), new Vector());
			}
			ret = (CompositeTag) tag;
			ret.setEndTag(end);
			ret.setChildren(new NodeList(last));
			last.setParent(ret);
			end.setParent(ret);
			ret.doSemanticAction();
		}
		finally
		{
			lexer.setNodeFactory(factory);
		}

		return (ret);
	}
}
