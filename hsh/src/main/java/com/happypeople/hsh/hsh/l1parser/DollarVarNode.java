package com.happypeople.hsh.hsh.l1parser;

import java.io.IOException;

import com.happypeople.hsh.HshContext;

public class DollarVarNode extends TokenNode implements Substitutable {
	DollarVarNode(final Token t) {
		super(t);
	}

	@Override
	public String getSubstitutedString(final HshContext context) throws IOException {
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
		return "special";
	}
}
