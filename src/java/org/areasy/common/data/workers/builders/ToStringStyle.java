package org.areasy.common.data.workers.builders;

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

import org.areasy.common.data.ClassUtility;
import org.areasy.common.data.ObjectUtility;
import org.areasy.common.data.SystemUtility;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

/**
 * <p>Controls <code>String</code> formatting for {@link ToStringBuilder}.
 * The main public interface is always via <code>ToStringBuilder</code>.</p>
 * <p/>
 * <p>These classes are intended to be used as <code>Singletons</code>.
 * There is no need to instantiate a new style each time. A program
 * will generally use one of the predefined constants on this class.
 * Alternatively, the {@link StandardToStringStyle} class can be used
 * to set the individual settings. Thus most styles can be achieved
 * without subclassing.</p>
 * <p/>
 * <p>If required, a subclass can override as many or as few of the
 * methods as it requires. Each object type (from <code>boolean</code>
 * to <code>long</code> to <code>Object</code> to <code>int[]</code>) has
 * its own methods to output it. Most have two versions, detail and summary.
 * <p/>
 * <p>For example, the detail version of the array based methods will
 * output the whole array, whereas the summary method will just output
 * the array length.</p>
 * <p/>
 * <p>If you want to format the output of certain objects, such as dates, you
 * must create a subclass and override a method.
 * <pre>
 * public class MyStyle extends ToStringStyle {
 *   protected void appendDetail(StringBuffer buffer, String fieldName, Object value) {
 *     if (value instanceof Date) {
 *       value = new SimpleDateFormat("yyyy-MM-dd").format(value);
 *     }
 *     buffer.append(value);
 *   }
 * }
 * </pre>
 * </p>
 *
 * @version $Id: ToStringStyle.java,v 1.2 2008/05/14 09:32:33 swd\stefan.damian Exp $
 */
public abstract class ToStringStyle implements Serializable
{

	/**
	 * The default toString style.
	 */
	public static final ToStringStyle DEFAULT_STYLE = new DefaultToStringStyle();

	/**
	 * The multi line toString style.
	 */
	public static final ToStringStyle MULTI_LINE_STYLE = new MultiLineToStringStyle();

	/**
	 * The no field names toString style.
	 */
	public static final ToStringStyle NO_FIELD_NAMES_STYLE = new NoFieldNameToStringStyle();

	/**
	 * The short prefix toString style.
	 *
	 */
	public static final ToStringStyle SHORT_PREFIX_STYLE = new ShortPrefixToStringStyle();

	/**
	 * The simple toString style.
	 */
	public static final ToStringStyle SIMPLE_STYLE = new SimpleToStringStyle();

	/**
	 * Whether to use the field names, the default is <code>true</code>.
	 */
	private boolean useFieldNames = true;

	/**
	 * Whether to use the class name, the default is <code>true</code>.
	 */
	private boolean useClassName = true;

	/**
	 * Whether to use short class names, the default is <code>false</code>.
	 */
	private boolean useShortClassName = false;

	/**
	 * Whether to use the identity hash code, the default is <code>true</code>.
	 */
	private boolean useIdentityHashCode = true;

	/**
	 * The content start <code>'['</code>.
	 */
	private String contentStart = "[";

	/**
	 * The content end <code>']'</code>.
	 */
	private String contentEnd = "]";

	/**
	 * The field name value separator <code>'='</code>.
	 */
	private String fieldNameValueSeparator = "=";

	/**
	 * Whether the field separator should be added before any other fields.
	 */
	private boolean fieldSeparatorAtStart = false;

	/**
	 * Whether the field separator should be added after any other fields.
	 */
	private boolean fieldSeparatorAtEnd = false;

	/**
	 * The field separator <code>','</code>.
	 */
	private String fieldSeparator = ",";

	/**
	 * The array start <code>'{'</code>.
	 */
	private String arrayStart = "{";

	/**
	 * The array separator <code>','</code>.
	 */
	private String arraySeparator = ",";

	/**
	 * The detail for array content.
	 */
	private boolean arrayContentDetail = true;

	/**
	 * The array end <code>'}'</code>.
	 */
	private String arrayEnd = "}";

	/**
	 * The value to use when fullDetail is <code>null</code>,
	 * the default value is <code>true</code>.
	 */
	private boolean defaultFullDetail = true;

	/**
	 * The <code>null</code> text <code>'&lt;null&gt;'</code>.
	 */
	private String nullText = "<null>";

	/**
	 * The summary size text start <code>'<size'</code>.
	 */
	private String sizeStartText = "<size=";

	/**
	 * The summary size text start <code>'&gt;'</code>.
	 */
	private String sizeEndText = ">";

	/**
	 * The summary object text start <code>'&lt;'</code>.
	 */
	private String summaryObjectStartText = "<";

	/**
	 * The summary object text start <code>'&gt;'</code>.
	 */
	private String summaryObjectEndText = ">";


	/**
	 * <p>Constructor.</p>
	 */
	protected ToStringStyle()
	{
		super();
	}


	/**
	 * <p>Append to the <code>toString</code> the superclass toString.</p>
	 * <p/>
	 * <p>A <code>null</code> <code>superToString</code> is ignored.</p>
	 *
	 * @param buffer        the <code>StringBuffer</code> to populate
	 * @param superToString the <code>super.toString()</code>
	 */
	public void appendSuper(StringBuffer buffer, String superToString)
	{
		appendToString(buffer, superToString);
	}

	/**
	 * <p>Append to the <code>toString</code> another toString.</p>
	 * <p/>
	 * <p>A <code>null</code> <code>toString</code> is ignored.</p>
	 *
	 * @param buffer   the <code>StringBuffer</code> to populate
	 * @param toString the additional <code>toString</code>
	 */
	public void appendToString(StringBuffer buffer, String toString)
	{
		if (toString != null)
		{
			int pos1 = toString.indexOf(contentStart) + contentStart.length();
			int pos2 = toString.lastIndexOf(contentEnd);
			if (pos1 != pos2 && pos1 >= 0 && pos2 >= 0)
			{
				String data = toString.substring(pos1, pos2);
				if (fieldSeparatorAtStart)
				{
					removeLastFieldSeparator(buffer);
				}
				buffer.append(data);
				appendFieldSeparator(buffer);
			}
		}
	}

	/**
	 * <p>Append to the <code>toString</code> the start of data indicator.</p>
	 *
	 * @param buffer the <code>StringBuffer</code> to populate
	 * @param object the <code>Object</code> to build a <code>toString</code> for
	 */
	public void appendStart(StringBuffer buffer, Object object)
	{
		if (object != null)
		{
			appendClassName(buffer, object);
			appendIdentityHashCode(buffer, object);
			appendContentStart(buffer);
			if (fieldSeparatorAtStart)
			{
				appendFieldSeparator(buffer);
			}
		}
	}

	/**
	 * <p>Append to the <code>toString</code> the end of data indicator.</p>
	 *
	 * @param buffer the <code>StringBuffer</code> to populate
	 * @param object the <code>Object</code> to build a
	 *               <code>toString</code> for.
	 */
	public void appendEnd(StringBuffer buffer, Object object)
	{
		if (this.fieldSeparatorAtEnd == false)
		{
			removeLastFieldSeparator(buffer);
		}
		appendContentEnd(buffer);
	}

	/**
	 * <p>Remove the last field separator from the buffer.</p>
	 *
	 * @param buffer the <code>StringBuffer</code> to populate
	 */
	protected void removeLastFieldSeparator(StringBuffer buffer)
	{
		int len = buffer.length();
		int sepLen = fieldSeparator.length();
		if (len > 0 && sepLen > 0 && len >= sepLen)
		{
			boolean match = true;
			for (int i = 0; i < sepLen; i++)
			{
				if (buffer.charAt(len - 1 - i) != fieldSeparator.charAt(sepLen - 1 - i))
				{
					match = false;
					break;
				}
			}
			if (match)
			{
				buffer.setLength(len - sepLen);
			}
		}
	}


