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

import com.lowagie.text.pdf.PdfAction;
import com.lowagie.text.pdf.PdfOutline;
import com.lowagie.text.pdf.PdfWriter;
import org.areasy.common.doclet.AbstractConfiguration;
import org.areasy.common.doclet.DefaultConfiguration;
import com.sun.javadoc.*;

import java.util.*;


/**
 * This class creates the bookmarks frame.
 * @version $Id: Bookmarks.java,v 1.4 2008/05/14 09:36:47 swd\stefan.damian Exp $
 */
public class Bookmarks implements AbstractConfiguration
{
	/**
	 * Stores a reference to the root PDF outline object.
	 */
	private static PdfOutline rootOutline = null;

	/**
	 * Stores a reference to the root node of the bookmark tree.
	 */
	private static BookmarkEntry rootEntry = null;

	private static TreeMap alphabeticalClassList = new TreeMap();

	/**
	 * Use this to store the bookmark entries for classes
	 * for re-using them in different branches of the tree
	 */
	private static TreeMap classBookmarks = new TreeMap();

	/**
	 * Use this to store the bookmark entries for packages
	 * for re-using them in different branches of the tree
	 */
	private static TreeMap packagesBookmarks = new TreeMap();

	/**
	 * This hashtable stores all packages for which the
	 * bookmark entry already has been connected in the outline.
	 */
	private static Hashtable usedPackages = new Hashtable();

	/**
	 * Initializes the bookmarks creation.
	 */
	public static void init()
	{
		PdfWriter writer = Document.getWriter();
		
		rootOutline = writer.getRootOutline();
		rootEntry = new BookmarkEntry();
	}

	/**
	 * Creates the tree branches for the classes and packages bookmarks.
	 */
	public static void prepareBookmarkEntries(Map packagesList)
	{
		for (Iterator i = packagesList.entrySet().iterator(); i.hasNext();)
		{
			// Get package..
			Map.Entry entry = (Map.Entry) i.next();
			List pkgList = (List) entry.getValue();

			// Get list of classes in package...
			ClassDoc[] pkgClasses = (ClassDoc[]) pkgList.toArray(new ClassDoc[pkgList.size()]);

			for (int no = 0; no < pkgClasses.length; no++)
			{
				alphabeticalClassList.put(pkgClasses[no].name(), pkgClasses[no]);
			}
		}

		// Now we have a list of packages and a list of classes.
		createClassesBookmarks(alphabeticalClassList);

		createPackagesBookmarks(packagesList);
	}

	/**
	 * Creates all bookmark entries for all classes.
	 *
	 * @param classesList The alphabetically sorted list of classes.
	 */
	private static void createClassesBookmarks(TreeMap classesList)
	{
		// Prepare bookmark entries for all classes
		String labelClasses = DefaultConfiguration.getString(ARG_LB_OUTLINE_CLASSES, LB_CLASSES);
		BookmarkEntry bookmarkClasses = Bookmarks.addStaticRootBookmark(labelClasses);

		Iterator iter = classesList.entrySet().iterator();

		while (iter.hasNext())
		{
			Map.Entry entry = (Map.Entry) iter.next();
			ClassDoc doc = (ClassDoc) entry.getValue();
			BookmarkEntry classBookmark = Bookmarks.addClassBookmark(bookmarkClasses, doc);
			
			classBookmarks.put(doc.qualifiedName(), classBookmark);
		}
	}

	/**
	 * Creates all bookmark entries for all packages.
	 *
	 * @param packagesList The list of all packages.
	 */
	private static void createPackagesBookmarks(Map packagesList)
	{
		Iterator iter = packagesList.entrySet().iterator();
		while (iter.hasNext())
		{
			Map.Entry entry = (Map.Entry) iter.next();
			PackageDoc doc = (PackageDoc) entry.getKey();
			String name = doc.name();

			BookmarkEntry packageBookmark = new BookmarkEntry(name, name);

			// Now create sub-entries for classes within that package

			ClassDoc[] classes = doc.allClasses();

			// Sort list of classes within a package alphabetically
			Map pkgMap = new TreeMap();
			for (int u = 0; u < classes.length; u++)
			{
				pkgMap.put(classes[u].qualifiedName(), classes[u]);
			}

			// Now iterate alphabetically
			for (Iterator i = pkgMap.keySet().iterator(); i.hasNext();)
			{
				String className = (String) i.next();
				BookmarkEntry classEntry = (BookmarkEntry) classBookmarks.get(className);

				// If filtering is active, no entry may exist for this class, so check for null
				if (classEntry != null) packageBookmark.addChild(classEntry);
			}

			packagesBookmarks.put(doc.name(), packageBookmark);
		}

		// Now connect package bookmarks according to default or -group parameters
		Properties groups = DefaultConfiguration.getGroups();

		String defaultGroup = "Packages";

		if (groups.size() != 0)
		{
			defaultGroup = "Other Packages";
			Enumeration groupNames = groups.keys();

			while (groupNames.hasMoreElements())
			{
				String groupName = (String) groupNames.nextElement();
				String groupList = groups.getProperty(groupName);
				connectPackages(groupName, groupList);
			}

			connectPackages(defaultGroup, "*");
		}
		else connectPackages(defaultGroup, "*");
	}

