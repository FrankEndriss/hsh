package com.happypeople.hsh.ls;

import java.nio.file.attribute.FileTime;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/** Wrapper for FileTime needed to implement the toString()-method.
 */
public class FileTimeWrapper implements Comparable<FileTimeWrapper> {
	private FileTime delegate;

	public FileTimeWrapper(FileTime fileTime) {
		this.delegate=fileTime;
	}
	
	public FileTime getFileTime() {
		return delegate;
	}
	
	public final static DateFormat DEFAULT_DATE_FORMAT=new SimpleDateFormat("d M HH:mm");
	public String toString() {
		// TODO: other format for times older than 6 months according to posix
		return DEFAULT_DATE_FORMAT.format(new Date(delegate.toMillis()));
	}

	public int compareTo(FileTimeWrapper o) {
		return delegate.compareTo(o.getFileTime());
	}
}
