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

import com.sun.javadoc.*;
import org.areasy.common.doclet.AbstractConfiguration;
import org.areasy.common.doclet.utilities.DocletUtility;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * This class processes an entire javadoc tree and stores
 * information about classes (their known subclasses) and
 * interfaces (known implementing classes).
 *
 * @version $Id: ImplementorsInformation.java,v 1.3 2008/05/14 09:36:47 swd\stefan.damian Exp $
 */
public class ImplementorsInformation implements AbstractConfiguration
{
	private static RootDoc rootDoc = null;
	private static Hashtable implementors = new Hashtable();

	/**
	 * Constructor which takes a root of a javadoc
	 * to be processed entirely.
	 *
	 * @param docRoot The javadoc root object.
	 */
	public static void initialize(RootDoc docRoot)
	{
		rootDoc = docRoot;
	}

	/**
	 * Traverses the entire javadoc tree and
	 * stores information about which classes
	 * implement which interfaces, relations
	 * between sub- and superclasses etc.
	 */
	public static void collectInformation()
	{
		// 1st run: Create a node for every class
		ClassDoc[] classDocs = rootDoc.classes();

		// add overview file
		Destinations.addValidDestinationFile(DocletUtility.getSourceFile(rootDoc));

		for (int u = 0; u < classDocs.length; u++)
		{
			TreeNode node = new TreeNode(classDocs[u].qualifiedName());
			implementors.put(classDocs[u].qualifiedName(), node);
		}

		for (int u = 0; u < classDocs.length; u++)
		{

			// Build list of all valid destinations
			// 1st, the class itself
			Destinations.addValidDestination(classDocs[u].qualifiedName());
			Destinations.addValidDestinationFile(DocletUtility.getSourceFile(classDocs[u]));

			// 2nd, all its fields
			FieldDoc[] fields = classDocs[u].fields();
			for (int i = 0; i < fields.length; i++)
			{
				Destinations.addValidDestination(fields[i].qualifiedName());
			}
			// 3rd, all its constructors
			ConstructorDoc[] constructors = classDocs[u].constructors();
			for (int i = 0; i < constructors.length; i++)
			{
				Destinations.addValidDestination(constructors[i].qualifiedName());
				Destinations.addValidDestination(constructors[i].qualifiedName()
						+ "()");
				Destinations.addValidDestination(constructors[i].qualifiedName()
						+ constructors[i].signature());
				Destinations.addValidDestination(constructors[i].qualifiedName()
						+ constructors[i].flatSignature());
			}
			// 4th, all methods
			MethodDoc[] methods = classDocs[u].methods();
			for (int i = 0; i < methods.length; i++)
			{
				Destinations.addValidDestination(methods[i].qualifiedName());
				Destinations.addValidDestination(methods[i].qualifiedName()
						+ "()");
				Destinations.addValidDestination(methods[i].qualifiedName()
						+ methods[i].signature());
				Destinations.addValidDestination(methods[i].qualifiedName()
						+ methods[i].flatSignature());
			}
			// 5th, its package
			if (classDocs[u].containingPackage().isIncluded())
			{
				PackageDoc packageDoc = classDocs[u].containingPackage();
				Destinations.addValidDestination(packageDoc.name());
				Destinations.addValidDestinationFile(DocletUtility.getSourceFile(packageDoc));
			}

			// Now collect inheritance relation information

			if (classDocs[u].isInterface())
			{
				// Check if interface has superinterfaces
				ClassDoc[] superDocs = classDocs[u].interfaces();

				if ((superDocs != null) && (superDocs.length > 0))
				{
					ClassDoc superDoc = superDocs[0];

					// Since the superclass may be OUTSIDE this package
					// (like java.lang.Object) check if the superclass also exists as a node.
					String name = superDoc.qualifiedName();

					if (implementors.get(name) != null)
					{
						// If yes, connect it with the parent class in the tree
						TreeNode parentNode = (TreeNode) implementors.get(name);
						TreeNode node = (TreeNode) implementors.get(classDocs[u].qualifiedName());
						parentNode.addNode(node);
					}
				}
			}
			else
			{
				// Check if class has a superclass
				ClassDoc superDoc = classDocs[u].superclass();

				if (superDoc != null)
				{
					// Since the superclass may be OUTSIDE this package
					// (like java.lang.Object) check if the superclass also as a node.
					String name = superDoc.qualifiedName();

					if (implementors.get(name) != null)
					{
						// If yes, connect it with the parent class in the tree
						TreeNode parentNode = (TreeNode) implementors.get(name);
						TreeNode node = (TreeNode) implementors.get(classDocs[u].qualifiedName());
						parentNode.addNode(node);
					}
				}
			}
		}
	}

	/**
	 * Returns a list of names of classes that implement
	 * a certain interfaces.
	 *
	 * @param interfaceName The name of the interface in question.
	 * @return An array of class name Strings
	 */
	public static String[] getImplementingClasses(String interfaceName)
	{
		List implementingClassesList = new ArrayList();

		//get all the classes
		ClassDoc[] classes = rootDoc.classes();

		for (int i = 0; i < classes.length; i++)
		{
			ClassDoc aClass = classes[i];
			if (!aClass.isInterface())
			{
				ClassDoc[] interfaces = aClass.interfaces();
				for (int j = 0; j < interfaces.length; j++)
				{
					ClassDoc anInterface = interfaces[j];

					if (!aClass.isInterface()
							&& anInterface.qualifiedName().equalsIgnoreCase(interfaceName))
					{
						implementingClassesList.add(aClass.qualifiedName());
					}
				}
			}
		}

		String[] returnvalue = new String[implementingClassesList.size()];

		return (String[]) implementingClassesList.toArray(returnvalue);
	}

	/**
	 * Returns a list of names of classes that are subclasses
	 * of a certain given class.
	 *
	 * @param className The (super)class in question.
	 * @return An array of class name Strings. If there are no subclasses,
	 *         the array has a length of 0 (zero entries).
	 */
	public static String[] getKnownSubclasses(String className)
	{
		TreeNode node = (TreeNode) implementors.get(className);
		TreeNode[] nodes = node.getNodes();
		String[] result = new String[nodes.length];

		for (int i = 0; i < result.length; i++)
		{
			result[i] = nodes[i].getName();
		}

		return result;
	}

	/**
	 * Returns a list of names of classes that are direct subclasses
	 * of a certain given class.
	 *
	 * @param className The (super)class in question.
	 * @return An array of class name Strings. If there are no subclasses,
	 *         the array has a length of 0 (zero entries).
	 */
	public static String[] getDirectSubclasses(String className)
	{
		TreeNode node = (TreeNode) implementors.get(className);
		TreeNode[] nodes = node.next();
		String[] result = new String[nodes.length];

		for (int i = 0; i < result.length; i++)
		{
			result[i] = nodes[i].getName();
		}

		return result;
	}

	/**
	 * Returns a list of names of classes that are superclasses
	 * of a certain given class.
	 *
	 * @param className The (sub)class in question.
	 * @return An array of class name Strings. If there are no superclasses,
	 *         the array has a length of 0 (zero entries).
	 */
	public static String[] getKnownSuperclasses(String className)
	{
		TreeNode node = (TreeNode) implementors.get(className);
		TreeNode[] nodes = node.getParents();
		String[] result = new String[nodes.length];

		for (int i = 0; i < result.length; i++)
		{
			result[i] = nodes[i].getName();
		}

		return result;
	}
}
