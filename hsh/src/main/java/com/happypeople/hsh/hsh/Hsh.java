package com.happypeople.hsh.hsh;

import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.Reader;
import java.util.Map;

import jline.console.ConsoleReader;

import org.apache.log4j.Logger;

import com.happypeople.hsh.HshContext;
import com.happypeople.hsh.HshEnvironment;
import com.happypeople.hsh.HshFDSet;
import com.happypeople.hsh.HshTerminal;
import com.happypeople.hsh.hsh.l1parser.L1Parser;
import com.happypeople.hsh.hsh.l1parser.L2TokenManager;
import com.happypeople.hsh.hsh.parser.ListNode;

/** Happy Shell main.
 * The program reads lines from stdin and executes them.
 * Until stdin is closed or an exit-command is given.
 * "quit" does an "exit 0"
 * "exit <number>" exits with status <number>
 */
public class Hsh {
	private final static boolean DEBUG=false;

	private ConsoleReader console;
	//private final HshEnvironment env=new HshEnvironmentImpl(null);
	//private final HshExecutorImpl executor=new HshExecutorImpl(null, this, new HshRedirectionsImpl());
	private final HshParser parser;
	private HshContext context;

	/** exit status of last command */
	private int status=0;

	/** Flag indicating that this instance has finished working. ie "exit" was called. */
    private final static Logger logger = Logger.getLogger(Hsh.class);

	public static void main(final String[] args) throws IOException {

		final ConsoleReader console = new ConsoleReader();
		console.setPrompt(">>> ");

		// connect console to Hsh
		final PipedWriter pWriter=new PipedWriter();
		final PipedReader pReader=new PipedReader();
		pWriter.connect(pReader);

		/** A ParserCallbackReader is used by an instance of Hsh to read input.
		 * The ParserCallbackReader created here reads lines from the console and
		 * hands them over to ths Hsh instance.
		 * If this process is _not_ connected to console the ParserCallbackReader should
		 * read from Stdin or the like. (still not implemented)
		 **/
		final Reader hshIn=new ParserCallbackReader(pReader, new ParserCallbackReader.Callback() {
			@Override
			public void feedMe() throws IOException {
				pWriter.write(console.readLine());
				pWriter.write("\n");
			}
		});

		/* now create the Hsh instance which input connected to the above created console. */
		final Hsh instance=new Hsh(hshIn);

		/* note that this call to setConsole() does _not_ set the input. The set console is used for
		 * calls like getColumnCount() and the like.
		 */
		instance.setConsole(console);

		try {
			/* run the rep-loop */
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
		final L2TokenManager l2tm=new L2TokenManager(new L1Parser(in));
		this.parser=new HshParser(l2tm);
		// TODO delete the ruleApplier, it is not needed
		this.parser.setRuleApplier(l2tm);
	}

	/** this call runs the rep-loop until one of the executed commands set the finished() flag
	 * on the root context.
	 */
	private void run() {

		final HshContextBuilder contextBuilder=new HshContextBuilder();
		final HshFDSetImpl fdSet=new HshFDSetImpl(null);
		// Setup stdstream
		fdSet.setInput(HshFDSet.STDIN, new HshPipeImpl(System.in));
		fdSet.setOutput(HshFDSet.STDOUT, new HshPipeImpl(System.out));
		fdSet.setOutput(HshFDSet.STDERR, new HshPipeImpl(System.err));

		context=contextBuilder.terminal(new HshTerminal() {
			@Override
			public int getCols() {
				return console.getTerminal().getWidth();
			}

			@Override
			public int getRows() {
				return console.getTerminal().getHeight();
			}
		}).fdSet(fdSet).create();

		// copy the System properties into the environment
		final HshEnvironment env=context.getEnv();
		for(final Map.Entry<Object, Object> ent : System.getProperties().entrySet())
			env.setVariableValue(""+ent.getKey(), ""+ent.getValue());

		// TODO listCallback is only usefull in interactive mode
		parser.setListCallback(new HshParser.ListCallback() {
			@Override
			public void listParsed(final ListNode listNode) {
				logger.debug("listParsed, image="+listNode.getImage());
				try {
					setStatus(listNode.doExecution(context));
				} catch (final Exception e) {
					e.printStackTrace();
					// TODO maybe reInit parser???
				}
			}
		});

		while(!context.isFinish()) {
			try {
				parser.complete_command();
				logger.debug("parsed complete command.");
			} catch (final ParseException e) {
				logger.debug("parse exception", e);
			}
		}
	}

	private void setConsole(final ConsoleReader console) {
		this.console=console;
	}

	private int getStatus() {
		return status;
	}

	private void setStatus(final int status) {
		this.status=status;
	}
}
