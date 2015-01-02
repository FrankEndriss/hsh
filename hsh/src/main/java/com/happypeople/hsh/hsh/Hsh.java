package com.happypeople.hsh.hsh;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Map;

import jline.console.ConsoleReader;

import com.happypeople.hsh.HshContext;
import com.happypeople.hsh.HshEnvironment;
import com.happypeople.hsh.hsh.l1parser.L1Parser;
import com.happypeople.hsh.hsh.l1parser.L2TokenManager;
import com.happypeople.hsh.hsh.parser.ListNode;

/** Happy Shell main.
 * The program reads lines from stdin and executes them.
 * Until stdin is closed or an exit-command is given.
 * "quit" does an "exit 0"
 * "exit <number>" exits with status <number>
 */
public class Hsh implements HshContext {
	private final static boolean DEBUG=false;

	private static PrintWriter log;
	private final ConsoleReader console;
	private final HshEnvironment env=new HshEnvironmentImpl(null);
	private final HshExecutorImpl executor=new HshExecutorImpl(this);
	private final PipedReader parserPipeReader=new PipedReader();
	private final PipedWriter parserPipeWriter;
	private final HshParser parser;

	/** exit status of last command */
	private int status=0;

	/** Flag indicating that this instance has finished working. ie "exit" was called. */
	private boolean finished=false;

	public static void main(final String[] args) throws IOException {

		//			log=new PrintWriter(new FileWriter("hsh.log"));
		log=new PrintWriter(System.err);

		final ConsoleReader console = new ConsoleReader();
		console.setPrompt(">>> ");

		final Hsh instance=new Hsh(console);

		String line;
		try {
			while(instance.acceptsFeed() && (line=console.readLine())!=null) {
				instance.feedLine(line);
			}
			System.exit(0);
		} catch (final Exception e) {
			System.err.println("fatal failure in Hsh.main(), will System.exit(1)");
			e.printStackTrace(System.err);
			System.exit(1);
		}
		System.exit(instance.getStatus());
	}

	/** Called throu main() only, its a singleton
	 * @param console the interactive screen
	 * @throws IOException
	 */
	private Hsh(final ConsoleReader console) throws IOException {
		this.console=console;
		// TODO need to create recursive Loop from parserPipeReader, so that
		// it calls "getMoreInputFromConsole" whenever the parser/tokenizer
		// requests more input.
		// This will cause the listParsed()-callback to be called after every line,
		// and the getMoreInputFromConsole() whenever more input is needed.
		// The Prompt can be set according to the states of these two callbacks.
		parserPipeWriter=new PipedWriter(parserPipeReader);
		parser=new HshParser(new L2TokenManager(new L1Parser(parserPipeReader)));

		// copy the System properties into the environment
		final HshEnvironment env=getEnv();
		for(final Map.Entry<Object, Object> ent : System.getProperties().entrySet())
			env.setVariableValue(""+ent.getKey(), ""+ent.getValue());

		// run parser in separate Thread
		new Thread() {
			@Override
			public void run() {
				try {
					parserLoop();
				} catch (final ParseException e) {
					e.printStackTrace();
					// TODO: reInit parser and go on
					System.exit(1);
				}
			}
		}.start();
	}

	private void feedLine(final String line) throws IOException {
		if(finished)
			throw new IllegalStateException("This instance has finished work");

		parserPipeWriter.write(line);
		parserPipeWriter.write("\n");
	}

	private void parserLoop() throws ParseException {
		parser.setListCallback(new HshParser.ListCallback() {
			@Override
			public void listParsed(final ListNode list) {
				// TODO set status to denote something gets executed
				setStatus(getExecutor().execute(list));
				// TODO set status to denote something was executed
				// TODO restore prompt here
			}
		});

		while(true) {
			parser.complete_command();
			if(DEBUG)
				System.out.println("parsed complete command: ");
		}
	}

	private boolean acceptsFeed() {
		return !finished;
	}

	/** A call to this method causes the Hsh to exit after the current command did finish.
	 *
	 */
	@Override
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

	@Override
	public PrintStream getStdOut() {
		return System.out;
	}

	@Override
	public InputStream getStdIn() {
		return System.in;
	}

	@Override
	public PrintStream getStdErr() {
		return System.err;
	}

	@Override
	public int getCols() {
		return console.getTerminal().getWidth();
	}

	@Override
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

	public HshExecutorImpl getExecutor() {
		return executor;
	}
}
