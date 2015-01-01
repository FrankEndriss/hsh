package com.happypeople.hsh.hsh;

import com.happypeople.hsh.hsh.l1parser.L1Node;

public class NodeTraversal {
	/** Parent-first Depth-first traversal of the nodes tree.
	 * @param listener called once on every node in the tree
	 */
	public static void traverse(final L1Node root, final TraverseListener listener) {
		traverse(root, listener, 0);
	}

	private static void traverse(final L1Node root, final TraverseListener listener, final int level) {
		final TraverseListenerResult res=listener.node(root, level);
		switch(res) {
		case STOP:
			return;
		case CONTINUE:
			for(final L1Node child : root)
				traverse(child, listener);
		case DONT_CHILDREN:
			// empty
		}
	}

	public enum TraverseListenerResult {
		/** Just continue traversal */
		CONTINUE,
		/** Simply stop traversal */
		STOP,
		/** Dont traverse children of current node */
		DONT_CHILDREN
	}

	public interface TraverseListener {
		TraverseListenerResult node(L1Node node, int level);
	}
}
