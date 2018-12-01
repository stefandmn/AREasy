package org.areasy.runtime.utilities;

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

import org.areasy.common.data.StringUtility;

import java.io.*;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtility
{
	public static void dpZip(File srcFolder, String destZipFile) throws Exception
	{
		ZipOutputStream zip = null;
		FileOutputStream fileWriter = null;

		fileWriter = new FileOutputStream(destZipFile);
		zip = new ZipOutputStream(fileWriter);

		addFolderToZip(srcFolder, srcFolder, zip);
		zip.flush();
		zip.close();
	}

	private static void addFolderToZip(File basePath, File folder, ZipOutputStream zip) throws Exception
	{
		for (File file : folder.listFiles())
		{
			if (file.isDirectory())
			{
				String baseName = basePath.getParentFile().getPath();
				String folderName = folder.getPath();
				String zipEntry = StringUtility.replace(folderName, baseName + File.separator, "").replace('\\', '/');

				zip.putNextEntry(new ZipEntry(zipEntry));
				zip.closeEntry();

				addFolderToZip(basePath, file, zip);
			}
			else
			{
				addFileToZip(basePath, file, zip);
			}
		}
	}

	private static void addFileToZip(File basePath, File srcFile, ZipOutputStream zip) throws Exception
	{
		if (srcFile == null || !srcFile.exists()) return;

		if (srcFile.isDirectory())
		{
			addFolderToZip(basePath, srcFile, zip);
		}
		else
		{
			String baseName = basePath.getParentFile().getPath();
			String folderName = srcFile.getPath();
			String zipEntry = StringUtility.replace(folderName, baseName + File.separator, "").replace('\\', '/');

			int len;
			byte[] buf = new byte[1024];

			FileInputStream in = new FileInputStream(srcFile);
			zip.putNextEntry(new ZipEntry(zipEntry));

			while ((len = in.read(buf)) > 0)
			{
				zip.write(buf, 0, len);
			}

			zip.closeEntry();
		}
	}

	public static File doZip(File fileIn, String fileOut) throws Exception
	{
		if (fileIn == null || !fileIn.exists()) return null;

		//Create the ZIP file
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(fileOut));
		FileInputStream in = new FileInputStream(fileIn);

		// Add ZIP entry to output stream.
		out.putNextEntry(new ZipEntry(fileIn.getName()));

		// Transfer bytes from the file to the ZIP file
		int len;
		byte[] buf = new byte[1024];

		while ((len = in.read(buf)) > 0)
		{
			out.write(buf, 0, len);
		}

		// Complete the entry
		out.closeEntry();
		in.close();

		// Complete the ZIP file
		out.close();

		return new File(fileOut);
	}

	public static File doZip(File file) throws Exception
	{
		if (file == null || !file.exists()) return null;

		String fileExt = "";
		String filePath = "";
		String fileBase = "";
		String fileName = file.getPath();

		//Get file name
		int index = file.getPath().lastIndexOf(File.separator);
		if (index > 0)
		{
			fileBase = file.getPath().substring(index + 1);
			filePath = file.getPath().substring(0, index);
		}

		index = fileBase.lastIndexOf(".");
		if (index > 0)
		{
			fileName = fileBase.substring(0, index);
			fileExt = fileBase.substring(index + 1);
		}

		//Create the ZIP file
		String target = filePath + File.separator + fileName + ".zip";

		//execute zip and return the answer
		return doZip(file, target);
	}

	public static void doUnjar(InputStream in, File dest) throws IOException
	{
		if (!dest.exists()) dest.mkdirs();
		if (!dest.isDirectory()) throw new IOException("Destination must be a directory");

		JarInputStream jin = new JarInputStream(in);
		ZipEntry entry = jin.getNextEntry();
		byte[] buffer = new byte[1024];

		while (entry != null)
		{
			String fileName = entry.getName();
			if (fileName.charAt(fileName.length() - 1) == '/')
			{
				fileName = fileName.substring(0, fileName.length() - 1);
			}

			if (fileName.charAt(0) == '/')
			{
				fileName = fileName.substring(1);
			}

			if (File.separatorChar != '/')
			{
				fileName = fileName.replace('/', File.separatorChar);
			}

			File file = new File(dest, fileName);
			if (entry.isDirectory())
			{
				// make sure the directory exists
				file.mkdirs();
				jin.closeEntry();
			}
			else
			{
				// make sure the directory exists
				File parent = file.getParentFile();
				if (parent != null && !parent.exists()) parent.mkdirs();

				// dump the file
				OutputStream out = new FileOutputStream(file);
				int len = 0;

				while ((len = jin.read(buffer, 0, buffer.length)) != -1)
				{
					out.write(buffer, 0, len);
				}

				out.flush();
				out.close();
				jin.closeEntry();

				file.setLastModified(entry.getTime());
			}

			entry = jin.getNextEntry();
		}

		//Explicitly write out the META-INF/MANIFEST.MF so that any headers such
		//as the Class-Path are see for the unpacked jar
		Manifest mf = jin.getManifest();

		if (mf != null)
		{
			File file = new File(dest, "META-INF/MANIFEST.MF");
			File parent = file.getParentFile();

			if (parent.exists() == false) parent.mkdirs();

			OutputStream out = new FileOutputStream(file);
			mf.write(out);
			out.flush();
			out.close();
		}

		jin.close();
	}
}
