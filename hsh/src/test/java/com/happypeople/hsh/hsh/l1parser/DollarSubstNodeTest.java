package com.happypeople.hsh.hsh.l1parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import com.happypeople.hsh.HshContext;
import com.happypeople.hsh.hsh.HshChildContext;

public class DollarSubstNodeTest {

	private DollarSubstNode out;
	private HshContext context;
	private final L1Node variableSet=new SimpleL1Node("x");
	private final String valueOfVariableSet="hallo";
	private final L1Node variableUnset=new SimpleL1Node("y");
	private final L1Node variableSetButNull=new SimpleL1Node("z");
	private final String valueOfSimpleWord="word";
	private final L1Node simpleWord=new SimpleL1Node(valueOfSimpleWord);

	@Before
	public void setup() {
		context=new HshChildContext(null, null, null);
		out=new DollarSubstNode();
		context.getEnv().setVariableValue("x", valueOfVariableSet);
		context.getEnv().setVariableValue("z", null);
	}

	@Test
	public void testOperators() throws Exception {
		final L1Node expVar=new SimpleL1Node(valueOfVariableSet);
		final L1Node expWord=new SimpleL1Node(valueOfSimpleWord);
		final L1Node expNull=new SimpleL1Node(null);

		final L1Node[][] testCases={
			new L1Node[] { variableSet,			new SimpleL1Node(":-"), expVar },
			new L1Node[] { variableUnset,		new SimpleL1Node(":-"), expWord },
			new L1Node[] { variableSetButNull,	new SimpleL1Node(":-"), expWord },
			new L1Node[] { variableSet,			new SimpleL1Node("-"), expVar },
			new L1Node[] { variableUnset,		new SimpleL1Node("-"), expWord },
			new L1Node[] { variableSetButNull,	new SimpleL1Node("-"), expNull },

			new L1Node[] { variableSet,			new SimpleL1Node(":+"), expWord },
			new L1Node[] { variableUnset,		new SimpleL1Node(":+"), expNull },
			new L1Node[] { variableSetButNull,	new SimpleL1Node(":+"), expNull },
			new L1Node[] { variableSet,			new SimpleL1Node("+"), expWord },
			new L1Node[] { variableUnset,		new SimpleL1Node("+"), expNull },
			new L1Node[] { variableSetButNull,	new SimpleL1Node("+"), expWord },

			new L1Node[] { variableSet,			new SimpleL1Node(":="),	expVar, expVar },
			new L1Node[] { variableUnset,		new SimpleL1Node(":="),	expWord, expWord },
			new L1Node[] { variableSetButNull,	new SimpleL1Node(":="),	expWord, expWord },
			new L1Node[] { variableSet,			new SimpleL1Node("="),	expVar, expVar },
			new L1Node[] { variableUnset,		new SimpleL1Node("="),	expWord, expWord },
			new L1Node[] { variableSetButNull,	new SimpleL1Node("="),	expNull, expNull }
		};
		for(final L1Node[] testData : testCases)
			doOperatorTest(testData);

		final L1Node[][] testCasesErrorCond={
			new L1Node[] { variableSet,			new SimpleL1Node(":?"), null },
			new L1Node[] { variableUnset,		new SimpleL1Node(":?"), null },
			new L1Node[] { variableSetButNull,	new SimpleL1Node(":?"), expVar },
			new L1Node[] { variableSet,			new SimpleL1Node("?"), expVar },
			new L1Node[] { variableUnset,		new SimpleL1Node("?"), expWord },
			new L1Node[] { variableSetButNull,	new SimpleL1Node("?"), expNull },
		};
		for(final L1Node[] testData : testCases)
			doOperatorErrorTest(testData);
	}

	private void doOperatorErrorTest(final L1Node[] testData) throws Exception {
		out.setParameter(testData[0]);
		out.setOperator(testData[1]);
		out.setWord(simpleWord);
		if(testData[2]==null) {	// indicates error should be thrown
			try {
				out.getSubstitutedString(context);
				fail("sould have thrown HshExit");
			}catch(final HshExit hshEx) {
				// ok
				return;
			}
		}

		final String exp=((SimpleL1Node)testData[2]).getImage();
		final String result=out.getSubstitutedString(context);

		String dbg1=null;
		if(testData[0]==variableSet)
			dbg1="set";
		else if(testData[0]==variableUnset)
			dbg1="unset";
		else if(testData[0]==variableSetButNull)
			dbg1="setButNull";

		assertEquals(dbg1+" "+testData[1], exp, result);
		setup();
	}

	private void doOperatorTest(final L1Node[] testData) throws Exception {
		out.setParameter(testData[0]);
		out.setOperator(testData[1]);
		out.setWord(simpleWord);
		final String exp=((SimpleL1Node)testData[2]).getImage();
		final String result=out.getSubstitutedString(context);

		String dbg1=null;
		if(testData[0]==variableSet)
			dbg1="set";
		else if(testData[0]==variableUnset)
			dbg1="unset";
		else if(testData[0]==variableSetButNull)
			dbg1="setButNull";

		assertEquals(dbg1+" "+testData[1], exp, result);

		if(testData.length>3 && testData[3]!=null) { // assignment test
			// the variable given in testData[0] should now have the value given in testData[3]
			final String varname=((SimpleL1Node)testData[0]).getImage();
			final String valueOfVar=context.getEnv().getVariableValue(varname);
			final String expOfAss=((SimpleL1Node)testData[3]).getImage();
			assertEquals("ass "+dbg1+" "+testData[1], expOfAss, valueOfVar);
		}
		setup();
	}

	@Test
	public void testGetSubstitutedString4() throws Exception {
		out.setParameter(variableUnset);
		assertNull("simple subst unset", out.getSubstitutedString(context));
	}

	@Test
	public void testGetSubstitutedString3() throws Exception {
		out.setParameter(variableUnset);
		assertNull("simple subst unset", out.getSubstitutedString(context));
	}

	@Test
	public void testGetSubstitutedString2() throws Exception {
		out.setParameter(variableSetButNull);
		assertNull("simple subst null", out.getSubstitutedString(context));
	}

	@Test
	public void testGetSubstitutedString1() throws Exception {
		out.setParameter(variableSet);
		assertEquals("simple subst", valueOfVariableSet, out.getSubstitutedString(context));
	}
}
