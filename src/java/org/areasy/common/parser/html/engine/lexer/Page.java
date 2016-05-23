package org.areasy.common.parser.html.engine.lexer;

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

import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;
import org.areasy.common.parser.html.utilities.EncodingChangeException;
import org.areasy.common.parser.html.utilities.LinkProcessor;
import org.areasy.common.parser.html.utilities.ParserException;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;

/**
 * Represents the contents of an HTML page.
 * Contains the source of characters and an index of positions of line
 * separators (actually the first character position on the next line).
 */
public class Page implements Serializable
{

	/**
	 * The logger
	 */
	private static Logger logger = LoggerFactory.getLog(Page.class);

	/**
	 * The default charset.
	 * This should be <code>ISO-8859-1</code>,
	 * see RFC 2616 (http://www.ietf.org/rfc/rfc2616.txt?number=2616) section 3.7.1
	 * Another alias is "8859_1".
	 */
	public static String DEFAULT_CHARSET = "ISO-8859-1";

	/**
	 * The default content type.
	 * In the absence of alternate information, assume html content.
	 */
	public static final String DEFAULT_CONTENT_TYPE = "text/html";

	/**
	 * The URL this page is coming from.
	 * Cached value of <code>getConnection().toExternalForm()</code> or
	 * <code>setUrl()</code>.
	 */
	protected String mUrl;

	/**
	 * The source of characters.
	 */
	protected Source mSource;

	/**
	 * Character positions of the first character in each line.
	 */
	protected PageIndex mIndex;

	/**
	 * The connection this page is coming from or <code>null</code>.
	 */
	protected transient URLConnection mConnection;

	/**
	 * The processor of relative links on this page.
	 * Holds any overridden base HREF.
	 */
	protected LinkProcessor mProcessor;

	/**
	 * Messages for page not there (404).
	 */
	static private final String[] mFourOhFour =
			{
				"The web site you seek cannot be located, but countless more exist",
				"You step in the stream, but the water has moved on. This page is not here.",
				"Yesterday the page existed. Today it does not. The internet is like that.",
				"That page was so big. It might have been very useful. But now it is gone.",
				"Three things are certain: death, taxes and broken links. Guess which has occured.",
				"Chaos reigns within. Reflect, repent and enter the correct URL. Order shall return.",
				"Stay the patient course. Of little worth is your ire. The page is not found.",
				"A non-existant URL reduces your expensive computer to a simple stone.",
				"Many people have visited that page. Today, you are not one of the lucky ones.",
				"Cutting the wind with a knife. Bookmarking a URL. Both are ephemeral.",
			};

	/**
	 * Set default charset.
	 *
	 * @param charset
	 */
	public static void setCharSet(String charset)
	{
		DEFAULT_CHARSET = charset;
	}

	/**
	 * Get Default character set.
	 *
	 * @return
	 */
	public static String getCharset()
	{
		return DEFAULT_CHARSET;
	}

	/**
	 * Construct an empty page.
	 */
	public Page()
	{
		this("");
	}

	/**
	 * Construct a page reading from a URL connection.
	 *
	 * @param connection A fully conditioned connection. The connect()
	 *                   method will be called so it need not be connected yet.
	 * @throws ParserException An exception object wrapping a number of
	 *                         possible error conditions, some of which are outlined below.
	 *                         <li>IOException If an i/o exception occurs creating the
	 *                         source.</li>
	 *                         <li>UnsupportedEncodingException if the character set specified in the
	 *                         HTTP header is not supported.</li>
	 */
	public Page(URLConnection connection) throws ParserException
	{
		if (null == connection) throw new IllegalArgumentException("Connection cannot be null");

		setConnection(connection);
		mProcessor = null;
	}

	/**
	 * Construct a page from a stream encoded with the given charset.
	 *
	 * @param stream  The source of bytes.
	 * @param charset The encoding used.
	 *                If null, defaults to the <code>DEFAULT_CHARSET</code>.
	 * @throws UnsupportedEncodingException If the given charset is not supported.
	 */
	public Page(InputStream stream, String charset) throws UnsupportedEncodingException
	{
		if (null == stream) throw new IllegalArgumentException("Stream cannot be null");
		if (null == charset) charset = Page.getCharset();
		mSource = new Source(stream, charset);
		mIndex = new PageIndex(this);
		mConnection = null;
		mUrl = null;
		mProcessor = null;
	}

