package org.areasy.common.velocity.runtime;

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

import org.areasy.common.support.configuration.Configuration;
import org.areasy.common.velocity.base.ResourceNotFoundException;
import org.areasy.common.velocity.base.Template;
import org.areasy.common.velocity.runtime.directive.Directive;
import org.areasy.common.velocity.runtime.introspection.Introspector;
import org.areasy.common.velocity.runtime.introspection.Uberspect;
import org.areasy.common.velocity.runtime.parser.ParseException;
import org.areasy.common.velocity.runtime.parser.ParseErrorException;
import org.areasy.common.velocity.runtime.parser.node.SimpleNode;
import org.areasy.common.velocity.runtime.resource.ContentResource;

import java.io.Reader;
import java.util.Properties;
import java.util.Vector;


/**
 * Interface for internal runtime services that are needed by the
 * various components w/in Velocity.  This was taken from the old
 * Runtime singleton, and anything not necessary was removed.
 * <p/>
 * Currently implemented by RuntimeInstance.
 *
 * @version $Id: RuntimeService.java,v 1.1 2008/05/25 22:33:18 swd\stefan.damian Exp $
 */
public interface RuntimeService
{
	/*
	  * This is the primary initialization method in the Velocity
	  * Runtime. The systems that are setup/initialized here are
	  * as follows:
	  *
	  * <ul>
	  *   <li>Logging System</li>
	  *   <li>ResourceManager</li>
	  *   <li>Parser Pool</li>
	  *   <li>Global Cache</li>
	  *   <li>Static Content Include System</li>
	  *   <li>Velocity macro system</li>
	  * </ul>
	  */
	public void init() throws Exception;

	/**
	 * Allows an external system to set a property in
	 * the Velocity Runtime.
	 */
	public void setConfigurationEntry(String key, Object value);

	/**
	 * Allow an external system to set an ExtendedProperties
	 * object to use. This is useful where the external
	 * system also uses the ExtendedProperties class and
	 * the velocity configuration is a subset of
	 * parent application's configuration. This is
	 * the case with portal instance.
	 */
	public void setConfiguration(Configuration configuration);

	/**
	 * Add a property to the configuration. If it already
	 * exists then the value stated here will be added
	 * to the configuration entry. For example, if
	 * <p/>
	 * resource.loaders = file
	 * <p/>
	 * is already present in the configuration and you
	 * <p/>
	 * addConfigurationKey("resource.loaders", "classpath")
	 * <p/>
	 * Then you will end up with a Vector like the
	 * following:
	 * <p/>
	 * ["file", "classpath"]
	 */
	public void addKey(String key, Object value);

	/**
	 * Allows an external caller to get a property.  The calling
	 * routine is required to know the type, as this routine
	 * will return an Object, as that is what properties can be.
	 *
	 * @param key property to return
	 */
	public Object getKey(String key);

	/**
	 * Initialize the Velocity Runtime with a Properties
	 * object.
	 */
	public void init(Properties p) throws Exception;

	/**
	 * Initialize the Velocity Runtime with the name of
	 * ExtendedProperties object.
	 */
	public void init(String configurationFile) throws Exception;

	/**
	 * Parse the input and return the root of
	 * AST node structure.
	 * <br><br>
	 * In the event that it runs out of parsers in the
	 * pool, it will create and let them be GC'd
	 * dynamically, logging that it has to do that.  This
	 * is considered an exceptional condition.  It is
	 * expected that the user will set the
	 * PARSER_POOL_SIZE property appropriately for their
	 * application.  We will revisit this.
	 */
	public SimpleNode parse(Reader reader, String templateName) throws ParseException;

	/**
	 * Parse the input and return the root of the AST node structure.
	 *
	 * @param dumpNamespace flag to dump the Velocimacro namespace for this template
	 */
	public SimpleNode parse(Reader reader, String templateName, boolean dumpNamespace) throws ParseException;

	/**
	 * Returns a <code>Template</code> from the resource manager.
	 * This method assumes that the character encoding of the
	 * template is set by the <code>input.encoding</code>
	 * property.  The default is "ISO-8859-1"
	 *
	 * @param name The file name of the desired template.
	 * @return The template.
	 * @throws ResourceNotFoundException if template not found
	 *                                   from any available source.
	 * @throws org.areasy.common.velocity.runtime.parser.ParseErrorException       if template cannot be parsed due
	 *                                   to syntax (or other) error.
	 * @throws Exception                 if an error occurs in template initialization
	 */
	public Template getTemplate(String name) throws ResourceNotFoundException, ParseErrorException, Exception;

