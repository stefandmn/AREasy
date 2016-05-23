package org.areasy.runtime.actions.system.sysmon;

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

import java.io.*;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Convenience methods for interacting with the filesystem.
 */
public class ParserUtility
{
	private static final Pattern PROC_DIR_PATTERN = Pattern.compile("([\\d]*)");

	private final static FilenameFilter PROCESS_DIRECTORY_FILTER = new FilenameFilter()
	{
		public boolean accept(File dir, String name)
		{
			File fileToTest = new File(dir, name);
			return fileToTest.isDirectory() && PROC_DIR_PATTERN.matcher(name).matches();
		}
	};

	/**
	 * If you're using an operating system that supports the proc filesystem,
	 * this returns a list of all processes by reading the directories under
	 * /proc
	 *
	 * @return An array of the ids of all processes running on the OS.
	 */
	public static String[] pidsFromProcFilesystem()
	{
		return new File("/proc").list(ParserUtility.PROCESS_DIRECTORY_FILTER);
	}

	/**
	 * Given a filename, reads the entire file into a string.
	 *
	 * @param fileName The path of the filename to read. Should be absolute.
	 * @return A string containing the entire contents of the file
	 * @throws IOException If there's an IO exception while trying to read the file
	 */
	public static String slurp(String fileName) throws IOException
	{
		return slurpFromInputStream(new FileInputStream(fileName));
	}

	/**
	 * Given a filename, reads the entire file into a byte array.
	 *
	 * @param fileName The path of the filename to read. Should be absolute.
	 * @return A byte array containing the entire contents of the file
	 * @throws IOException If there's an IO exception while trying to read the file
	 */
	public static byte[] slurpToByteArray(String fileName) throws IOException
	{
		File fileToRead = new File(fileName);
		byte[] contents = new byte[(int) fileToRead.length()];
		InputStream inputStream = null;

		try
		{
			inputStream = new FileInputStream(fileToRead);
			inputStream.read(contents);

			return contents;
		}
		finally
		{
			if (inputStream != null)
			{
				inputStream.close();
			}
		}
	}

	/**
	 * Given an InputStream, reads the entire file into a string.
	 *
	 * @param stream The InputStream representing the file to read
	 * @return A string containing the entire contents of the input stream
	 * @throws IOException If there's an IO exception while trying to read the input stream
	 */
	public static String slurpFromInputStream(InputStream stream) throws IOException
	{
		if (stream == null)
		{
			return null;
		}

		StringWriter sw = new StringWriter();
		String line;

		try
		{
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
			while ((line = reader.readLine()) != null)
			{
				sw.write(line);
				sw.write('\n');
			}
		}
		finally
		{
			stream.close();
		}

		return sw.toString();
	}

	/**
	 * Runs a regular expression on a file, and returns the first match.
	 *
	 * @param pattern  The regular expression to use.
	 * @param filename The path of the filename to match against. Should be absolute.
	 * @return The first match found. Null if no matches.
	 */
	public static String runRegexOnFile(Pattern pattern, String filename)
	{
		try
		{
			final String file = slurp(filename);

			Matcher matcher = pattern.matcher(file);
			matcher.find();

			final String firstMatch = matcher.group(1);

			if (firstMatch != null && firstMatch.length() > 0)
			{
				return firstMatch;
			}
		}
		catch (IOException e)
		{
			// return null to indicate failure
		}
		return null;
	}

	public static String secsInDaysAndHours(long seconds)
	{
		long days = seconds / (60 * 60 * 24);
		long hours = (seconds / (60 * 60)) - (days * 24);
		return days + " days " + hours + " hours";
	}

	public static String diskSizeFormat(long size)
	{
		if (size <= 0) return "0";
		final String[] units = new String[]{"B", "kB", "MB", "GB", "TB"};
		int digitGroups = (int) (Math.log10(size) / Math.log10(1024));

		return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
	}
}
