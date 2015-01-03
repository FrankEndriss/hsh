package com.happypeople.hsh.hsh.parser;

/** Interface used in Parser to denote state that a Backgroundable is _not_ executed in background before
 * the method returns.
 */
public interface NoBackgroundCallback {
	public void noBackground();
}
