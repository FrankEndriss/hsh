package com.happypeople.hsh.hsh.parser;

import java.io.File;
import java.lang.ProcessBuilder.Redirect;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.happypeople.hsh.HshContext;
import com.happypeople.hsh.hsh.HshEnvironmentImpl;
import com.happypeople.hsh.hsh.HshParserConstants;
import com.happypeople.hsh.hsh.HshRedirection;
import com.happypeople.hsh.hsh.HshRedirectionImpl;
import com.happypeople.hsh.hsh.L2Token;
import com.happypeople.hsh.hsh.NodeTraversal;
import com.happypeople.hsh.hsh.l1parser.Executable;

public class SimpleCommand extends L2Node implements Executable {
	private final static boolean DEBUG=true;

	// TODO cmdName should not be treated in another way than the args, since
	// the only thing that makes the cmdName to what it is, is the fact that
	// it is the arg at position 0.
	// ie if it expands to the empty string, arg[1] becomes the cmdName and so on
	private int cmdName=-1;
	private final List<Integer> args=new ArrayList<Integer>();
	private final List<Integer> assignments=new ArrayList<Integer>();
	private final List<Integer> redirects=new ArrayList<Integer>();

	public void setCmdName(final L2Token cmdName) {
		if(this.cmdName>-1)
			throw new RuntimeException("cannot set cmdName twice on SimpleCommand, old="+this.cmdName+" new="+cmdName);
		this.cmdName=addChild(cmdName);
	}

	public L2Token getCmdName() {
		return cmdName<0?null:getChild(cmdName);
	}

	public void addArg(final L2Token t) {
		args.add(addChild(t));
	}

	public List<L2Token> getArgs() {
		return createChildList(args);
	}

	private List<L2Token> createChildList(final List<Integer> idx) {
		final List<L2Token> ret=new ArrayList<L2Token>();
		for(final int i : idx)
			ret.add(getChild(i));
		return ret;
	}

	public void addAssignment(final L2Token t) {
		assignments.add(addChild(t));
	}

	public List<L2Token> getAssignments() {
		return createChildList(assignments);
	}

	public void addRedirect(final RedirNode node) {
		redirects.add(addChild(node));
	}

	public List<L2Token> getRedirects() {
		return createChildList(redirects);
	}

	@Override
	public int doExecution(final HshContext context) throws Exception {
		if(DEBUG)
			System.out.println("SimpleCommand.doExecution");

		final HshContext lContext=context.createChildContext(new HshEnvironmentImpl(context.getEnv()), null);

		//HshRedirections hshRedirections=lContext.getExecutor().
		final HshRedirection[] redirections=new HshRedirection[3];

		// Note: the HshExecutor must be set up step by step, since there are possibly assignments
		// exectuted in between the io-redirects.
		// Furthermore, parts of the assignments could reference the executor...
		// Bsp.: "x=input.txt <$x x=output.txt >$x cmd"
		// Should read from input.txt and write to output.txt.
		// Note also that bash does not work this way. (refuses while parsing)

		final List<Integer> assAndRedirs=new ArrayList<Integer>();
		assAndRedirs.addAll(assignments);
		assAndRedirs.addAll(redirects);
		Collections.sort(assAndRedirs); // sort by index, ie process left to right

		for(final Integer idx : assAndRedirs) {
			final L2Token tok=getChild(idx);
			if(tok instanceof RedirNode) {	// its an redirection
				if(DEBUG)
					System.out.println("SimpleCommand.doExecution, have RedirNode: "+tok);
				final RedirNode redirNode=(RedirNode)tok;
				Redirect redirect=null;

				final String filename=NodeTraversal.substituteSubtree(redirNode.getFilename(), lContext);
				final String ioNumberString=redirNode.getIoNumber();
				int ioNumber=ioNumberString==null?-1:Integer.parseInt(ioNumberString);
				int redirIdx=-1;

				switch(redirNode.getOperator().kind) {
					case HshParserConstants.CLOBBER:
					case HshParserConstants.LESS:
						redirect=Redirect.from(new File(filename));
						if(ioNumber<0)
							ioNumber=0;
						break;
					case HshParserConstants.LESSAND:
						redirect=Redirect.PIPE;
						redirIdx=0;
						break;
					case HshParserConstants.GREAT:
						redirect=Redirect.to(new File(filename));
						redirIdx=1;
					case HshParserConstants.GREATAND:
					case HshParserConstants.DGREAT:
						redirect=Redirect.appendTo(new File(filename));
					case HshParserConstants.LESSGREAT:
				default:
					throw new RuntimeException("bad operator type in RedirNode :/");
				}
				final HshRedirection hshRedir=new HshRedirectionImpl(redirect);

			} else {	// its an assignment
				final L2Token assi=tok;
				if(DEBUG)
					System.out.println("SimpleCommand.doExecution, have assignment: "+assi);
				// Assignment-L2Token (kind=ASSIGNMENT_WORD) are structured:
				// First child SimpleNode(varName)
				// Second is SimpleNode("=")
				// Other parts: the value
				final String varName=assi.getPart(0).getImage();
				L2Token rhs=null;

				if(assi.getPartCount()>2) {
					rhs=new L2Token();
					for(int i=2; i<assi.getPartCount(); i++)
						rhs.addPart(assi.getPart(i));
				}

				String value;
				if(rhs==null)
					value=null;
				else {
					final List<String> lValue=rhs.doExpansion(lContext);
					if(lValue.size()==0)
						value=null;
					else {
						value=lValue.get(0);
						for(int i=1; i<lValue.size(); i++)
							value+=" "+lValue.get(i);
					}
				}
				lContext.getEnv().setVariableValue(varName, value);
				if(DEBUG)
					System.out.println("SimpleCommand.doExecution, assignment, key/value: "+varName+" "+value);
			}
		}

		if(getCmdName()!=null) { // else SimpleCommand is just a list of assignments, no command
			final List<String> cmdList=new ArrayList<String>();
			// TODO cmdName and args can be empty (after substitution), and therefore should be
			// checked to contain anything else than WS.
			// Additionally, on cmdList[0] leading and trailing WS should be removed.
			cmdList.addAll(getCmdName().doExpansion(lContext));
			for(final L2Token arg : getArgs())
				cmdList.addAll(arg.doExpansion(lContext));

			// TODO make sure cmdList does not contain empty strings in previous steps
			while(cmdList.remove(""));

			// and finally execute the command
			if(!cmdList.isEmpty())
				return lContext.getExecutor().execute(cmdList.toArray(new String[0]));
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

		if(getCmdName()!=null) {
			System.out.println(t+"cmdName");
			getCmdName().dump(level+1);
		}
		System.out.println(t+"args");
		for(final L2Token arg : getArgs())
			arg.dump(level+1);
	}


}
