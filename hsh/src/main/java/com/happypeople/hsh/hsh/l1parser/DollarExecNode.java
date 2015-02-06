package com.happypeople.hsh.hsh.l1parser;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import com.happypeople.hsh.HshContext;
import com.happypeople.hsh.HshFDSet;
import com.happypeople.hsh.HshPipe;
import com.happypeople.hsh.hsh.HshContextBuilder;
import com.happypeople.hsh.hsh.HshFDSetImpl;
import com.happypeople.hsh.hsh.HshPipeImpl;
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
		final HshFDSetImpl fdSet=new HshFDSetImpl(context.getFDSet());
		final HshPipe hshPipe=new HshPipeImpl();
		fdSet.setOutput(HshFDSet.STDOUT, hshPipe);

		final HshContext lContext=new HshContextBuilder().parent(context).fdSet(fdSet).create();

		/*
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					NodeTraversal.executeSubtree(get(0), lContext);
				} catch (final Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					hshPipe.getOutputStream().close();
				}
			}
		}).start();
		*/

					NodeTraversal.executeSubtree(get(0), lContext);
					lContext.getStdOut().close();

		final int off=imageHolder.getLen();
		final Reader subReader=new InputStreamReader(hshPipe.getInputStream());
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
