package com.happypeople.hsh.hsh.l1parser;

/** Base class for Token which translate 1:1 from L1 to L2 (Operators, NL, WS and EOF)
 *
 */
public abstract class TokenNode implements L1Node {

	protected final Token t;

	public TokenNode(final Token t) {
		this.t=t;
	}

	public Token getToken() {
		return t;
	}

}