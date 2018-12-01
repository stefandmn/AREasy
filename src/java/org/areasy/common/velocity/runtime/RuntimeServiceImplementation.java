package org.areasy.common.velocity.runtime;

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

import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;
import org.areasy.common.support.configuration.Configuration;
import org.areasy.common.support.configuration.providers.properties.stream.PropertiesConfiguration;
import org.areasy.common.support.configuration.providers.properties.rstream.ClassPropertiesConfiguration;
import org.areasy.common.support.configuration.providers.properties.BasePropertiesConfiguration;
import org.areasy.common.velocity.base.ResourceNotFoundException;
import org.areasy.common.velocity.base.Template;
import org.areasy.common.velocity.base.VelocityFactory;
import org.areasy.common.velocity.runtime.directive.Directive;
import org.areasy.common.velocity.runtime.introspection.Introspector;
import org.areasy.common.velocity.runtime.introspection.Uberspect;
import org.areasy.common.velocity.runtime.parser.ParseException;
import org.areasy.common.velocity.runtime.parser.Parser;
import org.areasy.common.velocity.runtime.parser.ParseErrorException;
import org.areasy.common.velocity.runtime.parser.node.SimpleNode;
import org.areasy.common.velocity.runtime.resource.ContentResource;
import org.areasy.common.velocity.runtime.resource.ResourceManager;

import java.io.Reader;
import java.util.*;

/**
 * This is the Runtime system for Velocity. It is the
 * single access point for all functionality in Velocity.
 * It adheres to the mediator pattern and is the only
 * structure that developers need to be familiar with
 * in order to get Velocity to perform.
 *
 * @version $Id: RuntimeServiceImplementation.java,v 1.3 2008/05/25 23:32:02 swd\stefan.damian Exp $
 */
public class RuntimeServiceImplementation implements RuntimeService
{
	/** the logger */
	private static Logger logger = LoggerFactory.getLog(RuntimeServiceImplementation.class.getName());

	/**
	 * VelocimacroFactory object to manage VMs
	 */
	private VelocityFactory vmFactory = null;

	/**
	 * The Runtime parser pool
	 */
	private SimplePool parserPool;

	/**
	 * Indicate whether the Runtime has been fully initialized.
	 */
	private boolean initialized = false;

	/**
	 * These are the properties that are laid down over top
	 * of the default properties when requested.
	 */
	private Configuration overriding = new PropertiesConfiguration();

	/**
	 * This is a hashtable of initialized directives.
	 * The directives that populate this hashtable are
	 * taken from the RUNTIME_DEFAULT_DIRECTIVES
	 * property file. This hashtable is passed
	 * to each parser that is created.
	 */
	private Hashtable runtimeDirectives;

	/**
	 * Object that houses the configuration options for
	 * the velocity runtime. The ExtendedProperties object allows
	 * the convenient retrieval of a subset of properties.
	 * For example all the properties for a resource loader
	 * can be retrieved from the main ExtendedProperties object
	 * using something like the following:
	 * <p/>
	 * ExtendedProperties loaderConfiguration =
	 * configuration.subset(loaderID);
	 * <p/>
	 * And a configuration is a lot more convenient to deal
	 * with then conventional properties objects, or Maps.
	 */
	private Configuration configuration = null;

	/**
	 * Resource Manager
	 */
	private ResourceManager resourceManager = null;

	/*
	 *  Each runtime instance has it's own introspector
	 *  to ensure that each instance is completely separate.
	 */
	private Introspector introspector = null;

	/*
	 *  Opaque reference to something specificed by the
	 *  application for use in application supplied/specified
	 *  pluggable components
	 */
	private Map applicationAttributes = null;

	private Uberspect uberSpect;

	public RuntimeServiceImplementation()
	{
		/*
		 *  create a VM factory, resource manager
		 *  and introspector
		 */
		vmFactory = new VelocityFactory(this);

		/*
		 *  make a new introspector and initialize it
		 */
		introspector = new Introspector();

		/*
		 * and a store for the application attributes
		 */
		applicationAttributes = new HashMap();
	}