	/**
	 * <p>Append to the <code>toString</code> an <code>Object</code>
	 * value, printing the full <code>toString</code> of the
	 * <code>Object</code> passed in.</p>
	 *
	 * @param buffer     the <code>StringBuffer</code> to populate
	 * @param fieldName  the field name
	 * @param value      the value to add to the <code>toString</code>
	 * @param fullDetail <code>true</code> for detail, <code>false</code>
	 *                   for summary info, <code>null</code> for style decides
	 */
	public void append(StringBuffer buffer, String fieldName, Object value, Boolean fullDetail)
	{
		appendFieldStart(buffer, fieldName);

		if (value == null)
		{
			appendNullText(buffer, fieldName);

		}
		else
		{
			appendInternal(buffer, fieldName, value, isFullDetail(fullDetail));
		}

		appendFieldEnd(buffer, fieldName);
	}

	/**
	 * <p>Append to the <code>toString</code> an <code>Object</code>,
	 * correctly interpreting its type.</p>
	 * <p/>
	 * <p>This method performs the main lookup by Class type to correctly
	 * route arrays, <code>Collections</code>, <code>Maps</code> and
	 * <code>Objects</code> to the appropriate method.</p>
	 * <p/>
	 * <p>Either detail or summary views can be specified.</p>
	 * <p/>
	 * <p>If a cycle is detected, an object will be appended with the
	 * <code>Object.toString()</code> format.</p>
	 *
	 * @param buffer    the <code>StringBuffer</code> to populate
	 * @param fieldName the field name, typically not used as already appended
	 * @param value     the value to add to the <code>toString</code>,
	 *                  not <code>null</code>
	 * @param detail    output detail or not
	 */
	protected void appendInternal(StringBuffer buffer, String fieldName, Object value, boolean detail)
	{
		if (ReflectionToStringBuilder.isRegistered(value)
				&& !(value instanceof Number || value instanceof Boolean || value instanceof Character))
		{
			ObjectUtility.appendIdentityToString(buffer, value);

		}
		else if (value instanceof Collection)
		{
			if (detail)
			{
				appendDetail(buffer, fieldName, (Collection) value);
			}
			else
			{
				appendSummarySize(buffer, fieldName, ((Collection) value).size());
			}

		}
		else if (value instanceof Map)
		{
			if (detail)
			{
				appendDetail(buffer, fieldName, (Map) value);
			}
			else
			{
				appendSummarySize(buffer, fieldName, ((Map) value).size());
			}

		}
		else if (value instanceof long[])
		{
			if (detail)
			{
				appendDetail(buffer, fieldName, (long[]) value);
			}
			else
			{
				appendSummary(buffer, fieldName, (long[]) value);
			}

		}
		else if (value instanceof int[])
		{
			if (detail)
			{
				appendDetail(buffer, fieldName, (int[]) value);
			}
			else
			{
				appendSummary(buffer, fieldName, (int[]) value);
			}

		}
		else if (value instanceof short[])
		{
			if (detail)
			{
				appendDetail(buffer, fieldName, (short[]) value);
			}
			else
			{
				appendSummary(buffer, fieldName, (short[]) value);
			}

		}
		else if (value instanceof byte[])
		{
			if (detail)
			{
				appendDetail(buffer, fieldName, (byte[]) value);
			}
			else
			{
				appendSummary(buffer, fieldName, (byte[]) value);
			}

		}
		else if (value instanceof char[])
		{
			if (detail)
			{
				appendDetail(buffer, fieldName, (char[]) value);
			}
			else
			{
				appendSummary(buffer, fieldName, (char[]) value);
			}

		}
		else if (value instanceof double[])
		{
			if (detail)
			{
				appendDetail(buffer, fieldName, (double[]) value);
			}
			else
			{
				appendSummary(buffer, fieldName, (double[]) value);
			}

		}
		else if (value instanceof float[])
		{
			if (detail)
			{
				appendDetail(buffer, fieldName, (float[]) value);
			}
			else
			{
				appendSummary(buffer, fieldName, (float[]) value);
			}

		}
		else if (value instanceof boolean[])
		{
			if (detail)
			{
				appendDetail(buffer, fieldName, (boolean[]) value);
			}
			else
			{
				appendSummary(buffer, fieldName, (boolean[]) value);
			}

		}
		else if (value.getClass().isArray())
		{
			if (detail)
			{
				appendDetail(buffer, fieldName, (Object[]) value);
			}
			else
			{
				appendSummary(buffer, fieldName, (Object[]) value);
			}

		}
		else
		{
			if (detail)
			{
				appendDetail(buffer, fieldName, value);
			}
			else
			{
				appendSummary(buffer, fieldName, value);
			}
		}
	}

	/**
	 * <p>Append to the <code>toString</code> an <code>Object</code>
	 * value, printing the full detail of the <code>Object</code>.</p>
	 *
	 * @param buffer    the <code>StringBuffer</code> to populate
	 * @param fieldName the field name, typically not used as already appended
	 * @param value     the value to add to the <code>toString</code>,
	 *                  not <code>null</code>
	 */
	protected void appendDetail(StringBuffer buffer, String fieldName, Object value)
	{
		buffer.append(value);
	}

	/**
	 * <p>Append to the <code>toString</code> a <code>Collection</code>.</p>
	 *
	 * @param buffer    the <code>StringBuffer</code> to populate
	 * @param fieldName the field name, typically not used as already appended
	 * @param coll      the <code>Collection</code> to add to the
	 *                  <code>toString</code>, not <code>null</code>
	 */
	protected void appendDetail(StringBuffer buffer, String fieldName, Collection coll)
	{
		buffer.append(coll);
	}

	/**
	 * <p>Append to the <code>toString</code> a <code>Map<code>.</p>
	 *
	 * @param buffer    the <code>StringBuffer</code> to populate
	 * @param fieldName the field name, typically not used as already appended
	 * @param map       the <code>Map</code> to add to the <code>toString</code>,
	 *                  not <code>null</code>
	 */
	protected void appendDetail(StringBuffer buffer, String fieldName, Map map)
	{
		buffer.append(map);
	}

	/**
	 * <p>Append to the <code>toString</code> an <code>Object</code>
	 * value, printing a summary of the <code>Object</code>.</P>
	 *
	 * @param buffer    the <code>StringBuffer</code> to populate
	 * @param fieldName the field name, typically not used as already appended
	 * @param value     the value to add to the <code>toString</code>,
	 *                  not <code>null</code>
	 */
	protected void appendSummary(StringBuffer buffer, String fieldName, Object value)
	{
		buffer.append(summaryObjectStartText);
		buffer.append(getShortClassName(value.getClass()));
		buffer.append(summaryObjectEndText);
	}


	/**
	 * <p>Append to the <code>toString</code> a <code>long</code>
	 * value.</p>
	 *
	 * @param buffer    the <code>StringBuffer</code> to populate
	 * @param fieldName the field name
	 * @param value     the value to add to the <code>toString</code>
	 */
	public void append(StringBuffer buffer, String fieldName, long value)
	{
		appendFieldStart(buffer, fieldName);
		appendDetail(buffer, fieldName, value);
		appendFieldEnd(buffer, fieldName);
	}

	/**
	 * <p>Append to the <code>toString</code> a <code>long</code>
	 * value.</p>
	 *
	 * @param buffer    the <code>StringBuffer</code> to populate
	 * @param fieldName the field name, typically not used as already appended
	 * @param value     the value to add to the <code>toString</code>
	 */
	protected void appendDetail(StringBuffer buffer, String fieldName, long value)
	{
		buffer.append(value);
	}


	/**
	 * <p>Append to the <code>toString</code> an <code>int</code>
	 * value.</p>
	 *
	 * @param buffer    the <code>StringBuffer</code> to populate
	 * @param fieldName the field name
	 * @param value     the value to add to the <code>toString</code>
	 */
	public void append(StringBuffer buffer, String fieldName, int value)
	{
		appendFieldStart(buffer, fieldName);
		appendDetail(buffer, fieldName, value);
		appendFieldEnd(buffer, fieldName);
	}

