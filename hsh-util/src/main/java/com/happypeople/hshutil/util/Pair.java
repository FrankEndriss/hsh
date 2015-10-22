package com.happypeople.hshutil.util;

/** Simple immutable implementation of a pair of objects.
 * @param <T1> type of object 1
 * @param <T2> type of object 2
 */
public class Pair<T1, T2> {
	private final T1 o1;
	private final T2 o2;

	public Pair(final T1 o1, final T2 o2) {
		this.o1=o1;
		this.o2=o2;
	}

	public T1 getO1() {
		return o1;
	}

	public T2 getO2() {
		return o2;
	}

}
