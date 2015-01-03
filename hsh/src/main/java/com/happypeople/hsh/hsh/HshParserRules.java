package com.happypeople.hsh.hsh;

import java.util.HashMap;
import java.util.Map;

import com.happypeople.hsh.hsh.l1parser.AssignmentL1Node;

/**
* 2.10.2 Shell Grammar Rules
*
* 1. [Command Name]
* When the TOKEN is exactly a reserved word, the token identifier for that reserved word shall result. Otherwise, the
* token WORD shall be returned.
* Also, if the parser is in any state where only a reserved word could be the next correct
* token, proceed as above.
* Note:
* Because at this point quote marks are retained in the token, quoted strings cannot be recognized as reserved words.
* This rule also implies that reserved words are not recognized except in certain positions in the input, such as after
* a <newline> or semicolon; the grammar presumes that if the reserved word is intended, it is properly delimited by the
* user, and does not attempt to reflect that requirement directly. Also note that line joining is done before
* tokenization, as described in Escape Character (Backslash) , so escaped <newline>s are already removed at this point.
* Rule 1 is not directly referenced in the grammar, but is referred to by other rules, or applies globally.
*
* 2. [Redirection to or from filename]
* The expansions specified in Redirection shall occur. As specified there, exactly one field can result (or the result
* is unspecified), and there are additional requirements on pathname expansion.
*
* 3. [Redirection from here-document]
* Quote removal shall be applied to the word to determine the delimiter that is used to find the end of the here-document
* that begins after the next <newline>.
*
* 4. [Case statement termination]
* When the TOKEN is exactly the reserved word esac, the token identifier for esac shall result. Otherwise, the token
* WORD shall be returned.
*
* 5. [ NAME in for]
* When the TOKEN meets the requirements for a name (see the Base Definitions volume of IEEE Std 1003.1-2001,
* Section 3.230, Name), the token identifier NAME shall result. Otherwise, the token WORD shall be returned.
*
* 6. [Third word of for and case]
* 	a. [ case only]
* 	When the TOKEN is exactly the reserved word in, the token identifier for in shall result. Otherwise, the token
* 	WORD shall be returned.
* 	b. [ for only]
* 	When the TOKEN is exactly the reserved word in or do, the token identifier for in or do shall result, respectively.
* 	Otherwise, the token WORD shall be returned.
*
* (For a. and b.: As indicated in the grammar, a linebreak precedes the tokens in and do. If <newline>s are present at
* the indicated location, it is the token after them that is treated in this fashion.)
*
* 7. [Assignment preceding command name]
* 	a. [When the first word]
* 	If the TOKEN does not contain the character '=', rule 1 is applied. Otherwise, 7b shall be applied.
* 	b. [Not the first word]
*	If the TOKEN contains the equal sign character:
*		*If it begins with '=', the token WORD shall be returned.
*		*If all the characters preceding '=' form a valid name (see the Base Definitions volume of IEEE Std 1003.1-2001,
*		 Section 3.230, Name), the token ASSIGNMENT_WORD shall be returned. (Quoted characters cannot participate in
*		 forming a valid name.)
*		* Otherwise, it is unspecified whether it is ASSIGNMENT_WORD or WORD that is returned.
* Assignment to the NAME shall occur as specified in Simple Commands.
*
* 8. [ NAME in function]
* When the TOKEN is exactly a reserved word, the token identifier for that reserved word shall result. Otherwise, when
* the TOKEN meets the requirements for a name, the token identifier NAME shall result. Otherwise, rule 7 applies.
*
* 9. [Body of function]
* Word expansion and assignment shall never occur, even when required by the rules above, when this rule is being parsed.
* Each TOKEN that might either be expanded or have assignment applied to it shall instead be returned as a single WORD
* consisting only of characters that are exactly the token described in Token Recognition.
*/
public class HshParserRules {
	private final static boolean DEBUG=true;

	// reserved words
	private static Map<String, Integer> reservedWords=new HashMap<String, Integer>();
	static {
		reservedWords.put("if", HshParserConstants.IF);
		reservedWords.put("then", HshParserConstants.THEN);
		reservedWords.put("else", HshParserConstants.ELSE);
		reservedWords.put("elif", HshParserConstants.ELIF);
		reservedWords.put("fi", HshParserConstants.FI);
		reservedWords.put("do", HshParserConstants.DO);
		reservedWords.put("done", HshParserConstants.DONE);
		reservedWords.put("case", HshParserConstants.CASE);
		reservedWords.put("esac", HshParserConstants.ESAC);
		reservedWords.put("while", HshParserConstants.WHILE);
		reservedWords.put("until", HshParserConstants.UNTIL);
		reservedWords.put("for", HshParserConstants.FOR);
		reservedWords.put("{", HshParserConstants.LBRACE);
		reservedWords.put("}", HshParserConstants.RBRACE);
		reservedWords.put("!", HshParserConstants.BANG);
		reservedWords.put("in", HshParserConstants.IN);
	}

	public static void applyRule1(final L2Token t, final String str) {
		final Integer reservedKind=reservedWords.get(str);
		if(reservedKind==null)
			t.kind=HshParserConstants.WORD;
		else
			t.kind=reservedKind;
	}

	public static void applyRule7b(final L2Token t) {
		if(DEBUG)
			System.out.println("applyRule7b to Token, kind="+
				HshParserConstants.tokenImage[t.kind]+
				" image="+t.getString());
		applyRule7b(t, t.getString());
	}

	public static void applyRule7b(final L2Token t, final String string) {
		if(string.startsWith("="))
			t.kind=HshParserConstants.WORD;
		else // be gentle with names, accept all strings as name here
			t.kind=HshParserConstants.ASSIGNMENT_WORD;
	}

	/** Reworked. Now it tests if subnode is AssignmentL1Node.
	 * @param t
	 */
	public static void applyRule7a(final Token token) {
		if(token.kind!=HshParserConstants.WORD)
			return;
		final L2Token t=(L2Token)token;
		if(t.getPart(0) instanceof AssignmentL1Node)
			t.kind=HshParserConstants.ASSIGNMENT_WORD;
		else
			applyRule1(t, t.getString());
	}

	public static void orig_applyRule7a(final Token token) {
		if(DEBUG)
			System.out.println("applyRule7a to Token, kind="+
				HshParserConstants.tokenImage[token.kind]+
				" image="+((L2Token)token).getString());
		if(token.kind!=HshParserConstants.WORD)
			return;

		final L2Token t=(L2Token)token;
		final String image=t.getString();
		if(image.contains("="))
			applyRule7b(t, image);
		else
			applyRule1(t, image);
	}
}
