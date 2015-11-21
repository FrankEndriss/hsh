package com.happypeople.hsh.hsh;

import org.junit.Test;

public class HshParserTest_IORedirect extends HshParserTest {
	@Test
	public void testIORedirect3() throws Exception {
		parseTo_CompleteCommand("<infile.txt >outfile.txt 2>errfile.txt cat");
		// TODO assertions
	}

	@Test
	public void testIORedirect2() throws Exception {
		parseTo_CompleteCommand("<file.txt cat");
		// TODO assertions
	}

	@Test
	public void testIORedirect1() throws Exception {
		parseTo_CompleteCommand("cat <file.txt");
		// TODO assertions
	}


}
