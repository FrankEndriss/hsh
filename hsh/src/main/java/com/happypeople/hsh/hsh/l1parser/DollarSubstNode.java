package com.happypeople.hsh.hsh.l1parser;


public class DollarSubstNode extends ComplexL1Node {
	int parameterIdx=-1;
	int operatorIdx=-1;
	int wordIdx=-1;

	public void setParameter(final ComplexL1Node parts) {
		if(parameterIdx>=0)
			throw new RuntimeException("must not set parameter parts more than once");
		parameterIdx=add(parts);
	}

	public ComplexL1Node getParameter() {
		return (ComplexL1Node)get(parameterIdx);
	}

	public void setOperator(final L1Node operator) {
		if(operatorIdx>=0)
			throw new RuntimeException("must not set operator more than once");
		operatorIdx=add(operator);
	}

	/**
	 * @return null if not set, else the operator
	 */
	public L1Node getOperator() {
		return operatorIdx>=0?get(operatorIdx):null;
	}

	public void setWord(final ComplexL1Node parts) {
		if(wordIdx>=0)
			throw new RuntimeException("must not set word parts more than once");
		wordIdx=add(parts);
	}

	/**
	 * @return null if not set, else the word
	 */
	public ComplexL1Node getWord() {
		if(wordIdx<0)
			return null;
		return (ComplexL1Node)get(wordIdx);
	}
}
