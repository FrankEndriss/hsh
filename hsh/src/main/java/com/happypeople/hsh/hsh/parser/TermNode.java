package com.happypeople.hsh.hsh.parser;

import com.happypeople.hsh.HshContext;
import com.happypeople.hsh.hsh.l1parser.Executable;

/** TermNode is a list of And_orNode
 * Execution is from first to last.
 */
public class TermNode extends L2Node implements Backgroundable, Executable {
	public boolean isBackground() {
		return ((And_orNode)getChild(getChildCount()-1)).isBackground();
	}

	@Override
	public void setBackground(final boolean background) {
		// if a term() is set as background, that means the last and_or() of that term is set
		// to background
		((Backgroundable)getChild(getChildCount()-1)).setBackground(background);
	}

	@Override
	public int doExecution(final HshContext context) throws Exception {
		throw new RuntimeException("term execution still not implemented");
		//return 0;
	}
}
