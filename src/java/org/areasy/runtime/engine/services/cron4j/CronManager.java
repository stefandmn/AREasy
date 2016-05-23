package org.areasy.runtime.engine.services.cron4j;

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

import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;

import java.util.*;

/**
 * This class implements an alarm cron manager similar to Unix <code>cron</code>
 * and <code>at</code> daemons. It is intended to fire events
 * when alarms' date and time match the current ones. Alarms are
 * added dynamically and can be one-shot or repetitive
 * (i.e. rescheduled when matched). Time unit is seconds. Alarms
 * scheduled less than one second to the current time are rejected (a
 * <code>CronException</code> is thrown).<p>
 * <p/>
 * The alarm scheduler has been designed to
 * manage a large quantity of alarms (it uses a priority queue to
 * optimize alarm dates selection) and to reduce the use of the CPU
 * time (the CronManager's thread is started only when there are
 * alarms to be managed and it sleeps until the next alarm
 * date).<p>
 * <p/>
 * Note : because of clocks' skews some alarm dates may be erroneous,
 * particularly if the next alarm date is scheduled for a remote time
 * (e.g. more than a few days). In order to avoid that problem,
 * well-connected machines can use the <a
 * href="ftp://ftp.inria.fr/rfc/rfc13xx/rfc1305.Z">Network Time
 * Protocol</a> (NTP) to synchronize their clock.<p>
 * <p/>
 * Example of use:
 * <pre>
 *  // Creates a new CronManager
 *  CronManager cronMgr = new CronManager();
 *
 *  // Date alarm (non repetitive)
 *  cronMgr.addCronEntry("fixed5min",new Date(System.currentTimeMillis() + 300000), new CronListener()
 *  {
 *  	public void handleCron(CronEntry entry)
 *  	{
 *      	System.out.println("5 minutes later");
 *		}
 *  });
 *
 *  Calendar cal = Calendar.getInstance();
 *  cal.add(Calendar.WEEK_OF_YEAR, 1);
 *  cronMgr.addCronEntry("week_one", cal.getTime(), new CronListener()
 *  {
 *  	public void handleCron(CronEntry entry)
 *  	{
 *      	System.out.println("One week later");
 *		}
 *  });
 *
 *  // Alarm with a delay (in minute) relative to the current time.
 *  cronMgr.addCronEntry(1, true, new CronListener()
 *  {
 *  	public void handleCron(CronEntry entry)
 *  	{
 *      	System.out.println("1 more minute ! (" + new Date() + ")");
 *		}
 *  });
 *
 *
 *  // Cron-like alarm (minute, hour, day of month, month, day of week, year)
 *  // Repetitive when the year is not specified.
 *
 *  cronMgr.addCronEntry(-1, -1, -1, -1, -1, -1, new CronListener()
 *  {
 *  	public void handleCron(CronEntry entry)
 *  	{
 *      	System.out.println("Every minute (" + new Date() + ")");
 *      }
  *  });
 *
 *  cronMgr.addCronEntry(5, -1, -1, -1, -1, -1, new CronListener()
 *  {
 *  	public void handleCron(CronEntry entry)
 *  	{
 *   		System.out.println("Every hour at 5' (" + new Date() + ")");
 *		}
 *  });
 *
 *  cronMgr.addCronEntry(00, 12, -1, -1, -1, -1, new CronListener()
 *  {
 *  	public void handleCron(CronEntry entry)
 *  	{
 *      	System.out.println("Lunch time (" + new Date() + ")");
 *		}
 *  });
 *
 *  cronMgr.addCronEntry(07, 14, 1, Calendar.JANUARY, -1, -1, new CronListener()
 *  {
 *  	public void handleCron(CronEntry entry)
 *  	{
 *      	System.out.println("Happy birthday AREasy !");
 *	}
 *  });
 *
 *  cronMgr.addCronEntry(30, 9, 1, -1, -1, -1, new CronListener()
 *  {
 *  	public void handleCron(CronEntry entry)
 *  	{
 *      	System.out.println("On the first of every month at 9:30");
 *		}
 *  });
 *
 *  cronMgr.addCronEntry(00, 18, -1, -1, Calendar.FRIDAY, -1, new CronListener()
 *  {
 *  	public void handleCron(CronEntry entry)
 *  	{
 *      	System.out.println("On every Friday at 18:00");
 *		}
 *  });
 *
 *  cronMgr.addCronEntry(00, 13, 1, Calendar.AUGUST, -1, 2001,  new CronListener()
 *  {
 *  	public void handleCron(CronEntry entry)
 *  	{
 *      	System.out.println("2 years that this class was programmed !");
 *		}
 *  });
 * </pre>
 */
