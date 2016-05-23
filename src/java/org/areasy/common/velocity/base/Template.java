package org.areasy.common.velocity.base;

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

import org.areasy.common.velocity.context.Context;
import org.areasy.common.velocity.context.DefaultInternalContextAdapter;
import org.areasy.common.velocity.context.VelocityContext;
import org.areasy.common.velocity.runtime.parser.ParseErrorException;
import org.areasy.common.velocity.runtime.parser.ParseException;
import org.areasy.common.velocity.runtime.parser.node.SimpleNode;
import org.areasy.common.velocity.runtime.resource.Resource;

import java.io.*;

/**
 * This class is used for controlling all template
 * operations. This class uses a parser created
 * by JavaCC to create an AST that is subsequently
 * traversed by a Visitor.
 * <p/>
 * <pre>
 * Template template = Velocity.getTemplate("test.vm");
 * Context context = new VelocityContext();
 * <p/>
 * context.put("foo", "bar");
 * context.put("customer", new Customer());
 * <p/>
 * template.merge(context, writer);
 * </pre>
 *
 * @version $Id: Template.java,v 1.1 2008/05/25 22:33:17 swd\stefan.damian Exp $
 */
public class Template extends Resource
{
	private Exception errorCondition = null;

	/**
	 * Default constructor
	 */
	public Template()
	{
		//nothing to do now.
	}

	/**
	 * gets the named resource as a stream, parses and inits
	 *
	 * @return true if successful
	 * @throws ResourceNotFoundException if template not found
	 *                                   from any available source.
	 * @throws org.areasy.common.velocity.runtime.parser.ParseErrorException       if template cannot be parsed due
	 *                                   to syntax (or other) error.
	 * @throws Exception                 some other problem, should only be from
	 *                                   initialization of the template AST.
	 */
	public boolean process() throws ResourceNotFoundException, ParseErrorException, Exception
	{
		data = null;
		InputStream is = null;
		errorCondition = null;

		try
		{
			is = resourceLoader.getResourceStream(name);
		}
		catch (ResourceNotFoundException rnfe)
		{
			errorCondition = rnfe;
			throw rnfe;
		}

		if (is != null)
		{
			try
			{
				BufferedReader br = new BufferedReader(new InputStreamReader(is, encoding));

				data = runtime.parse(br, name);
				initDocument();

				return true;
			}
			catch (UnsupportedEncodingException uce)
			{
				String msg = "Unsupported input encoding : " + encoding + " for template " + name;

				errorCondition = new ParseErrorException(msg);
				throw errorCondition;
			}
			catch (ParseException pex)
			{
				errorCondition = new ParseErrorException(pex.getMessage());
				throw errorCondition;
			}
			catch (Exception e)
			{
				errorCondition = e;
				throw e;
			}
			finally
			{
				is.close();
			}
		}
		else
		{
			errorCondition = new ResourceNotFoundException("Unknown resource error for resource " + name);
			throw errorCondition;
		}
	}

	/**
	 * initializes the document.  init() is not longer
	 * dependant upon context, but we need to let the
	 * init() carry the template name down throught for VM
	 * namespace features
	 */
	public void initDocument() throws Exception
	{
		DefaultInternalContextAdapter ica = new DefaultInternalContextAdapter(new VelocityContext());

		try
		{
			ica.pushCurrentTemplateName(name);
			((SimpleNode) data).init(ica, runtime);
		}
		finally
		{
			ica.popCurrentTemplateName();
		}

	}

	/**
	 * The AST node structure is merged with the
	 * context to produce the final output.
	 * <p/>
	 * Throws IOException if failure is due to a file related
	 * issue, and Exception otherwise
	 *
	 * @param context Conext with data elements accessed by template
	 * @param writer  output writer for rendered template
	 * @throws ResourceNotFoundException if template not found
	 *                                   from any available source.
	 * @throws ParseErrorException       if template cannot be parsed due
	 *                                   to syntax (or other) error.
	 * @throws Exception                 anything else.
	 */
	public void merge(Context context, Writer writer) throws ResourceNotFoundException, ParseErrorException, MethodInvocationException, Exception
	{
		if (errorCondition != null) throw errorCondition;

		if (data != null)
		{
			DefaultInternalContextAdapter ica = new DefaultInternalContextAdapter(context);

			try
			{
				ica.pushCurrentTemplateName(name);
				ica.setCurrentResource(this);

				((SimpleNode) data).render(ica, writer);
			}
			finally
			{
				ica.popCurrentTemplateName();
				ica.setCurrentResource(null);
			}
		}
		else throw new Exception("Template.merge() failure. The document is null, most likely due to parsing error.");
	}
}


