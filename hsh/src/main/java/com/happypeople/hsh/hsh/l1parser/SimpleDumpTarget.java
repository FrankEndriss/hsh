package com.happypeople.hsh.hsh.l1parser;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class SimpleDumpTarget implements DumpTarget {
	private int level=0;
	private final List<String> buf=new ArrayList<String>();

	@Override
	public DumpTarget incLevel() {
		level+=1;
		return this;
	}

	@Override
	public DumpTarget decLevel() {
		level-=1;
		return this;
	}

	@Override
	public DumpTarget add(final CharSequence line) {
		buf.add(createPrefix(level)+line);
		return this;
	}

	private static String createPrefix(final int level) {
		final StringBuilder sb=new StringBuilder();
		for(int i=0; i<level; i++)
			sb.append('\t');
		return sb.toString();
	}

	public List<String> getBuf() {
		return buf;
	}

	public DumpTarget debug(final Logger logger) {
		for(final String s : buf)
			logger.debug(s);
		return this;
	}

	@Override
	public String toString() {
		final StringBuilder sb=new StringBuilder();
		for(final String s : buf)
			sb.append(s).append('\n');
		return sb.toString();
	}
}
