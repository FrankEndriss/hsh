package com.happypeople.hsh.hsh.l1parser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ComplexL1Node implements L1Node, Substitutable<String> {
	private final List<L1Node> children=new ArrayList<L1Node>();

	public void addChild(final L1Node child) {
		children.add(child);
	}

	@Override
	public Iterator<String> doSubstitution() throws ParseException {
		final List<String> ret=new ArrayList<String>();
		for(final L1Node child : children)
			if(child instanceof BacktickedNode) {
				for(final Iterator<String> iter=((BacktickedNode)child).doSubstitution() ;; iter.hasNext())
					ret.add(iter.next());
			} else if (child instanceof SimpleNode) {
				ret.add(((SimpleNode) child).getString());
			} else
				throw new RuntimeException("unknown node: "+child.getClass());

		return ret.iterator();
	}

}
