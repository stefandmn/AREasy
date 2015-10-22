package org.areasy.runtime.actions.arserver.data;

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

import com.bmc.arsys.api.CurrencyValue;
import com.bmc.arsys.api.DateInfo;
import com.bmc.arsys.api.Field;
import com.bmc.arsys.api.Timestamp;
import org.areasy.runtime.RuntimeManager;
import org.areasy.runtime.engine.RuntimeLogger;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.runtime.engine.structures.CoreItem;
import org.areasy.runtime.utilities.StreamUtility;
import org.areasy.common.data.DateFormatUtility;
import org.areasy.common.data.NumberUtility;
import org.areasy.common.data.StringEscapeUtility;
import org.areasy.common.data.StringUtility;
import org.areasy.common.data.type.credential.Credential;
import org.areasy.common.parser.csv.CsvWriter;
import org.areasy.common.parser.excel.Workbook;
import org.areasy.common.parser.excel.biff.DisplayFormat;
import org.areasy.common.parser.excel.format.Border;
import org.areasy.common.parser.excel.format.BorderLineStyle;
import org.areasy.common.parser.excel.format.Colour;
import org.areasy.common.parser.excel.write.*;
import org.areasy.common.parser.excel.write.biff.CellValue;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * This is a dedicated action used to export data from found and transformed CoreItem structure(s).
 */
public class DataExportAction extends DataTransferAction
{
	private List<Integer> fields = null;
	private List xmltags = null;
	private Object writer = null;

	private boolean runFirstTime = false;

	/**
	 * Open action's execution (initialize it with local details).
	 *
	 * @throws org.areasy.runtime.engine.base.AREasyException
	 *          if any error will occur
	 */
	public void open() throws AREasyException
	{
		super.open();

		String format = getConfiguration().getString("exportformat", "csv");

		if (StringUtility.equalsIgnoreCase(format, "csv")) writer = getCSVWriter();
			else if (StringUtility.equalsIgnoreCase(format, "xls")) writer = getXLSWriter();
				else if (StringUtility.equalsIgnoreCase(format, "xml")) writer = getXMLWriter();
					else throw new AREasyException("Unknown report format: " + format);
	}

	/**
	 * Execute an action for a specific <code>CoreItem</code>. This item must be identified previously and then the method
	 * could be called. This method will used by standard actions which implement an workflow using these type of action
	 * which permit single change or update.
	 *
	 * @param source	source <code>CoreItem</code> structure
	 * @param target	target <code>CoreItem</code> structure
	 * @param map	   the map structure between source and target
	 * @param operation operation that is asked to be executed
	 * @throws AREasyException in case of any error occurs
	 */
	public void develop(CoreItem source, CoreItem target, Map map, String operation) throws AREasyException
	{
		CoreItem entity = null;

		if (StringUtility.equalsIgnoreCase(getConfiguration().getString("exportentity", "target"), "target")) entity = target;
			else if (StringUtility.equalsIgnoreCase(getConfiguration().getString("exportentity", "target"), "source")) entity = source;
				else throw new AREasyException("Invalid 'exportentity' option! It could be only target|source");

		if(!runFirstTime)
		{
			//get export columns
			setFields(map);

			//define and write header
			List data = getHeader(entity);

			if(data != null)
			{
				if (StringUtility.equalsIgnoreCase(getConfiguration().getString("exportformat", "csv"), "xml"))
				{
					xmltags = new Vector();
					xmltags.addAll(data);
				}
				else addData(data);
			}
			else
			{
				if (StringUtility.equalsIgnoreCase(getConfiguration().getString("exportformat", "csv"), "xml"))
				{
					xmltags = new Vector();
					xmltags.addAll(getSelectedFields());
				}
			}

			//mark first run
			runFirstTime = true;
		}

		//write data
		addData(entity);
	}

