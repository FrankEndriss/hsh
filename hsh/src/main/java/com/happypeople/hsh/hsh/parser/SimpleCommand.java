package com.happypeople.hsh.hsh.parser;

import java.util.ArrayList;
import java.util.List;

import com.happypeople.hsh.hsh.L2Token;

public class SimpleCommand extends L2Node {
	private L2Token cmdName;
	private List<L2Token> args=new ArrayList<L2Token>();
	private List<L2Token> assignments=new ArrayList<L2Token>();

	public void setCmdName(final L2Token cmdName) {
		if(this.cmdName!=null)
			throw new RuntimeException("cannot set cmdName twice on SimpleCommand, old="+this.cmdName+" new="+cmdName);
		this.cmdName=cmdName;
	}

	public L2Token getCmdName() {
		return cmdName;
	}
	
	public void addArg(L2Token t) {
		args.add((L2Token)t);
	}
	
	public List<L2Token> getArgs() {
		return args;
	}
	
	public void addAssignment(L2Token t) {
		assignments.add(t);
	}
	
	public List<L2Token> getAssignments() {
		return assignments;
	}

	/** Creates a printout of the node-tree
	 * @param level the level of the tree this node lives in
	 */
	public void dump(final int level) {
		StringBuilder sb=new StringBuilder();
		for(int i=0; i<level; i++)
			sb.append("\t");
		String t=sb.toString();
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
