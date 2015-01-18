package com.happypeople.hsh.hsh.l1parser;

import java.io.IOException;

import com.happypeople.hsh.HshContext;
import com.happypeople.hsh.hsh.L2Token;

/** Abstraction of backticked command.
 * TODO real implementation.
 * This should base on (extend) ComplexL1Node since there can be infinite nested parts.
 */
public class BacktickedNode extends ComplexL1Node implements Substitutable {

	public BacktickedNode(final L2Token tok, final int off, final int len) {
		super(tok, off, len);
	}

	@Override
	public String getSubstitutedString(final HshContext env) throws IOException {
		throw new RuntimeException("backticked substitution still not implemented");
		//return null;
	}
}
