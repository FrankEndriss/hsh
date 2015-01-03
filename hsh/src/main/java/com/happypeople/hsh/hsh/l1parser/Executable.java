package com.happypeople.hsh.hsh.l1parser;

import com.happypeople.hsh.HshContext;

/** Interface for executable Nodes (i.e. SimpleCommand) in the parse tree.
 */
public interface Executable {
	/** Execute a Node. Execution includes substitution.
	 * @param context execution environment
	 * @return exit status of execution
	 * @throws Exception if something goes wrong
	 */
	public int doExecution(HshContext context) throws Exception;
}
