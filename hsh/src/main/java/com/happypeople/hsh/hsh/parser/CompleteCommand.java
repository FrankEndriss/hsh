package com.happypeople.hsh.hsh.parser;

public class CompleteCommand extends L2Node {
	
	public ListNode list;
	public SeparatorNode separator;

	public void setList(ListNode list) {
		this.list=list;
	}
	
	public void setSeparator(SeparatorNode separator) {
		this.separator=separator;
	}
}
