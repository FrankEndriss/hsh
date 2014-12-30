package com.happypeople.hsh.hsh.l1parser;

import java.util.Iterator;

import com.happypeople.hsh.HshContext;

public class BacktickedNode extends SimpleL1Node implements Substitutable {

	public BacktickedNode(final String command) {
		super(command);
	}

	@Override
	public Iterator<String> doSubstitution(final HshContext env) throws ParseException {
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

	public void dump(final int level) {
		for(int i=0; i<level; i++)
			System.out.print("\t");
		System.out.println(getClass().getName());
		for(int i=0; i<level+1; i++)
			System.out.print("\t");
		System.out.println("command="+getString());
	}

}
