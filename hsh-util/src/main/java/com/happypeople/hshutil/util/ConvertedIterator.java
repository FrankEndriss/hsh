/**
 */
package com.happypeople.hshutil.util;

import java.util.Iterator;

/** Converts the type of an Iterator by converting all objects
 * returned by an Iterator.
 * @author Frank Endriss (fj.endriss@gmail.com)
 * @version $Id$
 * @param <I> The input type
 * @param <O> The converted type
 * @since 0.1
 */
public class ConvertedIterator<I, O> implements Iterator<O> {
    /** The delegate Iterator.
     */
    private final Iterator<I> delegate;
    /** The converter object converting all objects returned by delegate.
     */
    private final OneToOneConverter<I, O> converter;

    /**
     * Only one constructor.
     * @param delegate The delegate Iterator
     * @param converter The converter to convert the objects
     */
    public ConvertedIterator(final Iterator<I> delegate,
        final OneToOneConverter<I, O> converter) {
        this.delegate = delegate;
        this.converter = converter;
    }

    @Override
    public final boolean hasNext() {
        return this.delegate.hasNext();
    }

    @Override
    public final O next() {
        return this.converter.convert(this.delegate.next());
    }

    @Override
    public final void remove() {
        this.delegate.remove();
    }
}
