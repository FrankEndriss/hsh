package com.happypeople.hsh.hsh;

import static org.junit.Assert.fail;

import java.io.StringReader;

import org.junit.Test;

public class L1ParserTest {

	@Test
	public void test_simple_quoting() throws ParseException {
		final String[] ok={
				// some strings with ws
			"",
			"\n",
			"yzx",
			"x",
			"x x",
				// and newline
			"x\n x",
			"xxx\nxx\n",
			"xx x\n \nxx\n",
				// and dquote
			"\"\"",
			"x\"x\"",
			"x\" x\"",
			"x \"\" xx \"\" x",
				// and squote
			"x\"` x\"",
			"x \"\"'x' '\"' \\xx \"\" x",
		};
		for(int i=0; i<ok.length; i++) {
			try {
				do_parse(i, ok[i]);
			}catch(final ParseException e) {
				System.out.println("ParseException at test "+i);
				throw e;
			}
		}
	}

	@Test
	public void test_quote_failing() {
		final String[] ok={
				// some strings with ws
			"'",
			"\n\"",
			"yz'x",
			"'x",
			"x' x",
				// and newline
			"x\n' x",
			"xxx\nx'x\n",
			"'xx x\n \nxx\n",
				// and dquote
			"\"\"'",
			"x'\"x\"",
			"x'\" x\"",
			"x \"\" x'x \"\" x",
				// and squote
			"x\"` x\"'",
			"x \"\"'x' '\"' \\x'x \"\" x",
		};
		for(int i=0; i<ok.length; i++) {
			try {
				do_parse(i, ok[i]);
				fail("should have failed at: "+i+" in:>"+ok[i]+"<");
			}catch(final ParseException e) {
				// ignore
			}
		}
	}


	private void do_parse(final int tNum, final String test) throws ParseException {
		final L1ParserTokenManager tokenMgr=new L1ParserTokenManager(new SimpleCharStream(new StringReader(test)));
		//tokenMgr.setDebugStream(System.out);
		final L1Parser parser=new L1Parser(tokenMgr);
		//parser.enable_tracing();
		parser.words();
	}

}
