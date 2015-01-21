package com.happypeople.hsh.hsh.l1parser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.happypeople.hsh.hsh.L2Token;

/** L1Node with children.
 */
public class ComplexL1Node extends AbstractL1Node {
	private final List<L1Node> children=new ArrayList<L1Node>();

	public ComplexL1Node(final L2Token tok, final int off, final int len) {
		super(tok, off, len);
	}

	public int add(final L1Node child) {
		children.add(child);
		return children.size()-1;
	}

	public L1Node get(final int idx) {
		return children.get(idx);
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
}
