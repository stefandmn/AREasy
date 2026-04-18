package org.areasy.runtime.actions.ars.dev.tools;

import com.bmc.arsys.api.ARException;
import com.bmc.arsys.api.Constants;
import org.areasy.common.data.NumberUtility;
import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;
import org.areasy.runtime.RuntimeAction;
import org.areasy.runtime.actions.ars.dev.DefinitionAction;
import org.areasy.runtime.engine.RuntimeLogger;
import org.areasy.runtime.engine.base.AREasyException;

import java.util.ArrayList;
import java.util.List;

public class RemoveFormFieldAction extends DefinitionAction implements RuntimeAction
{
	private static Logger logger = LoggerFactory.getLog(RemoveFormFieldAction.class);

	public void run() throws AREasyException
	{
		String formName = getConfiguration().getString("form", getConfiguration().getString("formname", null));
		List forms = getConfiguration().getList("forms", new ArrayList());
		if(formName != null && !forms.contains(formName)) forms.add(formName);

		String fieldId = getConfiguration().getString("fieldid", null);
		List fields = getConfiguration().getList("fields", new ArrayList());
		if(fieldId != null && !fields.contains(fieldId)) fields.add(fieldId);

		if(forms.isEmpty() || fields.isEmpty()) throw new AREasyException("Invalid input parameters");

		try
		{
			for(int i = 0; i < forms.size(); i++)
			{
				String form = (String) forms.get(i);

				for(int j = 0; j < fields.size(); j++)
				{
					String field = (String) fields.get(j);
					getServerConnection().getContext().deleteField(form, NumberUtility.toInt(field), Constants.AR_FIELD_FORCE_DELETE);
					RuntimeLogger.info("Removed field id " + field + " from form " + form);
				}
			}
		}
		catch (ARException e)
		{
			throw new AREasyException("Failed to remove field(s) from form '" + formName + "': " + e.getMessage(), e);
		}
	}
}
