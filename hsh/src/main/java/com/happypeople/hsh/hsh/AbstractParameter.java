package com.happypeople.hsh.hsh;

import com.happypeople.hsh.Parameter;

public abstract class AbstractParameter implements Parameter {
	private final String name;
	private final boolean readOnly;
	private final Parameter.Type type;
	private boolean export;

	public AbstractParameter(final String name, final boolean readOnly, final Parameter.Type type) {
		this.name=name;
		this.readOnly=readOnly;
		this.type=type;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean isReadOnly() {
		return readOnly;
	}

	@Override
	public Parameter.Type getType() {
		return type;
	}

	public void setExport(final boolean export) {
		this.export=export;
	}

	@Override
	public boolean isExport() {
		return export;
	}

}
