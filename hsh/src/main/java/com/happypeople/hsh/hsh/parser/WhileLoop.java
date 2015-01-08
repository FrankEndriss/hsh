package com.happypeople.hsh.hsh.parser;

import com.happypeople.hsh.HshContext;
import com.happypeople.hsh.hsh.l1parser.Executable;

/** A while loop.
 * Child[0] is the condition.
 * Child[1] is the body.
 */
public class WhileLoop extends L2Node implements Executable {
	@Override
	public int doExecution(final HshContext context) throws Exception {
		return 0;
	}
}
