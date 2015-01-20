package com.happypeople.hsh.hsh.l0parser;

import java.util.concurrent.ExecutorService;

import com.happypeople.hsh.hsh.syntaxparser.SyntaxParserConstants;
import com.happypeople.hshutil.util.AsyncIterator;

public class BacktickedDNode extends AbstractDNode {
	public BacktickedDNode() {
		super(SyntaxParserConstants.SUBSTITUTABLE);
	}

	@Override
	public void parse(final AsyncIterator<com.happypeople.hsh.hsh.syntaxparser.Token> tokenstream, final ExecutorService executor) {
		tokenstream.offer(new com.happypeople.hsh.hsh.syntaxparser.Token(SyntaxParserConstants.TEXT, "`"));
		// TODO implement
		if(true)
			throw new RuntimeException("not implemented");
		tokenstream.offer(new com.happypeople.hsh.hsh.syntaxparser.Token(SyntaxParserConstants.TEXT, "`"));
	}
}