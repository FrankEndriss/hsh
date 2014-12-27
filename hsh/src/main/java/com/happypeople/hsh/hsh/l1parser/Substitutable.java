package com.happypeople.hsh.hsh.l1parser;

import java.util.Iterator;

/** A L1Node which can and should be substituted by a list of Nodes of type N
 * @param the type of the result of the substitution.
 */
public interface Substitutable<N> extends L1Node {
	public Iterator<N> doSubstitution() throws ParseException;
}
