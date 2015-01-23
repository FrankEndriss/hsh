package com.happypeople.hsh.hsh.l1parser;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import com.happypeople.hsh.HshContext;
import com.happypeople.hsh.HshEnvironment;
import com.happypeople.hsh.hsh.L2Token;

/** The Token contained in a DollarVarNode is $<token>
 * if token.kind==DO_SPECIAL it is one of the single-character defined in "special parameters".
 * Else token.image is simply the variable name.
 */
public class DollarVarNode extends AbstractL1Node {
	private final Token t;
	DollarVarNode(final L2Token tok, final int off, final int len, final Token t) {
		super(tok, off, len);
		this.t=t;
	}

	public Token getToken() {
		return t;
	}

	@Override
	public L1Node transformSubstitution(final L2Token imageHolder, final HshContext context) throws Exception {
		final String s=getSubstitutedString(context);
		final SimpleL1Node node=new SimpleL1Node(imageHolder, imageHolder.getLen(), s.length());
		imageHolder.append(s);
		return node;
	}

	private String getSubstitutedString(final HshContext context) throws IOException {
		String val=null;
		switch(getToken().kind) {
		case L1ParserConstants.DO_SPECIAL:
			val=getSpecialVal(getToken().image.charAt(0), context);
		case L1ParserConstants.DO_MULTI:
			val=context.getEnv().getVariableValue(getToken().image);
			break;
		default:
			throw new RuntimeException("bad token kind in DollarVarNode, kind="+getToken().kind);
		}
		return val==null?"":val;
	}

	private String getSpecialVal(final char c, final HshContext context) {
		switch(c) {
/*
		case '0':
		case '1':
		case '2':
		case '3':
		case '4':
		case '5':
		case '6':
		case '7':
		case '8':
		case '9':
		case '?':
		case '_':
		case '$':
		case '!':
			val=context.getEnv().getVariableValue(new StringBuilder(c).toString());
			break;
*/
		case '@':
			return list_positionals(context, 0);
		case '*':
			return list_positionals(context, 1);
		case '#':
			return ""+context.getEnv().getPositionalCount();
		default:
			return context.getEnv().getVariableValue(new StringBuilder(c).toString());
		}
	}

	private String list_positionals(final HshContext context, final int startAt) {
		final HshEnvironment env=context.getEnv();
		final StringBuilder sb=new StringBuilder();

		// TODO this is a simple implementation, does not take care if variables contain blanks
		for(int i=startAt; i<env.getPositionalCount(); i++) {
			String val=env.getVariableValue(""+i);
			if(val==null)
				val="";
			if(sb.length()>0)
				sb.append(' ');
			sb.append(val);
		}
		return sb.toString();
	}

	@Override
	public Iterator<L1Node> iterator() {
		return Collections.EMPTY_LIST.iterator();
	}

	@Override
	public void dump(final int level) {
		final StringBuilder sb=new StringBuilder();
		for(int i=0; i<level; i++)
			sb.append('\t');
		sb.append(getClass().getName()+": $"+getToken().image);
		System.out.println(""+sb);
	}

	@Override
	public Collection<? extends L1Node> transformSplit(final HshContext context) {
		throw new RuntimeException("split has to be done after substitution");
	}

	@Override
	public void appendUnquoted(final StringBuilder sb) {
		throw new RuntimeException("unquote has to be done after split and substitution");
	}
}
