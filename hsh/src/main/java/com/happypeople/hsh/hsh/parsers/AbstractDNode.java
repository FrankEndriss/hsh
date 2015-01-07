package com.happypeople.hsh.hsh.parsers;

import java.util.Iterator;

import com.happypeople.hshutil.util.AsyncIterator;

/**
 */
public class AbstractDNode implements DNode {
	private final AsyncIterator<DNode> delegate=new AsyncIterator<DNode>();
	private boolean firstCall=true;

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

	public void close() {
		delegate.close();
	}
}
