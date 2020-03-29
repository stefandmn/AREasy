package org.areasy.runtime.actions.ars.admin;

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

import com.bmc.arsys.api.*;
import org.areasy.common.data.DateUtility;
import org.areasy.common.data.StringUtility;
import org.areasy.common.data.type.map.ListOrderedMap;
import org.areasy.runtime.RuntimeAction;
import org.areasy.runtime.actions.AbstractAction;
import org.areasy.runtime.engine.RuntimeLogger;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.runtime.engine.base.ServerConnection;
import org.areasy.runtime.engine.structures.CoreItem;

import java.util.*;

/**
 * Remedy licenses report action
 */
public class LicensesReportAction extends AbstractAction implements RuntimeAction
{
	private List users = null;
	private Map usageMap = null;
	private Map allocationMap = null;
	private Map registrationMap = null;

	/**
	 * Execute action's workflow.
	 *
	 * @throws org.areasy.runtime.engine.base.AREasyException if any global error occurs. All errors comming from action's execution will become output items
	 */
	public void run() throws AREasyException
	{
		StringBuffer buffer = new StringBuffer();
		boolean registration = getConfiguration().getBoolean("registration", false);
		boolean allocation = getConfiguration().getBoolean("allocation", false);
		boolean usage = getConfiguration().getBoolean("usage", false);
		boolean showusers = getConfiguration().getBoolean("showusers", false);
		if(!registration && !allocation && !usage) usage = true;

		//make a short info about this action
		String arservername = getTargetServerNameOrAlias();
		buffer.append("Licenses Report for server '").append(arservername).append("', created on ").append(new Date()).append("\n\n");

		//get a complete licenses inventory
		if(registration)
		{
			buffer.append("\nRegistered licenses:\n");

			//check if the statistic is done remotely to initiate a new connection
			ServerConnection connection = getRemoteServerConnection();
			registrationMap = getLicensesRegistration(connection);

			Map validLicenses = (Map) getRegistrationMap().get("valid");
			Map temporaryLicenses = (Map) getRegistrationMap().get("temporary");
			Map expiredLicenses = (Map) getRegistrationMap().get("expired");

			//display licenses categories
			if(!validLicenses.isEmpty())
			{
				buffer.append("\tValid licenses:\n");
				Iterator keys = validLicenses.keySet().iterator();

				while(keys != null && keys.hasNext())
				{
					String key = (String) keys.next();
					Integer total = (Integer) validLicenses.get(key);

					buffer.append("\t\t").append(key).append(": ").append(total).append("\n");
				}
			}

			if(!temporaryLicenses.isEmpty())
			{
				buffer.append("\tTemporary licenses:\n");
				Iterator keys = temporaryLicenses.keySet().iterator();

				while(keys != null && keys.hasNext())
				{
					String key = (String) keys.next();
					Integer total = (Integer) temporaryLicenses.get(key);

					buffer.append("\t\t").append(key).append(": ").append(total).append("\n");
				}
			}

			if(!expiredLicenses.isEmpty())
			{
				buffer.append("\tExpired licenses:\n");
				Iterator keys = expiredLicenses.keySet().iterator();

				while(keys != null && keys.hasNext())
				{
					String key = (String) keys.next();
					Integer total = (Integer) expiredLicenses.get(key);

					buffer.append("\t\t").append(key).append(": ").append(total).append("\n");
				}
			}

			//check if the statistic is done remotely to dispose initiated connection
			disconnectRemoteServerConnection(connection);
		}

		//get allocation licenses structure
		if(allocation)
		{
			buffer.append("\nAllocated licenses:\n");

			//check if the statistic is done remotely to initiate a new connection
			ServerConnection connection = getRemoteServerConnection();
			if(registrationMap == null) registrationMap = getLicensesRegistration(connection);

			Map validLicenses = (Map) registrationMap.get("valid");
			Map temporaryLicenses = (Map) registrationMap.get("temporary");

			if(!temporaryLicenses.isEmpty())
			{
				Iterator keys = temporaryLicenses.keySet().iterator();

				while(keys != null && keys.hasNext())
				{
					String key = (String) keys.next();
					Integer tempTotal = (Integer) temporaryLicenses.get(key);

					if(validLicenses.containsKey(key))
					{
						Integer validTotal = (Integer) validLicenses.get(key);
						validLicenses.put(key, new Integer(tempTotal.intValue() +validTotal.intValue()));
					}
					else validLicenses.put(key, tempTotal);
				}
			}

			allocationMap = getLicensesAllocation(connection, validLicenses);

			if(!allocationMap.isEmpty())
			{
				Iterator keys = allocationMap.keySet().iterator();

				while(keys != null && keys.hasNext())
				{
					String key = (String) keys.next();
					LicensesAllocation la = (LicensesAllocation) allocationMap.get(key);

					buffer.append("\t").append(key).append("\n");
					buffer.append("\t\t" + "Total: ").append(la.getTotal()).append("\n");
					buffer.append("\t\t" + "Allocated: ").append(la.getAllocated()).append("\n");
					if(key.toLowerCase().indexOf("floating", 0) < 0) buffer.append("\t\t" + "Available: ").append(la.getAvailable()).append("\n");

					if(showusers) buffer.append("\t\tUsers (login names): ").append(StringUtility.join(la.getUsers(), ", ")).append("\n");
				}
			}

			//check if the statistic is done remotely to dispose initiated connection
			disconnectRemoteServerConnection(connection);
		}

		//get used licenses inventory
		if(usage)
		{
			buffer.append("\nLicenses usage:\n");
			users = new ArrayList();
			List arservers = getConfiguration().getVector("arremoteserver", null);

			if(arservers != null && arservers.size() > 1)
			{
				//check if the statistic is done remotely to initiate a new connection
				ServerConnection connections[] = getRemoteServerConnections(true);

				usageMap = getUsedLicenses(connections, users);

				//check if the statistic is done remotely to dispose initiated connection
				disconnectRemoteServerConnections(connections);
			}
			else
			{
				//check if the statistic is done remotely to initiate a new connection
				ServerConnection connection = getRemoteServerConnection();

				usageMap = getUsedLicenses(connection, users);

				//check if the statistic is done remotely to dispose initiated connection
				disconnectRemoteServerConnection(connection);
			}

			buffer.append("\tDischarged licenses:\n");

			if(!usageMap.isEmpty())
			{
				Iterator keys = usageMap.keySet().iterator();

				while(keys != null && keys.hasNext())
				{
					String key = (String) keys.next();
					Integer total = (Integer) usageMap.get(key);

					buffer.append("\t\t").append(key).append(": ").append(total).append("\n");
				}
			}

			buffer.append("\n\tNumber of connected users: ").append(users.size()).append("\n");
			if(showusers) buffer.append("\tOnline users (login names): ").append(StringUtility.join(users.toArray(new String[users.size()]), ", ")).append("\n");
		}

		//publish obtained content.
		RuntimeLogger.add(buffer.toString());
	}

