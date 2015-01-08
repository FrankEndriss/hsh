package com.happypeople.hsh.hsh.syntaxparser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import com.happypeople.hsh.hsh.l0parser.DNode;
import com.happypeople.hsh.hsh.l0parser.L0Parser;
import com.happypeople.hsh.hsh.l0parser.ParseException;
import com.happypeople.hshutil.util.AsyncIterator;


/** TokenManager for the SyntaxParser.
 * It takes the tree produced by L0Parser and creates a stream of syntaxparser.Token from it.
 */
public class L0Bridge implements TokenManager, Runnable {
	private final AsyncIterator<Token> tokenstream=new AsyncIterator<Token>();
	private final L0Parser parser;
	private final ExecutorService executor;

	public L0Bridge(final L0Parser parser, final ExecutorService executor) {
		this.parser=parser;
		this.executor=executor;
	}

	@Override
	public void run() {
		final AsyncIterator<DNode> l0stream=new AsyncIterator<DNode>();
		executor.submit(new Runnable() {
			@Override
			public void run() {
				try {
					parser.parseAsync(l0stream);
				} catch (final ParseException e) {
					e.printStackTrace();
				}
			}
		});

		final DNode root=l0stream.next();

		final List<Future<Iterator<Token>>> jobs=new ArrayList<Future<Iterator<Token>>>();
		for(final DNode topLevelChild : root) {
			jobs.add(executor.submit(new Callable<Iterator<Token>>() {
				@Override
				public Iterator<Token> call() throws Exception {
					final AsyncIterator<Token> iter=new AsyncIterator<Token>();
					topLevelChild.parse(iter, executor);
					return iter;
				}
			}));
		}

		// wait for the jobs to finish in order,
		// and put the created Token in order into the tokenstream
		for(final Future<Iterator<Token>> job : jobs)
			try {
				final Iterator<Token> iter=job.get();
				while(iter.hasNext())
					tokenstream.offer(iter.next());
			} catch (final InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (final ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		// here the complete input should be parsed since all recursive jobs have finished
	}

	@Override
	public Token getNextToken() {
		return tokenstream.next();
	}

}
