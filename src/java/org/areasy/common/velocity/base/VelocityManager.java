package org.areasy.common.velocity.base;

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
import org.areasy.common.velocity.context.InternalContextAdapter;
import org.areasy.common.velocity.runtime.RuntimeService;
import org.areasy.common.velocity.runtime.directive.VelocityProxy;
import org.areasy.common.velocity.runtime.parser.node.SimpleNode;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.Hashtable;

/**
 * Manages VMs in namespaces.  Currently, two namespace modes are
 * supported:
 * <p/>
 * <ul>
 * <li>flat - all allowable VMs are in the global namespace</li>
 * <li>local - inline VMs are added to it's own template namespace</li>
 * </ul>
 * <p/>
 *
 * @version $Id: VelocityManager.java,v 1.1 2008/05/25 22:33:17 swd\stefan.damian Exp $
 */
public class VelocityManager
{
	/** the logger */
	private static Logger logger = LoggerFactory.getLog(VelocityManager.class.getName());

	private RuntimeService rsvc = null;
	private static String GLOBAL_NAMESPACE = "";

	private boolean registerFromLib = false;

	/**
	 * Hash of namespace hashes.
	 */
	private Hashtable namespaceHash = new Hashtable();

	/**
	 * map of names of library tempates/namespaces
	 */
	private Hashtable libraryMap = new Hashtable();

	/*
	 * big switch for namespaces.  If true, then properties control
	 * usage. If false, no.
	 */
	private boolean namespacesOn = true;
	private boolean inlineLocalMode = false;

	/**
	 * Adds the global namespace to the hash.
	 */
	VelocityManager(RuntimeService rs)
	{
		this.rsvc = rs;

		addNamespace(GLOBAL_NAMESPACE);
	}

	/**
	 * Adds a VM definition to the cache.
	 *
	 * @return Whether everything went okay.
	 */
	public boolean addVM(String vmName, String macroBody, String argArray[], String namespace)
	{
		MacroEntry me = new MacroEntry(this, vmName, macroBody, argArray, namespace);

		me.setFromLibrary(registerFromLib);

		boolean isLib = true;

		if (registerFromLib) libraryMap.put(namespace, namespace);
			else isLib = libraryMap.containsKey(namespace);

		if (!isLib && usingNamespaces(namespace))
		{
			Hashtable local = getNamespace(namespace, true);
			local.put(vmName, me);

			return true;
		}
		else
		{
			MacroEntry exist = (MacroEntry) getNamespace(GLOBAL_NAMESPACE).get(vmName);

			if (exist != null) me.setFromLibrary(exist.getFromLibrary());

			getNamespace(GLOBAL_NAMESPACE).put(vmName, me);

			return true;
		}
	}

	/**
	 * gets a new living VelocimacroProxy object by the
	 * name / source template duple
	 */
	public VelocityProxy get(String vmName, String namespace)
	{

		if (usingNamespaces(namespace))
		{
			Hashtable local = getNamespace(namespace, false);

			if (local != null)
			{
				MacroEntry me = (MacroEntry) local.get(vmName);

				if (me != null) return me.createVelocity(namespace);
			}
		}

		MacroEntry me = (MacroEntry) getNamespace(GLOBAL_NAMESPACE).get(vmName);

		if (me != null) return me.createVelocity(namespace);

		return null;
	}

	/**
	 * Removes the VMs and the namespace from the manager.
	 * Used when a template is reloaded to avoid
	 * accumulating drek
	 *
	 * @param namespace namespace to dump
	 * @return boolean representing success
	 */
	public boolean dumpNamespace(String namespace)
	{
		synchronized (this)
		{
			if (usingNamespaces(namespace))
			{
				Hashtable h = (Hashtable) namespaceHash.remove(namespace);

				if (h == null) return false;

				h.clear();

				return true;
			}

			return false;
		}
	}

	/**
	 * public switch to let external user of manager to control namespace
	 * usage indep of properties.  That way, for example, at startup the
	 * library files are loaded into global namespace
	 */
	public void setNamespaceUsage(boolean b)
	{
		namespacesOn = b;
	}

	public void setRegisterFromLib(boolean b)
	{
		registerFromLib = b;
	}

	public void setTemplateLocalInlineVM(boolean b)
	{
		inlineLocalMode = b;
	}