	/**
	 * <p>Append to the <code>toString</code> an <code>int</code>
	 * value.</p>
	 *
	 * @param buffer    the <code>StringBuffer</code> to populate
	 * @param fieldName the field name, typically not used as already appended
	 * @param value     the value to add to the <code>toString</code>
	 */
	protected void appendDetail(StringBuffer buffer, String fieldName, int value)
	{
		buffer.append(value);
	}


	/**
	 * <p>Append to the <code>toString</code> a <code>short</code>
	 * value.</p>
	 *
	 * @param buffer    the <code>StringBuffer</code> to populate
	 * @param fieldName the field name
	 * @param value     the value to add to the <code>toString</code>
	 */
	public void append(StringBuffer buffer, String fieldName, short value)
	{
		appendFieldStart(buffer, fieldName);
		appendDetail(buffer, fieldName, value);
		appendFieldEnd(buffer, fieldName);
	}

	/**
	 * <p>Append to the <code>toString</code> a <code>short</code>
	 * value.</p>
	 *
	 * @param buffer    the <code>StringBuffer</code> to populate
	 * @param fieldName the field name, typically not used as already appended
	 * @param value     the value to add to the <code>toString</code>
	 */
	protected void appendDetail(StringBuffer buffer, String fieldName, short value)
	{
		buffer.append(value);
	}


	/**
	 * <p>Append to the <code>toString</code> a <code>byte</code>
	 * value.</p>
	 *
	 * @param buffer    the <code>StringBuffer</code> to populate
	 * @param fieldName the field name
	 * @param value     the value to add to the <code>toString</code>
	 */
	public void append(StringBuffer buffer, String fieldName, byte value)
	{
		appendFieldStart(buffer, fieldName);
		appendDetail(buffer, fieldName, value);
		appendFieldEnd(buffer, fieldName);
	}

	/**
	 * <p>Append to the <code>toString</code> a <code>byte</code>
	 * value.</p>
	 *
	 * @param buffer    the <code>StringBuffer</code> to populate
	 * @param fieldName the field name, typically not used as already appended
	 * @param value     the value to add to the <code>toString</code>
	 */
	protected void appendDetail(StringBuffer buffer, String fieldName, byte value)
	{
		buffer.append(value);
	}


	/**
	 * <p>Append to the <code>toString</code> a <code>char</code>
	 * value.</p>
	 *
	 * @param buffer    the <code>StringBuffer</code> to populate
	 * @param fieldName the field name
	 * @param value     the value to add to the <code>toString</code>
	 */
	public void append(StringBuffer buffer, String fieldName, char value)
	{
		appendFieldStart(buffer, fieldName);
		appendDetail(buffer, fieldName, value);
		appendFieldEnd(buffer, fieldName);
	}

	/**
	 * <p>Append to the <code>toString</code> a <code>char</code>
	 * value.</p>
	 *
	 * @param buffer    the <code>StringBuffer</code> to populate
	 * @param fieldName the field name, typically not used as already appended
	 * @param value     the value to add to the <code>toString</code>
	 */
	protected void appendDetail(StringBuffer buffer, String fieldName, char value)
	{
		buffer.append(value);
	}


	/**
	 * <p>Append to the <code>toString</code> a <code>double</code>
	 * value.</p>
	 *
	 * @param buffer    the <code>StringBuffer</code> to populate
	 * @param fieldName the field name
	 * @param value     the value to add to the <code>toString</code>
	 */
	public void append(StringBuffer buffer, String fieldName, double value)
	{
		appendFieldStart(buffer, fieldName);
		appendDetail(buffer, fieldName, value);
		appendFieldEnd(buffer, fieldName);
	}

	/**
	 * <p>Append to the <code>toString</code> a <code>double</code>
	 * value.</p>
	 *
	 * @param buffer    the <code>StringBuffer</code> to populate
	 * @param fieldName the field name, typically not used as already appended
	 * @param value     the value to add to the <code>toString</code>
	 */
	protected void appendDetail(StringBuffer buffer, String fieldName, double value)
	{
		buffer.append(value);
	}


	/**
	 * <p>Append to the <code>toString</code> a <code>float</code>
	 * value.</p>
	 *
	 * @param buffer    the <code>StringBuffer</code> to populate
	 * @param fieldName the field name
	 * @param value     the value to add to the <code>toString</code>
	 */
	public void append(StringBuffer buffer, String fieldName, float value)
	{
		appendFieldStart(buffer, fieldName);
		appendDetail(buffer, fieldName, value);
		appendFieldEnd(buffer, fieldName);
	}

	/**
	 * <p>Append to the <code>toString</code> a <code>float</code>
	 * value.</p>
	 *
	 * @param buffer    the <code>StringBuffer</code> to populate
	 * @param fieldName the field name, typically not used as already appended
	 * @param value     the value to add to the <code>toString</code>
	 */
	protected void appendDetail(StringBuffer buffer, String fieldName, float value)
	{
		buffer.append(value);
	}


	/**
	 * <p>Append to the <code>toString</code> a <code>boolean</code>
	 * value.</p>
	 *
	 * @param buffer    the <code>StringBuffer</code> to populate
	 * @param fieldName the field name
	 * @param value     the value to add to the <code>toString</code>
	 */
	public void append(StringBuffer buffer, String fieldName, boolean value)
	{
		appendFieldStart(buffer, fieldName);
		appendDetail(buffer, fieldName, value);
		appendFieldEnd(buffer, fieldName);
	}

	/**
	 * <p>Append to the <code>toString</code> a <code>boolean</code>
	 * value.</p>
	 *
	 * @param buffer    the <code>StringBuffer</code> to populate
	 * @param fieldName the field name, typically not used as already appended
	 * @param value     the value to add to the <code>toString</code>
	 */
	protected void appendDetail(StringBuffer buffer, String fieldName, boolean value)
	{
		buffer.append(value);
	}

	/**
	 * <p>Append to the <code>toString</code> an <code>Object</code>
	 * array.</p>
	 *
	 * @param buffer     the <code>StringBuffer</code> to populate
	 * @param fieldName  the field name
	 * @param array      the array to add to the toString
	 * @param fullDetail <code>true</code> for detail, <code>false</code>
	 *                   for summary info, <code>null</code> for style decides
	 */
	public void append(StringBuffer buffer, String fieldName, Object[] array, Boolean fullDetail)
	{
		appendFieldStart(buffer, fieldName);

		if (array == null)
		{
			appendNullText(buffer, fieldName);

		}
		else if (isFullDetail(fullDetail))
		{
			appendDetail(buffer, fieldName, array);

		}
		else
		{
			appendSummary(buffer, fieldName, array);
		}

		appendFieldEnd(buffer, fieldName);
	}


	/**
	 * <p>Append to the <code>toString</code> the detail of an
	 * <code>Object</code> array.</p>
	 *
	 * @param buffer    the <code>StringBuffer</code> to populate
	 * @param fieldName the field name, typically not used as already appended
	 * @param array     the array to add to the <code>toString</code>,
	 *                  not <code>null</code>
	 */
	protected void appendDetail(StringBuffer buffer, String fieldName, Object[] array)
	{
		buffer.append(arrayStart);
		for (int i = 0; i < array.length; i++)
		{
			Object item = array[i];
			if (i > 0)
			{
				buffer.append(arraySeparator);
			}
			if (item == null)
			{
				appendNullText(buffer, fieldName);

			}
			else
			{
				appendInternal(buffer, fieldName, item, arrayContentDetail);
			}
		}
		buffer.append(arrayEnd);
	}

