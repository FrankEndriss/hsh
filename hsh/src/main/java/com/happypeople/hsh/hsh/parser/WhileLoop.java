package com.happypeople.hsh.hsh.parser;

import com.happypeople.hsh.HshContext;
import com.happypeople.hsh.hsh.NodeTraversal;
import com.happypeople.hsh.hsh.l1parser.Executable;
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
		// TODO check if execution of condition should influence result
		// because what is the return value of the loop if body is
		// never executed???
		while(qualifyResult(NodeTraversal.executeSubtree(condition, context)))
			result=NodeTraversal.executeSubtree(body, context);

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
}
