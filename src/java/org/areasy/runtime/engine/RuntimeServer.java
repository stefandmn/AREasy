package org.areasy.runtime.engine;

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

import org.areasy.common.data.BufferUtility;
import org.areasy.common.data.NumberUtility;
import org.areasy.common.data.StringUtility;
import org.areasy.common.data.type.Buffer;
import org.areasy.common.data.type.buffer.BoundedFifoBuffer;
import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;
import org.areasy.common.support.configuration.Configuration;
import org.areasy.runtime.RuntimeManager;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.runtime.engine.services.cache.DataCache;
import org.areasy.runtime.engine.services.cache.InitialObject;
import org.areasy.runtime.engine.services.cron4j.CronEntry;
import org.areasy.runtime.engine.services.cron4j.CronListener;
import org.areasy.runtime.engine.services.cron4j.CronManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Vector;

/**
 * This is the runtime server library managing all process from the server side. Here are implemented all flows to start
 * the server process and to manage client/server interaction.
 */
public class RuntimeServer extends RuntimeBase
{
	/** Library logger */
	protected static Logger logger =  LoggerFactory.getLog(RuntimeServer.class);

	/** Caching layer for the runtime server */
	private static DataCache cache = null;

	private ServerSocket server = null;
	private boolean running = true;
	private boolean stopped = false;

	/** Stores threads that are available within the pool. */
	private final Vector threads = new Vector();

	/** The thread groups used for all created threads. */
	private static ThreadGroup channels = null;
	private static ThreadGroup utilities = null;

	/** Create a new queue for adding Runnable templates to. */
	private Buffer queue = null;

	/** The minimum amount of threads that should always be available */
	private int minSpareThreads = 5;
	/** Holds the total number of threads that have ever been processed. */	
	private int processThreadCount = 0;

	/** Cron manager */
	private CronManager cronManager = null;

	public RuntimeServer(RuntimeManager manager)
	{
		logger.info("Starting AREasy Runtime Server..");

		//specify runtime caller (manager)
		setManager(manager);

		//set cache layer.
		cache = new DataCache(getManager().getConfiguration().getInt("app.server.cache.size", 100));
		cache.setDefaultAge(getManager().getConfiguration().getInt("app.server.cache.defaultage", 259200));

		//define the runtime server queue which will server client connections.
		this.minSpareThreads = getManager().getConfiguration().getInt("app.server.threads", 5);

		//thread groups repository
		channels = new ThreadGroup("AREASY-CHANNELS");
		utilities = new ThreadGroup("AREASY-UTILITIES");

		//queue definition for specific objects
		queue = BufferUtility.synchronizedBuffer(new BoundedFifoBuffer());

		//create initial threads (doubling spare threads.
		List threads = createAdditionalThreads(2 * this.minSpareThreads);
		release(threads);

		//pre-load all object that have to be stored in cache, to become shared
		InitialObject.load(getManager());

		//--now the server is UP

		//run all action which should be started when the server is started.
		cronManager = new CronManager();
		addScheduledCronEntries(cronManager);
	}

	public final void run()
	{
		int port = getManager().getConfiguration().getInt("app.server.port", 0);

		try
		{
			//1. creating a server socket
			server = new ServerSocket(port);
			logger.info("AREasy Runtime Server is listening on: " + server.toString());

			//run server execution
			while(running)
			{
				try
				{
					//2. Wait for connection
					Socket client = server.accept();

					//initiate a new connection
					if(running)
					{
						//call a runtime thread from the queue
						RuntimeRunner runner = new RuntimeRunner(this, new RuntimeBase(), client);
				    	process(runner);
					}
				}
				catch(Exception e)
				{
					logger.error("Error reading client socket connection: " + e.getMessage());
					if(logger.isDebugEnabled()) logger.debug("Exception", e);
				}
			}
		}
		catch (IOException e)
		{
			logger.error("Error starting socket server: " + e.getMessage());
			if(logger.isDebugEnabled()) logger.debug("Exception", e);
		}
		finally
		{
			//4: Closing connection
			try
			{
				if(server != null) server.close();
				server = null;
			}
			catch (IOException e)
			{
				logger.error("Error closing server socket: " + e.getMessage());
				if(logger.isDebugEnabled()) logger.debug("Exception", e);
			}
		}

		//stop thread queue services
		close();
	}

