package org.areasy.common.doclet;

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

import java.awt.*;


/**
 * Holder for constants and definitions.
 * 
 * @version $Id: AbstractConfiguration.java,v 1.4 2008/05/14 09:36:48 swd\stefan.damian Exp $
 */
public interface AbstractConfiguration
{
	/*
	 * Javadoc tag names
	 */
	public final static String DOC_TAG_PARAM = "param";
	public final static String DOC_TAG_RETURN = "return";
	public final static String DOC_TAG_THROWS = "throws";
	public final static String DOC_TAG_EXCEPTION = "exception";
	public final static String DOC_TAG_VERSION = "version";
	public final static String DOC_TAG_SINCE = "since";
	public final static String DOC_TAG_SEE = "see";
	public final static String DOC_TAG_AUTHOR = "author";

	public final static String DOC_TAGS_DEPRECATED = "@deprecated";
	public final static String DOC_INLINE_TAG_INHERITDOC = "{@inheritDoc}";

	/*
	 * Label defaults
	 */
	public static String LB_TITLE = "Title";
	public static String LB_DESCRIPTION = "Description";
	public static String LB_INDEX = "Index";
	public static String LB_OVERVIEW = "Overview";
	public static String LB_APPENDIX = "Appendix";
	public static String LB_APPENDICES = "Appendices";
	public static String LB_PACKAGE = "Package";
	public static String LB_PARM_TAG = "Parameters:";
	public static String LB_EXCEP_TAG = "Throws:";
	public static String LB_SEE_TAG = "See Also:";
	public static String LB_SINCE_TAG = "Since:";
	public static String LB_AUTHOR_TAG = "Author:";
	public static String LB_VERSION_TAG = "Version:";
	public static String LB_RETURNS_TAG = "Returns:";
	public static String LB_DEPRECATED_TAG = "Deprecated. ";
	public static String LB_PACKAGES = "Packages";
	public static String LB_OTHERPACKAGES = "Other Packages";
	public static String LB_CLASSES = "All Classes";
	public static String LB_FIELDS = "Fields";
	public static String LB_METHODS = "Methods";
	public static String LB_CONSTRUCTORS = "Constructors";

	/*
	 * HTML tag constants
	 */
	public final static int TAG_UNSUPPORTED = -1;
	public final static int TAG_BODY = 0;
	public final static int TAG_P = 1;
	public final static int TAG_BR = 2;
	public final static int TAG_CODE = 3;
	public final static int TAG_PRE = 4;
	public final static int TAG_BLOCKQUOTE = 5;
	public final static int TAG_CENTER = 6;
	public final static int TAG_TABLE = 7;
	public final static int TAG_TR = 8;
	public final static int TAG_TD = 9;
	public final static int TAG_I = 10;
	public final static int TAG_B = 11;
	public final static int TAG_TT = 12;
	public final static int TAG_UL = 13;
	public final static int TAG_OL = 14;
	public final static int TAG_LI = 15;
	public final static int TAG_EM = 16;
	public final static int TAG_A = 17;
	public final static int TAG_H1 = 18;
	public final static int TAG_H2 = 19;
	public final static int TAG_H3 = 20;
	public final static int TAG_H4 = 21;
	public final static int TAG_H5 = 22;
	public final static int TAG_H6 = 23;
	public final static int TAG_IMG = 24;
	public final static int TAG_U = 25;
	public final static int TAG_NEWPAGE = 26;
	public final static int TAG_TH = 27;
	public final static int TAG_THEAD = 28;
	public final static int TAG_HR = 29;
	public final static int TAG_S = 30;
	public final static int TAG_STRIKE = 31;
	public final static int TAG_DEL = 32;
	public final static int TAG_INS = 33;
	public final static int TAG_DL = 34;
	public final static int TAG_DT = 35;
	public final static int TAG_DD = 36;
	public final static int TAG_STRONG = 37;
	public final static int TAG_DIV = 38;
	public final static int TAG_FONT = 39;