	/*
	 * This is the primary initialization method in the Velocity
	 * Runtime. The systems that are setup/initialized here are
	 * as follows:
	 *
	 * <ul>
	 *   <li>Configuration System</li>
	 *   <li>ResourceManager</li>
	 *   <li>Parser Pool</li>
	 *   <li>Global Cache</li>
	 *   <li>Static Content Include System</li>
	 *   <li>Velocimacro System</li>
	 * </ul>
	 */
	public synchronized void init() throws Exception
	{
		if (!initialized)
		{
			initializeConfiguration();

			initializeResourceManager();

			initializeDirectives();

			initializeParserPool();

			initializeIntrospection();

			vmFactory.initVelocity();

			logger.debug("Velocity engine started.");

			initialized = true;
		}
	}

	/**
	 * Gets the classname for the Uberspect introspection package and
	 * instantiates an instance.
	 */
	private void initializeIntrospection() throws Exception
	{
		String rm = getString("runtime.introspector.uberspect.class");

		if (rm != null && rm.length() > 0)
		{
			Object o = null;

			try
			{
				o = Class.forName(rm).newInstance();
			}
			catch (ClassNotFoundException cnfe)
			{
				String err = "The specified class for uberspect (" + rm + ") does not exist or is not accessible to the current classlaoder.";

				logger.error(err);
				throw new Exception(err);
			}

			if (!(o instanceof Uberspect))
			{
				String err = "The specified class (" + rm + ") does not implement uberspect interface. Velocity not initialized correctly.";

				logger.error(err);
				throw new Exception(err);
			}

			uberSpect = (Uberspect) o;

			uberSpect.init();
		}
		else
		{
			String err = "It appears that no class was specified as the Uberspect. Please ensure that all configuration information is correct.";

			logger.error(err);
			throw new Exception(err);
		}
	}

	/**
	 * Initializes the Velocity Runtime with properties file.
	 * The properties file may be in the file system proper,
	 * or the properties file may be in the classpath.
	 */
	private void setDefaultProperties()
	{
		try
		{
			configuration = new ClassPropertiesConfiguration(Thread.currentThread().getContextClassLoader(), "/org/areasy/common/velocity/runtime/default.properties");
		}
		catch (Exception ioe)
		{
			logger.fatal("Can not get Velocity runtime default properties: " + ioe.getMessage());
			logger.debug("Exception", ioe);
		}
	}

	/**
	 * Allows an external system to set a property in
	 * the Velocity Runtime.
	 */
	public void setConfigurationEntry(String key, Object value)
	{
		if (overriding == null) overriding = new PropertiesConfiguration();

		overriding.setKey(key, value);
	}

	/**
	 * Allow an external system to set an ExtendedProperties
	 * object to use. This is useful where the external
	 * system also uses the ExtendedProperties class and
	 * the velocity configuration is a subset of
	 * parent application's configuration. This is
	 * the case with portal instance.
	 */
	public void setConfiguration(Configuration configuration)
	{
		if (overriding == null || overriding.isEmpty()) overriding = configuration;
		else
		{
			Iterator iterator = configuration.getKeys();
			while (iterator != null && iterator.hasNext())
			{
				String key = (String) iterator.next();
				overriding.setKey(key, configuration.getKey(key));
			}
		}
	}

	/**
	 * Add a property to the configuration. If it already
	 * exists then the value stated here will be added
	 * to the configuration entry. For example, if
	 * <p/>
	 * resource.loaders = file
	 * <p/>
	 * is already present in the configuration and you
	 * <p/>
	 * addConfigurationEntry("resource.loaders", "classpath")
	 * <p/>
	 * Then you will end up with a Vector like the
	 * following:
	 * <p/>
	 * ["file", "classpath"]
	 */
	public void addKey(String key, Object value)
	{
		overriding.addKey(key, value);
	}

