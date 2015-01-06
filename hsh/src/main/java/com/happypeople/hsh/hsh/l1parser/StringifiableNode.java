package com.happypeople.hsh.hsh.l1parser;

/** A StringifiableNode is a Node which can be translated into a String without being substituted.
 * The String value is anything but syntax-values removed.
 * i.e
 * "hallo" -> hallo
 * "\\" -> \
 */
public interface StringifiableNode {
	public void append(StringBuilder sb);
}
