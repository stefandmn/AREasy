package org.areasy.common.parser.html.utilities;

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

import org.areasy.common.parser.html.utilities.sort.Ordered;

import java.io.Serializable;

/**
 * Structure to hold a character and it's equivalent entity reference kernel.
 * For the character reference &amp;copy; the character would be '&copy;' and
 * the kernel would be "copy", for example.<p>
 * Character references are described at <a href="Character references">http://www.w3.org/TR/REC-html40/charset.html#entities</a>
 * Supports the Ordered interface so it's easy to create a list sorted by
 * kernel, to perform binary searches on.<p>
 *
 * @version $Id: CharacterReference.java,v 1.1 2008/05/25 17:26:04 swd\stefan.damian Exp $
 */
public class CharacterReference implements Serializable, Cloneable, Ordered
{

    /**
     * The character value as an integer.
     */
    protected int mCharacter;

    /**
     * This entity reference kernel.
     * The text between the ampersand and the semicolon.
     */
    protected String mKernel;

    /**
     * Construct a <code>CharacterReference</code> with the character and kernel given.
     *
     * @param kernel    The kernel in the equivalent character entity reference.
     * @param character The character needing encoding.
     */
    public CharacterReference(String kernel, int character)
    {
        mKernel = kernel;
        mCharacter = character;
        if(null == mKernel)
        {
            mKernel = "";
        }
    }

    /**
     * Get this CharacterReference's kernel.
     *
     * @return The kernel in the equivalent character entity reference.
     */
    public String getKernel()
    {
        return (mKernel);
    }

    /**
     * Set this CharacterReference's kernel.
     * This is used to avoid creating a new object to perform a binary search.
     *
     * @param kernel The kernel in the equivalent character entity reference.
     */
    void setKernel(String kernel)
    {
        mKernel = kernel;
    }

    /**
     * Get the character needing translation.
     *
     * @return The character.
     */
    public int getCharacter()
    {
        return (mCharacter);
    }

    /**
     * Set the character.
     * This is used to avoid creating a new object to perform a binary search.
     *
     * @param character The character needing translation.
     */
    void setCharacter(int character)
    {
        mCharacter = character;
    }

    /**
     * Visualize this character reference as a string.
     *
     * @return A string with the character and kernel.
     */
    public String toString()
    {
        String hex;
        StringBuffer ret;

        ret = new StringBuffer(6 + 8 + 2); // max 8 in string
        hex = Integer.toHexString((int) getCharacter());
        ret.append("\\u");
        for(int i = hex.length(); i < 4; i++)
        {
            ret.append("0");
        }
        ret.append(hex);
        ret.append("[");
        ret.append(getKernel());
        ret.append("]");

        return (ret.toString());
    }

    //
    // Ordered interface
    //

    /**
     * Compare one reference to another.
     *
     * @see org.areasy.common.parser.html.utilities.sort.Ordered
     */
    public int compare(Object that)
    {
        CharacterReference r;

        r = (CharacterReference) that;

        return (getKernel().compareTo(r.getKernel()));
    }
}

