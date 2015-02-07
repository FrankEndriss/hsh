package com.happypeople.hsh.hsh.l1parser;


public class GenericL1ParserTokenManager extends L1ParserTokenManager {
	private final boolean DEBUG=false;

	private final Callback callback;

	public GenericL1ParserTokenManager(final Callback callback) {
		super(null, null);
		this.callback=callback;
	}

	public interface Callback {
		public Token nextToken();
	}

	@Override
	public Token getNextToken() {
		final Token t=callback.nextToken();
		if(DEBUG)
			System.out.println("GenericL1ParserTokenManager.getNextToken(), kind="+t.kind+" image="+t.image);
		return t;
	}

	@Override
	public void SwitchTo(final int state) {
		// ignore
	}


}
