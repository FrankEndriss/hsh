package com.happypeople.hsh.hsh.parser;

public class CompleteCommand extends L2Node {

	public void setList(final ListNode list) {
		addChild(list);
	}

	public void setSeparator(final SeparatorNode separator) {
		addChild(separator);
	}
}
