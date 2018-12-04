package org.areasy.common.doclet.document;

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

import com.lowagie.text.Chunk;
import com.lowagie.text.Font;
import com.lowagie.text.Phrase;
import com.sun.javadoc.ConstructorDoc;
import com.sun.javadoc.ExecutableMemberDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.ProgramElementDoc;
import org.areasy.common.data.StringUtility;
import org.areasy.common.doclet.AbstractConfiguration;
import org.areasy.common.doclet.utilities.PDFUtility;
import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Properties;
import java.util.zip.CRC32;


/**
 * This class keeps a list of valid destinations for internal
 * links to avoid problems when the PDF is created.
 *
 * @version $Id: Destinations.java,v 1.4 2008/05/14 09:36:47 swd\stefan.damian Exp $
 */
public class Destinations implements AbstractConfiguration
{
	/**
	 * Logger reference
	 */
	private static Logger log = LoggerFactory.getLog(Destinations.class);

	/**
	 * Stores the names of all valid destinations.
	 */
	private static Properties destinations = new Properties();

	/**
	 * Stores the names of the .html files that we process
	 */
	private static HashSet destinationFiles = new HashSet();

	/**
	 * Adds a file that can be linked to with an HTML anchor
	 * tag.  Generally these are package.html files, class files,
	 * overview, title page, and appendicies.
	 */
	public static void addValidDestinationFile(File aFile)
	{
		if (aFile != null && aFile.exists() && aFile.isFile())
		{
			try
			{
				destinationFiles.add(aFile.getCanonicalPath());
			}
			catch (IOException e)
			{
				log.debug("Error adding destination file " + aFile, e);
			}
		}
	}

	/**
	 * Checks to see if the given file is a valid destination for an
	 * HTML anchor.
	 */
	public static boolean isValidDestinationFile(File aFile)
	{
		if (aFile == null)
		{
			return false;
		}
		try
		{
			return destinationFiles.contains(aFile.getCanonicalPath());
		}
		catch (IOException e)
		{
			log.debug("Error checking destination file " + aFile, e);
			return false;
		}
	}

	/**
	 * Adds a valid destination to the list.
	 *
	 * @param destination The valid destination.
	 */
	public static void addValidDestination(String destination)
	{
		destinations.setProperty(destination, "x");
	}

	/**
	 * Verifies if a given destination is valid.
	 *
	 * @param destination The local destination to check.
	 * @return True if the destination is valid, false if not.
	 * @throws IllegalArgumentException If the argument is null.
	 */
	public static boolean isValid(String destination)
	{
		if (destination == null) throw new IllegalArgumentException("Null destination not allowed!");

		if (destinations.get(destination) != null) return true;

		return false;
	}

	/**
	 * Return a destination based on an HTML anchor name and a filename
	 * (we do this in case the same anchor name occurs in more than one
	 * included file).
	 */
	public static String createAnchorDestination(File file, String htmlAnchor)
	{
		String fileHash = "";

		if (file != null)
		{
			String filePath;
			
			try
			{
				filePath = file.getCanonicalPath();
			}
			catch (IOException e)
			{
				filePath = file.getAbsolutePath();
			}

			CRC32 crc = new CRC32();
			crc.update(filePath.getBytes());
			fileHash = Long.toHexString(crc.getValue());
		}

		if (htmlAnchor == null) htmlAnchor = "";

		return "_LOCAL:" + fileHash + ":" + StringUtility.variable(htmlAnchor);
	}

	/**
	 * Creates a destination in the document. This method
	 * does nothing if the given destination is invalid.
	 * <p/>
	 * This method creates a phrase with one or two empty chunks
	 * who only serve as holders for the destinations.
	 *
	 * @param label The label for the destination.
	 * @param doc   The javadoc element for which to create a destination.
	 * @param font  The font for the label.
	 * @return The Phrase with the destination in it.
	 */
	public static Phrase createDestination(String label, ProgramElementDoc doc, Font font)
	{
		boolean multiPart = false;
		Chunk chunk = null;
		boolean canHaveParms = false;

		if (doc instanceof ConstructorDoc || doc instanceof MethodDoc) canHaveParms = true;

		if (canHaveParms) multiPart = true;

		chunk = new Chunk(label, font);
		Phrase phrase = new Phrase(chunk);

		String destination = doc.qualifiedName();
		phrase.add(PDFUtility.createAnchor(destination));

		if (multiPart)
		{
			ExecutableMemberDoc execDoc = (ExecutableMemberDoc) doc;
			String destinationTwo = destination + "()";
			phrase.add(PDFUtility.createAnchor(destinationTwo));

			String destinationThree = destination + execDoc.signature();
			phrase.add(PDFUtility.createAnchor(destinationThree));

			String destinationFour = destination + execDoc.flatSignature();
			phrase.add(PDFUtility.createAnchor(destinationFour));
		}

		return phrase;
	}

	/**
	 * Creates a link to a destination in the document. This method
	 * does nothing if the given destination is invalid.
	 */
	public static void createLinkTo(ProgramElementDoc doc, Chunk chunk)
	{
		String destination = doc.qualifiedName();
		if (destinations.get(destination) != null) chunk.setLocalGoto(destination);
	}
}
