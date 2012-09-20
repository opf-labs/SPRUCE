/**
 * 
 */
package org.opf_labs.spruce.filesystems;

import java.util.Date;

import org.opf_labs.spruce.bytestreams.ByteStreamId;

/**
 * Encapsulates some basic, universal properties of a FileSystemEntry. The
 * status is obtained by calling getStatus(), this determines the information
 * that can be retrieved for an entry as follows.
 * <p/>
 * <strong>ByteStreamStatus.UNCHECKED</strong>
 * <p/>
 * Indicates that the ByteStream data has yet to be checked. The entry details
 * have been populated from some kind of file system object, such as a file, or
 * a zip entry. There's no suggestion that there's a problem, just that no
 * attempt has been made to read the data.
 * <strong>ByteStreamStatus.OK</strong>
 * <p/>
 * Indicates a normal, healthy entry where the data can be read it provides:
 * <ul>
 * <li>The {@link ByteStreamStatus} of the entry, always
 * {@link ByteStreamStatus} for a healthy file</li>
 * <li>The {@link ByteStreamId} for the file.</li>
 * <li>The name of the FileSystemEntry, often a relative path, e.g. a zip entry
 * name.</li>
 * <li>The date that the ByteStream was last modified.</li>
 * </ul>
 * <strong>{@link ByteStreamStatus} == DAMAGED</strong>
 * <p/>
 * Indicates that there has been a problem reading the data associated with the
 * entry, usually when trying to create the enclosed ByteStreamId instance.
 * The Byte stream details can't be calculated so it can't be returned.
 * 
 * @author <a href="mailto:carl@openplanetsfoundation.org">Carl Wilson</a> <a
 *         href="https://github.com/carlwilson">carlwilson AT github</a>
 * @version 0.1 Created 11 Jul 2012:23:33:55
 */

public interface FileSystemEntry {
	/**
	 * @return the name given to the entry, often a relative path, always a non null, non-empty string
	 */
	public String getName();

	/**
	 * @return the compressed size of the entry in bytes, as reported by the
	 *         file system, and dependent upon implementation. If the file
	 *         system doesn't support compression this should return the same
	 *         value as getSize(). Always returns a value > -1.
	 */
	public long getCompressedSize();

	/**
	 * @return the size of the entry in bytes, as reported by the file system,
	 *         and dependent upon implementation. This may not be the same as
	 *         the size of the ByteStream, due to compression, or a file system
	 *         error. Always returns a value > -1.
	 */
	public long getSize();

	/**
	 * @return the date that the entry was modified
	 */
	public Date getModifiedDate();
	
	/**
	 * @return
	 */
	public long getModifiedMillis();
}
