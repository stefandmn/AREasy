package org.areasy.runtime.actions.flow.events;

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

import com.bmc.arsys.api.DateInfo;
import com.bmc.arsys.api.Time;
import com.bmc.arsys.api.Timestamp;
import org.areasy.common.data.StringUtility;
import org.areasy.common.support.configuration.Configuration;
import org.areasy.common.support.configuration.base.BaseConfiguration;
import org.areasy.runtime.actions.flow.FlowPatternAction;
import org.areasy.runtime.engine.RuntimeClient;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.runtime.engine.structures.CoreItem;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * THis action is design to find and fire all scheduled runtime jobs.
 */
public class RunSchedulerEvent extends AbstractEvent
{
	/**
	 * Execute event
	 *
	 * @throws org.areasy.runtime.engine.base.AREasyException if any error occurs
	 */
	public void execute() throws AREasyException
	{
		//disable notification
		getAction().getConfiguration().setKey("notification", "false");

		FlowPatternAction.JobEntry jobSearch = getAction().getJobEntryInstance();
		jobSearch.setAttribute(7, new Integer(0));
		jobSearch.setAttribute(2432, new Integer(1));
		jobSearch.setAttribute(536871134, new Integer(0));

		List jobs = jobSearch.search(getAction().getServerConnection());

		for(int i = 0; jobs != null && i < jobs.size(); i++)
		{
			CoreItem job = (FlowPatternAction.JobEntry) jobs.get(i);

			if(isReadyForFire(job)) fireJob(job);
				else logger.debug("Scheduled job didn't fire: " + job);
		}
	}

	protected boolean isReadyForFire(CoreItem job)
	{
		boolean fire;
		int recurrence = (Integer) job.getAttributeValue(2301);
		Object jobDateObject = job.getAttributeValue(2418);
		Object jobTimeObject = job.getAttributeValue(2422);

		Calendar jobStartDate = Calendar.getInstance();
		long jobStartTime = 0;

		Calendar currentDate = Calendar.getInstance();

		if(jobTimeObject != null) jobStartTime = ((Time)jobTimeObject).getValue();

		if(jobDateObject != null)
		{
			Calendar calendar = ((DateInfo)jobDateObject).GetDate();
			jobStartDate.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 0, 0, 0);

			if(jobStartTime > 0) jobStartDate.add(Calendar.SECOND, (int) jobStartTime);
		}
		else jobStartDate.add(Calendar.SECOND, -1);

		fire = currentDate.getTimeInMillis() >= jobStartDate.getTimeInMillis();

		if(fire)
		{
			switch(recurrence)
			{
				case 0:
					fire = isFiredForMinutes(job);
					break;
				case 1:
					fire = isFiredForHours(job);
					break;
				case 2:
					fire = isFiredForDays(job);
					break;
				case 3:
					fire = isFiredForWeeks(job);
					break;
				case 4:
					fire = isFiredForMonths(job);
					break;
				case 5:
					fire = isFiredForYears(job);
					break;
			}
		}

