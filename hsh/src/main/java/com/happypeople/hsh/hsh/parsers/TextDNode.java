package com.happypeople.hsh.hsh.parsers;

import java.util.Collections;
import java.util.Iterator;

import com.happypeople.hshutil.util.AsyncIterator;

/** The leaves in the DNode tree.
 * TODO change Object to Token
 */
public class TextDNode implements DNode {
	private final AsyncIterator<Object> tokens=new AsyncIterator<Object>();

	/** A TextDNode has no DNode children. */
	@Override
	public Iterator<DNode> iterator() {
		return Collections.EMPTY_LIST.iterator();
	}

	@Override
	public void offer(final DNode child) {
		throw new RuntimeException("not possible");
	}

	/** Called from the DefaultParser
	 * @param token
	 */
	public void add(final Object token) {
		tokens.offer(token);
	}

	public void close() {
		tokens.close();
	}

	/** Called from DNodeTraversal
	 * @return the iterator over the Token
	 */
	public Iterator<Object> tokenIterator() {
		return tokens;
	}
}
