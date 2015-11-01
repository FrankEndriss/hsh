package com.happypeople.hsh.hsh;

import java.util.List;

import com.happypeople.hsh.FunctionParameter;
import com.happypeople.hsh.HshContext;
import com.happypeople.hsh.HshExecutor;
import com.happypeople.hsh.HshRedirection;
import com.happypeople.hsh.Parameter;
import com.happypeople.hsh.hsh.l1parser.L1Node;

/** Executor for defined functions.
 */
public class FunctionHshExecutor implements HshExecutor {

	/** Creates a fairly simple stateless FunctionHshExecutor
	 */
	public FunctionHshExecutor() {
	}

	@Override
	public int execute(final String[] command, final HshContext parentContext, final List<HshRedirection> redirections) throws Exception {
		final FunctionParameter p=(FunctionParameter)parentContext.getEnv().getParameter(command[0]);
		final L1Node executable=((L1Node)p.getBody()).copySubtree();

		// TODO
		// 1. positional parameters
		// 2. setup execution context
		// 3. execution

		NodeTraversal.executeSubtree(executable, parentContext);

		return 0;
	}

	@Override
	public boolean canExecute(final String[] command, final HshContext parentContext) {
		final Parameter p=parentContext.getEnv().getParameter(command[0]);
		return p!=null && p instanceof FunctionParameter;
	}

	@Override
	public void close() {
		// ignore
	}

}
