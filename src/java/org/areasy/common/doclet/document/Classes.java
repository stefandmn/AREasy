package org.areasy.common.doclet.document;

/*
 * Copyright (c) 2007-2015 AREasy Runtime
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
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPTable;
import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;
import org.areasy.common.doclet.AbstractConfiguration;
import org.areasy.common.doclet.DefaultConfiguration;
import org.areasy.common.doclet.document.elements.LinkPhrase;
import org.areasy.common.doclet.document.tags.HtmlParserWrapper;
import org.areasy.common.doclet.utilities.DocletUtility;
import org.areasy.common.doclet.utilities.PDFUtility;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.Tag;

import java.util.Hashtable;
import java.util.Vector;


/**
 * Prints class information.
 * 
 * @version $Id: Classes.java,v 1.5 2008/05/14 09:36:47 swd\stefan.damian Exp $
 */
public class Classes implements AbstractConfiguration
{

	/**
	 * Logger reference
	 */
	private static Logger log = LoggerFactory.getLog(Classes.class);

	/**
	 * Prints javadoc of one given class.
	 *
	 * @param classDoc   The javadoc information about the class.
	 * @param packageDoc The package which the class is part of.
	 * @throws Exception
	 */
	public static void printClass(ClassDoc classDoc, PackageDoc packageDoc) throws Exception
	{
		Document.newPage();
		State.increasePackageSection();

		State.setCurrentClass(classDoc.qualifiedName());
		State.setCurrentDoc(classDoc);
		log.info("..> " + State.getCurrentClass());

		// Simulate javadoc HTML layout package (small) and class (large) name header
		Paragraph namePara = new Paragraph(packageDoc.name(),
				Fonts.getFont(TEXT_FONT, BOLD, 16));
		Document.add(namePara);

		Phrase linkPhrase = null;
		if (!classDoc.isInterface())
		{
			linkPhrase = Destinations.createDestination("Class "
					+ classDoc.name(), classDoc,
					Fonts.getFont(TEXT_FONT, BOLD, 16));
		}
		else
		{
			linkPhrase = Destinations.createDestination("Interface "
					+ classDoc.name(), classDoc,
					Fonts.getFont(TEXT_FONT, BOLD, 16));
		}

		Paragraph titlePara = new Paragraph((float) 16.0, "");
		String classFileAnchor = Destinations.createAnchorDestination(State.getCurrentFile(), null);
		titlePara.add(PDFUtility.createAnchor(classFileAnchor, titlePara.font()));
		titlePara.add(linkPhrase);

		Document.instance().add(titlePara);

		// class derivation tree - build list first
		Hashtable list = new Hashtable();
		ClassDoc currentTreeClass = classDoc;
		ClassDoc superClass = null;
		ClassDoc subClass = null;

		Vector interfacesList = new Vector();

		while ((superClass = currentTreeClass.superclass()) != null)
		{
			if (!classDoc.isInterface())
			{
				// Store interfaces implemented by superclasses
				// because the current class also implements all
				// interfaces of its superclass (by inheritance)
				ClassDoc[] interfaces = superClass.interfaces();

				for (int u = 0; u < interfaces.length; u++)
				{
					interfacesList.addElement(interfaces[u]);
				}
			}

			list.put(superClass, currentTreeClass);
			currentTreeClass = superClass;
		}


		// First line of derivation tree must NOT be printed, if it's
		// the only line, and it's an interface (not a class). This is
		// because a class ALWAYS has a superclass (if only java.lang.Object),
		// but an interface does not necessarily have a super instance.
		boolean firstLine = true;

		if (classDoc.isInterface() && (list.get(currentTreeClass) == null))
		{
			firstLine = false;
		}

		// top-level-class
		String blanks = "";

		if (firstLine)
		{
			Document.add(new Paragraph((float) 24.0,
					currentTreeClass.qualifiedTypeName(), Fonts.getFont(CODE_FONT, 10)));
		}

		while ((subClass = (ClassDoc) list.get(currentTreeClass)) != null)
		{
			blanks = blanks + "   ";
			Document.add(new Paragraph((float) 10.0, blanks + "|",
					Fonts.getFont(CODE_FONT, 10)));

			if (list.get(subClass) == null)
			{
				// it's last in list, so use bold font
				Document.add(new Paragraph((float) 8.0,
						blanks + "+-" + subClass.qualifiedTypeName(),
						Fonts.getFont(CODE_FONT, BOLD, 10)));
			}
			else
			{
				// If it's not last, it's a superclass. Create link to
				// it, if it's in same API.
				Paragraph newLine = new Paragraph((float) 8.0);
				newLine.add(new Chunk(blanks + "+-", Fonts.getFont(CODE_FONT, 10)));
				newLine.add(new LinkPhrase(subClass.qualifiedTypeName(), null, 10, false));
				Document.add(newLine);
			}

			currentTreeClass = subClass;
		}

		ClassDoc[] interfaces = classDoc.interfaces();

		// Now, for classes only, print implemented interfaces
		// and known subclasses
		if (!classDoc.isInterface())
		{
			// List All Implemented Interfaces
			if ((interfaces != null) && (interfaces.length > 0))
			{
				for (int i = 0; i < interfaces.length; i++)
				{
					interfacesList.addElement(interfaces[i]);
				}
			}

			String[] interfacesNames = new String[interfacesList.size()];
			for (int i = 0; i < interfacesNames.length; i++)
			{
				interfacesNames[i] = ((ClassDoc) interfacesList.elementAt(i)).qualifiedTypeName();
			}
			if (interfacesNames.length > 0)
			{
				Implementors.print("All Implemented Interfaces:", interfacesNames);
			}

			// Known subclasses
			String[] knownSubclasses = ImplementorsInformation.getDirectSubclasses(State.getCurrentClass());

			if ((knownSubclasses != null) && (knownSubclasses.length > 0))
			{
				Implementors.print("Direct Known Subclasses:", knownSubclasses);
			}
		}
		else
		{
			// For interfaces, print superinterfaces and all subinterfaces
			// Known super-interfaces
			String[] knownSuperInterfaces = ImplementorsInformation.getKnownSuperclasses(State.getCurrentClass());

			if ((knownSuperInterfaces != null) &&
					(knownSuperInterfaces.length > 0))
			{
				Implementors.print("All Superinterfaces:", knownSuperInterfaces);
			}

			// Known sub-interfaces
			String[] knownSubInterfaces = ImplementorsInformation.getKnownSubclasses(State.getCurrentClass());

			if ((knownSubInterfaces != null) &&
					(knownSubInterfaces.length > 0))
			{
				Implementors.print("All Subinterfaces:", knownSubInterfaces);
			}

			// Known Implementing Classes
			String[] knownImplementingClasses = ImplementorsInformation.getImplementingClasses(State.getCurrentClass());

			if ((knownImplementingClasses != null) && (knownImplementingClasses.length > 0))
			{
				Implementors.print("All Known Implementing Classes:", knownImplementingClasses);
			}
		}

		// Horizontal line
		PDFUtility.printLine();

		// Class type / declaration
		String info = "";

		Tag[] deprecatedTags = classDoc.tags(DOC_TAGS_DEPRECATED);

		if (deprecatedTags.length > 0)
		{
			Paragraph classDeprecatedParagraph = new Paragraph((float) 20);

			Chunk deprecatedClassText = new Chunk(LB_DEPRECATED_TAG,
					Fonts.getFont(TEXT_FONT, BOLD, 12));
			classDeprecatedParagraph.add(deprecatedClassText);

			String depText = DocletUtility.getComment(deprecatedTags[0].inlineTags());
			Element[] deprecatedInfoText = HtmlParserWrapper.createPdfObjects("<i>" + depText + "</i>");
			for (int n = 0; n < deprecatedInfoText.length; n++)
			{
				// Only phrases can be supported here (but no tables)
				if (deprecatedInfoText[n] instanceof Phrase)
				{
					classDeprecatedParagraph.add(deprecatedInfoText[n]);
				}
			}

			Document.add(classDeprecatedParagraph);
		}

		info = DocletUtility.getClassModifiers(classDoc);

		Paragraph infoParagraph = new Paragraph((float) 20, info,
				Fonts.getFont(TEXT_FONT, 12));
		infoParagraph.add(new Chunk(classDoc.name(),
				Fonts.getFont(TEXT_FONT, BOLD, 12)));
		Document.add(infoParagraph);

		// extends ...
		ClassDoc superClassOrInterface = null;

		if (classDoc.isInterface())
		{
			ClassDoc[] superInterfaces = classDoc.interfaces();

			if (superInterfaces.length > 0)
			{
				superClassOrInterface = superInterfaces[0];
			}
		}
		else
		{
			superClassOrInterface = classDoc.superclass();
		}

		if (superClassOrInterface != null)
		{
			Paragraph extendsPara = new Paragraph((float) 14.0);
			extendsPara.add(new Chunk("extends ", Fonts.getFont(TEXT_FONT, 12)));

			String superClassName = DocletUtility.getQualifiedNameIfNecessary(superClassOrInterface);
			extendsPara.add(new LinkPhrase(superClassOrInterface.qualifiedName(), superClassName, 12,
					true));

			Document.add(extendsPara);
		}

		if (!classDoc.isInterface())
		{
			//implements
			if ((interfaces != null) && (interfaces.length > 0))
			{
				String[] interfacesNames = new String[interfacesList.size()];

				for (int i = 0; i < interfacesNames.length; i++)
				{
					interfacesNames[i] = ((ClassDoc) interfacesList.elementAt(i)).qualifiedTypeName();
				}

				Paragraph extendsPara = new Paragraph((float) 14.0);
				extendsPara.add(new Chunk("implements ", Fonts.getFont(TEXT_FONT, 12)));

				Paragraph descPg = new Paragraph((float) 24.0);

				for (int i = 0; i < interfacesNames.length; i++)
				{
					String subclassName = DocletUtility.getQualifiedNameIfNecessary(interfacesNames[i]);
					Phrase subclassPhrase = new LinkPhrase(interfacesNames[i],
							subclassName, 12, true);
					descPg.add(subclassPhrase);

					if (i < (interfacesNames.length - 1))
					{
						descPg.add(new Chunk(", ", Fonts.getFont(CODE_FONT, 10)));
					}
				}

				extendsPara.add(descPg);

				Document.add(extendsPara);
			}
		}

		Document.add(new Paragraph((float) 20.0, " "));

		// Description
		String classText = DocletUtility.getComment(classDoc);
		Element[] objs = HtmlParserWrapper.createPdfObjects(classText);

		if (objs.length == 0)
		{
			String desc = DocletUtility.stripLineFeeds(classText);
			Document.add(new Paragraph((float) 14.0, desc,
					Fonts.getFont(TEXT_FONT, 12)));
		}
		else
		{
			PDFUtility.printPdfElements(objs);
		}

		TagLists.printClassTags(classDoc);
		// Horizontal line
		PDFUtility.printLine();

		if (DefaultConfiguration.isShowSummaryActive())
		{
			Summary.printAll(classDoc);
			PDFUtility.printLine();
		}

		float[] widths = {(float) 1.0, (float) 94.0};
		PdfPTable table = new PdfPTable(widths);
		table.setWidthPercentage((float) 100);

		// Some empty space...
		Document.add(new Paragraph((float) 6.0, " "));
		Members.printMembers(classDoc);
	}

}
