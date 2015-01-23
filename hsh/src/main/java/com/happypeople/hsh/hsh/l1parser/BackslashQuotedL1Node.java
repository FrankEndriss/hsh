package com.happypeople.hsh.hsh.l1parser;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import com.happypeople.hsh.HshContext;
import com.happypeople.hsh.hsh.L2Token;

/** A char escaped by an backslash. Because thats allways one char, the len is fixed to two chars.
 */
public class BackslashQuotedL1Node extends AbstractL1Node implements QuotedL1Node {
	public BackslashQuotedL1Node(final L2Token tok, final int off, final int len) {
		super(tok, off, 2);
		if(len!=2)
			throw new RuntimeException("len of escaped char !=2...something went wrong. :/");
	}

	@Override
	public Iterator<L1Node> iterator() {
		return Collections.EMPTY_LIST.iterator();
	}

	@Override
	public L1Node transformSubstitution(final L2Token imageHolder, final HshContext context) throws Exception {
		final BackslashQuotedL1Node node=new BackslashQuotedL1Node(imageHolder, imageHolder.getLen(), getLen());
		imageHolder.append(getImage());
		return node;
	}

	@Override
	public Collection<? extends L1Node> transformSplit(final HshContext context) {
		// a backslashed char cannot be a IFS separator, because it is quoted, so no split is done
		return Arrays.asList(this);
	}

	@Override
	public void appendUnquoted(final StringBuilder sb) {
		sb.append(getImage().charAt(1));
	}
}
