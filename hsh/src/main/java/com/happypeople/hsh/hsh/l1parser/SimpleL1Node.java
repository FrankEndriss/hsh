package com.happypeople.hsh.hsh.l1parser;

import java.util.Collections;
import java.util.Iterator;


/** SimpleL1Node is a node with simple text. The text is interpreted to have no special meaning.
 * So, that can be text with really no special meaning, or text which has not special meaning in
 * the context where it is found.
 */
public class SimpleL1Node implements L1Node, StringifiableNode {
	private final String str;

	public SimpleL1Node(final String str) {
		this.str=str;
	}

	public String getImage() {
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
		System.out.println("value="+str);
	}

	@Override
	public void append(final StringBuilder sb) {
		sb.append(str);
	}


}
