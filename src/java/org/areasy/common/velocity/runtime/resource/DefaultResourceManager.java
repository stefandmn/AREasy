package org.areasy.common.velocity.runtime.resource;

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
import org.areasy.common.velocity.base.ResourceNotFoundException;
import org.areasy.common.velocity.runtime.parser.ParseErrorException;
import org.areasy.common.velocity.runtime.RuntimeService;
import org.areasy.common.velocity.runtime.resource.loader.ResourceLoader;
import org.areasy.common.velocity.runtime.resource.loader.ResourceLoaderFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Class to manage the text resource for the Velocity
 * Runtime.
 *
 * @version $Id: DefaultResourceManager.java,v 1.1 2008/05/25 22:33:17 swd\stefan.damian Exp $
 */
public class DefaultResourceManager implements ResourceManager
{
	/** the logger */
	private static Logger logger = LoggerFactory.getLog(DefaultResourceManager.class.getName());

	/**
	 * A template resources.
	 */
	public static final int RESOURCE_TEMPLATE = 1;

	/**
	 * A static content resource.
	 */
	public static final int RESOURCE_CONTENT = 2;

	/**
	 * Object implementing ResourceCache to
	 * be our resource manager's Resource cache.
	 */
	protected ResourceCache globalCache = null;

	/**
	 * The List of templateLoaders that the Runtime will
	 * use to locate the InputStream source of a template.
	 */
	protected ArrayList resourceLoaders = new ArrayList();

	/**
	 * This is a list of the template input stream source
	 * initializers, basically properties for a particular
	 * template stream source. The order in this list
	 * reflects numbering of the properties i.e.
	 * <p/>
	 * <loader-id>.resource.loader.<property> = <value>
	 */
	private ArrayList sourceInitializerList = new ArrayList();

	/**
	 * Each loader needs a configuration object for
	 * its initialization, this flags keeps track of whether
	 * or not the configuration objects have been created
	 * for the resource loaders.
	 */
	private boolean resourceLoaderInitializersActive = false;

	/**
	 * switch to turn off log notice when a resource is found for
	 * the first time.
	 */
	private boolean logWhenFound = true;

	protected RuntimeService rsvc = null;

	/**
	 * Initialize the ResourceManager.
	 */
	public void initialize(RuntimeService rs) throws Exception
	{
		rsvc = rs;

		logger.debug("Default resource manager initializing... (" + this.getClass() + ")");

		ResourceLoader resourceLoader;

		assembleResourceLoaderInitializers();

		for (int i = 0; i < sourceInitializerList.size(); i++)
		{
			Configuration configuration = (Configuration) sourceInitializerList.get(i);
			String loaderClass = configuration.getString("class", null);

			if (loaderClass == null)
			{
				logger.error("Unable to find 'resource.loader." + configuration.getString("loader")
						+ ".classname' specification in configuation. This is a critical value. Please adjust configuration.");
				continue;
			}

			resourceLoader = ResourceLoaderFactory.getLoader(rsvc, loaderClass);

			resourceLoader.commonInit(rsvc, configuration);
			resourceLoader.init(configuration);

			resourceLoaders.add(resourceLoader);
		}

		logWhenFound = rsvc.getBoolean("resource.manager.log.when.isfound", true);

		String claz = rsvc.getString("resource.manager.cache.class");

		Object o = null;

		if (claz != null && claz.length() > 0)
		{
			try
			{
				o = Class.forName(claz).newInstance();
			}
			catch (ClassNotFoundException cnfe)
			{
				String err = "The specified class for ResourceCache (" + claz + ") does not exist (or is not accessible to the current classlaoder).";
				logger.error(err);

				o = null;
			}

			if (!(o instanceof ResourceCache))
			{
				String err = "The specified class for ResourceCache (" + claz + ") does not implement ResourceCache interface. Using default ResourceCache implementation.";

				logger.error(err);

				o = null;
			}
		}

		if (o == null)o = new DefaultResourceCache();

		globalCache = (ResourceCache) o;

		globalCache.initialize(rsvc);

		logger.debug("Default resource manager initialization complete");

	}

	/**
	 * This will produce a List of Hashtables, each
	 * hashtable contains the intialization info for
	 * a particular resource loader. This Hastable
	 * will be passed in when initializing the
	 * the template loader.
	 */
	private void assembleResourceLoaderInitializers()
	{
		if (resourceLoaderInitializersActive) return;

		String resourceLoaderNames[] = rsvc.getConfiguration().getStringArray("resource.loaders");

		for (int i = 0; i < resourceLoaderNames.length; i++)
		{
			String loaderId = "resource.loader." + resourceLoaderNames[i];
			Configuration loaderConfig = rsvc.getConfiguration().subset(loaderId);

			if (loaderConfig == null)
			{
				logger.warn("No configuration information for resource loader called '" + resourceLoaderNames[i] + "'. Skipping..");
				continue;
			}

			loaderConfig.addKey("loader", resourceLoaderNames[i]);
			sourceInitializerList.add(loaderConfig);
		}

		resourceLoaderInitializersActive = true;
	}

