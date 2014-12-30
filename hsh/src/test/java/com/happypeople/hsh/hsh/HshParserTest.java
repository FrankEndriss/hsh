package com.happypeople.hsh.hsh;

import java.io.StringReader;

import org.junit.Test;

import static org.junit.Assert.*;

import com.happypeople.hsh.hsh.NodeTraversal.TraverseListener;
import com.happypeople.hsh.hsh.l1parser.L1Node;
import com.happypeople.hsh.hsh.l1parser.L1Parser;
import com.happypeople.hsh.hsh.l1parser.L1ParserTokenManager;
import com.happypeople.hsh.hsh.l1parser.L2TokenManager;
import com.happypeople.hsh.hsh.l1parser.SimpleCharStream;
import com.happypeople.hsh.hsh.parser.CompleteCommand;
import com.happypeople.hsh.hsh.parser.SimpleCommand;

public class HshParserTest {

	private final static boolean DEBUG=false;

	public HshParser setup(final String input) {
		final L1ParserTokenManager tokenMgr=new L1ParserTokenManager(new SimpleCharStream(new StringReader(input)));
		final L1Parser parser=new L1Parser(tokenMgr);
		return new HshParser(new L2TokenManager(parser));
	}

	@Test
	public void testComplete_command_Assignment5() throws ParseException {
		CompleteCommand cc=doTestCompleteCommand("x=1 y=2 echo bla y=2 laber");
		SimpleCommand sc=findSimpleCommand(cc);
		assertEquals("# assignments", 2, sc.getAssignments().size());
		assertEquals("cmd", "echo", sc.getCmdName().getString());
		assertEquals("# args", 3, sc.getArgs().size());
	}

	@Test
	public void testComplete_command_Assignment4() throws ParseException {
		CompleteCommand cc=doTestCompleteCommand("x=1 y=2 echo bla laber");
		SimpleCommand sc=findSimpleCommand(cc);
		assertEquals("# assignments", 2, sc.getAssignments().size());
		assertEquals("cmd", "echo", sc.getCmdName().getString());
		assertEquals("# args", 2, sc.getArgs().size());
	}

	@Test
	public void testComplete_command_Assignment3() throws ParseException {
		CompleteCommand cc=doTestCompleteCommand("x=1 echo x");
		SimpleCommand sc=findSimpleCommand(cc);
		assertEquals("# assignments", 1, sc.getAssignments().size());
		assertEquals("cmd", "echo", sc.getCmdName().getString());
		assertEquals("# args", 1, sc.getArgs().size());
	}

	@Test
	public void testComplete_command_Assignment2() throws ParseException {
		CompleteCommand cc=doTestCompleteCommand("x=1 y=2");
		SimpleCommand sc=findSimpleCommand(cc);
		assertEquals("# assignments", 2, sc.getAssignments().size());
		assertNull("cmd", sc.getCmdName());
	}

	@Test
	public void testComplete_command_Assignment1() throws ParseException {
		CompleteCommand cc=doTestCompleteCommand("x=1");
		SimpleCommand sc=findSimpleCommand(cc);
		assertEquals("# assignments", 1, sc.getAssignments().size());
		assertNull("cmd", sc.getCmdName());
	}

	@Test
	public void testComplete_command7() throws ParseException {
		CompleteCommand cc=doTestCompleteCommand("echo \"a`echo x` b c\"");
		SimpleCommand sc=findSimpleCommand(cc);
		assertEquals("cmd", "echo", sc.getCmdName().getString());
		assertEquals("# assignments", 0, sc.getAssignments().size());
		assertEquals("# args", 1, sc.getArgs().size());
	}

	@Test
	public void testComplete_command6() throws ParseException {
		CompleteCommand cc=doTestCompleteCommand("echo \"a\\\\ b c\"");
		SimpleCommand sc=findSimpleCommand(cc);
		assertEquals("cmd", "echo", sc.getCmdName().getString());
		assertEquals("# assignments", 0, sc.getAssignments().size());
		assertEquals("# args", 1, sc.getArgs().size());
	}

	@Test
	public void testComplete_command5() throws ParseException {
		CompleteCommand cc=doTestCompleteCommand("echo \"a \\b c\"");
		SimpleCommand sc=findSimpleCommand(cc);
		assertEquals("cmd", "echo", sc.getCmdName().getString());
		assertEquals("# assignments", 0, sc.getAssignments().size());
		assertEquals("# args", 1, sc.getArgs().size());
	}

	@Test
	public void testComplete_command4() throws ParseException {
		CompleteCommand cc=doTestCompleteCommand("echo \"a b c\"");
		SimpleCommand sc=findSimpleCommand(cc);
		assertEquals("cmd", "echo", sc.getCmdName().getString());
		assertEquals("# assignments", 0, sc.getAssignments().size());
		assertEquals("# args", 1, sc.getArgs().size());
	}

	@Test
	public void testComplete_command3() throws ParseException {
		CompleteCommand cc=doTestCompleteCommand("echo 'a b c'");
		SimpleCommand sc=findSimpleCommand(cc);
		assertEquals("cmd", "echo", sc.getCmdName().getString());
		assertEquals("# assignments", 0, sc.getAssignments().size());
		assertEquals("# args", 1, sc.getArgs().size());
	}

	@Test
	public void testComplete_command2() throws ParseException {
		CompleteCommand cc=doTestCompleteCommand("echo a b c");
		SimpleCommand sc=findSimpleCommand(cc);
		assertEquals("cmd", "echo", sc.getCmdName().getString());
		assertEquals("# assignments", 0, sc.getAssignments().size());
		assertEquals("# args", 3, sc.getArgs().size());
	}

	@Test
	public void testComplete_command1() throws ParseException {
		CompleteCommand cc=doTestCompleteCommand("x");
		classTreeTraversal(cc);
		SimpleCommand sc=findSimpleCommand(cc);
		assertEquals("cmd", "x", sc.getCmdName().getString());
		assertEquals("# assignments", 0, sc.getAssignments().size());
		assertEquals("# args", 0, sc.getArgs().size());
	}

	private CompleteCommand doTestCompleteCommand(final String input) throws ParseException {
		final HshParser p=setup(input);
		final CompleteCommand cc=p.complete_command();
		if(DEBUG) {
			System.out.println("Test input: "+input);
			cc.dump(0);
		}
		return cc;
	}

	private void classTreeTraversal(L1Node root) {
		NodeTraversal.traverse(root, new TraverseListener() {
			@Override
			public void node(L1Node node, int level) {
				StringBuilder sb=new StringBuilder();
				for(int i=0; i<level; i++)
					sb.append("\t");
				System.out.println(sb.toString()+node.getClass().getName());
			}
		});
	}

	private SimpleCommand findSimpleCommand(CompleteCommand cc) {
		final int[] c={ 0 };
		final SimpleCommand[] sc=new SimpleCommand[1];
		NodeTraversal.traverse(cc, new NodeTraversal.TraverseListener() {
			@Override
			public void node(L1Node node, int level) {
				if(node instanceof SimpleCommand) {
					c[0]++;
					if(c[0]>1)
						fail("found second SimpleCommand");
					sc[0]=(SimpleCommand)node;
				}
			}
		});
		
		if(sc[0]==null)
			fail("didnt found SimpleCommand");
		
		return sc[0];
	}

}
