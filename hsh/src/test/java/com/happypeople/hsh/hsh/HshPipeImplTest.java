package com.happypeople.hsh.hsh;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;

public class HshPipeImplTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testClose() throws IOException {
		final boolean[] closedHolder=new boolean[] { false };
		final InputStream is=new ByteArrayInputStream("hallo".getBytes()) {
			@Override
			public void close() throws IOException {
				closedHolder[0]=true;
				super.close();
			}
		};

		new HshPipeImpl(is).close();

		assertTrue("should be closed", closedHolder[0]);
	}

}
