package com.happypeople.hsh.hsh.l1parser;

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
	 */
	@Override
	public Token getNextToken() {
		try {
			while(true) {
				if(cachedL2Token!=null) {
					if(!cachedL2TokenIsAppendable) {
						final L2Token ret=cachedL2Token;
						cachedL2Token=null;
						return ret;
					} // else it isAppendable. Try to append.

					final L1Node node=l1Parser.nextL1Node();
					final boolean isWordSeparator=node instanceof WordSeparator;
					if(isWordSeparator) {	// do not append
						final L2Token ret=cachedL2Token;
						cachedL2Token=new L2Token();
						cachedL2Token.addPart(node);
						cachedL2TokenIsAppendable=false;
						return ret;
					} // else do append
					cachedL2Token.addPart(node);
				} else {
					final L1Node node=l1Parser.nextL1Node();
					final boolean isWordSeparator=node instanceof WordSeparator;
					if(isWordSeparator) {	// do not append
						final L2Token ret=new L2Token();
						ret.addPart(node);
						return ret;
					} else {
						cachedL2Token=new L2Token();
						cachedL2Token.addPart(node);
						cachedL2TokenIsAppendable=true;
					}
				}
			}
		}catch(final ParseException e) {
			throw new TokenMgrError("error while parsing L1", 0);
		}
	}

}
