package org.areasy.runtime.actions.arserver.data.tools.flow.sources;

/*
 * Copyright (c) 2007-2016 AREasy Runtime
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

import org.areasy.runtime.engine.RuntimeLogger;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.*;
import java.util.*;

/**
 * Dedicated data source to process data from a LDAP server.
 */
public class LDAPSource extends AbstractSource
{
	/** Library logger */
	protected static Logger logger = LoggerFactory.getLog(LDAPSource.class);

	private LdapContext context = null;
	private String qualification = null;
	private String baseDN = null;

	private String securityAuthentication = "simple";
	private String factorInitial = "com.sun.jndi.ldap.LdapCtxFactory";
	private int pageSize = 1000;
	private String oidName= "cn";

	private Hashtable environment = null;
	private NamingEnumeration searchAnswer = null;
	private SearchControls searchControl = null;

	/**
	 * Dedicated method that has to be used internally, to set and validate the data-source configuration (<code>CoreItem</code> structure)
	 *
	 * @throws org.areasy.runtime.engine.base.AREasyException in case of any error will occur
	 */
	public void init() throws AREasyException
	{
		oidName = getAction().getConfiguration().getString("oidname", getAction().getManager().getConfiguration().getString("app.runtime.action." + getAction().getCode() + ".ldapcontext.search.oidname", "cn"));
		pageSize = getAction().getConfiguration().getInt("pagesize", getAction().getManager().getConfiguration().getInt("app.runtime.action." + getAction().getCode() + ".ldapcontext.search.pagesize", 1000));
		securityAuthentication = getAction().getConfiguration().getString("securityauthentication", getAction().getManager().getConfiguration().getString("app.runtime.action." + getAction().getCode() + ".ldapcontext.security.authentication", "simple"));
		factorInitial = getAction().getConfiguration().getString("factoryinitial", getAction().getManager().getConfiguration().getString("app.runtime.action." + getAction().getCode() + ".ldapcontext.factory.initial", "com.sun.jndi.ldap.LdapCtxFactory"));

		String ldapURL = "ldap://";
		String ldapServer = getSourceItem().getStringAttributeValue(536870921);
		String ldapPort = getSourceItem().getStringAttributeValue(536870924);
		String ldapUser = getSourceItem().getStringAttributeValue(536870925);
		String ldapPassword = getSourceItem().getStringAttributeValue(536870926);

		setBaseDN(getSourceItem().getStringAttributeValue(536870927));
		setQualification(getSourceItem().getStringAttributeValue(536870928));

		if(ldapPort == null) ldapPort = "389";
		ldapURL += ldapServer + ":" + ldapPort;

		environment = new Hashtable();
		environment.put(Context.SECURITY_AUTHENTICATION, securityAuthentication);
		environment.put(Context.INITIAL_CONTEXT_FACTORY, factorInitial);
		environment.put(Context.PROVIDER_URL, ldapURL);
		environment.put(Context.REFERRAL, "follow");

		if(ldapUser != null) environment.put(Context.SECURITY_PRINCIPAL, ldapUser);

		if(ldapUser != null && ldapPassword == null) environment.put(Context.SECURITY_CREDENTIALS, "");
			else if(ldapUser != null && ldapPassword != null) environment.put(Context.SECURITY_CREDENTIALS, ldapPassword);

		searchControl = new SearchControls();
		searchControl.setSearchScope(SearchControls.SUBTREE_SCOPE);
		searchControl.setReturningObjFlag(true);

		initContext();
	}

	protected void initContext() throws AREasyException
	{
		try
		{
			context = new InitialLdapContext(environment, null);
			logger.debug("LDAP context has been open: " + environment);
		}
		catch(NamingException ne)
		{
			throw new AREasyException("Error initiating LDAP connection! " + ne.getMessage(), ne);
		}
	}

	/**
	 * Dedicated method to release resources that are used by a data-source
	 *
	 * @throws AREasyException in case of any error will occur
	 */
	public void release() throws AREasyException
	{
		if(context != null)
		{
			try
			{
				context.close();
			}
			catch (Exception e) { /* ignore close errors */ }

			context = null;
			logger.debug("LDAP context has been released: " + environment);
		}
	}

	/**
	 * Take and deliver through a <code>Map</code> structure the data headers from the selected data-source.
	 *
	 * @return a <code>Map</code> with data-source headers.
	 * @throws AREasyException in case of any error will occur
	 */
	public Map getHeaders() throws AREasyException
	{
		Map map = new Hashtable();

		try
		{
			int count = 0;
			NamingEnumeration<SearchResult> enumeration = getContext().search(getBaseDN(), getQualification(), searchControl);

			while(enumeration != null && enumeration.hasMore() && count < 100)
			{
				SearchResult result = enumeration.next();
				Attributes attrs = result.getAttributes();

				for (NamingEnumeration ae = attrs.getAll(); ae.hasMore();)
				{
					Attribute attr = (Attribute) ae.next();
					if( !map.containsKey(attr.getID()) ) map.put(attr.getID(), attr.getID());
				}

				count++;
			}
		}
		catch(NamingException ne)
		{
			throw new AREasyException("Error reading LDAP attributes: " + ne.getMessage(), ne);
		}

		return map;
	}

