package org.areasy.runtime.actions.ars.admin;

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

import com.bmc.arsys.api.Constants;
import com.bmc.arsys.api.Timestamp;
import com.bmc.arsys.api.UserInfo;
import com.bmc.arsys.api.UserLicenseInfo;
import org.areasy.common.data.BooleanUtility;
import org.areasy.common.data.StringUtility;
import org.areasy.runtime.RuntimeAction;
import org.areasy.runtime.engine.RuntimeLogger;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.runtime.engine.base.ServerConnection;

import java.util.*;

/**
 * Remedy system monitor action
 */
public class LicensesMonitorAction extends LicensesReportAction implements RuntimeAction
{
	private Map inventoryMap = null;
	private List usageList = null;

	/**
	 * Execute action's workflow.
	 *
	 * @throws org.areasy.runtime.engine.base.AREasyException if any global error occurs. All errors comming from action's execution will become output items
	 */
	public void run() throws AREasyException
	{
		boolean inventory = getConfiguration().getBoolean("inventory", false);
		boolean usage = getConfiguration().getBoolean("usage", false);
		boolean showUsers = getConfiguration().getBoolean("showusers", false);
		if(!inventory && !usage) usage = true;

		//make a short info about this action
		String arServerName = getTargetServerNameOrAlias();
		RuntimeLogger.add("Licenses monitoring report for server '" + arServerName + "', generated today " + new Date() + "\n\n");

		if(usage)
		{
			usageList = new Vector();
			Map users = new Hashtable();
			Map licenses = new Hashtable();

			List arservers = getConfiguration().getVector("arremoteserver", null);

			if(arservers != null && arservers.size() > 1)
			{
				//check if the statistic is done remotely to initiate a new connection
				ServerConnection connections[] = getRemoteServerConnections(true);

				//get info about usage
				setUserLicensesUsage(connections, users, licenses, usageList);

				//check if the statistic is done remotely to dispose initiated connection
				disconnectRemoteServerConnections(connections);
			}
			else
			{
				//check if the statistic is done remotely to initiate a new connection
				ServerConnection connection = getRemoteServerConnection();

				//get info about usage
				setUserLicensesUsage(connection, users, licenses, usageList);

				//check if the statistic is done remotely to dispose initiated connection
				disconnectRemoteServerConnection(connection);
			}

			boolean option = false;

			//compose the output
			String userName = getConfiguration().getString("user", null);
			boolean userBool = BooleanUtility.toBoolean(userName);

			if(StringUtility.isNotEmpty(userName) && !userBool)
			{
				RuntimeLogger.add("\nUser Licenses");

				if(users.containsKey(userName))
				{
					List data = (List) users.get(userName);
					RuntimeLogger.add("\t" + userName);
					for(int i = 0; data != null && i < data.size(); i++) RuntimeLogger.add("\t\t" + data.get(i));
				}
				else RuntimeLogger.warn("User '" + userName + "' doesn't have currently open sessions, or is not registered!");

				option = true;
			}

			String licenseName = getConfiguration().getString("license", null);
			boolean licenseBool = BooleanUtility.toBoolean(licenseName);

			if(StringUtility.isNotEmpty(licenseName) && !licenseBool)
			{
				RuntimeLogger.add("\nLicense Loading");

				if(licenses.containsKey(licenseName))
				{
					List data = (List) licenses.get(licenseName);
					RuntimeLogger.add("\t" + licenseName + ": "  + data.size() + " user(s)");

					if(showUsers) RuntimeLogger.add("\tOnline users (login names) using this type of license: " + StringUtility.join(data.toArray(new String[data.size()]), ", "));
				}
				else RuntimeLogger.warn("License '" + licenseName + "' is not currently used, or is not allocated!");

				option = true;
			}

			if(!option)
			{
				RuntimeLogger.add("\nUsage Details");
				Iterator usernames = users.keySet().iterator();

				while(usernames != null && usernames.hasNext())
				{
					String username = (String) usernames.next();
					List data = (List) users.get(username);
					
					RuntimeLogger.add("\t" + username);
					for(int i = 0; data != null && i < data.size(); i++) RuntimeLogger.add("\t\t" + data.get(i));
				}

				RuntimeLogger.add("\n\tNumber of connected users: " + users.size());
			}
		}

		if(inventory)
		{
			RuntimeLogger.add("\nRegistered Licenses");

			//get server connection
			ServerConnection connection = getRemoteServerConnection();
			inventoryMap = getLicensesInventory(connection);

			//check if the statistic is done remotely to dispose initiated connection
			disconnectRemoteServerConnection(connection);

			if(!inventoryMap.isEmpty())
			{
				Iterator keys = inventoryMap.keySet().iterator();

				while(keys != null && keys.hasNext())
				{
					String key = (String) keys.next();
					LicensesAllocation la = (LicensesAllocation) inventoryMap.get(key);

					RuntimeLogger.add("\t" + key);
					RuntimeLogger.add("\t\t" + "Total: " + la.getTotal());
					RuntimeLogger.add("\t\t" + "Allocated: " + la.getAllocated());
					RuntimeLogger.add("\t\t" + "Available: " + la.getAvailable());
					if(showUsers) RuntimeLogger.add("\t\tUsers (login names): " + StringUtility.join(la.getUsers(), ", "));
				}
			}
		}
	}

