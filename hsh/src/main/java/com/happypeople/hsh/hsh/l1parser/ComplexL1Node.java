package com.happypeople.hsh.hsh.l1parser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.happypeople.hsh.HshContext;

/** L1Node with children. Because of the children it is Substitutable, too.
 */
public class ComplexL1Node implements Substitutable, L1Node {
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

	@Override
	public void dump(final int level) {
		for(int i=0; i<level; i++)
			System.out.print("\t");
		System.out.println(getClass().getName());
		for(final L1Node child : children)
			child.dump(level+1);
	}

}