	/**
	 * Gets the named resource.  Returned class type corresponds to specified type
	 * (i.e. <code>Template</code> to <code>RESOURCE_TEMPLATE</code>).
	 *
	 * @param resourceName The name of the resource to retrieve.
	 * @param resourceType The type of resource (<code>RESOURCE_TEMPLATE</code>,
	 *                     <code>RESOURCE_CONTENT</code>, etc.).
	 * @param encoding     The character encoding to use.
	 * @return Resource with the template parsed and ready.
	 * @throws ResourceNotFoundException if template not found
	 *                                   from any available source.
	 * @throws ParseErrorException       if template cannot be parsed due
	 *                                   to syntax (or other) error.
	 * @throws Exception                 if a problem in parse
	 */
	public Resource getResource(String resourceName, int resourceType, String encoding) throws ResourceNotFoundException, ParseErrorException, Exception
	{
		Resource resource = globalCache.get(resourceName);

		if (resource != null)
		{
			try
			{
				refreshResource(resource, encoding);
			}
			catch (ResourceNotFoundException rnfe)
			{
				globalCache.remove(resourceName);

				return getResource(resourceName, resourceType, encoding);
			}
			catch (ParseErrorException pee)
			{
				logger.error("Exception getting resource: " + pee.getMessage());

				throw pee;
			}
			catch (Exception eee)
			{
				logger.error("Exception loading resource: " + eee.getMessage());

				throw eee;
			}
		}
		else
		{
			try
			{
				resource = loadResource(resourceName, resourceType, encoding);

				if (resource.getResourceLoader().isCachingOn()) globalCache.put(resourceName, resource);
			}
			catch (ResourceNotFoundException rnfe2)
			{
				logger.warn("Unable to find resource '" + resourceName + "' in any resource loader: " + rnfe2.getMessage());

				throw rnfe2;
			}
			catch (ParseErrorException pee)
			{
				logger.error("Parser exception: " + pee.getMessage());

				throw pee;
			}
			catch (Exception ee)
			{
				logger.error("Error loading resource: " + ee.getMessage());

				throw ee;
			}
		}

		return resource;
	}

	/**
	 * Loads a resource from the current set of resource loaders
	 *
	 * @param resourceName The name of the resource to retrieve.
	 * @param resourceType The type of resource (<code>RESOURCE_TEMPLATE</code>,
	 *                     <code>RESOURCE_CONTENT</code>, etc.).
	 * @param encoding     The character encoding to use.
	 * @return Resource with the template parsed and ready.
	 * @throws ResourceNotFoundException if template not found
	 *                                   from any available source.
	 * @throws ParseErrorException       if template cannot be parsed due
	 *                                   to syntax (or other) error.
	 * @throws Exception                 if a problem in parse
	 */
	protected Resource loadResource(String resourceName, int resourceType, String encoding) throws ResourceNotFoundException, ParseErrorException, Exception
	{
		Resource resource = ResourceFactory.getResource(resourceName, resourceType);

		resource.setRuntimeServices(rsvc);

		resource.setName(resourceName);
		resource.setEncoding(encoding);
		long howOldItWas = 0;  // Initialize to avoid warnings

		ResourceLoader resourceLoader = null;

		for (int i = 0; i < resourceLoaders.size(); i++)
		{
			resourceLoader = (ResourceLoader) resourceLoaders.get(i);
			resource.setResourceLoader(resourceLoader);

			try
			{
				if (resource.process())
				{
					if (logWhenFound) logger.debug("Found " + resourceName + " with loader " + resourceLoader.getClassName());

					howOldItWas = resourceLoader.getLastModified(resource);
					break;
				}
			}
			catch (ResourceNotFoundException rnfe)
			{
			}
		}

		//Return null if we can't find a resource.
		if (resource.getData() == null) throw new ResourceNotFoundException("Unable to find resource '" + resourceName + "'");

		resource.setLastModified(howOldItWas);

		resource.setModificationCheckInterval(resourceLoader.getModificationCheckInterval());

		resource.touch();

		return resource;
	}

	/**
	 * Takes an existing resource, and 'refreshes' it. This
	 * generally means that the source of the resource is checked
	 * for changes according to some cache/check algorithm
	 * and if the resource changed, then the resource data is
	 * reloaded and re-parsed.
	 *
	 * @param resource resource to refresh
	 * @throws ResourceNotFoundException if template not found
	 *                                   from current source for this Resource
	 * @throws ParseErrorException       if template cannot be parsed due
	 *                                   to syntax (or other) error.
	 * @throws Exception                 if a problem in parse
	 */
	protected void refreshResource(Resource resource, String encoding) throws ResourceNotFoundException, ParseErrorException, Exception
	{
		/*
		 * The resource knows whether it needs to be checked
		 * or not, and the resource's loader can check to
		 * see if the source has been modified. If both
		 * these conditions are true then we must reload
		 * the input stream and parse it to make a new
		 * AST for the resource.
		 */
		if (resource.requiresChecking())
		{
			resource.touch();

			if (resource.isSourceModified())
			{
				if (!resource.getEncoding().equals(encoding))
				{
					logger.error("Declared encoding for template '" + resource.getName()
							+ "' is different on reload. Old: '" + resource.getEncoding()
							+ "',  New: '" + encoding);

					resource.setEncoding(encoding);
				}

				long howOldItWas = resource.getResourceLoader().getLastModified(resource);

				resource.process();

				resource.setLastModified(howOldItWas);
			}
		}
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
		ResourceLoader resourceLoader = null;

		for (int i = 0; i < resourceLoaders.size(); i++)
		{
			resourceLoader = (ResourceLoader) resourceLoaders.get(i);

			InputStream is = null;

			try
			{
				is = resourceLoader.getResourceStream(resourceName);

				if (is != null) return resourceLoader.getClass().toString();
			}
			catch (ResourceNotFoundException e)
			{
			}
			finally
			{
				if (is != null)
				{
					try
					{
						is.close();
					}
					catch (IOException ioe)
					{
					}
				}
			}
		}

		return null;
	}
}


