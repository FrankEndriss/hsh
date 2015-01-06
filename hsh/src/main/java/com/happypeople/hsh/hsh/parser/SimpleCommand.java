package com.happypeople.hsh.hsh.parser;

import java.util.ArrayList;
import java.util.List;

import com.happypeople.hsh.HshContext;
import com.happypeople.hsh.hsh.L2Token;
import com.happypeople.hsh.hsh.NodeTraversal;
import com.happypeople.hsh.hsh.l1parser.AssignmentL2Token;
import com.happypeople.hsh.hsh.l1parser.Executable;
import com.happypeople.hsh.hsh.l1parser.L1Node;
import com.happypeople.hsh.hsh.l1parser.SimpleL1Node;

public class SimpleCommand extends L2Node implements Executable {
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

	@Override
	public int doExecution(final HshContext context) throws Exception {

		// execute assignments
		for(final L2Token assi : getAssignments()) {
			// assignment should allways be AssignmentL2Token
			// fail fast if not
			final AssignmentL2Token assiTok=(AssignmentL2Token)assi;
			// AssignmentL2Token (kind=ASSIGNMENT_WORD) are structured:
			// First child SimpleNode(varName)
			// Second is SimpleNode("=")
			// Third is optional L2Token, the rhs of the assignment
			String varName=null;
			L2Token rhs=null;
			boolean first=true;
			boolean second=true;
			for(final L1Node child : assiTok) {
				if(first) {
					varName=((SimpleL1Node)child).getImage();
					first=false;
				} else if(second) {
					second=false;
					// ignore
				} else {
					rhs=(L2Token)child;
				}
			}

			final String value=rhs!=null?NodeTraversal.substituteSubtree(rhs, context):null;
			context.getEnv().setVariableValue(varName, value);
		}

		if(getCmdName()!=null) { // else SimpleCommand is just a list of assignments, no command
			final List<String> cmdList=new ArrayList<String>();
			// TODO cmdName and args can be empty (after substitution), and therefore should be
			// checked to contain anything else than WS.
			// Additionally, on cmdList[0] leading and trailing WS should be removed.
			cmdList.add(NodeTraversal.substituteSubtree(getCmdName(), context));
			for(final L1Node arg : getArgs())
				cmdList.add(NodeTraversal.substituteSubtree(arg, context));

			// and finally execute the command
			return context.getExecutor().execute(cmdList.toArray(new String[0]));
		}
		// TODO what to return for assignment only??? try 0
		return 0;

	}
}
