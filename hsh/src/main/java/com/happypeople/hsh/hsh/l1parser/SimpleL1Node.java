package com.happypeople.hsh.hsh.l1parser;


/** SimpleL1Node is a not Substitutable node.
 */
public class SimpleL1Node implements L1Node {
	private final StringBuilder sb=new StringBuilder();

	public SimpleL1Node(final String str) {
		sb.append(str);
	}

	public String getString() {
		return sb.toString();
	}

	/*
	public void append(final String s) {
		sb.append(s);
	}
	*/

	@Override
	public String toString() {
		return getString();
	}


}
