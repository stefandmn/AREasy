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

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.List;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.sun.javadoc.*;
import org.areasy.common.doclet.AbstractConfiguration;
import org.areasy.common.doclet.DefaultConfiguration;
import org.areasy.common.doclet.document.elements.*;
import org.areasy.common.doclet.document.tags.HtmlParserWrapper;
import org.areasy.common.doclet.utilities.DocletUtility;
import org.areasy.common.doclet.utilities.PDFUtility;
import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;

import java.awt.*;
import java.util.Arrays;

/**
 * Prints the summary tables.
 *
 * @version $Id: Summary.java,v 1.3 2008/05/14 09:36:47 swd\stefan.damian Exp $
 */
public class Summary implements AbstractConfiguration
{

	/**
	 * Logger reference
	 */
	private static Logger log = LoggerFactory.getLog(Summary.class);

	/**
	 * Prints all field, constructor and methods summary
	 *
	 * @param classDoc The class or interface whose methods should be printed.
	 * @throws Exception
	 */
	public static void printAll(ClassDoc classDoc) throws Exception
	{
		Phrase deprecatedPhrase = null;

		// test if class is deprecated
		boolean allDeprecated = false;

		if (classDoc.tags("@deprecated").length > 0) allDeprecated = true;

		// Print inner classes summary table
		ClassDoc[] innerClasses = classDoc.innerClasses();
		Arrays.sort(innerClasses);

		if ((innerClasses != null) && (innerClasses.length > 0))
		{
			PdfPTable mainTable = createTable("Nested Class");

			for (int i = 0; i < innerClasses.length; i++)
			{

				// test if field is deprecated
				boolean isDeprecated = false;

				if (allDeprecated || (innerClasses[i].tags("@deprecated").length > 0))
				{
					isDeprecated = true;
					deprecatedPhrase = new CustomDeprecatedPhrase(innerClasses[i]);
				}

				printInnerClass(innerClasses[i].name(),
						innerClasses[i].qualifiedName(),
						isDeprecated, deprecatedPhrase, mainTable);
			}

			Document.instance().add(mainTable);
		}

		// Print fields summary table
		FieldDoc[] fields = classDoc.fields();
		Arrays.sort(fields);

		if ((fields != null) && (fields.length > 0))
		{
			PdfPTable mainTable = createTable("Field");

			for (int i = 0; i < fields.length; i++)
			{

				// test if field is deprecated
				boolean isDeprecated = false;

				if (allDeprecated || (fields[i].tags("@deprecated").length > 0))
				{
					isDeprecated = true;
					deprecatedPhrase = new CustomDeprecatedPhrase(fields[i]);
				}

				printField(fields[i], fields[i].constantValue(),
						isDeprecated, deprecatedPhrase, mainTable);
			}

			Document.instance().add(mainTable);
		}

		// Now show inherited fields
		if (DefaultConfiguration.isShowInheritedSummaryActive())
		{
			if (!classDoc.isInterface())
			{
				// Print fields inherited from the superclass
				if (classDoc.superclass() != null)
				{
					Inherited.print(classDoc.superclass(), SHOW_FIELDS);
				}
				// Print fields inherited from any interfaces
				if (classDoc.interfaces() != null)
				{
					ClassDoc[] interfaces = classDoc.interfaces();
					for (int no = 0; no < interfaces.length; no++)
					{
						String interfaceName = interfaces[no].qualifiedName();
						if (Inherited.ifClassMustBePrinted(interfaceName))
						{
							Inherited.print(interfaces[no], SHOW_FIELDS);
						}
					}
				}
			}
			else
			{
				ClassDoc[] interf = classDoc.interfaces();
				if (interf.length > 0)
				{
					// Array must always be of length 1 because interfaces can
					// extend only one other interface (and not implement any
					// other interfaces by themselves)
					String interfaceName = interf[0].qualifiedName();
					if (Inherited.ifClassMustBePrinted(interfaceName))
					{
						Inherited.print(interf[0], SHOW_FIELDS);
					}
				}
			}
		}

		// Print constructor summary table
		ConstructorDoc[] constructors = classDoc.constructors();

		if ((constructors != null) && (constructors.length > 0))
		{
			PdfPTable mainTable = createTable("Constructor");

			for (int i = 0; i < constructors.length; i++)
			{

				// test if constructor is deprecated
				boolean isDeprecated = false;

				if (allDeprecated || (constructors[i].tags("@deprecated").length > 0))
				{
					isDeprecated = true;
					deprecatedPhrase = new CustomDeprecatedPhrase(constructors[i]);
				}

				printConstructor(constructors[i], isDeprecated, deprecatedPhrase, mainTable);
			}

			Document.instance().add(mainTable);
		}

		// Print method summary table
		MethodDoc[] methods = classDoc.methods();
		Arrays.sort(methods);

		if ((methods != null) && (methods.length > 0))
		{
			PdfPTable mainTable = createTable("Method");

			for (int i = 0; i < methods.length; i++)
			{

				// test if method is deprecated
				boolean isDeprecated = false;

				if (allDeprecated || (methods[i].tags("@deprecated").length > 0))
				{
					isDeprecated = true;
					deprecatedPhrase = new CustomDeprecatedPhrase(methods[i]);
				}

				String declaration = DocletUtility
						.getMethodSummaryModifiers(methods[i]);

				Phrase returnType = PDFUtility.getReturnType(methods[i], 9);
				printMethod(methods[i], declaration, returnType, isDeprecated, deprecatedPhrase, mainTable);
			}

			Document.instance().add(mainTable);
		}

		// Now show inherited methods
		if (DefaultConfiguration.isShowInheritedSummaryActive())
		{
			if (!classDoc.isInterface())
			{
				if (classDoc.superclass() != null)
				{
					String className = classDoc.superclass().qualifiedName();
					if (Inherited.ifClassMustBePrinted(className))
					{
						Inherited.print(classDoc.superclass(), SHOW_METHODS);
					}
				}
				// Print methods inherited from any interfaces
				if (classDoc.interfaces() != null)
				{
					ClassDoc[] interfaces = classDoc.interfaces();
					for (int no = 0; no < interfaces.length; no++)
					{
						String className = interfaces[no].qualifiedName();
						if (Inherited.ifClassMustBePrinted(className)) Inherited.print(interfaces[no], SHOW_METHODS);
					}
				}
			}
			else
			{
				ClassDoc[] interf = classDoc.interfaces();
				if (interf.length > 0)
				{
					// Array must always be of length 1 because interfaces can
					// extend only one other interface (and not implement any other interfaces by themselves)
					String interfaceName = interf[0].qualifiedName();
					if (Inherited.ifClassMustBePrinted(interfaceName)) Inherited.print(interf[0], SHOW_METHODS);
				}
			}
		}

	}