	/**
	 * <p>Append to the <code>toString</code> the detail of an array type.</p>
	 *
	 * @param buffer    the <code>StringBuffer</code> to populate
	 * @param fieldName the field name, typically not used as already appended
	 * @param array     the array to add to the <code>toString</code>,
	 *                  not <code>null</code>
	 */
	protected void reflectionAppendArrayDetail(StringBuffer buffer, String fieldName, Object array)
	{
		buffer.append(arrayStart);
		int length = Array.getLength(array);
		for (int i = 0; i < length; i++)
		{
			Object item = Array.get(array, i);
			if (i > 0)
			{
				buffer.append(arraySeparator);
			}
			if (item == null)
			{
				appendNullText(buffer, fieldName);

			}
			else
			{
				appendInternal(buffer, fieldName, item, arrayContentDetail);
			}
		}
		buffer.append(arrayEnd);
	}

	/**
	 * <p>Append to the <code>toString</code> a summary of an
	 * <code>Object</code> array.</p>
	 *
	 * @param buffer    the <code>StringBuffer</code> to populate
	 * @param fieldName the field name, typically not used as already appended
	 * @param array     the array to add to the <code>toString</code>,
	 *                  not <code>null</code>
	 */
	protected void appendSummary(StringBuffer buffer, String fieldName, Object[] array)
	{
		appendSummarySize(buffer, fieldName, array.length);
	}


	/**
	 * <p>Append to the <code>toString</code> a <code>long</code>
	 * array.</p>
	 *
	 * @param buffer     the <code>StringBuffer</code> to populate
	 * @param fieldName  the field name
	 * @param array      the array to add to the <code>toString</code>
	 * @param fullDetail <code>true</code> for detail, <code>false</code>
	 *                   for summary info, <code>null</code> for style decides
	 */
	public void append(StringBuffer buffer, String fieldName, long[] array, Boolean fullDetail)
	{
		appendFieldStart(buffer, fieldName);

		if (array == null)
		{
			appendNullText(buffer, fieldName);

		}
		else if (isFullDetail(fullDetail))
		{
			appendDetail(buffer, fieldName, array);

		}
		else
		{
			appendSummary(buffer, fieldName, array);
		}

		appendFieldEnd(buffer, fieldName);
	}

	/**
	 * <p>Append to the <code>toString</code> the detail of a
	 * <code>long</code> array.</p>
	 *
	 * @param buffer    the <code>StringBuffer</code> to populate
	 * @param fieldName the field name, typically not used as already appended
	 * @param array     the array to add to the <code>toString</code>,
	 *                  not <code>null</code>
	 */
	protected void appendDetail(StringBuffer buffer, String fieldName, long[] array)
	{
		buffer.append(arrayStart);
		for (int i = 0; i < array.length; i++)
		{
			if (i > 0)
			{
				buffer.append(arraySeparator);
			}
			appendDetail(buffer, fieldName, array[i]);
		}
		buffer.append(arrayEnd);
	}

	/**
	 * <p>Append to the <code>toString</code> a summary of a
	 * <code>long</code> array.</p>
	 *
	 * @param buffer    the <code>StringBuffer</code> to populate
	 * @param fieldName the field name, typically not used as already appended
	 * @param array     the array to add to the <code>toString</code>,
	 *                  not <code>null</code>
	 */
	protected void appendSummary(StringBuffer buffer, String fieldName, long[] array)
	{
		appendSummarySize(buffer, fieldName, array.length);
	}


	/**
	 * <p>Append to the <code>toString</code> an <code>int</code>
	 * array.</p>
	 *
	 * @param buffer     the <code>StringBuffer</code> to populate
	 * @param fieldName  the field name
	 * @param array      the array to add to the <code>toString</code>
	 * @param fullDetail <code>true</code> for detail, <code>false</code>
	 *                   for summary info, <code>null</code> for style decides
	 */
	public void append(StringBuffer buffer, String fieldName, int[] array, Boolean fullDetail)
	{
		appendFieldStart(buffer, fieldName);

		if (array == null)
		{
			appendNullText(buffer, fieldName);

		}
		else if (isFullDetail(fullDetail))
		{
			appendDetail(buffer, fieldName, array);

		}
		else
		{
			appendSummary(buffer, fieldName, array);
		}

		appendFieldEnd(buffer, fieldName);
	}

	/**
	 * <p>Append to the <code>toString</code> the detail of an
	 * <code>int</code> array.</p>
	 *
	 * @param buffer    the <code>StringBuffer</code> to populate
	 * @param fieldName the field name, typically not used as already appended
	 * @param array     the array to add to the <code>toString</code>,
	 *                  not <code>null</code>
	 */
	protected void appendDetail(StringBuffer buffer, String fieldName, int[] array)
	{
		buffer.append(arrayStart);
		for (int i = 0; i < array.length; i++)
		{
			if (i > 0)
			{
				buffer.append(arraySeparator);
			}
			appendDetail(buffer, fieldName, array[i]);
		}
		buffer.append(arrayEnd);
	}

	/**
	 * <p>Append to the <code>toString</code> a summary of an
	 * <code>int</code> array.</p>
	 *
	 * @param buffer    the <code>StringBuffer</code> to populate
	 * @param fieldName the field name, typically not used as already appended
	 * @param array     the array to add to the <code>toString</code>,
	 *                  not <code>null</code>
	 */
	protected void appendSummary(StringBuffer buffer, String fieldName, int[] array)
	{
		appendSummarySize(buffer, fieldName, array.length);
	}


	/**
	 * <p>Append to the <code>toString</code> a <code>short</code>
	 * array.</p>
	 *
	 * @param buffer     the <code>StringBuffer</code> to populate
	 * @param fieldName  the field name
	 * @param array      the array to add to the <code>toString</code>
	 * @param fullDetail <code>true</code> for detail, <code>false</code>
	 *                   for summary info, <code>null</code> for style decides
	 */
	public void append(StringBuffer buffer, String fieldName, short[] array, Boolean fullDetail)
	{
		appendFieldStart(buffer, fieldName);

		if (array == null)
		{
			appendNullText(buffer, fieldName);

		}
		else if (isFullDetail(fullDetail))
		{
			appendDetail(buffer, fieldName, array);

		}
		else
		{
			appendSummary(buffer, fieldName, array);
		}

		appendFieldEnd(buffer, fieldName);
	}

	/**
	 * <p>Append to the <code>toString</code> the detail of a
	 * <code>short</code> array.</p>
	 *
	 * @param buffer    the <code>StringBuffer</code> to populate
	 * @param fieldName the field name, typically not used as already appended
	 * @param array     the array to add to the <code>toString</code>,
	 *                  not <code>null</code>
	 */
	protected void appendDetail(StringBuffer buffer, String fieldName, short[] array)
	{
		buffer.append(arrayStart);
		for (int i = 0; i < array.length; i++)
		{
			if (i > 0)
			{
				buffer.append(arraySeparator);
			}
			appendDetail(buffer, fieldName, array[i]);
		}
		buffer.append(arrayEnd);
	}

	/**
	 * <p>Append to the <code>toString</code> a summary of a
	 * <code>short</code> array.</p>
	 *
	 * @param buffer    the <code>StringBuffer</code> to populate
	 * @param fieldName the field name, typically not used as already appended
	 * @param array     the array to add to the <code>toString</code>,
	 *                  not <code>null</code>
	 */
	protected void appendSummary(StringBuffer buffer, String fieldName, short[] array)
	{
		appendSummarySize(buffer, fieldName, array.length);
	}


	/**
	 * <p>Append to the <code>toString</code> a <code>byte</code>
	 * array.</p>
	 *
	 * @param buffer     the <code>StringBuffer</code> to populate
	 * @param fieldName  the field name
	 * @param array      the array to add to the <code>toString</code>
	 * @param fullDetail <code>true</code> for detail, <code>false</code>
	 *                   for summary info, <code>null</code> for style decides
	 */
	public void append(StringBuffer buffer, String fieldName, byte[] array, Boolean fullDetail)
	{
		appendFieldStart(buffer, fieldName);

		if (array == null)
		{
			appendNullText(buffer, fieldName);

		}
		else if (isFullDetail(fullDetail))
		{
			appendDetail(buffer, fieldName, array);

		}
		else
		{
			appendSummary(buffer, fieldName, array);
		}

		appendFieldEnd(buffer, fieldName);
	}

