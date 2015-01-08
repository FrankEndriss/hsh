package com.happypeople.hsh.hsh.l0parser;

import java.util.Iterator;
import java.util.concurrent.ExecutorService;

import com.happypeople.hsh.hsh.syntaxparser.Token;
import com.happypeople.hshutil.util.AsyncIterator;

/**
 */
public class AbstractDNode extends com.happypeople.hsh.hsh.syntaxparser.Token implements DNode {
	private final AsyncIterator<DNode> delegate=new AsyncIterator<DNode>();
	private boolean firstCall=true;

	protected AbstractDNode(final int kind) {
		super(kind);
	}

	@Override
	public Iterator<DNode> iterator() {
		if(firstCall)
			return delegate;
		firstCall=false;
		throw new IllegalStateException("cannot iterate more than once");
	}

	@Override
	public void offer(final DNode child) {
		delegate.offer(child);
	}

	@Override
	public void close() {
		delegate.close();
	}

	@Override
	public Token getToken() {
		return this;
	}

	@Override
	public void parse(final AsyncIterator<Token> tokenstream, final ExecutorService executor) {
		// TODO async
		for(final DNode child : this)
			child.parse(tokenstream, executor);
	}
}
