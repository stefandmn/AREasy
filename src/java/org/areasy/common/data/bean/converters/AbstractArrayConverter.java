package org.areasy.common.data.bean.converters;


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

import org.areasy.common.data.bean.ConversionException;
import org.areasy.common.data.bean.Converter;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Convenience base class for converters that translate the String
 * representation of an array into a corresponding array of primitives
 * object.  This class encapsulates the functionality required to parse
 * the String into a list of String elements that can later be
 * individually converted to the appropriate primitive type.</p>
 * <p/>
 * <p>The input syntax accepted by the <code>parseElements()</code> method
 * is designed to be compatible with the syntax used to initialize arrays
 * in a Java source program, except that only String literal values are
 * supported.  For maximum flexibility, the surrounding '{' and '}'
 * characters are optional, and individual elements may be separated by
 * any combination of whitespace and comma characters.</p>
 *
 * @version $Revision: 1.2 $ $Date: 2008/05/14 09:32:38 $
 */

public abstract class AbstractArrayConverter implements Converter
{
	/**
	 * The default value specified to our Constructor, if any.
	 */
	protected Object defaultValue = null;

	/**
	 * <p>Model object for string arrays.</p>
	 */
	protected static String strings[] = new String[0];

	/**
	 * Should we return the default value on conversion errors?
	 */
	protected boolean useDefault = true;

	/**
	 * Convert the specified input object into an output object of the
	 * specified type.  This method must be implemented by a concrete
	 * subclass.
	 *
	 * @param type  Data type to which this value should be converted
	 * @param value The input value to be converted
	 * @throws ConversionException if conversion cannot be performed
	 *                             successfully
	 */
	public abstract Object convert(Class type, Object value);


	/**
	 * <p>Parse an incoming String of the form similar to an array initializer
	 * in the Java language into a <code>List</code> individual Strings
	 * for each element, according to the following rules.</p>
	 * <ul>
	 * <li>The string must have matching '{' and '}' delimiters around
	 * a comma-delimited list of values.</li>
	 * <li>Whitespace before and after each element is stripped.
	 * <li>If an element is itself delimited by matching single or double
	 * quotes, the usual rules for interpreting a quoted String apply.</li>
	 * </ul>
	 *
	 * @param svalue String value to be parsed
	 * @throws ConversionException  if the syntax of <code>svalue</code>
	 *                              is not syntactically valid
	 * @throws NullPointerException if <code>svalue</code>
	 *                              is <code>null</code>
	 */
	protected List parseElements(String svalue)
	{
		// Validate the passed argument
		if (svalue == null)
		{
			throw new NullPointerException();
		}

		// Trim any matching '{' and '}' delimiters
		svalue = svalue.trim();
		if (svalue.startsWith("{") && svalue.endsWith("}"))
		{
			svalue = svalue.substring(1, svalue.length() - 1);
		}

		try
		{

			// Set up a StreamTokenizer on the characters in this String
			StreamTokenizer st =
					new StreamTokenizer(new StringReader(svalue));
			st.whitespaceChars(',', ','); // Commas are delimiters
			st.ordinaryChars('0', '9');  // Needed to turn off numeric flag
			st.ordinaryChars('.', '.');
			st.ordinaryChars('-', '-');
			st.wordChars('0', '9');      // Needed to make part of tokens
			st.wordChars('.', '.');
			st.wordChars('-', '-');

			// Split comma-delimited tokens into a List
			ArrayList list = new ArrayList();
			while (true)
			{
				int ttype = st.nextToken();
				if ((ttype == StreamTokenizer.TT_WORD) ||
						(ttype > 0))
				{
					list.add(st.sval);
				}
				else if (ttype == StreamTokenizer.TT_EOF)
				{
					break;
				}
				else
				{
					throw new ConversionException
							("Encountered token of type " + ttype);
				}
			}

			// Return the completed list
			return (list);

		}
		catch (IOException e)
		{

			throw new ConversionException(e);

		}


	}


}
