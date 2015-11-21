package com.happypeople.hsh.hsh;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import com.happypeople.hsh.HshContext;
import com.happypeople.hsh.hsh.NodeTraversal.TraverseListener;
import com.happypeople.hsh.hsh.NodeTraversal.TraverseListenerResult;
import com.happypeople.hsh.hsh.l1parser.DollarSubstNode;
import com.happypeople.hsh.hsh.l1parser.DollarVarNode;
import com.happypeople.hsh.hsh.l1parser.ImageHolder;
import com.happypeople.hsh.hsh.l1parser.L1Node;
import com.happypeople.hsh.hsh.l1parser.L1Parser;
import com.happypeople.hsh.hsh.l1parser.L2TokenManager;
import com.happypeople.hsh.hsh.l1parser.SimpleDumpTarget;
import com.happypeople.hsh.hsh.l1parser.SimpleImageHolder;
import com.happypeople.hsh.hsh.l1parser.StringifiableNode;
import com.happypeople.hsh.hsh.parser.CompleteCommand;
import com.happypeople.hsh.hsh.parser.SimpleCommand;

public class HshParserTest {
	private final static Logger log = Logger.getLogger(HshParserTest.class);

	private HshContext context;
	private SimpleDumpTarget dumpTarget;

	@Before
	public void init_setup() {
		context=new HshContextBuilder().create();
		dumpTarget=new SimpleDumpTarget();
	}

	public HshParser setup(final String input) {
		final L2TokenManager tokMgr=new L2TokenManager(new L1Parser(new StringReader(input)));
		final HshParser parser=new HshParser(tokMgr);
		return parser;
	}

	@Test
	public void testIORedirect3() throws Exception {
		doTestCompleteCommand("<infile.txt >outfile.txt 2>errfile.txt cat");
	}

	@Test
	public void testIORedirect2() throws Exception {
		doTestCompleteCommand("<file.txt cat");
	}

	@Test
	public void testIORedirect1() throws Exception {
		doTestCompleteCommand("cat <file.txt");
	}

	@Test
	public void testWhile3() throws Exception {
		doTestCompleteCommand("while false; true; do sleep 5; done");
		doTestCompleteCommand("until false; true; do sleep 5; done");
	}

	@Test
	public void testWhile2() throws Exception {
		doTestCompleteCommand("while ! false; do sleep 5 ; done");
		doTestCompleteCommand("until ! false; do sleep 5 ; done");
	}

	@Test
	public void testWhile1() throws Exception {
		doTestCompleteCommand("while true; do sleep 5 ; done");
		doTestCompleteCommand("until true; do sleep 5 ; done");
	}


	@Test
	public void testComplete_dollar_only1() throws Exception {
		context.getEnv().setVariableValue("x", "3");
		final CompleteCommand cc=doTestCompleteCommand("$x");
		final DollarVarNode dvn=findFirstNodeOfClass(cc, DollarVarNode.class);
		assertEquals("substitution", "3", NodeTraversal.substituteSubtree(dvn, context));
	}

	/* this does not parse in bash, too
	@Test
	public void testComplete_dollar_only2() throws Exception {
		context.getEnv().setVariableValue("x", "3");
		context.getEnv().setVariableValue("y", "x");
		final CompleteCommand cc=doTestCompleteCommand("${$y}");
		//final DollarVarNode dvn=findFirstNodeOfClass(cc, DollarVarNode.class);
		final String s=NodeTraversal.substituteSubtree(cc, context);
		assertEquals("substitution", "3", s);
	}

	@Test
	public void testComplete_dollar5() throws ParseException {
		final CompleteCommand cc=doTestCompleteCommand("${${y:-hallo}:?bla}");
		final DollarSubstNode dsn=findFirstDollarSubstNode(cc);
		assertNotNull("# parameter", dsn.getParameter());
		assertEquals("operator", ":?", node2String(dsn.getOperator()));
		assertEquals("word", "bla", node2String(dsn.getWord()));
	}

	@Test
	public void testComplete_dollar4() throws ParseException {
		final CompleteCommand cc=doTestCompleteCommand("${${y:-hallo}}");
		final DollarSubstNode dsn=findFirstDollarSubstNode(cc);
		assertNotNull("# parameter", dsn.getParameter());
		assertNull("no operator", dsn.getOperator());
		assertNull("no word", dsn.getWord());
	}

	*/

	private static String getSubstitutedString(final DollarSubstNode node, final HshContext context) throws Exception {
		final ImageHolder imageHolder=new SimpleImageHolder();
		final L1Node resultNode=node.transformSubstitution(imageHolder, context);
		return resultNode.getImage();
	}

	@Test
	public void testComplete_dollar3() throws ParseException, Exception {
		context.getEnv().setVariableValue("x", "3");
		context.getEnv().setVariableValue("y", "x");
		final CompleteCommand cc=doTestCompleteCommand("${x:-${y}}");
		final DollarSubstNode dsn=findFirstDollarSubstNode(cc);
		assertNotNull("# parameter", dsn.getParameter());
		assertNotNull("operator", dsn.getOperator());
		assertNotNull("no word", dsn.getWord());
		assertEquals("substitution", "3", getSubstitutedString(dsn, context));
	}