	public Page(String text)
	{
		InputStream stream;

		if (null == text) throw new IllegalArgumentException("text cannot be null");
		try
		{
			stream = new ByteArrayInputStream(text.getBytes(Page.getCharset()));
			mSource = new Source(stream, Page.getCharset(), text.length() + 1);
			mIndex = new PageIndex(this);
		}
		catch (UnsupportedEncodingException uee)
		{
			// this is unlikely, so we cover it up with a runtime exception
			throw new IllegalStateException(uee.getMessage());
		}

		mConnection = null;
		mUrl = null;
		mProcessor = null;
	}

	/**
	 * Serialize the page.
	 * There are two modes to serializing a page based on the connected state.
	 * If connected, the URL and the current offset is saved, while if
	 * disconnected, the underling source is saved.
	 *
	 * @param out The object stream to store this object in.
	 */
	private void writeObject(ObjectOutputStream out) throws IOException
	{
		String href;
		Source source;
		PageIndex index;

		// two cases, reading from a URL and not
		if (null != getConnection())
		{
			out.writeBoolean(true);
			out.writeInt(mSource.offset()); // need to preread this much
			href = getUrl();
			out.writeObject(href);
			setUrl(getConnection().getURL().toExternalForm());
			source = getSource();
			mSource = null; // don't serialize the source if we can avoid it
			index = mIndex;
			mIndex = null; // will get recreated; valid for the new page anyway?
			out.defaultWriteObject();
			mSource = source;
			mIndex = index;
		}
		else
		{
			out.writeBoolean(false);
			href = getUrl();
			out.writeObject(href);
			setUrl(null); // don't try and read a bogus URL
			out.defaultWriteObject();
			setUrl(href);
		}
	}

	/**
	 * Deserialize the page.
	 * For details see <code>writeObject()</code>.
	 *
	 * @param in The object stream to decode.
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		boolean fromurl;
		int offset;
		String href;
		URL url;
		Cursor cursor;

		fromurl = in.readBoolean();
		if (fromurl)
		{
			offset = in.readInt();
			href = (String) in.readObject();
			in.defaultReadObject();

			// open the URL
			if (null != getUrl())
			{
				url = new URL(getUrl());
				try
				{
					setConnection(url.openConnection());
				}
				catch (ParserException pe)
				{
					throw new IOException(pe.getMessage());
				}
			}

			cursor = new Cursor(this, 0);
			for (int i = 0; i < offset; i++)
			{
				try
				{
					getCharacter(cursor);
				}
				catch (ParserException pe)
				{
					throw new IOException(pe.getMessage());
				}
			}

			setUrl(href);
		}
		else
		{
			href = (String) in.readObject();
			in.defaultReadObject();
			setUrl(href);
		}
	}

	/**
	 * Reset the page by resetting the source of characters.
	 */
	public void reset()
	{
		getSource().reset();
		mIndex = new PageIndex(this);
	}

	/**
	 * Get the connection, if any.
	 *
	 * @return The connection object for this page, or null if this page
	 *         is built from a stream or a string.
	 */
	public URLConnection getConnection()
	{
		return (mConnection);
	}

	/**
	 * Set the URLConnection to be used by this page.
	 * Starts reading from the given connection.
	 * This also resets the current url.
	 *
	 * @param connection The connection to use.
	 *                   It will be connected by this method.
	 * @throws ParserException If the <code>connect()</code> method fails,
	 *                         or an I/O error occurs opening the input stream or the character set
	 *                         designated in the HTTP header is unsupported.
	 */
	public void setConnection(URLConnection connection) throws ParserException
	{
		Stream stream;
		String type;
		String charset;

		mConnection = connection;
		try
		{
			getConnection().connect();
		}
		catch (UnknownHostException uhe)
		{
			int message = (int) (Math.random() * mFourOhFour.length);
			throw new ParserException(mFourOhFour[message], uhe);
		}
		catch (IOException ioe)
		{
			throw new ParserException(ioe.getMessage(), ioe);
		}

		type = getContentType();

		charset = getCharset(type);
		try
		{
			stream = new Stream(getConnection().getInputStream());
			try
			{
				mSource = new Source(stream, charset);
			}
			catch (UnsupportedEncodingException uee)
			{
				charset = Page.getCharset();
				mSource = new Source(stream, charset);
			}
		}
		catch (IOException ioe)
		{
			throw new ParserException(ioe.getMessage(), ioe);
		}

		mUrl = connection.getURL().toExternalForm();
		mIndex = new PageIndex(this);
	}

	/**
	 * Get the URL for this page.
	 * This is only available if the page has a connection
	 * (<code>getConnection()</code> returns non-null), or the document base has
	 * been set via a call to <code>setUrl()</code>.
	 *
	 * @return The url for the connection, or <code>null</code> if there is
	 *         no conenction or the document base has not been set.
	 */
	public String getUrl()
	{
		return (mUrl);
	}

