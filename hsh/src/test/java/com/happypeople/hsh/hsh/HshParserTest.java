package com.happypeople.hsh.hsh;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.StringReader;

import org.junit.Test;

import com.happypeople.hsh.hsh.NodeTraversal.TraverseListener;
import com.happypeople.hsh.hsh.NodeTraversal.TraverseListenerResult;
import com.happypeople.hsh.hsh.l1parser.DollarSubstNode;
import com.happypeople.hsh.hsh.l1parser.L1Node;
import com.happypeople.hsh.hsh.l1parser.L1Parser;
import com.happypeople.hsh.hsh.l1parser.L2TokenManager;
import com.happypeople.hsh.hsh.parser.CompleteCommand;
import com.happypeople.hsh.hsh.parser.SimpleCommand;

public class HshParserTest {

	private final static boolean DEBUG=true;

	public HshParser setup(final String input) {
		return new HshParser(new L2TokenManager(new L1Parser(new StringReader(input))));
	}

	@Test
	public void testComplete_dollar5() throws ParseException {
		final CompleteCommand cc=doTestCompleteCommand("${${y:-hallo}:?bla}");
		final DollarSubstNode dsn=findFirstDollarSubstNode(cc);
		assertNotNull("# parameter", dsn.getParameter());
		assertEquals("operator", ":?", dsn.getOperator().getString());
		assertEquals("word", "bla", dsn.getWord().getString());
	}

	@Test
	public void testComplete_dollar4() throws ParseException {
		final CompleteCommand cc=doTestCompleteCommand("${${y:-hallo}}");
		final DollarSubstNode dsn=findFirstDollarSubstNode(cc);
		assertNotNull("# parameter", dsn.getParameter());
		assertNull("no operator", dsn.getOperator());
		assertNull("no word", dsn.getWord());
	}

	@Test
	public void testComplete_dollar3() throws ParseException {
		final CompleteCommand cc=doTestCompleteCommand("${${y}}");
		final DollarSubstNode dsn=findFirstDollarSubstNode(cc);
		assertNotNull("# parameter", dsn.getParameter());
		assertNull("no operator", dsn.getOperator());
		assertNull("no word", dsn.getWord());
	}

	@Test
	public void testComplete_dollar2() throws ParseException {
		final CompleteCommand cc=doTestCompleteCommand("${x:-hallo}");
		final DollarSubstNode dsn=findFirstDollarSubstNode(cc);
		assertNotNull("# parameter", dsn.getParameter());
		assertNotNull("# operator", dsn.getOperator());
		assertNotNull("# word", dsn.getWord());
	}

	@Test
	public void testComplete_dollar1() throws ParseException {
		final CompleteCommand cc=doTestCompleteCommand("${x}");
		final DollarSubstNode dsn=findFirstDollarSubstNode(cc);
		assertNotNull("# parameter", dsn.getParameter());
		assertNull("no operator", dsn.getOperator());
		assertNull("no word", dsn.getWord());
	}

	private DollarSubstNode findFirstDollarSubstNode(final CompleteCommand cc) {
		final DollarSubstNode[] dsnode=new DollarSubstNode[1];

		NodeTraversal.traverse(cc, new TraverseListener() {
			@Override
			public TraverseListenerResult node(final L1Node node, final int level) {
				if(node instanceof DollarSubstNode) {
					dsnode[0]=(DollarSubstNode)node;
					return TraverseListenerResult.STOP;
				}
				return TraverseListenerResult.CONTINUE;
			}
		});
		return dsnode[0];
	}

	@Test
	public void testComplete_command_Assignment5() throws ParseException {
		final CompleteCommand cc=doTestCompleteCommand("x=1 y=2 echo bla y=2 laber");
		final SimpleCommand sc=findSimpleCommand(cc);
		assertEquals("# assignments", 2, sc.getAssignments().size());
		assertEquals("cmd", "echo", sc.getCmdName().getString());
		assertEquals("# args", 3, sc.getArgs().size());
	}

	@Test
	public void testComplete_command_Assignment4() throws ParseException {
		final CompleteCommand cc=doTestCompleteCommand("x=1 y=2 echo bla laber");
		final SimpleCommand sc=findSimpleCommand(cc);
		assertEquals("# assignments", 2, sc.getAssignments().size());
		assertEquals("cmd", "echo", sc.getCmdName().getString());
		assertEquals("# args", 2, sc.getArgs().size());
	}

	@Test
	public void testComplete_command_Assignment3() throws ParseException {
		final CompleteCommand cc=doTestCompleteCommand("x=1 echo x");
		final SimpleCommand sc=findSimpleCommand(cc);
		assertEquals("# assignments", 1, sc.getAssignments().size());
		assertEquals("cmd", "echo", sc.getCmdName().getString());
		assertEquals("# args", 1, sc.getArgs().size());
	}

