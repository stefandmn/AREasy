package org.areasy.common.parser.html.beans;

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

import org.areasy.common.parser.html.engine.Parser;
import org.areasy.common.parser.html.engine.StringNode;
import org.areasy.common.parser.html.engine.tags.LinkTag;
import org.areasy.common.parser.html.engine.tags.Tag;
import org.areasy.common.parser.html.utilities.EncodingChangeException;
import org.areasy.common.parser.html.utilities.ParserException;
import org.areasy.common.parser.html.utilities.Translate;
import org.areasy.common.parser.html.engine.visitors.NodeVisitor;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.net.URLConnection;

/**
 * Extract strings from a URL.
 * <p>Text within &lt;SCRIPT&gt;&lt;/SCRIPT&gt; tags is removed.</p>
 * <p>The text within &lt;PRE&gt;&lt;/PRE&gt; tags is not altered.</p>
 * <p>The property <code>Strings</code>, which is the output property is null
 * until a URL is set. So a typical usage is:</p>
 * <pre>
 *     StringBean sb = new StringBean ();
 *     sb.setLinks (false);
 *     sb.setReplaceNonBreakingSpaces (true);
 *     sb.setCollapse (true);
 *     sb.setURL ("http://www.netbeans.org"); // the HTTP is performed here
 *     String s = sb.getStrings ();
 * </pre>
 * You can also use the StringBean as a NodeVisitor on your own parser,
 * in which case you have to refetch your page if you change one of the
 * properties because it resets the Strings property:</p>
 * <pre>
 *     StringBean sb = new StringBean ();
 *     Parser parser = new Parser ("http://cbc.ca");
 *     parser.visitAllNodesWith (sb);
 *     String s = sb.getStrings ();
 *     sb.setLinks (true);
 *     parser.reset ();
 *     parser.visitAllNodesWith (sb);
 *     String sl = sb.getStrings ();
 * </pre>
 * According to Nick Burch, who contributed the patch, this is handy if you
 * don't want StringBean to wander off and get the content itself, either
 * because you already have it, it's not on a website etc.
 *
 * @version $Id: StringBean.java,v 1.3 2008/05/25 23:20:16 swd\stefan.damian Exp $
 */
public class StringBean extends NodeVisitor implements Serializable
{
    /**
     * Property name in event where the URL contents changes.
     */
    public static final String PROP_STRINGS_PROPERTY = "Strings";

    /**
     * Property name in event where the 'embed links' state changes.
     */
    public static final String PROP_LINKS_PROPERTY = "Links";

    /**
     * Property name in event where the URL changes.
     */
    public static final String PROP_URL_PROPERTY = "URL";

    /**
     * Property name in event where the 'replace non-breaking spaces' state changes.
     */
    public static final String PROP_REPLACE_SPACE_PROPERTY = "ReplaceSpace";

    /**
     * Property name in event where the 'collapse whitespace' state changes.
     */
    public static final String PROP_COLLAPSE_PROPERTY = "Collapse";

    /**
     * Property name in event where the connection changes.
     */
    public static final String PROP_CONNECTION_PROPERTY = "Connection";

    /**
     * A newline.
     */
    private static final String newline = System.getProperty("line.separator");

    /**
     * The length of the newline.
     */
    private static final int newline_size = newline.length();

    /**
     * Bound property support.
     */
    protected PropertyChangeSupport mPropertySupport;

    /**
     * The parser used to extract strings.
     */
    protected Parser mParser;

    /**
     * The strings extracted from the URL.
     */
    protected String mStrings;

    /**
     * If <code>true</code> the link URLs are embedded in the text output.
     */
    protected boolean mLinks;

    /**
     * If <code>true</code> regular space characters are substituted for
     * non-breaking spaces in the text output.
     */
    protected boolean mReplaceSpace;

    /**
     * If <code>true</code> sequences of whitespace characters are replaced with a
     * single space character.
     */
    protected boolean mCollapse;

    /**
     * The buffer text is stored in while traversing the HTML.
     */
    protected StringBuffer mBuffer;

    /**
     * Set <code>true</code> when traversing a SCRIPT tag.
     */
    protected boolean mIsScript;

    /**
     * Set <code>true</code> when traversing a PRE tag.
     */
    protected boolean mIsPre;

    /**
     * Set <code>true</code> when traversing a STYLE tag.
     */
    protected boolean mIsStyle;

