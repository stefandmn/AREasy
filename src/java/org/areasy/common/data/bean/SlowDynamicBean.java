package org.areasy.common.data.bean;

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

import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * <p>DynamicBean which automatically adds properties to the <code>DynamicClass</code>
 * and provides <i>Slow List</i> and <i>Slow Map</i> features.</p>
 * <p/>
 * <p>DynamicBeans deal with three types of properties - <i>simple</i>, <i>indexed</i> and <i>mapped</i> and
 * have the following <code>get()</code> and <code>set()</code> methods for
 * each of these types:</p>
 * <ul>
 * <li><i>Simple</i> property methods - <code>get(name)</code> and <code>set(name, value)</code></li>
 * <li><i>Indexed</i> property methods - <code>get(name, index)</code> and <code>set(name, index, value)</code></li>
 * <li><i>Mapped</i> property methods - <code>get(name, key)</code> and <code>set(name, key, value)</code></li>
 * </ul>
 * <p/>
 * <p><b><u>Getting Property Values</u></b></p>
 * <p>Calling any of the <code>get()</code> methods, for a property which
 * doesn't exist, returns <code>null</code> in this implementation.</p>
 * <p/>
 * <p><b><u>Setting Simple Properties</u></b></p>
 * <p>The <code>SlowDynamicBean</code> will automatically add a property to the <code>DynamicClass</code>
 * if it doesn't exist when the <code>set(name, value)</code> method is called.</p>
 * <p/>
 * <code>DynamicBean myBean = new SlowDynamicBean();</code></br>
 * <code>myBean.set("myProperty", "myValue");</code></br>
 * <p/>
 * <p><b><u>Setting Indexed Properties</u></b></p>
 * <p>If the property <b>doesn't</b> exist, the <code>SlowDynamicBean</code> will automatically add
 * a property with an <code>ArrayList</code> type to the <code>DynamicClass</code> when
 * the <code>set(name, index, value)</code> method is called.
 * It will also instantiate a new <code>ArrayList</code> and automatically <i>grow</i>
 * the <code>List</code> so that it is big enough to accomodate the index being set.
 * <code>ArrayList</code> is the default indexed property that SlowDynamicBean uses but
 * this can be easily changed by overriding the <code>newIndexedProperty(name)</code>
 * method.</p>
 * <p/>
 * <code>DynamicBean myBean = new SlowDynamicBean();</code></br>
 * <code>myBean.set("myIndexedProperty", 0, "myValue1");</code></br>
 * <code>myBean.set("myIndexedProperty", 1, "myValue2");</code></br>
 * <p/>
 * <p>If the indexed property <b>does</b> exist in the <code>DynamicClass</code> but is set to
 * <code>null</code> in the <code>SlowDynamicBean</code>, then it will instantiate a
 * new <code>List</code> or <code>Array</code> as specified by the property's type
 * in the <code>DynamicClass</code> and automatically <i>grow</i> the <code>List</code>
 * or <code>Array</code> so that it is big enough to accomodate the index being set.</p>
 * <p/>
 * <code>DynamicBean myBean = new SlowDynamicBean();</code></br>
 * <code>MutableDynamicClass myClass = (MutableDynamicClass)myBean.getDynamicClass();</code></br>
 * <code>myClass.add("myIndexedProperty", int[].class);</code></br>
 * <code>myBean.set("myIndexedProperty", 0, new Integer(10));</code></br>
 * <code>myBean.set("myIndexedProperty", 1, new Integer(20));</code></br>
 * <p/>
 * <p><b><u>Setting Mapped Properties</u></b></p>
 * <p>If the property <b>doesn't</b> exist, the <code>SlowDynamicBean</code> will automatically add
 * a property with a <code>HashMap</code> type to the <code>DynamicClass</code> and
 * instantiate a new <code>HashMap</code> in the DynamicBean when the
 * <code>set(name, key, value)</code> method is called. <code>HashMap</code> is the default
 * mapped property that SlowDynamicBean uses but this can be easily changed by overriding
 * the <code>newMappedProperty(name)</code> method.</p>
 * <p/>
 * <code>DynamicBean myBean = new SlowDynamicBean();</code></br>
 * <code>myBean.set("myMappedProperty", "myKey", "myValue");</code></br>
 * <p/>
 * <p>If the mapped property <b>does</b> exist in the <code>DynamicClass</code> but is set to
 * <code>null</code> in the <code>SlowDynamicBean</code>, then it will instantiate a
 * new <code>Map</code> as specified by the property's type in the <code>DynamicClass</code>.</p>
 * <p/>
 * <code>DynamicBean myBean = new SlowDynamicBean();</code></br>
 * <code>MutableDynamicClass myClass = (MutableDynamicClass)myBean.getDynamicClass();</code></br>
 * <code>myClass.add("myMappedProperty", TreeMap.class);</code></br>
 * <code>myBean.set("myMappedProperty", "myKey", "myValue");</code></br>
 * <p/>
 * <p><b><u><i>Restricted</i> DynamicClass</u></b></p>
 * <p><code>MutableDynamicClass</code> have a facility to <i>restrict</i> the <code>DynamicClass</code>
 * so that its properties cannot be modified. If the <code>MutableDynamicClass</code> is
 * restricted then calling any of the <code>set()</code> methods for a property which
 * doesn't exist will result in a <code>IllegalArgumentException</code> being thrown.</p>
 *
 * @version $Id: SlowDynamicBean.java,v 1.2 2008/05/14 09:32:37 swd\stefan.damian Exp $
 */
