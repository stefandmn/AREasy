package org.areasy.common.support.configuration.providers.properties.stream;

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

import org.areasy.common.data.StringUtility;
import org.areasy.common.support.configuration.base.BaseConfigurationEntry;

import java.util.Arrays;
import java.util.List;

/**
 * Dedicated class to parse configuration line from properties files..
 *
 * @version $Id: PropertiesEntry.java,v 1.6 2008/05/21 15:08:32 swd\stefan.damian Exp $
 */
public class PropertiesEntry extends BaseConfigurationEntry
{
	/**
	 * Deault constructor.
	 */
	public PropertiesEntry()
	{
		super();
	}

	/**
	 * Constructor to create a data key.
	 *
	 * @param key configuration key
	 * @param value object configuration value
	 */
	public PropertiesEntry(String key, Object value)
	{
		super(key, value);
	}

	/**
	 * Copy constructor.
	 * @param entry properties entry structure
	 */
	public PropertiesEntry(PropertiesEntry entry)
	{
		super(entry);
	}

	/**
	 * This constructor will analyze an input text and will decide what kind of data entry is:
	 * comment or data key. For any value that contains comma character without escaping sequence
	 * will generate a list of values.
	 *
	 * @param line text line
	 */
	public PropertiesEntry(String line)
	{
		if(StringUtility.isEmpty(line)) setComment("");
		else
		{
			line = line.trim();

			if(line.startsWith(PROPERTY_COMMENT)) setComment(line);
			else
			{
				int index = line.indexOf('=', 0);

				if( index > 0 )
				{
					String key = line.substring(0, index).trim();
					String value = "";

					if( index + 1 < line.length() )
					{
						value = line.substring(index + 1).trim();

						if(StringUtility.isNotEmpty(value))
						{
							if(value != null && value.trim().startsWith(PROPERTY_BODYSTRING)) setData(key, value.substring(PROPERTY_BODYSTRING.length()).trim());
							else
							{
								List list = Arrays.asList( StringUtility.split(value, ','));
                            	setData(key, list);
							}
						}
						else setData(key, value);
					}
					else setData(key, value);
				}
				else setComment(line);
			}
		}
	}

	/**
	 * Set record value for a specified index from values list.
	 *
	 * @param value object value to be set.
	 * @param index index order where to be set the specified value.
	 */
	public void setValue(Object value, int index)
	{
		if(index > this.getValues().size() || index < 0) throw new IndexOutOfBoundsException("Length of values list in out of range");

		super.setValue(value, index);
	}

	/**
	 * Set a property like a comment (transformation from property to comment).
	 */
	public boolean convertInComment()
	{
		if(isData())
		{
			setComment(PROPERTY_COMMENT + " " + getKey() + " = " + valuesToString());
			return true;
		}

		return false;
	}

	/**
	 * Append a new comment text.
	 * 
	 * @param text text line
	 */
	public void appendInComment(String text)
	{
		if(text.trim().startsWith(PROPERTY_COMMENT)) super.appendInComment(text.trim().substring(PROPERTY_COMMENT.length()));
			else super.appendInComment(text);
	}

	/**
	 * Change comment value into a property (if conversion could be made).
	 */
	public boolean convertInData()
	{
		if(isComment())
		{
			PropertiesEntry entry;
			String comment = getComment().trim();

			while(comment.length() > 0 && comment.startsWith(PROPERTY_COMMENT))
			{
				comment = getComment().trim().substring(PROPERTY_COMMENT.length());
			}

			entry = new PropertiesEntry(comment);

			if(entry.isData())
			{
				setData(entry.getKey(), entry.getValues());
				return true;
			}
		}

		return false;
	}

	/**
	 * @return resource statement (line)
	 */
	public String toString()
	{
		if(isData()) return getKey() + " = " + valuesToString();
			else return getComment();
	}

	/**
	 * Convert values list into a string using comma delimiter.
	 * @return a joined values into a string
	 */
	protected String valuesToString()
	{
		if(getValues().size() > 1 ) return StringUtility.join( getValues().toArray(new String[ getValues().size() ]), ',' );
			else if(getValues().size() == 1) return (String) getValue();
				else return "";
	}
}
