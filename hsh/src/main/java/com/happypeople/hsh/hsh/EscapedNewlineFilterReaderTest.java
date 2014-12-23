package com.happypeople.hsh.hsh;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.junit.Test;

public class EscapedNewlineFilterReaderTest {

	@Test
	public void testReadCharArrayIntInt() throws IOException {
		String st1="bla laber";
		String st2=st1;
		do_test("1", st1, st2);

		st1="bla \\\nlaber";
		do_test("2", st1, st2);
		
		st1="bla laber\\\n";
		do_test("3", st1, st2);

		st1="\\\nbla laber";
		do_test("4", st1, st2);

		st1="\\\nbla \\laber";
		st2="bla \\laber";
		do_test("5", st1, st2);
	}
		
	private void do_test(String num, String in, String exp) throws IOException {
		final Reader reader=new StringReader(in);
		try(EscapedNewlineFilterReader out=new EscapedNewlineFilterReader(reader)) {
			String s="";
			int c;
			while((c=out.read())>0)
				s=s+(char)c;
			assertEquals("do_test "+num, exp, s);
		}
	}
}