public class CronManager implements Runnable
{
	private static Logger logger = LoggerFactory.getLog(CronManager.class);

	protected SortedSet queue = null;

	protected Thread thread;
	private long sleepUntil = -1;
	private boolean shutdown = false;

	/**
	 * Creates a new CronManager. The waiter thread will be started
	 * only when the first alarm listener will be added.
	 *
	 * @param isDaemon   true if the waiter thread should run as a daemon.
	 * @param threadName the name of the waiter thread
	 */
	private CronManager(boolean isDaemon, String threadName)
	{
		queue = new TreeSet();

		// define and start the thread
		thread = new Thread(this, threadName);
		thread.setDaemon(isDaemon);
		thread.setPriority(1);
		thread.start();
	}

	/**
	 * Creates a new CronManager. The waiter thread will be started
	 * only when the first alarm listener will be added. The waiter
	 * thread will <i>not</i> run as a daemon.
	 */
	public CronManager()
	{
		this(false, "cron-manager");
	}

	/**
	 * Adds an alarm for a specified date.
	 *
	 * @param date	 the alarm date to be added.
	 * @param listener the alarm listener.
	 * @return the JobEntry.
	 * @throws CronException if the alarm date is in the past or less than 1 second closed to the current date).
	 */
	public synchronized CronEntry addCronEntry(String name, Date date, CronListener listener) throws CronException
	{
		CronEntry entry = new CronEntry(name, date, listener);
		addCronEntry(entry);

		return entry;
	}

	/**
	 * @deprecated for backwards compatibility, w/o name param:
	 */
	public CronEntry addCronEntry(Date date, CronListener listener) throws CronException
	{
		return addCronEntry(null, date, listener);
	}

	/**
	 * Adds an alarm for a specified delay.
	 *
	 * @param name job name
	 * @param delay	   the alarm delay in minutes (relative to now).
	 * @param isRepeating <code>true</code> if the alarm must be
	 *                    reactivated, <code>false</code> otherwise.
	 * @param listener	the alarm listener.
	 * @return the JobEntry.
	 * @throws CronException if the alarm date is in the past (or less than 1 second closed to the current date).
	 */
	public synchronized CronEntry addCronEntry(String name, int delay, boolean isRepeating, CronListener listener) throws CronException
	{
		CronEntry entry = new CronEntry(name, delay, isRepeating, listener);

		addCronEntry(entry);
		return entry;
	}

	/**
	 * @deprecated for backwards compatibility, w/o name param:
	 */
	public CronEntry addCronEntry(int delay, boolean isRepeating, CronListener listener) throws CronException
	{
		return addCronEntry(null, delay, isRepeating, listener);
	}

