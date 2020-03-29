package org.areasy.runtime.engine.services.cron4j;

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

import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

/**
 * This class represents the attributes of a cron entry (an alarm).
 */
public class CronEntry implements Comparable, java.io.Serializable
{
	private static Logger logger = LoggerFactory.getLog(CronEntry.class);
	
	private int[] minutes = {-1};
	private static int minMinute = 0;
	private static int maxMinute = 59;

	private int[] hours = {-1};
	private static int minHour = 0;
	private static int maxHour = 23;

	private int[] daysOfMonth = {-1};
	private static int minDayOfMonth = 1;
	// maxDayOfMonth varies by month

	private int[] months = {-1};
	private static int minMonth = 0;
	private static int maxMonth = 11;

	private int[] daysOfWeek = {-1};
	private static int minDayOfWeek = 1;
	private static int maxDayOfWeek = 7;

	private int year = -1; // no support for a list of years -- must be * or specified

	private String name;
	private static int UNIQUE = 0; // used to generate names if they are null

	private boolean isRelative;
	public boolean isRepeating;
	public long alarmTime;
	private long lastUpdateTime;
	private transient CronListener listener;

	/**
	 * Creates a new CronEntry.  Fixed date format: this alarm will happen once, at
	 * the timestamp given.
	 *
	 * @param date	 the alarm date to be added.
	 * @param listener the alarm listener.
	 * @throws CronException if the alarm date is in the past
	 *                           (or less than 1 second away from the current date).
	 */
	public CronEntry(String name, Date date, CronListener listener) throws CronException
	{
		setName(name);
		this.listener = listener;
		
		Calendar alarm = Calendar.getInstance();
		alarm.setTime(date);
		
		minutes = new int[]{alarm.get(Calendar.MINUTE)};
		hours = new int[]{alarm.get(Calendar.HOUR_OF_DAY)};
		daysOfMonth = new int[]{alarm.get(Calendar.DAY_OF_MONTH)};
		months = new int[]{alarm.get(Calendar.MONTH)};
		year = alarm.get(Calendar.YEAR);

		isRepeating = false;
		isRelative = false;
		alarmTime = date.getTime();
		
		checkAlarmTime();
	}

	/**
	 * @deprecated for backwards compatibility, w/o name param:
	 */
	public CronEntry(Date date, CronListener listener) throws Exception
	{
		this(null, date, listener);
	}

	/**
	 * Creates a new JobEntry.  Delay format: this alarm will happen once or
	 * repeatedly, at increments of the number of minutes given.
	 *
	 * @param name		keeps the alarm unique from other alarms with the same schedule, and used for debugging.
	 * @param delayMinutes the alarm delay in minutes (relative to now).
	 * @param isRepeating <code>true</code> if the alarm must be
	 *                     reactivated, <code>false</code> otherwise.
	 * @param listener	 the alarm listener.
	 * @throws CronException if the alarm date is in the past (or less than 1 second closed to the current date).
	 */
	public CronEntry(String name, int delayMinutes, boolean isRepeating, CronListener listener) throws CronException
	{
		if (delayMinutes < 1) throw new CronException("Cron entry delay is less than one minute");

		setName(name);

		minutes = new int[]{delayMinutes};
		this.listener = listener;
		this.isRepeating = isRepeating;
		isRelative = true;

		updateCronTime();
	}

	/**
	 * @deprecated for backwards compatibility, w/o name param:
	 */
	public CronEntry(int delayMinutes, boolean isRepeating, CronListener listener) throws CronException
	{
		this(null, delayMinutes, isRepeating, listener);
	}


