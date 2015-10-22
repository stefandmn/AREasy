package org.areasy.common.parser.html.engine.tags;

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

import org.areasy.common.parser.html.engine.visitors.NodeVisitor;

/**
 * A title tag.
 */
public class TitleTag extends CompositeTag
{

	/**
	 * The set of names handled by this tag.
	 */
	private static final String[] mIds = new String[]{"TITLE"};

	/**
	 * The set of tag names that indicate the end of this tag.
	 */
	private static final String[] mEnders = new String[]{"TITLE", "BODY"};

	/**
	 * The set of end tag names that indicate the end of this tag.
	 */
	private static final String[] mEndTagEnders = new String[]{"HEAD", "HTML"};

	/**
	 * Create a new title tag.
	 */
	public TitleTag()
	{
	}

	/**
	 * Return the set of names handled by this tag.
	 *
	 * @return The names to be matched that create tags of this type.
	 */
	public String[] getIds()
	{
		return (mIds);
	}

	/**
	 * Return the set of tag names that cause this tag to finish.
	 *
	 * @return The names of following tags that stop further scanning.
	 */
	public String[] getEnders()
	{
		return (mEnders);
	}

	/**
	 * Return the set of end tag names that cause this tag to finish.
	 *
	 * @return The names of following end tags that stop further scanning.
	 */
	public String[] getEndTagEnders()
	{
		return (mEndTagEnders);
	}

	/**
	 * Get the title text.
	 */
	public String getTitle()
	{
		return toPlainTextString();
	}

	public String toString()
	{
		return "TITLE: " + getTitle();
	}

	/**
	 * Title visiting code.
	 * Invokes <code>visitTitleTag()</code> on the visitor and then
	 * invokes the normal tag processing.
	 *
	 * @param visitor The <code>NodeVisitor</code> object to invoke
	 *                <code>visitTitleTag()</code> on.
	 */
	public void accept(NodeVisitor visitor)
	{
		visitor.visitTitleTag(this);
		super.accept(visitor);
	}
}
