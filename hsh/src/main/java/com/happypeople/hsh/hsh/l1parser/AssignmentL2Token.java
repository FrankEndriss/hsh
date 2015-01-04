package com.happypeople.hsh.hsh.l1parser;

/** AssignmentL1Node is the left-hand side of a assignment.
 * i.e. "x="
 * It has two children, first is the left-hand side, second the equals-sign.
 */
public class AssignmentL1Node extends ComplexL1Node {
	/**
	 * @return the variable name (left hand side of assignment)
	 */
	public L1Node getVarname() {
		return get(0);
	}
}