	/**
	 * Take and deliver through a <code>Map</code> structure the data read it from
	 * the selected data-source. If the output is null means that the data-source goes to the end.
	 *
	 * @param list this is the list of data source keys.
	 * @return a <code>Map</code> having data source indexes as keys and data as values.
	 * @throws AREasyException in case of any error will occur
	 */
	public Map getNextObject(List list) throws AREasyException
	{
		Map map = null;

		if(searchAnswer == null)
		{
			try
			{
				getContext().setRequestControls(new Control[] {	new PagedResultsControl(pageSize, Control.NONCRITICAL) });
				searchAnswer = getContext().search(getBaseDN(), getQualification(), searchControl);
				logger.debug("LDAP Search answer has been initialized: " + searchAnswer.hasMore());
			}
			catch(Throwable th)
			{
				throw new AREasyException("Error creating search interface: " + th.getMessage(), th);
			}
		}

		try
		{
			if(searchAnswer.hasMore())
			{
				SearchResult result = (SearchResult) searchAnswer.next();
				Attributes attrs = result.getAttributes();

				map = new HashMap();

				for(int i = 0; i < list.size(); i++)
				{
					String colName = (String) list.get(i);

					if(colName != null)
					{
						Attribute attr = attrs.get(colName);

						if(attr != null)
						{
							NamingEnumeration en = attr.getAll();

							if(en != null)
							{
								List values = new Vector();

								while(en.hasMore())
								{
									values.add(en.next());
								}

								if(values.size() == 1) map.put(colName, values.get(0));
									else map.put(colName, values);
							}
							else map.put(colName, attr.get());
						}
						else map.put(colName, null);
					}
				}
			}
			else
			{
				int total;
				byte[] cookie = null;

				logger.debug("LDAP Search answer asking for a new page");

				// Examine the paged results control response
				Control[] controls = getContext().getResponseControls();

				if (controls != null)
				{
					for (int i = 0; i < controls.length; i++)
					{
						if (controls[i] instanceof PagedResultsResponseControl)
						{
							PagedResultsResponseControl prrc = (PagedResultsResponseControl)controls[i];
							total = prrc.getResultSize();
							cookie = prrc.getCookie();
						}
					}
				}

				if(cookie != null)
				{
					if(getAction().getConfiguration().getBoolean("pagereconnect", false))
					{
						release();
						initContext();
					}

					getContext().setRequestControls(new Control[] {	new PagedResultsControl(pageSize, cookie, Control.CRITICAL) });
					searchAnswer = getContext().search(getBaseDN(), getQualification(), searchControl);
					logger.debug("LDAP Search answer has been recreated: " +  searchAnswer.hasMore());

					return getNextObject(list);
				}
			}
		}
		catch(Throwable th)
		{
			throw new AREasyException("Error reading data: " + th.getMessage(), th);
		}

		return map;
	}

	/**
	 * Read and return the total number of records found in the data-source.
	 *
	 * @return number of records found
	 */
	public int getDataCount()
	{
		int counter = 0;

		try
		{
			int total;
			byte[] cookie = null;

			SearchControls ctls = new SearchControls();
			ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			ctls.setReturningObjFlag(false);
			ctls.setReturningAttributes( new String[] { oidName } );

			getContext().setRequestControls(new Control[] {	new PagedResultsControl(pageSize, Control.NONCRITICAL) });

			do
			{
				NamingEnumeration answer = getContext().search(getBaseDN(), getQualification(), ctls);

				while(answer != null && answer.hasMore())
				{
					++counter;
					answer .next();
				}

				// Examine the paged results control response
				Control[] controls = getContext().getResponseControls();
				if (controls != null)
				{
					for (int i = 0; i < controls.length; i++)
					{
						if (controls[i] instanceof PagedResultsResponseControl)
						{
							PagedResultsResponseControl prrc = (PagedResultsResponseControl)controls[i];
							total = prrc.getResultSize();
							cookie = prrc.getCookie();
						}
					}
				}

				getContext().setRequestControls(new Control[] {	new PagedResultsControl(pageSize, cookie, Control.CRITICAL) });
			}
			while (cookie != null);
		}
		catch(Throwable th)
		{
			RuntimeLogger.error("Error counting LDAP entities: " + th.getMessage());
			getAction().getLogger().debug("Exception", th);
		}

		return counter;
	}

	/**
	 * Get qualification that could be an LDAP expression
	 *
	 * @return qualification to select data and to point out the exact data source
	 */
	protected String getQualification()
	{
		return qualification;
	}

	/**
	 * Set qualification that could be an LDAP expression
	 *
	 * @param qualification qualification to select data and to point out the exact data source
	 */
	protected void setQualification(String qualification)
	{
		this.qualification = qualification;
	}

	/**
	 * Get base distinguish name
	 *
	 * @return base distinguish name too look in the LDAP tree
	 */
	protected String getBaseDN()
	{
		return baseDN;
	}

	/**
	 * Set base distinguish name
	 *
	 * @param baseDN base distinguish name too look in the LDAP tree
	 */
	protected void setBaseDN(String baseDN)
	{
		this.baseDN = baseDN;
	}

	/**
	 * Get LDAP context structure
	 *
	 * @return LDAP <code>DirContext</code> instance
	 */
	protected LdapContext getContext() throws AREasyException
	{
		if (context == null) init();

		return context;
	}
}
