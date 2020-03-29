package org.areasy.common.doclet.document.elements;

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
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPTable;
import com.sun.javadoc.Doc;
import com.sun.javadoc.Tag;
import org.areasy.common.doclet.AbstractConfiguration;
import org.areasy.common.doclet.document.tags.HtmlParserWrapper;
import org.areasy.common.doclet.utilities.DocletUtility;

/**
 * Custom phrase with 'deprecated' text. A phrase object
 * is required because it may contain link chunks.
 *
 * @version $Id: CustomDeprecatedPhrase.java,v 1.2 2008/05/14 09:36:47 swd\stefan.damian Exp $
 */
public class CustomDeprecatedPhrase extends Phrase implements AbstractConfiguration
{

	public CustomDeprecatedPhrase(Doc doc)
	{
		Tag[] tags = doc.tags("deprecated");
		StringBuffer buffer = new StringBuffer("<i>");

		if ((tags != null) && (tags.length > 0))
		{
			for (int i = 0; i < tags.length; i++)
			{
				buffer.append(DocletUtility.getComment(tags[i].inlineTags()));
			}
		}
		buffer.append("</i>");

		String text = DocletUtility.stripLineFeeds(buffer.toString());
		Element[] chunks = HtmlParserWrapper.createPdfObjects(text);

		for (int i = 0; i < chunks.length; i++)
		{
			if ((chunks[i] instanceof PdfPTable) == false) super.add(chunks[i]);
		}
	}
}
