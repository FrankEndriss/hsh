/**
 */
package com.happypeople.hsh;

/** A VariableParameter is one thats value can change over time.
 * @author Frank Endriss (frank.endriss@fumgroup.com)
 * @version $Id$
 * @since 0.1
 */
public interface VariableParameter extends Parameter {
    /**
     * Ask for the value.
     * @return The parameters value, that value may change over time.
     */
    String getValue();
}
