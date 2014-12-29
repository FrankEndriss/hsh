package com.happypeople.hsh.hsh.l1parser;

import static org.junit.Assert.fail;

import java.io.StringReader;

import org.junit.Test;

import com.happypeople.hsh.hsh.HshParserConstants;
import com.happypeople.hsh.hsh.L2Token;

public class L2TokenManagerTest {
	private final static boolean DEBUG=true;
	
	public L2TokenManager setup(String input) {
		final L1ParserTokenManager tokenMgr=new L1ParserTokenManager(new SimpleCharStream(new StringReader(input)));
		//tokenMgr.setDebugStream(System.out);
		final L1Parser parser=new L1Parser(tokenMgr);
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
			L2TokenManager tokenManager=setup(str[i]);
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

}
