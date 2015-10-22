package org.areasy.common.parser.excel.write.biff;

/*
 * Copyright (c) 2007-2015 AREasy Runtime
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

import java.io.IOException;
import java.io.OutputStream;

/**
 * Interface to abstract away an in-memory output or a temporary file
 * output.  Used by the File object
 */
interface ExcelDataOutput
{
	/**
	 * Appends the bytes to the end of the output
	 *
	 * @param d the data to write to the end of the array
	 */
	public void write(byte[] bytes) throws IOException;

	/**
	 * Gets the current position within the file
	 *
	 * @return the position within the file
	 */
	public int getPosition() throws IOException;

	/**
	 * Sets the data at the specified position to the contents of the array
	 *
	 * @param pos	 the position to alter
	 * @param newdata the data to modify
	 */
	public void setData(byte[] newdata, int pos) throws IOException;

	/**
	 * Writes the data to the output stream
	 */
	public void writeData(OutputStream out) throws IOException;

	/**
	 * Called when the final compound file has been written
	 */
	public void close() throws IOException;
}
