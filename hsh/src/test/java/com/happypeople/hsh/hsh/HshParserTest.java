package com.happypeople.hsh.hsh;

import java.io.StringReader;

import org.junit.Test;

import com.happypeople.hsh.hsh.l1parser.L1Parser;
import com.happypeople.hsh.hsh.l1parser.L1ParserTokenManager;
import com.happypeople.hsh.hsh.l1parser.L2TokenManager;
import com.happypeople.hsh.hsh.l1parser.SimpleCharStream;
import com.happypeople.hsh.hsh.parser.CompleteCommand;

public class HshParserTest {

	private final static boolean DEBUG=true;

	public HshParser setup(final String input) {
		final L1ParserTokenManager tokenMgr=new L1ParserTokenManager(new SimpleCharStream(new StringReader(input)));
		final L1Parser parser=new L1Parser(tokenMgr);
		return new HshParser(new L2TokenManager(parser));
	}


	@Test
	public void testComplete_command4() throws ParseException {
		doTestCompleteCommand("echo \"a b c\"");
	}

	@Test
	public void testComplete_command3() throws ParseException {
		doTestCompleteCommand("echo 'a b c'");
	}

	@Test
	public void testComplete_command2() throws ParseException {
		doTestCompleteCommand("echo a b c");
	}

	@Test
	public void testComplete_command1() throws ParseException {
		doTestCompleteCommand("x");
	}

	private void doTestCompleteCommand(final String input) throws ParseException {
		final HshParser p=setup(input);
		final CompleteCommand cc=p.complete_command();
		if(DEBUG)
			cc.dump(0);
	}

}
