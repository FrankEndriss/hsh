package com.happypeople.hsh.hsh;

import java.io.InputStream;
import java.io.PrintStream;

import com.happypeople.hsh.HshContext;
import com.happypeople.hsh.HshEnvironment;
import com.happypeople.hsh.HshExecutor;
import com.happypeople.hsh.HshRedirections;

public class HshChildContext implements HshContext {
	private final HshContext parent;
	private final HshEnvironment env;
	private final HshExecutor executor;

	public HshChildContext(final HshContext parent) {
		this.parent=parent;
		this.env=parent!=null?parent.getEnv():new HshEnvironmentImpl(null);
		this.executor=parent!=null?parent.getExecutor():new HshExecutorImpl(null, this, null);
	}

	/** Initializes a new HshContext.
	 * @param parent if parent is not null the new Context is a child of parent
	 * @param env if env is not null env is the env of this context. if env is null, parent.getEnv() is used.
	 * @param executor if executor is not null executor is the executor of this context. if executor is null, parent.getExecutor() is used.
	 */
	public HshChildContext(final HshContext parent, final HshEnvironment env, final HshExecutor executor) {
		if(parent==null)
			throw new IllegalArgumentException("parent must not be null with this constructor");
		this.parent=parent;

		this.env= env==null?parent.getEnv():env;
		this.executor= executor==null?parent.getExecutor():executor;

		//((HshEnvironmentImpl)env).addListener(executor);
	}


	@Override
	public PrintStream getStdOut() {
		// TODO replace by getExecutor().getRedirections().getOutRedirection().getPrintStream()
		return parent.getStdOut();
	}

	@Override
	public InputStream getStdIn() {
		return parent.getStdIn();
	}

	@Override
	public PrintStream getStdErr() {
		return parent.getStdErr();
	}

	@Override
	public int getCols() {
		return parent.getCols();
	}

	@Override
	public int getRows() {
		return parent.getRows();
	}

	@Override
	public void finish() {
		// this is a child
		// should cause that no more commands are executed in this context
	}

	@Override
	public HshContext createChildContext(final HshEnvironment newEnv, final HshExecutor newExcecutor) {
		return new HshChildContext(this, newEnv==null?this.getEnv():newEnv, newExcecutor==null?this.getExecutor():newExcecutor);
	}

	@Override
	public HshContext createChildContext(final HshRedirections hshRedirections) {
		return new HshChildContext(this, null, new HshExecutorImpl(getExecutor(), this, hshRedirections));
	}


	@Override
	public HshEnvironment getEnv() {
		return env;
	}

	@Override
	public HshExecutor getExecutor() {
		return executor;
	}


}
