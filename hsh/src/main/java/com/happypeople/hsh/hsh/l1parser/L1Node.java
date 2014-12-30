package com.happypeople.hsh.hsh.l1parser;


/** Interface for Nodes in L1Parser constructed node trees. 
 * iterator() creates an iterator over the children.
 **/
public interface L1Node extends Iterable<L1Node> {
	public void dump(int level);
	
	/**
	 * @return the String image of this node/token as parsed.
	 */
	public String getString();


}
