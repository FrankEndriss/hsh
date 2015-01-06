package com.happypeople.hsh.hsh.l1parser;

import com.happypeople.hsh.HshContext;
import com.happypeople.hsh.hsh.NodeTraversal;


public class DollarSubstNode extends ComplexL1Node implements Substitutable {
	int parameterIdx=-1;
	int operatorIdx=-1;
	int wordIdx=-1;

	public void setParameter(final L1Node parts) {
		if(parameterIdx>=0)
			throw new RuntimeException("must not set parameter parts more than once");
		parameterIdx=add(parts);
	}

	public L1Node getParameter() {
		return get(parameterIdx);
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

	public void setWord(final L1Node parts) {
		if(wordIdx>=0)
			throw new RuntimeException("must not set word parts more than once");
		wordIdx=add(parts);
	}

	/**
	 * @return null if not set, else the word
	 */
	public L1Node getWord() {
		if(wordIdx<0)
			return null;
		return get(wordIdx);
	}

	@Override
	public String getSubstitutedString(final HshContext env) throws Exception {
		final L1Node variable=getParameter();
		// if the variable name contains substitutions itself (i.e. "${${x}}"), substitute them now
		final String varName=NodeTraversal.substituteSubtree(variable, env);
		final String value=env.getEnv().getVariableValue(varName);
		if(getOperator()!=null)
			throw new RuntimeException("substitution with operator not implemented");
		return value;
	}
}