	/**
	 * <p>Creates a new Cron Entry.  Basic cron format - use each field to
	 * restrict alarms to a specific minute, hour, etc. OR pass in -1 to allow
	 * all values of that field.</p>
	 * <p/>
	 * <p>Params of (30, 13, -1, -1, 2, -1, listener) schedule an alarm for
	 * 1:30pm every Monday.</p>
	 * <p/>
	 * <p>NOTE: if both dayOfMonth and dayOfWeek are restricted, each alarm will
	 * be scheduled for the sooner match.</p>
	 *
	 * @param minute	 minute of the alarm. Allowed values 0-59.
	 * @param hour	   hour of the alarm. Allowed values 0-23.
	 * @param dayOfMonth day of month of the alarm (-1 if every
	 *                   day). Allowed values 1-31.
	 * @param month	  month of the alarm (-1 if every month). Allowed values
	 *                   0-11 (0 = January, 1 = February, ...). <code>java.util.Calendar</code>
	 *                   constants can be used.
	 * @param dayOfWeek  day of week of the alarm (-1 if every day). This
	 *                   attribute is exclusive with <code>dayOfMonth</code>. Allowed values 1-7
	 *                   (1 = Sunday, 2 = Monday, ...). <code>java.util.Calendar</code> constants
	 *                   can be used.
	 * @param year	   year of the alarm. When this field is not set (i.e. -1)
	 *                   the alarm is repetitive (i.e. it is rescheduled when reached).
	 * @param listener   the alarm listener.
	 * @return the CronEntry.
	 * @throws CronException if the alarm date is in the past
	 *                           (or less than 1 second away from the current date).
	 */
	public CronEntry(String name, int minute, int hour, int dayOfMonth, int month, int dayOfWeek, int year, CronListener listener) throws CronException
	{
		this(name, new int[]{minute}, new int[]{hour}, new int[]{dayOfMonth}, new int[]{month}, new int[]{dayOfWeek}, year, listener);
	}

	/**
	 * @deprecated for backwards compatibility, w/o name param:
	 */
	public CronEntry(int minute, int hour, int dayOfMonth, int month, int dayOfWeek, int year, CronListener listener) throws CronException
	{
		this(null, minute, hour, dayOfMonth, month, dayOfWeek, year, listener);
	}


	/**
	 * <p>Creates a new CronEntry.  Extended cron format - supports lists
	 * of values for each field, or {-1} to allow all values for that field.</p>
	 * <p/>
	 * <p>Params of (30, 13, -1, -1, 2, -1, listener) schedule an alarm for
	 * 1:30pm every Monday.</p>
	 * <p/>
	 * <p>NOTE: if both dayOfMonth and dayOfWeek are restricted, each alarm will
	 * be scheduled for the sooner match.</p>
	 *
	 * @param minutes	 valid minutes of the alarm. Allowed values
	 *                    0-59, or {-1} for all.
	 * @param hours	   valid hours of the alarm. Allowed values 0-23,
	 *                    or {-1} for all.
	 * @param daysOfMonth valid days of month of the alarm.  Allowed
	 *                    values 1-31, or {-1} for all.
	 * @param months	  valid months of the alarm. Allowed values
	 *                    0-11 (0 = January, 1 = February, ...), or {-1} for all.
	 *                    <code>java.util.Calendar</code> constants can be used.
	 * @param daysOfWeek  valid days of week of the alarm. This attribute
	 *                    is exclusive with <code>dayOfMonth</code>. Allowed values 1-7
	 *                    (1 = Sunday, 2 = Monday, ...), or {-1} for all.
	 *                    <code>java.util.Calendar</code> constants can be used.
	 * @param year		year of the alarm. When this field is not set (i.e. -1)
	 *                    the alarm is repetitive (i.e. it is rescheduled when reached).
	 * @param listener	the alarm listener.
	 * @return the CronEntry.
	 * @throws CronException if the alarm date is in the past
	 *                           (or less than 1 second away from the current date).
	 */
	public CronEntry(String name, int[] minutes, int[] hours, int[] daysOfMonth, int[] months, int[] daysOfWeek, int year, CronListener listener) throws CronException
	{
		setName(name);

		this.minutes = minutes;
		this.hours = hours;
		this.daysOfMonth = daysOfMonth;
		this.months = months;
		this.daysOfWeek = daysOfWeek;
		this.year = year;
		this.listener = listener;
		isRepeating = (year == -1);
		isRelative = false;

		updateCronTime();
		checkAlarmTime();
	}

	/**
	 * @deprecated for backwards compatibility, w/o name param:
	 */
	public CronEntry(int[] minutes, int[] hours, int[] daysOfMonth, int[] months, int[] daysOfWeek, int year, CronListener listener) throws CronException
	{
		this(null, minutes, hours, daysOfMonth, months, daysOfWeek, year, listener);
	}

	/**
	 * Just make sure it's not null -- and if it is, make it unique.
	 *
	 * @param name name of this cron entry
	 */
	private void setName(String name)
	{
		this.name = name;
		if (this.name == null) this.name = "areasy-cron-" + (UNIQUE++);
	}