	/**
	 * Read and organize licenses
	 *
	 * @param connection server connection instance
	 * @return a map with all registered licenses and quantities
	 * @throws AREasyException if any error will occur
	 */
	protected Map getLicensesRegistration(ServerConnection connection) throws AREasyException
	{
		Map inventory = new ListOrderedMap();

		Map validLicenses = new ListOrderedMap();
		Map temporaryLicenses = new ListOrderedMap();
		Map expiredLicenses = new ListOrderedMap();

		try
		{
			List<LicenseInfo> allLicenses = connection.getContext().getListLicense(null);

			if(allLicenses != null)
			{
				Date currentDate = DateUtility.round(new Date(), Calendar.HOUR);

				for(int i = 0; i < allLicenses.size(); i++)
				{
					LicenseInfo license = allLicenses.get(i);
					LicenseValidInfo validation = connection.getContext().validateLicense(license.getlicType());

					int number = license.getNumLicenses();
					String type = license.getlicType();

					Date expireDate = null;
					Date bufferDate = new Date(70, 0, 1);

					Date licenseExpireDate = new Date(license.getExpireDate().getYear(), license.getExpireDate().getMonth(), license.getExpireDate().getDate());
					Date validationExpireDate = new Date(validation.getExpireDate().getYear(), validation.getExpireDate().getMonth(), validation.getExpireDate().getDate());

					if(validationExpireDate.compareTo(licenseExpireDate) != 0)
					{
						if(validationExpireDate.compareTo(licenseExpireDate) < 0) expireDate = licenseExpireDate;
							else expireDate = validationExpireDate;
					}
					else expireDate = validationExpireDate; 

					//gathering licensing in three categories: permanent, temporary, expired
					if(bufferDate.compareTo(expireDate) != 0)
					{
						if(expireDate.compareTo(currentDate) < 0)
						{
							Object qty = expiredLicenses.get(type);

							if(qty == null)
							{
								expiredLicenses.put(type, new Integer(number));
							}
							else
							{
								int sum = number + ((Integer)qty).intValue();
								expiredLicenses.put(type, new Integer(sum));
							}
						}
						else
						{
							Object qty = temporaryLicenses.get(type);

							if(qty == null)
							{
								temporaryLicenses.put(type, new Integer(number));
							}
							else
							{
								int sum = number + ((Integer)qty).intValue();
								temporaryLicenses.put(type, new Integer(sum));
							}
						}
					}
					else
					{
						Object qty = validLicenses.get(type);

						if(qty == null)
						{
							validLicenses.put(type, new Integer(number));
						}
						else
						{
							int sum = number + ((Integer)qty).intValue();
							validLicenses.put(type, new Integer(sum));
						}
					}
				}

				inventory.put("valid", validLicenses);
				inventory.put("temporary", temporaryLicenses);
				inventory.put("expired", expiredLicenses);
			}
		}
		catch(Throwable th)
		{
			throw new AREasyException("Error reading licenses inventory: " + th.getMessage(), th);
		}

		return inventory;
	}

