/**
 */
package com.happypeople.hsh;

/**
 * Environment settings of a HshContext.
 * TODO implement declare/typeset functionality complete
 * @author Frank Endriss (fj.endriss@gmail.com)
 * @version $Id$
 * @since 0.1
 */
public interface HshEnvironment {

    /**
     * Special parameter used for explicit unset variables.
     */
    Parameter UNDEFINED = new UndefinedParameter();

    /**
     * Removes a parameter from this environment.
     * @param name Of the parameter. Must not be null.
     */
    void unsetParameter(String name);

    /**
     * Check if var is set/exists.
     * @param name Of the parameter. Must not be null.
     * @return True if the parameter of name name is set, else false.
     */
    boolean issetParameter(String name);

    /**
     * Finds the value of a property.
     * @param name Of the parameter. Must not be null.
     * @return The parameter or null if unset
     */
    Parameter getParameter(String name);

    /**
     * Set a variables value.
     * @param name The name of the variable
     * @param value The new value of the variable
     */
    void setVariableValue(String name, String value);

    /**
     * Returns the value of name, if parameter unset null.
     * @param name Of the Parameter
     * @return The value of the variable, null if unset
     */
    String getVariableValue(String name);

    /**
     * Add a changeListener to this HshEnvironment.
     * @param listener To add
     */
    void addListener(ChangeListener listener);

    /**
     * Removes a changeListener from this HshEnvironment.
     * @param listener To remove
     */
    void removeListener(ChangeListener listener);

    /**
     * Query the number of positional parameters, ie the argument count.
     * @return The count of positional parameters set in this context, or if not
     *  set the count of the parents positional parameters.
     */
    int getPositionalCount();

    /**
     * Sets the positional parameters ($1, $2, ...) all at once and implicit the
     * positionalCount. If these where not set on the current environment, the
     * parents positional parameters are used.
     * @param values The values of the positional parameters
     */
    void setPositionalValues(String... values);

    /**
     * Close/free this environment.
     */
    void close();

    /**
     * This method associates a name with a function definition.
     * @param name Name of the function
     * @param funcdef The definition of the function
     */
    void setFunction(String name, Executable funcdef);

    /**
     * Query a function by name.
     * @param name Name of the function
     * @return The function with name name
     */
    Executable getFunction(String name);

    /**
     * Interface for registered observers.
     */
    interface ChangeListener {
        /**
         * Called on Parameter creation.
         * @param parameter The newly created Parameter
         */
        void created(Parameter parameter);

        /**
         * Called on Parameter removal.
         * @param parameter The just removed Parameter
         */
        void removed(Parameter parameter);

        /**
         * Called on Parameter export.
         * @param parameter The just exported Parameter
         */
        void exported(Parameter parameter);

        /**
         * Called on Parameter value change.
         * @param parameter The changed Parameter
         * @param oldvalue The value of the Parameter before it was changed
         */
        void changed(VariableParameter parameter, String oldvalue);

        // TODO change of function definitions. Until implemented use
        // remove/create
    }

    /**
     * Parameter implementation for singleton Parameter UNDEFINED.
     */
    class UndefinedParameter implements Parameter {

        @Override
        public String getName() {
            return null;
        }

        @Override
        public Type getType() {
            return null;
        }

        @Override
        public boolean isReadOnly() {
            return false;
        }

        @Override
        public boolean isExport() {
            return false;
        }

        @Override
        public Parameter createCopy() {
            return this;
        }

        @Override
        public void setExport(final boolean isexport) {
            // intentionally blank
        }
    };

}
