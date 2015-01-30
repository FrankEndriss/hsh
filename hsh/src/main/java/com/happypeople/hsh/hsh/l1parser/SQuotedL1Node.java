package com.happypeople.hsh.hsh.l1parser;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.happypeople.hsh.HshContext;

public class SQuotedL1Node extends AbstractL1Node implements QuotedL1Node {
	public SQuotedL1Node(final ImageHolder imageHolder, final int off, final int len) {
		super(imageHolder, off, len);
	}

	@Override
	public Iterator<L1Node> iterator() {
		return Collections.EMPTY_LIST.iterator();
	}

	@Override
	public SQuotedL1Node transformSubstitution(final ImageHolder imageHolder, final HshContext context) throws Exception {
		return this;
	}

	@Override
	public List<? extends L1Node> transformSplit(final HshContext context) {
		return Arrays.asList(this);
	}

	@Override
	public void appendUnquoted(final StringBuilder sb) {
		final String s=getImage();
		sb.append(s.substring(1, s.length()-2));
	}

	@Override
	public SQuotedL1Node copySubtree() {
		return new SQuotedL1Node(getImageHolder(), getOff(), getLen());
	}
}
