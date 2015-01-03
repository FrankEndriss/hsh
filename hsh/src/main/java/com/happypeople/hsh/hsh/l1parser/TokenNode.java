package com.happypeople.hsh.hsh.l1parser;

import java.util.Collections;
import java.util.Iterator;


/** Base class for Token which translate 1:1 from L1 to L2 (Operators, NL, WS and EOF)
 * Luckily, all of these are WordSeparators, too.
 *
 */
public abstract class TokenNode implements L1Node, WordSeparator {

	private final Token t;

	public TokenNode(final Token t) {
		this.t=t;
	}

	public Token getToken() {
		return t;
	}

	@Override
	public Iterator<L1Node> iterator() {
		return Collections.emptyIterator();
	}

	@Override
	public String getString() {
		return t.image;
	}

	@Override
	public void dump(final int level) {
		for(int i=0; i<level; i++)
			System.out.print("\t");
		System.out.println(getClass().getName());
		for(int i=0; i<level+1; i++)
			System.out.print("\t");
		System.out.println("Token, kind="+L1ParserConstants.tokenImage[t.kind]+" image="+t.image);
	}

}