	@Test
	public void testComplete_command_Assignment2() throws ParseException {
		final CompleteCommand cc=doTestCompleteCommand("x=1 y=2");
		final SimpleCommand sc=findSimpleCommand(cc);
		assertEquals("# assignments", 2, sc.getAssignments().size());
		assertNull("cmd", sc.getCmdName());
	}

	@Test
	public void testComplete_command_Assignment1() throws ParseException {
		final CompleteCommand cc=doTestCompleteCommand("x=1");
		final SimpleCommand sc=findSimpleCommand(cc);
		assertEquals("# assignments", 1, sc.getAssignments().size());
		assertNull("cmd", sc.getCmdName());
	}

	@Test
	public void testComplete_command7() throws ParseException {
		final CompleteCommand cc=doTestCompleteCommand("echo \"a`echo x` b c\"");
		final SimpleCommand sc=findSimpleCommand(cc);
		assertEquals("cmd", "echo", sc.getCmdName().getString());
		assertEquals("# assignments", 0, sc.getAssignments().size());
		assertEquals("# args", 1, sc.getArgs().size());
	}

	@Test
	public void testComplete_command6() throws ParseException {
		final CompleteCommand cc=doTestCompleteCommand("echo \"a\\\\ b c\"");
		final SimpleCommand sc=findSimpleCommand(cc);
		assertEquals("cmd", "echo", sc.getCmdName().getString());
		assertEquals("# assignments", 0, sc.getAssignments().size());
		assertEquals("# args", 1, sc.getArgs().size());
	}

	@Test
	public void testComplete_command5() throws ParseException {
		final CompleteCommand cc=doTestCompleteCommand("echo \"a \\b c\"");
		final SimpleCommand sc=findSimpleCommand(cc);
		assertEquals("cmd", "echo", sc.getCmdName().getString());
		assertEquals("# assignments", 0, sc.getAssignments().size());
		assertEquals("# args", 1, sc.getArgs().size());
	}

	@Test
	public void testComplete_command4() throws ParseException {
		final CompleteCommand cc=doTestCompleteCommand("echo \"a b c\"");
		final SimpleCommand sc=findSimpleCommand(cc);
		assertEquals("cmd", "echo", sc.getCmdName().getString());
		assertEquals("# assignments", 0, sc.getAssignments().size());
		assertEquals("# args", 1, sc.getArgs().size());
	}

	@Test
	public void testComplete_command3() throws ParseException {
		final CompleteCommand cc=doTestCompleteCommand("echo 'a b c'");
		final SimpleCommand sc=findSimpleCommand(cc);
		assertEquals("cmd", "echo", sc.getCmdName().getString());
		assertEquals("# assignments", 0, sc.getAssignments().size());
		assertEquals("# args", 1, sc.getArgs().size());
	}

	@Test
	public void testComplete_command2() throws ParseException {
		final CompleteCommand cc=doTestCompleteCommand("echo a b c");
		final SimpleCommand sc=findSimpleCommand(cc);
		assertEquals("cmd", "echo", sc.getCmdName().getString());
		assertEquals("# assignments", 0, sc.getAssignments().size());
		assertEquals("# args", 3, sc.getArgs().size());
	}

	@Test
	public void testComplete_command1() throws ParseException {
		final CompleteCommand cc=doTestCompleteCommand("x");
		classTreeTraversal(cc);
		final SimpleCommand sc=findSimpleCommand(cc);
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

	private void classTreeTraversal(final L1Node root) {
		NodeTraversal.traverse(root, new TraverseListener() {
			@Override
			public TraverseListenerResult node(final L1Node node, final int level) {
				final StringBuilder sb=new StringBuilder();
				for(int i=0; i<level; i++)
					sb.append("\t");
				System.out.println(sb.toString()+node.getClass().getName());
				return TraverseListenerResult.CONTINUE;
			}
		});
	}

	private SimpleCommand findSimpleCommand(final CompleteCommand cc) {
		final int[] c={ 0 };
		final SimpleCommand[] sc=new SimpleCommand[1];
		NodeTraversal.traverse(cc, new NodeTraversal.TraverseListener() {
			@Override
			public TraverseListenerResult node(final L1Node node, final int level) {
				if(node instanceof SimpleCommand) {
					c[0]++;
					if(c[0]>1)
						fail("found second SimpleCommand");
					sc[0]=(SimpleCommand)node;
				}
				return TraverseListenerResult.CONTINUE;
			}
		});

		if(sc[0]==null)
			fail("didnt found SimpleCommand");

		return sc[0];
	}

}
