package org.areasy.common.doclet.document.filters;

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

import org.areasy.common.logger.LoggerFactory;
import org.areasy.common.doclet.AbstractConfiguration;
import org.areasy.common.doclet.DefaultConfiguration;
import com.sun.javadoc.*;

import java.util.*;


/**
 * Utility class for filtering out classes and methods from
 * a print process using standard or custom tags.
 *
 * @version $Id: Filter.java,v 1.3 2008/05/14 09:36:47 swd\stefan.damian Exp $
 */
public class Filter
{

	/**
	 * Logger reference
	 */
	private static org.areasy.common.logger.Logger log = LoggerFactory.getLog(Filter.class);

	/**
	 * Storage for all general filter tag names.
	 */
	private static Hashtable filterTags = new Hashtable();

	/**
	 * Storage for names of all filter tag that must be check contentwise.
	 */
	private static Properties filterTagsContent = new Properties();

	/**
	 * Initializes the list of filter tags.
	 */
	public static void initialize()
	{
		if (DefaultConfiguration.isFilterActive())
		{
			String tags = DefaultConfiguration.getString(AbstractConfiguration.ARG_FILTER_TAGS, "");

			// The tag names are in a comma separated list, so we use a tokenizer to parse the list
			StringTokenizer tok = new StringTokenizer(tags, ",");
			while (tok.hasMoreTokens())
			{
				String tag = "@" + tok.nextToken();
				filterTags.put(tag, "X");
			}

			// Now check for specific filter tags
			Iterator keys = DefaultConfiguration.getConfiguration().getKeys();
			while (keys.hasNext())
			{
				String key = ((String) keys.next()).trim();
				if (key.startsWith(AbstractConfiguration.ARG_FILTER_TAG_PREFIX))
				{
					String tagName = "@" + key.substring(key.lastIndexOf(".") + 1, key.length());
					String content = DefaultConfiguration.getString(key, "").toLowerCase();

					filterTagsContent.setProperty(tagName, content);
				}
			}
		}
	}

	/**
	 * Checks if the given field doc must be filtered from the
	 * list of field docs returned by a MethodDoc.
	 *
	 * @param fieldDoc The fieldDoc to check.
	 * @return True if it must be filtered (not printed).
	 */
	public static boolean mustBeFiltered(FieldDoc fieldDoc)
	{
		return mustBeFiltered(fieldDoc.tags());
	}

	/**
	 * Checks if the given method doc must be filtered from the
	 * list of method docs returned by a ClassDoc.
	 *
	 * @param methodDoc The methodDoc to check.
	 * @return True if it must be filtered (not printed).
	 */
	public static boolean mustBeFiltered(MethodDoc methodDoc)
	{
		return mustBeFiltered(methodDoc.tags());
	}

	/**
	 * Checks if the given constructor doc must be filtered from the
	 * list of constructor docs returned by a ClassDoc.
	 *
	 * @param constructorDoc The constructorDoc to check.
	 * @return True if it must be filtered (not printed).
	 */
	public static boolean mustBeFiltered(ConstructorDoc constructorDoc)
	{
		return mustBeFiltered(constructorDoc.tags());
	}

	/**
	 * Checks if the given class doc must be filtered from the
	 * list of class docs returned by a PackageDoc.
	 *
	 * @param classDoc The classDoc to check.
	 * @return True if it must be filtered (not printed).
	 */
	public static boolean mustBeFiltered(ClassDoc classDoc)
	{
		return mustBeFiltered(classDoc.tags());
	}