public class SlowDynamicBean implements DynamicBean, Serializable
{
	/**
	 * Commons Logging
	 */
	private static Logger logger = LoggerFactory.getLog(SlowDynamicBean.class);

	protected static final BigInteger BigInteger_ZERO = new BigInteger("0");
	protected static final BigDecimal BigDecimal_ZERO = new BigDecimal("0");
	protected static final Character Character_SPACE = new Character(' ');
	protected static final Byte Byte_ZERO = new Byte((byte) 0);
	protected static final Short Short_ZERO = new Short((short) 0);
	protected static final Integer Integer_ZERO = new Integer(0);
	protected static final Long Long_ZERO = new Long((long) 0);
	protected static final Float Float_ZERO = new Float((byte) 0);
	protected static final Double Double_ZERO = new Double((byte) 0);

	/**
	 * The <code>MutableDynamicClass</code> "base class" that this DynamicBean
	 * is associated with.
	 */
	protected Map values;

	/**
	 * The <code>MutableDynamicClass</code> "base class" that this DynamicBean
	 * is associated with.
	 */
	protected MutableDynamicClass dynamicClass;


	// ------------------- Constructors ----------------------------------

	/**
	 * Construct a new <code>SlowDynamicBean</code> with a <code>SlowDynamicClass</code> instance.
	 */
	public SlowDynamicBean()
	{
		this(new SlowDynamicClass());
	}

	/**
	 * Construct a new <code>SlowDynamicBean</code> with a <code>SlowDynamicClass</code> instance.
	 *
	 * @param name Name of this DynamicBean class
	 */
	public SlowDynamicBean(String name)
	{
		this(new SlowDynamicClass(name));
	}

	/**
	 * Construct a new <code>DynamicBean</code> associated with the specified
	 * <code>DynamicClass</code> instance - if its not a <code>MutableDynamicClass</code>
	 * then a new <code>SlowDynamicClass</code> is created and the properties copied.
	 *
	 * @param dynamicClass The DynamicClass we are associated with
	 */
	public SlowDynamicBean(DynamicClass dynamicClass)
	{
		values = newMap();

		if (dynamicClass instanceof MutableDynamicClass) this.dynamicClass = (MutableDynamicClass) dynamicClass;
			else this.dynamicClass = new SlowDynamicClass(dynamicClass.getName(), dynamicClass.getDynamicProperties());
	}


	/**
	 * Return the Map backing this <code>DynamicBean</code>
	 */
	public Map getMap()
	{
		return values;
	}

	/**
	 * <p>Return the size of an indexed or mapped property.</p>
	 *
	 * @param name Name of the property
	 * @throws IllegalArgumentException if no property name is specified
	 */
	public int size(String name)
	{
		if (name == null) throw new IllegalArgumentException("No property name specified");

		Object value = values.get(name);
		if (value == null)
		{
			return 0;
		}

		if (value instanceof Map)
		{
			return ((Map) value).size();
		}

		if (value instanceof List)
		{
			return ((List) value).size();
		}

		if ((value.getClass().isArray())) return Array.getLength(value);

		return 0;
	}