	/**
	 * Set the URL for this page.
	 * This doesn't affect the contents of the page, just the interpretation
	 * of relative links from this point forward.
	 *
	 * @param url The new URL.
	 */
	public void setUrl(String url)
	{
		mUrl = url;
	}

	/**
	 * Get the source this page is reading from.
	 */
	public Source getSource()
	{
		return (mSource);
	}

	/**
	 * Try and extract the content type from the HTTP header.
	 *
	 * @return The content type.
	 */
	public String getContentType()
	{
		URLConnection connection;
		String ret;

		ret = DEFAULT_CONTENT_TYPE;
		connection = getConnection();

		if (null != connection) ret = connection.getContentType();

		return (ret);
	}

	/**
	 * Read the character at the cursor position.
	 * The cursor position can be behind or equal to the current source position.
	 * Returns end of lines (EOL) as \n, by converting \r and \r\n to \n,
	 * and updates the end-of-line index accordingly
	 * Advances the cursor position by one (or two in the \r\n case).
	 *
	 * @param cursor The position to read at.
	 * @return The character at that position, and modifies the cursor to
	 *         prepare for the next read. If the source is exhausted a zero is returned.
	 * @throws ParserException If an IOException on the underlying source
	 *                         occurs, or an attemp is made to read characters in the future (the
	 *                         cursor position is ahead of the underlying stream)
	 */
	public char getCharacter(Cursor cursor) throws ParserException
	{
		int i;
		char ret;

		i = cursor.getPosition();

		// hmmm, we could skip ahead, but then what about the EOL index
		if (mSource.mOffset < i)
		{
			throw new ParserException("Attempt to read future characters from source");
		}

		else
		{
			if (mSource.mOffset == i)
			{
				try
				{
					i = mSource.read();
					if (0 > i)
					{
						ret = 0;
					}
					else
					{
						ret = (char) i;
						cursor.advance();
					}
				}
				catch (IOException ioe)
				{
					throw new ParserException("Problem reading a character at position " + cursor.getPosition(), ioe);
				}
			}
			else
			{
				// historic read
				ret = mSource.mBuffer[i];
				cursor.advance();
			}
		}

		// handle \r
		if ('\r' == ret)
		{   // switch to single character EOL
			ret = '\n';

			// check for a \n in the next position
			if (mSource.mOffset == cursor.getPosition())
			{
				try
				{
					i = mSource.read();
					if (-1 == i)
					{
						// do nothing
					}
					else
					{
						if ('\n' == (char) i)
						{
							cursor.advance();
						}
						else
						{
							try
							{
								mSource.unread();
							}
							catch (IOException ioe)
							{
								throw new ParserException("Can't unread a character at position " + cursor.getPosition(), ioe);
							}
						}
					}
				}
				catch (IOException ioe)
				{
					throw new ParserException("Problem reading a character at position " + cursor.getPosition(), ioe);
				}
			}
			else
			{
				if ('\n' == mSource.mBuffer[cursor.getPosition()]) cursor.advance();
			}
		}
		// update the EOL index in any case
		if ('\n' == ret) mIndex.add(cursor);

		return (ret);
	}

	/**
	 * Get a CharacterSet name corresponding to a charset parameter.
	 *
	 * @param content A text line of the form:
	 *                <pre>
	 *                text/html; charset=Shift_JIS
	 *                </pre>
	 *                which is applicable both to the HTTP header field Content-Type and
	 *                the meta tag http-equiv="Content-Type".
	 *                Note this method also handles non-compliant quoted charset directives such as:
	 *                <pre>
	 *                text/html; charset="UTF-8"
	 *                </pre>
	 *                and
	 *                <pre>
	 *                text/html; charset='UTF-8'
	 *                </pre>
	 *
	 * @return The character set name to use when reading the input stream.
	 *         For JDKs that have the Charset class this is qualified by passing
	 *         the name to findCharset() to render it into canonical form.
	 *         If the charset parameter is not found in the given string, the default
	 *         character set is returned.
	 * @see #findCharset
	 * @see #DEFAULT_CHARSET
	 */
	public String getCharset(String content)
	{
		final String CHARSET_STRING = "charset";
		int index;
		String ret;

		ret = Page.getCharset();
		if (null != content)
		{
			index = content.indexOf(CHARSET_STRING);

			if (index != -1)
			{
				content = content.substring(index + CHARSET_STRING.length()).trim();
				if (content.startsWith("="))
				{
					content = content.substring(1).trim();
					index = content.indexOf(";");
					if (index != -1) content = content.substring(0, index);

					//remove any double quotes from around charset string
					if (content.startsWith("\"") && content.endsWith("\"") && (1 < content.length())) content = content.substring(1, content.length() - 1);

					//remove any single quote from around charset string
					if (content.startsWith("'") && content.endsWith("'") && (1 < content.length())) content = content.substring(1, content.length() - 1);

					ret = findCharset(content, ret);
				}
			}
		}

		return (ret);
	}

