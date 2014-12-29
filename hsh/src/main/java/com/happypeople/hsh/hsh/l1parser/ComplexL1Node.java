package com.happypeople.hsh.hsh.l1parser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.happypeople.hsh.HshContext;

/** L1Node with children.
 */
public abstract class ComplexL1Node implements SubstitutableL1Node {
	private final List<L1Node> children=new ArrayList<L1Node>();

	public void add(final L1Node child) {
		children.add(child);
	}

	public String getString() {
		StringBuilder sb=new StringBuilder();
		for(L1Node child : children)
			sb.append(child.getString());
		return sb.toString();
	}

	// TODO: check if this makes sense
	@Override
	public Iterator<String> doSubstitution(final HshContext env) throws ParseException {
		final List<String> ret=new ArrayList<String>();
		for(final L1Node child : children)
			if(child instanceof BacktickedNode) {
				for(final Iterator<String> iter=((BacktickedNode)child).doSubstitution(env) ;; iter.hasNext())
					ret.add(iter.next());
			} else if (child instanceof SimpleL1Node) {
				ret.add(((SimpleL1Node) child).getString());
			} else
				throw new RuntimeException("unknown node: "+child.getClass());

		return ret.iterator();
	}

	public void dump(final int level) {
		for(int i=0; i<level; i++)
			System.out.print("\t");
		System.out.println(getClass().getName());
		for(L1Node child : children)
			child.dump(level+1);
	}

}
