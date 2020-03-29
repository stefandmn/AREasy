package org.areasy.common.parser.html.engine;

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

import org.areasy.common.parser.html.engine.lexer.Page;
import org.areasy.common.parser.html.engine.lexer.nodes.Attribute;
import org.areasy.common.parser.html.engine.lexer.nodes.NodeFactory;
import org.areasy.common.parser.html.engine.tags.*;
import org.areasy.common.parser.html.utilities.ParserException;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

/**
 * A node factory based on the prototype pattern.
 * This factory uses the prototype pattern to generate new Tag nodes.
 * Prototype tags, in the form of undifferentiated tags are held in a hash
 * table.
 *
 * @version $Id: PrototypicalNodeFactory.java,v 1.1 2008/05/25 17:26:05 swd\stefan.damian Exp $
 */
public class PrototypicalNodeFactory implements Serializable, NodeFactory
{

	/**
	 * The list of tags to return at the top level.
	 * The list is keyed by tag name.
	 */
	protected Map mBlastocyst;

	/**
	 * Create a new factory with all but DOM tags registered.
	 */
	public PrototypicalNodeFactory()
	{
		this(false);
	}

	/**
	 * Create a new factory with no registered tags.
	 */
	public PrototypicalNodeFactory(boolean empty)
	{
		clear();
		if (!empty)
		{
			registerTags();
		}
	}

	/**
	 * Create a new factory with the given tag as the only one registered.
	 */
	public PrototypicalNodeFactory(Tag tag)
	{
		this(true);
		registerTag(tag);
	}

	/**
	 * Create a new factory with the given tags registered.
	 */
	public PrototypicalNodeFactory(Tag[] tags)
	{
		this(true);
		for (int i = 0; i < tags.length; i++)
		{
			registerTag(tags[i]);
		}
	}

	/**
	 * Adds a tag to the registry.
	 *
	 * @param id  The name under which to register the tag.
	 * @param tag The tag to be returned from a createTag(id) call.
	 * @return The tag previously registered with that id,
	 *         or <code>null</code> if none.
	 */
	public Tag put(String id, Tag tag)
	{
		return ((Tag) mBlastocyst.put(id, tag));
	}

	/**
	 * Gets a tag from the registry.
	 *
	 * @param id The name of the tag to return.
	 * @return The tag registered under the id name or <code>null</code> if none.
	 */
	public Tag get(String id)
	{
		return ((Tag) mBlastocyst.get(id));
	}

	/**
	 * Remove a tag from the registry.
	 *
	 * @param id The name of the tag to remove.
	 * @return The tag that was registered with that id.
	 */
	public Tag remove(String id)
	{
		return ((Tag) mBlastocyst.remove(id));
	}

	/**
	 * Clean out the registry.
	 */
	public void clear()
	{
		mBlastocyst = new Hashtable();
	}


	public void registerTag(Tag tag)
	{
		String ids[];

		ids = tag.getIds();
		for (int i = 0; i < ids.length; i++)
		{
			put(ids[i], tag);
		}
	}

	public void unregisterTag(Tag tag)
	{
		String ids[];

		ids = tag.getIds();
		for (int i = 0; i < ids.length; i++)
		{
			remove(ids[i]);
		}
	}

	public PrototypicalNodeFactory registerTags()
	{
		registerTag(new AppletTag());
		registerTag(new BaseHrefTag());
		registerTag(new Bullet());
		registerTag(new BulletList());
		registerTag(new DoctypeTag());
		registerTag(new FormTag());
		registerTag(new FrameSetTag());
		registerTag(new FrameTag());
		registerTag(new ImageTag());
		registerTag(new InputTag());
		registerTag(new JspTag());
		registerTag(new LabelTag());
		registerTag(new LinkTag());
		registerTag(new MetaTag());
		registerTag(new OptionTag());
		registerTag(new ScriptTag());
		registerTag(new SelectTag());
		registerTag(new StyleTag());
		registerTag(new TableColumn());
		registerTag(new TableHeader());
		registerTag(new TableRow());
		registerTag(new TableTag());
		registerTag(new TextareaTag());
		registerTag(new TitleTag());
		registerTag(new Div());
		registerTag(new Span());
		registerTag(new BodyTag());
		registerTag(new HeadTag());
		registerTag(new Html());

		return (this);
	}

	//
	// NodeFactory interface
	//

	/**
	 * Create a new string node.
	 *
	 * @param page  The page the node is on.
	 * @param start The beginning position of the string.
	 * @param end   The ending positiong of the string.
	 */
	public Node createStringNode(Page page, int start, int end)
	{
		Node ret;

		ret = new StringNode(page, start, end);

		return (ret);
	}

	/**
	 * Create a new remark node.
	 *
	 * @param page  The page the node is on.
	 * @param start The beginning position of the remark.
	 * @param end   The ending positiong of the remark.
	 */
	public Node createRemarkNode(Page page, int start, int end)
	{
		return (new RemarkNode(page, start, end));
	}

	/**
	 * Create a new tag node.
	 * Note that the attributes vector contains at least one element,
	 * which is the tag name (standalone attribute) at position zero.
	 * This can be used to decide which type of node to create, or
	 * gate other processing that may be appropriate.
	 *
	 * @param page       The page the node is on.
	 * @param start      The beginning position of the tag.
	 * @param end        The ending positiong of the tag.
	 * @param attributes The attributes contained in this tag.
	 */
	public Node createTagNode(Page page, int start, int end, Vector attributes)
			throws
			ParserException
	{
		Attribute attribute;
		String id;
		Tag prototype;
		Tag ret;

		ret = null;

		if (0 != attributes.size())
		{
			attribute = (Attribute) attributes.elementAt(0);
			id = attribute.getName();
			if (null != id)
			{
				try
				{
					id = id.toUpperCase(Locale.ENGLISH);
					if (!id.startsWith("/"))
					{
						if (id.endsWith("/"))
						{
							id = id.substring(0, id.length() - 1);
						}
						prototype = (Tag) mBlastocyst.get(id);
						if (null != prototype)
						{
							ret = (Tag) prototype.clone();
							ret.setPage(page);
							ret.setStartPosition(start);
							ret.setEndPosition(end);
							ret.setAttributesEx(attributes);
						}
					}
				}
				catch (CloneNotSupportedException cnse)
				{
					// default to creating a new one
				}
			}
		}
		if (null == ret)
		{
			ret = new Tag(page, start, end, attributes);
		}

		return (ret);
	}
}
