/**
 * 
 */
package org.opf_labs.spruce.filesystems;

import java.util.Date;

/**
 * TODO: Beter JavaDoc for AbstractFileSystemEntry.
 * <p/>
 * TODO: JavaDoc for inheritance
 * 
 * @author <a href="mailto:carl.wilson@keepitdigital.eu">Carl Wilson</a> <a
 *         href="https://github.com/carlwilson">carlwilson AT github</a>
 * @version 0.1 Created 12 Jul 2012:00:44:17
 */
final class FileSystemEntryImpl implements FileSystemEntry {
	private final String name;
	private final long compressedSize;
	private final long size;
	private final long modifiedMillis;

	private FileSystemEntryImpl() {
		this.name = "";
		this.compressedSize = 0L;
		this.size = 0L;
		this.modifiedMillis = 0L;
	}

	private FileSystemEntryImpl(final String name, final long size,
			final long modified) {
		this(name, size, size, modified);
	}

	private FileSystemEntryImpl(final String name,
			final long compressedSize, final long size, final long modified) {
		// OK all roads lead to this constructor so do the arg checks once here
		if (name == null)
			throw new IllegalArgumentException("name == null");
		if (compressedSize < 0L)
			throw new IllegalArgumentException("(compressedSize "
					+ compressedSize + " < 0) == true");
		if (size < 0L)
			throw new IllegalArgumentException("(size " + size
					+ " < 0) == true");
		// Check consistency
		if (compressedSize > size)
			throw new IllegalArgumentException(
					"(compressedSize > size) == true");
		if ((compressedSize == 0) && (size != 0))
			throw new IllegalArgumentException(
					"((compressedSize == 0) && (size != 0)) == true");
		this.name = name;
		this.compressedSize = compressedSize;
		this.size = size;
		this.modifiedMillis = modified;
	}

	static FileSystemEntry fromValues(final String name, final long size,
			final long modified) {
		return new FileSystemEntryImpl(name, size, modified);
	}

	static FileSystemEntry fromValues(final String name, final long compressedSize, final long size,
			final long modified) {
		return new FileSystemEntryImpl(name, compressedSize, size, modified);
	}

	/**
	 * @see uk.ac.ox.bodleian.beam.filesystem.entry.FileSystemEntry#getName()
	 */
	@Override
	public final String getName() {
		return this.name;
	}

	/**
	 * @see uk.ac.ox.bodleian.beam.filesystem.entry.FileSystemEntry#getCompressedSize()
	 */
	@Override
	public final long getCompressedSize() {
		return this.compressedSize;
	}

	/**
	 * @see uk.ac.ox.bodleian.beam.filesystem.entry.FileSystemEntry#getSize()
	 */
	@Override
	public final long getSize() {
		return this.size;
	}

	/**
	 * @see uk.ac.ox.bodleian.beam.filesystem.entry.FileSystemEntry#getModifiedDate()
	 */
	@Override
	public final Date getModifiedDate() {
		return new Date(this.modifiedMillis);
	}

	/**
	 * @see uk.ac.ox.bodleian.beam.filesystem.entry.FileSystemEntry#getModifiedMillis()
	 */
	@Override
	public final long getModifiedMillis() {
		return this.modifiedMillis;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AbstractFileSystemEntry [name=" + this.name
				+ ", compressedSize=" + this.compressedSize + ", size="
				+ this.size + ", modified=" + this.getModifiedDate()
				+ "]";
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public final int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.name == null) ? 0 : this.name.hashCode());
		result = prime * result
				+ (int) (this.compressedSize ^ (this.compressedSize >>> 32));
		result = prime * result + (int) (this.size ^ (this.size >>> 32));
		result = prime * result
				+ (int) (this.modifiedMillis ^ (this.modifiedMillis >>> 32));
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public final boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof FileSystemEntry))
			return false;
		FileSystemEntry other = (FileSystemEntry) obj;
		if (this.compressedSize != other.getCompressedSize())
			return false;
		if (this.modifiedMillis != other.getModifiedDate().getTime())
			return false;
		if (this.name == null) {
			if (other.getName() != null)
				return false;
		} else if (!this.name.equals(other.getName()))
			return false;
		if (this.size != other.getSize())
			return false;
		return true;
	}
}
