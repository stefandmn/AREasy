package org.areasy.common.parser.excel.write.biff;

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

import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;
import org.areasy.common.parser.excel.WorkbookSettings;
import org.areasy.common.parser.excel.biff.ByteData;

import java.io.IOException;
import java.io.OutputStream;

/**
 * A file of excel data to be written out.  All the excel data is held
 * in memory, and when the close method is called a CompoundFile object
 * is used to write the Biff oriented excel data in the CompoundFile
 * format
 */
public final class File
{
	/**
	 * The logger
	 */
	private static Logger logger = LoggerFactory.getLog(File.class);

	/**
	 * The data from the excel 97 file
	 */
	private ExcelDataOutput data;
	/**
	 * The current position within the file
	 */
	private int pos;
	/**
	 * The output stream
	 */
	private OutputStream outputStream;
	/**
	 * The initial file size
	 */
	private int initialFileSize;
	/**
	 * The amount to increase the growable array by
	 */
	private int arrayGrowSize;
	/**
	 * The workbook settings
	 */
	private WorkbookSettings workbookSettings;
	/**
	 * The read compound file.  This will only be non-null if there are macros
	 * or other property sets of that ilk which that we should be copying
	 */
	org.areasy.common.parser.excel.read.biff.CompoundFile readCompoundFile;

	/**
	 * Constructor
	 *
	 * @param os  the output stream
	 * @param ws  the configuration settings for this workbook
	 * @param rcf the rea compound file
	 */
	File(OutputStream os, WorkbookSettings ws, org.areasy.common.parser.excel.read.biff.CompoundFile rcf)
			throws IOException
	{
		outputStream = os;
		workbookSettings = ws;
		readCompoundFile = rcf;
		createDataOutput();
	}

	private void createDataOutput() throws IOException
	{
		if (workbookSettings.getUseTemporaryFileDuringWrite())
		{
			data = new FileDataOutput
					(workbookSettings.getTemporaryFileDuringWriteDirectory());
		}
		else
		{
			initialFileSize = workbookSettings.getInitialFileSize();
			arrayGrowSize = workbookSettings.getArrayGrowSize();

			data = new MemoryDataOutput(initialFileSize, arrayGrowSize);
		}
	}

	/**
	 * Closes the file.  In fact, this writes out all the excel data
	 * to disk using a CompoundFile object, and then frees up all the memory
	 * allocated to the workbook
	 *
	 * @param cs TRUE if this should close the stream, FALSE if the application
	 *           closes it
	 * @throws IOException
	 */
	void close(boolean cs) throws IOException, ExcelWriteException
	{
		CompoundFile cf = new CompoundFile(data,
				data.getPosition(),
				outputStream,
				readCompoundFile);
		cf.write();

		outputStream.flush();
		data.close();

		if (cs)
		{
			outputStream.close();
		}

		// Cleanup the memory a bit
		data = null;

		if (!workbookSettings.getGCDisabled())
		{
			System.gc();
		}
	}

	/**
	 * Adds the biff record data to the memory allocated for this File
	 *
	 * @param record the record to add to the excel data
	 * @throws IOException
	 */
	public void write(ByteData record) throws IOException
	{
		byte[] bytes = record.getBytes();

		data.write(bytes);
	}

	/**
	 * Gets the current position within the file
	 *
	 * @return the current position
	 */
	int getPos() throws IOException
	{
		return data.getPosition();
	}

	/**
	 * Used to manually alter the contents of the written out data.  This
	 * is used when cross-referencing cell records
	 *
	 * @param pos	 the position to alter
	 * @param newdata the data to modify
	 */
	void setData(byte[] newdata, int pos) throws IOException
	{
		data.setData(newdata, pos);
	}

	/**
	 * Sets a new output file.  This allows the same workbook to be
	 * written to various different output files without having to
	 * read in any templates again
	 *
	 * @param os the output stream
	 */
	public void setOutputFile(OutputStream os) throws IOException
	{
		if (data != null)
		{
			logger.warn("Rewriting a workbook with non-empty data");
		}

		outputStream = os;
		createDataOutput();
	}
}
