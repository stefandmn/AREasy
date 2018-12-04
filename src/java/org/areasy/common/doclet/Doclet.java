package org.areasy.common.doclet;

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

import com.lowagie.text.Chunk;
import com.lowagie.text.Element;
import com.lowagie.text.Paragraph;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.RootDoc;
import org.areasy.common.doclet.document.*;
import org.areasy.common.doclet.document.filters.Filter;
import org.areasy.common.doclet.document.filters.FilteredRootDoc;
import org.areasy.common.doclet.document.tags.HtmlParserWrapper;
import org.areasy.common.doclet.utilities.DocletUtility;
import org.areasy.common.doclet.utilities.PDFUtility;
import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * This javadoc doclet creates PDF output
 * for an API documentation.
 * <p/>
 * Please note that this doclet is a very old-fashioned,
 * straightforward batch-process application. It holds the
 * current state of the process in static variables which
 * also means that it is definitely NOT thread-safe.
 * 
 * @version $Id: Doclet.java,v 1.5 2008/05/14 09:36:48 swd\stefan.damian Exp $
 */
public class Doclet implements AbstractConfiguration
{
	/**
	 * Logger reference
	 */
	private static Logger log = LoggerFactory.getLog(Doclet.class);

	/**
	 * Index generation class reference.
	 */
	private static Index index = null;

	/**
	 * Reference to the PDF file.
	 */
	private static File file = null;

	// Stores list of inner classes
	private static Hashtable innerClassesList = new Hashtable();

	/**
	 * Constructor for PDFDoclet.
	 */
	public Doclet()
	{
        //nothing to do.
	}

	/**
	 * Constructs a PDFDoclet object.
	 *
	 * @param filename The filename of the target otuput PDF file.
	 */
	public Doclet(String filename)
	{
		String workDir = DefaultConfiguration.getWorkDir();
		boolean relative = false;

		if ((filename.startsWith(".") || filename.startsWith("..")) && (workDir != null)) relative = true;

		if (filename.indexOf(File.separator) != -1)
		{
			String dirname = filename.substring(0, filename.lastIndexOf(File.separator));
			File dir = (!relative ? new File(dirname) : new File(workDir, dirname));

			dir.mkdirs();
		}

		file = (!relative ? new File(filename) : new File(workDir, filename));
	}

	/**
	 * Processes all packages of a given javadoc root.
	 *
	 * @param root The javadoc root.
	 * @throws Exception
	 */
	private void listClasses(RootDoc root) throws Exception
	{
		try
		{
			Document.initialize();

			Document.open();

			Bookmarks.init();
		}
		catch (IOException e)
		{
			log.error("Failed to open PDF file for writing", e);
			return;
		}

		// Prepare index
		index = new Index(Document.getWriter(), Document.instance());

		// Print title page
		CustomTitle title = new CustomTitle(Document.instance());
		State.setCurrentHeaderType(HEADER_DETAILS);
		title.print();

		// Print description page
		CustomDescription description = new CustomDescription(Document.instance());
		State.setCurrentHeaderType(HEADER_DETAILS);
		description.print();

		// Print javadoc overview
		State.setCurrentHeaderType(HEADER_OVERVIEW);
		Overview.print(root);
		
		State.setCurrentHeaderType(HEADER_API);

		ClassDoc[] classes = root.classes();

		Map pkgMap = null;
		if (DefaultConfiguration.getPackageOrder() != null)
		{
			// Use a custom comparator to sort the list of packages in a custom way
			Comparator cmp = new Comparator()
			{

				public int compare(Object packageName1, Object packageName2)
				{
					String packageOrder = DefaultConfiguration.getPackageOrder();
					PackageDoc package1 = (PackageDoc) packageName1;
					PackageDoc package2 = (PackageDoc) packageName2;
					int retval1 = packageOrder.indexOf(package1.name());
					int retval2 = packageOrder.indexOf(package2.name());

					if (retval1 < retval2) return -1;
					if (retval1 > retval2) return 1;

					return 0;
				}
			};

			pkgMap = new TreeMap(cmp);
		}
		else
		{
			// Use a treemap to create an alphabetically sorted list of all packages
			pkgMap = new TreeMap();
		}

		// Iterate through all single, separately specified classes
		for (int i = 0; i < classes.length; i++)
		{
			// Fetch the classes list for the package of this class
			List classList = (List) pkgMap.get(classes[i].containingPackage());
			if (classList == null)
			{
				// If there's no list for this package yet, create one
				classList = new ArrayList();
				pkgMap.put(classes[i].containingPackage(), classList);
			}

			// Store class in the list for this package
			classList.add(classes[i]);
		}

		// Prepare alphabetically sorted list of all classes for bookmarks
		Bookmarks.prepareBookmarkEntries(pkgMap);

		// Now process all packages and classes
		for (Iterator i = pkgMap.entrySet().iterator(); i.hasNext();)
		{
			// Get package..
			Map.Entry entry = (Map.Entry) i.next();
			PackageDoc pkgDoc = (PackageDoc) entry.getKey();
			List pkgList = (List) entry.getValue();

			// Get list of classes in package...
			ClassDoc[] pkgClasses = (ClassDoc[]) pkgList.toArray(new ClassDoc[pkgList.size()]);
			State.increasePackageChapter();

			// Print package info (includes printing classes info)
			printPackage(pkgDoc, pkgClasses);
		}

		Appendices.print();

		index.create();

		Bookmarks.createBookmarkOutline();

		String endMessage = "PDF completed: " + file.getPath();
		String line = DocletUtility.getLine(endMessage.length());
		log.debug(line);
		log.debug(endMessage);
		log.debug(line);

		// step 5: we close the document
		Document.close();
	}

