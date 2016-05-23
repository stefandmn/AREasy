package org.areasy.common.parser.csv;

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
import java.util.ArrayList;
import java.util.List;

/**
 * CSV reader.
 *
 * @version $Id: CsvReader.java,v 1.1 2008/05/25 17:26:07 swd\stefan.damian Exp $
 */
public class CsvReader
{
	private BufferedReader buffer;

	private boolean hasNext = true;

	private char separator;

	private char quotechar;

	private int skipLines;

	private boolean linesSkiped;

	/**
	 * The default separator to use if none is supplied to the constructor.
	 */
	public static final char DEFAULT_SEPARATOR = ',';

	/**
	 * The default quote character to use if none is supplied to the
	 * constructor.
	 */
	public static final char DEFAULT_QUOTE_CHARACTER = '"';

	/**
	 * The default line to start reading.
	 */
	public static final int DEFAULT_SKIP_LINES = 0;

	/**
	 * Constructs CSVReader using a comma for the separator.
	 *
	 * @param file the file name to an underlying CSV source.
	 * @throws IOException if any I/O error 
	 */
	public CsvReader(String file) throws IOException
	{
		this(new FileReader(file));
	}

	/**
	 * Constructs CSVReader using a comma for the separator.
	 *
	 * @param file the file structure to an underlying CSV source.
	 * @throws IOException if any I/O error 
	 */
	public CsvReader(File file) throws IOException
	{
		this(new FileReader(file));
	}

	/**
	 * Constructs CSVReader using a comma for the separator.
	 *
	 * @param reader the reader to an underlying CSV source.
	 */
	public CsvReader(Reader reader)
	{
		this(reader, DEFAULT_SEPARATOR);
	}

	/**
	 * Constructs CSVReader with supplied separator.
	 *
	 * @param reader	the reader to an underlying CSV source.
	 * @param separator the delimiter to use for separating entries.
	 */
	public CsvReader(Reader reader, char separator)
	{
		this(reader, separator, DEFAULT_QUOTE_CHARACTER);
	}


	/**
	 * Constructs CSVReader with supplied separator and quote char.
	 *
	 * @param reader	the reader to an underlying CSV source.
	 * @param separator the delimiter to use for separating entries
	 * @param quotechar the character to use for quoted elements
	 */
	public CsvReader(Reader reader, char separator, char quotechar)
	{
		this(reader, separator, quotechar, DEFAULT_SKIP_LINES);
	}

	/**
	 * Constructs CSVReader with supplied separator and quote char.
	 *
	 * @param reader	the reader to an underlying CSV source.
	 * @param separator the delimiter to use for separating entries
	 * @param quotechar the character to use for quoted elements
	 * @param line	  the line number to skip for start reading
	 */
	public CsvReader(Reader reader, char separator, char quotechar, int line)
	{
		this.buffer = new BufferedReader(reader);
		
		this.separator = separator;
		this.quotechar = quotechar;
		this.skipLines = line;
	}

	/**
	 * Reads the entire file into a List with each element being a String[] of
	 * tokens.
	 *
	 * @return a List of String[], with each String[] representing a line of the file.
	 * @throws IOException if bad things happen during the read
	 */
	public List readAll() throws IOException
	{
		List allElements = new ArrayList();
		
		while (hasNext)
		{
			String[] nextLineAsTokens = readNext();
			if (nextLineAsTokens != null) allElements.add(nextLineAsTokens);
		}

		return allElements;
	}

	/**
	 * Reads the next line from the buffer and converts to a string array.
	 *
	 * @return a string array with each comma-separated element as a separate
	 *         entry.
	 * @throws IOException if bad things happen during the read
	 */
	public String[] readNext() throws IOException
	{
		String nextLine = getNextLine();
		return hasNext ? parseLine(nextLine) : null;
	}

	/**
	 * Reads the next line from the file.
	 *
	 * @return the next line from the file without trailing newline
	 * @throws IOException if bad things happen during the read
	 */
	private String getNextLine() throws IOException
	{
		if (!this.linesSkiped)
		{
			for (int i = 0; i < skipLines; i++)
			{
				buffer.readLine();
			}

			this.linesSkiped = true;
		}

		String nextLine = buffer.readLine();
		if (nextLine == null) hasNext = false;

		return hasNext ? nextLine : null;
	}

	/**
	 * Parses an incoming String and returns an array of elements.
	 *
	 * @param nextLine the string to parse
	 * @return the comma-tokenized list of elements, or null if nextLine is null
	 * @throws IOException if bad things happen during the read
	 */
	private String[] parseLine(String nextLine) throws IOException
	{
		if (nextLine == null) return null;

		List tokensOnThisLine = new ArrayList();
		StringBuffer sb = new StringBuffer();
		boolean inQuotes = false;

		do
		{
			if (inQuotes)
			{
				// continuing a quoted section, reappend newline
				sb.append("\n");
				nextLine = getNextLine();

				if (nextLine == null) break;
			}
			for (int i = 0; i < nextLine.length(); i++)
			{
				char c = nextLine.charAt(i);
				if (c == quotechar)
				{
					// this gets complex... the quote may end a quoted block, or escape another quote.
					if (inQuotes && nextLine.length() > (i + 1) && nextLine.charAt(i + 1) == quotechar)
					{
						sb.append(nextLine.charAt(i + 1));
						i++;
					}
					else
					{
						inQuotes = !inQuotes;

						// the tricky case of an embedded quote in the middle: a,bc"d"ef,g
						if (i > 2 && nextLine.charAt(i - 1) != this.separator && nextLine.length() > (i + 1) && nextLine.charAt(i + 1) != this.separator) sb.append(c);
					}
				}
				else if (c == separator && !inQuotes)
				{
					tokensOnThisLine.add(sb.toString());
					sb = new StringBuffer(); // start work on next token
				}
				else sb.append(c);
			}
		}
		while (inQuotes);

		tokensOnThisLine.add(sb.toString());

		return (String[]) tokensOnThisLine.toArray(new String[0]);
	}

	/**
	 * Closes the underlying reader.
	 *
	 * @throws IOException if the close fails
	 */
	public void close() throws IOException
	{
		buffer.close();
	}

}