	/**
	 * Get and display how many licenses are in total, how many are allocated and and how are unset yet.
	 *
	 * @param connection server connection
	 * @param map licenses inventory
	 * @return a map with the allocation structure.
	 * @throws AREasyException if any error will occur
	 */
	protected Map getLicensesAllocation(ServerConnection connection, Map map) throws AREasyException
	{
		Map inventory = new ListOrderedMap();

		try
		{
			if(map != null && !map.isEmpty())
			{
				Iterator keys = map.keySet().iterator();

				while(keys != null && keys.hasNext())
				{
					String key = (String) keys.next();
					Integer total = (Integer) map.get(key);

					String license = key;
					if(license.endsWith("Fixed") || license.endsWith("Floating")) license += " Write";
						else if (license.equalsIgnoreCase("AR User")) license += " Fixed Write";

					LicensesAllocation allocation = new LicensesAllocation();

					if(total != null && key != null && total.intValue() > 0)
					{
						int indexUser = key.indexOf("User", 0);

						if(indexUser > 0)
						{
							allocation.setName(license);
							allocation.setTotal(total.intValue());

							CoreItem pool = new CoreItem();
							pool.setFormName("User");
							//pool.setAttribute(7, new Integer(0));

							if(key.startsWith("AR User"))
							{
								if(StringUtility.equalsIgnoreCase("AR User", key) || StringUtility.equalsIgnoreCase("AR User Fixed", key)) pool.setAttribute(109, new Integer(1));
									else if(StringUtility.equalsIgnoreCase("AR User Floating", key)) pool.setAttribute(109, new Integer(2));
							}
							else pool.setAttribute(122, "%" + key + "%");

							List items = pool.search(connection);

							for(int i = 0; items != null && i < items.size(); i++)
							{
								CoreItem item = (CoreItem) items.get(i);
								String username = item.getStringAttributeValue(101);

								allocation.addUser(username);
							}

							//put allocation structure in the output map
							inventory.put(license, allocation);
						}
					}
				}
			}
		}
		catch(Throwable th)
		{
			throw new AREasyException("Error reading and composing licenses allocation structure: " + th.getMessage(), th);
		}

		return inventory;
	}

	/**
	 * Read connected users and what licenses are consumed right now.
	 *
	 * @param connection server connection instance
	 * @param users here will be returned the list with all connected users
	 * @return a map with all consumed licenses and quantities
	 * @throws AREasyException if any error will occur
	 */
	private Map getInternalUsedLicenses(ServerConnection connection, List users) throws AREasyException
	{
		Map inventory = new ListOrderedMap();

		try
		{
			//check if 'currentUsers' structure is already filled, otherwise call utility class to get this structure instance
			List<UserInfo> currentUsers = connection.getContext().getListUser(Constants.AR_USER_LIST_CURRENT);

			if(currentUsers != null)
			{
				if(users == null) users = new ArrayList();
					else users.clear();

				for(int i = 0; i < currentUsers.size(); i++)
				{
					List<UserLicenseInfo> licenseinfos = currentUsers.get(i).getLicenseInfo();
					String username = currentUsers.get(i).getUserName();

					//store user name.
					if(!users.contains(username)) users.add(username);

					for(int x = 0; licenseinfos != null && x < licenseinfos.size(); x++)
					{
						String licname = getLicenseName(licenseinfos.get(x));

						if(licname != null)
						{
							Object qty = inventory.get(licname);

							if(qty == null) inventory.put(licname, new Integer(1));
							else
							{
								int sum = 1 + ((Integer)qty).intValue();
								inventory.put(licname, new Integer(sum));
							}
						}
					}
				}
			}
		}
		catch(Throwable th)
		{
			throw new AREasyException("Error reading used licenses: " + th.getMessage(), th);
		}

		return inventory;
	}

	/**
	 * Read connected users and what licenses are consumed right now.
	 *
	 * @param connection server connection instance
	 * @param users here will be returned the list with all connected users
	 * @return a map with all consumed licenses and quantities
	 * @throws AREasyException if any error will occur
	 */
	protected Map getUsedLicenses(ServerConnection connection, List users) throws AREasyException
	{
		return getInternalUsedLicenses(connection, users);
	}

