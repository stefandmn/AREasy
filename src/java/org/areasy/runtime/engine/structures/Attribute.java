package org.areasy.runtime.engine.structures;

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

import com.bmc.arsys.api.*;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.common.data.NumberUtility;
import org.areasy.common.data.StringUtility;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * ARS core structure, defining an usual form attribute like a pair composed by field and value.
 */
public class Attribute
{
	private String id = null;
	private String label = null;
	private Object value = null;

	private int type = DataType.NULL.toInt();
	private boolean changed = false;

	/**
	 * Default constructor.
	 *
	 * @param fieldId string value for attribute id (describing the field id from an ARS form)
	 * @param fieldValue object value instance.
	 */
	public Attribute(String fieldId, Object fieldValue)
	{
		this.id = fieldId;
		setDefaultValue(fieldValue);
	}

	/**
	 * Secondary constructor.
	 *
	 * @param fieldId long value for attribute id (describing the field id like it is into an ARS form)
	 * @param fieldValue object value instance.
	 */
	public Attribute(long fieldId, Object fieldValue)
	{
		this(String.valueOf(fieldId), fieldValue);
	}

	/**
	 * Other constructor used only to specify the field id and a null value.
	 *
	 * @param fieldId string value for attribute id (describing the field id from an ARS form)
	 */
	public Attribute(String fieldId)
	{
		this.id = fieldId;
	}

	/**
	 * Other constructor used only to specify the field id in <code>long</code> format and a null value.
	 *
	 * @param fieldId string value for attribute id (describing the field id from an ARS form)
	 */
	public Attribute(long fieldId)
	{
		this.id = String.valueOf(fieldId);
	}

	/**
	 * Get field id.
	 *
	 * @return attribute field id.
	 */
	public String getId()
	{
		return id;
	}

	/**
	 * Get field id (number/integer outout).
	 *
	 * @return attribute field id.
	 */
	public int getNumberId()
	{
		return NumberUtility.toInt(id);
	}

	/**
	 * Get object value for this attribute.
	 *
	 * @return the object value.
	 */
	public Object getValue()
	{
		return value;
	}

	/**
	 * Set object value for this attribute structure with a null value
	 */
	public void setNullValue()
	{
		setValue(null);
	}

	/**
	 * Set default object value for this attribute structure with a null value
	 */
	public void setDefaultNullValue()
	{
		setDefaultValue(null);
	}

	/**
	 * Set object value for this attribute structure with an empty value
	 */
	public void setEmptyValue()
	{
		setValue("");
	}

	/**
	 * Set object value for this attribute structure. When this method is called the status of this attributes will be changed 
	 *
	 * @param value object value (could be any data type but in the end will be processed only <code>String</code>,
	 * <code>Diary</code> and <code>AttachmentValue</code> structures)
	 */
	public void setValue(Object value)
	{
		if(value == null)
		{
			if(getValue() != null) setChanged();

			this.value = null;
			setType(DataType.NULL.toInt());
		}
		else
		{
			boolean equals = false;
			Object object = null;

			if (value instanceof Value)
			{
				object = ((Value)value).getValue();
				setType(((Value)value).getDataType().toInt());

                //02.09.2008,RRO - CurrencyValue patch
                if (object instanceof CurrencyValue)
				{
                    if (((CurrencyValue)object).getValue().compareTo(BigDecimal.ZERO) == 0)
					{
						//equals does not work here
                        ((CurrencyValue)object).setValue(BigDecimal.ZERO);
                    }
                }
            }
			else
			{
				object = value;
				setType(DataType.NULL.toInt());
			}

            //RRO 20080916 - SelectFields patch, SDA 20150610, Date and Calendar value conversion to Timestamp
            if (object instanceof Long)
			{
                object = new Integer(((Long)object).intValue());
            }
			else if (object instanceof Date)
			{
				object = new Timestamp((Date)object);
			}
			else if (object instanceof Calendar)
			{
				object = new Timestamp(((Calendar)object).getTime());
			}
			else if(object instanceof File)
			{
				File file = (File)object;

				try
				{
					object = new AttachmentValue(file.getName(), file.getPath());
				}
				catch(IOException ioe)
				{
					throw new RuntimeException("Error creating AttachmentValue object from file '" + file + "'. " + ioe.getMessage(), ioe);
				}
			}
			else if(object instanceof DiaryItem)
			{
				DiaryListValue diary = new DiaryListValue();
				diary.add((DiaryItem) object);

				object = diary;
			}
			else if(object instanceof List && ((List)object).size() > 0 && ((List)object).get(0) instanceof DiaryItem)
			{
				DiaryListValue diary = new DiaryListValue();
				diary.addAll( (List)object );

				object = diary;
			}

            if(object instanceof String && this.value instanceof String) equals = StringUtility.equals((String)object, (String)getValue());
				else equals = object.equals(getValue());

			if(!equals)
			{
				this.value = object;
				setChanged();
			}
		}
	}

