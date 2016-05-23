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

import org.areasy.runtime.RuntimeAction;
import org.areasy.runtime.actions.AbstractAction;
import org.areasy.runtime.engine.RuntimeLogger;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.runtime.engine.services.parser.ParserEngine;
import org.areasy.runtime.engine.services.status.BaseStatus;
import org.areasy.runtime.engine.structures.CoreItem;
import org.areasy.runtime.engine.structures.MultiPartItem;
import org.areasy.runtime.engine.workflows.ProcessorLevel0Reader;
import org.areasy.common.data.NumberUtility;
import org.areasy.common.data.StopWatchUtility;
import org.areasy.common.data.StringUtility;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

/**
 * Wrapper data processor action.
 *
 * This action will allow you to run a normal action (command) but with the input from a file
 */
public class WrapperFileAction extends AbstractAction
{
	protected static final String FQUERY = "Q";
	protected static final String FDATA = "D";

	private int recordsCounter = 0;
	private int errorsCounter = 0;

	private StopWatchUtility cron = new StopWatchUtility();

	/**
	 * Execute the current action.
	 *
	 * @throws org.areasy.runtime.engine.base.AREasyException
	 * if any error will occur
	 */
	public void run() throws AREasyException
	{
		/** The delegated action to be executed **/
		ParserEngine parser = null;
		RuntimeAction command;
		boolean errorFlag = false;

		String file = getConfiguration().getString("parserfile", getConfiguration().getString("inputfile", getConfiguration().getString("file", null)));
		String call = getConfiguration().getString("call", getConfiguration().getString("subaction", getConfiguration().getString("command", null)));
		boolean force = getConfiguration().getBoolean("force", false);

		//validate and set parser-type parameter
		if((getConfiguration().containsKey("file") || getConfiguration().containsKey("inputfile") || getConfiguration().containsKey("parserfile")) && !getConfiguration().containsKey("parsertype"))
		{
			getConfiguration().setKey("parsertype", "file");
			getConfiguration().setKey("parserfile", file);
			getConfiguration().removeKey("file");
			getConfiguration().removeKey("inputfile");
		}

		if(StringUtility.isEmpty(call)) throw new AREasyException("Command action is null");
		else
		{
			command = getManager().getRuntimeAction(call);
			RuntimeLogger.info("Wrapper processor runs '" + command.getCode() + "' action.");
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

			getCron().start();

			int emptyDataCounter = 0;
			
			int indexLimit = 0;
			int maxLimit = getConfiguration().getInt("limit", 0);
			
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

						//initialize and execute comand operation
						command.init(this);
						command.run();

						//execution counter incrementation
						setRecordsCounter();
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

				//limitation to 10 empty lines, to cancel the process.
				if(emptyDataCounter >= 3) errorFlag = true;
				
				//close loop evaluation
				indexLimit++;
				if(maxLimit > 0) errorFlag = indexLimit >= maxLimit;

				// check interruption and and exit if the execution was really interrupted
				if(isInterrupted())
				{
					RuntimeLogger.warn("Execution interrupted by user");
					return;
				}

				if(getConfiguration().containsKey("sleep") && !errorFlag)
				{
					try
					{
						int sleep = getConfiguration().getInt("sleep", 1000);
						if(sleep < 1000) sleep *= 1000;

						Thread.sleep(sleep);
					}
					catch(InterruptedException e) { /** nothing to do */ }
				}
			}
			while(!errorFlag);

			getCron().stop();
		}
		else
		{
			try
			{
				//execute operation
				command.run();
			}
			catch(Throwable th)
			{
				String error = "Error running action '" + command.getCode() + "'";
				error += ": " + th.getMessage();

				RuntimeLogger.error(error);

				getLogger().error(error);
				getLogger().debug("Exception", th);
			}
		}
	}

	protected Map getDataFields() throws AREasyException
	{
		boolean multipart = getConfiguration().getBoolean("multipart", false);
		Iterator ids = getConfiguration().getKeys();
		Map map = new Hashtable();

		while(ids != null && ids.hasNext())
		{
			String id = (String)ids.next();
			if(id != null && id.startsWith(FDATA))
			{
				if(NumberUtility.isNumber(id.substring(1)) || multipart) map.put(id.substring(1), getConfiguration().getString(id, null));
			}
		}

		return map;
	}

	protected void setDataFields(CoreItem entry) throws AREasyException
	{
		boolean multipart = getConfiguration().getBoolean("multipart", false);
		Iterator ids = getConfiguration().getKeys();

		while(ids != null && ids.hasNext())
		{
			String id = (String)ids.next();
			if(id != null && id.startsWith(FDATA))
			{
				if(NumberUtility.isNumber(id.substring(1)) || multipart) entry.setAttribute(id.substring(1), getConfiguration().getString(id, null));
			}
		}
	}

	protected void setQueryFields(CoreItem entry) throws AREasyException
	{
		boolean multipart = getConfiguration().getBoolean("multipart", false);
		Iterator ids = getConfiguration().getKeys();

		while(ids != null && ids.hasNext())
		{
			String id = (String)ids.next();
			if(id != null && id.startsWith(FQUERY))
			{
				if(NumberUtility.isNumber(id.substring(1)) || multipart) entry.setAttribute(id.substring(1), getConfiguration().getString(id, null));
			}
		}
	}

	protected void setPartForms(CoreItem entry)
	{
		if(getConfiguration().getBoolean("multipart", false))
		{
			String partformsStr = getConfiguration().getString("partforms", null);

			if(partformsStr != null)
			{
				String partforms[] = StringUtility.split(partformsStr, ';');

				for(int i = 0; i < partforms.length; i++)
				{
					int index = partforms[i].indexOf(MultiPartItem.partSeparator, 0);
					if(index > 0) entry.setFormName(partforms[i]);
				}
			}
		}
	}

	protected boolean isDataEmpty(String[] data)
	{
		if(data == null || data.length == 0) return true;
		else
		{
			boolean empty = true;

			for (int i = 0; empty && i < data.length; i++)
			{
				empty = StringUtility.isEmpty(data[i]);
			}

			return empty;
		}
	}

	public int setRecordsCounter()
	{
		return this.recordsCounter++;
	}

	public int setErrorsCounter()
	{
		return this.errorsCounter++;
	}

	public int getRecordsCounter()
	{
		return this.recordsCounter;
	}

	public int getErrorsCounter()
	{
		return this.errorsCounter;
	}

	@Override
	public BaseStatus getCurrentStatus()
	{
		return new WrapperDataStatus(this);
	}

	/**
	 * Get a help text about syntaxt execution of the current action.
	 *
	 * @return text message specifying the syntaxt of the current action
	 */
	@Override
	public String help()
	{
		return "-form <form name> -id <entry id> [-Q10001 <value 1> -Q1000N <value N>] -D10001 <value 1> -D1000N <value N> [-validate] [-file <file path> -startindex <start index> [-endindex <end index>] [-pageindex <page> -force]] [-multipart] -[partforms <par1@formname1;part2@formname2;..;partx@formnamex>] [-entity <company|organisation|site|people|sgroup|productc|operationalc>]";
	}

	protected StopWatchUtility getCron()
	{
		return cron;
	}

	protected String getCronTime()
	{
		return cron.toString();
	}

	/**
	 * To change this template use File | Settings | File Templates.
	 */
	public class WrapperDataStatus extends BaseStatus
	{
		WrapperFileAction action;

		public WrapperDataStatus(WrapperFileAction action)
		{
			this.action = action;
		}

		protected String getMessage()
		{
			String message;

			if (action.getRecordsCounter() > 0)
			{
				message = "Wrapper action processed " + action.getRecordsCounter() + " records";

				if (action.getErrorsCounter() > 0) message += " and discovered " + action.getErrorsCounter() + " errors in " + getCronTime();
					else message += " without errors, in " + getCronTime();
			}
			else message = "Wrapper action didn't start to process data-source records";

			return message;
		}
	}
}