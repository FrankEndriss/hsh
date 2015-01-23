package com.happypeople.hsh.hsh.l1parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.happypeople.hsh.HshContext;
import com.happypeople.hsh.hsh.L2Token;

public class DQuotedL1Node extends ComplexL1Node implements QuotedL1Node {

	public DQuotedL1Node(final L2Token tok, final int off, final int len) {
		super(tok, off, len);
	}

	@Override
	public DQuotedL1Node transformSubstitution(final L2Token imageHolder, final HshContext context) throws Exception {
		final int off=imageHolder.getLen();
		imageHolder.append('"');
		final List<L1Node> children=new ArrayList<L1Node>();
		for(final L1Node child : this)
			children.add(child.transformSubstitution(imageHolder, context));
		imageHolder.append('"');
		final DQuotedL1Node node=new DQuotedL1Node(imageHolder, off, imageHolder.getLen()-off);
		for(final L1Node child : children)
			node.add(child);

		return node;
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
}
