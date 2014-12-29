package com.happypeople.hsh.hsh.parser;

import com.happypeople.hsh.hsh.L2Token;

public class SimpleCommand extends L2Node {
	private L2Token cmdName;

	public void setCmdName(final L2Token cmdName) {
		this.cmdName=cmdName;
	}

	public L2Token getCmdName() {
		return cmdName;
	}
}
