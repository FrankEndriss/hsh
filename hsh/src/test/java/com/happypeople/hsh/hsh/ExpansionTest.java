package com.happypeople.hsh.hsh;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.happypeople.hsh.HshContext;
import com.happypeople.hsh.HshFDSet;
import com.happypeople.hsh.HshRedirection;
import com.happypeople.hsh.hsh.l1parser.L1Parser;
import com.happypeople.hsh.hsh.l1parser.L2TokenManager;
import com.happypeople.hsh.hsh.parser.CompleteCommand;

public class ExpansionTest {
	private final static boolean DEBUG=false;

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
		final HshFDSetImpl fdSet=new HshFDSetImpl(null);
		fdSet.setOutput(HshFDSet.STDOUT, new HshPipeImpl());
		context=new HshContextBuilder().executor(new HshExecutorImpl() {
			@Override
			public int execute(final String[] command, final HshContext context, final List<HshRedirection> redirs) throws Exception {
				if(DEBUG)
					System.out.println("mocked executor, command: "+Arrays.asList(command));
				executedList.add(command);
				if(command.length>0 && "echo".equals(command[0])) {
						final PrintStream ps=context.getStdOut();
						for(int i=1; i<command.length; i++)
							ps.print((i>1?" ":"")+command[i]);
						ps.flush();
				}
				return 0;
			}
		}).environment(new HshEnvironmentImpl(null)).fdSet(fdSet).create();
		context.getEnv().setVariableValue("IFS", " \t\n");
		context.getEnv().getParameter("IFS").setExport(true);
	}

	private void runTest(final String input) throws Exception {
		runTest(input, 1);
	}

	private void runTest(final String input, final int count) throws Exception {
		toParser.write(input);
		toParser.close();
		for(int i=0; i<count; i++) {
			final CompleteCommand cc=parser.complete_command();
			NodeTraversal.executeSubtree(cc, context);
		}
	}

	@Test
	public void test_dollarExec1() throws Exception {
		runTest("$(echo myCmd)");
		// should execute "echo myCmd" while substitution, and then "myCmd" as command
		assertEquals("# execs", 2, executedList.size());
		assertEquals("# cmdline", 2, executedList.get(0).length);
		assertEquals("# cmdline", "echo", executedList.get(0)[0]);
		assertEquals("# cmdline", "myCmd", executedList.get(0)[1]);
		assertEquals("# cmdline", 1, executedList.get(1).length);
		assertEquals("# cmdline", "myCmd", executedList.get(1)[0]);
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
	public void test_split4() throws Exception {
		runTest("echo 'x y' hallo");
		assertEquals("# execs", 1, executedList.size());
		if(DEBUG)
			System.out.println("executed[0]: "+Arrays.asList(executedList.get(0)));
		assertEquals("# cmdline", 3, executedList.get(0).length);
		assertEquals("# cmdline", "echo", executedList.get(0)[0]);
		assertEquals("# cmdline", "x y", executedList.get(0)[1]);
		assertEquals("# cmdline", "hallo", executedList.get(0)[2]);
	}

	@Test
	public void test_split3() throws Exception {
		runTest("x=\"\\\"x y\"\\\" ; echo $x hallo");
		assertEquals("# execs", 1, executedList.size());
		assertEquals("# cmdline", 4, executedList.get(0).length);
		assertEquals("# cmdline", "echo", executedList.get(0)[0]);
		assertEquals("# cmdline", "\"x", executedList.get(0)[1]);
		assertEquals("# cmdline", "y\"", executedList.get(0)[2]);
		assertEquals("# cmdline", "hallo", executedList.get(0)[3]);
	}

	@Test
	public void test_split2() throws Exception {
		runTest("x=\"x y\" ; echo $x hallo");
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
		if(DEBUG)
			System.out.println("executed[0]: "+Arrays.asList(executedList.get(0)));
		assertEquals("# cmdline", 3, executedList.get(0).length);
		assertEquals("# cmdline", "echo", executedList.get(0)[0]);
		assertEquals("# cmdline", "x y", executedList.get(0)[1]);
		assertEquals("# cmdline", "hallo", executedList.get(0)[2]);
	}

	@Test
	public void test_assignment2() throws Exception {
		runTest("x=foo ; x=bla echo $x hallo");
		if(DEBUG)
			System.out.println("executed[0]: "+Arrays.asList(executedList.get(0)));
		assertEquals("# execs", 1, executedList.size());
		assertEquals("# cmdline", 3, executedList.get(0).length);
		assertEquals("# cmdline", "echo", executedList.get(0)[0]);
		assertEquals("# cmdline", "foo", executedList.get(0)[1]);
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
