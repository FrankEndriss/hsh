package com.happypeople.hsh.hsh;

import java.io.File;
import java.lang.ProcessBuilder.Redirect;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.happypeople.hsh.HshContext;
import com.happypeople.hsh.HshExecutor;
import com.happypeople.hsh.HshFDSet;
import com.happypeople.hsh.HshRedirection;
import com.happypeople.hsh.HshRedirection.OperationType;
import com.happypeople.hsh.HshRedirection.TargetType;

/** HshExecutor to execute commands from PATH by starting processes using javas ProcessBuilder
 */
public class PathHshExecutor implements HshExecutor {
	private final List<File> path=new ArrayList<File>();
	private final String lastUsedPath="";

	@Override
	public int execute(final String[] command, final HshContext parentContext, final List<HshRedirection> redirections) throws Exception {
		try {
			final ProcessBuilder builder=new ProcessBuilder();
			command[0]=resolveCmd(command[0], parentContext.getEnv().getVariableValue("PATH"));
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
	public boolean canExecute(final String[] command, final HshContext context) {
		// note that this makes sence because the PathExecutor is the last one
		// in the list of executors.
		return true;
	}

	@Override
	public void close() {
		// empty
	}

	/** Resolves a String to an executable file, using path
	 * @param string a command name
	 * @return absolute filename to the executable, or cmd if no executable was found
	 */
	private String resolveCmd(final String cmd, final String lpath) {
		final File f=new File(cmd);
		if(f.isAbsolute())
			return cmd;

		if(!lastUsedPath.equals(lpath))
			parsePath(lpath);

		for(final File p : path) {
			final File r=new File(p, cmd);
			if(r.canExecute())
				return r.getAbsolutePath();

			// honor windows
			final File w=new File(p, cmd+".exe");
			if(w.canExecute())
				return w.getAbsolutePath();
		}

		return cmd;
	}

	/** This method parses the environment var PATH and
	 * places that list of directories in the class var path.
	 */
	private void parsePath(final String lpath) {
		path.clear();
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

	/**
	 * Setup the processBuilder to execute a process using this redirection.
	 * This is possible only if this is a FILE redirection and this refers to one of the
	 * standard streams
	 * @param processBuilder
	 * @return true if a redirection was set, else false
	 */
	private boolean setupFileRedirection(final ProcessBuilder processBuilder, final HshRedirection redir) {
		if(redir.getTargetType()==TargetType.FILE) {
			if(redir.getRedirectedFD()==HshFDSet.STDIN && redir.getOperationType()==OperationType.READ) {
				processBuilder.redirectInput(redir.getTargetFile());
				return true;
			} else if(redir.getRedirectedFD()==HshFDSet.STDOUT) {
				if(redir.getOperationType()==OperationType.WRITE) {
					processBuilder.redirectOutput(redir.getTargetFile());
					return true;
				} else if(redir.getOperationType()==OperationType.APPEND) {
					processBuilder.redirectOutput(Redirect.appendTo(redir.getTargetFile()));
					return true;
				}
			} else if(redir.getRedirectedFD()==HshFDSet.STDERR) {
				if(redir.getOperationType()==OperationType.WRITE) {
					processBuilder.redirectError(redir.getTargetFile());
					return true;
				} else if(redir.getOperationType()==OperationType.APPEND) {
					processBuilder.redirectError(Redirect.appendTo(redir.getTargetFile()));
					return true;
				}
			}
		} // else ignore
		return false;
	}
}
