package com.happypeople.hsh.hsh;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.ProcessBuilder.Redirect;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import jline.console.ConsoleReader;

import com.happypeople.hsh.HshCmd;
import com.happypeople.hsh.HshContext;

/** Happy Shell main.
 * The program reads lines from stdin and executes them.
 * Until stdin is closed or an exit-command is given.
 * "quit" does an "exit 0"
 * "exit <number>" exits with status <number>
 */
public class Hsh implements HshContext {
	private final static Map<String, String> predefs=init_predefines();
	private static PrintWriter log;
	private static Collection<File> path=new ArrayList<File>();
	private final ConsoleReader console;

	/** exit status of last command */
	private int status=0;

	/** Flag indicating that this instance has finished working. ie "exit" was called. */
	private boolean finished=false;

	public static void main(final String[] args) throws IOException {

		//			log=new PrintWriter(new FileWriter("hsh.log"));
		log=new PrintWriter(System.err);

		parsePath();

		final ConsoleReader console = new ConsoleReader();
		console.setPrompt("> ");

		final Hsh instance=new Hsh(console);

		String line;
		try {
			while(instance.acceptsFeed() && (line=console.readLine())!=null) {
				instance.feedLine(line);
				//log.println("cmd-line:>"+line+"<");
				//log.flush();
			}
		} catch (final Exception e) {
			System.err.println("fatal failure in Hsh.main(), will System.exit(1)");
			e.printStackTrace(System.err);
			System.exit(1);
		}
		System.exit(instance.getStatus());
	}

	private Hsh(final ConsoleReader console) {
		this.console=console;
	}

	private void feedLine(final String line) {
		if(finished)
			throw new IllegalStateException("This instance has finished work");

		final String[] tokenlist=parse_and_substitution(line);
		if(tokenlist.length>0)
			try {
				setStatus(execute(tokenlist));
			} catch (final Exception e) {
				System.err.println("failure while cmd execution");
				e.printStackTrace(System.err);
			}
	}

	private boolean acceptsFeed() {
		return !finished;
	}

	/** A call to this method causes the Hsh to exit after the current command did finish.
	 *
	 */
	public void finish() {
		finished=true;
	}

	private int getStatus() {
		return status;
	}

	private void setStatus(final int status) {
		this.status=status;
	}

	/** This method parses, substitutes and splits the command line line.
	 * @param line cmd-line as typed by the user
	 * @return tokenized line
	 */
	private String[] parse_and_substitution(String line) {
		// TODO real implementation
		// -handle splitted lines, which end with \
		// -handle cmd separations like ";"
		// -handle explicit separated tokens like "foo bar" and "foo""bar"
		// -handle variable asignments like "x=foo"
		// -substitute variables like $x
		// -handle hsh invocations like "hsh -c "x=foo ; cat $(x)"
		// -handle hsh invocations like "( x=foo ; cat $(x) )"
		// -substitute executions like $(x)
		// -handle pipes like "ls|grep foo"
		// -parse for loop
		// -parse while loop
		// -handle redirections like "grep foo <bar"
		// -run cmds in background

		// simple implementation
		line=line.trim();
		if(line.length()<1)
			return new String[0];
		return line.split("\\s"); // split by whitespace
	}

	private int execute(final String[] token) throws Exception {
		if(token.length<1)
			return 0;	// empty line

		final String buildin=predefs.get(token[0]);
		if(buildin!=null)
			return exec_buildin_Main(buildin, token);
		else
			return exec_extern_synchron(token);
	}

	/** Executes the cmd line given in args.
	 * args[0] is the command to execute.
	 * $PATH is resolved to find that program.
	 * @param args
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private static int exec_extern_synchron(final String[] args) {
		// TODO handle piped commands and stdstream redirection
		try {
			final ProcessBuilder builder=new ProcessBuilder();
			args[0]=resolveCmd(args[0]);
			builder.command(Arrays.asList(args));
			builder.redirectError(Redirect.INHERIT);
			builder.redirectOutput(Redirect.INHERIT);
			builder.redirectInput(Redirect.INHERIT);

			final Process p=builder.start();
			p.waitFor();
			return p.exitValue();
		}catch(final Exception e) {
			e.printStackTrace(System.err);
			return -1;
		}
	}

	/** Executes the main of buildinClass
	 * @param buildinClass
	 * @param args
	 * @throws ClassNotFoundException
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	private int exec_buildin_Main(final String buildinClass, final String[] args) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException {
		try {
			final Class<?> cls=Class.forName(buildinClass);
			if(HshCmd.class.isAssignableFrom(cls)) { // cls implements HshCmd
				final HshCmd hshCmd=(HshCmd) cls.newInstance(); // TODO cache instance
				return hshCmd.execute(this, new ArrayList<String>(Arrays.asList(args)));
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

	private static Map<String, String> init_predefines() {
		final Map<String, String> predefs=new HashMap<String, String>();
		predefs.put("find",	"com.happypeople.hsh.find.Main");
		predefs.put("ls", 	"com.happypeople.hsh.ls.Ls");
		predefs.put("tail",	"com.happypeople.hsh.tail.Main");
		predefs.put("exit",	"com.happypeople.hsh.exit.Main");
		predefs.put("quit",	"com.happypeople.hsh.exit.Main");
		return predefs;
	};


	/** This method parses the environment var PATH and
	 * places that list of directories in the class var path.
	 */
	private static void parsePath() {
		String lpath=System.getenv().get("PATH");
		if(lpath==null)
			lpath=System.getProperty("PATH");
		System.out.println("PATH: "+lpath);

		final Collection<String> lpaths=Arrays.asList(lpath.split(File.pathSeparator));
		for(final String p : lpaths) {
			final File f=new File(p);
			if(f.isDirectory()) {
				path.add(f);
				System.out.println("adding path: "+f);
			}
		}
	}

	/** Resolves a String to an executable file, using path
	 * @param string a command name
	 * @return File to the executable
	 */
	private static String resolveCmd(final String cmd) {
		final File f=new File(cmd);
		if(f.isAbsolute())
			return cmd;

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

	public PrintStream getStdOut() {
		return System.out;
	}

	public InputStream getStdIn() {
		return System.in;
	}

	public PrintStream getStdErr() {
		return System.err;
	}

	public int getCols() {
		return console.getTerminal().getWidth();
	}

	public int getRows() {
		return console.getTerminal().getHeight();
	}
}
