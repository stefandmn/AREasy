package org.areasy.common.parser.html.engine.tags;

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

/**
 * An option tag within a form.
 */
public class OptionTag extends CompositeTag
{

	/**
	 * The set of names handled by this tag.
	 */
	private static final String[] mIds = new String[]{"OPTION"};

	/**
	 * The set of tag names that indicate the end of this tag.
	 */
	private static final String[] mEnders = new String[]{"INPUT", "TEXTAREA", "SELECT", "OPTION"};

	/**
	 * The set of end tag names that indicate the end of this tag.
	 */
	private static final String[] mEndTagEnders = new String[]{"SELECT", "FORM", "BODY", "HTML"};

	/**
	 * Create a new option tag.
	 */
	public OptionTag()
	{
	}

	/**
	 * Return the set of names handled by this tag.
	 *
	 * @return The names to be matched that create tags of this type.
	 */
	public String[] getIds()
	{
		return (mIds);
	}

	/**
	 * Return the set of tag names that cause this tag to finish.
	 *
	 * @return The names of following tags that stop further scanning.
	 */
	public String[] getEnders()
	{
		return (mEnders);
	}

	/**
	 * Return the set of end tag names that cause this tag to finish.
	 *
	 * @return The names of following end tags that stop further scanning.
	 */
	public String[] getEndTagEnders()
	{
		return (mEndTagEnders);
	}

	/**
	 * Get the value of the value attribute.
	 */
	public String getValue()
	{
		return (getAttribute("VALUE"));
	}

	/**
	 * Set the value of the value attribute.
	 */
	public void setValue(String value)
	{
		this.setAttribute("VALUE", value);
	}

	/**
	 * Get the text of this optin.
	 */
	public String getOptionText()
	{
		return toPlainTextString();
	}

	public String toString()
	{
		String output = "OPTION VALUE: " + getValue() + " TEXT: " + getOptionText() + "\n";
		return output;
	}

}
