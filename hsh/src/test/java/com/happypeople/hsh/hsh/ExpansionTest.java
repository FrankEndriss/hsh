package com.happypeople.hsh.hsh;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.PipedReader;
import java.io.PipedWriter;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import com.happypeople.hsh.HshContext;
import com.happypeople.hsh.HshRedirections;
import com.happypeople.hsh.hsh.l1parser.L1Parser;
import com.happypeople.hsh.hsh.l1parser.L2TokenManager;
import com.happypeople.hsh.hsh.parser.CompleteCommand;

public class ExpansionTest {

	private HshParser parser;
	private PipedWriter toParser;
	private HshContext context;
	private ArrayList<String[]> executedList;

	@Before
	public void setUp() throws Exception {
		toParser=new PipedWriter();
		final PipedReader parserIn=new PipedReader();
		toParser.connect(parserIn);
		final L2TokenManager tokMgr=new L2TokenManager(new L1Parser(parserIn));
		parser=new HshParser(tokMgr);
		executedList=new ArrayList<String[]>();
		context=new HshChildContext(null).createChildContext(null, new HshExecutorImpl(null, null, new HshRedirectionsImpl(null, null, null)) {
			@Override
			public int execute(final String[] command, final HshRedirections redir) throws Exception {
				executedList.add(command);
				return 0;
			}
		});
		context.getEnv().setVariableValue("IFS", " \t\n");
		context.getEnv().getParameter("IFS").setExport(true);
	}

	private void runTest(final String input) throws Exception {
		runTest(input, 1);
	}

	private void runTest(final String input, final int count) throws Exception {
		toParser.write(input);
		toParser.close();
		final CompleteCommand cc=parser.complete_command();
		NodeTraversal.executeSubtree(cc, context);
	}

	@Test
	public void test_dollarExec1() throws Exception {
		runTest("$(echo myCmd)");
		assertEquals("# execs", 1, executedList.size());
		assertEquals("# cmdline", 2, executedList.get(0).length);
		assertEquals("# cmdline", "echo", executedList.get(0)[0]);
		assertEquals("# cmdline", "myCmd", executedList.get(0)[1]);
	}

	@Test
	public void test_pathExpand() throws Exception {
		runTest("ls *");
		assertEquals("# execs", 1, executedList.size());
		assertEquals("# cmdline", "ls", executedList.get(0)[0]);
		assertTrue("# * should be substituted", !"*".equals(executedList.get(0)[1]));
	}

	@Test
	public void test_simple2() throws Exception {
		runTest("echo hallo bla laber");
		assertEquals("# execs", 1, executedList.size());
		assertEquals("# cmdline", 4, executedList.get(0).length);
		assertEquals("# cmdline", "echo", executedList.get(0)[0]);
		assertEquals("# cmdline", "laber", executedList.get(0)[3]);
	}

	@Test
	public void test_simple1() throws Exception {
		runTest("echo hallo");
		assertEquals("# execs", 1, executedList.size());
		assertEquals("# cmdline", 2, executedList.get(0).length);
		assertEquals("# cmdline", "echo", executedList.get(0)[0]);
		assertEquals("# cmdline", "hallo", executedList.get(0)[1]);
	}

	@Test
	public void test_split3() throws Exception {
		runTest("x=\"\\\"x y\"\\\" echo $x hallo");
		assertEquals("# execs", 1, executedList.size());
		assertEquals("# cmdline", 4, executedList.get(0).length);
		assertEquals("# cmdline", "echo", executedList.get(0)[0]);
		assertEquals("# cmdline", "\"x", executedList.get(0)[1]);
		assertEquals("# cmdline", "y\"", executedList.get(0)[2]);
		assertEquals("# cmdline", "hallo", executedList.get(0)[3]);
	}

	@Test
	public void test_split2() throws Exception {
		runTest("x=\"x y\" echo $x hallo");
		assertEquals("# execs", 1, executedList.size());
		//assertEquals("# cmdline", 4, executedList.get(0).length);
		assertEquals("# cmdline", "echo", executedList.get(0)[0]);
		assertEquals("# cmdline", "x", executedList.get(0)[1]);
		assertEquals("# cmdline", "y", executedList.get(0)[2]);
		assertEquals("# cmdline", "hallo", executedList.get(0)[3]);
	}

	@Test
	public void test_split1() throws Exception {
		runTest("echo \"x y\" hallo");
		assertEquals("# execs", 1, executedList.size());
		assertEquals("# cmdline", 3, executedList.get(0).length);
		assertEquals("# cmdline", "echo", executedList.get(0)[0]);
		assertEquals("# cmdline", "x y", executedList.get(0)[1]);
		assertEquals("# cmdline", "hallo", executedList.get(0)[2]);
	}

	@Test
	public void test_assignment2() throws Exception {
		runTest("x=5 echo $x hallo");
		assertEquals("# execs", 1, executedList.size());
		assertEquals("# cmdline", 3, executedList.get(0).length);
		assertEquals("# cmdline", "echo", executedList.get(0)[0]);
		assertEquals("# cmdline", "5", executedList.get(0)[1]);
		assertEquals("# cmdline", "hallo", executedList.get(0)[2]);
	}

	@Test
	public void test_assignment1() throws Exception {
		runTest("x=5 echo hallo");
		assertEquals("# execs", 1, executedList.size());
		assertEquals("# cmdline", 2, executedList.get(0).length);
		assertEquals("# cmdline", "echo", executedList.get(0)[0]);
		assertEquals("# cmdline", "hallo", executedList.get(0)[1]);
	}

}