    /**
     * Create a StringBean object.
     * Default property values are set to 'do the right thing':
     * <p><code>Links</code> is set <code>false</code> so text appears like a
     * browser would display it, albeit without the colour or underline clues
     * normally associated with a link.</p>
     * <p><code>ReplaceNonBreakingSpaces</code> is set <code>true</code>, so
     * that printing the text works, but the extra information regarding these
     * formatting marks is available if you set it false.</p>
     * <p><code>Collapse</code> is set <code>true</code>, so text appears
     * compact like a browser would display it.</p>
     */
    public StringBean()
    {
        super(true, true);
        mPropertySupport = new PropertyChangeSupport(this);
        mParser = new Parser();
        mStrings = null;
        mLinks = false;
        mReplaceSpace = true;
        mCollapse = true;
        mBuffer = new StringBuffer(4096);
        mIsScript = false;
        mIsPre = false;
        mIsStyle = false;
    }

    /**
     * Appends a newline to the buffer if there isn't one there already.
     * Except if the buffer is empty.
     */
    protected void carriage_return()
    {
        int length;

        length = mBuffer.length();
        if((0 != length) // why bother appending newlines to the beginning of a buffer
           && ((newline_size <= length) // not enough chars to hold a newline
               && (!mBuffer.substring(length - newline_size, length).equals(newline))))
        {
            mBuffer.append(newline);
        }
    }

    /**
     * Add the given text collapsing whitespace.
     * Use a little finite state machine:
     * <pre>
     * state 0: whitepace was last emitted character
     * state 1: in whitespace
     * state 2: in word
     * A whitespace character moves us to state 1 and any other character
     * moves us to state 2, except that state 0 stays in state 0 until
     * a non-whitespace and going from whitespace to word we emit a space
     * before the character:
     *    input:     whitespace   other-character
     * state\next
     *    0               0             2
     *    1               1        space then 2
     *    2               1             2
     * </pre>
     *
     * @param buffer The buffer to append to.
     * @param string The string to append.
     */
    protected void collapse(StringBuffer buffer, String string)
    {
        int chars;
        int length;
        int state;
        char character;

        chars = string.length();

        if(0 != chars)
        {
            length = buffer.length();

            state = ((0 == length)
                     || (buffer.charAt(length - 1) == ' ')
                     || ((newline_size <= length) && buffer.substring(length - newline_size, length).equals(newline))) ? 0 : 1;

            for(int i = 0; i < chars; i++)
            {
                character = string.charAt(i);
                switch(character)
                {
                    // see HTML specification section 9.1 White space
                    // http://www.w3.org/TR/html4/struct/text.html#h-9.1
                    case '\u0020':
                    case '\u0009':
                    case '\u000C':
                    case '\u200B':
                    case '\r':
                    case '\n':
                        if(0 != state) state = 1;
                        break;
                    default:
                        if(1 == state) buffer.append(' ');
                        state = 2;
                        buffer.append(character);
                }
            }
        }
    }

    /**
     * Extract the text from a page.
     *
     * @return The textual contents of the page.
     */
    protected String extractStrings() throws ParserException
    {
        String ret;

        mParser.visitAllNodesWith(this);
        ret = mBuffer.toString();
        mBuffer = new StringBuffer(4096);

        return (ret);
    }

    /**
     * Assign the <code>Strings</code> property, firing the property change.
     *
     * @param strings The new value of the <code>Strings</code> property.
     */
    protected void updateStrings(String strings)
    {
        String oldValue;

        if((null == mStrings) || !mStrings.equals(strings))
        {
            oldValue = mStrings;
            mStrings = strings;
			
			mPropertySupport.firePropertyChange(PROP_STRINGS_PROPERTY, oldValue, strings);
        }
    }

    /**
     * Fetch the URL contents.
     * Only do work if there is a valid parser with it's URL set.
     */
    protected void setStrings()
    {
        if(null != getURL())
        {
            try
            {
                try
                {
                    mParser.visitAllNodesWith(this);
                    updateStrings(mBuffer.toString());
                }
                finally
                {
                    mBuffer = new StringBuffer(4096);
                }
            }
            catch(EncodingChangeException ece)
            {
                mIsPre = false;
                mIsScript = false;
                mIsStyle = false;
                try
                {   // try again with the encoding now in force
                    mParser.reset();
                    mBuffer = new StringBuffer(4096);
                    mParser.visitAllNodesWith(this);
                    updateStrings(mBuffer.toString());
                }
                catch(ParserException pe)
                {
                    updateStrings(pe.toString());
                }
                finally
                {
                    mBuffer = null;
                }
            }
            catch(ParserException pe)
            {
                updateStrings(pe.toString());
            }
        }
        else
        {
            // reset in case this StringBean is used as a visitor
            // on another parser, not it's own
            mStrings = null;
            mBuffer = new StringBuffer(4096);
        }
    }