	public void shutdown()
	{
		//locking server socket instance
		this.running = false;

		try
		{
			Socket client = new Socket("127.0.0.1", getManager().getConfiguration().getInt("app.server.port", 0));
			client.close();
		}
		catch(Throwable th)
		{
			if(logger.isDebugEnabled()) logger.debug("Error calling server control action: " + th.getMessage());
		}
	}

	protected void close()
	{
		//stop cron manager thread
		getCronManager().stop();

		//clearing threads
		try
		{
			this.processThreadCount = 0;

			Thread threads[] = new Thread[getChannelsThreadGroup().activeCount()];
			getChannelsThreadGroup().enumerate(threads);

			for(int i = 0; i < threads.length; i++)
			{
				if(threads[i] != null && threads[i] instanceof RuntimeThread)
				{
					//get current thread
					RuntimeThread thread = (RuntimeThread) threads[i];

					try
					{
						if(thread != null && thread.isWaiting()) thread.close();
							else if(thread != null && thread.isRunning()) thread.interrupt();
					}
					catch(Exception e)
					{
						logger.debug("Error closing thread: " + e.getMessage());
						logger.trace("Exception", e);
					}
				}
			}

			getChannelsThreadGroup().interrupt();
			channels = null;
		}
		catch (Exception ex) { /* nothing to do here */ }

		//cleaning thread pool
		this.threads.clear();

		//Release objects from cache
		InitialObject.release();

		//removing object from cache layer
		getCache().clear();

		//closing server socket connection.
		try
		{
			if(server != null) server.close();
			server = null;
		}
		catch (IOException e)
		{
			logger.error("Error closing server socket: " + e.getMessage());
			if(logger.isDebugEnabled()) logger.debug("Exception", e);
		}

		//mark an event in runtime log channel
		logger.info("AREasy Runtime server is closed.");
	}

	/**
	 * Create "count" number of threads and make them available.
	 *
	 * @param count the number of threads to create
	 * @return a list of threads
	 */
	private synchronized List createAdditionalThreads(int count)
	{
		List list = new Vector();

		if (getAvailableChannelsCount() < this.minSpareThreads)
		logger.info("Create '" + count + "' more new thread(s)");

		for (int i = 0; i < count; ++i)
		{
			RuntimeThread thread = new RuntimeThread(this, getChannelsThreadGroup());
			thread.setPriority(Thread.MIN_PRIORITY);

			//start created thread
			thread.start();

			list.add(thread);
		}

		return list;
	}

	/**
	 * Get a thread that is available from the pool or null if there are no more threads left.
	 *
	 * @return a thread from the pool or null if non available
	 */
	public RuntimeThread getAvailableThread()
	{
		RuntimeThread thread = null;

		synchronized (this.threads)
		{
			//if the current number of available threads is less than minSpareThreads then we need to create more
			if (getAvailableChannelsCount() < this.minSpareThreads)
			{
				List list = createAdditionalThreads(this.minSpareThreads);
				release(list);
			}

			//now if there aren't any threads available then just return null.
			if (getAvailableChannelsCount() > 0)
			{
				thread = (RuntimeThread) this.threads.elementAt(0);
				this.threads.removeElementAt(0);
			}

			return thread;
		}
	}

	/**
	 * Place this thread back into the pool so that it can be used again
	 *
	 * @param thread the thread to flows back to the pool
	 */
	public void release(RuntimeThread thread)
	{
		release(thread, false);
	}