	/**
	 * Does the specified mapped property contain a value for the specified
	 * key value?
	 *
	 * @param name Name of the property to check
	 * @param key  Name of the key to check
	 * @throws IllegalArgumentException if no property name is specified
	 */
	public boolean contains(String name, String key)
	{
		if (name == null) throw new IllegalArgumentException("No property name specified");

		Object value = values.get(name);
		if (value == null) return false;

		if (value instanceof Map) return (((Map) value).containsKey(key));

		return false;
	}

	/**
	 * <p>Return the value of a simple property with the specified name.</p>
	 * <p/>
	 * <p><strong>N.B.</strong> Returns <code>null</code> if there is no property
	 * of the specified name.</p>
	 *
	 * @param name Name of the property whose value is to be retrieved.
	 * @throws IllegalArgumentException if no property name is specified
	 */
	public Object get(String name)
	{

		if (name == null)
		{
			throw new IllegalArgumentException("No property name specified");
		}

		// Value found
		Object value = values.get(name);
		if (value != null) return value;

		// Property doesn't exist
		if (!isDynamicProperty(name)) return null;

		// Property doesn't exist
		value = createProperty(name, dynamicClass.getDynamicProperty(name).getType());

		if (value != null) set(name, value);

		return value;
	}

	/**
	 * <p>Return the value of an indexed property with the specified name.</p>
	 * <p/>
	 * <p><strong>N.B.</strong> Returns <code>null</code> if there is no 'indexed'
	 * property of the specified name.</p>
	 *
	 * @param name  Name of the property whose value is to be retrieved
	 * @param index Index of the value to be retrieved
	 * @throws IllegalArgumentException  if the specified property
	 *                                   exists, but is not indexed
	 * @throws IndexOutOfBoundsException if the specified index
	 *                                   is outside the range of the underlying property
	 */
	public Object get(String name, int index)
	{
		// If its not a property, then create default indexed property
		if (!isDynamicProperty(name)) set(name, defaultIndexedProperty(name));

		// Get the indexed property
		Object indexedProperty = get(name);

		// Check that the property is indexed
		if (!dynamicClass.getDynamicProperty(name).isIndexed()) throw new IllegalArgumentException("Non-indexed property for '" + name + "[" + index + "]' " + dynamicClass.getDynamicProperty(name).getName());

		// Grow indexed property to appropriate size
		indexedProperty = growIndexedProperty(name, indexedProperty, index);

		// Return the indexed value
		if (indexedProperty.getClass().isArray()) return Array.get(indexedProperty, index);
			else if (indexedProperty instanceof List) return ((List) indexedProperty).get(index);
				else throw new IllegalArgumentException("Non-indexed property for '" + name + "[" + index + "]' " + indexedProperty.getClass().getName());
	}

	/**
	 * <p>Return the value of a mapped property with the specified name.</p>
	 * <p/>
	 * <p><strong>N.B.</strong> Returns <code>null</code> if there is no 'mapped'
	 * property of the specified name.</p>
	 *
	 * @param name Name of the property whose value is to be retrieved
	 * @param key  Key of the value to be retrieved
	 * @throws IllegalArgumentException if the specified property
	 *                                  exists, but is not mapped
	 */
	public Object get(String name, String key)
	{
		// If its not a property, then create default mapped property
		if (!isDynamicProperty(name)) set(name, defaultMappedProperty(name));

		// Get the mapped property
		Object mappedProperty = get(name);

		// Check that the property is mapped
		if (!dynamicClass.getDynamicProperty(name).isMapped()) throw new IllegalArgumentException("Non-mapped property for '" + name + "(" + key + ")' " + dynamicClass.getDynamicProperty(name).getType().getName());

		// Get the value from the Map
		if (mappedProperty instanceof Map) return (((Map) mappedProperty).get(key));
			else throw new IllegalArgumentException("Non-mapped property for '" + name + "(" + key + ")'" + mappedProperty.getClass().getName());
	}


