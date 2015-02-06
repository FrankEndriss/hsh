package com.happypeople.hsh;

/** Interface to execute commands in a given HshContext
 */
public interface HshExecutor {
	/** Execute command using context
	 * @param command and args executed
	 * @param context given to execution
	 * @return exit status of execution
	 * @throws Exception
	 */
	public int execute(final String[] command, HshContext context) throws Exception;

	/** Closes this executor and releases all resources held.
	 */
	public void close();

	/** Checks if a cmd is an external command (executed by ProcessBuilder.exec() or internal, executed by call)
	 * @param cmd command to check
	 * @return true if it is an external command, else false
	 */
	boolean isExternalCommand(String cmd);

}
