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

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;
import org.areasy.common.doclet.AbstractConfiguration;
import org.areasy.common.doclet.Doclet;
import org.areasy.common.doclet.document.elements.CellNoBorderNoPadding;
import org.areasy.common.doclet.document.elements.CustomDeprecatedPhrase;
import org.areasy.common.doclet.document.elements.CustomPdfPCell;
import org.areasy.common.doclet.document.tags.HtmlParserWrapper;
import org.areasy.common.doclet.utilities.DocletUtility;
import org.areasy.common.doclet.utilities.PDFUtility;
import com.sun.javadoc.*;


/**
 * Prints member (method, variable) information.
 *
 * @version $Id: Members.java,v 1.2 2008/05/14 09:36:47 swd\stefan.damian Exp $
 */
public class Members implements AbstractConfiguration
{

	/**
	 * Logger reference
	 */
	private static Logger log = LoggerFactory.getLog(Members.class);


	/**
	 * Prints all methods of a class or interface.
	 *
	 * @param classDoc The class or interface whose methods should be printed.
	 * @throws Exception
	 */
	public static void printMembers(ClassDoc classDoc) throws Exception
	{
		Phrase deprecatedPhrase = null;

		// test if class is deprecated
		boolean allDeprecated = false;

		if (classDoc.tags(DOC_TAGS_DEPRECATED).length > 0) allDeprecated = true;

		State.setTypeOfCurrentMember(State.TYPE_FIELD);
		FieldDoc[] fields = classDoc.fields();

		if ((fields != null) && (fields.length > 0))
		{
			for (int i = 0; i < fields.length; i++)
			{
				boolean isFirst = false;

				if (i == 0) isFirst = true;

				// test if field is deprecated
				boolean isDeprecated = false;

				if (allDeprecated || (fields[i].tags(DOC_TAGS_DEPRECATED).length > 0))
				{
					isDeprecated = true;
					deprecatedPhrase = new CustomDeprecatedPhrase(fields[i]);
				}

				String declaration = DocletUtility.getFieldModifiers(fields[i]) + fields[i].type().qualifiedTypeName() + " ";

				Members.printMember(declaration, null, fields[i], null, null, isFirst, true, false, isDeprecated, deprecatedPhrase, fields[i].constantValue());

				TagLists.printMemberTags(fields[i]);
				State.setContinued(false);

				if (i < (fields.length - 1)) PDFUtility.printLine();
			}
		}

		State.setTypeOfCurrentMember(State.TYPE_CONSTRUCTOR);
		ConstructorDoc[] constructors = classDoc.constructors();

		if ((constructors != null) && (constructors.length > 0))
		{
			for (int i = 0; i < constructors.length; i++)
			{
				boolean isFirst = false;

				if (i == 0) isFirst = true;

				// test if constructor is deprecated
				boolean isDeprecated = false;

				if (allDeprecated || (constructors[i].tags(DOC_TAGS_DEPRECATED).length > 0))
				{
					isDeprecated = true;
					deprecatedPhrase = new CustomDeprecatedPhrase(constructors[i]);
				}

				String declaration = DocletUtility.getConstructorModifiers(constructors[i]);
				Members.printMember(declaration, null,
						constructors[i],
						constructors[i].parameters(), null,
						isFirst, false, true, isDeprecated, deprecatedPhrase, null);

				TagLists.printMemberTags(constructors[i]);

				State.setContinued(false);

				if (i < (constructors.length - 1)) PDFUtility.printLine();
			}
		}

		log.trace("Print methods...");
		State.setTypeOfCurrentMember(State.TYPE_METHOD);
		MethodDoc[] methods = classDoc.methods();

		if ((methods != null) && (methods.length > 0))
		{
			for (int i = 0; i < methods.length; i++)
			{
				boolean isFirst = false;

				if (i == 0) isFirst = true;

				// test if method is deprecated
				boolean isDeprecated = false;

				if (allDeprecated || (methods[i].tags(DOC_TAGS_DEPRECATED).length > 0))
				{
					isDeprecated = true;
					deprecatedPhrase = new CustomDeprecatedPhrase(methods[i]);
				}

				String declaration = DocletUtility.getMethodModifiers(methods[i]);

				if (i == (methods.length - 1)) State.setLastMethod(true);

				State.increasePackageMethod();
				State.setCurrentMethod(methods[i].name());

				Phrase returnType = PDFUtility.getReturnType(methods[i], 10);
				Members.printMember(declaration, returnType, methods[i],
						methods[i].parameters(), methods[i].thrownExceptions(),
						isFirst, false, false, isDeprecated,
						deprecatedPhrase, null);

				TagLists.printMemberTags(methods[i]);

				State.setContinued(false);

				if (i < (methods.length - 1)) PDFUtility.printLine();
			}
		}

		State.setTypeOfCurrentMember(State.TYPE_NONE);
	}


