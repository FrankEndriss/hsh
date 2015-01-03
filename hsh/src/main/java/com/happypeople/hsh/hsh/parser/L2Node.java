package com.happypeople.hsh.hsh.parser;

import com.happypeople.hsh.hsh.L2Token;
import com.happypeople.hsh.hsh.l1parser.L1Node;

/** L2Node is a node in the L2 parse tree.
 * It extends L2Token, because several nodes are 1:1 related to tokens. With this extension we can use
 * directly the tokens to place them as childs into the tree of nodes.
 */
public class L2Node extends L2Token {
	public int addChild(final L2Token node) {
		return addPart(node);
	}

	public L2Token getChild(final int idx) {
		return (L2Token)getPart(idx);
	}

	/** Creates a printout of the node-tree
	 * @param level the level of the tree this node lives in
	 */
	@Override
	public void dump(final int level) {
		for(int i=0; i<level; i++)
			System.out.print("\t");
		System.out.println(getClass().getName());
		for(final L1Node child : this)
			child.dump(level+1);
	}
}
