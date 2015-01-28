package com.happypeople.hsh.hsh.l1parser;

import com.happypeople.hshutil.util.AsyncIterator;

public class GenericL1ParserTokenManager extends L1ParserTokenManager {
	private final AsyncIterator<Token> tokenQ=new AsyncIterator<Token>();

	public GenericL1ParserTokenManager(final L1Parser parserArg, final SimpleCharStream stream) {
		super(parserArg, stream);
	}

	public void close() {
		tokenQ.close();
	}

	public void put(final Token t) {
		tokenQ.offer(t);
	}

	@Override
	public Token getNextToken() {
		if(tokenQ.hasNext())
			return tokenQ.next();
		return new Token(L1ParserConstants.EOF);
	}

	@Override
	public void SwitchTo(final int state) {
		// ignore
	}


}
