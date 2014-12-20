package com.happypeople.hsh.ls;

import java.nio.file.Path;

/** This class extends a FileEntry with an AsyncIterator<FileEntry>
 * This makes sense thoug the way Files.walkFileTree(...) works. With this we can walk the File tree
 * asynchronously.
 */
public class FileEntryWithIterator extends FileEntry {

	private final AsyncIterator<FileEntry> files;

	FileEntryWithIterator(final Path path, final AsyncIterator<FileEntry> files) {
		super(path);
		this.files=files;
	}

	/**
	 * @return the iterator given in the constructor. Note that it makes not much sence to call this
	 * method more than once, since it is an Iterator, not an Iterable.
	 */
	public AsyncIterator<FileEntry> getFilesIterator() {
		return files;
	}

}
