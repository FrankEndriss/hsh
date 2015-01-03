package com.happypeople.hsh;


/** Environment settings of a HshContext
 *
 * TODO implement declare/typeset functionality complete
 */
public interface HshEnvironment {

	/** Removes a parameter from the environment
	 * @param name of the parameter. Must not be null.
	 */
	public void unsetParameter(final String name);

	/** Check if var is set/exists.
	 */
	public boolean issetParameter(final String name);

	/** Finds the value of a property.
	 * @param name of the parameter. Must not be null.
	 * @return the parameter or null if unset
	 */
	public Parameter getParameter(final String name);

	/** Set a variables value.
	 * @param parameter
	 * @param value
	 */
	public void setVariableValue(final String name, final String value);

	/** Returns the value of name, if parameter unset null
	 * @param name of the Parameter
	 */
	public String getVariableValue(final String name);

	/** Add a changeListener to this env
	 * @param listener
	 */
	public void addListener(ChangeListener listener);

	/** Special parameter used for explicit unset variables.
	 */
	public final static Parameter UNDEFINED=new Parameter() {

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
	};

	public interface ChangeListener {
		public void created(Parameter parameter);

		public void removed(Parameter parameter);

		public void exported(Parameter parameter);

		public void changed(VariableParameter parameter, String oldValue);

		// TODO change of function definitions. Until implemented use remove/create
	}


}
