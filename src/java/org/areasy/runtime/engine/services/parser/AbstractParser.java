package org.areasy.runtime.engine.services.parser;

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
import org.areasy.common.support.configuration.Configuration;
import org.areasy.runtime.engine.base.ServerConnection;

/**
 * Parser template class, defining the main method used in parsing flows.
 *
 */
public abstract class AbstractParser
{
	private static Logger logger = LoggerFactory.getLog(AbstractParser.class);

	/** Remedy user connection & session */
	private ServerConnection arsession = null;

	/** Parser and parser engine configuration */
	protected Configuration parserConfig = null;
    protected Configuration runtimeConfig = null;

	private int currentIndex = 0;
	private int startIndex = 0;
	private int endIndex = 0;

	/**
     * Set the configuration and initialize parser class 
     *
	 * @param arsession the current user session regarding the connectivity between AREasy and Remedy
	 * @param runtimeConfig runtime manager configuration
	 * @param parserConfig engine configuration.
     * @throws ParserException if any error will occur
     */
    public final void init(ServerConnection arsession, Configuration runtimeConfig, Configuration parserConfig) throws ParserException
	{
		this.arsession = arsession;
		this.parserConfig = parserConfig;
        this.runtimeConfig = runtimeConfig;

		setStartIndex(getParserConfig().getInt("startindex", Math.max(getParserConfig().getInt("startline", 2) - 1, 1)));
		setEndIndex(getParserConfig().getInt("endindex", Math.max(getParserConfig().getInt("endline", 1) - 1, 0)));

		if(getEndIndex() > 0 && getStartIndex() >= getEndIndex()) throw new ParserException("Start index is greater or equal with end index"); 
		if(getCurrentIndex() != getStartIndex()) setCursor(getStartIndex());

		open();
    }

	/**
	 * Call this method after parser initialization.
	 *
	 * @throws ParserException if any error will occur
	 */
	protected abstract void open() throws ParserException;	

    /**
	 * Close and dispose parser class
	 */
	public abstract void close();

	/**
	 * Execute parser class and return the output.
	 *
	 * @return an array with strings.
	 * @throws ParserException if any error will occur
	 */
	public abstract String[] read() throws ParserException;

	/**
	 * Execute parser class and return the output from the specified index.
	 *
	 * @param index reading index
	 * @return an array with strings.
	 * @throws org.areasy.runtime.engine.services.parser.ParserException if any error will occur
	 */
	public abstract String[] read(int index) throws ParserException;

	/**
	 * Get total number of columns that will be delivered by the specified parser
	 *
	 * @return number of columns found in the source file or -1
	 */
	public abstract int getNumberOfColumns();

	/**
	 * Get parser configuration structure
	 *
	 * @return parser <code>Configuration</code> structure instance
	 */
	protected Configuration getParserConfig()
	{
		return parserConfig;
	}

	/**
	 * Get runtime configuration structure used to identity the parser engine and to initialize it
	 *
	 * @return parser <code>Configuration</code> structure instance
	 */
	protected Configuration getRuntimeConfig()
	{
		return runtimeConfig;
	}

	public int getStartIndex()
	{
		return startIndex;
	}

	public void setStartIndex(int startIndex)
	{
		this.startIndex = startIndex;
	}

	public int getEndIndex()
	{
		return endIndex;
	}

	public void setEndIndex(int endIndex)
	{
		this.endIndex = endIndex;
	}

	public int getCurrentIndex()
	{
		return currentIndex;
	}

	public void setCursor(int currentIndex)
	{
		this.currentIndex = currentIndex;
	}

	public void setNextCursor()
	{
		this.currentIndex++;
	}

	protected ServerConnection getServerConnection()
	{
		return arsession;
	}

	protected Logger getLogger()
	{
		return logger;
	}
}