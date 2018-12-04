package org.areasy.common.doclet.document;

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

import com.lowagie.text.pdf.PdfOutline;
import com.sun.javadoc.Doc;
import org.areasy.common.doclet.utilities.DocletUtility;

import java.io.File;

/**
 * Holds the state of the doclet creation process.
 *
 * @version $Id: State.java,v 1.3 2008/05/14 09:36:47 swd\stefan.damian Exp $
 */
public class State
{

	public static final int TYPE_NONE = -1;
	public static final int TYPE_CONSTRUCTOR = 1;
	public static final int TYPE_FIELD = 2;
	public static final int TYPE_METHOD = 3;

	private static int CURRENT_MEMBER_TYPE = TYPE_NONE;

	private static boolean lastTagEndedWithText = false;

	/**
	 * Determines if an overview page is currently being printed.
	 */
	private static boolean isOverview = false;

	/**
	 * Debug output flag.
	 */
	public static boolean debug = false;

	/**
	 * Current type of page header (none, class name, index..)
	 */
	private static int headerType = 0;

	/**
	 * Number of current chapter (package)
	 */
	private static int packageChapter = 0;

	/**
	 * Number of current section (class)
	 */
	private static int packageSection = 0;

	/**
	 * Number of current method (class)
	 */
	private static int packageMethod = 0;

	/**
	 * Number of current page
	 */
	private static int currentPage = 0;

	/**
	 * Determines if it is the last method to be managed.
	 */
	private static boolean last = false;

	/**
	 * Determines if it a text is continued on the next page.
	 */
	private static boolean isContinued = false;

	/**
	 * Determines if current class is an inner class
	 */
	private static boolean isInnerClass = false;

	/**
	 * Name of current package
	 */
	public static String currentPackage = "";

	/**
	 * Name of current class
	 */
	public static String currentClass = "";

	/**
	 * Name of current member
	 */
	public static String currentMember = "";

	/**
	 * Name of current method
	 */
	public static String currentMethod = "";

	/**
	 * Current Doc
	 */
	public static Doc currentDoc = null;

	/**
	 * Current File
	 */
	public static File currentFile = null;

	/**
	 * Current root outline for wrapping package outlines in bookmarks.
	 */
	public static PdfOutline rootPackagesOutline = null;

	/**
	 * Current package outline in bookmarks.
	 */
	public static PdfOutline packageOutline = null;

	/**
	 * Current class outline in bookmarks.
	 */
	public static PdfOutline classOutline = null;

	/**
	 * Current fields outline in bookmarks.
	 */
	public static PdfOutline fieldsOutline = null;

	/**
	 * Current constructors outline in bookmarks.
	 */
	public static PdfOutline constructorsOutline = null;

	/**
	 * Current methods outline in bookmarks.
	 */
	public static PdfOutline methodsOutline = null;

	/**
	 * Sets the current methods outline in the bookmarks.
	 *
	 * @param outline The current outline object instance.
	 */
	public static void setCurrentMethodsOutline(PdfOutline outline)
	{
		methodsOutline = outline;
	}

	/**
	 * Returns the current methods outline in the bookmarks.
	 *
	 * @return The current outline object instance.
	 */
	public static PdfOutline getCurrentMethodsOutline()
	{
		return methodsOutline;
	}

	/**
	 * Sets the current constructors outline in the bookmarks.
	 *
	 * @param outline The current outline object instance.
	 */
	public static void setCurrentConstructorsOutline(PdfOutline outline)
	{
		constructorsOutline = outline;
	}

	/**
	 * Returns the current constructors outline in the bookmarks.
	 *
	 * @return The current outline object instance.
	 */
	public static PdfOutline getCurrentConstructorsOutline()
	{
		return constructorsOutline;
	}

	/**
	 * Sets the current fields outline in the bookmarks.
	 *
	 * @param outline The current outline object instance.
	 */
	public static void setCurrentFieldsOutline(PdfOutline outline)
	{
		fieldsOutline = outline;
	}

	/**
	 * Returns the current fields outline in the bookmarks.
	 *
	 * @return The current outline object instance.
	 */
	public static PdfOutline getCurrentFieldsOutline()
	{
		return fieldsOutline;
	}

	/**
	 * Sets the current class outline in the bookmarks.
	 *
	 * @param outline The current outline object instance.
	 */
	public static void setCurrentClassOutline(PdfOutline outline)
	{
		classOutline = outline;
	}

