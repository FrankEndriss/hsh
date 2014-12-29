package com.happypeople.hsh.hsh.l1parser;

public class DQuotedL1Node extends ComplexL1Node {

	public String getString() {
		return "\""+super.getString()+"\"";
	}
}
