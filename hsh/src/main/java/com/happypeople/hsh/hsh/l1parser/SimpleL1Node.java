package com.happypeople.hsh.hsh.l1parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.happypeople.hsh.HshContext;
import com.happypeople.hsh.hsh.L2Token;


/** SimpleL1Node is a node with simple text. The text is interpreted to have no special meaning.
 * So, that can be text with really no special meaning, or text which has not special meaning in
 * the context where it is found.
 */
public class SimpleL1Node extends AbstractL1Node implements L1Node, StringifiableNode {

	private final int l1Kind;

	public SimpleL1Node(final L2Token tok, final int off, final int len) {
		this(tok, off, len, -1);
	}

	public SimpleL1Node(final L2Token tok, final int off, final int len, final int l1Kind) {
		super(tok, off, len);
		this.l1Kind=l1Kind;
	}

	@Override
	public Iterator<L1Node> iterator() {
		return Collections.emptyIterator();
	}

	@Override
	public void append(final StringBuilder sb) {
		sb.append(getImage());
	}

	/**
	 * @return the L1 token.kind of this word part
	 */
	public int getL1Kind() {
		return l1Kind;
	}

	@Override
	public L1Node transformSubstitution(final L2Token imageHolder, final HshContext context) throws Exception {
		final SimpleL1Node node=new SimpleL1Node(imageHolder, imageHolder.getLen(), getLen());
		imageHolder.append(getImage());
		return node;
	}

	@Override
	public Collection<? extends L1Node> transformSplit(final HshContext context) {
		// This is the real implementation of L1Node splitting

		final String input=getImage();
		if(input.length()==0)
			return Arrays.asList(this);

		final String ifs=context.getEnv().getVariableValue("IFS");
		if(ifs==null || ifs.length()==0)
			return Arrays.asList(this);

		// TODO move Pattern compiling to HshEnvironmentImpl
		final StringBuilder ifsWSsb=new StringBuilder();
		for(final char c : ifs.toCharArray()) {
			if(c==' ' || c=='\t' || c=='\n')
				ifsWSsb.append(c);
		}

		int startIdx=0;
		int endIdx=input.length();

		final List<L1Node> ret=new ArrayList<L1Node>();
		final String ifsWS=ifsWSsb.toString();
		if(ifsWS.length()>0) {
			// find indexes of leading and trailing WS, because if found these are returned
			// as an empty field
			while(startIdx<input.length() && ifsWS.indexOf(input.charAt(startIdx))>=0)
				startIdx++;
			if(startIdx>0) // found leading WS
				ret.add(new SimpleL1Node(getImageHolder(), 0, 0));

			while(endIdx>startIdx && ifsWS.indexOf(input.charAt(endIdx))>=0)
				endIdx--;
		}

		if(input.length()>0) {
			final int IN_FS=1;
			final int IN_MATCH=2;
			// dont use a java.regex.Pattern since there is a lot of overhead
			final String FS=ifs;

			int state=IN_FS;
			int lastStart=-42;	// initialization here is irrelevant
			for(int i=startIdx; i<endIdx; i++) {
				if(FS.indexOf(input.charAt(i))>=0) {
					if(state==IN_MATCH) {
						ret.add(new SimpleL1Node(getImageHolder(), lastStart, i));
						state=IN_FS;
					}
				} else {
					if(state==IN_FS) {
						lastStart=i;
						state=IN_MATCH;
					}
				}
			}
			if(state==IN_MATCH)
				ret.add(new SimpleL1Node(getImageHolder(), lastStart, endIdx));
		}

		if(endIdx>startIdx && endIdx<input.length()) // found trailing WS
			ret.add(new SimpleL1Node(getImageHolder(), 0, 0));

		return ret;
	}

	@Override
	public void appendUnquoted(final StringBuilder sb) {
		sb.append(getImageHolder().image.substring(getOff(), getOff()+getLen()));
	}

}
