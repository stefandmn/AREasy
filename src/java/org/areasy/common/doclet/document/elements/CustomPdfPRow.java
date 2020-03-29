package org.areasy.common.doclet.document.elements;

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

import com.lowagie.text.Cell;
import com.lowagie.text.Phrase;
import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;


/**
 * Wraps the PdfPRow object to provide an
 * additional method for querying the
 * number of columns of this row.
 *
 * @version $Id: CustomPdfPRow.java,v 1.2 2008/05/14 09:36:47 swd\stefan.damian Exp $
 */
public class CustomPdfPRow extends Phrase
{

	/**
	 * Logger reference
	 */
	private static Logger log = LoggerFactory.getLog(CustomPdfPRow.class);

	/**
	 * Stores the number of columns in this row.
	 */
	private int columns = 0;

	private Cell[] cells = new Cell[0];

	/**
	 * Creates a row with a certain number of cells.
	 *
	 * @param cells The cells contained in this row.
	 */
	public CustomPdfPRow(Cell[] cells)
	{
		super();
		
		this.cells = cells;
		columns = cells.length;
	}

	/**
	 * Returns the cells in this row.
	 *
	 * @return The cells in this row.
	 */
	public Cell[] getCells()
	{
		return cells;
	}

	/**
	 * Returns the number of columns of this row.
	 *
	 * @return The number of columns (cells).
	 */
	public int getColumns()
	{
		return columns;
	}
}
