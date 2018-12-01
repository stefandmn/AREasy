package org.areasy.common.parser.excel.write.biff;

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

import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;

/**
 * Used to generate the excel biff data using a temporary file.  This
 * class wraps a RandomAccessFile
 */
class FileDataOutput implements ExcelDataOutput
{
	// The logger
	private static Logger logger = LoggerFactory.getLog(FileDataOutput.class);

	/**
	 * The temporary file
	 */
	private File temporaryFile;

	/**
	 * The excel data
	 */
	private RandomAccessFile data;

	/**
	 * Constructor
	 *
	 * @param tmpdir the temporary directory used to write files.  If this is
	 *               NULL then the sytem temporary directory will be used
	 */
	public FileDataOutput(File tmpdir) throws IOException
	{
		temporaryFile = File.createTempFile("excel", ".tmp", tmpdir);
		temporaryFile.deleteOnExit();
		data = new RandomAccessFile(temporaryFile, "rw");
	}

	/**
	 * Writes the bytes to the end of the array, growing the array
	 * as needs dictate
	 *
	 * @param bytes the data to write to the end of the array
	 */
	public void write(byte[] bytes) throws IOException
	{
		data.write(bytes);
	}

	/**
	 * Gets the current position within the file
	 *
	 * @return the position within the file
	 */
	public int getPosition() throws IOException
	{
		// As all excel data structures are four bytes anyway, it's ok to
		// truncate the long to an int
		return (int) data.getFilePointer();
	}

	/**
	 * Sets the data at the specified position to the contents of the array
	 *
	 * @param pos	 the position to alter
	 * @param newdata the data to modify
	 */
	public void setData(byte[] newdata, int pos) throws IOException
	{
		long curpos = data.getFilePointer();
		data.seek(pos);
		data.write(newdata);
		data.seek(curpos);
	}

	/**
	 * Writes the data to the output stream
	 */
	public void writeData(OutputStream out) throws IOException
	{
		byte[] buffer = new byte[1024];
		int length = 0;
		data.seek(0);
		while ((length = data.read(buffer)) != -1)
		{
			out.write(buffer, 0, length);
		}
	}

	/**
	 * Called when the final compound file has been written
	 */
	public void close() throws IOException
	{
		data.close();

		// Explicitly delete the temporary file, since sometimes it is the case
		// that a single process may be generating multiple different excel files
		temporaryFile.delete();
	}
}
