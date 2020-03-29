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
import org.areasy.common.parser.html.engine.visitors.NodeVisitor;
import org.areasy.common.parser.html.utilities.ParserTool;
import org.areasy.common.parser.html.utilities.SimpleNodeIterator;

/**
 * Identifies a link tag.
 */
public class LinkTag extends CompositeTag
{

	/**
	 * The set of names handled by this tag.
	 */
	private static final String[] mIds = new String[]{"A"};

	/**
	 * The set of tag names that indicate the end of this tag.
	 */
	private static final String[] mEnders = new String[]{"A", "P", "DIV", "TD", "TR", "FORM", "LI"};

	/**
	 * The set of end tag names that indicate the end of this tag.
	 */
	private static final String[] mEndTagEnders = new String[]{"P", "DIV", "TD", "TR", "FORM", "LI", "BODY", "HTML"};

	/**
	 * The URL where the link points to
	 */
	protected String mLink;

	/**
	 * Set to true when the link was a mailto: URL.
	 */
	private boolean mailLink;

	/**
	 * Set to true when the link was a javascript: URL.
	 */
	private boolean javascriptLink;

	/**
	 * Constructor creates an LinkTag object, which basically stores the location
	 * where the link points to, and the text it contains.
	 * <p/>
	 * In order to get the contents of the link tag, use the method linkData(),
	 * which returns an enumeration of nodes encapsulated within the link.
	 * <p/>
	 * The following code will get all the images inside a link tag.
	 * <pre>
	 * Node node ;
	 * ImageTag imageTag;
	 * for (Enumeration e=linkTag.linkData();e.hasMoreElements();) {
	 *      node = (Node)e.nextElement();
	 *      if (node instanceof ImageTag) {
	 *          imageTag = (ImageTag)node;
	 *          // Process imageTag
	 *      }
	 * }
	 * </pre>
	 */
	public LinkTag()
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
	 * Returns the accesskey attribute value, if any.
	 */
	public String getAccessKey()
	{
		return (getAttribute("ACCESSKEY"));
	}

	/**
	 * Returns the url as a string, to which this link points.
	 * This string has had the "mailto:" and "javascript:" protocol stripped
	 * off the front (if those predicates return <code>true</code>) but not
	 * for other protocols. Don't ask me why, it's a legacy thing.
	 */
	public String getLink()
	{
		if (null == mLink)
		{
			mailLink = false;
			javascriptLink = false;
			mLink = extractLink();

			int mailto = mLink.indexOf("mailto");
			if (mailto == 0)
			{
				// yes it is
				mailto = mLink.indexOf(":");
				mLink = mLink.substring(mailto + 1);
				mailLink = true;
			}
			int javascript = mLink.indexOf("javascript:");
			if (javascript == 0)
			{
				mLink = mLink.substring(11); // this magic number is "javascript:".length()
				javascriptLink = true;
			}
		}
		return (mLink);
	}

	/**
	 * Returns the text contained inside this link tag
	 */
	public String getLinkText()
	{
		String ret;

		if (null != getChildren())
		{
			ret = getChildren().toString();
		}
		else
		{
			ret = "";
		}

		return (ret);
	}

	/**
	 * Is this a mail address
	 *
	 * @return boolean true/false
	 */
	public boolean isMailLink()
	{
		getLink(); // force an evaluation of the booleans
		return (mailLink);
	}

	/**
	 * Tests if the link is javascript
	 *
	 * @return flag indicating if the link is a javascript code
	 */
	public boolean isJavascriptLink()
	{
		getLink(); // force an evaluation of the booleans
		return (javascriptLink);
	}

	/**
	 * Tests if the link is an FTP link.
	 *
	 * @return flag indicating if this link is an FTP link
	 */
	public boolean isFTPLink()
	{
		return getLink().indexOf("ftp://") == 0;
	}

	/**
	 * Tests if the link is an IRC link.
	 *
	 * @return flag indicating if this link is an IRC link
	 */
	public boolean isIRCLink()
	{
		return getLink().indexOf("irc://") == 0;
	}

	/**
	 * Tests if the link is an HTTP link.
	 *
	 * @return flag indicating if this link is an HTTP link
	 */
	public boolean isHTTPLink()
	{
		return (!isFTPLink() && !isHTTPSLink() && !isJavascriptLink() && !isMailLink() && !isIRCLink());
	}

	/**
	 * Tests if the link is an HTTPS link.
	 *
	 * @return flag indicating if this link is an HTTPS link
	 */
	public boolean isHTTPSLink()
	{
		return getLink().indexOf("https://") == 0;
	}

	/**
	 * Tests if the link is an HTTP link or one of its variations (HTTPS, etc.).
	 *
	 * @return flag indicating if this link is an HTTP link or one of its variations (HTTPS, etc.)
	 */
	public boolean isHTTPLikeLink()
	{
		return isHTTPLink() || isHTTPSLink();
	}


	/**
	 * Insert the method's description here.
	 * Creation date: (8/3/2001 1:49:31 AM)
	 *
	 * @param newMailLink boolean
	 */
	public void setMailLink(boolean newMailLink)
	{
		mailLink = newMailLink;
	}

	/**
	 * Set the link as a javascript link.
	 *
	 * @param newJavascriptLink flag indicating if the link is a javascript code
	 */
	public void setJavascriptLink(boolean newJavascriptLink)
	{
		javascriptLink = newJavascriptLink;
	}

	/**
	 * Print the contents of this Link Node
	 */
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("Link to : " + getLink() + "; titled : " + getLinkText() + "; begins at : " + getStartPosition() + "; ends at : " + getEndPosition() + ", AccessKey=");
		if (getAccessKey() == null)
		{
			sb.append("null\n");
		}
		else
		{
			sb.append(getAccessKey() + "\n");
		}
		if (null != getChildren())
		{
			sb.append("  " + "LinkData\n");
			sb.append("  " + "--------\n");

			Node node;
			int i = 0;
			for (SimpleNodeIterator e = children(); e.hasMoreNodes();)
			{
				node = (Node) e.nextNode();
				sb.append("   " + (i++) + " ");
				sb.append(node.toString() + "\n");
			}
		}

		return sb.toString();
	}

	public void setLink(String link)
	{
		mLink = link;
		setAttribute("HREF", link);
	}

	/**
	 * This method returns an enumeration of data that it contains
	 *
	 * @return Enumeration
	 * @deprecated Use children() instead.
	 */
	public SimpleNodeIterator linkData()
	{
		return children();
	}

	/**
	 * Link visiting code.
	 * Invokes <code>visitLinkTag()</code> on the visitor and then
	 * invokes the normal tag processing.
	 *
	 * @param visitor The <code>NodeVisitor</code> object to invoke
	 *                <code>visitLinkTag()</code> on.
	 */
	public void accept(NodeVisitor visitor)
	{
		visitor.visitLinkTag(this);
		super.accept(visitor);
	}

	/**
	 * Extract the link from the HREF attribute.
	 * The URL of the actual html page is also provided.
	 */
	public String extractLink()
	{
		String relativeLink = getAttribute("HREF");
		if (relativeLink != null)
		{
			relativeLink = ParserTool.removeChars(relativeLink, '\n');
			relativeLink = ParserTool.removeChars(relativeLink, '\r');
		}
		return (getPage().getLinkProcessor().extract(relativeLink, getPage().getUrl()));
	}
}
