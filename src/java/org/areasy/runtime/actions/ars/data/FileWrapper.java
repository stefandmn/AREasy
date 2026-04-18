package org.areasy.runtime.actions.ars.data;

/*
 *  Copyright (c) 2007-2023 AREasy Runtime
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

import org.areasy.common.data.StringUtility;
import org.areasy.runtime.RuntimeAction;
import org.areasy.runtime.engine.RuntimeLogger;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.runtime.engine.services.parser.ParserEngine;
import org.areasy.runtime.engine.structures.CoreItem;
import org.areasy.runtime.engine.workflows.ProcessorLevel0Reader;

/**
 * Wrapper data processor action.
 *
 * This action will allow you to run a normal action (command) but with the input from a file
 */
public class FileWrapper extends BaseData
{
	private boolean reportdata = false;
	private boolean reporterrors = false;
	private String reportheader[] = null;

	/**
	 * Execute the current action.
	 *
	 * @throws org.areasy.runtime.engine.base.AREasyException
	 * if any error will occur
	 */
	public void run() throws AREasyException
	{
		/* The delegated action to be executed **/
		RuntimeAction action = null;
		boolean spinAction = false;

		boolean stopFlag = false;
		ParserEngine parser = null;

		this.reportdata = getConfiguration().getBoolean("reportdata", false);
		this.reporterrors = getConfiguration().getBoolean("reporterrors", false);
		this.reportheader = getConfiguration().getStringArray("reportheader", null);

		String file = getConfiguration().getString("parserfile", getConfiguration().getString("inputfile", getConfiguration().getString("file", null)));

		String subAction = getConfiguration().getString("subaction",null);
		String callAction = getConfiguration().getString("call", null);

		//validate and set parser-type parameter
		if((getConfiguration().containsKey("file") || getConfiguration().containsKey("inputfile") || getConfiguration().containsKey("parserfile")) && !getConfiguration().containsKey("parsertype"))
		{
			getConfiguration().setKey("parsertype", "file");
			getConfiguration().setKey("parserfile", file);
			getConfiguration().removeKey("file");
			getConfiguration().removeKey("inputfile");
		}

		if(StringUtility.isEmpty(subAction) && StringUtility.isEmpty(callAction)) throw new AREasyException("Command action is null");
		else if(!StringUtility.isEmpty(subAction))
		{
			if(!isLinked()) RuntimeLogger.info("File Wrapper processor runs '" + subAction + "' independent action");
			getConfiguration().removeKey("subaction");
			spinAction = false;
		}
		else if(!StringUtility.isEmpty(callAction))
		{
			if(!isLinked()) RuntimeLogger.info("File Wrapper processor runs '" + callAction + "' action action");
			getConfiguration().removeKey("call");
			spinAction = true;
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
				logger.error("Error reading data source file: " + th.getMessage());
				logger.debug("Exception", th);

				throw new AREasyException("Data source is not present or parsing process recorded an error: " + th.getMessage());
			}

			int maxLimit = getConfiguration().getInt("limit", 0);
			int chunk = getConfiguration().getInt("chunk", 1000);

			int indexLimit = 0;
			int emptyDataCounter = 0;

			do
			{
				String data[] = null;
				
				/* The delegated action to be executed **/
				if(!spinAction) action = getManager().getRuntimeAction(subAction);
					else action = getManager().getSpinAction(callAction);

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

						if(getWhenCondition(null))
						{
							action.init(this);
							action.run();
						}
					}
					else emptyDataCounter++;
				}
				catch(Throwable th)
				{
					setErrorsCounter();

					if (isForced())
					{
						RuntimeLogger.error("Error running action using data extracted from line '" + parser.getParser().getCurrentIndex() + "': " + th.getMessage());
						logger.debug("Exception", th);
					}
					else
					{
						if (th instanceof AREasyException) throw (AREasyException) th;
							else throw new AREasyException(th);
					}
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

				//execution counter incrementation
				if(action instanceof BaseData)
				{
					if( ((BaseData)action).getRecordsCounter() > 0 ) addRecordsCounter( ((BaseData)action).getRecordsCounter() );
						else setRecordsCounter();

					if( ((BaseData)action).getProcessorsCounter() > 0 ) addProcessorsCounter( ((BaseData)action).getProcessorsCounter() );

					if( ((BaseData)action).getErrorsCounter() > 0 ) addErrorsCounter( ((BaseData)action).getErrorsCounter() );
				}
				else setRecordsCounter();

                //display info
                if(chunk > 0 && getRecordsCounter() >= chunk && getRecordsCounter() % chunk == 0) displayChunkStatusMessage(false, null);

				//set session flags
				stopFlag = RuntimeLogger.hasErrors();
				if(isForced() && stopFlag) stopFlag = false;

				//limitation to 10 empty lines, to cancel the process.
				if(emptyDataCounter >= 3) stopFlag = true;
				
				//close loop evaluation
				indexLimit++;
				if(maxLimit > 0) stopFlag = indexLimit >= maxLimit;

				// check interruption and exit if the execution was really interrupted
				if(isInterrupted())
				{
					RuntimeLogger.warn("Execution interrupted by user");
					return;
				}

				//execution sleep
				if(getConfiguration().containsKey("sleep") && !stopFlag)
				{
					try
					{
						int sleep = getConfiguration().getInt("sleep", 1000);
						if(sleep < 1000) sleep *= 1000;

						Thread.sleep(sleep);
					}
					catch(InterruptedException e) { /* nothing to do */ }
				}
			}
			while(!stopFlag);

            //display info
            displayChunkStatusMessage(true, null);
		}
		else
		{
			try
			{
				//execute operation
				action.run();
			}
			catch(Throwable th)
			{
				String error = "Error running action '" + action.getCode() + "'";
				error += ": " + th.getMessage();

				RuntimeLogger.error(error);
				logger.debug("Exception", th);
			}
		}

		if(!isLinked() && reportdata)
		{
			if(RuntimeLogger.hasData())
			{
				if(getConfiguration().getString("dataout", null) != null)
				{
					if(reportheader != null) writeTextFile(getConfiguration().getString("dataout", null), StringUtility.join(reportheader, ",") + "\n" + RuntimeLogger.getCompactData());
					else writeTextFile(getConfiguration().getString("dataout", null), RuntimeLogger.getCompactData());
				}
				else
				{
					if(reportheader != null) RuntimeLogger.info(StringUtility.capitalize(getCode()) + " processor ended providing output data\n" + StringUtility.join(reportheader,",") + "\n" + RuntimeLogger.getCompactData());
					else RuntimeLogger.info(StringUtility.capitalize(getCode()) + " processor ended providing output data\n" + RuntimeLogger.getCompactData());
				}			}
			else RuntimeLogger.warn("Processor ended without output data");
		}
		else
		{
			if(!isLinked() && reporterrors)
			{
				if (RuntimeLogger.hasErrors())
				{
					if(getConfiguration().getString("errorsout", null) != null) writeTextFile(getConfiguration().getString("errorsout", null), RuntimeLogger.getCompactData());
						else RuntimeLogger.info(StringUtility.capitalize(getCode()) + " processor ended by collecting errors for request Ids\n" + RuntimeLogger.getCompactData());
				}
			}
		}
	}

	public void run(CoreItem item) throws AREasyException
	{
		//nothing to do here
	}
}