	/**
	 * Return the <code>DynamicClass</code> instance that describes the set of
	 * properties available for this DynamicBean.
	 */
	public DynamicClass getDynamicClass()
	{
		return (DynamicClass) dynamicClass;
	}

	/**
	 * Remove any existing value for the specified key on the
	 * specified mapped property.
	 *
	 * @param name Name of the property for which a value is to
	 *             be removed
	 * @param key  Key of the value to be removed
	 * @throws IllegalArgumentException if there is no property
	 *                                  of the specified name
	 */
	public void remove(String name, String key)
	{

		if (name == null) throw new IllegalArgumentException("No property name specified");

		Object value = values.get(name);
		if (value == null) return;

		if (value instanceof Map)((Map) value).remove(key);
			else throw new IllegalArgumentException ("Non-mapped property for '" + name + "(" + key + ")'" + value.getClass().getName());
	}

	/**
	 * Set the value of a simple property with the specified name.
	 *
	 * @param name  Name of the property whose value is to be set
	 * @param value Value to which this property is to be set
	 * @throws IllegalArgumentException if this is not an existing property
	 *                                  name for our DynamicClass and the MutableDynamicClass is restricted
	 * @throws ConversionException      if the specified value cannot be
	 *                                  converted to the type required for this property
	 * @throws NullPointerException     if an attempt is made to set a
	 *                                  primitive property to null
	 */
	public void set(String name, Object value)
	{

		// If the property doesn't exist, then add it
		if (!isDynamicProperty(name))
		{

			if (dynamicClass.isRestricted())
			{
				throw new IllegalArgumentException
						("Invalid property name '" + name + "' (DynamicClass is restricted)");
			}

			if (value == null) dynamicClass.add(name);
				else dynamicClass.add(name, value.getClass());
		}

		DynamicProperty descriptor = dynamicClass.getDynamicProperty(name);

		if (value == null)
		{
			if (descriptor.getType().isPrimitive()) throw new NullPointerException("Primitive value for '" + name + "'");
		}
		else if (!isAssignable(descriptor.getType(), value.getClass()))
		{
			throw new ConversionException("Cannot assign value of type '" + value.getClass().getName() + "' to property '" + name + "' of type '" + descriptor.getType().getName() + "'");
		}

		// Set the property's value
		values.put(name, value);

	}

	/**
	 * Set the value of an indexed property with the specified name.
	 *
	 * @param name  Name of the property whose value is to be set
	 * @param index Index of the property to be set
	 * @param value Value to which this property is to be set
	 * @throws ConversionException       if the specified value cannot be
	 *                                   converted to the type required for this property
	 * @throws IllegalArgumentException  if there is no property
	 *                                   of the specified name
	 * @throws IllegalArgumentException  if the specified property
	 *                                   exists, but is not indexed
	 * @throws IndexOutOfBoundsException if the specified index
	 *                                   is outside the range of the underlying property
	 */
	public void set(String name, int index, Object value)
	{
		// If its not a property, then create default indexed property
		if (!isDynamicProperty(name)) set(name, defaultIndexedProperty(name));

		// Get the indexed property
		Object indexedProperty = get(name);

		// Check that the property is indexed
		if (!dynamicClass.getDynamicProperty(name).isIndexed()) throw new IllegalArgumentException("Non-indexed property for '" + name + "[" + index + "]'" + dynamicClass.getDynamicProperty(name).getType().getName());

		// Grow indexed property to appropriate size
		indexedProperty = growIndexedProperty(name, indexedProperty, index);

		// Set the value in an array
		if (indexedProperty.getClass().isArray())
		{
			Array.set(indexedProperty, index, value);
		}
		else if (indexedProperty instanceof List)
		{
			((List) indexedProperty).set(index, value);
		}
		else throw new IllegalArgumentException("Non-indexed property for '" + name + "[" + index + "]' " + indexedProperty.getClass().getName());
	}

