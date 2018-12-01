package org.areasy.runtime.engine.workflows;

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

import org.areasy.runtime.engine.base.ARDictionary;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.common.data.BooleanUtility;
import org.areasy.common.data.StringUtility;
import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;
import org.areasy.common.velocity.Velocity;
import org.areasy.common.velocity.context.Context;

import java.io.*;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Workflow processor: data format and reader from configuration and excel files
 *
 */
public abstract class ProcessorLevel0Reader implements ARDictionary
{
	private static Logger logger = LoggerFactory.getLog(ProcessorLevel1Context.class);

	private final static char chars[] = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };

	/**
	 * Get column index from a generic column name.
	 * 
	 * @param value column letter (generic column name)
	 * @return the column index.
	 */
	public static int getIndexFromGenericColumn(String value)
	{
		if(value == null || value.length() == 0) return -1;
		final char data[] = value.trim().toLowerCase().toCharArray();

		int index = -1;

		if(data.length == 1)
		{
			for(int i = 0; index < 0 && i < chars.length; i++)
			{
				if(data[0] == chars[i]) index = i;
			}
		}
		else if(data.length == 2)
		{
			int first = -1;
			int last = -1;
			int x = 0;

			while(x < chars.length && first < 0)
			{
				if(first < 0 && data[0] == chars[x]) first = x;
				x++;
			}

			x = 0;
			while(x < chars.length && last < 0)
			{
				if(last < 0 && data[1] == chars[x]) last = x;
				x++;
			}

			index = chars.length * (first + 1) + last;
			logger.debug("Index values: first = " + first + ", last = " + last + ", index = " + index);
		}

		return index;
	}

	/**
	 * Get generic column name from column index.
	 *
	 * @param index column index
	 * @return the generic column name (in excel format).
	 */
	public static String getGenericColumnFromIndex(int index)
	{
		String value = null;

		if(index >= chars.length)
		{
			int part1 = Math.max(0, index % chars.length);
			int part2 = Math.max(0, (index / chars.length) - 1);

			value = String.valueOf(chars[part2]).toUpperCase() + String.valueOf(chars[part1]).toUpperCase();
		}
		else value = String.valueOf(chars[index]).toUpperCase();

		return value;
	}

	public static String getKeysToString(List keyids, List keyvalues)
	{
		String output = "Key(s) [";

		for(int i = 0; keyids != null && i < keyids.size(); i++)
		{
			Object key = keyids.get(i);
			String value = "";

			if(keyvalues != null && i < keyvalues.size()) value = (String) keyvalues.get(i);

			output += key + " = " + value;
			if(i < keyids.size() - 1) output += ", ";
		}

		output += "]";

		return output;
	}

	public static String getKeysToString(Map maps)
	{
		String output = "Key(s) [";

		if(maps == null) return null;
		Iterator iterator = maps.keySet().iterator();

		int i = 0;
		while(iterator.hasNext())
		{
			Object key = iterator.next();
			String value = (String) maps.get(key);

			output += key + " = " + value;
			if(i < maps.size() - 1) output += ", ";
		}

		output += "]";

		return output;
	}

	/**
	 * Process the request and fill in the temporary template with the values
	 * you set in the Context. This method will trim the resulted text.
	 *
	 * @param context Velocity context
	 * @param expression    input string containing the VTL to be rendered
	 * @return The process text as a String.
	 */
	public static boolean evaluate(Context context, String expression)
	{
		return evaluate(context, null, expression);
	}

	/**
	 * Process the request and fill in the temporary template with the values
	 * you set in the Context. This method will trim the resulted text.
	 *
	 * @param context Velocity context
	 * @param map parameter's map
	 * @param expression    input string containing the VTL to be rendered
	 * @return The process text as a String.
	 */
	public static boolean evaluate(Context context, Map map, String expression)
	{
		boolean checked = true;

		try
		{
			if(StringUtility.isNotEmpty(expression))
			{
				if(map != null && !map.isEmpty())
				{
					Iterator iterator = map.keySet().iterator();

					while(iterator != null && iterator.hasNext())
					{
						Object key = iterator.next();
						Object value = map.get(key);

						context.put("v" + key, value);
					}
				}

				String template = "#if(" + expression + ")true\n#else\nfalse\n#end";
				checked = BooleanUtility.toBoolean(parseText(context, template));
			}
		}
		catch(Throwable th)
		{
			checked = false;

			logger.error("Error evaluation expression: " + th.getMessage());
			logger.debug("Exception", th);
		}

		return checked;
	}

	/**
	 * Process the request and fill in the temporary template with the values
	 * you set in the Context. This method will trim the resulted text.
	 *
	 * @param context Velocity context
	 * @param source    input string containing the VTL to be rendered
	 * @return The process text as a String.
	 * @throws AREasyException if any parsing and execution exeption will occur
	 */
	public static String parseText(Context context, String source) throws AREasyException
	{
		String text = null;

		String charset = "UTF-8";
		ByteArrayOutputStream output = null;
		OutputStreamWriter writer = null;

		if(source == null) return "";

		try
		{
			output = new ByteArrayOutputStream();
			writer = new OutputStreamWriter(output, charset);

			Velocity.evaluate(context, writer, charset, source);

			try
			{
				writer.flush();
			}
			catch (Exception ignored)
			{
				logger.debug("Ignored error when writer is closed: " + ignored.toString());
			}

			text = output.toString(charset);

			writer.close();
			writer = null;
		}
		catch (Throwable th)
		{
			throw new AREasyException(th);
		}
		finally
		{
			try
			{
				if (output != null) output.close();
			}
			catch (IOException ignored)
			{
				logger.debug("Ignored error when output is closed: " + ignored.toString());
			}

			output = null;
		}

		return StringUtility.trim(text);
	}

	/**
	 * Process the request and fill in the temporary template with the values
	 * you set in the Context. This method will trim the resulted text.
	 *
	 * @param context Velocity context
	 * @param fileIn input file structure containing the VTL to be rendered
	 * @return The process text as a String.
	 * @throws AREasyException if any parsing and execution exeption will occur
	 */
	public static String parseTemplate(Context context, File fileIn) throws AREasyException
	{
		String content = null;

		try
		{
			char allElem[];

			if (fileIn.exists())
			{
				InputStreamReader in = new InputStreamReader(new FileInputStream(fileIn.getAbsolutePath()), "UTF-8");

				allElem = new char[(int) fileIn.length()];
				in.read(allElem);

				content = String.valueOf(allElem);
			}

			allElem = null;
		}
		catch (Exception e)
		{
			content = "";
		}

		return parseText(context, content);
	}
}
