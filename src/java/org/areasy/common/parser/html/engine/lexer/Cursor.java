package org.areasy.common.parser.html.engine.lexer;

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

import org.areasy.common.parser.html.utilities.sort.Ordered;

import java.io.Serializable;

/**
 * A bookmark in a page.
 * This class remembers the page it came from and its position within the page.
 */
public class Cursor
		implements
		Serializable,
		Ordered,
		Cloneable
{

	/**
	 * This cursor's position.
	 */
	protected int mPosition;

	/**
	 * This cursor's page.
	 */
	protected Page mPage;

	/**
	 * Construct a <code>Cursor</code> from the page and position given.
	 *
	 * @param page   The page this cursor is on.
	 * @param offset The character offset within the page.
	 */
	public Cursor(Page page, int offset)
	{
		mPage = page;
		mPosition = offset;
	}

	/**
	 * Get this cursor's page.
	 *
	 * @return The page associated with this cursor.
	 */
	public Page getPage()
	{
		return (mPage);
	}

	/**
	 * Get the position of this cursor.
	 *
	 * @return The cursor position.
	 */
	public int getPosition()
	{
		return (mPosition);
	}

	/**
	 * Set the position of this cursor.
	 *
	 * @param position The new cursor position.
	 */
	public void setPosition(int position)
	{
		mPosition = position;
	}

	/**
	 * Move the cursor position ahead one character.
	 */
	public void advance()
	{
		mPosition++;
	}

	/**
	 * Move the cursor position back one character.
	 */
	public void retreat()
	{
		mPosition--;
		if (0 > mPosition)
		{
			mPosition = 0;
		}
	}

	/**
	 * Make a new cursor just like this one.
	 *
	 * @return The new cursor positioned where <code>this</code> one is,
	 *         and referring to the same page.
	 */
	public Cursor dup()
	{
		try
		{
			return ((Cursor) clone());
		}
		catch (CloneNotSupportedException cnse)
		{
			return (new Cursor(getPage(), getPosition()));
		}
	}

	public String toString()
	{
		int row;
		int column;
		StringBuffer ret;

		ret = new StringBuffer(9 * 3 + 3); // three ints and delimiters
		ret.append(getPosition());
		ret.append("[");
		if (null != mPage)
		{
			ret.append(mPage.row(this));
		}
		else
		{
			ret.append("?");
		}
		ret.append(",");
		if (null != mPage)
		{
			ret.append(mPage.column(this));
		}
		else
		{
			ret.append("?");
		}
		ret.append("]");

		return (ret.toString());
	}

	//
	// Ordered interface
	//

	/**
	 * Compare one reference to another.
	 *
	 * @see org.areasy.common.parser.html.utilities.sort.Ordered
	 */
	public int compare(Object that)
	{
		Cursor r = (Cursor) that;
		return (getPosition() - r.getPosition());
	}
}

       