package org.areasy.common.doclet.utilities;

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

import org.areasy.common.data.StringUtility;
import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;
import org.areasy.common.logger.base.LoggerManager;
import org.areasy.common.doclet.AbstractConfiguration;
import org.areasy.common.doclet.DefaultConfiguration;
import org.areasy.common.doclet.document.Destinations;
import org.areasy.common.doclet.document.State;
import com.sun.javadoc.*;

import java.io.*;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;

/**
 * Javadoc parsing utility class.
 * 
 * @version $Id: DocletUtility.java,v 1.5 2008/05/14 09:36:48 swd\stefan.damian Exp $
 */
public class DocletUtility implements AbstractConfiguration
{
	/**
	 * Logger reference
	 */
	private static Logger log = LoggerFactory.getLog(DocletUtility.class);

	/**
	 * List of package prefixes (built at startup time).
	 */
	private static String[] PACKAGE_PREFIXES = {"org.areasy.common.doclet"};

	/**
	 * Returns the source File for the given Doc; this is
	 * either the .java filename for classes/methods/fields/etc,
	 * the package.html for packages, or the overview HTML file
	 * for RootDoc.  Might return null if there is no source
	 * file for the given Doc.
	 *
	 * @param doc The doc for which to look up the filename.
	 */
	public static File getSourceFile(Doc doc)
	{
		if (doc != null)
		{
			SourcePosition pos = doc.position();
			if (pos != null) return pos.file();
		}

		return null;
	}

	/**
	 * Get formatted <code>see</code> tag.
	 * @param tag
	 */
	public static String formatSeeTag(SeeTag tag)
	{
		boolean plainText = tag.name().startsWith("@linkplain");
		String linkDest = null;
		String defaultLabel = null;
		String label = tag.label();
		String fullText = getComment(tag.inlineTags());

		if (fullText.startsWith("<") || fullText.startsWith("\"")) return fullText;

		if (tag.referencedMemberName() != null)
		{
			MemberDoc member = tag.referencedMember();
			if (member != null)
			{
				defaultLabel = member.name();
				if (member.containingClass().isIncluded()) linkDest = member.qualifiedName();

				if (!member.containingClass().qualifiedName().equals(State.getCurrentClass()))
				{
					if (member instanceof ConstructorDoc) defaultLabel = member.containingPackage().name() + "." + defaultLabel;
						else defaultLabel = member.containingClass().name() + "." + defaultLabel;
				}

				if (member instanceof ExecutableMemberDoc)
				{
					if (linkDest != null)
					{
						linkDest += ((ExecutableMemberDoc) member).signature();
						defaultLabel += ((ExecutableMemberDoc) member).flatSignature();
					}
					else defaultLabel += ((ExecutableMemberDoc) member).signature();
				}
			}
			else defaultLabel = tag.referencedMemberName();
		}
		else if (tag.referencedClassName() != null)
		{
			ClassDoc classDoc = tag.referencedClass();
			if (classDoc != null)
			{
				if (classDoc.isIncluded())
				{
					linkDest = classDoc.qualifiedName();
					defaultLabel = classDoc.name();
				}
				else defaultLabel = classDoc.qualifiedName();
			}
			else
			{
				defaultLabel = tag.referencedClassName();
				// This actually might be a package reference
				if (tag.referencedPackage() != null && Destinations.isValid(defaultLabel)) linkDest = defaultLabel;
			}
		}
		else if (tag.referencedPackage() != null)
		{
			PackageDoc packageDoc = tag.referencedPackage();
			defaultLabel = packageDoc.name();
			if (packageDoc.isIncluded() || Destinations.isValid(packageDoc.name())) linkDest = defaultLabel;
		}

		if (label == null || label.length() == 0)
		{
			if (defaultLabel != null) label = defaultLabel;
				else label = fullText;
		}

		if (linkDest != null && plainText) return "<a href=\"locallinkplain:" + linkDest + "\">" + label + "</a>";
			else if (linkDest != null) return "<a href=\"locallink:" + linkDest + "\">" + label + "</a>";
				else if (!plainText) return "<code>" + label + "</code>";
					else return label;
	}

	/**
	 * This utility method returns the comment text for a given method doc. If
	 * the method comment uses the "inheritDoc"-tag, it is resolved recursively.
	 *
	 * @param doc The method doc.
	 * @return The final comment text.
	 */
	public static String getComment(Doc doc)
	{
		if (doc == null) return "";

		String text = getComment(doc.inlineTags());
		if (doc instanceof MethodDoc && (text == null || text.length() == 0))
		{
			MethodDoc superMethod = ((MethodDoc) doc).overriddenMethod();
			if (superMethod != null) text = getComment(superMethod);
		}

		return text;
	}

