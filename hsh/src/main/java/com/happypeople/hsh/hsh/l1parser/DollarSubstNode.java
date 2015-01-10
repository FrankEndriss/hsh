package com.happypeople.hsh.hsh.l1parser;

import java.util.HashMap;
import java.util.Map;

import com.happypeople.hsh.HshContext;
import com.happypeople.hsh.hsh.NodeTraversal;


/** See http://pubs.opengroup.org/onlinepubs/009695399/utilities/xcu_chap02.html#tag_02_06_02
 * Chapter "2.6.2 Parameter Expansion"
 *
 */
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
		// Note that this is not Posix, (and also per 2015-01-10 this does not parse)
		final String varName=NodeTraversal.substituteSubtree(variable, env);
		final L1Node operatorNode=getOperator();
		if(operatorNode!=null) {
			// if the operator contains substitutions itself (i.e. "${x${op}hello}"), substitute them now
			// Note that this is not Posix, (and also per 2015-01-10 this does not parse)
			final Operator operator=operatorMap.get(NodeTraversal.substituteSubtree(operatorNode, env));
			if(operator==null)
				throw new RuntimeException("operator in DollarSubstNode unknown: "+operatorNode);
			else
				return operator.doSubst(varName, getWord(), env);
		} else
			return env.getEnv().getVariableValue(varName);
	}

	private static Map<String, Operator> operatorMap=new HashMap<String, Operator>();

	private enum Case {
		SET_AND_NOT_NULL,
		SET_BUT_NULL,
		UNSET
	};

	private static abstract class Operator {
		abstract String doSubst(String variable, L1Node word, HshContext context) throws Exception;
	}

	static {
		operatorMap.put(":-", new Operator() { // "Use Default Values" - operator
			@Override
			String doSubst(final String variable, final L1Node word, final HshContext context) throws Exception {
				if(context.getEnv().issetParameter(variable)) {
					final String value=context.getEnv().getVariableValue(variable);
					if(value!=null)
						return value;
				}
				return NodeTraversal.substituteSubtree(word, context);
			}
		});

		operatorMap.put("-", new Operator() {
			@Override
			String doSubst(final String variable, final L1Node word, final HshContext context) throws Exception {
				if(context.getEnv().issetParameter(variable))
					return context.getEnv().getVariableValue(variable);
				else
					return NodeTraversal.substituteSubtree(word, context);
			}
		});

		operatorMap.put(":=", new Operator() { // "Assign Default Values" - operator
			@Override
			String doSubst(final String variable, final L1Node word, final HshContext context) throws Exception {
				if(context.getEnv().issetParameter(variable)) {
					final String value=context.getEnv().getVariableValue(variable);
					if(value!=null)
						return value;
				}
				final String value=NodeTraversal.substituteSubtree(word, context);
				context.getEnv().setVariableValue(variable, value);
				return value;
			}
		});

		operatorMap.put("=", new Operator() {
			@Override
			String doSubst(final String variable, final L1Node word, final HshContext context) throws Exception {
				if(context.getEnv().issetParameter(variable)) {
					return context.getEnv().getVariableValue(variable);
				}
				final String value=NodeTraversal.substituteSubtree(word, context);
				context.getEnv().setVariableValue(variable, value);
				return value;
			}
		});

		operatorMap.put(":?", new Operator() { // "Indicate Error if Null or Unset" - operator
			@Override
			String doSubst(final String variable, final L1Node word, final HshContext context) throws Exception {
				if(context.getEnv().issetParameter(variable)) {
					final String value=context.getEnv().getVariableValue(variable);
					if(value!=null)
						return value;
				}
				throw new HshExit();
			}
		});

		operatorMap.put("?", new Operator() {
			@Override
			String doSubst(final String variable, final L1Node word, final HshContext context) throws Exception {
				if(context.getEnv().issetParameter(variable))
					return context.getEnv().getVariableValue(variable);
				throw new HshExit();
			}
		});

		operatorMap.put(":+", new Operator() { // "Use Alternative Value" - operator
			@Override
			String doSubst(final String variable, final L1Node word, final HshContext context) throws Exception {
				if(context.getEnv().getVariableValue(variable)!=null)
					return NodeTraversal.substituteSubtree(word, context);
				return null;
			}
		});

		operatorMap.put("+", new Operator() {
			@Override
			String doSubst(final String variable, final L1Node word, final HshContext context) throws Exception {
				if(context.getEnv().issetParameter(variable))
					return NodeTraversal.substituteSubtree(word, context);
				return null;
			}
		});

		operatorMap.put("##", new Operator() { // "Remove Largest Prefix Pattern" - operator
			@Override
			String doSubst(final String variable, final L1Node word, final HshContext context) throws Exception {
				if(context.getEnv().issetParameter(variable)) {
					final String paramValue=context.getEnv().getVariableValue(variable);
					if(paramValue==null)
						return null;
					final String pattern=NodeTraversal.substituteSubtree(word, context);
					// Note that pattern is a Posix pattern:
					// TODO parse pattern
					// TODO apply patter by
					// 	TODO translate pattern to java Pattern
					// 	TODO use java Pattern to match the prefix

					// For now treat pattern as a simple String
					if(paramValue.startsWith(pattern))
						return paramValue.substring(pattern.length());
					else
						return paramValue;
				} else
					return null;
			}
		});
		// TODO Since patterns are not implemented it does not make a difference if shortes or longtest prefix is matched
		operatorMap.put("#", operatorMap.get("##"));

		operatorMap.put("%%", new Operator() { // "Remove Largest Postfix Pattern" - operator
			@Override
			String doSubst(final String variable, final L1Node word, final HshContext context) throws Exception {
				if(context.getEnv().issetParameter(variable)) {
					final String paramValue=context.getEnv().getVariableValue(variable);
					if(paramValue==null)
						return null;
					final String pattern=NodeTraversal.substituteSubtree(word, context);
					// Note that pattern is a Posix pattern:
					// TODO parse pattern
					// TODO apply patter by
					// 	TODO translate pattern to java Pattern
					// 	TODO use java Pattern to match the prefix

					// For now treat pattern as a simple String
					if(paramValue.endsWith(pattern))
						return paramValue.substring(0, paramValue.length()-pattern.length()-1);
					else
						return paramValue;
				} else
					return null;
			}
		});
		// TODO Since patterns are not implemented it does not make a difference if shortes or longtest postfix is matched
		operatorMap.put("%", operatorMap.get("%%"));

	}
}