	/**
	 * Returns a <code>Template</code> from the resource manager
	 *
	 * @param name     The  name of the desired template.
	 * @param encoding Character encoding of the template
	 * @return The template.
	 * @throws ResourceNotFoundException if template not found
	 *                                   from any available source.
	 * @throws ParseErrorException       if template cannot be parsed due
	 *                                   to syntax (or other) error.
	 * @throws Exception                 if an error occurs in template initialization
	 */
	public Template getTemplate(String name, String encoding) throws ResourceNotFoundException, ParseErrorException, Exception;

	/**
	 * Returns a static content resource from the
	 * resource manager.  Uses the current value
	 * if INPUT_ENCODING as the character encoding.
	 *
	 * @param name Name of content resource to get
	 * @return parsed ContentResource object ready for use
	 * @throws org.areasy.common.velocity.base.ResourceNotFoundException if template not found
	 *                                   from any available source.
	 */
	public ContentResource getContent(String name) throws ResourceNotFoundException, ParseErrorException, Exception;

	/**
	 * Returns a static content resource from the
	 * resource manager.
	 *
	 * @param name     Name of content resource to get
	 * @param encoding Character encoding to use
	 * @return parsed ContentResource object ready for use
	 * @throws ResourceNotFoundException if template not found
	 *                                   from any available source.
	 */
	public ContentResource getContent(String name, String encoding) throws ResourceNotFoundException, ParseErrorException, Exception;

	/**
	 * Determines is a template exists, and returns name of the loader that
	 * provides it.  This is a slightly less hokey way to support
	 * the Velocity.templateExists() utility method, which was broken
	 * when per-template encoding was introduced.  We can revisit this.
	 *
	 * @param resourceName Name of template or content resource
	 * @return class name of loader than can provide it
	 */
	public String getLoaderNameForResource(String resourceName);

	/**
	 * String property accessor method with default to hide the
	 * configuration implementation.
	 *
	 * @return String  value of key or default
	 */
	public String getString(String key, String defaultValue);

	/**
	 * Returns the appropriate VelocimacroProxy object if strVMname
	 * is a valid current Velocimacro.
	 *
	 * @return String VelocimacroProxy
	 */
	public Directive getVelocityMacro(String vmName, String templateName);

	/**
	 * Adds a new Velocimacro. Usually called by Macro only while parsing.
	 *
	 * @return boolean  True if added, false if rejected for some
	 *         reason (either parameters or permission settings)
	 */
	public boolean addVelocityMacro(String name, String macro, String argArray[], String sourceTemplate);

	/**
	 * Checks to see if a VM exists
	 *
	 * @return boolean  True if VM by that name exists, false if not
	 */
	public boolean isVelocityMacro(String vmName, String templateName);

	/**
	 * tells the vmFactory to dump the specified namespace.  This is to support
	 * clearing the VM list when in inline-VM-local-scope mode
	 */
	public boolean dumpVMNamespace(String namespace);

	/**
	 * String property accessor method to hide the configuration implementation
	 *
	 * @param key property key
	 * @return value of key or null
	 */
	public String getString(String key);

	/**
	 * Int property accessor method to hide the configuration implementation.
	 *
	 * @param key property key
	 * @return int value
	 */
	public int getInt(String key);

	/**
	 * Int property accessor method to hide the configuration implementation.
	 *
	 * @param key property key
	 * @param defaultValue
	 * @return int  value
	 */
	public int getInt(String key, int defaultValue);

	/**
	 * Boolean property accessor method to hide the configuration implementation.
	 *
	 * @param key  property key
	 * @param def default value if property not found
	 * @return boolean  value of key or default value
	 */
	public boolean getBoolean(String key, boolean def);

	/**
	 * Vector property accessor method to hide the configuration implementation.
	 *
	 * @param key  property key
	 * @return Vector value.
	 */
	public Vector getVector(String key);

	/**
	 * Return the velocity runtime configuration object.
	 *
	 * @return ExtendedProperties configuration object which houses
	 *         the velocity runtime properties.
	 */
	public Configuration getConfiguration();

	/**
	 * Return the specified applcation attribute.
	 *
	 * @param key The name of the attribute to retrieve.
	 */
	public Object getApplicationAttribute(Object key);

	/**
	 * Returns the configured class introspection/reflection
	 * implemenation.
	 */
	public Uberspect getUberspect();

	/**
	 * Returns the configured method introspection/reflection
	 * implemenation.
	 */
	public Introspector getIntrospector();
}
