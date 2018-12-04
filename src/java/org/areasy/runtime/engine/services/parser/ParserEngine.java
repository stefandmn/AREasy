package org.areasy.runtime.engine.services.parser;

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

import org.areasy.common.data.ClassUtility;
import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;
import org.areasy.common.support.configuration.Configuration;
import org.areasy.runtime.engine.base.ServerConnection;

import java.util.Vector;

/**
 * Parser engine has the main role to initiate the right parser instance and to drive the
 * entire flow of data parsing procedure.
 */
public class ParserEngine
{
	private static Logger logger = LoggerFactory.getLog(ParserEngine.class);

	/** Loaded parser instance */
	private AbstractParser parser = null;

	/** Parser and parser engine configuration */
	private Configuration parserConfig = null;
	private Configuration runtimeConfig = null;

	/** Remedy user connection & session */
	private ServerConnection arsession = null;

	/** Flag to say if the parser engine was initialized */
	private boolean isinit = false;

	/**
	 * Default constructor to create an instance of parser engine.
	 *
	 * @param arsession the current user session regarding the connectivity between AREasy and Remedy
	 * @param runtimeConfig runtime manager configuration
	 * @param parserConfig engine configuration.
	 */
	public ParserEngine(ServerConnection arsession, Configuration runtimeConfig, Configuration parserConfig)
	{
		this.arsession = arsession;
		this.runtimeConfig = runtimeConfig;
		this.parserConfig = parserConfig;
	}

	/**
	 * Initialize parser engine to know what parser implementation will be used. The parser type is read from configuration
	 * using <code>parsertype</code> reserved keyword.
	 *
	 * Also this method will initialize parser interface.
	 *
	 * @throws ParserException if any error will occur.
	 */
	public void init() throws ParserException
	{
		if(getParserConfig().containsKey("parsertype")) init(getParserConfig().getString("parsertype"));
			else throw new ParserException("Invalid parser type");
	}

	/**
	 * Initialize parser engine to know what parser implementation will be used.
	 * Also this method will initialize parser interface.
	 *
	 * @param type parser type.
	 * @throws ParserException if any error will occur.
	 */
	public void init(String type) throws ParserException
	{
		logger.debug("Requested parser: " + type);
		if(!this.getRuntimeConfig().getVector("app.runtime.parsers", new Vector()).contains(type)) throw new ParserException("Parser type '" + type + "' is not registered");

        try
		{
			this.parser = (AbstractParser) ClassUtility.getInstance( this.getRuntimeConfig().getString("app.runtime.parser." + type + ".class") );
			this.parser.init(getServerConnection(), getRuntimeConfig(), getParserConfig());

			this.setIsinit(true);
		}
		catch(Throwable th)
		{
			throw new ParserException("Can not create instance for '" + type + "' parser type: " + th.getMessage(), th);
		}
	}

	public void setResource(String key, Object object)
	{
		if(key != null && object != null) parserConfig.setKey(key, object);
	}

	/**
	 * Get the parser instance which will be used to delivered data.
	 *
	 * @return <code>AbstractParser</code> instance.
	 */
	public AbstractParser getParser()
	{
		return this.parser;
	}

	/**
	 * Close and dispose parser class (if is defined and if is initialized)
	 */
	public void close()
	{
		if(this.parser != null && isIsinit())
		{
			this.parser.close();
			setIsinit(false);
		}
	}

	/**
	 * Execute parser class and return the output (if the parser is defined and if is initialized)
	 *
	 * @return an array with strings.
	 * @throws ParserException if any error will occur
	 */
	public String[] read() throws ParserException
	{
		if(this.parser != null && isIsinit())
		{
			try
			{
				return this.parser.read();
			}
			catch(Throwable th)
			{
				if(th instanceof ParserException) throw (ParserException) th;
					else throw new ParserException(th);
			}
		}
		else throw new ParserException("Parser interface is not defined or is not initialized");
	}

	/**
	 * Execute parser class and return the output (if the parser is defined and if is initialized)
	 *
	 * @param index reading index
	 * @return an array with strings.
	 * @throws ParserException if any error will occur
	 */
	public String[] read(int index) throws ParserException
	{
		if(this.parser != null && isIsinit())
		{
			try
			{
				return this.parser.read(index);
			}
			catch(Throwable th)
			{
				if(th instanceof ParserException) throw (ParserException) th;
					else throw new ParserException(th);
			}
		}
		else throw new ParserException("Parser interface is not defined or is not initialized");
	}

	/**
	 * Get total number of columns that will be delivered by the specified parser
	 *
	 * @return number of columns found in the source file or -1
	 */
	public int getNumberOfColumns()
	{
		if(this.parser != null && isIsinit()) return this.parser.getNumberOfColumns();
			else return -1;
	}

	public boolean isIsinit()
	{
		return isinit;
	}

	protected void setIsinit(boolean isinit)
	{
		this.isinit = isinit;
	}

	public Configuration getParserConfig()
	{
		return parserConfig;
	}

	public Configuration getRuntimeConfig()
	{
		return runtimeConfig;
	}

	public ServerConnection getServerConnection()
	{
		return arsession;
	}

	public int getStartIndex()
	{
		return this.parser.getStartIndex();
	}

	public int getEndIndex()
	{
		return this.parser.getEndIndex();
	}

	public int getCurrentIndex()
	{
		return this.parser.getCurrentIndex();
	}

	public void setCurrentIndex(int currentIndex)
	{
		this.parser.setCursor(currentIndex);
	}
}
