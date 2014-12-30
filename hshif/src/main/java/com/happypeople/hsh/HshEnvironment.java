package com.happypeople.hsh;


/** Environment settings of a HshContext
 * shell variables
 * shell functions (TODO)
 * 
 * TODO implement declare/typeset
 */
public interface HshEnvironment {

	/** Sets a property
	 * @param name of the property. Must not be null.
	 * @param value of the property. Note that can be null, and null is different to the empty String ""
	 */
	public void setVar(final String name, final String value);
	
	/** Removes a property from the environment
	 * @param name of the property. Must not be null.
	 */
	public void unsetVar(final String name);
	
	public boolean issetVar(final String name);

	/** Finds the value of a property.
	 * @param name of the property. Must not be null.
	 * @return value of the property
	 */
	public String getVar(final String name);

	/** Add a changeListener to this env
	 * @param listener
	 */
	public void addListener(ChangeListener listener);

	public final static String UNDEFINED="<undefined>";
	
	public interface ChangeListener {
		/** Called if a var changed its value. (set or unset was called)
		 * @param name of the var
		 */
		public void varChanged(final String name, final String oldValue);
	}
	

}