	/**
	 * <p>Append to the <code>toString</code> the detail of a
	 * <code>byte</code> array.</p>
	 *
	 * @param buffer    the <code>StringBuffer</code> to populate
	 * @param fieldName the field name, typically not used as already appended
	 * @param array     the array to add to the <code>toString</code>,
	 *                  not <code>null</code>
	 */
	protected void appendDetail(StringBuffer buffer, String fieldName, byte[] array)
	{
		buffer.append(arrayStart);
		for (int i = 0; i < array.length; i++)
		{
			if (i > 0)
			{
				buffer.append(arraySeparator);
			}
			appendDetail(buffer, fieldName, array[i]);
		}
		buffer.append(arrayEnd);
	}

	/**
	 * <p>Append to the <code>toString</code> a summary of a
	 * <code>byte</code> array.</p>
	 *
	 * @param buffer    the <code>StringBuffer</code> to populate
	 * @param fieldName the field name, typically not used as already appended
	 * @param array     the array to add to the <code>toString</code>,
	 *                  not <code>null</code>
	 */
	protected void appendSummary(StringBuffer buffer, String fieldName, byte[] array)
	{
		appendSummarySize(buffer, fieldName, array.length);
	}


	/**
	 * <p>Append to the <code>toString</code> a <code>char</code>
	 * array.</p>
	 *
	 * @param buffer     the <code>StringBuffer</code> to populate
	 * @param fieldName  the field name
	 * @param array      the array to add to the <code>toString</code>
	 * @param fullDetail <code>true</code> for detail, <code>false</code>
	 *                   for summary info, <code>null</code> for style decides
	 */
	public void append(StringBuffer buffer, String fieldName, char[] array, Boolean fullDetail)
	{
		appendFieldStart(buffer, fieldName);

		if (array == null)
		{
			appendNullText(buffer, fieldName);

		}
		else if (isFullDetail(fullDetail))
		{
			appendDetail(buffer, fieldName, array);

		}
		else
		{
			appendSummary(buffer, fieldName, array);
		}

		appendFieldEnd(buffer, fieldName);
	}

	/**
	 * <p>Append to the <code>toString</code> the detail of a
	 * <code>char</code> array.</p>
	 *
	 * @param buffer    the <code>StringBuffer</code> to populate
	 * @param fieldName the field name, typically not used as already appended
	 * @param array     the array to add to the <code>toString</code>,
	 *                  not <code>null</code>
	 */
	protected void appendDetail(StringBuffer buffer, String fieldName, char[] array)
	{
		buffer.append(arrayStart);
		for (int i = 0; i < array.length; i++)
		{
			if (i > 0)
			{
				buffer.append(arraySeparator);
			}
			appendDetail(buffer, fieldName, array[i]);
		}
		buffer.append(arrayEnd);
	}

	/**
	 * <p>Append to the <code>toString</code> a summary of a
	 * <code>char</code> array.</p>
	 *
	 * @param buffer    the <code>StringBuffer</code> to populate
	 * @param fieldName the field name, typically not used as already appended
	 * @param array     the array to add to the <code>toString</code>,
	 *                  not <code>null</code>
	 */
	protected void appendSummary(StringBuffer buffer, String fieldName, char[] array)
	{
		appendSummarySize(buffer, fieldName, array.length);
	}


	/**
	 * <p>Append to the <code>toString</code> a <code>double</code>
	 * array.</p>
	 *
	 * @param buffer     the <code>StringBuffer</code> to populate
	 * @param fieldName  the field name
	 * @param array      the array to add to the toString
	 * @param fullDetail <code>true</code> for detail, <code>false</code>
	 *                   for summary info, <code>null</code> for style decides
	 */
	public void append(StringBuffer buffer, String fieldName, double[] array, Boolean fullDetail)
	{
		appendFieldStart(buffer, fieldName);

		if (array == null)
		{
			appendNullText(buffer, fieldName);

		}
		else if (isFullDetail(fullDetail))
		{
			appendDetail(buffer, fieldName, array);

		}
		else
		{
			appendSummary(buffer, fieldName, array);
		}

		appendFieldEnd(buffer, fieldName);
	}

	/**
	 * <p>Append to the <code>toString</code> the detail of a
	 * <code>double</code> array.</p>
	 *
	 * @param buffer    the <code>StringBuffer</code> to populate
	 * @param fieldName the field name, typically not used as already appended
	 * @param array     the array to add to the <code>toString</code>,
	 *                  not <code>null</code>
	 */
	protected void appendDetail(StringBuffer buffer, String fieldName, double[] array)
	{
		buffer.append(arrayStart);
		for (int i = 0; i < array.length; i++)
		{
			if (i > 0)
			{
				buffer.append(arraySeparator);
			}
			appendDetail(buffer, fieldName, array[i]);
		}
		buffer.append(arrayEnd);
	}

	/**
	 * <p>Append to the <code>toString</code> a summary of a
	 * <code>double</code> array.</p>
	 *
	 * @param buffer    the <code>StringBuffer</code> to populate
	 * @param fieldName the field name, typically not used as already appended
	 * @param array     the array to add to the <code>toString</code>,
	 *                  not <code>null</code>
	 */
	protected void appendSummary(StringBuffer buffer, String fieldName, double[] array)
	{
		appendSummarySize(buffer, fieldName, array.length);
	}


	/**
	 * <p>Append to the <code>toString</code> a <code>float</code>
	 * array.</p>
	 *
	 * @param buffer     the <code>StringBuffer</code> to populate
	 * @param fieldName  the field name
	 * @param array      the array to add to the toString
	 * @param fullDetail <code>true</code> for detail, <code>false</code>
	 *                   for summary info, <code>null</code> for style decides
	 */
	public void append(StringBuffer buffer, String fieldName, float[] array, Boolean fullDetail)
	{
		appendFieldStart(buffer, fieldName);

		if (array == null)
		{
			appendNullText(buffer, fieldName);

		}
		else if (isFullDetail(fullDetail))
		{
			appendDetail(buffer, fieldName, array);

		}
		else
		{
			appendSummary(buffer, fieldName, array);
		}

		appendFieldEnd(buffer, fieldName);
	}

	/**
	 * <p>Append to the <code>toString</code> the detail of a
	 * <code>float</code> array.</p>
	 *
	 * @param buffer    the <code>StringBuffer</code> to populate
	 * @param fieldName the field name, typically not used as already appended
	 * @param array     the array to add to the <code>toString</code>,
	 *                  not <code>null</code>
	 */
	protected void appendDetail(StringBuffer buffer, String fieldName, float[] array)
	{
		buffer.append(arrayStart);
		for (int i = 0; i < array.length; i++)
		{
			if (i > 0)
			{
				buffer.append(arraySeparator);
			}
			appendDetail(buffer, fieldName, array[i]);
		}
		buffer.append(arrayEnd);
	}

	/**
	 * <p>Append to the <code>toString</code> a summary of a
	 * <code>float</code> array.</p>
	 *
	 * @param buffer    the <code>StringBuffer</code> to populate
	 * @param fieldName the field name, typically not used as already appended
	 * @param array     the array to add to the <code>toString</code>,
	 *                  not <code>null</code>
	 */
	protected void appendSummary(StringBuffer buffer, String fieldName, float[] array)
	{
		appendSummarySize(buffer, fieldName, array.length);
	}


