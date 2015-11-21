package com.happypeople.hsh.hsh.parser;

import com.happypeople.hsh.Executable;
import com.happypeople.hsh.HshContext;
import com.happypeople.hsh.hsh.NodeTraversal;
import com.happypeople.hsh.hsh.l1parser.L1Node;

/** A while loop.
 * Child[0] is the condition.
 * Child[1] is the body.
 */
public class WhileLoop extends L2Node implements Executable {
	private boolean untilFlag=false;

	@Override
	public int doExecution(final HshContext context) throws Exception {
		final L1Node condition=getChild(0);
		final L1Node body=getChild(1);
		int result=-1;
		// result of while loop is last executed command...usually the last one of the condition
		// TODO: break???
		while(qualifyResult(result=NodeTraversal.executeSubtree(condition.copySubtree(), context)))
			result=NodeTraversal.executeSubtree(body.copySubtree(), context);

		return result;
	}

	/** Sets this WhileLoop to be a until loop.
	 */
	public void setUntil() {
		untilFlag=true;
	}

	private boolean qualifyResult(final int status) {
		return untilFlag?status!=0:status==0;
	}

	@Override
	public WhileLoop copySubtree() {
		final WhileLoop ret=new WhileLoop();
		if(untilFlag)
			ret.setUntil();
		for(final L1Node child : this)
			ret.addPart(child);
		return ret;
	}
}
