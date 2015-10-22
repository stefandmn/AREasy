package org.areasy.common.doclet.document.tags;

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

import com.lowagie.text.Element;
import com.lowagie.text.pdf.PdfPCell;
import org.areasy.common.doclet.utilities.DocletUtility;
import org.areasy.common.doclet.utilities.PDFUtility;


/**
 * TagCELL creates an iText Cell instance and returns it as the only
 * conent from getPdfObjects(), and is meant to be added to a Table
 * instance created by the TagTABLE class.
 *
 *
 */
public class TagTD extends HtmlTag
{
	/**
	 * Constant value for a header-cell/row indicator attribute.
	 */
	public static final String HEADER_INDICATOR_ATTR = "doclet.table.header.indicator";

	/**
	 * Creates a cell tag object.
	 *
	 * @param parent The parent tag.
	 * @param type   The type of this tag.
	 */
	public TagTD(HtmlTag parent, int type)
	{
		super(parent, type);
		if (type == TAG_TH) setBold(true);
	}

	private String getInheritedAttribute(String attrib, boolean includeTable)
	{
		String value = getAttribute(attrib);
		HtmlTag currTag = parent;

		while (value == null && currTag != null)
		{
			int type = currTag.getType();

			if (type == TAG_TR || type == TAG_THEAD || (includeTable && type == TAG_TABLE)) value = currTag.getAttribute(attrib);
			if (type == TAG_TABLE) break;

			currTag = currTag.parent;
		}

		return value;
	}

	private int parseSpan(String intString)
	{
		int span = 0;
		
		try
		{
			if (intString != null) span = Integer.parseInt(intString);
		}
		catch (NumberFormatException e)
		{
			DocletUtility.error(e.toString());
		}

		return (span <= 0) ? 1 : span;
	}

	private PdfPCell createCell(Element[] content)
	{
		int defaultAlign = (getType() == TAG_TH) ? Element.ALIGN_CENTER : Element.ALIGN_LEFT;

		String align = getInheritedAttribute("align", false);
		String valign = getInheritedAttribute("valign", false);
		String bgcolor = getInheritedAttribute("bgcolor", true);
		int alignment = HtmlTagUtility.getAlignment(align, defaultAlign);

		PdfPCell cell = PDFUtility.createElementCell(2, alignment, content);

		cell.setHorizontalAlignment(HtmlTagUtility.getAlignment(align, defaultAlign));
		cell.setVerticalAlignment(HtmlTagUtility.getVerticalAlignment(valign, Element.ALIGN_MIDDLE));
		cell.setBackgroundColor(HtmlTagUtility.getColor(bgcolor));
		cell.setColspan(parseSpan(getAttribute("colspan")));

		cell.setUseAscender(true);  // needs newer iText
		cell.setUseDescender(true); // needs newer iText
		cell.setUseBorderPadding(true); // needs newer iText

		if (getAttribute("nowrap") != null) cell.setNoWrap(true);
		if (getType() == TAG_TH) cell.setMarkupAttribute(HEADER_INDICATOR_ATTR, "true");

		return cell;
	}

	public Element[] toPdfObjects()
	{
		Element[] content = super.toPdfObjects();
		PdfPCell cell = createCell(content);

		return new Element[]{cell};
	}

}
