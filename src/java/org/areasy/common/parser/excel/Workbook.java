package org.areasy.common.parser.excel;

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

import org.areasy.common.parser.excel.read.biff.BiffException;
import org.areasy.common.parser.excel.read.biff.File;
import org.areasy.common.parser.excel.read.biff.PasswordException;
import org.areasy.common.parser.excel.read.biff.WorkbookParser;
import org.areasy.common.parser.excel.write.WritableWorkbook;
import org.areasy.common.parser.excel.write.biff.DefaultWritableWorkbook;

import java.io.*;

/**
 * Represents a Workbook.  Contains the various factory methods and provides
 * a variety of accessors which provide access to the work sheets.
 */
public abstract class Workbook
{
	/**
	 * The current version of the software
	 */
	private static final String VERSION = "2.6.12";

	/**
	 * The constructor
	 */
	protected Workbook()
	{
	}

	/**
	 * Gets the sheets within this workbook.  Use of this method for
	 * large worksheets can cause performance problems.
	 *
	 * @return an array of the individual sheets
	 */
	public abstract Sheet[] getSheets();

	/**
	 * Gets the sheet names
	 *
	 * @return an array of strings containing the sheet names
	 */
	public abstract String[] getSheetNames();

	/**
	 * Gets the specified sheet within this workbook
	 * As described in the accompanying technical notes, each call
	 * to getSheet forces a reread of the sheet (for memory reasons).
	 * Therefore, do not make unnecessary calls to this method.  Furthermore,
	 * do not hold unnecessary references to Sheets in client code, as
	 * this will prevent the garbage collector from freeing the memory
	 *
	 * @param index the zero based index of the reQuired sheet
	 * @return The sheet specified by the index
	 * @throws IndexOutOfBoundException when index refers to a non-existent
	 *                                  sheet
	 */
	public abstract Sheet getSheet(int index)
			throws IndexOutOfBoundsException;

	/**
	 * Gets the sheet with the specified name from within this workbook.
	 * As described in the accompanying technical notes, each call
	 * to getSheet forces a reread of the sheet (for memory reasons).
	 * Therefore, do not make unnecessary calls to this method.  Furthermore,
	 * do not hold unnecessary references to Sheets in client code, as
	 * this will prevent the garbage collector from freeing the memory
	 *
	 * @param name the sheet name
	 * @return The sheet with the specified name, or null if it is not found
	 */
	public abstract Sheet getSheet(String name);

	/**
	 * Accessor for the software version
	 *
	 * @return the version
	 */
	public static String getVersion()
	{
		return VERSION;
	}

	/**
	 * Returns the number of sheets in this workbook
	 *
	 * @return the number of sheets in this workbook
	 */
	public abstract int getNumberOfSheets();

	/**
	 * Gets the named cell from this workbook.  If the name refers to a
	 * range of cells, then the cell on the top left is returned.  If
	 * the name cannot be found, null is returned.
	 * This is a convenience function to quickly access the contents
	 * of a single cell.  If you need further information (such as the
	 * sheet or adjacent cells in the range) use the functionally
	 * richer method, findByName which returns a list of ranges
	 *
	 * @param name the name of the cell/range to search for
	 * @return the cell in the top left of the range if found, NULL
	 *         otherwise
	 */
	public abstract Cell findCellByName(String name);

	/**
	 * Returns the cell for the specified location eg. "Sheet1!A4".
	 * This is identical to using the CellReferenceHelper with its
	 * associated performance overheads, consequently it should
	 * be use sparingly
	 *
	 * @param loc the cell to retrieve
	 * @return the cell at the specified location
	 */
	public abstract Cell getCell(String loc);

	/**
	 * Gets the named range from this workbook.  The Range object returns
	 * contains all the cells from the top left to the bottom right
	 * of the range.
	 * If the named range comprises an adjacent range,
	 * the Range[] will contain one object; for non-adjacent
	 * ranges, it is necessary to return an array of length greater than
	 * one.
	 * If the named range contains a single cell, the top left and
	 * bottom right cell will be the same cell
	 *
	 * @param name the name of the cell/range to search for
	 * @return the range of cells, or NULL if the range does not exist
	 */
	public abstract Range[] findByName(String name);

	/**
	 * Gets the named ranges
	 *
	 * @return the list of named cells within the workbook
	 */
	public abstract String[] getRangeNames();


	/**
	 * Determines whether the sheet is protected
	 *
	 * @return TRUE if the workbook is protected, FALSE otherwise
	 */
	public abstract boolean isProtected();

	/**
	 * Parses the excel file.
	 * If the workbook is password protected a PasswordException is thrown
	 * in case consumers of the API wish to handle this in a particular way
	 *
	 * @throws BiffException
	 * @throws PasswordException
	 */
	protected abstract void parse() throws BiffException, PasswordException;

	/**
	 * Closes this workbook, and frees makes any memory allocated available
	 * for garbage collection
	 */
	public abstract void close();

	/**
	 * A factory method which takes in an excel file and reads in the contents.
	 *
	 * @param file the excel 97 spreadsheet to parse
	 * @return a workbook instance
	 * @throws IOException
	 * @throws BiffException
	 */
	public static Workbook getWorkbook(java.io.File file)
			throws IOException, BiffException
	{
		return getWorkbook(file, new WorkbookSettings());
	}

