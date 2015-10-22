package org.areasy.common.doclet.document.tags;

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

import com.lowagie.text.Element;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import org.areasy.common.data.NumberUtility;
import org.areasy.common.doclet.document.elements.TableParagraph;

import java.util.List;
import java.util.Properties;
import java.util.Vector;

/**
 * TagTABLE creates an iText PdfPTable instance and returns it as the only
 * content.  The content is made up of PdfPCell instances
 * created by TagCELL, and put in rows by TagROW.
 *
 * @version $Id: TagTABLE.java,v 1.2 2008/05/14 09:36:47 swd\stefan.damian Exp $
 */
public class TagTABLE extends HtmlTag
{
	/**
	 * Reference to the PdfPTable instance.
	 */
	private PdfPTable table;

	/**
	 * Flag which defines if the table has a header row.
	 */
	private boolean hasHeaders = false;

	/**
	 * Creats a HTML table object.
	 *
	 * @param parent The parent HTML tag.
	 * @param type   The tag type.
	 */
	public TagTABLE(HtmlTag parent, int type)
	{
		super(parent, type);
	}

	private PdfPCell[] getRow(Element[] elements)
	{
		if (elements == null || elements.length == 0) return null;

		PdfPCell[] row = new PdfPCell[elements.length];
		for (int i = 0; i < elements.length; i++)
		{
			if (!(elements[i] instanceof PdfPCell)) return null;

			row[i] = (PdfPCell) elements[i];
		}

		return row;
	}

	private boolean cellHasAttribute(PdfPCell cell, String attr)
	{
		Properties props = cell.getMarkupAttributes();
		return (props == null) ? false : props.containsKey(attr);
	}

	private int getNumColumns(PdfPCell[] row)
	{
		int cols = 0;
		for (int i = 0; i < row.length; i++)
		{
			/* THEAD tag may cause multiple rows to be combined; check for TagROW indicator */
			if (i > 0 && cellHasAttribute(row[i], TagTR.ROW_START_ATTR)) break;

			cols += row[i].getColspan();
		}
		return cols;
	}

	private void createTable(int numcols)
	{
		table = new PdfPTable(numcols);

		String width = getAttribute("width");
		if (width == null) table.setWidthPercentage(100);
			else if (width.endsWith("%")) table.setWidthPercentage(HtmlTagUtility.parseFloat(width, 100f));
				else table.setTotalWidth(HtmlTagUtility.parseFloat(width, 400f));

		table.getDefaultCell().setPadding(HtmlTagUtility.parseFloat(getAttribute("cellpadding"), 2.0f));
		table.getDefaultCell().setBackgroundColor(HtmlTagUtility.getColor(getAttribute("bgcolor")));
		table.setHorizontalAlignment(HtmlTagUtility.getAlignment(getAttribute("align"), Element.ALIGN_CENTER));

		/* Border doesn't have to have a value set */
		if (getAttribute("border") != null)
		{
			table.getDefaultCell().setBorder(Rectangle.BOX);
			table.getDefaultCell().setBorderWidth(HtmlTagUtility.parseFloat(getAttribute("border"), 1.0f));
			table.getDefaultCell().setBorderColor(HtmlTagUtility.getColor("gray"));
		}
		else
		{
			table.getDefaultCell().setBorder(Rectangle.NO_BORDER);
			table.getDefaultCell().setBorderWidth(0.0f);
		}

		//set width data for columns.
		setTableWidthsForCells();
	}

	private void setTableWidthsForCells()
	{
		//set width data for columns.
		try
		{
			List rows = getContentTags();
			if(rows != null && rows.size() > 0)
			{
				List values = new Vector();
				for(int i = 0; i < rows.size(); i++)
				{
					HtmlTag row = (HtmlTag) rows.get(i);
					List columns = row.getContentTags();

					for(int j = 0; j < columns.size(); j++)
					{
						float actual = 0f;
						Object object = null;

						if(!values.isEmpty() && j < values.size()) object = values.get(j);
						if(object != null) actual = ((Float)object).floatValue();

						float found = 0f;
						HtmlTag column = (HtmlTag) columns.get(j);
						String attribute = column.getAttribute("width");

						if(attribute != null && attribute.endsWith("%")) found = HtmlTagUtility.parseFloat(attribute.substring(0, attribute.length() - 1), 100f);
							else found = HtmlTagUtility.parseFloat(attribute, 0);

						Float calculated = new Float(Math.max(actual, found));

						if(i == 0) values.add(calculated);
							else if(j < values.size()) values.set(j, calculated);
								else values.add(calculated);
					}
				}

				if(!values.isEmpty())
				{
					Float floats[] = (Float[]) values.toArray(new Float[values.size()]);
					float widths[] = new float[floats.length];

					for(int i = 0; i < widths.length; i++)
					{
						widths[i] = floats[i].floatValue();
						if(i > 0 && widths[i] == 0) widths[i] = NumberUtility.max(widths);
					}

					//set table
					if(NumberUtility.max(widths) > 0) table.setWidths(widths);
				}
			}
		}
		catch(Exception e)
		{
			log.warn("Can not set width array for cells: " + e.getMessage());
		}
	}

	private boolean isHeader(PdfPCell cell)
	{
		return cellHasAttribute(cell, TagTD.HEADER_INDICATOR_ATTR);
	}

	private int getCurrCol(PdfPTable table)
	{
		return new IndexAccess(table).getNextColumnIndex();
	}

	public void addNestedTagContent(Element[] elements)
	{
		PdfPCell[] row = getRow(elements);

		// If elements is a row of Cells, add it to the table

		if (row != null)
		{
			if (table == null)
			{
				createTable(getNumColumns(row));
				hasHeaders = isHeader(row[0]);
			}
			else if (!isHeader(row[0]) && hasHeaders)
			{
				table.setHeaderRows(table.size());
				hasHeaders = false;
			}

			for (int i = 0; i < row.length; i++)
			{
				row[i].setBorderWidth(table.getDefaultCell().borderWidth());
				row[i].setBorderColor(table.getDefaultCell().borderColor());
				row[i].setBorder(table.getDefaultCell().border());
				row[i].setPadding(table.getDefaultCell().getPaddingLeft());

				table.addCell(row[i]);
			}

			/* If we haven't finished a row, fill it out.  Not sure this is a good idea. */
			int col = getCurrCol(table);
			int expectedCols = table.getAbsoluteWidths().length;
			if (col > 0)
			{
				for (int i = col; i < expectedCols; i++)
				{
					table.addCell(new PdfPCell(new Phrase()));
				}
			}
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see com.snt.common.doclet.document.tags.HTMLTag#toElement(java.lang.String)
	 */
	public Element toElement(String text)
	{
		return null;
	}

	/*
	 *  (non-Javadoc)
	 * @see com.snt.common.doclet.document.tags.HTMLTag#openTagElements()
	 */
	public Element[] openTagElements()
	{
		table = null;
		Element[] elements = new Element[1];
		elements[0] = new Paragraph((float) 8.0, " ");

		return elements;
	}

	public Element[] closeTagElements()
	{
		if (table == null) createTable(1);

		return new Element[]{new TableParagraph(table)};
	}

	private class IndexAccess extends PdfPTable
	{
		IndexAccess(PdfPTable table)
		{
			super(table);
		}

		int getNextColumnIndex()
		{
			return this.currentRowIdx;
		}
	}

	PdfPTable getTable()
	{
		return table;
	}
}
