package org.areasy.common.data;


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

import java.util.Date;

/**
 * <p><code>StopWatch</code> provides a convenient API for timings.</p>
 * <p/>
 * <p>To start the watch, call {@link #start()}. At this point you can:</p>
 * <ul>
 * <li>{@link #split()} the watch to get the time whilst the watch continues in the
 * background. {@link #unsplit()} will remove the effect of the split. At this point,
 * these three options are available again.</li>
 * <li>{@link #suspend()} the watch to pause it. {@link #resume()} allows the watch
 * to continue. Any time between the suspend and resume will not be counted in
 * the total. At this point, these three options are available again.</li>
 * <li>{@link #stop()} the watch to complete the timing session.</li>
 * </ul>
 * <p/>
 * <p>It is intended that the output methods {@link #toString()} and {@link #getTime()}
 * should only be called after stop, split or suspend, however a suitable result will
 * be returned at other points.</p>
 * <p/>
 * <p>NOTE: As from v2.1, the methods protect against inappropriate calls.
 * Thus you cannot now call stop before start, resume before suspend or
 * unsplit before split.</p>
 * <p/>
 * <p>1. split(), suspend(), or stop() cannot be invoked twice<br />
 * 2. unsplit() may only be called if the watch has been split()<br />
 * 3. resume() may only be called if the watch has been suspend()<br />
 * 4. start() cannot be called twice without calling reset()</p>
 *
 * @version $Id: StopWatchUtility.java,v 1.3 2008/05/14 09:32:29 swd\stefan.damian Exp $
 */
public class StopWatchUtility
{
	// running states
	private static final int STATE_UNSTARTED = 0;
	private static final int STATE_RUNNING = 1;
	private static final int STATE_STOPPED = 2;
	private static final int STATE_SUSPENDED = 3;

	// split state
	private static final int STATE_UNSPLIT = 10;
	private static final int STATE_SPLIT = 11;

	/**
	 * The current running state of the StopWatch.
	 */
	private int runningState = STATE_UNSTARTED;

	/**
	 * Whether the stopwatch has a split time recorded.
	 */
	private int splitState = STATE_UNSPLIT;

	/**
	 * The start time.
	 */
	private long startTime = -1;
	/**
	 * The stop time.
	 */
	private long stopTime = -1;

	/**
	 * <p>Constructor.</p>
	 */
	public StopWatchUtility()
	{
		//nothing to do here
	}

	/**
	 * <p>Start the stopwatch.</p>
	 * <p/>
	 * <p>This method starts a new timing session, clearing any previous values.</p>
	 *
	 * @throws IllegalStateException if the StopWatch is already running.
	 */
	public void start()
	{
		if (this.runningState == STATE_STOPPED || this.runningState != STATE_UNSTARTED) reset();

		stopTime = -1;
		startTime = System.currentTimeMillis();

		this.runningState = STATE_RUNNING;
	}

	/**
	 * <p>Stop the stopwatch.</p>
	 * <p/>
	 * <p>This method ends a new timing session, allowing the time to be retrieved.</p>
	 *
	 * @throws IllegalStateException if the StopWatch is not running.
	 */
	public void stop()
	{
		if (this.runningState != STATE_RUNNING && this.runningState != STATE_SUSPENDED) throw new IllegalStateException("Stopwatch is not running. ");

		stopTime = System.currentTimeMillis();
		this.runningState = STATE_STOPPED;
	}

	/**
	 * <p>Resets the stopwatch. Stops it if need be. </p>
	 * <p/>
	 * <p>This method clears the internal values to allow the object to be reused.</p>
	 */
	public void reset()
	{
		this.runningState = STATE_UNSTARTED;
		this.splitState = STATE_UNSPLIT;

		startTime = -1;
		stopTime = -1;
	}

	/**
	 * <p>Split the time.</p>
	 * <p/>
	 * <p>This method sets the stop time of the watch to allow a time to be extracted.
	 * The start time is unaffected, enabling {@link #unsplit()} to continue the
	 * timing from the original start point.</p>
	 *
	 * @throws IllegalStateException if the StopWatch is not running.
	 */
	public void split()
	{
		if (this.runningState != STATE_RUNNING) throw new IllegalStateException("Stopwatch is not running. ");

		stopTime = System.currentTimeMillis();
		this.splitState = STATE_SPLIT;
	}

