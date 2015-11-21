package com.happypeople.hsh.hsh;

import static org.junit.Assert.fail;

import java.io.StringReader;

import org.junit.Test;

import com.happypeople.hsh.hsh.l1parser.L1Parser;
import com.happypeople.hsh.hsh.l1parser.L1ParserConstants;
import com.happypeople.hsh.hsh.l1parser.ParseException;

public class L1ParserTest {

	private final static boolean DEBUG=true;

	@Test
	public void test_function_def() throws ParseException {
		final String[] ok={
				"myEcho() ( echo hallo ; )",
				"myEcho() ( echo hallo ; cat bla.txt ; )",
				"myEcho() { echo hallo ; }",
				"myEcho() { echo hallo ; cat bla.txt ; }"
		};

		for(int i=0; i<ok.length; i++) {
			try {
				do_parse(i, ok[i]);
			}catch(final ParseException e) {
				if(DEBUG)
					System.out.println("ParseException at test "+i+" in="+ok[i]);
				throw e;
			}catch(final TokenMgrError e) {
				if(DEBUG)
					System.out.println("TokenMgrError at test "+i+" in="+ok[i]);
				throw e;
			}
		}
	}

	@Test
	public void test_simple_quoting() throws ParseException {
		final String[] ok={
			"\n",
			"yzx",
			"a",
			"a x",
				// and newline
			"a\n x",
			"axx\nxx\n",
			"ax x\n \nxx\n",
				// and dquote
			"a\"x\"",
			"a\" x\"",
			"a \"\" xx \"\" x",
				// and squote
			"a\"' x\"",
			"''''",
			"a \"\"'x' '\"' \\xx \"\" x",

			"",
			"''",
			"\"\"",
			"``",
			// some dollar
			"${xy}",
			"${xy:-hallo}",
			"${xy:+hallo}",
			"${x_:?hallo}"
		};
		for(int i=0; i<ok.length; i++) {
			try {
				do_parse(i, ok[i]);
			}catch(final ParseException e) {
				if(DEBUG)
					System.out.println("ParseException at test "+i+" in="+ok[i]);
				throw e;
			}catch(final TokenMgrError e) {
				if(DEBUG)
					System.out.println("TokenMgrError at test "+i+" in="+ok[i]);
				throw e;
			}
		}
	}

	@Test
	public void test_backticsInQuotes() throws ParseException {
		final String str="\"`x`\"";
		do_parse(0, str);
	}

	@Test
	public void test_quote_failing() {
		final String[] ok={
				// some strings with ws
			"'",
			"\n\"",
			"bz'x",
			"'b",
			"b' x",
				// and newline
			"b\n' x",
			"bxx\nx'x\n",
			"'bx x\n \nxx\n",
				// and dquote
			"\"\"'",
			"b'\"x\"",
			"b'\" x\"",
			"b \"\" x'x \"\" x",
				// and squote
			"b\"` x\"'",
			"b \"\"'x' '\"' \\x'x \"\" x",
			"${x :=hallo}"
		};
		for(int i=0; i<ok.length; i++) {
			try {
				do_parse(i, ok[i]);
				fail("should have failed at: "+i+" in:>"+ok[i]+"<");
			}catch(final ParseException e) {
				// ignore
			}catch(final com.happypeople.hsh.hsh.l1parser.TokenMgrError e) {
				// ignore
			}
		}
	}


	private void do_parse(final int tNum, final String test) throws ParseException {
		if(DEBUG)
			System.out.println("do_parse "+tNum+": "+test);
		final L1Parser parser=new L1Parser(new StringReader(test));
		L2Token node=null;
		while(((node=parser.nextL1Node()).kind!=L1ParserConstants.EOF))
			if(DEBUG)
				System.out.println(""+node);
	}

}
