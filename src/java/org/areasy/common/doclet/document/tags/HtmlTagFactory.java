package org.areasy.common.doclet.document.tags;

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

/**
 * Factory for creating HTML tag objects.
 *
 *
 */
public class HtmlTagFactory
{
	/**
	 * Array with HTML tag names. NOTE: The contents of this
	 * table relate directly to the HTML tag constants defined
	 * in the {@see org.areasy.common.doclet.document.IConstants IConstants} interface.
	 */
	protected static String[] tags =
	{
		"body",
		"p",
		"br",
		"code",
		"pre",
		"blockquote",
		"center",
		"table",
		"tr",
		"td",
		"i",
		"b",
		"tt",
		"ul",
		"ol",
		"li",
		"em",
		"a",
		"h1",
		"h2",
		"h3",
		"h4",
		"h5",
		"h6",
		"img",
		"u",
		"newpage",
		"th",
		"thead",
		"hr",
		"s",
		"strike",
		"del",
		"ins",
		"dl",
		"dt",
		"dd",
		"strong",
		"div",
		"font"
	};

	/**
	 * Creates a HTML tag object of the given type.
	 *
	 * @param parent The parent HTML tag. Can be null if the HTML tag
	 *               to be created does not have a parent tag (i.e. is
	 *               not a nested tag).
	 * @param type   The type for the HTML tag to be created.
	 * @return A HTML tag object or null if none could be created.
	 */
	public static HtmlTag createTag(HtmlTag parent, int type)
	{
		switch (type)
		{
			case HtmlTag.TAG_FONT:
				return new TagFONT(parent, type);

			case HtmlTag.TAG_UL:
				return new TagUL(parent, type);

			case HtmlTag.TAG_OL:
				return new TagOL(parent, type);

			case HtmlTag.TAG_LI:
				return new TagLI(parent, type);

			case HtmlTag.TAG_P:
				return new TagP(parent, type);

			case HtmlTag.TAG_BR:
				return new TagBR(parent, type);

			case HtmlTag.TAG_A:
				return new TagA(parent, type);

			case HtmlTag.TAG_EM:
				return new TagEM(parent, type);

			case HtmlTag.TAG_B:
				return new TagB(parent, type);

			case HtmlTag.TAG_INS:
			case HtmlTag.TAG_U:
				return new TagU(parent, type);

			case HtmlTag.TAG_I:
				return new TagI(parent, type);

			case HtmlTag.TAG_CODE:
				return new TagCODE(parent, type);

			case HtmlTag.TAG_DEL:
			case HtmlTag.TAG_S:
			case HtmlTag.TAG_STRIKE:
				return new TagSTRIKE(parent, type);

			case HtmlTag.TAG_H1:
			case HtmlTag.TAG_H2:
			case HtmlTag.TAG_H3:
			case HtmlTag.TAG_H4:
			case HtmlTag.TAG_H5:
			case HtmlTag.TAG_H6:
				return new TagH(parent, type);

			case HtmlTag.TAG_CENTER:
				return new TagCENTER(parent, type);

			case HtmlTag.TAG_DIV:
				return new TagDIV(parent, type);

			case HtmlTag.TAG_PRE:
				return new TagPRE(parent, type);

			case HtmlTag.TAG_IMG:
				return new TagIMG(parent, type);

			case HtmlTag.TAG_HR:
				return new TagHR(parent, type);

			case HtmlTag.TAG_TABLE:
				return new TagTABLE(parent, type);

			case HtmlTag.TAG_THEAD:
				return new TagTHEAD(parent, type);

			case HtmlTag.TAG_TR:
				return new TagTR(parent, type);

			case HtmlTag.TAG_TD:
			case HtmlTag.TAG_TH:
				return new TagTD(parent, type);

			case HtmlTag.TAG_DL:
				return new TagDL(parent, type);

			case HtmlTag.TAG_DT:
				return new TagDT(parent, type);

			case HtmlTag.TAG_DD:
				return new TagDD(parent, type);

			case HtmlTag.TAG_STRONG:
				return new TagSTRONG(parent, type);

			case HtmlTag.TAG_BODY:
			default:
				return new TagBODY(parent, type);
		}
	}
}