	/**
	 * <p>Append to the <code>toString</code> a <code>boolean</code>
	 * array.</p>
	 *
	 * @param buffer     the <code>StringBuffer</code> to populate
	 * @param fieldName  the field name
	 * @param array      the array to add to the toString
	 * @param fullDetail <code>true</code> for detail, <code>false</code>
	 *                   for summary info, <code>null</code> for style decides
	 */
	public void append(StringBuffer buffer, String fieldName, boolean[] array, Boolean fullDetail)
	{
		appendFieldStart(buffer, fieldName);

		if (array == null)
		{
			appendNullText(buffer, fieldName);

		}
		else if (isFullDetail(fullDetail))
		{
			appendDetail(buffer, fieldName, array);

		}
		else
		{
			appendSummary(buffer, fieldName, array);
		}

		appendFieldEnd(buffer, fieldName);
	}

	/**
	 * <p>Append to the <code>toString</code> the detail of a
	 * <code>boolean</code> array.</p>
	 *
	 * @param buffer    the <code>StringBuffer</code> to populate
	 * @param fieldName the field name, typically not used as already appended
	 * @param array     the array to add to the <code>toString</code>,
	 *                  not <code>null</code>
	 */
	protected void appendDetail(StringBuffer buffer, String fieldName, boolean[] array)
	{
		buffer.append(arrayStart);
		for (int i = 0; i < array.length; i++)
		{
			if (i > 0)
			{
				buffer.append(arraySeparator);
			}
			appendDetail(buffer, fieldName, array[i]);
		}
		buffer.append(arrayEnd);
	}

	/**
	 * <p>Append to the <code>toString</code> a summary of a
	 * <code>boolean</code> array.</p>
	 *
	 * @param buffer    the <code>StringBuffer</code> to populate
	 * @param fieldName the field name, typically not used as already appended
	 * @param array     the array to add to the <code>toString</code>,
	 *                  not <code>null</code>
	 */
	protected void appendSummary(StringBuffer buffer, String fieldName, boolean[] array)
	{
		appendSummarySize(buffer, fieldName, array.length);
	}


	/**
	 * <p>Append to the <code>toString</code> the class name.</p>
	 *
	 * @param buffer the <code>StringBuffer</code> to populate
	 * @param object the <code>Object</code> whose name to output
	 */
	protected void appendClassName(StringBuffer buffer, Object object)
	{
		if (useClassName && object != null)
		{
			if (useShortClassName)
			{
				buffer.append(getShortClassName(object.getClass()));
			}
			else
			{
				buffer.append(object.getClass().getName());
			}
		}
	}

	/**
	 * <p>Append the {@link System#identityHashCode(java.lang.Object)}.</p>
	 *
	 * @param buffer the <code>StringBuffer</code> to populate
	 * @param object the <code>Object</code> whose id to output
	 */
	protected void appendIdentityHashCode(StringBuffer buffer, Object object)
	{
		if (this.isUseIdentityHashCode() && object != null)
		{
			buffer.append('@');
			buffer.append(Integer.toHexString(System.identityHashCode(object)));
		}
	}

	/**
	 * <p>Append to the <code>toString</code> the content start.</p>
	 *
	 * @param buffer the <code>StringBuffer</code> to populate
	 */
	protected void appendContentStart(StringBuffer buffer)
	{
		buffer.append(contentStart);
	}

	/**
	 * <p>Append to the <code>toString</code> the content end.</p>
	 *
	 * @param buffer the <code>StringBuffer</code> to populate
	 */
	protected void appendContentEnd(StringBuffer buffer)
	{
		buffer.append(contentEnd);
	}

	/**
	 * <p>Append to the <code>toString</code> an indicator for <code>null</code>.</p>
	 * <p/>
	 * <p>The default indicator is <code>'&lt;null&gt;'</code>.</p>
	 *
	 * @param buffer    the <code>StringBuffer</code> to populate
	 * @param fieldName the field name, typically not used as already appended
	 */
	protected void appendNullText(StringBuffer buffer, String fieldName)
	{
		buffer.append(nullText);
	}

	/**
	 * <p>Append to the <code>toString</code> the field separator.</p>
	 *
	 * @param buffer the <code>StringBuffer</code> to populate
	 */
	protected void appendFieldSeparator(StringBuffer buffer)
	{
		buffer.append(fieldSeparator);
	}

	/**
	 * <p>Append to the <code>toString</code> the field start.</p>
	 *
	 * @param buffer    the <code>StringBuffer</code> to populate
	 * @param fieldName the field name
	 */
	protected void appendFieldStart(StringBuffer buffer, String fieldName)
	{
		if (useFieldNames && fieldName != null)
		{
			buffer.append(fieldName);
			buffer.append(fieldNameValueSeparator);
		}
	}

	/**
	 * <p>Append to the <code>toString<code> the field end.</p>
	 *
	 * @param buffer    the <code>StringBuffer</code> to populate
	 * @param fieldName the field name, typically not used as already appended
	 */
	protected void appendFieldEnd(StringBuffer buffer, String fieldName)
	{
		appendFieldSeparator(buffer);
	}

	/**
	 * <p>Append to the <code>toString</code> a size summary.</p>
	 * <p/>
	 * <p>The size summary is used to summarize the contents of
	 * <code>Collections</code>, <code>Maps</code> and arrays.</p>
	 * <p/>
	 * <p>The output consists of a prefix, the passed in size
	 * and a suffix.</p>
	 * <p/>
	 * <p>The default format is <code>'&lt;size=n&gt;'<code>.</p>
	 *
	 * @param buffer    the <code>StringBuffer</code> to populate
	 * @param fieldName the field name, typically not used as already appended
	 * @param size      the size to append
	 */
	protected void appendSummarySize(StringBuffer buffer, String fieldName, int size)
	{
		buffer.append(sizeStartText);
		buffer.append(size);
		buffer.append(sizeEndText);
	}

	/**
	 * <p>Is this field to be output in full detail.</p>
	 * <p/>
	 * <p>This method converts a detail request into a detail level.
	 * The calling code may request full detail (<code>true</code>),
	 * but a subclass might ignore that and always return
	 * <code>false</code>. The calling code may pass in
	 * <code>null</code> indicating that it doesn't care about
	 * the detail level. In this case the default detail level is
	 * used.</p>
	 *
	 * @param fullDetailRequest the detail level requested
	 * @return whether full detail is to be shown
	 */
	protected boolean isFullDetail(Boolean fullDetailRequest)
	{
		if (fullDetailRequest == null)
		{
			return defaultFullDetail;
		}
		return fullDetailRequest.booleanValue();
	}

	/**
	 * <p>Gets the short class name for a class.</p>
	 * <p/>
	 * <p>The short class name is the classname excluding
	 * the package name.</p>
	 *
	 * @param cls the <code>Class</code> to get the short name of
	 * @return the short name
	 */
	protected String getShortClassName(Class cls)
	{
		return ClassUtility.getShortClassName(cls);
	}

	// Setters and getters for the customizable parts of the style
	// These methods are not expected to be overridden, except to make public
	// (They are not public so that immutable subclasses can be written)

	/**
	 * <p>Gets whether to use the class name.</p>
	 *
	 * @return the current useClassName flag
	 */
	protected boolean isUseClassName()
	{
		return useClassName;
	}

	/**
	 * <p>Sets whether to use the class name.</p>
	 *
	 * @param useClassName the new useClassName flag
	 */
	protected void setUseClassName(boolean useClassName)
	{
		this.useClassName = useClassName;
	}


	/**
	 * <p>Gets whether to output short or long class names.</p>
	 *
	 * @return the current useShortClassName flag
	 */
	protected boolean isUseShortClassName()
	{
		return useShortClassName;
	}

	/**
	 * <p>Sets whether to output short or long class names.</p>
	 *
	 * @param useShortClassName the new useShortClassName flag
	 */
	protected void setUseShortClassName(boolean useShortClassName)
	{
		this.useShortClassName = useShortClassName;
	}

	/**
	 * <p>Gets whether to use the identity hash code.</p>
	 *
	 * @return the current useIdentityHashCode flag
	 */
	protected boolean isUseIdentityHashCode()
	{
		return useIdentityHashCode;
	}

