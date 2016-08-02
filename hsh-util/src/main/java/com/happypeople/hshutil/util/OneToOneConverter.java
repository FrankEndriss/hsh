/**
 */
package com.happypeople.hshutil.util;

/**
 * Converter for arbitrary types, converts allways one object of type I to
 * exactly one object of type O.
 * @author Frank Endriss (fj.endriss@gmail.com)
 * @version $Id$
 * @param <I> Input object type
 * @param <O> Output object type
 * @since 0.1
 */
public interface OneToOneConverter<I, O> {
    /**
     * Convert input to output.
     * @param input The input object
     * @return The output object, must not be null
     */
    O convert(I input);
}
