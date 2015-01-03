package com.happypeople.hsh.hsh.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.happypeople.hsh.HshContext;
import com.happypeople.hsh.hsh.L2Token;
import com.happypeople.hsh.hsh.NodeTraversal;
import com.happypeople.hsh.hsh.NodeTraversal.TraverseListener;
import com.happypeople.hsh.hsh.NodeTraversal.TraverseListenerResult;
import com.happypeople.hsh.hsh.l1parser.AssignmentL1Node;
import com.happypeople.hsh.hsh.l1parser.ComplexL1Node;
import com.happypeople.hsh.hsh.l1parser.Executable;
import com.happypeople.hsh.hsh.l1parser.L1Node;
import com.happypeople.hsh.hsh.l1parser.SimpleL1Node;
import com.happypeople.hsh.hsh.l1parser.Substitutable;
import com.happypeople.hsh.hsh.l1parser.TokenNode;

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
			//L2Token of kind ASSIGNMENT_WORD are structured:
			// First child is AssignmentL1Node
			// All other childs are the right-hand side of the assignment
			String varName=null;
			final ComplexL1Node rhs=new ComplexL1Node();
			boolean first=true;
			boolean rhsHasChildren=false;
			for(final L1Node child : assi) {
				if(first) {
					varName=((AssignmentL1Node)child).getVarname().getString();
					first=false;
				} else {
					rhs.add(child);
					rhsHasChildren=true;
				}
			}

			final String value=rhsHasChildren?NodeTraversal.substituteSubtree(rhs, context):null;
			context.getEnv().setVariableValue(varName, value);
		}

		if(getCmdName()!=null) { // else SimpleCommand is just a list of assignments, no command
			final List<String> cmdList=new ArrayList<String>();
			cmdList.add(NodeTraversal.substituteSubtree(getCmdName(), context));
			for(final L1Node arg : getArgs())
				cmdList.add(NodeTraversal.substituteSubtree(arg, context));

			// and finally execute the command
			return context.getExecutor().execute(cmdList.toArray(new String[0]));
		}
		// TODO what to return for assignment only??? try 0
		return 0;

	}

	private String substituteSubtree(final L1Node subtree, final HshContext context) {
		final StringBuilder sb=new StringBuilder();
		NodeTraversal.traverse(subtree, new TraverseListener() {
			@Override
			public TraverseListenerResult node(final L1Node node, final int level) {
				try {
					if(node instanceof Substitutable) {
						sb.append(((Substitutable)node).getSubstitutedString(context));
						return TraverseListenerResult.DONT_CHILDREN;
					} else // TODO must be on Stringifiable only (or on Leafs only)
						if(node instanceof SimpleL1Node || node instanceof TokenNode)
							sb.append(node.getString());
				} catch (final IOException e) {
					e.printStackTrace();
					return TraverseListenerResult.STOP;
				}
				return TraverseListenerResult.CONTINUE;
			}
		});
		return sb.toString();
	}
}