	/**
	 * A factory method which takes in an excel file and reads in the contents.
	 *
	 * @param file the excel 97 spreadsheet to parse
	 * @param ws   the settings for the workbook
	 * @return a workbook instance
	 * @throws IOException
	 * @throws BiffException
	 */
	public static Workbook getWorkbook(java.io.File file, WorkbookSettings ws)
			throws IOException, BiffException
	{
		FileInputStream fis = new FileInputStream(file);

		// Always close down the input stream, regardless of whether or not the
		// file can be parsed.  Thanks to Steve Hahn for this
		File dataFile = null;

		try
		{
			dataFile = new File(fis, ws);
		}
		catch (IOException e)
		{
			fis.close();
			throw e;
		}
		catch (BiffException e)
		{
			fis.close();
			throw e;
		}

		fis.close();

		Workbook workbook = new WorkbookParser(dataFile, ws);
		workbook.parse();

		return workbook;
	}

	/**
	 * A factory method which takes in an excel file and reads in the contents.
	 *
	 * @param is an open stream which is the the excel 97 spreadsheet to parse
	 * @return a workbook instance
	 * @throws IOException
	 * @throws BiffException
	 */
	public static Workbook getWorkbook(InputStream is)
			throws IOException, BiffException
	{
		return getWorkbook(is, new WorkbookSettings());
	}

	/**
	 * A factory method which takes in an excel file and reads in the contents.
	 *
	 * @param is an open stream which is the the excel 97 spreadsheet to parse
	 * @param ws the settings for the workbook
	 * @return a workbook instance
	 * @throws IOException
	 * @throws BiffException
	 */
	public static Workbook getWorkbook(InputStream is, WorkbookSettings ws)
			throws IOException, BiffException
	{
		File dataFile = new File(is, ws);

		Workbook workbook = new WorkbookParser(dataFile, ws);
		workbook.parse();

		return workbook;
	}

	/**
	 * Creates a writable workbook with the given file name
	 *
	 * @param file the workbook to copy
	 * @return a writable workbook
	 * @throws IOException
	 */
	public static WritableWorkbook createWorkbook(java.io.File file)
			throws IOException
	{
		return createWorkbook(file, new WorkbookSettings());
	}

	/**
	 * Creates a writable workbook with the given file name
	 *
	 * @param file the file to copy from
	 * @param ws   the global workbook settings
	 * @return a writable workbook
	 * @throws IOException
	 */
	public static WritableWorkbook createWorkbook(java.io.File file,
												  WorkbookSettings ws)
			throws IOException
	{
		FileOutputStream fos = new FileOutputStream(file);
		WritableWorkbook w = new DefaultWritableWorkbook(fos, true, ws);
		return w;
	}

	/**
	 * Creates a writable workbook with the given filename as a copy of
	 * the workbook passed in.  Once created, the contents of the writable
	 * workbook may be modified
	 *
	 * @param file the output file for the copy
	 * @param in   the workbook to copy
	 * @return a writable workbook
	 * @throws IOException
	 */
	public static WritableWorkbook createWorkbook(java.io.File file,
												  Workbook in)
			throws IOException
	{
		return createWorkbook(file, in, new WorkbookSettings());
	}

	/**
	 * Creates a writable workbook with the given filename as a copy of
	 * the workbook passed in.  Once created, the contents of the writable
	 * workbook may be modified
	 *
	 * @param file the output file for the copy
	 * @param in   the workbook to copy
	 * @param ws   the configuration for this workbook
	 * @return a writable workbook
	 */
	public static WritableWorkbook createWorkbook(java.io.File file,
												  Workbook in,
												  WorkbookSettings ws)
			throws IOException
	{
		FileOutputStream fos = new FileOutputStream(file);
		WritableWorkbook w = new DefaultWritableWorkbook(fos, in, true, ws);
		return w;
	}

	/**
	 * Creates a writable workbook as a copy of
	 * the workbook passed in.  Once created, the contents of the writable
	 * workbook may be modified
	 *
	 * @param os the stream to write to
	 * @param in the workbook to copy
	 * @return a writable workbook
	 * @throws IOException
	 */
	public static WritableWorkbook createWorkbook(OutputStream os,
												  Workbook in)
			throws IOException
	{
		return createWorkbook(os, in, ((WorkbookParser) in).getSettings());
	}

	/**
	 * Creates a writable workbook as a copy of
	 * the workbook passed in.  Once created, the contents of the writable
	 * workbook may be modified
	 *
	 * @param os the output stream to write to
	 * @param in the workbook to copy
	 * @param ws the configuration for this workbook
	 * @return a writable workbook
	 * @throws IOException
	 */
	public static WritableWorkbook createWorkbook(OutputStream os,
												  Workbook in,
												  WorkbookSettings ws)
			throws IOException
	{
		WritableWorkbook w = new DefaultWritableWorkbook(os, in, false, ws);
		return w;
	}

	/**
	 * Creates a writable workbook.  When the workbook is closed,
	 * it will be streamed directly to the output stream.  In this
	 * manner, a generated excel spreadsheet can be passed from
	 * a servlet to the browser over HTTP
	 *
	 * @param os the output stream
	 * @return the writable workbook
	 * @throws IOException
	 */
	public static WritableWorkbook createWorkbook(OutputStream os)
			throws IOException
	{
		return createWorkbook(os, new WorkbookSettings());
	}

	/**
	 * Creates a writable workbook.  When the workbook is closed,
	 * it will be streamed directly to the output stream.  In this
	 * manner, a generated excel spreadsheet can be passed from
	 * a servlet to the browser over HTTP
	 *
	 * @param os the output stream
	 * @param ws the configuration for this workbook
	 * @return the writable workbook
	 * @throws IOException
	 */
	public static WritableWorkbook createWorkbook(OutputStream os,
												  WorkbookSettings ws)
			throws IOException
	{
		WritableWorkbook w = new DefaultWritableWorkbook(os, false, ws);
		return w;
	}
}