	/**
	 * returns the hash for the specified namespace.  Will not create a new one
	 * if it doesn't exist
	 *
	 * @param namespace name of the namespace :)
	 * @return namespace Hashtable of VMs or null if doesn't exist
	 */
	private Hashtable getNamespace(String namespace)
	{
		return getNamespace(namespace, false);
	}

	/**
	 * returns the hash for the specified namespace, and if it doesn't exist
	 * will create a new one and add it to the namespaces
	 *
	 * @param namespace name of the namespace :)
	 * @param addIfNew  flag to add a new namespace if it doesn't exist
	 * @return namespace Hashtable of VMs or null if doesn't exist
	 */
	private Hashtable getNamespace(String namespace, boolean addIfNew)
	{
		Hashtable h = (Hashtable) namespaceHash.get(namespace);

		if (h == null && addIfNew) h = addNamespace(namespace);

		return h;
	}

	/**
	 * adds a namespace to the namespaces
	 *
	 * @param namespace name of namespace to add
	 * @return Hash added to namespaces, ready for use
	 */
	private Hashtable addNamespace(String namespace)
	{
		Hashtable h = new Hashtable();
		Object oh;

		if ((oh = namespaceHash.put(namespace, h)) != null)
		{
			/*
			 * There was already an entry on the table, restore it!
			 * This condition should never occur, given the code
			 * and the fact that this method is private.
			 * But just in case, this way of testing for it is much
			 * more efficient than testing before hand using get().
			 */
			namespaceHash.put(namespace, oh);

			/*
			 * Should't we be returning the old entry (oh)?
			 * The previous code was just returning null in this case.
			 */
			return null;
		}

		return h;
	}

	/**
	 * determines if currently using namespaces.
	 *
	 * @param namespace currently ignored
	 * @return true if using namespaces, false if not
	 */
	private boolean usingNamespaces(String namespace)
	{
		if (!namespacesOn) return false;

		if (inlineLocalMode) return true;

		return false;
	}

	public String getLibraryName(String vmName, String namespace)
	{
		if (usingNamespaces(namespace))
		{
			Hashtable local = getNamespace(namespace, false);

			if (local != null)
			{
				MacroEntry me = (MacroEntry) local.get(vmName);

				if (me != null) return null;
			}
		}

		/*
		 * if we didn't return from there, we need to simply see
		 * if it's in the global namespace
		 */

		MacroEntry me = (MacroEntry) getNamespace(GLOBAL_NAMESPACE).get(vmName);

		if (me != null) return me.getSourceTemplate();

		return null;
	}


	/**
	 * wrapper class for holding VM information
	 */
	protected class MacroEntry
	{
		String macroname;
		String[] argarray;
		String macrobody;
		String sourcetemplate;
		SimpleNode nodeTree = null;
		VelocityManager manager = null;
		boolean fromLibrary = false;

		MacroEntry(VelocityManager vmm, String vmName, String macroBody, String argArray[], String sourceTemplate)
		{
			this.macroname = vmName;
			this.argarray = argArray;
			this.macrobody = macroBody;
			this.sourcetemplate = sourceTemplate;
			this.manager = vmm;
		}

		public void setFromLibrary(boolean b)
		{
			fromLibrary = b;
		}

		public boolean getFromLibrary()
		{
			return fromLibrary;
		}

		public SimpleNode getNodeTree()
		{
			return nodeTree;
		}

		public String getSourceTemplate()
		{
			return sourcetemplate;
		}

		VelocityProxy createVelocity(String namespace)
		{
			VelocityProxy vp = new VelocityProxy();
			vp.setName(this.macroname);
			vp.setArgArray(this.argarray);
			vp.setMacrobody(this.macrobody);
			vp.setNodeTree(this.nodeTree);
			vp.setNamespace(namespace);
			return vp;
		}

		void setup(InternalContextAdapter ica)
		{
			if (nodeTree == null) parseTree(ica);
		}

		void parseTree(InternalContextAdapter ica)
		{
			try
			{
				BufferedReader br = new BufferedReader(new StringReader(macrobody));

				nodeTree = rsvc.parse(br, "VM:" + macroname, true);
				nodeTree.init(ica, null);
			}
			catch (Exception e)
			{
				logger.error("VelocimacroManager.parseTree() exception " + macroname + " : " + e.getMessage());
				logger.debug("Exception", e);
			}
		}
	}
}
