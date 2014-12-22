package com.happypeople.hsh.hsh;

import java.util.List;

public interface Node {
	public List<Node> children();
	public void parse() throws NotParsedException;
}