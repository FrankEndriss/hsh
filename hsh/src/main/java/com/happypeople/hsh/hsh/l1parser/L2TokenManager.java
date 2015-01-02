package com.happypeople.hsh.hsh.l1parser;

import com.happypeople.hsh.hsh.HshParserConstants;
import com.happypeople.hsh.hsh.L2Token;
import com.happypeople.hsh.hsh.Token;
import com.happypeople.hsh.hsh.TokenManager;

/** This class is the Adapter between L1- and L2-Parser.
 * It translates a stream of L1-Nodes into a Stream of L2-Token.
 */
public class L2TokenManager implements TokenManager {
	private final L1Parser l1Parser;

	public L2TokenManager(final L1Parser l1Parser) {
		this.l1Parser=l1Parser;
	}

	private L2Token cachedL2Token=null;
	private boolean cachedL2TokenIsAppendable=false;

	/* (non-Javadoc)
	 * @see com.happypeople.hsh.hsh.TokenManager#getNextToken()
	 * White spaces are appended as childs of otherwise appendable L2Token. So, they do not act as L2Token,
	 * but they are reconstructable later in the process while substituting (and concatenating).
	 */
	@Override
	public Token getNextToken() {
		// cases:
		// ct==null
		//  1. nt not appendable -> nt is returned
		//  2. nt appendable -> ct=nt, continue
		// ct!=null
		//  3. ct not appendable -> ct is returned, ct=null
		//  ct appendable
		//   4. nt not appendable -> ct is returned, ct=nt
		//   5. nt appendable -> append, continue
		try {
			while(true) {
				if(cachedL2Token==null) {
					final L1Node nt=l1Parser.nextL1Node();
					if(isWS(nt))
						continue;
					final boolean isWordSeparator=nt instanceof WordSeparator;
					final L2Token ret=createL2Token(nt);
					if(isWordSeparator) { // case 1.
						return ret;
					} else {	// case 2.
						cachedL2Token=ret;
						cachedL2TokenIsAppendable=true;
						// continue
					}
				} else { // cachedL2Token!=null
					if(!cachedL2TokenIsAppendable) { // case 3.
						final L2Token ret=cachedL2Token;
						cachedL2Token=null;
						return ret;
					} else {
						final L1Node nt=l1Parser.nextL1Node();
						final boolean isWordSeparator=nt instanceof WordSeparator;
						if(isWordSeparator) { // case 4.
							final L2Token ret=cachedL2Token;
							if(isWS(nt)) {
								ret.addPart(nt);
								cachedL2Token=null;
							} else {
								cachedL2Token=createL2Token(nt);
								cachedL2TokenIsAppendable=false;
							}
							return ret;
						} else { // case 5.
							cachedL2Token.addPart(nt);
						}
					}
				}
			}
		}catch(final ParseException e) {
			throw new TokenMgrError("error while parsing L1", 0);
		}
	}

	private boolean isWS(final L1Node l1node) {
		return l1node instanceof WsL1Node;
	}

	/** Creates a new L2Token with the first child l1node, kind set according
	 * to the kind of l1node
	 * @param l1node
	 * @return a new L2Token
	 */
	private L2Token createL2Token(final L1Node l1node) {
		final L2Token token=new L2Token();
		token.addPart(l1node);

		if(l1node instanceof TokenNode)
			token.kind=translateL1KindToL2Kind(((TokenNode)l1node).getToken().kind);
		else
			token.kind=HshParserConstants.WORD;
		return token;
	}

	private final static int[] kindMap=new int[Math.max(
			L1ParserConstants.tokenImage.length,
			HshParserConstants.tokenImage.length)];
	static {
		for(int i=0; i<kindMap.length; i++)
			kindMap[i]=-1;	// undefined

		kindMap[L1ParserConstants.EOF]=HshParserConstants.EOF;
		kindMap[L1ParserConstants.AND_IF]=HshParserConstants.AND_IF;
		kindMap[L1ParserConstants.OR_IF]=HshParserConstants.OR_IF;
		kindMap[L1ParserConstants.DSEMI]=HshParserConstants.DSEMI;
		kindMap[L1ParserConstants.DLESS]=HshParserConstants.DLESS;
		kindMap[L1ParserConstants.DGREAT]=HshParserConstants.DGREAT;
		kindMap[L1ParserConstants.LESSAND]=HshParserConstants.LESSAND;
		kindMap[L1ParserConstants.GREATAND]=HshParserConstants.GREATAND;
		kindMap[L1ParserConstants.LESSGREAT]=HshParserConstants.LESSGREAT;
		kindMap[L1ParserConstants.DLESSDASH]=HshParserConstants.DLESSDASH;
		kindMap[L1ParserConstants.CLOBBER]=HshParserConstants.CLOBBER;
		kindMap[L1ParserConstants.NEWLINE]=HshParserConstants.NEWLINE;
		kindMap[L1ParserConstants.SEMICOLON]=HshParserConstants.SEMICOLON;
		// TODO others
	}

	/** Returns the L2-kind according to l1kind
	 * @param kind L1 kind
	 * @return L2 kind
	 */
	private int translateL1KindToL2Kind(final int l1kind) {
		final int l2kind=kindMap[l1kind];
		if(l2kind<0)
			throw new RuntimeException("internal error: cannot map L1-Kind: "+l1kind);
		return l2kind;
	}
}
