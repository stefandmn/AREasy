package org.areasy.common.errors;

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
import java.io.Serializable;
import java.io.StringWriter;
import java.util.*;

/**
 * <p>A shared implementation of the nestable exception functionality.</p>
 * <p/>
 * The code is shared between
 * {@link org.areasy.common.errors.NestableError NestableError},
 * {@link org.areasy.common.errors.NestableException NestableException} and
 * {@link org.areasy.common.errors.NestableRuntimeException NestableRuntimeException}.
 * </p>
 *
 * @version $Id: NestableDelegate.java,v 1.2 2008/05/14 09:32:32 swd\stefan.damian Exp $
 */
public class NestableDelegate implements Serializable
{

	/**
	 * Constructor error message.
	 */
	private transient static final String MUST_BE_THROWABLE = "The Nestable implementation passed to the NestableDelegate(Nestable) constructor must extend java.lang.Throwable";

	/**
	 * Holds the reference to the exception or error that we're
	 * wrapping (which must be a {@link
	 * org.areasy.common.data.language.exception.Nestable} implementation).
	 */
	private Throwable nestable = null;

	/**
	 * Whether to print the stack trace top-down.
	 * This public flag may be set by calling code, typically in initialisation.
	 * This exists for backwards compatability, setting it to false will return
	 * the library to v1.0 behaviour (but will affect all users of the library
	 * in the classloader).
	 */
	public static boolean topDown = true;

	/**
	 * Whether to trim the repeated stack trace.
	 * This public flag may be set by calling code, typically in initialisation.
	 * This exists for backwards compatability, setting it to false will return
	 * the library to v1.0 behaviour (but will affect all users of the library
	 * in the classloader).
	 */
	public static boolean trimStackFrames = true;

	/**
	 * Whether to match subclasses via indexOf.
	 * This public flag may be set by calling code, typically in initialisation.
	 * This exists for backwards compatability, setting it to false will return
	 * the library to v2.0 behaviour (but will affect all users of the library
	 * in the classloader).
	 */
	public static boolean matchSubclasses = true;

	/**
	 * Constructs a new <code>NestableDelegate</code> instance to manage the
	 * specified <code>Nestable</code>.
	 *
	 * @param nestable the Nestable implementation (<i>must</i> extend
	 *                 {@link java.lang.Throwable})
	 */
	public NestableDelegate(Nestable nestable)
	{
		if (nestable instanceof Throwable) this.nestable = (Throwable) nestable;
			else throw new IllegalArgumentException(MUST_BE_THROWABLE);

	}

	/**
	 * Returns the error message of the <code>Throwable</code> in the chain
	 * of <code>Throwable</code>s at the specified index, numbered from 0.
	 *
	 * @param index the index of the <code>Throwable</code> in the chain of
	 *              <code>Throwable</code>s
	 * @return the error message, or null if the <code>Throwable</code> at the
	 *         specified index in the chain does not contain a message
	 * @throws IndexOutOfBoundsException if the <code>index</code> argument is
	 *                                   negative or not less than the count of <code>Throwable</code>s in the
	 *                                   chain
	 */
	public String getMessage(int index)
	{
		Throwable t = this.getThrowable(index);

		if (Nestable.class.isInstance(t)) return ((Nestable) t).getMessage(0);
			else return t.getMessage();
	}

	/**
	 * Returns the full message contained by the <code>Nestable</code>
	 * and any nested <code>Throwable</code>s.
	 *
	 * @param baseMsg the base message to use when creating the full
	 *                message. Should be generally be called via
	 *                <code>nestableHelper.getMessage(super.getMessage())</code>,
	 *                where <code>super</code> is an instance of {@link
	 *                java.lang.Throwable}.
	 * @return The concatenated message for this and all nested
	 *         <code>Throwable</code>s
	 */
	public String getMessage(String baseMsg)
	{
		StringBuffer msg = new StringBuffer();
		if (baseMsg != null) msg.append(baseMsg);

		Throwable nestedCause = ExceptionUtility.getCause(this.nestable);
		if (nestedCause != null)
		{
			String causeMsg = nestedCause.getMessage();
			if (causeMsg != null)
			{
				if (baseMsg != null) msg.append(": ");
				msg.append(causeMsg);
			}

		}

		return msg.length() > 0 ? msg.toString() : null;
	}

