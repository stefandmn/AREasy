package org.areasy.common.support.configuration.providers.properties.rstream;

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

import org.areasy.common.support.configuration.Configuration;
import org.areasy.common.support.configuration.ConfigurationException;
import org.areasy.common.support.configuration.ConfigurationLocator;
import org.areasy.common.support.configuration.providers.properties.stream.PropertiesLocator;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

/**
 * Implementation of Locator interface for class properties files.
 *
 * @version $Id: ClassPropertiesLocator.java,v 1.3 2008/05/14 09:32:42 swd\stefan.damian Exp $
 */
public class ClassPropertiesLocator extends PropertiesLocator
{
	/** Specific class loader */
	private ClassLoader loader = null;

	/**
	 * Default constructor, specifing source location
	 */
	public ClassPropertiesLocator(ClassLoader loader, String source)
	{
		super(source);

		if(loader != null) this.loader = loader;
			else this.loader = ClassPropertiesLocator.class.getClassLoader();
	}

	/**
	 * Constructor to define a new locator with a specific parent
	 */
	public ClassPropertiesLocator(ConfigurationLocator parent, ClassLoader loader, String source)
	{
		super(parent, source);

		if(loader != null) this.loader = loader;
			else this.loader = ClassPropertiesLocator.class.getClassLoader();

	}
	/**
	 * Get real location from the current locator, considering all parents.
	 */
	public Object getSource()
	{
		InputStream resource = ClassPropertiesLocator.class.getResourceAsStream(this.file);
		if (resource == null) resource = this.loader.getResourceAsStream(this.file);

		if(resource == null && hasParent()) return ((ClassPropertiesLocator)getParent()).getSource(this.file);
			else return this.file;
	}

	/**
	 * Get parent location for a resoource stream.
	 */
	protected String getParentResourceStream(String base, String location)
	{
		String reference = base.substring(0, base.lastIndexOf('/') + 1);
		if(!reference.endsWith("/")) reference += "/" + location;
			else reference += location;

		return reference;
	}

	/**
	 * Recursive methods to get real location for the current locator.
	 */
	protected String getSource(String location)
	{
		InputStream resource = ClassPropertiesLocator.class.getResourceAsStream(getParentResourceStream(this.file, location));
		if(resource == null) resource = this.loader.getResourceAsStream(getParentResourceStream(this.file, location));

		if(resource != null) return getParentResourceStream(this.file, location);
			else if(hasParent()) return ((ClassPropertiesLocator)getParent()).getSource(this.file + "/" + location);
				else  return getParentResourceStream(this.file, location);
	}

	/**
	 * Get source reader instance.
	 */
	protected LineNumberReader getReader() throws IOException
	{
		InputStream resource = ClassPropertiesLocator.class.getResourceAsStream( (String)getSource() );
		if(resource == null) resource = this.loader.getResourceAsStream( (String)getSource() );

		return new LineNumberReader(new InputStreamReader(resource));
	}

	/**
	 * Write configuration entries from a specific locator.
	 * <b>This method is UNSUPPORTED and NOT IMPLEMENTED</b>
	 *
	 * @throws org.areasy.common.support.configuration.ConfigurationException
	 */
	public void write(boolean all) throws ConfigurationException
	{
		throw new ConfigurationException("Unsupported method.");
	}


	/**
	 * Create configuration structure the current locator.
	 *
	 * @return configuration structure which encapsulate this locator.
	 */
	public Configuration getConfiguration()
	{
		return new ClassPropertiesConfiguration(this);
	}
}
