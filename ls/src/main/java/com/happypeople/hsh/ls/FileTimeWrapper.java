package com.happypeople.hsh.ls;

import java.nio.file.attribute.FileTime;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/** Wrapper for FileTime needed to implement the toString()-method.
 */
public class FileTimeWrapper implements Comparable<FileTimeWrapper> {
	private final FileTime delegate;

	public FileTimeWrapper(final FileTime fileTime) {
		this.delegate=fileTime;
	}

	public FileTime getFileTime() {
		return delegate;
	}

	public final static DateFormat DEFAULT_DATE_FORMAT=new SimpleDateFormat("dd. LLL HH:mm");
	public final static DateFormat SIX_MONTH_DATE_FORMAT=new SimpleDateFormat("dd. LLL yyyy");

	@Override
	public String toString() {
		return System.currentTimeMillis()-183L*24*60*60*1000>delegate.toMillis()?
			SIX_MONTH_DATE_FORMAT.format(new Date(delegate.toMillis())):
			DEFAULT_DATE_FORMAT.format(new Date(delegate.toMillis()));
	}

	public int compareTo(final FileTimeWrapper o) {
		return delegate.compareTo(o.getFileTime());
	}
}
