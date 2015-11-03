package com.happypeople.hsh.hsh;

import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.Reader;
import java.util.Map;

import org.apache.log4j.Logger;

import com.happypeople.hsh.HshContext;
import com.happypeople.hsh.HshEnvironment;
import com.happypeople.hsh.HshFDSet;
import com.happypeople.hsh.HshMessage;
import com.happypeople.hsh.HshMessageListener;
import com.happypeople.hsh.HshTerminal;
import com.happypeople.hsh.hsh.l1parser.L1Parser;
import com.happypeople.hsh.hsh.l1parser.L2TokenManager;
import com.happypeople.hsh.hsh.parser.ListNode;

import jline.console.ConsoleReader;

/** Happy Shell main.
 * The program reads lines from stdin and executes them.
 * Until stdin is closed or an exit-command is given.
 * "quit" does an "exit 0"
 * "exit <number>" exits with status <number>
 */
public class Hsh {
	private HshTerminal terminal;
	private final HshParser parser;
	private HshContext context;

	/** exit status of last command */
	private int status=0;

    private final static Logger log = Logger.getLogger(Hsh.class);

	public static void main(final String[] args) throws IOException {

		final ConsoleReader console = new ConsoleReader();
		console.setPrompt(">>> ");

		// connect console to Hsh
		final PipedWriter pWriter=new PipedWriter();
		final PipedReader pReader=new PipedReader();
		pWriter.connect(pReader);

		/** A ParserCallbackReader is used by an instance of Hsh to read input.
		 * The ParserCallbackReader created here reads lines from the console and
		 * hands them over to the Hsh instance.
		 * If this process is _not_ connected to console the ParserCallbackReader should
		 * read from Stdin or the like. (still not implemented)
		 **/
		final Reader hshIn=new ParserCallbackReader(pReader, new ParserCallbackReader.Callback() {
			@Override
			public void feedMe() throws IOException {
				log.info("feedMe called from parser");
				pWriter.write(console.readLine());
				pWriter.write("\n");
				log.info("sent a line from feedMe() to parser");
			}
		});

		/* now create the Hsh instance which input connected to the above created console. */
		final Hsh instance=new Hsh(hshIn);

		/* Create an adapter to console. */
		instance.setTerminal( new HshTerminal() {
				@Override
				public int getCols() {
					return console.getTerminal().getWidth();
				}

				@Override
				public int getRows() {
					return console.getTerminal().getHeight();
				}
			});

		try {
			/* run the rep-loop */
			instance.run();
			log.info("instance.run() returned, status="+instance.getStatus());
			System.exit(instance.getStatus());
		} catch (final Exception e) {
			log.error("fatal failure in Hsh.main(), will System.exit(1)", e);
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
	}

	/** this call runs the rep-loop until one of the executed commands set the finished() flag
	 * on the root context.
	 */
	private void run() {

		final HshContextBuilder contextBuilder=new HshContextBuilder();

		// Setup stdstream
		try(final HshFDSetImpl fdSet=new HshFDSetImpl()) {
			fdSet.setPipe(HshFDSet.STDIN, new HshPipeImpl(System.in));
			fdSet.setPipe(HshFDSet.STDOUT, new HshPipeImpl(System.out));
			fdSet.setPipe(HshFDSet.STDERR, new HshPipeImpl(System.err));

			context=contextBuilder.terminal(terminal).fdSet(fdSet).create();

			// copy the System properties into the environment
			final HshEnvironment env=context.getEnv();
			for(final Map.Entry<Object, Object> ent : System.getProperties().entrySet())
				env.setVariableValue(""+ent.getKey(), ""+ent.getValue());

			// setup listener for Finished-Messages
			final boolean[] finished= { false };
			context.addMsgListener(new HshMessageListener() {
				@Override
				public void msg(final HshMessage msg) {
					if(msg.getType()==HshMessage.Type.Finish) {
						finished[0]=true;
						parser.finish();
					}
				}
			});

			// TODO listCallback is only usefull in interactive mode
			parser.setListCallback(new HshParser.ListCallback() {
				@Override
				public void listParsed(final ListNode listNode) {
					log.debug("listParsed, image="+listNode.getImage());
					try {
						setStatus(listNode.doExecution(context));
					} catch (final Exception e) {
						e.printStackTrace();
						// TODO maybe reInit parser???
					}
				}
			});

			while(!finished[0]) {
				try {
					log.info("parsing complete command...");
					parser.complete_command();
					log.info("parsed complete command.");
				} catch (final ParseException e) {
					log.info("parse exception, abord", e);
				}
			}
		}catch(final IOException e) {
			log.error("finished root context caused by exception", e);
			throw new RuntimeException(e);
		}
	}

	private void setTerminal(final HshTerminal terminal) {
		this.terminal=terminal;
	}

	private int getStatus() {
		return status;
	}

	private void setStatus(final int status) {
		this.status=status;
	}
}
