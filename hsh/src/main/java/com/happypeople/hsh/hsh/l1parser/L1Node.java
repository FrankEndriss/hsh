package com.happypeople.hsh.hsh.l1parser;

import java.util.List;

import com.happypeople.hsh.HshContext;
import com.happypeople.hsh.hsh.L2Token;



/** Interface for Nodes in L1Parser constructed node trees.
 * iterator() creates an iterator over the children.
 * TODO
 * *implement CharSequence
 * *Split tree Nodes into
 * **L1Node extends CharSequence and adds the transforms methods plus boolean isLeaf(),isTree()
 * **L1Leaf extends CharSequence as base for SimpleL1Node
 * **L1Tree as base for ComplexL1Node
 * **L2Token implements L1Subtree
 *
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
	 * @return the image of this node as parsed/read
	 */
	public String getImage();

	/** Transforms this subtree into one which is not Substitutable and contains no Substitutables.
	 * ie all Substitutables in the tree rooted by this node are replaced by ones whithout
	 * substitutions.
	 * This is done in place if possible, so it might return this.
	 * This is called as one step of the execution of commands.
	 * @param tok the image holder for the new L1Nodes
	 * @param context context for substitution
	 * @return the new node
	 * @throws Exception
	 */
	public L1Node transformSubstitution(L2Token imageHolder, HshContext context) throws Exception;

	/** Transforms this node-tree into n trees. These n trees are the splitted ones.
	 * If more than one, this L1Node was splitted and the list contains the parts.
	 * If this node starts or ends with an IFS, an empty L1Node is contained in the list
	 * at that position.
	 * This is called as one step of the execution of commands.
	 * @param context context of splitting (IFS)
	 * @return a list of at least on L1Node. If it is one most likely the one is this, because no split was done.
	 * TODO should be more stream-like, since the returned nodes are processed left to right.
	 */
	public List<? extends L1Node> transformSplit(HshContext context);

	/** Appends this node as a "value" to sb. This is called while the last step in expanding a command line.
	 * @param sb the buffer to append this to.
	 */
	public void appendUnquoted(StringBuilder sb);

	/** Creates a copy of this subtree (this node, and all subnotes).
	 * Useful while execution of commands, if executed more than once (ie within loops)
	 * @return an independent copy of this node.
	 */
	public L1Node copySubtree();
}