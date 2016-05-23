package org.areasy.common.doclet.document.tags;

/*
 * Copyright (c) 2007-2016 AREasy Runtime
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
import org.areasy.common.data.StringUtility;
import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;
import org.areasy.common.doclet.AbstractConfiguration;
import org.areasy.common.doclet.document.Fonts;
import org.areasy.common.doclet.document.State;
import org.areasy.common.doclet.utilities.DocletUtility;

import java.awt.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Locale;

/**
 * Base class for HTML tags. This class
 * also includes the HTML parsing code.
 *
 * @version $Id: HtmlTag.java,v 1.2 2008/05/14 09:36:47 swd\stefan.damian Exp $
 */
public abstract class HtmlTag implements AbstractConfiguration
{
	public static int DEFAULT_FONT_SIZE = 10;

	/** Logger reference */
	protected static Logger log = LoggerFactory.getLog(HtmlTag.class);
	
	private Hashtable attributes = new Hashtable();
	protected HtmlTag parent = null;

	// List of String or (nested) HTMLTag objects
	private ArrayList contentTags = new ArrayList(100);
	private ArrayList contentPdf = null;

	private Boolean isItalic = null;
	private Boolean isBold = false;
	private Boolean isCode = false;
	private Boolean isUnderline = false;
	private Boolean isStrikethrough = false;
	private Boolean isPre = false;
	private Boolean isLink = false;
	private Boolean isCentered = false;
	private Boolean isRight = false;
	private Integer fontSize = new Integer(DEFAULT_FONT_SIZE);
	private Color fontColor = null;
	private String fontFace = null;

	private int type = -1;

	/**
	 * Creates a HTML tag object.
	 *
	 * @param parent The parent HTML tag object (or null).
	 * @param type tag type id
	 */
	public HtmlTag(HtmlTag parent, int type)
	{
		if (parent != null) setParent(parent);

		this.type = type;
	}

	/**
	 * Sets the parent tag for this HTML tag. Which means that this tag is
	 * nested into the specified parent tag. This method also ensures that this tag
	 * inherits certain attributes from the parent tag (bold or italic text, link).
	 *
	 * @param parent The parent tag.
	 */
	public void setParent(HtmlTag parent)
	{
		this.parent = parent;

		if (parent == null) return;

		setCentered(parent.getCentered());
		setItalic(parent.getItalic());
		setBold(parent.isBold());
		setUnderline(parent.isUnderline());
		setStrikethrough(parent.isStrikethrough());
		setCode(parent.isCode());
		setLink(parent.isLink());
		setPre(parent.isPre());
		setFontSize(parent.getFontSize());
		setFontColor(parent.getFontColor());
		setFontFace(parent.getFontFace());
	}

	/**
	 * Returns one or several PDF objects which best represent this
	 * HTML tag.
	 *
	 * @return The PDF element(s) for this HTML tag. May be null if
	 *         there was a problem.
	 */
	public Element[] toPdfObjects()
	{
		try
		{
			setContentPdf(new ArrayList(100));

			Element[] openTagStuff = openTagElements();

			if (openTagStuff != null && openTagStuff.length > 0)
			{
				for (int i = 0; i < openTagStuff.length; i++)
				{
					getContentPdf().add(openTagStuff[i]);
				}
			}

			for (int i = 0; i < getContentTags().size(); i++)
			{
				Object obj = getContentTags().get(i);

				if (obj instanceof java.lang.String)
				{
					String text = (String) obj;
					
					if (text.length() > 0)
					{
						// Keep in memory if the text ended with a blank or not
						if (text.endsWith(" ")) State.setLastTagEndedWithText(true);
							else State.setLastTagEndedWithText(false);

						if (!isPre()) text = DocletUtility.stripLineFeeds(text);

						Element elem = toElement(text);
						if (elem != null) getContentPdf().add(elem);
					}
				}
				else
				{
					HtmlTag tag = (HtmlTag) obj;
					Element[] subElements = tag.toPdfObjects();

					if (subElements != null && subElements.length > 0) addNestedTagContent(subElements);
				}
			}

			Element[] closeTagStuff = closeTagElements();

			if (closeTagStuff != null && closeTagStuff.length > 0)
			{
				for (int i = 0; i < closeTagStuff.length; i++)
				{
					getContentPdf().add(closeTagStuff[i]);
				}
			}

			Element[] elements = new Element[getContentPdf().size()];
			elements = (Element[]) getContentPdf().toArray(elements);

			return elements;
		}
		catch (RuntimeException e)
		{
			DocletUtility.error("Failed to create PDF objects", e);
		}

		return null;
	}