	/*
	 * Argument / properties names
	 */
	public final static String ARG_SOURCEPATH = "sourcepath";
	public final static String ARG_PDF = "pdf";
	public final static String ARG_CONFIG = "config";
	public final static String ARG_DEBUG = "debug";
	public final static String ARG_AUTHOR = "author";
	public final static String ARG_VERSION = "version";
	public final static String ARG_GROUP = "group";
	public final static String ARG_DONTSPEC = "dontspec";
	public final static String ARG_SORT = "sort";
	public final static String ARG_WORKDIR = "workdir";
	public final static String ARG_SINCE = "since";
	public final static String ARG_SUMMARY_TABLE = "summary.table";
	public final static String ARG_INHERITED_SUMMARY_TABLE = "inherited.summary.table";
	public final static String ARG_CREATE_LINKS = "create.links";
	public final static String ARG_CREATE_INDEX = "create.index";

	public final static String ARG_ALLOW_ENCRYPTION = "allow.encryption";
	public final static String ARG_ALLOW_PRINTING = "allow.printing";
	public final static String ARG_HEADER_LEFT = "header.left";
	public final static String ARG_HEADER_CENTER = "header.center";
	public final static String ARG_HEADER_RIGHT = "header.right";
	public final static String ARG_PGN_TYPE = "page.numbers.type";
	public final static String ARG_PGN_PREFIX = "page.numbers.prefix";
	public final static String ARG_PGN_ALIGNMENT = "page.numbers.alignment";
	public final static String ARG_CREATE_FRAME = "create.frame";
	public final static String ARG_DOC_TITLE_PAGE = "document.title.page";
	public final static String ARG_DOC_DESCRIPTION_PAGE = "document.description.page";
	public final static String ARG_DOC_TITLE_FILE = "document.title.file";
	public final static String ARG_API_DESCRIPTION_FILE = "document.description.file";
	public final static String ARG_API_DESCRIPTION_BOOKMARKS = "document.description.bookmarks";
	public final static String ARG_DOC_TITLE = "document.title";
	public final static String ARG_DOC_COPYRIGHT = "document.copyright";
	public final static String ARG_DOC_AUTHOR = "document.author";
	public final static String ARG_DOC_VERSION = "document.version";
	public final static String ARG_OVERVIEW_PDF_FILE = "overview.pdf.file";
	public final static String ARG_DOC_MARGIN_LEFT = "document.margin.left";
	public final static String ARG_DOC_MARGIN_RIGHT = "document.margin.right";
	public final static String ARG_DOC_MARGIN_TOP = "document.margin.top";
	public final static String ARG_DOC_MARGIN_BOTTOM = "document.margin.bottom";

	public final static String ARG_FILTER = "filter";
	public final static String ARG_FILTER_TAGS = "filter.tags";
	public final static String ARG_FILTER_TAG_PREFIX = "filter.tag.";

	public final static String ARG_APPENDIX_PREFIX = "appendix.";
	public final static String ARG_APPENDIX_NAME_SUFFIX = ".name";
	public final static String ARG_APPENDIX_FILE_SUFFIX = ".file";
	public final static String ARG_APPENDIX_TITLE_SUFFIX = ".title";

	public final static String ARG_LB_APPENDIX = "label.appendix";

	public final static String ARG_LB_OUTLINE_TITLE = "label.bookmarks.title";
	public final static String ARG_LB_OUTLINE_DESCRIPTION = "label.bookmarks.description";
	public final static String ARG_LB_OUTLINE_INDEX = "label.bookmarks.index";
	public final static String ARG_LB_OUTLINE_OVERVIEW = "label.bookmarks.overview";
	public final static String ARG_LB_OUTLINE_PACKAGES = "label.bookmarks.packages";
	public final static String ARG_LB_OUTLINE_OTHERPACKAGES = "label.bookmarks.otherPackages";
	public final static String ARG_LB_OUTLINE_CLASSES = "label.bookmarks.classes";
	public final static String ARG_LB_OUTLINE_APPENDICES = "label.bookmarks.appendices";

