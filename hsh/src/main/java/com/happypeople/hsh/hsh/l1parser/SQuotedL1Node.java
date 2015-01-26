package com.happypeople.hsh.hsh.l1parser;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import com.happypeople.hsh.HshContext;
import com.happypeople.hsh.hsh.L2Token;

public class SQuotedL1Node extends AbstractL1Node implements QuotedL1Node {
	public SQuotedL1Node(final L2Token tok, final int off, final int len) {
		super(tok, off, len);
	}

	@Override
	public Iterator<L1Node> iterator() {
		return Collections.EMPTY_LIST.iterator();
	}

	@Override
	public SQuotedL1Node transformSubstitution(final L2Token imageHolder, final HshContext context) throws Exception {
		return this;
	}

	@Override
	public Collection<? extends L1Node> transformSplit(final HshContext context) {
		return Arrays.asList(this);
	}

	@Override
	public void appendUnquoted(final StringBuilder sb) {
		sb.append(getImageHolder().image.substring(getOff()+1, getOff()+getLen()-1));
	}

	@Override
	public SQuotedL1Node copySubtree() {
		return new SQuotedL1Node(getImageHolder(), getOff(), getLen());
	}
}
