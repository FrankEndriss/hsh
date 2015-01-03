package com.happypeople.hsh.hsh.l1parser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/** L1Node with children. Because of the children it is Substitutable, too.
 */
public class ComplexL1Node implements L1Node {
	private final List<L1Node> children=new ArrayList<L1Node>();

	public int add(final L1Node child) {
		children.add(child);
		return children.size()-1;
	}

	public L1Node get(final int idx) {
		return children.get(idx);
	}

	@Override
	public String getString() {
		final StringBuilder sb=new StringBuilder();
		for(final L1Node child : children)
			sb.append(child.getString());
		return sb.toString();
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