	/**
	 * <p>Sets whether to use the identity hash code.</p>
	 *
	 * @param useIdentityHashCode the new useIdentityHashCode flag
	 */
	protected void setUseIdentityHashCode(boolean useIdentityHashCode)
	{
		this.useIdentityHashCode = useIdentityHashCode;
	}


	/**
	 * <p>Gets whether to use the field names passed in.</p>
	 *
	 * @return the current useFieldNames flag
	 */
	protected boolean isUseFieldNames()
	{
		return useFieldNames;
	}

	/**
	 * <p>Sets whether to use the field names passed in.</p>
	 *
	 * @param useFieldNames the new useFieldNames flag
	 */
	protected void setUseFieldNames(boolean useFieldNames)
	{
		this.useFieldNames = useFieldNames;
	}


	/**
	 * <p>Gets whether to use full detail when the caller doesn't
	 * specify.</p>
	 *
	 * @return the current defaultFullDetail flag
	 */
	protected boolean isDefaultFullDetail()
	{
		return defaultFullDetail;
	}

	/**
	 * <p>Sets whether to use full detail when the caller doesn't
	 * specify.</p>
	 *
	 * @param defaultFullDetail the new defaultFullDetail flag
	 */
	protected void setDefaultFullDetail(boolean defaultFullDetail)
	{
		this.defaultFullDetail = defaultFullDetail;
	}


	/**
	 * <p>Gets whether to output array content detail.</p>
	 *
	 * @return the current array content detail setting
	 */
	protected boolean isArrayContentDetail()
	{
		return arrayContentDetail;
	}

	/**
	 * <p>Sets whether to output array content detail.</p>
	 *
	 * @param arrayContentDetail the new arrayContentDetail flag
	 */
	protected void setArrayContentDetail(boolean arrayContentDetail)
	{
		this.arrayContentDetail = arrayContentDetail;
	}


	/**
	 * <p>Gets the array start text.</p>
	 *
	 * @return the current array start text
	 */
	protected String getArrayStart()
	{
		return arrayStart;
	}

	/**
	 * <p>Sets the array start text.</p>
	 * <p/>
	 * <p><code>null</code> is accepted, but will be converted to
	 * an empty String.</p>
	 *
	 * @param arrayStart the new array start text
	 */
	protected void setArrayStart(String arrayStart)
	{
		if (arrayStart == null)
		{
			arrayStart = "";
		}
		this.arrayStart = arrayStart;
	}


	/**
	 * <p>Gets the array end text.</p>
	 *
	 * @return the current array end text
	 */
	protected String getArrayEnd()
	{
		return arrayEnd;
	}

	/**
	 * <p>Sets the array end text.</p>
	 * <p/>
	 * <p><code>null</code> is accepted, but will be converted to
	 * an empty String.</p>
	 *
	 * @param arrayEnd the new array end text
	 */
	protected void setArrayEnd(String arrayEnd)
	{
		if (arrayEnd == null)
		{
			arrayEnd = "";
		}
		this.arrayEnd = arrayEnd;
	}


	/**
	 * <p>Gets the array separator text.</p>
	 *
	 * @return the current array separator text
	 */
	protected String getArraySeparator()
	{
		return arraySeparator;
	}

	/**
	 * <p>Sets the array separator text.</p>
	 * <p/>
	 * <p><code>null</code> is accepted, but will be converted to
	 * an empty String.</p>
	 *
	 * @param arraySeparator the new array separator text
	 */
	protected void setArraySeparator(String arraySeparator)
	{
		if (arraySeparator == null)
		{
			arraySeparator = "";
		}
		this.arraySeparator = arraySeparator;
	}


	/**
	 * <p>Gets the content start text.</p>
	 *
	 * @return the current content start text
	 */
	protected String getContentStart()
	{
		return contentStart;
	}

	/**
	 * <p>Sets the content start text.</p>
	 * <p/>
	 * <p><code>null</code> is accepted, but will be converted to
	 * an empty String.</p>
	 *
	 * @param contentStart the new content start text
	 */
	protected void setContentStart(String contentStart)
	{
		if (contentStart == null)
		{
			contentStart = "";
		}
		this.contentStart = contentStart;
	}


	/**
	 * <p>Gets the content end text.</p>
	 *
	 * @return the current content end text
	 */
	protected String getContentEnd()
	{
		return contentEnd;
	}

	/**
	 * <p>Sets the content end text.</p>
	 * <p/>
	 * <p><code>null</code> is accepted, but will be converted to
	 * an empty String.</p>
	 *
	 * @param contentEnd the new content end text
	 */
	protected void setContentEnd(String contentEnd)
	{
		if (contentEnd == null)
		{
			contentEnd = "";
		}
		this.contentEnd = contentEnd;
	}


	/**
	 * <p>Gets the field name value separator text.</p>
	 *
	 * @return the current field name value separator text
	 */
	protected String getFieldNameValueSeparator()
	{
		return fieldNameValueSeparator;
	}

	/**
	 * <p>Sets the field name value separator text.</p>
	 * <p/>
	 * <p><code>null</code> is accepted, but will be converted to
	 * an empty String.</p>
	 *
	 * @param fieldNameValueSeparator the new field name value separator text
	 */
	protected void setFieldNameValueSeparator(String fieldNameValueSeparator)
	{
		if (fieldNameValueSeparator == null)
		{
			fieldNameValueSeparator = "";
		}
		this.fieldNameValueSeparator = fieldNameValueSeparator;
	}


	/**
	 * <p>Gets the field separator text.</p>
	 *
	 * @return the current field separator text
	 */
	protected String getFieldSeparator()
	{
		return fieldSeparator;
	}

	/**
	 * <p>Sets the field separator text.</p>
	 * <p/>
	 * <p><code>null</code> is accepted, but will be converted to
	 * an empty String.</p>
	 *
	 * @param fieldSeparator the new field separator text
	 */
	protected void setFieldSeparator(String fieldSeparator)
	{
		if (fieldSeparator == null)
		{
			fieldSeparator = "";
		}
		this.fieldSeparator = fieldSeparator;
	}


	/**
	 * <p>Gets whether the field separator should be added at the start
	 * of each buffer.</p>
	 *
	 * @return the fieldSeparatorAtStart flag
	 */
	protected boolean isFieldSeparatorAtStart()
	{
		return fieldSeparatorAtStart;
	}

	/**
	 * <p>Sets whether the field separator should be added at the start
	 * of each buffer.</p>
	 *
	 * @param fieldSeparatorAtStart the fieldSeparatorAtStart flag
	 */
	protected void setFieldSeparatorAtStart(boolean fieldSeparatorAtStart)
	{
		this.fieldSeparatorAtStart = fieldSeparatorAtStart;
	}


	/**
	 * <p>Gets whether the field separator should be added at the end
	 * of each buffer.</p>
	 *
	 * @return fieldSeparatorAtEnd flag
	 */
	protected boolean isFieldSeparatorAtEnd()
	{
		return fieldSeparatorAtEnd;
	}

	/**
	 * <p>Sets whether the field separator should be added at the end
	 * of each buffer.</p>
	 *
	 * @param fieldSeparatorAtEnd the fieldSeparatorAtEnd flag
	 */
	protected void setFieldSeparatorAtEnd(boolean fieldSeparatorAtEnd)
	{
		this.fieldSeparatorAtEnd = fieldSeparatorAtEnd;
	}


	/**
	 * <p>Gets the text to output when <code>null</code> found.</p>
	 *
	 * @return the current text to output when null found
	 */
	protected String getNullText()
	{
		return nullText;
	}

	/**
	 * <p>Sets the text to output when <code>null</code> found.</p>
	 * <p/>
	 * <p><code>null</code> is accepted, but will be converted to
	 * an empty String.</p>
	 *
	 * @param nullText the new text to output when null found
	 */
	protected void setNullText(String nullText)
	{
		if (nullText == null)
		{
			nullText = "";
		}
		this.nullText = nullText;
	}