	/**
	 * Lookup a character set name.
	 * <em>Vacuous for JVM's without <code>java.nio.charset</code>.</em>
	 * This uses reflection so the code will still run under prior JDK's but
	 * in that case the default is always returned.
	 *
	 * @param name     The name to look up. One of the aliases for a character set.
	 * @param _default The name to return if the lookup fails.
	 */
	public String findCharset(String name, String _default)
	{
		String ret;

		try
		{
			Class cls;
			Method method;
			Object object;

			cls = Class.forName("java.nio.charset.Charset");
			method = cls.getMethod("forName", new Class[]{String.class});
			object = method.invoke(null, new Object[]{name});
			method = cls.getMethod("name", new Class[]{});
			object = method.invoke(object, new Object[]{});
			ret = (String) object;
		}
		catch (ClassNotFoundException cnfe)
		{
			// for reflection exceptions, assume the name is correct
			ret = name;
		}
		catch (NoSuchMethodException nsme)
		{
			// for reflection exceptions, assume the name is correct
			ret = name;
		}
		catch (IllegalAccessException ia)
		{
			// for reflection exceptions, assume the name is correct
			ret = name;
		}
		catch (InvocationTargetException ita)
		{
			// java.nio.charset.IllegalCharsetNameException
			// and java.nio.charset.UnsupportedCharsetException
			// return the default
			ret = _default;
			logger.error("Unable to determine cannonical charset name for "
					+ name
					+ " - using "
					+ _default);
		}

		return (ret);
	}

	/**
	 * Get the current encoding being used.
	 *
	 * @return The encoding used to convert characters.
	 */
	public String getEncoding()
	{
		return (mSource.getEncoding());
	}

	/**
	 * Begins reading from the source with the given character set.
	 * If the current encoding is the same as the requested encoding,
	 * this method is a no-op. Otherwise any subsequent characters read from
	 * this page will have been decoded using the given character set.<p>
	 * Some magic happens here to obtain this result if characters have already
	 * been consumed from this page.
	 * Since a Reader cannot be dynamically altered to use a different character
	 * set, the underlying stream is reset, a new Source is constructed
	 * and a comparison made of the characters read so far with the newly
	 * read characters up to the current position.
	 * If a difference is encountered, or some other problem occurs,
	 * an exception is thrown.
	 *
	 * @param character_set The character set to use to convert bytes into
	 *                      characters.
	 * @throws ParserException If a character mismatch occurs between
	 *                         characters already provided and those that would have been returned
	 *                         had the new character set been in effect from the beginning. An
	 *                         exception is also thrown if the underlying stream won't put up with
	 *                         these shenanigans.
	 */
	public void setEncoding(String character_set) throws ParserException
	{
		String encoding;
		InputStream stream;
		char[] buffer;
		int offset;
		char[] new_chars;

		encoding = getEncoding();
		if (!encoding.equalsIgnoreCase(character_set))
		{
			stream = getSource().getStream();
			try
			{
				buffer = mSource.mBuffer;
				offset = mSource.mOffset;
				stream.reset();
				mSource = new Source(stream, character_set);
				if (0 != offset)
				{
					new_chars = new char[offset];
					if (offset != mSource.read(new_chars))
					{
						throw new ParserException("reset stream failed");
					}
					for (int i = 0; i < offset; i++)
					{
						if (new_chars[i] != buffer[i])
						{
							throw new EncodingChangeException("character mismatch (new: "
									+ new_chars[i]
									+ " != old: "
									+ buffer[i]
									+ ") for encoding change from "
									+ encoding
									+ " to "
									+ character_set
									+ " at character offset "
									+ offset);
						}
					}
				}
			}
			catch (IOException ioe)
			{
				throw new ParserException(ioe.getMessage(), ioe);
			}
		}
	}

	/**
	 * Get the link processor associated with this page.
	 *
	 * @return The link processor that has the base HREF.
	 */
	public LinkProcessor getLinkProcessor()
	{
		if (null == mProcessor) mProcessor = new LinkProcessor();

		return (mProcessor);
	}

