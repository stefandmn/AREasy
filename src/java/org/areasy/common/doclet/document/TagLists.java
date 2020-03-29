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

import com.lowagie.text.Element;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.sun.javadoc.*;
import org.areasy.common.doclet.AbstractConfiguration;
import org.areasy.common.doclet.document.elements.CellNoBorderNoPadding;
import org.areasy.common.doclet.document.tags.HtmlParserWrapper;
import org.areasy.common.doclet.utilities.DocletUtility;
import org.areasy.common.doclet.utilities.PDFUtility;
import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;

import java.util.ArrayList;

/**
 * Prints class and member tag lists. These are javadoc tags like "author" etc.
 *
 * @version $Id: TagLists.java,v 1.2 2008/05/14 09:36:47 swd\stefan.damian Exp $
 */
public class TagLists implements AbstractConfiguration
{

	/**
	 * Logger reference
	 */
	private static Logger log = LoggerFactory.getLog(TagLists.class);

	/**
	 * @param classDoc
	 * @throws Exception
	 */
	public static void printClassTags(ClassDoc classDoc) throws Exception
	{
		// Get list of all tags
		TagList list = new TagList(classDoc);
		String[] names = list.getTagNames();

		// Are there any tags to print?
		for (int i = 0; i < names.length; i++)
		{
			String tagLabel = TagList.getTagLabel(names[i]);
			Tag[] tags = list.getTags(names[i]);
			if (tags == null) log.error("NO TAGS FOR: " + names[i]);

			printTags(tagLabel, tags, true, false);
		}
	}

	/**
	 * Prints one single tag type, for example "Throws" or "Parameters" for a
	 * method.
	 *
	 * @param list    The tag list object containing all tags of that type.
	 * @param tagName The name of the tag type ("param", "throws"..).
	 * @throws Exception If something failed.
	 */
	private static void printTag(TagList list, String tagName) throws Exception
	{

		Tag[] paramTags = list.getTags(tagName);
		if (paramTags != null)
		{
			String tagLabel = TagList.getTagLabel(tagName);
			printTags(tagLabel, paramTags, false, true);
		}
	}

	/**
	 * Takes a list of tag names and clears it of certain default method tags
	 * (like "Parameters", "Throws" etc.).
	 *
	 * @param names The list of all tags of a method.
	 * @return The list with only the additional tags.
	 */
	private static String[] clearTagsFromList(String[] names)
	{
		ArrayList newList = new ArrayList();
		int counter = 0;
		for (int i = 0; i < names.length; i++)
		{
			// XXX what about DOC_TAG_SEE?
			if (names[i].equals(DOC_TAG_EXCEPTION) == false
					&& names[i].equals(DOC_TAG_RETURN) == false
					&& names[i].equals(DOC_TAG_THROWS) == false
					&& names[i].equals(DOC_TAG_SINCE) == false
					&& names[i].equals(DOC_TAG_PARAM))
			{
				newList.add(names[i]);
				counter++;
			}
		}
		return (String[]) newList.toArray(new String[counter]);
	}

	/**
	 * Print all tags for a member (method).
	 *
	 * @param doc The doc for which to print tags.
	 * @throws Exception
	 */
	public static void printMemberTags(Doc doc) throws Exception
	{
		// Get list of all tags
		TagList list = new TagList(doc);

		String[] names = list.getTagNames();
		// Are there any tags to print?
		if (names.length > 0)
		{
			String[] customNames = clearTagsFromList(names);
			if (doc instanceof ExecutableMemberDoc)
			{
				ExecutableMemberDoc execDoc = (ExecutableMemberDoc) doc;
				// First, print "Parameter" tags
				printMemberTags(TagList.getTagLabel(DOC_TAG_PARAM), execDoc.paramTags());
				// Then print "Returns" tags
				printTag(list, DOC_TAG_RETURN);
				// Then print "Throws"/"Exception" tags
				printMemberTags(TagList.getTagLabel(DOC_TAG_THROWS), execDoc.throwsTags());
			}
			// Then print "See Also" tags
			printMemberTags(TagList.getTagLabel(DOC_TAG_SEE), doc.seeTags());
			// Then print "Since" tags
			printTag(list, DOC_TAG_SINCE);

			// Then print custom tags
			for (int i = 0; i < customNames.length; i++)
			{
				String tagLabel = TagList.getTagLabel(customNames[i]);
				Tag[] tags = list.getTags(customNames[i]);
				if (tags == null)
				{
					log.error("NO TAGS FOR: " + customNames[i]);
				}
				printTags(tagLabel, tags, false, true);
			}
		}
	}

	/**
	 * Prints tags of a class member (method, field).
	 *
	 * @param title      The bold face title text for the tag (like "Parameters:")
	 * @param tags       The list of tags to be printed.
	 */
	public static void printMemberTags(String title, Tag[] tags) throws Exception
	{
		printTags(title, tags, false, true);
	}

