package org.areasy.common.velocity.base;

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

import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;
import org.areasy.common.velocity.runtime.RuntimeService;
import org.areasy.common.velocity.runtime.directive.Directive;
import org.areasy.common.velocity.runtime.directive.VelocityProxy;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * Velocity macro factory.
 * <p/>
 * Manages the set of VMs in a running Velocity engine.
 *
 * @version $Id: VelocityFactory.java,v 1.1 2008/05/25 22:33:17 swd\stefan.damian Exp $
 */
public class VelocityFactory
{
	/** the logger */
	private static Logger logger = LoggerFactory.getLog(VelocityFactory.class.getName());

	/**
	 * runtime services for this instance
	 */
	private RuntimeService rsvc = null;

	/**
	 * VMManager : deal with namespace management
	 * and actually keeps all the VM definitions
	 */
	private VelocityManager vmManager = null;

	/**
	 * determines if replacement of global VMs are allowed
	 * controlled by  VM_PERM_ALLOW_INLINE_REPLACE_GLOBAL
	 */
	private boolean replaceAllowed = false;

	/**
	 * controls if new VMs can be added.  Set by
	 * VM_PERM_ALLOW_INLINE  Note the assumption that only
	 * through inline dev can this happen.
	 * additions through autoloaded VMs is allowed
	 */
	private boolean addNewAllowed = true;

	/**
	 * sets if template-local namespace in used
	 */
	private boolean templateLocal = false;

	/**
	 * controls log output
	 */
	private boolean blather = false;

	/**
	 * determines if the libraries are auto-loaded
	 * when they change
	 */
	private boolean autoReloadLibrary = false;

	/**
	 * vector of the library names
	 */
	private Vector macroLibVec = null;

	/**
	 * map of the library Template objects
	 * used for reload determination
	 */
	private Map libModMap;

	public VelocityFactory(RuntimeService rs)
	{
		this.rsvc = rs;

		/*
		 *  we always access in a synchronized(), so we
		 *  can use an unsynchronized hashmap
		 */
		libModMap = new HashMap();
		vmManager = new VelocityManager(rsvc);
	}

	/**
	 * initialize the factory - setup all permissions
	 * load all global libraries.
	 */
	public void initVelocity()
	{
		synchronized (this)
		{
			setReplacementPermission(true);
			setBlather(true);

			vmManager.setNamespaceUsage(false);

			macroLibVec = rsvc.getVector("macro.libraries");

			if (macroLibVec != null)
			{
				for (int i = 0; i < macroLibVec.size(); i++)
				{
					String lib = (String) macroLibVec.elementAt(i);

					if (lib != null && !lib.equals(""))
					{
						vmManager.setRegisterFromLib(true);
						logger.debug("Adding VMs from library template : " + lib);

						try
						{
							Template template = rsvc.getTemplate(lib);

							Twonk twonk = new Twonk();
							twonk.template = template;

							twonk.modificationTime = template.getLastModified();
							libModMap.put(lib, twonk);
						}
						catch (Exception e)
						{
							logger.debug("Error using VM library template '" + lib + "' : " + e.getMessage());
						}

						logger.debug("VM library template macro registration complete.");

						vmManager.setRegisterFromLib(false);
					}
				}
			}

			setAddMacroPermission(true);
			if (!rsvc.getBoolean("macro.permissions.allow.inline", true))
			{
				setAddMacroPermission(false);

				logger.debug("VMs can not be defined inline in templates");
			}
			else logger.debug("VMs can be defined inline in templates");

			setReplacementPermission(false);
			if (rsvc.getBoolean("macro.permissions.allow.inline.to.replace.global", false))
			{
				setReplacementPermission(true);

				logger.debug("VMs defined inline may replace previous VM definitions");
			}
			else logger.debug("VMs defined inline may NOT replace previous VM definitions");

			vmManager.setNamespaceUsage(true);

			setTemplateLocalInline(rsvc.getBoolean("macro.permissions.allow.inline.local.scope", false));
			if (getTemplateLocalInline()) logger.debug("VMs defined inline will be local to their defining template only.");
				else logger.debug("VMs defined inline will be  global in scope if allowed.");

			vmManager.setTemplateLocalInlineVM(getTemplateLocalInline());

			setBlather(rsvc.getBoolean("macro.messages.on", true));

			if (getBlather()) logger.debug("VM system will output logging messages");
				else logger.debug("VM system will be quiet");

			setAutoload(rsvc.getBoolean("macro.libraries.autoreload", false));

			if (getAutoload()) logger.debug("VM system will automatically reload global library macros");
				else logger.debug("VM system will not automatically reload global library macros");
		}
	}

	/**
	 * adds a macro to the factory.
	 */
	public boolean addVelocityMacro(String name, String macroBody, String argArray[], String sourceTemplate)
	{
		/*
		 * maybe we should throw an exception, maybe just tell
		 * the caller like this...
		 *
		 * I hate this : maybe exceptions are in order here...
		 */
		if (name == null || macroBody == null || argArray == null || sourceTemplate == null)
		{
			logger.warn("VM addition rejected: programmer error : arg null");
			return false;
		}

		// see if the current ruleset allows this addition
		if (!canAddVelocity(name, sourceTemplate)) return false;

		// seems like all is good.  Lets do it.
		synchronized (this)
		{
			vmManager.addVM(name, macroBody, argArray, sourceTemplate);
		}

		// if we are to blather, blather...
		if (blather)
		{
			String s = "#" + argArray[0];
			s += "(";

			for (int i = 1; i < argArray.length; i++)
			{
				s += " ";
				s += argArray[i];
			}

			s += " ) : source = ";
			s += sourceTemplate;

			logger.debug("Added new VM : " + s);
		}

		return true;
	}