	/**
	 * Set the link processor associated with this page.
	 *
	 * @param processor The new link processor for this page.
	 */
	public void setLinkProcessor(LinkProcessor processor)
	{
		mProcessor = processor;
	}

	/**
	 * Get the line number for a cursor.
	 *
	 * @param cursor The character offset into the page.
	 * @return The line number the character is in.
	 */
	public int row(Cursor cursor)
	{
		return (mIndex.row(cursor));
	}

	/**
	 * Get the line number for a cursor.
	 *
	 * @param position The character offset into the page.
	 * @return The line number the character is in.
	 */
	public int row(int position)
	{
		return (mIndex.row(position));
	}

	/**
	 * Get the column number for a cursor.
	 *
	 * @param cursor The character offset into the page.
	 * @return The character offset into the line this cursor is on.
	 */
	public int column(Cursor cursor)
	{
		return (mIndex.column(cursor));
	}

	/**
	 * Get the column number for a cursor.
	 *
	 * @param position The character offset into the page.
	 * @return The character offset into the line this cursor is on.
	 */
	public int column(int position)
	{
		return (mIndex.column(position));
	}

	/**
	 * Get the text identified by the given limits.
	 *
	 * @param start The starting position, zero based.
	 * @param end   The ending position
	 *              (exclusive, i.e. the character at the ending position is not included),
	 *              zero based.
	 * @return The text from <code>start</code> to <code>end</code>.
	 * @throws IllegalArgumentException If an attempt is made to get
	 *                                  characters ahead of the current source offset (character position).
	 * @see #getText(StringBuffer, int, int)
	 */
	public String getText(int start, int end)
	{
		return (new String(mSource.mBuffer, start, end - start));
	}

	/**
	 * Put the text identified by the given limits into the given buffer.
	 *
	 * @param buffer The accumulator for the characters.
	 * @param start  The starting position, zero based.
	 * @param end    The ending position
	 *               (exclusive, i.e. the character at the ending position is not included),
	 *               zero based.
	 * @throws IllegalArgumentException If an attempt is made to get
	 *                                  characters ahead of the current source offset (character position).
	 */
	public void getText(StringBuffer buffer, int start, int end)
	{
		int length;

		if ((mSource.mOffset < start) || (mSource.mOffset < end)) throw new IllegalArgumentException("Attempt to extract future characters from source");
		if (end < start)
		{
			length = end;
			end = start;
			start = length;
		}
		length = end - start;
		buffer.append(mSource.mBuffer, start, length);
	}

	/**
	 * Get all text read so far from the source.
	 *
	 * @return The text from the source.
	 * @see #getText(StringBuffer)
	 */
	public String getText()
	{
		return (new String(mSource.mBuffer, 0, mSource.mOffset));
	}

	/**
	 * Put all text read so far from the source into the given buffer.
	 *
	 * @param buffer The accumulator for the characters.
	 * @see #getText(StringBuffer,int,int)
	 */
	public void getText(StringBuffer buffer)
	{
		getText(buffer, 0, mSource.mOffset);
	}

	/**
	 * Get the text line the position of the cursor lies on.
	 *
	 * @param cursor The position to calculate for.
	 * @return The contents of the URL or file corresponding to the line number
	 *         containg the cursor position.
	 */
	public String getLine(Cursor cursor)
	{
		int line;
		int size;
		int start;
		int end;

		line = row(cursor);
		size = mIndex.size();

		if (line < size)
		{
			start = mIndex.elementAt(line);
			line++;
			if (line <= size)
			{
				end = mIndex.elementAt(line);
			}
			else
			{
				end = mSource.mOffset;
			}
		}
		else // current line
		{
			start = mIndex.elementAt(line - 1);
			end = mSource.mOffset;
		}

		return (getText(start, end));
	}

	/**
	 * Get the text line the position of the cursor lies on.
	 *
	 * @param position The position to calculate for.
	 * @return The contents of the URL or file corresponding to the line number
	 *         containg the cursor position.
	 */
	public String getLine(int position)
	{
		return (getLine(new Cursor(this, position)));
	}

	/**
	 * Display some of this page as a string.
	 *
	 * @return The last few characters the source read in.
	 */
	public String toString()
	{
		StringBuffer buffer;
		int start;
		String ret;

		if (mSource.mOffset > 0)
		{
			buffer = new StringBuffer(43);
			start = mSource.mOffset - 40;
			if (0 > start)
			{
				start = 0;
			}
			else
			{
				buffer.append("...");
			}

			getText(buffer, start, mSource.mOffset);
			ret = buffer.toString();
		}
		else
		{
			ret = super.toString();
		}

		return (ret);
	}
}