	/**
	 * Place this thread back into the pool so that it can be used again
	 *
	 * @param thread the thread to flows back to the pool
	 * @param forced if it is true will remove also persistent <code>Runnable</code> objects
	 */
	public void release(RuntimeThread thread, boolean forced)
	{
		if(thread == null) return;

		synchronized (this.threads)
		{
			if(!this.threads.contains(thread))
			{
				//get current runnable job
				Runnable runnable = thread.getRunner();

				if(runnable != null)
				{
					boolean reset = true;
					if(!forced && runnable instanceof RuntimeRunner) reset = !((RuntimeRunner)runnable).isPersistent();

					if(reset)
					{
						//include thread in the pool
						this.threads.addElement(thread);

						//increment number of total processed threads
						this.processThreadCount++;

						//reset thread coordinates: runnable object and priority
						thread.setRunner(null);
						thread.setPriority(Thread.MIN_PRIORITY);
					}
				}
				else
				{
					//include thread in the pool
					this.threads.addElement(thread);

					//increment number of total processed threads
					this.processThreadCount++;

					//reset thread coordinates: runnable object and priority
					thread.setRunner(null);
					thread.setPriority(Thread.MIN_PRIORITY);
				}


				//It is important to synchronize here because it is possible that between the time we check the queue
				//and we get this another thread might return and fetch the queue to the end.
				synchronized (getQueue())
				{
					//now if there are any templates in the queue add one for processing to the thread that you just freed up.
					if (getQueue().size() > 0)
					{
						Runnable runner = (Runnable) getQueue().get();

						if (runner != null) process(runner);
							else logger.debug("No runnable found for the next thread from queue.");
					}
				}
			}
		}
	}

	/**
	 * Place these threads back into the pool so that it can be used again
	 *
	 * @param threads the list of threads to flows back to the pool
	 */
	public void release(List threads)
	{
		for(int i = 0; i < threads.size(); i++)
		{
			release( (RuntimeThread)threads.get(i), true);
		}
	}

	/**
	 * Processes the runnable object with an available thread at default priority
	 *
	 * @param runnable the runnable code to process
	 * @see #process( Runnable, int )
	 * @return the current runtime thread instance used to run this job
	 */
	public RuntimeThread process(Runnable runnable)
	{
		return process(runnable, Thread.MIN_PRIORITY);
	}

	/**
	 * Process a runnable object by allocating a Thread for it at the given priority
	 *
	 * @param runnable the runnable code to process
	 * @param priority the priority used be the thread that will run this runnable
	 * @return the current runtime thread instance used to run this job
	 */
	public RuntimeThread process(Runnable runnable, int priority)
	{
		return process(runnable, priority, true);
	}

	/**
	 * Process a runnable object by allocating a Thread for it at the given priority
	 *
	 * @param runnable the runnable code to process
	 * @param fire if is true will notify the thread to weak-up and to start execution
	 * @return the current runtime thread instance used to run this job
	 */
	public RuntimeThread process(Runnable runnable, boolean fire)
	{
		return process(runnable, Thread.MIN_PRIORITY, fire);
	}

	/**
	 * Process a runnable object by allocating a Thread for it at the given priority
	 *
	 * @param runnable the runnable code to process
	 * @param priority the priority used be the thread that will run this runnable
	 * @param fire if is true will notify the thread to weak-up and to start execution
	 * @return the current runtime thread instance used to run this job
	 */
	public RuntimeThread process(Runnable runnable, int priority, boolean fire)
	{
		RuntimeThread thread = getAvailableThread();

		if (thread == null) getQueue().add(runnable);
		else
		{
			try
			{
				synchronized (thread)
				{
					//get the default priority of this Thread
					int defaultPriority = thread.getPriority();

					//setting priority triggers security checks, so we do it only if needed.
					if (defaultPriority != priority) thread.setPriority(priority);

					//set runnable process.
					thread.setRunner(runnable);
					if(logger.isDebugEnabled()) logger.debug("Set thread with runnable process and waiting time: " + thread);

					//execute thread.
					if(fire)
					{
						if(logger.isDebugEnabled()) logger.debug("Notify thread to run it: " + thread);
						thread.notify();
					}
				}
			}
			catch (Throwable t)
			{
				logger.error("Error processing runnable thread: " + t.getMessage());
				if(logger.isDebugEnabled()) logger.debug("Exception", t);
			}
		}

		return thread;
	}

	/**
	 * Interrupt cycle execution of the host thread for the specified runnable object and destroy it.
	 *
	 * @param runnable runnable object.
	 */
	public void interrupt(Runnable runnable)
	{
		RuntimeThread thread = getHostThread(runnable);

		if(thread != null)
		{
			if(logger.isDebugEnabled()) logger.debug("Interrupting thread: " + thread);

			try
			{
				thread.interrupt();

				thread.setRunner(null);
				thread = null;
			}
			catch(Exception e) { /* nothing to do here */ }
		}
		else logger.warn("Cannot find the thread to be interrupted: " + runnable.toString());
	}

