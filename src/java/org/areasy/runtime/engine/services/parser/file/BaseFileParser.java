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

import org.areasy.common.data.ClassUtility;
import org.areasy.common.data.StringUtility;
import org.areasy.runtime.RuntimeManager;
import org.areasy.runtime.engine.services.parser.AbstractParser;
import org.areasy.runtime.engine.services.parser.ParserException;

import java.io.File;
import java.util.Vector;

/**
 * Specialized parser class to identify the source file and to call the proper parser in order to deliver
 * the data-source content.
 *
 */
public class BaseFileParser extends AbstractParser
{
	/** delegated parser engine */
	private AbstractParser reader = null;

	/**
	 * Initialize parser class
	 *
	 * @throws org.areasy.runtime.engine.services.parser.ParserException if any error will occur
	 */
	public void open() throws ParserException
	{
		String fileName = getParserConfig().getString("parserfile", null);
		String fileType = null;

		if(StringUtility.isNotEmpty(fileName))
		{
			File file = new File(fileName);

			if(!file.exists()) file = new File(RuntimeManager.getWorkingDirectory(), fileName);
			if(!file.exists()) file = new File(RuntimeManager.getHomeDirectory(), fileName);

			if(!file.exists()) throw new ParserException("File '" + fileName + "' couldn't be identified");
			else
			{
				fileName = file.getPath();
				getParserConfig().setKey("parserfile", fileName);
			}
		}

		int index = fileName.lastIndexOf(".");
		if(index > 0) fileType = fileName.substring(index + 1).toLowerCase();

		getLogger().debug("Requested parser: " + fileType);
		if(!getRuntimeConfig().getVector("app.runtime.parsers", new Vector()).contains(fileType)) throw new ParserException("Parser type '" + fileType + "' is not registered");

        try
		{
			this.reader = (AbstractParser) ClassUtility.getInstance( getRuntimeConfig().getString("app.runtime.parser." + fileType + ".class") );
            this.reader.init(getServerConnection(), getRuntimeConfig(), getParserConfig());
		}
		catch(Throwable th)
		{
			throw new ParserException("Can not create instance for '" + fileType + "' parser type", th);
		}
	}

	/**
	 * Execute parser class and return the output.
	 *
	 * @return an array with strings.
	 * @throws org.areasy.runtime.engine.services.parser.ParserException if any error will occur
	 */
	public String[] read() throws ParserException
	{
		if (reader != null) return reader.read();
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
		if (reader != null) return reader.read(index);
			else return null;
	}

	/**
	 * Get total number of columns that will be delivered by the specified parser
	 *
	 * @return number of columns found in the source file or -1
	 */
	public int getNumberOfColumns()
	{
		if (reader != null) return reader.getNumberOfColumns();
			else return -1;
	}

	/**
	 * Close and dispose parser class
	 */
	public void close()
	{
		if(reader != null) reader.close();
	}

	public int getStartIndex()
	{
		if(reader != null) return reader.getStartIndex();
			else return super.getStartIndex();
	}

	public void setStartIndex(int startIndex)
	{
		if(reader != null) reader.setStartIndex(startIndex);
	}

	public int getEndIndex()
	{
		if(reader != null) return reader.getEndIndex();
			else return super.getEndIndex();
	}

	public void setEndIndex(int endIndex)
	{
		if(reader != null) reader.setEndIndex(endIndex);
	}

	public int getCurrentIndex()
	{
		if(reader != null) return reader.getCurrentIndex();
			else return super.getCurrentIndex();
	}

	public void setCursor(int currentIndex)
	{
		if(reader != null) reader.setCursor(currentIndex);
	}

	public void setNextCursor()
	{
		if(reader != null) reader.setNextCursor();
	}
}