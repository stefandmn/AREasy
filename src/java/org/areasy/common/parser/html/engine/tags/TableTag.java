package org.areasy.common.parser.html.engine.tags;

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

import org.areasy.common.parser.html.engine.NodeFilter;
import org.areasy.common.parser.html.engine.filters.*;
import org.areasy.common.parser.html.utilities.NodeList;

/**
 * A table tag.
 */
public class TableTag extends CompositeTag
{

	/**
	 * The set of names handled by this tag.
	 */
	private static final String[] mIds = new String[]{"TABLE"};

	/**
	 * The set of end tag names that indicate the end of this tag.
	 */
	private static final String[] mEndTagEnders = new String[]{"BODY", "HTML"};

	/**
	 * Create a new table tag.
	 */
	public TableTag()
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
	 * Get the row tags within this table.
	 *
	 * @return The rows directly contained by this table.
	 */
	public TableRow[] getRows()
	{
		NodeList kids;
		NodeClassFilter cls;
		HasParentFilter recursion;
		NodeFilter filter;
		TableRow[] ret;

		kids = getChildren();
		if (null != kids)
		{
			cls = new NodeClassFilter(TableTag.class);
			recursion = new HasParentFilter(null);
			filter = new OrFilter(new AndFilter(cls,
					new IsEqualFilter(this)),
					new AndFilter(// recurse up the parent chain
							new NotFilter(cls), // but not past the first table
							recursion));
			recursion.mFilter = filter;
			kids = kids.extractAllNodesThatMatch(// it's a row, and has this table as it's enclosing table
					new AndFilter(new NodeClassFilter(TableRow.class),
							filter), true);
			ret = new TableRow[kids.size()];
			kids.copyToNodeArray(ret);
		}
		else
		{
			ret = new TableRow[0];
		}

		return (ret);
	}

	/**
	 * Get the number of rows in this table.
	 */
	public int getRowCount()
	{
		return (getRows().length);
	}

	/**
	 * Get the row at the given index.
	 */
	public TableRow getRow(int i)
	{
		TableRow[] rows;
		TableRow ret;

		rows = getRows();
		if (i < rows.length)
		{
			ret = rows[i];
		}
		else
		{
			ret = null;
		}

		return (ret);
	}

	public String toString()
	{
		return
				"TableTag\n" +
				"********\n" +
				toHtml();
	}

}
