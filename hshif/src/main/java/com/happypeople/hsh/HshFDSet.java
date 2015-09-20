package com.happypeople.hsh;


/** A Set of Input and Output streams usable by a client of a context.
 * iE
 * getIntput(STDIN) returns a HshPipe which refers to the standard input stream.
 * getOutput(4) returns a HshPipe to fd 4 if such one was opened.
 */
public interface HshFDSet {
	public final static int STDIN=0;
	public final static int STDOUT=1;
	public final static int STDERR=2;

	public HshPipe getInput(final int fd);
	public HshPipe getOutput(final int fd);

	public void close();
}
