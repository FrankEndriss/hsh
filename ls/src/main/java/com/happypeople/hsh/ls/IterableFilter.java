package com.happypeople.hsh.ls;

import java.io.Closeable;
import java.io.IOException;
import java.util.Iterator;

/** Filters the outcome of an Iterable throug a given OneToOneConverter
 * @param <I> the input type
 * @param <O> the output type
 */
public class IterableFilter<I, O> implements Iterable<O>, Closeable {

	private final Iterable<I> delegate;
	private final OneToOneConverter<I, O> oneToOneConverter;
	private final Iterator<I> preIterator;
	private Iterator<I> delegateIterator;

	public IterableFilter(final Iterable<I> delegate, final OneToOneConverter<I, O> filter, final Iterator<I> preIterator) {
		if(delegate==null)
			throw new IllegalArgumentException("delegate must not be null");
		if(filter==null)
			throw new IllegalArgumentException("oneToOneConverter must not be null");
		this.preIterator=preIterator;
		this.delegate=delegate;
		this.oneToOneConverter=filter;
	}

	@Override
	public void close() throws IOException {
		if(delegate instanceof Closeable)
			((Closeable)delegate).close();
	}

	@Override
	public Iterator<O> iterator() {
		// TODO use preIterator if !=null
		delegateIterator=delegate.iterator();
		return new Iterator<O>() {
			@Override
			public boolean hasNext() {
				return delegateIterator.hasNext();
			}

			@Override
			public O next() {
				return oneToOneConverter.convert(delegateIterator.next());
			}
			@Override
			public void remove() {
				delegateIterator.remove();
			}
		};
	}
}
