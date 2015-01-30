package com.happypeople.hsh.hsh.l1parser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.happypeople.hsh.HshContext;

/** L1Node with children.
 */
public abstract class ComplexL1Node extends AbstractL1Node {
	private final List<L1Node> children=new ArrayList<L1Node>();

	public ComplexL1Node(final ImageHolder imageHolder, final int off, final int len) {
		super(imageHolder, off, len);
	}

	public int add(final L1Node child) {
		children.add(child);
		return children.size()-1;
	}

	public L1Node get(final int idx) {
		return children.get(idx);
	}

	public int getChildCount() {
		return children.size();
	}

	@Override
	public Iterator<L1Node> iterator() {
		return children.iterator();
	}

	@Override
	public void dump(final int level) {
		for(int i=0; i<level; i++)
			System.out.print("\t");
		System.out.println(getClass().getName());
		for(final L1Node child : children)
			child.dump(level+1);
	}

	/* (non-Javadoc)
	 * @see com.happypeople.hsh.hsh.l1parser.L1Node#transformSubstitution(com.happypeople.hsh.hsh.L2Token, com.happypeople.hsh.HshContext)
	 * @return this, children substituted
	 */
	@Override
	public L1Node transformSubstitution(final ImageHolder imageHolder, final HshContext context) throws Exception {
		for(int i=0; i<children.size(); i++)
			children.set(i, children.get(i).transformSubstitution(imageHolder, context));
		return this;
	}

}
