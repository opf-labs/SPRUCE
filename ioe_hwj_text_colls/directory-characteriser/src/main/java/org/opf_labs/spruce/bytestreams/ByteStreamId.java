/**
 * 
 */
package org.opf_labs.spruce.bytestreams;

/**
 * Encapsulates a few byte stream properties:
 * <ul>
 * <li>The length of the ByteStream in bytes, will be >= 0.</li>
 * <li>The hex encoded MD5 digest calculated from the ByteStream, a 32 character
 * hex string, i.e. [0-9a-f]{32}.</li>
 * <li>The hex encoded SHA256 digest calculated from the ByteStream, a 64
 * character hex string, i.e. [0-9a-f]{64}.</li>
 * </ul>
 * Used as an identifier for tagging richer metadata and associating it with a
 * ByteStream.
 * 
 * @author <a href="mailto:carl@openplanetsfoundation.org">Carl Wilson</a> <a
 *         href="https://github.com/carlwilson">carlwilson AT github</a>
 * @version 0.1 Created 02 Jul 2012:02:37:33
 */

public interface ByteStreamId {
	/**
	 * @return The length of the ByteStream in bytes, will be >= 0.
	 */
	public long getLength();

	/**
	 * @return The hex encoded SHA256 digest calculated from the ByteStream, a
	 *         64 character hex string, i.e. [0-9a-f]{64}.
	 */
	public String getHexSHA256();

	/**
	 * @return The hex encoded MD5 digest calculated from the ByteStream, a 32
	 *         character hex string, i.e. [0-9a-f]{32}.
	 */
	public String getHexMD5();
}
