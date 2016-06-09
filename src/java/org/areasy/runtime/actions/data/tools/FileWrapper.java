package org.areasy.runtime.actions.data.tools;

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
import org.areasy.runtime.actions.data.BaseData;
import org.areasy.runtime.engine.RuntimeLogger;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.runtime.engine.services.parser.ParserEngine;
import org.areasy.runtime.engine.services.status.BaseStatus;
import org.areasy.runtime.engine.structures.CoreItem;
import org.areasy.runtime.engine.workflows.ProcessorLevel0Reader;
import org.areasy.common.data.StopWatchUtility;
import org.areasy.common.data.StringUtility;

/**
 * Wrapper data processor action.
 *
 * This action will allow you to run a normal action (command) but with the input from a file
 */
public class FileWrapper extends BaseData
{
	/**
	 * Execute the current action.
	 *
	 * @throws org.areasy.runtime.engine.base.AREasyException
	 * if any error will occur
	 */
	public void run() throws AREasyException
	{
		/** The delegated action to be executed **/
		RuntimeAction command;
		boolean errorFlag = false;
		ParserEngine parser = null;

		String file = getConfiguration().getString("parserfile", getConfiguration().getString("inputfile", getConfiguration().getString("file", null)));
		String call = getConfiguration().getString("call", getConfiguration().getString("subaction", null));

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
						if((command instanceof BaseData) && ((BaseData)command).getRecordsCounter() > 0) addRecordsCounter( ((BaseData)command).getRecordsCounter() );
							else setRecordsCounter();
					}
					else emptyDataCounter++;
				}
				catch(Throwable th)
				{
					errorFlag = true;
					if((command instanceof BaseData) && ((BaseData)command).getErrorsCounter() > 0) addErrorsCounter( ((BaseData)command).getErrorsCounter() );
						else setErrorsCounter();

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
				if(isForced() && errorFlag) errorFlag = false;

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

	public void run(CoreItem item) throws AREasyException
	{
		//nothing to do here
	}
}