package org.areasy.common.doclet.document;

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

import org.areasy.common.data.StringUtility;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents one entry in the bookmarks outline tree.
 * @version $Id: BookmarkEntry.java,v 1.3 2008/05/14 09:36:47 swd\stefan.damian Exp $
 */
public class BookmarkEntry
{
	/**
	 * The bookmark label.
	 */
	private String label = "";

	/**
	 * Name of the internal destination.
	 */
	private String destinationName = null;

	/**
	 * List of child nodes.
	 */
	private ArrayList children = new ArrayList();

	/**
	 * Creates the root bookmark entry.
	 */
	public BookmarkEntry()
	{
		//nothing to do here
	}

	/**
	 * Creates a bookmark entry with a certain label.
	 *
	 * @param label The label for the bookmark entry.
	 * @param name  Name of the internal destination.
	 */
	public BookmarkEntry(String label, String name)
	{
		this.label = label;
		this.destinationName = name;
	}

	/**
	 * Connects a child node to this entry.
	 *
	 * @param entry A sub-node of this bookmark entry.
	 */
	public void addChild(BookmarkEntry entry)
	{
		if (entry == null) throw new IllegalArgumentException("Null entry not allowed!");

		children.add(entry);
	}

	/**
	 * Returns all children of this entry as an array.
	 * The order of the child nodes in the array
	 * corresponds to the order in which they have
	 * been added to this entry.
	 *
	 * @return The array with child nodes.
	 */
	public BookmarkEntry[] getChildren()
	{
		return (BookmarkEntry[]) children.toArray(new BookmarkEntry[children.size()]);
	}

	/**
	 * Returns the label of this bookmark entry
	 * or an empty string, if it was not set.
	 *
	 * @return The label text or an empty string.
	 */
	public String getLabel()
	{
		return label;
	}

	/**
	 * Set bookmark entry label.
	 * @param label string text
	 */
	public void setLabel(String label)
	{
		this.label = label;
	}

	/**
	 * Returns the name of the destination of this
	 * bookmark entry.
	 *
	 * @return The name of the destination.
	 */
	public String getDestinationName()
	{
		return destinationName;
	}

	/**
	 * Set bookmark destination name.
	 *
	 * @param destinationName string identifier.
	 */
	public void setDestinationName(String destinationName)
	{
		this.destinationName = destinationName;
	}

	public void setChildren(List list)
	{
		children.clear();
		children.addAll(list);
	}

	/**
	 * Add heading types line in the bookmark repository.
	 *
	 * @param index header (heading) index.
	 * @param text label entry.
	 */
	public String addBookmarkEntryByHeaderType(int index, String text)
	{
		if(StringUtility.isEmpty(text)) return null;

		BookmarkEntry entries[] = getChildren();

		int count = 0;
		int indexes = 0;

		for(int x = 0; x < entries.length; x++)
		{
			if(entries[x].getDestinationName().startsWith("_H"))
			{
				count++;

				if(indexes == 0)
				{
					String destinationName = entries[x].getDestinationName().substring(2).trim();
					String values[] = StringUtility.split(destinationName, '.');

					indexes = values.length;
				}
			}
		}

		if(index == indexes || (index > indexes && entries.length == 0))
		{
			String destinationName;

			if(index == 1) destinationName = "_H" + String.valueOf(count + 1);
				else destinationName = getDestinationName() + "." + String.valueOf(count + 1);

			BookmarkEntry entry = new BookmarkEntry(text, destinationName);
			addChild(entry);

			return destinationName;
		}
		else if(index > indexes)
		{
			BookmarkEntry entry = entries[entries.length - 1];
			return entry.addBookmarkEntryByHeaderType(index, text);
		}
		else return null;
	}

	/**
	 * Add a new bookmark entry into the bookmark tree structure.
	 *
	 * @param parent parent bookmark entry - and could be the label or the destination name.
	 * @param label text label for the actual entry.
	 * @return the destination name for the actual bookmark entry.
	 */
	public String addBookmarkEntry(String parent, String label)
	{
		if(parent == null || parent.length() == 0 || getDestinationName().equalsIgnoreCase(parent) || getLabel().equalsIgnoreCase(parent))
		{
			String destinationName = "_LOCAL:" + StringUtility.variable(label);

			BookmarkEntry entry = new BookmarkEntry(label, destinationName);
			addChild(entry);

			return destinationName;
		}
		else
		{
			String destinationName = null;
			BookmarkEntry entries[] = getChildren();

			for(int x = 0; destinationName == null && x < entries.length; x++)
			{
				destinationName = entries[x].addBookmarkEntry(parent, label);
			}

			return destinationName;
		}
	}

	/**
	 * Returns a string representation of the object. In general, the
     * <code>toString</code> method returns a string that
     * "textually represents" this object. The result should
     * be a concise but informative representation that is easy for a
     * person to read.
	 */
	public String toString()
	{
		return getDestinationName() + "@" + getLabel();	
	}
}

