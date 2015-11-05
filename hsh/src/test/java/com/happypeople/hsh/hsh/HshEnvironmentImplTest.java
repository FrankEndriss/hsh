package com.happypeople.hsh.hsh;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class HshEnvironmentImplTest {

	HshEnvironmentImpl parentEnv;
	HshEnvironmentImpl childEnv;

	@Before
	public void setUp() throws Exception {
		parentEnv=new HshEnvironmentImpl(null);
		parentEnv.setVariableValue("var1", "value1");
		parentEnv.setVariableValue("var2", "value2");
		parentEnv.setVariableValue("var3", "value3");
		parentEnv.getParameter("var3").setExport(true);

		childEnv=new HshEnvironmentImpl(parentEnv);
		childEnv.setVariableValue("var4", "value4");
		childEnv.setVariableValue("var2", "value2child");
	}

	@Test
	public void testSimpleValue() {
		assertTrue("isset var1", parentEnv.issetParameter("var1"));
		assertTrue("isset var3", parentEnv.issetParameter("var3"));
		assertTrue("not isset var4", !parentEnv.issetParameter("var4"));

		assertEquals("simple var 1", "value1", parentEnv.getVariableValue("var1"));
		assertEquals("simple var 3", "value3", parentEnv.getVariableValue("var3"));
		assertEquals("value of not set var 4", null, parentEnv.getVariableValue("var4"));
		assertEquals("value of not set var 5", null, parentEnv.getVariableValue("var5"));
	}

	@Test
	public void testExport() {
		assertEquals("var 3 in child", "value3", childEnv.getVariableValue("var3"));
	}

	@Test
	public void testNullValue() {
		final String vName="newVar";
		parentEnv.setVariableValue(vName, null);
		assertTrue("isset newVar", parentEnv.issetParameter(vName));
		assertEquals("newVar==null", null, parentEnv.getVariableValue(vName));
		assertNotNull("getParameter()!=null", parentEnv.getParameter(vName));
	}

	@Test
	public void testExistsInChildAndParent() {
		assertEquals("var2 in parent", "value2", parentEnv.getVariableValue("var2"));
		assertEquals("var2 in child", "value2child", childEnv.getVariableValue("var2"));

		parentEnv.setVariableValue("var2", "newValue2");
		assertEquals("var2 in child", "value2child", childEnv.getVariableValue("var2")); // unchanged in child
	}

	// TODO implement and test multi threading

}
