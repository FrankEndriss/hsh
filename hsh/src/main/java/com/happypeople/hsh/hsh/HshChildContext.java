package com.happypeople.hsh.hsh;

import java.io.InputStream;
import java.io.PrintStream;

import com.happypeople.hsh.HshContext;
import com.happypeople.hsh.HshEnvironment;
import com.happypeople.hsh.HshExecutor;

class HshChildContext implements HshContext {
	private final HshContext parent;
	private final HshEnvironment env;
	private final HshExecutor executor;

	HshChildContext(HshContext parent) {
		this.parent=parent;
		this.env=new HshEnvironmentImpl(parent.getEnv());
		this.executor=new HshExecutorImpl(this);
		((HshEnvironmentImpl)env).addListener((HshExecutorImpl)executor);
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
	public HshContext createChildContext() {
		return new HshChildContext(this);
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
