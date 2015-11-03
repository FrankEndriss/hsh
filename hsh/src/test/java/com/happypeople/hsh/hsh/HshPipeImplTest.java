package com.happypeople.hsh.hsh;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;

import com.happypeople.hsh.HshPipe;

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

		final HshPipe p1=new HshPipeImpl(is);
		final HshPipe p2=new HshPipeImpl(is);

		p1.close();
		assertTrue("should not be closed", !closedHolder[0]);

		p2.close();
		assertTrue("should be closed", closedHolder[0]);
	}

	// TODO test multi thread features

}
