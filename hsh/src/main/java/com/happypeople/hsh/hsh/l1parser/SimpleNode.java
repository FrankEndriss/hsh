package com.happypeople.hsh.hsh.l1parser;


/** SimpleNode is a not Substitutable node.
 */
public class SimpleNode implements L1Node {
	private final StringBuilder sb=new StringBuilder();

	public SimpleNode(final String str) {
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
