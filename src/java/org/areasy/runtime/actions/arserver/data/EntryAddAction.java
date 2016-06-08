package org.areasy.runtime.actions.arserver.data;

/*
 * Copyright (c) 2007-2016 AREasy Runtime
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

import org.areasy.runtime.actions.data.BaseData;
import org.areasy.runtime.engine.RuntimeLogger;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.runtime.engine.services.parser.ParserEngine;
import org.areasy.runtime.engine.structures.CoreItem;
import org.areasy.runtime.engine.structures.MultiPartItem;
import org.areasy.runtime.engine.workflows.ProcessorLevel0Reader;
import org.areasy.common.data.StringUtility;

import java.util.List;

/**
 * Add one entry in form.
 */
public class EntryAddAction extends BaseData
{
	/**
	 * Execute the current action.
	 *
	 * @throws org.areasy.runtime.engine.base.AREasyException
	 * if any error will occur
	 */
	public void run() throws AREasyException
	{
		runner();
	}

	/**
	 * Execute an action for a specific <code>CoreItem</code>. This item must be identified previously and then the method
	 * could be called. This method will used by standard actions which implement an workflow using these type of action
	 * which permit single change or update.
	 *
	 * @param entry <code>CoreItem</code> structure, which should be instantiated.
	 * @throws org.areasy.runtime.engine.base.AREasyException if any error will occur.
	 */
	public void run(CoreItem entry) throws AREasyException
	{
		//data fill-in and create record
		if(!entry.exists())
		{
			setDataFields(entry);
			entry.create(getServerConnection());

			if(getConfiguration().getBoolean("multipart", false) && entry instanceof MultiPartItem)
			{
				//set multipart form names
				setMultiPartForms(entry);

				//execute transactions
				((MultiPartItem)entry).commitParts(getServerConnection(), getMultiPartQueryFields(), getMultiPartDataFields());
			}

			RuntimeLogger.debug("Created data entry: " + entry);

			//execution counter incrementation
			setRecordsCounter();
		}
		else RuntimeLogger.debug("Data entry already exists: " + entry);
	}

	/**
	 * Execute the current action but acting as an iterator into a detasource provided by a file.
	 *
	 * @throws org.areasy.runtime.engine.base.AREasyException
	 *          if any error will occur
	 */
	public void runner() throws AREasyException
	{
		ParserEngine parser = null;
		boolean errorFlag = false;

		getCron().start();

		//check if there are registered data-maps and load them
		setDataMaps();

		boolean force = getConfiguration().getBoolean("force", false);
		String file = getConfiguration().getString("parserfile", getConfiguration().getString("inputfile", getConfiguration().getString("file", null)));

		//read all ignored null field values.
		setIgnoreNullFields();

		//validate and set parsertype parameter
		if((getConfiguration().containsKey("file") || getConfiguration().containsKey("inputfile") || getConfiguration().containsKey("parserfile")) && !getConfiguration().containsKey("parsertype"))
		{
			getConfiguration().setKey("parsertype", "file");
			getConfiguration().setKey("parserfile", file);
			getConfiguration().removeKey("file");
			getConfiguration().removeKey("inputfile");
		}

		if(StringUtility.isNotEmpty(file))
		{
			try
			{
				//initialization
				parser = new ParserEngine(getServerConnection(), getManager().getConfiguration(), getConfiguration());
				parser.init();
			}
			catch(AREasyException are)
			{
				throw are;
			}
			catch(Throwable th)
			{
				getLogger().error("Error reading data source file: " + th.getMessage());
				getLogger().debug("Exception", th);

				throw new AREasyException("Data source is not present or parsing process recorded an error: " + th.getMessage());
			}

			int emptyDataCounter = 0;

			do
			{
				String data[] = null;

				try
				{
					//get data from parser engine
					data = parser.read();

					//validate data
					if(!isDataEmpty(data))
					{
						emptyDataCounter = 0;
						if(logger.isDebugEnabled()) logger.debug("Read data from external source (line " + parser.getParser().getCurrentIndex() + "): " + StringUtility.join(data, ", "));

						for(int i = 0; i < data.length; i++)
						{
							String index = (i + 1) + "";
							String column = ProcessorLevel0Reader.getGenericColumnFromIndex(i);

							getConfiguration().addKey(index, data[i]);
							getConfiguration().addKey(column, data[i]);
						}

						//execute operation
						operation();
					}
					else emptyDataCounter++;
				}
				catch(Throwable th)
				{
					errorFlag = true;
					setErrorsCounter();

					String errorMsg = "Error running action using data extracted from line '" + parser.getParser().getCurrentIndex() + "'";
					errorMsg += ": " + th.getMessage();

					RuntimeLogger.error(errorMsg);

					getLogger().error(errorMsg);
					getLogger().debug("Exception", th);
				}
				finally
				{
					if(data != null)
					{
						for(int i = 0; i < data.length; i++)
						{
							getConfiguration().removeKey( (i + 1) + "" );
							getConfiguration().removeKey( ProcessorLevel0Reader.getGenericColumnFromIndex(i) );
						}
					}
				}

				//set session flags
				errorFlag = RuntimeLogger.hasErrors();
				if(force && errorFlag) errorFlag = false;

				//limitation to 3 empty lines, to cancel the process.
				if(emptyDataCounter >= 3) errorFlag = true;

				// check interruption and and exit if the execution was really interrupted
				if(isInterrupted())
				{
					RuntimeLogger.warn("Execution interrupted by user");
					return;
				}
			}
			while(!errorFlag);
		}
		else if(getConfiguration().getInt("repeat", 0) > 0)
		{
			int counter = getConfiguration().getInt("repeat", 0);
			int sizepad = String.valueOf(counter).length();

			for(int i = 1; i <= counter; i++)
			{
				String index = StringUtility.leftPad(String.valueOf(i), sizepad, '0');
				getConfiguration().setKey("index", index);

				//execute operation
				operation();

				//execution counter incrementation
				setRecordsCounter();
			}
		}
		else operation();

		getCron().stop();
	}

	protected void operation() throws AREasyException
	{
		String form = getConfiguration().getString("form", getConfiguration().getString("formname", null));

		CoreItem entry = getEntity();
		if(form != null) entry.setFormName(form);
			else throw new AREasyException("Form name is null");

		if(getConfiguration().getBoolean("validate", false))
		{
			setQueryFields(entry);
			List list = entry.search(getServerConnection());

			if(list != null && list.size() > 0)
			{
				RuntimeLogger.info("Found " + list.size() + " " + (list.size() > 1 ? "data entries" : "data entry") );
				return;
			}
		}

		//data fill-in and create record
		run(entry);
	}
}