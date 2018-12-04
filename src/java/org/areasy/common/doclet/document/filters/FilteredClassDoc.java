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

import com.sun.javadoc.*;
import org.areasy.common.doclet.DefaultConfiguration;


/**
 * Wraps a Javadoc RootDoc object to allow to filter out
 * packages / classes / methods transparently.
 *
 * @version $Id: FilteredClassDoc.java,v 1.3 2008/05/14 09:36:47 swd\stefan.damian Exp $
 */
public class FilteredClassDoc implements ClassDoc
{
	/**
	 * Wrapped ClassDoc object reference.
	 */
	private ClassDoc classDoc = null;

	/**
	 * Default constructor.
	 */
	public FilteredClassDoc()
	{
		//nothing to do here
	}

	/**
	 * Creates an instance of this class which wraps
	 * the given ClassDoc object.
	 *
	 * @param wrapped The ClassDoc to be wrapped.
	 */
	public FilteredClassDoc(ClassDoc wrapped)
	{
		this.classDoc = wrapped;
	}

	/**
	 * Allows to set the ClassDoc object that whould
	 * be wrapped by this class.
	 *
	 * @param wrapped The ClassDoc to be wrapped.
	 */
	public void setClassDoc(ClassDoc wrapped)
	{
		this.classDoc = wrapped;
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.ClassDoc#definesSerializableFields()
	 */
	public boolean definesSerializableFields()
	{
		return classDoc.definesSerializableFields();
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.ClassDoc#isAbstract()
	 */
	public boolean isAbstract()
	{
		return classDoc.isAbstract();
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.ClassDoc#isExternalizable()
	 */
	public boolean isExternalizable()
	{
		return classDoc.isExternalizable();
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.ClassDoc#isSerializable()
	 */
	public boolean isSerializable()
	{
		return classDoc.isSerializable();
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.ClassDoc#superclass()
	 */
	public ClassDoc superclass()
	{
		return classDoc.superclass();
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.ClassDoc#importedClasses()
	 */
	public ClassDoc[] importedClasses()
	{
		return classDoc.importedClasses();
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.ClassDoc#innerClasses()
	 */
	public ClassDoc[] innerClasses()
	{
		if (!DefaultConfiguration.isFilterActive())
		{
			// If no filtering is active, return the original list of fields
			return classDoc.innerClasses();
		}
		return Filter.createClassesList(classDoc.innerClasses());
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.ClassDoc#interfaces()
	 */
	public ClassDoc[] interfaces()
	{
		if (!DefaultConfiguration.isFilterActive())
		{
			// If no filtering is active, return the original list of fields
			return classDoc.interfaces();
		}
		return Filter.createClassesList(classDoc.interfaces());
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.ClassDoc#subclassOf(com.sun.javadoc.ClassDoc)
	 */
	public boolean subclassOf(ClassDoc arg0)
	{
		return classDoc.subclassOf(arg0);
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.ClassDoc#innerClasses(boolean)
	 */
	public ClassDoc[] innerClasses(boolean arg0)
	{
		return classDoc.innerClasses(arg0);
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.ClassDoc#constructors()
	 */
	public ConstructorDoc[] constructors()
	{
		if (!DefaultConfiguration.isFilterActive())
		{
			// If no filtering is active, return the original list of fields
			return classDoc.constructors();
		}
		return Filter.createConstructorList(classDoc.constructors());
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.ClassDoc#constructors(boolean)
	 */
	public ConstructorDoc[] constructors(boolean arg0)
	{
		if (!DefaultConfiguration.isFilterActive())
		{
			// If no filtering is active, return the original list of fields
			return classDoc.constructors(arg0);
		}
		return Filter.createConstructorList(classDoc.constructors(arg0));
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.ClassDoc#fields()
	 */
	public FieldDoc[] fields()
	{
		if (!DefaultConfiguration.isFilterActive())
		{
			// If no filtering is active, return the original list of fields
			return classDoc.fields();
		}
		return Filter.createFieldList(classDoc.fields());
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.ClassDoc#serializableFields()
	 */
	public FieldDoc[] serializableFields()
	{
		if (!DefaultConfiguration.isFilterActive())
		{
			// If no filtering is active, return the original list of fields
			return classDoc.serializableFields();
		}
		return Filter.createFieldList(classDoc.serializableFields());
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.ClassDoc#fields(boolean)
	 */
	public FieldDoc[] fields(boolean arg0)
	{
		if (!DefaultConfiguration.isFilterActive())
		{
			// If no filtering is active, return the original list of fields
			return classDoc.fields(arg0);
		}
		return Filter.createFieldList(classDoc.fields(arg0));
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.ClassDoc#methods()
	 */
	public MethodDoc[] methods()
	{
		if (!DefaultConfiguration.isFilterActive())
		{
			// If no filtering is active, return the original list of methods
			return classDoc.methods();
		}
		return Filter.createMethodList(classDoc.methods());
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.ClassDoc#serializationMethods()
	 */
	public MethodDoc[] serializationMethods()
	{
		if (!DefaultConfiguration.isFilterActive())
		{
			// If no filtering is active, return the original list of methods
			return classDoc.serializationMethods();
		}
		return Filter.createMethodList(classDoc.serializationMethods());
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.ClassDoc#methods(boolean)
	 */
	public MethodDoc[] methods(boolean arg0)
	{
		if (!DefaultConfiguration.isFilterActive())
		{
			// If no filtering is active, return the original list of methods
			return classDoc.methods(arg0);
		}
		return Filter.createMethodList(classDoc.methods(arg0));
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.ClassDoc#importedPackages()
	 */
	public PackageDoc[] importedPackages()
	{
		// Packages need not to be filtered
		return classDoc.importedPackages();
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.ClassDoc#findClass(java.lang.String)
	 */
	public ClassDoc findClass(String arg0)
	{
		return classDoc.findClass(arg0);
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.ProgramElementDoc#modifierSpecifier()
	 */
	public int modifierSpecifier()
	{
		return classDoc.modifierSpecifier();
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.ProgramElementDoc#isFinal()
	 */
	public boolean isFinal()
	{
		return classDoc.isFinal();
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.ProgramElementDoc#isPackagePrivate()
	 */
	public boolean isPackagePrivate()
	{
		return classDoc.isPackagePrivate();
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.ProgramElementDoc#isPrivate()
	 */
	public boolean isPrivate()
	{
		return classDoc.isPrivate();
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.ProgramElementDoc#isProtected()
	 */
	public boolean isProtected()
	{
		return classDoc.isProtected();
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.ProgramElementDoc#isPublic()
	 */
	public boolean isPublic()
	{
		return classDoc.isPublic();
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.ProgramElementDoc#isStatic()
	 */
	public boolean isStatic()
	{
		return classDoc.isStatic();
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.ProgramElementDoc#containingClass()
	 */
	public ClassDoc containingClass()
	{
		return classDoc.containingClass();
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.ProgramElementDoc#containingPackage()
	 */
	public PackageDoc containingPackage()
	{
		return classDoc.containingPackage();
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.ProgramElementDoc#modifiers()
	 */
	public String modifiers()
	{
		return classDoc.modifiers();
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.ProgramElementDoc#qualifiedName()
	 */
	public String qualifiedName()
	{
		return classDoc.qualifiedName();
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.Type#asClassDoc()
	 */
	public ClassDoc asClassDoc()
	{
		return classDoc.asClassDoc();
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.Type#dimension()
	 */
	public String dimension()
	{
		return classDoc.dimension();
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.Type#qualifiedTypeName()
	 */
	public String qualifiedTypeName()
	{
		return classDoc.qualifiedTypeName();
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.Type#typeName()
	 */
	public String typeName()
	{
		return classDoc.typeName();
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.Doc#isClass()
	 */
	public boolean isClass()
	{
		return classDoc.isClass();
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.Doc#isConstructor()
	 */
	public boolean isConstructor()
	{
		return classDoc.isConstructor();
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.Doc#isError()
	 */
	public boolean isError()
	{
		return classDoc.isError();
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.Doc#isException()
	 */
	public boolean isException()
	{
		return classDoc.isException();
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.Doc#isField()
	 */
	public boolean isField()
	{
		return classDoc.isField();
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.Doc#isIncluded()
	 */
	public boolean isIncluded()
	{
		return classDoc.isIncluded();
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.Doc#isInterface()
	 */
	public boolean isInterface()
	{
		return classDoc.isInterface();
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.Doc#isMethod()
	 */
	public boolean isMethod()
	{
		return classDoc.isMethod();
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.Doc#isOrdinaryClass()
	 */
	public boolean isOrdinaryClass()
	{
		return classDoc.isOrdinaryClass();
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.Doc#seeTags()
	 */
	public SeeTag[] seeTags()
	{
		return classDoc.seeTags();
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.Doc#position()
	 */
	public SourcePosition position()
	{
		return classDoc.position();
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.Doc#firstSentenceTags()
	 */
	public Tag[] firstSentenceTags()
	{
		return classDoc.firstSentenceTags();
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.Doc#inlineTags()
	 */
	public Tag[] inlineTags()
	{
		return classDoc.inlineTags();
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.Doc#tags()
	 */
	public Tag[] tags()
	{
		return classDoc.tags();
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object arg0)
	{
		return classDoc.compareTo(arg0);
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.Doc#commentText()
	 */
	public String commentText()
	{
		return classDoc.commentText();
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.Doc#getRawCommentText()
	 */
	public String getRawCommentText()
	{
		return classDoc.getRawCommentText();
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.Doc#name()
	 */
	public String name()
	{
		return classDoc.name();
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.Doc#setRawCommentText(java.lang.String)
	 */
	public void setRawCommentText(String arg0)
	{
		classDoc.setRawCommentText(arg0);
	}

	/* (non-Javadoc)
	 * @see com.sun.javadoc.Doc#tags(java.lang.String)
	 */
	public Tag[] tags(String arg0)
	{
		return classDoc.tags(arg0);
	}

    /**
     * Return interfaces implemented by this class or interfaces extended
     * by this interface. Includes only directly-declared interfaces, not
     * inherited interfaces.
     * Return an empty array if there are no interfaces.
     *
     * @return an array of interfaces, each represented by a
     *	       <code>ClassDoc</code> or a <code>ParametrizedType</code>.
     * @since 1.5
     */
    public  Type[] interfaceTypes()
	{
		return classDoc.interfaceTypes();
	}

    /**
     * Is this Doc item an annotation type?
     *
     * @return true if it represents an annotation type
     * @since 1.5
     */
    public boolean isAnnotationType()
	{
		return classDoc.isAnnotationType();
	}

	/**
	 * Return the enum constants if this is an enum type.
	 * Return an empty array if there are no enum constants, or if
	 * this is not an enum type.
	 *
	 * @return the enum constants if this is an enum type.
	 */
	public FieldDoc[] enumConstants()
	{
		return classDoc.enumConstants();
	}

    /**
     * Is this Doc item an enum constant?
     *
     * @return true if it represents an enum constant
     * @since 1.5
     */
    public boolean isEnumConstant()
	{
		return classDoc.isEnumConstant();
	}


    /**
     * Return this type as an <code>AnnotationTypeDoc</code> if it represents
     * an annotation type.  Array dimensions are ignored.
     *
     * @return an <code>AnnotationTypeDoc</code> if the type is an annotation
     *         type, or null if it is not.
     * @since 1.5
     */
    public AnnotationTypeDoc asAnnotationTypeDoc()
	{
		return classDoc.asAnnotationTypeDoc();
	}

    /**
     * Return the formal type parameters of this class or interface.
     * Return an empty array if there are none.
     *
     * @return the formal type parameters of this class or interface.
     * @since 1.5
     */
    public TypeVariable[] typeParameters()
	{
		return classDoc.typeParameters();
	}

    /**
     * Return the superclass of this class.  Return null if this is an
     * interface.  A superclass is represented by either a
     * <code>ClassDoc</code> or a <code>ParametrizedType</code>.
     *
     * @return the superclass of this class, or null if there is no superclass.
     * @since 1.5
     */
    public Type superclassType()
	{
		return classDoc.superclassType();
	}

    /**
     * Return the simple name of this type excluding any dimension information.
     * This is the unqualified name of the type, except that for nested types
     * only the identifier of the innermost type is included.
     * <p>
     * For example, the class {@code Outer.Inner} returns
     * "<code>Inner</code>".
     *
     * @since 1.5
     */
    public String simpleTypeName()
	{
		return classDoc.simpleTypeName();
	}

    /**
     * Return this type as a <code>WildcardType</code> if it represents
     * a wildcard type.
     *
     * @return a <code>WildcardType</code> if the type is a wildcard type,
     *         or null if it is not.
     * @since 1.5
     */
    public WildcardType asWildcardType()
	{
		return classDoc.asWildcardType();
	}

    /**
     * Is this Doc item an annotation type element?
     *
     * @return true if it represents an annotation type element
     * @since 1.5
     */
    public boolean isAnnotationTypeElement()
	{
		return classDoc.isAnnotationTypeElement();
	}

    /**
     * Return this type as a <code>ParameterizedType</code> if it represents
     * an invocation of a generic class or interface.  Array dimensions
     * are ignored.
     *
     * @return a <code>ParameterizedType</code> if the type is an
     *         invocation of a generic type, or null if it is not.
     * @since 1.5
     */
    public ParameterizedType asParameterizedType()
	{
		return classDoc.asParameterizedType();
	}

    /**
     * Get the annotations of this program element.
     * Return an empty array if there are none.
     *
     * @return the annotations of this program element.
     * @since 1.5
     */
    public AnnotationDesc[] annotations()
	{
		return classDoc.annotations();
	}

    /**
     * Return this type as a <code>TypeVariable</code> if it represents
     * a type variable.  Array dimensions are ignored.
     *
     * @return a <code>TypeVariable</code> if the type is a type variable,
     *         or null if it is not.
     * @since 1.5
     */
    public TypeVariable asTypeVariable()
	{
		return classDoc.asTypeVariable();
	}

    /**
     * Is this Doc item an enum type?
     *
     * @return true if it represents an enum type
     * @since 1.5
     */
    public boolean isEnum()
	{
		return classDoc.isEnum();
	}

	/**
     * Return the type parameter tags of this class or interface.
     * Return an empty array if there are none.
     *
     * @return the type parameter tags of this class or interface.
     * @since 1.5
     */
    public ParamTag[] typeParamTags()
	{
		return classDoc.typeParamTags();
	}


	/**
     * Return true if this type represents a primitive type.
     *
     * @return true if this type represents a primitive type.
     * @since 1.5
     */
    public boolean isPrimitive()
	{
		return classDoc.isPrimitive();
	}
}