	/**
	 * Adds an alarm for a specified date.
	 *
	 * @param minute	 minute of the alarm. Allowed values 0-59, or -1 for all.
	 * @param hour	   hour of the alarm. Allowed values 0-23, or -1 for all.
	 * @param dayOfMonth day of month of the alarm.  Allowed values 1-7
	 *                   (1 = Sunday, 2 = Monday, ...), or -1 for all.
	 *                   <code>java.util.Calendar</code> constants can be used.
	 * @param month	  month of the alarm. Allowed values 0-11 (0 = January,
	 *                   1 = February, ...), or -1 for all. <code>java.util.Calendar</code>
	 *                   constants can be used.
	 * @param dayOfWeek  day of week of the alarm. Allowed values 1-31,
	 *                   or -1 for all.
	 * @param year	   year of the alarm. When this field is not set
	 *                   (i.e. -1) the alarm is repetitive (i.e. it is rescheduled when
	 *                   reached).
	 * @param listener   the alarm listener.
	 * @return the JobEntry.
	 * @throws CronException if the alarm date is in the past (or less than 1 second away from the current date).
	 */
	public synchronized CronEntry addCronEntry(String name, int minute, int hour, int dayOfMonth, int month, int dayOfWeek, int year, CronListener listener) throws CronException
	{
		CronEntry entry = new CronEntry(name, minute, hour, dayOfMonth, month, dayOfWeek, year, listener);

		addCronEntry(entry);
		return entry;
	}

	/**
	 * Adds an alarm for a specified date or matching dates (for unrestricted
	 * fields).
	 *
	 * @param minutes	 minutes of the alarm. Allowed values 0-59, or -1 for all.
	 * @param hours	   hours of the alarm. Allowed values 0-23, or -1 for all.
	 * @param daysOfMonth days of month of the alarm.  Allowed values 1-7
	 *                    (1 = Sunday, 2 = Monday, ...), or -1 for all.
	 *                    <code>java.util.Calendar</code> constants can be used.
	 * @param months	  months of the alarm. Allowed values 0-11 (0 = January,
	 *                    1 = February, ...), or -1 for all. <code>java.util.Calendar</code>
	 *                    constants can be used.
	 * @param daysOfWeek  days of week of the alarm. Allowed values 1-31,
	 *                    or -1 for all.
	 * @param year		year of the alarm. When this field is not set
	 *                    (i.e. -1) the alarm is repetitive (i.e. it is rescheduled when
	 *                    reached).
	 * @param listener	the alarm listener.
	 * @return the JobEntry.
	 * @throws CronException if the alarm date is in the past (or less than 1 second away from the current date).
	 */
	public synchronized CronEntry addCronEntry(String name, int[] minutes, int[] hours, int[] daysOfMonth, int[] months, int[] daysOfWeek, int year, CronListener listener) throws CronException
	{
		CronEntry entry = new CronEntry(name, minutes, hours, daysOfMonth, months, daysOfWeek, year, listener);

		addCronEntry(entry);
		return entry;
	}

	/**
	 * Adds an alarm for a specified JobEntry
	 *
	 * @param entry the JobEntry.
	 * @throws CronException if the alarm date is in the past (or less than one second away from the current date).
	 */
	public synchronized void addCronEntry(CronEntry entry) throws CronException
	{
		logger.debug("Add a new cron entry : " + entry);

		queue.add(entry);

		if (queue.first().equals(entry))
		{
			logger.debug("This new cron entry is the top one, update the waiter thread");
			update(entry.alarmTime);
		}
	}

	/**
	 * Removes the specified JobEntry.
	 *
	 * @param entry the JobEntry that needs to be removed.
	 * @return <code>true</code> if there was an alarm for this date,
	 *         <code>false</code> otherwise.
	 */
	public synchronized boolean removeCronEntry(CronEntry entry)
	{
		boolean found = false;

		if (!queue.isEmpty())
		{
			CronEntry was_first = (CronEntry) queue.first();
			found = queue.remove(entry);

			// update the queue if it's not now empty, and the first alarm has changed
			if (!queue.isEmpty() && entry.equals(was_first))
			{
				update(((CronEntry) queue.first()).alarmTime);
			}
		}

		return found;
	}

	/**
	 * Removes all the alarms. No more alarms, even newly added ones, will
	 * be fired.
	 */
	public synchronized void removeAllCronEntries()
	{
		queue.clear();
	}