	/**
	 * Set the value of a mapped property with the specified name.
	 *
	 * @param name  Name of the property whose value is to be set
	 * @param key   Key of the property to be set
	 * @param value Value to which this property is to be set
	 * @throws ConversionException      if the specified value cannot be
	 *                                  converted to the type required for this property
	 * @throws IllegalArgumentException if there is no property
	 *                                  of the specified name
	 * @throws IllegalArgumentException if the specified property
	 *                                  exists, but is not mapped
	 */
	public void set(String name, String key, Object value)
	{
		// If the 'mapped' property doesn't exist, then add it
		if (!isDynamicProperty(name)) set(name, defaultMappedProperty(name));

		// Get the mapped property
		Object mappedProperty = get(name);

		// Check that the property is mapped
		if (!dynamicClass.getDynamicProperty(name).isMapped()) throw new IllegalArgumentException("Non-mapped property for '" + name + "(" + key + ")'" + dynamicClass.getDynamicProperty(name).getType().getName());

		// Set the value in the Map
		((Map) mappedProperty).put(key, value);
	}


	protected Object growIndexedProperty(String name, Object indexedProperty, int index)
	{
		// Grow a List to the appropriate size
		if (indexedProperty instanceof List)
		{

			List list = (List) indexedProperty;
			while (index >= list.size())
			{
				list.add(null);
			}

		}

		// Grow an Array to the appropriate size
		if ((indexedProperty.getClass().isArray()))
		{

			int length = Array.getLength(indexedProperty);
			if (index >= length)
			{
				Class componentType = indexedProperty.getClass().getComponentType();
				Object newArray = Array.newInstance(componentType, (index + 1));
				System.arraycopy(indexedProperty, 0, newArray, 0, length);
				indexedProperty = newArray;
				set(name, indexedProperty);
				int newLength = Array.getLength(indexedProperty);
				for (int i = length; i < newLength; i++)
				{
					Array.set(indexedProperty, i, createProperty(name + "[" + i + "]", componentType));
				}
			}
		}

		return indexedProperty;
	}

	/**
	 * Create a new Instance of a Property
	 */
	protected Object createProperty(String name, Class type)
	{
		// Create Lists, arrays or DynamicBeans
		if (type.isArray() || List.class.isAssignableFrom(type))
		{
			return createIndexedProperty(name, type);
		}

		if (Map.class.isAssignableFrom(type))
		{
			return createMappedProperty(name, type);
		}

		if (DynamicBean.class.isAssignableFrom(type))
		{
			return createDynamicBeanProperty(name, type);
		}

		if (type.isPrimitive())
		{
			return createPrimitiveProperty(name, type);
		}

		if (Number.class.isAssignableFrom(type))
		{
			return createNumberProperty(name, type);
		}

		return createOtherProperty(name, type);
	}

	/**
	 * Create a new Instance of an 'Indexed' Property
	 */
	protected Object createIndexedProperty(String name, Class type)
	{
		// Create the indexed object
		Object indexedProperty = null;

		if (type == null)
		{
			indexedProperty = defaultIndexedProperty(name);
		}
		else if (type.isArray())
		{
			indexedProperty = Array.newInstance(type.getComponentType(), 0);
		}
		else if (List.class.isAssignableFrom(type))
		{
			if (type.isInterface())
			{
				indexedProperty = defaultIndexedProperty(name);
			}
			else
			{
				try
				{
					indexedProperty = type.newInstance();
				}
				catch (Exception ex)
				{
					throw new IllegalArgumentException("Error instantiating indexed property of type '" + type.getName() + "' for '" + name + "' " + ex);
				}
			}
		}
		else
		{

			throw new IllegalArgumentException("Non-indexed property of type '" + type.getName() + "' for '" + name + "'");
		}

		return indexedProperty;
	}

	/**
	 * Create a new Instance of a 'Mapped' Property
	 */
	protected Object createMappedProperty(String name, Class type)
	{
		// Create the mapped object
		Object mappedProperty = null;

		if (type == null)
		{

			mappedProperty = defaultMappedProperty(name);

		}
		else if (type.isInterface())
		{

			mappedProperty = defaultMappedProperty(name);

		}
		else if (Map.class.isAssignableFrom(type))
		{
			try
			{
				mappedProperty = type.newInstance();
			}
			catch (Exception ex)
			{
				throw new IllegalArgumentException("Error instantiating mapped property of type '" + type.getName() + "' for '" + name + "' " + ex);
			}
		}
		else
		{

			throw new IllegalArgumentException("Non-mapped property of type '" + type.getName() + "' for '" + name + "'");
		}

		return mappedProperty;
	}

