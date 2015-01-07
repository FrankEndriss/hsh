package com.happypeople.hsh.hsh.parsers;

/** Interface for all Nodes in DefaultParser.
 * They can be traversed, but only once!
 */
public interface DNode extends Iterable<DNode> {
	public void offer(DNode child);
	public void close();
}
