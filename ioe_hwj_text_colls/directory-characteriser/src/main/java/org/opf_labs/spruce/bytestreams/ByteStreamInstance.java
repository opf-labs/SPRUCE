/**
 * 
 */
package org.opf_labs.spruce.bytestreams;


/**
 * TODO JavaDoc for ByteStreamInstance.</p>
 * TODO Tests for ByteStreamInstance.</p>
 * TODO Implementation for ByteStreamInstance.</p>
 * 
 * @author  <a href="mailto:carl@openplanetsfoundation.org">Carl Wilson</a>.</p>
 *          <a href="https://github.com/carlwilson">carlwilson AT github</a>.</p>
 * @version 0.1
 * 
 * Created 13 Sep 2012:09:57:30
 */

public interface ByteStreamInstance {
	/**
	 * @author <a href="mailto:carl@openplanetsfoundation.org">Carl Wilson</a>
	 *         <a href="https://github.com/carlwilson">carlwilson AT github</a>
	 * @version 0.1 Created 12 Jul 2012:02:41:29
	 */
	public enum ByteStreamStatus {
		/** indicates a damaged entry */
		DAMAGED,
		/** indicates a lost entry that was believed to be there */
		LOST,
		/** indicates all is OK */
		OK,
		/** indicates not yet checked */
		UNCHECKED;
	}

	/**
	 * @return the status of the entry
	 */
	public ByteStreamStatus getByteStreamStatus();

	/**
	 * @return true if the entry has been checked
	 */
	public boolean isChecked();

	/**
	 * @return true if the entry is OK
	 */
	public boolean isOK();

	/**
	 * @return the ByteStreamId
	 */
	public ByteStreamId getByteStreamId();

	/**
	 * @return for damaged entries (getStatus() ==
	 *         FileSystemEntryStatus.DAMAGED) or lost (getStatus() ==
	 *         FileSystemEntryStatus.LOST) entries the exception that caused the
	 *         status, or a null pointer exception if no problems have been found (getStatus() ==
	 *         FileSystemEntryStatus.OK), or (getStatus() ==
	 *         FileSystemEntryStatus.UNCHECKED)
	 */
	public Exception getException();

}
