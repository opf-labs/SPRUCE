/**
 * 
 */
package org.opf_labs.spruce.bytestreams;

/**
 * TODO: Beter JavaDoc for ByteStreamDetailsImpl.
 * <p/>
 * Immutable implementation of the Byte Sequence interface, reference
 * implementation of {@link ByteStreamId}.
 * 
 * @author <a href="mailto:carl@openplanetsfoundation.org">Carl Wilson</a> <a
 *         href="https://github.com/carlwilson">carlwilson AT github</a>
 * @version 0.1 Created 8 Jul 2012:19:56:33
 */

final class ByteStreamDetailsImpl implements ByteStreamId {
	private final String hexSHA256;
	private final String hexMD5;
	private final long length;

	private ByteStreamDetailsImpl() {
		this.hexSHA256 = ByteStreams.NULL_SHA256;
		this.hexMD5 = ByteStreams.NULL_MD5;
		this.length = 0L;
	}

	private ByteStreamDetailsImpl(final long length, final String sha256,
			final String md5) {
		this.length = length;
		this.hexSHA256 = sha256;
		this.hexMD5 = md5;
	}

	static final ByteStreamId fromValues(final long length,
			final String sha256, final String md5) {
		if (length < 0L)
			throw new IllegalArgumentException("(length " + length + " < 0) == true");
		if (!ByteStreams.isHexSHA256(sha256))
			throw new IllegalArgumentException(
					"ByteStreams.isHexSHA256(sha256) != true, regex used: " + ByteStreams.HEX_SHA256_REGEX);
		if (!ByteStreams.isHexMD5(md5))
			throw new IllegalArgumentException(
					"ByteStreams.isHexMD5(md5) != true, regex used: " + ByteStreams.HEX_MD5_REGEX);
		if ((sha256 == null) || (sha256.isEmpty()))
			throw new IllegalArgumentException("((sha256 == null) || (sha256.isEmpty())) == true");
		if ((md5 == null) || (md5.isEmpty()))
			throw new IllegalArgumentException("((md5 == null) || (md5.isEmpty())) == true");
		if (length < 0L)
			throw new IllegalArgumentException("(length " + length + " < 0) == true");
		// This is the only route to the constructor, lower case the hash values to prevent
		// case sensitivity ruining equals and hashCode.  Upper and lower case hex digest strings 
		// are equivalent.
		return new ByteStreamDetailsImpl(length, sha256.toLowerCase(), md5.toLowerCase());
	}

	/**
	 * @see uk.ac.ox.bodleian.beam.filesystem.bytestream.ByteStreamId#getLength()
	 */
	@Override
	public final long getLength() {
		return this.length;
	}

	/**
	 * @see uk.ac.ox.bodleian.beam.filesystem.bytestream.ByteStreamId#getHexSHA256()
	 */
	@Override
	public final String getHexSHA256() {
		return this.hexSHA256;
	}

	/**
	 * @see uk.ac.ox.bodleian.beam.filesystem.bytestream.ByteStreamId#getHexMD5()
	 */
	@Override
	public final String getHexMD5() {
		return this.hexMD5;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ByteStreamId [sha256=" + this.hexSHA256 + ", md5="
				+ this.hexMD5 + ", length=" + this.length + " bytes]";
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof ByteStreamId))
			return false;
		ByteStreamId other = (ByteStreamId) obj;
		if (this.length != other.getLength())
			return false;
		if (this.hexMD5 == null) {
			if (other.getHexMD5() != null)
				return false;
		} else if (!this.hexMD5.equals(other.getHexMD5()))
			return false;
		if (this.hexSHA256 == null) {
			if (other.getHexSHA256() != null)
				return false;
		} else if (!this.hexSHA256.equals(other.getHexSHA256()))
			return false;
		return true;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (this.length ^ (this.length >>> 32));
		result = prime * result
				+ ((this.hexMD5 == null) ? 0 : this.hexMD5.hashCode());
		result = prime * result
				+ ((this.hexSHA256 == null) ? 0 : this.hexSHA256.hashCode());
		return result;
	}
}
