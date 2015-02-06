package com.happypeople.hsh.hsh;

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;

import com.happypeople.hsh.HshContext;
import com.happypeople.hsh.hsh.l1parser.Executable;
import com.happypeople.hsh.hsh.l1parser.ImageHolder;
import com.happypeople.hsh.hsh.l1parser.L1Node;
import com.happypeople.hsh.hsh.l1parser.SimpleImageHolder;

public class NodeTraversal {
	/** Parent-first Depth-first traversal of the nodes tree.
	 * @param listener called once on every node in the tree
	 */
	public static void traverse(final L1Node root, final TraverseListener listener) throws Exception {
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
		TraverseListenerResult node(L1Node node, int level) throws Exception;
	}

	public static String substituteSubtree(final L1Node subtree, final HshContext context) throws Exception {
		final ImageHolder tok=new SimpleImageHolder();
		final L1Node transformed=subtree.transformSubstitution(tok, context);
		return transformed.getImage();
	}

	/** Executes substitution on a subtree.
	 * The traversal is implemented in a way that all Substitutable are searched in subtree, parent-first.
	 * If one found, thats nodes getSubstitutedString() is called, and thats nodes children are not
	 * traversed any more.
	 * That means the implementation of getSubstitutedString() must take care of childs substitutions for
	 * itself, i.e. by calling this method with the children as subtree.
	 * @param subtree the subtree to substitute
	 * @param context context of substitution
	 * @return the stringified subtree, substitutions executed
	 * @throws IOException if one of the getSubstitutedString()-calls throws an Exception the traversal is stopped, and
	 * that Exception is rethrown
	public static String substituteSubtree(final L1Node subtree, final HshContext context) throws Exception {
		final StringBuilder sb=new StringBuilder();
		final Exception[] ex=new Exception[1];

		traverse(subtree, new TraverseListener() {
			@Override
			public TraverseListenerResult node(final L1Node node, final int level) {
				try {
					if(node instanceof Substitutable) {
						sb.append(((Substitutable)node).getSubstitutedString(context));
						return TraverseListenerResult.DONT_CHILDREN;
					} else if(node instanceof StringifiableNode)
						((StringifiableNode)node).append(sb);
				} catch (final Exception e) {
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
	 */

	/** Executes a subtree.
	 * The traversal is implemented in a way that all Executables are searched in subtree, parent-first.
	 * If one found, thats nodes doExecution() is called, and thats nodes children are not
	 * traversed any more.
	 * That means the implementation of doExecution() must take care of childs executions and substitutions for
	 * itself, i.e. by calling this method with the children as subtree.
	 * @param subtree the subtree to execute
	 * @param context context of execution
	 * @return the return status of the last execution
	 * @throws Exception if one of the doExecution()-calls throws an Exception the traversal is stopped, and
	 * that Exception is rethrown
	 */
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
