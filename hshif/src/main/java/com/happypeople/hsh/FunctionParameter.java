/**
 */
package com.happypeople.hsh;

/**
 * Interface for function objects used as parameters.
 * TODO One needs to explain what this is good for.
 * @author Frank Endriss (fj.endriss@gmail.com)
 * @version $Id$
 * @since 0.1
 */
public interface FunctionParameter extends Parameter {

    /**
     * Query the body of the FunctionParameter.
     * @return The body of the Function, ie the Function itself.
     */
    Function getBody();

    /**
     * Opaque type for defined functions.
     */
    interface Function {
        // empty
    }
}
