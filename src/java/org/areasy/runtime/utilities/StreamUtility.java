package org.areasy.runtime.utilities;

/*
 * Copyright (c) 2007-2015 AREasy Runtime
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

import org.areasy.common.data.SerializationException;
import org.areasy.common.data.StringUtility;
import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;

import java.io.*;
import java.util.Vector;

/**
 * Common {@link java.io.File} manipulation routines.
 */
public class StreamUtility
{
	/** Library logger */
	protected static Logger logger =  LoggerFactory.getLog(StreamUtility.class);

	public static final int BUFFER_SIZE = 4096;
	public static final int ONE_KB = 1024;
	public static final int ONE_MB = ONE_KB * ONE_KB;
	public static final int ONE_GB = ONE_KB * ONE_MB;

	/**
	 * Returns a human-readable version of the file size (original is in
	 * bytes).
	 *
	 * @param size The number of bytes.
	 * @return A human-readable display value (includes units).
	 */
	public static String byteCountToDisplaySize(int size)
	{
		String displaySize;

		if (size / ONE_GB > 0) displaySize = String.valueOf(size / ONE_GB) + " GB";
		else
		{
			if (size / ONE_MB > 0) displaySize = String.valueOf(size / ONE_MB) + " MB";
			else
			{
				if (size / ONE_KB > 0) displaySize = String.valueOf(size / ONE_KB) + " KB";
					else displaySize = String.valueOf(size) + " bytes";
			}
		}

		return displaySize;
	}

	/**
	 * Get the short file name of a file instance.
	 *
	 * @param file input file
	 * @return short file name
	 */
	public static String getShortFileName(File file)
	{
		String fileName = file.getName();

		if(StringUtility.isEmpty(fileName))
		{
			fileName = file.getPath();

			int index = fileName.lastIndexOf("/");
			if(index < 0) index = fileName.lastIndexOf("\\");

			if(index > 0 && index < fileName.length()) fileName = fileName.substring(index + 1);
		}

		return fileName;
	}

	/**
	 * Copy resources from a source to a destination.
	 * <p/>
	 * If destination doesn't exists will be created. <br/>
	 * If destination is directory will be appended source file name and will be created.
	 *
	 * @param source source file path
	 * @param destination destination location or file path
	 * @return final destination <Code>File</Code> instance
	 */
	public static File copyFile(String source, String destination) throws IOException
	{
		return copyFile(new File(source), new File(destination));
	}

	/**
	 * Copy resources from a source to a destination.
	 * <p/>
	 * If destination doesn't exists will be created. <br/>
	 * If destination is directory will be appended source file name and will be created.
	 *
	 * @param sourceFile source file entity
	 * @param destFile destination folder entity of real destination file entity
	 * @return final destination <Code>File</Code> instance
	 */
	public static File copyFile(File sourceFile, File destFile) throws IOException
	{
		byte[] buffer = new byte[BUFFER_SIZE];
		BufferedInputStream input;
		BufferedOutputStream output;

		//check if destination is directory.
		if (destFile.exists() && destFile.isDirectory())
		{
			String destination = destFile.getPath() + File.separator + sourceFile.getName();
			destFile = new File(destination);
		}

		//check if destination location doesn't exists.
		if (!destFile.getParentFile().exists()) destFile.getParentFile().mkdirs();

		//check if destination file exists.
		if (!destFile.exists()) destFile.createNewFile();

		input = new BufferedInputStream(new FileInputStream(sourceFile));
		output = new BufferedOutputStream(new FileOutputStream(destFile));

		int bytesRead;

		while ((bytesRead = input.read(buffer)) != -1)
		{
			output.write(buffer, 0, bytesRead);
		}

		input.close();
		output.close();

		return destFile;
	}

	/**
	 * Get string content from file.
	 *
	 * @param URI - file location.
	 */
	public static String readTextFile(String charset, String URI)
	{
		String content = null;
		File fileIn = new File(URI);

		try
		{
			char allElem[];
			if (fileIn.exists())
			{
				InputStreamReader in = null;

				if(charset != null) in = new InputStreamReader(new FileInputStream(fileIn.getAbsolutePath()), charset);
					else  in = new InputStreamReader(new FileInputStream(fileIn.getAbsolutePath()));

				allElem = new char[(int) fileIn.length()];
				in.read(allElem);

				content = String.valueOf(allElem);
			}

			allElem = null;
		}
		catch (Exception e)
		{
			logger.error("Error reading text file: " +e.getMessage());
			logger.debug("Exception", e);

			content = null;
		}

		return content;
	}

	/**
	 * Write (create or rewrite) a text in a file.
	 */
	public static boolean writeTextFile(String charset, String URI, String text)
	{
		File fileIn = new File(URI);

		return writeTextFile(charset, fileIn, text);
	}

