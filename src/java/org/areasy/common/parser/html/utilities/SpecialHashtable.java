package org.areasy.common.parser.html.utilities;

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

import java.util.Hashtable;

/**
 * Acts like a regular HashTable, except some values are translated in get(String).
 * Specifically, <code>Tag.NULLVALUE</code> is translated to <code>null</code> and
 * <code>Tag.NOTHING</code> is translated to <code>""</code>.
 * This is done for backwards compatibility, users are expecting a HashTable,
 * but Tag.toHTML needs to know when there is no attribute value (&lt;<TAG ATTRIBUTE&gt;)
 * and when the value was not present (&lt;<TAG ATTRIBUTE=&gt;).
 *
 * @version $Id: SpecialHashtable.java,v 1.1 2008/05/25 17:26:04 swd\stefan.damian Exp $
 */
public class SpecialHashtable extends Hashtable
{

    /**
     * Special key for the tag name.
     */
    public final static String TAGNAME = "$<TAGNAME>$";

    /**
     * Special value for a null attribute value.
     */
    public final static String NULLVALUE = "$<NULL>$";

    /**
     * Special value for an empty attribute value.
     */
    public final static String NOTHING = "$<NOTHING>$";

    /**
     * Constructs a new, empty hashtable with a default initial capacity (11)
     * and load factor, which is 0.75.
     */
    public SpecialHashtable()
    {
        super();
    }

    /**
     * Constructs a new, empty hashtable with the specified initial capacity
     * and default load factor, which is 0.75.
     */
    public SpecialHashtable(int initialCapacity)
    {
        super(initialCapacity);
    }

    /**
     * Constructs a new, empty hashtable with the specified initial capacity
     * and the specified load factor.
     */
    public SpecialHashtable(int initialCapacity, float loadFactor)
    {
        super(initialCapacity, loadFactor);
    }

    /**
     * Returns the value to which the specified key is mapped in this hashtable.
     * This is translated to provide backwards compatibility.
     *
     * @return The translated value of the attribute. <em>This will be
     *         <code>null</code> if the attribute is a stand-alone attribute.</em>
     */
    public Object get(Object key)
    {
        Object ret;

        ret = getRaw(key);
        if(NULLVALUE == ret)
        {
            ret = null;
        }
        else
        {
            if(NOTHING == ret)
            {
                ret = "";
            }
        }

        return (ret);
    }

    /**
     * Returns the raw (untranslated) value to which the specified key is
     * mapped in this hashtable.
     */
    public Object getRaw(Object key)
    {
        return (super.get(key));
    }
}
