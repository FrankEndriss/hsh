package com.happypeople.hsh.hsh.parser;

import com.happypeople.hsh.HshContext;
import com.happypeople.hsh.hsh.NodeTraversal;
import com.happypeople.hsh.hsh.l1parser.Executable;
import com.happypeople.hsh.hsh.l1parser.L1Node;

/** ListNode is a list of And_orNode.
 * An And_orNode provides information if executed synchron or asynchron isBackground()
 * Return of execution is the status of the last synchron executed command.
 */
public class ListNode extends L2Node implements Executable, Backgroundable {

	@Override
	public int doExecution(final HshContext context) throws Exception {
		int exitState=-1;
		for(final L1Node node : this) {
			if(node instanceof And_orNode) {
				if(((And_orNode)node).isBackground())
					//exitState=NodeTraversal.executeSubtree(node, context);
					throw new RuntimeException("background execution of ListNode not implemented");
				else // TODO execute in background
					exitState=NodeTraversal.executeSubtree(node, context);
			} else {
				throw new RuntimeException("internal error, unknown child of ListNode:"+node);
			}
		}
		return exitState;
	}

	/* (non-Javadoc)
	 * @see com.happypeople.hsh.hsh.parser.Backgroundable#setBackground(boolean)
	 * Setting a ListNode as background means setting the last And_orNode of this list
	 * to background.
	 */
	@Override
	public void setBackground(final boolean background) {
		((Backgroundable)getChild(getChildCount()-1)).setBackground(background);
	}

	@Override
	public boolean isBackground() {
		return ((Backgroundable)getChild(getChildCount()-1)).isBackground();
	}

	@Override
	public ListNode copySubtree() {
		final ListNode ret=new ListNode();
		for(final L1Node child : this)
			ret.addPart(child);
		return ret;
	}
}
