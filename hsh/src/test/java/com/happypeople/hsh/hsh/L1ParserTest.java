package com.happypeople.hsh.hsh;

import static org.junit.Assert.fail;

import java.io.StringReader;

import org.junit.Test;

import com.happypeople.hsh.hsh.l1parser.EofL1Node;
import com.happypeople.hsh.hsh.l1parser.L1Node;
import com.happypeople.hsh.hsh.l1parser.L1Parser;
import com.happypeople.hsh.hsh.l1parser.ParseException;

public class L1ParserTest {

	private final static boolean DEBUG=false;

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
			"``"
		};
		for(int i=0; i<ok.length; i++) {
			try {
				do_parse(i, ok[i]);
			}catch(final ParseException e) {
				if(DEBUG)
					System.out.println("ParseException at test "+i);
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
		};
		for(int i=0; i<ok.length; i++) {
			try {
				do_parse(i, ok[i]);
				fail("should have failed at: "+i+" in:>"+ok[i]+"<");
			}catch(final ParseException e) {
				// ignore
			}catch(final TokenMgrError e) {
				// ignore
			}
		}
	}


	private void do_parse(final int tNum, final String test) throws ParseException {
		if(DEBUG)
			System.out.println("do_parse "+tNum+": "+test);
		//final L1ParserTokenManager tokenMgr=new L1ParserTokenManager(new SimpleCharStream(new StringReader(test)));
		//tokenMgr.setDebugStream(System.out);
		final L1Parser parser=new L1Parser(new StringReader(test));
		//parser.enable_tracing();
		L1Node node=null;
		while(!((node=parser.nextL1Node())instanceof EofL1Node))
			if(DEBUG)
				System.out.println(""+node);
	}

}