	/**
	 * Prints inner classes summaries.
	 *
	 * @param name
	 * @param destination
	 * @param isDeprecated
	 * @param deprecatedPhrase
	 * @param mainTable
	 * @throws Exception
	 */
	private static void printInnerClass(String name, String destination,
										boolean isDeprecated, Phrase deprecatedPhrase,
										PdfPTable mainTable) throws Exception
	{

		Element[] objs = HtmlParserWrapper.createPdfObjects(name);

		PdfPTable commentsTable = createColumnsAndDeprecated(objs,
				isDeprecated, deprecatedPhrase);

		PdfPTable anotherinnertable = new PdfPTable(1);
		anotherinnertable.setWidthPercentage(100f);
		anotherinnertable.getDefaultCell().setBorder(Rectangle.NO_BORDER);

		PdfPTable innerTable = addDeclaration("class", null);

		// right part of the table
		PdfPCell cell = PDFUtility.createElementCell(2,
				new LinkPhrase(destination, name, Fonts.getFont(CODE_FONT, 9)));
		cell.setPaddingTop((float) 2.0);
		cell.setPaddingLeft((float) 7.0);
		anotherinnertable.addCell(cell);
		anotherinnertable.addCell(commentsTable);

		innerTable.addCell(anotherinnertable);
		mainTable.addCell(innerTable);
	}

