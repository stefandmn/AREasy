package org.areasy.common.velocity;

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
import org.areasy.common.velocity.base.MethodInvocationException;
import org.areasy.common.velocity.base.ResourceNotFoundException;
import org.areasy.common.velocity.base.Template;
import org.areasy.common.velocity.context.Context;
import org.areasy.common.velocity.context.DefaultInternalContextAdapter;
import org.areasy.common.velocity.runtime.RuntimeServiceImplementation;
import org.areasy.common.velocity.runtime.parser.ParseErrorException;
import org.areasy.common.velocity.runtime.parser.ParseException;
import org.areasy.common.velocity.runtime.parser.node.SimpleNode;

import java.io.*;
import java.util.Properties;

/**
 * This class provides  services to the application developer, such as :
 * <ul>
 * <li> simple Velocity Runtime engine initialization methods.
 * <li> functions to apply the template engine to streams and strings
 * to allow embedding and dynamic template generation.
 * </ul>
 * <p/>
 * <br><br>
 * While the most common way to use Velocity is via templates, as
 * Velocity is a general-purpose template engine, there are other
 * uses that Velocity is well suited for, such as processing dynamically
 * created templates, or processing content streams.
 * <p/>
 * <br><br>
 * The methods herein were developed to allow easy access to the Velocity
 * facilities without direct spelunking of the internals.  If there is
 * something you feel is necessary to add here, please, send a patch.
 *
 * @version $Id: Velocity.java,v 1.4 2008/05/25 22:33:16 swd\stefan.damian Exp $
 */
public class Velocity
{
	/** the logger */
	private static Logger logger = LoggerFactory.getLog(Velocity.class.getName());

	private static RuntimeServiceImplementation runtime = new RuntimeServiceImplementation();

	/**
	 * initialize the Velocity runtime engine, using the default
	 * properties of the Velocity distribution
	 */
	public static void init() throws Exception
	{
		runtime.init();
	}

	/**
	 * initialize the Velocity runtime engine, using default properties
	 * plus the properties in the properties file passed in as the arg
	 *
	 * @param propsFilename file containing properties to use to initialize
	 *                      the Velocity runtime
	 */
	public static void init(String propsFilename) throws Exception
	{
		runtime.init(propsFilename);
	}

	/**
	 * initialize the Velocity runtime engine, using default properties
	 * plus the properties in the passed in java.util.Properties object
	 *
	 * @param p Proprties object containing initialization properties
	 */
	public static void init(Properties p) throws Exception
	{
		runtime.init(p);
	}

	/**
	 * Initialize the Velocity runtime engine, using default properties
	 * plus the properties in the passed in Configuration object
	 *
	 * @param c Configuration object containing initialization properties
	 */
	public static void init(Configuration c) throws Exception
	{
		runtime.init(c);
	}

	/**
	 * Set a Velocity Runtime property.
	 *
	 */
	public static void setProperty(String key, Object value)
	{
		runtime.setConfigurationEntry(key, value);
	}

	/**
	 * Add a Velocity Runtime property.
	 */
	public static void addProperty(String key, Object value)
	{
		runtime.addKey(key, value);
	}

	/**
	 * Set an entire configuration at once. This is
	 * useful in cases where the parent application uses
	 * the ExtendedProperties class and the velocity configuration
	 * is a subset of the parent application's configuration.
	 *
	 * @param configuration
	 */
	public static void setConfiguration(Configuration configuration)
	{
		runtime.setConfiguration(configuration);
	}

	/**
	 * Get a Velocity Runtime property.
	 *
	 * @param key property to retrieve
	 * @return property value or null if the property
	 *         not currently set
	 */
	public static Object getProperty(String key)
	{
		return runtime.getKey(key);
	}

	/**
	 * renders the input string using the context into the output writer.
	 * To be used when a template is dynamically constructed, or want to use
	 * Velocity as a token replacer.
	 *
	 * @param context  context to use in rendering input string
	 * @param out      Writer in which to render the output
	 * @param logTag   string to be used as the template name for log
	 *                 messages in case of error
	 * @param instring input string containing the VTL to be rendered
	 * @return true if successful, false otherwise.  If false, see
	 *         Velocity runtime log
	 */
	public static boolean evaluate(Context context, Writer out, String logTag, String instring) throws ParseErrorException, MethodInvocationException, ResourceNotFoundException, IOException
	{
		return evaluate(context, out, logTag, new BufferedReader(new StringReader(instring)));
	}