	/**
	 * Get the field ids that have to be exported. This method topically is guided by the value of <code>-listcolumns</code> option but also,
	 * it is called even if the mentioned option isn't specified. The specified order  will be considered as columns or entities order.
	 * All specified field ids wills be validated to source or target (depends on <code>-exportentity</code> option value). In case of the specified option
	 * is not declared in the command line it will be considered the list of columns of source or target entity.
	 *
	 * @param map input mapping between source and target.
	 */
	protected void setFields(Map map)
	{
		fields = new Vector<Integer>();
		String listColumns[] = getConfiguration().getStringArray("listcolumns", null);

		if(listColumns != null)
		{
			if(listColumns.length == 1)
			{
				listColumns = StringUtility.split(listColumns[0], ",");
				if(listColumns.length == 1) listColumns = StringUtility.split(listColumns[0], ";");
			}

			for(int i = 0; i < listColumns.length; i++)
			{
				Integer column = new Integer( NumberUtility.toInt(listColumns[i].trim(), 0) );

				if(StringUtility.equalsIgnoreCase(getConfiguration().getString("exportentity", "target"), "target"))
				{
					if(map.containsValue(column)) fields.add(column);
						else RuntimeLogger.debug("Column Id '" + column + "' is ignored because is not defined in the target entity");
				}
				else
				{
					if(map.containsKey(column)) fields.add(column);
						else RuntimeLogger.debug("Column Id '" + column + "' is ignored because is not defined in the source entity");
				}
			}
		}
		else if(listColumns == null && getConfiguration().containsKey("headermaps"))
		{
			String headerMaps[] = getConfiguration().getStringArray("headermaps", null);

			if(headerMaps.length == 1)
			{
				headerMaps = StringUtility.split(headerMaps[0], ",");
				if(headerMaps.length == 1) headerMaps = StringUtility.split(headerMaps[0], ";");
			}

			for(int i = 0; i < headerMaps.length; i++)
			{
				String data[] = StringUtility.split(headerMaps[i], "=");
				Integer column = new Integer( NumberUtility.toInt(data[0].trim()) );

				if(StringUtility.equalsIgnoreCase(getConfiguration().getString("exportentity", "target"), "target"))
				{
					if(map.containsValue(column)) fields.add(column);
						else RuntimeLogger.debug("Column Id '" + column + "' is ignored because is not defined in the target entity");
				}
				else
				{
					if(map.containsKey(column)) fields.add(column);
						else RuntimeLogger.debug("Column Id '" + column + "' is ignored because is not defined in the source entity");
				}
			}
		}
		else
		{
			if(StringUtility.equalsIgnoreCase(getConfiguration().getString("exportentity", "target"), "target")) fields.addAll(map.values());
				else fields.addAll(map.keySet());
		}
	}

	/**
	 * Dedicated method to collect and return report headers.
	 *
	 * @param entity first returned data entity to extract header details
	 * @return the list of report columns that will be considered the header
	 * @throws org.areasy.runtime.engine.base.AREasyException
	 *          if any error will occur
	 */
	protected List getHeader(CoreItem entity) throws AREasyException
	{
		List output = null;
		boolean noHeader = getConfiguration().getBoolean("noheader", false);
		boolean headerFieldIds = getConfiguration().getBoolean("headerfieldids", false);
		boolean headerFieldNames = getConfiguration().getBoolean("headerfieldnames", false);
		boolean headerFieldInfos = getConfiguration().getBoolean("headerfieldinfos", false);
		String headerMaps[] = getConfiguration().getStringArray("headermaps", null);

		//if -noheader is specified then no header will be written in the output file.
		if(noHeader) return output;

		if(!headerFieldInfos && !headerFieldNames && !headerFieldIds && headerMaps == null) headerFieldNames = true;
		else if(headerFieldInfos)
		{
			headerMaps = null;
			headerFieldIds = false;
			headerFieldNames = false;
		}
		else if(headerFieldNames)
		{
			headerMaps = null;
			headerFieldIds = false;
			headerFieldInfos = false;
		}
		else if(headerFieldIds)
		{
			headerMaps = null;
			headerFieldNames = false;
			headerFieldInfos = false;
		}
		else if(headerMaps != null)
		{
			headerFieldIds = false;
			headerFieldNames = false;
			headerFieldInfos = false;
		}

		if(headerFieldIds || headerFieldNames || headerFieldInfos)
		{
			if(getSelectedFields() != null && getSelectedFields().size() > 0) output = new Vector();

			try
			{
				for (int i = 0; getSelectedFields() != null && i < getSelectedFields().size(); i++)
				{
					Integer fieldId = getSelectedFields().get(i);
					Field field = getServerConnection().getContext().getField(entity.getFormName(), fieldId);

					String name = field != null ? field.getName() : String.valueOf(fieldId);
					String id =  field != null ? String.valueOf(field.getFieldID()) : String.valueOf(fieldId);

					if(headerFieldInfos)
					{
						if(StringUtility.equals(name, id)) output.add( name );
							else output.add( name + " - " + id );
					}
					else if(headerFieldNames) output.add( name );
					else if(headerFieldIds) output.add( id );
				}
			}
			catch (Throwable th)
			{
				throw new AREasyException("Report header can not be detected", th);
			}
		}
		else if(headerMaps != null)
		{
			if(headerMaps.length == 1)
			{
				headerMaps = StringUtility.split(headerMaps[0], ",");
				if(headerMaps.length == 1) headerMaps = StringUtility.split(headerMaps[0], ";");
			}

			if(headerMaps != null &&  headerMaps.length > 0)
			{
				output = new Vector();
				List newfields = new Vector();

				for(int i = 0; i < headerMaps.length; i++)
				{
					String data[] = StringUtility.split(headerMaps[i], "=");
					Integer field = new Integer( NumberUtility.toInt(data[0].trim()) );

					if(getSelectedFields().contains(field))
					{
						if(data.length > 1) output.add( data[1].trim() );
							else output.add( data[0].trim() );

						newfields.add(field);
					}
					else RuntimeLogger.debug("Field " + field + " has been skipped because is not specified in the selected fields");
				}

				fields.clear();
				fields.addAll(newfields);
			}
		}

		return output;
	}

