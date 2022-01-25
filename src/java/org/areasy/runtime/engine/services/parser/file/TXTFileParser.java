package org.areasy.runtime.engine.services.parser.file;

/*
 *  Copyright (c) 2007-2022 AREasy Runtime
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

import org.areasy.common.data.StringUtility;
import org.areasy.runtime.engine.services.parser.AbstractParser;
import org.areasy.runtime.engine.services.parser.ParserException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


/**
 * Specialized parser class to extract data from "comma separated value" files.
 */
public class TXTFileParser extends AbstractParser
{
	private List lines = null;

	/**
	 * Initialize parser class
	 *
	 * @throws ParserException if any error will occur
	 */
	public void open() throws ParserException
	{
		String charset = null;
		String file = getParserConfig().getString("parserfile", null);
		boolean useSignatureFormat = getParserConfig().getBoolean("parserobjsigformat", false);
		String separator = getParserConfig().getString("parserseparator", null);
		BufferedReader buffer = null; // buffered for readLine()

        try
		{
            if (getRuntimeConfig() != null) charset = getRuntimeConfig().getString("charset",null);

			String line;
			buffer = new BufferedReader((charset != null) ? new InputStreamReader(new FileInputStream(file),charset) : new InputStreamReader(new FileInputStream(file)));

			if (buffer != null)
			{
				lines = new ArrayList();

				while ((line = buffer.readLine()) != null)
				{
					String data[] = null;

					if(useSignatureFormat) data = StringUtility.split(line, ":", 1);
						else if(separator != null) data = StringUtility.splitByWholeSeparator(line, separator);
							else data = StringUtility.splitByWholeSeparator(line, "@#$");

					if (data != null && data.length >0) lines.add(data);
				}
			}
		}
		catch (Exception ex)
		{
			throw new ParserException(ex);
		}
		finally
		{
			if (buffer != null)
			{
				try
				{
					buffer.close();
				}
				catch (Throwable t)
				{ /* ensure close happens */ }
			}
		}

		//validate EndIndex value
		if(getEndIndex() == 0)
		{
			setEndIndex(lines.size());
			getLogger().info("Found " + lines.size() + " rows in the data file");
		}
		else
		{
			if(lines.size() < getEndIndex())
			{
				setEndIndex(lines.size());
				getLogger().warn("Found only " + lines.size() + " rows in the data file that means 'startindex' parameter will be re-adapted");
			}
		}
	}

	/**
	 * Execute parser class and return the output.
	 *
	 * @return an array with strings.
	 * @throws ParserException if any error will occur
	 */
	public String[] read() throws ParserException
	{
		if (lines != null && ((getEndIndex() == 0) || (getEndIndex() > 0 && getCurrentIndex() < getEndIndex())))
		{
			String data[] = (String[]) lines.get(getCurrentIndex());
			setNextCursor();

			return data;
		}
		else return null;
	}

	/**
	 * Execute parser class and return the output from the specified index.
	 *
	 * @param index reading index
	 * @return an array with strings.
	 * @throws ParserException if any error will occur
	 */
	public String[] read(int index) throws ParserException
	{
		if (lines != null && getStartIndex() >= index && index < getEndIndex())
		{
			return (String[]) lines.get(index);
		}
		else return null;
	}

	/**
	 * Get total number of columns that will be delivered by the CSV parser
	 *
	 * @return number of columns found in the source file or -1
	 */
	public int getNumberOfColumns()
	{
		if (lines != null) return ((String[]) lines.get(0)).length;
			else return -1;
	}

	/**
	 * Close and dispose parser class
	 */
	public void close()
	{
		try
		{
			lines.clear();
			setCursor(0);
			
			lines = null;
		}
		catch (Exception ex)
		{
			//ignore it
		}
	}
}