	/**
	 * Allows an external caller to get a property.  The calling
	 * routine is required to know the type, as this routine
	 * will return an Object, as that is what properties can be.
	 *
	 * @param key property to return
	 */
	public Object getKey(String key)
	{
		return configuration.getKey(key);
	}

	/**
	 * Initialize Velocity properties, if the default properties have not been laid down first then
	 * do so. Then proceed to process any overriding properties. Laying down the default properties
	 * gives a much greater chance of having a working system.
	 */
	private void initializeConfiguration()
	{
		if (!initialized)
		{
			setDefaultProperties();

			if (overriding != null && !overriding.isEmpty())  configuration.merge(overriding);
		}
	}

	/**
	 * Initialize the Velocity Runtime with a Properties
	 * object.
	 */
	public void init(Properties p) throws Exception
	{
		overriding = BasePropertiesConfiguration.getConfiguration(p);

		init();
	}

	/**
	 * Initialize the Velocity Runtime with a Configration
	 * object.
	 */
	public void init(Configuration c) throws Exception
	{
		overriding = c;

		init();
	}

	/**
	 * Initialize the Velocity Runtime with the name of
	 * Configuration object.
	 */
	public void init(String configurationFile) throws Exception
	{
		overriding = new PropertiesConfiguration(configurationFile);

		init();
	}

	private void initializeResourceManager() throws Exception
	{
		String rm = getString("resource.manager.class");

		if (rm != null && rm.length() > 0)
		{
			Object o = null;

			try
			{
				o = Class.forName(rm).newInstance();
			}
			catch (ClassNotFoundException cnfe)
			{
				String err = "The specified class for resource manager (" + rm + ") does not exist or is not accessible to the current classlaoder.";
				logger.error(err);

				throw new Exception(err);
			}

			if (!(o instanceof ResourceManager))
			{
				String err = "The specified class for resource manager (" + rm + ") does not implement Velocity ResourceManager signature. Velocity engine is not initialized correctly.";

				logger.error(err);
				throw new Exception(err);
			}

			resourceManager = (ResourceManager) o;
			resourceManager.initialize(this);
		}
		else
		{
			String err = "It appears that no class was specified as the ResourceManager. Please ensure that all configuration information is correct.";

			logger.error(err);
			throw new Exception(err);
		}
	}

	/**
	 * This methods initializes all the directives
	 * that are used by the Velocity Runtime. The
	 * directives to be initialized are listed in
	 * the RUNTIME_DEFAULT_DIRECTIVES properties
	 * file.
	 *
	 * @throws Exception if any erro will occur
	 */
	private void initializeDirectives() throws Exception
	{
		/*
		 * Initialize the runtime directive table.
		 * This will be used for creating parsers.
		 */
		runtimeDirectives = new Hashtable();

		String directives[] = getConfiguration().getStringArray("engine.directives");

		if (directives == null || directives.length <= 0) throw new Exception("Error loading directives. Invalid Velocity engine configuration!");

		for (int i = 0; i < directives.length; i++)
		{
			String code = directives[i];
			String classname = getConfiguration().getString("engine.directive." + code + ".class");

			loadDirective(classname, code);
		}
	}

	/**
	 * instantiates and loads the directive with some basic checks
	 *
	 * @param directiveClass classname of directive to load
	 */
	private void loadDirective(String directiveClass, String caption)
	{
		try
		{
			Object o = Class.forName(directiveClass).newInstance();

			if (o instanceof Directive)
			{
				Directive directive = (Directive) o;
				runtimeDirectives.put(directive.getName(), directive);

				logger.debug("Loaded '" + caption + "' directive: " + directiveClass);
			}
		}
		catch (Exception e)
		{
			logger.error("Exception loading '" + caption + "' directive - " + directiveClass + ": " + e.getMessage());
			logger.debug("Exception", e);
		}
	}


