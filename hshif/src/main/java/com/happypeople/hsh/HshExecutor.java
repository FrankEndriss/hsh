package com.happypeople.hsh;

import java.util.List;

/** Interface to execute commands in a given HshContext
 *
 * TODO:
 * The definition of what an Executor ist should be reworked. Currently it is a part of an Context, and usable to execute command.
 * But it should be the encapsulation of a way to execute commands. So, an HshContext should have a (configurable) List of Executors
 * available. For any command to be executed whithin an HshContext, there should be a way to find the Executor to do so.
 */
public interface HshExecutor {
	/** Execute command using context
	 * @param command and args executed
	 * @param context given to execution
	 * @param redirections redirections activ for this execution
	 * @return exit status of execution
	 * @throws Exception
	 */
	public int execute(final String[] command, HshContext context, List<HshRedirection> redirections) throws Exception;

	/** Checks if this executor can execute a specific command
	 * @param command
	 * @return true if this executor thinks it is able to execute command
	 */
	public boolean canExecute(final String[] command);

	/** Closes this executor and releases all resources held.
	 */
	public void close();

}