	/**
	 * Returns the error message of this and any nested <code>Throwable</code>s
	 * in an array of Strings, one element for each message. Any
	 * <code>Throwable</code> not containing a message is represented in the
	 * array by a null. This has the effect of cause the length of the returned
	 * array to be equal to the result of the {@link #getThrowableCount()}
	 * operation.
	 *
	 * @return the error messages
	 */
	public String[] getMessages()
	{
		Throwable[] throwables = this.getThrowables();
		String[] msgs = new String[throwables.length];
		for (int i = 0; i < throwables.length; i++)
		{
			msgs[i] = (Nestable.class.isInstance(throwables[i])
					? ((Nestable) throwables[i]).getMessage(0)
					: throwables[i].getMessage());
		}

		return msgs;
	}

	/**
	 * Returns the <code>Throwable</code> in the chain of
	 * <code>Throwable</code>s at the specified index, numbered from 0.
	 *
	 * @param index the index, numbered from 0, of the <code>Throwable</code> in
	 *              the chain of <code>Throwable</code>s
	 * @return the <code>Throwable</code>
	 * @throws IndexOutOfBoundsException if the <code>index</code> argument is
	 *                                   negative or not less than the count of <code>Throwable</code>s in the
	 *                                   chain
	 */
	public Throwable getThrowable(int index)
	{
		if (index == 0) return this.nestable;

		Throwable[] throwables = this.getThrowables();
		return throwables[index];
	}

	/**
	 * Returns the number of <code>Throwable</code>s contained in the
	 * <code>Nestable</code> contained by this delegate.
	 *
	 * @return the throwable count
	 */
	public int getThrowableCount()
	{
		return ExceptionUtility.getThrowableCount(this.nestable);
	}

	/**
	 * Returns this delegate's <code>Nestable</code> and any nested
	 * <code>Throwable</code>s in an array of <code>Throwable</code>s, one
	 * element for each <code>Throwable</code>.
	 *
	 * @return the <code>Throwable</code>s
	 */
	public Throwable[] getThrowables()
	{
		return ExceptionUtility.getThrowables(this.nestable);
	}

	/**
	 * Returns the index, numbered from 0, of the first <code>Throwable</code>
	 * that matches the specified type, or a subclass, in the chain of <code>Throwable</code>s
	 * with an index greater than or equal to the specified index.
	 * The method returns -1 if the specified type is not found in the chain.
	 * <p/>
	 * NOTE: From v2.1, we have clarified the <code>Nestable</code> interface
	 * such that this method matches subclasses.
	 * If you want to NOT match subclasses, please use
	 * {@link org.areasy.common.errors.ExceptionUtility#indexOfThrowable(Throwable, Class, int)}
	 * (which is avaiable in all versions of lang).
	 * An alternative is to use the public static flag {@link #matchSubclasses}
	 * on <code>NestableDelegate</code>, however this is not recommended.
	 *
	 * @param type      the type to find, subclasses match, null returns -1
	 * @param fromIndex the index, numbered from 0, of the starting position in
	 *                  the chain to be searched
	 * @return index of the first occurrence of the type in the chain, or -1 if
	 *         the type is not found
	 * @throws IndexOutOfBoundsException if the <code>fromIndex</code> argument
	 *                                   is negative or not less than the count of <code>Throwable</code>s in the
	 *                                   chain
	 */
	public int indexOfThrowable(Class type, int fromIndex)
	{
		if (type == null) return -1;
		if (fromIndex < 0) throw new IndexOutOfBoundsException("The start index was out of bounds: " + fromIndex);

		Throwable[] throwables = ExceptionUtility.getThrowables(this.nestable);
		if (fromIndex >= throwables.length)
		{
			throw new IndexOutOfBoundsException("The start index was out of bounds: "
					+ fromIndex + " >= " + throwables.length);
		}
		if (matchSubclasses)
		{
			for (int i = fromIndex; i < throwables.length; i++)
			{
				if (type.isAssignableFrom(throwables[i].getClass())) return i;
			}
		}
		else
		{
			for (int i = fromIndex; i < throwables.length; i++)
			{
				if (type.equals(throwables[i].getClass())) return i;
			}
		}

		return -1;
	}

