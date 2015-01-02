package com.happypeople.hsh.hsh;

import com.happypeople.hsh.FunctionParameter;
import com.happypeople.hsh.Parameter;
import com.happypeople.hsh.hsh.parser.CompoundCommand;

/** Abstraction of a defined, callable shell function.
 */
public class FunctionParameterImpl extends AbstractParameter implements FunctionParameter {
	private CompoundCommand body;

	public FunctionParameterImpl(final String name, final boolean readOnly) {
		super(name, readOnly, Parameter.Type.FUNCTION);
	}

	/**
	 * @param body should be immutable
	 */
	void setBody(final CompoundCommand body) {
		this.body=body;
	}

	/* (non-Javadoc)
	 * @see com.happypeople.hsh.hsh.FunctionParameter#getBody()
	 */
	@Override
	public Function getBody() {
		return body;
	}

	@Override
	public Parameter createCopy() {
		return new FunctionParameterImpl(getName(), isReadOnly());
	}

}
