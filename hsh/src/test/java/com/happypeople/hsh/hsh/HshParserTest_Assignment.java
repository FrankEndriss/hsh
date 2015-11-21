package com.happypeople.hsh.hsh;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.happypeople.hsh.hsh.parser.CompleteCommand;
import com.happypeople.hsh.hsh.parser.SimpleCommand;

public class HshParserTest_Assignment extends HshParserTest {
	@Test
	public void testComplete_command_Assignment8() throws Exception {
		final CompleteCommand cc=parseTo_CompleteCommand("echo <input.txt");
		final SimpleCommand sc=findSimpleCommand(cc);

		assertEquals("# assignments", 0, sc.getAssignments().size());
		assertEquals("# args", 1, sc.getArgs().size());
		assertEquals("cmd", "echo", node2String(sc.getArgs().get(0)));
		assertEquals("# redir", 1, sc.getRedirects().size());
	}

	@Test
	public void testComplete_command_Assignment7() throws Exception {
		final CompleteCommand[] res=parseTo_CompleteCommandList(
				"x=1 y=2 2>file.txt echo bla y=2<input.txt <input.txt laber\nx=1 y=2 echo2 bla y=2 laber <input.txt", 2);
		final SimpleCommand sc=findSimpleCommand(res[0]);

		assertEquals("# assignments", 2, sc.getAssignments().size());
		assertEquals("# args", 4, sc.getArgs().size());
		assertEquals("cmd", "echo", node2String(sc.getArgs().get(0)));
		assertEquals("# redir", 2, sc.getRedirects().size());

		final SimpleCommand sc2=findSimpleCommand(res[1]);
		assertEquals("2.# assignments", 2, sc2.getAssignments().size());
		assertEquals("2.# args", 4, sc2.getArgs().size());
		assertEquals("2.cmd", "echo2", node2String(sc2.getArgs().get(0)));
		assertEquals("# redir", 1, sc2.getRedirects().size());
	}


	@Test
	public void testComplete_command_Assignment6() throws Exception {
		final CompleteCommand[] cc=parseTo_CompleteCommandList("x=1 y=2 echo bla y=2 laber\nx=1 y=2 echo2 bla y=2 laber", 2);
		final SimpleCommand sc1=findSimpleCommand(cc[0]);

		assertEquals("# assignments", 2, sc1.getAssignments().size());
		assertEquals("# args", 4, sc1.getArgs().size());
		assertEquals("cmd", "echo", node2String(sc1.getArgs().get(0)));

		final SimpleCommand sc2=findSimpleCommand(cc[1]);
		assertEquals("2.# assignments", 2, sc2.getAssignments().size());
		assertEquals("2.# args", 4, sc2.getArgs().size());
		assertEquals("2.cmd", "echo2", node2String(sc2.getArgs().get(0)));
	}

	@Test
	public void testComplete_command_Assignment5() throws Exception {
		final CompleteCommand cc=parseTo_CompleteCommand("x=1 y=2 echo bla y=2 laber");
		final SimpleCommand sc=findSimpleCommand(cc);
		assertEquals("# assignments", 2, sc.getAssignments().size());
		assertEquals("# args", 4, sc.getArgs().size());
		assertEquals("cmd", "echo", node2String(sc.getArgs().get(0)));
	}

	@Test
	public void testComplete_command_Assignment4() throws Exception {
		final CompleteCommand cc=parseTo_CompleteCommand("x=1 y=2 echo bla laber");
		final SimpleCommand sc=findSimpleCommand(cc);
		assertEquals("# assignments", 2, sc.getAssignments().size());
		assertEquals("# args", 3, sc.getArgs().size());
		assertEquals("cmd", "echo", node2String(sc.getArgs().get(0)));
	}

	@Test
	public void testComplete_command_Assignment3() throws Exception {
		final CompleteCommand cc=parseTo_CompleteCommand("x=1 echo x");
		final SimpleCommand sc=findSimpleCommand(cc);
		assertEquals("# assignments", 1, sc.getAssignments().size());
		assertEquals("# args", 2, sc.getArgs().size());
		assertEquals("cmd", "echo", node2String(sc.getArgs().get(0)));
	}

	@Test
	public void testComplete_command_Assignment2() throws Exception {
		final CompleteCommand cc=parseTo_CompleteCommand("x=1 y=2");
		final SimpleCommand sc=findSimpleCommand(cc);
		assertEquals("# assignments", 2, sc.getAssignments().size());
		assertEquals("# args", 0, sc.getArgs().size());
	}

	@Test
	public void testComplete_command_Assignment1() throws Exception {
		final CompleteCommand cc=parseTo_CompleteCommand("x=1");
		final SimpleCommand sc=findSimpleCommand(cc);
		assertEquals("# assignments", 1, sc.getAssignments().size());
		assertEquals("# args", 0, sc.getArgs().size());
	}

}