	/**
	 * Get original data type (from API provider).
	 *
	 * @return integer value.
	 */
	public int getType()
	{
		return this.type;
	}

	/**
	 * Set original data type (from API provider)
	 *
	 * @param type attribute value data type.
	 */
	public void setType(int type)
	{
		this.type = type;
	}

	/**
	 * Set object value performing a data conversion from string to the corresponding data type specified by value format.
	 * This method should be used especially when an object must be used for interrogations and the value will contain also the operator.
	 *
	 * @param value string value representation.
	 */
	public void setConvertedValue(String value)
	{
		if(value != null && value.length() > 0)
		{
			if(StringUtility.equalsIgnoreCase(value, "null"))
			{
				setValue(null);
			}
			else if(NumberUtility.isNumber(value))
			{
				Number number = NumberUtility.createNumber(value);
				setValue(number);
			}
			else
			{
				if(value.startsWith("\n") && value.endsWith("\n")) value = StringUtility.trim(value.substring(1, value.length() - 2));
				setValue(value);
			}
		}
		else setValue(value);
	}

	/**
	 * Set default object value for this attribute structure. When this method is called the status of this attributes will be unchanged
	 *
	 * @param value object value.
	 */
	public void setDefaultValue(Object value)
	{
		setValue(value);
		setUnchanged();
	}

	/**
	 * Check if actual field value was changed.
	 *
	 * @return true if the value was changed after instance creation.
	 */
	public boolean isChanged()
	{
		return changed;
	}

	/**
	 * Check if actual field value was not changed.
	 *
	 * @return true if the value was not changed after instance creation.
	 */
	public boolean isNotChanged()
	{
		return !isChanged();
	}

	/**
	 * Mark that this field attribute was changed (internal method)
	 */
	protected void setChanged()
	{
		this.changed = true;
	}

	/**
	 * Mark that is field value wasn't changed (internal method used in <code>reset</code> method).
	 */
	protected void setUnchanged()
	{
		this.changed = false;
	}

	/**
	 * Reset the value of this attribute field and specified that wasn't changed.
	 */
	public void reset()
	{
		setValue(null);
		setUnchanged();
	}

	/**
	 * Check if the actual field value is <code>Diary</code> instance.
	 *
	 * @return true if is <code>Diary</code> value structure.
	 */
	public boolean isDiaryValue()
	{
		return getValue() instanceof DiaryListValue;
	}

	/**
	 * Check if the actual field value is <code>AttachmentValue</code> instance.
	 *
	 * @return true if is <code>AttachmentValue</code> value structure.
	 */
	public boolean isAttachmentValueValue()
	{
		return getValue() instanceof AttachmentValue;
	}

	/**
	 * Get <code>long</code> fiel id format.
	 *
	 * @return <code>long</code> fiel id format
	 */
	public long getLongFieldId()
	{
		return NumberUtility.toLong(getId(), 0);
	}

	/**
	 * Get <code>String</code> format of this field value.
	 *
	 * @return <code>String</code> field value.
	 */
	public String getStringValue()
	{
		if(this.value != null) return String.valueOf(this.value);
			else return null;
	}

	/**
	 * Get <code>FieldID</code> structure format from an attribute field value.
	 * 
	 * @return <code>FieldID</code> structure
	 */
	public Integer getFieldIdFormat()
	{
		return Attribute.getFieldIdFormat(getLongFieldId());
	}

	/**
	 * Get <code>FieldID</code> structure format from a string value.
	 *
	 * @param fieldid string value for a field id
	 * @return <code>FieldID</code> structure
	 */
	public static Integer getFieldIdFormat(String fieldid)
	{
		return Attribute.getFieldIdFormat(NumberUtility.toInt(fieldid, 0));
	}

	/**
	 * Get <code>FieldID</code> structure format from a long value.
	 *
	 * @param fieldid long value for a field id
	 * @return <code>FieldID</code> structure
	 */
	public static Integer getFieldIdFormat(long fieldid)
	{
		return (int) fieldid;
	}

	/**
	 * Get <code>int</code> structure format from a long value.
	 *
	 * @param fieldid long value for a field id
	 * @return <code>FieldID</code> structure
	 */
	public static Integer getFieldIdFormat(int fieldid)
	{
		return fieldid;
	}

