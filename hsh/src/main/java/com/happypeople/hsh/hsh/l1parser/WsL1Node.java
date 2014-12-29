package com.happypeople.hsh.hsh.l1parser;

public class WsL1Node extends TokenNode {
	public WsL1Node(final Token t) {
		super(t);
	}
	
	/* (non-Javadoc)
	 * @see com.happypeople.hsh.hsh.l1parser.TokenNode#getString()
	 * Note: WS is returned as an empty String because WS is not handed from L1- to Hsh-Parser.
	 */
	public String getString() {
		return "";
	}
}
