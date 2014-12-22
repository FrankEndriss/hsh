package com.happypeople.hsh.hsh.parser;

import com.happypeople.hsh.hsh.Token;

public class SeparatorNode extends JJNode {

	private Token token;

	public void setToken(final Token t) {
		this.token=t;
	}

	public Token getToken() {
		return token;
	}
}