	/**
	 * Create a new Instance of a 'Mapped' Property
	 */
	protected Object createDynamicBeanProperty(String name, Class type)
	{
		try
		{
			return type.newInstance();
		}
		catch (Exception ex)
		{
			if (logger.isWarnEnabled())
			{
				logger.warn("Error instantiating DynamicBean property of type '" + type.getName() + "' for '" + name + "' " + ex);
			}
			return null;
		}
	}

	/**
	 * Create a new Instance of a 'Primitive' Property
	 */
	protected Object createPrimitiveProperty(String name, Class type)
	{
		if (type == Boolean.TYPE) return Boolean.FALSE;
		else if (type == Integer.TYPE) return Integer_ZERO;
		else if (type == Long.TYPE) return Long_ZERO;
		else if (type == Double.TYPE) return Double_ZERO;
		else if (type == Float.TYPE) return Float_ZERO;
		else if (type == Byte.TYPE) return Byte_ZERO;
		else if (type == Short.TYPE) return Short_ZERO;
		else if (type == Character.TYPE) return Character_SPACE;
		else return null;
	}

	/**
	 * Create a new Instance of a 'Primitive' Property
	 */
	protected Object createNumberProperty(String name, Class type)
	{
		return null;
	}

	/**
	 * Create a new Instance of a 'Mapped' Property
	 */
	protected Object createOtherProperty(String name, Class type)
	{
		if (type == String.class || type == Boolean.class || type == Character.class || Date.class.isAssignableFrom(type)) return null;

		try
		{
			return type.newInstance();
		}
		catch (Exception ex)
		{
			logger.warn("Error instantiating property of type '" + type.getName() + "' for '" + name + "' " + ex);

			return null;
		}
	}

	/**
	 * <p>Creates a new <code>ArrayList</code> for an 'indexed' property
	 * which doesn't exist.</p>
	 * <p/>
	 * <p>This method shouls be overriden if an alternative <code>List</code>
	 * or <code>Array</code> implementation is required for 'indexed' properties.</p>
	 *
	 * @param name Name of the 'indexed property.
	 */
	protected Object defaultIndexedProperty(String name)
	{
		return new ArrayList();
	}

	/**
	 * <p>Creates a new <code>HashMap</code> for a 'mapped' property
	 * which doesn't exist.</p>
	 * <p/>
	 * <p>This method can be overriden if an alternative <code>Map</code>
	 * implementation is required for 'mapped' properties.</p>
	 *
	 * @param name Name of the 'mapped property.
	 */
	protected Map defaultMappedProperty(String name)
	{
		return new HashMap();
	}

	/**
	 * Indicates if there is a property with the specified name.
	 */
	protected boolean isDynamicProperty(String name)
	{
		if (name == null) throw new IllegalArgumentException("No property name specified");

		// Handle SlowDynamicClasses
		if (dynamicClass instanceof SlowDynamicClass) return ((SlowDynamicClass) dynamicClass).isDynamicProperty(name);

		// Handle other MutableDynamicClass
		return dynamicClass.getDynamicProperty(name) == null ? false : true;
	}

	/**
	 * Is an object of the source class assignable to the destination class?
	 *
	 * @param dest   Destination class
	 * @param source Source class
	 */
	protected boolean isAssignable(Class dest, Class source)
	{

		if (dest.isAssignableFrom(source) ||
				((dest == Boolean.TYPE) && (source == Boolean.class)) ||
				((dest == Byte.TYPE) && (source == Byte.class)) ||
				((dest == Character.TYPE) && (source == Character.class)) ||
				((dest == Double.TYPE) && (source == Double.class)) ||
				((dest == Float.TYPE) && (source == Float.class)) ||
				((dest == Integer.TYPE) && (source == Integer.class)) ||
				((dest == Long.TYPE) && (source == Long.class)) ||
				((dest == Short.TYPE) && (source == Short.class))) return (true);
			else return (false);
	}

	/**
	 * <p>Creates a new instance of the <code>Map</code>.</p>
	 */
	protected Map newMap()
	{
		return new HashMap();
	}

}