	/**
	 * Adds elements of a nested tag to the contents
	 * of this HTML tag.
	 *
	 * @param elements The PDF elements of the nexted tag.
	 */
	public void addNestedTagContent(Element[] elements)
	{
		// special hack for list elements without specified
		// list type (<li> tags without <ul> or <ol>)
		boolean missingList = false;
		for (int i = 0; i < elements.length; i++)
		{
			if (elements[i] instanceof ListItem) missingList = true;
		}

		List list = null;
		if (missingList) list = new List(false, 8);

		for (int i = 0; i < elements.length; i++)
		{
			if (missingList && elements[i] instanceof ListItem) list.add(elements[i]);
				else getContentPdf().add(elements[i]);
		}

		if (missingList && list != null) getContentPdf().add(list);
	}

	/**
	 * This method must be implemented by subclasses in order
	 * to provide a mechanism to convert an HTML tag into
	 * a PDF document object.
	 *
	 * @param text The text with HTML tags.
	 * @return The PDF Element representint that HTML code.
	 */
	public Element toElement(String text)
	{
		if (!isPre()) text = DocletUtility.stripLineFeeds(text);

		Phrase result = new Phrase();
		result.add(new Chunk(text, getFont()));

		return result;
	}

	/**
	 * Returns any number of PDF Elements preceeding
	 * a given HTML tag. For a H1-H6, for example,
	 * there will always be a preceeding Paragraph
	 * in order to provide some leading space.
	 * This default method returns no objects.
	 *
	 * @return Any number of PDF Elements (may be null).
	 */
	public Element[] openTagElements()
	{
		return null;
	}

	/**
	 * Returns any number of PDF Elements following
	 * a given HTML tag. For a H1-H6, for example,
	 * there will always be a Paragraph following
	 * in order to provide some additional space.
	 * This default method returns no objects.
	 *
	 * @return Any number of PDF Elements (may be null).
	 */
	public Element[] closeTagElements()
	{
		return null;
	}

	/**
	 * Returns the leading for this type of HTML tag
	 * in the PDF document. For most tags it's just
	 * about the font size.
	 *
	 * @return The leading for this tag.
	 */
	public float getLeading()
	{
		Font font = getFont();
		float leading = (float) font.size() + (float) 1.0;
		
		return leading;
	}

	/**
	 * Creates a PDF Paragraph with the appropriate
	 * alignment and the default leading and correct font.
	 *
	 * @param content The text that goes into the Paragraph.
	 * @return The resulting PDF Paragraph object.
	 */
	public Paragraph createParagraph(String content)
	{
		return createParagraph(new Chunk(content, getFont()));
	}

	/**
	 * Creates a PDF Paragraph with the appropriate
	 * alignment and the default leading and correct font.
	 *
	 * @param content The Chunk that goes into the Paragraph.
	 * @return The resulting PDF Paragraph object.
	 */
	public Paragraph createParagraph(Chunk content)
	{
		Paragraph result = new Paragraph(getLeading(), content);

		if (isCentered()) result.setAlignment(Element.ALIGN_CENTER);
		if (isRight()) result.setAlignment(Element.ALIGN_RIGHT);

		return result;
	}

