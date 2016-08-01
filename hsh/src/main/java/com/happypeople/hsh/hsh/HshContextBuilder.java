package com.happypeople.hsh.hsh;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.happypeople.hsh.HshContext;
import com.happypeople.hsh.HshEnvironment;
import com.happypeople.hsh.HshExecutor;
import com.happypeople.hsh.HshFdSet;
import com.happypeople.hsh.HshTerminal;

/** Builder class to ease creation of HshContext
 */
public class HshContextBuilder {
	private final static Logger log=Logger.getLogger(HshContextBuilder.class);

	private HshContext parentHshContext;
	private HshExecutor executor;
	private HshTerminal terminal;
	private HshEnvironment environment;
	private HshFdSet fdSet;

	public HshContextBuilder terminal(final HshTerminal terminal) {
		this.terminal=terminal;
		return this;
	}

	public HshContextBuilder fdSet(final HshFdSet fdSet) {
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

	public HshContextBuilder parent(final HshContext parentHshContext) {
		this.parentHshContext=parentHshContext;
		return this;
	}

	/** Creates a new Context.
	 * If parent is null (root context), then environment, fdSet and executor are created (if null).
	 * If parent is not null environment and executor of parent are used (if null). fdSet is copied from parent,
	 * and environment is all exported parts of parents environment.
	 *
	 * The terminal is never copied, its allways the parents one or null.
	 *
	 * @return a new, usable HshContext
	 */
	public HshContext create() {
		log.debug("create Context, parent="+parentHshContext);

		final HshTerminal lTerminal= terminal!=null ? terminal :
						parentHshContext!=null ? parentHshContext.getTerminal() :
						null;

		final HshEnvironment lEnvironment= environment!=null ? environment :
						parentHshContext!=null ? parentHshContext.getEnv() :
						new HshEnvironmentImpl(null);

		final HshExecutor lExecutor= executor!=null ? executor :
						parentHshContext!=null ? parentHshContext.getExecutor() :
						createDefaultExecutor();

		final HshFdSet lFDSet= fdSet!=null ? fdSet :
						parentHshContext!=null ? parentHshContext.getFdSet().createCopy() :
						createDefaultFDSet();


		return new HshChildContext(parentHshContext, lEnvironment, lExecutor, lFDSet, lTerminal);
	}


	private static HshFdSet createDefaultFDSet() {
		return new HshFDSetImpl();
	}

	private static HshExecutor createDefaultExecutor() {
		final List<HshExecutor> xecutors=new ArrayList<>();

		xecutors.add(new FunctionHshExecutor());
		xecutors.add(new InProcessHshExecutor(init_predefines()));
		xecutors.add(new PathHshExecutor());

		return new DelegatingHshExecutor(xecutors);
	}

	private static Map<String, String> init_predefines() {
		final Map<String, String> predefs=new HashMap<>();
		predefs.put("exit",	"com.happypeople.hsh.exit.Exit");
		predefs.put("find",	"com.happypeople.hsh.find.Find");
		predefs.put("ls", 	"com.happypeople.hsh.ls.Ls");
		predefs.put("quit",	"com.happypeople.hsh.exit.Exit");
		predefs.put("tail",	"com.happypeople.hsh.tail.Main");
		return predefs;
	}


}
