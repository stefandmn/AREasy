package org.areasy.common.parser.html.utilities;

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

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Vector;

/**
 * @version $Id: ChainedException.java,v 1.1 2008/05/25 17:26:04 swd\stefan.damian Exp $
 */
public class ChainedException extends Exception
{

    protected Throwable throwable;

    public ChainedException()
    {
    }

    public ChainedException(String message)
    {
        super(message);
    }

    public ChainedException(Throwable throwable)
    {
        this.throwable = throwable;
    }

    public ChainedException(String message, Throwable throwable)
    {
        super(message);
        this.throwable = throwable;
    }

    public String[] getMessageChain()
    {
        Vector list = getMessageList();
        String[] chain = new String[list.size()];
        list.copyInto(chain);
        return chain;
    }

    public Vector getMessageList()
    {
        Vector list = new Vector();
        list.addElement(getMessage());
        if(throwable != null)
        {
            if(throwable instanceof ChainedException)
            {
                ChainedException chain = (ChainedException) throwable;
                Vector sublist = chain.getMessageList();
                for(int i = 0; i < sublist.size(); i++)
                {
                    list.addElement(sublist.elementAt(i));
                }
            }
            else
            {
                String message = throwable.getMessage();
                if(message != null && !message.equals(""))
                {
                    list.addElement(message);
                }
            }
        }
        return list;
    }

    public Throwable getThrowable()
    {
        return throwable;
    }

    public void printStackTrace()
    {
        printStackTrace(System.err);
    }

    public void printStackTrace(PrintStream out)
    {
        synchronized(out)
        {
            if(throwable != null)
            {
                out.println(getClass().getName() + ": " + getMessage() + ";");
                throwable.printStackTrace(out);
            }
            else
            {
                super.printStackTrace(out);
            }
        }
    }

    public void printStackTrace(PrintWriter out)
    {
        synchronized(out)
        {
            if(throwable != null)
            {
                out.println(getClass().getName() + ": " + getMessage() + ";");
                throwable.printStackTrace(out);
            }
            else super.printStackTrace(out);
        }
    }
}