    /**
     * Refetch the URL contents.
     * Only need to worry if there is already a valid parser and it's
     * been spent fetching the string contents.
     */
    private void resetStrings()
    {
        if(null != mStrings)
        {
            try
            {
                mParser.setURL(getURL());
                setStrings();
            }
            catch(ParserException pe)
            {
                updateStrings(pe.toString());
            }
        }
    }


    /**
     * Add a PropertyChangeListener to the listener list.
     * The listener is registered for all properties.
     *
     * @param listener The PropertyChangeListener to be added.
     */
    public void addPropertyChangeListener(PropertyChangeListener listener)
    {
        mPropertySupport.addPropertyChangeListener(listener);
    }

    /**
     * Remove a PropertyChangeListener from the listener list.
     * This removes a PropertyChangeListener that was registered for all properties.
     *
     * @param listener The PropertyChangeListener to be removed.
     */
    public void removePropertyChangeListener(PropertyChangeListener listener)
    {
        mPropertySupport.removePropertyChangeListener(listener);
    }

    /**
     * Return the textual contents of the URL.
     * This is the primary output of the bean.
     *
     * @return The user visible (what would be seen in a browser) text from the URL.
     */
    public String getStrings()
    {
        if(null == mStrings)
        {
            if(0 == mBuffer.length()) setStrings();
            	else updateStrings(mBuffer.toString());
        }

        return (mStrings);
    }

    /**
     * Get the current 'include links' state.
     *
     * @return <code>true</code> if link text is included in the text extracted
     *         from the URL, <code>false</code> otherwise.
     */
    public boolean getLinks()
    {
        return (mLinks);
    }

    /**
     * Set the 'include links' state.
     * If the setting is changed after the URL has been set, the text from the
     * URL will be reacquired, which is possibly expensive.
     *
     * @param links Use <code>true</code> if link text is to be included in the
     *              text extracted from the URL, <code>false</code> otherwise.
     */
    public void setLinks(boolean links)
    {
        boolean oldValue = mLinks;

        if(oldValue != links)
        {
            mLinks = links;
            mPropertySupport.firePropertyChange(PROP_LINKS_PROPERTY, oldValue, links);
            resetStrings();
        }
    }

    /**
     * Get the current URL.
     *
     * @return The URL from which text has been extracted, or <code>null</code>
     *         if this property has not been set yet.
     */
    public String getURL()
    {
        return ((null != mParser) ? mParser.getURL() : null);
    }

    /**
     * Set the URL to extract strings from.
     * The text from the URL will be fetched, which may be expensive, so this
     * property should be set last.
     *
     * @param url The URL that text should be fetched from.
     */
    public void setURL(String url)
    {
        String old;
        URLConnection conn;

        old = getURL();
        conn = getConnection();

        if(((null == old) && (null != url)) || ((null != old) && !old.equals(url)))
        {
            try
            {
                if(null == mParser) mParser = new Parser(url);
                	else mParser.setURL(url);

                mPropertySupport.firePropertyChange(PROP_URL_PROPERTY, old, getURL());
                mPropertySupport.firePropertyChange(PROP_CONNECTION_PROPERTY, conn, mParser.getConnection());

                setStrings();
            }
            catch(ParserException pe)
            {
                updateStrings(pe.toString());
            }
        }
    }

    /**
     * Get the current 'replace non breaking spaces' state.
     *
     * @return <code>true</code> if non-breaking spaces (character '&#92;u00a0',
     *         numeric character reference &amp;#160; or character entity reference &amp;nbsp;)
     *         are to be replaced with normal spaces (character '&#92;u0020').
     */
    public boolean getReplaceNonBreakingSpaces()
    {
        return (mReplaceSpace);
    }

    /**
     * Set the 'replace non breaking spaces' state.
     * If the setting is changed after the URL has been set, the text from the
     * URL will be reacquired, which is possibly expensive.
     *
     * @param replace_space <code>true</code> if non-breaking spaces (character '&#92;u00a0',
     *                      numeric character reference &amp;#160; or character entity reference &amp;nbsp;)
     *                      are to be replaced with normal spaces (character '&#92;u0020').
     */
    public void setReplaceNonBreakingSpaces(boolean replace_space)
    {
        boolean oldValue = mReplaceSpace;

        if(oldValue != replace_space)
        {
            mReplaceSpace = replace_space;
            mPropertySupport.firePropertyChange(PROP_REPLACE_SPACE_PROPERTY, oldValue, replace_space);

            resetStrings();
        }
    }