	/**
	 * Connects the previously created package bookmark entries of
	 * a group with the outline tree.
	 *
	 * @param groupName The name of the group
	 * @param list      The list of packages as defined by the -group parameter; "*" means all packages.
	 */
	private static void connectPackages(String groupName, String list)
	{
		BookmarkEntry bookmarkPackages = null;

		if (list.equals("*"))
		{
			// If there have been -group parameters, the root entry for all packages not in any group must be "Other Packages". If no -group
			// parameters have been specified, the root entry for all packages is just "Packages".
			String labelPackages = null;
			if (DefaultConfiguration.getGroups().size() > 0) labelPackages = DefaultConfiguration.getString(ARG_LB_OUTLINE_OTHERPACKAGES, LB_OTHERPACKAGES);
				else labelPackages = DefaultConfiguration.getString(ARG_LB_OUTLINE_PACKAGES, LB_PACKAGES);

			bookmarkPackages = new BookmarkEntry(labelPackages, null);

			Iterator packageNames = packagesBookmarks.keySet().iterator();
			while (packageNames.hasNext())
			{
				String name = (String) packageNames.next();
				if (usedPackages.get(name) == null)
				{
					BookmarkEntry packageBookmark = (BookmarkEntry) packagesBookmarks.get(name);
					bookmarkPackages.addChild(packageBookmark);
				}
			}

			if (bookmarkPackages.getChildren().length > 0) rootEntry.addChild(bookmarkPackages);

		}
		else
		{

			bookmarkPackages = Bookmarks.addStaticRootBookmark(groupName);

			// parse list of packages for group
			StringTokenizer tok = new StringTokenizer(list, ":");
			while (tok.hasMoreTokens())
			{
				String token = tok.nextToken();
				boolean matchStartsWith = false;

				// Check if exact package name is specified or with wildcard
				if (token.endsWith("*")) matchStartsWith = true;

				Iterator packageNames = packagesBookmarks.keySet().iterator();
				while (packageNames.hasNext())
				{
					boolean addPackage = false;
					String name = (String) packageNames.next();

					if (matchStartsWith) if (name.startsWith(token.substring(0, token.length() - 1))) addPackage = true;
						else if (name.equals(token)) addPackage = true;

					if (addPackage)
					{
						BookmarkEntry packageBookmark = (BookmarkEntry) packagesBookmarks.get(name);
						bookmarkPackages.addChild(packageBookmark);
						// Store in list of used packages, so that its not used
						// again when the rest of the packages (not included in any group) is added to the outline.
						usedPackages.put(name, packageBookmark);
					}
				}
			}
		}
	}

	/**
	 * Finally creates the entire outline tree with all
	 * bookmarks in the PDF document.
	 */
	public static void createBookmarkOutline()
	{
		createBookmarks(rootOutline, rootEntry.getChildren());
	}

	/**
	 * Creates entries for all the given bookmark entry objects.
	 * If any of them has child nodes, the method calls itself
	 * recursively to process them as well.
	 *
	 * @param parent  The parent PDF outline object.
	 * @param entries The bookmark entries for which to add outline objects.
	 */
	private static void createBookmarks(PdfOutline parent, BookmarkEntry[] entries)
	{
		if (entries == null) return;

		for (int i = 0; i < entries.length; i++)
		{
			String name = entries[i].getDestinationName();

			PdfAction action = null;

			if (name == null) action = new PdfAction();
				else action = PdfAction.gotoLocalPage(name, false);

			PdfOutline outline = new PdfOutline(parent, action, entries[i].getLabel());
			outline.setOpen(false);

			createBookmarks(outline, entries[i].getChildren());
		}
	}

