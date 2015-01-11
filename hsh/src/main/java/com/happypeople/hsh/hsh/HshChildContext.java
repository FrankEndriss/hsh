package com.happypeople.hsh.hsh;

import java.io.InputStream;
import java.io.PrintStream;

import com.happypeople.hsh.HshContext;
import com.happypeople.hsh.HshEnvironment;
import com.happypeople.hsh.HshExecutor;

public class HshChildContext implements HshContext {
	private final HshContext parent;
	private final HshEnvironment env;
	private final HshExecutor executor;

	public HshChildContext(final HshContext parent, final HshEnvironment env, final HshExecutor executor) {
		this.parent=parent;
		this.env=env;
		this.executor=executor;
		//((HshEnvironmentImpl)env).addListener(executor);
	}


	@Override
	public PrintStream getStdOut() {
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
	public HshEnvironment getEnv() {
		return env;
	}

	@Override
	public HshExecutor getExecutor() {
		return executor;
	}


}
