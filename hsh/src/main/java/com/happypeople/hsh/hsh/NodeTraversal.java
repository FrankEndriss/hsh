package com.happypeople.hsh.hsh;

import java.io.IOException;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;

import com.happypeople.hsh.HshContext;
import com.happypeople.hsh.hsh.l1parser.Executable;
import com.happypeople.hsh.hsh.l1parser.L1Node;
import com.happypeople.hsh.hsh.l1parser.SimpleL1Node;
import com.happypeople.hsh.hsh.l1parser.Substitutable;
import com.happypeople.hsh.hsh.l1parser.TokenNode;

public class NodeTraversal {
	/** Parent-first Depth-first traversal of the nodes tree.
	 * @param listener called once on every node in the tree
	 */
	public static void traverse(final L1Node root, final TraverseListener listener) {
		if(listener.node(root, 0)!=TraverseListenerResult.CONTINUE)
			return;

		// use loop instead of recursion
		final Deque<Iterator<L1Node>> stack=new LinkedList<Iterator<L1Node>>();
		stack.push(root.iterator());

		while(!stack.isEmpty()) {
			if(stack.peek().hasNext()) {
				final L1Node child=stack.peek().next();
				final TraverseListenerResult res=listener.node(child, stack.size());
				switch(res) {
				case STOP:
					return;
				case CONTINUE:
					stack.push(child.iterator());
					continue;
				case DONT_CHILDREN:
					// empty
				}
			} else
				stack.pop();
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

	/** Executes substitution on a subtree
	 * @param subtree the subtree to substitute
	 * @param context context of substitution
	 * @return the stringified subtree, substitutions executed
	 */
	public static String substituteSubtree(final L1Node subtree, final HshContext context) throws IOException {
		final StringBuilder sb=new StringBuilder();
		final IOException[] ex=new IOException[1];

		traverse(subtree, new TraverseListener() {
			@Override
			public TraverseListenerResult node(final L1Node node, final int level) {
				try {
					if(node instanceof Substitutable) {
						sb.append(((Substitutable)node).getSubstitutedString(context));
						return TraverseListenerResult.DONT_CHILDREN;
					} else // TODO must be on Stringifiable only (or on Leafs only)
						if(node instanceof SimpleL1Node || node instanceof TokenNode)
							sb.append(node.getString());
				} catch (final IOException e) {
					ex[0]=e;
					return TraverseListenerResult.STOP;
				}
				return TraverseListenerResult.CONTINUE;
			}
		});

		if(ex[0]!=null)
			throw ex[0];

		return sb.toString();
	}

	public static int executeSubtree(final L1Node subtree, final HshContext context) throws Exception {
		final int[] res=new int[1];
		res[0]=Integer.MIN_VALUE;
		final Exception[] ex=new Exception[1];
		traverse(subtree, new TraverseListener() {
			@Override
			public TraverseListenerResult node(final L1Node node, final int level) {
				try {
					if(node instanceof Executable) {
						res[0]=((Executable)node).doExecution(context);
						return TraverseListenerResult.DONT_CHILDREN;
					}
				} catch(final Exception e) {
					ex[0]=e;
					return TraverseListenerResult.STOP;
				}
				return TraverseListenerResult.CONTINUE;
			}
		});

		if(ex[0]!=null)
			throw ex[0];

		return res[0];
	}
}
