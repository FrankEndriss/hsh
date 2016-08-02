/**
 */
package com.happypeople.hshutil.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;

/**
 * This class implements the concept of a closeable queue. For simplyfication
 * all methods are declared synchronized.
 * @author Frank Endriss (fj.endriss@gmail.com)
 * @version $Id$
 * @param <E> the element type
 * @since 0.1
 */
public class AsyncIterator<E> implements Iterator<E> {
    /**
     * Flag for the colosed state.
     */
    private boolean closed;
    /**
     * Queue with the elements available in this Iterator.
     */
    private final Queue<E> elements;

    /**
     * Lock object for synchronization.
     */
    private final Object lock;

    /**
     * Creates an initially empty Iterator.
     */
    public AsyncIterator() {
        this(new ArrayList<E>(0).iterator());
    }

    /**
     * Creates an Iterator initially filled whith the elements of the argument.
     * @param elements Element data
     */
    public AsyncIterator(final Iterable<E> elements) {
        this(elements.iterator());
    }

    /**
     * Creates an Iterator initially filled whith the elements of the argument.
     * @param elements Element data
     */
    public AsyncIterator(final Iterator<E> elements) {
        this.elements = initElements(elements);
        this.lock = new Object();
    }

    @Override
    public final boolean hasNext() {
        boolean ret;
        synchronized (this.lock) {
            while (true) {
                if (this.elements.size() > 0) {
                    ret = true;
                    break;
                } else if (this.closed) {
                    ret = false;
                    break;
                } else {
                    try {
                        this.lock.wait();
                    } catch (final InterruptedException excep) {
                    }
                }
            }
        }
        return ret;
    }

    @Override
    public final E next() {
        synchronized (this.lock) {
            while (true) {
                if (this.elements.size() > 0) {
                    return this.elements.poll();
                } else if (this.closed) {
                    throw new NoSuchElementException("empty and closed");
                } else {
                    try {
                        this.lock.wait();
                    } catch (final InterruptedException excep) {
                    }
                }
            }
        }
    }

    @Override
    public final void remove() {
        throw new UnsupportedOperationException();
    }

    /**
     * Puts an element into the iterator.
     * @param element New element in the Iterator/Queue
     * @throws ClassCastException If the underlying Queue throws it
     * @throws NullPointerException If the underlying Queue throws it
     * @throws IllegalArgumentException If the underlying Queue throws it
     */
    public final void offer(final E element) {
        synchronized (this.lock) {
            try {
                this.elements.offer(element);
            } finally {
                this.lock.notifyAll();
            }
        }
    }

    /**
     * Closes this Iterator.
     */
    public final void close() {
        synchronized (this.lock) {
            this.closed = true;
            this.lock.notifyAll();
        }
    }

    /**
     * Initializes the Queue with the elements from iterator.
     * @param iterator Initial elements of this queue
     * @param <E> The type of the Iterator
     * @return The queue
     */
    private static <E> Queue<E> initElements(final Iterator<E> iterator) {
        final LinkedList<E> list = new LinkedList<>();
        while (iterator.hasNext()) {
            list.offer(iterator.next());
        }
        return list;
    }
}
