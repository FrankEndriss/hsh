package com.happypeople.hsh.hsh;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Test;

import com.happypeople.hsh.hsh.NodeTraversal.TraverseListener;
import com.happypeople.hsh.hsh.NodeTraversal.TraverseListenerResult;
import com.happypeople.hsh.hsh.l1parser.DollarSubstNode;
import com.happypeople.hsh.hsh.l1parser.DollarVarNode;
import com.happypeople.hsh.hsh.l1parser.L1Node;
import com.happypeople.hsh.hsh.parser.CompleteCommand;

public class HshParserTest_Dollar extends HshParserTest {

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
	public void testComplete_dollar_only1() throws Exception {
		context.getEnv().setVariableValue("x", "3");
		final CompleteCommand cc=parseTo_CompleteCommand("$x");
		final DollarVarNode dvn=findFirstNodeOfClass(cc, DollarVarNode.class);
		assertEquals("substitution", "3", NodeTraversal.substituteSubtree(dvn, context));
	}


	@Test
	public void testComplete_dollar3() throws ParseException, Exception {
		context.getEnv().setVariableValue("x", "3");
		context.getEnv().setVariableValue("y", "x");
		final CompleteCommand cc=parseTo_CompleteCommand("${x:-${y}}");
		final DollarSubstNode dsn=findFirstDollarSubstNode(cc);
		assertNotNull("# parameter", dsn.getParameter());
		assertNotNull("operator", dsn.getOperator());
		assertNotNull("no word", dsn.getWord());
		assertEquals("substitution", "3", getSubstitutedString(dsn, context));
	}

	@Test
	public void testComplete_dollar2() throws Exception {
		final CompleteCommand cc=parseTo_CompleteCommand("${x:-hallo}");
		final DollarSubstNode dsn=findFirstDollarSubstNode(cc);
		assertNotNull("# parameter", dsn.getParameter());
		assertNotNull("# operator", dsn.getOperator());
		assertNotNull("# word", dsn.getWord());
	}

	@Test
	public void testComplete_dollar1() throws Exception {
		context.getEnv().setVariableValue("x", "3 2");
		context.getEnv().setVariableValue("IFS", " \t\n");
		final CompleteCommand cc=parseTo_CompleteCommand("${x}");
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

}
