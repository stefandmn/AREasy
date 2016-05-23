package org.areasy.runtime.engine.services.parser.file;

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


import org.areasy.runtime.engine.services.parser.AbstractParser;
import org.areasy.runtime.engine.services.parser.ParserException;
import org.areasy.common.parser.excel.Cell;
import org.areasy.common.parser.excel.Sheet;
import org.areasy.common.parser.excel.Workbook;
import org.areasy.common.parser.excel.WorkbookSettings;

import java.io.File;
import java.util.List;
import java.util.Vector;

/**
 * Specialized parser class to extract data from excel files.
 */
public class XLSFileParser extends AbstractParser
{
	private Workbook workbook = null;
	private Sheet sheet = null;

	private int pageIndex = 0;

	/**
	 * Initialize parser class
	 *
	 * @throws ParserException if any error will occur
	 */
	public void open() throws ParserException
	{
		String file = getParserConfig().getString("parserfile", null);
		setPageIndex(getParserConfig().getInt("pageindex", 0));

		try
		{
			WorkbookSettings wkbs = new WorkbookSettings();
			wkbs.setGCDisabled(false);

			//get workbook
			workbook = Workbook.getWorkbook(new File(file));
			Sheet sheets[] = workbook.getSheets();

			if(sheets == null) throw new ParserException("Workbook is null or corrupted: " + file);
				else sheet = workbook.getSheets()[getPageIndex()];

			//validate EndIndex value
			if(getEndIndex() == 0)
			{
				setEndIndex(sheet.getRows());
				getLogger().info("Found " + sheet.getRows() + " rows in the excel file");
			}
			else
			{
				if(sheet.getRows() < getEndIndex())
				{
					setEndIndex(sheet.getRows());
					getLogger().warn("Found only " + sheet.getRows() + " rows in the excel file that means 'startindex' parameter will be re-adapted");
				}
			}

		}
		catch(Exception e)
		{
			throw new ParserException(e);
		}
	}

	/**
	 * Close and dispose parser class
	 */
	public void close()
	{
		workbook.close();
		workbook = null;

		setCursor(0);
	}

	/**
	 * Execute parser class and return the output.
	 *
	 * @return an array with strings.
	 * @throws org.areasy.runtime.engine.services.parser.ParserException if any error will occur
	 */
	public String[] read() throws ParserException
	{
		if (sheet != null && ((getEndIndex() == 0) || (getEndIndex() >= 0 && getCurrentIndex() < getEndIndex())))
		{
			List output =  new Vector();
			int columns = sheet.getColumns();

			for(int i = 0; i < columns; i++)
			{
				Cell cell = sheet.getCell(i, getCurrentIndex());

				if(cell != null)
				{
					String data = cell.getContents();
					if(data != null) output.add(data);
				}
			}

			setNextCursor();

			return (String[])output.toArray(new String[output.size()]);
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
		if (sheet!= null && getStartIndex() >= index && index < getEndIndex())
		{
			List output =  new Vector();
			int columns = sheet.getColumns();

			for(int i = 0; i < columns; i++)
			{
				Cell cell = sheet.getCell(i, index);

				if(cell != null)
				{
					String data = cell.getContents();
					if(data != null) output.add(data);
				}
			}

			return (String[])output.toArray(new String[output.size()]);
		}
		else return null;
	}

	/**
	 * Get total number of columns that will be delivered by the XLS parser
	 *
	 * @return number of columns found in the source file  or -1
	 */
	public int getNumberOfColumns()
	{
		if (sheet != null) return sheet.getColumns();
			else return -1;
	}

	public int getPageIndex()
	{
		return pageIndex;
	}

	public void setPageIndex(int pageIndex)
	{
		this.pageIndex = pageIndex;
	}
}
