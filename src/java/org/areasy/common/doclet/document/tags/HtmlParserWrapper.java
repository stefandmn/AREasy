package org.areasy.common.doclet.document.tags;

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

import com.lowagie.text.Element;
import com.lowagie.text.List;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import org.areasy.common.doclet.AbstractConfiguration;
import org.areasy.common.doclet.document.BookmarkEntry;
import org.areasy.common.doclet.document.State;
import org.areasy.common.doclet.utilities.DocletUtility;
import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Wrapper for the 3rd-party HTML parser. The
 * purpose of this class is to isolate all
 * references to the HTML parser code in one
 * place. This way it will be easier to change
 * to a different parser if necessary.
 * @version $Id: HtmlParserWrapper.java,v 1.2 2008/05/14 09:36:47 swd\stefan.damian Exp $
 */
public class HtmlParserWrapper implements AbstractConfiguration
{
	/**
	 * Logger reference
	 */
	private static Logger log = LoggerFactory.getLog(HtmlParserWrapper.class);

	private static Tidy parser = null;

	private static BookmarkEntry rootBookmarkEntry = null;

	/**
	 * Parses an explanation text which may have HTML tags
	 * embedded, and create appropriate Pdf objects (this
	 * is somewhat similar to the rendering process of
	 * a webbrowser).
	 *
	 * @param text The explanation text (of a package, class or member).
	 * @return An array of Phrase objects.
	 */
	public static Element[] createPdfObjects(String text)
	{
		Element[] result = new Element[0];

		try
		{
			initializeParser();

			String content = preProcessHtmlContent(text);
			InputStream in = new ByteArrayInputStream(content.getBytes());

			// Parse the HTML document and return the root node
			Node node = parser.parseDOM(in, null);

			// Now build HTML object tree
			HtmlTag bodyTag = HtmlTagFactory.createTag(null, HtmlTag.TAG_BODY);
			HtmlParserWrapper.processTree(bodyTag, node);

			// Now let every HTML tag object convert itself into one or more PDF objects
			Element[] elements = bodyTag.toPdfObjects();

			// Now compress everything into a list of paragraphs
			ArrayList arrayList = new ArrayList(result.length);
			Paragraph currentParagraph = new Paragraph((float) 11.0);

			arrayList.add(currentParagraph);

			for (int i = 0; i < elements.length; i++)
			{
				if (elements[i] instanceof Paragraph)
				{
					currentParagraph = (Paragraph) elements[i];
					arrayList.add(elements[i]);

				}
				else if (elements[i] instanceof PdfPTable)
				{
					arrayList.add(elements[i]);
					currentParagraph = new Paragraph((float) 11.0);
					arrayList.add(currentParagraph);

				}
				else if (elements[i] instanceof List)
				{
					arrayList.add(elements[i]);
					currentParagraph = new Paragraph((float) 11.0);
					arrayList.add(currentParagraph);
				}
				else currentParagraph.add(elements[i]);
			}

			Object[] obj = arrayList.toArray();
			result = new Element[obj.length];

			for (int i = 0; i < obj.length; i++)
			{
				result[i] = (Element) obj[i];
			}

		}
		catch (Exception e)
		{
			log.error("HTML parsing failed with exception: (package, class, method, member): " +
				State.currentPackage + ", " + State.currentClass + ", " + State.currentMethod + ", " + State.currentMember + " : " + text, e);
		}

		return result;
	}

	/**
	 * Prints the specified node, recursively.
	 *
	 * @param node The node to start with.
	 */
	public static void processTree(HtmlTag htmlTag, Node node)
	{
		if (node == null) return;

		int nodeType = node.getNodeType();

		switch (nodeType)
		{
			case Node.DOCUMENT_NODE:
				processTree(htmlTag, ((Document) node).getDocumentElement());
				break;

			case Node.ELEMENT_NODE:

				NamedNodeMap attrs = node.getAttributes();

				if (attrs.getLength() > 0)
				{
					String attributes = "";

					for (int i = 0; i < attrs.getLength(); i++)
					{
						String attrName = attrs.item(i).getNodeName();
						String attrValue = attrs.item(i).getNodeValue();
						
						attributes = attributes + attrName;
						attributes = attributes + "=";
						attributes = attributes + attrValue;
						attributes = attributes + " ";

						htmlTag.setAttribute(attrName.toLowerCase(), attrValue);
					}
				}

				NodeList children = node.getChildNodes();
				if (children != null)
				{
					int len = children.getLength();

					for (int i = 0; i < len; i++)
					{
						String nestedTagName = children.item(i).getNodeName();
						int tagType = HtmlTagUtility.getTagType(nestedTagName);

						if (tagType != HtmlTag.TAG_UNSUPPORTED)
						{
							HtmlTag nestedTag = HtmlTagFactory.createTag(htmlTag, tagType);
							processTree(nestedTag, children.item(i));
							htmlTag.getContentTags().add(nestedTag);
						}
						else HtmlParserWrapper.processTree(htmlTag, children.item(i));
					}
				}

				break;

			case Node.TEXT_NODE:
				htmlTag.getContentTags().add(node.getNodeValue());
				break;
		}
	}

	/**
	 * Prepares the HTML content for parsing.
	 *
	 * @param text The HTML content.
	 * @return The preprocessed result.
	 */

	private static String preProcessHtmlContent(String text) throws Exception
	{
		InputStream in = new ByteArrayInputStream(text.getBytes());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		parser.parse(in, out);
		String content = out.toString();

		// Cut off any <html> tag
		if (DocletUtility.indexOfIgnoreCase(content, "<html>") == 0) content = content.substring(6, content.length());
		if (DocletUtility.indexOfIgnoreCase(content, "</html>") == (content.length() - 7)) content = content.substring(0, content.length() - 7);

		// Insert NEWPAGE tags for any NEWPAGE comments
		while (content.indexOf("<!-- NEWPAGE -->") != -1)
		{
			int pos = content.indexOf("<!-- NEWPAGE -->");
			String leftText = content.substring(0, pos);
			String rightText = content.substring(pos + 16);
			content = leftText + "<a href=\"newpage\" />" + rightText;
		}

		return content;
	}

	/**
	 * Initializes the HTML parser implementation.
	 *
	 * @throws Exception If the initialization failed.
	 */
	private static void initializeParser() throws Exception
	{
		parser = new Tidy();
		parser.setXmlTags(false);

		parser.setQuiet(true);
		parser.setShowWarnings(false);
		parser.setDropEmptyParas(false);
		parser.setMakeClean(false);  // ***  "true" changes CENTER to DIV  ***
		parser.setTrimEmptyElements(false);
		parser.setFixComments(false);
		parser.setForceOutput(true);
		parser.setHideEndTags(false);
	}

	public static BookmarkEntry getRootBookmarkEntry()
	{
		return HtmlParserWrapper.rootBookmarkEntry;
	}

	public static void setRootBookmarkEntry(BookmarkEntry rootBookmarkEntry)
	{
		HtmlParserWrapper.rootBookmarkEntry = rootBookmarkEntry;
	}
}
