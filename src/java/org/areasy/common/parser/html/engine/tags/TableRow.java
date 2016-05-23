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
 * A table row tag.
 */
public class TableRow extends CompositeTag
{

	/**
	 * The set of names handled by this tag.
	 */
	private static final String[] mIds = new String[]{"TR"};

	/**
	 * The set of end tag names that indicate the end of this tag.
	 */
	private static final String[] mEndTagEnders = new String[]{"TABLE"};

	/**
	 * Create a new table row tag.
	 */
	public TableRow()
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
	 * Get the column tags within this row.
	 */
	public TableColumn[] getColumns()
	{
		NodeList kids;
		NodeClassFilter cls;
		HasParentFilter recursion;
		NodeFilter filter;
		TableColumn[] ret;

		kids = getChildren();
		if (null != kids)
		{
			cls = new NodeClassFilter(TableRow.class);
			recursion = new HasParentFilter(null);
			filter = new OrFilter(new AndFilter(cls,
					new IsEqualFilter(this)),
					new AndFilter(// recurse up the parent chain
							new NotFilter(cls), // but not past the first row
							recursion));
			recursion.mFilter = filter;
			kids = kids.extractAllNodesThatMatch(// it's a column, and has this row as it's enclosing row
					new AndFilter(new NodeClassFilter(TableColumn.class),
							filter), true);
			ret = new TableColumn[kids.size()];
			kids.copyToNodeArray(ret);
		}
		else
		{
			ret = new TableColumn[0];
		}

		return (ret);
	}

	/**
	 * Get the number of columns in this row.
	 */
	public int getColumnCount()
	{
		return (getColumns().length);
	}

	/**
	 * Get the header of this table
	 *
	 * @return Table header tags contained in this row.
	 */
	public TableHeader[] getHeaders()
	{
		NodeList kids;
		NodeClassFilter cls;
		HasParentFilter recursion;
		NodeFilter filter;
		TableHeader[] ret;

		kids = getChildren();
		if (null != kids)
		{
			cls = new NodeClassFilter(TableRow.class);
			recursion = new HasParentFilter(null);
			filter = new OrFilter(new AndFilter(cls,
					new IsEqualFilter(this)),
					new AndFilter(// recurse up the parent chain
							new NotFilter(cls), // but not past the first row
							recursion));
			recursion.mFilter = filter;
			kids = kids.extractAllNodesThatMatch(// it's a header, and has this row as it's enclosing row
					new AndFilter(new NodeClassFilter(TableHeader.class),
							filter), true);
			ret = new TableHeader[kids.size()];
			kids.copyToNodeArray(ret);
		}
		else
		{
			ret = new TableHeader[0];
		}

		return (ret);
	}

	/**
	 * Get the number of headers in this row.
	 *
	 * @return The count of header tags in this row.
	 */
	public int getHeaderCount()
	{
		return (getHeaders().length);
	}

	/**
	 * Checks if this table has a header
	 *
	 * @return <code>true</code> if there is a header tag.
	 */
	public boolean hasHeader()
	{
		return (0 != getHeaderCount());
	}
}
