package com.happypeople.hsh.hsh;

/** A HshRedirections object is used to setup the ProcessBuilder while executing a command.
 */
public interface HshRedirections {
	public HshRedirection getStderrRedirection();
	public HshRedirection getStdoutRedirection();
	public HshRedirection getStdinRedirection();

	/** Creates a copy of this HshRedirections, but all not null parameters overwritten.
	 * @param stdin
	 * @param stdout
	 * @param stderr
	 * @return a child HshRedirections.
	 */
	public HshRedirections createChild(HshRedirection stdin, HshRedirection stdout, HshRedirection stderr);
}
