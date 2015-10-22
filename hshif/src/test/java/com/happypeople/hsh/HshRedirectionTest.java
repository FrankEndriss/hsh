package com.happypeople.hsh;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

public class HshRedirectionTest {

	@Test
	public void testHshRedirectionIntOperationTypeFile() {
		final HshRedirection hshRedir=new HshRedirection(HshFDSet.STDIN, HshRedirection.OperationType.READ, new File("/tmp/bla.f"));
		assertEquals("redirected stream", HshFDSet.STDIN, hshRedir.getRedirectedFD());
		assertEquals("targetType FILE", HshRedirection.TargetType.FILE, hshRedir.getTargetType());
	}

	@Test
	public void testHshRedirectionIntOperationTypeInteger() {
		new HshRedirection(HshFDSet.STDERR, HshRedirection.OperationType.WRITE, HshFDSet.STDOUT);
	}

}
