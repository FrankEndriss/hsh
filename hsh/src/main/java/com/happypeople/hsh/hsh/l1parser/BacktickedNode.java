package com.happypeople.hsh.hsh.l1parser;

import java.io.IOException;

import com.happypeople.hsh.HshContext;

/** Abstraction of backticked command.
 * TODO real implementation.
 * This should base on (extend) ComplexL1Node since there can be infinite nested parts.
 */
public class BacktickedNode extends SimpleL1Node implements Substitutable {

	public BacktickedNode(final String command) {
		super(command);
	}

	@Override
	public String getSubstitutedString(final HshContext env) throws IOException {
		throw new RuntimeException("backticked substitution still not implemented");
		//return null;
	}

	@Override
	public void dump(final int level) {
		for(int i=0; i<level; i++)
			System.out.print("\t");
		System.out.println(getClass().getName());
		for(int i=0; i<level+1; i++)
			System.out.print("\t");
		System.out.println("command="+getString());
	}

}
