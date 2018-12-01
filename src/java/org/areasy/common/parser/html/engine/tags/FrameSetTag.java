package org.areasy.common.parser.html.engine.tags;

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

import org.areasy.common.parser.html.engine.Node;
import org.areasy.common.parser.html.utilities.NodeList;
import org.areasy.common.parser.html.utilities.SimpleNodeIterator;

import java.util.Locale;

/**
 * Identifies an frame set tag.
 */
public class FrameSetTag extends CompositeTag
{

	/**
	 * The set of names handled by this tag.
	 */
	private static final String[] mIds = new String[]{"FRAMESET"};

	/**
	 * The set of end tag names that indicate the end of this tag.
	 */
	private static final String[] mEndTagEnders = new String[]{"HTML"};

	/**
	 * Create a new frame set tag.
	 */
	public FrameSetTag()
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
	 * Return the set of end tag names that cause this tag to finish.
	 *
	 * @return The names of following end tags that stop further scanning.
	 */
	public String[] getEndTagEnders()
	{
		return (mEndTagEnders);
	}

	/**
	 * Print the contents of the FrameSetTag
	 */
	public String toString()
	{
		return "FRAMESET TAG : begins at : " + getStartPosition() + "; ends at : " + getEndPosition();
	}

	/**
	 * Returns the frames.
	 *
	 * @return The children of this tag.
	 */
	public NodeList getFrames()
	{
		return (getChildren());
	}

	/**
	 * Gets a frame by name.
	 * Names are checked without case sensitivity and conversion to uppercase
	 * is performed with an English locale.
	 *
	 * @param name The name of the frame to retrieve.
	 * @return The specified frame or <code>null</code> if it wasn't found.
	 */
	public FrameTag getFrame(String name)
	{
		return (getFrame(name, Locale.ENGLISH));
	}

	/**
	 * Gets a frame by name.
	 * Names are checked without case sensitivity and conversion to uppercase
	 * is performed with the locale provided.
	 *
	 * @param name   The name of the frame to retrieve.
	 * @param locale The locale to use when converting to uppercase.
	 * @return The specified frame or <code>null</code> if it wasn't found.
	 */
	public FrameTag getFrame(String name, Locale locale)
	{
		Node node;
		FrameTag ret;

		ret = null;

		name = name.toUpperCase(locale);
		for (SimpleNodeIterator e = getFrames().elements(); e.hasMoreNodes() && (null == ret);)
		{
			node = e.nextNode();
			if (node instanceof FrameTag)
			{
				ret = (FrameTag) node;
				if (!ret.getFrameName().toUpperCase(locale).equals(name))
				{
					ret = null;
				}
			}
		}

		return (ret);
	}

	/**
	 * Sets the frames (children of this tag).
	 *
	 * @param frames The frames to set
	 */
	public void setFrames(NodeList frames)
	{
		setChildren(frames);
	}
}
