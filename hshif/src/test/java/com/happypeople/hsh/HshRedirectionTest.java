package com.happypeople.hsh;

import java.io.File;

import org.junit.Test;

public class HshRedirectionTest {

	@Test
	public void testHshRedirectionIntOperationTypeFile() {
		new HshRedirection(HshFDSet.STDIN, HshRedirection.OperationType.READ, new File("/tmp/bla.f"));
	}

	@Test
	public void testHshRedirectionIntOperationTypeInteger() {
		new HshRedirection(HshFDSet.STDERR, HshRedirection.OperationType.WRITE, HshFDSet.STDOUT);
	}

}