	/**
	 * Processes one Java package of the whole API.
	 *
	 * @param packageDoc The javadoc information for the package.
	 * @throws Exception
	 */
	private void printPackage(PackageDoc packageDoc, ClassDoc[] packageClasses) throws Exception
	{
		State.setCurrentPackage(packageDoc.name());
		State.setCurrentDoc(packageDoc);

		Document.newPage();

		String packageName = State.getCurrentPackage();

		// Text "package"
		State.setCurrentClass("");

		Paragraph label = new Paragraph((float) 22.0, LB_PACKAGE, Fonts.getFont(TEXT_FONT, BOLD, 18));
		Document.add(label);

		Paragraph titlePara = new Paragraph((float) 30.0, "");
		// Name of the package (large font)
		Chunk titleChunk = new Chunk(packageName, Fonts.getFont(TEXT_FONT, BOLD, 30));
		titleChunk.setLocalDestination(packageName);
		if (State.getCurrentFile() != null)
		{
			String packageAnchor = Destinations.createAnchorDestination(State.getCurrentFile(), null);
			titlePara.add(PDFUtility.createAnchor(packageAnchor, titleChunk.font()));
		}

		titlePara.add(titleChunk);
		Document.add(titlePara);

		// Some empty space
		Document.add(new Paragraph((float) 20.0, " "));

		State.setContinued(true);

		String packageText = DocletUtility.getComment(packageDoc);
		Element[] objs = HtmlParserWrapper.createPdfObjects(packageText);

		if (objs.length == 0)
		{
			String packageDesc = DocletUtility.stripLineFeeds(packageText);
			Document.add(new Paragraph((float) 11.0, packageDesc, Fonts.getFont(TEXT_FONT, 10)));
		}
		else PDFUtility.printPdfElements(objs);

		State.setContinued(false);

		State.increasePackageSection();

		printClasses(DocletUtility.sort(packageClasses), packageDoc);
	}

	/**
	 * Processes all classes of one Java package..
	 *
	 * @param classDocs  The javadoc information list for the classes.
	 * @param packageDoc The javadoc information for the package
	 *                   which the classes belong to.
	 * @throws Exception
	 */
	private void printClasses(ClassDoc[] classDocs, PackageDoc packageDoc) throws Exception
	{
		for (int i = 0; i < classDocs.length; i++)
		{

			// Avoid processing a class which has already been processed as an inner class
			if (innerClassesList.get(classDocs[i]) == null)
			{
				ClassDoc doc = (ClassDoc) classDocs[i];
				printClassWithInnerClasses(doc, packageDoc);
			}
		}
	}

	/**
	 * This method prints the doc of a class and of all
	 * its inner classes. It calls itself recursively
	 * to make sure that also nested inner classes are
	 * printed correctly.
	 *
	 * @param doc        The doc of the class to print.
	 * @param packageDoc The packagedoc of the containing package.
	 * @throws Exception If something failed.
	 */
	private void printClassWithInnerClasses(ClassDoc doc, PackageDoc packageDoc) throws Exception
	{
		Classes.printClass(doc, packageDoc);

		ClassDoc[] innerClasses = doc.innerClasses();
		if (innerClasses != null && innerClasses.length > 0)
		{
			for (int u = 0; u < innerClasses.length; u++)
			{
				// Check if this inner class has not yet been handled
				if (innerClassesList.get(innerClasses[u]) == null)
				{
					innerClassesList.put(innerClasses[u], "x");
					
					State.setInnerClass(true);
					ClassDoc innerDoc = (ClassDoc) innerClasses[u];
					printClassWithInnerClasses(innerDoc, packageDoc);

					State.setInnerClass(false);
				}
			}
		}
	}

