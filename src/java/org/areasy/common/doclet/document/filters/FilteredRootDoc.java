package org.areasy.common.doclet.document.filters;

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
import org.areasy.common.doclet.DefaultConfiguration;


/**
 * Wraps a Javadoc RootDoc object to allow to filter out
 * packages / classes / methods transparently.
 *
 * @version $Id: FilteredRootDoc.java,v 1.3 2008/05/14 09:36:47 swd\stefan.damian Exp $ 
 */
public class FilteredRootDoc implements RootDoc
{
	/**
	 * Wrapped RootDoc object reference.
	 */
	private RootDoc rootDoc = null;

	/**
	 * Default constructor.
	 */
	public FilteredRootDoc()
	{
		//nothing to do
	}

	/**
	 * Creates an instance of this class which wraps the
	 * given RootDoc object.
	 *
	 * @param rootDoc The RootDoc object to be wrapped.
	 */
	public FilteredRootDoc(RootDoc rootDoc)
	{
		this.rootDoc = rootDoc;
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.RootDoc#classes()
	 */
	public ClassDoc[] classes()
	{
		if (!DefaultConfiguration.isFilterActive()) return rootDoc.classes();
		
		return Filter.createFilteredClassesList(rootDoc.classes());
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.RootDoc#specifiedClasses()
	 */
	public ClassDoc[] specifiedClasses()
	{
		if (!DefaultConfiguration.isFilterActive()) return rootDoc.specifiedClasses();

		return Filter.createFilteredClassesList(rootDoc.specifiedClasses());
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.RootDoc#specifiedPackages()
	 */
	public PackageDoc[] specifiedPackages()
	{
		PackageDoc[] list = rootDoc.specifiedPackages();
		FilteredPackageDoc[] result = new FilteredPackageDoc[list.length];

		for (int i = 0; i < list.length; i++)
		{
			result[i] = new FilteredPackageDoc(list[i]);
		}

		return result;
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.RootDoc#options()
	 */
	public String[][] options()
	{
		return rootDoc.options();
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.RootDoc#classNamed(java.lang.String)
	 */
	public ClassDoc classNamed(String arg0)
	{
		return rootDoc.classNamed(arg0);
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.RootDoc#packageNamed(java.lang.String)
	 */
	public PackageDoc packageNamed(String arg0)
	{
		return rootDoc.packageNamed(arg0);
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.Doc#isClass()
	 */
	public boolean isClass()
	{
		return rootDoc.isClass();
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.Doc#isConstructor()
	 */
	public boolean isConstructor()
	{
		return rootDoc.isConstructor();
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.Doc#isError()
	 */
	public boolean isError()
	{
		return rootDoc.isError();
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.Doc#isException()
	 */
	public boolean isException()
	{
		return rootDoc.isException();
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.Doc#isField()
	 */
	public boolean isField()
	{
		return rootDoc.isField();
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.Doc#isIncluded()
	 */
	public boolean isIncluded()
	{
		return rootDoc.isIncluded();
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.Doc#isInterface()
	 */
	public boolean isInterface()
	{
		return rootDoc.isInterface();
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.Doc#isMethod()
	 */
	public boolean isMethod()
	{
		return rootDoc.isMethod();
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.Doc#isOrdinaryClass()
	 */
	public boolean isOrdinaryClass()
	{
		return rootDoc.isOrdinaryClass();
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.Doc#seeTags()
	 */
	public SeeTag[] seeTags()
	{
		return rootDoc.seeTags();
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.Doc#position()
	 */
	public SourcePosition position()
	{
		return rootDoc.position();
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.Doc#firstSentenceTags()
	 */
	public Tag[] firstSentenceTags()
	{
		return rootDoc.firstSentenceTags();
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.Doc#inlineTags()
	 */
	public Tag[] inlineTags()
	{
		return rootDoc.inlineTags();
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.Doc#tags()
	 */
	public Tag[] tags()
	{
		return rootDoc.tags();
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object arg0)
	{
		return rootDoc.compareTo(arg0);
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.Doc#commentText()
	 */
	public String commentText()
	{
		return rootDoc.commentText();
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.Doc#getRawCommentText()
	 */
	public String getRawCommentText()
	{
		return rootDoc.getRawCommentText();
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.Doc#name()
	 */
	public String name()
	{
		return rootDoc.name();
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.Doc#setRawCommentText(java.lang.String)
	 */
	public void setRawCommentText(String arg0)
	{
		rootDoc.setRawCommentText(arg0);
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.Doc#tags(java.lang.String)
	 */
	public Tag[] tags(String arg0)
	{
		return rootDoc.tags(arg0);
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.DocErrorReporter#printError(java.lang.String)
	 */
	public void printError(String arg0)
	{
		rootDoc.printError(arg0);
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.DocErrorReporter#printNotice(java.lang.String)
	 */
	public void printNotice(String arg0)
	{
		rootDoc.printNotice(arg0);
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.DocErrorReporter#printWarning(java.lang.String)
	 */
	public void printWarning(String arg0)
	{
		rootDoc.printWarning(arg0);
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.DocErrorReporter#printError(com.sun.javadoc.SourcePosition, java.lang.String)
	 */
	public void printError(SourcePosition arg0, String arg1)
	{
		rootDoc.printError(arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.DocErrorReporter#printNotice(com.sun.javadoc.SourcePosition, java.lang.String)
	 */
	public void printNotice(SourcePosition arg0, String arg1)
	{
		rootDoc.printNotice(arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.DocErrorReporter#printWarning(com.sun.javadoc.SourcePosition, java.lang.String)
	 */
	public void printWarning(SourcePosition arg0, String arg1)
	{
		rootDoc.printWarning(arg0, arg1);
	}

    /**
     * Is this Doc item an enum type?
     *
     * @return true if it represents an enum type
     * @since 1.5
     */
    public boolean isEnum()
	{
		return rootDoc.isEnum();
	}

	/**
     * Is this Doc item an annotation type?
     *
     * @return true if it represents an annotation type
     * @since 1.5
     */
    public boolean isAnnotationType()
	{
		return rootDoc.isAnnotationType();
	}

	/**
     * Is this Doc item an annotation type element?
     *
     * @return true if it represents an annotation type element
     * @since 1.5
     */
    public boolean isAnnotationTypeElement()
	{
		return rootDoc.isAnnotationTypeElement();
	}

	/**
     * Is this Doc item an enum constant?
     *
     * @return true if it represents an enum constant
     * @since 1.5
     */
    public boolean isEnumConstant()
	{
		return rootDoc.isEnumConstant();
	}
}
