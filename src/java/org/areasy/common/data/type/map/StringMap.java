package org.areasy.common.data.type.map;

/*
 * Copyright (c) 2007-2016 AREasy Runtime
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

import java.io.Externalizable;
import java.util.*;

/**
 * Map like class of Strings to Objects.
 * This String Map has been optimized for mapping small sets of
 * Strings where the most frequently accessed Strings have been put to
 * the map first.
 * <p/>
 * It also has the benefit that it can look up entries by substring or
 * sections of char and byte arrays.  This can prevent many String
 * objects from being created just to look up in the map.
 * <p/>
 * This map is NOT synchronized.
 *
 * @version $Id: StringMap.java,v 1.2 2008/05/14 09:32:30 swd\stefan.damian Exp $
 */
public class StringMap extends AbstractMap implements Externalizable
{
	private static final int HASH_WIDTH = 9;

	protected int width = HASH_WIDTH;

	protected Node root = new Node();
	protected boolean ignoreCase = false;

	protected NullEntry nullEntry = null;
	protected Object nullValue = null;

	protected HashSet entrySet = new HashSet(3);

	protected Set umEntrySet = Collections.unmodifiableSet(entrySet);

	/**
	 * Constructor.
	 */
	public StringMap()
	{
		//nothing to do
	}

	/**
	 * Constructor.
	 *
	 * @param ignoreCase
	 */
	public StringMap(boolean ignoreCase)
	{
		this.ignoreCase = ignoreCase;
	}

	/**
	 * Constructor.
	 *
	 * @param ignoreCase
	 * @param width      Width of hash tables, larger values are faster but
	 *                   use more memory.
	 */
	public StringMap(boolean ignoreCase, int width)
	{
		this.ignoreCase = ignoreCase;
		this.width = width;
	}

	/**
	 * Set the ignoreCase attribute.
	 *
	 * @param ic If true, the map is case insensitive for keys.
	 */
	public void setIgnoreCase(boolean ic)
	{
		if (root._children != null) throw new IllegalStateException("Must be set before first put");
		ignoreCase = ic;
	}

	public boolean isIgnoreCase()
	{
		return ignoreCase;
	}

	/**
	 * Set the hash width.
	 *
	 * @param width Width of hash tables, larger values are faster but
	 *              use more memory.
	 */
	public void setWidth(int width)
	{
		this.width = width;
	}

	public int getWidth()
	{
		return width;
	}

	public Object put(Object key, Object value)
	{
		if (key == null) return put(null, value);
		return put(key.toString(), value);
	}

	public Object put(String key, Object value)
	{
		if (key == null)
		{
			Object oldValue = nullValue;
			nullValue = value;

			if (nullEntry == null)
			{
				nullEntry = new NullEntry();
				entrySet.add(nullEntry);
			}

			return oldValue;
		}

		Node node = root;
		
		int ni = -1;
		Node prev = null;
		Node parent = null;

		// look for best match
		charLoop:
			for (int i = 0; i < key.length(); i++)
			{
				char c = key.charAt(i);

				// Advance node
				if (ni == -1)
				{
					parent = node;
					prev = null;
					ni = 0;
					node = (node._children == null) ? null : node._children[c % width];
				}

				// Loop through a node chain at the same level
				while (node != null)
				{
					// If it is a matching node, goto next char
					if (node._char[ni] == c || ignoreCase && node._ochar[ni] == c)
					{
						prev = null;
						ni++;

						if (ni == node._char.length) ni = -1;

						continue charLoop;
					}

					// no char match if the first char,
					if (ni == 0)
					{
						// look along the chain for a char match
						prev = node;
						node = node._next;
					}
					else
					{
						// Split the current node!
						node.split(this, ni);
						i--;
						ni = -1;
						
						continue charLoop;
					}
				}

				// We have run out of nodes, so as this is a put, make one
				node = new Node(ignoreCase, key, i);

				if (prev != null) prev._next = node;
				else if (parent != null) // add new child
				{
					if (parent._children == null) parent._children = new Node[width];

					parent._children[c % width] = node;
					int oi = node._ochar[0] % width;
					if (node._ochar != null && node._char[0] % width != oi)
					{
						if (parent._children[oi] == null) parent._children[oi] = node;
						else
						{
							Node n = parent._children[oi];
							while (n._next != null)
							{
								n = n._next;
							}

							n._next = node;
						}
					}
				}
				else root = node;
				break;
			}

		// Do we have a node
		if (node != null)
		{
			// Split it if we are in the middle
			if (ni > 0) node.split(this, ni);

			Object old = node._value;
			node._key = key;
			node._value = value;
			entrySet.add(node);

			return old;
		}

		return null;
	}

