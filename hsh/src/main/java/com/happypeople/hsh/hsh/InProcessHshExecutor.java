package com.happypeople.hsh.hsh;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.happypeople.hsh.HshCmd;
import com.happypeople.hsh.HshContext;
import com.happypeople.hsh.HshExecutor;
import com.happypeople.hsh.HshRedirection;

/** Executor execution commands by instantiating and calling java classes in the current process.
 * TODO implement better class loading. Currently all classes are loaded within one and the same class loader.
 * Take a look at java9 modules.
 */
public class InProcessHshExecutor implements HshExecutor {
	private final Map<String, String> predefs;

	/**
	 * @param cmd2ClassnameMap the map maps command names to fully qualified classnames.
	 */
	public InProcessHshExecutor(final Map<String, String> cmd2ClassnameMap) {
		predefs=cmd2ClassnameMap;
	}

	@Override
	public int execute(final String[] command, final HshContext parentContext, final List<HshRedirection> redirections)
				throws Exception {

		try(final HshContext childContext=new HshContextBuilder().parent(parentContext).create()) {
			final String className=predefs.get(command[0]);

			for(final HshRedirection redir : redirections)
				childContext.getFDSet().addRedirection(redir);

			final Class<?> cls=Class.forName(className);
			if(HshCmd.class.isAssignableFrom(cls)) { // cls implements HshCmd
				final HshCmd hshCmd=(HshCmd) cls.newInstance();
				return hshCmd.execute(childContext, new ArrayList<String>(Arrays.asList(command)));
			} else {
				// should set System.in/out/err
				cls.getMethod("main", new Class[]{ command.getClass()}).invoke(null, new Object[] { command });
				return 0;
			}
		}catch(final Exception e) {
			e.printStackTrace();
			return 1;
		}
	}

	@Override
	public boolean canExecute(final String[] command, final HshContext parentContext) {
		return predefs.get(command[0])!=null;
	}

	@Override
	public void close() {
		// empty
	}
}