	public String getName()
	{
		return name;
	}

	/**
	 * Checks that alarm is not in the past, or less than 1 second
	 * away.
	 *
	 * @throws CronException if the alarm date is in the past (or less than 1 second in the future).
	 */
	void checkAlarmTime() throws CronException
	{
		long delay = alarmTime - System.currentTimeMillis();

		if (delay <= 1000)
		{
			throw new CronException("Cron delay is less than 1 second in the future");
		}
	}

	/**
	 * Notifies the listener.
	 */
	public void ringCron()
	{
		listener.handleCron(this);
	}

	/**
	 * Updates this alarm entry to the next valid alarm time, AFTER the current time.
	 */
	public void updateCronTime()
	{
		Calendar now = Calendar.getInstance();

		if (isRelative)
		{
			// relative only uses minutes field, with only a single value (NOT -1)
			alarmTime = now.getTime().getTime() + (minutes[0] * 60000);
		}
		else
		{
			Calendar alarm = (Calendar) now.clone();
			alarm.set(Calendar.SECOND, 0);
			logger.debug("Update cron time: " + now.getTime());

			// increase alarm minutes
			int current = alarm.get(Calendar.MINUTE);
			int offset = 0;

			// force increment at least to next minute
			offset = getOffsetToNext(current, minMinute, maxMinute, minutes);
			alarm.add(Calendar.MINUTE, offset);
			logger.debug("Set alarm time after min: " + alarm.getTime());

			// update alarm hours if necessary
			current = alarm.get(Calendar.HOUR_OF_DAY);  // (as updated by minute shift)
			offset = getOffsetToNextOrEqual(current, minHour, maxHour, hours);
			alarm.add(Calendar.HOUR_OF_DAY, offset);
			logger.debug("Set alarm time after hour (current: " + current + "): " + alarm.getTime());

			// If days of month AND days of week are restricted, we take whichever match comes sooner.
			// If only one is restricted, take the first match for that one. If neither is restricted, don't do anything.
			if (daysOfMonth[0] != -1 && daysOfWeek[0] != -1)
			{
				// BOTH are restricted - take earlier match
				Calendar dayOfWeekAlarm = (Calendar) alarm.clone();
				updateDayOfWeekAndMonth(dayOfWeekAlarm);

				Calendar dayOfMonthAlarm = (Calendar) alarm.clone();
				updateDayOfMonthAndMonth(dayOfMonthAlarm);

				// take the earlier one
				if (dayOfMonthAlarm.getTime().getTime() < dayOfWeekAlarm.getTime().getTime())
				{
					alarm = dayOfMonthAlarm;
					logger.debug("Set alarm time after dayOfMonth CLOSER: " + alarm.getTime());
				}
				else
				{
					alarm = dayOfWeekAlarm;
					logger.debug("Set alarm time after dayOfWeek CLOSER: " + alarm.getTime());
				}
			}
			else if (daysOfWeek[0] != -1) // only dayOfWeek is restricted
			{
				// update dayInWeek and month if necessary
				updateDayOfWeekAndMonth(alarm);
				logger.debug("Set alarm time after dayOfWeek: " + alarm.getTime());
			}
			else if (daysOfMonth[0] != -1) // only dayOfMonth is restricted
			{
				// update dayInMonth and month if necessary
				updateDayOfMonthAndMonth(alarm);
				logger.debug("Set alarm time after dayOfMonth: " + alarm.getTime());
			}
			// else if neither is restricted (both[0] == -1), we don't need to do anything.

			alarmTime = alarm.getTime().getTime();
			lastUpdateTime = System.currentTimeMillis();
		}

		logger.debug("Set cron entry alarm time: " + new Date(alarmTime));
	}