	/**
	 * Prints field summaries.
	 * @param constantValue
	 * @param isDeprecated
	 * @param deprecatedPhrase
	 * @param mainTable
	 * @throws Exception
	 */
	private static void printField(FieldDoc fieldDoc,
								   Object constantValue, boolean isDeprecated,
								   Phrase deprecatedPhrase, PdfPTable mainTable) throws Exception
	{

		String name = fieldDoc.name();
		String modifier = fieldDoc.modifiers();
		String commentText = DocletUtility.getFirstSentence(fieldDoc);
		String destination = fieldDoc.qualifiedName();

		Element[] objs = HtmlParserWrapper.createPdfObjects(commentText);

		PdfPTable commentsTable = createColumnsAndDeprecated(objs,
				isDeprecated, deprecatedPhrase);

		if (constantValue != null)
		{
			// Add 2nd comment line (left cell empty, right cell text)
			commentsTable.addCell(new Phrase(""));
			Chunk valueTextChunk = new Chunk("Value: ", Fonts.getFont(TEXT_FONT, PLAIN, 10));
			Chunk valueContentChunk = new Chunk(constantValue.toString(), Fonts
					.getFont(CODE_FONT, BOLD, 10));
			Phrase constantValuePhrase = new Phrase("");
			constantValuePhrase.add(valueTextChunk);
			constantValuePhrase.add(valueContentChunk);
			commentsTable.addCell(constantValuePhrase);
		}

		PdfPTable anotherinnertable = new PdfPTable(1);
		anotherinnertable.setWidthPercentage(100f);
		anotherinnertable.getDefaultCell().setBorder(Rectangle.NO_BORDER);

		PdfPTable innerTable = addDeclaration(modifier, null);

		// Link to field
		LinkPhrase linkPhrase = new LinkPhrase(destination,
				name, Fonts.getFont(CODE_FONT, 9));

		// right part of the table
		PdfPCell cell = PDFUtility.createElementCell(2, linkPhrase);
		cell.setPaddingTop((float) 2.0);
		cell.setPaddingLeft((float) 7.0);
		anotherinnertable.addCell(cell);
		anotherinnertable.addCell(commentsTable);

		innerTable.addCell(anotherinnertable);
		mainTable.addCell(innerTable);
	}

	/**
	 * Prints constructor summaries.
	 * @param isDeprecated
	 * @param deprecatedPhrase
	 * @param mainTable
	 * @throws Exception
	 */
	private static void printConstructor(ConstructorDoc constructorDoc,
										 boolean isDeprecated, Phrase deprecatedPhrase, PdfPTable mainTable)
			throws Exception
	{

		String name = constructorDoc.name();
		String modifier = constructorDoc.modifiers();
		String commentText = DocletUtility.getFirstSentence(constructorDoc);
		String destination = constructorDoc.qualifiedName() +
				constructorDoc.signature();
		Parameter[] parms = constructorDoc.parameters();

		Element[] objs = HtmlParserWrapper.createPdfObjects(commentText);

		PdfPTable commentsTable = createColumnsAndDeprecated(objs,
				isDeprecated, deprecatedPhrase);

		PdfPTable anotherinnertable = new PdfPTable(1);
		anotherinnertable.setWidthPercentage(100f);
		anotherinnertable.getDefaultCell().setBorder(Rectangle.NO_BORDER);

		// Link to constructor
		Font constructorFont = Fonts.getFont(CODE_FONT, 9);
		Phrase phrase = new Phrase("", constructorFont);
		phrase.add(new LinkPhrase(destination, name, constructorFont));

		phrase.add("(");
		if ((parms != null) && (parms.length > 0))
		{
			for (int i = 0; i < parms.length; i++)
			{
				phrase.add(PDFUtility.getParameterTypePhrase(parms[i], 9));
				phrase.add(" ");
				phrase.add(parms[i].name());
				if (i != (parms.length - 1))
				{
					phrase.add(", ");
				}
			}
		}
		phrase.add(")");


		PdfPCell cell = PDFUtility.createElementCell(2, phrase);
		cell.setPaddingLeft((float) 7.0);
		anotherinnertable.addCell(cell);
		anotherinnertable.addCell(commentsTable);

		PdfPTable innerTable = addDeclaration(modifier, null);
		innerTable.addCell(anotherinnertable);

		mainTable.addCell(innerTable);
	}

	/**
	 * Prints the summary tables for a method.
	 * @param modifier
	 * @param returnType
	 * @param isDeprecated
	 * @param deprecatedPhrase
	 * @param mainTable
	 * @throws Exception
	 */
	private static void printMethod(MethodDoc methodDoc, String modifier,
									Phrase returnType,
									boolean isDeprecated, Phrase deprecatedPhrase,
									PdfPTable mainTable) throws Exception
	{

		String name = methodDoc.name();
		String destination = methodDoc.qualifiedName()
				+ methodDoc.signature();
		String commentText = DocletUtility.getFirstSentence(methodDoc);
		Parameter[] parms = methodDoc.parameters();

		// Create inner table for both columns (left column already filled in)
		PdfPTable rowTable = addDeclaration(modifier, returnType);

		// Inner table with 1st sentence of javadoc of this method.
		// We use a table in order to be able to create two cells
		// in it (1st an empty one for intendation)

		Element[] objs = HtmlParserWrapper.createPdfObjects(commentText);
		// Phrase descPhr = new Phrase();

		PdfPTable commentsTable = createColumnsAndDeprecated(objs,
				isDeprecated, deprecatedPhrase);

		// Table with 1 column and 2 rows (row 1 is parameters etc.,
		// row 2 is the description
		PdfPTable rightColumnInnerTable = new PdfPTable(1);

		rightColumnInnerTable.setWidthPercentage(100f);
		rightColumnInnerTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);

