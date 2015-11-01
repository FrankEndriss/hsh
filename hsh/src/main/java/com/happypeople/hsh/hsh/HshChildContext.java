package com.happypeople.hsh.hsh;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;

import com.happypeople.hsh.HshContext;
import com.happypeople.hsh.HshEnvironment;
import com.happypeople.hsh.HshExecutor;
import com.happypeople.hsh.HshFDSet;
import com.happypeople.hsh.HshMessage;
import com.happypeople.hsh.HshMessageListener;
import com.happypeople.hsh.HshTerminal;

/** Simple aggregation of Environment, Executor, FDSet and Terminal.
 */
public class HshChildContext implements HshContext {
	private final Set<HshMessageListener> msgListeners=new HashSet<HshMessageListener>();
	private final HshEnvironment env;
	private final HshExecutor executor;
	private final HshFDSet fdSet;
	private final HshTerminal terminal;

	/** Initializes a new HshContext.
	 * @param env of the new Context
	 * @param executor of the new Context
	 * @param fdSet of the new Context
	 */
	HshChildContext(
		final HshMessageListener msgListener,
		final HshEnvironment env,
		final HshExecutor executor,
		final HshFDSet fdSet,
		final HshTerminal terminal)
	{
		this.env=env;
		this.executor=executor;
		if(fdSet==null)
			throw new IllegalArgumentException("HshFDSet must not be null");
		this.fdSet=fdSet;
		this.terminal=terminal;
		if(msgListener!=null)
			this.msgListeners.add(msgListener);
	}

	@Override
	public InputStream getStdIn() {
		return getFDSet().getPipe(HshFDSet.STDIN).getInputStream();
	}

	@Override
	public PrintStream getStdOut() {
		return getFDSet().getPipe(HshFDSet.STDOUT).getOutputStream();

	}

	@Override
	public PrintStream getStdErr() {
		return getFDSet().getPipe(HshFDSet.STDERR).getOutputStream();
	}

	@Override
	public HshEnvironment getEnv() {
		return env;
	}

	@Override
	public HshExecutor getExecutor() {
		return executor;
	}

	@Override
	public HshFDSet getFDSet() {
		return fdSet;
	}

	@Override
	public HshTerminal getTerminal() {
		return terminal;
	}

	@Override
	public void close() throws Exception {
		if(env!=null)
			env.close();
		if(executor!=null) // because of this call to close() the executor should be a copy of the parents one
			executor.close();
		if(fdSet!=null)
			fdSet.close();
	}

	@Override
	public void msg(final HshMessage msg) {
		fireMessage(msg);
	}

	@Override
	public void addMsgListener(final HshMessageListener listener) {
		msgListeners.add(listener);
	}

	protected void fireMessage(final HshMessage message) {
		for(final HshMessageListener  listener : msgListeners)
			listener.msg(message);
	}

}
