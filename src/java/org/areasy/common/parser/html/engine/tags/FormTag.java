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

import org.areasy.common.parser.html.utilities.NodeList;
import org.areasy.common.parser.html.utilities.SimpleNodeIterator;

/**
 * Represents a FORM tag.
 */
public class FormTag extends CompositeTag
{

	public static final String POST = "POST";
	public static final String GET = "GET";

	/**
	 * This is the derived form location, based on action.
	 */
	protected String mFormLocation;

	/**
	 * The set of names handled by this tag.
	 */
	private static final String[] mIds = new String[]{"FORM"};

	/**
	 * The set of end tag names that indicate the end of this tag.
	 */
	private static final String[] mEndTagEnders = new String[]{"HTML", "BODY", "TABLE"};

	/**
	 * Create a new form tag.
	 */
	public FormTag()
	{
		mFormLocation = null;
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
	 * Get the list of input fields.
	 *
	 * @return Input elements in the form.
	 */
	public NodeList getFormInputs()
	{
		return (searchFor(InputTag.class, true));
	}

	/**
	 * Get the list of text areas.
	 *
	 * @return Textarea elements in the form.
	 */
	public NodeList getFormTextareas()
	{
		return (searchFor(TextareaTag.class, true));
	}

	/**
	 * Get the value of the action attribute.
	 *
	 * @return The submit url of the form.
	 */
	public String getFormLocation()
	{
		if (null == mFormLocation)
		// ... is it true that without an ACTION the default is to send it back to the same page?
		{
			mFormLocation = extractFormLocn(getPage().getUrl());
		}

		return (mFormLocation);
	}

	/**
	 * Set the form location. Modification of this element will cause the HTML rendering
	 * to change as well (in a call to toHTML()).
	 *
	 * @param url The new FORM location
	 */
	public void setFormLocation(String url)
	{
		mFormLocation = url;
		setAttribute("ACTION", url);
	}

	/**
	 * Returns the method of the form, GET or POST.
	 *
	 * @return String The method of the form (GET if nothing is specified).
	 */
	public String getFormMethod()
	{
		String ret;

		ret = getAttribute("METHOD");
		if (null == ret)
		{
			ret = GET;
		}

		return (ret);
	}

	/**
	 * Get the input tag in the form corresponding to the given name
	 *
	 * @param name The name of the input tag to be retrieved
	 * @return Tag The input tag corresponding to the name provided
	 */
	public InputTag getInputTag(String name)
	{
		InputTag inputTag;
		boolean found;
		String inputTagName;

		inputTag = null;
		found = false;
		for (SimpleNodeIterator e = getFormInputs().elements(); e.hasMoreNodes() && !found;)
		{
			inputTag = (InputTag) e.nextNode();
			inputTagName = inputTag.getAttribute("NAME");
			if (inputTagName != null && inputTagName.equalsIgnoreCase(name))
			{
				found = true;
			}
		}
		if (found)
		{
			return (inputTag);
		}
		else
		{
			return (null);
		}
	}

	/**
	 * Get the value of the name attribute.
	 *
	 * @return String The name of the form
	 */
	public String getFormName()
	{
		return (getAttribute("NAME"));
	}

	/**
	 * Find the textarea tag matching the given name
	 *
	 * @param name Name of the textarea tag to be found within the form
	 */
	public TextareaTag getTextAreaTag(String name)
	{
		TextareaTag textareaTag = null;
		boolean found = false;
		for (SimpleNodeIterator e = getFormTextareas().elements(); e.hasMoreNodes() && !found;)
		{
			textareaTag = (TextareaTag) e.nextNode();
			String textAreaName = textareaTag.getAttribute("NAME");
			if (textAreaName != null && textAreaName.equals(name))
			{
				found = true;
			}
		}
		if (found)
		{
			return (textareaTag);
		}
		else
		{
			return (null);
		}
	}

	/**
	 * @return A textual representation of the form tag.
	 */
	public String toString()
	{
		return "FORM TAG : Form at " + getFormLocation() + "; begins at : " + getStartPosition() + "; ends at : " + getEndPosition();
	}

	/**
	 * Extract the location of the image, given the tag, and the url
	 * of the html page in which this tag exists.
	 *
	 * @param url URL of web page being parsed.
	 */
	public String extractFormLocn(String url)// throws ParserException
	{
		String formURL;

		formURL = getAttribute("ACTION");
		if (null == formURL)
		{
			return "";
		}
		else
		{
			return (getPage().getLinkProcessor().extract(formURL, url));
		}
	}
}