	protected CsvWriter getCSVWriter() throws AREasyException
	{
		CsvWriter stream = null;

		File file = getLocalExportFile();

		//remove it the file already exist
		if (file.exists()) file.delete();

		char csvSeparator = getConfiguration().containsKey("csvseparator") ? getConfiguration().getString("csvseparator", ",").trim().charAt(0) : CsvWriter.DEFAULT_SEPARATOR;
		char csvQuote = getConfiguration().containsKey("csvquote") && !getConfiguration().containsKey("csvnoquote") ? getConfiguration().getString("csvquote", "\"").trim().charAt(0) : getConfiguration().containsKey("csvnoquote") && getConfiguration().getBoolean("csvnoquote, false") ? CsvWriter.NO_QUOTE_CHARACTER : CsvWriter.DEFAULT_QUOTE_CHARACTER;

		try
		{
			Writer writer = new FileWriter(file);
			stream = new CsvWriter(writer, csvSeparator, csvQuote);
		}
		catch (Exception e)
		{
			throw new AREasyException("Error creating CSV file: " + e.getMessage(), e);
		}

		return stream;
	}

	protected WritableWorkbook getXLSWriter() throws AREasyException
	{
		WritableWorkbook stream = null;

		File file = getLocalExportFile();

		//remove it the file already exist
		if (file.exists()) file.delete();

		try
		{
			stream = Workbook.createWorkbook(file);
			stream.createSheet("Report", 0);
		}
		catch (Exception e)
		{
			throw new AREasyException("Error creating Excel file: " + e.getMessage(), e);
		}

		return stream;
	}

	protected org.w3c.dom.Document getXMLWriter() throws AREasyException
	{
		org.w3c.dom.Document stream = null;

		try
		{
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			// root elements
			stream = docBuilder.newDocument();

			// create the root element
        	Element root = stream.createElement("data");
			stream.appendChild(root);
		}
		catch (ParserConfigurationException pce)
		{
			throw new AREasyException("Error creating XML file: " + pce.getMessage(), pce);
		}

		return stream;
	}

	protected void addData(List data)
	{
		if(getSelectedWriter() instanceof CsvWriter) addData((CsvWriter) getSelectedWriter(), data);
			else if(getSelectedWriter() instanceof WritableWorkbook) addData( (WritableWorkbook) getSelectedWriter(), data);
	}

	protected void addData(CsvWriter stream, List data)
	{
		String values[] = new String[data.size()];

		try
		{
			for (int i = 0; i < data.size(); i++)
			{
				String key = (String) data.get(i);
				values[i] = key;
			}

			stream.writeNext(values);
			stream.flush();
		}
		catch (Exception e)
		{
			RuntimeLogger.error("Error appending local data into CSV stream: " + e.getMessage());
			logger.debug("Exception", e);
		}
	}

	protected void addData(WritableWorkbook stream, List data)
	{
		WritableSheet sheet = stream.getSheets()[0];
		int row = sheet.getRows();

		try
		{
			for (int i = 0; i < data.size(); i++)
			{
				Object value = data.get(i);
				addCell(sheet, value, i, row, false);
			}
		}
		catch (Exception e)
		{
			RuntimeLogger.error("Error appending local data into XLS stream: " + e.getMessage());
			logger.debug("Exception", e);
		}
	}

	protected void addData(CoreItem entry)
	{
		if(getSelectedWriter() instanceof CsvWriter) addData( (CsvWriter) getSelectedWriter(), entry);
			else if(getSelectedWriter() instanceof WritableWorkbook) addData( (WritableWorkbook) getSelectedWriter(), entry);
				else if(getSelectedWriter() instanceof org.w3c.dom.Document) addData( (org.w3c.dom.Document) getSelectedWriter(), entry);
	}