	/**
	 * Returns the HTML string for an array of Tags. Meant to be called with the
	 * result of the inlineTags() method of Doc and/or Tag, as this does not
	 * support the tag types found elsewhere. Specifically, this method handles
	 * docRoot, inheritDoc, link, and linkplain tags, as well as raw-text Tags.
	 */
	public static String getComment(Tag[] tags)
	{
		if (tags == null || tags.length == 0) return "";

		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < tags.length; i++)
		{
			buf.append(getComment(tags[i]));
		}

		return buf.toString();
	}

	/**
	 * Returns the HTML string for a single tag.
	 */
	public static String getComment(Tag tag)
	{
		if (tag == null) return "";

		if (tag.kind().equalsIgnoreCase("Text")) return tag.text();
		else if (tag.name().equalsIgnoreCase("@docRoot")) return DefaultConfiguration.getWorkDir();
		else if (tag.name().equalsIgnoreCase("@inheritDoc"))
		{
			Doc doc = tag.holder();
			if (doc instanceof MethodDoc) return getComment(((MethodDoc) doc).overriddenMethod());
				else if (doc instanceof ClassDoc) return getComment(((ClassDoc) doc).superclass());
					else log.warn("Unknown @inheritDoc doc type " + doc);

			return tag.text();
		}
		else if (tag instanceof SeeTag) return formatSeeTag((SeeTag) tag);
		else return tag.text();
	}

	/**
	 * Builds the internal list of packages used later to decide if a given
	 * class is an "external" class or if it belongs to one of these packages.
	 *
	 * @param root The javadoc root
	 */
	public static void buildPackageList(RootDoc root)
	{

		ClassDoc classes[] = root.classes();
		Hashtable pkgMap = new Hashtable();
		for (int i = 0; i < classes.length; i++)
		{
			pkgMap.put(classes[i].containingPackage().name(), "");
		}

		// Now check if there have been any additional
		// packages been specified with "dontspec"
		String dontSpec = DefaultConfiguration.getString(ARG_DONTSPEC);
		if (dontSpec != null)
		{
			StringTokenizer tok = new StringTokenizer(dontSpec, ",");
			while (tok.hasMoreTokens())
			{
				String token = tok.nextToken();
				pkgMap.put(token, "");
			}
		}

		int number = pkgMap.size();

		PACKAGE_PREFIXES = new String[number];
		Enumeration keys = pkgMap.keys();
		int ct = 0;

		while (keys.hasMoreElements())
		{
			String key = (String) keys.nextElement();
			PACKAGE_PREFIXES[ct] = key;
			ct++;
		}
	}

	/**
	 * Utility method which returns the name of a class or interface. For
	 * classes and interfaces of external packages, the fully qualified name is
	 * returned; for such of of the same package, only the short name is
	 * returned.
	 *
	 * @param classDoc The class or interface whose name should be returned.
	 * @return The short or fully qualifed name of the class or interface.
	 */
	public static String getQualifiedNameIfNecessary(ClassDoc classDoc)
	{
		String name = classDoc.name();
		boolean isExternal = true;

		for (int i = 0; (i < PACKAGE_PREFIXES.length) && isExternal; i++)
		{
			if (classDoc.qualifiedTypeName().startsWith(PACKAGE_PREFIXES[i])) isExternal = false;
		}

		if (isExternal) name = classDoc.qualifiedTypeName();

		return name;
	}

	/**
	 * Utility method which returns only the first sentence of a given text
	 * String. This is used for the summary tables where only the first sentence
	 * of the doc of a constructor, field or method is printed.
	 *
	 * @param text The whole doc text.
	 * @return The first sentence of the text.
	 */
	public static String getFirstSentence(Doc text)
	{
		return getComment(text.firstSentenceTags());
	}

	/**
	 * Utility method which returns the name of a class or interface. For
	 * classes and interfaces of external packages, the fully qualified name is
	 * returned; for such of of the same package, only the short name is
	 * returned.
	 *
	 * @param qualifiedName The qualified name of the class or interface.
	 * @return The short or fully qualifed name of the class or interface.
	 */
	public static String getQualifiedNameIfNecessary(String qualifiedName)
	{
		String name = qualifiedName;
		boolean isExternal = true;

		for (int i = 0; (i < PACKAGE_PREFIXES.length) && isExternal; i++)
		{
			if (qualifiedName.startsWith(PACKAGE_PREFIXES[i])) isExternal = false;
		}

		if (!isExternal && (qualifiedName.indexOf(".") != -1)) name = qualifiedName.substring(qualifiedName.lastIndexOf(".") + 1, qualifiedName.length());

		return name;
	}

	/**
	 * Returns a text String with the modifiers of a given class.
	 *
	 * @param classDoc The ClassDoc for the given class.
	 * @return A text String with all modifiers.
	 */
	public static String getClassModifiers(ClassDoc classDoc)
	{
		String info = "";

		if (classDoc.isPublic()) info = "public ";
		if (classDoc.isPrivate()) info = "private ";
		if (classDoc.isProtected()) info = "protected ";

		if (!classDoc.isInterface())
		{
			if (classDoc.isStatic()) info = info + "static ";
			if (classDoc.isFinal()) info = info + "final ";
			if (classDoc.isAbstract()) info = info + "abstract ";

			info = info + "class ";
		}
		else info = info + "interface ";

		return info;
	}

	/**
	 * Returns a text String with the modifiers of a given method.
	 *
	 * @param methodDoc The MethodDoc for the given method.
	 * @return A text String with all modifiers.
	 */
	public static String getMethodModifiers(MethodDoc methodDoc)
	{
		String declaration = "";

		if (methodDoc.isPublic()) declaration = "public ";
			else if (methodDoc.isProtected()) declaration = "protected ";
				else if (methodDoc.isPrivate()) declaration = "private ";

		if (methodDoc.isFinal()) declaration = declaration + "final ";

		if (methodDoc.isStatic()) declaration = declaration + "static ";

		if (methodDoc.isNative()) declaration = declaration + "native ";

		if (methodDoc.isAbstract()) declaration = declaration + "abstract ";

		return declaration;
	}

	/**
	 * Returns a text String with the modifiers of the method summary of a given
	 * method.
	 *
	 * @param methodDoc The MethodDoc for the given method.
	 * @return A text String with all modifiers.
	 */
	public static String getMethodSummaryModifiers(MethodDoc methodDoc)
	{
		String declaration = "";

		if (methodDoc.isStatic()) declaration = declaration + "static ";
		if (methodDoc.isNative()) declaration = declaration + "native ";
		if (methodDoc.isAbstract()) declaration = declaration + "abstract ";

		return declaration;
	}

	/**
	 * Returns a text String with the modifiers of a given constructor.
	 *
	 * @param constructorDoc The ConstructorDoc for the given constructor.
	 * @return A text String with all modifiers.
	 */
	public static String getConstructorModifiers(ConstructorDoc constructorDoc)
	{
		String declaration = "";

		if (constructorDoc.isPublic()) declaration = "public ";
		if (constructorDoc.isProtected()) declaration = "protected ";
		if (constructorDoc.isPrivate()) declaration = "private ";
		if (constructorDoc.isFinal()) declaration = declaration + "final ";

		return declaration;
	}

	/**
	 * Returns a text String with the modifiers of a given field.
	 *
	 * @param fieldDoc The FieldDoc for the given field.
	 * @return A text String with all modifiers.
	 */
	public static String getFieldModifiers(FieldDoc fieldDoc)
	{
		return fieldDoc.modifiers() + " ";
	}

	/**
	 * Returns a string with the modifiers of a given field.
	 *
	 * @param fieldDoc The FieldDoc for the given field.
	 * @return A text String with all modifiers.
	 */
	public static String getFieldSummaryModifiers(FieldDoc fieldDoc)
	{
		StringBuffer buffer = new StringBuffer();

		if (fieldDoc.isStatic()) buffer.append("static ");
		if (fieldDoc.isFinal()) buffer.append("final ");

		String type = getQualifiedNameIfNecessary(fieldDoc.type().toString());
		buffer.append(type);

		return buffer.toString();
	}

	/**
	 * Utility method for sorting ClassDoc table in function of names;
	 *
	 * @param original to be sorted
	 * @return The sorted classdocs in an array.
	 */
	public static ClassDoc[] sort(ClassDoc[] original)
	{
		int taille = original.length;

		if (taille > 1)
		{
			String[] str = new String[taille];
			ClassDoc[] tabresult = new ClassDoc[taille];

			for (int i = 0; i < taille; i++)
			{
				str[i] = original[i].name().toLowerCase();
			}

			Arrays.sort(str);

			for (int i = 0; i < taille; i++)
			{
				for (int j = 0; j < taille; j++)
				{
					if (str[i].equals(original[j].name().toLowerCase()))
					{
						tabresult[i] = original[j];

						break;
					}
				}
			}

			return tabresult;
		}
		else return original;
	}

	/**
	 * Utility method for concatening ClassDoc tables.
	 *
	 * @param all     destination table
	 * @param pos     from where must be included source table into destination
	 *                table
	 * @param classes table, to be included into destination table
	 */
	public static void addClassDoc(ClassDoc[] all, int pos, ClassDoc[] classes)
	{
		for (int i = 0; i < classes.length; i++)
		{
			all[i + pos] = classes[i];
		}
	}

	/**
	 * Returns the type of a parameter.
	 *
	 * @param parm The parameter whose type is to be determined.
	 * @return The class name or primitive type name.
	 */
	public static String getParameterType(Parameter parm)
	{
		String result = "";
		Type type = parm.type();
		ClassDoc classDoc = type.asClassDoc();
		String dimension = type.dimension();

		if (classDoc == null) result = type.typeName();
			else result = DocletUtility.getQualifiedNameIfNecessary(classDoc);

		return result + dimension;
	}

	public static String findSuperClassWithMethod(String method)
	{
		String result = State.currentClass;
		Doc doc = State.getCurrentDoc();
		if (doc instanceof ClassDoc)
		{
			ClassDoc classDoc = (ClassDoc) doc;
			result = findMethodInClass(classDoc, method);
		}

		return result;
	}

	/**
	 * Recursively looks for a superclass with a certain method.
	 *
	 * @param classDoc The current class to search.
	 * @param method   The method name and optionally signature.
	 * @return The class name found or null.
	 */
	private static String findMethodInClass(ClassDoc classDoc, String method)
	{
		String result = null;
		MethodDoc[] methods = classDoc.methods();
		if (methods != null && methods.length > 0)
		{
			method = method.trim();
			for (int i = 0; i < methods.length; i++)
			{
				MethodDoc doc = methods[i];
				if (method.indexOf("(") == -1 || method.endsWith("()"))
				{
					// If method search string does not contain parameters just compare method name (what else can we do?)
					if (method.equals(doc.name())) result = doc.qualifiedName();
				}
				else
				{
					int pos1 = method.indexOf("(");
					int pos2 = method.indexOf(")");
					if (pos1 != -1 && pos2 > pos1)
					{
						String methodName = method.substring(0, pos1);
						if (doc.name().equals(methodName))
						{
							// extract the parameter list from the search method string
							String args1 = method.substring(pos1 + 1, pos2);

							// build the parameter list for the doc method string
							String args2 = doc.flatSignature();

							// extract brackets
							args2 = args2.substring(1, args2.length() - 1);

							// Build list 1 (search method string)
							StringTokenizer tok1 = new StringTokenizer(args1, ",");
							String list1 = "";
							while (tok1.hasMoreTokens())
							{
								String arg = tok1.nextToken().trim();
								list1 = list1 + arg;
								if (tok1.hasMoreTokens())list1 = list1 + ",";
							}

							// Build list 2 (doc method string)
							StringTokenizer tok2 = new StringTokenizer(args2, ",");
							String list2 = "";

							while (tok2.hasMoreTokens())
							{
								String arg = tok2.nextToken().trim();
								list2 = list2 + arg;

								if (tok2.hasMoreTokens()) list2 = list2 + ",";
							}

							// Now we have to compacted lists we can compare
							if (list1.equals(list2)) result = doc.qualifiedName();
						}
					}
				}
			}
		}

		// If it was not found in this class, check next superclass
		if (result == null)
		{
			ClassDoc superClassDoc = classDoc.superclass();
			if (superClassDoc != null) result = findMethodInClass(superClassDoc, method);
		}

		return result;
	}

	/**
	 * Returns the fully qualified type of a parameter.
	 *
	 * @param parm The parameter whose type is to be determined.
	 * @return The class name or primitive type name.
	 */
	public static String getQualifiedParameterType(Parameter parm)
	{
		String result = "";
		Type type = parm.type();
		ClassDoc classDoc = type.asClassDoc();

		if (classDoc == null) result = type.typeName();
			else result = classDoc.qualifiedTypeName();

		return result;
	}

	/**
	 * Creates a String containing the specified number of "-" characters.
	 *
	 * @return The String with the '-'-characters.
	 */
	public static String getLine(int size)
	{
		StringBuffer line = new StringBuffer(size + 1);
		for (int i = 0; i < size; i++)
		{
			line.append("-");
		}

		return new String(line);
	}

	/**
	 * Replaces all occurrences of a String in a given String.
	 *
	 * @param content     The String in which to search and replace.
	 * @param toReplace   The String to be replaced.
	 * @param replaceWith The String to replace the search String with.
	 * @return The new String.
	 */
	public static String replace(String content, String toReplace, String replaceWith)
	{
		if (toReplace.equals(replaceWith))
		{
			log.warn("Replace-String is equal to the one to be replaced!");
			return content;
		}

		String result = content;
		int pos = result.indexOf(toReplace);
		while (pos != -1)
		{
			String firstPart = "", endPart = "";
			if (pos > 0) firstPart = result.substring(0, pos);

			if (pos + toReplace.length() < result.length()) endPart = result.substring(pos + toReplace.length(), result.length());

			result = firstPart + replaceWith + endPart;
			pos = result.indexOf(toReplace);
		}

		return result;
	}

	/**
	 * Method getFile returns the name of the file relative
	 * to the current package within the sourcepath.
	 *
	 * @param filename The relative path of the file.
	 * @return String The absolute path of the file.
	 * @throws java.io.FileNotFoundException If no such file was found.
	 */
	public static String getFilePath(String filename) throws FileNotFoundException
	{
		String foundPath = null;
		if (filename == null) return ".";

		foundPath = getFoundFilePath(filename);

		String JAVADOC_PACKAGE_FILE = "package-summary.html";
		if (foundPath == null && filename.endsWith(JAVADOC_PACKAGE_FILE))
		{
			String leadPath = filename.substring(0, filename.length() - JAVADOC_PACKAGE_FILE.length());
			String realPackageHtml = leadPath + "package.html";
			foundPath = getFoundFilePath(realPackageHtml);
		}

		/* Similar try for a link to class documentation. */
		if (foundPath == null && filename.endsWith(".html"))
		{
			String maybeJavaFile = filename.substring(0, filename.length() - 5) + ".java";
			foundPath = getFoundFilePath(maybeJavaFile);
		}

		if (foundPath == null) throw new FileNotFoundException("File: " + filename + " not found.");

		return foundPath;
	}

	private static String getFoundFilePath(String fileName)
	{
		String filePath = null;

		// Handle {@docRoot} tag
		String DOC_ROOT = "{@docRoot}";
		if (fileName.startsWith(DOC_ROOT))
		{
			// replace tag with value of work dir
			String newName = DefaultConfiguration.getWorkDir();
			fileName = newName + fileName.substring(DOC_ROOT.length(), fileName.length());

			File file = new File(fileName);
			if (file.exists()) filePath = file.getAbsolutePath();
		}

		/* Try relative to the current file, if we know it. */
		if (filePath == null)
		{
			File currFile = State.getCurrentFile();
			log.debug("Current processing file: " + currFile);

			if (currFile != null)
			{
				File currDir = currFile.isDirectory() ? currFile : currFile.getParentFile();
				File file = new File(currDir, fileName);

				if (file.exists()) filePath = file.getAbsolutePath();
			}
		}

		if (filePath == null)
		{
			log.info("Current workign directory: " + DefaultConfiguration.getWorkDir());
			File file = new File(DefaultConfiguration.getWorkDir(), fileName);

			if (file.exists()) filePath = file.getAbsolutePath();
		}

		log.info("Identified resource file: " + filePath);
		return filePath;
	}

	/**
	 * Prints an error message to stdout with
	 * process status information.
	 *
	 * @param text The error message text.
	 */
	public static void error(String text)
	{
		log.error("Error for state (package, class, method, member): " + State.currentPackage + ", " + State.currentClass + ", " + State.currentMethod + ", " + State.currentMember + " : " + text);
	}

	/**
	 * Prints an error message to stdout with
	 * process status information.
	 *
	 * @param text      The error message text.
	 * @param throwable Some causing throwable
	 */
	public static void error(String text, Throwable throwable)
	{
		log.error("Error for state (package, class, method, member): " + State.currentPackage + ", " + State.currentClass + ", " + State.currentMethod + ", " + State.currentMember + " : " + text, throwable);
	}

	/**
	 * Initializes the log4j facility.
	 */
	public static void initLogger(boolean debug)
	{
		LoggerManager manager = LoggerFactory.getLogManager();

		if(!manager.isInit())
		{
			int level = debug ? Logger.LOG_LEVEL_DEBUG : Logger.LOG_LEVEL_INFO;
			manager.addConsoleLogger(null, level, "doclet", null);
		}
	}

	/**
	 * Removes carriage return and linefeed characters from
	 * a given text String.
	 *
	 * @param text The original text from the source file.
	 * @return The new text without any linefeed characters.
	 */
	public static String stripLineFeeds(String text)
	{
		if (text.length() == 0) return "";

		char[] charArray = text.toCharArray();

		String leftBlank = "";
		String rightBlank = "";
		int ct = 0;

		while ((charArray[ct++] == ' ') && (ct < charArray.length))
		{
			leftBlank = leftBlank + " ";
		}

		ct = charArray.length - 1;

		while ((charArray[ct--] == ' ') && (ct > -1))
		{
			rightBlank = rightBlank + " ";
		}

		// Step 1:dCount number of parts
		int listSize = 0;

		for (int i = 0; i < charArray.length; i++)
		{
			if ((charArray[i] == '\n') || (charArray[i] == '\r')) listSize++;
		}

		listSize++;

		if (listSize == 1) return text;

		// Step 2: Create list
		int lastPos = 0;
		int partCt = 0;
		String[] parts = new String[listSize];
		int i = 0;

		for (i = 0; i < charArray.length; i++)
		{
			if ((charArray[i] == '\n') || (charArray[i] == '\r'))
			{
				if (i > (lastPos + 1))
				{
					parts[partCt] = new String(charArray, lastPos, i - lastPos).trim();
					partCt++;
				}

				lastPos = i;
			}
		}

		if (i > (lastPos + 1))
		{
			parts[partCt] = new String(charArray, lastPos, i - lastPos).trim();
			partCt++;
		}

		String result = "";

		for (i = 0; i < partCt; i++)
		{
			result = result + parts[i] + " ";
		}

		result = leftBlank + result.trim() + rightBlank;

		return result;
	}

	/**
	 * A case-insensitive version of the String.indexOf() method.
	 *
	 * @param target    The string in which to search.
	 * @param substring The string to look for.
	 * @return int The position of the search string in the target string.
	 *         If it was not found, -1 is returned.
	 */
	public static int indexOfIgnoreCase(String target, String substring)
	{
		if (target == null || substring == null) return -1;

		int targetLength = target.length();
		int searchLength = substring.length();

		int spotsToSearch = targetLength - searchLength + 1;
		for (int i = 0; i < spotsToSearch; i++)
		{
			if (target.regionMatches(true, i, substring, 0, searchLength)) return i;
		}

		return -1;
	}

	/**
	 * Returns the content of the specified HTML file.
	 *
	 * @param htmlFile The HTML file to read.
	 * @return The HTML content in one String.
	 * @throws java.io.IOException If reading the file failed.
	 */
	public static String getContentFromFile(File htmlFile) throws IOException
	{

		BufferedReader rd = new BufferedReader(new FileReader(htmlFile));
		StringBuffer buffer = new StringBuffer();
		char charBuffer[] = new char[2048];
		int numRead = 0;

		do
		{
			numRead = rd.read(charBuffer);
			if (numRead > 0) buffer.append(charBuffer, 0, numRead);
		}

		while (numRead >= 0);
		rd.close();

		return buffer.toString();
	}

	/**
	 * Returns the content of the specified HTML content, and if there are
	 * &lt;body&gt; ... &lt;/body&gt; tags, it returns the section inside
	 * those tags.
	 *
	 * @return The HTML content in one String.
	 */
	public static String getHTMLBodyContent(String html)
	{
		if(StringUtility.isEmpty(html)) return html;

		int startIndex = indexOfIgnoreCase(html, "<body");
		int endIndex = -1;

		if (startIndex >= 0) startIndex = html.indexOf('>', startIndex);
		if (startIndex >= 0) endIndex = indexOfIgnoreCase(html, "</body");
		if (startIndex >= 0 && endIndex >= 0) html = html.substring(startIndex + 1, endIndex);

		return html;
	}

	/**
	 * Returns the content of the specified HTML file, and if there are
	 * &lt;body&gt; ... &lt;/body&gt; tags, it returns the section inside
	 * those tags.
	 *
	 * @param htmlFile The HTML file to read.
	 * @return The HTML content in one String.
	 * @throws java.io.IOException If reading the file failed.
	 */
	public static String getHTMLBodyContentFromFile(File htmlFile) throws IOException
	{
		String html = getContentFromFile(htmlFile);
		return getHTMLBodyContent(html);
	}
}