	/**
	 * <p>Remove a split.</p>
	 * <p/>
	 * <p>This method clears the stop time. The start time is unaffected, enabling
	 * timing from the original start point to continue.</p>
	 *
	 * @throws IllegalStateException if the StopWatch has not been split.
	 */
	public void unsplit()
	{
		if (this.splitState != STATE_SPLIT) throw new IllegalStateException("Stopwatch has not been split. ");

		stopTime = -1;
		this.splitState = STATE_UNSPLIT;
	}

	/**
	 * <p>Suspend the stopwatch for later resumption.</p>
	 * <p/>
	 * <p>This method suspends the watch until it is resumed. The watch will not include
	 * time between the suspend and resume calls in the total time.</p>
	 *
	 * @throws IllegalStateException if the StopWatch is not currently running.
	 */
	public void suspend()
	{
		if (this.runningState != STATE_RUNNING) throw new IllegalStateException("Stopwatch must be running to suspend. ");

		stopTime = System.currentTimeMillis();
		this.runningState = STATE_SUSPENDED;
	}

	/**
	 * <p>Resume the stopwatch after a suspend.</p>
	 * <p/>
	 * <p>This method resumes the watch after it was suspended. The watch will not include
	 * time between the suspend and resume calls in the total time.</p>
	 *
	 * @throws IllegalStateException if the StopWatch has not been suspended.
	 */
	public void resume()
	{
		if (this.runningState != STATE_SUSPENDED) throw new IllegalStateException("Stopwatch must be suspended to resume. ");

		startTime += (System.currentTimeMillis() - stopTime);
		stopTime = -1;

		this.runningState = STATE_RUNNING;
	}

	/**
	 * <p>Get the time on the stopwatch.</p>
	 * <p/>
	 * <p>This is either the time between the start and the moment this method
	 * is called, or the amount of time between start and stop.</p>
	 *
	 * @return the time in milliseconds
	 */
	public long getTime()
	{
		if (this.runningState == STATE_STOPPED || this.runningState == STATE_SUSPENDED) return this.stopTime - this.startTime;
			else if (this.runningState == STATE_UNSTARTED) return 0;
				else if (this.runningState == STATE_RUNNING) return System.currentTimeMillis() - this.startTime;

		throw new RuntimeException("Illegal running state has occured. ");
	}

	/**
	 * <p>Get the split time on the stopwatch.</p>
	 * <p/>
	 * <p>This is the time between start and latest split. </p>
	 *
	 * @return the split time in milliseconds
	 * @throws IllegalStateException if the StopWatch has not yet been split.
	 */
	public long getSplitTime()
	{
		if (this.splitState != STATE_SPLIT) throw new IllegalStateException("Stopwatch must be split to get the split time. ");

		return this.stopTime - this.startTime;
	}

	/**
	 * <p>Gets a summary of the time that the stopwatch recorded as a string.</p>
	 * <p/>
	 * <p>The format used is ISO8601-like,
	 * <i>hours</i>:<i>minutes</i>:<i>seconds</i>.<i>milliseconds</i>.</p>
	 *
	 * @return the time as a String
	 */
	public String toString()
	{
		return DurationFormatUtility.formatDurationHMS(getTime());
	}

	/**
	 * <p>Gets a summary of the split time that the stopwatch recorded as a string.</p>
	 * <p/>
	 * <p>The format used is ISO8601-like,
	 * <i>hours</i>:<i>minutes</i>:<i>seconds</i>.<i>milliseconds</i>.</p>
	 *
	 * @return the split time as a String
	 */
	public String toSplitString()
	{
		return DurationFormatUtility.formatDurationHMS(getSplitTime());
	}

	/**
	 * Get the initial time when the counting has ben started
	 *
	 * @return a date/time structure
	 */
	public Date getStartTime()
	{
		return new Date(this.startTime);
	}

}
