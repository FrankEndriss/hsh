package com.happypeople.hsh.hsh;

import java.util.Arrays;
import java.util.List;

import com.happypeople.hsh.HshContext;
import com.happypeople.hsh.HshExecutor;
import com.happypeople.hsh.HshRedirection;

/** This class acts like a single Executor, but delegates the calls to a list of executors.
 */
public class DelegatingHshExecutor implements HshExecutor {

	private final Iterable<HshExecutor> delegates;

	/** Call this constructor with a List of HshExecutor objects, the iterable will be used in all methods to loop
	 * over the delegates.
	 * @param delegatesProvider List (or similar) of delegates
	 */
	public DelegatingHshExecutor(final Iterable<HshExecutor> delegatesProvider) {
		this.delegates=delegatesProvider;
	}

	@Override
	public int execute(final String[] command, final HshContext context, final List<HshRedirection> redirections) throws Exception {
		for(final HshExecutor delegate : delegates)
			if(delegate.canExecute(command))
				return delegate.execute(command, context, redirections);
		throw new IllegalArgumentException("no provided executor can execute command: "+Arrays.asList(command));
	}

	@Override
	public void close() {
		for(final HshExecutor delegate : delegates)
			delegate.close();
	}

	@Override
	public boolean canExecute(final String[] command) {
		for(final HshExecutor delegate : delegates)
			if(delegate.canExecute(command))
				return true;
		return false;
	}

}