	/**
	 * Initializes the Velocity parser pool.
	 * This still needs to be implemented.
	 */
	private void initializeParserPool()
	{
		int numParsers = getInt("parser.pool.size", 20);

		parserPool = new SimplePool(numParsers);

		for (int i = 0; i < numParsers; i++)
		{
			parserPool.put(createNewParser());
		}

		logger.debug("Created '" + numParsers + "' parser instances.");
	}

	/**
	 * Returns a JavaCC generated Parser.
	 *
	 * @return Parser javacc generated parser
	 */
	public Parser createNewParser()
	{
		Parser parser = new Parser(this);
		parser.setDirectives(runtimeDirectives);

		return parser;
	}

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
	public SimpleNode parse(Reader reader, String templateName) throws ParseException
	{
		return parse(reader, templateName, true);
	}

	/**
	 * Parse the input and return the root of the AST node structure.
	 *
	 * @param dumpNamespace flag to dump the Velocimacro namespace for this template
	 */
	public SimpleNode parse(Reader reader, String templateName, boolean dumpNamespace) throws ParseException
	{

		SimpleNode ast = null;
		boolean madeNew = false;

		Parser parser = (Parser) parserPool.get();

		if (parser == null)
		{
			logger.error("Ran out of parsers. Please increment the 'parser.pool.size' property. The current value is too small.");

			parser = createNewParser();

			if (parser != null) madeNew = true;
		}

		if (parser != null)
		{
			try
			{
				if (dumpNamespace) dumpVMNamespace(templateName);

				ast = parser.parse(reader, templateName);
			}
			finally
			{
				if (!madeNew) parserPool.put(parser);
			}
		}
		else logger.error("Ran out of parsers and unable to create more.");

		return ast;
	}

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
	 * @throws ParseErrorException       if template cannot be parsed due
	 *                                   to syntax (or other) error.
	 * @throws Exception                 if an error occurs in template initialization
	 */
	public Template getTemplate(String name) throws ResourceNotFoundException, ParseErrorException, Exception
	{
		return getTemplate(name, getString("input.encoding", "ISO-8859-1"));
	}

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
	public Template getTemplate(String name, String encoding) throws ResourceNotFoundException, ParseErrorException, Exception
	{
		return (Template) resourceManager.getResource(name, ResourceManager.RESOURCE_TEMPLATE, encoding);
	}

	/**
	 * Returns a static content resource from the
	 * resource manager.  Uses the current value
	 * if INPUT_ENCODING as the character encoding.
	 *
	 * @param name Name of content resource to get
	 * @return parsed ContentResource object ready for use
	 * @throws ResourceNotFoundException if template not found
	 *                                   from any available source.
	 */
	public ContentResource getContent(String name) throws ResourceNotFoundException, ParseErrorException, Exception
	{
		return getContent(name, getString("input.encoding", "ISO-8859-1"));
	}

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
	public ContentResource getContent(String name, String encoding) throws ResourceNotFoundException, ParseErrorException, Exception
	{
		return (ContentResource) resourceManager.getResource(name, ResourceManager.RESOURCE_CONTENT, encoding);
	}


	/**
	 * Determines is a template exists, and returns name of the loader that
	 * provides it.  This is a slightly less hokey way to support
	 * the Velocity.templateExists() utility method, which was broken
	 * when per-template encoding was introduced.  We can revisit this.
	 *
	 * @param resourceName Name of template or content resource
	 * @return class name of loader than can provide it
	 */
	public String getLoaderNameForResource(String resourceName)
	{
		return resourceManager.getLoaderNameForResource(resourceName);
	}

	/**
	 * String property accessor method with default to hide the
	 * configuration implementation.
	 *
	 * @return String  value of key or default
	 */
	public String getString(String key, String defaultValue)
	{
		return configuration.getString(key, defaultValue);
	}

	/**
	 * Returns the appropriate VelocimacroProxy object if strVMname
	 * is a valid current Velocimacro.
	 *
	 * @return String VelocimacroProxy
	 */
	public Directive getVelocityMacro(String vmName, String templateName)
	{
		return vmFactory.getVelocityMacro(vmName, templateName);
	}

