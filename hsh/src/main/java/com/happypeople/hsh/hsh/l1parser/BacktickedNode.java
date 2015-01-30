package com.happypeople.hsh.hsh.l1parser;

import java.util.List;

import com.happypeople.hsh.HshContext;

/** Abstraction of backticked command.
 * TODO real implementation.
 * This should base on (extend) ComplexL1Node since there can be infinite nested parts.
 * See DollarExecNode
 */
public class BacktickedNode extends ComplexL1Node {

	public BacktickedNode(final ImageHolder imageHolder, final int off, final int len) {
		super(imageHolder, off, len);
	}

	@Override
	public L1Node transformSubstitution(final ImageHolder imageHolder, final HshContext context) throws Exception {
		throw new RuntimeException("backticked substitution still not implemented");
	}

	@Override
	public List<? extends L1Node> transformSplit(final HshContext context) {
		throw new RuntimeException("split has to be done after substitution");
	}

	@Override
	public void appendUnquoted(final StringBuilder sb) {
		throw new RuntimeException("unquote has to be done after split and substitution");
	}

	@Override
	public L1Node copySubtree() {
		final BacktickedNode ret=new BacktickedNode(getImageHolder(), getOff(), getLen());
		for(final L1Node child : this)
			ret.add(child.copySubtree());
		return ret;
	}
}
