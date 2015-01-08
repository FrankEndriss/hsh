package com.happypeople.hsh.hsh.l0parser;

import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;

import com.happypeople.hsh.hsh.syntaxparser.SyntaxParserConstants;
import com.happypeople.hshutil.util.AsyncIterator;

/** The leaves in the DNode tree.
 * TODO change Object to Token
 */
public class TextDNode extends com.happypeople.hsh.hsh.syntaxparser.Token implements DNode {
	private final AsyncIterator<Token> tokens=new AsyncIterator<Token>();

	public TextDNode() {
		super(SyntaxParserConstants.TEXT);
	}

	/** A TextDNode has no DNode children. */
	@Override
	public Iterator<DNode> iterator() {
		return Collections.EMPTY_LIST.iterator();
	}

	@Override
	public void offer(final DNode child) {
		throw new RuntimeException("not possible");
	}

	/** Called from the DefaultParser
	 * @param token
	 */
	public void add(final Token token) {
		tokens.offer(token);
	}

	@Override
	public void close() {
		tokens.close();
	}

	/** Called from DNodeTraversal
	 * @return the iterator over the Token
	 */
	public Iterator<Token> tokenIterator() {
		return tokens;
	}

	@Override
	public com.happypeople.hsh.hsh.syntaxparser.Token getToken() {
		return this;
	}

	@Override
	public void parse(final AsyncIterator<com.happypeople.hsh.hsh.syntaxparser.Token> tokenstream, final ExecutorService executor) {
		final Iterator<Token> iter=tokenIterator();
		while(iter.hasNext()) {
			final Token t=iter.next();
			final com.happypeople.hsh.hsh.syntaxparser.Token tOut=new com.happypeople.hsh.hsh.syntaxparser.Token();
			switch(t.kind) {
			case L0ParserConstants.TEXT:
				tOut.kind=SyntaxParserConstants.TEXT;
				break;
			case L0ParserConstants.WS:
				tOut.kind=SyntaxParserConstants.WS;
				break;
			case L0ParserConstants.NEWLINE:
				tOut.kind=SyntaxParserConstants.WS;
				break;
			default:
				throw new RuntimeException("bad kind in TextDNode: "+t.kind);
			}
			tOut.image=t.image;
			// TODO copy line info from t to tOut
			tokenstream.offer(tOut);
		}
	}
}
