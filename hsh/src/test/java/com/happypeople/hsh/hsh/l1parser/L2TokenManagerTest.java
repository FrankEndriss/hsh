package com.happypeople.hsh.hsh.l1parser;


public class L2TokenManagerTest {
	private final static boolean DEBUG=false;

	/*
	public L2TokenManager setup(final String input) {
		final L1Parser parser=new L1Parser(new StringReader(input));
		return new L2TokenManager(parser);
	}

	@Test
	public void test() {
		final String bt=""+L1ParserConstants.tokenImage[L1ParserConstants.BACKTIC].toCharArray()[1];
		final String[] str={
				"x",
				"xx",
				"x x",
				"x ,x",
				"x "+bt+"bla"+bt+"x",
				"x "+bt+"bla"+bt+" x",
				"x \"abc\"x",
				"x x'abc'",
				"x x'abc' x"
		};

		for(int i=0; i<str.length; i++) {
			if(DEBUG)
				System.out.println("Test "+i+":"+str[i]);
			final L2TokenManager tokenManager=setup(str[i]);
			L2Token t;
			final int MAXLOOP=1000;
			int c=0;
			do {
				if(++c>MAXLOOP)
					fail("MAXLOOP reached");
				t=((L2Token)tokenManager.getNextToken());
				if(DEBUG)
					t.dump(0);
			} while(t.kind!=HshParserConstants.EOF);
		}
	}
	*/

}
