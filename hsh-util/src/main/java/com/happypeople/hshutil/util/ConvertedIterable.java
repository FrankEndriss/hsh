/**
 */
package com.happypeople.hshutil.util;

import java.io.Closeable;
import java.io.IOException;
import java.util.Iterator;

/**
 * Filters the outcome of an Iterable throug a given OneToOneConverter.
 * @author Frank Endriss (fj.endriss@gmail.com)
 * @version $Id$
 * @param <I> the input type
 * @param <O> the output type
 * @since 0.1
 */
public class ConvertedIterable<I, O> implements Iterable<O>, Closeable {
    /**
     * The delegate Iterable.
     */
    private final Iterable<I> delegate;
    /**
     * The converter converting the outcome of delegate.
     */
    private final OneToOneConverter<I, O> converter;

    /**
     * Only one constructor.
     * @param delegate The delegate to use
     * @param converter The converter to convert the outcome of delegate
     */
    public ConvertedIterable(final Iterable<I> delegate,
        final OneToOneConverter<I, O> converter) {
        this.delegate = checkDelegate(delegate);
        this.converter = checkConverter(converter);
    }

    @Override
    public final void close() throws IOException {
        if (this.delegate instanceof Closeable) {
            ((Closeable) this.delegate).close();
        }
    }

    @Override
    public final Iterator<O> iterator() {
        return new ConvertedIterator<>(
            this.delegate.iterator(), this.converter
        );
    }

    /**
     * Checks constructor parameter "delegate".
     * @param delegate Reference to check for not null
     * @param <I> The Iterable type
     * @return The delegate
     */
    private static <I> Iterable<I> checkDelegate(final Iterable<I> delegate) {
        if (delegate == null) {
            throw new IllegalArgumentException("delegate must not be null");
        }
        return delegate;
    }

    /**
     * Checks constructor parameter "converter".
     * @param converter Reference to check for not null
     * @param <I> The Iterable type
     * @param <O> The Outcome type
     * @return The converter
     */
    private static <I, O> OneToOneConverter<I, O> checkConverter(
        final OneToOneConverter<I, O> converter) {
        if (converter == null) {
            throw new IllegalArgumentException(
                "oneToOneConverter must not be null"
            );
        }
        return converter;
    }
}