	@Test
	public void testComplete_dollar2() throws Exception {
		final CompleteCommand cc=doTestCompleteCommand("${x:-hallo}");
		final DollarSubstNode dsn=findFirstDollarSubstNode(cc);
		assertNotNull("# parameter", dsn.getParameter());
		assertNotNull("# operator", dsn.getOperator());
		assertNotNull("# word", dsn.getWord());
	}

	@Test
	public void testComplete_dollar1() throws Exception {
		context.getEnv().setVariableValue("x", "3 2");
		context.getEnv().setVariableValue("IFS", " \t\n");
		final CompleteCommand cc=doTestCompleteCommand("${x}");
		final DollarSubstNode dsn=findFirstDollarSubstNode(cc);
		assertNotNull("# parameter", dsn.getParameter());
		assertNull("no operator", dsn.getOperator());
		assertNull("no word", dsn.getWord());
		final List<String> res=cc.doExpansion(context);
		while(res.remove(""));
		assertEquals("expansion len, res="+res, 2, res.size());
		assertEquals("substitution", "3", res.get(0));
		assertEquals("substitution", "2", res.get(1));
	}

	private static <T> T findFirstNodeOfClass(final CompleteCommand cc, final Class<T> class1) throws Exception {
		final List<T> listT=new ArrayList<T>();

		NodeTraversal.traverse(cc, new TraverseListener() {
			@Override
			public TraverseListenerResult node(final L1Node node, final int level) {
				if(class1.isAssignableFrom(node.getClass())) {
					listT.add((T)node);
					return TraverseListenerResult.STOP;
				}
				return TraverseListenerResult.CONTINUE;
			}
		});

		return listT.size()>0?listT.get(0):null;
	}

