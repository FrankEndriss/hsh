package com.happypeople.hsh;

/** Interface to execute commands in a given HshContext
 */
public interface HshExecutor {
	public int execute(final String[] command) throws Exception;
}
