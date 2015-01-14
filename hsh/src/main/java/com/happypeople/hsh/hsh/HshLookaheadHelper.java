package com.happypeople.hsh.hsh;

public class HshLookaheadHelper {
	private final HshParser parser;

	public HshLookaheadHelper(final HshParser parser) {
		this.parser=parser;
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

	public boolean lookahead_isAssignment() {
		final L2Token t=getToken(1);
		if(t.kind!=HshParserConstants.WORD)
			return t.kind==HshParserConstants.ASSIGNMENT_WORD;

		if(getToken(1).image.indexOf('=')>0) {
			getToken(1).kind=HshParserConstants.ASSIGNMENT_WORD;
			return true;
		}
		return false;
	}

	/** Format of a redir is:
	 *
	 * 	[<IO_NUMBER>]<redir_operator><FILENAME>
	 * where <redir_operator> is
	 * one of operators DLESS  DGREAT  LESSAHND  GREATAND  LESSGREAT  DLESSDAS CLOBBER
	 * or one of the Strings "<" or ">" (since these are not operators)
	 * case1: In the case of "<" and ">" the whole redir is one word.
	 * case2: In the case of the other operators, it is three words.
	 * @return
	 */
	public boolean lookahead_isIoRedir() {
		final L2Token t1=getToken(1);

		if(t1.kind!=HshParserConstants.WORD)	// if not WORD dont change
			return t1.kind==HshParserConstants.IO_NUMBER
				|| t1.kind==HshParserConstants.DLESS
				|| t1.kind==HshParserConstants.DGREAT
				|| t1.kind==HshParserConstants.LESSAND
				|| t1.kind==HshParserConstants.GREATAND
				|| t1.kind==HshParserConstants.LESSGREAT
				|| t1.kind==HshParserConstants.DLESSDASH
				|| t1.kind==HshParserConstants.CLOBBER
				|| t1.kind==HshParserConstants.LESS
				|| t1.kind==HshParserConstants.GREAT;


		// case1: optional digits followed by "<" or ">" followed by at least one char
		final char[] chars=t1.image.toCharArray();
		for(int i=0; i<chars.length; i++) {
			if(Character.isDigit(chars[i])) { // skip digits
				;
			} else if(chars[i]=='<' || chars[i]=='>') {
				if(i>=chars.length-1)
					return false;	// no chars after the operator

				// match, replace the single token by two or three (with io_number) tokens
				// TODO t1 could have arbitrary parts!!!
				if(i==0) { // two token
					t1.kind=chars[i]=='<'?HshParserConstants.LESS:HshParserConstants.GREAT;
					t1.image=new String(chars, 0, 1);
					final L2Token t2=new L2Token();
					t2.kind=HshParserConstants.WORD;
					t2.image=new String(chars, 1, chars.length-1);
					connectTokens(t1, t2);
					return true;
				} else {	// three token
					t1.kind=HshParserConstants.IO_NUMBER;
					t1.image=new String(chars, 0, i);
					final L2Token t2=new L2Token();
					t2.kind=chars[i]=='<'?HshParserConstants.LESS:HshParserConstants.GREAT;
					t2.image=new String(chars, i, 1);
					connectTokens(t1, t2);
					final L2Token t3=new L2Token();
					t3.kind=HshParserConstants.WORD;	// filename
					t3.image=new String(chars, i+1, chars.length-i-1);
					connectTokens(t2, t3);
					return true;
				} // else non-digit before operator, does not match
			} else
				break;
		}

		// case 2
		return isDigits(getToken(1).image) && is_redir_operator(getToken(2));
	}

	/** Inserts Token t2 after t1
	 * @param t1
	 * @param t2
	 */
	private void connectTokens(final L2Token t1, final L2Token t2) {
		t2.next=t1.next;
		t1.next=t2;
	}

	private boolean isDigits(final String s) {
		for(int i=0; i<s.length(); i++) {
			if(!Character.isDigit(s.charAt(i)))
				return false;
		}
		return true;
	}

	private boolean is_redir_operator(final Token t) {
		switch(t.kind) {
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

	private L2Token getToken(final int i) {
		return (L2Token)parser.getToken(i);
	}
}
