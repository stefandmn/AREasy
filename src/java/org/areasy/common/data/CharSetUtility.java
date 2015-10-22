package org.areasy.common.data;

/*
 * Copyright (c) 2007-2015 AREasy Runtime
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

import org.areasy.common.data.workers.ranges.CharRange;

import java.io.Serializable;
import java.util.*;

/**
 * <p>Operations on <code>CharSet</code>s.</p>
 * <p/>
 * <p>This class handles <code>null</code> input gracefully.
 * An exception will not be thrown for a <code>null</code> input.
 * Each method documents its behaviour in more detail.</p>
 *
 * @version $Id: CharSetUtility.java,v 1.2 2008/05/14 09:32:29 swd\stefan.damian Exp $
 * @see CharSet
 */
public class CharSetUtility
{

	/**
	 * <p>CharSetUtility instances should NOT be constructed in standard programming.
	 * Instead, the class should be used as <code>CharSetUtility.evaluateSet(null);</code>.</p>
	 * <p/>
	 * <p>This constructor is public to permit tools that require a JavaBean instance
	 * to operate.</p>
	 */
	public CharSetUtility()
	{
		//nothing to do here
	}

	// Factory
	/**
	 * <p>Creates a <code>CharSet</code> instance which allows a certain amount of
	 * set logic to be performed.</p>
	 * <p>The syntax is:</p>
	 * <ul>
	 * <li>&quot;aeio&quot; which implies 'a','e',..</li>
	 * <li>&quot;^e&quot; implies not e.</li>
	 * <li>&quot;ej-m&quot; implies e,j-&gt;m. e,j,k,l,m.</li>
	 * </ul>
	 * <p/>
	 * <pre>
	 * CharSetUtility.evaluateSet(null)    = null
	 * CharSetUtility.evaluateSet([])      = CharSet matching nothing
	 * CharSetUtility.evaluateSet(["a-e"]) = CharSet matching a,b,c,d,e
	 * </pre>
	 *
	 * @param set the set, may be null
	 * @return a CharSet instance, <code>null</code> if null input
	 */
	public static CharSet evaluateSet(String[] set)
	{
		if (set == null) return null;

		return new CharSet(set);
	}

	// Squeeze
	/**
	 * <p>Squeezes any repetitions of a character that is mentioned in the
	 * supplied set.</p>
	 * <p/>
	 * <pre>
	 * CharSetUtility.squeeze(null, *)        = null
	 * CharSetUtility.squeeze("", *)          = ""
	 * CharSetUtility.squeeze(*, null)        = *
	 * CharSetUtility.squeeze(*, "")          = *
	 * CharSetUtility.squeeze("hello", "k-p") = "helo"
	 * CharSetUtility.squeeze("hello", "a-e") = "hello"
	 * </pre>
	 *
	 * @param str the string to squeeze, may be null
	 * @param set the character set to use for manipulation, may be null
	 * @return modified String, <code>null</code> if null string input
	 * @see #evaluateSet(java.lang.String[]) for set-syntax.
	 */
	public static String squeeze(String str, String set)
	{
		if (StringUtility.isEmpty(str) || StringUtility.isEmpty(set))
		{
			return str;
		}
		String[] strs = new String[1];
		strs[0] = set;
		return squeeze(str, strs);
	}

	/**
	 * <p>Squeezes any repetitions of a character that is mentioned in the
	 * supplied set.</p>
	 * <p/>
	 * <p>An example is:</p>
	 * <ul>
	 * <li>squeeze(&quot;hello&quot;, {&quot;el&quot;}) => &quot;helo&quot;</li>
	 * </ul>
	 *
	 * @param str the string to squeeze, may be null
	 * @param set the character set to use for manipulation, may be null
	 * @return modified String, <code>null</code> if null string input
	 * @see #evaluateSet(java.lang.String[]) for set-syntax.
	 */
	public static String squeeze(String str, String[] set)
	{
		if (StringUtility.isEmpty(str) || ArrayUtility.isEmpty(set))
		{
			return str;
		}
		CharSet chars = evaluateSet(set);
		StringBuffer buffer = new StringBuffer(str.length());
		char[] chrs = str.toCharArray();
		int sz = chrs.length;
		char lastChar = ' ';
		char ch = ' ';
		for (int i = 0; i < sz; i++)
		{
			ch = chrs[i];
			if (chars.contains(ch))
			{
				if ((ch == lastChar) && (i != 0))
				{
					continue;
				}
			}
			buffer.append(ch);
			lastChar = ch;
		}
		return buffer.toString();
	}