	/**
	 * Prints the stack trace of this exception the the standar error
	 * stream.
	 */
	public void printStackTrace()
	{
		printStackTrace(System.err);
	}

	/**
	 * Prints the stack trace of this exception to the specified
	 * stream.
	 *
	 * @param out <code>PrintStream</code> to use for output.
	 * @see #printStackTrace(PrintWriter)
	 */
	public void printStackTrace(PrintStream out)
	{
		synchronized (out)
		{
			PrintWriter pw = new PrintWriter(out, false);
			printStackTrace(pw);
			// Flush the PrintWriter before it's GC'ed.
			pw.flush();
		}
	}

	/**
	 * Prints the stack trace of this exception to the specified
	 * writer. If the Throwable class has a <code>getCause</code>
	 * method (i.e. running on jre1.4 or higher), this method just
	 * uses Throwable's printStackTrace() method. Otherwise, generates
	 * the stack-trace, by taking into account the 'topDown' and
	 * 'trimStackFrames' parameters. The topDown and trimStackFrames
	 * are set to 'true' by default (produces jre1.4-like stack trace).
	 *
	 * @param out <code>PrintWriter</code> to use for output.
	 */
	public void printStackTrace(PrintWriter out)
	{
		Throwable throwable = this.nestable;

		// if running on jre1.4 or higher, use default printStackTrace
		if (ExceptionUtility.isThrowableNested())
		{
			if (throwable instanceof Nestable) ((Nestable) throwable).printPartialStackTrace(out);
				else throwable.printStackTrace(out);

			return;
		}

		// generating the nested stack trace
		List stacks = new ArrayList();
		while (throwable != null)
		{
			String[] st = getStackFrames(throwable);
			stacks.add(st);
			throwable = ExceptionUtility.getCause(throwable);
		}

		// If NOT topDown, reverse the stack
		String separatorLine = "Caused by: ";
		if (!topDown)
		{
			separatorLine = "Rethrown as: ";
			Collections.reverse(stacks);
		}

		// Remove the repeated lines in the stack
		if (trimStackFrames)
		{
			trimStackFrames(stacks);
		}

		synchronized (out)
		{
			for (Iterator iter = stacks.iterator(); iter.hasNext();)
			{
				String[] st = (String[]) iter.next();
				for (int i = 0, len = st.length; i < len; i++)
				{
					out.println(st[i]);
				}

				if (iter.hasNext()) out.print(separatorLine);
			}
		}
	}

	/**
	 * Captures the stack trace associated with the specified
	 * <code>Throwable</code> object, decomposing it into a list of
	 * stack frames.
	 *
	 * @param t The <code>Throwable</code>.
	 * @return An array of strings describing each stack frame.
	 */
	protected String[] getStackFrames(Throwable t)
	{
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw, true);

		// Avoid infinite loop between decompose() and printStackTrace().
		if (t instanceof Nestable) ((Nestable) t).printPartialStackTrace(pw);
			else t.printStackTrace(pw);

		return ExceptionUtility.getStackFrames(sw.getBuffer().toString());
	}

	/**
	 * Trims the stack frames. The first set is left untouched. The rest
	 * of the frames are truncated from the bottom by comparing with
	 * one just on top.
	 *
	 * @param stacks The list containing String[] elements
	 */
	protected void trimStackFrames(List stacks)
	{
		for (int size = stacks.size(), i = size - 1; i > 0; i--)
		{
			String[] curr = (String[]) stacks.get(i);
			String[] next = (String[]) stacks.get(i - 1);

			List currList = new ArrayList(Arrays.asList(curr));
			List nextList = new ArrayList(Arrays.asList(next));
			ExceptionUtility.removeCommonFrames(currList, nextList);

			int trimmed = curr.length - currList.size();

			if (trimmed > 0)
			{
				currList.add("\t... " + trimmed + " more");
				stacks.set(i, currList.toArray(new String[currList.size()]));
			}
		}
	}
}