	public Object get(Object key)
	{
		if (key == null) return nullValue;
		if (key instanceof String) return get((String) key);

		return get(key.toString());
	}

	public Object get(String key)
	{
		if (key == null) return nullValue;

		Map.Entry entry = getEntry(key, 0, key.length());
		if (entry == null) return null;

		return entry.getValue();
	}

	/**
	 * Get a map entry by substring key.
	 *
	 * @param key    String containing the key
	 * @param offset Offset of the key within the String.
	 * @param length The length of the key
	 * @return The Map.Entry for the key or null if the key is not in
	 *         the map.
	 */
	public Map.Entry getEntry(String key, int offset, int length)
	{
		if (key == null) return nullEntry;

		Node node = root;
		int ni = -1;

		// look for best match
		charLoop:
			for (int i = 0; i < length; i++)
			{
				char c = key.charAt(offset + i);

				// Advance node
				if (ni == -1)
				{
					ni = 0;
					node = (node._children == null) ? null : node._children[c % width];
				}

				// Look through the node chain
				while (node != null)
				{
					// If it is a matching node, goto next char
					if (node._char[ni] == c || ignoreCase && node._ochar[ni] == c)
					{
						ni++;
						if (ni == node._char.length) ni = -1;

						continue charLoop;
					}

					// No char match, so if mid node then no match at all.
					if (ni > 0) return null;

					// try next in chain
					node = node._next;
				}

				return null;
			}

		if (ni > 0) return null;
		if (node != null && node._key == null) return null;

		return node;
	}

	/**
	 * Get a map entry by char array key.
	 *
	 * @param key    char array containing the key
	 * @param offset Offset of the key within the array.
	 * @param length The length of the key
	 * @return The Map.Entry for the key or null if the key is not in
	 *         the map.
	 */
	public Map.Entry getEntry(char[] key, int offset, int length)
	{
		if (key == null) return nullEntry;

		Node node = root;
		int ni = -1;

		// look for best match
		charLoop:
			for (int i = 0; i < length; i++)
			{
				char c = key[offset + i];

				// Advance node
				if (ni == -1)
				{
					ni = 0;
					node = (node._children == null) ? null : node._children[c % width];
				}

				// While we have a node to try
				while (node != null)
				{
					// If it is a matching node, goto next char
					if (node._char[ni] == c || ignoreCase && node._ochar[ni] == c)
					{
						ni++;
						if (ni == node._char.length) ni = -1;

						continue charLoop;
					}

					// No char match, so if mid node then no match at all.
					if (ni > 0) return null;

					// try next in chain
					node = node._next;
				}

				return null;
			}

		if (ni > 0) return null;
		if (node != null && node._key == null) return null;

		return node;
	}

	/**
	 * Get a map entry by byte array key.
	 *
	 * @param key    byte array containing the key. A simple ASCII byte
	 *               to char mapping is used.
	 * @param offset Offset of the key within the array.
	 * @param length The length of the key
	 * @return The Map.Entry for the key or null if the key is not in
	 *         the map.
	 */
	public Map.Entry getEntry(byte[] key, int offset, int length)
	{
		if (key == null) return nullEntry;

		Node node = root;
		int ni = -1;

		// look for best match
		charLoop:
			for (int i = 0; i < length; i++)
			{
				char c = (char) (key[offset + i]);

				// Advance node
				if (ni == -1)
				{
					ni = 0;
					node = (node._children == null) ? null : node._children[c % width];
				}

				// While we have a node to try
				while (node != null)
				{
					// If it is a matching node, goto next char
					if (node._char[ni] == c || ignoreCase && node._ochar[ni] == c)
					{
						ni++;
						if (ni == node._char.length) ni = -1;

						continue charLoop;
					}

					// No char match, so if mid node then no match at all.
					if (ni > 0) return null;

					// try next in chain
					node = node._next;
				}

				return null;
			}

		if (ni > 0) return null;
		if (node != null && node._key == null) return null;

		return node;
	}

	public Object remove(Object key)
	{
		if (key == null) return remove(null);
		return remove(key.toString());
	}

	public Object remove(String key)
	{
		if (key == null)
		{
			Object oldValue = nullValue;
			if (nullEntry != null)
			{
				entrySet.remove(nullEntry);
				nullEntry = null;
				nullValue = null;
			}

			return oldValue;
		}

		Node node = root;
		int ni = -1;

		// look for best match
		charLoop:
			for (int i = 0; i < key.length(); i++)
			{
				char c = key.charAt(i);

				// Advance node
				if (ni == -1)
				{
					ni = 0;
					node = (node._children == null) ? null : node._children[c % width];
				}

				// While we have a node to try
				while (node != null)
				{
					// If it is a matching node, goto next char
					if (node._char[ni] == c || ignoreCase && node._ochar[ni] == c)
					{
						ni++;
						if (ni == node._char.length) ni = -1;

						continue charLoop;
					}

					// No char match, so if mid node then no match at all.
					if (ni > 0) return null;

					// try next in chain
					node = node._next;
				}
				return null;
			}

		if (ni > 0) return null;
		if (node == null || node._key == null) return null;

		Object old = node._value;
		entrySet.remove(node);
		node._value = null;
		node._key = null;

		return old;
	}

