package org.areasy.common.parser.excel.read.biff;

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
import org.areasy.common.parser.excel.biff.DoubleHelper;
import org.areasy.common.parser.excel.biff.IntegerHelper;
import org.areasy.common.parser.excel.biff.RecordData;
import org.areasy.common.parser.excel.biff.Type;

/**
 * Contains the page set up for a sheet
 */
public class SetupRecord extends RecordData
{
	// The logger
	private static Logger logger = LoggerFactory.getLog(SetupRecord.class);

	/**
	 * The raw data
	 */
	private byte[] data;

	/**
	 * The orientation flag
	 */
	private boolean portraitOrientation;

	/**
	 * The Page Order flag
	 */
	private boolean pageOrder;

	/**
	 * The header margin
	 */
	private double headerMargin;

	/**
	 * The footer margin
	 */
	private double footerMargin;

	/**
	 * The paper size
	 */
	private int paperSize;

	/**
	 * The scale factor
	 */
	private int scaleFactor;

	/**
	 * The page start
	 */
	private int pageStart;

	/**
	 * The fit width
	 */
	private int fitWidth;

	/**
	 * The fit height
	 */
	private int fitHeight;

	/**
	 * The horizontal print resolution
	 */
	private int horizontalPrintResolution;

	/**
	 * The vertical print resolution
	 */
	private int verticalPrintResolution;

	/**
	 * The number of copies
	 */
	private int copies;

	/**
	 * Indicates whether the setup data should be  initiliazed in the setup
	 * box
	 */
	private boolean initialized;

	/**
	 * Constructor which creates this object from the binary data
	 *
	 * @param t the record
	 */
	SetupRecord(Record t)
	{
		super(Type.SETUP);

		data = t.getData();

		paperSize = IntegerHelper.getInt(data[0], data[1]);
		scaleFactor = IntegerHelper.getInt(data[2], data[3]);
		pageStart = IntegerHelper.getInt(data[4], data[5]);
		fitWidth = IntegerHelper.getInt(data[6], data[7]);
		fitHeight = IntegerHelper.getInt(data[8], data[9]);
		horizontalPrintResolution = IntegerHelper.getInt(data[12], data[13]);
		verticalPrintResolution = IntegerHelper.getInt(data[14], data[15]);
		copies = IntegerHelper.getInt(data[32], data[33]);

		headerMargin = DoubleHelper.getIEEEDouble(data, 16);
		footerMargin = DoubleHelper.getIEEEDouble(data, 24);


		int grbit = IntegerHelper.getInt(data[10], data[11]);
		pageOrder = ((grbit & 0x01) != 0);
		portraitOrientation = ((grbit & 0x02) != 0);
		initialized = ((grbit & 0x04) == 0);
	}

	/**
	 * Accessor for the orientation.  Called when copying sheets
	 *
	 * @return TRUE if the orientation is portrait, FALSE if it is landscape
	 */
	public boolean isPortrait()
	{
		return portraitOrientation;
	}


	/**
	 * Accessor for the page order. Called when copying sheets
	 *
	 * @return TRUE if the page order is Left to Right, then Down, otherwise
	 *         FALSE
	 */
	public boolean isRightDown()
	{
		return pageOrder;
	}

	/**
	 * Accessor for the header.  Called when copying sheets
	 *
	 * @return the header margin
	 */
	public double getHeaderMargin()
	{
		return headerMargin;
	}

	/**
	 * Accessor for the footer.  Called when copying sheets
	 *
	 * @return the footer margin
	 */
	public double getFooterMargin()
	{
		return footerMargin;
	}

	/**
	 * Accessor for the paper size.  Called when copying sheets
	 *
	 * @return the footer margin
	 */
	public int getPaperSize()
	{
		return paperSize;
	}

	/**
	 * Accessor for the scale factor.  Called when copying sheets
	 *
	 * @return the scale factor
	 */
	public int getScaleFactor()
	{
		return scaleFactor;
	}

	/**
	 * Accessor for the page height.  called when copying sheets
	 *
	 * @return the page to start printing at
	 */
	public int getPageStart()
	{
		return pageStart;
	}

	/**
	 * Accessor for the fit width.  Called when copying sheets
	 *
	 * @return the fit width
	 */
	public int getFitWidth()
	{
		return fitWidth;
	}

	/**
	 * Accessor for the fit height.  Called when copying sheets
	 *
	 * @return the fit height
	 */
	public int getFitHeight()
	{
		return fitHeight;
	}

	/**
	 * The horizontal print resolution.  Called when copying sheets
	 *
	 * @return the horizontal print resolution
	 */
	public int getHorizontalPrintResolution()
	{
		return horizontalPrintResolution;
	}

	/**
	 * Accessor for the vertical print resolution.  Called when copying sheets
	 *
	 * @return an vertical print resolution
	 */
	public int getVerticalPrintResolution()
	{
		return verticalPrintResolution;
	}

	/**
	 * Accessor for the number of copies
	 *
	 * @return the number of copies
	 */
	public int getCopies()
	{
		return copies;
	}

	/**
	 * Accessor for the initialized flag
	 *
	 * @return whether the print page setup should be initialized in the dialog
	 *         box
	 */
	public boolean getInitialized()
	{
		return initialized;
	}

}