	/**
	 * <p>Gets the start text to output when a <code>Collection</code>,
	 * <code>Map</code> or array size is output.</p>
	 * <p/>
	 * <p>This is output before the size value.</p>
	 *
	 * @return the current start of size text
	 */
	protected String getSizeStartText()
	{
		return sizeStartText;
	}

	/**
	 * <p>Sets the start text to output when a <code>Collection</code>,
	 * <code>Map</code> or array size is output.</p>
	 * <p/>
	 * <p>This is output before the size value.</p>
	 * <p/>
	 * <p><code>null</code> is accepted, but will be converted to
	 * an empty String.</p>
	 *
	 * @param sizeStartText the new start of size text
	 */
	protected void setSizeStartText(String sizeStartText)
	{
		if (sizeStartText == null)
		{
			sizeStartText = "";
		}
		this.sizeStartText = sizeStartText;
	}


	/**
	 * <p>Gets the end text to output when a <code>Collection</code>,
	 * <code>Map</code> or array size is output.</p>
	 * <p/>
	 * <p>This is output after the size value.</p>
	 *
	 * @return the current end of size text
	 */
	protected String getSizeEndText()
	{
		return sizeEndText;
	}

	/**
	 * <p>Sets the end text to output when a <code>Collection</code>,
	 * <code>Map</code> or array size is output.</p>
	 * <p/>
	 * <p>This is output after the size value.</p>
	 * <p/>
	 * <p><code>null</code> is accepted, but will be converted to
	 * an empty String.</p>
	 *
	 * @param sizeEndText the new end of size text
	 */
	protected void setSizeEndText(String sizeEndText)
	{
		if (sizeEndText == null)
		{
			sizeEndText = "";
		}
		this.sizeEndText = sizeEndText;
	}


	/**
	 * <p>Gets the start text to output when an <code>Object</code> is
	 * output in summary mode.</p>
	 * <p/>
	 * <p>This is output before the size value.</p>
	 *
	 * @return the current start of summary text
	 */
	protected String getSummaryObjectStartText()
	{
		return summaryObjectStartText;
	}

	/**
	 * <p>Sets the start text to output when an <code>Object</code> is
	 * output in summary mode.</p>
	 * <p/>
	 * <p>This is output before the size value.</p>
	 * <p/>
	 * <p><code>null</code> is accepted, but will be converted to
	 * an empty String.</p>
	 *
	 * @param summaryObjectStartText the new start of summary text
	 */
	protected void setSummaryObjectStartText(String summaryObjectStartText)
	{
		if (summaryObjectStartText == null)
		{
			summaryObjectStartText = "";
		}
		this.summaryObjectStartText = summaryObjectStartText;
	}


	/**
	 * <p>Gets the end text to output when an <code>Object</code> is
	 * output in summary mode.</p>
	 * <p/>
	 * <p>This is output after the size value.</p>
	 *
	 * @return the current end of summary text
	 */
	protected String getSummaryObjectEndText()
	{
		return summaryObjectEndText;
	}

	/**
	 * <p>Sets the end text to output when an <code>Object</code> is
	 * output in summary mode.</p>
	 * <p/>
	 * <p>This is output after the size value.</p>
	 * <p/>
	 * <p><code>null</code> is accepted, but will be converted to
	 * an empty String.</p>
	 *
	 * @param summaryObjectEndText the new end of summary text
	 */
	protected void setSummaryObjectEndText(String summaryObjectEndText)
	{
		if (summaryObjectEndText == null)
		{
			summaryObjectEndText = "";
		}
		this.summaryObjectEndText = summaryObjectEndText;
	}


	/**
	 * <p>Default <code>ToStringStyle</code>.</p>
	 * <p/>
	 * <p>This is an inner class rather than using
	 * <code>StandardToStringStyle</code> to ensure its immutability.</p>
	 */
	private static final class DefaultToStringStyle extends ToStringStyle
	{

		/**
		 * <p>Constructor.</p>
		 * <p/>
		 * <p>Use the static constant rather than instantiating.</p>
		 */
		private DefaultToStringStyle()
		{
			super();
		}

		/**
		 * <p>Ensure <code>Singleton</code> after serialization.</p>
		 *
		 * @return the singleton
		 */
		private Object readResolve()
		{
			return ToStringStyle.DEFAULT_STYLE;
		}

	}


	/**
	 * <p><code>ToStringStyle</code> that does not print out
	 * the field names.</p>
	 * <p/>
	 * <p>This is an inner class rather than using
	 * <code>StandardToStringStyle</code> to ensure its immutability.
	 */
	private static final class NoFieldNameToStringStyle extends ToStringStyle
	{

		/**
		 * <p>Constructor.</p>
		 * <p/>
		 * <p>Use the static constant rather than instantiating.</p>
		 */
		private NoFieldNameToStringStyle()
		{
			super();
			this.setUseFieldNames(false);
		}

		/**
		 * <p>Ensure <code>Singleton</code> after serialization.</p>
		 *
		 * @return the singleton
		 */
		private Object readResolve()
		{
			return ToStringStyle.NO_FIELD_NAMES_STYLE;
		}

	}


	/**
	 * <p><code>ToStringStyle</code> that prints out the short
	 * class name and no identity hashcode.</p>
	 * <p/>
	 * <p>This is an inner class rather than using
	 * <code>StandardToStringStyle</code> to ensure its immutability.</p>
	 */
	private static final class ShortPrefixToStringStyle extends ToStringStyle
	{

		/**
		 * <p>Constructor.</p>
		 * <p/>
		 * <p>Use the static constant rather than instantiating.</p>
		 */
		private ShortPrefixToStringStyle()
		{
			super();
			this.setUseShortClassName(true);
			this.setUseIdentityHashCode(false);
		}

		/**
		 * <p>Ensure <code>Singleton</ode> after serialization.</p>
		 *
		 * @return the singleton
		 */
		private Object readResolve()
		{
			return ToStringStyle.SHORT_PREFIX_STYLE;
		}

	}

	/**
	 * <p><code>ToStringStyle</code> that does not print out the
	 * classname, identity hashcode, content start or field name.</p>
	 * <p/>
	 * <p>This is an inner class rather than using
	 * <code>StandardToStringStyle</code> to ensure its immutability.</p>
	 */
	private static final class SimpleToStringStyle extends ToStringStyle
	{

		/**
		 * <p>Constructor.</p>
		 * <p/>
		 * <p>Use the static constant rather than instantiating.</p>
		 */
		private SimpleToStringStyle()
		{
			super();
			this.setUseClassName(false);
			this.setUseIdentityHashCode(false);
			this.setUseFieldNames(false);
			this.setContentStart("");
			this.setContentEnd("");
		}

		/**
		 * <p>Ensure <code>Singleton</ode> after serialization.</p>
		 *
		 * @return the singleton
		 */
		private Object readResolve()
		{
			return ToStringStyle.SIMPLE_STYLE;
		}

	}


	/**
	 * <p><code>ToStringStyle</code> that outputs on multiple lines.</p>
	 * <p/>
	 * <p>This is an inner class rather than using
	 * <code>StandardToStringStyle</code> to ensure its immutability.</p>
	 */
	private static final class MultiLineToStringStyle extends ToStringStyle
	{

		/**
		 * <p>Constructor.</p>
		 * <p/>
		 * <p>Use the static constant rather than instantiating.</p>
		 */
		private MultiLineToStringStyle()
		{
			super();
			this.setContentStart("[");
			this.setFieldSeparator(SystemUtility.LINE_SEPARATOR + "  ");
			this.setFieldSeparatorAtStart(true);
			this.setContentEnd(SystemUtility.LINE_SEPARATOR + "]");
		}

		/**
		 * <p>Ensure <code>Singleton</code> after serialization.</p>
		 *
		 * @return the singleton
		 */
		private Object readResolve()
		{
			return ToStringStyle.MULTI_LINE_STYLE;
		}

	}

}
