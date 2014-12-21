package com.happypeople.hshutil.util;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;

/** This class implements the concept of a closeable queue. For simplyfication all methods are declared synchronized.
 * @param <E> the element type
 */
public class AsyncIterator<E> implements Iterator<E> {
	private boolean closed=false;
	private final Queue<E> elements=new LinkedList<E>();

	/** Creates an initially empty Iterator<E>
	 */
	public AsyncIterator() {
	}

	/** Creates an Iterator initially filled whith the elements of the argument
	 * @param elements element data
	 */
	public AsyncIterator(final Iterable<E> elements) {
		this(elements.iterator());
	}

	/** Creates an Iterator initially filled whith the elements of the argument
	 * @param elements element data
	 */
	public AsyncIterator(final Iterator<E> elements) {
		while(elements.hasNext())
			this.elements.offer(elements.next());
	}

	/**
	 * @see java.util.Iterator#hasNext()
	 * Probably blocks until an element is available or this Iterator is closed
	 */
	@Override
	public synchronized boolean hasNext() {
		while(true)
			if(elements.size()>0)
				return true;
			else
				if(closed)
					return false;
				else
					doWait();
	}

	/**
	 * @see java.util.Iterator#next()
	 * Probably blocks until an element is available or this Iterator is closed
	 */
	@Override
	public synchronized E next() {
		while(true)
			if(elements.size()>0)
				return elements.poll();
			else
				if(closed)
					throw new NoSuchElementException("empty and closed");
				else
					doWait();
	}

	@Override
	public void remove() {
		// remove does not makes sence on an Iterator like this.
		throw new UnsupportedOperationException();
	}

	/** Puts an element into the iterator
	 * @param element
	 */
	public synchronized void offer(final E element) {
		elements.offer(element);
		notifyAll();
	}

	/** Closes this Iterator
	 */
	public synchronized void close() {
		closed=true;
		notifyAll();
	}

	private synchronized void doWait() {
		try {
			wait();
		} catch (final InterruptedException e) {
			// ignore
		}
	}
}