	public Map getInventoryMap()
	{
		return inventoryMap;
	}

	public List getUsageList()
	{
		return usageList;
	}

	protected void setUserLicensesUsage(ServerConnection connections[], Map users, Map licenses, List records) throws AREasyException
	{
		for(int i = 0; connections != null && i < connections.length; i++) setInternalUserLicensesUsage(connections[i], users, licenses, records);
	}

	protected void setUserLicensesUsage(ServerConnection connection, Map users, Map licenses, List records) throws AREasyException
	{
		setInternalUserLicensesUsage(connection, users, licenses, records);
	}

	protected void setInternalUserLicensesUsage(ServerConnection connection, Map users, Map licenses, List records) throws AREasyException
	{
		try
		{
			//check if 'currentUsers' structure is already filled, otherwise call utility class to get this structure instance
			List<UserInfo> currentUsers = connection.getContext().getListUser(Constants.AR_USER_LIST_CURRENT);
			String arservername = getTargetServerNameOrAlias();

			if(currentUsers != null)
			{
				for(int i = 0; i < currentUsers.size(); i++)
				{
					//get licenses used now by the selected user.
					List<UserLicenseInfo> licenseinfos = currentUsers.get(i).getLicenseInfo();

					//if is this user skip all licenses for this user
					if(StringUtility.equals(connection.getUserName(), currentUsers.get(i).getUserName())) continue;

					//get, check and store licenses
					for(int j = 0; j < licenseinfos.size(); j++)
					{
						String username = currentUsers.get(i).getUserName().toString();
						String licensename = getLicenseName(licenseinfos.get(j));

						//if license name is null skip actual record
						if(licensename == null || username == null) continue;

						//update user licenses list
						List lofu = (List) users.get(username);

						if(lofu == null)
						{
							lofu = new Vector();
							users.put(username, lofu);
						}

						lofu.add(licensename);

						//update licenses descriptor
						List lofl = (List) licenses.get(licensename);

						if(lofl == null)
						{
							lofl = new Vector();
							licenses.put(licensename, lofl);
						}

						lofl.add(username);

						//data records management
						LicensesUsageData data = new LicensesUsageData();
						data.setServerName(arservername);
						data.setUserName(username);
						data.setLicenseName(licensename);
						data.setConnectionTime(currentUsers.get(i).getConnectionTime());
						data.setLastAccessTime(licenseinfos.get(j).getLastAccess());

						records.add(data);
					}
				}
			}
			else RuntimeLogger.info("No user licenses sessions available");
		}
		catch(Throwable th)
		{
			throw new AREasyException("Error reading used licenses: " + th.getMessage(), th);
		}
	}

	protected Map getLicensesInventory(ServerConnection connection) throws AREasyException
	{
		Map inventoryMap = getLicensesRegistration(connection);

		Map validLicenses = (Map) inventoryMap.get("valid");
		Map temporaryLicenses = (Map) inventoryMap.get("temporary");

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
					validLicenses.put(key, new Integer(tempTotal.intValue() + validTotal.intValue()));
				}
				else validLicenses.put(key, tempTotal);
			}
		}

		return getLicensesAllocation(connection, validLicenses);
	}

	public class LicensesUsageData
	{
		private String serverName = null;
		private String userName = null;
		private String licenseName = null;
		private Timestamp lastAccessTime = null;
		private Timestamp connectionTime = null;

		public String getUserName()
		{
			return userName;
		}

		public void setUserName(String userName)
		{
			this.userName = userName;
		}

		public Timestamp getLastAccessTime()
		{
			return lastAccessTime;
		}

		public void setLastAccessTime(Timestamp lastAccessTime)
		{
			this.lastAccessTime = lastAccessTime;
		}

		public Timestamp getConnectionTime()
		{
			return connectionTime;
		}

		public void setConnectionTime(Timestamp connectionTime)
		{
			this.connectionTime = connectionTime;
		}

		public String getServerName()
		{
			return serverName;
		}

		public void setServerName(String serverName)
		{
			this.serverName = serverName;
		}

		public String getLicenseName()
		{
			return licenseName;
		}

		public void setLicenseName(String licenseName)
		{
			this.licenseName = licenseName;
		}
	}
}