    /**
     * Get the current 'collapse whitespace' state.
     * If set to <code>true</code> this emulates the operation of browsers
     * in interpretting text where <quote>user agents should collapse input white
     * space sequences when producing output inter-word space</quote>.
     * See HTML specification section 9.1 White space
     * <a href="http://www.w3.org/TR/html4/struct/text.html#h-9.1">
     * http://www.w3.org/TR/html4/struct/text.html#h-9.1</a>.
     *
     * @return <code>true</code> if sequences of whitespace (space '&#92;u0020',
     *         tab '&#92;u0009', form feed '&#92;u000C', zero-width space '&#92;u200B',
     *         carriage-return '\r' and newline '\n') are to be replaced with a single
     *         space.
     */
    public boolean getCollapse()
    {
        return (mCollapse);
    }

    /**
     * Set the current 'collapse whitespace' state.
     * If the setting is changed after the URL has been set, the text from the
     * URL will be reacquired, which is possibly expensive.
     *
     * @param collapse_whitespace If <code>true</code>, sequences of whitespace
     *                            will be reduced to a single space.
     */
    public void setCollapse(boolean collapse_whitespace)
    {
        boolean oldValue = mCollapse;

        if(oldValue != collapse_whitespace)
        {
            mCollapse = collapse_whitespace;
            mPropertySupport.firePropertyChange(PROP_COLLAPSE_PROPERTY, oldValue, collapse_whitespace);
            resetStrings();
        }
    }

    /**
     * Get the current connection.
     *
     * @return The connection that the parser has or <code>null</code> if it
     *         hasn't been set or the parser hasn't been constructed yet.
     */
    public URLConnection getConnection()
    {
        return ((null != mParser) ? mParser.getConnection() : null);
    }

    /**
     * Set the parser's connection.
     * The text from the URL will be fetched, which may be expensive, so this
     * property should be set last.
     *
     * @param connection New value of property Connection.
     */
    public void setConnection(URLConnection connection)
    {
        String url;
        URLConnection conn;

        url = getURL();
        conn = getConnection();
        if(((null == conn) && (null != connection)) || ((null != conn) && !conn.equals(connection)))
        {
            try
            {
                if(null == mParser) mParser = new Parser(connection);
                	else mParser.setConnection(connection);

                mPropertySupport.firePropertyChange(PROP_URL_PROPERTY, url, getURL());
                mPropertySupport.firePropertyChange(PROP_CONNECTION_PROPERTY, conn, mParser.getConnection());
                setStrings();
            }
            catch(ParserException pe)
            {
                updateStrings(pe.toString());
            }
        }
    }

    /**
     * Appends the link as text between angle brackets to the output.
     *
     * @param link The link to process.
     */
    public void visitLinkTag(LinkTag link)
    {
        if(getLinks())
        {
            mBuffer.append("<");
            mBuffer.append(link.getLink());
            mBuffer.append(">");
        }
    }

    /**
     * Appends the text to the output.
     *
     * @param string The text node.
     */
    public void visitStringNode(StringNode string)
    {
        if(!mIsScript && !mIsStyle)
        {
            String text = string.getText();
            if(!mIsPre)
            {
                text = Translate.decode(text);
                if(getReplaceNonBreakingSpaces()) text = text.replace('\u00a0', ' ');
                if(getCollapse()) collapse(mBuffer, text);
                else  mBuffer.append(text);
            }
            else mBuffer.append(text);
        }
    }

    /**
     * Appends a newline to the output if the tag breaks flow, and
     * possibly sets the state of the PRE and SCRIPT flags.
     */
    public void visitTag(Tag tag)
    {
        String name;

        name = tag.getTagName();
        if(name.equalsIgnoreCase("PRE")) mIsPre = true;
        else
        {
            if(name.equalsIgnoreCase("SCRIPT")) mIsScript = true;
            	else if(name.equalsIgnoreCase("STYLE")) mIsStyle = true;
        }

        if(tag.breaksFlow()) carriage_return();
    }

    /**
     * Resets the state of the PRE and SCRIPT flags.
     *
     * @param tag The end tag to process.
     */
    public void visitEndTag(Tag tag)
    {
        String name;

        name = tag.getTagName();

        if(name.equalsIgnoreCase("PRE")) mIsPre = false;
        else
        {
            if(name.equalsIgnoreCase("SCRIPT")) mIsScript = false;
            	else if(name.equalsIgnoreCase("STYLE")) mIsStyle = false;
        }
    }

    /**
     * Extract the text from a page.
     * @param resource Either a URL or a file name.
     * @param links if <code>true</code> include hyperlinks in output.
     * @return The textual contents of the page.
     */
	public static String getString(String resource, boolean links)
	{
        StringBean sb;

        sb = new StringBean();
        sb.setLinks(links);
        sb.setURL(resource);

        return (sb.getStrings());
	}
}
