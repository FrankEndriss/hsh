package com.happypeople.hsh.hsh.l1parser;

import java.util.Collections;
import java.util.Iterator;


/** SimpleL1Node is a not Substitutable node.
 */
public class SimpleL1Node implements L1Node {
	private final String str;

	public SimpleL1Node(final String str) {
		this.str=str;
	}

	@Override
	public String getString() {
		return str;
	}

	@Override
	public Iterator<L1Node> iterator() {
		return Collections.emptyIterator();
	}

	@Override
	public void dump(final int level) {
		for(int i=0; i<level; i++)
			System.out.print("\t");
		System.out.println(getClass().getName());
		for(int i=0; i<level+1; i++)
			System.out.print("\t");
		System.out.println("value="+getString());
	}


}
