package com.happypeople.hsh.hsh;

import java.io.InputStream;
import java.io.PrintStream;

import com.happypeople.hsh.HshContext;
import com.happypeople.hsh.HshEnvironment;
import com.happypeople.hsh.HshExecutor;
import com.happypeople.hsh.HshFDSet;
import com.happypeople.hsh.HshTerminal;

public class HshChildContext implements HshContext {
	private final HshContext parent;
	private final HshEnvironment env;
	private final HshExecutor executor;
	private final HshFDSet fdSet;
	private final HshTerminal terminal;
	private boolean finished=false;

	/** Initializes a new HshContext.
	 * @param parent of the new Context
	 * @param env of the new Context
	 * @param executor of the new Context
	 * @param fdSet of the new Context
	 */
	HshChildContext(final HshContext parent,
		final HshEnvironment env,
		final HshExecutor executor,
		final HshFDSet fdSet,
		final HshTerminal terminal)
	{
		this.parent=parent;
		this.env=env;
		this.executor=executor;
		if(fdSet==null)
			throw new IllegalArgumentException("HshFDSet must not be null");
		this.fdSet=fdSet;
		this.terminal=terminal;
	}

	@Override
	public InputStream getStdIn() {
		return getFDSet().getInput(HshFDSet.STDIN).getInputStream();
	}

	@Override
	public PrintStream getStdOut() {
		return getFDSet().getOutput(HshFDSet.STDOUT).getOutputStream();

	}

	@Override
	public PrintStream getStdErr() {
		return getFDSet().getOutput(HshFDSet.STDERR).getOutputStream();
	}

	@Override
	public void finish() {
		finished=true;
		if(parent!=null)
			parent.finish();
	}

	@Override
	public boolean isFinish() {
		return finished;
	}

	@Override
	public HshEnvironment getEnv() {
		return env!=null?env:parent.getEnv();
	}

	@Override
	public HshExecutor getExecutor() {
		return executor!=null?executor:parent.getExecutor();
	}

	@Override
	public HshFDSet getFDSet() {
		return fdSet;
	}

	@Override
	public HshTerminal getTerminal() {
		return terminal==null && parent!=null?parent.getTerminal():terminal;
	}

	@Override
	public void close() {
		if(env!=null)
			env.close();
		if(executor!=null)
			executor.close();
		if(fdSet!=null)
			fdSet.close();
	}

}
