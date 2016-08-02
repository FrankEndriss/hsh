/**
 */
package com.happypeople.hshutil.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * Combines a list of Comparator into one Comparator.
 * @author Frank Endriss (fj.endriss@gmail.com)
 * @version $Id$
 * @param <T> The compared type
 * @since 0.1
 */
public class CombinedComparator<T> implements Comparator<T>, Serializable {
    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 1L;
    /**
     * List of Comparators, first has highest priority, last lowest.
     */
    private transient List<Comparator<? super T>> comparators;

    /**
     * Only one constructor.
     * @param comparators The first entry in comparator has highest priority
     */
    public CombinedComparator(
        final Iterable<Comparator<? super T>> comparators) {
        this.comparators = initComparatorList(comparators);
    }

    @Override
    public final int compare(final T first, final T second) {
        int ret = 0;
        for (final Comparator<? super T> comp : this.comparators) {
            ret = comp.compare(first, second);
            if (ret != 0) {
                break;
            }
        }
        return ret;
    }

    /**
     * Serialization contract. Note that this method is not implemented.
     * @param stream The stream to read from.
     * @throws IOException If stream throws it
     * @throws ClassNotFoundException If stream throws it
     */
    private void readObject(final ObjectInputStream stream)
        throws ClassNotFoundException, IOException {
        this.comparators = (List<Comparator<? super T>>) stream.readObject();
    }

    /**
     * Initializes the list of Comparators.
     * @param comps The initial list of Compoarators
     * @param <T> Type of Comparators
     * @return A new List of Comparators
     */
    private static <T> List<Comparator<? super T>> initComparatorList(
        final Iterable<Comparator<? super T>> comps) {
        final List<Comparator<? super T>> comparators = new LinkedList<>();
        for (final Comparator<? super T> comp : comps) {
            comparators.add(comp);
        }
        return comparators;
    }

}
