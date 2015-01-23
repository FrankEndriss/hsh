package com.happypeople.hsh.hsh.l1parser;

import java.util.Collection;

import com.happypeople.hsh.HshContext;
import com.happypeople.hsh.hsh.L2Token;

public class GenericComplexL1Node extends ComplexL1Node {

	public GenericComplexL1Node(final L2Token tok, final int off, final int len) {
		super(tok, off, len);
	}

	@Override
	public L1Node transformSubstitution(final L2Token imageHolder, final HshContext context) throws Exception {
		final int off=imageHolder.getLen();
		final GenericComplexL1Node ret=new GenericComplexL1Node(imageHolder, off, 0);
		for(final L1Node child : this)
			ret.add(child.transformSubstitution(imageHolder, context));
		ret.addLen(imageHolder.getLen()-off);
		return ret;
	}

	@Override
	public Collection<? extends L1Node> transformSplit(final HshContext context) {
		throw new RuntimeException("TODO not implemented");
	}

	@Override
	public void appendUnquoted(final StringBuilder sb) {
		for(final L1Node child : this)
			child.appendUnquoted(sb);
	}

}