	/**
	 * Write (create or rewrite) a text in a file.
	 */
	public static boolean writeTextFile(String charset, File fileIn, String text)
	{
		try
		{
			OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(fileIn.getAbsolutePath()), charset);
			out.write(text);
			out.close();

			return true;
		}
		catch (Exception e)
		{
			logger.error("Error writing text file: " +e.getMessage());
			logger.debug("Exception", e);

			return false;
		}
	}

	/**
	 * Write a file using an input reader.
	 */
	public static File writeReader(String charset, String URI, Reader is) throws IOException
	{
		File newfile = new File(URI);
		OutputStreamWriter os = null;

		if(StringUtility.isNotEmpty(charset)) os = new OutputStreamWriter(new FileOutputStream(newfile), charset);
			else os = new OutputStreamWriter(new FileOutputStream(newfile));

		//now process the InputStream...
		char chars[] = new char[BUFFER_SIZE];

		int readCount = 0;
		while ((readCount = is.read(chars)) > 0)
		{
			os.write(chars, 0, readCount);
		}

		is.close();
		os.close();

		return newfile;
	}

	/**
	 * Removes a directory and all sub-directories and files beneath it.
	 *
	 * @param folder The name of the root directory to be deleted.
	 * @return boolean If all went successful, returns true, otherwise false.
	 */
	public static boolean deleteFolder(String folder) throws IOException
	{
		return deleteFolder(new File(folder));
	}

	/**
	 * Removes a directory and all sub-directories and files beneath it.
	 *
	 * @param folder The name of the root directory to be deleted.
	 * @return boolean If all went successful, returns true, otherwise false.
	 */
	public static boolean deleteFolder(File folder) throws IOException
	{
		if (!folder.isDirectory())
		{
			boolean executed = folder.delete();

			if (!executed)
			{
				folder.deleteOnExit();
				return true;
			}
			else
			{
				return executed;
			}
		}

		deleteFile(folder.getPath());

		boolean executed = folder.delete();
		if (!executed)
		{
			folder.deleteOnExit();
			return true;
		}
		else
		{
			return executed;
		}
	}

	public static boolean createFolder(String name) throws IOException
	{
		return createFolder(new File(name));
	}

	public static boolean createFolder(File dir) throws IOException
	{
		if(dir == null) throw new IOException("Input directory is null");

		if (dir.exists()) return true;
			else return dir.mkdirs();
	}

	/**
	 * Recursive deletion engine, traverses through all sub-directories,
	 * attempting to delete every file and directory it comes across.
	 * NOTE: this version doesn't do any security checks, nor does it
	 * check for file modes and attempt to change them.
	 *
	 * @param path The directory path to be traversed.
	 */
	public static void deleteFile(String path) throws IOException
	{
		if(path == null) throw new IOException("Input file path is null");
		deleteFile(new File(path));
	}

	/**
	 * Recursive deletion engine, traverses through all sub-directories,
	 * attempting to delete every file and directory it comes across.
	 * NOTE: this version doesn't do any security checks, nor does it
	 * check for file modes and attempt to change them.
	 *
	 * @param file The directory <code>File</code> entity to be traversed.
	 */
	public static void deleteFile(File file) throws IOException
	{
		if(file == null) throw new IOException("Input file path is null");

		if (file.isFile())
		{
			boolean executed = file.delete();
			if (!executed) file.deleteOnExit();
		}
		else
		{
			if (file.isDirectory())
			{
				File list[] = file.listFiles();

				// Process all files recursively
				for (int ix = 0; list != null && ix < list.length; ix++) deleteFile(list[ix]);

				// now try to delete the directory
				boolean executed = file.delete();
				if (!executed) file.deleteOnExit();
			}
		}
	}

	/**
	 * Copy resources from a source to a destination. This method is recursive copying all existent resource
	 * and will be created source structure excluding source root. Destination must exists
	 */
	public static Vector copyFolderContent(String source, String destination) throws IOException
	{
		return copyFolderContent(new File(source), new File(destination));
	}

	/**
	 * Copy resources from a source to a destination. This method is recursive copying all existent resource
	 * and will be created source structure excluding source root. Destination must exists
	 */
	public static Vector copyFolderContent(File fileSrc, File destFile) throws IOException
	{
		Vector vector = new Vector();

		if (fileSrc != null && fileSrc.exists() && fileSrc.isDirectory())
		{
			File files[] = fileSrc.listFiles();

			if (destFile == null || !destFile.exists() || !destFile.isDirectory()) throw new IOException("Destination directory doesn't exist or is not directory: " + (destFile == null ? "destination is null" : destFile.getPath()));

			for (int i = 0; i < files.length; i++)
			{
				if (files[i].isFile())
				{
					File newDestFile = new File(destFile, files[i].getName());
					vector.add(copyFile(files[i], newDestFile));
				}
				else
				{
					if (files[i].isDirectory())
					{
						File newDir = new File(destFile, files[i].getName());

						if (newDir.exists())
						{
							if (newDir.isDirectory())
							{
								vector.addAll( copyFolderContent(files[i], newDir) );
							}
							else
							{
								throw new IOException("Error building directory structure. The next path already exist and is not directory: " + newDir.getAbsolutePath());
							}
						}
						else
						{
							if (newDir.mkdir())
							{
								vector.addAll(copyFolderContent(files[i], newDir));
							}
							else
							{
								throw new IOException("Error creating directory structure: " + newDir.getAbsolutePath());
							}
						}
					}
				}
			}
		}
		else if (fileSrc != null && fileSrc.exists() && fileSrc.isFile())
		{
			copyFile(fileSrc, destFile);
		}
		else throw new IOException("Source directory doesn't exist or is not directory: " + (fileSrc == null ? "source is null" : fileSrc.getPath()));

		return vector;
	}

	/**
	 * <p>Deep clone an <code>Object</code> using serialization.</p>
	 * <p/>
	 * <p>This is many times slower than writing clone methods by hand
	 * on all objects in your object graph. However, for complex object
	 * graphs, or for those that don't support deep cloning this can
	 * be a simple alternative implementation. Of course all the objects
	 * must be <code>Serializable</code>.</p>
	 *
	 * @param object the <code>Serializable</code> object to clone
	 * @return the cloned object
	 * @throws org.areasy.common.data.SerializationException (runtime) if the serialization fails
	 */
	public static Object clone(Serializable object)
	{
		return deserialize(serialize(object));
	}

	/**
	 * <p>Serializes an <code>Object</code> to the specified stream.</p>
	 * <p/>
	 * <p>The stream will be closed once the object is written.
	 * This avoids the need for a finally clause, and maybe also exception
	 * handling, in the application code.</p>
	 * <p/>
	 * <p>The stream passed in is not buffered internally within this method.
	 * This is the responsibility of your application if desired.</p>
	 *
	 * @param obj          the object to serialize to bytes, may be null
	 * @param outputStream the stream to write to, must not be null
	 * @throws IllegalArgumentException if <code>outputStream</code> is <code>null</code>
	 * @throws org.areasy.common.data.SerializationException   (runtime) if the serialization fails
	 */
	public static void serialize(Serializable obj, OutputStream outputStream)
	{
		if (outputStream == null) throw new IllegalArgumentException("The OutputStream must not be null");

		ObjectOutputStream out = null;
		try
		{
			// stream closed in the finally
			out = new ObjectOutputStream(outputStream);
			out.writeObject(obj);

		}
		catch (IOException ex)
		{
			throw new SerializationException(ex);
		}
		finally
		{
			try
			{
				if (out != null) out.close();
			}
			catch (IOException ex)
			{
				// ignore;
			}
		}
	}

	/**
	 * <p>Serializes an <code>Object</code> to a byte array for
	 * storage/serialization.</p>
	 *
	 * @param obj the object to serialize to bytes
	 * @return a byte[] with the converted Serializable
	 * @throws SerializationException (runtime) if the serialization fails
	 */
	public static byte[] serialize(Serializable obj)
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream(512);
		serialize(obj, baos);

		return baos.toByteArray();
	}

	/**
	 * <p>Deserializes an <code>Object</code> from the specified stream.</p>
	 * <p/>
	 * <p>The stream will be closed once the object is written. This
	 * avoids the need for a finally clause, and maybe also exception
	 * handling, in the application code.</p>
	 * <p/>
	 * <p>The stream passed in is not buffered internally within this method.
	 * This is the responsibility of your application if desired.</p>
	 *
	 * @param inputStream the serialized object input stream, must not be null
	 * @return the deserialized object
	 * @throws IllegalArgumentException if <code>inputStream</code> is <code>null</code>
	 * @throws SerializationException   (runtime) if the serialization fails
	 */
	public static Object deserialize(InputStream inputStream)
	{
		if (inputStream == null) throw new IllegalArgumentException("The InputStream must not be null");

		ObjectInputStream in = null;
		try
		{
			// stream closed in the finally
			in = new ObjectInputStream(inputStream);
			return in.readObject();

		}
		catch (ClassNotFoundException ex)
		{
			throw new SerializationException(ex);
		}
		catch (IOException ex)
		{
			throw new SerializationException(ex);
		}
		finally
		{
			try
			{
				if (in != null) in.close();
			}
			catch (IOException ex)
			{
				// ignore
			}
		}
	}

	/**
	 * <p>Deserializes a single <code>Object</code> from an array of bytes.</p>
	 *
	 * @param objectData the serialized object, must not be null
	 * @return the deserialized object
	 * @throws IllegalArgumentException if <code>objectData</code> is <code>null</code>
	 * @throws SerializationException   (runtime) if the serialization fails
	 */
	public static Object deserialize(byte[] objectData)
	{
		if (objectData == null) throw new IllegalArgumentException("The byte[] must not be null");

		ByteArrayInputStream bais = new ByteArrayInputStream(objectData);

		return deserialize(bais);
	}
}