	protected void addData(CsvWriter stream, CoreItem entry)
	{
		String values[] = new String[getSelectedFields().size()];

		try
		{
			for (int i = 0; i < getSelectedFields().size(); i++)
			{
				Integer key = getSelectedFields().get(i);
				Object value = entry.getAttributeValue(key);

				values[i] = getObject2String(value);
			}

			stream.writeNext(values);
			stream.flush();
		}
		catch (Exception e)
		{
			RuntimeLogger.error("Error appending CoreItem data into CSV stream: " + e.getMessage());
			logger.debug("Exception", e);
		}
	}

	protected void addData(WritableWorkbook stream, CoreItem entry)
	{
		WritableSheet sheet = stream.getSheets()[0];
		int row = sheet.getRows();

		try
		{
			for (int i = 0; i < getSelectedFields().size(); i++)
			{
				Integer key = getSelectedFields().get(i);
				Object value = entry.getAttributeValue(key);

				addCell(sheet, value, i, row, false);
			}
		}
		catch (Exception e)
		{
			RuntimeLogger.error("Error appending CoreItem data into XLS stream: " + e.getMessage());
			logger.debug("Exception", e);
		}
	}

	protected void addData(org.w3c.dom.Document stream, CoreItem entry)
	{
		String xmlnode = getConfiguration().getString("xmlnodetype", "entity");

		try
		{
			Element data = stream.getDocumentElement();
			Element record = stream.createElement("record");
			data.appendChild(record);

			for (int i = 0; i < getSelectedFields().size(); i++)
			{
				Integer key = getSelectedFields().get(i);
				Object object = entry.getAttributeValue(key);

				String value = getObject2String(object);
				String xmltag = xmltags != null && xmltags.size() > i ? (String) xmltags.get(i) : String.valueOf(key);

				if(StringUtility.equalsIgnoreCase(xmlnode, "entity"))
				{
					Element node = stream.createElement(getXmlVariable(xmltag));
					node.appendChild(stream.createTextNode(value));
					record.appendChild(node);
				}
				else if(StringUtility.equalsIgnoreCase(xmlnode, "attribute"))
				{
					Attr attr = stream.createAttribute(getXmlVariable(xmltag));
					attr.setValue(StringEscapeUtility.escapeCsv(value));
					record.setAttributeNode(attr);
				}
			}

		}
		catch (Exception e)
		{
			RuntimeLogger.error("Error appending CoreItem data into XML stream: " + e.getMessage());
			logger.debug("Exception", e);
		}
	}

	private String getObject2String(Object value)
	{
		if (value == null) value = "";

		if (value instanceof CurrencyValue)
		{
			return String.valueOf(((CurrencyValue) value).getValue());
		}
		else if (value instanceof Date)
		{
			return String.valueOf(value);
		}
		else if (value instanceof DateInfo)
		{
			return DateFormatUtility.ISOMT_DATETIME_FORMAT.format(((DateInfo) value).GetDate().getTime());
		}
		else if (value instanceof Timestamp)
		{
			return DateFormatUtility.ISOMT_DATETIME_FORMAT.format(new Date(((Timestamp) value).getValue()));
		}
		else
		{
			return String.valueOf(value);
		}
	}