	/**
	 * Main doclet method.
	 *
	 * @param rootDoc The root of the javadoc information.
	 * @return True if the javadoc generation was successful,
	 *         false if it failed.
	 */
	public static boolean start(RootDoc rootDoc)
	{
		try
		{
			FilteredRootDoc root = new FilteredRootDoc(rootDoc);

			DefaultConfiguration.start(root);

			Filter.initialize();
			TagList.initialize();
			Appendices.initialize();

			String outputFilename = DefaultConfiguration.getString(ARG_PDF, ARG_VAL_PDF);

			// Prepare list of classes and packages
			DocletUtility.buildPackageList(root);

			// Do some pre-processing first (building class derivation trees)
			ImplementorsInformation.initialize(root);
			ImplementorsInformation.collectInformation();

			Doclet doclet = new Doclet(outputFilename);

			if (root.classes() != null) doclet.listClasses(root);
				else DocletUtility.error("No classes available");

			return true;

		}
		catch (Throwable e)
		{
			DocletUtility.error("Exception", e);

			return false;
		}
	}

	/**
	 * Doclet method called by Javadoc to recognize
	 * custom parameters.
	 *
	 * @param option The parameter found in the command line
	 * @return Zero (0) if the parameter is unknown, or the number
	 *         of parts that make up the whole parameter.
	 */
	public static int optionLength(String option)
	{
		if (option.equals("-" + ARG_WORKDIR)) return 2;

		if (option.equals("-" + ARG_SOURCEPATH)) return 2;

		if (option.equals("-" + ARG_PDF)) return 2;

		if (option.equals("-" + ARG_CONFIG)) return 2;

		if (option.equals("-" + ARG_DEBUG)) return 1;

		if (option.equals("-" + ARG_AUTHOR)) return 1;

		if (option.equals("-" + ARG_VERSION)) return 1;

		if (option.equals("-" + ARG_GROUP)) return 3;

		if (option.equals("-" + ARG_DONTSPEC)) return 2;

		if (option.equals("-" + ARG_SORT)) return 2;

		if (option.equals("-" + ARG_SINCE)) return 1;

		if (option.equals("-" + ARG_SUMMARY_TABLE)) return 1;

		if (option.equals("-" + ARG_CREATE_LINKS)) return 1;

		if (option.equals("-" + ARG_ALLOW_ENCRYPTION)) return 1;

		if (option.equals("-" + ARG_ALLOW_PRINTING)) return 1;

		if (option.equals("-" + ARG_HEADER_LEFT)) return 2;

		if (option.equals("-" + ARG_HEADER_CENTER)) return 2;

		if (option.equals("-" + ARG_HEADER_RIGHT)) return 2;

		if (option.equals("-" + ARG_PGN_TYPE)) return 2;

		if (option.equals("-" + ARG_PGN_ALIGNMENT)) return 2;

		if (option.equals("-" + ARG_CREATE_FRAME)) return 1;

		if (option.equals("-" + ARG_DOC_TITLE_PAGE)) return 1;

		if (option.equals("-" + ARG_DOC_TITLE_FILE)) return 2;

		if (option.equals("-" + ARG_DOC_TITLE)) return 2;

		if (option.equals("-" + ARG_DOC_AUTHOR)) return 2;

		if (option.equals("-" + ARG_DOC_COPYRIGHT)) return 2;

		if (option.equals("-" + ARG_FONT_TEXT_NAME)) return 2;

		if (option.equals("-" + ARG_FONT_TEXT_ENC)) return 2;

		if (option.equals("-" + ARG_FONT_CODE_NAME)) return 2;

		if (option.equals("-" + ARG_FONT_CODE_ENC)) return 2;

		if (option.startsWith("-" + ARG_APPENDIX_PREFIX)) return 2;

		return 0;
	}

	/**
	 * Returns a reference to the Index object.
	 *
	 * @return The index object.
	 */
	public static Index getIndex()
	{
		return index;
	}

	/**
	 * Returns a reference to the PDF file object.
	 *
	 * @return The PDF file object.
	 */
	public static File getPdfFile()
	{
		return file;
	}

	/**
	 * Set a reference to the PDF file object.
	 *
	 * @param f The PDF file object.
	 */
	public static void setPdfFile(File f)
	{
		file = f;
	}
}
