package com.happypeople.hsh.hsh.parser;

import com.happypeople.hsh.HshContext;
import com.happypeople.hsh.hsh.L2Token;
import com.happypeople.hsh.hsh.l1parser.Executable;

public class FunctionDefinition extends L2Node implements Executable {

	private L2Token name;
	private L2Node body;

	@Override
	public int doExecution(final HshContext context) throws Exception {
		throw new RuntimeException("not implemented");
		// TODO define this function in context in a way that it
		// can be called later by the FunctionExecutor
		//return 0;
	}

	public void setName(final L2Token t) {
		this.name=t;
	}

	public void setBody(final L2Node cc) {
		if(!(cc instanceof Executable))
			throw new RuntimeException("Function body must be Eexecutable");
		this.body=cc;
	}

	// TODO setRedirects(Redirs)

}
