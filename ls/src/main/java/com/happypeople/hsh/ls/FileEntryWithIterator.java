/**
 */
package com.happypeople.hsh.ls;

import com.happypeople.hshutil.util.AsyncIterator;
import java.nio.file.Path;

/**
 * This class extends a FileEntry with an AsyncIterator of FileEntry. This makes
 * sense thoug the way Files.walkFileTree(...) works. With this we can walk the
 * File tree asynchronously.
 */
class FileEntryWithIterator extends FileEntry {

    /**
     * Iterator over the file rooted at path.
     */
    private final AsyncIterator<FileEntry> files;

    /** The only one constructor.
     * @param path The path to the file
     * @param files An Iterator iterating all files given path is the
     *  root of a file tree.
     */
    FileEntryWithIterator(final Path path,
        final AsyncIterator<FileEntry> files) {
        super(path);
        this.files = files;
    }

    /**
     * Query the Iterator.
     * @return The iterator given in the constructor. Note that it makes not
     *  much sense to call this method more than once, since it is an
     *  Iterator, not an Iterable.
     */
    AsyncIterator<FileEntry> getFilesIterator() {
        return this.files;
    }
}
