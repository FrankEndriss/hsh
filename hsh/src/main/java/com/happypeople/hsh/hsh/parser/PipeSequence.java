package com.happypeople.hsh.hsh.parser;

import com.happypeople.hsh.HshContext;
import com.happypeople.hsh.hsh.NodeTraversal;
import com.happypeople.hsh.hsh.l1parser.Executable;
import com.happypeople.hsh.hsh.l1parser.L1Node;

public class PipeSequence extends L2Node implements Executable {
	private boolean banged;

	public boolean isBanged() {
		return banged;
	}

	public void setBanged(final boolean banged) {
		this.banged = banged;
	}

	@Override
	public int doExecution(final HshContext context) throws Exception {
		// TODO implement io redirection
		if(getChildCount()>1)
			throw new RuntimeException("pipes not implementd");

		int result=0;
		for(final L1Node child : this)
			result=NodeTraversal.executeSubtree(child, context);
		// if banged swap result true/false
		if(isBanged())
			return result==0?1:0;
		return result;
	}

}
