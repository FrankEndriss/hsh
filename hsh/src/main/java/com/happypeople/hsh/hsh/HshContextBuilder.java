package com.happypeople.hsh.hsh;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.happypeople.hsh.HshContext;
import com.happypeople.hsh.HshEnvironment;
import com.happypeople.hsh.HshExecutor;
import com.happypeople.hsh.HshFDSet;
import com.happypeople.hsh.HshRedirections;
import com.happypeople.hsh.HshTerminal;

/** Builder class to ease creation of HshContext
 */
public class HshContextBuilder {
	private final static boolean DEBUG=false;

	private HshContext parentHshContext;
	private HshRedirections redirections;
	private HshExecutor executor;
	private HshEnvironment environment;
	private HshFDSet fdSet;
	private HshTerminal terminal;

	public HshContextBuilder terminal(final HshTerminal terminal) {
		this.terminal=terminal;
		return this;
	}

	public HshContextBuilder fdSet(final HshFDSet fdSet) {
		this.fdSet=fdSet;
		return this;
	}

	public HshContextBuilder environment(final HshEnvironment environment) {
		this.environment=environment;
		return this;
	}

	public HshContextBuilder executor(final HshExecutor executor) {
		this.executor=executor;
		return this;
	}

	public HshContextBuilder redirections(final HshRedirections redirections) {
		this.redirections=redirections;
		return this;
	}

	public HshContextBuilder parent(final HshContext parentHshContext) {
		this.parentHshContext=parentHshContext;
		return this;
	}

	/** Creates a new Context.
	 * If parent is null (root context), then environment and executor are created (if null).
	 * If parent is not null environment and executor of parent are used (if null).
	 * If environment and/or executor are not null, they are used.
	 *
	 * If fdSet is null a new HshFDSet is created. The parent of this FDSet is the parents
	 * FDSet, or null if parent is null.
	 *
	 * The terminal is used as is, so if you dont set the terminal the new HshContext is not
	 * connected to a terminal.
	 *
	 * If the execution context of the executor is null, then it is set to be the new
	 * created context.
	 *
	 * @return a new, usable HshContext
	 */
	public HshContext create() {
		if(DEBUG)
			System.out.println("create Context, parent="+parentHshContext);

		HshEnvironment lEnvironment=environment;
		HshExecutor lExecutor=executor;
		HshFDSet lFDSet=fdSet;

		// if parent is null (root context) then environment and executor must not be null
		if(parentHshContext==null) {
			if(lEnvironment==null)
				lEnvironment=new HshEnvironmentImpl(null);
			if(lExecutor==null)
				lExecutor=createDefaultExecutor(lEnvironment);
		}

		// fdSet must not be null at all
		if(lFDSet==null)
			lFDSet=new HshFDSetImpl(parentHshContext==null?null:parentHshContext.getFDSet());

		final HshContext context=new HshChildContext(parentHshContext,
				lEnvironment, lExecutor, lFDSet, terminal);

		return context;
	}

	private HshExecutor createDefaultExecutor(final HshEnvironment env) {
		final List<HshExecutor> xecutors=new ArrayList<HshExecutor>();

		xecutors.add(new FunctionHshExecutor(env));
		xecutors.add(new InProcessHshExecutor(init_predefines()));
		xecutors.add(new PathHshExecutor(env));

		return new DelegatingHshExecutor(xecutors);
	}

	private static Map<String, String> init_predefines() {
		final Map<String, String> predefs=new HashMap<String, String>();
		predefs.put("exit",	"com.happypeople.hsh.exit.Exit");
		predefs.put("find",	"com.happypeople.hsh.find.Find");
		predefs.put("ls", 	"com.happypeople.hsh.ls.Ls");
		predefs.put("quit",	"com.happypeople.hsh.exit.Exit");
		predefs.put("tail",	"com.happypeople.hsh.tail.Main");
		return predefs;
	}


}
