package com.happypeople.hsh.hsh;

import java.io.EOFException;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/** http://pubs.opengroup.org/onlinepubs/009695399/utilities/xcu_chap02.html#tag_02_02
 ** 2.3 Token Recognition
* The shell shall read its input in terms of lines from a file, from a terminal in the case of an interactive shell,
* or from a string in the case of sh -c or system(). The input lines can be of unlimited length.
* These lines shall be parsed using two major modes: ordinary token recognition and processing of here-documents.

* When an io_here token has been recognized by the grammar (see Shell Grammar), one or more of the subsequent lines
* immediately following the next NEWLINE token form the body of one or more here-documents and shall be parsed according
* to the rules of Here-Document.

* When it is not processing an io_here, the shell shall break its input into tokens by applying the first applicable
* rule below to the next character in its input. The token shall be from the current position in the input until a
* token is delimited according to one of the rules below; the characters forming the token are exactly those in the
* input, including any quoting characters. If it is indicated that a token is delimited, and no characters have been
* included in a token, processing shall continue until an actual token is delimited.

* 1. If the end of input is recognized, the current token shall be delimited. If there is no current token,
* 	the end-of-input indicator shall be returned as the token.

* 2. If the previous character was used as part of an operator and the current character is not quoted and can be used
* 	with the current characters to form an operator, it shall be used as part of that (operator) token.

* 3. If the previous character was used as part of an operator and the current character cannot be used with the current
* 	characters to form an operator, the operator containing the previous character shall be delimited.

* 4. If the current character is backslash, single-quote, or double-quote ( '\', '", or ' )' and it is not quoted,
* 	it shall affect quoting for subsequent characters up to the end of the quoted text. The rules for quoting are as
* 	described in Quoting. During token recognition no substitutions shall be actually performed, and the result token
* 	shall contain exactly the characters that appear in the input (except for <newline> joining), unmodified, including
* 	any embedded or enclosing quotes or substitution operators, between the quote mark and the end of the quoted text.
* 	The token shall not be delimited by the end of the quoted field.

* 5. If the current character is an unquoted '$' or '`', the shell shall identify the start of any candidates for
* 	parameter expansion ( Parameter Expansion), command substitution ( Command Substitution), or arithmetic expansion
* 	( Arithmetic Expansion) from their introductory unquoted character sequences: '$' or "${", "$(" or '`', and
*	 "$((", respectively. The shell shall read sufficient input to determine the end of the unit to be expanded
*	(as explained in the cited sections). While processing the characters, if instances of expansions or quoting are
*	found nested within the substitution, the shell shall recursively process them in the manner specified for the
*	construct that is found. The characters found from the beginning of the substitution to its end, allowing for any
*	recursion necessary to recognize embedded constructs, shall be included unmodified in the result token, including any
*	embedded or enclosing substitution operators or quotes. The token shall not be delimited by the end of the substitution.

* 6. If the current character is not quoted and can be used as the first character of a new operator, the current token
* 	(if any) shall be delimited. The current character shall be used as the beginning of the next (operator) token.

* 7. If the current character is an unquoted <newline>, the current token shall be delimited.

* 8. If the current character is an unquoted <blank>, any token containing the previous character is delimited and the
* 	current character shall be discarded.

* 9. If the previous character was part of a word, the current character shall be appended to that word.

* 10. If the current character is a '#', it and all subsequent characters up to, but excluding, the next <newline>
* shall be discarded as a comment. The <newline> that ends the line is not considered part of the comment.

* 11. The current character is used as the start of a new word.

* Once a token is delimited, it is categorized as required by the grammar in Shell Grammar.
* ******************************************************************************************
* The implementation works as a Iterator<String>. So, it parses while calls to hasNext() and next().
* No buffering or asynchronization is done at all.
* 
* ******************************
* NOTE: This does not work, has to be integrated into Parser.java
*/
public class Tokenizer implements Iterator<Token>, TokenManager {
	/** The input-stream of characters */
	private final SimplePushbackInput in;
	/** The next token to return */
	private Token nextToken;
	/** The token currently built, not finished */
	private Token currentToken;

	/** Flag to indicate EOF was read, no further processing will be done. */
	private boolean closed;

	/** Only one constructor
	 * @param reader the input stream
	 */
	public Tokenizer(final Reader reader) {
		this.in=new SimplePushbackInput(reader);
	}

	@Override
	public boolean hasNext() {
		if(nextToken!=null)
			return true;

		nextToken=parse();
		return nextToken!=null;
	}

	@Override
	public Token next() {
		if(nextToken!=null) {
			final Token ret=nextToken;
			nextToken=null;
			return ret;
		}
		return parse();
	}

	private Token parse() {
		throw new RuntimeException("still not implemented");
	}

	// (starting) characters of operators
	public static final EnumSet<Tok> operatorChars=EnumSet.of (
			Tok.PIPE,
			Tok.UPPERSANT,
			Tok.SEMICOLON,
			Tok.LESS,
			Tok.BIGGER,
			Tok.KLAMMER_AUF,
			Tok.KLAMMER_ZU,
			Tok.DOLLAR);

	/** Token-IDs */
	public enum Tok {
		EOF((char)-1),
		UPPERSANT('&'),
		SEMICOLON(';'),
		SINGLEQUOTE('\''),
		BACKSLASH('\\'),
		DOUBLEQUOTE('"'),
		NEWLINE('\n'),
		BACKTICK('`'),
		PIPE('|'),
		LESS('<'),
		BIGGER('<'),
		KLAMMER_AUF('('),
		KLAMMER_ZU(')'),
		DOLLAR('$'),
		AND_IF("&&"),
		AND_OR("||"),
		BANG('!')
		;

		private char[] value;

		private Tok(final char value) {
			this.value=new char[] { value };
		};

		private Tok(final String value) {
			this.value=value.toCharArray();
		}

		public char[] value() {
			return value;
		}

		/** Parses this token transactional.
		 * @param in input stream
		 * @throws NotParsedException if parsing failed
		 */
		public void parse(final SimplePushbackInput in) throws NotParsedException {
			for(int i=0; i<value.length; i++) {
				final char c=in.read();
				if(c!=value[i]) {
					// pushback all previously read chars in reverse order
					in.pushback(c);
					for(int j=i-1; j>=0; j--)
						in.pushback(value[i]);
					throw new NotParsedException();
				}
			}
		}
	}

	private void state_doublequote() throws EOFException {
		char c;
		do {
			currentToken.append(c=in.read());
			if(c==Tok.BACKSLASH.value()[0])
				state_backslash();
		}while(c!=Tok.DOUBLEQUOTE.value[0]);
	}

	private void state_singlequote() throws EOFException {
		char c;
		do {
			currentToken.append(c=in.read());
			if(c==Tok.BACKSLASH.value()[0])
				state_backslash();
		}while(c!=Tok.SINGLEQUOTE.value[0]);
	}

	private void state_backslash() throws EOFException {
		final char c=in.read();
		if(c==Tok.NEWLINE.value[0])
			currentToken.pop();
		else
			currentToken.append(c);
	}

	@Override
	public void remove() {
		throw new RuntimeException("not implemented");
	}

	@Override
	public Token getNextToken() {
		if(hasNext())
			return next();
		return new Token(0);
	}
}