	/**
	 * daysInMonth can't use simple offsets like the other fields, because the
	 * number of days varies per month (think of an alarm that executes on every
	 * 31st).  Instead we advance month and dayInMonth together until we're on a
	 * matching value pair.
	 */
	void updateDayOfMonthAndMonth(Calendar alarm)
	{
		int currentMonth = alarm.get(Calendar.MONTH);
		int currentDayOfMonth = alarm.get(Calendar.DAY_OF_MONTH);
		int offset = 0;

		// loop until we have a valid day AND month (if current is invalid)
		while (!isIn(currentMonth, months) || !isIn(currentDayOfMonth, daysOfMonth))
		{
			// if current month is invalid, advance to 1st day of next valid month
			if (!isIn(currentMonth, months))
			{
				offset = getOffsetToNextOrEqual(currentMonth, minMonth, maxMonth, months);
				alarm.add(Calendar.MONTH, offset);
				alarm.set(Calendar.DAY_OF_MONTH, 1);
				currentDayOfMonth = 1;
			}

			// advance to the next valid day of month, if necessary
			if (!isIn(currentDayOfMonth, daysOfMonth))
			{
				int maxDayOfMonth = alarm.getActualMaximum(Calendar.DAY_OF_MONTH);
				offset = getOffsetToNextOrEqual(currentDayOfMonth, minDayOfMonth, maxDayOfMonth, daysOfMonth);
				alarm.add(Calendar.DAY_OF_MONTH, offset);
			}

			currentMonth = alarm.get(Calendar.MONTH);
			currentDayOfMonth = alarm.get(Calendar.DAY_OF_MONTH);
		}
	}

	void updateDayOfWeekAndMonth(Calendar alarm)
	{
		int currentMonth = alarm.get(Calendar.MONTH);
		int currentDayOfWeek = alarm.get(Calendar.DAY_OF_WEEK);
		int offset = 0;

		// loop until we have a valid day AND month (if current is invalid)
		while (!isIn(currentMonth, months) || !isIn(currentDayOfWeek, daysOfWeek))
		{
			// if current month is invalid, advance to 1st day of next valid month
			if (!isIn(currentMonth, months))
			{
				offset = getOffsetToNextOrEqual(currentMonth, minMonth, maxMonth, months);
				alarm.add(Calendar.MONTH, offset);
				alarm.set(Calendar.DAY_OF_MONTH, 1);
				currentDayOfWeek = alarm.get(Calendar.DAY_OF_WEEK);
			}

			// advance to the next valid day of week, if necessary
			if (!isIn(currentDayOfWeek, daysOfWeek))
			{
				offset = getOffsetToNextOrEqual(currentDayOfWeek, minDayOfWeek, maxDayOfWeek, daysOfWeek);
				alarm.add(Calendar.DAY_OF_YEAR, offset);
			}

			currentDayOfWeek = alarm.get(Calendar.DAY_OF_WEEK);
			currentMonth = alarm.get(Calendar.MONTH);
		}
	}

	/**
	 * Obtain offset value: <br/>
	 * If values = {-1} offset is 1 (because next value definitely matches)
	 * If current < last(values) offset is diff to next valid value
	 * If current >= last(values) offset is diff to values[0], wrapping from max to min
	 */
	static int getOffsetToNext(int current, int min, int max, int[] values)
	{
		int offset = 0;

		// find the distance to the closest valid value > current (wrapping if necessary)

		// {-1} means *  -- offset is 1 because current++ is valid value
		if (values[0] == -1)
		{
			offset = 1;
		}
		else
		{
			// need to wrap
			if (current >= last(values))
			{
				int next = values[0];
				offset = (max - current + 1) + (next - min);
			}
			else // current < max(values) -- find next valid value after current
			{
findvalue:
				for (int i = 0; i < values.length; i++)
				{
					if (current < values[i])
					{
						offset = values[i] - current;
						break findvalue;
					}
				}
			} // end current < max(values)
		}

		return offset;
	}

	/**
	 * Obtain offset value: <br/>
	 * If values = {-1} or current is valid offset is 0.
	 * If current < last(values) offset is diff to next valid value
	 * If current >= last(values) offset is diff to values[0], wrapping from max to min
	 */
	static int getOffsetToNextOrEqual(int current, int min, int max, int[] values)
	{
		int offset = 0;
		int[] safeValues = null;

		// find the distance to the closest valid value >= current (wrapping if necessary)

		// {-1} means *  -- offset is 0 if current is valid value
		if (values[0] == -1 || isIn(current, values))
		{
			offset = 0;
		}
		else
		{
			safeValues = discardValuesOverMax(values, max);

			// need to wrap
			if (current > last(safeValues))
			{
				int next = safeValues[0];
				offset = (max - current + 1) + (next - min);
			}
			else // current <= max(values) -- find next valid value
			{
findvalue:
				for (int i = 0; i < values.length; i++)
				{
					if (current < safeValues[i])
					{
						offset = safeValues[i] - current;
						break findvalue;
					}
				}
			} // end current <= max(values)
		}

		return offset;
	}

