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

import org.areasy.common.parser.excel.biff.FontRecord;
import org.areasy.common.parser.excel.format.Font;
import org.areasy.common.parser.excel.write.WriteException;

/**
 * A writable Font record.  This class intercepts any set accessor calls
 * and throws and exception if the Font is already initialized
 */
public class WritableFontRecord extends FontRecord
{
	/**
	 * Constructor, used when creating a new font for writing out.
	 *
	 * @param bold the bold indicator
	 * @param ps   the point size
	 * @param us   the underline style
	 * @param fn   the name
	 * @param it   italicised indicator
	 * @param c	the colour
	 * @param ss   the script style
	 */
	protected WritableFontRecord(String fn, int ps, int bold, boolean it,
								 int us, int ci, int ss)
	{
		super(fn, ps, bold, it, us, ci, ss);
	}

	/**
	 * Publicly available copy constructor
	 *
	 * @param the font to copy
	 */
	protected WritableFontRecord(Font f)
	{
		super(f);
	}


	/**
	 * Sets the point size for this font, if the font hasn't been initialized
	 *
	 * @param pointSize the point size
	 * @throws WriteException, if this font is already in use elsewhere
	 */
	protected void setPointSize(int pointSize) throws WriteException
	{
		if (isInitialized())
		{
			throw new ExcelWriteException(ExcelWriteException.formatInitialized);
		}

		super.setFontPointSize(pointSize);
	}

	/**
	 * Sets the bold style for this font, if the font hasn't been initialized
	 *
	 * @param boldStyle the bold style
	 * @throws WriteException, if this font is already in use elsewhere
	 */
	protected void setBoldStyle(int boldStyle) throws WriteException
	{
		if (isInitialized())
		{
			throw new ExcelWriteException(ExcelWriteException.formatInitialized);
		}

		super.setFontBoldStyle(boldStyle);
	}

	/**
	 * Sets the italic indicator for this font, if the font hasn't been
	 * initialized
	 *
	 * @param italic the italic flag
	 * @throws WriteException, if this font is already in use elsewhere
	 */
	protected void setItalic(boolean italic) throws WriteException
	{
		if (isInitialized())
		{
			throw new ExcelWriteException(ExcelWriteException.formatInitialized);
		}

		super.setFontItalic(italic);
	}

	/**
	 * Sets the underline style for this font, if the font hasn't been
	 * initialized
	 *
	 * @param us the underline style
	 * @throws WriteException, if this font is already in use elsewhere
	 */
	protected void setUnderlineStyle(int us) throws WriteException
	{
		if (isInitialized())
		{
			throw new ExcelWriteException(ExcelWriteException.formatInitialized);
		}

		super.setFontUnderlineStyle(us);
	}

	/**
	 * Sets the colour for this font, if the font hasn't been
	 * initialized
	 *
	 * @param colour the colour
	 * @throws WriteException, if this font is already in use elsewhere
	 */
	protected void setColour(int colour) throws WriteException
	{
		if (isInitialized())
		{
			throw new ExcelWriteException(ExcelWriteException.formatInitialized);
		}

		super.setFontColour(colour);
	}

	/**
	 * Sets the script style (eg. superscript, subscript) for this font,
	 * if the font hasn't been initialized
	 *
	 * @param scriptStyle the colour
	 * @throws WriteException, if this font is already in use elsewhere
	 */
	protected void setScriptStyle(int scriptStyle) throws WriteException
	{
		if (isInitialized())
		{
			throw new ExcelWriteException(ExcelWriteException.formatInitialized);
		}

		super.setFontScriptStyle(scriptStyle);
	}

	/**
	 * Sets the struck out flag
	 *
	 * @param so TRUE if the font is struck out, false otherwise
	 * @throws WriteException, if this font is already in use elsewhere
	 */
	protected void setStruckout(boolean os) throws WriteException
	{
		if (isInitialized())
		{
			throw new ExcelWriteException(ExcelWriteException.formatInitialized);
		}
		super.setFontStruckout(os);
	}
}
