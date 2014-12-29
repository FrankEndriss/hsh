package com.happypeople.hsh.hsh;

import static org.junit.Assert.*;

import java.io.StringReader;

import org.junit.Test;

import com.happypeople.hsh.hsh.l1parser.L1Parser;
import com.happypeople.hsh.hsh.l1parser.L1ParserTokenManager;
import com.happypeople.hsh.hsh.l1parser.L2TokenManager;
import com.happypeople.hsh.hsh.l1parser.SimpleCharStream;
import com.happypeople.hsh.hsh.parser.CompleteCommand;

public class HshParserTest {
	
	private final static boolean DEBUG=true;
	
	public HshParser setup(String input) {
		final L1ParserTokenManager tokenMgr=new L1ParserTokenManager(new SimpleCharStream(new StringReader(input)));
		final L1Parser parser=new L1Parser(tokenMgr);
		return new HshParser(new L2TokenManager(parser));
	}


	@Test
	public void testComplete_command() throws ParseException {
		HshParser p=setup("x");
		CompleteCommand cc=p.complete_command();
		if(DEBUG)
			cc.dump(0);
	}

}
