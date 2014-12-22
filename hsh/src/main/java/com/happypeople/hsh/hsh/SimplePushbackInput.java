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
	private Stack<Character> pushbackStack=new Stack<Character>();

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

	/** A Transaction takes a snapshot of this SimplePushbackInput while it is created.
	 * Later, on rollback(), the SimplePushbackInput is restored to the same state as the
	 * snapshot.
	 * On commit() nothing is done but to release this Transaction.
	 * 
	 * One can create nested Transactions. This is, after creating one Transaction you create another one.
	 * The commit and rollback calls must be made in reverse order of creation of the Transaction.
	 * So, you have to commit()/rollback() allways the last created Transaction, else you get an Exception.
	 * 
	 * The committing/rollback of an Transaction allways includes the actions of a sub-Transaction.
	 * So, if you create two Transactions, committing the second, and rollback the first, the actions of 
	 * the second Transaction are rolled back, too.
	 */
	public class Transaction {
		private final Stack<Character> stackCopy=(Stack<Character>)pushbackStack.clone();
		private final LinkedList<Character> chars=new LinkedList<Character>();

		/** Closes this Transaction without modifying the underling Stream.
		 */
		public void commit() {
			if(transactionStack.peek()!=this)
				throw new RuntimeException("cannot commit, there are unclosed sub-transactions");
			transactionStack.pop();
		}

		/** Closes this Transaction after restoring the underling Stream to the state when this
		 * Transaction was created.
		 */
		public void rollback() {
			if(transactionStack.peek()!=this)
				throw new RuntimeException("cannot rollback, there are unclosed sub-transactions");
			pushbackStack=stackCopy;
			while(!chars.isEmpty())
				pushback(chars.pop());
			transactionStack.pop();
		}

		private void didRead(final char c) {
			chars.push(c);
		}
	}
}
