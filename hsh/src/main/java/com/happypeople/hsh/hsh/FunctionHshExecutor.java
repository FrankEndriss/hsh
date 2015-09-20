package com.happypeople.hsh.hsh;

import java.util.List;

import com.happypeople.hsh.FunctionParameter;
import com.happypeople.hsh.HshContext;
import com.happypeople.hsh.HshEnvironment;
import com.happypeople.hsh.HshExecutor;
import com.happypeople.hsh.HshRedirection;
import com.happypeople.hsh.Parameter;
import com.happypeople.hsh.hsh.l1parser.L1Node;

/** Executor for defined functions.
 */
public class FunctionHshExecutor implements HshExecutor {

	private final HshEnvironment env;

	/** Creates a FunctionHshExecutor which reads the function-definitions from the given HshEnvironment
	 * @param env Envirionment with function definitions
	 */
	public FunctionHshExecutor(final HshEnvironment env) {
		if(env==null)
			throw new RuntimeException("env must not be null");
		this.env=env;
	}

	@Override
	public int execute(final String[] command, final HshContext context, final List<HshRedirection> redirections) throws Exception {
		final FunctionParameter p=(FunctionParameter)env.getParameter(command[0]);
		final L1Node executable=((L1Node)p.getBody()).copySubtree();

		// TODO
		// 1. positional parameters
		// 2. redirections
		// 3. execution

		NodeTraversal.executeSubtree(executable, context);

		return 0;
	}

	@Override
	public boolean canExecute(final String[] command) {
		final Parameter p=env.getParameter(command[0]);
		return p!=null && p instanceof FunctionParameter;
	}

	@Override
	public void close() {
		// ignore
	}

}