	/**
	 * Removes all the alarms. No more alarms, even newly added ones, will
	 * be fired.
	 */
	public synchronized void removeAllAndStop()
	{
		stop();
		queue.clear();
	}

	/**
	 * Tests whether the supplied JobEntry is in the manager.
	 *
	 * @param jobEntry cron entry instance
	 * @return boolean whether <code>CronEntry</code> is contained within the manager
	 */
	public synchronized boolean containsCronEntry(CronEntry jobEntry)
	{
		return queue.contains(jobEntry);
	}

	/**
	 * Returns a copy of all alarms in the manager.
	 */
	public synchronized List getAllCronEntries()
	{
		List result = new ArrayList();

		Iterator iterator = queue.iterator();
		while (iterator.hasNext())
		{
			result.add(iterator.next());
		}

		return result;
	}

	/**
	 * This is method is called when an alarm date is reached. It
	 * is only be called by the the by itself (if the next alarm is less than 1 second away).
	 */
	protected synchronized void ringNextAlarm()
	{
		// if the queue is empty, there's nothing to do
		if (queue.isEmpty()) return;

		// Removes this alarm and notifies the listener
		CronEntry entry = (CronEntry) queue.first();
		queue.remove(entry);

		try
		{
			logger.debug("Calling next cron entry: " + entry);
			entry.ringCron();
		}
		catch (Exception e)
		{
			logger.error("Error calling cron entry: " + e.getMessage());
			logger.debug("Exception", e);
		}

		// Reactivates the alarm if it is repetitive
		if (entry.isRepeating)
		{
			entry.updateCronTime();
			queue.add(entry);
		}

		// Notifies the CronManager thread for the next alarm
		if (queue.isEmpty())
		{
			logger.debug("No more cron entries to handle; queue is empty");
		}
		else
		{
			long alarmTime = ((CronEntry) queue.first()).alarmTime;
			if (alarmTime - System.currentTimeMillis() < 1000)
			{
				logger.debug("Next cron entry is within 1 sec or already past - ring it without waiting");
				ringNextAlarm();
			}
			else
			{
				restart(alarmTime);
				logger.debug("Time updated for next cron entry: " + queue.first());
			}
		}
	}

	/**
	 * Updates the time to sleep.
	 *
	 * @param sleepUntil the new time to sleep until.
	 */
	public synchronized void update(long sleepUntil)
	{
		this.sleepUntil = sleepUntil;
		notify();
	}

	/**
	 * Restarts the thread for a new time to sleep until.
	 *
	 * @param sleepUntil the new time to sleep until.
	 */
	public synchronized void restart(long sleepUntil)
	{
		this.sleepUntil = sleepUntil;
		notify();
	}

	/**
	 * Stops (destroy) the thread.
	 */
	public synchronized void stop()
	{
		shutdown = true;
		notify();
	}

	public synchronized void run()
	{
		logger.debug("Cron Manager has been started");

		while (!shutdown)
		{
			try
			{
				// check if there's an alarm scheduled
				if (sleepUntil <= 0)
				{
					// no alarm. Wait for a new alarm to come along.
					wait();
				}
				else
				{
					// Found alarm, set timeout based on alarm time
					long timeout = sleepUntil - System.currentTimeMillis();

					if (timeout > 0)
					{
						wait(timeout);
					}
				}

				// now that we've awakened again, check if an alarm is due (within 1 second or already past)
				if (sleepUntil >= 0 && (sleepUntil - System.currentTimeMillis() < 1000))
				{
					// yes, an alarm is ready (or already past). Notify the manager to ring it.
					sleepUntil = -1;

					logger.debug("Notifying cron-manager to ring next entry");
					ringNextAlarm();
				}

			}
			catch (InterruptedException e)
			{
				logger.debug("Cron Manager has been interrupted");
			}
		}

		logger.debug("Cron Manager ha been stopped");
	}
}
