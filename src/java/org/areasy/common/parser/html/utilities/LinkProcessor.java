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

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Processor class for links, is present basically as a utility class.
 *
 * @version $Id: LinkProcessor.java,v 1.1 2008/05/25 17:26:04 swd\stefan.damian Exp $
 */
public class LinkProcessor implements Serializable
{
    /**
     * Overriding base URL.
     * If set, this is used instead of a provided base URL in extract().
     */
    private String baseUrl;

    /**
     * Create an HTMLLinkProcessor.
     */
    public LinkProcessor()
    {
        baseUrl = null;
    }

    /**
     * Create an absolute URL from a possibly relative link and a base URL.
     *
     * @param link The reslative portion of a URL.
     * @param base The base URL unless overridden by the current baseURL property.
     * @return The fully qualified URL or the original link if a failure occured.
     */
    public String extract(String link, String base)
    {
        String ret;

        try
        {
            if(null == link) link = "";
            	else link = stripQuotes(link);

            if(null != getBaseUrl()) base = getBaseUrl();

            if((null == base) || ("".equals(link))) ret = link;
            else
            {
                URL url = constructUrl(link, base);
                ret = url.toExternalForm();
            }
        }
        catch(MalformedURLException murle)
        {
            ret = link;
        }

        // avoid bug #995744 Translate.decode(String)
        // don't apply translation to URLs
        return (ret);
    }

    /**
     * Remove double or single quotes from the string.
     */
    public String stripQuotes(String string)
    {
        //remove any double quotes from around charset string
        if(string.startsWith("\"") && string.endsWith("\"") && (1 < string.length())) string = string.substring(1, string.length() - 1);

        //remove any single quote from around charset string
        if(string.startsWith("'") && string.endsWith("'") && (1 < string.length())) string = string.substring(1, string.length() - 1);

        return (string);
    }

    public URL constructUrl(String link, String base) throws MalformedURLException
    {
        String path;
		
		boolean modified;
        boolean absolute;
        int index;

		URL url; // constructed URL combining relative link and base
        url = new URL(new URL(base), link);
        path = url.getFile();
        modified = false;
        absolute = link.startsWith("/");

        if(!absolute)
        {   // we prefer to fix incorrect relative links
            // this doesn't fix them all, just the ones at the start
            while(path.startsWith("/."))
            {
                if(path.startsWith("/../"))
                {
                    path = path.substring(3);
                    modified = true;
                }
                else
                {
                    if(path.startsWith("/./") || path.startsWith("/."))
                    {
                        path = path.substring(2);
                        modified = true;
                    }
                    else break;
                }
            }
        }
        // fix backslashes
        while(-1 != (index = path.indexOf("/\\")))
        {
            path = path.substring(0, index + 1) + path.substring(index + 2);
            modified = true;
        }

        if(modified) url = new URL(url, path);

        return url;
    }

    /**
     * Turn spaces into %20.
     *
     * @param url The url containing spaces.
     * @return The URL with spaces as %20 sequences.
     */
    public static String fixSpaces(String url)
    {
        int index;
        int length;
        char ch;
        StringBuffer returnURL;

        index = url.indexOf(' ');
        if(-1 != index)
        {
            length = url.length();
            returnURL = new StringBuffer(length * 3);
            returnURL.append(url.substring(0, index));

            for(int i = index; i < length; i++)
            {
                ch = url.charAt(i);
                if(ch == ' ') returnURL.append("%20");
                	else returnURL.append(ch);
            }

            url = returnURL.toString();
        }

        return (url);
    }

    /**
     * Check if a resource is a valid URL.
     *
     * @param resourceLocn The resource to test.
     * @return <code>true</code> if the resource is a valid URL.
     */
    public static boolean isURL(String resourceLocn)
    {
        boolean ret;

        try
        {
            new URL(resourceLocn);
            ret = true;
        }
        catch(MalformedURLException murle)
        {
            ret = false;
        }

        return (ret);
    }

    /**
     * Returns the baseUrl.
     *
     * @return String
     */
    public String getBaseUrl()
    {
        return baseUrl;
    }

    /**
     * Sets the baseUrl.
     *
     * @param baseUrl The baseUrl to set
     */
    public void setBaseUrl(String baseUrl)
    {
        this.baseUrl = baseUrl;
    }

    public static String removeLastSlash(String baseUrl)
    {
        if(baseUrl.charAt(baseUrl.length() - 1) == '/') return baseUrl.substring(0, baseUrl.length() - 1);
        	else return baseUrl;
    }

}
