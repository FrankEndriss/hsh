package com.happypeople.hsh.hsh.parser;

import java.util.ArrayList;
import java.util.List;

import com.happypeople.hsh.hsh.L2Token;

/** L2Node is a node in the L2 parse tree.
 * It extends L2Token, because several nodes are 1:1 related to tokens. With this extension we can use
 * directly the tokens to place them as childs into the tree of nodes.
 */
public class L2Node extends L2Token {
	private final List<L2Token> children=new ArrayList<L2Token>();
	public void addChild(final L2Token node) {
		children.add(node);
	}

	/** Creates a printout of the node-tree
	 * @param level the level of the tree this node lives in
	 */
	public void dump(final int level) {
		for(int i=0; i<level; i++)
			System.out.print("\t");
		System.out.println(getClass().getName());
		for(final L2Token child : children)
			child.dump(level+1);
	}

	/*
	public void execute() {
		for(final L2Node child : children)
			child.execute();
	}
	*/
}
