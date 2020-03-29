package org.areasy.common.doclet.document;

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

import com.sun.javadoc.Doc;
import com.sun.javadoc.Tag;
import org.areasy.common.doclet.AbstractConfiguration;
import org.areasy.common.doclet.DefaultConfiguration;
import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;
import org.areasy.common.support.configuration.Configuration;

import java.util.*;

/**
 * This class builds a list of all tags in a given doc
 * object, such as a method doc. When the list is returned for printing, it
 * is sorted and filtered according to the configuration
 * (for instance, the 'author' tag is only returned if it's enabled in the configuration).
 *
 * @version $Id: TagList.java,v 1.3 2008/05/14 09:36:47 swd\stefan.damian Exp $
 */
public class TagList implements AbstractConfiguration
{
	/**
	 * Logger reference
	 */
	private static Logger log = LoggerFactory.getLog(TagList.class);

	/**
	 * Stores the tags and their values.
	 */
	private Hashtable allTags = new Hashtable();

	/**
	 * Stores the print labels for the tags.
	 */
	private static Properties labels = new Properties();

	/**
	 * Pre-sets some tag name labels.
	 */
	public static void initialize()
	{
		// First set some default values
		setTagLabel(DOC_TAG_PARAM, LB_PARM_TAG);
		setTagLabel(DOC_TAG_RETURN, LB_RETURNS_TAG);
		setTagLabel(DOC_TAG_THROWS, LB_EXCEP_TAG);
		setTagLabel(DOC_TAG_EXCEPTION, LB_EXCEP_TAG);
		setTagLabel(DOC_TAG_VERSION, LB_VERSION_TAG);
		setTagLabel(DOC_TAG_SEE, LB_SEE_TAG);
		setTagLabel(DOC_TAG_AUTHOR, LB_AUTHOR_TAG);
		setTagLabel(DOC_TAGS_DEPRECATED, LB_DEPRECATED_TAG);
		setTagLabel(DOC_TAG_SINCE, LB_SINCE_TAG);

		// Now override with configuration
		Configuration props = DefaultConfiguration.getConfiguration();
		Iterator names = props.getKeys();
		int len = ARG_LB_TAGS_PREFIX.length();
		while (names.hasNext())
		{
			String value = (String) names.next();
			if (value.startsWith(ARG_LB_TAGS_PREFIX))
			{
				// extract name of tag to set label for
				String tagName = value.substring(len, value.length());
				setTagLabel(tagName, DefaultConfiguration.getString(value, ""));
			}
		}
	}

	/**
	 * Sets the printing label for a certain tag.
	 *
	 * @param name  The name of the tag (like "@revision").
	 * @param label The label to print for that tag.
	 */
	public static void setTagLabel(String name, String label)
	{
		if (!name.startsWith("@")) name = "@" + name;

		labels.setProperty(name.toLowerCase(), label);
	}

	/**
	 * Returns the label for a given tag or an empty String,
	 * if it's an unknown tag.
	 *
	 * @param name The name of the tag.
	 * @return The label to print for that tag, or an empty string.
	 */
	public static String getTagLabel(String name)
	{
		if (!name.startsWith("@"))
		{
			name = "@" + name;
		}
		String label = labels.getProperty(name.toLowerCase());
		return label;
	}

	/**
	 * Creates a taglist object.
	 *
	 * @param doc The doc with the tags (like a methoddoc).
	 */
	public TagList(Doc doc)
	{
		Tag[] tags = doc.tags();
		if (tags != null && tags.length > 0)
		{
			// fill all tags into the hashtable
			for (int i = 0; i < tags.length; i++)
			{
				String name = tags[i].name().toLowerCase();
				if (!isDisabled(name))
				{
					// Do not print the "deprecated" tag separately for a class,
					// because it's printed right after the heritation tree
					// as a special remark already.
					if (!name.equalsIgnoreCase(DOC_TAGS_DEPRECATED))
					{
						Tag[] namedTags = doc.tags(name);
						if (namedTags != null && namedTags.length > 0)
						{
							allTags.put(name, namedTags);
						}
					}
				}
			}
		}
	}

	/**
	 * @param name
	 * @return
	 */
	public Tag[] getTags(String name)
	{

		if (!name.startsWith("@"))
		{
			name = "@" + name;
		}

		return (Tag[]) allTags.get(name);
	}

	/**
	 * Returns a list of all tags with their names.
	 *
	 * @return The array with all tag names. If there are none, the array
	 *         will have 0 entries.
	 */
	public String[] getTagNames()
	{
		Enumeration names = allTags.keys();
		ArrayList nameList = new ArrayList();
		while (names.hasMoreElements())
		{
			String name = (String) names.nextElement();
			nameList.add(name);
		}

		return (String[]) nameList.toArray(new String[0]);
	}

	/**
	 * Checks if a tag with a certain name is disabled and should
	 * not be printed.
	 *
	 * @param tag The tag to check.
	 * @return True if the tag should not be printed.
	 */
	private boolean isDisabled(String tag)
	{
		boolean disabled = false;
		String comp = tag;
		if (comp.startsWith("@"))
		{
			comp = comp.substring(1, comp.length());
		}
		if (comp.equalsIgnoreCase(DOC_TAG_VERSION))
		{
			if (!DefaultConfiguration.isShowVersionActive())
			{
				disabled = true;
			}
		}
		else if (comp.equalsIgnoreCase(DOC_TAG_AUTHOR))
		{
			if (!DefaultConfiguration.isShowAuthorActive())
			{
				disabled = true;
			}
		}
		else if (comp.equalsIgnoreCase(DOC_TAG_SINCE))
		{
			if (!DefaultConfiguration.isShowSinceActive())
			{
				disabled = true;
			}
		}
		else
		{
			// custom tags are disabled, if there is no label defined for them
			if (getTagLabel(tag) == null)
			{
				log.warn("Custom tag not printed (no label defined): " + tag);
				disabled = true;
			}
		}
		return disabled;
	}
}