		// Link to method
		Font methodFont = Fonts.getFont(CODE_FONT, 9);
		Phrase phrase = new Phrase("", methodFont);
		phrase.add(new LinkPhrase(destination, name, methodFont));
		phrase.add("(");
		if ((parms != null) && (parms.length > 0))
		{
			for (int i = 0; i < parms.length; i++)
			{
				phrase.add(PDFUtility.getParameterTypePhrase(parms[i], 9));
				phrase.add(" ");
				phrase.add(parms[i].name());
				if (i != (parms.length - 1))
				{
					phrase.add(", ");
				}
			}
		}
		phrase.add(")");

		PdfPCell cell = PDFUtility.createElementCell(2, phrase);
		cell.setPaddingLeft((float) 7.0);
		rightColumnInnerTable.addCell(cell);
		rightColumnInnerTable.addCell(commentsTable);

		// Now fill in right column as well
		rowTable.addCell(rightColumnInnerTable);

		// And add inner table to main summary table as a new row
		mainTable.addCell(rowTable);
	}

	/**
	 * Creates the inner table for both columns. The left column
	 * already contains the declaration text part.
	 *
	 * @param text       The text (like "static final"..)
	 */
	private static PdfPTable addDeclaration(String text, Phrase returnType)
			throws DocumentException
	{
		PdfPTable innerTable = new PdfPTable(2);
		innerTable.setWidthPercentage(100f);
		innerTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);
		innerTable.setWidths(new int[]{24, 76});
		Paragraph declarationParagraph = new Paragraph((float) 9.0);
		Chunk leftPart = new Chunk(text, Fonts.getFont(CODE_FONT, 9));
		declarationParagraph.add(leftPart);
		if (returnType != null)
		{
			declarationParagraph.add(returnType);
			declarationParagraph.add(new Chunk(" ", Fonts.getFont(CODE_FONT, 9)));
		}
		PdfPCell cell = new CustomPdfPCell(Rectangle.RIGHT,
				declarationParagraph, 1, Color.gray);
		cell.setPaddingTop((float) 4.0);
		cell.setVerticalAlignment(PdfPCell.ALIGN_TOP);

		innerTable.addCell(cell);
		return innerTable;
	}

	/**
	 * Creates the two columns for the summary table and, if necessary,
	 * fills in the "Deprecated" text. Otherwise, the given elements
	 * are filled in.
	 *
	 * @param objs             The description elements.
	 * @param isDeprecated     If true, the whole class/method is deprecated.
	 * @param deprecatedPhrase The phrase for the deprecated text.
	 * @return The summary table columns.
	 * @throws DocumentException If something failed.
	 */
	private static PdfPTable createColumnsAndDeprecated(Element[] objs,
														boolean isDeprecated, Phrase deprecatedPhrase)
			throws DocumentException
	{

		PdfPTable commentsTable = null;
		commentsTable = new PdfPTable(2);
		commentsTable.setWidths(new int[]{5, 95});
		commentsTable.setWidthPercentage(100f);
		commentsTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);
		commentsTable.addCell(new Phrase(""));

		Phrase descPhr = new Phrase();

		CellNoBorderNoPadding cell = new CellNoBorderNoPadding(descPhr);

		commentsTable.addCell(cell);

		if (isDeprecated)
		{
			// if the method is deprecated...
			// do not print the comment text...
			// just print the deprecated text
			descPhr.add(new Phrase(AbstractConfiguration.LB_DEPRECATED_TAG, Fonts.getFont(TEXT_FONT, BOLD, 10)));
			descPhr.add(deprecatedPhrase);
		}
		else if (objs.length != 0)
		{
			for (int i = 0; i < objs.length; i++)
			{
				if (objs[i] instanceof List)
				{
					cell.addElement(objs[i]);
					descPhr = new Phrase("");
					cell.addElement(descPhr);
				}
				else
				{
					descPhr.add(objs[i]);
				}
			}
		}

		return commentsTable;
	}

	/**
	 * Creates a summary table with a coloured title bar.
	 *
	 * @param title The title for the summary table.
	 * @return The newly created summary table.
	 * @throws DocumentException If something fails.
	 */
	private static PdfPTable createTable(String title)
			throws DocumentException
	{

		PdfPTable mainTable = new CustomPdfPTable();
		PdfPCell colorTitleCell = new CustomPdfPCell(title + " Summary");
		// Some empty space...
		Document.instance().add(new Paragraph((float) 8.0, " "));
		mainTable.addCell(colorTitleCell);

		return mainTable;
	}
}