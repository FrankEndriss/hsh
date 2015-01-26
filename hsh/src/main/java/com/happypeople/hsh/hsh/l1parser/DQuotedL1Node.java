package com.happypeople.hsh.hsh.l1parser;

import java.util.Arrays;
import java.util.Collection;

import com.happypeople.hsh.HshContext;
import com.happypeople.hsh.hsh.L2Token;

public class DQuotedL1Node extends ComplexL1Node implements QuotedL1Node {

	public DQuotedL1Node(final L2Token tok, final int off, final int len) {
		super(tok, off, len);
	}

	@Override
	public Collection<? extends L1Node> transformSplit(final HshContext context) {
		// Double quoted words/parts are not split.
		return Arrays.asList(this);
	}

	@Override
	public void appendUnquoted(final StringBuilder sb) {
		sb.append(getImageHolder().image.substring(getOff()+1, getOff()+getLen()-1));
	}

	@Override
	public L1Node copySubtree() {
		final DQuotedL1Node ret=new DQuotedL1Node(getImageHolder(), getOff(), getLen());
		for(final L1Node child : this)
			ret.add(child.copySubtree());
		return ret;

	}
}
