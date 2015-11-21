package com.happypeople.hsh.hsh.parser;

import com.happypeople.hsh.Executable;
import com.happypeople.hsh.HshContext;
import com.happypeople.hsh.hsh.L2Token;

public class FunctionDefinitionNode extends L2Node implements Executable {

	private L2Token name;
	private Executable body;

	/** The execution of a function definition simply stores that function
	 * in that context.
	 * @see com.happypeople.hsh.Executable#doExecution(com.happypeople.hsh.HshContext)
	 */
	@Override
	public int doExecution(final HshContext context) throws Exception {
		context.getEnv().setFunction(name.image, getBody());
		return 0;
	}

	public void setName(final L2Token t) {
		this.name=t;
	}

	public void setBody(final L2Node cc) {
		if(!(cc instanceof Executable))
			throw new RuntimeException("Function body must be Executable (CompoundCommand)!");
		this.body=(Executable)cc;
	}

	Executable getBody() {
		return body;
	}

}
