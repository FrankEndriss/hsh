package com.happypeople.hsh.hsh;

import com.happypeople.hsh.hsh.l1parser.L1Node;
import com.happypeople.hsh.hsh.l1parser.L1ParserConstants;
import com.happypeople.hsh.hsh.l1parser.SimpleL1Node;

public class HshLookaheadHelper {
	private final HshParser parser;

	public HshLookaheadHelper(final HshParser parser) {
		this.parser=parser;
	}

	/** Checks if the next chars form the pattern <func_name>()
	 * L1Parser parses them as NAME, KLAMMER_AUF, KLAMMER_ZU, but
	 * since there can be WS in between them, that could be
	 * one L2Token, two L2Token or three L2Token.
	 * if return true, the next three L2Tokens where changed to NAME, KLAMMER_AUF, KLAMMER_ZU
	 * if return false, the tokens where not changed
	 * @return true if the tokens where setup
	 */
	public boolean lookahead_functionDef() {
		final int[] expectedKinds=new int[] { L1ParserConstants.NAME, L1ParserConstants.KLAMMER_AUF, L1ParserConstants.KLAMMER_ZU };
		L2Token t;

		// check if the outcome was created by a previous call
		if((t=getToken(1))!=null && t.kind==HshParserConstants.NAME5 &&
				(t=getToken(2))!=null && t.kind==HshParserConstants.KLAMMER_AUF &&
				(t=getToken(3))!=null && t.kind==HshParserConstants.KLAMMER_ZU)
			return true;

		// readahead at least as possible
		for(int i=0; i<3; i++) {
			t=getTokenOfPart(i);
			if(t==null || t.kind!=HshParserConstants.WORD)
				return false;
			final L1Node p=getPart(i);
			if(!(p instanceof SimpleL1Node))
				return false;
			if(((SimpleL1Node)p).getL1Kind()!=expectedKinds[i])
				return false;
		}

		(t=getToken(1)).splitFirstPart();
		t.kind=HshParserConstants.NAME5;
		(t=getToken(2)).splitFirstPart();
		t.kind=HshParserConstants.KLAMMER_AUF;
		(t=getToken(3)).splitFirstPart();
		t.kind=HshParserConstants.KLAMMER_ZU;

		return true;
	}

	/** Return the 0 based part found starting at the current L2Token
	 * @param idx idx of searched part
	 * @return the found part, or null if not exists (end of stream)
	 */
	public L1Node getPart(final int idx) {
		return getPart(idx, 1);
	}

	/** Return the 0 based part found starting at the L2Token at tIdx
	 * @param pIdx idx of searched part
	 * @param tIdx idx of the token
	 * @return the found part, or null if not exists (end of stream)
	 */
	private L1Node getPart(final int pIdx, final int tIdx) {
		final L2Token t=getToken(tIdx);
		if(t==null || t.getPartCount()==0)
			return null;

		if(t.getPartCount()>pIdx)
			return t.getPart(pIdx);
		return getPart(pIdx-t.getPartCount(), tIdx+1);
	}

	/** Does the same as getPart(), but returns the L2Token of that part
	 * @param pIdx
	 * @return
	 */
	public L2Token getTokenOfPart(final int pIdx) {
		return getTokenOfPart(pIdx, 1);

	}

	/** Does the same as getPart(), but returns the L2Token of that part
	 * @param pIdx
	 * @return
	 */
	private L2Token getTokenOfPart(final int pIdx, final int tIdx) {
		final L2Token t=getToken(tIdx);
		if(t==null || t.getPartCount()==0)
			return null;

		if(t.getPartCount()>pIdx)
			return t;

		return getTokenOfPart(pIdx-t.getPartCount(), tIdx+1);
	}


	/** If next token is of kind==WORD it is checked if the image.equals() the image
	 * of the reserved word with that kind.
	 * If yes, the tokens kind is set to that kind.
	 * @param kind
	 * @return true if the tokens image equals the reserved word of kind kind.
	 */
	public boolean lookahead_reserved(final int kind) {
		final L2Token t=getToken(1);
		if(t.kind==HshParserConstants.WORD && HshParserConstants.tokenImage[kind].equals(t.image)) {
			t.kind=kind;
			return true;
		}
		return false;
	}

	public boolean lookahead_isCmdPrefix() {
		return lookahead_isIoRedir() || lookahead_isAssignment();
	}

