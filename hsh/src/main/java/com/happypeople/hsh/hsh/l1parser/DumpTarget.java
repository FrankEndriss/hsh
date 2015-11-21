package com.happypeople.hsh.hsh.l1parser;

/** Target to dump hirarchical data to structure. */
public interface DumpTarget {
	/** Increment the hirarchical Level.
	 * @return this
	 **/
	public DumpTarget incLevel();
	/** Decrement the hirarchical Level.
	 * @return this
	 **/
	public DumpTarget decLevel();
	/** Add a line of information.
	 * @param line the line without end-of-line characters.
	 * @return this
	 **/
	public DumpTarget add(CharSequence line);
}