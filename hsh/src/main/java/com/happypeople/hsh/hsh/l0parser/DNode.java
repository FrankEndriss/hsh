package com.happypeople.hsh.hsh.l0parser;

import java.util.concurrent.ExecutorService;

import com.happypeople.hsh.hsh.syntaxparser.Token;
import com.happypeople.hshutil.util.AsyncIterator;


/** Interface for all Nodes in DefaultParser.
 * They can be traversed, but only once!
 */
public interface DNode extends Iterable<DNode> {
	public void offer(DNode child);
	public void close();
	public com.happypeople.hsh.hsh.syntaxparser.Token getToken();

	/** Should parse this DNode and send the created tokens to tokenstream.
	 * May use executor to do so.
	 * @param tokenstream target for the outcome of the parsing process
	 * @param executor
	 */
	public void parse(AsyncIterator<Token> tokenstream, ExecutorService executor);
}
