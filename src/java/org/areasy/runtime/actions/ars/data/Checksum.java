package org.areasy.runtime.actions.ars.data;

import org.areasy.common.data.StringUtility;
import org.areasy.runtime.engine.RuntimeLogger;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.runtime.engine.structures.CoreItem;
import org.areasy.runtime.engine.workflows.ProcessorLevel1Context;

import java.util.List;

public class Checksum extends BaseData
{
	private String formName = null;

	private int formType = 0;

	/**
	 * Execute the current action.
	 *
	 * @throws AREasyException
	 * if any error will occur
	 */
	public void run() throws AREasyException
	{
		CoreItem search = new CoreItem();
		RuntimeLogger.add(getServerConnection().getUserName() + "@" + getServerConnection().getServerName() + ":" + getServerConnection().getServerPort());

		if(setNextForm(search))
		{
			search.setFormName(formName);
			operation(search, null);
		}
		else throw new AREasyException("Form name is null");
	}
	public void run(CoreItem entry)
	{
		//nothing to do
	}

	public void operation(CoreItem entry, String nextChunkId) throws AREasyException
	{
		int count = 0;
		boolean exit = false;

		String qualification = getQualification();
		int chunk = getConfiguration().getInt("chunk", 1000);

		if(qualification == null)
		{
			setQueryFields(entry);
			if(getConfiguration().getBoolean("usecmdb", false) && getConfiguration().getString("classid", null) != null)  entry.setAttribute(400079600, getConfiguration().getString("classid", null));
			if(getConfiguration().getBoolean("usecmdb", false) && getConfiguration().getString("datasetid", null) != null && StringUtility.indexOf(entry.getFormName(), "AST:") != 0) entry.setAttribute(400127400,getConfiguration().getString("datasetid", null));
			if(nextChunkId != null) entry.setAttribute(1, ">" + nextChunkId);
				else RuntimeLogger.add("\tStructure: " + entry.toFullString(false));

			entry.resetEntryId();
			logger.debug("Searching in '" + entry.getFormName() + "' form, with query: " + entry.toFullString(false));
			count = entry.count(getServerConnection(), chunk);
		}
		else
		{
			if(!qualification.trim().startsWith("(")) qualification = "(" + qualification + ")";
			if(getConfiguration().getBoolean("usecmdb", false) && getConfiguration().getString("classid", null) != null) qualification += " AND ('400079600' = \"" + getConfiguration().getString("classid", null) + "\")";
			if(getConfiguration().getBoolean("usecmdb", false) && getConfiguration().getString("datasetid", null) != null && StringUtility.indexOf(entry.getFormName(), "AST:") != 0) qualification += " AND ('400127400' = \"" + getConfiguration().getString("datasetid", null) + "\")";
			if(nextChunkId != null ) qualification += " AND ('1' > \"" + nextChunkId + "\")";
				else RuntimeLogger.add("\tQuery: " + qualification);

			logger.debug("Searching in '" + entry.getFormName() + "' form, with query: " + qualification);
			count = entry.count(getServerConnection(), qualification, chunk);
		}

		if (chunk > 0 && count == chunk) nextChunkId = entry.getEntryId();
			else nextChunkId = null;

		addRecordsCounter(count);

		if (nextChunkId != null) operation(entry, nextChunkId);
		else
		{
			//record data for previous query
			RuntimeLogger.add("\tCount: " + getRecordsCounter());

			//check if next linked query exist
			if (setNextForm(entry))
			{
				entry = new CoreItem();
				entry.setFormName(formName);

				resetCounters();
				operation(entry, null);
			}
			else exit = true;
		}

		if(exit) RuntimeLogger.infoData();
	}

	protected void operation_old(CoreItem search, String nextChunkId) throws AREasyException
	{
		boolean exit = false;

		//run search operation
		int chunk = getConfiguration().getInt("chunk", 1000);
		List entries = searchData(search, nextChunkId);

		if(entries != null && entries.size() > 0)
		{
			CoreItem entry = (CoreItem) entries.get(entries.size() - 1);

			if (chunk > 0 && entries.size() == chunk) nextChunkId = entry.getEntryId();
				else nextChunkId = null;

			addRecordsCounter(entries.size());
			entries.clear();
			entries = null;
		}


		// run for next chunk
		if (nextChunkId != null)
		{
			operation(search, nextChunkId);
			exit = false;
		}
		else
		{
			//record data for previous query
			RuntimeLogger.add("\tCount: " + getRecordsCounter());

			//check if next linked query exist
			if(setNextForm(search))
			{
				search = new CoreItem();
				search.setFormName(formName);

				resetCounters();
				operation(search, null);
				exit = false;
			}
			else exit = true;
		}

		if(exit) report();
	}

