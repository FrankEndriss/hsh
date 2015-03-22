package com.happypeople.hsh.hsh;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.happypeople.hsh.HshContext;
import com.happypeople.hsh.HshEnvironment;
import com.happypeople.hsh.HshExecutor;
import com.happypeople.hsh.HshRedirection;
import com.happypeople.hsh.Parameter;
import com.happypeople.hsh.VariableParameter;

/** HshExecutor to execute commands from PATH
 */
public class PathHshExecutor implements HshExecutor {
	private final List<File> path=new ArrayList<File>();
	private final HshEnvironment env;

	/**
	 * @param env this Executor registers itself as a listener to env, to listen to changes of the PATH variable.
	 */
	public PathHshExecutor(final HshEnvironment env) {
		this.env=env;
		env.addListener(new HshEnvironment.ChangeListener() {
			@Override
			public void created(final Parameter parameter) {
				varChanged(parameter.getName());
			}

			@Override
			public void removed(final Parameter parameter) {
				varChanged(parameter.getName());
			}

			@Override
			public void exported(final Parameter parameter) {
			}

			@Override
			public void changed(final VariableParameter parameter, final String oldValue) {
				varChanged(parameter.getName());
			}
		});
	}

	@Override
	public int execute(final String[] command, final HshContext context, final List<HshRedirection> redirections) throws Exception {
		try {
			final ProcessBuilder builder=new ProcessBuilder();
			command[0]=resolveCmd(command[0]);
			builder.command(Arrays.asList(command));

			// TODO set env based on context

			/*
			final HshRedirection stderrRedir=hshRedirections.getStderrRedirection();
			builder.redirectError(stderrRedir.getType());
			final HshRedirection stdoutRedir=hshRedirections.getStdoutRedirection();
			builder.redirectOutput(stdoutRedir.getType());
			final HshRedirection stdinRedir=hshRedirections.getStdinRedirection();
			builder.redirectInput(stdinRedir.getType());
			*/

			final Process p=builder.start();

			/*
			if(stderrRedir.getType()==Redirect.PIPE)
				stderrRedir.setIn(new HshInput(p.getErrorStream()));
			if(stdoutRedir.getType()==Redirect.PIPE)
				stdoutRedir.setOut(new HshOutput(p.getOutputStream()));
			if(stdinRedir.getType()==Redirect.PIPE)
				stdinRedir.setIn(new HshInput(p.getInputStream()));
				*/

			p.waitFor();
			return p.exitValue();
		}catch(final Exception e) {
			e.printStackTrace(System.err);
			return -1;
		}
	}

	@Override
	public boolean canExecute(final String[] command) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
	}

	/** Resolves a String to an executable file, using path
	 * @param string a command name
	 * @return absolute filename to the executable, or cmd if no executable was found
	 */
	private String resolveCmd(final String cmd) {
		final File f=new File(cmd);
		if(f.isAbsolute())
			return cmd;

		if(path.size()==0)
			parsePath();

		for(final File p : path) {
			final File r=new File(p, cmd);
			if(r.exists() && r.isFile())
				return r.getAbsolutePath();

			// honor windows
			final File w=new File(p, cmd+".exe");
			if(w.exists() && w.isFile())
				return w.getAbsolutePath();
		}

		return cmd;
	}

	/** This method parses the environment var PATH and
	 * places that list of directories in the class var path.
	 */
	private void parsePath() {
		final String lpath=env.getVariableValue("PATH");

		if(lpath!=null) {
			final Collection<String> lpaths=Arrays.asList(lpath.split(File.pathSeparator));
			for(final String p : lpaths) {
				final File f=new File(p);
				if(f.isDirectory()) {
					path.add(f);
					System.out.println("adding path: "+f);
				}
			}
		} else
			System.out.println("no path.");
	}

	// HshEnvirionment-Listener
	private void varChanged(final String name) {
		// throw away cache
		if("PATH".equals(name))
			path.clear();
	}

}
