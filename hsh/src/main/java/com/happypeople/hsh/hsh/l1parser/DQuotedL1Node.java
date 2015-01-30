package com.happypeople.hsh.hsh.l1parser;

import java.util.Arrays;
import java.util.List;

import com.happypeople.hsh.HshContext;

public class DQuotedL1Node extends ComplexL1Node implements QuotedL1Node {

	public DQuotedL1Node(final ImageHolder imageHolder, final int off, final int len) {
		super(imageHolder, off, len);
	}

	@Override
	public List<? extends L1Node> transformSplit(final HshContext context) {
		// Double quoted words/parts are not split.
		return Arrays.asList(this);
	}

	@Override
	public void appendUnquoted(final StringBuilder sb) {
		final String s=getImage();
		sb.append(s.substring(1, s.length()-2));
	}

	@Override
	public L1Node copySubtree() {
		final DQuotedL1Node ret=new DQuotedL1Node(getImageHolder(), getOff(), getLen());
		for(final L1Node child : this)
			ret.add(child.copySubtree());
		return ret;

	}
}