	/**
	 * Get <code>Value</code> structure from an attribute field value.
	 *
	 * @return <code>Value</code> structure
	 */
	public Value getValueFormat()
	{
		if(value == null) return new Value();
		else if(value instanceof String && getType() != DataType.NULL.toInt()) return new Value( String.valueOf(value), DataType.toDataType(getType()) );
		else if(value instanceof String && getType() == DataType.NULL.toInt()) return new Value( String.valueOf(value) );
		else if(value instanceof DiaryListValue) return new Value( (DiaryListValue)value );
		else if(value instanceof AttachmentValue) return new Value((AttachmentValue)value );
		else if(value instanceof Timestamp) return new Value( ((Timestamp)value) );
		else if(value instanceof Time) return new Value( ((Time)value) );
		else if(value instanceof CurrencyValue) return new Value( ((CurrencyValue)value) );
		else if(value instanceof DateInfo) return new Value( ((DateInfo)value) );
		else if(value instanceof Integer) return new Value( ((Integer)value).intValue() );
		else if(value instanceof Long && getType() != DataType.NULL.toInt()) return new Value(String.valueOf(value), DataType.toDataType(getType()));
		else if(value instanceof Long && getType() == DataType.NULL.toInt()) return new Value(((Long)value).longValue() );
		else if(value instanceof Double) return new Value( ((Double)value).doubleValue() );
		else if(value instanceof Float) return new Value( ((Float)value).floatValue() );
		else if(value instanceof Date) return new Value( new Timestamp( ((Date)value).getTime() ) );
		else if(value instanceof Calendar) return new Value( new Timestamp( ((Calendar)value).getTime() ) );
		else return new Value( String.valueOf(value) );
	}

	/**
	 * Get <code>Value</code> structure from a value.
	 *
	 * @param value object value
	 * @deprecated <b>not use anymore (unstable) because doesn't consider data type member.</b>
	 * @return <code>Value</code> structure
	 */
	public static Value getValueFormat(Object value)
	{
		if(value == null) return null;
		else if(value instanceof String) return new Value( String.valueOf(value) );
		else if(value instanceof DiaryListValue) return new Value( (DiaryListValue)value );
		else if(value instanceof AttachmentValue) return new Value((AttachmentValue)value );
		else if(value instanceof Timestamp) return new Value( ((Timestamp)value) );
		else if(value instanceof Time) return new Value( ((Time)value) );
		else if(value instanceof CurrencyValue) return new Value( ((CurrencyValue)value) );
		else if(value instanceof DateInfo) return new Value( ((DateInfo)value) );
		else if(value instanceof Integer) return new Value( ((Integer)value).intValue() );
		else if(value instanceof Long) return new Value( ((Long)value).longValue() );
		else if(value instanceof Double) return new Value( ((Double)value).doubleValue() );
		else if(value instanceof Float) return new Value( ((Float)value).floatValue() );
		else if(value instanceof Date) return new Value( new Timestamp( ((Date)value).getTime() ) );
		else if(value instanceof Calendar) return new Value( new Timestamp( ((Calendar)value).getTime() ) );
		else return new Value( String.valueOf(value) );
	}

	/**
	 * Get field label. If the proper label is null will return the field id in <code>String</code> format.
	 *
	 * @return attribute field label.
	 */
	public String getLabel()
	{
		if(label != null) return label;
			else return getId();
	}

	/**
	 * Set field label for this attribute instance.
	 *
	 * @param label field label.
	 */
	public void setLabel(String label)
	{
		this.label = label;
	}

	/**
	 * Set attachment value for this attribute considering a file structure (which should exist).
	 *
	 * @param file file structure and is not validated.
	 * @throws AREasyException if file argument is null of doesn't exist in the specified location.
	 */
	public void setAttachment(File file) throws AREasyException
	{
		if(file == null) throw new AREasyException("Null file attachment for '" + getId() + "' field Id.");
		if(!file.exists()) throw new AREasyException("For field Id '" + getId() + "' the attachment doesn't exist in the specified location: " + file.getAbsolutePath());

		try
		{
			AttachmentValue attachInfo = new AttachmentValue(file.getName(), file.getPath());
			setValue(attachInfo);
		}
		catch(IOException ioe)
		{
			throw new AREasyException(ioe);
		}
	}

	public String toString()
	{
		if(getLabel() != null) return getLabel() + " = " + getStringValue();
			else return getId() + " = " + getStringValue();
	}

	/**
	 * Clone the current attribute.
	 * 
	 * @return a new instance of this attribute structure
	 */
	public Attribute copy()
	{
		Attribute attr = new Attribute(id, value);

		attr.changed = changed;
		attr.label = label;

		return attr;
	}

    /**
     * Equal method which takes into account only the attribute's id and value.
     * @param otherAttribute other attribute to compare to
     * @return true if attributes have the same id's and equal values, false otherwise 
     */
    public boolean equalsByValue(Attribute otherAttribute)
	{
		return otherAttribute != null &&
			this.getId().equals(otherAttribute.getId()) &&
			((this.getValue() == null && otherAttribute.getValue() == null) || (this.getValue() != null && this.getValue().equals(otherAttribute.getValue())));
    }
}