	/**
	 * Returns the appropriate PDF font for this
	 * HTML tag. The font is created based on the
	 * type and attributes of the tag.
	 *
	 * @return The PDF document font.
	 */
	public Font getFont()
	{
		Font font;

		int faceId = TEXT_FONT;
		boolean parentIsFixedFont = false;

		if (isCode() || isPre()) faceId = CODE_FONT;

		if (parent != null)
		{
			if(getFontSize() == DEFAULT_FONT_SIZE) setFontSize(parent.getFontSize());
			parentIsFixedFont = parent.isCode() || parent.isPre();
		}

		// Pre-formatted text parts tend to appear bigger, so make them 1 point smaller
		if ((isCode() || isPre()) && !parentIsFixedFont) setFontSize(getFontSize() - 1);

		if (type == TAG_I) setItalic(true);
		if (type == TAG_B) setBold(true);
		if (type == TAG_U) setUnderline(true);

		if (type == TAG_H1) setFontSize(26);
		if (type == TAG_H2) setFontSize(22);
		if (type == TAG_H3) setFontSize(19);
		if (type == TAG_H4) setFontSize(16);
		if (type == TAG_H5) setFontSize(13);
		if (type == TAG_H6) setFontSize(10);

		int style = 0;

		if (isBold() && isItalic()) style = BOLD + ITALIC;
			else if (isBold()) style = BOLD;
				else if (isItalic()) style = ITALIC;

		if (isLink()) style = style + LINK;
		if (isUnderline()) style = style + UNDERLINE;
		if (isStrikethrough()) style = style + STRIKETHROUGH;

		if (type == TAG_BODY && Locale.getDefault().getLanguage().equals(Locale.JAPANESE.getLanguage())) font = FontFactory.getFont("HeiseiMin-W3", "UniJIS-UCS2-HW-H", getFontSize(), style, getFontColor());
			else font = Fonts.getFont(faceId, style, getFontSize());

		if(getFontColor() == null && parent != null && parent.getFontColor() != null) setFontColor(parent.getFontColor());
		if(getFontFace() == null && parent != null && parent.getFontFace() != null) setFontFace(parent.getFontFace());

		if(getFontColor() != null) font.setColor(getFontColor());
		if(StringUtility.isNotEmpty(getFontFace())) font.setFamily(getFontFace());

		return font;
	}

	/**
	 * Determines if the content text of this HTML tag
	 * is italic.
	 *
	 * @return True if it is italic, false if it's not.
	 */
	public boolean isItalic()
	{
		if(getItalic() != null) return getItalic().booleanValue();
			else return false;
	}

	/**
	 * Determines if the content text of this HTML tag
	 * is bold.
	 *
	 * @return True if it is bold, false if it's not.
	 */
	public boolean isBold()
	{
		if(getBold() != null) return getBold().booleanValue();
			else return false;
	}

	/**
	 * Determines if the content text of this HTML tag
	 * is preformatted.
	 *
	 * @return True if it is preformatted, false if it's not.
	 */
	public boolean isPre()
	{
		if(getPre() != null) return getPre().booleanValue();
			else return false;
	}

	/**
	 * Determines if the content text of this HTML tag
	 * is a link.
	 *
	 * @return True if it is a link, false if it's not.
	 */
	public boolean isLink()
	{
		if(getLink() != null) return getLink().booleanValue();
			else return false;
	}

	/**
	 * Determines if the content text of this HTML tag
	 * is underlined.
	 *
	 * @return True if it is underlined, false if it's not.
	 */
	public boolean isUnderline()
	{
		if(getUnderline() != null) return getUnderline().booleanValue();
			else return false;
	}

	/**
	 * Determines if the content text of this HTML tag
	 * is strikethrough.
	 *
	 * @return True if it is strikethrough, false if it's not.
	 */
	public boolean isStrikethrough()
	{
		if(getStrikethrough() != null) return getStrikethrough().booleanValue();
			else return false;
	}

	/**
	 * Determines if the content text of this HTML tag
	 * is code.
	 *
	 * @return True if it is code, false if it's not.
	 */
	public boolean isCode()
	{
		if(getCode() != null) return getCode().booleanValue();
			else return false;
	}

	/**
	 * Determines if the content text of this HTML tag
	 * is aligned centered.
	 *
	 * @return True if it is aligned centered, false if it's not.
	 */
	public boolean isCentered()
	{
		if(getCentered() != null) return getCentered().booleanValue();
			else return false;
	}

	/**
	 * Determines if the content text of this HTML tag
	 * is aligned right.
	 *
	 * @return True if it is aligned right, false if it's not.
	 */
	public boolean isRight()
	{
		if(getRight() != null) return getRight().booleanValue();
			else return false;
	}

	/**
	 * Returns an attribute of this HTML tag. Attributes are optional values, like
	 * "border=.." for the "<table>" tag.
	 *
	 * @param key The name of the attribute.
	 * @return The value of the attribute or null if no such attribute was found.
	 */
	protected String getAttribute(String key)
	{
		if (attributes.get(key) == null) return null;

		return (String) attributes.get(key);
	}

	protected void setContentTags(ArrayList contentTags)
	{
		this.contentTags = contentTags;
	}

	/**
	 * Returns the tags contained within this
	 * HTML tag as an ArrayList.
	 *
	 * @return The contained tags.
	 */
	public ArrayList getContentTags()
	{
		return contentTags;
	}

	protected ArrayList getContentPdf()
	{
		return contentPdf;
	}