		return fire;
	}

	protected Calendar getLastFiredDate(CoreItem job)
	{
		Object object = job.getAttributeValue(536871171);
		if(object == null) object = job.getAttributeValue(536871135);

		if(object != null)
		{
			Calendar lastExecutionCalendar = Calendar.getInstance();
			Date lastExecutionDate = ((Timestamp)object).toDate();
			lastExecutionCalendar.setTime(lastExecutionDate);

			return lastExecutionCalendar;
		}
		else return null;
	}

	protected boolean isFireAllowed(CoreItem job, long ratio)
	{
		Calendar now = Calendar.getInstance();
		Calendar lastStartExecutionCalendar = getLastFiredDate(job);

		if(lastStartExecutionCalendar != null)
		{
			Calendar nextRegularFire = Calendar.getInstance();
			nextRegularFire.setTimeInMillis( lastStartExecutionCalendar.getTimeInMillis() + ratio * 1000);

			return now.getTimeInMillis() >= nextRegularFire.getTimeInMillis() ;
		}
		else return true;
	}

	protected boolean isFiredForMinutes(CoreItem job)
	{
		Object freqObject = job.getAttributeValue(2342);

		if(freqObject == null) return false;
		else
		{
			int frequency = (Integer) freqObject;
			return isFireAllowed(job, frequency * 60);
		}
	}

	protected boolean isFiredForHours(CoreItem job)
	{
		Object freqObject = job.getAttributeValue(2344);

		if(freqObject == null) return false;
		{
			int frequency = (Integer) freqObject;
			return isFireAllowed(job, frequency * 3600);
		}
	}

	protected boolean isFiredForDays(CoreItem job)
	{
		boolean fire;
		Calendar calendar = Calendar.getInstance();
		Object freqObject = job.getAttributeValue(2324);

		Object monObject = job.getAttributeValue(2318);
		Object tueObject = job.getAttributeValue(2319);
		Object wedObject = job.getAttributeValue(2320);
		Object thuObject = job.getAttributeValue(2321);
		Object friObject = job.getAttributeValue(2322);
		Object satObject = job.getAttributeValue(2323);
		Object sunObject = job.getAttributeValue(2317);

		if(freqObject != null)
		{
			int frequency = (Integer) freqObject;
			fire = isFireAllowed(job, frequency * 24 * 3600);
		}
		else fire = false;

		if(fire)
		{
			int dow = calendar.get(Calendar.DAY_OF_WEEK);

			switch(dow)
			{
				case Calendar.MONDAY:
					fire = monObject != null;
					break;
				case Calendar.TUESDAY:
					fire = tueObject != null;
					break;
				case Calendar.WEDNESDAY:
					fire = wedObject != null;
					break;
				case Calendar.THURSDAY:
					fire = thuObject != null;
					break;
				case Calendar.FRIDAY:
					fire = friObject != null;
					break;
				case Calendar.SATURDAY:
					fire = satObject != null;
					break;
				case Calendar.SUNDAY:
					fire = sunObject != null;
					break;
			}

			if(fire)
			{
				long jobStartTime = 0;
				long nowTime = calendar.get(Calendar.HOUR_OF_DAY) * 3600 + calendar.get(Calendar.MINUTE) * 60 + calendar.get(Calendar.SECOND);

				Object jobTimeObject = job.getAttributeValue(2422);
				if(jobTimeObject != null) jobStartTime = ((Time)jobTimeObject).getValue();

				if( jobStartTime > 0) fire &= nowTime > jobStartTime;
			}
		}

		return fire;
	}

	protected boolean isFiredForWeeks(CoreItem job)
	{
		boolean fire;
		Calendar calendar = Calendar.getInstance();
		Object freqObject = job.getAttributeValue(2316);

		if(freqObject != null)
		{
			int frequency = (Integer) freqObject;
			fire = isFireAllowed(job, frequency * 7 * 24 * 3600);
		}
		else fire = false;

		if(fire)
		{
			long jobStartTime = 0;
			long nowTime = calendar.get(Calendar.HOUR_OF_DAY) * 3600 + calendar.get(Calendar.MINUTE) * 60 + calendar.get(Calendar.SECOND);

			Object jobTimeObject = job.getAttributeValue(2422);
			if(jobTimeObject != null) jobStartTime = ((Time)jobTimeObject).getValue();

			if( jobStartTime > 0) fire &= nowTime > jobStartTime;
		}

		return fire;
	}

	protected boolean isFiredForMonths(CoreItem job)
	{
		boolean fire = false;
		Calendar calendar = Calendar.getInstance();
		Calendar lastStartExecutionCalendar = getLastFiredDate(job);

		Object mbDayObject = job.getAttributeValue(2309);
		Object mbDowObject = job.getAttributeValue(2312);

		if(mbDayObject != null)
		{
			Object domObject = job.getAttributeValue(2310);
			Object fomObject = job.getAttributeValue(2311);

			if(domObject == null || fomObject == null) fire = false;
			else
			{
				int day = calendar.get(Calendar.DAY_OF_MONTH);
				fire = day == ((Integer) domObject).intValue();

				if(fire)
				{
					int month = calendar.get(Calendar.MONTH);
					int frequence = ((Integer) fomObject).intValue();
					int lastmonth = lastStartExecutionCalendar.get(Calendar.MONTH);

					fire = lastmonth >= month + frequence;
				}
			}
		}
		else if(mbDowObject != null)
		{
			Object womObject = job.getAttributeValue(2313);
			Object dowObject = job.getAttributeValue(2314);
			Object fomObject = job.getAttributeValue(2315);

			if(womObject == null || dowObject == null || fomObject == null) fire = false;
			else
			{
				int day = calendar.get(Calendar.DAY_OF_WEEK);
				int week = calendar.get(Calendar.WEEK_OF_MONTH);

				fire = week == (((Integer) womObject).intValue() + 1);

				if(fire)
				{
					fire = day == (((Integer) dowObject).intValue() + 1);

					if(fire)
					{
						int month = calendar.get(Calendar.MONTH);
						int frequence = ((Integer) fomObject).intValue();
						int lastmonth = lastStartExecutionCalendar.get(Calendar.MONTH);

						fire = lastmonth >= month + frequence;
					}
				}
			}
		}

		if(fire)
		{
			long jobStartTime = 0;
			long nowTime = calendar.get(Calendar.HOUR_OF_DAY) * 3600 + calendar.get(Calendar.MINUTE) * 60 + calendar.get(Calendar.SECOND);

			Object jobTimeObject = job.getAttributeValue(2422);
			if(jobTimeObject != null) jobStartTime = ((Time)jobTimeObject).getValue();

			if( jobStartTime > 0) fire &= nowTime > jobStartTime;
		}

		return fire;
	}

	protected boolean isFiredForYears(CoreItem job)
	{
		boolean fire = false;
		Calendar calendar = Calendar.getInstance();
		Calendar lastStartExecutionCalendar = getLastFiredDate(job);

		Object ybDayObject = job.getAttributeValue(2302);
		Object ybDowObject = job.getAttributeValue(2305);

		if(ybDayObject != null)
		{
			Object domObject = job.getAttributeValue(2304);
			Object moyObject = job.getAttributeValue(2303);

			if(domObject == null || moyObject == null) fire = false;
			else
			{
				int day = calendar.get(Calendar.DAY_OF_MONTH);
				fire = day == ((Integer) domObject).intValue();

				if(fire)
				{
					int year = calendar.get(Calendar.YEAR);
					int lastyear = lastStartExecutionCalendar.get(Calendar.MONTH);

					fire = lastyear >= year + 1;
				}
			}
		}
		else if(ybDowObject != null)
		{
			Object womObject = job.getAttributeValue(2313);
			Object dowObject = job.getAttributeValue(2314);
			Object moyObject = job.getAttributeValue(2315);

			if(womObject == null || dowObject == null || moyObject == null) fire = false;
			else
			{
				int day = calendar.get(Calendar.DAY_OF_WEEK);
				int week = calendar.get(Calendar.WEEK_OF_MONTH);
				int month = calendar.get(Calendar.MONTH);

				fire = week == (((Integer) womObject).intValue() + 1);

				if(fire)
				{
					fire = day == (((Integer) dowObject).intValue() + 1);

					if(fire)
					{
						fire = month == (((Integer) moyObject).intValue());

						if(fire)
						{
							int year = calendar.get(Calendar.MONTH);
							int lastyear = lastStartExecutionCalendar.get(Calendar.MONTH);

							fire = lastyear >= year + 1;
						}
					}
				}
			}
		}

		if(fire)
		{
			long jobStartTime = 0;
			long nowTime = calendar.get(Calendar.HOUR_OF_DAY) * 3600 + calendar.get(Calendar.MINUTE) * 60 + calendar.get(Calendar.SECOND);

			Object jobTimeObject = job.getAttributeValue(2422);
			if(jobTimeObject != null) jobStartTime = ((Time)jobTimeObject).getValue();

			if( jobStartTime > 0) fire &= nowTime > jobStartTime;
		}

		return fire;
	}

	protected void fireJob(CoreItem job) throws AREasyException
	{
		String actionName = job.getStringAttributeValue(536871088);
		String instanceId = job.getStringAttributeValue(179);
		String actionConfig = job.getStringAttributeValue(2431);

		if(actionName != null)
		{
			Configuration config = new BaseConfiguration();
			config.setKey("asynchron", "true");
			config.setKey("scheduled", "true");
			config.setKey("action", actionName);
			config.setKey("instanceid", instanceId);

			if(actionConfig != null)
			{
				Configuration specific = getAction().getManager().getConfiguration(actionConfig);
				config.merge(specific);
			}

			if(StringUtility.equals(actionName, "data.processflow")) config.setKey("event", "runworkflow");

			RuntimeClient client = new RuntimeClient(getAction().getManager());
			client.run(config);

			logger.info("Scheduler job fired: " + job);
		}
	}
}
