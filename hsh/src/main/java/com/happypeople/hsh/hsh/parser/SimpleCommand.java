package com.happypeople.hsh.hsh.parser;

import java.util.ArrayList;
import java.util.List;

import com.happypeople.hsh.hsh.L2Token;

public class SimpleCommand extends L2Node {
	private L2Token cmdName;
	private final List<L2Token> args=new ArrayList<L2Token>();
	private final List<L2Token> assignments=new ArrayList<L2Token>();

	public void setCmdName(final L2Token cmdName) {
		if(this.cmdName!=null)
			throw new RuntimeException("cannot set cmdName twice on SimpleCommand, old="+this.cmdName+" new="+cmdName);
		this.cmdName=cmdName;
		addChild(cmdName);
	}

	public L2Token getCmdName() {
		return cmdName;
	}

	public void addArg(final L2Token t) {
		args.add(t);
		addChild(t);
	}

	public List<L2Token> getArgs() {
		return args;
	}

	public void addAssignment(final L2Token t) {
		assignments.add(t);
		addChild(t);
	}

	public List<L2Token> getAssignments() {
		return assignments;
	}

	/** Creates a printout of the node-tree
	 * @param level the level of the tree this node lives in
	 */
	@Override
	public void dump(final int level) {
		final StringBuilder sb=new StringBuilder();
		for(int i=0; i<level; i++)
			sb.append("\t");
		final String t=sb.toString();
		System.out.println(t+getClass().getName());
		System.out.println(t+"assignments");
		for(final L2Token ass : assignments)
			ass.dump(level+1);
		if(cmdName!=null) {
			System.out.println(t+"cmdName");
			cmdName.dump(level+1);
		}
		System.out.println(t+"args");
		for(final L2Token arg : args)
			arg.dump(level+1);
	}

}
