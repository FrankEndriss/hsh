package com.happypeople.hsh;

public interface FunctionParameter extends Parameter {

    public Function getBody();

    /**
     * Opaque type for defined functions.
     */
    public interface Function {
        // empty
    }
}