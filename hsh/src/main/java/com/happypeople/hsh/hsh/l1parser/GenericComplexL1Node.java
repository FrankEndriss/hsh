package com.happypeople.hsh.hsh.l1parser;

import java.util.List;

import com.happypeople.hsh.HshContext;

public class GenericComplexL1Node extends ComplexL1Node {

	public GenericComplexL1Node(final ImageHolder imageHolder, final int off, final int len) {
		super(imageHolder, off, len);
	}

	@Override
	public List<? extends L1Node> transformSplit(final HshContext context) {
		throw new RuntimeException("TODO not implemented");
	}

	@Override
	public void appendUnquoted(final StringBuilder sb) {
		for(final L1Node child : this)
			child.appendUnquoted(sb);
	}

	@Override
	public GenericComplexL1Node copySubtree() {
		final GenericComplexL1Node ret=new GenericComplexL1Node(getImageHolder(), getOff(), getLen());
		for(final L1Node child : this)
			ret.add(child.copySubtree());
		return ret;
	}

}
