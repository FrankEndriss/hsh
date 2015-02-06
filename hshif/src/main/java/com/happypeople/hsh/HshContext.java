package com.happypeople.hsh;

import java.io.InputStream;
import java.io.PrintStream;

/** Context information of a calling instance.
 */
public interface HshContext extends AutoCloseable {

	/** @return StdIn to use */
	public InputStream getStdIn();

	/** @return the StdOut to use */
	public PrintStream getStdOut();

	/** @return StdErr to use */
	public PrintStream getStdErr();

	/** Denotes that this context should exit after the execution of the current command.
	 * Used ie by "exit".
	 */
	public void finish();

	/** Check if finish was called.
	 * @return true after finish() has been called at least once
	 */
	public boolean isFinish();

	/**
	 * @return the environment of this context
	 */
	public HshEnvironment getEnv();

	/**
	 * @return the executor of this context, usefull to execute commands. And has nothing to do with javas
	 * ExecutorService.
	 */
	public HshExecutor getExecutor();

	/**
	 * @return the open FDs of this context
	 */
	public HshFDSet getFDSet();

	/**
	 * @return the terminal of this context, or null if there is none
	 */
	public HshTerminal getTerminal();

	/** Closes this context by releasing all resources held.
	 */
	@Override
	public void close();


}