	public Set entrySet()
	{
		return umEntrySet;
	}

	public int size()
	{
		return entrySet.size();
	}

	public boolean isEmpty()
	{
		return entrySet.isEmpty();
	}

	public boolean containsKey(Object key)
	{
		if (key == null) return nullEntry != null;
		return getEntry(key.toString(), 0, key == null ? 0 : key.toString().length()) != null;
	}

	public void clear()
	{
		root = new Node();
		nullEntry = null;
		nullValue = null;
		entrySet.clear();
	}


	private static class Node implements Map.Entry
	{
		char[] _char;
		char[] _ochar;
		Node _next;
		Node[] _children;
		String _key;
		Object _value;

		Node()
		{
			//null constructor.
		}

		Node(boolean ignoreCase, String s, int offset)
		{
			int l = s.length() - offset;
			_char = new char[l];
			_ochar = new char[l];
			for (int i = 0; i < l; i++)
			{
				char c = s.charAt(offset + i);
				_char[i] = c;
				if (ignoreCase)
				{
					char o = c;

					if (Character.isUpperCase(c)) o = Character.toLowerCase(c);
						else if (Character.isLowerCase(c)) o = Character.toUpperCase(c);

					_ochar[i] = o;
				}
			}
		}

		Node split(StringMap map, int offset)
		{
			Node split = new Node();
			int sl = _char.length - offset;

			char[] tmp = this._char;
			this._char = new char[offset];
			split._char = new char[sl];
			System.arraycopy(tmp, 0, this._char, 0, offset);
			System.arraycopy(tmp, offset, split._char, 0, sl);

			if (this._ochar != null)
			{
				tmp = this._ochar;
				this._ochar = new char[offset];
				split._ochar = new char[sl];
				System.arraycopy(tmp, 0, this._ochar, 0, offset);
				System.arraycopy(tmp, offset, split._ochar, 0, sl);
			}

			split._key = this._key;
			split._value = this._value;
			this._key = null;
			this._value = null;

			if (map.entrySet.remove(this)) map.entrySet.add(split);

			split._children = this._children;
			this._children = new Node[map.width];
			this._children[split._char[0] % map.width] = split;

			if (split._ochar != null && this._children[split._ochar[0] % map.width] != split) this._children[split._ochar[0] % map.width] = split;

			return split;
		}

		public Object getKey()
		{
			return _key;
		}

		public Object getValue()
		{
			return _value;
		}

		public Object setValue(Object o)
		{
			Object old = _value;
			_value = o;
			return old;
		}

		public String toString()
		{
			StringBuffer buf = new StringBuffer();
			synchronized (buf)
			{
				toString(buf);
			}
			return buf.toString();
		}

		private void toString(StringBuffer buf)
		{
			buf.append("{[");

			if (_char == null) buf.append('-');
			else
			{
				for (int i = 0; i < _char.length; i++)
				{
					buf.append(_char[i]);
				}
			}

			buf.append(':');
			buf.append(_key);
			buf.append('=');
			buf.append(_value);
			buf.append(']');

			if (_children != null)
			{
				for (int i = 0; i < _children.length; i++)
				{
					buf.append('|');
					if (_children[i] != null) _children[i].toString(buf);
					else buf.append("-");
				}
			}

			buf.append('}');
			if (_next != null)
			{
				buf.append(",\n");
				_next.toString(buf);
			}
		}
	}

	private class NullEntry implements Map.Entry
	{
		public Object getKey()
		{
			return null;
		}

		public Object getValue()
		{
			return nullValue;
		}

		public Object setValue(Object o)
		{
			Object old = nullValue;
			nullValue = o;

			return old;
		}

		public String toString()
		{
			return "[:null=" + nullValue + "]";
		}
	}

	public void writeExternal(java.io.ObjectOutput out) throws java.io.IOException
	{
		HashMap map = new HashMap(this);
		out.writeObject(map);
	}

	public void readExternal(java.io.ObjectInput in) throws java.io.IOException, ClassNotFoundException
	{
		HashMap map = (HashMap) in.readObject();
		this.putAll(map);
	}
}