	/**
	 * Checks if a given doc containing the given tags must be
	 * filtered from printing or not.
	 *
	 * @return True if it must be filtered (not printed).
	 */
	private static boolean mustBeFiltered(Tag[] tags)
	{
		for (int i = 0; i < tags.length; i++)
		{
			String tagName = tags[i].name();
			if (filterTags.get(tagName) != null)
			{
				// If we found one tag that is in the list of
				// tags to filter after, this method must be printed.
				return false;
			}
			if (filterTagsContent.get(tagName) != null)
			{
				String correct = filterTagsContent.getProperty(tagName);
				String content = tags[i].text().toLowerCase();
				if (content.indexOf(correct) != -1)
				{
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Creates a list of FilteredClassDoc elements for the
	 * given input list of ClassDoc objects. The list itself
	 * is also filtered according to the configuration.
	 *
	 * @param input The list of ClassDoc objects to be filtered.
	 * @return The filtered list of wrapper objects.
	 */
	public static ClassDoc[] createFilteredClassesList(ClassDoc[] input)
	{
		FilteredClassDoc[] list = new FilteredClassDoc[input.length];
		for (int i = 0; i < input.length; i++)
		{
			list[i] = new FilteredClassDoc(input[i]);
		}

		// Otherwise, create a filtered list.
		ArrayList filteredList = new ArrayList();
		for (int i = 0; i < list.length; i++)
		{
			// If it doesn't have to be filtered out, add it to the list.
			if (!Filter.mustBeFiltered(list[i])) filteredList.add(list[i]);
		}

		ClassDoc[] result = new ClassDoc[filteredList.size()];

		return (ClassDoc[]) filteredList.toArray(result);
	}

	/**
	 * Creates a list of ClassDoc elements for the
	 * given input list of ClassDoc objects. The list
	 * is filtered according to the configuration.
	 *
	 * @param input The list of ClassDoc objects to be filtered.
	 * @return The filtered list of ClassDoc objects.
	 */
	public static ClassDoc[] createClassesList(ClassDoc[] input)
	{
		// Otherwise, create a filtered list.
		ArrayList filteredList = new ArrayList();
		for (int i = 0; i < input.length; i++)
		{
			// If it doesn't have to be filtered out, add it to the list.
			if (!Filter.mustBeFiltered(input[i])) filteredList.add(input[i]);
		}

		ClassDoc[] result = new ClassDoc[filteredList.size()];

		return (ClassDoc[]) filteredList.toArray(result);
	}

	/**
	 * Creates a list of MethodDoc elements for the
	 * given input list of MethodDoc objects. The list
	 * is filtered according to the configuration.
	 *
	 * @param input The list of MethodDoc objects to be filtered.
	 * @return The filtered list of MethodDoc objects.
	 */
	public static MethodDoc[] createMethodList(MethodDoc[] input)
	{
		// Otherwise, create a filtered list.
		ArrayList filteredList = new ArrayList();
		for (int i = 0; i < input.length; i++)
		{
			// If it doesn't have to be filtered out, add it to the list.
			if (!Filter.mustBeFiltered(input[i]))
			{
				filteredList.add(input[i]);
			}
		}

		MethodDoc[] result = new MethodDoc[filteredList.size()];

		return (MethodDoc[]) filteredList.toArray(result);
	}

	/**
	 * Creates a list of FieldDoc elements for the
	 * given input list of FieldDoc objects. The list
	 * is filtered according to the configuration.
	 *
	 * @param input The list of FieldDoc objects to be filtered.
	 * @return The filtered list of FieldDoc objects.
	 */
	public static FieldDoc[] createFieldList(FieldDoc[] input)
	{
		// Otherwise, create a filtered list.
		ArrayList filteredList = new ArrayList();
		for (int i = 0; i < input.length; i++)
		{
			// If it doesn't have to be filtered out, add it to the list.
			if (!Filter.mustBeFiltered(input[i])) filteredList.add(input[i]);
		}

		FieldDoc[] result = new FieldDoc[filteredList.size()];

		return (FieldDoc[]) filteredList.toArray(result);
	}

	/**
	 * Creates a list of ConstructorDoc elements for the
	 * given input list of ConstructorDoc objects. The list
	 * is filtered according to the configuration.
	 *
	 * @param input The list of ConstructorDoc objects to be filtered.
	 * @return The filtered list of ConstructorDoc objects.
	 */
	public static ConstructorDoc[] createConstructorList(ConstructorDoc[] input)
	{
		// Otherwise, create a filtered list.
		ArrayList filteredList = new ArrayList();
		for (int i = 0; i < input.length; i++)
		{
			// If it doesn't have to be filtered out, add it to the list.
			if (!Filter.mustBeFiltered(input[i])) filteredList.add(input[i]);
		}

		ConstructorDoc[] result = new ConstructorDoc[filteredList.size()];
		
		return (ConstructorDoc[]) filteredList.toArray(result);
	}

}
