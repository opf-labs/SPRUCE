package org.opf_labs.spruce.filesystems;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;

/**
 * TODO JavaDoc for JavaIOFileSystem
 * TODO Tests for JavaIOFileSystem
 * TODO Implementation for JavaIOFileSystem
 * 
 * @author  <a href="mailto:carl@openplanetsfoundation.org">Carl Wilson</a>
 *          <a href="https://github.com/carlwilson">carlwilson AT github</a>
 * @version 0.1
 * 
 * Created 27 Jul 2012:22:42:52
 */
public final class JavaIOFileSystem extends AbstractFileSystem {
	private final static String TYPE = "[Unchecked]";
	private Iterator<FileSystemEntry> entryIterator;
	private FileSystemEntry currentEntry;
	JavaIOFileSystem(final String name, final String path,
			final long totalSize, final Map<String, FileSystemEntry> entries) {
		super(name, path, TYPE,
				totalSize, entries);
		this.entryIterator = this.entries.values().iterator(); 
	}


	static final long populateEntriesFromDirectory(File dir, Map<String, FileSystemEntry> entries, String rootPath) {
		long totalSize = 0L;
		File[] children = dir.listFiles();
		if (children != null) {
			// Loop through the java.io.File children
			for (File child : children) {
				if (child.isDirectory()) {
					totalSize += populateEntriesFromDirectory(child, entries, rootPath);
				} else {
					String entryName = FileSystems.relatavisePaths(rootPath, child.getAbsolutePath());
					entries.put(entryName, FileSystemEntries.fromFile(entryName, child));
					totalSize += child.length();
				}
			}
		}
		return totalSize;
	}

	/**
	 * @param name
	 * @return
	 * @throws EntryNotFoundException
	 * @throws DamagedEntryException
	 */
	public InputStream getEntryStream(String name)
			throws EntryNotFoundException, DamagedEntryException {
		try {
			return new FileInputStream(this.getEntryFile(name));
		} catch (FileNotFoundException excep) {
			throw new DamagedEntryException("", excep);
		}
	}

	/**
	 * @param name
	 * @return
	 * @throws UnsupportedOperationException
	 * @throws EntryNotFoundException
	 * @throws DamagedEntryException
	 */
	public File getEntryFile(String name) throws UnsupportedOperationException,
			EntryNotFoundException, DamagedEntryException {
		return new File(this.path + File.separator + name);
	}

	@Override
	public FileSystemEntry getNextEntry() {
		// TODO Auto-generated method stub
		if (!this.entryIterator.hasNext()) return null;
		this.currentEntry = this.entryIterator.next();
		return this.currentEntry;
	}

	@Override
	public InputStream getEntryStream() throws EntryNotFoundException, DamagedEntryException {
		// TODO Auto-generated method stub
		return this.getEntryStream(this.currentEntry.getName());
	}

	@Override
	public void resetEntries() {
		this.entryIterator = this.entries.values().iterator();
	}

	@Override
	public void resetEntries(InputStream stream) {
		this.resetEntries();
	}

}
