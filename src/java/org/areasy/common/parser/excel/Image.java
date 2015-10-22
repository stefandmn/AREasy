package org.areasy.common.parser.excel;

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

import org.areasy.common.parser.excel.common.LengthUnit;

import java.io.File;

/**
 * Accessor functions for an image
 */
public interface Image
{
	/**
	 * Accessor for the image position
	 *
	 * @return the column number at which the image is positioned
	 */
	public double getColumn();

	/**
	 * Accessor for the image position
	 *
	 * @return the row number at which the image is positioned
	 */
	public double getRow();

	/**
	 * Accessor for the image dimensions
	 *
	 * @return the number of columns this image spans
	 */
	public double getWidth();

	/**
	 * Accessor for the image dimensions
	 *
	 * @return the number of rows which this image spans
	 */
	public double getHeight();

	/**
	 * Accessor for the image file
	 *
	 * @return the file which the image references
	 */
	public File getImageFile();

	/**
	 * Accessor for the image data
	 *
	 * @return the image data
	 */
	public byte[] getImageData();

	/**
	 * Get the width of this image as rendered within Excel
	 *
	 * @param unit the unit of measurement
	 * @return the width of the image within Excel
	 */
	public double getWidth(LengthUnit unit);

	/**
	 * Get the height of this image as rendered within Excel
	 *
	 * @param unit the unit of measurement
	 * @return the height of the image within Excel
	 */
	public double getHeight(LengthUnit unit);

	/**
	 * Gets the width of the image.  Note that this is the width of the
	 * underlying image, and does not take into account any size manipulations
	 * that may have occurred when the image was added into Excel
	 *
	 * @return the image width in pixels
	 */
	public int getImageWidth();

	/**
	 * Gets the height of the image.  Note that this is the height of the
	 * underlying image, and does not take into account any size manipulations
	 * that may have occurred when the image was added into Excel
	 *
	 * @return the image height in pixels
	 */
	public int getImageHeight();

	/**
	 * Gets the horizontal resolution of the image, if that information
	 * is available.
	 *
	 * @return the number of dots per unit specified, if available, 0 otherwise
	 */
	public double getHorizontalResolution(LengthUnit unit);

	/**
	 * Gets the vertical resolution of the image, if that information
	 * is available.
	 *
	 * @return the number of dots per unit specified, if available, 0 otherwise
	 */
	public double getVerticalResolution(LengthUnit unit);
}