	/**
	 * Read connected users and what licenses are consummed right now.
	 *
	 * @param connections server connection instances
	 * @param users here will be retured the list with all connected users
	 * @return a map with all consummed licenses and quantities
	 * @throws AREasyException if any erro will occur
	 */
	protected Map getUsedLicenses(ServerConnection connections[], List users) throws AREasyException
	{
		List usersList = new ArrayList();
		Map inventory = getInternalUsedLicenses(connections[0], users);

		for(int i = 1; i < connections.length; i++)
		{
			Map map = getInternalUsedLicenses(connections[i], usersList);

			//merge quantities from different servers
			Iterator iterator = map.keySet().iterator();
			while(iterator != null && iterator.hasNext())
			{
				String licname = (String) iterator.next();
				Integer licqty = (Integer) map.get(licname);

				if(inventory.containsKey(licname)) inventory.put(licname, new Integer( ((Integer)inventory.get(licname)).intValue() + licqty.intValue() ));
					else inventory.put(licname, licqty);
			}

			//append user lists.
			users.addAll(usersList);
		}

		return inventory;
	}

	protected String getLicenseName(UserLicenseInfo licenseinfo)
	{
		String name = null;
		String suffix = null;

		int tag = licenseinfo.getLicenseTag();
		int type = licenseinfo.getLicenseType();
		int current = licenseinfo.getCurrentLicenseType();

		if(tag == Constants.AR_LICENSE_TAG_FULL_TEXT) name = "AR User Full Text";
			else if(tag == Constants.AR_LICENSE_TAG_WRITE) name = "AR User";
				else if(tag == Constants.AR_LICENSE_TAG_RESERVED1) return null;
					else name = "";

		if(licenseinfo.getAppLicenseDescriptor() != null)
		{
			name += licenseinfo.getAppLicenseDescriptor();
			if(name != null && !name.endsWith("User")) name += " User";
		}

		if(type == Constants.AR_LICENSE_TYPE_FIXED || type == Constants.AR_LICENSE_TYPE_FIXED2)
		{
			suffix = "Fixed";

			if(current == Constants.AR_LICENSE_TYPE_FIXED || current == Constants.AR_LICENSE_TYPE_FIXED2) suffix += " Write";
				else  suffix += " Read";
		}
		else if(type == Constants.AR_LICENSE_TYPE_FLOATING)
		{
			suffix = "Floating";
			
			if(current == Constants.AR_LICENSE_TYPE_FLOATING) suffix += " Write";
				else  suffix += " Read";
		}
		else suffix = "Read";

		if(StringUtility.isNotEmpty(name)) if(StringUtility.isNotEmpty(suffix)) name += " " + suffix;

		return name;
	}

	/**
	 * Get a help text about syntaxt execution of the current action.
	 *
	 * @return text message specifying the syntaxt of the current action
	 */

	public final Map getUsageMap()
	{
		return usageMap;
	}

	public final List getUsersList()
	{
		return users;
	}

	public final Map getAllocationMap()
	{
		return allocationMap;
	}

	public final Map getRegistrationMap()
	{
		return registrationMap;
	}

	public class LicensesAllocation
	{
		/** Licenses name */
		private String name = null;

		/** Total number of registered licenses */
		private int total = 0;
		/** Total allocated licenses */
		private int allocated = 0;

		/** List of users which have allocated this type of licenses */
		private List users = new Vector();

		public String getName()
		{
			return name;
		}

		public void setName(String name)
		{
			this.name = name;
		}

		public int getTotal()
		{
			return total;
		}

		public void setTotal(int total)
		{
			this.total = total;
		}

		public int getAllocated()
		{
			return allocated;
		}

		public int getAvailable()
		{
			return this.total - this.allocated;
		}

		public void addUser(String username)
		{
			if(!users.contains(username))
			{
				allocated++;
				users.add(username);
			}
		}

		public void addAllUsers(List users)
		{
			addAllUsers(users, false);
		}

		public void addAllUsers(List users, boolean filtered)
		{
			if(filtered)
			{
				if(users != null)
				{
					allocated = allocated + users.size();
					this.users.addAll(users);
				}
			}
			else
			{
				for(int i = 0; users != null && i < users.size(); i++)
				{
					String username = (String) users.get(i);

					if(StringUtility.isNotEmpty(username) && !this.users.contains(username))
					{
						allocated++;
						this.users.add(username);
					}
				}
			}
		}

		public String[] getUsers()
		{
			return (String[]) this.users.toArray(new String[users.size()]);
		}

		public void clean()
		{
			total = 0;
			allocated = 0;
			this.users.clear();
		}
	}
}