	// Count
	/**
	 * <p>Takes an argument in set-syntax, see evaluateSet,
	 * and returns the number of characters present in the specified string.</p>
	 * <p/>
	 * <pre>
	 * CharSetUtility.count(null, *)        = 0
	 * CharSetUtility.count("", *)          = 0
	 * CharSetUtility.count(*, null)        = 0
	 * CharSetUtility.count(*, "")          = 0
	 * CharSetUtility.count("hello", "k-p") = 3
	 * CharSetUtility.count("hello", "a-e") = 1
	 * </pre>
	 *
	 * @param str String to count characters in, may be null
	 * @param set String set of characters to count, may be null
	 * @return character count, zero if null string input
	 * @see #evaluateSet(java.lang.String[]) for set-syntax.
	 */
	public static int count(String str, String set)
	{
		if (StringUtility.isEmpty(str) || StringUtility.isEmpty(set))
		{
			return 0;
		}
		String[] strs = new String[1];
		strs[0] = set;
		return count(str, strs);
	}

	/**
	 * <p>Takes an argument in set-syntax, see evaluateSet,
	 * and returns the number of characters present in the specified string.</p>
	 * <p/>
	 * <p>An example would be:</p>
	 * <ul>
	 * <li>count(&quot;hello&quot;, {&quot;c-f&quot;, &quot;o&quot;}) returns 2.</li>
	 * </ul>
	 *
	 * @param str String to count characters in, may be null
	 * @param set String[] set of characters to count, may be null
	 * @return character count, zero if null string input
	 * @see #evaluateSet(java.lang.String[]) for set-syntax.
	 */
	public static int count(String str, String[] set)
	{
		if (StringUtility.isEmpty(str) || ArrayUtility.isEmpty(set))
		{
			return 0;
		}
		CharSet chars = evaluateSet(set);
		int count = 0;
		char[] chrs = str.toCharArray();
		int sz = chrs.length;
		for (int i = 0; i < sz; i++)
		{
			if (chars.contains(chrs[i]))
			{
				count++;
			}
		}
		return count;
	}

	// Keep
	/**
	 * <p>Takes an argument in set-syntax, see evaluateSet,
	 * and keeps any of characters present in the specified string.</p>
	 * <p/>
	 * <pre>
	 * CharSetUtility.keep(null, *)        = null
	 * CharSetUtility.keep("", *)          = ""
	 * CharSetUtility.keep(*, null)        = ""
	 * CharSetUtility.keep(*, "")          = ""
	 * CharSetUtility.keep("hello", "hl")  = "hll"
	 * CharSetUtility.keep("hello", "le")  = "ell"
	 * </pre>
	 *
	 * @param str String to keep characters from, may be null
	 * @param set String set of characters to keep, may be null
	 * @return modified String, <code>null</code> if null string input
	 * @see #evaluateSet(java.lang.String[]) for set-syntax.
	 */
	public static String keep(String str, String set)
	{
		if (str == null)
		{
			return null;
		}
		if (str.length() == 0 || StringUtility.isEmpty(set))
		{
			return "";
		}
		String[] strs = new String[1];
		strs[0] = set;
		return keep(str, strs);
	}

	/**
	 * <p>Takes an argument in set-syntax, see evaluateSet,
	 * and keeps any of characters present in the specified string.</p>
	 * <p/>
	 * <p>An example would be:</p>
	 * <ul>
	 * <li>keep(&quot;hello&quot;, {&quot;c-f&quot;, &quot;o&quot;})
	 * returns &quot;eo&quot;</li>
	 * </ul>
	 *
	 * @param str String to keep characters from, may be null
	 * @param set String[] set of characters to keep, may be null
	 * @return modified String, <code>null</code> if null string input
	 * @see #evaluateSet(java.lang.String[]) for set-syntax.
	 */
	public static String keep(String str, String[] set)
	{
		if (str == null)
		{
			return null;
		}
		if (str.length() == 0 || ArrayUtility.isEmpty(set))
		{
			return "";
		}
		return modify(str, set, true);
	}

	// Delete
	/**
	 * <p>Takes an argument in set-syntax, see evaluateSet,
	 * and deletes any of characters present in the specified string.</p>
	 * <p/>
	 * <pre>
	 * CharSetUtility.delete(null, *)        = null
	 * CharSetUtility.delete("", *)          = ""
	 * CharSetUtility.delete(*, null)        = *
	 * CharSetUtility.delete(*, "")          = *
	 * CharSetUtility.delete("hello", "hl")  = "eo"
	 * CharSetUtility.delete("hello", "le")  = "ho"
	 * </pre>
	 *
	 * @param str String to delete characters from, may be null
	 * @param set String set of characters to delete, may be null
	 * @return modified String, <code>null</code> if null string input
	 * @see #evaluateSet(java.lang.String[]) for set-syntax.
	 */
	public static String delete(String str, String set)
	{
		if (StringUtility.isEmpty(str) || StringUtility.isEmpty(set))
		{
			return str;
		}
		String[] strs = new String[1];
		strs[0] = set;
		return delete(str, strs);
	}

