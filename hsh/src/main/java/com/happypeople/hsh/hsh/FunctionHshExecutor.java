package com.happypeople.hsh.hsh;

import java.util.List;
import java.util.Map;

import com.happypeople.hsh.HshContext;
import com.happypeople.hsh.HshExecutor;
import com.happypeople.hsh.HshRedirection;
import com.happypeople.hsh.hsh.parser.L2Node;

/** Executor for defined functions.
 */
public class FunctionHshExecutor implements HshExecutor {

	private final Map<String, L2Node> functionMap;

	public FunctionHshExecutor(final Map<String, L2Node> functionMap) {
		this.functionMap=functionMap;
	}

	@Override
	public int execute(final String[] command, final HshContext context, final List<HshRedirection> redirections) throws Exception {
		// TODO setup new HshContext based on context and args in command
		// then call function
		return 0;
	}

	@Override
	public boolean canExecute(final String[] command) {
		return functionMap.containsKey(command[0]);
	}

	@Override
	public void close() {
		// ignore
	}

}
