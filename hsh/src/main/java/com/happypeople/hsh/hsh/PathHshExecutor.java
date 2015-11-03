package com.happypeople.hsh.hsh;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import com.happypeople.hsh.HshContext;
import com.happypeople.hsh.HshExecutor;
import com.happypeople.hsh.HshFDSet;
import com.happypeople.hsh.HshPipe;
import com.happypeople.hsh.HshRedirection;
import com.happypeople.hsh.HshRedirection.OperationType;

/** HshExecutor to execute commands from PATH by starting processes using javas ProcessBuilder
 */
public class PathHshExecutor implements HshExecutor {
	private final static Logger log=Logger.getLogger(PathHshExecutor.class);
	private final List<File> path=new ArrayList<File>();
	private String lastUsedPath="";

	@Override
	public int execute(final String[] command, final HshContext parentContext, final List<HshRedirection> redirections) throws Exception {
		log.info("execute: "+Arrays.asList(command));

		try(HshFDSet childFDSet=parentContext.getFDSet().createCopy()) {

			final ProcessBuilder builder=new ProcessBuilder();
			command[0]=resolveCmd(command[0], parentContext.getEnv().getVariableValue("PATH"));
			builder.command(Arrays.asList(command));

			// TODO set env based on context

			boolean stdInRedirected=false;
			// process redirections in order
			// Since redirection can be "chained"
			// ie "cat >myfile.txt 2>&1 4<myInput.txt <&4"
			// this loop should live somewhere else since it is needed in other HshExecutors and Executables, too
			for(final HshRedirection redir : redirections) {
				HshPipe newPipe=null;
				switch(redir.getTargetType()) {
				case ANOTHER_FD:
					newPipe=childFDSet.getPipe(redir.getTargetFD()).createCopy();
					break;
				case FILE:
					newPipe=redir.getOperationType()==OperationType.READ?
							new HshPipeImpl(new FileInputStream(redir.getTargetFile())):
							new HshPipeImpl(new PrintStream(new FileOutputStream(redir.getTargetFile())));
					break;
				default:
					throw new IllegalStateException("redirection of unknown TargetType");
				}
				childFDSet.closePipe(redir.getRedirectedFD());
				childFDSet.setPipe(redir.getRedirectedFD(), newPipe);

				if(redir.getRedirectedFD()==HshFDSet.STDIN)
					stdInRedirected=true;
			}

			// if stdin is not explicitly redirected, then inherit parents process stdin
			// note that this will cause truble when executed as part of a script
			// or loop or any other context which might have an redirected stdin
			// Because of this we check against System.in...
			boolean stdinInherited=false;
			if(!stdInRedirected && childFDSet.getPipe(HshFDSet.STDIN).getInputStream()==System.in) {
				builder.redirectInput(ProcessBuilder.Redirect.INHERIT);
				stdinInherited=true;
			}

			// now start the process
			final Process p=builder.start();

			if(!stdinInherited) {
				new HshPipeImpl(
						childFDSet.getPipe(HshFDSet.STDIN).getInputStream(),
						new PrintStream(p.getOutputStream())
					).startConnectThread();
			}

			new HshPipeImpl(
					p.getInputStream(),
					childFDSet.getPipe(HshFDSet.STDOUT).getOutputStream()
				).startConnectThread();

			new HshPipeImpl(
					p.getErrorStream(),
					childFDSet.getPipe(HshFDSet.STDERR).getOutputStream()
				).startConnectThread();

			p.waitFor();
			// TODO need to restore prompt here in case that p did some output
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

	private boolean notEquals(final String s1, final String s2) {
		if(s1==s2)
			return false;
		return (s1==null && s2!=null) || s2==null || !s1.equals(s2);

	}

	/** Resolves a String to an executable file, using path
	 * @param string a command name
	 * @return absolute filename to the executable, or cmd if no executable was found
	 */
	private String resolveCmd(final String cmd, final String lpath) {
		final File f=new File(cmd);
		if(f.isAbsolute())
			return cmd;

		if(notEquals(lastUsedPath, lpath)) {
			parsePath(lpath);
			lastUsedPath=lpath;
		}

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
	 */
}