	private void addCell(WritableSheet sheet, Object value, int col, int row, boolean isSelected) throws org.areasy.common.parser.excel.write.WriteException
	{
		DisplayFormat format = null;

		if (value == null) value = "";

		if (value instanceof Integer)
		{
			org.areasy.common.parser.excel.write.Number number = new org.areasy.common.parser.excel.write.Number(col, row, (Integer) value);
			setCellFormat(number, format, isSelected);
			sheet.addCell(number);
		}
		else if (value instanceof Long)
		{
			org.areasy.common.parser.excel.write.Number number = new org.areasy.common.parser.excel.write.Number(col, row, (Long) value);
			setCellFormat(number, format, isSelected);
			sheet.addCell(number);
		}
		else if (value instanceof Double)
		{
			format = new NumberFormat("#.###");
			org.areasy.common.parser.excel.write.Number number = new org.areasy.common.parser.excel.write.Number(col, row, (Double) value);
			setCellFormat(number, format, isSelected);
			sheet.addCell(number);
		}
		else if (value instanceof BigDecimal)
		{
			format = new NumberFormat("#.##");
			org.areasy.common.parser.excel.write.Number number = new org.areasy.common.parser.excel.write.Number(col, row, ((BigDecimal) value).floatValue());
			setCellFormat(number, format, isSelected);
			sheet.addCell(number);
		}
		else if (value instanceof CurrencyValue)
		{
			format = new NumberFormat("#.##");
			org.areasy.common.parser.excel.write.Number number = new org.areasy.common.parser.excel.write.Number(col, row, ((CurrencyValue) value).getValue().floatValue());
			setCellFormat(number, format, isSelected);
			sheet.addCell(number);
		}
		else if (value instanceof Date)
		{
			format = new DateFormat("MMM/dd/yyyy");
			org.areasy.common.parser.excel.write.DateTime date = new org.areasy.common.parser.excel.write.DateTime(col, row, (Date) value);
			setCellFormat(date, format, isSelected);
			sheet.addCell(date);
		}
		else if (value instanceof DateInfo)
		{
			format = new DateFormat("MMM/dd/yyyy");
			org.areasy.common.parser.excel.write.DateTime date = new org.areasy.common.parser.excel.write.DateTime(col, row, ((DateInfo) value).GetDate().getTime());
			setCellFormat(date, format, isSelected);
			sheet.addCell(date);
		}
		else if (value instanceof Timestamp)
		{
			format = new DateFormat("MMM/dd/yyyy hh:mm:ss");
			org.areasy.common.parser.excel.write.DateTime date = new org.areasy.common.parser.excel.write.DateTime(col, row, new Date(((Timestamp) value).getValue()));
			setCellFormat(date, format, isSelected);
			sheet.addCell(date);
		}
		else if (value instanceof String && NumberUtility.isNumber(String.valueOf(value)))
		{
			org.areasy.common.parser.excel.write.Number number = null;

			if (value instanceof String && String.valueOf(value).contains(".") || String.valueOf(value).contains(",")) number = new org.areasy.common.parser.excel.write.Number(col, row, NumberUtility.toFloat(String.valueOf(value)));
			else number = new org.areasy.common.parser.excel.write.Number(col, row, NumberUtility.toLong(String.valueOf(value)));

			setCellFormat(number, format, isSelected);
			sheet.addCell(number);
		}
		else
		{
			Label label = new Label(col, row, String.valueOf(value));

			setCellFormat(label, format, isSelected);
			sheet.addCell(label);
		}
	}