	/**
	 * Handles -1 in values as * and returns true
	 * otherwise returns true iff given value is in the array
	 */
	static boolean isIn(int find, int[] values)
	{
		if (values[0] == -1)
		{
			return true;
		}
		else
		{
			for (int i = 0; i < values.length; i++)
			{
				if (find == values[i]) return true;
			}
			
			return false;
		}
	}

	/**
	 * @return the last int in the array
	 */
	static int last(int[] intArray)
	{
		return intArray[intArray.length - 1];
	}

	/**
	 * Assumes inputted values are not null, have at least one value, and are in
	 * ascending order.
	 *
	 * @return copy of values without any trailing values that exceed the max
	 */
	static int[] discardValuesOverMax(int[] values, int max)
	{
		int[] safeValues = null;
		for (int i = 0; i < values.length; i++)
		{
			if (values[i] > max)
			{
				safeValues = new int[i];
				System.arraycopy(values, 0, safeValues, 0, i);
				
				return safeValues;
			}
		}
		
		return values;
	}

	private static String arrToString(int[] intArray)
	{
		if (intArray == null) return "null";
		if (intArray.length == 0) return "{}";

		String s = "{";
		for (int i = 0; i < intArray.length - 1; i++)
		{
			s += intArray[i] + ", ";
		}
		
		s += intArray[intArray.length - 1] + "}";

		return s;
	}

	/**
	 * Compares this JobEntry with the specified JobEntry for order.
	 * One twist -- if the alarmTime matches, this alarm will STILL place
	 * itself before the other based on the lastUpdateTime.  If the other
	 * alarm has been rung more recently, this one should get priority.
	 *
	 * @param obj the JobEntry with which to compare.
	 * @return a negative integer, zero, or a positive integer as this
	 *         JobEntry is less than, equal to, or greater than the given
	 *         JobEntry.
	 * @throws ClassCastException if the specified Object's type
	 *                            prevents it from being compared to this JobEntry.
	 */
	public int compareTo(Object obj)
	{
		CronEntry other = (CronEntry) obj;
		
		if (alarmTime < other.alarmTime) return -1;
		else if (alarmTime > other.alarmTime) return 1;
		else
		{
			if (lastUpdateTime < other.lastUpdateTime) return -1;
			else if (lastUpdateTime > other.lastUpdateTime) return 1;
			else return name.compareTo(other.name);
		}
	}

	/**
	 * Indicates whether some other JobEntry is "equal to" this one.
	 * This is where the name is important, since two alarms can have the
	 * exact same schedule.
	 *
	 * @param obj the JobEntry with which to compare.
	 * @return <code>true if this JobEntry has the same name,
	 *         <code>alarmTime</code> AND the same schedule as the
	 *         obj argument;
	 *         <code>false</code> otherwise.
	 */
	public boolean equals(Object obj)
	{
		CronEntry entry = null;

		if (obj == null || !(obj instanceof CronEntry)) return false;

		entry = (CronEntry) obj;

		return (name.equals(entry.name)
				&& alarmTime == entry.alarmTime
				&& isRelative == entry.isRelative
				&& isRepeating == entry.isRepeating
				&& Arrays.equals(minutes, entry.minutes)
				&& Arrays.equals(hours, entry.hours)
				&& Arrays.equals(daysOfMonth, entry.daysOfMonth)
				&& Arrays.equals(months, entry.months)
				&& Arrays.equals(daysOfWeek, entry.daysOfWeek));
	}

	/**
	 * @return a string representation of this alarm.
	 */
	public String toString()
	{
		if (year != -1) return "Cron Entry (" + name + ") at " + new Date(alarmTime);

		StringBuffer sb = new StringBuffer("Cron Entry (" + name + ") [");
		sb.append("Minute: ");
		sb.append(arrToString(minutes));
		sb.append(", Hour: ");
		sb.append(arrToString(hours));
		sb.append(", DayOfMonth: ");
		sb.append(arrToString(daysOfMonth));
		sb.append(", Month: ");
		sb.append(arrToString(months));
		sb.append(", DayOfWeek: ");
		sb.append(arrToString(daysOfWeek));
		sb.append("] (Next execution date: " + new Date(alarmTime) + ")");
		
		return sb.toString();
	}
}









