package com.happypeople.hsh.hsh.parser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.happypeople.hsh.HshContext;
import com.happypeople.hsh.HshFDSet;
import com.happypeople.hsh.HshRedirection;
import com.happypeople.hsh.hsh.HshContextBuilder;
import com.happypeople.hsh.hsh.HshParserConstants;
import com.happypeople.hsh.hsh.L2Token;
import com.happypeople.hsh.hsh.NodeTraversal;
import com.happypeople.hsh.hsh.l1parser.Executable;

public class SimpleCommand extends L2Node implements Executable {
	private final static Logger log=Logger.getLogger(SimpleCommand.class);

	private final List<Integer> args=new ArrayList<Integer>();
	private final List<Integer> assignments=new ArrayList<Integer>();
	private final List<Integer> redirects=new ArrayList<Integer>();

	public void addArg(final L2Token t) {
		args.add(addChild(t));
	}

	public List<L2Token> getArgs() {
		return createChildList(args);
	}

	public void addAssignment(final L2Token t) {
		assignments.add(addChild(t));
	}

	public List<L2Token> getAssignments() {
		return createChildList(assignments);
	}

	public void addRedirect(final RedirNode node) {
		log.debug("redirection was added: "+node);
		if(node==null)
			throw new IllegalArgumentException("node must not be null");
		log.debug("node properties: "+node.getIoNumber()+node.getOperator()+node.getFilename());
		redirects.add(addChild(node));
	}

	public List<L2Token> getRedirects() {
		return createChildList(redirects);
	}

	private List<L2Token> createChildList(final List<Integer> idx) {
		final List<L2Token> ret=new ArrayList<L2Token>();
		for(final int i : idx)
			ret.add(getChild(i));
		return ret;
	}

	@Override
	public int doExecution(final HshContext context) throws Exception {
		log.debug("SimpleCommand.doExecution");

		// 1. Expand cmd (using parent context)

		// If cmd isEmpty()
		// 2a. If cmd is empty, do assignments in parent context
		// 4. create files touched by io redirections
		// return 0

		// else (cmd !isEmpty())
		// 2b. Create a new Context for this execution
		// 3b. do assignments in child context
		// 4. Setup IO-Redirections.
		// 5. Execute cmd
		// 6. Close the created context (Close IOs opened while step 4.)
		// 7. return result of step 5.

		// TODO outsource some of the above steps to be reusable by other executables L2Nodes

		// Step 1.
		final List<String> cmdList=new ArrayList<String>();
		for(final L2Token arg : getArgs())
			cmdList.addAll(arg.doExpansion(context));


		// Step 2.
		//final HshFDSetImpl fdSet=new HshFDSetImpl(context.getFDSet());
		final HshContext lContext=cmdList.isEmpty()?context:new HshContextBuilder().parent(context).create();

		// Step 3.
		for(final Integer idx : assignments) {
			final L2Token assi=getChild(idx);
			log.debug("SimpleCommand.doExecution, have assignment: "+assi);
			// Assignment-L2Token (kind=ASSIGNMENT_WORD) are structured:
			// First child SimpleNode(varName)
			// Second is SimpleNode("=")
			// Other parts: the value
			final String varName=assi.getPart(0).getImage();
			StringBuilder value=null;
			if(assi.getPartCount()>2) {
				final L2Token rhs=new L2Token();
				for(int i=2; i<assi.getPartCount(); i++)
					rhs.addPart(assi.getPart(i));
				final List<String> valueParts=rhs.doExpansion(lContext);
				if(valueParts.size()==0)
					value=null;
				else {
					value=new StringBuilder();
					value.append(valueParts.get(0));
					for(int i=1; i<valueParts.size(); i++) {
						value.append(' ').append(valueParts.get(i));
					}
				}
			}
			lContext.getEnv().setVariableValue(varName, value==null?null:value.toString());
			log.debug("SimpleCommand.doExecution, assignment, key/value: "+varName+" "+value);
		}

		// Step 4.
		// Note that this Code should be moved to a more common class,
		// since it is needed in several other command.
		// Most likely, it should go into the executors.
		// Another good place would be class RedirNode
		final List<HshRedirection> redirList=new ArrayList<HshRedirection>();
		for(final Integer idx : redirects) {
			final L2Token tok=getChild(idx);
			log.debug("SimpleCommand.doExecution, have RedirNode: "+tok);
			final RedirNode redirNode=(RedirNode)tok;

			final String filename=NodeTraversal.substituteSubtree(redirNode.getFilename(), lContext);
			log.debug("redirNode.filename="+filename);
			final String ioNumberString=redirNode.getIoNumber();
			log.debug("redirNode.ioNumber="+ioNumberString);
			int ioNumber=ioNumberString==null?-1:Integer.parseInt(ioNumberString);

			final HshRedirection hshRedir;

			switch(redirNode.getOperator().kind) {
				case HshParserConstants.CLOBBER:
				case HshParserConstants.LESS:
					if(ioNumber<0)
						ioNumber=HshFDSet.STDIN;
					hshRedir=new HshRedirection(ioNumber, HshRedirection.OperationType.READ, new File(filename));
					break;
				case HshParserConstants.LESSAND:
					if(ioNumber<0)
						ioNumber=HshFDSet.STDIN;
					hshRedir=new HshRedirection(ioNumber, HshRedirection.OperationType.READ, Integer.parseInt(filename));
					break;
				case HshParserConstants.GREAT:
					if(ioNumber<0)
						ioNumber=HshFDSet.STDOUT;
					hshRedir=new HshRedirection(ioNumber, HshRedirection.OperationType.WRITE, new File(filename));
					break;
				case HshParserConstants.GREATAND:
					if(ioNumber<0)
						ioNumber=HshFDSet.STDOUT;
					hshRedir=new HshRedirection(ioNumber, HshRedirection.OperationType.WRITE, Integer.parseInt(filename));
					break;
				case HshParserConstants.DGREAT:
					if(ioNumber<0)
						ioNumber=HshFDSet.STDOUT;
					hshRedir=new HshRedirection(ioNumber, HshRedirection.OperationType.APPEND, new File(filename));
					break;
				case HshParserConstants.LESSGREAT:
					log.error("LESSGREAT redirection not implemented");
					throw new RuntimeException("LESSGREAT redirection not implemented");
			default:
				throw new RuntimeException("unknown operator type in RedirNode :/ "+redirNode.getOperator());
			}
			redirList.add(hshRedir);
		}

		// Step 5.
		if(!cmdList.isEmpty()) {
			log.info("SimpleCommand, execute: "+cmdList);
			final int result=context.getExecutor().execute(cmdList.toArray(new String[0]), lContext, redirList);
			lContext.close();
			return result;
		}

		// TODO what to return for assignment only??? try 0
		return 0;

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

		System.out.println(t+"redirects");
		for(final L2Token redir : getRedirects())
			redir.dump(level+1);

		System.out.println(t+"assignments");
		for(final L2Token ass : getAssignments())
			ass.dump(level+1);

		System.out.println(t+"args");
		for(final L2Token arg : getArgs())
			arg.dump(level+1);
	}


}
