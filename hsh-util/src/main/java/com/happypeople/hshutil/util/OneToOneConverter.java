package com.happypeople.hshutil.util;

/** Converter for arbitrary types, converts allways one object of type I to exactly one object of type O
 * @param <I> input object type
 * @param <O> output object type
 */
public interface OneToOneConverter<I, O> {
	public O convert(I input);
}