	public final static String ARG_LB_FIELDS = "label.fields";
	public final static String ARG_LB_METHODS = "label.methods";
	public final static String ARG_LB_CONSTRUCTORS = "label.constructors";

	/**
	 * Prefix for configuration property which overrides a tag label.
	 */
	public final static String ARG_LB_TAGS_PREFIX = "label.tag.";

	/*
	 * Temporary parameters for fonts
	 */
	public final static String ARG_FONT_TEXT_NAME = "font.text.name";
	public final static String ARG_FONT_TEXT_ENC = "font.text.enc";
	public final static String ARG_FONT_CODE_NAME = "font.code.name";
	public final static String ARG_FONT_CODE_ENC = "font.code.enc";

	/*
	 * Argument / prop values
	 */
	public final static String ARG_VAL_NO = "no";
	public final static String ARG_VAL_YES = "yes";
	public final static String ARG_VAL_INTERNAL = "internal";
	public final static String ARG_VAL_NONE = "none";
	public final static String ARG_VAL_FULL = "full";
	public final static String ARG_VAL_SWITCH = "switch";
	public final static String ARG_VAL_CENTER = "center";
	public final static String ARG_VAL_LEFT = "left";
	public final static String ARG_VAL_RIGHT = "right";
	public final static String ARG_VAL_PDF = "api.pdf";

	/*
	 * Font constants
	 */
	public static int TEXT_FONT = 0;
	public static int CODE_FONT = 1;
	public static int PLAIN = 0;
	public static int BOLD = 1;
	public static int ITALIC = 2;
	public static int LINK = 4;
	public static int UNDERLINE = 8;
	public static int STRIKETHROUGH = 16;

	public final static Color COLOR_BLACK = new Color(0, 0, 0);
	public final static Color COLOR_LIGHT_GRAY = new Color(0xEF, 0xEF, 0XEF);
	public final static Color COLOR_LIGHTER_GRAY = new Color(0xF4, 0xF4, 0XF4);
	public final static Color COLOR_SUMMARY_HEADER = new Color(0xCC, 0xCC, 0xFF);
	public final static Color COLOR_INHERITED_SUMMARY = new Color(0xF0, 0xF0, 0xFF);
	public final static Color COLOR_LINK = new Color(0, 0, 240);

	public static int HEADER_DEFAULT = 0;
	public static int HEADER_API = 1;
	public static int HEADER_INDEX = 2;
	public static int HEADER_OVERVIEW = 3;
	public static int HEADER_APPENDIX = 4;
	public static int HEADER_DETAILS = 5;

	public static float LEFT_MARGIN_WIDTH = 34;
	public static float RIGHT_MARGIN_WIDTH = 34;
	public static float TOP_MARGIN_WIDTH = 50;
	public static float BOTTOM_MARGIN_WIDTH = 80;
	public static float DOCUMENT_WIDTH = 598;
	public static float LEFT_MARGIN = 34;
	public static float RIGHT_MARGIN = DOCUMENT_WIDTH - RIGHT_MARGIN_WIDTH;
	public static float FOOTER_BASELINE = 30;
	public static float HEADER_BASELINE = 820;

	/**
	 * For summaries of inherited methods
	 */
	public final static int SHOW_METHODS = 1;

	/**
	 * For summaries of inherited fields
	 */
	public final static int SHOW_FIELDS = 2;

	/**
	 * Predefined name for additional custom files directory
	 */
//	public static final String DOC_FILES = "doc-files";

	public static final String ANCHOR_PREFIX = "LOCALANCHOR:";

	public static final String ROOT_BOOKMARK = "ROOT:";

	public static final String PREFIX_ANCHOR_CLASS = "CLASS:";
	public static final String PREFIX_ANCHOR_METHOD = "METHOD:";
	public static final String PREFIX_ANCHOR_FIELD = "FIELD:";
	public static final String PREFIX_ANCHOR_CONSTRUCTOR = "CONSTRUCTOR:";
}