	protected void setContentPdf(ArrayList contentPdf)
	{
		this.contentPdf = contentPdf;
	}

	/**
	 * Returns the type of this HTML tag.
	 *
	 * @return The HTML tag type.
	 */
	public int getType()
	{
		return type;
	}

	/**
	 * Sets an attribute for this HTML tag object.
	 *
	 * @param key   The name of the attribute.
	 * @param value The value of the attribute.
	 */
	protected void setAttribute(String key, String value)
	{
		if (value == null) value = "";

		attributes.put(key, value);

		// set common attributes like alignmenent just after reading attributes
		String align = getAttribute("align");

		if(align != null)
		{
			if (align.equalsIgnoreCase("center")) setCentered(true);

			if (align.equalsIgnoreCase("right")) setRight(true);
		}
	}

	/**
	 * Returns a string representation of the object. In general, the
     * <code>toString</code> method returns a string that
     * "textually represents" this object.
	 */
	public String toString()
	{
		return HtmlTagFactory.tags[getType()];
	}

	/**
	 * Set color property for the current HTML tag.
	 *
	 * @param fontColor color structure.
	 */
	protected void setFontColor(Color fontColor)
	{
		this.fontColor = fontColor;
	}

	protected Color getFontColor()
	{
		return fontColor;
	}

	protected Boolean getItalic()
	{
		return isItalic;
	}

	protected void setItalic(boolean italic)
	{
		isItalic = new Boolean(italic);
	}

	protected void setItalic(Boolean italic)
	{
		isItalic = italic;
	}

	protected Boolean getBold()
	{
		return isBold;
	}

	protected void setBold(boolean bold)
	{
		isBold = new Boolean(bold);
	}

	protected void setBold(Boolean bold)
	{
		isBold = bold;
	}

	protected Boolean getCode()
	{
		return isCode;
	}

	protected void setCode(boolean code)
	{
		isCode = new Boolean(code);
	}

	protected void setCode(Boolean code)
	{
		isCode = code;
	}

	protected Boolean getUnderline()
	{
		return isUnderline;
	}

	protected void setUnderline(boolean underline)
	{
		isUnderline = new Boolean(underline);
	}

	protected void setUnderline(Boolean underline)
	{
		isUnderline = underline;
	}

	protected Boolean getStrikethrough()
	{
		return isStrikethrough;
	}

	protected void setStrikethrough(boolean strikethrough)
	{
		isStrikethrough = new Boolean(strikethrough);
	}

	protected void setStrikethrough(Boolean strikethrough)
	{
		isStrikethrough = strikethrough;
	}

	protected Boolean getPre()
	{
		return isPre;
	}

	protected void setPre(boolean pre)
	{
		isPre = new Boolean(pre);
	}

	protected void setPre(Boolean pre)
	{
		isPre = pre;
	}

	protected Boolean getLink()
	{
		return isLink;
	}

	protected void setLink(boolean link)
	{
		isLink = new Boolean(link);
	}

	protected void setLink(Boolean link)
	{
		isLink = link;
	}

	protected Boolean getCentered()
	{
		return isCentered;
	}

	protected void setCentered(boolean centered)
	{
		isCentered = new Boolean(centered);
	}

	protected void setCentered(Boolean centered)
	{
		isCentered = centered;
	}

	protected Boolean getRight()
	{
		return isRight;
	}

	protected void setRight(boolean right)
	{
		isRight = new Boolean(right);
	}

	protected void setRight(Boolean right)
	{
		isRight = right;
	}

	protected int getFontSize()
	{
		return fontSize;
	}

	protected void setFontSize(int fontSize)
	{
		this.fontSize = new Integer(fontSize);
	}

	protected void setFontSize(Integer fontSize)
	{
		this.fontSize = fontSize;
	}

	protected void setFontSize(String fontSize)
	{
		if(fontSize == null || fontSize.length() == 0) return;
		else if(fontSize.endsWith("px"))
		{
			int size = HtmlTagUtility.parseInt(fontSize, getFontSize());
			this.fontSize = new Integer(size);
		}
		else
		{
			int size = HtmlTagUtility.parseInt(fontSize, getFontSize());
			this.fontSize = new Integer(DEFAULT_FONT_SIZE + size);
		}
	}

	protected String getFontFace()
	{
		return fontFace;
	}

	protected void setFontFace(String fontFace)
	{
		this.fontFace = fontFace;
	}
}
