package org.areasy.runtime.engine;

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

import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;


/**
 * A thread that is used to process Runnable processes. This thread will wait until it is
 * notified by another thread that it needs processing. However it will only
 * process if getRunnable != null.
 *
 */
public class RuntimeThread extends Thread
{
	/** Library logger */
	protected static Logger logger =  LoggerFactory.getLog(RuntimeThread.class);

	/** Thread identifier. */
	private static int nextId = 0;

	/** Check if thread is running. */
	private boolean running = false;

	/** Check if thread is waiting. */
	private boolean waiting = false;

	/** Check if thread is waiting. */
	private boolean alive = true;

	/** Check if thread should be closed/stopped after the current execution */
	private boolean closing = false;

	/** Thread id */
	private int id = 0;

	/** The runnable that you want to process */
	private Runnable runner = null;

	/** Runtime server instance */
	private RuntimeServer server = null;

	/**
	 * Increment a counter so that we can identify threads  easily.
	 * @return next thread id.
	 */
	private static synchronized int getNextId()
	{
		return ++nextId;
	}

	/**
	 * Default constructor.
	 */
	public RuntimeThread()
	{
		super();

		this.id = getNextId();
		super.setName("areasy-" + getId());

		//set priority
		this.setPriority(Thread.MIN_PRIORITY);
		this.setDaemon(true);
	}

	/**
	 * Creates a new Thread in the specified threadgroup
	 *
	 * @param group the Threadgroup which will contain the new Thread
	 */
	RuntimeThread(ThreadGroup group)
	{
		//set a temporary group name
		super(group, "areasy");

		//set thread name
		this.id = getNextId();
		super.setName("areasy-" + getId());

		//set priority
		this.setPriority(Thread.MIN_PRIORITY);
		this.setDaemon(true);
	}

	/**
	 * Creates a new Thread in the specified thread-group
	 *
	 * @param server runtime server instance.
	 * @param group the Thread group which will contain the new Thread
	 */
	public RuntimeThread(RuntimeServer server, ThreadGroup group)
	{
		this(group);
		setRuntimeServer(server);
	}

	/**
	 * Specify the runtime server instance.
	 *
	 * @param server runtime server instance.
	 */
	public void setRuntimeServer(RuntimeServer server)
	{
		this.server = server;
	}

	/**
	 * Processes the Runnable object assigned to it, whenever one is available
	 */
	public void run()
	{
		while(alive)
		{
			if(getRunner() != null && !isClosing())
			{
				//mark as running
				setRunning();

				try
				{
					//execute runnable action
					if(logger.isDebugEnabled()) logger.debug("Running thread: " + this);
					getRunner().run();
				}
				catch (Throwable t)
				{
					logger.error("A problem occurred while trying to run '" + getRunner() + "' object hosted by this thread: " + t.getMessage());
					if(logger.isDebugEnabled())logger.debug("Exception", t);
				}
			}

			//mark on waiting.
			setWaiting();

			synchronized (this)
			{
				try
				{
					if(logger.isDebugEnabled()) logger.debug("Releasing thread: " + this);
					if(server != null) server.release(this);
				}
				catch (Throwable t)
				{
					logger.error("A problem occurred while trying to release '" + getName() + "' thread: " + t.getMessage());
					if(logger.isDebugEnabled()) logger.debug("Exception", t);
				}

				//wait because it has been not been directly assigned a task..
				try
				{
					this.wait();
				}
				catch (InterruptedException e) { /*nothing to do here */ }
			}
		}

		//remove all flags
		setNothing();
	}

	/**
	 * Set the Runnable process to install
	 *
	 * @param runner the Object to install
	 */
	public void setRunner(Runnable runner)
	{
		this.runner = runner;
	}

	/**
	 * Get the Runnable process executing
	 *
	 * @return the Object executed by this thread
	 */
	public Runnable getRunner()
	{
		return this.runner;
	}

	/**
	 * Test whether the thread is currently executing a process
	 *
	 * @return the status of this thread. If true, the thread is currently
	 *         executing a Runnable process, if false it's waiting for a new process
	 */
	public boolean isRunning()
	{
		return this.running;
	}

	/**
	 * Set the running status of this thread.
	 */
	private void setRunning()
	{
		this.running = true;
		this.waiting = false;
	}

	/**
	 * Test whether the thread is currently waiting
	 *
	 * @return the status of this thread. If true, the thread is currently waiting
	 */
	public boolean isWaiting()
	{
		return this.waiting;
	}

	/**
	 * Set the waiting status of this thread.
	 */
	private void setWaiting()
	{
		this.waiting = true;
		this.running = false;
	}

	private void setNothing()
	{
		this.waiting = false;
		this.running = false;
	}


	/**
	 * Set the closing event status of this thread.
	 */
	protected void setClosing()
	{
		this.closing = true;
		this.alive = false;
	}

	/**
	 * Check closing event for this thread.
	 *
	 * @return true if this thread will be stopped after the next execution
	 */
	public boolean isClosing()
	{
		return this.closing;
	}

	/**
	 * Stops (destroy) the thread.
	 */
	public void close()
	{
		logger.info("Closing thread instance: " + this);

		setClosing();
		if(!isRunning()) setRunner(null);

		synchronized (this)
		{
			notify();
		}
	}

	/**
	 * Get the numeric identifier of this thread
	 *
	 * @return the identifier of the thread
	 */
	public long getId()
	{
		return this.id;
	}

	/**
	 * Interrupts execution of this thread. If the thread is in execution the host thread will
	 * wait 10000 ms and after that will force the interruption.
	 */
	public void interrupt()
	{
		interrupt(0);
	}

	/**
	 * Interrupts execution of this thread. If the thread is in execution the host thread will
	 * wait a custom number of ms and after that will force the interruption.
	 *
	 * @param nosec number of seconds.
	 */
	public void interrupt(int nosec)
	{
		try
		{
			//mark this thread that is under closing event.
			setClosing();
			logger.info("Interrupting thread instance: " + this);

			synchronized (this)
			{
				notify();
			}
		}
		catch (Exception e)
		{
			/*nothing to do here */
			if(logger.isDebugEnabled()) logger.debug("Exception", e);
		}

		if(getRunner() != null)
		{
			int counter = 0;

			while((isRunning() || isWaiting()) && counter < nosec)
			{
				try
				{
					join(1000);
				}
				catch (InterruptedException e) { /*nothing to do here */ }

				counter++;
			}
		}

		super.interrupt();
	}

	/**
     * Returns a string representation of this thread, including the thread's name, priority, and thread group.
     *
     * @return  a string representation of this thread.
     */
	public String toString()
	{
		String description = "Runtime Thread [id = " + getId() + ", name = " + getName() + ", status = " +
				(isRunning() ? (isClosing() ? "running and closing" : "running") : (isWaiting() ? (isClosing() ? "waiting to closed" : "waiting") : "just alive"));

		if(getRunner() instanceof RuntimeRunner)
		{
			description += ", runner = " + (((RuntimeRunner) getRunner()).getRunnerAction() == null ? (RuntimeRunner) getRunner() : ((RuntimeRunner) getRunner()).getRunnerAction()) + "]";
		}
		else description += ", runner = " + getRunner() + "]";

		return description;
	}
}

