package com.happypeople.hsh.hsh;

import java.util.List;

import com.happypeople.hsh.hsh.Parser.NotParsedException;

public interface Node {
	public List<Node> children();
	public void parse() throws NotParsedException;
}