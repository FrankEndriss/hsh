package com.happypeople.hsh;

import java.io.InputStream;
import java.io.PrintStream;

/** Context information of the calling instance
 */
public interface HshContext {

	/** @return the StdOut to use */
	public PrintStream getStdOut();

	/** @return StdIn to use */
	public InputStream getStdIn();

	/** @return StdErr to use */
	public PrintStream getStdErr();

	/** @return the number of columns of the display
	 */
	public int getCols();

	/** @return the number of rows of the display
	 */
	public int getRows();

	/** Denotes that this context should exit after the execution of the current command.
	 * Used ie by "exit".
	 */
	public void finish();

	/** Creates a new HshContext with this context as parent.
	 * @return the new created context
	 */
	public HshContext createChildContext();

	/**
	 * @return the environment of this context
	 */
	public HshEnvironment getEnv();

	/**
	 * @return the executor of this context, usefull to execute commands. And has nothing to do with javas
	 * ExecutorService.
	 */
	public HshExecutor getExecutor();
}
