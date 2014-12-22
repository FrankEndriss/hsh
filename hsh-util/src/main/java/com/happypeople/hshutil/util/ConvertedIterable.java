package com.happypeople.hshutil.util;

import java.io.Closeable;
import java.io.IOException;
import java.util.Iterator;

/** Filters the outcome of an Iterable<I> throug a given OneToOneConverter<I, O>
 * @param <I> the input type
 * @param <O> the output type
 */
public class ConvertedIterable<I, O> implements Iterable<O>, Closeable {

	private final Iterable<I> delegate;
	private final OneToOneConverter<I, O> oneToOneConverter;

	public ConvertedIterable(final Iterable<I> delegate, final OneToOneConverter<I, O> filter) {
		if(delegate==null)
			throw new IllegalArgumentException("delegate must not be null");
		if(filter==null)
			throw new IllegalArgumentException("oneToOneConverter must not be null");
		this.delegate=delegate;
		this.oneToOneConverter=filter;
	}

	public void close() throws IOException {
		if(delegate instanceof Closeable)
			((Closeable)delegate).close();
	}

	public Iterator<O> iterator() {
		return new ConvertedIterator<I, O>(delegate.iterator(), oneToOneConverter);
	}
}
