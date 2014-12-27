package com.happypeople.hsh.hsh.l1parser;

import java.util.Iterator;

public class BacktickedNode implements Substitutable<String> {
	private final String command;

	public BacktickedNode(final String command) {
		this.command=command;
	}

	@Override
	public Iterator<String> doSubstitution() throws ParseException {
		// TODO:
		// -do create HshParser
		// -do run parser over command
		// -do substitution etc
		// -do execution
		// -grab output of execution
		// -output should be parsed into simple words (like squoted) according to $IFS
		// -and returned throu iterator
		return null;
	}

}
