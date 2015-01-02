package com.happypeople.hsh;

public interface Parameter {

	public enum Type {
		FUNCTION,
		VARIABLE
	}

	public String getName();

	public Type getType();

	public boolean isReadOnly();
	
	public boolean isExport();
	
	public Parameter createCopy();
}