	/**
	 * <p>Takes an argument in set-syntax, see evaluateSet,
	 * and deletes any of characters present in the specified string.</p>
	 * <p/>
	 * <p>An example would be:</p>
	 * <ul>
	 * <li>delete(&quot;hello&quot;, {&quot;c-f&quot;, &quot;o&quot;}) returns
	 * &quot;hll&quot;</li>
	 * </ul>
	 *
	 * @param str String to delete characters from, may be null
	 * @param set String[] set of characters to delete, may be null
	 * @return modified String, <code>null</code> if null string input
	 * @see #evaluateSet(java.lang.String[]) for set-syntax.
	 */
	public static String delete(String str, String[] set)
	{
		if (StringUtility.isEmpty(str) || ArrayUtility.isEmpty(set))
		{
			return str;
		}
		return modify(str, set, false);
	}

	/**
	 * Implementation of delete and keep
	 *
	 * @param str    String to modify characters within
	 * @param set    String[] set of characters to modify
	 * @param expect whether to evaluate on match, or non-match
	 * @return modified String
	 */
	private static String modify(String str, String[] set, boolean expect)
	{
		CharSet chars = evaluateSet(set);
		StringBuffer buffer = new StringBuffer(str.length());
		char[] chrs = str.toCharArray();
		int sz = chrs.length;
		for (int i = 0; i < sz; i++)
		{
			if (chars.contains(chrs[i]) == expect)
			{
				buffer.append(chrs[i]);
			}
		}
		return buffer.toString();
	}
}

/**
 * <p>A set of characters.</p>
 * <p/>
 * <p>Instances are immutable, but instances of subclasses may not be.</p>
 *
 * @version $Id: CharSetUtility.java,v 1.2 2008/05/14 09:32:29 swd\stefan.damian Exp $
 */
class CharSet implements Serializable
{

	/**
	 * A CharSet defining no characters.
	 *
	 */
	public static final CharSet EMPTY = new CharSet((String) null);

	/**
	 * A CharSet defining ASCII alphabetic characters "a-zA-Z".
	 *
	 */
	public static final CharSet ASCII_ALPHA = new CharSet("a-zA-Z");

	/**
	 * A CharSet defining ASCII alphabetic characters "a-z".
	 *
	 */
	public static final CharSet ASCII_ALPHA_LOWER = new CharSet("a-z");

	/**
	 * A CharSet defining ASCII alphabetic characters "A-Z".
	 *
	 */
	public static final CharSet ASCII_ALPHA_UPPER = new CharSet("A-Z");

	/**
	 * A CharSet defining ASCII alphabetic characters "0-9".
	 *
	 */
	public static final CharSet ASCII_NUMERIC = new CharSet("0-9");

	/**
	 * A Map of the common cases used in the factory.
	 * Subclasses can add more common patterns if desired.
	 *
	 */
	protected static final Map COMMON = new HashMap();

	static
	{
		COMMON.put(null, EMPTY);
		COMMON.put("", EMPTY);
		COMMON.put("a-zA-Z", ASCII_ALPHA);
		COMMON.put("A-Za-z", ASCII_ALPHA);
		COMMON.put("a-z", ASCII_ALPHA_LOWER);
		COMMON.put("A-Z", ASCII_ALPHA_UPPER);
		COMMON.put("0-9", ASCII_NUMERIC);
	}

	/**
	 * The set of CharRange objects.
	 */
	private Set set = new HashSet();

