package com.happypeople.hsh.hsh;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.Map;

import jline.console.ConsoleReader;

import com.happypeople.hsh.HshContext;
import com.happypeople.hsh.HshEnvironment;
import com.happypeople.hsh.HshExecutor;
import com.happypeople.hsh.HshRedirections;
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
	private ConsoleReader console;
	private final HshEnvironment env=new HshEnvironmentImpl(null);
	private final HshExecutorImpl executor=new HshExecutorImpl(null, this, new HshRedirectionsImpl());
	private final HshParser parser;

	/** Reader with input */
	private final Reader in;

	/** exit status of last command */
	private int status=0;

	/** Flag indicating that this instance has finished working. ie "exit" was called. */
	private boolean finished=false;


	public static void main(final String[] args) throws IOException {

		//			log=new PrintWriter(new FileWriter("hsh.log"));
		log=new PrintWriter(System.err);

		final ConsoleReader console = new ConsoleReader();
		console.setPrompt(">>> ");

		// connect console to Hsh
		final PipedWriter pWriter=new PipedWriter();
		final PipedReader pReader=new PipedReader();
		pWriter.connect(pReader);
		final Reader hshIn=new ParserCallbackReader(pReader, new ParserCallbackReader.Callback() {
			@Override
			public void feedMe() throws IOException {
				pWriter.write(console.readLine());
				pWriter.write("\n");
			}
		});

		final Hsh instance=new Hsh(hshIn);
		instance.setConsole(console);

		try {
			instance.run();
			System.exit(instance.getStatus());
		} catch (final Exception e) {
			System.err.println("fatal failure in Hsh.main(), will System.exit(1)");
			e.printStackTrace(System.err);
			System.exit(1);
		}
	}

	/** Called throu main() only, its a singleton
	 * @param console the interactive screen
	 * @throws IOException
	 */
	private Hsh(final Reader in) {
		this.in=in;
		final L2TokenManager l2tm=new L2TokenManager(new L1Parser(in));
		this.parser=new HshParser(l2tm);
		this.parser.setRuleApplier(l2tm);
	}

	private void run() {

		// copy the System properties into the environment
		final HshEnvironment env=getEnv();
		for(final Map.Entry<Object, Object> ent : System.getProperties().entrySet())
			env.setVariableValue(""+ent.getKey(), ""+ent.getValue());

		// TODO listCallback is only usefull in interactive mode
		parser.setListCallback(new HshParser.ListCallback() {
			@Override
			public void listParsed(final ListNode listNode) {
				try {
					setStatus(listNode.doExecution(Hsh.this));
				} catch (final Exception e) {
					e.printStackTrace();
					// TODO maybe reInit parser???
				}
			}
		});

		while(!finished) {
			try {
				parser.complete_command();
				if(DEBUG)
					System.out.println("parsed complete command: ");
			} catch (final ParseException e) {
				e.printStackTrace();
			}
		}
	}

	private void setConsole(final ConsoleReader console) {
		this.console=console;
	}

	/** A call to this method causes the Hsh to exit after the current command did finish.
	 *
	 */
	@Override
	public void finish() {
		finished=true;
		// if interactive close stream to make parser return
		if(console!=null)
			try {
				in.close();
			} catch (final IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	private int getStatus() {
		return status;
	}

	private void setStatus(final int status) {
		this.status=status;
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
	public HshEnvironment getEnv() {
		return env;
	}

	@Override
	public HshExecutor getExecutor() {
		return executor;
	}

	@Override
	public HshContext createChildContext(final HshEnvironment env, final HshExecutor executor) {
		return new HshChildContext(this, env, executor);
	}

	@Override
	public HshContext createChildContext(final HshRedirections hshRedirections) {
		return new HshChildContext(this, null, new HshExecutorImpl(getExecutor(), this, hshRedirections));
	}
}
