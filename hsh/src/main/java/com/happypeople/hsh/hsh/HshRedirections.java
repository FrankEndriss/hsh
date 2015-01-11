package com.happypeople.hsh.hsh;

/** A HshRedirections object is used to setup the ProcessBuilder while executing a command.
 */
public interface HshRedirections {
	public HshRedirection getStderrRedirection();
	public HshRedirection getStdoutRedirection();
	public HshRedirection getStdinRedirection();
}