	/**
	 * determines if a given macro/namespace (name, source) combo is allowed
	 * to be added
	 *
	 * @param name           Name of VM to add
	 * @param sourceTemplate Source template that contains the defintion of the VM
	 * @return true if it is allowed to be added, false otherwise
	 */
	private boolean canAddVelocity(String name, String sourceTemplate)
	{
		// short circuit and do it if autoloader is on, and the template is one of the library templates
		if (getAutoload())
		{
			for (int i = 0; i < macroLibVec.size(); i++)
			{
				String lib = (String) macroLibVec.elementAt(i);

				if (lib.equals(sourceTemplate))
				{
					return true;
				}
			}
		}


		/*
		 * maybe the rules should be in manager?  I dunno. It's to manage
		 * the namespace issues first, are we allowed to add VMs at all?
		 * This trumps all.
		 */
		if (!addNewAllowed)
		{
			logger.warn("VM addition rejected: '" + name + "' : inline VMs not allowed.");
			return false;
		}

		// are they local in scope?  Then it is ok to add.
		if (!templateLocal)
		{
			/*
			 * otherwise, if we have it already in global namespace, and they can't replace
			 * since local templates are not allowed, the global namespace is implied.
			 *  remember, we don't know anything about namespace managment here, so lets
			 *  note do anything fancy like trying to give it the global namespace here
			 *
			 *  so if we have it, and we aren't allowed to replace, bail
			 */
			if (isVelocityMacro(name, sourceTemplate) && !replaceAllowed)
			{
				logger.warn("VM addition rejected: '" + name + "' : inline not allowed to replace existing VM");
				return false;
			}
		}

		return true;
	}

	/**
	 * Tells the world if a given directive string is a Velocimacro
	 */
	public boolean isVelocityMacro(String vm, String sourceTemplate)
	{
		synchronized (this)
		{
			/*
			 * first we check the locals to see if we have
			 * a local definition for this template
			 */
			if (vmManager.get(vm, sourceTemplate) != null)
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * actual factory : creates a Directive that will
	 * behave correctly wrt getting the framework to
	 * dig out the correct # of args
	 */
	public Directive getVelocityMacro(String vmName, String sourceTemplate)
	{
		VelocityProxy vp = null;

		synchronized (this)
		{
			vp = vmManager.get(vmName, sourceTemplate);

			if (vp != null && getAutoload())
			{
				String lib = vmManager.getLibraryName(vmName, sourceTemplate);

				if (lib != null)
				{
					try
					{
						Twonk tw = (Twonk) libModMap.get(lib);

						if (tw != null)
						{
							Template template = tw.template;

							long tt = tw.modificationTime;
							long ft = template.getResourceLoader().getLastModified(template);

							if (ft > tt)
							{
								logger.info("Autoload reload for VMs from VM library template : " + lib);

								tw.modificationTime = ft;

								template = rsvc.getTemplate(lib);

								tw.template = template;
								tw.modificationTime = template.getLastModified();
							}
						}
					}
					catch (Exception e)
					{
						logger.error("Error using  VM ibrary template '" + lib + "' : " + e.getMessage());
					}

					vp = vmManager.get(vmName, sourceTemplate);
				}
			}
		}

		return vp;
	}

	/**
	 * tells the vmManager to dump the specified namespace
	 */
	public boolean dumpVMNamespace(String namespace)
	{
		return vmManager.dumpNamespace(namespace);
	}

	/**
	 * sets permission to have VMs local in scope to their declaring template
	 * note that this is really taken care of in the VMManager class, but
	 * we need it here for gating purposes in addVM
	 * eventually, I will slide this all into the manager, maybe.
	 */
	private void setTemplateLocalInline(boolean b)
	{
		templateLocal = b;
	}

	private boolean getTemplateLocalInline()
	{
		return templateLocal;
	}

	/**
	 * sets the permission to add new macros
	 */
	private boolean setAddMacroPermission(boolean arg)
	{
		boolean b = addNewAllowed;

		addNewAllowed = arg;
		return b;
	}

	/**
	 * sets the permission for allowing addMacro() calls to
	 * replace existing VM's
	 */
	private boolean setReplacementPermission(boolean arg)
	{
		boolean b = replaceAllowed;
		replaceAllowed = arg;
		return b;
	}

	/**
	 * set output message mode
	 */
	private void setBlather(boolean b)
	{
		blather = b;
	}

	/**
	 * get output message mode
	 */
	private boolean getBlather()
	{
		return blather;
	}

	/**
	 * set the switch for automatic reloading of
	 * global library-based VMs
	 */
	private void setAutoload(boolean b)
	{
		autoReloadLibrary = b;
	}

	/**
	 * get the switch for automatic reloading of
	 * global library-based VMs
	 */
	private boolean getAutoload()
	{
		return autoReloadLibrary;
	}

	/**
	 * small continer class to hold the duple
	 * of a template and modification time.
	 * We keep the modification time so we can
	 * 'override' it on a reload to prevent
	 * recursive reload due to inter-calling
	 * VMs in a library
	 */
	private class Twonk
	{
		public Template template;
		public long modificationTime;
	}
}







