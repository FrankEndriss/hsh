package com.happypeople.hsh.hsh;

import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;
import java.util.Stack;

/** Simplyfication of a character stream.
 * Transactional reading and rollback.
 */
public class SimplePushbackInput {
	private final Reader in;
	/** pushback Stack */
	private final Stack<Character> pushbackStack=new Stack<Character>();

	/** stack of Transactions */
	private Stack<Transaction> transactionStack;

	public SimplePushbackInput(final Reader reader) {
		this.in=reader;
	}

	/**
	 * @return the next read char, or -1 for EOF or Error
	 */
	public char read() {
		try {
			if(!pushbackStack.isEmpty())
				return pushbackStack.pop();

			final char ret=(char)in.read();
			final Transaction trans=transactionStack.peek();
			if(trans!=null)
				trans.didRead(ret);
			return ret;
		}catch(final IOException e) {
			e.printStackTrace(System.err);
			return (char)-1;
		}
	}

	public void pushback(final char c) {
		pushbackStack.push(c);
	}

	public Transaction transactionStart() {
		final Transaction trans=new Transaction();
		transactionStack.push(trans);
		return trans;
	}

	/**
	 */
	public class Transaction {
		private final Stack<Character> stackCopy=(Stack<Character>)pushbackStack.clone();
		private final LinkedList<Character> chars=new LinkedList<Character>();

		public void commit() {
			if(transactionStack.peek()!=this)
				throw new RuntimeException("cannot commit, there are unclosed sub-transactions");
			transactionStack.pop();
		}

		public void rollback() {
			if(transactionStack.peek()!=this)
				throw new RuntimeException("cannot rollback, there are unclosed sub-transactions");
			pushbackStack=stackCopy();
			while(!chars.isEmpty())
				pushback(chars.pop());
			transactionStack.pop();
		}

		private void didRead(final char c) {
			chars.push(c);
		}
	}
}
