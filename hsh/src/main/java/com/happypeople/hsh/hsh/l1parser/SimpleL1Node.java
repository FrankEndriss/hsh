package com.happypeople.hsh.hsh.l1parser;

import java.util.Collections;
import java.util.Iterator;

import com.happypeople.hsh.hsh.L2Token;


/** SimpleL1Node is a node with simple text. The text is interpreted to have no special meaning.
 * So, that can be text with really no special meaning, or text which has not special meaning in
 * the context where it is found.
 */
public class SimpleL1Node extends AbstractL1Node implements L1Node, StringifiableNode {

	private final int l1Kind;

	public SimpleL1Node(final L2Token tok, final int off, final int len) {
		this(tok, off, len, -1);
	}

	public SimpleL1Node(final L2Token tok, final int off, final int len, final int l1Kind) {
		super(tok, off, len);
		this.l1Kind=l1Kind;
	}

	@Override
	public Iterator<L1Node> iterator() {
		return Collections.emptyIterator();
	}

	@Override
	public void append(final StringBuilder sb) {
		sb.append(getImage());
	}

	/**
	 * @return the L1 token.kind of this word part
	 */
	public int getL1Kind() {
		return l1Kind;
	}

}
