package com.happypeople.hsh.hsh.l1parser;


/** SimpleL1Node is a not Substitutable node.
 */
public class SimpleL1Node implements L1Node {
	private final String str;

	public SimpleL1Node(final String str) {
		this.str=str;
	}

	public String getString() {
		return str;
	}

	public void dump(final int level) {
		for(int i=0; i<level; i++)
			System.out.print("\t");
		System.out.println(getClass().getName());
		for(int i=0; i<level+1; i++)
			System.out.print("\t");
		System.out.println("value="+getString());
	}

}