	/**
	 * Search data from Remedy search and in case of matching it will return a list of records
	 *
	 * @param entry <code>CoreItem</code> structure used by read or search process
	 * @return <code>CoreItem</code> structure returned by read operation
	 * @throws AREasyException in case any error occurs
	 */
	protected List searchData(CoreItem entry, String nextChunkId) throws AREasyException
	{
		String qualification = getQualification();
		int chunk = getConfiguration().getInt("chunk", 1000);

		if(qualification == null)
		{
			setQueryFields(entry);
			if(getConfiguration().getBoolean("usecmdb", false) && getConfiguration().getString("classid", null) != null)  entry.setAttribute(400079600, getConfiguration().getString("classid", null));
			if(getConfiguration().getBoolean("usecmdb", false) && getConfiguration().getString("datasetid", null) != null && StringUtility.indexOf(entry.getFormName(), "AST:") != 0) entry.setAttribute(400127400,getConfiguration().getString("datasetid", null));
			if(nextChunkId != null) entry.setAttribute(1, ">" + nextChunkId);
				else RuntimeLogger.add("\tStructure: " + entry);

			logger.debug("Searching with structure: " + entry.toFullString());
			 return entry.search(getServerConnection(), chunk);
		}
		else
		{
			if(!qualification.trim().startsWith("(")) qualification = "(" + qualification + ")";
			if(getConfiguration().getBoolean("usecmdb", false) && getConfiguration().getString("classid", null) != null) qualification += " AND ('400079600' = \"" + getConfiguration().getString("classid", null) + "\")";
			if(getConfiguration().getBoolean("usecmdb", false) && getConfiguration().getString("datasetid", null) != null && StringUtility.indexOf(entry.getFormName(), "AST:") != 0) qualification += " AND ('400127400' = \"" + getConfiguration().getString("datasetid", null) + "\")";
			if(nextChunkId != null ) qualification += " AND ('1' > \"" + nextChunkId + "\")";
				else RuntimeLogger.add("\tQuery: " + qualification);

			logger.debug("Searching in '" + entry.getFormName() + "' form, with query: " + qualification);
			return entry.search(getServerConnection(), qualification, chunk);
		}
	}

	protected boolean setNextForm(CoreItem entry) throws AREasyException
	{
		String currentFormName = null;

		if(entry != null) currentFormName = entry.getFormName();

		if(getConfiguration().getBoolean("usecmdb", false))
		{
			String classId = getConfiguration().getString("classid", null);

			if(getConfiguration().getBoolean("breakdown", false))
			{
				String baseFormName = ProcessorLevel1Context.FORM_BASEELEMENT;
				String classFormName = ProcessorLevel1Context.getCmdbFormName(getServerConnection(), classId);
				String assetFormName = ProcessorLevel1Context.FORM_ASSETATTRIBUTES;

				if(StringUtility.isEmpty(currentFormName))
				{
					this.formName = baseFormName;
					this.formType = 1;
				}
				else if(StringUtility.equalsIgnoreCase(currentFormName, baseFormName))
				{
					this.formName = classFormName;
					this.formType = 1;
				}
				else if(StringUtility.equalsIgnoreCase(currentFormName, classFormName))
				{
					this.formName = assetFormName;
					this.formType = 2;
				}
				else
				{
					this.formName = null;
					this.formType = -1;
				}
			}
			else
			{
				this.formName = ProcessorLevel1Context.getCmdbFormName(getServerConnection(), classId);
				this.formType = 1;
			}
		}
		else
		{
			if(StringUtility.isEmpty(currentFormName) || !StringUtility.equalsIgnoreCase(currentFormName, getConfiguration().getString("form", getConfiguration().getString("formname", null)) ))
			{
				this.formName = getConfiguration().getString("form", getConfiguration().getString("formname", null));
				this.formType = 0;
			}
			else
			{
				this.formName = null;
				this.formType = -1;
			}

		}

		if(this.formType >= 0)
		{
			RuntimeLogger.add(this.formName);
			return true;
		}
		else return false;
	}

	protected String getQualification()
	{
		String labelQualification = "qualification";
		String qualification = null;

		if(this.formType == 2)
		{
			qualification = getConfiguration().getString("ast-" + labelQualification, null);
			if(qualification == null) qualification = getConfiguration().getString(labelQualification, null);
		}
		else if(this.formType == 1)
		{
			qualification = getConfiguration().getString("cmdb-" + labelQualification, null);
			if(qualification == null) qualification = getConfiguration().getString(labelQualification, null);
		}
		else qualification = getConfiguration().getString(labelQualification, null);

		if(qualification != null) return getTranslatedQualification(qualification);
			else return null;
	}
}