	/**
	 * Returns the current class outline in the bookmarks.
	 *
	 * @return The current outline object instance.
	 */
	public static PdfOutline getCurrentClassOutline()
	{
		return classOutline;
	}

	/**
	 * Sets the current package outline in the bookmarks.
	 *
	 * @param outline The current outline object instance.
	 */
	public static void setCurrentPackageOutline(PdfOutline outline)
	{
		packageOutline = outline;
	}

	/**
	 * Returns the current package outline in the bookmarks.
	 *
	 * @return The current outline object instance.
	 */
	public static PdfOutline getCurrentPackageOutline()
	{
		return packageOutline;
	}

	/**
	 * Sets the current root packages outline in the bookmarks.
	 *
	 * @param outline The current outline object instance.
	 */
	public static void setCurrentRootPackagesOutline(PdfOutline outline)
	{
		rootPackagesOutline = outline;
	}

	/**
	 * Returns the current root packages outline in the bookmarks.
	 *
	 * @return The current outline object instance.
	 */
	public static PdfOutline getCurrentRootPackagesOutline()
	{
		return rootPackagesOutline;
	}

	/**
	 * Returns the type of the member which is currently
	 * being processed (constructor, field, method).
	 *
	 * @return The member type (see constant values).
	 */
	public static int getTypeOfCurrentMember()
	{
		return CURRENT_MEMBER_TYPE;
	}

	/**
	 * Sets the type of the member which is currently
	 * being processed (constructor, field, method).
	 *
	 * @param type The type of the member (must be one of
	 *             the constant values of this class).
	 */
	public static void setTypeOfCurrentMember(int type)
	{
		if (type == TYPE_CONSTRUCTOR || type == TYPE_FIELD ||
				type == TYPE_METHOD || type == TYPE_NONE)
		{
			CURRENT_MEMBER_TYPE = type;
		}
		else
		{
			String msg = "Invalid member type: " + type;
			throw new IllegalArgumentException(msg);
		}
	}

	/**
	 * Returns the currently processed Doc.
	 *
	 * @return The currently processed Doc (or null).
	 */
	public static Doc getCurrentDoc()
	{
		return currentDoc;
	}

	/**
	 * Sets the currently processed Doc.
	 */
	public static void setCurrentDoc(Doc doc)
	{
		currentDoc = doc;
		setCurrentFile(DocletUtility.getSourceFile(doc));
	}

	/**
	 * Returns the currently processed File.
	 *
	 * @return The currently processed File (or null).
	 */
	public static File getCurrentFile()
	{
		return currentFile;
	}

	/**
	 * Sets the currently processed File.
	 */
	public static void setCurrentFile(File file)
	{
		currentFile = file;
	}

	public static void setLastTagEndedWithText(boolean value)
	{
		lastTagEndedWithText = value;
	}

	public boolean getLastTagEndedWithText()
	{
		return lastTagEndedWithText;
	}

	/**
	 * Activates or deactivates debug output.
	 *
	 * @param value True to activate debug output.
	 */
	public static void setDebug(boolean value)
	{
		debug = value;
	}

	/**
	 * Determines if debug output is active or not.
	 *
	 * @return True if debug output is active, false otherwise.
	 */
	public static boolean isDebug()
	{
		return debug;
	}

	/**
	 * Determines if currently an overview page is
	 * being processed.
	 *
	 * @return True if an overview page is being processed.
	 */
	public static boolean isOverview()
	{
		return isOverview;
	}

	/**
	 * Defines if currently an overview page is
	 * being processed.
	 *
	 * @param value True if an overview page is being processed.
	 */
	public static void setOverview(boolean value)
	{
		isOverview = value;
	}

	/**
	 * State method which returns the name of the
	 * class currently processed by the doclet.
	 *
	 * @return The name of the current class.
	 */
	public static String getCurrentClass()
	{
		return currentClass;
	}

	/**
	 * Sets the name of the class that is
	 * currently processed.
	 *
	 * @param value The name of the class.
	 */
	public static void setCurrentClass(String value)
	{
		currentClass = value;
	}

	/**
	 * State method which returns the name of the
	 * package currently processed by the doclet.
	 *
	 * @return The name of the current package.
	 */
	public static String getCurrentPackage()
	{
		return currentPackage;
	}

