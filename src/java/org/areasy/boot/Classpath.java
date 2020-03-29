package org.areasy.boot;

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

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.Vector;


/**
 * Class to handle CLASSPATH construction.
 *
 * @version $Id: Classpath.java,v 1.2 2007/03/29 07:33:02 swd\stefan.damian Exp $
 */
public class Classpath
{
	Vector elements = new Vector();

	/**
	 * Default classpath constructor.
	 */
	public Classpath()
	{
		//default constructor
	}

	/**
	 * Constructor appending an initial classpath in the actual classpath
	 *
	 * @param initial classpath
	 */
	public Classpath(String initial)
	{
		addClasspath(initial);
	}

	/**
	 * Tests if the specified object is a component in this classpath.
	 *
	 * @return <code>true</code> if and only if the specified object
	 *         is the same as a component in this classpath, as determined by the
	 *         <tt>equals</tt> method; <code>false</code> otherwise.
	 */
	public boolean exists(String component)
	{
		return elements.contains(component);
	}

	/**
	 * Add a new component (string file path or url) in the actual classpath.
	 *
	 * @param component general component to be added in the classpath
	 * @return true if the specified component was appended.
	 */
	public boolean addComponent(String component)
	{
		if ((component != null) && (component.length() > 0))
		{
			try
			{
				File f = new File(component);

				if (f.exists())
				{
					File key = f.getCanonicalFile();

					if (!elements.contains(key))
					{
						elements.add(key);
						if (Main.getDebug()) System.err.println("Append in classpath (by string): " + key);

						return true;
					}
				}
			}
			catch (IOException e)
			{
				//nothing to do here
			}
		}

		return false;
	}

	/**
	 * Add a new component (file) in the actual classpath.
	 *
	 * @param component file component to be added in the classpath
	 * @return true if the specified component was appended.
	 */
	public boolean addComponent(File component)
	{
		if (component != null)
		{
			try
			{
				if (component.exists())
				{
					File key = component.getCanonicalFile();
					if (!elements.contains(key))
					{
						elements.add(key);
						if (Main.getDebug()) System.err.println("Append in classpath (by file): " + key);

						return true;
					}
				}
			}
			catch (IOException e)
			{
				//nothing to do here
			}
		}

		return false;
	}

	/**
	 * Append an existent classpath in the actual classpath
	 *
	 * @return true if the specified components was appended.
	 */
	public boolean addClasspath(String s)
	{
		boolean added = false;
		if (s != null)
		{
			StringTokenizer t = new StringTokenizer(s, File.pathSeparator);
			while (t.hasMoreTokens())
			{
				added |= addComponent(t.nextToken());
			}
		}

		return added;
	}

	/**
	 * Get string representation of the actual classpath.
	 */
	public String toString()
	{
		StringBuffer cp = new StringBuffer(1024);
		int cnt = elements.size();
		if (cnt >= 1) cp.append(((File) (elements.elementAt(0))).getPath());

		for (int i = 1; i < cnt; i++)
		{
			cp.append(File.pathSeparatorChar);
			cp.append(((File) (elements.elementAt(i))).getPath());
		}

		return cp.toString();
	}

	/**
	 * Get actual classloader for the actual classpath.
	 */
	public ClassLoader getClassLoader()
	{
		int cnt = elements.size();
		URL[] urls = new URL[cnt];

		for (int i = 0; i < cnt; i++)
		{
			try
			{
				urls[i] = ((File) (elements.elementAt(i))).toURL();
			}
			catch (MalformedURLException e)
			{
				//nothing to do
			}
		}

		ClassLoader parent = Thread.currentThread().getContextClassLoader();

		if (parent == null) parent = Classpath.class.getClassLoader();
		if (parent == null) parent = ClassLoader.getSystemClassLoader();

		if (parent == null) return new BootClassLoader(urls);
			else return new BootClassLoader(urls, parent);
	}

	/**
	 * Loader for the <code>Classpath</code> structure derivated from <code>URLClassLoader</code>
	 */
	public class BootClassLoader extends URLClassLoader
	{
		String name;

		/**
		 * Default constructor.
		 * @param urls an array with <code>URLClassLoader</code> entities
		 */
		BootClassLoader(URL[] urls)
		{
			super(urls);
			name = "Boot: " + Arrays.asList(urls);
		}

		/**
		 * Second constructor using an array with with <code>URLClassLoader</code> entities
		 * and a parent classloader.
		 *
		 * @param urls with <code>URLClassLoader</code> entities
		 * @param parent parent class loader.
		 */
		BootClassLoader(URL[] urls, ClassLoader parent)
		{
			super(urls, parent);
			name = "Boot: " + Arrays.asList(urls);
		}

		/**
		 * Returns a string representation of the object
		 */
		public String toString()
		{
			return name;
		}

		public void addLibrary(File file)
		{
			if(file != null && file.exists())
			{
				try
				{
					addURL(file.toURL());
					if (Main.getDebug()) System.out.println("Append in classpath (by file): " + file.getPath());
				}
				catch (MalformedURLException e)
				{
					//nothing to do
				}
			}
			else if (Main.getDebug()) System.err.println("File doesn't exist: " + (file == null ? "null" : file.getPath()));
		}
	}
}
