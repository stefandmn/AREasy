package org.areasy.common.parser.html.engine.tags;

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

import org.areasy.common.parser.html.engine.Node;
import org.areasy.common.parser.html.engine.scanners.ScriptScanner;
import org.areasy.common.parser.html.utilities.SimpleNodeIterator;

/**
 * A script tag.
 */
public class ScriptTag extends CompositeTag
{

	/**
	 * The set of names handled by this tag.
	 */
	private static final String[] mIds = new String[]{"SCRIPT"};

	/**
	 * The set of end tag names that indicate the end of this tag.
	 */
	private static final String[] mEndTagEnders = new String[]{"BODY", "HTML"};

	/**
	 * Script code if different from the page contents.
	 */
	protected String mCode;

	/**
	 * Create a new script tag.
	 */
	public ScriptTag()
	{
		setThisScanner(new ScriptScanner());
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
	 * Return the set of end tag names that cause this tag to finish.
	 *
	 * @return The names of following end tags that stop further scanning.
	 */
	public String[] getEndTagEnders()
	{
		return (mEndTagEnders);
	}

	/**
	 * Get the language attribute value.
	 */
	public String getLanguage()
	{
		return (getAttribute("LANGUAGE"));
	}

	/**
	 * Get the script code.
	 * Normally this is the contents of the children, but in the rare case that
	 * the script is encoded, this is the plaintext decrypted code.
	 *
	 * @return The plaintext or overridden code contents of the tag.
	 */
	public String getScriptCode()
	{
		String ret;

		if (null != mCode)
		{
			ret = mCode;
		}
		else
		{
			ret = getChildrenHTML();
		}

		return (ret);
	}

	/**
	 * Set the code contents.
	 *
	 * @param code The new code contents of this tag.
	 */
	public void setScriptCode(String code)
	{
		mCode = code;
	}

	/**
	 * Get the type attribute value.
	 */
	public String getType()
	{
		return (getAttribute("TYPE"));
	}

	/**
	 * Set the language of the script tag.
	 *
	 * @param language The new language value.
	 */
	public void setLanguage(String language)
	{
		setAttribute("LANGUAGE", language);
	}

	/**
	 * Set the type of the script tag.
	 *
	 * @param type The new type value.
	 */
	public void setType(String type)
	{
		setAttribute("TYPE", type);
	}

	protected void putChildrenInto(StringBuffer sb)
	{
		Node node;

		if (null != getScriptCode())
		{
			sb.append(getScriptCode());
		}
		else
		{
			for (SimpleNodeIterator e = children(); e.hasMoreNodes();)
			{
				node = e.nextNode();
				// eliminate virtual tags
				//            if (!(node.getStartPosition () == node.getEndPosition ()))
				sb.append(node.toHtml());
			}
		}
	}

	/**
	 * Print the contents of the script tag.
	 */
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("Script Node : \n");
		if (getLanguage() != null || getType() != null)
		{
			sb.append("Properties -->\n");
			if (getLanguage() != null && getLanguage().length() != 0)
			{
				sb.append("[Language : " + getLanguage() + "]\n");
			}
			if (getType() != null && getType().length() != 0)
			{
				sb.append("[Type : " + getType() + "]\n");
			}
		}
		sb.append("\n");
		sb.append("Code\n");
		sb.append("****\n");
		sb.append(getScriptCode() + "\n");
		return sb.toString();
	}
}
