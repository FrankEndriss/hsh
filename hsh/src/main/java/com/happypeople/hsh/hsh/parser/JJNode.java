package com.happypeople.hsh.hsh.parser;

import java.util.ArrayList;
import java.util.List;

public class JJNode {
	private final List<JJNode> children=new ArrayList<JJNode>();
	public void addChild(final JJNode node) {
		children.add(node);
	}

	public void dump(final int level) {
		for(int i=0; i<level; i++)
			System.out.print("\t");
		System.out.println(getClass().getName());
		for(final JJNode child : children)
			child.dump(level+1);
	}

	public void execute() {
		for(final JJNode child : children)
			child.execute();
	}
}
