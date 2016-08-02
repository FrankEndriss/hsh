/**
 */
package com.happypeople.hshutil.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Conncatenates two Iterators to act as one Iterator.
 * @author Frank Endriss (fj.endriss@gmail.com)
 * @version $Id$
 * @param <E> The type of the Iterators
 * @since 0.1
 */
public class ConcatIterator<E> implements Iterator<E> {
    /**
     * The first Iterator.
     */
    private Iterator<? extends E> itfirst;
    /**
     * The second Iterator.
     */
    private final Iterator<? extends E> itsecond;

    /**
     * Only one constructor, concatenates the two argument Iterators.
     * @param itfirst The first Iterator, must not be null
     * @param itsecond The second Iterator, must not be null
     */
    public ConcatIterator(final Iterator<? extends E> itfirst,
        final Iterator<? extends E> itsecond) {
        this.itfirst = checkNotNull("itfirst", itfirst);
        this.itsecond = checkNotNull("itsecond", itsecond);
    }

    @Override
    public final boolean hasNext() {
        boolean ret = this.itfirst.hasNext();
        if (!ret && this.itfirst != this.itsecond) {
            this.itfirst = this.itsecond;
            ret = this.itfirst.hasNext();
        }
        return ret;
    }

    @Override
    public final E next() {
        E ret;
        if (this.itfirst == this.itsecond) {
            ret = this.itfirst.next();
        } else {
            try {
                ret = this.itfirst.next();
            } catch (final NoSuchElementException exep) {
                this.itfirst = this.itsecond;
                ret = this.itfirst.next();
            }
        }
        return ret;
    }

    @Override
    public final void remove() {
        this.itfirst.remove();
    }

    /**
     * Checks constructor arguments for non null.
     * @param msg Message for exception
     * @param iterator Argument to check for not null
     * @param <E> Type of the Iterator
     * @return The iterator reference
     */
    private static <E> Iterator<? extends E> checkNotNull(final String msg,
        final Iterator<? extends E> iterator) {
        if (iterator == null) {
            throw new IllegalArgumentException(
                new StringBuilder(msg).append(" must not be null").toString()
            );
        }
        return iterator;
    }
}
