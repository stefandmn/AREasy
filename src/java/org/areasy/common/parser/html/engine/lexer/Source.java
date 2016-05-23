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

import java.io.*;

/**
 * A buffered source of characters.
 * A Source is very similar to a the following construct:
 * <pre>
 * new InputStreamReader (new BufferedInputStream (connection.getInputStream ()), charset)
 * </pre>
 * It differs from the above, in two ways:
 * <li>the fetching of bytes from the connection's input stream may be asynchronous</li>
 * <li>the character set may be changed, which resets the input stream</li>
 */
public class Source
		extends
		Reader
		implements
		Serializable
{

	/**
	 * An initial buffer size.
	 */
	public static int BUFFER_SIZE = 16384;

	/**
	 * Return value when no more characters are left.
	 */
	public static final int EOF = -1;

	/**
	 * The stream of bytes.
	 */
	protected transient InputStream mStream;

	/**
	 * The character set in use.
	 */
	protected String mEncoding;

	/**
	 * The converter from bytes to characters.
	 */
	protected transient InputStreamReader mReader;

	/**
	 * The characters read so far.
	 */
	public /*volatile*/ char[] mBuffer;

	/**
	 * The number of valid bytes in the buffer.
	 */
	public /*volatile*/ int mLevel;

	/**
	 * The offset of the next byte returned by read().
	 */
	public /*volatile*/ int mOffset;

	/**
	 * The bookmark.
	 */
	protected int mMark;

	/**
	 * Create a source of characters using the default character set.
	 *
	 * @param stream The stream of bytes to use.
	 */
	public Source(InputStream stream)
			throws
			UnsupportedEncodingException
	{
		this(stream, null, BUFFER_SIZE);
	}

	/**
	 * Create a source of characters.
	 *
	 * @param stream  The stream of bytes to use.
	 * @param charset The character set used in encoding the stream.
	 */
	public Source(InputStream stream, String charset)
			throws
			UnsupportedEncodingException
	{
		this(stream, charset, BUFFER_SIZE);
	}

	/**
	 * Create a source of characters.
	 *
	 * @param stream  The stream of bytes to use.
	 * @param charset The character set used in encoding the stream.
	 */
	public Source(InputStream stream, String charset, int buffer_size)
			throws
			UnsupportedEncodingException
	{
		if (null == stream)
		{
			stream = new Stream(null);
		}
		mStream = stream;
		if (null == charset)
		{
			mReader = new InputStreamReader(stream);
			mEncoding = mReader.getEncoding();
		}
		else
		{
			mEncoding = charset;
			mReader = new InputStreamReader(stream, charset);
		}
		mBuffer = new char[buffer_size];
		mLevel = 0;
		mOffset = 0;
		mMark = -1;
	}

	//
	// Serialization support
	//

	private void writeObject(ObjectOutputStream out)
			throws
			IOException
	{
		int offset;
		char[] buffer;

		if (null != mStream)
		{
			// remember the offset, drain the input stream, restore the offset
			offset = mOffset;
			buffer = new char[4096];
			while (-1 != read(buffer))
			{
				;
			}
			mOffset = offset;
		}

		out.defaultWriteObject();
	}

	private void readObject(ObjectInputStream in)
			throws
			IOException,
			ClassNotFoundException
	{
		in.defaultReadObject();
		if (null != mBuffer) // buffer is null when destroy's been called
		// pretend we're open, mStream goes null when exhausted
		{
			mStream = new ByteArrayInputStream(new byte[0]);
		}
	}

	/**
	 * Get the input stream being used.
	 *
	 * @return The current input stream.
	 */
	public InputStream getStream()
	{
		return (mStream);
	}

	/**
	 * Get the encoding being used to convert characters.
	 *
	 * @return The current encoding.
	 */
	public String getEncoding()
	{
		return (mEncoding);
	}

	/**
	 * Fetch more characters from the underlying reader.
	 * Has no effect if the underlying reader has been drained.
	 *
	 * @param min The minimum to read.
	 * @throws IOException If the underlying reader read() throws one.
	 */
	protected void fill(int min)
			throws
			IOException
	{
		char[] buffer;
		int size;
		int read;

		if (null != mReader) // mReader goes null when it's been sucked dry
		{
			size = mBuffer.length - mLevel; // available space
			if (size < min) // oops, better get some buffer space
			{
				// unknown length... keep doubling
				size = mBuffer.length * 2;
				read = mLevel + min;
				if (size < read) // or satisfy min, whichever is greater
				{
					size = read;
				}
				else
				{
					min = size - mLevel; // read the max
				}
				buffer = new char[size];
			}
			else
			{
				buffer = mBuffer;
				min = size;
			}

			// read into the end of the 'new' buffer
			read = mReader.read(buffer, mLevel, min);
			if (-1 == read)
			{
				mReader.close();
				mReader = null;
			}
			else
			{
				if (mBuffer != buffer)
				{   // copy the bytes previously read
					System.arraycopy(mBuffer, 0, buffer, 0, mLevel);
					mBuffer = buffer;
				}
				mLevel += read;
			}
		}
	}

	/**
	 * Does nothing.
	 * It's supposed to close the stream, but use destroy() instead.
	 *
	 * @see #destroy
	 */
	public void close() throws IOException
	{
	}

	/**
	 * Read a single character.
	 * This method will block until a character is available,
	 * an I/O error occurs, or the end of the stream is reached.
	 *
	 * @return The character read, as an integer in the range 0 to 65535
	 *         (<tt>0x00-0xffff</tt>), or -1 if the end of the stream has
	 *         been reached
	 * @throws IOException If an I/O error occurs.
	 */
	public int read() throws IOException
	{
		int ret;

		if (mLevel - mOffset < 1)
		{
			if (null == mStream) // mStream goes null on close()
			{
				throw new IOException("reader is closed");
			}
			fill(1);
			if (mOffset >= mLevel)
			{
				ret = EOF;
			}
			else
			{
				ret = mBuffer[mOffset++];
			}
		}
		else
		{
			ret = mBuffer[mOffset++];
		}

		return (ret);
	}

	/**
	 * Read characters into a portion of an array.  This method will block
	 * until some input is available, an I/O error occurs, or the end of the
	 * stream is reached.
	 *
	 * @param cbuf Destination buffer
	 * @param off  Offset at which to start storing characters
	 * @param len  Maximum number of characters to read
	 * @return The number of characters read, or -1 if the end of the
	 *         stream has been reached
	 * @throws IOException If an I/O error occurs.
	 */
	public int read(char[] cbuf, int off, int len) throws IOException
	{
		int ret;

		if (null == mStream) // mStream goes null on close()
		{
			throw new IOException("reader is closed");
		}
		if ((null == cbuf) || (0 > off) || (0 > len))
		{
			throw new IOException("illegal argument read ("
					+ ((null == cbuf) ? "null" : "cbuf")
					+ ", " + off + ", " + len + ")");
		}
		if (mLevel - mOffset < len)
		{
			fill(len - (mLevel - mOffset)); // minimum to satisfy this request
		}
		if (mOffset >= mLevel)
		{
			ret = EOF;
		}
		else
		{
			ret = Math.min(mLevel - mOffset, len);
			System.arraycopy(mBuffer, mOffset, cbuf, off, ret);
			mOffset += ret;
		}

		return (ret);
	}

	/**
	 * Read characters into an array.
	 * This method will block until some input is available, an I/O error occurs,
	 * or the end of the stream is reached.
	 *
	 * @param cbuf Destination buffer.
	 * @return The number of characters read, or -1 if the end of the stream has
	 *         been reached.
	 * @throws IOException If an I/O error occurs.
	 */

	public int read(char[] cbuf) throws IOException
	{
		return (read(cbuf, 0, cbuf.length));
	}

	/**
	 * Reset the source.
	 * Repositions the read point to begin at zero.
	 *
	 * @throws IllegalStateException If the source has been closed.
	 */
	public void reset()
	{
		if (null == mStream) // mStream goes null on close()
		{
			throw new IllegalStateException("source is closed");
		}
		if (-1 != mMark)
		{
			mOffset = mMark;
		}
		else
		{
			mOffset = 0;
		}
	}

	/**
	 * Tell whether this stream supports the mark() operation.
	 *
	 * @return <code>true</code> if and only if this stream supports the mark operation.
	 */
	public boolean markSupported()
	{
		return (true);
	}

	/**
	 * Mark the present position in the stream.  Subsequent calls to reset()
	 * will attempt to reposition the stream to this point.  Not all
	 * character-input streams support the mark() operation.
	 *
	 * @param readAheadLimit <em>Not used.</em>
	 * @throws IOException <em>Never thrown</em>.
	 */
	public void mark(int readAheadLimit) throws IOException
	{
		if (null == mStream) // mStream goes null on close()
		{
			throw new IOException("reader is closed");
		}
		mMark = mOffset;
	}

	/**
	 * Tell whether this stream is ready to be read.
	 *
	 * @return <code>true</code> if the next read() is guaranteed not to block
	 *         for input, <code>false</code> otherwise.
	 *         Note that returning false does not guarantee that the next read will block.
	 * @throws IOException <em>Never thrown</em>.
	 */
	public boolean ready() throws IOException
	{
		if (null == mStream) // mStream goes null on close()
		{
			throw new IOException("reader is closed");
		}
		return (mOffset < mLevel);
	}

	/**
	 * Skip characters.
	 * This method will block until some characters are available,
	 * an I/O error occurs, or the end of the stream is reached.
	 * <em>Note: n is treated as an int</em>
	 *
	 * @param n The number of characters to skip.
	 * @return The number of characters actually skipped
	 * @throws IllegalArgumentException If <code>n</code> is negative.
	 * @throws IOException              If an I/O error occurs.
	 */
	public long skip(long n) throws IOException
	{
		long ret;

		if (null == mStream) // mStream goes null on close()
		{
			throw new IOException("reader is closed");
		}
		if (mLevel - mOffset < n)
		{
			fill((int) (n - (mLevel - mOffset))); // minimum to satisfy this request
		}
		if (mOffset >= mLevel)
		{
			ret = EOF;
		}
		else
		{
			ret = Math.min(mLevel - mOffset, n);
			mOffset += ret;
		}

		return (ret);
	}

	//
	// Methods not in your Daddy's Reader
	//

	/**
	 * Undo the read of a single character.
	 *
	 * @throws IOException If no characters have been read.
	 */
	public void unread() throws IOException
	{
		if (0 < mOffset)
		{
			mOffset--;
		}
		else
		{
			throw new IOException("can't unread no characters");
		}
	}

	/**
	 * Close the stream.  Once a stream has been closed, further read(),
	 * ready(), mark(), or reset() invocations will throw an IOException.
	 * Closing a previously-closed stream, however, has no effect.
	 *
	 * @throws IOException If an I/O error occurs
	 */
	public void destroy() throws IOException
	{
		mStream = null;
		if (null != mReader)
		{
			mReader.close();
		}
		mReader = null;
		mBuffer = null;
		mLevel = 0;
		mOffset = 0;
		mMark = -1;
	}

	/**
	 * Get the position (in characters).
	 *
	 * @return The number of characters that have been read.
	 */
	public int offset()
	{
		return (mOffset);
	}

	/**
	 * Get the number of available characters.
	 *
	 * @return The number of characters that can be read without blocking.
	 */
	public int available()
	{
		return (mLevel - mOffset);
	}
}