	/** Income {<NAME><EQUALS>[<WORD>]}
	 * @return true if getToken(1).kind==ASSIGNMENT_WORD
	 */
	public boolean lookahead_isAssignment() {
		final L2Token t=getToken(1);
		// check if a previous call of this method did
		// created the outcome.
		if(t.kind==HshParserConstants.ASSIGNMENT_WORD)
			return true;

		if(t==null || t.kind!=HshParserConstants.WORD)
			return false;

		if(t.getPartCount()<2)
			return false;

		L1Node p=t.getPart(0);
		if(!(p instanceof SimpleL1Node))
			return false;
		if(((SimpleL1Node)p).getL1Kind()!=L1ParserConstants.NAME)
			return false;

		p=t.getPart(1);
		if(!(p instanceof SimpleL1Node))
			return false;
		if(((SimpleL1Node)p).getL1Kind()!=L1ParserConstants.EQUALS)
			return false;

		t.kind=HshParserConstants.ASSIGNMENT_WORD;

		return true;
	}

	/** After L1 format of a redir is one, two or three L2Token:
	 * one:
	 * 	{[<NUMBER>] <"<"|">"> <FILENAME>}
	 * two:
	 * 	{<redir_operator>}{<FILENAME>}
	 * three:
	 * 	{<NUMBER>}{<redir_operator>}{<FILENAME>}
	 *
	 * where <redir_operator> is
	 * one of operators
	 * DLESS  DGREAT  LESSAHND  GREATAND  LESSGREAT  DLESSDAS CLOBBER
	 *
	 * If match, the outcome is two or three L2Token:
	 * 	[{<NUMBER>}]{<redir_operator>|<LESS>|<GREAT>}{<FILENAME>}
	 * @return true if match, else false
	 */
	public boolean lookahead_isIoRedir() {
		// check if a previous call of this method did
		// created the outcome.
		if(getToken(1).kind==HshParserConstants.IO_NUMBER ||
			(isHshRedirOperator(getToken(1).kind) && getToken(2)!=null && getToken(2).kind==HshParserConstants.WORD))
				return true;

		// match and count the parts, beginn with two
		final L1Node p1=getPart(0);
		final L1Node p2=getPart(1);
		if(p1==null || p2==null)
			return false;

		final L2Token filenameToken;
		if((isNUMBER(p1) && is_redir_operator(p2)) && isWORD(getTokenOfPart(3)) ||
			is_redir_operator(p1) && isWORD(getTokenOfPart(2))) {
			// match, do split
			if(isNUMBER(p1)) {
				getToken(1).splitFirstPart();
				getToken(1).kind=HshParserConstants.IO_NUMBER;
				final L2Token t2=getToken(2);
				t2.splitFirstPart();
				// if splitted io-operator is "<" or ">" we must set/translate the kind
				final int l1Kind=((SimpleL1Node)t2.getPart(0)).getL1Kind();
				if(l1Kind==L1ParserConstants.LESS)
					t2.kind=HshParserConstants.LESS;
				else if(l1Kind==L1ParserConstants.GREAT)
					t2.kind=HshParserConstants.GREAT;
			} else {
				final L2Token t1=getToken(1);
				t1.splitFirstPart();
				final int l1Kind=((SimpleL1Node)t1.getPart(0)).getL1Kind();
				if(l1Kind==L1ParserConstants.LESS)
					t1.kind=HshParserConstants.LESS;
				else if(l1Kind==L1ParserConstants.GREAT)
					t1.kind=HshParserConstants.GREAT;
			}

			return true;
		}
		return false;
	}

	private boolean isWORD(final L2Token t) {
		return t!=null && t.kind==HshParserConstants.WORD;
	}

	private boolean isNUMBER(final L1Node p) {
		return p!=null && p instanceof SimpleL1Node && ((SimpleL1Node)p).getL1Kind()==L1ParserConstants.NUMBER;
	}

	/** Checks if kind is a redir operator on Hsh-Level
	 * @param kind
	 * @return true if kind is a Hsh redir operator
	 */
	private boolean isHshRedirOperator(final int kind) {
		switch(kind) {
		case HshParserConstants.LESS:
		case HshParserConstants.GREAT:
		case HshParserConstants.DLESS:
		case HshParserConstants.DGREAT:
		case HshParserConstants.LESSAND:
		case HshParserConstants.GREATAND:
		case HshParserConstants.LESSGREAT:
		case HshParserConstants.DLESSDASH:
		case HshParserConstants.CLOBBER:
			return true;
		default:
			return false;
		}
	}

	private boolean is_redir_operator(final L1Node p) {
		if(p==null)
			return false;
		if(! (p instanceof SimpleL1Node))
			return false;
		switch(((SimpleL1Node)p).getL1Kind()) {
		case L1ParserConstants.LESS:
		case L1ParserConstants.GREAT:
		case L1ParserConstants.DLESS:
		case L1ParserConstants.DGREAT:
		case L1ParserConstants.LESSAND:
		case L1ParserConstants.GREATAND:
		case L1ParserConstants.LESSGREAT:
		case L1ParserConstants.DLESSDASH:
		case L1ParserConstants.CLOBBER:
			return true;
		default:
			return false;
		}
	}

	private L2Token getToken(final int i) {
		return (L2Token)parser.getToken(i);
	}
}