	/**
	 * Adds a bookmark entry which will be in the root of
	 * the bookmark outline.
	 *
	 * @param label The label for the entry.
	 * @param dest  The named destination to which the entry points.
	 * @return The newly created bookmark entry object.
	 */
	public static BookmarkEntry addRootBookmark(String label, String dest)
	{
		BookmarkEntry entry = new BookmarkEntry(label, dest);
		rootEntry.addChild(entry);

		return entry;
	}

	/**
	 * Adds a bookmark entry which will be in the root of
	 * the bookmark outline. The entry will be static, meaning
	 * that a click on it will not let the PDF viewer jump
	 * to any position in the document. This can be used to
	 * create parent nodes which don't have any sensible
	 * target in the document by themselves.
	 *
	 * @param label The label for this entry.
	 * @return The newly created bookmark entry object.
	 */
	public static BookmarkEntry addStaticRootBookmark(String label)
	{
		BookmarkEntry entry = new BookmarkEntry(label, null);
		rootEntry.addChild(entry);

		return entry;
	}

	/**
	 * Adds a bookmark entry which will become a child node
	 * of a given parent entry.
	 *
	 * @param parent The parent bookmark entry.
	 * @param label  The label for this entry.
	 * @param dest   The named destination to which the entry points.
	 * @return The newly created bookmark entry object.
	 */
	public static BookmarkEntry addSubBookmark(BookmarkEntry parent, String label, String dest)
	{
		BookmarkEntry entry = new BookmarkEntry(label, dest);
		parent.addChild(entry);

		return entry;
	}

	/**
	 * Creates a bookmark entry for a class and adds it to a parent bookmark entry.
	 *
	 * @param parent   The parent bookmark entry.
	 * @param classDoc The doc of the class.
	 * @return The newly created class bookmark entry with all member subentries.
	 */
	public static BookmarkEntry addClassBookmark(BookmarkEntry parent, ClassDoc classDoc)
	{
		// Create class entry
		String dest = classDoc.qualifiedName();
		String label = classDoc.name();
		
		BookmarkEntry entry = new BookmarkEntry(label, dest);
		parent.addChild(entry);

		// Create sub-entries for fields, constructors and methods

		FieldDoc[] fieldDocs = classDoc.fields();
		if (fieldDocs != null && fieldDocs.length > 0)
		{
			BookmarkEntry fields = new BookmarkEntry("Fields", null);
			entry.addChild(fields);
			addMemberEntries(fields, classDoc.fields(), false);
		}

		ConstructorDoc[] constructorDocs = classDoc.constructors();
		if (!classDoc.isInterface() && constructorDocs != null && constructorDocs.length > 0)
		{
			BookmarkEntry constructors = new BookmarkEntry("Constructors", null);
			entry.addChild(constructors);
			addMemberEntries(constructors, classDoc.constructors(), true);
		}

		MethodDoc[] methodDocs = classDoc.methods();
		if (methodDocs != null && methodDocs.length > 0)
		{
			BookmarkEntry methods = new BookmarkEntry("Methods", null);
			entry.addChild(methods);
			addMemberEntries(methods, classDoc.methods(), true);
		}

		return entry;
	}

	/**
	 * Adds the bookmark entries of a set of members of a class
	 * (like all methods, all fields or all constructors) to
	 * the bookmark entry of the class.
	 *
	 * @param parent    The bookmark entry of the class.
	 * @param docs      The list of members to be added.
	 * @param isExec    If true, the members are executable (methods or constructors),
	 *                  if false, the members are fields.
	 */
	private static void addMemberEntries(BookmarkEntry parent, MemberDoc[] docs, boolean isExec)
	{
		for (int i = 0; i < docs.length; i++)
		{
			String label = docs[i].name();
			String dest = docs[i].qualifiedName();

			if (isExec)
			{
				ExecutableMemberDoc execDoc = (ExecutableMemberDoc) docs[i];
				dest = dest + execDoc.flatSignature();
				label = label + execDoc.flatSignature();
			}

			BookmarkEntry entry = new BookmarkEntry(label, dest);

			parent.addChild(entry);
		}
	}
}
