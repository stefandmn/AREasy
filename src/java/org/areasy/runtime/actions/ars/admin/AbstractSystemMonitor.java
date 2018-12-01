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

import org.areasy.runtime.RuntimeAction;
import org.areasy.runtime.actions.AbstractAction;
import org.areasy.runtime.engine.RuntimeLogger;
import org.areasy.runtime.engine.RuntimeServer;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.runtime.engine.services.cache.CacheEntry;
import org.areasy.runtime.engine.structures.CoreItem;
import org.areasy.common.data.BooleanUtility;
import org.areasy.common.data.StringUtility;
import org.areasy.common.data.type.credential.Credential;
import org.areasy.common.data.type.credential.MD5Credential;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * Remedy system information
 */
public abstract class AbstractSystemMonitor extends AbstractAction implements RuntimeAction
{
	/**
	 * This method execute the initialization workflow fr system actions.
	 *
	 * @param createConnection specify if server connection will be created
	 * @throws AREasyException if any error will occur
	 */
	protected void initWorkflow(boolean createConnection) throws AREasyException
	{
		//run secondary initialization (from the final implementation class).
		open();
	}

	/**
	 * Execute action's for monitoring.
	 *
	 * @return true of false in case of monitoring procedure observed an error or not
	 */
	protected abstract boolean monitor();

	/**
	 * Execute action's workflow.
	 *
	 * @throws org.areasy.runtime.engine.base.AREasyException
	 *          if any global error occurs. All errors coming from action's execution will become output items
	 */
	public void run() throws AREasyException
	{
		//get previous status value
		Object cacheObject = RuntimeServer.getCache().getCacheEntry(getCacheId());
		Object cacheValue = cacheObject != null ? ((CacheEntry)cacheObject).getContent() : null;
		boolean previousStatus = cacheValue != null && BooleanUtility.toBoolean(((Integer) cacheValue).intValue());

		//get monitor value
		boolean status = monitor();

		//approver for notification
		if(status)
		{
			//set error flag
			RuntimeServer.getCache().add(getCacheId(), new Integer(BooleanUtility.toInteger(status)));

			if(previousStatus && isNotified())
			{
				//reset output in case of action needed to be notified and if the notification was sent previous execution(s)
				RuntimeLogger.reset();
			}
		}
		else
		{
			if(cacheObject != null)
			{
				//remove cache object if it is already defined and the current answer was "no error(s)"
				RuntimeServer.getCache().remove(getCacheId());
			}

			RuntimeLogger.reset();
		}		
	}

	/**
	 * Get report content containing data and log message (according with specified log level for the current action).
	 *
	 * @return content for this notification
	 */
	protected String getReportContent()
	{
		if(RuntimeLogger.hasErrors()) return RuntimeLogger.getMessages();
			else return null;
	}
	
	/**
	 * Send the notification corresponding with actual action execution.
	 *
	 * @param body notification content
	 */
	protected CoreItem sendReport(String body)
	{
		//read FROM recipient
		String defaultRecipientFrom = getManager().getConfiguration().getString("app.runtime.notification.recipient.from", null);
		String recipientFrom = getConfiguration().getString("notificationrecipientfrom", defaultRecipientFrom);

		//read TO recipient
		String defaultRecipientTo = getManager().getConfiguration().getString("app.runtime.notification.recipient.to", null);
		String recipientTo = getConfiguration().getString("notificationrecipientto", null);

		//read CC recipient
		String defaultRecipientCc = getManager().getConfiguration().getString("app.runtime.notification.recipient.cc", null);
		String recipientCc = getConfiguration().getString("notificationrecipientcc", null);

		//decide TO and CC recipients
		if(StringUtility.isEmpty(recipientTo))
		{
			recipientTo = defaultRecipientTo;
			if(StringUtility.isEmpty(recipientCc)) recipientCc = defaultRecipientCc;
		}

		//read Subject
		String defaultSubject = getManager().getConfiguration().getString("app.runtime.notification.subject", null);
		if(StringUtility.isNotEmpty(getDefaultMessageSubject()))
		{
			if(StringUtility.isEmpty(defaultSubject)) defaultSubject = getDefaultMessageSubject();
				else defaultSubject += " - " + getDefaultMessageSubject();
		}
		
		String subject = getConfiguration().getString("notificationsubject", defaultSubject);

		//read SMTP connection properties
		String smtpServer = getManager().getConfiguration().getString("app.runtime.notification.smtp.server", null);
        String smtpPort = getManager().getConfiguration().getString("app.runtime.notification.smtp.port", "25");
        String smtpUserName = getManager().getConfiguration().getString("app.runtime.notification.smtp.user", null);
        String smtpPassword = getManager().getConfiguration().getString("app.runtime.notification.smtp.password", null);

		try
		{
			Session session;

			//define message hash properties
			Properties properties = new Properties();
			properties.setProperty("mail.smtp.host", smtpServer);
			properties.setProperty("mail.smtp.port", smtpPort);

			if(StringUtility.isNotEmpty(smtpUserName))
			{
				smtpPassword = Credential.getCredential(smtpPassword).decode();
				SmtpAuthenticator authenticator = new SmtpAuthenticator(smtpUserName, smtpPassword);

				properties.setProperty("mail.smtp.submitter", authenticator.getPasswordAuthentication().getUserName());
				properties.setProperty("mail.smtp.auth", "true");

				session =  Session.getInstance(properties, authenticator);
			}
			else session =  Session.getInstance(properties);

			//initiate a new message
			Message message = new MimeMessage(session);

			//set FROM clause
			message.addFrom(new InternetAddress[] { new InternetAddress(recipientFrom) });

			//set TO clause
			String toRecips[] = StringUtility.split(recipientTo, ';');
			for(int i = 0; toRecips!= null && i < toRecips.length; i++) message.addRecipient(MimeMessage.RecipientType.TO, new InternetAddress(toRecips[i]));

			String ccRecips[] = StringUtility.split(recipientCc, ';');
			for(int i = 0; ccRecips!= null && i < ccRecips.length; i++) message.addRecipient(MimeMessage.RecipientType.CC, new InternetAddress(ccRecips[i]));

			//set body content
			message.setSubject(subject);
			message.setContent(body, "text/plain");

			//set priority
			message.addHeader("X-Priority", "1");
			message.addHeader("Importance", "high");
			message.addHeader("X-MSMail-Priority", "high");

			//send message
			Transport.send(message);
		}
		catch(Throwable th)
		{
			getLogger().error("Error sending status report notification: " + th.getMessage());
			getLogger().debug("Exception", th);
		}

		return null;
	}

	/**
	 * Get notification subject
	 *
	 * @return notification subject
	 */
	protected String getDefaultMessageSubject()
	{
		return null;
	}

	/**
	 * Dedicated method to return an unique identifier to store action status in
	 * runtime server cache layer.
	 *
	 * @return a string label (which must be unique for many instance of the same action)
	 */
	protected String getCacheId()
	{
		return MD5Credential.digest (getManager().getCommandLine( getConfiguration() ) );
	}

	private class SmtpAuthenticator extends javax.mail.Authenticator
	{
		private PasswordAuthentication authentication;

		public SmtpAuthenticator(String username, String password)
		{
			authentication = new PasswordAuthentication(username, password);
		}

		protected PasswordAuthentication getPasswordAuthentication()
		{
			return authentication;
		}
	}
}