	private static DollarSubstNode findFirstDollarSubstNode(final CompleteCommand cc) throws Exception {
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
	public void testComplete_command_Assignment8() throws Exception {
		final CompleteCommand cc=doTestCompleteCommand("echo <input.txt");
		final SimpleCommand sc=findSimpleCommand(cc);

		assertEquals("# assignments", 0, sc.getAssignments().size());
		assertEquals("# args", 1, sc.getArgs().size());
		assertEquals("cmd", "echo", node2String(sc.getArgs().get(0)));
		assertEquals("# redir", 1, sc.getRedirects().size());
	}

	@Test
	public void testComplete_command_Assignment7() throws Exception {
		final CompleteCommand[] res=doTestCompleteCommand2(
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
		final CompleteCommand[] cc=doTestCompleteCommand2("x=1 y=2 echo bla y=2 laber\nx=1 y=2 echo2 bla y=2 laber", 2);
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
		final CompleteCommand cc=doTestCompleteCommand("x=1 y=2 echo bla y=2 laber");
		final SimpleCommand sc=findSimpleCommand(cc);
		assertEquals("# assignments", 2, sc.getAssignments().size());
		assertEquals("# args", 4, sc.getArgs().size());
		assertEquals("cmd", "echo", node2String(sc.getArgs().get(0)));
	}

	@Test
	public void testComplete_command_Assignment4() throws Exception {
		final CompleteCommand cc=doTestCompleteCommand("x=1 y=2 echo bla laber");
		final SimpleCommand sc=findSimpleCommand(cc);
		assertEquals("# assignments", 2, sc.getAssignments().size());
		assertEquals("# args", 3, sc.getArgs().size());
		assertEquals("cmd", "echo", node2String(sc.getArgs().get(0)));
	}

	@Test
	public void testComplete_command_Assignment3() throws Exception {
		final CompleteCommand cc=doTestCompleteCommand("x=1 echo x");
		final SimpleCommand sc=findSimpleCommand(cc);
		assertEquals("# assignments", 1, sc.getAssignments().size());
		assertEquals("# args", 2, sc.getArgs().size());
		assertEquals("cmd", "echo", node2String(sc.getArgs().get(0)));
	}

	@Test
	public void testComplete_command_Assignment2() throws Exception {
		final CompleteCommand cc=doTestCompleteCommand("x=1 y=2");
		final SimpleCommand sc=findSimpleCommand(cc);
		assertEquals("# assignments", 2, sc.getAssignments().size());
		assertEquals("# args", 0, sc.getArgs().size());
	}

	@Test
	public void testComplete_command_Assignment1() throws Exception {
		final CompleteCommand cc=doTestCompleteCommand("x=1");
		final SimpleCommand sc=findSimpleCommand(cc);
		assertEquals("# assignments", 1, sc.getAssignments().size());
		assertEquals("# args", 0, sc.getArgs().size());
	}

	@Test
	public void testComplete_command7() throws Exception {
		final CompleteCommand cc=doTestCompleteCommand("echo \"a`echo x` b c\"");
		final SimpleCommand sc=findSimpleCommand(cc);
		assertEquals("# args", 2, sc.getArgs().size());
		assertEquals("cmd", "echo", node2String(sc.getArgs().get(0)));
		assertEquals("# assignments", 0, sc.getAssignments().size());
	}

	@Test
	public void testComplete_command6() throws Exception {
		final CompleteCommand cc=doTestCompleteCommand("echo \"a\\\\ b c\"");
		final SimpleCommand sc=findSimpleCommand(cc);
		assertEquals("# args", 2, sc.getArgs().size());
		assertEquals("cmd", "echo", node2String(sc.getArgs().get(0)));
		assertEquals("# assignments", 0, sc.getAssignments().size());
	}

	@Test
	public void testComplete_command5() throws Exception {
		final CompleteCommand cc=doTestCompleteCommand("echo \"a \\b c\"");
		final SimpleCommand sc=findSimpleCommand(cc);
		assertEquals("# args", 2, sc.getArgs().size());
		assertEquals("cmd", "echo", node2String(sc.getArgs().get(0)));
		assertEquals("# assignments", 0, sc.getAssignments().size());
	}

	@Test
	public void testComplete_command4() throws Exception {
		final CompleteCommand cc=doTestCompleteCommand("echo \"a b c\"");
		final SimpleCommand sc=findSimpleCommand(cc);
		assertEquals("# args", 2, sc.getArgs().size());
		assertEquals("cmd", "echo", node2String(sc.getArgs().get(0)));
		assertEquals("# assignments", 0, sc.getAssignments().size());
	}

	@Test
	public void testComplete_command3() throws Exception {
		final CompleteCommand cc=doTestCompleteCommand("echo 'a b c'");
		final SimpleCommand sc=findSimpleCommand(cc);
		assertEquals("# args", 2, sc.getArgs().size());
		assertEquals("cmd", "echo", node2String(sc.getArgs().get(0)));
		assertEquals("# assignments", 0, sc.getAssignments().size());
	}

	@Test
	public void testComplete_command2() throws Exception {
		final CompleteCommand cc=doTestCompleteCommand("echo a b c");
		final SimpleCommand sc=findSimpleCommand(cc);
		assertEquals("# args", 4, sc.getArgs().size());
		assertEquals("cmd", "echo", node2String(sc.getArgs().get(0)));
		assertEquals("# assignments", 0, sc.getAssignments().size());
	}

	@Test
	public void testComplete_command1() throws Exception {
		final CompleteCommand cc=doTestCompleteCommand("x");
		final SimpleCommand sc=findSimpleCommand(cc);
		assertEquals("# args", 1, sc.getArgs().size());
		assertEquals("cmd", "x", node2String(sc.getArgs().get(0)));
		assertEquals("# assignments", 0, sc.getAssignments().size());
	}

	private CompleteCommand[] doTestCompleteCommand2(final String input, final int count) throws ParseException {
		final CompleteCommand[] res=new CompleteCommand[2];
		final HshParser p=setup(input);
		log.debug("Test input: "+input);
		for(int i=0; i<count; i++)
			res[i]=p.complete_command();
		for(int i=0; i<count; i++)
			if(res[i]!=null)
				res[i].dump(dumpTarget);
		dumpTarget.debug(log);
		return res;
	}

	private CompleteCommand doTestCompleteCommand(final String input) throws ParseException {
		final HshParser p=setup(input);
		log.debug("Test input: "+input);
		final CompleteCommand cc=p.complete_command();
		final SimpleDumpTarget target=new SimpleDumpTarget();
		cc.dump(target);
		System.out.print(target.toString());
		return cc;
	}

	private static SimpleCommand findSimpleCommand(final CompleteCommand cc) throws Exception {
		return findSimpleCommands(cc, 1)[0];
	}

	private static SimpleCommand[] findSimpleCommands(final CompleteCommand cc, final int count) throws Exception {
		final int[] c={ 0 };
		final SimpleCommand[] sc=new SimpleCommand[count];
		NodeTraversal.traverse(cc, new NodeTraversal.TraverseListener() {
			@Override
			public TraverseListenerResult node(final L1Node node, final int level) {
				if(node instanceof SimpleCommand) {
					c[0]++;
					if(c[0]>count)
						fail("found one more SimpleCommand than: "+count);
					sc[c[0]-1]=(SimpleCommand)node;
				}
				return TraverseListenerResult.CONTINUE;
			}
		});

		if(sc[0]==null)
			fail("didnt found SimpleCommand");

		if(c[0]<count)
			fail("found less SimpleCommands than: "+count);

		return sc;
	}

	private static String node2String(final L1Node node) throws Exception {
		final StringBuilder sb=new StringBuilder();
		NodeTraversal.traverse(node, new NodeTraversal.TraverseListener() {
			@Override
			public TraverseListenerResult node(final L1Node node, final int level) {
				if(node instanceof StringifiableNode)
					((StringifiableNode)node).append(sb);
				return TraverseListenerResult.CONTINUE;
			}
		});
		return sb.toString();
	}

	@Test
	public void test_functionDeclaration() throws ParseException {
		final CompleteCommand cc=doTestCompleteCommand("myEcho() { echo $1 }");
		assertNotNull("command must not be null", cc);
	}
}
