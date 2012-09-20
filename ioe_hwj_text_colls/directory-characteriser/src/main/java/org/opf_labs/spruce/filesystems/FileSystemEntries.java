/**
 * 
 */
package org.opf_labs.spruce.filesystems;

import java.io.File;


/**
 * TODO: JavaDoc for FileSystemEntries.<p/>
 * 
 * @author  <a href="mailto:carl@openplanetsfoundation.org">Carl Wilson</a>
 *          <a href="https://github.com/carlwilson">carlwilson AT github</a>
 * @version 0.1
 * 
 * Created 20 Jul 2012:12:52:10
 */

public final class FileSystemEntries {
	/** A null entry constant that breaks the rules with an empty name*/
	public static final FileSystemEntry NULL_ENTRY = FileSystemEntryImpl.fromValues("", 0L, 0L);
	private FileSystemEntries() {
		/** Disable default constructor for static class */
		throw new AssertionError("[FileSystemEntries] Default constructor should never be called.");
	}

	/**
	 * @param name a non null, non empty string name
	 * @param size and long > -1
	 * @param modified
	 * @return a new {@link FileSystemEntry} created from the values.
	 */
	public static final FileSystemEntry fromValues(final String name, final long size, final long modified) {
		if (name == null) throw new IllegalArgumentException("name == null");
		if (name.isEmpty()) throw new IllegalArgumentException("name.isEmpty() == true");
		return FileSystemEntryImpl.fromValues(name, size, modified);
	}

	/**
	 * @param name a non null, non empty string name
	 * @param compressedSize 
	 * @param size and long > -1
	 * @param modified
	 * @return a new {@link FileSystemEntry} created from the values.
	 */
	public static final FileSystemEntry fromValues(final String name, final long compressedSize, final long size, final long modified) {
		if (name == null) throw new IllegalArgumentException("name == null");
		if (name.isEmpty()) throw new IllegalArgumentException("name.isEmpty() == true");
		return FileSystemEntryImpl.fromValues(name, compressedSize, size, modified);
	}

	/**
	 * @param name
	 *            the entry name, must be non null and not empty
	 * @param file
	 *            the file to create entry details from, must not be a directory.
	 * @return a final unchecked FileSystemEntry populated from the file
	 */
	public static final FileSystemEntry fromFile(final String name, final File file) {
		if (name == null) throw new IllegalArgumentException("name == null");
		if (name.isEmpty()) throw new IllegalArgumentException("name.isEmpty() == true");
		if (file == null) throw new IllegalArgumentException("file == null");
		if (file.isDirectory()) throw new IllegalArgumentException("file.isDirectory() != true for " + file);
		return FileSystemEntryImpl.fromValues(name, file.length(), file.length(), file.lastModified());
	}
}
