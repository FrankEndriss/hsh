/**
 */
package com.happypeople.hsh.hsh;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;

import com.happypeople.hsh.HshContext;
import com.happypeople.hsh.HshEnvironment;
import com.happypeople.hsh.HshExecutor;
import com.happypeople.hsh.HshFdSet;
import com.happypeople.hsh.HshMessage;
import com.happypeople.hsh.HshMessageListener;
import com.happypeople.hsh.HshTerminal;

/** Simple aggregation of Environment, Executor, FDSet and Terminal.
 * @author Frank Endriss (fj.endriss@gmail.com)
 * @version $Id$
 * @since 0.1
 */
public class HshChildContext implements HshContext {
	private final Set<HshMessageListener> msgListeners=new HashSet<>();
	private final HshEnvironment env;
	private final HshExecutor executor;
	private final HshFdSet fdSet;
	private final HshTerminal terminal;

	/** Initializes a new HshContext.
	 * @param msglistener An initial observer
	 * @param env Of the new Context
	 * @param executor Of the new Context
	 * @param fdSet Of the new Context
	 * @param terminal Of the new Context
	 */
	HshChildContext(
		final HshMessageListener msglistener,
		final HshEnvironment env,
		final HshExecutor executor,
		final HshFdSet fdSet,
		final HshTerminal terminal)
	{
		this.env=env;
		this.executor=executor;
		if(fdSet==null) {
			throw new IllegalArgumentException("HshFdSet must not be null");
		}
		this.fdSet=fdSet;
		this.terminal=terminal;
		if(msglistener!=null) {
			this.msgListeners.add(msglistener);
		}
	}

	@Override
	public InputStream getStdIn() {
		return getFdSet().getPipe(HshFdSet.STDIN).getInputStream();
	}

	@Override
	public PrintStream getStdOut() {
		return getFdSet().getPipe(HshFdSet.STDOUT).getOutputStream();
	}

	@Override
	public PrintStream getStdErr() {
		return getFdSet().getPipe(HshFdSet.STDERR).getOutputStream();
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
	public HshFdSet getFdSet() {
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
