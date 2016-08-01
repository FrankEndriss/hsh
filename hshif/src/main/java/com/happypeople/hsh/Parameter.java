/**
 */
package com.happypeople.hsh;

/**
 * Interface for environment variables.
 * @author Frank Endriss (fj.endriss@gmail.com)
 * @version $Id$
 * @since 0.1
 */
public interface Parameter {

    /**
     * Type of a parameter, function or variable.
     */
    enum Type {
        /**
         * The type fuction.
         */
        FUNCTION,
        /**
         * The type varable.
         */
        VARIABLE
    }

    /**
     * X.
     * @return The name of the Parameter, distinct within one environment.
     */
    String getName();

    /**
     * X.
     * @return A Parameter is a variable or a function
     */
    Type getType();

    /**
     * X.
     * @return A Parameter can be settable, or not settable
     */
    boolean isReadOnly();

    /**
     * X.
     * @return Environments are organized hirarchical, and Parameters are
     *  visible in child envirionments, too, or not.
     */
    boolean isExport();

    /**
     * X.
     * @return A copy of a parameter
     */
    Parameter createCopy();

    /**
     * X.
     * @param export Sets the export flag of this parameter.
     */
    void setExport(boolean export);
}
