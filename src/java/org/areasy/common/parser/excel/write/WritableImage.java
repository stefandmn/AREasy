package org.areasy.common.parser.excel.write;

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

import org.areasy.common.parser.excel.biff.drawing.Drawing;
import org.areasy.common.parser.excel.biff.drawing.DrawingGroup;
import org.areasy.common.parser.excel.biff.drawing.DrawingGroupObject;

import java.io.File;

/**
 * Allows an image to be created, or an existing image to be manipulated
 * Note that co-ordinates and dimensions are given in cells, so that if for
 * example the width or height of a cell which the image spans is altered,
 * the image will have a correspondign distortion
 */
public class WritableImage extends Drawing
{
	// Shadow these values from the superclass.  The only practical reason
	// for doing this is that they  appear nicely in the javadoc

	/**
	 * Image anchor properties which will move and resize an image
	 * along with the cells
	 */
	public static ImageAnchorProperties MOVE_AND_SIZE_WITH_CELLS =
			Drawing.MOVE_AND_SIZE_WITH_CELLS;

	/**
	 * Image anchor properties which will move an image
	 * when cells are inserted or deleted
	 */
	public static ImageAnchorProperties MOVE_WITH_CELLS =
			Drawing.MOVE_WITH_CELLS;

	/**
	 * Image anchor properties which will leave an image unaffected when
	 * other cells are inserted, removed or resized
	 */
	public static ImageAnchorProperties NO_MOVE_OR_SIZE_WITH_CELLS =
			Drawing.NO_MOVE_OR_SIZE_WITH_CELLS;

	/**
	 * Constructor
	 *
	 * @param x	  the column number at which to position the image
	 * @param y	  the row number at which to position the image
	 * @param width  the number of columns cells which the image spans
	 * @param height the number of rows which the image spans
	 * @param image  the source image file
	 */
	public WritableImage(double x, double y,
						 double width, double height,
						 File image)
	{
		super(x, y, width, height, image);
	}

	/**
	 * Constructor
	 *
	 * @param x		 the column number at which to position the image
	 * @param y		 the row number at which to position the image
	 * @param width	 the number of columns cells which the image spans
	 * @param height	the number of rows which the image spans
	 * @param imageData the image data
	 */
	public WritableImage(double x,
						 double y,
						 double width,
						 double height,
						 byte[] imageData)
	{
		super(x, y, width, height, imageData);
	}

	/**
	 * Constructor, used when copying sheets
	 *
	 * @param d  the image to copy
	 * @param dg the drawing group
	 */
	public WritableImage(DrawingGroupObject d, DrawingGroup dg)
	{
		super(d, dg);
	}

	/**
	 * Accessor for the image position
	 *
	 * @return the column number at which the image is positioned
	 */
	public double getColumn()
	{
		return super.getX();
	}

	/**
	 * Accessor for the image position
	 *
	 * @param c the column number at which the image should be positioned
	 */
	public void setColumn(double c)
	{
		super.setX(c);
	}

	/**
	 * Accessor for the image position
	 *
	 * @return the row number at which the image is positions
	 */
	public double getRow()
	{
		return super.getY();
	}

	/**
	 * Accessor for the image position
	 *
	 * @param c the row number at which the image should be positioned
	 */
	public void setRow(double c)
	{
		super.setY(c);
	}

	/**
	 * Accessor for the image dimensions
	 *
	 * @return the number of columns this image spans
	 */
	public double getWidth()
	{
		return super.getWidth();
	}

	/**
	 * Accessor for the image dimensions
	 * Note that the actual size of the rendered image will depend on the
	 * width of the columns it spans
	 *
	 * @param c the number of columns which this image spans
	 */
	public void setWidth(double c)
	{
		super.setWidth(c);
	}

	/**
	 * Accessor for the image dimensions
	 *
	 * @return the number of rows which this image spans
	 */
	public double getHeight()
	{
		return super.getHeight();
	}

	/**
	 * Accessor for the image dimensions
	 * Note that the actual size of the rendered image will depend on the
	 * height of the rows it spans
	 *
	 * @param c the number of rows which this image should span
	 */
	public void setHeight(double c)
	{
		super.setHeight(c);
	}

	/**
	 * Accessor for the image file
	 *
	 * @return the file which the image references
	 */
	public File getImageFile()
	{
		return super.getImageFile();
	}

	/**
	 * Accessor for the image data
	 *
	 * @return the image data
	 */
	public byte[] getImageData()
	{
		return super.getImageData();
	}

	/**
	 * Accessor for the anchor properties
	 */
	public void setImageAnchor(ImageAnchorProperties iap)
	{
		super.setImageAnchor(iap);
	}

	/**
	 * Accessor for the anchor properties
	 */
	public ImageAnchorProperties getImageAnchor()
	{
		return super.getImageAnchor();
	}
}
