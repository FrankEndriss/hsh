package com.happypeople.hsh;

/** Abstraction of the Terminal within a HshContext.
 */
public interface HshTerminal {
	/**
	 * @return number of columns of the terminal window
	 */
	public int getCols();

	/**
	 * @return Number of rows of the terminal window
	 */
	public int getRows();
}
