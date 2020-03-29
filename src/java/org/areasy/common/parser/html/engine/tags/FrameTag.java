package org.areasy.common.parser.html.engine.tags;

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

/**
 * Identifies a frame tag
 */
public class FrameTag extends Tag
{

	/**
	 * The set of names handled by this tag.
	 */
	private static final String[] mIds = new String[]{"FRAME"};

	/**
	 * Create a new frame tag.
	 */
	public FrameTag()
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
	 * Returns the location of the frame.
	 *
	 * @return The contents of the SRC attribute converted to an absolute URL.
	 */
	public String getFrameLocation()
	{
		String src;

		src = getAttribute("SRC");
		if (null == src)
		{
			return "";
		}
		else
		{
			return (getPage().getLinkProcessor().extract(src, getPage().getUrl()));
		}
	}

	/**
	 * Sets the location of the frame.
	 *
	 * @param url The new frame location.
	 */
	public void setFrameLocation(String url)
	{
		setAttribute("SRC", url);
	}

	public String getFrameName()
	{
		return (getAttribute("NAME"));
	}

	/**
	 * Print the contents of the FrameTag.
	 */
	public String toString()
	{
		return "FRAME TAG : Frame " + getFrameName() + " at " + getFrameLocation() + "; begins at : " + getStartPosition() + "; ends at : " + getEndPosition();
	}
}
