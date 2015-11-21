package com.happypeople.hsh.hsh;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.happypeople.hsh.hsh.parser.CompleteCommand;
import com.happypeople.hsh.hsh.parser.SimpleCommand;

public class HshParserTest_SimpleCommands extends HshParserTest {
	@Test
	public void testComplete_command7() throws Exception {
		final CompleteCommand cc=parseTo_CompleteCommand("echo \"a`echo x` b c\"");
		final SimpleCommand sc=findSimpleCommand(cc);
		assertEquals("# args", 2, sc.getArgs().size());
		assertEquals("cmd", "echo", node2String(sc.getArgs().get(0)));
		assertEquals("# assignments", 0, sc.getAssignments().size());
	}

	@Test
	public void testComplete_command6() throws Exception {
		final CompleteCommand cc=parseTo_CompleteCommand("echo \"a\\\\ b c\"");
		final SimpleCommand sc=findSimpleCommand(cc);
		assertEquals("# args", 2, sc.getArgs().size());
		assertEquals("cmd", "echo", node2String(sc.getArgs().get(0)));
		assertEquals("# assignments", 0, sc.getAssignments().size());
	}

	@Test
	public void testComplete_command5() throws Exception {
		final CompleteCommand cc=parseTo_CompleteCommand("echo \"a \\b c\"");
		final SimpleCommand sc=findSimpleCommand(cc);
		assertEquals("# args", 2, sc.getArgs().size());
		assertEquals("cmd", "echo", node2String(sc.getArgs().get(0)));
		assertEquals("# assignments", 0, sc.getAssignments().size());
	}

	@Test
	public void testComplete_command4() throws Exception {
		final CompleteCommand cc=parseTo_CompleteCommand("echo \"a b c\"");
		final SimpleCommand sc=findSimpleCommand(cc);
		assertEquals("# args", 2, sc.getArgs().size());
		assertEquals("cmd", "echo", node2String(sc.getArgs().get(0)));
		assertEquals("# assignments", 0, sc.getAssignments().size());
	}

	@Test
	public void testComplete_command3() throws Exception {
		final CompleteCommand cc=parseTo_CompleteCommand("echo 'a b c'");
		final SimpleCommand sc=findSimpleCommand(cc);
		assertEquals("# args", 2, sc.getArgs().size());
		assertEquals("cmd", "echo", node2String(sc.getArgs().get(0)));
		assertEquals("# assignments", 0, sc.getAssignments().size());
	}

	@Test
	public void testComplete_command2() throws Exception {
		final CompleteCommand cc=parseTo_CompleteCommand("echo a b c");
		final SimpleCommand sc=findSimpleCommand(cc);
		assertEquals("# args", 4, sc.getArgs().size());
		assertEquals("cmd", "echo", node2String(sc.getArgs().get(0)));
		assertEquals("# assignments", 0, sc.getAssignments().size());
	}

	@Test
	public void testComplete_command1() throws Exception {
		final CompleteCommand cc=parseTo_CompleteCommand("x");
		final SimpleCommand sc=findSimpleCommand(cc);
		assertEquals("# args", 1, sc.getArgs().size());
		assertEquals("cmd", "x", node2String(sc.getArgs().get(0)));
		assertEquals("# assignments", 0, sc.getAssignments().size());
	}

}
