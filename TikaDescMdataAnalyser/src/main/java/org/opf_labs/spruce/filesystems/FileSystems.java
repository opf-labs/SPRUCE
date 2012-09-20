/**
 * 
 */
package org.opf_labs.spruce.filesystems;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;


/**
 * TODO: JavaDoc for FileSystems.
 * <p/>
 * 
 * @author <a href="mailto:carl.wilson@keepitdigital.eu">Carl Wilson</a> <a
 *         href="https://github.com/carlwilson">carlwilson AT github</a>
 * @version 0.1
 * 
 *          Created 10 Jul 2012:15:25:51
 */

public final class FileSystems {
	private FileSystems() {
		/** disable default constructor */
		throw new AssertionError("In disabled private constructor.");
	}
	
	/**
	 * @param firstPath
	 * @param secondPath
	 * @return the relative path to use as an entry name
	 */
	public static String relatavisePaths(String firstPath, String secondPath) {
		if (secondPath.startsWith(firstPath)) {
			return secondPath
					.substring(firstPath.length() + 1, secondPath.length())
					.replace("\\", "/").trim();
		}
		return "";
	}

	/**
	 * Build an FileSystem from a root directory, built by traversing from the root directory, and named from the directory name.
	 * @param directory the root directory to be traversed, must be a non null, existing directory
	 * @return a new {@link FileSystem} created from the directory
	 */
	public static final JavaIOFileSystem fromDirectory(File directory) {
		if (directory == null) throw new IllegalArgumentException("directory == null");
		return fromDirectory(directory.getName(), directory);
	}

	/**
	 * Build an FileSystem from a root directory, built by traversing from the root directory, and named from the directory name.
	 * @param name the name of the file system
	 * @param directory the root directory to be traversed, must be a non null, existing directory
	 * @return a new {@link FileSystem} created from the directory
	 */
	public static final JavaIOFileSystem fromDirectory(String name, File directory) {
		if (name == null) throw new IllegalArgumentException("name == null");
		if (directory == null) throw new IllegalArgumentException("directory == null");
		if ((!directory.exists()) || (!directory.isDirectory())) {
			throw new IllegalArgumentException("directory parameter must be an existing directory");
		}
		// Create an entry set
		Map<String, FileSystemEntry> entries = new HashMap<String, FileSystemEntry>();
		// Populate the entry set and get the total size
		long totalSize = JavaIOFileSystem.populateEntriesFromDirectory(directory, entries, directory.getAbsolutePath());
		// Return the JavaIOFileSystem
		return new JavaIOFileSystem(directory.getName(), directory.getAbsolutePath(), totalSize, entries);
	}
	
}