	/**
	 * Prints tags of a class member (method, field).
	 *
	 * @param title    The bold face title text for the tag (like "Parameters:")
	 * @param tags     The list of tags to be printed.
	 * @param compress If true, the text of all the given tags will be concatenated
	 *                 into one, comma separated. This is used for the author tag,
	 *                 for example, where several separate author tags should be
	 *                 printed as one only.
	 * @param isMember If true, the whole tag paragraph is printed with additional
	 *                 intendation (because it's a tag of a method, like the
	 *                 "Parameters:" tag).
	 * @throws Exception
	 */
	private static void printTags(String title, Tag[] tags, boolean compress, boolean isMember) throws Exception
	{
		if ((tags != null) && (tags.length > 0))
		{
			float[] widthsMember = {(float) 6.0, (float) 4.0, (float) 94.0};
			float[] widthsClass = {(float) 6.0, (float) 94.0};

			PdfPTable table = null;
			if (isMember)
			{
				table = new PdfPTable(widthsMember);
			}
			else
			{
				table = new PdfPTable(widthsClass);
			}
			table.setWidthPercentage((float) 100);

			Paragraph empty = new Paragraph(" ");

			// Add empty line after the title ("Parameters:" etc.)
			if (isMember)
			{
				table.addCell(new CellNoBorderNoPadding(empty));
				table.addCell(new CellNoBorderNoPadding(empty));
				table.addCell(new CellNoBorderNoPadding(empty));
			}

			PdfPCell titleCell = new CellNoBorderNoPadding(new Paragraph((float) 24.0, title, Fonts.getFont(TEXT_FONT, BOLD, 10)));
			titleCell.setColspan(2);
			if (isMember)
			{
				table.addCell(new CellNoBorderNoPadding(empty)); // indentation
				// column
			}
			table.addCell(titleCell);

			int number = tags.length;
			String tagText = "";
			if (compress)
			{
				number = 1;
				for (int i = 0; i < tags.length; i++)
				{
					tagText = tagText + getTagText(tags[i]);
					if (i < tags.length - 1)
					{
						tagText = tagText + ", ";
					}
				}
			}

			for (int i = 0; i < number; i++)
			{

				// indentation columns
				if (isMember)
				{
					table.addCell(new CellNoBorderNoPadding(empty));
					table.addCell(new CellNoBorderNoPadding(empty));
				}
				else
				{
					table.addCell(new CellNoBorderNoPadding(empty));
				}

				if (!compress)
				{
					tagText = getTagText(tags[i]);
				}

				Element[] elements =
						HtmlParserWrapper.createPdfObjects(tagText);
				table.addCell(PDFUtility.createElementCell(0,
						Element.ALIGN_LEFT, elements));
			}

			// Add whole method block to document
			Document.instance().add(table);
		}
	}

	/**
	 * Returns the (HTML) text for a given Tag.
	 *
	 * @param tag The tag for which to return text for the document.
	 * @return The text for the PDF document.
	 */
	private static String getTagText(Tag tag)
	{

		if (tag instanceof SeeTag)
		{

			return DocletUtility.formatSeeTag((SeeTag) tag);

		}
		else if (tag instanceof ParamTag)
		{

			ParamTag currParamTag = (ParamTag) tag;
			String name = "<code>" + currParamTag.parameterName() + "</code>";
			String pText = null;

			/*
			 * In 1.4 at least, presense of {@inheritDoc}in param means that
			 * the entire text gets replaced by the superclass' text.
			 */
			while (true)
			{
				String pComment = currParamTag.parameterComment();
				if (pComment == null
						|| pComment.indexOf(AbstractConfiguration.DOC_INLINE_TAG_INHERITDOC) == -1)
				{
					pText = DocletUtility.getComment(currParamTag.inlineTags());
					break;
				}
				pText = currParamTag.parameterComment();
				if (!(currParamTag.holder() instanceof MethodDoc))
				{
					break;
				}
				MethodDoc methodDoc = (MethodDoc) currParamTag.holder();
				if (methodDoc.overriddenMethod() == null)
				{
					break;
				}
				Tag[] superTags = methodDoc.overriddenMethod().tags(tag.name());
				if (superTags == null || superTags.length == 0)
				{
					break;
				}
				if (!(superTags[0] instanceof ParamTag))
				{
					break;
				}
				currParamTag = (ParamTag) superTags[0];
				// and repeat loop
			}

			if (pText != null && pText.length() > 0)
			{
				return name + " - " + pText;
			}
			else
			{
				return name;
			}

		}
		else if (tag instanceof ThrowsTag)
		{

			ClassDoc eDoc = ((ThrowsTag) tag).exception();
			String eName = ((ThrowsTag) tag).exceptionName();
			String eText = DocletUtility.getComment(tag.inlineTags());
			String base;
			if (eDoc != null && Destinations.isValid(eDoc.qualifiedName()))
			{
				base = "<a href=\"locallink:" + eDoc.qualifiedName() + "\">"
						+ eName + "</a>";
			}
			else
			{
				base = "<code>" + eName + "</code>";
			}
			if (eText != null && eText.length() > 0)
			{
				return base + " - " + eText;
			}
			else
			{
				return base;
			}

		}
		else
		{
			return DocletUtility.getComment(tag.inlineTags());
		}
	}

}