	/**
	 * Check if actual host thread is under execution (of the specified runnable object) or not.
	 *
	 * @param runnable runnable object.
	 * @return true if the <code>Runnable</code> structure is still running.
	 */
	public boolean isRunning(Runnable runnable)
	{
		RuntimeThread thread = getHostThread(runnable);

		return thread != null && thread.isRunning();
	}

	public RuntimeThread getHostThread(Runnable runnable)
	{
		if(runnable == null) return null;

		Thread threads[] = new Thread[getChannelsThreadGroup().activeCount()];
		getChannelsThreadGroup().enumerate(threads);

		for(int i = 0; i < threads.length; i++)
		{
			if(threads[i] != null && threads[i] instanceof RuntimeThread)
			{
				RuntimeThread thp = (RuntimeThread) threads[i];
				if(thp.getRunner() != null && thp.getRunner().equals(runnable)) return thp;
			}
		}

		return null;
	}

	/**
	 * Get the queue used by the DefaultThreadPoolService
	 *
	 * @return the queue holding the waiting processes
	 */
	Buffer getQueue()
	{
		return this.queue;
	}

	/**
	 * Get the number of threads that have been created
	 *
	 * @return the number of threads currently created by the pool
	 */
	public int getChannelsCount()
	{
		int counter = 0;
		
		Thread threads[] = new Thread[getChannelsThreadGroup().activeCount()];
		getChannelsThreadGroup().enumerate(threads);

		for(int i = 0; i < threads.length; i++) if(threads[i] != null && threads[i] instanceof RuntimeThread) counter++;

		return counter;
	}

	/**
	 * Get the number of threads that are available.
	 *
	 * @return the number of threads available in the pool
	 */
	public int getAvailableChannelsCount()
	{
		return this.threads.size();
	}

	/**
	 * Get the current length of the Runnable queue, waiting for processing
	 *
	 * @return the length of the queue of waiting processes
	 */
	public int getQueueLength()
	{
		return this.getQueue().size();
	}

	/**
	 * Get the number of threads that have successfully been processed
	 * for logger and debugging purposes.
	 *
	 * @return the number of processes executed since initialization
	 */
	public int getProcessedActionsCount()
	{
		return this.processThreadCount;
	}

	/**
	 * Get the number of threads that have been created for runtime threads
	 *
	 * @return the number of threads currently created by the pool
	 */
	public static ThreadGroup getChannelsThreadGroup()
	{
		return channels;
	}

	/**
	 * Get the number of threads that have been created for utilities workers
	 *
	 * @return the number of threads currently created by the pool
	 */
	public static ThreadGroup getUtilitiesThreadGroup()
	{
		return utilities;
	}

