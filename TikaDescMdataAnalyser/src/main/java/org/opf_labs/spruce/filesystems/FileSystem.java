/**
 * 
 */
package org.opf_labs.spruce.filesystems;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

/**
 * TODO: Break down FileSystem processing into stages as follows:
 * <ul>
 * <li>Find entries - quick pass of the file system getting entry data. No
 * attempt to read the data.</li>
 * <li>Check entries - read and check sum the byte stream data.</li>
 * <li>Tika based FileSystem that identifies aggregates on the quick pass, and
 * recursively finds entries.</li>
 * </ul>
 * All file systems come in checked and unchecked flavours, unchecked just
 * processed the entries, checked reads the byte streams.
 * <p/>
 * TODO: <strong>Develop recursive identification first (i.e. find containers
 * and find entries), FOLLOWED BY check-summing and characterisation.</strong>
 * <p/>
 * TODO: Better JavaDoc for FileSystem
 * <p/>
 * TODO: Tests for FileSystem
 * <p/>
 * 
 * @author <a href="mailto:carl@openplanetsfoundation.org">Carl Wilson</a> <a
 *         href="https://github.com/carlwilson">carlwilson AT github</a>
 * @version 0.1 Created 9 Jul 2012:00:40:38
 */

public interface FileSystem extends FileSystemManifest {
	/**
	 * @return a set of the entry names for the file system, usually a relative
	 *         path, or equivalent
	 */
	public Set<String> getEntryNames();

	/**
	 * @param name
	 *            the name of the entry for which the details are required
	 * @return the details for the entry
	 * @throws EntryNotFoundException
	 *             if no entry of that name exists
	 */
	public FileSystemEntry getEntry(String name) throws EntryNotFoundException;

	/**
	 * Retrieve the next entry for this FileSystem
	 * @return the next entry for the file syste,
	 * @throws IOException 
	 */
	public FileSystemEntry getNextEntry() throws IOException;

	/**
	 * @return the input stream for the current entry
	 * @throws EntryNotFoundException
	 * @throws DamagedEntryException 
	 */
	public InputStream getEntryStream() throws EntryNotFoundException, DamagedEntryException ;

	/**
	 * @throws UnsupportedOperationException
	 */
	public void resetEntries() throws UnsupportedOperationException;

	/**
	 * @param stream
	 * @throws IOException 
	 * @throws UnsupportedOperationException
	 */
	public void resetEntries(InputStream stream) throws IOException;
	/**
	 * @param name
	 *            the name of the entry for which the stream is wanted
	 * @return a java.io.InputStream to the entry byte stream
	 * @throws UnsupportedOperationException 
	 * @throws EntryNotFoundException
	 *             if no entry of that name exists
	 * @throws DamagedEntryException
	 *             if the entry cannot be read from the file system, i.e. it is
	 *             damaged
	 */
	public InputStream getEntryStream(String name)
			throws UnsupportedOperationException, EntryNotFoundException, DamagedEntryException;

	/**
	 * @param name
	 *            the name of the entry for which the java.io.File is wanted
	 * @return a java.io.File object for the entry
	 * @throws UnsupportedOperationException
	 *             if the implementation doesn't support files
	 * @throws EntryNotFoundException
	 *             if no entry of that name exists
	 * @throws DamagedEntryException
	 *             if the entry cannot be read from the file system, i.e. it is
	 *             damaged
	 */
	public File getEntryFile(String name) throws UnsupportedOperationException,
			EntryNotFoundException, DamagedEntryException;

	/**
	 * Exception class to indicate that a particular file system entry cannot be
	 * found.
	 * 
	 * @author <a href="mailto:carl@openplanetsfoundation.org">Carl Wilson</a>
	 *         <a href="https://github.com/carlwilson">carlwilson AT github</a>
	 * @version 0.1 Created 12 Jul 2012:00:04:46
	 */
	public static class EntryNotFoundException extends Exception {
		/**
		 * 
		 */
		private static final long serialVersionUID = -5546764680275314283L;

		/**
		 * Default no arg constructor
		 */
		public EntryNotFoundException() {
			super();
		}

		/**
		 * @param message
		 *            a message for the Exception
		 */
		public EntryNotFoundException(String message) {
			super(message);
		}

		/**
		 * @param message
		 *            a message for the Exception
		 * @param cause
		 *            a Throwable cause for this Exception
		 */
		public EntryNotFoundException(String message, Throwable cause) {
			super(message, cause);
		}
	}

	/**
	 * Exception class to indicate that a particular file system entry cannot be
	 * read.
	 * 
	 * @author <a href="mailto:carl@openplanetsfoundation.org">Carl Wilson</a>
	 *         <a href="https://github.com/carlwilson">carlwilson AT github</a>
	 * @version 0.1 Created 12 Jul 2012:00:07:04
	 */
	public static class DamagedEntryException extends Exception {
		/**
		 * 
		 */
		private static final long serialVersionUID = -7191975779800447811L;

		/**
		 * Default no arg constructor
		 */
		public DamagedEntryException() {
			super();
		}

		/**
		 * @param message
		 *            a message for the Exception
		 */
		public DamagedEntryException(String message) {
			super(message);
		}

		/**
		 * @param message
		 *            a message for the Exception
		 * @param cause
		 *            a Throwable cause for this Exception
		 */
		public DamagedEntryException(String message, Throwable cause) {
			super(message, cause);
		}
	}

	/**
	 * Exception class to indicate that a file system can not be created.
	 * 
	 * @author <a href="mailto:carl@openplanetsfoundation.org">Carl Wilson</a>
	 *         <a href="https://github.com/carlwilson">carlwilson AT github</a>
	 * @version 0.1 Created 12 Jul 2012:00:07:04
	 */
	public static class FileSystemException extends Exception {
		/**
		 * 
		 */
		private static final long serialVersionUID = -7191975779800447811L;

		/**
		 * Default no arg constructor
		 */
		public FileSystemException() {
			super();
		}

		/**
		 * @param message
		 *            a message for the Exception
		 */
		public FileSystemException(String message) {
			super(message);
		}

		/**
		 * @param message
		 *            a message for the Exception
		 * @param cause
		 *            a Throwable cause for this Exception
		 */
		public FileSystemException(String message, Throwable cause) {
			super(message, cause);
		}
	}
}
