package com.happypeople.hsh.hsh.l1parser;

/** A rule which can be applied to a Token (in HshParser)
 */
public interface ParserRule {
	public void apply(com.happypeople.hsh.hsh.Token t);
}
