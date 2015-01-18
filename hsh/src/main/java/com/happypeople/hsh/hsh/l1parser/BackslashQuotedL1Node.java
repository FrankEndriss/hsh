package com.happypeople.hsh.hsh.l1parser;

import java.util.Collections;
import java.util.Iterator;

import com.happypeople.hsh.hsh.L2Token;

/** A char escaped by an backslash. Because thats allways one char, the len is fixed to two chars.
 */
public class BackslashQuotedL1Node extends AbstractL1Node {
	public BackslashQuotedL1Node(final L2Token tok, final int off, final int len) {
		super(tok, off, 2);
		if(len!=2)
			throw new RuntimeException("len of escaped char !=2...something went wrong. :/");
	}

	@Override
	public Iterator<L1Node> iterator() {
		return Collections.EMPTY_LIST.iterator();
	}

}
