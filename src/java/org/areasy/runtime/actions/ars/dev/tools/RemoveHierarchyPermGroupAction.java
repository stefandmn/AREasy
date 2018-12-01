package org.areasy.runtime.actions.ars.dev.tools;

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

import com.bmc.arsys.api.*;
import org.areasy.runtime.RuntimeAction;
import org.areasy.runtime.engine.RuntimeLogger;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.common.data.StringUtility;
import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class RemoveHierarchyPermGroupAction extends SetHierarchyPermGroupAction implements RuntimeAction
{
	private static Logger logger = LoggerFactory.getLog(RemoveHierarchyPermGroupAction.class);

	public void run() throws AREasyException
	{
		List<String> excluded;

		int sourceId = getConfiguration().getInt("source", 0);
		int parentId = getConfiguration().getInt("parent", 0);
		String formName = getConfiguration().getString("form", null);
		List forms = getConfiguration().getList("forms", new ArrayList());
		boolean recursivejoin = getConfiguration().getBoolean("recursivejoin", false);
		boolean skipvalidation = getConfiguration().getBoolean("skipvalidation", true);

		if(formName != null && !forms.contains(formName)) forms.add(formName);

		if(forms.isEmpty() || sourceId <= 0 || parentId <= 0) throw new AREasyException("Invalid input parameters");

		if(getServerConnection().isOverlayMode()) userOverlay = getServerConnection().getContext();
			else userOverlay = getServerConnection().newConnection(true).getContext();

		//define the form list that should be excluded from the process
		if(skipvalidation) excluded = getExclusionList(userOverlay);
			else excluded = new Vector();

		//permission groups validation
		if (!validateGroupId(sourceId, true)) throw new AREasyException("Source group " + sourceId + " is in wrong range");
		if (!validateGroupId(parentId, false)) throw new AREasyException("Parent group " + parentId + " is in wrong range");

		for(int i = 0; i < forms.size(); i++)
		{
			String form = (String) forms.get(i);

			if(StringUtility.isNotEmpty(form) && !excluded.contains(form))
			{
				try
				{
					if(recursivejoin)
					{
						Vector joinforms = new Vector();
						joinforms.addAll( getUpLink(form) );

						for(int j = 0; j < joinforms.size(); j++)
						{
							String join = (String) joinforms.get(j);
							joinforms.addAll( getUpLink(join) );
						}

						for(int j = joinforms.size() - 1; j >= 0; j--)
						{
							String join = (String) joinforms.get(j);

							if (!excluded.contains(join))
							{
								boolean fix = fixForm(join, sourceId, parentId);
								if(fix) RuntimeLogger.info("Permission group " + parentId + " has been removed from '" + join + "' related join form of '" + form  + "' target form");
							}
						}
					}

					boolean fix = fixForm(form, sourceId, parentId);
					if(fix) RuntimeLogger.info("Permission group " + parentId + " has been removed from '" + form + "' target form");
				}
				catch(AREasyException are)
				{
					RuntimeLogger.error("Error removing " + parentId + " permission group for '" + form + "' form (or related join form): " + are.getMessage());
					logger.debug("Exception", are);
				}
			}
			else logger.debug("Form '" + form + "' is skipped because it was found in exceptions list");
		}
	}

	protected boolean fixForm(String formName, int sourceGroupId, int parentGroupId) throws AREasyException
	{
		logger.debug("Remove hierarchic permission group " + parentGroupId + " for '" + formName + "' form name");

		try
		{
			Form form = userOverlay.getForm(formName);

			if (!hasPermissionField(formName, sourceGroupId))
			{
				logger.warn("Form " + formName + " has no source field " + sourceGroupId);
				return false;
			}

			if (hasPermissionField(formName, parentGroupId))
			{
				userOverlay.deleteField(formName, parentGroupId, Constants.AR_FIELD_DATA_DELETE);

				ObjectPropertyMap props = form.getProperties();
				Value dynamicInheritance = props.get(Integer.valueOf(60040));

				if(dynamicInheritance != null)
				{
					String dynamicInheritanceText = dynamicInheritance.toString();

					if (dynamicInheritanceText.contains(sourceGroupId + ":" + parentGroupId))
					{
						dynamicInheritanceText = StringUtility.trim(StringUtility.replace(dynamicInheritanceText, sourceGroupId + ":" + parentGroupId, ""));
						props.put(Integer.valueOf(60040), new Value(dynamicInheritanceText));

						form.setProperties(props);
						userOverlay.setForm(form);
					}
				}

				delRequestIdPermissions(formName, parentGroupId);
				return true;
			}

			return false;
		}
		catch (ARException e)
		{
			throw new AREasyException("Failed to fix form " + formName + ": " + e.getMessage(), e);
		}
	}

	protected void delRequestIdPermissions(String formName, int parentId) throws AREasyException
	{
		try
		{
			CharacterField requestId = (CharacterField) userOverlay.getField(formName, 1);
			List<PermissionInfo> permissions = requestId.getPermissions();

			PermissionInfo fPerm = null;
			boolean hasParent = false;

			for (PermissionInfo perm : permissions)
			{
				if (perm.getGroupID() == parentId)
				{
					hasParent = true;
					fPerm = perm;
				}
			}

			if (hasParent)
			{
				permissions.remove(fPerm);
				requestId.setPermissions(permissions);

				userOverlay.setField(requestId);
			}
		}
		catch (ARException e)
		{
			throw new AREasyException(e);
		}
	}
}