	/**
	 * <p>Factory method to create a new CharSet using a special syntax.</p>
	 * <p/>
	 * <ul>
	 * <li><code>null</code> or empty string ("")
	 * - set containing no characters</li>
	 * <li>Single character, such as "a"
	 * - set containing just that character</li>
	 * <li>Multi character, such as "a-e"
	 * - set containing characters from one character to the other</li>
	 * <li>Negated, such as "^a" or "^a-e"
	 * - set containing all characters except those defined</li>
	 * <li>Combinations, such as "abe-g"
	 * - set containing all the characters from the individual sets</li>
	 * </ul>
	 * <p/>
	 * <p>The matching order is:</p>
	 * <ol>
	 * <li>Negated multi character range, such as "^a-e"
	 * <li>Ordinary multi character range, such as "a-e"
	 * <li>Negated single character, such as "^a"
	 * <li>Ordinary single character, such as "a"
	 * </ol>
	 * <p>Matching works left to right. Once a match is found the
	 * search starts again from the next character.</p>
	 * <p/>
	 * <p>If the same range is defined twice using the same syntax, only
	 * one range will be kept.
	 * Thus, "a-ca-c" creates only one range of "a-c".</p>
	 * <p/>
	 * <p>If the start and end of a range are in the wrong order,
	 * they are reversed. Thus "a-e" is the same as "e-a".
	 * As a result, "a-ee-a" would create only one range,
	 * as the "a-e" and "e-a" are the same.</p>
	 * <p/>
	 * <p>The set of characters represented is the union of the specified ranges.</p>
	 * <p/>
	 * <p>All CharSet objects returned by this method will be immutable.</p>
	 *
	 * @param setStr the String describing the set, may be null
	 * @return a CharSet instance
	 */
	public static CharSet getInstance(String setStr)
	{
		Object set = COMMON.get(setStr);
		if (set != null) return (CharSet) set;

		return new CharSet(setStr);
	}

	/**
	 * <p>Constructs a new CharSet using the set syntax.</p>
	 *
	 * @param setStr the String describing the set, may be null
	 */
	protected CharSet(String setStr)
	{
		super();
		add(setStr);
	}

	/**
	 * <p>Constructs a new CharSet using the set syntax.
	 * Each string is merged in with the set.</p>
	 *
	 * @param set Strings to merge into the initial set
	 * @throws NullPointerException if set is <code>null</code>
	 */
	protected CharSet(String[] set)
	{
		super();
		int sz = set.length;

		for (int i = 0; i < sz; i++)
		{
			add(set[i]);
		}
	}

	/**
	 * <p>Add a set definition string to the <code>CharSet</code>.</p>
	 *
	 * @param str set definition string
	 */
	protected void add(String str)
	{
		if (str == null)
		{
			return;
		}

		int len = str.length();
		int pos = 0;
		while (pos < len)
		{
			int remainder = (len - pos);
			if (remainder >= 4 && str.charAt(pos) == '^' && str.charAt(pos + 2) == '-')
			{
				// negated range
				set.add(new CharRange(str.charAt(pos + 1), str.charAt(pos + 3), true));
				pos += 4;
			}
			else if (remainder >= 3 && str.charAt(pos + 1) == '-')
			{
				// range
				set.add(new CharRange(str.charAt(pos), str.charAt(pos + 2)));
				pos += 3;
			}
			else if (remainder >= 2 && str.charAt(pos) == '^')
			{
				// negated char
				set.add(new CharRange(str.charAt(pos + 1), true));
				pos += 2;
			}
			else
			{
				// char
				set.add(new CharRange(str.charAt(pos)));
				pos += 1;
			}
		}
	}

	/**
	 * <p>Gets the internal set as an array of CharRange objects.</p>
	 *
	 * @return an array of immutable CharRange objects
	 */
	public CharRange[] getCharRanges()
	{
		return (CharRange[]) set.toArray(new CharRange[set.size()]);
	}

	/**
	 * <p>Does the <code>CharSet</code> contain the specified
	 * character <code>ch</code>.</p>
	 *
	 * @param ch the character to check for
	 * @return <code>true</code> if the set contains the characters
	 */
	public boolean contains(char ch)
	{
		for (Iterator it = set.iterator(); it.hasNext();)
		{
			CharRange range = (CharRange) it.next();
			if (range.contains(ch)) return true;
		}
		return false;
	}

	// Basics
	/**
	 * <p>Compares two CharSet objects, returning true if they represent
	 * exactly the same set of characters defined in the same way.</p>
	 * <p/>
	 * <p>The two sets <code>abc</code> and <code>a-c</code> are <i>not</i>
	 * equal according to this method.</p>
	 *
	 * @param obj the object to compare to
	 * @return true if equal
	 */
	public boolean equals(Object obj)
	{
		if (obj == this)
		{
			return true;
		}
		if (obj instanceof CharSet == false)
		{
			return false;
		}
		CharSet other = (CharSet) obj;
		return set.equals(other.set);
	}

	/**
	 * <p>Gets a hashCode compatible with the equals method.</p>
	 *
	 * @return a suitable hashCode
	 */
	public int hashCode()
	{
		return 89 + set.hashCode();
	}

	/**
	 * <p>Gets a string representation of the set.</p>
	 *
	 * @return string representation of the set
	 */
	public String toString()
	{
		return set.toString();
	}

}
