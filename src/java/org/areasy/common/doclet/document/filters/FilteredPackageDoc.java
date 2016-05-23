package org.areasy.common.doclet.document.filters;

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

import org.areasy.common.doclet.DefaultConfiguration;
import com.sun.javadoc.*;

/**
 * @version $Id: FilteredPackageDoc.java,v 1.3 2008/05/14 09:36:47 swd\stefan.damian Exp $
 */
public class FilteredPackageDoc implements PackageDoc
{

	private PackageDoc packageDoc = null;

	/**
	 * Default constructor.
	 */
	public FilteredPackageDoc()
	{
		//nothing to do
	}

	/**
	 * Creates an instance of this class which wraps
	 * the given PackageDoc object.
	 *
	 * @param packageDoc The PackageDoc object to be wrapped.
	 */
	public FilteredPackageDoc(PackageDoc packageDoc)
	{
		this.packageDoc = packageDoc;
	}

	/**
	 * Allows to set the PackageDoc object which this class
	 * should wrap.
	 *
	 * @param packageDoc The PackageDoc object to be wrapped.
	 */
	public void setPackageDoc(PackageDoc packageDoc)
	{
		this.packageDoc = packageDoc;
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.PackageDoc#allClasses()
	 */
	public ClassDoc[] allClasses()
	{
		if (!DefaultConfiguration.isFilterActive())
		{
			return packageDoc.allClasses();
		}
		return Filter.createFilteredClassesList(packageDoc.allClasses());
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.PackageDoc#errors()
	 */
	public ClassDoc[] errors()
	{
		if (!DefaultConfiguration.isFilterActive())
		{
			return packageDoc.errors();
		}
		return Filter.createFilteredClassesList(packageDoc.errors());
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.PackageDoc#exceptions()
	 */
	public ClassDoc[] exceptions()
	{
		if (!DefaultConfiguration.isFilterActive())
		{
			return packageDoc.exceptions();
		}
		return Filter.createFilteredClassesList(packageDoc.exceptions());
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.PackageDoc#interfaces()
	 */
	public ClassDoc[] interfaces()
	{
		if (!DefaultConfiguration.isFilterActive())
		{
			return packageDoc.interfaces();
		}
		return Filter.createFilteredClassesList(packageDoc.interfaces());
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.PackageDoc#ordinaryClasses()
	 */
	public ClassDoc[] ordinaryClasses()
	{
		if (!DefaultConfiguration.isFilterActive())
		{
			return packageDoc.ordinaryClasses();
		}
		return Filter.createFilteredClassesList(packageDoc.ordinaryClasses());
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.PackageDoc#allClasses(boolean)
	 */
	public ClassDoc[] allClasses(boolean arg0)
	{
		if (!DefaultConfiguration.isFilterActive())
		{
			return packageDoc.allClasses(arg0);
		}
		return Filter.createFilteredClassesList(packageDoc.allClasses(arg0));
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.PackageDoc#findClass(java.lang.String)
	 */
	public ClassDoc findClass(String arg0)
	{
		return packageDoc.findClass(arg0);
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.Doc#isClass()
	 */
	public boolean isClass()
	{
		return packageDoc.isClass();
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.Doc#isConstructor()
	 */
	public boolean isConstructor()
	{
		return packageDoc.isConstructor();
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.Doc#isError()
	 */
	public boolean isError()
	{
		return packageDoc.isError();
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.Doc#isException()
	 */
	public boolean isException()
	{
		return packageDoc.isException();
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.Doc#isField()
	 */
	public boolean isField()
	{
		return packageDoc.isField();
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.Doc#isIncluded()
	 */
	public boolean isIncluded()
	{
		return packageDoc.isIncluded();
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.Doc#isInterface()
	 */
	public boolean isInterface()
	{
		return packageDoc.isInterface();
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.Doc#isMethod()
	 */
	public boolean isMethod()
	{
		return packageDoc.isMethod();
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.Doc#isOrdinaryClass()
	 */
	public boolean isOrdinaryClass()
	{
		return packageDoc.isOrdinaryClass();
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.Doc#seeTags()
	 */
	public SeeTag[] seeTags()
	{
		return packageDoc.seeTags();
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.Doc#position()
	 */
	public SourcePosition position()
	{
		return packageDoc.position();
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.Doc#firstSentenceTags()
	 */
	public Tag[] firstSentenceTags()
	{
		return packageDoc.firstSentenceTags();
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.Doc#inlineTags()
	 */
	public Tag[] inlineTags()
	{
		return packageDoc.inlineTags();
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.Doc#tags()
	 */
	public Tag[] tags()
	{
		return packageDoc.tags();
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object arg0)
	{
		return packageDoc.compareTo(arg0);
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.Doc#commentText()
	 */
	public String commentText()
	{
		return packageDoc.commentText();
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.Doc#getRawCommentText()
	 */
	public String getRawCommentText()
	{
		return packageDoc.getRawCommentText();
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.Doc#name()
	 */
	public String name()
	{
		return packageDoc.name();
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.Doc#setRawCommentText(java.lang.String)
	 */
	public void setRawCommentText(String arg0)
	{
		packageDoc.setRawCommentText(arg0);
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.Doc#tags(java.lang.String)
	 */
	public Tag[] tags(String arg0)
	{
		return packageDoc.tags(arg0);
	}

    /**
     * Get included annotation types in this package.
     *
     * @return included annotation types in this package.
     * @since 1.5
     */
    public AnnotationTypeDoc[] annotationTypes()
	{
		return packageDoc.annotationTypes();
	}

	/**
     * Get the annotations of this package.
     * Return an empty array if there are none.
     *
     * @return the annotations of this package.
     * @since 1.5
     */
    public AnnotationDesc[] annotations()
	{
		return packageDoc.annotations();
	}

	/**
     * Get included enum types in this package.
     *
     * @return included enum types in this package.
     * @since 1.5
     */
    public ClassDoc[] enums()
	{
		return packageDoc.enums();
	}

	/**
     * Is this Doc item an annotation type element?
     *
     * @return true if it represents an annotation type element
     * @since 1.5
     */
    public boolean isAnnotationTypeElement()
	{
		return packageDoc.isAnnotationTypeElement();
	}

	/**
     * Is this Doc item an enum type?
     *
     * @return true if it represents an enum type
     * @since 1.5
     */
    public boolean isEnum()
	{
		return packageDoc.isEnum();
	}

	/**
     * Is this Doc item an annotation type?
     *
     * @return true if it represents an annotation type
     * @since 1.5
     */
    public boolean isAnnotationType()
	{
		return packageDoc.isAnnotationType();
	}

	/**
     * Is this Doc item an enum constant?
     *
     * @return true if it represents an enum constant
     * @since 1.5
     */
    public boolean isEnumConstant()
	{
		return packageDoc.isEnumConstant();
	}
}