	private void setCellFormat(CellValue cell, DisplayFormat format, boolean isSelected) throws org.areasy.common.parser.excel.write.WriteException
	{
		WritableCellFormat cellformat = null;

		if (isSelected)
		{
			WritableFont arial12font = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD, false);
			arial12font.setColour(Colour.WHITE);

			if (format != null) cellformat = new WritableCellFormat(arial12font, format);
			else cellformat = new WritableCellFormat(arial12font);

			cellformat.setBackground(Colour.DARK_BLUE);
			cellformat.setBorder(Border.ALL, BorderLineStyle.DASHED);
			cellformat.setWrap(true);
			cellformat.setShrinkToFit(false);

			cell.setCellFormat(cellformat);
		}
		else
		{
			WritableFont arial12font = new WritableFont(WritableFont.ARIAL, 9, WritableFont.NO_BOLD, false);

			if (format != null) cellformat = new WritableCellFormat(arial12font, format);
			else cellformat = new WritableCellFormat(arial12font);

			cellformat.setBorder(Border.ALL, BorderLineStyle.DASHED);
			cellformat.setShrinkToFit(false);
			cellformat.setWrap(true);

			cell.setCellFormat(cellformat);
		}
	}

	/**
	 * Close action's execution.
	 *
	 * @throws org.areasy.runtime.engine.base.AREasyException
	 * if any error will occur
	 */
	public void close() throws AREasyException
	{
		try
		{
			//close writer
			closeWriter();

			boolean uploadit = false;
			File fileOutput = getLocalExportFile();

			if (getConfiguration().containsKey("exportlocation"))
			{
				String location = getConfiguration().getString("exportlocation", null);

				if (StringUtility.isNotEmpty(location))
				{
					if (!location.startsWith("smb://") && !location.contains("@") && !location.startsWith("sftp://") &&
							!StringUtility.equalsIgnoreCase(getConfiguration().getString("exportprotocol", null), "smb") &&
							!StringUtility.equalsIgnoreCase(getConfiguration().getString("exportprotocol", null), "ssh") &&
							!StringUtility.equalsIgnoreCase(getConfiguration().getString("exportprotocol", null), "sftp"))
					{
						File fileLocation = new File(location);

						try
						{
							if (fileLocation.exists() && fileLocation.isFile())
							{
								backupFile(fileLocation);
								StreamUtility.copyFile(fileOutput, fileLocation);
							}
							else if (fileLocation.exists() && fileLocation.isDirectory())
							{
								String name = fileOutput.getName();
								fileLocation = new File(location, name);

								backupFile(fileLocation);
								StreamUtility.copyFile(fileOutput, fileLocation);
							}
							else if (!fileLocation.exists())
							{
								StreamUtility.copyFile(fileOutput, fileLocation);
							}
						}
						catch (IOException ioe)
						{
							throw new AREasyException("Error copying output file to the specified location (" + location + "): " + ioe.getMessage(), ioe);
						}
					}
					else if(StringUtility.equalsIgnoreCase(getConfiguration().getString("exportprotocol", null), "smb") || location.startsWith("smb://"))
					{
						try
						{
							copyToFileOverSMB(fileOutput, location);
						}
						catch (IOException ioe)
						{
							throw new AREasyException("Error copying output file over SMB to the specified location (" + location + "): " + ioe.getMessage(), ioe);
						}
					}
					else if(StringUtility.equalsIgnoreCase(getConfiguration().getString("exportprotocol", null), "ssh") || (location.contains("@") && !location.startsWith("sftp://") && !location.startsWith("smb://")))
					{
						try
						{
							copyToFileOverSSH(fileOutput, location);
						}
						catch (IOException ioe)
						{
							throw new AREasyException("Error copying output file over SSH to the specified location (" + location + "): " + ioe.getMessage(), ioe);
						}
					}
					else if(StringUtility.equalsIgnoreCase(getConfiguration().getString("exportprotocol", null), "sftp") || location.startsWith("sftp://"))
					{
						try
						{
							copyToFileOverSFTP(fileOutput, location);
						}
						catch (IOException ioe)
						{
							throw new AREasyException("Error copying output file over SFTP to the specified location (" + location + "): " + ioe.getMessage(), ioe);
						}
					}
				}
				else uploadit = true;
			}
			else uploadit = true;

			//upload in output log area
			if (uploadit && getJobEntry() != null)
			{
				//append the output to job data history.
				addLoggerOutput("Export Data", fileOutput);
			}
		}
		catch (AREasyException ioe)
		{
			RuntimeLogger.error("Error closing data-export procedure: " + ioe.getMessage());
			if(logger.isDebugEnabled()) logger.debug("Exception", ioe);
		}

		super.close();
	}

	protected void closeWriter() throws AREasyException
	{
		if(getSelectedWriter() instanceof CsvWriter) closeWriter((CsvWriter) getSelectedWriter());
			else if(getSelectedWriter() instanceof WritableWorkbook) closeWriter((WritableWorkbook) getSelectedWriter());
					else if(getSelectedWriter() instanceof org.w3c.dom.Document) closeWriter((org.w3c.dom.Document) getSelectedWriter());
	}

	protected void closeWriter(CsvWriter stream) throws AREasyException
	{
		if (stream != null)
		{
			try
			{
				stream.close();
			}
			catch (IOException ioe)
			{
				throw new AREasyException("Error closing CSV stream: " + ioe.getMessage(), ioe);
			}
		}
	}

	protected void closeWriter(WritableWorkbook stream) throws AREasyException
	{
		if (stream != null)
		{
			try
			{
				stream.write();
				stream.close();
			}
			catch (Exception e)
			{
				throw new AREasyException("Error closing XLS stream: " + e.getMessage(), e);
			}
		}
	}

	protected void closeWriter(org.w3c.dom.Document stream) throws AREasyException
	{
		if (stream != null)
		{
			File file = getLocalExportFile();

			//remove it the file already exist
			if (file.exists()) file.delete();

			try
			{
				// write the content into xml file
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            	transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            	transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            	transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

				DOMSource source = new DOMSource(stream);
				StreamResult result = new StreamResult(file);

				transformer.transform(source, result);
			}
			catch (Exception e)
			{
				throw new AREasyException("Error closing XLS stream: " + e.getMessage(), e);
			}
		}
	}

	/**
	 * Copy a local file to a samba location.
	 *
	 * @param remotePath samba (cifs) location
	 * @param file	   local file (store on local file system)
	 * @throws IOException any I/O error that could occur
	 */
	protected void copyToFileOverSMB(File file, String remotePath) throws IOException
	{
		byte[] buffer = new byte[StreamUtility.BUFFER_SIZE];
		jcifs.smb.SmbFileOutputStream out = null;
		BufferedInputStream in = null;
		int bytesRead;

		String jcifsWins = getConfiguration().getString("smbwins", null);
		String jcifsDomain = getConfiguration().getString("smbdomain", null);
		String jcifsUsername = getConfiguration().getString("smbusername", null);
		String jcifsPassword = getConfiguration().getString("smbpassword", null);

		if (StringUtility.isNotEmpty(jcifsPassword)) jcifsPassword = Credential.getCredential(jcifsPassword).decode();
		if (StringUtility.isNotEmpty(jcifsWins)) jcifs.Config.setProperty("jcifs.netbios.wins", jcifsWins);

		jcifs.smb.NtlmPasswordAuthentication auth = new jcifs.smb.NtlmPasswordAuthentication(jcifsDomain, jcifsUsername, jcifsPassword);

		try
		{
			jcifs.smb.SmbFile fileOut = new jcifs.smb.SmbFile(remotePath, auth);
			if (!fileOut.exists()) fileOut.createNewFile();

			out = new jcifs.smb.SmbFileOutputStream(fileOut);
			in = new BufferedInputStream(new FileInputStream(file));

			while ((bytesRead = in.read(buffer)) != -1)
			{
				out.write(buffer, 0, bytesRead);
			}
		}
		catch (Exception e)
		{
			if (e instanceof IOException) throw (IOException) e;
				else throw new IOException(e);
		}
		finally
		{
			if (in != null)
			{
				try
				{
					in.close();
				}
				catch (Exception ex) { /* nothing to do */ }
			}

			if (out != null)
			{
				try
				{
					out.close();
				}
				catch (Exception ex) { /* nothing to do */ }
			}
		}
	}

	/**
	 * Copy a local file to a ssh/scp location.
	 *
	 * @param remotePath Unix/Linux location over ssh
	 * @param file local file (store on local file system)
	 * @throws IOException any I/O error that could occur
	 */
	protected void copyToFileOverSSH(File file, String remotePath) throws IOException
	{
		int jschPort = getConfiguration().getInt("sshport", 22);
		String jschHost = getConfiguration().getString("sshhost", null);
		String jschUsername = getConfiguration().getString("sshusername", null);
		String jschPassword = getConfiguration().getString("sshpassword", "");

		if (jschUsername == null && remotePath.contains("@")) jschUsername = remotePath.substring(0, remotePath.indexOf("@", 0));
		if (jschHost == null && remotePath.contains("@") && remotePath.contains(":")) jschHost = remotePath.substring(remotePath.indexOf("@", 0) + 1, remotePath.indexOf(":", 0));
		if (StringUtility.isNotEmpty(jschPassword)) jschPassword = Credential.getCredential(jschPassword).decode();

		try
		{
			String rfile = remotePath.contains(":") ? remotePath.substring(remotePath.indexOf(":", 0) + 1) : remotePath;
			com.jcraft.jsch.Session session = (new com.jcraft.jsch.JSch()).getSession(jschUsername, jschHost, jschPort);
			session.setPassword(jschPassword);

			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.connect();

			// exec 'scp -t rfile' remotely
			String command = "scp -p -t " + rfile;
			com.jcraft.jsch.Channel channel = session.openChannel("exec");
			((com.jcraft.jsch.ChannelExec) channel).setCommand(command);

			// Get I/O streams for remote scp
			OutputStream out = channel.getOutputStream();
			InputStream in = channel.getInputStream();
			channel.connect();
			if(checkAck(in) != 0) throw new RuntimeException("Unknown status of SSH communication channel");

			command = "T " + (file.lastModified() / 1000) + " 0";
			command += (" " + (file.lastModified() / 1000) + " 0\n");
			out.write(command.getBytes());
			out.flush();
			if(checkAck(in) != 0) throw new RuntimeException("Unknown status of SSH communication channel");

			// send "C0644 filesize filename", where filename should not include '/'
			long filesize = file.length();
			command = "C0644 " + filesize + " ";

			if (file.getPath().lastIndexOf('/') > 0) command += file.getPath().substring(file.getPath().lastIndexOf('/') + 1);
			else command += file;

			command += "\n";
			out.write(command.getBytes());
			out.flush();
			if(checkAck(in) != 0) throw new RuntimeException("Unknown status of SSH communication channel");

			// send a content of file
			FileInputStream fis = new FileInputStream(file);
			byte[] buf = new byte[1024];

			while (true)
			{
				int len = fis.read(buf, 0, buf.length);
				if (len <= 0) break;
				out.write(buf, 0, len); //out.flush();
			}

			fis.close();
			fis = null;

			// send '\0'
			buf[0] = 0;
			out.write(buf, 0, 1);
			out.flush();
			if(checkAck(in) != 0) throw new RuntimeException("Unknown status of SSH communication channel");
			out.close();

			channel.disconnect();
			session.disconnect();
		}
		catch (com.jcraft.jsch.JSchException jsche)
		{
			throw new IOException(jsche);
		}
	}

	static int checkAck(InputStream in) throws IOException
	{
		int b = in.read();
		// b may be 0 for success,
		// 1 for error,
		// 2 for fatal error,
		// -1
		if (b == 0) return b;
		if (b == -1) return b;

		if (b == 1 || b == 2)
		{
			StringBuffer sb = new StringBuffer();
			int c;

			do
			{
				c = in.read();
				sb.append((char) c);
			}
			while (c != '\n');

			// error
			if (b == 1) throw new RuntimeException("Error checking SSH communication channel: " + sb.toString());

			// fatal error
			if (b == 2) throw new RuntimeException("Fatal error checking SSH communication channel: " + sb.toString());
		}

		return b;
	}

	/**
	 * Copy a local file to a sftp location.
	 *
	 * @param remotePath Unix/Linux location over ssh
	 * @param file local file (store on local file system)
	 * @throws IOException any I/O error that could occur
	 */
	protected void copyToFileOverSFTP(File file, String remotePath) throws IOException
	{
		int jschPort = getConfiguration().getInt("sftpport", 22);
		String jschHost = getConfiguration().getString("sftphost", null);
		String jschUsername = getConfiguration().getString("sftpusername", null);
		String jschPassword = getConfiguration().getString("sftppassword", "");

		if (jschUsername == null && remotePath.contains("@") && remotePath.startsWith("sftp://")) jschUsername = remotePath.substring("sftp://".length(), remotePath.indexOf("@", 0));
		if (jschHost == null && remotePath.contains("@") && remotePath.indexOf(":", remotePath.indexOf("@",  0)) + 1 > 0 && remotePath.startsWith("sftp://")) jschHost = remotePath.substring(remotePath.indexOf("@", 0) + 1, remotePath.indexOf(":", remotePath.indexOf("@", 0)));
		if (StringUtility.isNotEmpty(jschPassword)) jschPassword = Credential.getCredential(jschPassword).decode();

		try
		{
			String rFileName = remotePath.substring("sftp://".length()).contains(":") && remotePath.startsWith("sftp://") ? remotePath.substring(remotePath.indexOf(":", "sftp://".length()) + 1) : remotePath;
			com.jcraft.jsch.Session session = (new com.jcraft.jsch.JSch()).getSession(jschUsername, jschHost, jschPort);
			session.setPassword(jschPassword);

			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.connect();

			// open and SFTP communication channel
			com.jcraft.jsch.Channel channel = session.openChannel("sftp");
			channel.connect();

			com.jcraft.jsch.ChannelSftp channelSftp = (com.jcraft.jsch.ChannelSftp) channel;
			String rParent = null;
			String rName = null;

			if(rFileName.endsWith("/"))
			{
				rParent = rFileName;
				rName = file.getName();
			}
			else
			{
				if(rFileName.lastIndexOf('/') > 0 )
				{
					rName = rFileName.substring(rFileName.lastIndexOf('/') + 1);
					rParent = rFileName.substring(0, rFileName.lastIndexOf('/'));
				}
				else
				{
					rParent = rFileName;
					rName = file.getName();
				}
			}

			channelSftp.cd(rParent);
			channelSftp.put(new FileInputStream(file), rName);

			channel.disconnect();
			session.disconnect();
		}
		catch (com.jcraft.jsch.JSchException jsche)
		{
			throw new IOException(jsche);
		}
		catch (com.jcraft.jsch.SftpException jsftp)
		{
			throw new IOException(jsftp);
		}
	}

	/**
	 * Get a help text about syntax execution of the current action.
	 *
	 * @return text message specifying the syntax of the current action
	 */
	public String help()
	{
		return help(null, null, null);
	}

	public List<Integer> getSelectedFields()
	{
		return fields;
	}

	public Object getSelectedWriter()
	{
		return writer;
	}

	private boolean backupFile(File fileLocation)
	{
		boolean renamed = false;

		if (fileLocation.exists() && fileLocation.isFile())
		{
			int index = 0;
			boolean found = false;

			while (!found)
			{
				String backupName = fileLocation.getAbsolutePath() + ".bak" + index;
				File fileBak = new File(backupName);

				if (!fileBak.exists())
				{
					renamed = fileLocation.renameTo(fileBak);
					found = true;
				}
				else index++;
			}
		}
		else renamed = true;

		return renamed;
	}

	protected File getLocalExportFile()
	{
		String localFileName = getConfiguration().getString("exportinternalfilename", "export" + "-" + Thread.currentThread().getName());
		String localFileExt = "." + getConfiguration().getString("exportformat", "csv");

		if(!localFileName.endsWith(localFileExt)) localFileName += localFileExt;
		String localFilePath = RuntimeManager.getWorkingDirectory() + File.separator + localFileName;

		return new File(localFilePath);
	}

	private String getXmlVariable(String text)
	{
		if(text != null)
		{
			if(NumberUtility.isNumber(text)) text = FDATA + text;
			else
			{
				text = StringUtility.replace(text, " - ", "_");
				text = StringUtility.escapeSpecialChars(text);
			}
		}

		return text;
	}
}
