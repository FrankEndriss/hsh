package com.happypeople.hsh;

import java.util.List;

/** Interface to execute commands in a given HshContext
 */
public interface HshExecutor {
	/** Execute command using context
	 * @param command and args executed
	 * @param context given to execution
	 * @param redirections redirections activ for this execution
	 * @return exit status of execution
	 * @throws Exception
	 */
	public int execute(final String[] command, HshContext parentContext, List<HshRedirection> redirections) throws Exception;

	/** Checks if this executor can execute a specific command within a given parentContext
	 * @param command contains the command to execute
	 * @param parentContext contains the context, ie the PATH variable
	 * @return true if this executor thinks it is able to execute command
	 */
	public boolean canExecute(final String[] command, HshContext parentContext);

	/** Closes this executor and releases all resources held.
	 */
	public void close();

}