	/**
	 * Renders the input reader using the context into the output writer.
	 * To be used when a template is dynamically constructed, or want to
	 * use Velocity as a token replacer.
	 *
	 * @param context context to use in rendering input string
	 * @param logTag  string to be used as the template name for log messages
	 *                in case of error
	 * @param reader  Reader containing the VTL to be rendered
	 * @return true if successful, false otherwise.  If false, see
	 *         Velocity runtime log
	 */
	public static boolean evaluate(Context context, Writer writer, String logTag, Reader reader) throws ParseErrorException, MethodInvocationException, ResourceNotFoundException, IOException
	{
		SimpleNode nodeTree;

		try
		{
			nodeTree = runtime.parse(reader, logTag);
		}
		catch (ParseException pex)
		{
			throw  new ParseErrorException(pex.getMessage());
		}

		if (nodeTree != null)
		{
			DefaultInternalContextAdapter ica = new DefaultInternalContextAdapter(context);

			ica.pushCurrentTemplateName(logTag);

			try
			{
				try
				{
					nodeTree.init(ica, runtime);
				}
				catch (Exception e)
				{
					logger.error("Velocity.evaluate() : init exception for tag = " + logTag + " : " + e.getMessage());
					logger.debug("Exception", e);
				}

				nodeTree.render(ica, writer);
			}
			finally
			{
				ica.popCurrentTemplateName();
			}

			return true;
		}

		return false;
	}

	/**
	 * Invokes a currently registered Velocimacro with the parms provided
	 * and places the rendered stream into the writer.
	 * <p/>
	 * Note : currently only accepts args to the VM if they are in the context.
	 *
	 * @param vmName   name of Velocimacro to call
	 * @param logTag   string to be used for template name in case of error
	 * @param params args used to invoke Velocimacro. In context key format :
	 *                 eg  "foo","bar" (rather than "$foo","$bar")
	 * @param context  Context object containing data/objects used for rendering.
	 * @param writer   Writer for output stream
	 * @return true if Velocimacro exists and successfully invoked, false otherwise.
	 */
	public static boolean invokeVelocity(String vmName, String logTag, String params[], Context context, Writer writer)
	{

		if (vmName == null || params == null || context == null || writer == null || logTag == null)
		{
			logger.error("Invalid parameters");
			return false;
		}

		if (!runtime.isVelocityMacro(vmName, logTag))
		{
			logger.error("VM '" + vmName + "' not registered.");
			return false;
		}

		StringBuffer construct = new StringBuffer("#");

		construct.append(vmName);
		construct.append("(");

		for (int i = 0; i < params.length; i++)
		{
			construct.append(" $");
			construct.append(params[i]);
		}

		construct.append(" )");

		try
		{
			return evaluate(context, writer, logTag, construct.toString());
		}
		catch (Exception e)
		{
			logger.error("Error invoking Velocity macro: " + e.getMessage());
			logger.debug("Exception", e );
		}

		return false;
	}

	/**
	 * merges a template and puts the rendered stream into the writer
	 *
	 * @param templateName name of template to be used in merge
	 * @param encoding     encoding used in template
	 * @param context      filled context to be used in merge
	 * @param writer       writer to write template into
	 * @return true if successful, false otherwise.  Errors
	 *         logged to velocity log
	 */
	public static boolean mergeTemplate(String templateName, String encoding, Context context, Writer writer) throws ResourceNotFoundException, ParseErrorException, MethodInvocationException, Exception
	{
		Template template = runtime.getTemplate(templateName, encoding);

		if (template == null)
		{
			logger.error("Failed loading template '" + templateName + "'");
			return false;
		}
		else
		{
			template.merge(context, writer);
			return true;
		}
	}

	/**
	 * Returns a <code>Template</code> from the Velocity
	 * resource management system.
	 *
	 * @param name The file name of the desired template.
	 * @return The template.
	 * @throws org.areasy.common.velocity.base.ResourceNotFoundException if template not found
	 *                                   from any available source.
	 * @throws org.areasy.common.velocity.runtime.parser.ParseErrorException       if template cannot be parsed due
	 *                                   to syntax (or other) error.
	 * @throws Exception                 if an error occurs in template initialization
	 */
	public static Template getTemplate(String name) throws ResourceNotFoundException, ParseErrorException, Exception
	{
		return runtime.getTemplate(name);
	}

	/**
	 * Returns a <code>Template</code> from the Velocity
	 * resource management system.
	 *
	 * @param name     The file name of the desired template.
	 * @param encoding The character encoding to use for the template.
	 * @return The template.
	 * @throws org.areasy.common.velocity.base.ResourceNotFoundException if template not found
	 *                                   from any available source.
	 * @throws ParseErrorException       if template cannot be parsed due
	 *                                   to syntax (or other) error.
	 * @throws Exception                 if an error occurs in template initialization
	 * @since Velocity v1.1
	 */
	public static Template getTemplate(String name, String encoding) throws ResourceNotFoundException, ParseErrorException, Exception
	{
		return runtime.getTemplate(name, encoding);
	}

	/**
	 * <p>Determines whether a resource is accessable via the
	 * currently configured resource loaders.  {@link
	 * org.areasy.common.velocity.runtime.resource.Resource} is the generic
	 * description of templates, static content, etc.</p>
	 * <p/>
	 * <p>Note that the current implementation will <b>not</b> change
	 * the state of the system in any real way - so this cannot be
	 * used to pre-load the resource cache, as the previous
	 * implementation did as a side-effect.</p>
	 *
	 * @param resourceName The name of the resource to search for.
	 * @return Whether the resource was located.
	 */
	public static boolean resourceExists(String resourceName)
	{
		return (runtime.getLoaderNameForResource(resourceName) != null);
	}
}
