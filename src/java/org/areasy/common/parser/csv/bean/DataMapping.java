package org.areasy.common.parser.csv.bean;

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

import org.areasy.common.parser.csv.CsvReader;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.IOException;

public interface DataMapping
{
	/**
	 * Implementation will have to return a property descriptor from a bean based on the current column.
	 */
	public abstract PropertyDescriptor findDescriptor(int col) throws IntrospectionException;

	public abstract Object createBean() throws InstantiationException, IllegalAccessException;

	/**
	 * Implemention of this method can grab the header line before parsing begins to use to map columns
	 * to bean properties.
	 */
	public void captureHeader(CsvReader reader) throws IOException;

}