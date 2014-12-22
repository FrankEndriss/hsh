package com.happypeople.hsh.hsh;

import java.util.ArrayList;
import java.util.List;

public class JJNode {
	private List<JJNode> children=new ArrayList<JJNode>();
	public void addChild(final JJNode node) {
		children.add(node);
	}
}