	/**
	 * Define the cron jobs that should run scheduled actions.
	 * @param cronMgr <code>CronManager</code> instance
	 */
	protected void addScheduledCronEntries(CronManager cronMgr)
	{
		//start all actions which must be started at runtime startup
		List startup = getManager().getConfiguration().getVector("app.server.cron4j.jobs", null);

		for(int i = 0; startup != null && i < startup.size(); i++)
		{
			final String jobName = (String)startup.get(i);

			//gt scheduling parameters
			String schedule = getManager().getConfiguration().getString("app.server.cron4j." + jobName + ".schedule", null);
			String period = getManager().getConfiguration().getString("app.server.cron4j." + jobName + ".period", null);

			//get and decode action configuration
			String command = getManager().getConfiguration().getString("app.server.cron4j." + jobName + ".runner");
			Configuration config = getManager().getConfiguration(command);

			//initialize action runner
			RuntimeRunner runner = new RuntimeRunner(this, config);
			runner.setPersistent();

			//reserve the thread execution
			final RuntimeThread thread = process(runner, Thread.MIN_PRIORITY, false);
			logger.info("Scheduling job '" + jobName + "' using command line: " + command);

			//create cron listener
			CronListener listener = new CronListener()
			{
				public void handleCron(CronEntry entry)
				{
				    synchronized(thread)
					{
						thread.notify();
					}
				}
			};

			try
			{
				if(period != null)
				{
					int minutes = 0;
					if(period.endsWith("m")) minutes = NumberUtility.toInt(StringUtility.replace(period, "m", "").trim(), 0);
					else if(period.endsWith("min")) minutes = NumberUtility.toInt(StringUtility.replace(period, "min", "").trim(), 0);
					else if(period.endsWith("h")) minutes = NumberUtility.toInt(StringUtility.replace(period, "h", "").trim(), 0) * 60;
					else if(period.endsWith("d")) minutes = NumberUtility.toInt(StringUtility.replace(period, "d", "").trim(), 0) * 60 * 24;
					else minutes = NumberUtility.toInt(period.trim(), 0);

					//validating and adding cron
					if(minutes > 0) cronMgr.addCronEntry(jobName, minutes, true, listener);
						else logger.warn("Invalid period format for job '" + jobName + "'. The action will not be scheduled");
				}
				else if(schedule != null)
				{
					int[] minutes = {-1};
					int[] hours = {-1};
					int[] months = {-1};
					int[] daysofmonth = {-1};
					int[] daysofweek = {-1};

					String schMinutesValues[] = null;
					String schHoursValues[] = null;
					String schMonthsValues[] = null;
					String schDaysOfMonthValues[] = null;
					String schDaysOfWeekValues[] = null;

					String schValues[] = StringUtility.split(schedule, ' ');

					if(schValues.length >= 1) schMinutesValues = StringUtility.split(schValues[0].trim(), ',');
					if(schValues.length >= 2) schHoursValues = StringUtility.split(schValues[1].trim(), ',');
					if(schValues.length >= 3) schDaysOfMonthValues = StringUtility.split(schValues[2].trim(), ',');
					if(schValues.length >= 4) schMonthsValues = StringUtility.split(schValues[3].trim(), ',');
					if(schValues.length >= 5) schDaysOfWeekValues = StringUtility.split(schValues[4].trim(), ',');

					int maxSize = Math.max(schMinutesValues.length, schHoursValues.length);
					maxSize = Math.max(maxSize, schDaysOfMonthValues.length);
					maxSize = Math.max(maxSize, schMonthsValues.length);
					maxSize = Math.max(maxSize, schDaysOfWeekValues.length);

					minutes = new int[maxSize];
					hours = new int[maxSize];
					months = new int[maxSize];
					daysofmonth = new int[maxSize];
					daysofweek = new int[maxSize];

					for(int x = 0; x < schMinutesValues.length; x++)
					{
						minutes[x] = -1;
						hours[x] = -1;
						months[x] = -1;
						daysofmonth[x] = -1;
						daysofweek[x] = -1;
					}

					for(int x = 0; x < schMinutesValues.length; x++) minutes[x] = NumberUtility.toInt(schMinutesValues[x], -1);
					for(int x = 0; x < schHoursValues.length; x++) hours[x] = NumberUtility.toInt(schHoursValues[x], -1);
					for(int x = 0; x < schMonthsValues.length; x++) months[x] = NumberUtility.toInt(schMonthsValues[x], -1);
					for(int x = 0; x < schDaysOfMonthValues.length; x++) daysofmonth[x] = NumberUtility.toInt(schDaysOfMonthValues[x], -1);
					for(int x = 0; x < schDaysOfWeekValues.length; x++) daysofweek[x] = NumberUtility.toInt(schDaysOfWeekValues[x], -1);

					//adding cron
					cronMgr.addCronEntry(jobName, minutes, hours, daysofmonth, months, daysofweek, -1, listener);
				}
				else logger.warn("No valid schedule for job '" + jobName + "'. The job will be ignored by CronManager utility");
			}
			catch(AREasyException are)
			{
				logger.error("Error initiating cron entry: " + are.getMessage());
				logger.debug("Exception", are);
			}
		}
	}

	/**
	 * Get cache layer structure
	 *
	 * @return <code>DataCache</code> structure.
	 */
	public static DataCache getCache()
	{
		if(cache == null)
		{
			//set cache layer.
			cache = new DataCache(100);
		}

		return cache;
	}

	/**
	 * Get <code>CronManager</code> instance which it manages all cron entries loaded by the server
	 * instance or added on demand.
	 *
	 * @return <code>CronManager</code> instance
	 */
	public final CronManager getCronManager()
	{
		return cronManager;
	}
}
