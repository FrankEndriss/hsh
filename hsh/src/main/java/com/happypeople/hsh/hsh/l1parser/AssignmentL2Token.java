package com.happypeople.hsh.hsh.l1parser;

import com.happypeople.hsh.hsh.HshParserConstants;
import com.happypeople.hsh.hsh.L2Token;

/** AssignmentL2Token is a assignment()
 * It has three children:
 * -SimpleL1Node(varname)
 * -SimpleL1Node(<EQUALS>)
 * -optional word_part_list()
 */
public class AssignmentL2Token extends L2Token {
	public AssignmentL2Token() {
		super(HshParserConstants.ASSIGNMENT_WORD, "AssignmentL2Token");
	}
}
