package com.happypeople.hsh.hsh;

import org.junit.Test;

public class HshParserTest_WhileLoop extends HshParserTest {
	@Test
	public void testWhile3() throws Exception {
		parseTo_CompleteCommand("while false; true; do sleep 5; done");
		parseTo_CompleteCommand("until false; true; do sleep 5; done");
		// TODO assertions
	}

	@Test
	public void testWhile2() throws Exception {
		parseTo_CompleteCommand("while ! false; do sleep 5 ; done");
		parseTo_CompleteCommand("until ! false; do sleep 5 ; done");
		// TODO assertions
	}

	@Test
	public void testWhile1() throws Exception {
		parseTo_CompleteCommand("while true; do sleep 5 ; done");
		parseTo_CompleteCommand("until true; do sleep 5 ; done");
		// TODO assertions
	}

}
