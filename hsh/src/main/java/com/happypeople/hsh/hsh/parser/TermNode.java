package com.happypeople.hsh.hsh.parser;

import com.happypeople.hsh.HshContext;
import com.happypeople.hsh.hsh.NodeTraversal;
import com.happypeople.hsh.hsh.l1parser.Executable;
import com.happypeople.hsh.hsh.l1parser.L1Node;

/** TermNode is a list of And_orNode
 * Execution is from first to last.
 */
public class TermNode extends L2Node implements Backgroundable, Executable {
	@Override
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
		int result=-1;
		for(final L1Node child : this) {
			if(((Backgroundable)child).isBackground())
			throw new RuntimeException("background execution still not implemented");

			result=NodeTraversal.executeSubtree(child, context);
		}
		return result;
	}

	@Override
	public TermNode copySubtree() {
		final TermNode ret=new TermNode();
		for(final L1Node child : this)
			ret.addPart(child);
		return ret;
	}
}
