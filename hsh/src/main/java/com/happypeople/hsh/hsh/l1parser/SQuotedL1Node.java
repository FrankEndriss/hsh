package com.happypeople.hsh.hsh.l1parser;

import java.util.Collections;
import java.util.Iterator;

import com.happypeople.hsh.hsh.L2Token;

public class SQuotedL1Node extends AbstractL1Node {
	public SQuotedL1Node(final L2Token tok, final int off, final int len) {
		super(tok, off, len);
	}

	@Override
	public Iterator<L1Node> iterator() {
		return Collections.EMPTY_LIST.iterator();
	}
}
