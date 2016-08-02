/**
 */
package com.happypeople.hshutil.util;

/** Simple immutable implementation of a pair of objects.
 * @author Frank Endriss (fj.endriss@gmail.com)
 * @version $Id$
 * @param <F> type of first
 * @param <S> type of second
 * @since 0.1
 */
public class Pair<F, S> {
    /** The first object.
     */
    private final F first;
    /** The second object.
     */
    private final S second;

    /**
     * Constructs a Pair of two typed object references.
     * @param first The first object
     * @param second The second object
     */
    public Pair(final F first, final S second) {
        this.first = first;
        this.second = second;
    }

    /**
     * Query the first object.
     * @return The first object.
     */
    public final F getFirst() {
        return this.first;
    }

    /**
     * Query the second object.
     * @return The second object.
     */
    public final S getSecond() {
        return this.second;
    }
}
