package com.happypeople.hsh.hsh.l1parser;

import java.util.Iterator;

import com.happypeople.hsh.HshContext;


/** A L1Node which can and should be substituted to another String
 */
public interface SubstitutableL1Node extends L1Node {
	public Iterator<String> doSubstitution(HshContext env) throws ParseException;
}
