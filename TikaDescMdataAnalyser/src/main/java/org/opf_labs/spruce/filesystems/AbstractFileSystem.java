/**
 * 
 */
package org.opf_labs.spruce.filesystems;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * TODO: Better JavaDoc for AbstractFileSystem
 * <p/>
 * TODO: JavaDoc for inheritance
 * <p/>
 * TODO: Tests for FileSystem
 * <p/>
 * 
 * @author <a href="mailto:carl@openplanetsfoundation.org">Carl Wilson</a> <a
 *         href="https://github.com/carlwilson">carlwilson AT github</a>
 * @version 0.1 Created 13 Jul 2012:13:00:06
 */

abstract class AbstractFileSystem implements
		FileSystem {
	protected static Logger LOGGER = Logger.getLogger(AbstractFileSystem.class);
	protected final String name;
	protected final String path;
	protected final String type;
	protected final Date created = new Date();
	protected final long totalSize;
	protected final Map<String, FileSystemEntry> entries;

	AbstractFileSystem(final String name, final String path, final String type,
			final long totalSize, final Map<String, FileSystemEntry> entries) {
		if (name == null) {
			LOGGER.fatal("name == null");
			throw new IllegalArgumentException("name == null");
		}
		if (path == null) {
			LOGGER.fatal("path == null");
			throw new IllegalArgumentException("path == null");
		}
		if (type == null) {
			LOGGER.fatal("type == null");
			throw new IllegalArgumentException("type == null");
		}
		if (totalSize < 0) {
			LOGGER.fatal("totalSize < 0");
			throw new IllegalArgumentException("totalSize < 0");
		}
		if (entries == null) {
			LOGGER.fatal("entries == null");
			throw new IllegalArgumentException("entries == null");
		}
		this.name = name;
		this.path = path;
		this.type = type;
		this.totalSize = totalSize;
		this.entries = entries;
	}

	/**
	 * @see uk.ac.ox.bodleian.beam.filesystem.FileSystem#getName()
	 */
	@Override
	public final String getName() {
		return this.name;
	}

	/**
	 * @see uk.ac.ox.bodleian.beam.filesystem.FileSystem#getPath()
	 */
	@Override
	public final String getPath() {
		return this.path;
	}

	/**
	 * @see uk.ac.ox.bodleian.beam.filesystem.FileSystem#getCreatedDate()
	 */
	@Override
	public Date getCreatedDate() {
		return this.created;
	}

	/**
	 * @see uk.ac.ox.bodleian.beam.filesystem.FileSystem#getEntryCount()
	 */
	@Override
	public int getEntryCount() {
		return this.entries.size();
	}

	/**
	 * @see uk.ac.ox.bodleian.beam.filesystem.FileSystem#getTotalBytes()
	 */
	@Override
	public long getTotalBytes() {
		return this.totalSize;
	}

	/**
	 * @see uk.ac.ox.bodleian.beam.filesystem.FileSystem#getEntries()
	 */
	@Override
	public Set<FileSystemEntry> getEntries() {
		return Collections.unmodifiableSet(new HashSet<FileSystemEntry>(this.entries.values()));
	}

	/**
	 * @see uk.ac.ox.bodleian.beam.filesystem.FileSystem#getType()
	 */
	@Override
	public final String getType() {
		return this.type;
	}


	/**
	 * @see uk.ac.ox.bodleian.beam.filesystem.FileSystem#getEntryNames()
	 */
	@Override
	public final Set<String> getEntryNames() {
		return Collections.unmodifiableSet(this.entries.keySet());
	}

	@Override
	public FileSystemEntry getEntry(String name) throws EntryNotFoundException {
		if (this.entries.containsKey(name))
			return this.entries.get(name);
		LOGGER.debug("Entry:" + name + " not found for FileSystem " + this.name);
		throw new EntryNotFoundException("Entry:" + name + " not found for FileSystem " + this.name);
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "FileSystem [name=" + this.name + ", path=" + this.path
				+ ", totalSize=" + this.totalSize + ", entries="
				+ ((this.entries == null) ? 0L : this.entries.size()) + "]";
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.created == null) ? 0 : this.created.hashCode());
		result = prime * result
				+ ((this.entries == null) ? 0 : this.entries.hashCode());
		result = prime * result
				+ ((this.name == null) ? 0 : this.name.hashCode());
		result = prime * result
				+ ((this.path == null) ? 0 : this.path.hashCode());
		result = prime * result
				+ (int) (this.totalSize ^ (this.totalSize >>> 32));
		result = prime * result
				+ ((this.type == null) ? 0 : this.type.hashCode());
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof AbstractFileSystem)) {
			return false;
		}
		AbstractFileSystem other = (AbstractFileSystem) obj;
		if (this.created == null) {
			if (other.created != null) {
				return false;
			}
		} else if (!this.created.equals(other.created)) {
			return false;
		}
		if (this.entries == null) {
			if (other.entries != null) {
				return false;
			}
		} else if (!this.entries.equals(other.entries)) {
			return false;
		}
		if (this.name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!this.name.equals(other.name)) {
			return false;
		}
		if (this.path == null) {
			if (other.path != null) {
				return false;
			}
		} else if (!this.path.equals(other.path)) {
			return false;
		}
		if (this.totalSize != other.totalSize) {
			return false;
		}
		if (this.type == null) {
			if (other.type != null) {
				return false;
			}
		} else if (!this.type.equals(other.type)) {
			return false;
		}
		return true;
	}
}
