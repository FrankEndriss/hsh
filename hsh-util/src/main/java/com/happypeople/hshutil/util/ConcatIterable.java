/**
 */
package com.happypeople.hshutil.util;

import java.util.Iterator;

/**
 * Concatenates the iterators of two Iterable.
 * @author Frank Endriss (fj.endriss@gmail.com)
 * @version $Id$
 * @param <T> the type of the Iterables/Iterators.
 * @since 0.1
 */
public class ConcatIterable<T> implements Iterable<T> {
    /**
     * The first Iterator to concatenate.
     */
    private final Iterable<? extends T> itfirst;
    /**
     * The second Iterator to concatenate.
     */
    private final Iterable<? extends T> itsecond;

    /**
     * Only one constructor.
     * @param itfirst The first Iterator
     * @param itsecond The second Iterator
     */
    public ConcatIterable(final Iterable<? extends T> itfirst,
        final Iterable<? extends T> itsecond) {
        this.itfirst = itfirst;
        this.itsecond = itsecond;
    }

    @Override
    public final Iterator<T> iterator() {
        return new ConcatIterator<>(
            this.itfirst.iterator(), this.itsecond.iterator()
        );
    }

}
