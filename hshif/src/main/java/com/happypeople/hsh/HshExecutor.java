package com.happypeople.hsh;

/** Interface to execute commands in a given HshContext
 */
public interface HshExecutor {
	/** Execute command using hshRedirections
	 * @param command executed
	 * @param hshRedirections used for this invocation
	 * @return exit status of execution
	 * @throws Exception
	 */
	public int execute(final String[] command, HshRedirections hshRedirections) throws Exception;
	public int execute(final String[] command) throws Exception;
	
	public HshRedirections getRedirecions();
	
	/** Create a new HshExecutor
	 * @param context context send to executed processes/commands
	 * @param hshRedirections default redirections used by this executor
	 * @return the new HshExecutor instance
	 */
	public HshExecutor createChild(HshContext context, HshRedirections hshRedirections);
}
