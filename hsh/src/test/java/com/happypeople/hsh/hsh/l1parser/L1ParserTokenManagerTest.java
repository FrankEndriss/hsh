package com.happypeople.hsh.hsh.l1parser;

import static org.junit.Assert.assertEquals;

import java.io.StringReader;

import org.junit.Before;
import org.junit.Test;

public class L1ParserTokenManagerTest {

	private final static boolean DEBUG=false;

	private class TTest {
		TTest(final int parserState, final String s, final int c) {
			this.str=s;
			this.count=c;
			this.parserState=parserState;
		}
		public String str;
		public int count;
		public int parserState;
	};

	private L1ParserTokenManager tokenMgr;

	@Before
	public void init() {
	}

	@Test
	public void testCountToken() {
		final TTest[] tests={
				// WS and NEWLINE
				new TTest(L1ParserConstants.DEFAULT, "", 0),
				new TTest(L1ParserConstants.DEFAULT, "a", 1),
				new TTest(L1ParserConstants.DEFAULT, "bx", 1),
				new TTest(L1ParserConstants.DEFAULT, "cx ", 2),
				new TTest(L1ParserConstants.DEFAULT, "dx xx", 3),
				new TTest(L1ParserConstants.DEFAULT, "ex xx ", 4),
				new TTest(L1ParserConstants.DEFAULT, "fx  xx ", 4),
				new TTest(L1ParserConstants.DEFAULT, "gx \txx ", 4),
				new TTest(L1ParserConstants.DEFAULT, "hx 	\nxx ", 5),
				new TTest(L1ParserConstants.DEFAULT, "ix \t\n\nxx ", 6),
				// SQUOTE
				new TTest(L1ParserConstants.SQUOTED, "", 0),
				new TTest(L1ParserConstants.SQUOTED, "a", 1),
				new TTest(L1ParserConstants.SQUOTED, " a", 1),
				new TTest(L1ParserConstants.SQUOTED, " a\n", 1),
				new TTest(L1ParserConstants.SQUOTED, " \"a\n", 1),
				new TTest(L1ParserConstants.SQUOTED, " \\a\n", 1),
				new TTest(L1ParserConstants.SQUOTED, " \\a\n ", 1),
				new TTest(L1ParserConstants.SQUOTED, " \\a\n \\", 1),
				new TTest(L1ParserConstants.SQUOTED, " \\a\n '", 2),
				new TTest(L1ParserConstants.SQUOTED, " \\a\n \\'", 2),
				// DQUOTE
				new TTest(L1ParserConstants.DQUOTED, "", 0),
				new TTest(L1ParserConstants.DQUOTED, "a", 1),
				new TTest(L1ParserConstants.DQUOTED, "bb", 1),
				new TTest(L1ParserConstants.DQUOTED, "c\\b", 3),	// backslash does _not_ escape every char
				new TTest(L1ParserConstants.DQUOTED, "d\\\"", 2),	// but does " $ ` and \
				new TTest(L1ParserConstants.DQUOTED, "d\\$", 2),
				new TTest(L1ParserConstants.DQUOTED, "d\\`", 2),
				new TTest(L1ParserConstants.DQUOTED, "d\\\\", 2),
				new TTest(L1ParserConstants.DQUOTED, "d\"", 2),
				// BACKTICKED
				new TTest(L1ParserConstants.BACKTICKED, "", 0),
				new TTest(L1ParserConstants.BACKTICKED, "a", 1),
				new TTest(L1ParserConstants.BACKTICKED, "aa", 1),
				new TTest(L1ParserConstants.BACKTICKED, "a\\a", 2),
				new TTest(L1ParserConstants.BACKTICKED, "a\\`", 2),
				// TODO DOLLAR_LBRACE_START
				new TTest(L1ParserConstants.DEFAULT, "${x}", 3),
				new TTest(L1ParserConstants.DEFAULT, "${x:-hallo}", 5),
				new TTest(L1ParserConstants.DEFAULT, "${x:+hallo}", 5),
				new TTest(L1ParserConstants.DEFAULT, "${x:?hallo}", 5),
				new TTest(L1ParserConstants.DEFAULT, "${x:=hallo}", 5),
				new TTest(L1ParserConstants.DEFAULT, "${x\\=hallo}", 3),
		};
		int i=0;
		for(final TTest ttest : tests)
			countToken(ttest, "Test"+(i++)+":"+ttest.str);
	}

	final static int MAXLOOP=1000;
	private void countToken(final TTest ttest, final String message) {
		final L1Parser parser=new L1Parser(new StringReader(ttest.str));
		tokenMgr=parser.token_source;
		tokenMgr.pushState(0);
		tokenMgr.SwitchTo(ttest.parserState);


		//tokenMgr.ReInit(new SimpleCharStream(new StringReader(ttest.str)), ttest.parserState);

		if(DEBUG)
			System.out.println("new Test, state="+tokenMgr.curLexState);
		int i=0;
		Token t;
		while((t=tokenMgr.getNextToken()).kind!=L1ParserConstants.EOF && i<MAXLOOP) {
			i++;
			if(DEBUG)
				System.out.println(L1ParserConstants.tokenImage[t.kind]+": "+t);
		}
		assertEquals(message, ttest.count, i);
	}
}
