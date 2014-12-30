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
import com.happypeople.hsh.HshEnvironment;
import com.happypeople.hsh.HshExecutor;

/** Happy Shell main.
 * The program reads lines from stdin and executes them.
 * Until stdin is closed or an exit-command is given.
 * "quit" does an "exit 0"
 * "exit <number>" exits with status <number>
 */
public class Hsh implements HshContext {
	private static PrintWriter log;
	private final ConsoleReader console;
	private HshEnvironment env=new HshEnvironmentImpl(null);
	private HshExecutor executor=new HshExecutorImpl(this);

	/** exit status of last command */
	private int status=0;

	/** Flag indicating that this instance has finished working. ie "exit" was called. */
	private boolean finished=false;

	public static void main(final String[] args) throws IOException {

		//			log=new PrintWriter(new FileWriter("hsh.log"));
		log=new PrintWriter(System.err);

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

	/** Called throu main() only, its a singleton
	 * @param console the interactive screen
	 */
	private Hsh(final ConsoleReader console) {
		this.console=console;
		// copy the System properties into the environment
		for(Map.Entry<Object, Object> ent : System.getProperties().entrySet())
			getEnv().setVar(""+ent.getKey(), ""+ent.getValue());
	}

	private void feedLine(final String line) {
		if(finished)
			throw new IllegalStateException("This instance has finished work");

		final String[] tokenlist=parse_and_substitution(line);
		if(tokenlist.length>0)
			try {
				setStatus(getExecutor().execute(tokenlist));
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
		// TODO addhere connection to HshParser
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

	@Override
	public HshContext createChildContext() {
		return new HshChildContext(this);
	}

	@Override
	public HshEnvironment getEnv() {
		return env;
	}

	@Override
	public HshExecutor getExecutor() {
		return executor;
	}
}