	/**
	 * Adds a new Velocimacro. Usually called by Macro only while parsing.
	 *
	 * @return boolean  True if added, false if rejected for some
	 *         reason (either parameters or permission settings)
	 */
	public boolean addVelocityMacro(String name, String macro, String argArray[], String sourceTemplate)
	{
		return vmFactory.addVelocityMacro(name, macro, argArray, sourceTemplate);
	}

	/**
	 * Checks to see if a VM exists
	 *
	 * @return boolean  True if VM by that name exists, false if not
	 */
	public boolean isVelocityMacro(String vmName, String templateName)
	{
		return vmFactory.isVelocityMacro(vmName, templateName);
	}

	/**
	 * tells the vmFactory to dump the specified namespace.  This is to support
	 * clearing the VM list when in inline-VM-local-scope mode
	 */
	public boolean dumpVMNamespace(String namespace)
	{
		return vmFactory.dumpVMNamespace(namespace);
	}

	/**
	 * String property accessor method to hide the configuration implementation
	 *
	 * @param key property key
	 * @return value of key or null
	 */
	public String getString(String key)
	{
		return configuration.getString(key);
	}

	/**
	 * Int property accessor method to hide the configuration implementation.
	 *
	 * @return int value
	 */
	public int getInt(String key)
	{
		return configuration.getInt(key);
	}

	/**
	 * Int property accessor method to hide the configuration implementation.
	 *
	 * @param key property key
	 * @return int  value
	 */
	public int getInt(String key, int defaultValue)
	{
		return configuration.getInt(key, defaultValue);
	}

	/**
	 * Boolean property accessor method to hide the configuration implementation.
	 *
	 * @return boolean  value of key or default value
	 */
	public boolean getBoolean(String key, boolean def)
	{
		return configuration.getBoolean(key, def);
	}

	/**
	 * Vector property accessor method to hide the configuration implementation.
	 *
	 * @param key  property key
	 * @return Vector value.
	 */
	public Vector getVector(String key)
	{
		return configuration.getVector(key, new Vector());
	}

	/**
	 * Return the velocity runtime configuration object.
	 *
	 * @return ExtendedProperties configuration object which houses
	 *         the velocity runtime properties.
	 */
	public Configuration getConfiguration()
	{
		return configuration;
	}

	/**
	 * Return the Introspector for this instance
	 */
	public Introspector getIntrospector()
	{
		return introspector;
	}

	public Object getApplicationAttribute(Object key)
	{
		return applicationAttributes.get(key);
	}

	public Object setApplicationAttribute(Object key, Object o)
	{
		return applicationAttributes.put(key, o);
	}

	public Uberspect getUberspect()
	{
		return uberSpect;
	}

	/**
	 * Simple object pool. Based on ThreadPool and few other classes
	 * <p/>
	 * The pool will ignore overflow and return null if empty.
	 */
	public final class SimplePool
	{
		/*
		 * Where the objects are held.
		 */
		private Object pool[];

		/**
		 * max amount of objects to be managed
		 * set via CTOR
		 */
		private int max;

		/**
		 * index of previous to next
		 * free slot
		 */
		private int current = -1;

		public SimplePool(int max)
		{
			this.max = max;
			pool = new Object[max];
		}

		/**
		 * Add the object to the pool, silent nothing if the pool is full
		 */
		public void put(Object o)
		{
			int idx = -1;

			synchronized(this)
			{
				if (current < max - 1) idx = ++current;

				if (idx >= 0) pool[idx] = o;
			}
		}

		/**
		 * Get an object from the pool, null if the pool is empty.
		 */
		public Object get()
		{
			synchronized(this)
			{
				if( current >= 0 )
				{
					Object o = pool[current];
					pool[current] = null;
	
					current--;

					return o;
				}
			}

			return null;
		}

		/**
		 * Return the size of the pool
		 */
		public int getMax()
		{
			return max;
		}
	}
}