	/**
	 * Prints member information.
	 *
	 * @param declaration      The modifiers ("public static final..").
	 * @param returnType       Phrase with the return type text (might be
	 *                         a hyperlink)
	 * @param parms            Parameters of a method or constructor, null for a field.
	 * @param thrownExceptions Exceptions of a method, null for a field or constructor.
	 * @param isFirst          True if it is the first field/method/constructor in the list.
	 * @param isField          True if it is a field.
	 * @param isConstructor    True if it is a constructor.
	 * @throws Exception
	 */
	public static void printMember(String declaration, Phrase returnType, ProgramElementDoc commentDoc, Parameter[] parms, ClassDoc[] thrownExceptions, boolean isFirst, boolean isField, boolean isConstructor, boolean isDeprecated, Phrase deprecatedPhrase, Object constantValue) throws Exception
	{
		String name = commentDoc.name();

		State.setCurrentMember(State.getCurrentClass() + "." + name);
		State.setCurrentDoc(commentDoc);

		// Returns the text, resolving any "inheritDoc" inline tags
		String commentText = DocletUtility.getComment(commentDoc);

		// TODO: The following line may set the wrong page number
		//      in the index, when the member gets printed on a
		//      new page completely (because it is in one table).
		// Solution unknown yet. Probably split up table.
		Doclet.getIndex().addToMemberList(State.getCurrentMember());

		// Prepare list of exceptions (if it throws any)
		String throwsText = "throws";
		int parmsColumn = declaration.length() + (name.length() - throwsText.length());

		// First output text line (declaration of method and first parameter or "()" ).
		// This first line is a special case because the class name is bold,
		// while the rest is regular plain text, so it must be built using three Chunks.
		Paragraph declarationParagraph = new Paragraph((float) 10.0);

		// left part / declaration ("public static..")
		Chunk leftPart = new Chunk(declaration, Fonts.getFont(CODE_FONT, 10));

		declarationParagraph.add(leftPart);

		if (returnType != null)
		{
			// left middle part / declaration ("public static..")
			declarationParagraph.add(returnType);
			declarationParagraph.add(new Chunk(" ", Fonts.getFont(CODE_FONT, 10)));
			parmsColumn = 2;
		}

		// right middle part / bold class name
		declarationParagraph.add(new Chunk(name, Fonts.getFont(CODE_FONT, BOLD, 10)));

		if (!isField)
		{
			// 1st parameter or empty brackets

			if ((parms != null) && (parms.length > 0))
			{
				Phrase wholePhrase = new Phrase("(", Fonts.getFont(CODE_FONT, 10));
				// create link for parameter type
				wholePhrase.add(PDFUtility.getParameterTypePhrase(parms[0], 10));
				// then normal text for parameter name
				wholePhrase.add(" " + parms[0].name());
				if (parms.length > 1)
				{
					wholePhrase.add(",");
				}
				else
				{
					wholePhrase.add(")");
				}

				// In order to have the parameter types in the bookmark,
				// make the current state text more detailled
				String txt = State.getCurrentMethod() + "(";
				for (int i = 0; i < parms.length; i++)
				{
					if (i > 0) txt = txt + ",";
					txt = txt + DocletUtility.getParameterType(parms[i]);
				}

				txt = txt + ")";
				State.setCurrentMethod(txt);

				// right part / parameter and brackets
				declarationParagraph.add(wholePhrase);

			}
			else
			{
				String lastPart = "()";
				State.setCurrentMethod(State.getCurrentMethod() + lastPart);

				// right part / parameter and brackets
				declarationParagraph.add(new Chunk(lastPart, Fonts.getFont(CODE_FONT, 10)));
			}

		}

		float[] widths = {(float) 6.0, (float) 94.0};
		PdfPTable table = new PdfPTable(widths);
		table.setWidthPercentage((float) 100);

		// Before the first constructor or method, create a coloured title bar
		if (isFirst)
		{
			PdfPCell colorTitleCell = null;

			// Some empty space...
			Document.add(new Paragraph((float) 6.0, " "));

			if (isConstructor) colorTitleCell = new CustomPdfPCell("Constructors");
				else if (isField) colorTitleCell = new CustomPdfPCell("Fields");
					else colorTitleCell = new CustomPdfPCell("Methods");

			colorTitleCell.setColspan(2);
			table.addCell(colorTitleCell);
		}

		// Method name (large, first line of a method description block)
		Phrase linkPhrase = Destinations.createDestination(commentDoc.name(), commentDoc, Fonts.getFont(TEXT_FONT, BOLD, 14));
		Paragraph nameTitle = new Paragraph(linkPhrase);
		PdfPCell nameCell = new CellNoBorderNoPadding(nameTitle);

		if (isFirst) nameCell.setPaddingTop(10);
			else nameCell.setPaddingTop(0);

		nameCell.setPaddingBottom(8);
		nameCell.setColspan(1);

		// Create nested table in order to try to prevent the stuff inside
		// this table from being ripped appart over a page break. The method
		// name and the declaration/parm/exception line(s) should always be
		// together, because everything else just looks bad
		PdfPTable linesTable = new PdfPTable(1);
		linesTable.addCell(nameCell);
		linesTable.addCell(new CellNoBorderNoPadding(declarationParagraph));

		if (!isField)
		{
			// Set up following declaration lines
			Paragraph[] params = PDFUtility.createParameters(parmsColumn, parms);
			Paragraph[] exceps = PDFUtility.createExceptions(parmsColumn, thrownExceptions);

			for (int i = 0; i < params.length; i++)
			{
				linesTable.addCell(new CellNoBorderNoPadding(params[i]));
			}

			for (int i = 0; i < exceps.length; i++)
			{
				linesTable.addCell(new CellNoBorderNoPadding(exceps[i]));
			}
		}

		// Create cell for inserting the nested table into the outer table
		PdfPCell cell = new PdfPCell(linesTable);
		cell.setPadding(5);
		cell.setBorder(Rectangle.NO_BORDER);
		cell.setColspan(2);
		table.addCell(cell);

		// The empty, left cell (the invisible indentation column)
		State.setContinued(true);

		PdfPCell leftCell = PDFUtility.createElementCell(5, new Phrase("", Fonts.getFont(TEXT_FONT, BOLD, 6)));
		PdfPCell spacingCell = new PdfPCell();
		spacingCell.setFixedHeight((float) 8.0);
		spacingCell.setBorder(Rectangle.NO_BORDER);
		table.addCell(spacingCell);
		table.addCell(spacingCell);

		// The descriptive method explanation text

		if (isDeprecated)
		{
			Phrase commentPhrase = new Phrase();
			commentPhrase.add(new Phrase(AbstractConfiguration.LB_DEPRECATED_TAG,
					Fonts.getFont(TEXT_FONT, BOLD, 10)));
			commentPhrase.add(deprecatedPhrase);
			table.addCell(leftCell);
			table.addCell(PDFUtility.createElementCell(0, commentPhrase));

			commentPhrase = new Phrase();
			commentPhrase.add(Chunk.NEWLINE);
			table.addCell(leftCell);
			table.addCell(PDFUtility.createElementCell(0, commentPhrase));
		}

		Element[] objs = HtmlParserWrapper.createPdfObjects(commentText);

		if (objs.length == 1)
		{
			table.addCell(leftCell);
			table.addCell(PDFUtility.createElementCell(0, objs[0]));
		}
		else
		{
			table.addCell(leftCell);
			table.addCell(PDFUtility.createElementCell(0, Element.ALIGN_LEFT, objs));
		}

		// TODO: FORMAT THIS CONSTANT VALUE OUTPUT CORRECTLY

		if (isField)
		{
			if (constantValue != null)
			{
				// Add 2nd comment line (left cell empty, right cell text)
				Chunk valueTextChunk = new Chunk("Constant value: ", Fonts.getFont(TEXT_FONT, PLAIN, 10));
				Chunk valueContentChunk = new Chunk(constantValue.toString(), Fonts .getFont(CODE_FONT, BOLD, 10));
				Phrase constantValuePhrase = new Phrase("");
				constantValuePhrase.add(valueTextChunk);
				constantValuePhrase.add(valueContentChunk);
				table.addCell(leftCell);
				table.addCell(PDFUtility.createElementCell(0, constantValuePhrase));
			}
		}

		// Add whole method block to document
		Document.add(table);
	}
}
