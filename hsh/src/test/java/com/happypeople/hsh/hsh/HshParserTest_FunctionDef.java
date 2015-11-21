package com.happypeople.hsh.hsh;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.happypeople.hsh.hsh.parser.CompleteCommand;
import com.happypeople.hsh.hsh.parser.FunctionDefinition;

public class HshParserTest_FunctionDef extends HshParserTest {

	@Test
	public void test_functionDeclaration_brace_group() throws Exception {
		final CompleteCommand cc=parseTo_CompleteCommand("myEcho() { echo hello ; }");
		assertNotNull("command must not be null", cc);
		final FunctionDefinition fdef=findFirstNodeOfClass(cc, FunctionDefinition.class);
		assertNotNull("fdef must not be null", fdef);
		fdef.dump(dumpTarget);

	}

	@Test
	public void test_functionDeclaration_subshell() throws Exception {
		final CompleteCommand cc=parseTo_CompleteCommand("myEcho() ( echo hello ; )");
		assertNotNull("command must not be null", cc);
		final FunctionDefinition fdef=findFirstNodeOfClass(cc, FunctionDefinition.class);
		assertNotNull("fdef must not be null", fdef);
		fdef.dump(dumpTarget);
	}
}