	/**
	 * Sets the name of the package that is
	 * currently processed.
	 *
	 * @param value The name of the package.
	 */
	public static void setCurrentPackage(String value)
	{
		currentPackage = value;
	}

	/**
	 * State method which returns the name of the
	 * member currently processed by the doclet.
	 *
	 * @return The name of the current member.
	 */
	public static String getCurrentMember()
	{
		return currentMember;
	}

	/**
	 * Sets the name of the member that is
	 * currently processed.
	 *
	 * @param value The name of the member.
	 */
	public static void setCurrentMember(String value)
	{
		currentMember = value;
	}

	/**
	 * State method which returns the name of the
	 * method currently processed by the doclet.
	 *
	 * @return The name of the current method.
	 */
	public static String getCurrentMethod()
	{
		return currentMethod;
	}

	/**
	 * Sets the name of the method that is
	 * currently processed.
	 *
	 * @param value The name of the method.
	 */
	public static void setCurrentMethod(String value)
	{
		currentMethod = value;
	}

	/**
	 * Defines if the document is currently in the
	 * state of a "continuing" section.
	 *
	 * @param value True if a documentation text is
	 *              currently being continued.
	 */
	public static void setContinued(boolean value)
	{
		isContinued = value;
	}

	/**
	 * Determines if the document is currently in the state of
	 * a "continuing" section (member documentation). If this is
	 * the case when the end of a page is reached, the footer
	 * of that page will get an additional remark ("continued
	 * on the next page") and the header of the next page
	 * another one ("continued from last page").
	 *
	 * @return true if currently something is continued.
	 */
	public static boolean isContinued()
	{
		return isContinued;
	}

	/**
	 * Sets the current page to a specific value.
	 *
	 * @param page The number of the current page.
	 */
	public static void setCurrentPage(int page)
	{
		currentPage = page;
	}

	/**
	 * Returns the current page number.
	 *
	 * @return The number of the current page.
	 */
	public static int getCurrentPage()
	{
		return currentPage;
	}

	/**
	 * Defines if current class is an inner class.
	 *
	 * @param value True if it's an inner class.
	 */
	public static void setInnerClass(boolean value)
	{
		isInnerClass = value;
	}

	/**
	 * Determines if current class is an inner class.
	 *
	 * @return True if it's an inner class.
	 */
	public static boolean isInnerClass()
	{
		return isInnerClass;
	}

	/**
	 * State method which returns true if it is the last method
	 * of the class.
	 *
	 * @return boolean to warn if it is the last method.
	 */
	public static boolean isLastMethod()
	{
		return last;
	}

	/**
	 * Defines if the method currently processed is
	 * the last.
	 *
	 * @param value True if it is the last method.
	 */
	public static void setLastMethod(boolean value)
	{
		last = value;
	}

	/**
	 * Returns the number of the current package method
	 * (used for the navigation frame entry numbering).
	 *
	 * @return The number of the current package method.
	 */
	public static int getPackageMethod()
	{
		return packageMethod;
	}

	/**
	 * Increases the number of the current package method
	 * (used for the navigation frame entry numbering).
	 */
	public static void increasePackageMethod()
	{
		packageMethod++;
	}

	/**
	 * Returns the number of the current package section
	 * (used for the navigation frame entry numbering).
	 *
	 * @return The number of the current package section.
	 */
	public static int getPackageSection()
	{
		return packageSection;
	}

	/**
	 * Increases the number of the current package section
	 * (used for the navigation frame entry numbering).
	 */
	public static void increasePackageSection()
	{
		packageSection++;
	}

	/**
	 * Returns the number of the current package chapter
	 * (used for the navigation frame entry numbering).
	 *
	 * @return The number of the current package chapter.
	 */
	public static int getPackageChapter()
	{
		return packageChapter;
	}

	/**
	 * Increases the number of the current package chapter
	 * (used for the navigation frame entry numbering).
	 */
	public static void increasePackageChapter()
	{
		packageChapter++;
	}

	/**
	 * Sets the type of header to be used for the
	 * current page (none, class name or index title).
	 *
	 * @param value The header type.
	 */
	public static void setCurrentHeaderType(int value)
	{
		headerType = value;
	}

	/**
	 * Returns the type of header to be used for
	 * the current page (none, class name or
	 * index title).
	 *
	 * @return The header type value.
	 */
	public static int getCurrentHeaderType()
	{
		return headerType;
	}
}
