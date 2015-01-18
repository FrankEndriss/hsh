package com.happypeople.hsh.hsh.l1parser;



/** Interface for Nodes in L1Parser constructed node trees.
 * iterator() creates an iterator over the children.
 **/
public interface L1Node extends Iterable<L1Node> {
	public void dump(int level);

	/**
	 * @return the len of the image of this node
	 */
	public int getLen();

	/** Change the image length
	 * @param increment
	 */
	public void addLen(int increment);

	/**
	 * @return the offset of the image of this node
	 */
	public int getOff();

	/** Move the nodes image index.
	 * @param increment
	 */
	public void addOff(int increment);

	/**
	 * @return the image of this token
	 */
	public String getImage();
}
