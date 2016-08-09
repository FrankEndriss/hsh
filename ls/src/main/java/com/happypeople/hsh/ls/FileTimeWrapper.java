package com.happypeople.hsh.ls;

import java.nio.file.attribute.FileTime;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Wrapper for FileTime needed to implement the toString()-method.
 * @author Frank Endriss (fj.endriss@gmail.com)
 * @version $Id$
 * @since 0.1
 */
public class FileTimeWrapper implements Comparable<FileTimeWrapper> {
    private final FileTime delegate;

    /**
     * Only one constructor.
     * @param fileTime The delegate.
     */
    public FileTimeWrapper(final FileTime fileTime) {
        if (fileTime == null)
            throw new IllegalArgumentException("must not be null");
        this.delegate = fileTime;
    }

    /**
     * Query the delegate.
     * @return The delegate of this wrapper.
     */
    public FileTime getFileTime() {
        return delegate;
    }

    /**
     * DateFormat used in toString() for FileTimes within the last six months.
     */
    public final static DateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat(
        "dd. MMM HH:mm"
    );
    /**
     * DateFormat used in toString for FileTimes more than six months ago.
     */
    public final static DateFormat SIX_MONTH_DATE_FORMAT = new SimpleDateFormat(
        "dd. MMM yyyy"
    );

    @Override
    public String toString() {
        return System.currentTimeMillis()
            - 183L * 24 * 60 * 60 * 1000 > delegate.toMillis()
                ? SIX_MONTH_DATE_FORMAT.format(new Date(delegate.toMillis()))
                : DEFAULT_DATE_FORMAT.format(new Date(delegate.toMillis()));
    }

    @Override
    public int compareTo(final FileTimeWrapper o) {
        return delegate.compareTo(o.getFileTime());
    }
}
