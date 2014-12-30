package com.happypeople.hsh.hsh;

import com.happypeople.hsh.hsh.l1parser.L1Node;

public class NodeTraversal {
	/** Depth-first traversal of the nodes tree.
	 * @param listener called once on every node in the tree
	 */
	public static void traverse(L1Node root, TraverseListener listener) {
		traverse(root, listener, 0);
	}

	private static void traverse(L1Node root, TraverseListener listener, int level) {
		listener.node(root, level);
		for(L1Node child : root)
			traverse(child, listener);
	}
	
	public interface TraverseListener {
		void node(L1Node node, int level);
	}
}
