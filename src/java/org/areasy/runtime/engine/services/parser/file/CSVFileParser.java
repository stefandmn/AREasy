package org.areasy.runtime.engine.services.parser.file;

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

import org.areasy.common.parser.csv.CsvReader;
import org.areasy.runtime.engine.services.parser.AbstractParser;
import org.areasy.runtime.engine.services.parser.ParserException;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Specialized parser class to extract data from "comma separated value" files.
 */
public class CSVFileParser extends AbstractParser
{
	private CsvReader reader = null;
	private List lines = null;

	/**
	 * Initialize parser class
	 *
	 * @throws org.areasy.runtime.engine.services.parser.ParserException if any error will occur
	 */
	public void open() throws ParserException
	{
		String charset = null;
		String file = getParserConfig().getString("parserfile", null);

        try
		{
            if (getRuntimeConfig() != null) charset = getRuntimeConfig().getString("charset",null);

            reader = new CsvReader((charset != null) ? new InputStreamReader(new FileInputStream(file),charset) : new FileReader(file));
			lines = reader.readAll();
		}
		catch (Exception ex)
		{
			throw new ParserException(ex);
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
	 * @throws org.areasy.runtime.engine.services.parser.ParserException if any error will occur
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
			if(reader != null) reader.close();

			setCursor(0);
			
			lines = null;
			reader = null;
		}
		catch (IOException ex)
		{
			//ignore it
		}
	}
}