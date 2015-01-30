package com.happypeople.hsh.hsh.l1parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.Reader;
import java.lang.ProcessBuilder.Redirect;
import java.util.List;

import com.happypeople.hsh.HshContext;
import com.happypeople.hsh.HshExecutor;
import com.happypeople.hsh.HshOutput;
import com.happypeople.hsh.HshRedirection;
import com.happypeople.hsh.HshRedirections;
import com.happypeople.hsh.hsh.HshRedirectionImpl;
import com.happypeople.hsh.hsh.NodeTraversal;

/** Abstraction of "$(...)" construct
 * May be used for Backticked, too. (?)
 * The part between the () are the children.
 */
public class DollarExecNode extends ComplexL1Node {

	public DollarExecNode(final ImageHolder imageHolder, final int off, final int len) {
		super(imageHolder, off, len);
	}

	@Override
	public L1Node transformSubstitution(final ImageHolder imageHolder, final HshContext context) throws Exception {
		// execute subtree and append grabbed output to imageHolder
		final HshRedirection stdOutRedir=new HshRedirectionImpl(Redirect.PIPE);
		final HshExecutor hshExecutor=context.getExecutor();
		final HshRedirections parentRedirections=hshExecutor.getRedirecions();
		final HshRedirections hshRedirections=parentRedirections.createChild(null, stdOutRedir, null);
		final HshContext lContext=context.createChildContext(hshRedirections);
		final PipedInputStream subIn=new PipedInputStream();
		final PipedOutputStream subOut=new PipedOutputStream();
		subOut.connect(subIn);
		stdOutRedir.setOut(new HshOutput(subOut));

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					NodeTraversal.executeSubtree(get(0), lContext);
				} catch (final Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					try {
						subOut.close();
					} catch (final IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}).start();

		final int off=imageHolder.getLen();
		final Reader subReader=new BufferedReader(new InputStreamReader(subIn), 1024);
		final char[] buf=new char[1024];
		int c;
		while((c=subReader.read(buf))>0)
			imageHolder.append(buf, 0, c);

		return new SimpleL1Node(imageHolder, off, imageHolder.getLen()-off);
	}

	@Override
	public List<? extends L1Node> transformSplit(final HshContext context) {
		throw new RuntimeException("should be substituted before split");
	}

	@Override
	public void appendUnquoted(final StringBuilder sb) {
		sb.append(getImage());
	}

	@Override
	public L1Node copySubtree() {
		final DollarExecNode ret=new DollarExecNode(getImageHolder(), getOff(), getLen());
		ret.add(get(0).copySubtree());
		return ret;
	}

}
