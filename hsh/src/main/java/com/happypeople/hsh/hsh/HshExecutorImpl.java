package com.happypeople.hsh.hsh;

import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.happypeople.hsh.HshCmd;
import com.happypeople.hsh.HshContext;
import com.happypeople.hsh.HshExecutor;
import com.happypeople.hsh.Parameter;
import com.happypeople.hsh.VariableParameter;

public class HshExecutorImpl implements HshExecutor, HshEnvironmentImpl.ChangeListener {
	private final static Map<String, String> predefs=init_predefines();
	private List<File> path=new ArrayList<File>();
	private final HshContext hshContext;
	private final HshRedirections hshRedirections;

	public HshExecutorImpl(final HshContext context, final HshRedirections redirections) {
		this.hshContext=context;
		this.hshRedirections=redirections;
	}

	@Override
	public int execute(final String[] command) throws Exception {
		if(command.length<1)
			return 0;	// empty line

		final String buildin=predefs.get(command[0]);
		if(buildin!=null)
			return exec_buildin_Main(buildin, command);
		else
			return exec_extern_synchron(command);
	}

	/** Executes the cmd line given in args and waits for it to finish execution.
	 * args[0] is the command to execute.
	 * $PATH is resolved to find that program.
	 * @param args the arg vector
	 * @param redirections the redirections to apply to the ProcessBuilder
	 * @return exit status of the created process
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private int exec_extern_synchron(final String[] args) {
		// TODO export hshContext as environment
		try {
			final ProcessBuilder builder=new ProcessBuilder();
			args[0]=resolveCmd(args[0]);
			builder.command(Arrays.asList(args));

			final HshRedirection stderrRedir=hshRedirections.getStderrRedirection();
			builder.redirectError(stderrRedir.getType());
			final HshRedirection stdoutRedir=hshRedirections.getStdoutRedirection();
			builder.redirectOutput(stdoutRedir.getType());
			final HshRedirection stdinRedir=hshRedirections.getStdinRedirection();
			builder.redirectInput(stdinRedir.getType());

			final Process p=builder.start();

			if(stderrRedir.getType()==Redirect.PIPE)
				stderrRedir.setIn(new HshInput(p.getErrorStream()));
			if(stdoutRedir.getType()==Redirect.PIPE)
				stdoutRedir.setOut(new HshOutput(p.getOutputStream()));
			if(stdinRedir.getType()==Redirect.PIPE)
				stdinRedir.setIn(new HshInput(p.getInputStream()));

			p.waitFor();
			return p.exitValue();
		}catch(final Exception e) {
			e.printStackTrace(System.err);
			return -1;
		}
	}

	/** Executes a buildin class/cmd.
	 * If the class implements HshCmd that interface is used, else main(String[] args) is called.
	 * @param buildinClass full qualified name of class implementing the command args[0]
	 * @param args command arg vector unix style, ie args[0] is the called command
	 * @throws ClassNotFoundException if the class cannot be loaded or called
	 * @throws SecurityException if the class cannot be loaded or called
	 * @throws NoSuchMethodException if the class cannot be loaded or called
	 * @throws InvocationTargetException if the class cannot be loaded or called
	 * @throws IllegalArgumentException if the class cannot be loaded or called
	 * @throws IllegalAccessException if the class cannot be loaded or called
	 */
	private int exec_buildin_Main(final String buildinClass, final String[] args) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException {
		try {
			final Class<?> cls=Class.forName(buildinClass);
			if(HshCmd.class.isAssignableFrom(cls)) { // cls implements HshCmd
				final HshCmd hshCmd=(HshCmd) cls.newInstance(); // TODO cache instance or not
				return hshCmd.execute(hshContext, new ArrayList<String>(Arrays.asList(args)));
			} else {
				Class.forName(buildinClass).getMethod("main", new Class[]{ args.getClass()}).invoke(
					null, new Object[] { args });
				return 0;
			}
		}catch(final Exception e) {
			e.printStackTrace();
			return 1;
		}
	}
	/** This method parses the environment var PATH and
	 * places that list of directories in the class var path.
	 */
	private void parsePath() {
		String lpath=System.getenv().get("PATH");
		if(lpath==null)
			lpath=System.getProperty("PATH");
		System.out.println("PATH: "+lpath);

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

	/** Resolves a String to an executable file, using path
	 * @param string a command name
	 * @return absolute filename to the executable, or cmd if no executable was found
	 */
	private String resolveCmd(final String cmd) {
		final File f=new File(cmd);
		if(f.isAbsolute())
			return cmd;

		if(path==null)
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

	private static Map<String, String> init_predefines() {
		final Map<String, String> predefs=new HashMap<String, String>();
		predefs.put("exit",	"com.happypeople.hsh.exit.Exit");
		predefs.put("find",	"com.happypeople.hsh.find.Find");
		predefs.put("ls", 	"com.happypeople.hsh.ls.Ls");
		predefs.put("quit",	"com.happypeople.hsh.exit.Exit");
		predefs.put("tail",	"com.happypeople.hsh.tail.Main");
		return predefs;
	}

	// HshEnvirionment-Listener
	private void varChanged(final String name) {
		// throw away cache
		if("PATH".equals(name))
			path=null;
	}

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

	// end HshEnvirionment-Listener

}
