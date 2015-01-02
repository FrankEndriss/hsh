package com.happypeople.hsh.hsh;

import com.happypeople.hsh.Parameter;
import com.happypeople.hsh.VariableParameter;

class VariableParameterImpl extends AbstractParameter implements VariableParameter {
	private String value;

	public VariableParameterImpl(final String name, final boolean readOnly) {
		super(name, readOnly, Parameter.Type.VARIABLE);
	}

	@Override
	public String getValue() {
		return value;
	}

	void setValue(final String value) {
		if(isReadOnly())
			throw new RuntimeException("Parameter is readOnly");
		this.value=value;
	}

	@Override
	public Parameter createCopy() {
		return new VariableParameterImpl(getName(), isReadOnly());
	}
}
