package com.happypeople.hsh;

import java.util.ArrayList;
import java.util.List;

/** This class acts like a single Executor, but delegates the calls to a list of executors.
 */
public class DelegatingHshExecutor implements HshExecutor {

	private final List<HshExecutor> delegates=new ArrayList<HshExecutor>();

	@Override
	public int execute(final String[] command, final HshContext context, final List<HshRedirection> redirections) throws Exception {
		for(final HshExecutor delegate : delegates)
			if(delegate.canExecute(command))
				return delegate.execute(command, context, redirections);
		return -1;
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
