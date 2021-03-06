package com.happypeople.hsh.hsh.l1parser;

import org.apache.log4j.Logger;

import com.happypeople.hsh.hsh.HshParserConstants;
import com.happypeople.hsh.hsh.L2Token;
import com.happypeople.hsh.hsh.Token;
import com.happypeople.hsh.hsh.TokenManager;

/** This class is the Adapter between L1- and L2-Parser.
 * It translates a stream of L1-Nodes into a Stream of L2-Token.
 */
public class L2TokenManager implements TokenManager {
	private final Logger log=Logger.getLogger(L2TokenManager.class);

	private final L1Parser l1Parser;

	public L2TokenManager(final L1Parser l1Parser) {
		this.l1Parser=l1Parser;
	}

	@Override
	public Token getNextToken() {
		try {
			final L2Token t=l1Parser.nextL1Node();
			t.kind=translateL1KindToL2Kind(t.kind);
			log.debug("L2TokenManager.getNextToken(): "+HshParserConstants.tokenImage[t.kind]+":"+t.image);
			return t;
		} catch (final ParseException e) {
			throw new RuntimeException(e);
		}
	}

	/** Returns the L2-kind according to l1kind
	 * @param kind L1 kind
	 * @return L2 kind
	 */
	private int translateL1KindToL2Kind(final int l1kind) {
		switch(l1kind) {
		case L1ParserConstants.EOF:			return HshParserConstants.EOF;
		case L1ParserConstants.AND_IF:		return HshParserConstants.AND_IF;
		case L1ParserConstants.OR_IF:		return HshParserConstants.OR_IF;
		case L1ParserConstants.DSEMI:		return HshParserConstants.DSEMI;
		case L1ParserConstants.DLESS:		return HshParserConstants.DLESS;
		case L1ParserConstants.DGREAT:		return HshParserConstants.DGREAT;
		case L1ParserConstants.LESSAND:		return HshParserConstants.LESSAND;
		case L1ParserConstants.LESS:		return HshParserConstants.LESS;
		case L1ParserConstants.GREAT:		return HshParserConstants.GREAT;
		case L1ParserConstants.GREATAND: 	return HshParserConstants.GREATAND;
		case L1ParserConstants.LESSGREAT: 	return HshParserConstants.LESSGREAT;
		case L1ParserConstants.DLESSDASH: 	return HshParserConstants.DLESSDASH;
		case L1ParserConstants.CLOBBER:		return HshParserConstants.CLOBBER;
		case L1ParserConstants.NEWLINE: 	return HshParserConstants.NEWLINE;
		case L1ParserConstants.SEMICOLON: 	return HshParserConstants.SEMICOLON;
		case L1ParserConstants.UPPERSANT: 	return HshParserConstants.UPPERSANT;
		case L1ParserConstants.BANG:		return HshParserConstants.BANG;
		case L1ParserConstants.KLAMMER_AUF:	return HshParserConstants.KLAMMER_AUF;
		case L1ParserConstants.KLAMMER_ZU:	return HshParserConstants.KLAMMER_ZU;
		case L1ParserConstants.LBRACE:		return HshParserConstants.LBRACE;
		case L1ParserConstants.RBRACE:		return HshParserConstants.RBRACE;
		case L1ParserConstants.WS:			return -42;
		default:
			return HshParserConstants.WORD;
		}
	}
}
